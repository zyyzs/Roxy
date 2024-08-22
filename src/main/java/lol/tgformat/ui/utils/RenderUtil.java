package lol.tgformat.ui.utils;

import lol.tgformat.ui.clickgui.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.netease.utils.ColorUtils;
import net.netease.utils.GLUtil;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import javax.vecmath.Vector2f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector4d;
import java.awt.*;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ConcurrentModificationException;
import java.util.List;

import static lol.tgformat.ui.utils.MathUtils.interpolate;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_LINE_SMOOTH;

/**
 * @author TG_format
 * @since 2024/6/9 下午6:55
 */
public class RenderUtil implements Utils {
    public static void drawTargetCapsule(Entity entity, double rad, boolean shade, Color color) {
        GL11.glPushMatrix();
        GL11.glDisable(3553);
        GL11.glEnable(2848);
        GL11.glEnable(2832);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glHint(3154, 4354);
        GL11.glHint(3155, 4354);
        GL11.glHint(3153, 4354);
        GL11.glDepthMask(false);
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.0F);
        if (shade) GL11.glShadeModel(GL11.GL_SMOOTH);
        GlStateManager.disableCull();
        GL11.glBegin(GL11.GL_TRIANGLE_STRIP);
        final Minecraft mc = Minecraft.getMinecraft();
        final double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * mc.timer.renderPartialTicks - RenderManager.renderPosX;
        final double y = (entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * mc.timer.renderPartialTicks - RenderManager.renderPosY) + Math.sin(System.currentTimeMillis() / 2E+2) + 1;
        final double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * mc.timer.renderPartialTicks - RenderManager.renderPosZ;

        Color c;

        for (float i = 0; i < Math.PI * 2; i += (float) (Math.PI * 2 / 64.F)) {
            final double vecX = x + rad * Math.cos(i);
            final double vecZ = z + rad * Math.sin(i);

            c = color;

            if (shade) {
                GL11.glColor4f(c.getRed() / 255.F,
                        c.getGreen() / 255.F,
                        c.getBlue() / 255.F,
                        0
                );
                GL11.glVertex3d(vecX, y - Math.cos(System.currentTimeMillis() / 2E+2) / 2.0F, vecZ);
                GL11.glColor4f(c.getRed() / 255.F,
                        c.getGreen() / 255.F,
                        c.getBlue() / 255.F,
                        c.getAlpha() / 255.F
                );
            }
            GL11.glVertex3d(vecX, y, vecZ);
        }

