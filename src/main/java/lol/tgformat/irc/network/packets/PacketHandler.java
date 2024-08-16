package lol.tgformat.irc.network.packets;

/**
 * @author DiaoLing
 * @since 2/20/2024
 */
public interface PacketHandler<T extends Packet> {
    void handle(T packet);
}
