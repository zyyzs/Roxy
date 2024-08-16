package lol.tgformat.irc.network.packets;

import java.io.IOException;

/**
 * @author DiaoLing
 * @since 2/20/2024
 */
public interface Packet {
    void read(PacketBuffer buffer) throws IOException;
    void write(PacketBuffer buffer) throws IOException;
}