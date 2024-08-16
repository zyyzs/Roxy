package lol.tgformat.ui.clickgui;

import lol.tgformat.Client;
import lol.tgformat.ui.utils.RoundedUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.netease.utils.ColorUtil;
import net.netease.utils.RenderUtil;

import java.awt.*;
import java.util.function.BiConsumer;

/**
 * @author TG_format
 * @since 2024/6/9 下午8:21
 */
@Getter
@Setter
@RequiredArgsConstructor
public abstract class Form implements Screen {
    private float x, y, width, height, alpha;
    private final String title;
    private BiConsumer<String, String> uploadAction;
    private Triplet.TriConsumer<String, String, String> triUploadAction;


    @Override
    public void drawScreen(int mouseX, int mouseY) {
        RoundedUtil.drawRound(x, y, width, height, 5, ColorUtil.tripleColor(37, alpha));
        tenacityBoldFont40.drawString(title, x + 5, y + 3, getTextColor());
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if (!RenderUtil.isHovering(x, y, width, height, mouseX, mouseY)) {
            Client.instance.getSideGui().displayForm(null);
        }

    }

    public float getSpacing() {
        return 8;
    }

    public Color getTextColor() {
        return ColorUtil.applyOpacity(Color.WHITE, alpha);
    }

    public Color getAccentColor() {
        return ColorUtil.applyOpacity(Color.CYAN, alpha);
    }

    public abstract void clear();

}
