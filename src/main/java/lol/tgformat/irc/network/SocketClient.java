package lol.tgformat.irc.network;

import lol.tgformat.irc.network.packets.Packet;
import lol.tgformat.irc.network.packets.PacketBuffer;
import lol.tgformat.irc.network.packets.PacketManager;
import lol.tgformat.irc.network.packets.PacketRegistry;
import lol.tgformat.irc.network.packets.client.ClientGetRankPacket;
import lol.tgformat.irc.network.packets.client.ClientHandshakePacket;
import lol.tgformat.irc.network.packets.server.ServerHandshakePacket;
import lol.tgformat.irc.utils.logger.Logger;
import lol.tgformat.utils.client.LogUtil;
import lol.tgformat.verify.GuiLogin;
import lombok.Getter;
import lombok.Setter;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import java.io.IOException;
import java.net.Socket;

/**
 * @author DiaoLing
 * @since 1/29/2024
 */
@Getter
@Setter
@StringEncryption
public class SocketClient {
    private Socket socket;
    private PacketBuffer packetBuffer;
    private PacketManager packetManager;
    private boolean isConnected = false;

    public void connect(String host, int port) throws IOException {
        socket = new Socket(host, port);
        isConnected = true;
        Logger.success("Connected to server at BaiZi's verification server");

        packetBuffer = new PacketBuffer(socket.getInputStream(), socket.getOutputStream());
        packetManager = new PacketManager();
        PacketRegistry packetRegistry = new PacketRegistry(packetManager);
        packetRegistry.register();

        process();
    }

    private void process() {
        try {
            packetManager.sendPacket(packetBuffer, new ClientHandshakePacket(1), 1);
            while (!socket.isClosed()) {
                Packet packet = packetManager.processPacket(packetBuffer);
                if (packet instanceof ServerHandshakePacket wrapper) {
                    if (wrapper.getStatus() == 1) {
                        ClientGetRankPacket getRankPacket = new ClientGetRankPacket(GuiLogin.uid);
                        packetManager.sendPacket(packetBuffer, getRankPacket, 3);
                    }
                }
            }
        } catch (Exception e) {
            Logger.error("Connection with server lost. Error: " + e);
        }
    }

    public void close() {
        try {
            if (packetBuffer != null) {
                packetBuffer.close();
            }
            if (socket != null) {
                socket.close();
                isConnected = false;
                Logger.info("Connection closed");
            }
        } catch (IOException e) {
            LogUtil.addChatMessage(e.getMessage());
            Logger.error("Error closing client resources: " + e.getMessage());
        }
    }
}
