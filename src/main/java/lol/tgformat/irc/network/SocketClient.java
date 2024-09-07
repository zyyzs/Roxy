package lol.tgformat.irc.network;

import lol.tgformat.Client;
import lol.tgformat.irc.network.packets.Packet;
import lol.tgformat.irc.network.packets.PacketBuffer;
import lol.tgformat.irc.network.packets.PacketManager;
import lol.tgformat.irc.network.packets.PacketRegistry;
import lol.tgformat.irc.network.packets.client.CKeepAlive;
import lol.tgformat.irc.network.packets.client.ClientGetRankPacket;
import lol.tgformat.irc.network.packets.client.ClientHandshakePacket;
import lol.tgformat.irc.network.packets.server.SKeepAlive;
import lol.tgformat.irc.network.packets.server.ServerHandshakePacket;
import lol.tgformat.irc.network.packets.server.ServerLoginStatusPacket;
import lol.tgformat.irc.utils.logger.Logger;
import lol.tgformat.utils.client.LogUtil;
import lol.tgformat.utils.timer.TimerUtil;

import lol.tgformat.verify.GuiLogin;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import java.io.IOException;
import java.net.Socket;

/**
 * @author DiaoLing
 * @since 1/29/2024
 */
@Getter
@Setter
@NativeObfuscation
@StringEncryption
@ControlFlowObfuscation
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
    @NativeObfuscation(virtualize = NativeObfuscation.VirtualMachine.TIGER_BLACK)
    private void process() {
        try {
            packetManager.sendPacket(packetBuffer, new ClientHandshakePacket(1), 1);
            while (!socket.isClosed()) {
                Packet packet = packetManager.processPacket(packetBuffer);
                if (packet instanceof ServerLoginStatusPacket packet1) {
                    if (packet1.isSuccess()) {
                        Minecraft.getMinecraft().guiLogin.handleSuccessfulLogin();
                        Client.instance.XuJingLiangSiMa = "许锦良死妈";
                    } else {
                        Minecraft.getMinecraft().guiLogin.handleFailedLogin();
                    }
                }
                if (packet instanceof SKeepAlive) {
                    packetManager.sendPacket(packetBuffer, new CKeepAlive(), 6);
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
