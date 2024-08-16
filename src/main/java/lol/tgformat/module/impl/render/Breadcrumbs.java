package lol.tgformat.module.impl.render;

import lol.tgformat.api.event.Listener;
import lol.tgformat.events.render.Render3DEvent;
import lol.tgformat.module.Module;
import lol.tgformat.module.ModuleType;
import lol.tgformat.module.values.impl.ModeSetting;
import lol.tgformat.module.values.impl.NumberSetting;
import lol.tgformat.ui.utils.RenderUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Vec3;
import net.netease.utils.ColorUtils;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;

/**
 * @Author KuChaZi
 * @Date 2024/7/2 15:18
 * @ClassName: Breadcrumbs
 */
public class Breadcrumbs extends Module {
    private final ModeSetting mod = new ModeSetting("Mode", "FDP", "Novoline", "FDP");
    private final NumberSetting size = new NumberSetting("Size", 100.0, 500.0, 10.0, 1.0);
    private final List<Vec3> path = new ArrayList<>();

    public Breadcrumbs() {
        super("Breadcrumbs", ModuleType.Render);
    }

    @Listener
    public void onRender3D(Render3DEvent event) {
        if (mod.is("FDP")) {
            if (mc.thePlayer.lastTickPosX != mc.thePlayer.posX || mc.thePlayer.lastTickPosY != mc.thePlayer.posY || mc.thePlayer.lastTickPosZ != mc.thePlayer.posZ) {
                path.add(new Vec3(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ));
            }
            while (path.size() > size.getValue().intValue()) {
                path.remove(0);
            }
            RenderUtil.renderBreadCrumbs(path, HUD.getClientColors().getFirst(), HUD.getClientColors().getSecond());
        } else if (mod.is("Novoline")) {
            for (EntityPlayer entityPlayer : mc.theWorld.playerEntities) {
                ArrayList<Breadcrumbs.Point> points = entityPlayer.points;
                boolean render;
                render = entityPlayer != mc.thePlayer || mc.gameSettings.thirdPersonView != 0;

                points.removeIf(p -> p.age >= size.getValue().intValue());

                double x = entityPlayer.lastTickPosX + (entityPlayer.posX - entityPlayer.lastTickPosX) * event.getPartialTicks();
                double y = entityPlayer.lastTickPosY + (entityPlayer.posY - entityPlayer.lastTickPosY) * event.getPartialTicks();
                double z = entityPlayer.lastTickPosZ + (entityPlayer.posZ - entityPlayer.lastTickPosZ) * event.getPartialTicks();

                points.add(new Point(x, y, z));

                if (render) {
                    GL11.glPushMatrix();
                    GL11.glDisable(GL_ALPHA_TEST);
                    GL11.glEnable(GL11.GL_BLEND);
                    GL11.glEnable(GL11.GL_LINE_SMOOTH);
                    GL11.glDisable(GL11.GL_TEXTURE_2D);
                    GL11.glBlendFunc(770, 771);
                    GL11.glDisable(GL11.GL_CULL_FACE);
                }
                int yOffset = 0;
                for (Point t : points) {
                    if (points.indexOf(t) >= points.size() - 1) continue;
                    Point temp = points.get(points.indexOf(t) + 1);
                    float a = 200 * (points.indexOf(t) / (float) points.size());
                    if (render) {
                        Color color = ColorUtils.interpolateColorsBackAndForth(5, yOffset, HUD.getClientColors().getFirst(), HUD.getClientColors().getSecond(), false);
                        Color c = ColorUtils.injectAlpha(color, (int) a);
                        glBegin(GL_QUAD_STRIP);
                        final double x2 = t.x - mc.getRenderManager().getRenderPosX();
                        final double y2 = t.y - mc.getRenderManager().getRenderPosY();
                        final double z2 = t.z - mc.getRenderManager().getRenderPosZ();
                        final double x1 = temp.x - mc.getRenderManager().getRenderPosX();
                        final double y1 = temp.y - mc.getRenderManager().getRenderPosY();
                        final double z1 = temp.z - mc.getRenderManager().getRenderPosZ();
                        ColorUtils.glColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 0).getRGB());
                        glVertex3d(x2, y2 + entityPlayer.height - 0.1, z2);
                        ColorUtils.glColor(c.getRGB());
                        glVertex3d(x2, y2 + 0.2, z2);
                        glVertex3d(x1, y1 + entityPlayer.height - 0.1, z1);
                        glVertex3d(x1, y1 + 0.2, z1);
                        glEnd();
                    }
                    ++t.age;
                    yOffset += 1;
                }
                if (render) {
                    GlStateManager.resetColor();
                    GL11.glDisable(GL11.GL_BLEND);
                    GL11.glEnable(GL_ALPHA_TEST);
                    GL11.glEnable(GL11.GL_TEXTURE_2D);
                    GL11.glEnable(GL_CULL_FACE);
                    GL11.glDisable(GL11.GL_LINE_SMOOTH);
                    GL11.glDisable(GL11.GL_BLEND);
                    GL11.glPopMatrix();
                }
            }
        }
    }

    public static class Point {
        public final double x, y, z;

        public float age = 0;

        public Point(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }
}
