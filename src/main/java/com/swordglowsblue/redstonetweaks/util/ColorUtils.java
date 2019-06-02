package com.swordglowsblue.redstonetweaks.util;

import net.minecraft.util.DyeColor;
import net.minecraft.util.math.MathHelper;

import java.awt.*;

public class ColorUtils {
    public static int rgbIntFromDye(DyeColor color) { return colorFromDye(color).getRGB(); }
    public static Color colorFromDye(DyeColor color) {
        float[] c = color.getColorComponents();
        return new Color(c[0], c[1], c[2]);
    }

    public static Color multiply(Color a, Color b) {
        float[] ac = a.getRGBColorComponents(null);
        float[] bc = b.getRGBColorComponents(null);
        return new Color(
            ac[0] * bc[0],
            ac[1] * bc[1],
            ac[2] * bc[2]
        );
    }

    public static Color getPowerBrightnessMask(int power) { return getPowerBrightnessMask(power, 15f); }
    public static int getPowerBrightnessMaskInt(int power) { return getPowerBrightnessMask(power).getRGB(); }
    public static Color getPowerBrightnessMask(int power, float max) {
        float p = MathHelper.clamp(power <= 0 ? 0.3f : power / max * 0.6f + 0.4f, 0, 1);
        return new Color(p,p,p);
    }
}
