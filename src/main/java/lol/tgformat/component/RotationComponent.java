package lol.tgformat.component;

import lol.tgformat.accessable.IMinecraft;
import lol.tgformat.api.event.Listener;
import lol.tgformat.events.PreUpdateEvent;
import lol.tgformat.events.motion.PreMotionEvent;
import lol.tgformat.events.movement.JumpEvent;
import lol.tgformat.events.movement.MoveInputEvent;
import lol.tgformat.events.movement.StrafeEvent;
import lol.tgformat.events.render.LookEvent;
import lol.tgformat.module.ModuleManager;
import lol.tgformat.module.impl.movement.Speed;
import lol.tgformat.module.impl.player.Blink;
import lol.tgformat.module.impl.world.Scaffold;
import lol.tgformat.utils.block.Rotation;
import lol.tgformat.utils.enums.MovementFix;
import lol.tgformat.utils.move.MoveUtil;
import lol.tgformat.utils.rotation.RotationUtil;
import lol.tgformat.utils.vector.Vector2f;
import tech.skidonion.obfuscator.annotations.Renamer;
import tech.skidonion.obfuscator.annotations.StringEncryption;

/**
 * @author TG_format
 * @since 2024/5/31 23:09
 */
@Renamer
@StringEncryption
public final class RotationComponent implements IMinecraft {
    public static boolean active;
    private static boolean smoothed;
    public static Vector2f rotations, lastRotations, targetRotations, lastServerRotations;
    private static double rotationSpeed;
    private static MovementFix correctMovement;
    private static boolean forceSilent;
    /*
     * This method must be called on Pre Update Event to work correctly
     */
    public static void setRotations(final Vector2f rotations, final double rotationSpeed, final MovementFix correctMovement) {
        RotationComponent.targetRotations = rotations;
        RotationComponent.rotationSpeed = rotationSpeed * 18;
        RotationComponent.correctMovement = correctMovement;
        active = true;
        forceSilent = false;
        smooth();
    }
    public static void setRotations(final Vector2f rotations, final double rotationSpeed, final MovementFix correctMovement, boolean silent) {
        RotationComponent.targetRotations = rotations;
        RotationComponent.rotationSpeed = rotationSpeed * 18;
        RotationComponent.correctMovement = correctMovement;
        active = true;
        forceSilent = silent;
        smooth();
    }
    public static void setFollow(boolean follow) {
        if (targetRotations != null) {
            RotationComponent.forceSilent = follow;
        }
    }
    public static double getRotationDifference(Vector2f rotation) {
        return lastServerRotations == null ? 0.0D : getRotationDifference(rotation, lastServerRotations);
    }
    public static double getRotationDifference(Vector2f a, Vector2f b) {
        return Math.hypot((double)getAngleDifference(a.x, b.x), (double)(a.y - b.y));
    }
    public static double getRotationDifference(Rotation rotation) {
        return lastServerRotations == null ? 0.0D : getRotationDifference(rotation, lastServerRotations);
    }
    public static double getRotationDifference(Rotation a, Vector2f b) {
        return Math.hypot((double)getAngleDifference(a.getYaw(), b.getX()), (double)(a.getPitch() - b.getY()));
    }
    public static float getAngleDifference(float a, float b) {
        return ((a - b) % 360.0F + 540.0F) % 360.0F - 180.0F;
    }

    @Listener(100)
    public void onPreUpdate(PreUpdateEvent event) {

        if (!active || rotations == null || lastRotations == null || targetRotations == null || lastServerRotations == null) {
            rotations = lastRotations = targetRotations = lastServerRotations = new Vector2f(mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch);
        }

        if (active) {
            smooth();
        }

        if (correctMovement == MovementFix.BACKWARDS_SPRINT && active) {
            if (Math.abs(rotations.x - Math.toDegrees(MoveUtil.direction())) > 45) {
                mc.gameSettings.keyBindSprint.pressed = false;
                mc.thePlayer.setSprinting(false);
            }
        }
    };


