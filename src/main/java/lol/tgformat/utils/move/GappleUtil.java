package lol.tgformat.utils.move;

import lol.tgformat.accessable.IMinecraft;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;

/**
 * @author Genius
 * @since 2024/8/25 下午10:39
 * IntelliJ IDEA
 */

public class GappleUtil implements IMinecraft {

    public static final GappleUtil INSTANCE = new GappleUtil();
    public static int noMovePackets = 0;

    public static void packetEvent(Packet packet) {
        if (packet instanceof C03PacketPlayer) {
            noMovePackets = ((C03PacketPlayer)packet).isMoving() ? 0 : (noMovePackets = noMovePackets + 1);
        }
    }

}
