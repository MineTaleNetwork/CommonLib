package cc.minetale.commonlib.util;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;

import java.awt.*;

public class MC {

    private static final GsonComponentSerializer gsonSerializer = GsonComponentSerializer.gson();

    // TODO -> Convert to proper Components
    @Getter private static final int chatPixels = 320;
    @Getter private static final int centerPixels = chatPixels / 2;

    public static TextComponent CONSOLE = Component.text("Console", NamedTextColor.DARK_RED);
    public static TextComponent SEPARATOR_32 = Component.text(StringUtil.repeat(" ", 32), NamedTextColor.DARK_GRAY, TextDecoration.STRIKETHROUGH);
    public static TextComponent SEPARATOR_50 = Component.text(StringUtil.repeat(" ", 50), NamedTextColor.DARK_GRAY, TextDecoration.STRIKETHROUGH);
    public static TextComponent SEPARATOR_80 = Component.text(StringUtil.repeat(" ", 80), NamedTextColor.DARK_GRAY, TextDecoration.STRIKETHROUGH);

    public static Component fixItalics(Component component) {
        if (component.decoration(TextDecoration.ITALIC) == TextDecoration.State.NOT_SET) {
            component = component.decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE);
        }

        return component;
    }

    public static Component fromGson(String string) {
        return gsonSerializer.deserialize(string);
    }

    public static String toGson(Component component) {

        return gsonSerializer.serializeOrNull(component);
    }

    public static Color fromNamedTextColor(NamedTextColor color) {
        return new Color(color.red(), color.green(), color.blue());
    }

    public static TextColor toTextColor(Color color) {
        return TextColor.color(color.getRGB());
    }

    public static Color hexToColor(String colorStr) {
        return new Color(
                Integer.valueOf(colorStr.substring(0, 2), 16),
                Integer.valueOf(colorStr.substring(2, 4), 16),
                Integer.valueOf(colorStr.substring(4, 6), 16)
        );
    }

    public static Color bleach(Color color, double amount) {
        return new Color(
                (int) ((color.getRed() * (1 - amount) / 255 + amount) * 255),
                (int) ((color.getGreen() * (1 - amount) / 255 + amount) * 255),
                (int) ((color.getBlue() * (1 - amount) / 255 + amount) * 255)
        );
    }

    public static TextComponent gradientComponent(String text, Color from, Color to) {
        TextComponent.Builder builder = Component.text();

        int steps = text.length();
        int step = 0;

        for (char c : text.toCharArray()) {
            builder.append(Component.text(c)
                    .color(TextColor.color(getColorBetween(from, to, step / (float) steps).getRGB())));
            step++;
        }

        return builder.build();
    }

    public static Color getColorBetween(Color from, Color to, float x) {
        float stepR = Math.abs(from.getRed() - to.getRed());
        float stepG = Math.abs(from.getGreen() - to.getGreen());
        float stepB = Math.abs(from.getBlue() - to.getBlue());

        int r = (int) (from.getRed() < to.getRed() ? from.getRed() + (stepR * x) : from.getRed() - (stepR * x));
        int g = (int) (from.getGreen() < to.getGreen() ? from.getGreen() + (stepG * x) : from.getGreen() - (stepG * x));
        int b = (int) (from.getBlue() < to.getBlue() ? from.getBlue() + (stepB * x) : from.getBlue() - (stepB * x));

        return new Color(r, g, b);
    }

    public static TextComponent colorPing(int ping) {
        if (ping <= 40) {
            return Component.text(ping, NamedTextColor.GREEN);
        } else if (ping <= 70) {
            return Component.text(ping, NamedTextColor.YELLOW);
        } else if (ping <= 100) {
            return Component.text(ping, NamedTextColor.GOLD);
        } else {
            return Component.text(ping, NamedTextColor.RED);
        }
    }

    public static TextComponent colorHealth(double health) {
        if (health > 15) {
            return Component.text(convertHealth(health), NamedTextColor.GREEN);
        } else if (health > 10) {
            return Component.text(convertHealth(health), NamedTextColor.YELLOW);
        } else if (health > 5) {
            return Component.text(convertHealth(health), NamedTextColor.GOLD);
        } else {
            return Component.text(convertHealth(health), NamedTextColor.RED);
        }
    }

    public static double convertHealth(double health) {
        double dividedHealth = health / 2;

        if (dividedHealth % 1 == 0) {
            return dividedHealth;
        }

        if (dividedHealth % .5 == 0) {
            return dividedHealth;
        }

        if (dividedHealth - ((int) dividedHealth) > .5) {
            return ((int) dividedHealth) + 1;
        } else if (dividedHealth - ((int) dividedHealth) > .25) {
            return ((int) dividedHealth) + .5;
        } else {
            return ((int) dividedHealth);
        }
    }

    public static TextComponent notificationMessage(String prefix, Component message) {
        return Component.text()
                .append(
                        Component.text(prefix, NamedTextColor.GOLD, TextDecoration.BOLD),
                        Component.text(" » ", NamedTextColor.DARK_GRAY, TextDecoration.BOLD),
                        message
                ).build();
    }

    public static TextComponent healthToHearts(double health, double maxHealth, double heartScale, boolean displayAmount) {
        String heart = "❤";
        int allHearts = Math.floorDiv((int) maxHealth, (int) heartScale);
        int fullHearts = Math.floorDiv((int) health, (int) heartScale);
        boolean halfHeart = (maxHealth - health) % heartScale != 0;
        int emptyHearts = allHearts - fullHearts - (halfHeart ? 1 : 0);

        return Component.text()
                .append(
                        Component.text(StringUtil.repeat(heart, fullHearts), NamedTextColor.DARK_RED),
                        halfHeart ? Component.text(heart, NamedTextColor.RED) : Component.empty(),
                        Component.text(StringUtil.repeat(heart, emptyHearts), NamedTextColor.DARK_GRAY),
                        displayAmount ? Component.text(" (" + (int) health + "/" + (int) maxHealth + ")", NamedTextColor.WHITE) : Component.empty()
                ).build();
    }

    public static TextComponent levelProgressMessage(int lvl, int nextLvl, long exp, long nextLevelExp, Color currentColor, Color nextColor, boolean displayTitle, boolean displayLevels, boolean displayUnder) {
        var builder = Component.text();

        float progress = Math.min(exp / (float) nextLevelExp, 1);

        Color endColor = getColorBetween(currentColor, nextColor, progress);

        if (displayTitle) {
            builder.append(centerAlignMessage(Component.text("Level Progress\n", NamedTextColor.GOLD, TextDecoration.BOLD)));
        }

        Component component = Component.text("█");

        int amountOfSteps = 41;
        int progressSteps = Math.min((int) Math.floor(amountOfSteps * progress) + 1, amountOfSteps); // TODO 0 is one line when should be none
        int stepsLeft = amountOfSteps - progressSteps;

        Component progressBar = gradientComponent(StringUtil.repeat(" ", progressSteps), currentColor, endColor)
                .decorate(TextDecoration.STRIKETHROUGH);

        component = component.append(Component.text(StringUtil.repeat(" ", stepsLeft), NamedTextColor.DARK_GRAY, TextDecoration.STRIKETHROUGH));
        builder.append(component);

        component = Component.text("█", TextColor.color((progress < 1 ? new Color(64, 64, 64) : endColor).getRGB()));
        progressBar = progressBar.append(component);

        if (displayLevels) {
            component = Component.text(lvl + " ")
                    .append(progressBar)
                    .append(Component.text(" " + nextLvl + "\n"));
        } else {
            component = progressBar;
        }

        builder.append(centerAlignMessage(component));

        if (displayUnder) {
            builder.append(centerAlignMessage(
                    progress < 1 ?
                            Component.text("(" + exp + "/" + nextLevelExp + ")\n") :
                            Component.text("Leveled Up!\n", NamedTextColor.YELLOW, TextDecoration.BOLD))
            );
        }

        return builder.build();
    }

    //    public static List<BaseComponent> equalizeLengthsReversed(List<List<BaseComponent>> lines) {
