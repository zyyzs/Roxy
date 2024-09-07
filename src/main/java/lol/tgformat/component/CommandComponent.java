package lol.tgformat.component;

import lol.tgformat.Client;
import lol.tgformat.accessable.IMinecraft;
import lol.tgformat.api.event.Listener;
import lol.tgformat.config.ConfigManager;
import lol.tgformat.events.ChatInputEvent;
import lol.tgformat.firend.FriendsCollection;
import lol.tgformat.module.Module;
import lol.tgformat.module.ModuleManager;
import lol.tgformat.utils.client.LogUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import org.lwjgl.input.Keyboard;
import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;
import tech.skidonion.obfuscator.annotations.Renamer;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

/**
 * @author TG_format
 * @since 2024/6/9 下午9:15
 */
@Renamer
@StringEncryption
public class CommandComponent implements IMinecraft {
    private static final int CACHE_SIZE = 10;
    private LinkedList<String> chatCache = new LinkedList<>();

    @Listener
    public void onChat(ChatInputEvent event) {
        String msg = event.getMessage();

        if (!msg.startsWith(".")) return;

        addToCache(msg);

        String[] command = msg.substring(1).split(" ");
        if (command.length == 0) return;
        switch (command[0]) {
            case "bind":
                try {
                    Module module = ModuleManager.getModuleByName(command[1]);
                    module.setKey(Keyboard.getKeyIndex(command[2].toUpperCase()));
                    LogUtil.addChatMessage("Bind " + module.getName() + " to " + command[2].toUpperCase());
                    event.setCancelled(true);
                } catch (Exception e) {
                    event.setCancelled(true);
                    LogUtil.print(e.getMessage());
                }
                event.setCancelled(true);
                break;
            case "binds":
                for (Module module : ModuleManager.getModules()) {
                    if (module.getKey() == 0) {
                        continue;
                    }
                    LogUtil.addChatMessage(module.getName() + " " + Keyboard.getKeyName(module.getKey()));
                }
                event.setCancelled(true);
                break;
            case "config":
                if (command[1].equals("load")) {
                    Client.instance.getConfigManager().loadConfig(Client.instance.getConfigManager().readConfigData(
                            new File(Minecraft.getMinecraft().mcDataDir + "/" + Client.instance.getName() + "/Configs/" + command[2] + ".json").toPath()
                    ));
                    LogUtil.addChatMessage("Config loaded");
                }
                event.setCancelled(true);
                break;
            case "friend":
                if (command[1].equals("add")) {
                    FriendsCollection.addFriend(command[2]);
                    for (String name : FriendsCollection.friends) {
                        LogUtil.addChatMessage(name);
                    }
                    event.setCancelled();
                } else if (command[1].equals("remove")) {
                    FriendsCollection.removeFriend(command[2]);
                    for (String name : FriendsCollection.friends) {
                        LogUtil.addChatMessage(name);
                    }
                    event.setCancelled();
                }
        }
    }

    private void addToCache(String msg) {
        if (chatCache.size() >= CACHE_SIZE) {
            chatCache.removeFirst();
        }
        chatCache.addLast(msg);
        updateChatHistory(msg);
    }

    private void updateChatHistory(String msg) {
        GuiChat guiChat = (GuiChat) mc.currentScreen;
        if (guiChat != null) {
            List<String> history = mc.ingameGUI.getChatGUI().getSentMessages();
            if (history.size() >= CACHE_SIZE) {
                history.remove(0);
            }
            history.add(msg);
        }
    }
}
