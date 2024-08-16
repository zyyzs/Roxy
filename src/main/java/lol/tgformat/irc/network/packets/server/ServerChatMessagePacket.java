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
public class ServerChatMessagePacket implements Packet {
    private String message;
    private String username;
    private String rank;
    private boolean isSystemMessage;
    @Override
    public void read(PacketBuffer buffer) throws IOException {
        this.message = buffer.readString();
        this.username = buffer.readString();
        this.rank = buffer.readString();
        this.isSystemMessage = buffer.readBoolean();
    }

    @Override
    public void write(PacketBuffer buffer) throws IOException {
        buffer.writeString(message);
        buffer.writeString(username);
        buffer.writeString(rank);
        buffer.writeBoolean(isSystemMessage);
    }
}

