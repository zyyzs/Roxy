package lol.tgformat.ui.clickgui;

/**
 * @author TG_format
 * @since 2024/6/9 下午7:08
 */
public abstract class Panel implements Utils {

    public float x, y, bigRecty;

    abstract public void initGui();

    public abstract void keyTyped(char typedChar, int keyCode);

    abstract public void drawScreen(int mouseX, int mouseY);

    abstract public void mouseClicked(int mouseX, int mouseY, int button);

    abstract public void mouseReleased(int mouseX, int mouseY, int state);

}
