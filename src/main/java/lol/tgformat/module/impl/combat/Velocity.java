package lol.tgformat.module.impl.combat;

import lol.tgformat.api.event.Listener;
import lol.tgformat.events.PreUpdateEvent;
import lol.tgformat.events.packet.PacketReceiveEvent;
import lol.tgformat.module.Module;
import lol.tgformat.module.ModuleManager;
import lol.tgformat.module.ModuleType;
import lol.tgformat.module.values.impl.BooleanSetting;
import lol.tgformat.utils.network.PacketUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityEgg;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import tech.skidonion.obfuscator.annotations.Renamer;
import tech.skidonion.obfuscator.annotations.StringEncryption;

/**
 * @author TG_format
 * @since 2024/6/1 0:36
 */
@Renamer
@StringEncryption
public class Velocity extends Module {
    private final BooleanSetting invalidEntity = new BooleanSetting("Attack Invalid", false);
    private final int c02s = 5;
    public boolean getKB = false;

    public Velocity() {
        super("Velocity", ModuleType.Combat);
    }

    @Override
    public void onEnable() {
        this.getKB = false;
    }

    @Override
    public void onDisable() {
        this.getKB = false;
    }

    @Listener
    public void onUpdate(PreUpdateEvent event) {
        if (this.getKB) {
            Velocity.reduce();
            this.getKB = false;
        }
    }

    @Listener
    public void onPacketReceiveSync(PacketReceiveEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if (event.getPacket() instanceof S12PacketEntityVelocity && ((S12PacketEntityVelocity)event.getPacket()).getEntityID() == mc.thePlayer.getEntityId()) {
            this.getKB = true;
        }
    }

    public static void reduce() {
        Velocity velocity = ModuleManager.getModule(Velocity.class);
        if (!velocity.isState()) {
            return;
        }
        if (velocity.getTarget() == null) return;
        Entity target = velocity.getTarget();
        velocity.getKB = true;
        for (int i = 0; i < velocity.c02s; ++i) {
            Gapple gapple;
            mc.getNetHandler().addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING));
            mc.thePlayer.setSprinting(true);
            mc.thePlayer.serverSprintState = true;
            PacketUtil.sendPacket(new C0APacketAnimation());
            PacketUtil.sendPacket(new C02PacketUseEntity(target, C02PacketUseEntity.Action.ATTACK));
            if ((gapple = ModuleManager.getModule(Gapple.class)).isState() && gapple.noC02 && Gapple.eating) {
                return;
            }
            mc.thePlayer.motionX *= 0.6;
            mc.thePlayer.motionZ *= 0.6;
        }
    }
    public Entity getTarget() {
        if (KillAura.target != null && mc.thePlayer.getClosestDistanceToEntity(KillAura.target) <= 3.0f) {
            return KillAura.target;
        } else {
            for (Entity entity : mc.theWorld.loadedEntityList) {
                if (!entity.equals(mc.thePlayer) && !entity.isDead && invalidEntity.isEnabled()) {
                    if (entity instanceof EntityArrow entityArrow) {
                        if (entityArrow.getTicksInGround() <= 0) {
                            return entityArrow;
                        }
                    }

                    if (entity instanceof EntitySnowball) {
                        return entity;
                    }

                    if (entity instanceof EntityEgg) {
                        return entity;
                    }

                    if (entity instanceof EntityTNTPrimed) {
                        return entity;
                    }
                    if (entity instanceof EntityFishHook) {
                        return entity;
                    }
                }
            }
        }
        return null;
    }
}