    @Listener(0)
    public void onMove(MoveInputEvent event) {
        Speed speed = ModuleManager.getModule(Speed.class);
        if (speed.isState() && speed.strafe.isEnabled() && !(ModuleManager.getModule(Blink.class).isState() || ModuleManager.getModule(Scaffold.class).isState())) {
            if (!forceSilent) {
                return;
            }
        }
        if (active && correctMovement == MovementFix.NORMAL && rotations != null) {
            final float yaw = rotations.x;
            MoveUtil.fixMovement(event, yaw);
        }
    };

    @Listener(0)
    public void onLook(LookEvent event) {
        if (active && rotations != null) {
            event.setRotation(rotations);
        }
    };

    @Listener(0)
    public void onStrafe(StrafeEvent event) {
        if (active && (correctMovement == MovementFix.NORMAL || correctMovement == MovementFix.TRADITIONAL) && rotations != null) {
            event.setYaw(rotations.x);
        }
    };

    @Listener(0)
    public void onJump(JumpEvent event) {
        if (active && (correctMovement == MovementFix.NORMAL || correctMovement == MovementFix.TRADITIONAL || correctMovement == MovementFix.BACKWARDS_SPRINT) && rotations != null) {
            event.setYaw(rotations.x);
        }
    };

    @Listener(0)
    public void onPreMotionEvent(PreMotionEvent event) {
        if (active && rotations != null) {
            final float yaw = rotations.x;
            final float pitch = rotations.y;
            event.setYaw(yaw);
            event.setPitch(pitch);
            mc.thePlayer.renderYawOffset = yaw;
            mc.thePlayer.rotationYawHead = yaw;
            mc.thePlayer.renderPitchHead = pitch;

            lastServerRotations = new Vector2f(yaw, pitch);

            if (Math.abs((rotations.x - mc.thePlayer.rotationYaw) % 360) < 1 && Math.abs((rotations.y - mc.thePlayer.rotationPitch)) < 1) {
                active = false;

                this.correctDisabledRotations();
            }

            lastRotations = rotations;
        } else {
            lastRotations = new Vector2f(mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch);
        }

        targetRotations = new Vector2f(mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch);
        smoothed = false;
    }
//    @Listener
//    public void onPacketSendHigher(PacketSendHigherEvent event) {
//        if (active && rotations != null && GappleUtils.blinking) {
//            final float yaw = rotations.x;
//            final float pitch = rotations.y;
//            if (event.isNoEvent() && event.getPacket() instanceof C03PacketPlayer) {
//                C03PacketPlayer newPacket = new C03PacketPlayer.C05PacketPlayerLook(yaw, pitch, MovementUtils.lastOnGround);
//                event.setPacket(newPacket);
//            }
//        }
//    }

    private void correctDisabledRotations() {
        final Vector2f rotations = new Vector2f(mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch);
        final Vector2f fixedRotations = RotationUtil.resetRotation(RotationUtil.applySensitivityPatch(rotations, lastRotations));

        mc.thePlayer.rotationYaw = fixedRotations.x;
        mc.thePlayer.rotationPitch = fixedRotations.y;
    }

    public static void smooth() {
        if (!smoothed) {
            final float lastYaw = lastRotations.x;
            final float lastPitch = lastRotations.y;
            final float targetYaw = targetRotations.x;
            final float targetPitch = targetRotations.y;

            rotations = RotationUtil.smooth(new Vector2f(lastYaw, lastPitch), new Vector2f(targetYaw, targetPitch),
                    rotationSpeed + Math.random());

            if (correctMovement == MovementFix.NORMAL || correctMovement == MovementFix.TRADITIONAL) {
                mc.thePlayer.movementYaw = rotations.x;
            }

            mc.thePlayer.velocityYaw = rotations.x;
        }

        smoothed = true;

        /*
         * Updating MouseOver
         */
        mc.entityRenderer.getMouseOver(1);
    }
}
