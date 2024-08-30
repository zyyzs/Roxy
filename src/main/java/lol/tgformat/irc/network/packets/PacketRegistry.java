package lol.tgformat.irc.network.packets;

import lol.tgformat.irc.network.packets.server.*;
import lol.tgformat.irc.network.packets.server.handlers.*;

/**
 * @author DiaoLing
 * @since 2/20/2024
 */
public class PacketRegistry {
    private final PacketManager packetManager;

    public PacketRegistry(PacketManager packetManager) {
        this.packetManager = packetManager;
    }

    public void register() {
        registerServerPackets();
        registerServerHandlers();
    }

    private void registerServerPackets() {
        packetManager.registerServerPacket(1, ServerHandshakePacket::new);
        packetManager.registerServerPacket(2, ServerRankPacket::new);
        packetManager.registerServerPacket(3, ServerChatMessagePacket::new);
        packetManager.registerServerPacket(4, ServerLoginStatusPacket::new);
        packetManager.registerServerPacket(5, ServerResponseOnlineUsersPacket::new);
        packetManager.registerServerPacket(6, SKeepAlive::new);
    }

    private void registerServerHandlers() {
        packetManager.registerPacketHandler(ServerHandshakePacket.class, new HandshakePacketHandler());
        packetManager.registerPacketHandler(ServerRankPacket.class, new RankPacketHandler());
        packetManager.registerPacketHandler(ServerChatMessagePacket.class, new ChatMessagePacketHandler());
        packetManager.registerPacketHandler(ServerResponseOnlineUsersPacket.class, new OnlineUsersHandler());
        packetManager.registerPacketHandler(SKeepAlive.class, new SKeepAliveHandler());
    }
}