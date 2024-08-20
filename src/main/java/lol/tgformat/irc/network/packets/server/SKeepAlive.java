package lol.tgformat.irc.network.packets.server;

import lol.tgformat.irc.network.packets.Packet;
import lol.tgformat.irc.network.packets.PacketBuffer;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;

/**
 * @author TG_format
 * @since 2024/8/20 下午9:03
 */
@Getter
@Setter
public class SKeepAlive implements Packet {
    public SKeepAlive() {

    }

    @Override
    public void read(PacketBuffer buffer) throws IOException {

    }

    @Override
    public void write(PacketBuffer buffer) throws IOException {

    }
}
