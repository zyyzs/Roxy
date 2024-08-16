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
 * @since 2024/7/22 下午11:53
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ServerResponseOnlineUsersPacket implements Packet {
    private String users;
    @Override
    public void read(PacketBuffer buffer) throws IOException {
        this.users = buffer.readString();
    }

    @Override
    public void write(PacketBuffer buffer) throws IOException {
        buffer.writeString(this.users);
    }
}
