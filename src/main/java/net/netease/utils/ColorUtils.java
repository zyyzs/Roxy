package net.netease.utils;

import lol.tgformat.utils.math.MathUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;

import java.awt.*;

/**
 * @author TG_format
 * @since 2024/6/1 15:41
 */
public final class ColorUtils {
    public static int getRGB(int r, int g, int b) {
        return getRGB(r, g, b, 255);
    }
    public static void resetColor() {
        GlStateManager.color(1, 1, 1, 1);
    }
    public static Color getRainbow(int speed, int offset, float saturation) {
        float hue = (System.currentTimeMillis() + (long) offset) % (long) speed;
        return Color.getHSBColor(hue / (float) speed, saturation, 1.0f);
    }
    public static Color injectAlpha(Color color, int alpha) {
        alpha = MathHelper.clamp_int(alpha, 0, 255);
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
    }
    public static void glColor(int hex) {
        float alpha = (hex >> 24 & 0xFF) / 255.0F;
        float red = (hex >> 16 & 0xFF) / 255.0F;
        float green = (hex >> 8 & 0xFF) / 255.0F;
        float blue = (hex & 0xFF) / 255.0F;
        GL11.glColor4f(red, green, blue, alpha);
    }
    public static int getColor(float hueOffset, float saturation, float brightness) {
        float speed = 4500f;
        float hue = (System.currentTimeMillis() % (int) speed) / speed;
        return Color.HSBtoRGB(hue - hueOffset / 54, saturation, brightness);
    }

    public static Color interpolateColorC(Color color1, Color color2, float amount) {
        amount = Math.min(1.0F, Math.max(0.0F, amount));
        return new Color(MathUtil.interpolateInt(color1.getRed(), color2.getRed(), (double)amount), MathUtil.interpolateInt(color1.getGreen(), color2.getGreen(), (double)amount), MathUtil.interpolateInt(color1.getBlue(), color2.getBlue(), (double)amount), MathUtil.interpolateInt(color1.getAlpha(), color2.getAlpha(), (double)amount));
    }

    public static int getRGB(int r, int g, int b, int a) {
        return (a & 255) << 24 | (r & 255) << 16 | (g & 255) << 8 | b & 255;
    }

    public static int[] splitRGB(int rgb) {
        int[] ints = new int[]{rgb >> 16 & 255, rgb >> 8 & 255, rgb & 255};
        return ints;
    }

    public static Color interpolateColorsBackAndForth(int speed, int index, Color start, Color end, boolean trueColor) {
        int angle = (int)((System.currentTimeMillis() / (long)speed + (long)index) % 360L);
        angle = (angle >= 180 ? 360 - angle : angle) * 2;
        return trueColor ? interpolateColorHue(start, end, (float)angle / 360.0F) : interpolateColorC(start, end, (float)angle / 360.0F);
    }

    public static Color interpolateColorHue(Color color1, Color color2, float amount) {
        amount = Math.min(1.0F, Math.max(0.0F, amount));
        float[] color1HSB = Color.RGBtoHSB(color1.getRed(), color1.getGreen(), color1.getBlue(), (float[])null);
        float[] color2HSB = Color.RGBtoHSB(color2.getRed(), color2.getGreen(), color2.getBlue(), (float[])null);
        Color resultColor = Color.getHSBColor(MathUtil.interpolateFloat(color1HSB[0], color2HSB[0], (double)amount), MathUtil.interpolateFloat(color1HSB[1], color2HSB[1], (double)amount), MathUtil.interpolateFloat(color1HSB[2], color2HSB[2], (double)amount));
        return applyOpacity(resultColor, (float)MathUtil.interpolateInt(color1.getAlpha(), color2.getAlpha(), (double)amount) / 255.0F);
    }

    public static Color applyOpacity(Color color, float opacity) {
        opacity = Math.min(1.0F, Math.max(0.0F, opacity));
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), (int)((float)color.getAlpha() * opacity));
    }

    public static int interpolateColor(Color color1, Color color2, float amount) {
        amount = Math.min(1.0F, Math.max(0.0F, amount));
        return interpolateColorC(color1, color2, amount).getRGB();
    }

    public static Color brighter(Color color, float FACTOR) {
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();
        int alpha = color.getAlpha();
        int i = (int)(1.0D / (1.0D - (double)FACTOR));
        if (r == 0 && g == 0 && b == 0) {
            return new Color(i, i, i, alpha);
        } else {
            if (r > 0 && r < i) {
                r = i;
            }

            if (g > 0 && g < i) {
                g = i;
            }

            if (b > 0 && b < i) {
                b = i;
            }

            return new Color(Math.min((int)((float)r / FACTOR), 255), Math.min((int)((float)g / FACTOR), 255), Math.min((int)((float)b / FACTOR), 255), alpha);
        }
    }

    public static int getRGB(int rgb) {
        return -16777216 | rgb;
    }

    public static int reAlpha(int rgb, int alpha) {
        return getRGB(getRed(rgb), getGreen(rgb), getBlue(rgb), alpha);
    }

    public static int getRed(int rgb) {
        return rgb >> 16 & 255;
    }

    public static int getGreen(int rgb) {
        return rgb >> 8 & 255;
    }

    public static int getBlue(int rgb) {
        return rgb & 255;
    }

    public static int getAlpha(int rgb) {
        return rgb >> 24 & 255;
    }
}
