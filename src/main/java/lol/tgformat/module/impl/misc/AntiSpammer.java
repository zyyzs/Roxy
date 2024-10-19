package lol.tgformat.module.impl.misc;

import lol.tgformat.api.event.Listener;
import lol.tgformat.events.packet.PacketReceiveEvent;
import lol.tgformat.module.Module;
import lol.tgformat.module.ModuleType;
import net.minecraft.network.play.server.S02PacketChat;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AntiSpammer extends Module {
    public AntiSpammer() {
        super("AntiSpammer", ModuleType.Misc);
    }

    final Pattern pattern = Pattern.compile("(\\([0-9]+\\)) §r§f<([^>]+)> (.*)");

    public final List<String> sb = Arrays.asList(
            "SilenceFix",
            "xinxin."
    );

    @Listener
    public void onPacket(PacketReceiveEvent event) {
        if (event.getPacket() instanceof S02PacketChat chat) {
            String message = chat.getChatComponent().getFormattedText();
            Matcher matcher = pattern.matcher(message.trim());
            if (matcher.find()) {
                String s1 = matcher.group(2);
                if (sb.contains(s1)) {
                    event.setCancelled(true);
                }
            }
        }
    }
}
