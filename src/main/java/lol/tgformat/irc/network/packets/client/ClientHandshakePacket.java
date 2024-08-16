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
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClientHandshakePacket implements Packet {
    private int protocolVersion;

    @Override
    public void read(PacketBuffer buffer) throws IOException {
        this.protocolVersion = buffer.readInt();
    }

    @Override
    public void write(PacketBuffer buffer) throws IOException {
        buffer.writeInt(protocolVersion);
    }
}