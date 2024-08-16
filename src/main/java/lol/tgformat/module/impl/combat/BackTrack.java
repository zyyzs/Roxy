package lol.tgformat.module.impl.combat;

import lol.tgformat.api.event.Listener;
import lol.tgformat.events.AttackEvent;
import lol.tgformat.events.TickEvent;
import lol.tgformat.events.packet.PacketReceiveEvent;
import lol.tgformat.events.render.Render3DEvent;
import lol.tgformat.module.Module;
import lol.tgformat.module.ModuleType;
import lol.tgformat.module.values.impl.BooleanSetting;
import lol.tgformat.module.values.impl.NumberSetting;
import lol.tgformat.ui.utils.RenderUtil;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.play.server.*;
import net.minecraft.util.Vec3;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;
import tech.skidonion.obfuscator.annotations.Renamer;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import java.awt.*;

/**
 * @author TG_format
 * @since 2024/7/13 下午11:25
 */
@Renamer
@StringEncryption
public class BackTrack extends Module {
    public static EntityLivingBase target;
    private final NumberSetting amount = new NumberSetting("Amount", 1.0D, 3.0D, 1.0D, 0.1D);
    private final NumberSetting range = new NumberSetting("Range", 2.0D, 8.0D, 2.0D, 0.1D);
    private final NumberSetting interval = new NumberSetting("IntervalTick", 1.0D, 10.0D, 0.0D, 1.0D);
    private final BooleanSetting esp = new BooleanSetting("Esp", false);
    private Vec3 realTargetPosition = new Vec3(0.0D, 0.0D, 0.0D);
    public static double realX;
    public static double realY;
    public static double realZ;
    int tick = 0;

    public BackTrack() {
        super("BackTrack", ModuleType.Combat);
    }

    @Listener
    public void onAttack(AttackEvent e) {
        target = (EntityLivingBase)e.getTarget();
    }

    @Listener
    
    public void onTick(TickEvent e) {
        if ((double)this.tick <= this.interval.getValue()) {
            ++this.tick;
        }

        if (target != null && (double)mc.thePlayer.getDistanceToEntity(target) <= this.range.getValue() && (new Vec3(target.posX, target.posY, target.posZ)).distanceTo(this.realTargetPosition) < (Double)this.amount.getValue() && (double)this.tick > (Double)this.interval.getValue()) {
            target.posX = target.lastTickPosX;
            target.posY = target.lastTickPosY;
            target.posZ = target.lastTickPosZ;
            this.tick = 0;
        }

    }

    @Listener
    
    public void onPacketReceive(PacketReceiveEvent e) {
        if (e.getPacket() instanceof S18PacketEntityTeleport s18) {
            if (s18.getEntityId() == target.getEntityId()) {
                this.realTargetPosition = new Vec3((double)s18.getX() / 32.0D, (double)s18.getY() / 32.0D, (double)s18.getZ() / 32.0D);
                realX = (double)s18.getX() / 32.0D;
                realY = (double)s18.getY() / 32.0D;
                realZ = (double)s18.getZ() / 32.0D;
            }
        }

    }

    @Listener
    public void onRender3D(Render3DEvent event) {
        if (this.esp.isEnabled()) {
            RenderUtil.renderBoundingBox(target, Color.red, 100.0F);
            RenderUtil.resetColor();
        }

    }


    public void onDisable() {
        target = null;
        this.tick = 0;
    }
}