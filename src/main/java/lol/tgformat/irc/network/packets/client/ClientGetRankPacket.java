package lol.tgformat.irc.network.packets.client;

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
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ClientGetRankPacket implements Packet {
    private String username;

    @Override
    public void read(PacketBuffer buffer) throws IOException {
        this.username = buffer.readString();
    }

    @Override
    public void write(PacketBuffer buffer) throws IOException {
        buffer.writeString(username);
    }
}