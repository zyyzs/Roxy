package lol.tgformat.irc.network.packets.server.handlers;

import lol.tgformat.irc.network.packets.PacketHandler;
import lol.tgformat.irc.network.packets.server.ServerRankPacket;
import lol.tgformat.irc.utils.logger.Logger;

/**
 * @author DiaoLing
 * @since 2/20/2024
 */
public class RankPacketHandler implements PacketHandler<ServerRankPacket> {
    @Override
    public void handle(ServerRankPacket packet) {
        Logger.info("Received rank: " + packet.getRank());
    }
}