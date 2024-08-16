package lol.tgformat.ui.clickgui;

import lombok.Getter;
import lombok.Setter;

/**
 * @author TG_format
 * @since 2024/6/9 下午6:48
 */
@Getter
@Setter
public class Drag {

    private float x, y, initialX, initialY;
    private float startX, startY;
    private boolean dragging;

    public Drag(float initialXVal, float initialYVal) {
        this.initialX = initialXVal;
        this.initialY = initialYVal;
        this.x = initialXVal;
        this.y = initialYVal;
    }

    public final void onDraw(int mouseX, int mouseY) {
        if (dragging) {
            x = (mouseX - startX);
            y = (mouseY - startY);
        }
    }

    public final void onClick(int mouseX, int mouseY, int button, boolean canDrag) {
        if (button == 0 && canDrag) {
            dragging = true;
            startX = (int) (mouseX - x);
            startY = (int) (mouseY - y);
        }
    }

    public final void onRelease(int button) {
        if (button == 0) dragging = false;
    }

}
