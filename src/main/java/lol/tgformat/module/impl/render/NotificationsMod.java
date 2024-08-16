package lol.tgformat.module.impl.render;

import lol.tgformat.module.Module;
import lol.tgformat.module.ModuleType;
import lol.tgformat.module.values.impl.BooleanSetting;
import lol.tgformat.module.values.impl.NumberSetting;
import lol.tgformat.ui.clickgui.Utils;
import lol.tgformat.ui.notifications.Notification;
import lol.tgformat.ui.notifications.NotificationManager;
import lol.tgformat.ui.utils.Animation;
import lol.tgformat.ui.utils.Direction;
import net.minecraft.client.gui.ScaledResolution;

/**
 * @author TG_format
 * @since 2024/6/15 下午9:07
 */
public class NotificationsMod extends Module {
    private final NumberSetting time = new NumberSetting("Time on Screen", 2, 10, 1, .5);
    public static final BooleanSetting toggleNotifications = new BooleanSetting("Show Toggle", true);

    public NotificationsMod() {
        super("Notifications", ModuleType.Render);
    }

    public void render() {
        float yOffset = 0;
        int notificationHeight;
        int notificationWidth;
        int actualOffset;
        ScaledResolution sr = new ScaledResolution(Utils.mc);

        NotificationManager.setToggleTime(time.getValue().floatValue());

        for (Notification notification : NotificationManager.getNotifications()) {
            Animation animation = notification.getAnimation();
            animation.setDirection(notification.getTimerUtil().hasTimeElapsed((long) notification.getTime()) ? Direction.BACKWARDS : Direction.FORWARDS);

            if (animation.finished(Direction.BACKWARDS)) {
                NotificationManager.getNotifications().remove(notification);
                continue;
            }

            float x, y;

                    animation.setDuration(250);
                    actualOffset = 10;
                    notificationHeight = 24;
                    notificationWidth = (int) Math.max(Utils.tenacityBoldFont20.getStringWidth(notification.getTitle()), Utils.tenacityFont18.getStringWidth(notification.getDescription())) + 25;

                    x = sr.getScaledWidth() - (notificationWidth + 5) * (float) animation.getOutput().floatValue();
                    y = sr.getScaledHeight() - (yOffset + 18 + 0 + notificationHeight + (15));

                    notification.drawDefault(x, y, notificationWidth, notificationHeight);

            yOffset += (notificationHeight + actualOffset) * animation.getOutput().floatValue();

        }
    }
}