        GL11.glEnd();
        if (shade) GL11.glShadeModel(GL11.GL_FLAT);
        GL11.glDepthMask(true);
        GL11.glEnable(2929);
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
        GlStateManager.enableCull();
        GL11.glDisable(2848);
        GL11.glDisable(2848);
        GL11.glEnable(2832);
        GL11.glEnable(3553);
        GL11.glPopMatrix();
        GL11.glColor3f(255, 255, 255);
    }
    public static int hexColor(int red, int green, int blue) {
        return hexColor(red, green, blue, 255);
    }

    public static int hexColor(int red, int green, int blue, int alpha) {
        return alpha << 24 | red << 16 | green << 8 | blue;
    }
    public static void drawPlayerHead(ResourceLocation skin, int x2, int y2, int width, int height) {
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        mc.getTextureManager().bindTexture(skin);
        Gui.drawScaledCustomSizeModalRect(x2, y2, 8.0f, 8.0f, 8, 8, width, height, 64.0f, 64.0f);
    }
    public static void renderBreadCrumbs(List<Vec3> vec3s, Color color, Color color2) {
        Color c1 = ColorUtils.interpolateColorsBackAndForth(50, 3, color, color2, false);
        Color c2 = ColorUtils.interpolateColorsBackAndForth(50, 3, color2, color, false);
        GlStateManager.disableDepth();
        glEnable(GL_BLEND);
        glDisable(GL_TEXTURE_2D);
        glEnable(GL_LINE_SMOOTH);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        int i = 0;
        try {
            for (final Vec3 v : vec3s) {

                i++;

                boolean draw = true;

                final double x = v.xCoord - mc.getRenderManager().getRenderPosX();
                final double y = v.yCoord - mc.getRenderManager().getRenderPosY();
                final double z = v.zCoord - mc.getRenderManager().getRenderPosZ();

                final double distanceFromPlayer = mc.thePlayer.getDistance(v.xCoord, v.yCoord - 1, v.zCoord);
                int quality = (int) (distanceFromPlayer * 4 + 10);

                if (quality > 350)
                    quality = 350;

                if (i % 10 != 0 && distanceFromPlayer > 25) {
                    draw = false;
                }

                if (i % 3 == 0 && distanceFromPlayer > 15) {
                    draw = false;
                }

                if (draw) {

                    glPushMatrix();
                    glTranslated(x, y, z);

                    final float scale = 0.04f;
                    glScalef(-scale, -scale, -scale);

                    glRotated(-(mc.getRenderManager()).playerViewY, 0.0D, 1.0D, 0.0D);
                    glRotated((mc.getRenderManager()).playerViewX, 1.0D, 0.0D, 0.0D);

                    RenderUtil.drawFilledCircle(0, 0, 0.7, c1.hashCode(), quality);

                    if (distanceFromPlayer < 4)
                        RenderUtil.drawFilledCircle(0, 0, 1.4, new Color(c1.getRed(), c1.getGreen(), c1.getBlue(), 50).hashCode(), quality);

                    if (distanceFromPlayer < 20)
                        RenderUtil.drawFilledCircle(0, 0, 2.3, new Color(c2.getRed(), c2.getGreen(), c2.getBlue(), 30).hashCode(), quality);

                    glScalef(0.8f, 0.8f, 0.8f);

                    glPopMatrix();

                }

            }
        } catch (final ConcurrentModificationException ignored) {
        }

        glDisable(GL_LINE_SMOOTH);
        glEnable(GL_TEXTURE_2D);
        glDisable(GL_BLEND);
        GlStateManager.enableDepth();

        glColor3d(255, 255, 255);
    }
    public static void drawTargetESP2D(float x, float y, Color color, Color color2, Color color3, Color color4, float scale, int index) {
        long millis = System.currentTimeMillis() + (long)index * 400L;
        double angle = MathHelper.clamp_double((Math.sin((double)millis / 150.0) + 1.0) / 2.0 * 30.0, 0.0, 30.0);
        double scaled = MathHelper.clamp_double((Math.sin((double)millis / 500.0) + 1.0) / 2.0, 0.8, 1.0);
        double rotate = MathHelper.clamp_double((Math.sin((double)millis / 1000.0) + 1.0) / 2.0 * 360.0, 0.0, 360.0);
        rotate += 45.0 - (angle - 15.0);
        float size = 128.0F * scale * (float)scaled;
        float x2 = (x -= size / 2.0F) + size;
        float y2 = (y -= size / 2.0F) + size;
        GlStateManager.pushMatrix();
        customRotatedObject2D(x, y, size, size, (float)rotate);
        GL11.glDisable(3008);
        GlStateManager.depthMask(false);
        GlStateManager.enableBlend();
        GlStateManager.shadeModel(7425);
        GlStateManager.tryBlendFuncSeparate(770, 1, 1, 0);
        drawESPImage(new ResourceLocation("bloodline/rectangle.png"), x, y, x2, y2, color, color2, color3, color4);
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.resetColor();
        GlStateManager.shadeModel(7424);
        GlStateManager.depthMask(true);
        GL11.glEnable(3008);
        GlStateManager.popMatrix();
    }
    private static void drawESPImage(ResourceLocation resource, double x, double y, double x2, double y2, Color c, Color c2, Color c3, Color c4) {
        mc.getTextureManager().bindTexture(resource);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer bufferbuilder = tessellator.getWorldRenderer();
        bufferbuilder.begin(9, DefaultVertexFormats.POSITION_TEX_COLOR);
        bufferbuilder.pos(x, y2, 0.0).tex(0.0, 1.0).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex();
        bufferbuilder.pos(x2, y2, 0.0).tex(1.0, 1.0).color(c2.getRed(), c2.getGreen(), c2.getBlue(), c2.getAlpha()).endVertex();
        bufferbuilder.pos(x2, y, 0.0).tex(1.0, 0.0).color(c3.getRed(), c3.getGreen(), c3.getBlue(), c3.getAlpha()).endVertex();
        bufferbuilder.pos(x, y, 0.0).tex(0.0, 0.0).color(c4.getRed(), c4.getGreen(), c4.getBlue(), c4.getAlpha()).endVertex();
        GlStateManager.shadeModel(7425);
        GlStateManager.depthMask(false);
        tessellator.draw();
        GlStateManager.depthMask(true);
        GlStateManager.shadeModel(7424);
    }

    public static void customRotatedObject2D(float oXpos, float oYpos, float oWidth, float oHeight, float rotate) {
        GL11.glTranslated((oXpos + oWidth / 2.0F), (oYpos + oHeight / 2.0F), 0.0);
        GL11.glRotated(rotate, 0.0, 0.0, 1.0);
        GL11.glTranslated((-oXpos - oWidth / 2.0F), (-oYpos - oHeight / 2.0F), 0.0);
    }





    public static void drawFilledCircle(final int x, final int y, final double r, final int c, final int quality) {
        final float f = ((c >> 24) & 0xff) / 255F;
        final float f1 = ((c >> 16) & 0xff) / 255F;
        final float f2 = ((c >> 8) & 0xff) / 255F;
        final float f3 = (c & 0xff) / 255F;

        glColor4f(f1, f2, f3, f);
        glBegin(GL_TRIANGLE_FAN);

        for (int i = 0; i <= 360 / quality; i++) {
            final double x2 = Math.sin(((i * quality * Math.PI) / 180)) * r;
            final double y2 = Math.cos(((i * quality * Math.PI) / 180)) * r;
            glVertex2d(x + x2, y + y2);
        }

        glEnd();
    }
    public static Vector2f targetESPSPos(EntityLivingBase entity) {
        EntityRenderer entityRenderer = mc.entityRenderer;
        float partialTicks = mc.timer.renderPartialTicks;
        int scaleFactor = (new ScaledResolution(mc)).getScaleFactor();
        double x = interpolate(entity.prevPosX, entity.posX, (double)partialTicks);
        double y = interpolate(entity.prevPosY, entity.posY, (double)partialTicks);
        double z = interpolate(entity.prevPosZ, entity.posZ, (double)partialTicks);
        double height = (double)(entity.height / (entity.isChild() ? 1.75F : 1.0F) / 2.0F);
        double width = 0.0;
        AxisAlignedBB aabb = new AxisAlignedBB(x - 0.0, y, z - 0.0, x + 0.0, y + height, z + 0.0);
        Vector3d[] vectors = new Vector3d[]{new Vector3d(aabb.minX, aabb.minY, aabb.minZ), new Vector3d(aabb.minX, aabb.maxY, aabb.minZ), new Vector3d(aabb.maxX, aabb.minY, aabb.minZ), new Vector3d(aabb.maxX, aabb.maxY, aabb.minZ), new Vector3d(aabb.minX, aabb.minY, aabb.maxZ), new Vector3d(aabb.minX, aabb.maxY, aabb.maxZ), new Vector3d(aabb.maxX, aabb.minY, aabb.maxZ), new Vector3d(aabb.maxX, aabb.maxY, aabb.maxZ)};
        entityRenderer.setupCameraTransform(partialTicks, 0);
        Vector4d position = null;
        Vector3d[] vecs3 = vectors;
        int vecLength = vectors.length;

        for(int vecI = 0; vecI < vecLength; ++vecI) {
            Vector3d vector = vecs3[vecI];
            vector = project2D(scaleFactor, vector.x - mc.getRenderManager().viewerPosX, vector.y - mc.getRenderManager().viewerPosY, vector.z - mc.getRenderManager().viewerPosZ);
            if (vector != null && vector.z >= 0.0 && vector.z < 1.0) {
                if (position == null) {
                    position = new Vector4d(vector.x, vector.y, vector.z, 0.0);
                }

                position.x = Math.min(vector.x, position.x);
                position.y = Math.min(vector.y, position.y);
                position.z = Math.max(vector.x, position.z);
                position.w = Math.max(vector.y, position.w);
            }
        }

        entityRenderer.setupOverlayRendering();
        if (position != null) {
            return new Vector2f((float)position.x, (float)position.y);
        } else {
            return null;
        }
    }
    private static Vector3d project2D(int scaleFactor, double x, double y, double z) {
        IntBuffer viewport = GLAllocation.createDirectIntBuffer(16);
        FloatBuffer modelView = GLAllocation.createDirectFloatBuffer(16);
        FloatBuffer projection = GLAllocation.createDirectFloatBuffer(16);
        FloatBuffer vector = GLAllocation.createDirectFloatBuffer(4);
        GL11.glGetFloat(2982, modelView);
        GL11.glGetFloat(2983, projection);
        GL11.glGetInteger(2978, viewport);
        return GLU.gluProject((float)x, (float)y, (float)z, modelView, projection, viewport, vector) ? new Vector3d((double)(vector.get(0) / (float)scaleFactor), (double)(((float) Display.getHeight() - vector.get(1)) / (float)scaleFactor), (double)vector.get(2)) : null;
    }

    public static void drawImage(ResourceLocation resourceLocation, float x, float y, float imgWidth, float imgHeight) {
        GLUtil.startBlend();
        mc.getTextureManager().bindTexture(resourceLocation);
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0, 0, imgWidth, imgHeight, imgWidth, imgHeight);
        GLUtil.endBlend();
    }

    public static void glColor(int color) {
        float f = (float)(color >> 24 & 0xFF) / 255.0f;
        float f1 = (float)(color >> 16 & 0xFF) / 255.0f;
        float f2 = (float)(color >> 8 & 0xFF) / 255.0f;
        float f3 = (float)(color & 0xFF) / 255.0f;
        GL11.glColor4f(f1, f2, f3, f);
    }
    public static Color getColor(int color) {
        int f = color >> 24 & 0xFF;
        int f1 = color >> 16 & 0xFF;
        int f2 = color >> 8 & 0xFF;
        int f3 = color & 0xFF;
        return new Color(f1, f2, f3, f);
    }
    public static void drawAxisAlignedBB(AxisAlignedBB axisAlignedBB, boolean outline, int color) {
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(3042);
        GL11.glLineWidth(2.0f);
        GL11.glDisable(3553);
        GL11.glDisable(2929);
        GL11.glDepthMask(false);
        RenderUtil.color(color);
        RenderGlobal.renderCustomBoundingBox(axisAlignedBB, outline, true);
        GlStateManager.resetColor();
        GL11.glEnable(3553);
        GL11.glEnable(2929);
        GL11.glDepthMask(true);
        GL11.glDisable(3042);
    }
    public static Framebuffer createFrameBuffer(Framebuffer framebuffer) {
        return createFrameBuffer(framebuffer, false);
    }

    public static Framebuffer createFrameBuffer(Framebuffer framebuffer, boolean depth) {
        if (needsNewFramebuffer(framebuffer)) {
            if (framebuffer != null) {
                framebuffer.deleteFramebuffer();
            }
            return new Framebuffer(mc.displayWidth, mc.displayHeight, depth);
        }
        return framebuffer;
    }

    public static boolean needsNewFramebuffer(Framebuffer framebuffer) {
        return framebuffer == null || framebuffer.framebufferWidth != mc.displayWidth || framebuffer.framebufferHeight != mc.displayHeight;
    }

    public static void drawTracerLine(Entity entity, float width, Color color, float alpha) {
        float ticks = mc.timer.renderPartialTicks;
        glPushMatrix();

        glLoadIdentity();

        mc.entityRenderer.orientCamera(ticks);
        double[] pos = ESPUtil.getInterpolatedPos(entity);

        glDisable(GL_DEPTH_TEST);
        GLUtil.setup2DRendering();

        double yPos = pos[1] + entity.height / 2f;
        glEnable(GL_LINE_SMOOTH);
        glLineWidth(width);

        glBegin(GL_LINE_STRIP);
        color(color.getRGB(), alpha);
        glVertex3d(pos[0], yPos, pos[2]);
        glVertex3d(0, mc.thePlayer.getEyeHeight(), 0);
        glEnd();

        glDisable(GL_LINE_SMOOTH);
        glEnable(GL_DEPTH_TEST);

        GLUtil.end2DRendering();

        glPopMatrix();
    }

    public static void drawMicrosoftLogo(float x, float y, float size, float spacing, float alpha) {
        float rectSize = size / 2f - spacing;
        int alphaVal = (int) (255 * alpha);
        Gui.drawRect2(x, y, rectSize, rectSize, new Color(244, 83, 38, alphaVal).getRGB());
        Gui.drawRect2(x + rectSize + spacing, y, rectSize, rectSize, new Color(130, 188, 6, alphaVal).getRGB());
        Gui.drawRect2(x, y + spacing + rectSize, rectSize, rectSize, new Color(5, 166, 241, alphaVal).getRGB());
        Gui.drawRect2(x + rectSize + spacing, y + spacing + rectSize, rectSize, rectSize, new Color(254, 186, 7, alphaVal).getRGB());
    }

    public static void drawMicrosoftLogo(float x, float y, float size, float spacing) {
        drawMicrosoftLogo(x, y, size, spacing, 1f);
    }


    public static void fixBlendIssues() {
        GlStateManager.color(1, 1, 1, 1);
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(516, 0.0f);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    }

    public static void drawUnfilledCircle(double x, double y, float radius, float lineWidth, int color) {
        GLUtil.setup2DRendering();
        color(color);
        glLineWidth(lineWidth);
        glEnable(GL_LINE_SMOOTH);
        glBegin(GL_POINT_BIT);

        int i = 0;
        while (i <= 360) {
            glVertex2d(x + Math.sin((double) i * 3.141526 / 180.0) * (double) radius, y + Math.cos((double) i * 3.141526 / 180.0) * (double) radius);
            ++i;
        }

        glEnd();
        glDisable(GL_LINE_SMOOTH);
        GLUtil.end2DRendering();
    }


    public static double ticks = 0;
    public static long lastFrame = 0;

    public static void drawCircle(Entity entity, float partialTicks, double rad, int color, float alpha) {
        /*Got this from the people I made the Gui for*/
        ticks += .004 * (System.currentTimeMillis() - lastFrame);

        lastFrame = System.currentTimeMillis();

        glPushMatrix();
        glDisable(GL_TEXTURE_2D);
        glEnable(GL_BLEND);
        GlStateManager.color(1, 1, 1, 1);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glDisable(GL_DEPTH_TEST);
        glDepthMask(false);
        glShadeModel(GL_SMOOTH);
        GlStateManager.disableCull();

        final double x = interpolate(entity.lastTickPosX, entity.posX, mc.timer.renderPartialTicks) - mc.getRenderManager().renderPosX;
        final double y = interpolate(entity.lastTickPosY, entity.posY, mc.timer.renderPartialTicks) - mc.getRenderManager().renderPosY + Math.sin(ticks) + 1;
        final double z = interpolate(entity.lastTickPosZ, entity.posZ, mc.timer.renderPartialTicks) - mc.getRenderManager().renderPosZ;

        glBegin(GL_TRIANGLE_STRIP);

        for (float i = 0; i < (Math.PI * 2); i += (Math.PI * 2) / 64.F) {

            final double vecX = x + rad * Math.cos(i);
            final double vecZ = z + rad * Math.sin(i);

            color(color, 0);

            glVertex3d(vecX, y - Math.sin(ticks + 1) / 2.7f, vecZ);

            color(color, .52f * alpha);


            glVertex3d(vecX, y, vecZ);
        }

        glEnd();


        glEnable(GL_LINE_SMOOTH);
        glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
        glLineWidth(1.5f);
        glBegin(GL_LINE_STRIP);
        GlStateManager.color(1, 1, 1, 1);
        color(color, .5f * alpha);
        for (int i = 0; i <= 180; i++) {
            glVertex3d(x - Math.sin(i * MathHelper.PI2 / 90) * rad, y, z + Math.cos(i * MathHelper.PI2 / 90) * rad);
        }
        glEnd();

        glShadeModel(GL_FLAT);
        glDepthMask(true);
        glEnable(GL_DEPTH_TEST);
        GlStateManager.enableCull();
        glDisable(GL_LINE_SMOOTH);
        glEnable(GL_TEXTURE_2D);
        glPopMatrix();
        glColor4f(1f, 1f, 1f, 1f);
    }

    //From rise, alan gave me this
    public static void drawFilledCircleNoGL(int x, int y, double r, int c, int quality) {
        RenderUtil.resetColor();
        RenderUtil.setAlphaLimit(0);
        GLUtil.setup2DRendering();
        color(c);
        glBegin(GL_TRIANGLE_FAN);

        for (int i = 0; i <= 360 / quality; i++) {
            final double x2 = Math.sin(((i * quality * Math.PI) / 180)) * r;
            final double y2 = Math.cos(((i * quality * Math.PI) / 180)) * r;
            glVertex2d(x + x2, y + y2);
        }

        glEnd();
        GLUtil.end2DRendering();
    }

    public static void renderBoundingBox(EntityLivingBase entityLivingBase, Color color, float alpha) {
        AxisAlignedBB bb = ESPUtil.getInterpolatedBoundingBox(entityLivingBase);
        GlStateManager.pushMatrix();
        GLUtil.setup2DRendering();
        GLUtil.enableCaps(GL_BLEND, GL_POINT_SMOOTH, GL_POLYGON_SMOOTH, GL_LINE_SMOOTH);

        glDisable(GL_DEPTH_TEST);
        glDepthMask(false);
        glLineWidth(1);
        float actualAlpha = .3f * alpha;
        glColor4f(color.getRed(), color.getGreen(), color.getBlue(), actualAlpha);
        color(color.getRGB(), actualAlpha);
        RenderGlobal.renderCustomBoundingBox(bb, true, true);
        glDepthMask(true);
        glEnable(GL_DEPTH_TEST);

        GLUtil.disableCaps();
        GLUtil.end2DRendering();

        GlStateManager.popMatrix();
    }

    public static void circleNoSmoothRGB(double x, double y, double radius, int color) {
        radius /= 2;
        glEnable(GL_BLEND);
        glDisable(GL_TEXTURE_2D);
        glDisable(GL_CULL_FACE);
        color(color);
        glBegin(GL_TRIANGLE_FAN);

        for (double i = 0; i <= 360; i++) {
            double angle = (i * (Math.PI * 2)) / 360;
            glVertex2d(x + (radius * Math.cos(angle)) + radius, y + (radius * Math.sin(angle)) + radius);
        }

        glEnd();
        glEnable(GL_CULL_FACE);
        glEnable(GL_TEXTURE_2D);
    }


    public static void drawBorderedRect(float x, float y, float width, float height, final float outlineThickness, int rectColor, int outlineColor) {
        Gui.drawRect2(x, y, width, height, rectColor);
        glEnable(GL_LINE_SMOOTH);
        color(outlineColor);

        GLUtil.setup2DRendering();

        glLineWidth(outlineThickness);
        float cornerValue = (float) (outlineThickness * .19);

        glBegin(GL_LINES);
        glVertex2d(x, y - cornerValue);
        glVertex2d(x, y + height + cornerValue);
        glVertex2d(x + width, y + height + cornerValue);
        glVertex2d(x + width, y - cornerValue);
        glVertex2d(x, y);
        glVertex2d(x + width, y);
        glVertex2d(x, y + height);
        glVertex2d(x + width, y + height);
        glEnd();

        GLUtil.end2DRendering();

        glDisable(GL_LINE_SMOOTH);
    }

    // Bad rounded rect method but the shader one requires scaling that sucks
    public static void renderRoundedRect(float x, float y, float width, float height, float radius, int color) {
        RenderUtil.drawGoodCircle(x + radius, y + radius, radius, color);
        RenderUtil.drawGoodCircle(x + width - radius, y + radius, radius, color);
        RenderUtil.drawGoodCircle(x + radius, y + height - radius, radius, color);
        RenderUtil.drawGoodCircle(x + width - radius, y + height - radius, radius, color);

        Gui.drawRect2(x + radius, y, width - radius * 2, height, color);
        Gui.drawRect2(x, y + radius, width, height - radius * 2, color);
    }


    // Scales the data that you put in the runnable
    public static void scaleStart(float x, float y, float scale) {
        glPushMatrix();
        glTranslatef(x, y, 0);
        glScalef(scale, scale, 1);
        glTranslatef(-x, -y, 0);
    }

    public static void scaleEnd() {
        glPopMatrix();
    }


    // TODO: Replace this with a shader as GL_POINTS is not consistent with gui scales
    public static void drawGoodCircle(double x, double y, float radius, int color) {
        color(color);
        GLUtil.setup2DRendering();

        glEnable(GL_POINT_SMOOTH);
        glHint(GL_POINT_SMOOTH_HINT, GL_NICEST);
        glPointSize(radius * (2 * mc.gameSettings.guiScale));

        glBegin(GL_POINTS);
        glVertex2d(x, y);
        glEnd();

        GLUtil.end2DRendering();
    }

    public static void fakeCircleGlow(float posX, float posY, float radius, Color color, float maxAlpha) {
        setAlphaLimit(0);
        glShadeModel(GL_SMOOTH);
        GLUtil.setup2DRendering();
        color(color.getRGB(), maxAlpha);

        glBegin(GL_TRIANGLE_FAN);
        glVertex2d(posX, posY);
        color(color.getRGB(), 0);
        for (int i = 0; i <= 100; i++) {
            double angle = (i * .06283) + 3.1415;
            double x2 = Math.sin(angle) * radius;
            double y2 = Math.cos(angle) * radius;
            glVertex2d(posX + x2, posY + y2);
        }
        glEnd();

        GLUtil.end2DRendering();
        glShadeModel(GL_FLAT);
        setAlphaLimit(1);
    }

    // animation for sliders and stuff
    public static double animate(double endPoint, double current, double speed) {
        boolean shouldContinueAnimation = endPoint > current;
        if (speed < 0.0D) {
            speed = 0.0D;
        } else if (speed > 1.0D) {
            speed = 1.0D;
        }

        double dif = Math.max(endPoint, current) - Math.min(endPoint, current);
        double factor = dif * speed;
        return current + (shouldContinueAnimation ? factor : -factor);
    }

    public static void rotateStart(float x, float y, float width, float height, float rotation) {
        glPushMatrix();
        x += width / 2;
        y += height / 3;
        glTranslatef(x, y, 0);
        glRotatef(rotation, 0, 0, 1);
        glTranslatef(-x, -y, 0);
    }

    public static void rotateStartReal(float x, float y, float width, float height, float rotation) {
        glPushMatrix();
        glTranslatef(x, y, 0);
        glRotatef(rotation, 0, 0, 1);
        glTranslatef(-x, -y, 0);
    }

    public static void rotateEnd() {
        glPopMatrix();
    }

    // Arrow for clickgui
    public static void drawClickGuiArrow(float x, float y, float size, Animation animation, int color) {
        glTranslatef(x, y, 0);
        color(color);

        GLUtil.setup2DRendering();

        glBegin(GL_TRIANGLE_STRIP);
        double interpolation = interpolate(0.0, size / 2.0, animation.getOutput().floatValue());
        if (animation.getOutput().floatValue() >= .48) {
            glVertex2d(size / 2f, interpolate(size / 2.0, 0.0, animation.getOutput().floatValue()));
        }
        glVertex2d(0, interpolation);

        if (animation.getOutput().floatValue() < .48) {
            glVertex2d(size / 2f, interpolate(size / 2.0, 0.0, animation.getOutput().floatValue()));
        }
        glVertex2d(size, interpolation);

        glEnd();

        GLUtil.end2DRendering();

        glTranslatef(-x, -y, 0);
    }

    // Draws a circle using traditional methods of rendering
    public static void drawCircleNotSmooth(double x, double y, double radius, int color) {
        radius /= 2;
        GLUtil.setup2DRendering();
        glDisable(GL_CULL_FACE);
        color(color);
        glBegin(GL_TRIANGLE_FAN);

        for (double i = 0; i <= 360; i++) {
            double angle = i * .01745;
            glVertex2d(x + (radius * Math.cos(angle)) + radius, y + (radius * Math.sin(angle)) + radius);
        }

        glEnd();
        glEnable(GL_CULL_FACE);
        GLUtil.end2DRendering();
    }

    public static void scissor(double x, double y, double width, double height, Runnable data) {
        glEnable(GL_SCISSOR_TEST);
        scissor(x, y, width, height);
        data.run();
        glDisable(GL_SCISSOR_TEST);
    }

    public static void scissor(double x, double y, double width, double height) {
        ScaledResolution sr = new ScaledResolution(mc);
        final double scale = sr.getScaleFactor();
        double finalHeight = height * scale;
        double finalY = (sr.getScaledHeight() - y) * scale;
        double finalX = x * scale;
        double finalWidth = width * scale;
        glScissor((int) finalX, (int) (finalY - finalHeight), (int) finalWidth, (int) finalHeight);
    }

    public static void scissorStart(double x, double y, double width, double height) {
        glEnable(GL_SCISSOR_TEST);
        ScaledResolution sr = new ScaledResolution(mc);
        final double scale = sr.getScaleFactor();
        double finalHeight = height * scale;
        double finalY = (sr.getScaledHeight() - y) * scale;
        double finalX = x * scale;
        double finalWidth = width * scale;
        glScissor((int) finalX, (int) (finalY - finalHeight), (int) finalWidth, (int) finalHeight);
    }

    public static void scissorEnd() {
        glDisable(GL_SCISSOR_TEST);
    }


    // This will set the alpha limit to a specified value ranging from 0-1
    public static void setAlphaLimit(float limit) {
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(GL_GREATER, (float) (limit * .01));
    }

    // This method colors the next avalible texture with a specified alpha value ranging from 0-1
    public static void color(int color, float alpha) {
        float r = (float) (color >> 16 & 255) / 255.0F;
        float g = (float) (color >> 8 & 255) / 255.0F;
        float b = (float) (color & 255) / 255.0F;
        GlStateManager.color(r, g, b, alpha);
    }

    // Colors the next texture without a specified alpha value
    public static void color(int color) {
        color(color, (float) (color >> 24 & 255) / 255.0F);
    }

    /**
     * Bind a texture using the specified integer refrence to the texture.
     *
     * @see org.lwjgl.opengl.GL13 for more information about texture bindings
     */
    public static void bindTexture(int texture) {
        glBindTexture(GL_TEXTURE_2D, texture);
    }

    // Sometimes colors get messed up in for loops, so we use this method to reset it to allow new colors to be used
    public static void resetColor() {
        GlStateManager.color(1, 1, 1, 1);
    }

    public static boolean isHovered(float mouseX, float mouseY, float x, float y, float width, float height) {
        return mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
    }

    public static void drawGradientRect(double left, double top, double right, double bottom, int startColor, int endColor) {
        GLUtil.setup2DRendering();
        glEnable(GL_LINE_SMOOTH);
        glShadeModel(GL_SMOOTH);
        glPushMatrix();
        glBegin(GL_QUADS);
        color(startColor);
        glVertex2d(left, top);
        glVertex2d(left, bottom);
        color(endColor);
        glVertex2d(right, bottom);
        glVertex2d(right, top);
        glEnd();
        glPopMatrix();
        glDisable(GL_LINE_SMOOTH);
        GLUtil.end2DRendering();
        resetColor();
    }

    public static void drawGradientRectBordered(double left, double top, double right, double bottom, double width, int startColor, int endColor, int borderStartColor, int borderEndColor) {
        drawGradientRect(left + width, top + width, right - width, bottom - width, startColor, endColor);
        drawGradientRect(left + width, top, right - width, top + width, borderStartColor, borderEndColor);
        drawGradientRect(left, top, left + width, bottom, borderStartColor, borderEndColor);
        drawGradientRect(right - width, top, right, bottom, borderStartColor, borderEndColor);
        drawGradientRect(left + width, bottom - width, right - width, bottom, borderStartColor, borderEndColor);
    }

    public static boolean isHovering(float x, float y, float width, float height, int mouseX, int mouseY) {
        return (float)mouseX >= x && (float)mouseY >= y && (float)mouseX < x + width && (float)mouseY < y + height;
    }

    public static void doGlScissor(float x, float y, float windowWidth2, float windowHeight2) {
        int scaleFactor = 1;
        float k = mc.gameSettings.guiScale;
        if (k == 0) {
            k = 1000;
        }
        while (scaleFactor < k && mc.displayWidth / (scaleFactor + 1) >= 320
                && mc.displayHeight / (scaleFactor + 1) >= 240) {
            ++scaleFactor;
        }
        glScissor((int) (x * scaleFactor), (int) (mc.displayHeight - (y + windowHeight2) * scaleFactor),
                (int) (windowWidth2 * scaleFactor), (int) (windowHeight2 * scaleFactor));
    }
}

