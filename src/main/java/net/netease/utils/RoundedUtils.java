package net.netease.utils;

/**
 * @author TG_format
 * @since 2024/6/1 15:35
 */
import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

public class RoundedUtils {
    public static ShaderUtil roundedShader = new ShaderUtil("bloodline/shader/roundedRect.frag");
    public static ShaderUtil roundedOutlineShader = new ShaderUtil("bloodline/shader/roundRectOutline.frag");
    private static final ShaderUtil roundedTexturedShader = new ShaderUtil("bloodline/shader/roundRectTextured.frag");
    private static final ShaderUtil roundedGradientShader = new ShaderUtil("bloodline/shader/roundedRectGradient.frag");
    private static final ShaderUtil circleShader = new ShaderUtil("arc");

    public static void drawGradientRoundLR(float x, float y, float width, float height, float radius, Color color1, Color color2) {
        drawGradientRound(x, y, width, height, radius, color1, color2, color2, color1);
    }

    public static void drawCircle(float x, float y, float radius, float progress, int change, Color color, float smoothness) {
        GLUtil.startBlend();
        float borderThickness = 1.0F;
        circleShader.init();
        circleShader.setUniformf("radialSmoothness", smoothness);
        circleShader.setUniformf("radius", radius);
        circleShader.setUniformf("borderThickness", borderThickness);
        circleShader.setUniformf("progress", progress);
        circleShader.setUniformi("change", change);
        circleShader.setUniformf("color", (float)color.getRed() / 255.0F, (float)color.getGreen() / 255.0F, (float)color.getBlue() / 255.0F, (float)color.getAlpha() / 255.0F);
        float wh = radius + 10.0F;
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        circleShader.setUniformf("pos", (x + (wh / 2.0F - (radius + borderThickness) / 2.0F)) * (float)sr.getScaleFactor(), (float)Minecraft.getMinecraft().displayHeight - (radius + borderThickness) * (float)sr.getScaleFactor() - (y + (wh / 2.0F - (radius + borderThickness) / 2.0F)) * (float)sr.getScaleFactor());
        ShaderUtil.drawQuads(x, y, wh, wh);
        circleShader.unload();
        GLUtil.endBlend();
    }
    public static void color(final double red, final double green, final double blue, final double alpha) {
        GL11.glColor4d(red, green, blue, alpha);
    }
    public static void color(Color color) {
        if (color == null)
            color = Color.white;
        color(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, color.getAlpha() / 255F);
    }
    public static void circle(final double x, final double y, final double radius, final double sides, final boolean filled, final Color color) {
        polygon(x, y, radius, sides, filled, color);
    }
    public static void polygon(final double x, final double y, double sideLength, final double amountOfSides, final boolean filled, final Color color) {
        sideLength /= 2;
        start();
        if (color != null)
            color(color);
        if (!filled) GL11.glLineWidth(2);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        begin(filled ? GL11.GL_TRIANGLE_FAN : GL11.GL_LINE_STRIP);
        {
            for (double i = 0; i <= amountOfSides / 4; i++) {
                final double angle = i * 4 * (Math.PI * 2) / 360;
                vertex(x + (sideLength * Math.cos(angle)) + sideLength, y + (sideLength * Math.sin(angle)) + sideLength);
            }
        }
        end();
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        stop();
    }
    public static void start() {
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.disableTexture2D();
        GlStateManager.disableCull();
        GlStateManager.disableAlpha();
        GlStateManager.disableDepth();
    }
    public static void vertex(final double x, final double y) {
        GL11.glVertex2d(x, y);
    }
    public static void begin(final int glMode) {
        GL11.glBegin(glMode);
    }
    public static void end() {
        GL11.glEnd();
    }
    public static void stop() {
        GlStateManager.enableDepth();
        GlStateManager.enableAlpha();
        GlStateManager.enableCull();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.resetColor();
    }



