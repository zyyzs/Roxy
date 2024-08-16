package net.netease.utils;

import lombok.Getter;
import net.minecraft.client.gui.GuiScreen;
import net.netease.font.FontManager;

import java.awt.*;

/**
 * @author TG_format
 * @since 2024/6/1 13:32
 */
@Getter
public class CustomMenuButton extends GuiScreen {
    public final String text;
    private Animation displayAnimation;
    private Animation hoverAnimation = new DecelerateAnimation(500, 1.0D);
    public float x;
    public float y;
    public float width;
    public float height;
    public Runnable clickAction;
    public RapeMasterFontManager font;

    public CustomMenuButton(String text, Runnable clickAction) {
        this.font = FontManager.arial20;
        this.text = text;
        this.displayAnimation = new DecelerateAnimation(1000, 255.0D);
        this.font = FontManager.arial20;
        this.clickAction = clickAction;
    }

    public CustomMenuButton(String text) {
        this.font = FontManager.arial20;
        this.text = text;
        this.displayAnimation = new DecelerateAnimation(1000, 255.0D);
        this.font = FontManager.arial20;
    }

    public void initGui() {
        this.hoverAnimation = new DecelerateAnimation(500, 1.0D);
        this.displayAnimation.setDirection(Direction.FORWARDS);
    }

    public void keyTyped(char typedChar, int keyCode) {
    }

    public void drawScreen(int mouseX, int mouseY, float ticks) {
        boolean hovered = RenderUtil.isHovering(this.x, this.y, this.width, this.height, mouseX, mouseY);
        this.hoverAnimation.setDirection(hovered ? Direction.FORWARDS : Direction.BACKWARDS);
        Color rectColor = new Color(32, 32, 32, (int)(this.displayAnimation.getOutput() * Math.max(0.7D, this.hoverAnimation.getOutput())));
        RoundedUtils.drawRound(this.x, this.y, this.width, this.height, 4.0F, rectColor);
        this.font.drawCenteredString(this.text, this.x + this.width / 2.0F, this.y + this.font.getMiddleOfBox(this.height) + 2.0F, (new Color(255, 255, 255, (int)this.displayAnimation.getOutput())).getRGB());
    }

    public void mouseClicked(int mouseX, int mouseY, int button) {
        boolean hovered = RenderUtil.isHovering(this.x, this.y, this.width, this.height, mouseX, mouseY);
        if (hovered) {
            this.clickAction.run();
        }

    }

    public void mouseReleased(int mouseX, int mouseY, int state) {
    }

    public void onGuiClosed() {
        this.displayAnimation.setDirection(Direction.BACKWARDS);
    }

    public void setDisplayAnimation(Animation displayAnimation) {
        this.displayAnimation = displayAnimation;
    }

    public void setHoverAnimation(Animation hoverAnimation) {
        this.hoverAnimation = hoverAnimation;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public void setClickAction(Runnable clickAction) {
        this.clickAction = clickAction;
    }

    public void setFont(RapeMasterFontManager font) {
        this.font = font;
    }
}
