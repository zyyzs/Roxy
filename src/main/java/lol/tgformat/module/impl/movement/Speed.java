package lol.tgformat.module.impl.movement;

import lol.tgformat.api.event.Listener;
import lol.tgformat.component.RotationComponent;
import lol.tgformat.events.PreUpdateEvent;
import lol.tgformat.events.motion.PreMotionEvent;
import lol.tgformat.events.movement.MoveInputEvent;
import lol.tgformat.events.movement.StrafeEvent;
import lol.tgformat.events.packet.PacketReceiveEvent;
import lol.tgformat.module.Module;
import lol.tgformat.module.ModuleManager;
import lol.tgformat.module.ModuleType;
import lol.tgformat.module.impl.combat.Criticals;
import lol.tgformat.module.impl.combat.KillAura;
import lol.tgformat.module.impl.player.Blink;
import lol.tgformat.module.impl.world.Scaffold;
import lol.tgformat.module.values.impl.BooleanSetting;
import lol.tgformat.module.values.impl.ModeSetting;
import lol.tgformat.ui.notifications.NotificationManager;
import lol.tgformat.ui.notifications.NotificationType;
import lol.tgformat.utils.move.MoveUtil;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.util.AxisAlignedBB;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import java.util.Iterator;

/**
 * @Author KuChaZi
 * @Date 2024/6/30 17:09
 * @ClassName: Speed
 */
@StringEncryption
public class Speed extends Module {
    private final ModeSetting mode = new ModeSetting("Mode", "Grim", "Watchdog", "Grim", "AutoJump");
    private final ModeSetting watchdogmode = new ModeSetting("WatchDog Mode","Ground", "Glide", "Glide2", "Ground", "Test");
    private final BooleanSetting lagbackcheck = new BooleanSetting("LagBackCheck", true);
    public final BooleanSetting strafe = new BooleanSetting("Grim-Strafe", false);
    private final BooleanSetting scaffoldCheck = new BooleanSetting("Scaffold Check", false);
    private final BooleanSetting blinkCheck = new BooleanSetting("Blink Check", false);

    public Speed() {
        super("Speed", ModuleType.Movement);
        watchdogmode.addParent(mode, modeSetting -> mode.is("Watchdog"));
        strafe.addParent(mode, modeSetting -> mode.is("Grim"));
    }
    private int inAirTicks;
    @Override
    public void onEnable() {
        inAirTicks = 0;
        if (mc.thePlayer == null) return;
        super.onEnable();
    }

    @Override
    public void onDisable() {
        mc.timer.timerSpeed = 1.0f;
        super.onDisable();
    }

    @Listener
    private void onPacketReceive(PacketReceiveEvent event) {
        Packet<?> packet = event.getPacket();
        if (packet instanceof S08PacketPlayerPosLook) {
            if (this.lagbackcheck.isEnabled()) {
                NotificationManager.post(NotificationType.WARNING, "Speed", "Speed Disabled in LagBack");
                this.setState(false);
            }
        }
    }

    @Listener
    private void onUpdate(PreUpdateEvent event) {
        this.setSuffix(mode.getMode());
        if ((blinkCheck.isEnabled() && ModuleManager.getModule(Blink.class).isState()) || (scaffoldCheck.isEnabled() && ModuleManager.getModule(Scaffold.class).isState())) {
            return;
        }
        if (mode.is("Watchdog")) {
            if (watchdogmode.is("Ground")) {
                if (mc.thePlayer.onGround && MoveUtil.isMoving()) {
                    mc.thePlayer.jump();
                    MoveUtil.strafe(0.45);
                }
            } else if (watchdogmode.is("Glide")) {
                if(MoveUtil.isMoving()) {
                    if((mc.thePlayer.offGroundTicks == 10) && MoveUtil.isOnGround(0.769)) {
                        mc.thePlayer.motionY = 0;
                    }

                    if(MoveUtil.isOnGround(0.769) && mc.thePlayer.offGroundTicks >= 9) {
                        MoveUtil.strafe(0.29);
                    }

                    if(mc.thePlayer.onGround) {
                        if(mc.gameSettings.keyBindForward.isPressed()) MoveUtil.strafe(0.28);
                        else MoveUtil.strafe(0.45);
                        mc.thePlayer.jump();
                    }
                }
            } else if (watchdogmode.is("Glide2")) {
                if(MoveUtil.isMoving()) {
                    if((mc.thePlayer.offGroundTicks == 10 || mc.thePlayer.offGroundTicks == 11 ) && MoveUtil.isOnGround(0.769)) {
                        mc.thePlayer.motionY = 0;
                        MoveUtil.strafe(0.15);
                    }

                    if(mc.thePlayer.onGround) {
                        if(mc.gameSettings.keyBindForward.pressed) MoveUtil.strafe(0.28);
                        else MoveUtil.strafe(0.45);
                        mc.thePlayer.jump();
                    }
                }
            } else if (watchdogmode.is("Test")) {
                if (mc.thePlayer.onGround) {
                    if (MoveUtil.isMoving()) {
                        mc.thePlayer.jump();
                        MoveUtil.setSpeed(MoveUtil.getBaseMoveSpeed() * 1.6);
                        this.inAirTicks = 0;
                    }
                } else {
                    ++this.inAirTicks;
                    if (this.inAirTicks == 1) {
                        MoveUtil.setSpeed(MoveUtil.getBaseMoveSpeed() * 1.16);
                    }
                }
            }
        } else if (mode.is("AutoJump")) {
            if (MoveUtil.isMoving() && mc.thePlayer.onGround && mc.thePlayer.isCollidedVertically) {
                mc.thePlayer.jump();
            }
        }
    }

