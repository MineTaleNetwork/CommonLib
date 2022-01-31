package cc.minetale.commonlib.util;

import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.Template;

import java.util.ArrayList;

@UtilityClass
public class Message {

    public static TextComponent CONSOLE = Component.text("Console", NamedTextColor.DARK_RED);

    public static Component notification(String prefix, Component component) {
        return Component.text()
                .append(
                        Component.text(prefix, NamedTextColor.GOLD, TextDecoration.BOLD),
                        Component.text(" » ", NamedTextColor.DARK_GRAY, TextDecoration.BOLD),
                        component
                ).build();
    }

    public static Component separator(int times) {
        return Component.text(StringUtil.repeat(" ", times), NamedTextColor.DARK_GRAY, TextDecoration.STRIKETHROUGH);
    }

    public static Component chatSeparator() {
        return separator(80);
    }

    public static Component menuSeparator() {
        return separator(50);
    }

    public static Component scoreboardSeparator() {
        return separator(32);
    }

    public static Component gradientComponent(String text, TextColor from, TextColor to) {
        var builder = Component.text();

        int steps = text.length();
        int step = 0;

        for (char c : text.toCharArray()) {
            builder.append(Component.text(c, Colors.getColorBetween(from, to, step / (float) steps)));

            step++;
        }

        return builder.build();
    }

    public static Component removeItalics(Component component) {
        component.decoration(TextDecoration.ITALIC, false);

        component.children().forEach(child -> {
            System.out.println("Child: " + ((TextComponent) child).content());
            removeItalics(child);
        });

        return component;
    }

    public static Component parse(String input, Object... replacements) {
        var templates = new ArrayList<Template>();

        for (int i = 0; i < replacements.length; i++) {
            var key = String.valueOf(i);
            var replacement = replacements[i];

            if (replacement instanceof Component component) {
                templates.add(Template.of(key, component));
            } else {
                templates.add(Template.of(key, replacement.toString()));
            }
        }

        return MiniMessage.get().parse(input, templates);
    }

    public static Component coloredPing(int ping) {
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

    public static Component coloredHealth(double health) {
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

        if (dividedHealth % 1 == 0 || dividedHealth % .5 == 0) {
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

    public static Component healthToHearts(double health, double maxHealth, double heartScale, boolean displayAmount) {
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

}
