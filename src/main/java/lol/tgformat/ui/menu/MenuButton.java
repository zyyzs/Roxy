package lol.tgformat.ui.menu;

import lol.tgformat.ui.clickgui.Screen;
import lol.tgformat.ui.clickgui.Utils;
import lol.tgformat.ui.utils.Animation;
import lol.tgformat.ui.utils.DecelerateAnimation;
import lol.tgformat.ui.utils.Direction;
import lol.tgformat.ui.utils.RenderUtil;
import lol.tgformat.utils.render.GlowUtils;
import net.minecraft.util.ResourceLocation;
import net.netease.utils.ColorUtils;
import net.netease.utils.RoundedUtils;
import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;
import tech.skidonion.obfuscator.annotations.Renamer;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import java.awt.*;

/**
 * @Author KuChaZi
 * @Date 2024/6/24 14:11
 * @ClassName: MenuButton
 */
@Renamer
@StringEncryption
public class MenuButton implements Screen {

    public final String text;
    private Animation hoverAnimation;
    public float x, y, width, height;
    public Runnable clickAction;

    public MenuButton(String text) {
        this.text = text;
    }


    @Override
    public void initGui() {
        hoverAnimation = new DecelerateAnimation(200, 1);
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {

    }

    private static final ResourceLocation rs = new ResourceLocation("bloodline/bg/menu-rect.png");

    @Override
    public void drawScreen(int mouseX, int mouseY) {

        boolean hovered = isHovering(x, y, width, height, mouseX, mouseY);
        hoverAnimation.setDirection(hovered ? Direction.FORWARDS : Direction.BACKWARDS);


//        Color rectColor = new Color(35, 37, 43, 102);
//        rectColor = ColorUtils.interpolateColorC(rectColor, ColorUtils.brighter(rectColor, 0.4f), (float)this.hoverAnimation.getOutput().doubleValue());
//        RoundedUtils.drawRoundOutline(this.x, this.y, this.width, this.height, 12.0f, 1.0f, rectColor, new Color(30, 30, 30, 100));
//        Utils.tenacityFont24.drawCenteredString(this.text, this.x + this.width / 2.0f, this.y + Utils.tenacityFont24.getMiddleOfBox(this.height) + 2.0f, -1);


        Color rectColor = new Color(35, 37, 43, 102);
        rectColor = ColorUtils.interpolateColorC(rectColor, ColorUtils.brighter(rectColor, 0.4f), hoverAnimation.getOutput().floatValue());
        RoundedUtils.drawRound(x, y, width, height, 3, rectColor);
//        RenderUtil.color(-1);
        GlowUtils.drawGlow(x, y, width, height, 22, new Color(0,0,0,120));
        // RenderUtil.drawImage(rs, x, y, width, height);
        Utils.tenacityFont24.drawCenteredString(text, x + width / 2f, y + 8, -1);
    }

    public void drawOutline() {
        RenderUtil.drawImage(rs, x, y, width, height);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        boolean hovered = isHovering(x, y, width, height, mouseX, mouseY);
        if (hovered) {
            clickAction.run();
        }

    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {

    }

    
    public static boolean isHovering(float x, float y, float width, float height, int mouseX, int mouseY) {
        return mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
    }


}