    @Listener
    private void onPreMotion(PreMotionEvent event) {
        if ((blinkCheck.isEnabled() && ModuleManager.getModule(Blink.class).isState()) || (scaffoldCheck.isEnabled() && ModuleManager.getModule(Scaffold.class).isState())) {
            return;
        }
        if (isGapple()) return;
        if (mode.is("Grim")) {
            AxisAlignedBB playerBox = mc.thePlayer.boundingBox.expand(1.0D, 1.0D, 1.0D);
            int c = 0;
            Iterator<Entity> entitys = mc.theWorld.loadedEntityList.iterator();

            while(true) {
                Entity entity;
                do {
                    if (!entitys.hasNext()) {
                        if (c > 0 && MoveUtil.isMoving()) {
                            double strafeOffset = (double)Math.min(c, 3) * 0.08D;
                            float yaw = this.getMoveYaw();
                            double mx = -Math.sin(Math.toRadians(yaw));
                            double mz = Math.cos(Math.toRadians(yaw));
                            mc.thePlayer.addVelocity(mx * strafeOffset, 0.0D, mz * strafeOffset);
                            if (c < 4 && KillAura.target != null && this.shouldFollow()) {
                                mc.gameSettings.keyBindLeft.pressed = true;
                            } else {
                                mc.gameSettings.keyBindLeft.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindLeft);
                            }
                            return;
                        } else {
                            mc.gameSettings.keyBindLeft.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindLeft);
                            return;
                        }
                    }

                    entity = entitys.next();
                } while(!(entity instanceof EntityLivingBase) && !(entity instanceof EntityBoat) && !(entity instanceof EntityMinecart) && !(entity instanceof EntityFishHook));

                if (!(entity instanceof EntityArmorStand) && entity.getEntityId() != mc.thePlayer.getEntityId() && playerBox.intersectsWith(entity.boundingBox) && entity.getEntityId() != -8 && entity.getEntityId() != -1337 && !(ModuleManager.getModule(Blink.class)).isState()) {
                    ++c;
                }
            }
        }
    }

    public boolean shouldFollow() {
        return this.isState() && mc.gameSettings.keyBindJump.isKeyDown();
    }

    private float getMoveYaw() {
        EntityPlayerSP thePlayer = mc.thePlayer;
        float moveYaw = thePlayer.rotationYaw;
        if (thePlayer.moveForward != 0.0F && thePlayer.moveStrafing == 0.0F) {
            moveYaw += thePlayer.moveForward > 0.0F ? 0.0F : 180.0F;
        } else if (thePlayer.moveForward != 0.0F) {
            if (thePlayer.moveForward > 0.0F) {
                moveYaw += thePlayer.moveStrafing > 0.0F ? -45.0F : 45.0F;
            } else {
                moveYaw -= thePlayer.moveStrafing > 0.0F ? -45.0F : 45.0F;
            }

            moveYaw += thePlayer.moveForward > 0.0F ? 0.0F : 180.0F;
        } else if (thePlayer.moveStrafing != 0.0F) {
            moveYaw += thePlayer.moveStrafing > 0.0F ? -70.0F : 70.0F;
        }

        if (KillAura.target != null && mc.gameSettings.keyBindJump.isKeyDown()) {
            moveYaw = RotationComponent.rotations.x;
        }

        return moveYaw;
    }
}
