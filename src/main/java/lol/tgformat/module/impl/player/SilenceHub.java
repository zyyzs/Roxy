package lol.tgformat.module.impl.player;

import lol.tgformat.api.event.Listener;
import lol.tgformat.events.PreUpdateEvent;
import lol.tgformat.module.Module;
import lol.tgformat.module.ModuleType;
import lol.tgformat.module.values.impl.NumberSetting;
import lol.tgformat.ui.notifications.NotificationManager;
import lol.tgformat.ui.notifications.NotificationType;

/**
 * @author TG_format
 * @since 2024/8/22 下午8:10
 */
public class SilenceHub extends Module {
    private final NumberSetting health = new NumberSetting("Health", 2.0,20.0, 0.1,0.1);
    public SilenceHub() {
        super("SilenceHub", ModuleType.Player);
    }
    @Listener
    public void onUpdate(PreUpdateEvent event) {
        if (mc.thePlayer.getHealth() <= health.getValue()) {
            mc.thePlayer.sendChatMessage("欣欣哥的自动回城");
            mc.thePlayer.sendChatMessage("/hub");
            NotificationManager.post(NotificationType.SUCCESS,"HUB", "SilenceFix is Best");
        }
    }
}
