package lol.tgformat.module.impl.misc;

import lol.tgformat.Client;
import lol.tgformat.api.event.Listener;
import lol.tgformat.events.WorldEvent;
import lol.tgformat.irc.network.packets.client.ClientUpdateIGNPacket;
import lol.tgformat.module.Module;
import lol.tgformat.module.ModuleType;
import lol.tgformat.utils.client.LogUtil;

import tech.skidonion.obfuscator.annotations.Renamer;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import java.io.IOException;
import java.util.Objects;

/**
 * @author TG_format
 * @since 2024/7/13 下午5:25
 */
@Renamer
@StringEncryption
public class IRC extends Module {
    public IRC() {
        super("IRC", ModuleType.Misc);
    }
    String name = "";
    @Override
    public void onEnable() {
        Client.instance.getExecutor().execute(() -> {
            try {
                Client.instance.getIrcServer().getClient().connect("103.40.13.87", 28673);
                //Client.instance.getIrcServer().getClient().connect("127.0.0.1", 45600);
            } catch (IOException e) {
                LogUtil.print("Failed to connect to the server: " + e.getMessage());
            }
        });
    }
    @Override
    public void onDisable() {
        Client.instance.getIrcServer().getClient().close();
    }
    @Listener
    public void onWorldChange(WorldEvent e) {
        try {
            if (!Objects.equals(name, mc.thePlayer.getName())) {
                Client.instance.getIrcServer().getClient().getPacketManager().sendPacket(Client.instance.getIrcServer().getClient().getPacketBuffer(),
                        new ClientUpdateIGNPacket(mc.thePlayer.getName()), 5);
                name = mc.thePlayer.getName();
            }
        } catch (IOException ex) {
            LogUtil.addChatMessage(ex.getMessage());
        }
    }
}
