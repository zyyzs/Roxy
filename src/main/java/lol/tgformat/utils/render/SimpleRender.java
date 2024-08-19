package lol.tgformat.utils.render;

import lol.tgformat.accessable.IMinecraft;
import lombok.experimental.UtilityClass;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

import java.awt.*;

/**
 * @author TG_format
 * @since 2024/7/13 下午1:27
 */
@UtilityClass
public class SimpleRender implements IMinecraft {

    public double delta;

    public double interpolate(final double current, final double old, final double scale) {
        return old + (current - old) * scale;
    }

    public float processFPS(final float defV) {
        final float defF = 1000;
        int limitFPS = Math.abs(Minecraft.getDebugFPS());
        return defV / (limitFPS <= 0 ? 1 : limitFPS / defF);
    }


    public void drawCircle(final float x, final float y, final float r, final float lineWidth, final boolean isFull, final int color) {
        drawCircle(x, y, r, 10, lineWidth, 360, isFull, color);
    }

    public void drawCircle(float cx, float cy, double r, final int segments, final float lineWidth, final int part, final boolean isFull, final int c) {
        GL11.glScalef(0.5F, 0.5F, 0.5F);
        r *= 2.0D;
        cx *= 2.0F;
        cy *= 2.0F;
        final float f2 = (c >> 24 & 0xFF) / 255.0F;
        final float f3 = (c >> 16 & 0xFF) / 255.0F;
        final float f4 = (c >> 8 & 0xFF) / 255.0F;
        final float f5 = (c & 0xFF) / 255.0F;
        GL11.glEnable(3042);
        GL11.glLineWidth(lineWidth);
        GL11.glDisable(3553);
        GL11.glEnable(2848);
        GL11.glBlendFunc(770, 771);
        GL11.glColor4f(f3, f4, f5, f2);
        GL11.glBegin(3);
        for (int i = segments - part; i <= segments; i++) {
            final double x = Math.sin(i * Math.PI / 180.0D) * r;
            final double y = Math.cos(i * Math.PI / 180.0D) * r;
            GL11.glVertex2d(cx + x, cy + y);
            if (isFull)
                GL11.glVertex2d(cx, cy);
        }
        GL11.glEnd();
        GL11.glDisable(2848);
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glScalef(2.0F, 2.0F, 2.0F);
    }

    public Color getBlendColor(final double current, final double max) {
        final long base = Math.round(max / 5.0);
        if (current >= base * 5L) {
            return new Color(15, 255, 15);
        }
        if (current >= base << 2) {
            return new Color(166, 255, 0);
        }
        if (current >= base * 3L) {
            return new Color(255, 191, 0);
        }
        if (current >= base << 1) {
            return new Color(255, 89, 0);
        }
        return new Color(255, 0, 0);
    }

    public void enableRender2D() {
        GL11.glEnable(3042);
        GL11.glDisable(2884);
        GL11.glDisable(3553);
        GL11.glEnable(2848);
        GL11.glBlendFunc(770, 771);
        GL11.glLineWidth(1.0F);
    }

    public void disableRender2D() {
        GL11.glDisable(3042);
        GL11.glEnable(2884);
        GL11.glEnable(3553);
        GL11.glDisable(2848);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
    }

    public  void setColor(final int colorHex) {
        final float alpha = (colorHex >> 24 & 255) / 255.0F;
        final float red = (colorHex >> 16 & 255) / 255.0F;
        final float green = (colorHex >> 8 & 255) / 255.0F;
        final float blue = (colorHex & 255) / 255.0F;
        GL11.glColor4f(red, green, blue, alpha);
    }
}

