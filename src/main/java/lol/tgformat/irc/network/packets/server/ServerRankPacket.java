package lol.tgformat.irc.network.packets.server;

import lol.tgformat.irc.network.packets.Packet;
import lol.tgformat.irc.network.packets.PacketBuffer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.IOException;

/**
 * @author DiaoLing
 * @since 2/20/2024
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ServerRankPacket implements Packet {
    private String rank;

    @Override
    public void read(PacketBuffer buffer) throws IOException {
        this.rank = buffer.readString();
    }

    @Override
    public void write(PacketBuffer buffer) throws IOException {
        buffer.writeString(rank);
    }
}