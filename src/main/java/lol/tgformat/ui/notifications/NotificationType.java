package lol.tgformat.ui.notifications;

import lol.tgformat.ui.font.FontUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.awt.*;

@Getter
@AllArgsConstructor
public enum NotificationType {
    SUCCESS(new Color(0, 255, 81), FontUtil.CHECKMARK),
    DISABLE(new Color(255, 0, 0), FontUtil.XMARK),
    INFO(new Color(23, 139, 255), FontUtil.INFO),
    WARNING(Color.YELLOW, FontUtil.WARNING);
    private final Color color;
    private final String icon;
}