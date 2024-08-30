package lol.tgformat.module.impl.render;

import lol.tgformat.accessable.IMinecraft;
import lol.tgformat.api.event.Listener;
import lol.tgformat.events.render.Render2DEvent;
import lol.tgformat.events.render.Render3DEvent;
import lol.tgformat.module.Module;
import lol.tgformat.module.ModuleType;
import lol.tgformat.module.impl.combat.KillAura;
import lol.tgformat.utils.render.ESPColor;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import org.lwjgl.opengl.GL11;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;
import tech.skidonion.obfuscator.annotations.Renamer;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import java.awt.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @author TG_format
 * @since 2024/6/7 下午2:00
 */
@Renamer

@StringEncryption
public class ESP implements IMinecraft {

    public static ConcurrentLinkedQueue<ESP> esps = new ConcurrentLinkedQueue<>();
    static Executor threadPool = Executors.newFixedThreadPool(2);
    public ESPColor espColor;
    public int tick;
    public Entity target;
    public Color getColor(EntityLivingBase entity) {
        Color color = espColor.getNormalColor();

        if (entity == null) return color;

        if (entity.hurtTime > 0) {
            color = espColor.getDamageColor();
        } else if (KillAura.target == entity) {
            color = espColor.getTargetColor();
        }

        return color;
    }
    public ESP(ESPColor espColor) {
        this.espColor = espColor;
        tick = mc.thePlayer.ticksExisted;
    }

    public void render2D() {

    }

    public void render3D() {

    }

    public static void add(ESP esp) {
        threadPool.execute(() -> {
            boolean modified = false;
            for (ESP esp1 : esps) {
                if (esp.getClass().getSimpleName().equals(esp1.getClass().getSimpleName())) {
                    esp1.espColor = esp.espColor;
                    esp1.tick = mc.thePlayer.ticksExisted;;
                    modified = true;
                }
            }

            if (!modified) {
                esps.add(esp);
            }
        });
    }
    public void updateTargets() {
        target = KillAura.target;
    }
}