    public static void drawRound(float x, float y, float width, float height, float radius, Color color) {
        RenderUtil.resetColor();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        roundedShader.init();
        setupRoundedRectUniforms(x, y, width, height, radius, roundedShader);
        roundedShader.setUniformf("color", (float)color.getRed() / 255.0F, (float)color.getGreen() / 255.0F, (float)color.getBlue() / 255.0F, (float)color.getAlpha() / 255.0F);
        ShaderUtil.drawQuads(x - 1.0F, y - 1.0F, width + 2.0F, height + 2.0F);
        roundedShader.unload();
        GlStateManager.disableBlend();
    }

    public static void drawRoundNoOffset(float x, float y, float width, float height, float radius, Color color) {
        ColorUtils.resetColor();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        roundedShader.init();

        setupRoundedRectUniforms(x, y, width, height, radius, roundedShader);
        roundedShader.setUniformf("color", color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);

        ShaderUtil.drawQuads(x, y, width, height);
        roundedShader.unload();
        GlStateManager.disableBlend();
    }
    public static void drawRound(float x, float y, float width, float height, float radius, boolean blur, Color color) {
        RenderUtil.resetColor();
        GlStateManager.enableBlend();
        GL11.glBlendFunc(770, 771);
        RenderUtil.setAlphaLimit(0.0F);
        roundedShader.init();
        setupRoundedRectUniforms(x, y, width, height, radius, roundedShader);
        roundedShader.setUniformi("blur", blur ? 1 : 0);
        roundedShader.setUniformf("color", (float)color.getRed() / 255.0F, (float)color.getGreen() / 255.0F, (float)color.getBlue() / 255.0F, (float)color.getAlpha() / 255.0F);
        ShaderUtil.drawQuads(x - 1.0F, y - 1.0F, width + 2.0F, height + 2.0F);
        roundedShader.unload();
        GlStateManager.disableBlend();
    }

    public static void drawGradientRound(float x, float y, float width, float height, float radius, Color bottomLeft, Color topLeft, Color bottomRight, Color topRight) {
        RenderUtil.resetColor();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        roundedGradientShader.init();
        setupRoundedRectUniforms(x, y, width, height, radius, roundedGradientShader);
        roundedGradientShader.setUniformf("color1", (float)topLeft.getRed() / 255.0F, (float)topLeft.getGreen() / 255.0F, (float)topLeft.getBlue() / 255.0F, (float)topLeft.getAlpha() / 255.0F);
        roundedGradientShader.setUniformf("color2", (float)bottomRight.getRed() / 255.0F, (float)bottomRight.getGreen() / 255.0F, (float)bottomRight.getBlue() / 255.0F, (float)bottomRight.getAlpha() / 255.0F);
        roundedGradientShader.setUniformf("color3", (float)bottomLeft.getRed() / 255.0F, (float)bottomLeft.getGreen() / 255.0F, (float)bottomLeft.getBlue() / 255.0F, (float)bottomLeft.getAlpha() / 255.0F);
        roundedGradientShader.setUniformf("color4", (float)topRight.getRed() / 255.0F, (float)topRight.getGreen() / 255.0F, (float)topRight.getBlue() / 255.0F, (float)topRight.getAlpha() / 255.0F);
        ShaderUtil.drawQuads(x - 1.0F, y - 1.0F, width + 2.0F, height + 2.0F);
        roundedGradientShader.unload();
        GlStateManager.disableBlend();
    }

