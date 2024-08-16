package lol.tgformat.ui.panel;

import lol.tgformat.Client;
import lol.tgformat.ui.clickgui.Screen;
import lombok.Getter;
import lombok.Setter;
import net.netease.utils.ColorUtil;

import java.awt.*;

/**
 * @author TG_format
 * @since 2024/6/9 下午8:37
 */
@Getter
@Setter
public abstract class Panel implements Screen {
    private float x, y, width, height, alpha;

    public Color getTextColor() {
        return ColorUtil.applyOpacity(Color.WHITE, alpha);
    }

    public Color getAccentColor() {
        return ColorUtil.applyOpacity(Color.CYAN, alpha);
    }

}
