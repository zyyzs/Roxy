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
public class ServerHandshakePacket implements Packet {
    private int status;
    private String message;

    public ServerHandshakePacket(int status) {
        this.status = status;
    }

    @Override
    public void read(PacketBuffer buffer) throws IOException {
        this.status = buffer.readInt();
        this.message = buffer.readString();
    }

    @Override
    public void write(PacketBuffer buffer) throws IOException {
        buffer.writeInt(status);
        buffer.writeString(message);
    }
}