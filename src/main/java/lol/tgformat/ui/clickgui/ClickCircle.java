package lol.tgformat.ui.clickgui;

import lol.tgformat.ui.utils.Animation;
import lol.tgformat.ui.utils.DecelerateAnimation;
import lol.tgformat.ui.utils.Direction;
import lol.tgformat.ui.utils.RenderUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.netease.utils.ColorUtil;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;
import tech.skidonion.obfuscator.annotations.Renamer;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import java.awt.*;

/**
 * @author TG_format
 * @since 2024/6/9 下午6:48
 */
@Renamer

@StringEncryption
public class ClickCircle extends Component {

    public final Animation fadeAnimation = new DecelerateAnimation(150, 1, Direction.FORWARDS);
    private final Animation scaleAnimation = new DecelerateAnimation(450, 1);

    public ClickCircle() {
        fadeAnimation.setDirection(Direction.FORWARDS);
    }

    @Override
    public void initGui() {

    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {

    }

    @Override
    
    public void drawScreen(int mouseX, int mouseY) {
        if (fadeAnimation.isDone() && fadeAnimation.getDirection().equals(Direction.FORWARDS)) {
            fadeAnimation.setDirection(Direction.BACKWARDS);
            fadeAnimation.setDuration(300);
        }
        GlStateManager.alphaFunc(516, 0.15f);
        int color = ColorUtil.interpolateColor(
                new Color(249, 249, 249, 28), new Color(255, 255, 240), (float) fadeAnimation.getOutput().floatValue());
        GlStateManager.color(1, 1, 1, 1);
        RenderUtil.drawUnfilledCircle(x, y, 1 + (6 * scaleAnimation.getOutput().floatValue()), 4, color);
        GlStateManager.alphaFunc(516, 0.1f);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {

    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int button) {

    }
}
