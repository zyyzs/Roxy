package lol.tgformat.utils.player;

import lol.tgformat.accessable.IMinecraft;
import lol.tgformat.utils.vector.Vector2f;
import net.minecraft.network.play.client.C03PacketPlayer;

/**
 * @author TG_format
 * @since 2024/8/12 下午5:09
 */
public class CurrentRotationUtil implements IMinecraft {
    public static Vector2f currentRotation = new Vector2f(0, 0);
    public static void getCurrentRotation(C03PacketPlayer rotationPacket) {
        if (rotationPacket.rotating) {
            currentRotation = new Vector2f(rotationPacket.getYaw(), rotationPacket.getPitch());
        }
    }
}
