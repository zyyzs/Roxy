package lol.tgformat.ui.altlogin.util;

import lol.tgformat.ui.utils.Animation;
import lol.tgformat.ui.utils.DecelerateAnimation;
import lol.tgformat.ui.utils.Direction;
import net.netease.font.FontManager;
import net.netease.utils.ColorUtils;
import net.netease.utils.RoundedUtils;

import java.awt.*;

/**
 * @Author KuChaZi
 * @Date 2024/6/30 22:04
 * @ClassName: Button
 */
public class Button {
    public String displayName;
    public float x;
    public float y;
    public float width;
    public float height;
    private final Animation hoverAnimation = new DecelerateAnimation(500, 1.0);;

    private boolean isHovered;


    public Button(String displayName, float x, float y, float width, float height) {
        this.displayName = displayName;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void drawButton(int mouseX, int mouseY) {
        isHovered = isHovering(x, y, width, height, mouseX, mouseY);
        hoverAnimation.setDirection(isHovered ? Direction.FORWARDS : Direction.BACKWARDS);
        Color rectColor = new Color(35, 37, 43, 102);
        rectColor = ColorUtils.interpolateColorC(rectColor, ColorUtils.brighter(rectColor, 0.4f), hoverAnimation.getOutput().floatValue());
        RoundedUtils.drawRoundNoOffset(x, y, width, height, 2, rectColor);
        FontManager.arial18.drawCenteredString(displayName, x + width / 2, y + height / 2 - 3, -1);
    }

    public void clicked(int mouse, Runnable runnable) {
        if (isHovered && mouse == 0) {
            runnable.run();
        }
    }

    public void update(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public static boolean isHovering(float x, float y, float width, float height, int mouseX, int mouseY) {
        return mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
    }
}