//        int amountOfGroups = lines.get(0).size();
//
//        List<Integer> biggestElementPixelsInGroup = new ArrayList<>();
//
//        for(int i = 0; i < amountOfGroups; i++) {
//            biggestElementPixelsInGroup.add(0);
//        }
//
//        for (List<BaseComponent> line : lines) {
//            for (int groupIndex = 0; groupIndex < amountOfGroups; groupIndex++) {
//                int possibleBiggestPixels = getComponentPixels(line.get(groupIndex));
//                if (possibleBiggestPixels > biggestElementPixelsInGroup.get(groupIndex)) {
//                    biggestElementPixelsInGroup.set(groupIndex, possibleBiggestPixels);
//                }
//            }
//        }
//
//        List<BaseComponent> alignedLines = new ArrayList<>();
//
//        for (List<BaseComponent> line : lines) {
//            TextComponent alignedLine = new TextComponent();
//            int prevGroupDifference = 0;
//            for (int groupIndex = 0; groupIndex < amountOfGroups; groupIndex++) {
//                TextComponent alignedElement = new TextComponent(getSpacePadding(Math.max(biggestElementPixelsInGroup.get(groupIndex) - getComponentPixels(line.get(groupIndex)) - prevGroupDifference, 0)));
//                alignedElement.addExtra(line.get(groupIndex));
//                alignedLine.addExtra(alignedElement);
//                prevGroupDifference = getComponentPixels(alignedElement) - biggestElementPixelsInGroup.get(groupIndex);
//            }
//            alignedLines.add(alignedLine);
//        }
//
//        return alignedLines;
//    }
//
//    public static TextComponent rightAlignMessage(BaseComponent message) {
//        int messagePxSize = getComponentPixels(message);
//
//        TextComponent spacePadding = getSpacePadding(chatPixels - messagePxSize - 3);
//
//        TextComponent centeredComponent = new TextComponent(spacePadding);
//        centeredComponent.addExtra(message);
//
//        return centeredComponent;
//    }
//
//    public static TextComponent rightAlignWithPadding(BaseComponent padding, BaseComponent line) {
//        int paddingPxSize = getComponentPixels(padding);
//        int linePxSize = getComponentPixels(line);
//
//        TextComponent spacePadding = getSpacePadding(chatPixels - paddingPxSize - linePxSize - 3);
//
//        TextComponent centeredComponent = new TextComponent(padding);
//        centeredComponent.addExtra(spacePadding);
//        centeredComponent.addExtra(line);
//
//        return centeredComponent;
//    }
//
//
    public static TextComponent centerAlignMessage(Component message) {
        int messagePxSize = getComponentPixels(message);

        TextComponent spacePadding = getSpacePadding(centerPixels - (messagePxSize / 2));

        return spacePadding.append(message);
    }

    //
