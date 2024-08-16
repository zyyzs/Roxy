package lol.tgformat.ui.splash;

import lol.tgformat.accessable.IMinecraft;
import lol.tgformat.ui.utils.Animation;
import lol.tgformat.ui.utils.DecelerateAnimation;
import lol.tgformat.ui.utils.RenderUtil;
import lol.tgformat.ui.utils.RoundedUtil;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.shader.Framebuffer;
import net.netease.font.FontManager;
import org.lwjgl.opengl.GL11;
import tech.skidonion.obfuscator.annotations.Renamer;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import java.awt.*;

/**
 * @Author KuChaZi
 * @Date 2024/7/21 23:11
 * @ClassName: SplashScreen
 */
@Renamer
@StringEncryption
public class SplashScreen implements IMinecraft {
    private static Animation progressAnim;
    private static Animation fadeAnim;
    private static Framebuffer framebuffer;
    private static boolean isFadedOut = false;
    private static void drawScreen(float width, float height) {
        Gui.drawRect2(0, 0, width, height, Color.BLACK.getRGB());
        drawSplashBackground(width, height);
        float reduceAlpha = fadeAnim == null ? 0f : fadeAnim.getOutput().floatValue();
        int alphaValue = (int) ((1f - reduceAlpha) * 255f);

        if (alphaValue <= 0) {
            isFadedOut = true;
        }

        if (!isFadedOut) {
            FontManager.arial40.drawCenteredString("MOJANG", width / 2f, (height - FontManager.arial40.getHeight()) / 2f, new Color(255, 255, 255, alphaValue).getRGB());
        }

        float rectWidth = FontManager.arial40.getStringWidth("MOJANG") + 110;//
        float rectHeight = 5;
        float roundX = (width / 2f - rectWidth / 2f);
        float roundY = height / 2f - rectHeight / 2f + 40;

        if (progressAnim.timerUtil.getTime() >= 1800 && fadeAnim == null) fadeAnim = new DecelerateAnimation(600, 1);
        float progress = progressAnim.getOutput().floatValue();

        RoundedUtil.drawRoundOutline(roundX - 2, roundY - 2, rectWidth + 4, rectHeight + 4, (rectHeight / 2f) - .25f, 1, new Color(0, 0, 0, 0), new Color(255, 255, 255, alphaValue));
        RoundedUtil.drawRound(roundX, roundY, rectWidth * progress, rectHeight, (rectHeight / 2f) - .25f, new Color(255, 255, 255, alphaValue));
    }

    public static void drawScreen() {
        ScaledResolution sr = new ScaledResolution(mc);
        // Create the scale factor
        int scaleFactor = sr.getScaleFactor();
        // Bind the width and height to the framebuffer
        framebuffer = RenderUtil.createFrameBuffer(framebuffer);
        progressAnim = new DecelerateAnimation(2400, 1);
        while (!progressAnim.isDone()) {
            framebuffer.framebufferClear();
            framebuffer.bindFramebuffer(true);
            // Create the projected image to be rendered
            GlStateManager.matrixMode(GL11.GL_PROJECTION);
            GlStateManager.loadIdentity();
            GlStateManager.ortho(0.0D, sr.getScaledWidth(), sr.getScaledHeight(), 0.0D, 1000.0D, 3000.0D);
            GlStateManager.matrixMode(GL11.GL_MODELVIEW);
            GlStateManager.loadIdentity();
            GlStateManager.translate(0.0F, 0.0F, -2000.0F);
            GlStateManager.disableLighting();
            GlStateManager.disableFog();
            GlStateManager.disableDepth();
            GlStateManager.enableTexture2D();


            GlStateManager.color(0, 0, 0, 0);
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

            drawScreen(sr.getScaledWidth(), sr.getScaledHeight());

            // Unbind the width and height as it's no longer needed
            framebuffer.unbindFramebuffer();

            // Render the previously used frame buffer
            framebuffer.framebufferRender(sr.getScaledWidth() * scaleFactor, sr.getScaledHeight() * scaleFactor);

            // Update the texture to enable alpha drawing
            RenderUtil.setAlphaLimit(1);

            // Update the users screen
            mc.updateDisplay();
        }
    }

    public static void drawSplashBackground(float width, float height) {
        float alpha = fadeAnim == null ? 1.0f : 1.0f - fadeAnim.getOutput().floatValue();
        RenderUtil.resetColor();
        GlStateManager.color(1, 1, 1, alpha);
        Gui.drawRect(0, 0, width, height, new Color(239, 50, 61, (int) (alpha * 255)).getRGB());
    }
}
