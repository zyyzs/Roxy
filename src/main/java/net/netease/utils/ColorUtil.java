package net.netease.utils;

import com.viaversion.viaversion.util.MathUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.util.MathHelper;
import net.optifine.util.MathUtils;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * @author TG_format
 * @since 2024/6/1 15:50
 */
public class ColorUtil {
    public static Color withAlpha(Color color, int alpha) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), MathUtil.clamp(0, 255, alpha));
    }

    public static Color mixColors(Color color1, Color color2, double percent) {
        double inverse_percent = 1.0D - percent;
        int redPart = (int)((double)color1.getRed() * percent + (double)color2.getRed() * inverse_percent);
        int greenPart = (int)((double)color1.getGreen() * percent + (double)color2.getGreen() * inverse_percent);
        int bluePart = (int)((double)color1.getBlue() * percent + (double)color2.getBlue() * inverse_percent);
        return new Color(redPart, greenPart, bluePart);
    }

    public static Color getBlendColor(double current, double max) {
        long base = Math.round(max / 5.0D);
        if (current >= (double)(base * 5L)) {
            return new Color(15, 255, 15);
        } else if (current >= (double)(base * 4L)) {
            return new Color(165, 255, 0);
        } else if (current >= (double)(base * 3L)) {
            return new Color(255, 190, 0);
        } else {
            return current >= (double)(base * 2L) ? new Color(255, 90, 0) : new Color(255, 0, 0);
        }
    }

    public static Color getRandomColor() {
        return new Color(Color.HSBtoRGB((float)Math.random(), (float)(0.5D + Math.random() / 2.0D), (float)(0.5D + Math.random() / 2.0D)));
    }

    public static int overwriteAlphaComponent(int colour, int alphaComponent) {
        int red = colour >> 16 & 255;
        int green = colour >> 8 & 255;
        int blue = colour & 255;
        return (alphaComponent & 255) << 24 | (red & 255) << 16 | (green & 255) << 8 | blue & 255;
    }

    public static int getColor(int red, int green, int blue) {
        return getColor(red, green, blue, 255);
    }

    public static int getColor(int red, int green, int blue, int alpha) {
        int color = MathHelper.clamp_int(alpha, 0, 255) << 24;
        color |= MathHelper.clamp_int(red, 0, 255) << 16;
        color |= MathHelper.clamp_int(green, 0, 255) << 8;
        color |= MathHelper.clamp_int(blue, 0, 255);
        return color;
    }

    public static Color tripleColor(int rgbValue) {
        return tripleColor(rgbValue, 1.0F);
    }

    public static Color tripleColor(int rgbValue, float alpha) {
        alpha = Math.min(1.0F, Math.max(0.0F, alpha));
        return new Color(rgbValue, rgbValue, rgbValue, (int)(255.0F * alpha));
    }

    public static int removeAlphaComponent(int colour) {
        int red = colour >> 16 & 255;
        int green = colour >> 8 & 255;
        int blue = colour & 255;
        return (red & 255) << 16 | (green & 255) << 8 | blue & 255;
    }

    public static Color getRainbow() {
        return new Color(Color.HSBtoRGB((float)((double) Minecraft.getMinecraft().thePlayer.ticksExisted / 50.0D + Math.sin(0.032D)) % 1.0F, 0.5F, 1.0F));
    }

    public static Color[] getAnalogousColor(Color color) {
        Color[] colors = new Color[2];
        float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), (float[])null);
        float degree = 0.083333336F;
        float newHueAdded = hsb[0] + degree;
        colors[0] = new Color(Color.HSBtoRGB(newHueAdded, hsb[1], hsb[2]));
        float newHueSubtracted = hsb[0] - degree;
        colors[1] = new Color(Color.HSBtoRGB(newHueSubtracted, hsb[1], hsb[2]));
        return colors;
    }

    public static Color hslToRGB(float[] hsl) {
        float red;
        float green;
        float blue;
        if (hsl[1] == 0.0F) {
            blue = 1.0F;
            green = 1.0F;
            red = 1.0F;
        } else {
            float q = (double)hsl[2] < 0.5D ? hsl[2] * (1.0F + hsl[1]) : hsl[2] + hsl[1] - hsl[2] * hsl[1];
            float p = 2.0F * hsl[2] - q;
            red = hueToRGB(p, q, hsl[0] + 0.33333334F);
            green = hueToRGB(p, q, hsl[0]);
            blue = hueToRGB(p, q, hsl[0] - 0.33333334F);
        }

        red *= 255.0F;
        green *= 255.0F;
        blue *= 255.0F;
        return new Color((int)red, (int)green, (int)blue);
    }

    public static float hueToRGB(float p, float q, float t) {
        float newT = t;
        if (t < 0.0F) {
            newT = t + 1.0F;
        }

        if (newT > 1.0F) {
            --newT;
        }

        if (newT < 0.16666667F) {
            return p + (q - p) * 6.0F * newT;
        } else if (newT < 0.5F) {
            return q;
        } else {
            return newT < 0.6666667F ? p + (q - p) * (0.6666667F - newT) * 6.0F : p;
        }
    }

    public static float[] rgbToHSL(Color rgb) {
        float red = (float)rgb.getRed() / 255.0F;
        float green = (float)rgb.getGreen() / 255.0F;
        float blue = (float)rgb.getBlue() / 255.0F;
        float max = Math.max(Math.max(red, green), blue);
        float min = Math.min(Math.min(red, green), blue);
        float c = (max + min) / 2.0F;
        float[] hsl = new float[]{c, c, c};
        if (max == min) {
            hsl[0] = hsl[1] = 0.0F;
        } else {
            float d = max - min;
            hsl[1] = (double)hsl[2] > 0.5D ? d / (2.0F - max - min) : d / (max + min);
            if (max == red) {
                hsl[0] = (green - blue) / d + (float)(green < blue ? 6 : 0);
            } else if (max == blue) {
                hsl[0] = (blue - red) / d + 2.0F;
            } else if (max == green) {
                hsl[0] = (red - green) / d + 4.0F;
            }

            hsl[0] /= 6.0F;
        }

        return hsl;
    }

    public static Color imitateTransparency(Color backgroundColor, Color accentColor, float percentage) {
        return new Color(interpolateColor(backgroundColor, accentColor, 255.0F * percentage / 255.0F));
    }

    public static int applyOpacity(int color, float opacity) {
        Color old = new Color(color);
        return applyOpacity(old, opacity).getRGB();
    }

    public static Color applyOpacity(Color color, float opacity) {
        opacity = Math.min(1, Math.max(0, opacity));
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), (int) (color.getAlpha() * opacity));
    }

    public static Color darker(Color color, float FACTOR) {
        return new Color(Math.max((int)((float)color.getRed() * FACTOR), 0), Math.max((int)((float)color.getGreen() * FACTOR), 0), Math.max((int)((float)color.getBlue() * FACTOR), 0), color.getAlpha());
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

    public static Color averageColor(BufferedImage bi, int width, int height, int pixelStep) {
        int[] color = new int[3];

        int x;
        for(x = 0; x < width; x += pixelStep) {
            for(int y = 0; y < height; y += pixelStep) {
                Color pixel = new Color(bi.getRGB(x, y));
                color[0] += pixel.getRed();
                color[1] += pixel.getGreen();
                color[2] += pixel.getBlue();
            }
        }

        x = width * height / (pixelStep * pixelStep);
        return new Color(color[0] / x, color[1] / x, color[2] / x);
    }

    public static Color rainbow(int speed, int index, float saturation, float brightness, float opacity) {
        int angle = (int)((System.currentTimeMillis() / (long)speed + (long)index) % 360L);
        float hue = (float)angle / 360.0F;
        Color color = new Color(Color.HSBtoRGB(hue, saturation, brightness));
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), Math.max(0, Math.min(255, (int)(opacity * 255.0F))));
    }

    public static Color rainbow() {
        Color currentColor = new Color(Color.HSBtoRGB((float)(System.nanoTime() + 400000L) / 1.0E10F % 1.0F, 1.0F, 1.0F));
        return new Color((float)currentColor.getRed() / 255.0F, (float)currentColor.getGreen() / 255.0F, (float)currentColor.getBlue() / 255.0F, (float)currentColor.getAlpha() / 255.0F);
    }

    public static Color interpolateColorsBackAndForth(int speed, int index, Color start, Color end, boolean trueColor) {
        int angle = (int)((System.currentTimeMillis() / (long)speed + (long)index) % 360L);
        angle = (angle >= 180 ? 360 - angle : angle) * 2;
        return trueColor ? interpolateColorHue(start, end, (float)angle / 360.0F) : interpolateColorC(start, end, (float)angle / 360.0F);
    }

    public static int interpolateColor(Color color1, Color color2, float amount) {
        amount = Math.min(1.0F, Math.max(0.0F, amount));
        return interpolateColorC(color1, color2, amount).getRGB();
    }

    public static int interpolateColor(int color1, int color2, float amount) {
        amount = Math.min(1.0F, Math.max(0.0F, amount));
        Color cColor1 = new Color(color1);
        Color cColor2 = new Color(color2);
        return interpolateColorC(cColor1, cColor2, amount).getRGB();
    }

    public static Color interpolateColorC(Color color1, Color color2, float amount) {
        amount = Math.min(1.0F, Math.max(0.0F, amount));
        return new Color(MathUtils.interpolateInt(color1.getRed(), color2.getRed(), (double)amount), MathUtils.interpolateInt(color1.getGreen(), color2.getGreen(), (double)amount), MathUtils.interpolateInt(color1.getBlue(), color2.getBlue(), (double)amount), MathUtils.interpolateInt(color1.getAlpha(), color2.getAlpha(), (double)amount));
    }

    public static Color interpolateColorHue(Color color1, Color color2, float amount) {
        amount = Math.min(1.0F, Math.max(0.0F, amount));
        float[] color1HSB = Color.RGBtoHSB(color1.getRed(), color1.getGreen(), color1.getBlue(), (float[])null);
        float[] color2HSB = Color.RGBtoHSB(color2.getRed(), color2.getGreen(), color2.getBlue(), (float[])null);
        Color resultColor = Color.getHSBColor(MathUtils.interpolateFloat(color1HSB[0], color2HSB[0], (double)amount), MathUtils.interpolateFloat(color1HSB[1], color2HSB[1], (double)amount), MathUtils.interpolateFloat(color1HSB[2], color2HSB[2], (double)amount));
        return new Color(resultColor.getRed(), resultColor.getGreen(), resultColor.getBlue(), MathUtils.interpolateInt(color1.getAlpha(), color2.getAlpha(), (double)amount));
    }

    public static Color fade(int speed, int index, Color color, float alpha) {
        float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), (float[])null);
        int angle = (int)((System.currentTimeMillis() / (long)speed + (long)index) % 360L);
        angle = (angle > 180 ? 360 - angle : angle) + 180;
        Color colorHSB = new Color(Color.HSBtoRGB(hsb[0], hsb[1], (float)angle / 360.0F));
        return new Color(colorHSB.getRed(), colorHSB.getGreen(), colorHSB.getBlue(), Math.max(0, Math.min(255, (int)(alpha * 255.0F))));
    }

    private static float getAnimationEquation(int index, int speed) {
        int angle = (int)((System.currentTimeMillis() / (long)speed + (long)index) % 360L);
        return (float)((angle > 180 ? 360 - angle : angle) + 180) / 360.0F;
    }

    public static int[] createColorArray(int color) {
        return new int[]{bitChangeColor(color, 16), bitChangeColor(color, 8), bitChangeColor(color, 0), bitChangeColor(color, 24)};
    }

    public static int getOppositeColor(int color) {
        int R = bitChangeColor(color, 0);
        int G = bitChangeColor(color, 8);
        int B = bitChangeColor(color, 16);
        int A = bitChangeColor(color, 24);
        R = 255 - R;
        G = 255 - G;
        B = 255 - B;
        return R + (G << 8) + (B << 16) + (A << 24);
    }

    private static int bitChangeColor(int color, int bitChange) {
        return color >> bitChange & 255;
    }
}
