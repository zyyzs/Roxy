package lol.tgformat.utils.network;

import lol.tgformat.accessable.IMinecraft;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;

/**
 * @author TG_format
 * @since 2024/8/11 下午6:25
 */
public class GetC03StatusUtil implements IMinecraft {
    public static final GetC03StatusUtil INSTANCE = new GetC03StatusUtil();
    public static int noMovePackets = 0;

    public static void packetEvent(Packet<?> packet) {
        if (packet instanceof C03PacketPlayer) {
            noMovePackets = ((C03PacketPlayer) packet).isMoving() ? 0 : ++noMovePackets;
        }
    }
}
