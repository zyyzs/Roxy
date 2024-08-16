package lol.tgformat.verify.utils;

import lol.tgformat.utils.render.RenderUtils;
import lol.tgformat.utils.timer.TimerUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.MathHelper;
import net.netease.font.FontManager;
import org.lwjgl.input.Mouse;
import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;
import tech.skidonion.obfuscator.annotations.Renamer;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import java.awt.*;

import static lol.tgformat.accessable.IMinecraft.mc;

@Renamer
@StringEncryption
@NativeObfuscation
public class UIDField extends GuiTextField {

    private final String shit;
    private int color, textColor;
    private final TimerUtil timer = new TimerUtil();

    public UIDField(int componentId, FontRenderer fontRenderer, int x, int y, int width, int height, String shit) {
        super(componentId, fontRenderer, x, y, width, height);
        this.shit = shit;
    }

    @Override
    public void drawTextBox() {
        if (this.getVisible()) {
            final ScaledResolution scaledresolution = new ScaledResolution(mc);
            int lmx = scaledresolution.getScaledWidth();
            int imy = scaledresolution.getScaledHeight();
            final int mouseX = Mouse.getX() * lmx / mc.displayWidth;
            final int mouseY = imy - Mouse.getY() * imy / mc.displayHeight - 1;
            boolean hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
            RenderUtils.drawBorderedRect(xPosition, yPosition, xPosition + width, yPosition + height, 0.1f,color == 0xffffffff && (hovered || isFocused) ? new Color(0, 0, 0,100).getRGB() : new Color(0, 0, 0,50).getRGB(), new Color(0,0,0,50).getRGB());
            FontManager.posterama16.drawString("User-ID", xPosition + 5, yPosition + 5, textColor);
            int i = this.isEnabled ? this.enabledColor : this.disabledColor;
            int j = this.getCursorPosition() - this.lineScrollOffset;
            int k = this.getSelectionEnd() - this.lineScrollOffset;
            String s = FontManager.posterama20.trimStringToWidth(this.text.substring(this.lineScrollOffset), this.getWidth(),false);
            boolean flag = j >= 0 && j <= s.length();
            boolean flag1 = this.isFocused && this.cursorCounter / 6 % 2 == 0 && flag;
            int l = this.enableBackgroundDrawing ? (int) this.xPosition + 4 : (int) this.xPosition;
            int i1 = this.enableBackgroundDrawing ? (int) this.yPosition + (this.height - 8) / 2 : (int) this.yPosition;
            int j1 = l;

            if (k > s.length()) {
                k = s.length();
            }

            if (!s.isEmpty()) {
                String s1 = flag ? s.substring(0, j) : s;
                j1 = (int) FontManager.posterama20.drawString(s1, xPosition + 5, yPosition + 17, textColor, color != 0xffffffff);
            }

            boolean flag2 = this.getCursorPosition() < this.text.length() || this.text.length() >= this.getMaxStringLength();
            int k1 = j1;

            if (!flag) {
                k1 = j > 0 ? l + this.width : l;
            } else if (flag2) {
                k1 = j1 - 1;
                --j1;
            }

            if (!s.isEmpty() && flag && j < s.length()) {
                FontManager.posterama20.drawString(s.substring(j), (float) j1, (float) i1, i, true);
            }

            if (flag1) {
                if (flag2) {
                    Gui.drawRect(k1, i1 - 1, k1 + 1, i1 + 1 + this.fontRendererInstance.getHeight(), -3092272);
                } else {
                    FontManager.posterama20.drawString("_", xPosition + 5 + (getText().isEmpty() ? 0 : FontManager.posterama20.getStringWidth(getText()) + 1), yPosition + 17, i, true);
                }
            }

            if (k != j) {
                int l1 = l + FontManager.posterama20.getStringWidth(s.substring(0, k));
                this.drawCursorVertical(k1, i1 - 1, l1 - 1, i1 + 1 + this.fontRendererInstance.getHeight());
            }
        }
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    @Override
    public void mouseClicked(int p_146192_1_, int p_146192_2_, int p_146192_3_) {
        boolean flag = p_146192_1_ >= this.xPosition && p_146192_1_ < this.xPosition + this.width && p_146192_2_ >= this.yPosition && p_146192_2_ < this.yPosition + this.height;

        if (canLoseFocus) {
            setFocused(flag);
        }

        if (this.isFocused && flag && p_146192_3_ == 0) {
            int i = p_146192_1_ - (int) this.xPosition;

            if (this.enableBackgroundDrawing) {
                i -= 4;
            }

            final String s = FontManager.posterama20.trimStringToWidth(this.text.substring(this.lineScrollOffset), this.getWidth(), false);
            setCursorPosition(FontManager.posterama20.trimStringToWidth(s, i, false).length() + this.lineScrollOffset);
        }
    }

    @Override
    public void setSelectionPos(int p_146199_1_) {
        final int i = text.length();

        if (p_146199_1_ > i) p_146199_1_ = i;
        if (p_146199_1_ < 0) p_146199_1_ = 0;

        this.selectionEnd = p_146199_1_;

        if (lineScrollOffset > i) {
            this.lineScrollOffset = i;
        }

        final int j = getWidth();
        final String s = FontManager.posterama20.trimStringToWidth(text.substring(lineScrollOffset), j, false);
        final int k = s.length() + lineScrollOffset;

        if (p_146199_1_ == lineScrollOffset) {
            this.lineScrollOffset -= FontManager.posterama20.trimStringToWidth(text, j, true).length();
        }

        if (p_146199_1_ > k) {
            this.lineScrollOffset += p_146199_1_ - k;
        } else if (p_146199_1_ <= this.lineScrollOffset) {
            this.lineScrollOffset -= lineScrollOffset - p_146199_1_;
        }

        this.lineScrollOffset = MathHelper.clamp_int(lineScrollOffset, 0, i);
    }

    public void updateCoordinates(float x, float y) {
        xPosition = (int) x;
        yPosition = (int) y;
    }

}
