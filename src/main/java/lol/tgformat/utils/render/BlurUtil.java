package lol.tgformat.utils.render;

import com.google.gson.JsonSyntaxException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.util.ResourceLocation;
import net.netease.utils.RenderUtil;

import java.io.IOException;

/**
 * @Author KuChaZi
 * @Date 2024/6/19 21:31
 * @ClassName: BlurUtil
 */
public class BlurUtil {
    private static Minecraft mc = Minecraft.getMinecraft();
    private static ShaderGroup shaderGroup;
    private static Framebuffer frbuffer;
    private static Framebuffer framebuffer;
    private static double lastFactor;
    private static double lastWidth;
    private static double lastHeight;
    private static double lastX;
    private static double lastY;
    private static double lastW;
    private static double lastH;
    private static double lastStrength;
    private static ResourceLocation blurShader;

    public static void init() {
        try {
            shaderGroup = new ShaderGroup(mc.getTextureManager(), mc.getResourceManager(), mc.getFramebuffer(), blurShader);
            shaderGroup.createBindFramebuffers(BlurUtil.mc.displayWidth, BlurUtil.mc.displayHeight);
            framebuffer = BlurUtil.shaderGroup.mainFramebuffer;
            frbuffer = shaderGroup.getFramebufferRaw("result");
        }
        catch (JsonSyntaxException | IOException e) {
            e.printStackTrace();
        }
    }

    private static void setValues(double strength, double x, double y, double w, double h, double width, double height) {
        if (strength == lastStrength && lastX == x && lastY == y && lastW == w && lastH == h) {
            return;
        }
        lastStrength = strength;
        lastX = x;
        lastY = y;
        lastW = w;
        lastH = h;
        for (int i = 0; i < 2; ++i) {
            shaderGroup.getShaders().get(i).getShaderManager().getShaderUniform("Radius").set((float)strength);
            shaderGroup.getShaders().get(i).getShaderManager().getShaderUniform("BlurXY").set((float)x, (float)(height - y - h));
            shaderGroup.getShaders().get(i).getShaderManager().getShaderUniform("BlurCoord").set((float)w, (float)h);
        }
    }

    public static void blurArea(float x, float y, float x2, float y2, float blurStrength) {
        int height;
        int width;
        ScaledResolution scaledResolution;
        int scaleFactor;
        float z;
        if (!OpenGlHelper.isFramebufferEnabled()) {
            return;
        }
        if (x > x2) {
            z = x;
            x = x2;
            x2 = z;
        }
        if (y > y2) {
            z = y;
            y = y2;
            y2 = z;
        }
        if (BlurUtil.sizeHasChanged(scaleFactor = (scaledResolution = new ScaledResolution(mc)).getScaleFactor(), width = scaledResolution.getScaledWidth(), height = scaledResolution.getScaledHeight()) || framebuffer == null || frbuffer == null || shaderGroup == null) {
            BlurUtil.init();
        }
        lastFactor = scaleFactor;
        lastWidth = width;
        lastHeight = height;
        float _w = x2 - x;
        float _h = y2 - y;
        BlurUtil.setValues(blurStrength, x, y, _w, _h, width, height);
        framebuffer.bindFramebuffer(true);
        shaderGroup.loadShaderGroup(BlurUtil.mc.timer.renderPartialTicks);
        mc.getFramebuffer().bindFramebuffer(true);
        StencilUtil.write(false);
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        RenderUtil.quickDrawRect(x, y, x2, y2);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        StencilUtil.erase(true);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        GlStateManager.pushMatrix();
        GlStateManager.colorMask(true, true, true, false);
        GlStateManager.disableDepth();
        GlStateManager.depthMask(false);
        GlStateManager.enableTexture2D();
        GlStateManager.disableLighting();
        GlStateManager.disableAlpha();
        frbuffer.bindFramebufferTexture();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        float f = width;
        float f1 = height;
        float f2 = (float)BlurUtil.frbuffer.framebufferWidth / (float)BlurUtil.frbuffer.framebufferTextureWidth;
        float f3 = (float)BlurUtil.frbuffer.framebufferHeight / (float)BlurUtil.frbuffer.framebufferTextureHeight;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        worldrenderer.pos(0.0, f1, 0.0).tex(0.0, 0.0).color(255, 255, 255, 255).endVertex();
        worldrenderer.pos(f, f1, 0.0).tex(f2, 0.0).color(255, 255, 255, 255).endVertex();
        worldrenderer.pos(f, 0.0, 0.0).tex(f2, f3).color(255, 255, 255, 255).endVertex();
        worldrenderer.pos(0.0, 0.0, 0.0).tex(0.0, f3).color(255, 255, 255, 255).endVertex();
        tessellator.draw();
        frbuffer.unbindFramebufferTexture();
        GlStateManager.enableDepth();
        GlStateManager.depthMask(true);
        GlStateManager.colorMask(true, true, true, true);
        GlStateManager.popMatrix();
        GlStateManager.disableBlend();
        StencilUtil.dispose();
        GlStateManager.enableAlpha();
    }

    private static boolean sizeHasChanged(int scaleFactor, int width, int height) {
        return lastFactor != (double)scaleFactor || lastWidth != (double)width || lastHeight != (double)height;
    }

    static {
        lastStrength = 5.0;
        blurShader = new ResourceLocation("bloodline/shader/blur.json");
    }
}
