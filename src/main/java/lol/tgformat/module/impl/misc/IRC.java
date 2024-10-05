package lol.tgformat.module.impl.misc;

import lol.tgformat.Client;
import lol.tgformat.api.event.Listener;
import lol.tgformat.events.ChatInputEvent;
import lol.tgformat.events.WorldEvent;
import lol.tgformat.events.packet.PacketReceiveEvent;
import lol.tgformat.module.Module;
import lol.tgformat.module.ModuleType;

import lol.tgformat.ui.notifications.NotificationManager;
import lol.tgformat.ui.notifications.NotificationType;
import lol.tgformat.utils.client.LogUtil;
import net.minecraft.network.play.server.S02PacketChat;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;
import tech.skidonion.obfuscator.annotations.Renamer;
import tech.skidonion.obfuscator.annotations.StringEncryption;
import tech.skidonion.obfuscator.inline.Wrapper;
import us.cubk.irc.client.IRCHandler;
import us.cubk.irc.client.IRCTransport;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    public static IRCTransport transport;
    public boolean banned = false;
    @Override
    public void onEnable() {
        try {
            IRCTransport transport = new IRCTransport("103.40.13.87", 14250, new IRCHandler() {
            //IRCTransport transport = new IRCTransport("127.0.0.1", 8888, new IRCHandler() {
                @Override
                public void onMessage(String sender,String message) {
                    LogUtil.addIRCMessage(sender + "("+getName()+"): " + message);
                }

                @Override
                public void onDisconnected(String message) {
                    System.out.println("Disconnected: " + message);
                    IRC.transport = null;
                }

                @Override
                public void onConnected() {
                    System.out.println("Connected");
                }

                @Override
                public String getInGameUsername() {
                    if (mc.thePlayer == null) {
                        return "Unknown";
                    }
                    return mc.thePlayer.getName();
                }
            });
            if(Wrapper.getUsername().get().equals("development")){
                File ircname = new File(mc.mcDataDir+"/ircname");
                BufferedReader br = new BufferedReader(new FileReader(ircname));
                transport.connect(br.readLine(), "none");
            }else{
                transport.connect(Wrapper.getUsername().get(),"none");
            }
            if (IRC.transport == null) {
                IRC.transport = transport;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Listener
    public void onChat(ChatInputEvent event) {
        if (event.getMessage().startsWith(".irc")) {
            String message = event.getMessage().substring(".irc".length() + 1);
            event.setCancelled();
            if (transport != null) {
                transport.sendChat("§f"+"(§b"+mc.thePlayer.getName()+"§f) :"+message);
            }
        }
    }
    @Listener
    public void onWorld(WorldEvent event) {
        banned = false;
        if (transport != null) {
            transport.sendInGameUsername();
        }
    }
    @Listener
    public void onPacketReceive(PacketReceiveEvent event) {
        Object packet = event.getPacket();
        if (packet instanceof S02PacketChat) {
            S02PacketChat s02PacketChat = (S02PacketChat) packet;
            String text = s02PacketChat.getChatComponent().getUnformattedText();
            Matcher matcher4 = Pattern.compile("玩家(.*?)在本局游戏中行为异常").matcher(text);
            if (matcher4.find()) {
                if (transport.isUser(matcher4.group(1))) {
                    LogUtil.addChatMessage("User"+transport.getName(matcher4.group(1))+"Was Banned.");
                }
            }
        }
    }
}
