package lol.tgformat.module.impl.misc;

import lol.tgformat.api.event.Listener;
import lol.tgformat.events.ChatInputEvent;
import lol.tgformat.events.WorldEvent;
import lol.tgformat.module.Module;
import lol.tgformat.module.ModuleType;

import lol.tgformat.utils.client.LogUtil;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;
import tech.skidonion.obfuscator.annotations.Renamer;
import tech.skidonion.obfuscator.annotations.StringEncryption;
import us.cubk.irc.client.IRCHandler;
import us.cubk.irc.client.IRCTransport;

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
    public static IRCTransport transport;
    @Override
    @NativeObfuscation(virtualize = NativeObfuscation.VirtualMachine.TIGER_BLACK)
    public void onEnable() {
        try {
            IRCTransport transport = new IRCTransport("103.40.13.87", 14250, new IRCHandler() {
                @Override
                public void onMessage(String sender,String message) {
                    LogUtil.addIRCMessage(sender + ": " + message);
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
                    return String.valueOf(mc.thePlayer.getEntityId());
                }
            });
            transport.connect(mc.session.getUsername(),"none");
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
                transport.sendChat(message);
            }
        }
    }
    @Listener
    @NativeObfuscation(virtualize = NativeObfuscation.VirtualMachine.TIGER_BLACK)
    public void onWorld(WorldEvent event) {
        if (transport != null) {
            transport.sendInGameUsername();
        }
    }
}
