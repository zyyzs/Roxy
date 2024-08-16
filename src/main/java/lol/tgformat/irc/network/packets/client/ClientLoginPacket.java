package lol.tgformat.irc.network.packets.client;

import lol.tgformat.irc.network.packets.Packet;
import lol.tgformat.irc.network.packets.PacketBuffer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.IOException;

/**
 * @author TG_format
 * @since 2024/7/22 下午1:18
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClientLoginPacket implements Packet {
    private String uid;
    private String hwid;

    @Override
    public void read(PacketBuffer buffer) throws IOException {
        this.uid = buffer.readString();
        this.hwid = buffer.readString();
    }

    @Override
    public void write(PacketBuffer buffer) throws IOException {
        buffer.writeString(this.uid);
        buffer.writeString(this.hwid);
    }
}
