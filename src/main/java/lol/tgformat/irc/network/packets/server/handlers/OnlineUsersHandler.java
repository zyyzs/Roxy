package lol.tgformat.irc.network.packets.server.handlers;

import com.alibaba.fastjson.JSONObject;
import lol.tgformat.firend.FriendsCollection;
import lol.tgformat.irc.items.User;
import lol.tgformat.irc.network.packets.PacketHandler;
import lol.tgformat.irc.network.packets.server.ServerResponseOnlineUsersPacket;

/**
 * @author TG_format
 * @since 2024/7/23 上午12:02
 */
public class OnlineUsersHandler implements PacketHandler<ServerResponseOnlineUsersPacket> {
    @Override
    public void handle(ServerResponseOnlineUsersPacket packet) {
        JSONObject user = JSONObject.parseObject(packet.getUsers());
        User onlineUser = user.toJavaObject(User.class);
        FriendsCollection.friendsNames.add(onlineUser.getIGN());
        FriendsCollection.IRC_friends.add(onlineUser);
    }
}
