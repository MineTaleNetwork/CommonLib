package cc.minetale.commonlib.modules.balance;

import cc.minetale.commonlib.util.FastNoiseLite;

import java.awt.*;

public class Levels {

    public static final String EXP_SUFFIX = "EXP";

//    public static final ComponentFormatting EXP_FORMAT = ComponentFormatting.builder().color(ChatColor.AQUA).build();
//    public static final ComponentFormatting LEVELS_FORMAT = EXP_FORMAT;

    private static final FastNoiseLite noiseGenerator = new FastNoiseLite(337101234);
//    private static final SimplexNoiseGenerator noiseGenerator = new SimplexNoiseGenerator(3371012348295728621L);

    public static String getLevelAffix(int level) {
        return "level" + (level > 1 ? "s" : "");
    }

    public static int getLevelFromExperience(long experience) {
        return (int) Math.floor(Math.pow(experience / 300.0, 1 / 2.0));
    }

    public static long getExperienceFromLevel(int level) {
        return (long) (Math.pow(level, 2) * 300);
    }

    public static Color getLevelColor(int level) {
        float h = (noiseGenerator.GetNoise(level / 20f, 0f) + 1f) / 2f;
        float s = 0.5f + (((noiseGenerator.GetNoise(0, level / 5f) + 1f) / 2f) / 2f);
        float b = 0.5f + (((noiseGenerator.GetNoise(0, 0, level / 5f) + 1f) / 2f) / 2f);
        return Color.getHSBColor(h, s, b);
    }

}
