package lol.tgformat.irc.network.packets.server.handlers;

import lol.tgformat.irc.network.packets.PacketHandler;
import lol.tgformat.irc.network.packets.server.ServerHandshakePacket;
import lol.tgformat.irc.utils.logger.Logger;

/**
 * @author DiaoLing
 * @since 2/20/2024
 */
public class HandshakePacketHandler implements PacketHandler<ServerHandshakePacket> {

    @Override
    public void handle(ServerHandshakePacket packet) {
        if (packet.getStatus() == 1) {
            Logger.success("Handshake successful: " + packet.getMessage());
        } else {
            Logger.error("Handshake failed: " + packet.getMessage());
        }
    }
}
