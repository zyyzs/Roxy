package lol.tgformat.utils.render;

import lol.tgformat.api.event.Listener;
import lol.tgformat.events.render.RenderModelEvent;
import lol.tgformat.module.impl.render.HUD;
import lol.tgformat.ui.font.Pair;
import lol.tgformat.ui.utils.RenderUtil;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;

import java.awt.*;

import static lol.tgformat.module.ModuleManager.getModule;
import static lol.tgformat.ui.clickgui.Utils.mc;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.glPopMatrix;

public class Chams {
    @Listener
    public void onRenderModelEvent(RenderModelEvent event) {
        if (!isValidEntity(event.getEntity())) return;

        Pair<Color, Color> colors = HUD.getClientColors();
        if (!isValidEntity(event.getEntity())) return;


        if (event.isPost()) {
            glEnable(GL_DEPTH_TEST);
            glDepthMask(true);
            Color visibleColor =new Color(0,255,255,255);
            RenderUtil.color(visibleColor.getRGB());
            event.drawModel();
            glEnable(GL_TEXTURE_2D);
            glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            glDisable(GL_BLEND);
            glEnable(GL_LIGHTING);

            glPolygonOffset(1.0f, -1000000.0f);
            glDisable(GL_POLYGON_OFFSET_LINE);
            glPopMatrix();


        } else {
            Color behindWallsColor = new Color(0,255,255,0);

            glPushMatrix();
            glEnable(GL_POLYGON_OFFSET_LINE);
            glPolygonOffset(1.0F, 1000000.0F);

            glDisable(GL_TEXTURE_2D);
            RenderUtil.color(behindWallsColor.getRGB());

            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
            glDisable(GL_DEPTH_TEST);
            glDepthMask(false);

        }
    }


    private boolean isValidEntity(Entity entity) {
        return entity instanceof EntityAnimal ||
                entity instanceof EntityMob;
    }
}
