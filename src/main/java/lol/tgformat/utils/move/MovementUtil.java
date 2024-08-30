package lol.tgformat.utils.move;

import lol.tgformat.accessable.IMinecraft;
import lol.tgformat.api.event.Listener;
import lol.tgformat.events.PreUpdateEvent;
import lol.tgformat.events.TickEvent;
import lol.tgformat.events.motion.PostMotionEvent;
import lol.tgformat.events.movement.MoveEvent;
import lol.tgformat.events.packet.PacketReceiveEvent;
import lol.tgformat.events.packet.PacketSendEvent;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S12PacketEntityVelocity;

/**
 * @author Genius
 * @since 2024/8/26 上午12:45
 * IntelliJ IDEA
 */

public class MovementUtil implements IMinecraft {

    public static final MovementUtil INSTANCE = new MovementUtil();
    public static Boolean pre = false;
    public static boolean cancelMove = false;
    public static double motionX = 0.0;
    public static double motionY = 0.0;
    public static double motionZ = 0.0;
    public static float fallDistance = 0.0f;
    public static int moveTicks = 0;



    public static void cancelMove() {
        if (MovementUtil.mc.thePlayer == null) {
            return;
        }
        if (cancelMove) {
            return;
        }
        cancelMove = true;
        motionX = MovementUtil.mc.thePlayer.motionX;
        motionY = MovementUtil.mc.thePlayer.motionY;
        motionZ = MovementUtil.mc.thePlayer.motionZ;
        fallDistance = MovementUtil.mc.thePlayer.fallDistance;
    }

    public static void resetMove() {
        cancelMove = false;
        moveTicks = 0;
    }

    @Listener
    public void onMotion(PostMotionEvent event) {
        MovementUtil.pre = false;
    }

    @Listener
    public void onUpdate(PreUpdateEvent event) {
        if (MovementUtil.cancelMove) {
            if (MovementUtil.moveTicks > 0) {
                return;
            }
            MovementUtil.mc.thePlayer.motionX = MovementUtil.motionX;
            MovementUtil.mc.thePlayer.motionZ = MovementUtil.motionZ;
            MovementUtil.mc.thePlayer.motionY = MovementUtil.motionY;
            MovementUtil.mc.thePlayer.fallDistance = MovementUtil.fallDistance;
        }
    }

    @Listener
    public void onPacket(PacketSendEvent event) {
        if (event.getPacket() instanceof C03PacketPlayer && MovementUtil.cancelMove && MovementUtil.moveTicks > 0) {
            MovementUtil.motionX = MovementUtil.mc.thePlayer.motionX;
            MovementUtil.motionZ = MovementUtil.mc.thePlayer.motionZ;
            MovementUtil.motionY = MovementUtil.mc.thePlayer.motionY;
            MovementUtil.fallDistance = MovementUtil.mc.thePlayer.fallDistance;
            --moveTicks;
        }
    }

    @Listener
    public void onTick(TickEvent event) {
        if (MovementUtil.mc.thePlayer == null) {
            MovementUtil.resetMove();
            return;
        }
        MovementUtil.pre = true;
        if (MovementUtil.cancelMove) {
            if (GappleUtil.noMovePackets >= 20) {
                MovementUtil.mc.thePlayer.motionX = MovementUtil.motionX;
                MovementUtil.mc.thePlayer.motionY = MovementUtil.motionY;
                MovementUtil.mc.thePlayer.motionZ = MovementUtil.motionZ;
                MovementUtil.mc.thePlayer.fallDistance = MovementUtil.fallDistance;
                ++moveTicks;
            }
            if (MovementUtil.moveTicks > 0) {
                return;
            }
            MovementUtil.mc.thePlayer.motionX = MovementUtil.motionX;
            MovementUtil.mc.thePlayer.motionZ = MovementUtil.motionZ;
            MovementUtil.mc.thePlayer.motionY = MovementUtil.motionY;
            MovementUtil.mc.thePlayer.fallDistance = MovementUtil.fallDistance;
        }
    }

    @Listener
    public void onMove(MoveEvent event) {
        if (MovementUtil.cancelMove) {
            if (MovementUtil.moveTicks > 0) {
                return;
            }
            event.setCancelled(true);
        }
    }

    @Listener
    public void onPacketReceive(PacketReceiveEvent event) {
        if (mc.thePlayer == null || mc.theWorld == null) return;
        S12PacketEntityVelocity s12;
        Packet<?> packet = event.getPacket();
        if (packet instanceof S12PacketEntityVelocity && (s12 = (S12PacketEntityVelocity) packet).getEntityID() == MovementUtil.mc.thePlayer.getEntityId() && MovementUtil.cancelMove) {
            MovementUtil.mc.thePlayer.motionX = MovementUtil.motionX;
            MovementUtil.mc.thePlayer.motionY = MovementUtil.motionY;
            MovementUtil.mc.thePlayer.motionZ = MovementUtil.motionZ;
            MovementUtil.mc.thePlayer.fallDistance = MovementUtil.fallDistance;
            ++moveTicks;
        }
    }


}
