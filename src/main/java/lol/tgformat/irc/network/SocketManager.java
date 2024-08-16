package lol.tgformat.irc.network;

import lol.tgformat.irc.network.packets.Packet;
import lol.tgformat.irc.network.packets.client.ClientChatMessagePacket;
import lol.tgformat.irc.utils.logger.Logger;
import lol.tgformat.api.event.EventManager;
import lol.tgformat.api.event.Listener;
import lol.tgformat.events.ChatInputEvent;
import lol.tgformat.module.ModuleManager;
import lol.tgformat.module.impl.misc.IRC;
import lol.tgformat.verify.GuiLogin;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.Gui;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import java.io.IOException;

/**
 * @author DiaoLing
 * @since 1/21/2024
 */
@Getter
@Setter
@StringEncryption
public class SocketManager {
    private String ircPrefix = "!";
    private SocketClient client;

    public void initialize() {
        EventManager.register(this);
        this.client = new SocketClient();
    }

    @Listener
    public void onChatInput(ChatInputEvent event) {
        String message = event.getMessage();

        if (!message.startsWith(ircPrefix) || !ModuleManager.getModule(IRC.class).isState()) {
            return;
        }

        event.setCancelled(true);

        String msg = message.substring(ircPrefix.length());
        sendPacket(new ClientChatMessagePacket(msg, GuiLogin.uid, getRank(GuiLogin.uid)));
    }
    public String getRank(String uid) {
        return switch (uid) {
            case "zyyzs", "KuChaZi" -> "Admin";
            default -> "User";
        };
    }

    public void sendPacket(Packet packet) {
        try {
            client.getPacketManager().sendPacket(client.getPacketBuffer(), packet, 2);
        } catch (IOException e) {
            Logger.error("Error sending packet: " + e.getMessage());
        }
    }
}
