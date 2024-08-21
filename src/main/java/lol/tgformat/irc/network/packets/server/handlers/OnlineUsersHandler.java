package lol.tgformat.irc.network.packets.server.handlers;

import lol.tgformat.firend.FriendsCollection;
import lol.tgformat.irc.network.packets.PacketHandler;
import lol.tgformat.irc.network.packets.server.ServerResponseOnlineUsersPacket;

/**
 * @author TG_format
 * @since 2024/7/23 上午12:02
 */
public class OnlineUsersHandler implements PacketHandler<ServerResponseOnlineUsersPacket> {
    @Override
    public void handle(ServerResponseOnlineUsersPacket packet) {
        FriendsCollection.friends = packet.getUsers().split(" ");
    }
}
