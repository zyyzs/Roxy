package lol.tgformat.module.impl.misc;

import lol.tgformat.api.event.Listener;
import lol.tgformat.events.PreUpdateEvent;
import lol.tgformat.module.Module;
import lol.tgformat.module.ModuleType;
import lol.tgformat.module.values.impl.ModeSetting;
import lol.tgformat.module.values.impl.NumberSetting;
import lol.tgformat.utils.network.PacketUtil;
import lol.tgformat.utils.timer.TimerUtil;
import net.minecraft.network.play.client.C01PacketChatMessage;
import tech.skidonion.obfuscator.annotations.StringEncryption;

/**
 * @author TG_format
 * @since 2024/8/22 下午8:17
 */
@StringEncryption
public class Spammer extends Module {
    public Spammer() {
        super("Spammer", ModuleType.Misc);
    }
    private final NumberSetting delay = new NumberSetting("Delay",1000, 5000, 100,10);
    public final ModeSetting mode = new ModeSetting("TextMode", "SilenceMoment","SilenceMoment","NightSense");
    private TimerUtil timer = new TimerUtil();
    @Listener
    public void onUpdate(PreUpdateEvent event) {
        if (timer.hasReached(delay.getValue() * 1.5)) {
            PacketUtil.sendPacketNoEvent(new C01PacketChatMessage(AutoL.getRandomText(getTextMode())));
            timer.reset();
        }
    }
    public String[] getTextMode() {
        return switch (mode.getMode()) {
            case "NightSense" -> AutoL.Nightsensetext;
            default -> AutoL.hytText;
        };
    }
}
