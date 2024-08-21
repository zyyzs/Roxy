package lol.tgformat.module.impl.misc;

import lol.tgformat.Client;
import lol.tgformat.api.event.Listener;
import lol.tgformat.events.WorldEvent;
import lol.tgformat.irc.network.packets.client.ClientUpdateIGNPacket;
import lol.tgformat.module.Module;
import lol.tgformat.module.ModuleType;
import lol.tgformat.utils.client.LogUtil;
import lol.tgformat.verify.GuiLogin;
import tech.skidonion.obfuscator.annotations.Renamer;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import java.io.IOException;

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
    @Override
    public void onEnable() {
        Client.instance.getExecutor().execute(() -> {
            try {
                Client.instance.getIrcServer().getClient().connect("103.40.13.87", 28673);
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
            Client.instance.getIrcServer().getClient().getPacketManager().sendPacket(Client.instance.getIrcServer().getClient().getPacketBuffer(),
                    new ClientUpdateIGNPacket(GuiLogin.uid, mc.thePlayer.getDisplayName().getUnformattedText()), 5);
        } catch (IOException ex) {
            LogUtil.addChatMessage(ex.getMessage());
        }
    }
}
