package cc.minetale.commonlib.util;

import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

import java.awt.*;

@UtilityClass
public class Colors {

    public static TextColor DARK_RED       = TextColor.color(192, 57, 43);
    public static TextColor RED            = TextColor.color(231, 76, 60);
    public static TextColor DARK_ORANGE    = TextColor.color(211, 84, 0);
    public static TextColor ORANGE         = TextColor.color(230, 126, 34);
    public static TextColor DARK_YELLOW    = TextColor.color(243, 156, 18);
    public static TextColor YELLOW         = TextColor.color(241, 196, 15);
    public static TextColor DARK_GREEN     = TextColor.color(39, 174, 96);
    public static TextColor GREEN          = TextColor.color(60, 220, 130);
    public static TextColor DARK_TURQUOISE = TextColor.color(22, 160, 133);
    public static TextColor TURQUOISE      = TextColor.color(26, 188, 156);
    public static TextColor DARK_BLUE      = TextColor.color(41, 128, 185);
    public static TextColor BLUE           = TextColor.color(52, 152, 219);
    public static TextColor DARK_PURPLE    = TextColor.color(142, 68, 173);
    public static TextColor PURPLE         = TextColor.color(155, 89, 182);
    public static TextColor BLACK          = TextColor.color(44, 62, 80);
    public static TextColor MIDNIGHT       = TextColor.color(52, 73, 94);
    public static TextColor DARK_GRAY      = TextColor.color(127, 140, 141);
    public static TextColor GRAY           = TextColor.color(149, 165, 166);
    public static TextColor LIGHT_GRAY     = TextColor.color(189, 195, 199);
    public static TextColor WHITE          = TextColor.color(236, 240, 241);

    public static TextColor getColorBetween(TextColor from, TextColor to, float percent) {
        float stepR = Math.abs(from.red() - to.red());
        float stepG = Math.abs(from.green() - to.green());
        float stepB = Math.abs(from.blue() - to.blue());

        return TextColor.color(
                (int) (from.red() < to.red() ? from.red() + (stepR * percent) : from.red() - (stepR * percent)),
                (int) (from.green() < to.green() ? from.green() + (stepG * percent) : from.green() - (stepG * percent)),
                (int) (from.blue() < to.blue() ? from.blue() + (stepB * percent) : from.blue() - (stepB * percent))
        );
    }

    public static Color hexToColor(String colorStr) {
        return new Color(
                Integer.valueOf(colorStr.substring(0, 2), 16),
                Integer.valueOf(colorStr.substring(2, 4), 16),
                Integer.valueOf(colorStr.substring(4, 6), 16)
        );
    }

    public static TextColor bleach(NamedTextColor color, double amount) {
        return TextColor.color(
                (int) ((color.red() * (1 - amount) / 255 + amount) * 255),
                (int) ((color.green() * (1 - amount) / 255 + amount) * 255),
                (int) ((color.blue() * (1 - amount) / 255 + amount) * 255)
        );
    }

}
