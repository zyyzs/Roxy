package lol.tgformat.verify.utils;

import java.awt.Color;

import lol.tgformat.utils.render.RenderUtils;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.netease.font.FontManager;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;
import tech.skidonion.obfuscator.annotations.Renamer;
import tech.skidonion.obfuscator.annotations.StringEncryption;

/**
 * @Author KuChaZi
 * @Date 2024/7/15 11:52
 * @ClassName: HydraButton
 */

@Setter
@Renamer
@StringEncryption
public class HydraButton extends GuiButton {
    private int color;

    public HydraButton(final int buttonId, final int x, final int y, final int widthIn, final int heightIn, final String buttonText) {
        super(buttonId, x, y, widthIn, heightIn, buttonText);
    }

    @Override
    public void drawButton(final Minecraft mc, final int mouseX, final int mouseY) {
        if (this.visible) {
            this.hovered = (mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height);
            RenderUtils.drawRect(this.xPosition, this.yPosition, this.xPosition + this.width, this.yPosition + this.height, this.color);
            FontManager.arial40.drawString(this.displayString, this.xPosition + (this.width / 2) - FontManager.arial40.getStringWidth(this.displayString) / 2, this.yPosition + (this.height / 2) - FontManager.arial40.getHeight() / 2, Color.WHITE.getRGB(), true);
        }
    }

    public void updateCoordinates(final float x, final float y) {
        this.xPosition = (int) x;
        this.yPosition = (int) y;
    }

    public boolean hovered(final int mouseX, final int mouseY) {
        return mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
    }
}

