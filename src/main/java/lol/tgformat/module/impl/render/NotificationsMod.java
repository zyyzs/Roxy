package lol.tgformat.module.impl.render;

import lol.tgformat.module.Module;
import lol.tgformat.module.ModuleType;
import lol.tgformat.module.values.impl.BooleanSetting;
import lol.tgformat.module.values.impl.ModeSetting;
import lol.tgformat.module.values.impl.NumberSetting;
import lol.tgformat.ui.clickgui.Utils;
import lol.tgformat.ui.notifications.Notification;
import lol.tgformat.ui.notifications.NotificationManager;
import lol.tgformat.ui.utils.Animation;
import lol.tgformat.ui.utils.Direction;
import net.minecraft.client.gui.ScaledResolution;
import net.netease.font.FontManager;

import static net.netease.font.FontManager.arial18;
import static net.netease.font.FontManager.arial20;

/**
 * @author TG_format
 * @since 2024/6/15 下午9:07
 */
public class NotificationsMod extends Module {
    private final NumberSetting time = new NumberSetting("Time on Screen", 2, 10, 1, .5);
    private final ModeSetting modes = new ModeSetting("Mode", "Default", "Default", "New");
    public static final BooleanSetting toggleNotifications = new BooleanSetting("Show Toggle", true);
    public int offsetValue = 0;

    public NotificationsMod() {
        super("Notifications", ModuleType.Render);
    }

    public void render() {
        switch (modes.getMode()) {
            case "Default": {
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
            case "New":{
                ScaledResolution sr = new ScaledResolution(mc);
                float yOffset = 0.0f;
                NotificationManager.setToggleTime(2.0f);
                for (Notification notification : NotificationManager.getNotifications()) {
                    Animation animation = notification.getAnimation();
                    animation.setDirection(notification.getTimerUtil().hasTimeElapsed((long)notification.getTime()) ? Direction.BACKWARDS : Direction.FORWARDS);
                    if (animation.finished(Direction.BACKWARDS)) {
                        NotificationManager.getNotifications().remove(notification);
                        continue;
                    }
                    animation.setDuration(200);
                    int actualOffset = 5;
                    int notificationHeight = 31;
                    int notificationWidth = FontManager.arial20.getStringWidth(notification.getTitle())+ arial20.getStringWidth(notification.getDescription()) + 33;
                    float x2 = (float)((double)sr.getScaledWidth() - (double)(notificationWidth + 8) * animation.getOutput());
                    float y2 = (float)sr.getScaledHeight() - (yOffset + 18.0f + (float)this.offsetValue + (float)notificationHeight + 15.0f);
                    notification.drawLettuce(x2, y2, notificationWidth, notificationHeight);
                    yOffset = (float)((double)yOffset + (double)(notificationHeight + actualOffset) * animation.getOutput());
                }

            }

        }
    }
}


