package lol.tgformat.module.impl.combat;

import com.mojang.realmsclient.gui.ChatFormatting;
import lol.tgformat.api.event.Listener;
import lol.tgformat.events.PreUpdateEvent;
import lol.tgformat.events.VelocityEvent;
import lol.tgformat.events.WorldEvent;
import lol.tgformat.events.motion.PostMotionEvent;
import lol.tgformat.events.packet.PacketReceiveEvent;
import lol.tgformat.module.Module;
import lol.tgformat.module.ModuleManager;
import lol.tgformat.module.ModuleType;
import lol.tgformat.module.values.impl.BooleanSetting;
import lol.tgformat.module.values.impl.ModeSetting;
import lol.tgformat.utils.network.PacketUtil;
import lol.tgformat.utils.vector.Vector3d;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityEgg;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.util.ChatComponentText;
import tech.skidonion.obfuscator.annotations.Renamer;
import tech.skidonion.obfuscator.annotations.StringEncryption;

@Renamer
@StringEncryption
public class Velocity extends Module {
    private final ModeSetting mode = new ModeSetting("Mode", "GrimAC", "GrimAC", "Watchdog");
    private final BooleanSetting invalidEntity = new BooleanSetting("Attack Invalid Entity",true);
    private final BooleanSetting debug = new BooleanSetting("Debug", true);
    public Velocity(){
        super("Velocity", ModuleType.Combat);
        invalidEntity.addParent(mode, modeSetting -> mode.is("GrimAC"));
        debug.addParent(mode, modeSetting -> mode.is("GrimAC"));
    }
    public boolean shouldVelo;
    Entity target;
    @Listener
    public void onReceive(PacketReceiveEvent event){
        if(isNull())return;
        if (mode.is("GrimAC")) {
            if (event.getPacket() instanceof S12PacketEntityVelocity s12) {
                double strength = new Vector3d(s12.getMotionX(), s12.getMotionY(), s12.getMotionZ()).length();
                if (s12.getEntityID() == mc.thePlayer.getEntityId() && !mc.thePlayer.isOnLadder() && !mc.thePlayer.isInWeb) {
                    target = getNearTarget();
                    if (target == null) return;
                    if (mc.thePlayer.getDistanceToEntity(target) > 3.3F) {
                        reset();
                        return;
                    }
                    shouldVelo = true;
                    if (debug.isEnabled()) {
                        mc.thePlayer.addChatMessage(new ChatComponentText("[" + ChatFormatting.RED + "Received Velocity" + ChatFormatting.RESET + "]" + strength + " " + (mc.thePlayer.onGround ? "on Ground" : "on Air") + (target != null ? " - Distance: " + mc.thePlayer.getClosestDistanceToEntity(target) : "")));
                    }
                }
            }
        }
        if (mode.is("Watchdog")) {
            this.setSuffix("Watchdog");
            if (event.getPacket() instanceof S12PacketEntityVelocity s12) {
                if (mc.thePlayer != null && s12.getEntityID() == mc.thePlayer.getEntityId()) {
                    s12.motionX *= (0 / 100);
                    s12.motionZ *= (0 / 100);
                }
            }
        }
    }
    @Listener
    public void onWorld(WorldEvent event){
        if (mode.is("GrimAC")) {
            reset();
        }
    }

    private Entity getNearTarget() {
        Entity target = null;
        EntityLivingBase clientTarget = KillAura.target;
        if (clientTarget != null) {
            target = clientTarget;
            return target;
        } else {
            for (Entity entity : mc.theWorld.loadedEntityList) {
                if (!entity.equals(mc.thePlayer) && !entity.isDead && invalidEntity.isEnabled()) {
                    if (entity instanceof EntityArrow entityArrow) {
                        if (entityArrow.getTicksInGround() <= 0) {
                            target = entityArrow;
                        }
                    }

                    if (entity instanceof EntitySnowball) {
                        target = entity;
                    }

                    if (entity instanceof EntityEgg) {
                        target = entity;
                    }

                    if (entity instanceof EntityTNTPrimed) {
                        target = entity;
                    }

                    if (entity instanceof EntityFishHook) {
                        target = entity;
                    }
                }
            }
        }

        return target;
    }

    @Listener
    public void onUpdate(PreUpdateEvent event){
        if(isNull())return;
        if (mode.is("GrimAC")) {
            if (shouldVelo) {
                if (mc.thePlayer.getDistanceToEntity(target) > 3.0) {
                    reset();
                    return;
                }
                for (int i = 0; i < 5; i++) {
                    if (!mc.thePlayer.serverSprintState) {
                        PacketUtil.sendPacket(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING));
                        mc.thePlayer.setSprinting(true);
                        mc.thePlayer.serverSprintState = true;
                    }
                    PacketUtil.sendPacket(new C0APacketAnimation());
                    PacketUtil.sendPacket(new C02PacketUseEntity(target, C02PacketUseEntity.Action.ATTACK));
                    mc.thePlayer.motionX *= 0.6F;
                    mc.thePlayer.motionZ *= 0.6F;
                }
                shouldVelo = false;
            }
        }
    }

    private void reset(){
        shouldVelo = false;
        target = null;
    }
}