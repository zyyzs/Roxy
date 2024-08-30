package lol.tgformat.module.impl.render;

import lol.tgformat.api.event.Listener;
import lol.tgformat.events.render.Render2DEvent;
import lol.tgformat.module.Module;
import lol.tgformat.module.ModuleType;
import lol.tgformat.module.values.impl.NumberSetting;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.opengl.GL11;

import static lol.tgformat.ui.clickgui.Utils.mc;

public class MotionBlur extends Module
{
    public final NumberSetting blurAmount;
    private Framebuffer framebuffer = null;
    private Framebuffer framebuffer_ = null;


    public MotionBlur() {
        super("MotionBlur", ModuleType.Render);
        this.blurAmount = new NumberSetting("Amount", 6.0, 10.0, 1.0, 1.0);
        add(blurAmount);
    }

    @Listener
    public void onRender2DEvent(Render2DEvent event) {
        this.setSuffix(String.valueOf(blurAmount.getValue().intValue()));
        if (mc.currentScreen == null && mc.theWorld != null) {
            if (OpenGlHelper.isFramebufferEnabled()) {
                int width = mc.getFramebuffer().framebufferWidth;
                int height = mc.getFramebuffer().framebufferHeight;

                GlStateManager.matrixMode(5889);
                GlStateManager.loadIdentity();
                GlStateManager.ortho(0, width, height, 0, 2000, 4000);
                GlStateManager.matrixMode(5888);
                GlStateManager.loadIdentity();
                GlStateManager.translate(0, 0, -2000);
                framebuffer = checkFramebufferSizes(framebuffer, width, height);
                framebuffer_ = checkFramebufferSizes(framebuffer_, width, height);
                framebuffer_.framebufferClear();
                framebuffer_.bindFramebuffer(true);
                OpenGlHelper.glBlendFunc(770, 771, 0, 1);
                GlStateManager.disableLighting();
                GlStateManager.disableFog();
                GlStateManager.disableBlend();
                mc.getFramebuffer().bindFramebufferTexture();
                GlStateManager.color(1, 1, 1, 1);
                drawTexturedRectNoBlend(0.0F, 0.0F, (float) width, (float) height, 0.0F, 1.0F, 0.0F, 1.0F, 9728);
                GlStateManager.enableBlend();
                framebuffer.bindFramebufferTexture();
                GlStateManager.color(1, 1, 1, blurAmount.getValue().intValue() / 10F - 0.1f);
                drawTexturedRectNoBlend(0, 0, (float) width, (float) height, 0, 1, 1, 0, 9728);
                mc.getFramebuffer().bindFramebuffer(true);
                framebuffer_.bindFramebufferTexture();
                GlStateManager.color(1, 1, 1, 1);
                GlStateManager.enableBlend();
                OpenGlHelper.glBlendFunc(770, 771, 1, 771);
                drawTexturedRectNoBlend(0.0F, 0.0F, (float) width, (float) height, 0.0F, 1.0F, 0.0F, 1.0F, 9728);
                Framebuffer tempBuff = this.framebuffer;
                framebuffer = this.framebuffer_;
                framebuffer_ = tempBuff;
            }
        }
    }

    private static Framebuffer checkFramebufferSizes(Framebuffer framebuffer, int width, int height) {
        if (framebuffer == null || framebuffer.framebufferWidth != width || framebuffer.framebufferHeight != height) {
            if (framebuffer == null) {
                framebuffer = new Framebuffer(width, height, true);
            }
            else {
                framebuffer.createBindFramebuffer(width, height);
            }

            framebuffer.setFramebufferFilter(9728);
        }

        return framebuffer;
    }

    public static void drawTexturedRectNoBlend(float x, float y, float width, float height, float uMin, float uMax, float vMin, float vMax, int filter) {
        GlStateManager.enableTexture2D();
        GL11.glTexParameteri(3553, 10241, filter);
        GL11.glTexParameteri(3553, 10240, filter);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();

        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldrenderer.pos(x, (y + height), 0.0D).tex(uMin, vMax).endVertex();
        worldrenderer.pos((x + width), (y + height), 0.0D).tex(uMax, vMax).endVertex();
        worldrenderer.pos((x + width), y, 0.0D).tex(uMax, vMin).endVertex();
        worldrenderer.pos(x, y, 0.0D).tex(uMin, vMin).endVertex();
        tessellator.draw();
        GL11.glTexParameteri(3553, 10241, 9728);
        GL11.glTexParameteri(3553, 10240, 9728);
    }
}