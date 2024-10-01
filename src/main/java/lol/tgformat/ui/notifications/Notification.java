package lol.tgformat.ui.notifications;

import lol.tgformat.ui.clickgui.Utils;
import lol.tgformat.ui.font.CustomFont;
import lol.tgformat.ui.font.FontUtil;
import lol.tgformat.ui.utils.*;
import lombok.Getter;
import net.minecraft.client.gui.Gui;
import net.netease.font.FontManager;
import net.netease.utils.ColorUtil;
import net.netease.utils.RoundedUtils;

import java.awt.*;


@Getter
public class Notification implements Utils {

    private final NotificationType notificationType;
    private final String title, description;
    private final float time;
    private final TenaTimerUtil timerUtil;
    private final Animation animation;

    public Notification(NotificationType type, String title, String description) {
        this(type, title, description, NotificationManager.getToggleTime());
    }

    public Notification(NotificationType type, String title, String description, float time) {
        this.title = title;
        this.description = description;

        this.time = (long) (time * 1000);
        timerUtil = new TenaTimerUtil();
        this.notificationType = type;
        animation = new EaseOutSine(250, 1);
    }
    public void drawDefault(float x, float y, float width, float height) {
        RoundedUtil.drawRound(x, y, width, height, 2, new Color(0, 0, 0, 100));
        tenacityBoldFont20.drawString(getTitle(), x + 11, y + 2f, new Color(255, 255, 255, 255));
        tenacityFont18.drawString(getDescription(), x + 11, y + 6f + tenacityFont18.getHeight(), new Color(255, 255, 255, 255));
        RoundedUtil.drawRound(x + 2f, y + 6f, 2, 12, 0.5f,  notificationType.getColor());
        RoundedUtil.drawGradientHorizontal(x, y, ((getTime() - getTimerUtil().getTime()) / getTime()) * width, height, 2,new Color(0, 249, 255, 119),new Color(211, 24, 255, 97));
    }


    public void drawLettuce(float x2, float y2, float width, float height) {
        RoundedUtils.drawGradientRoundLR(x2, y2-10,(this.getTime() - (float)this.getTimerUtil().getTime()) / this.getTime() * width, height-10, 2.0f, this.notificationType.getColor(), this.notificationType.getColor());
        Color textColor = ColorUtil.applyOpacity(Color.WHITE.brighter(), 80.0f);
        RoundedUtils.drawRound(x2, y2-10,width, height-10, 2.0f, new Color(0, 0, 0, 100));
        FontManager.arial20.drawString(getTitle()+" "+getDescription() + "!", x2 + 12.0f, y2 + 7.0f-10.0f, textColor.getRGB());
        RoundedUtils.drawRound(x2 + 2.0f, y2 + 10.0f-10, 2.0f, 2.0f, 0.5f, this.notificationType.getColor());
    }

    public void drawExhi(float x, float y, float width, float height) {
        Gui.drawRect2(x, y, width, height, new Color(0.1F, 0.1F, 0.1F, .75f).getRGB());
        float percentage = Math.min((timerUtil.getTime() / getTime()), 1);
        Gui.drawRect2(x + (width * percentage), y + height - 1, width - (width * percentage), 1, getNotificationType().getColor().getRGB());
        FontUtil.iconFont40.drawString(getNotificationType().getIcon(), x + 3, (y + FontUtil.iconFont40.getMiddleOfBox(height) + 1), getNotificationType().getColor());

        CustomFont tenacity18 = tenacityFont.size(18);
        tenacityFont20.drawString(getTitle(), x + 7 + FontUtil.iconFont40.getStringWidth(getNotificationType().getIcon()), y + 4, Color.WHITE.getRGB());
        tenacityBoldFont14.drawString(getDescription(), x + 7 + FontUtil.iconFont40.getStringWidth(getNotificationType().getIcon()), y + 8.5f + tenacity18.getHeight(), Color.WHITE.getRGB());
    }


}
