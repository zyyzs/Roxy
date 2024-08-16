package lol.tgformat.ui.clickgui;

/**
 * @author TG_format
 * @since 2024/6/9 下午6:49
 */
public abstract class Component implements Utils {

    public float x;
    public float y;
    public float bigRecty;
    public boolean hovering;
    public float rectWidth;

    public abstract void initGui();

    public abstract void keyTyped(char typedChar, int keyCode);

    public abstract void drawScreen(int mouseX, int mouseY);

    public abstract void mouseClicked(int mouseX, int mouseY, int button);

    public abstract void mouseReleased(int mouseX, int mouseY, int button);
}