package lol.tgformat.utils.client;

import com.mojang.realmsclient.gui.ChatFormatting;
import lol.tgformat.accessable.IMinecraft;
import net.minecraft.util.ChatComponentText;
import tech.skidonion.obfuscator.annotations.Renamer;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import java.text.SimpleDateFormat;
import java.util.Date;

import static lol.tgformat.module.impl.render.HUD.clientName;

/**
 * @author TG_format
 * @since 2024/5/31 20:08
 */

@Renamer
@StringEncryption
public class LogUtil implements IMinecraft {

    private static final String prefix = "[" + ChatFormatting.DARK_AQUA + name() + ChatFormatting.RESET + "]";

    public static void print(Object message) {
        System.out.println(message);
    }

    public static void addChatMessage(String message) {
        if(mc.thePlayer == null)return;
        mc.thePlayer.addChatMessage(new ChatComponentText(prefix + " " + message));
    }
    public static void addIRCMessage(String message) {
        if(mc.thePlayer == null)return;
        mc.thePlayer.addChatMessage(new ChatComponentText(message));
    }
    private static String name() {
        String name = "Rise";
        if (!clientName.getString().isEmpty()) {
            name = clientName.getString().replace("%time%", new SimpleDateFormat("HH:mm").format(new Date()));
        }

        return name;
    }
}
