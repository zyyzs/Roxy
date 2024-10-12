package lol.tgformat.component;

import lol.tgformat.accessable.IMinecraft;
import lol.tgformat.api.event.Listener;
import lol.tgformat.events.PreUpdateEvent;
import lol.tgformat.events.TickEvent;
import lol.tgformat.events.motion.PostMotionEvent;
import lol.tgformat.events.movement.MoveEvent;
import lol.tgformat.events.packet.PacketReceiveEvent;
import lol.tgformat.events.packet.PacketSendEvent;
import lol.tgformat.utils.network.GetC03StatusUtil;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;

public final class MovementComponent implements IMinecraft {
    public static final MovementComponent INSTANCE = new MovementComponent();
    public static boolean cancelMove = false;
    public static boolean forceStuck = false;
    public static void cancelMove() {
        cancelMove(false);
    }
    public static void cancelMove(boolean force) {
        if (mc.thePlayer == null) {
            return;
        }
        if (cancelMove) {
            return;
        }
        forceStuck = force;
        cancelMove = true;
    }

    public static void resetMove() {
        cancelMove = false;
        mc.theWorld.skiptick = 0;
    }
    @Listener
    @NativeObfuscation(verificationLock = "User")
    public void onMove(MoveEvent event) {
        if (cancelMove) {
            if (forceStuck) {
                return;
            }
            if (mc.theWorld.skiptick > 0) {
                return;
            }
            mc.theWorld.skiptick = 20;
        }
    }
    @Listener
    @NativeObfuscation(verificationLock = "User")
    public void onTick(TickEvent event) {
        if (cancelMove && forceStuck) {
            mc.theWorld.skiptick = 20;
        }
    }
//    @Listener
//    @NativeObfuscation(verificationLock = "User")
//    public void onPacketReceive(PacketReceiveEvent event) {
//        if (event.getPacket() instanceof S12PacketEntityVelocity s12 && s12.getEntityID() == mc.thePlayer.getEntityId() && cancelMove && !forceStuck) {
//            if (mc.theWorld.skiptick <= 0) {
//                mc.theWorld.skiptick--;
//                return;
//            }
//            mc.theWorld.skiptick = 0;
//        }
//    }
}