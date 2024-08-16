package lol.tgformat.irc.network.packets.server;

import lol.tgformat.irc.network.packets.Packet;
import lol.tgformat.irc.network.packets.PacketBuffer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.IOException;

/**
 * @author TG_format
 * @since 2024/7/22 下午1:37
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ServerLoginStatusPacket implements Packet {
    private boolean success;
    @Override
    public void read(PacketBuffer buffer) throws IOException {
        this.success = buffer.readBoolean();
    }

    @Override
    public void write(PacketBuffer buffer) throws IOException {
        buffer.writeBoolean(this.success);
    }
}
