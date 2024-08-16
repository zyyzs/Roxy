package lol.tgformat.component;

import lol.tgformat.accessable.IMinecraft;
import lol.tgformat.api.event.Listener;
import lol.tgformat.events.ChatInputEvent;
import lol.tgformat.module.Module;
import lol.tgformat.module.ModuleManager;
import lol.tgformat.utils.client.LogUtil;
import net.minecraft.client.gui.GuiChat;
import org.lwjgl.input.Keyboard;
import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;
import tech.skidonion.obfuscator.annotations.Renamer;
import tech.skidonion.obfuscator.annotations.StringEncryption;

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

        if (command[0].equals("bind")) {
            try {
                Module module = ModuleManager.getModuleByName(command[1]);
                module.setKey(Keyboard.getKeyIndex(command[2].toUpperCase()));
                LogUtil.addChatMessage("Bind " + module.getName() + " to " + command[2]);
                event.setCancelled(true);
            } catch (Exception e) {
                event.setCancelled(true);
                LogUtil.print(e.getMessage());
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
