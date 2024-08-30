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

public final class MovementComponent implements IMinecraft {
    public static final MovementComponent INSTANCE = new MovementComponent();
    public static boolean cancelMove = false;

    public static void cancelMove() {
        if (mc.thePlayer == null) {
            return;
        }
        if (cancelMove) {
            return;
        }
        cancelMove = true;
    }

    public static void resetMove() {
        cancelMove = false;
        mc.theWorld.skiptick = 0;
    }
    @Listener
    public void onMove(MoveEvent event) {
        if (cancelMove) {
            if (mc.theWorld.skiptick > 0) {
                return;
            }
            mc.theWorld.skiptick = 20;
        }
    }
    @Listener
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacket() instanceof S12PacketEntityVelocity s12 && s12.getEntityID() == mc.thePlayer.getEntityId() && cancelMove) {
            mc.theWorld.skiptick = 0;
        }
    }
}