//    public static List<BaseComponent> centerAlongFirst(List<BaseComponent> lines) {
//        int start = centerPixels - (getComponentPixels(lines.get(0)) / 2);
//
//        List<BaseComponent> alignedLines = new ArrayList<>();
//        for(BaseComponent line : lines) {
//            TextComponent spacePadding = getSpacePadding(start);
//
//            TextComponent alignedLine = new TextComponent(spacePadding);
//            alignedLine.addExtra(line);
//            alignedLine.addExtra("\n");
//            alignedLines.add(alignedLine);
//        }
//
//        return alignedLines;
//    }
//
//    public static List<BaseComponent> centerAlongBiggest(List<BaseComponent> lines) {
//        int biggestLinePixels = 0;
//        for(BaseComponent line : lines) {
//            int possibleBiggestPixels = getComponentPixels(line);
//            if(possibleBiggestPixels > biggestLinePixels) {
//                biggestLinePixels = possibleBiggestPixels;
//            }
//        }
//
//        int start = centerPixels - (biggestLinePixels / 2);
//
//        List<BaseComponent> alignedLines = new ArrayList<>();
//        for(BaseComponent line : lines) {
//            TextComponent spacePadding = getSpacePadding(start);
//
//            TextComponent alignedLine = new TextComponent(spacePadding);
//            alignedLine.addExtra(line);
//            alignedLine.addExtra("\n");
//            alignedLines.add(alignedLine);
//        }
//
//        return alignedLines;
//    }
//
//    public static List<BaseComponent> centerWithPaddingAlongBiggest(List<BaseComponent> paddings, List<BaseComponent> linesToCenter) {
//        int biggestPaddingPixels = 0;
//        for(BaseComponent padding : paddings) {
//            int possibleBiggestPixels = getComponentPixels(padding);
//            if(possibleBiggestPixels > biggestPaddingPixels) {
//                biggestPaddingPixels = possibleBiggestPixels;
//            }
//        }
//
//        int biggestToCenterPixels = 0;
//        for(BaseComponent line : linesToCenter) {
//            int possibleBiggestPixels = getComponentPixels(line);
//            if(possibleBiggestPixels > biggestToCenterPixels) {
//                biggestToCenterPixels = possibleBiggestPixels;
//            }
//        }
//
//        int center = biggestPaddingPixels + ((chatPixels - biggestPaddingPixels) / 2);
//
//        int start = center - (biggestToCenterPixels / 2);
//
//        List<BaseComponent> alignedLines = new ArrayList<>();
//        int numOfLines = Math.max(paddings.size(), linesToCenter.size());
//        for(int i = 0; i < numOfLines; i++) {
//            if(i < linesToCenter.size()) {
//                BaseComponent lineToCenter = linesToCenter.get(i);
//                if (lineToCenter != null) {
//                    BaseComponent padding = null;
//                    int paddingPxSize = 0;
//                    if (i < paddings.size()) {
//                        padding = paddings.get(i);
//                        if (padding != null) {
//                            paddingPxSize = getComponentPixels(padding);
//                        }
//                    }
//
//                    TextComponent spacePadding = getSpacePadding(start - paddingPxSize);
//
//                    TextComponent alignedLine;
//                    if(padding != null) {
//                        alignedLine = new TextComponent(padding);
//                        alignedLine.addExtra(spacePadding);
//                    } else {
//                        alignedLine = new TextComponent(spacePadding);
//                    }
//                    alignedLine.addExtra(lineToCenter);
//                    alignedLine.addExtra("\n");
//                    alignedLines.add(alignedLine);
//                }
//            }
//        }
//
//        return alignedLines;
//    }
//
    public static int getComponentPixels(Component message) {
        if (message == null) {
            return 0;
        }

        int messagePxSize = 0;
        if (message instanceof TextComponent) {
            TextComponent textMessage = (TextComponent) message;

            textMessage.content();
            String plainText = textMessage.content();

            boolean isBold = textMessage.hasDecoration(TextDecoration.BOLD);

            for (char c : plainText.toCharArray()) {
                var dFI = DefaultFontInfo.getDefaultFontInfo(c);
                messagePxSize += isBold ? dFI.getBoldLength() : dFI.getLength();
                messagePxSize++; //Include one pixel for background
            }
        }

        if (!message.children().isEmpty()) {
            for (Component extra : message.children()) {
                messagePxSize += getComponentPixels(extra);
            }
        }

        return messagePxSize;
    }

    public static TextComponent getSpacePadding(int compensate) {
        int spaceLength = DefaultFontInfo.SPACE.getLength() + 1;
        int boldSpaces = compensate % spaceLength;
        int spaces = (compensate / spaceLength) - boldSpaces;

        return Component.text(StringUtil.repeat(" ", spaces))
                .append(Component.text(StringUtil.repeat(" ", boldSpaces)));
    }

}
