package net.netease.utils;

import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author TG_format
 * @since 2024/6/1 15:47
 */
public class RippleAnimation {
    public final List<Ripple> ripples = new ArrayList();

    public void addRipple(float x, float y, float radius, float speed) {
        this.ripples.add(new RippleAnimation.Ripple(x, y, radius, speed));
    }

    public void mouseClicked(float mouseX, float mouseY, float speed) {
        this.ripples.add(new RippleAnimation.Ripple(mouseX, mouseY, 100.0F, speed));
    }

    public void mouseClicked(float mouseX, float mouseY) {
        this.ripples.add(new RippleAnimation.Ripple(mouseX, mouseY, 100.0F, 1.0F));
    }

    public void draw(float x, float y, float width, float height) {
        GL11.glDepthMask(true);
        GL11.glClearDepth(1.0D);
        GL11.glClear(256);
        GL11.glDepthFunc(519);
        GL11.glEnable(2929);
        GL11.glDepthMask(true);
        GL11.glColorMask(false, false, false, false);
        RenderUtil.drawRect((double)x, (double)y, (double)width, (double)height, -1);
        GL11.glDepthMask(false);
        GL11.glColorMask(true, true, true, true);
        GL11.glDepthFunc(514);
        Iterator var5 = this.ripples.iterator();

        while(var5.hasNext()) {
            RippleAnimation.Ripple c = (RippleAnimation.Ripple)var5.next();
            c.progress = AnimationUtil.animateSmooth(c.progress, c.topRadius, c.speed / 50.0F);
            RenderUtil.drawCircle2((double)c.x, (double)c.y, c.progress, (new Color(1.0F, 1.0F, 1.0F, (1.0F - Math.min(1.0F, Math.max(0.0F, c.progress / c.topRadius))) / 2.0F)).getRGB());
        }

        GL11.glDepthMask(true);
        GL11.glClearDepth(1.0D);
        GL11.glClear(256);
        GL11.glDepthFunc(515);
        GL11.glDepthMask(false);
        GL11.glDisable(2929);
    }

    public void draw(Runnable context) {
        GL11.glDepthMask(true);
        GL11.glClearDepth(1.0D);
        GL11.glClear(256);
        GL11.glDepthFunc(519);
        GL11.glEnable(2929);
        GL11.glDepthMask(true);
        GL11.glColorMask(false, false, false, false);
        context.run();
        GL11.glDepthMask(false);
        GL11.glColorMask(true, true, true, true);
        GL11.glDepthFunc(514);
        Iterator var2 = this.ripples.iterator();

        while(var2.hasNext()) {
            RippleAnimation.Ripple c = (RippleAnimation.Ripple)var2.next();
            c.progress = AnimationUtil.animateSmooth(c.progress, c.topRadius, c.speed / 50.0F);
            RenderUtil.drawCircle2((double)c.x, (double)c.y, c.progress, (new Color(1.0F, 1.0F, 1.0F, (1.0F - Math.min(1.0F, Math.max(0.0F, c.progress / c.topRadius))) / 2.0F)).getRGB());
        }

        GL11.glDepthMask(true);
        GL11.glClearDepth(1.0D);
        GL11.glClear(256);
        GL11.glDepthFunc(515);
        GL11.glDepthMask(false);
        GL11.glDisable(2929);
    }

    public static class Ripple {
        public float x;
        public float y;
        public float topRadius;
        public float speed;
        public float alpha;
        public float progress;
        public boolean complete;

        public Ripple(float x, float y, float rad, float speed) {
            this.x = x;
            this.y = y;
            this.alpha = 200.0F;
            this.topRadius = rad;
            this.speed = speed;
        }
    }
}