    public static void drawRoundOutline(float x, float y, float width, float height, float radius, float outlineThickness, Color color, Color outlineColor) {
        RenderUtil.resetColor();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        roundedOutlineShader.init();
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        setupRoundedRectUniforms(x, y, width, height, radius, roundedOutlineShader);
        roundedOutlineShader.setUniformf("outlineThickness", outlineThickness * (float)sr.getScaleFactor());
        roundedOutlineShader.setUniformf("color", (float)color.getRed() / 255.0F, (float)color.getGreen() / 255.0F, (float)color.getBlue() / 255.0F, (float)color.getAlpha() / 255.0F);
        roundedOutlineShader.setUniformf("outlineColor", (float)outlineColor.getRed() / 255.0F, (float)outlineColor.getGreen() / 255.0F, (float)outlineColor.getBlue() / 255.0F, (float)outlineColor.getAlpha() / 255.0F);
        ShaderUtil.drawQuads(x - (2.0F + outlineThickness), y - (2.0F + outlineThickness), width + 4.0F + outlineThickness * 2.0F, height + 4.0F + outlineThickness * 2.0F);
        roundedOutlineShader.unload();
        GlStateManager.disableBlend();
    }

    public static void drawGradientCornerLR(float x, float y, float width, float height, float radius, Color topLeft, Color bottomRight) {
        Color mixedColor = ColorUtils.interpolateColorC(topLeft, bottomRight, 0.5F);
        drawGradientRound(x, y, width, height, radius, mixedColor, topLeft, bottomRight, mixedColor);
    }

    public static void drawGradientCornerRL(float x, float y, float width, float height, float radius, Color bottomLeft, Color topRight) {
        Color mixedColor = ColorUtils.interpolateColorC(topRight, bottomLeft, 0.5F);
        drawGradientRound(x, y, width, height, radius, bottomLeft, mixedColor, mixedColor, topRight);
    }

    public static void drawRoundTextured(float x, float y, float width, float height, float radius, float alpha) {
        RenderUtil.resetColor();
        roundedTexturedShader.init();
        roundedTexturedShader.setUniformi("textureIn", 0);
        setupRoundedRectUniforms(x, y, width, height, radius, roundedTexturedShader);
        roundedTexturedShader.setUniformf("alpha", alpha);
        ShaderUtil.drawQuads(x - 1.0F, y - 1.0F, width + 2.0F, height + 2.0F);
        roundedTexturedShader.unload();
        GlStateManager.disableBlend();
    }

    private static void setupRoundedRectUniforms(float x, float y, float width, float height, float radius, ShaderUtil roundedTexturedShader) {
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        roundedTexturedShader.setUniformf("location", x * (float)sr.getScaleFactor(), (float)Minecraft.getMinecraft().displayHeight - height * (float)sr.getScaleFactor() - y * (float)sr.getScaleFactor());
        roundedTexturedShader.setUniformf("rectSize", width * (float)sr.getScaleFactor(), height * (float)sr.getScaleFactor());
        roundedTexturedShader.setUniformf("radius", radius * (float)sr.getScaleFactor());
    }

    public static void round(float x, float y, float width, float height, float radius, Color color) {
        RenderUtil.resetColor();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        roundedShader.init();
        setupRoundedRectUniforms(x, y, width, height, radius, roundedShader);
        roundedShader.setUniformf("color", (float)color.getRed() / 255.0F, (float)color.getGreen() / 255.0F, (float)color.getBlue() / 255.0F, (float)color.getAlpha() / 255.0F);
        ShaderUtil.drawQuads(x - 1.0F, y - 1.0F, width + 2.0F, height + 2.0F);
        roundedShader.unload();
        GlStateManager.disableBlend();
    }

    public static void rect(float x, float y, float width, float height) {
        GL11.glBegin(7);
        GL11.glTexCoord2f(0.0F, 0.0F);
        GL11.glVertex2f(x, y);
        GL11.glTexCoord2f(0.0F, 1.0F);
        GL11.glVertex2f(x, y + height);
        GL11.glTexCoord2f(1.0F, 1.0F);
        GL11.glVertex2f(x + width, y + height);
        GL11.glTexCoord2f(1.0F, 0.0F);
        GL11.glVertex2f(x + width, y);
        GL11.glEnd();
    }

    public static void round(int x, int y, int width, int height, float radius, int rgb) {
        round((float)x, (float)y, (float)width, (float)height, radius, new Color(rgb));
    }
}
