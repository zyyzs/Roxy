package lol.tgformat.ui.clickgui;

/**
 * @author TG_format
 * @since 2024/6/9 下午7:10
 */
public interface Screen extends Utils {

    default void onDrag(int mouseX, int mouseY) {

    }

    void initGui();

    void keyTyped(char typedChar, int keyCode);

    void drawScreen(int mouseX, int mouseY);

    void mouseClicked(int mouseX, int mouseY, int button);

    void mouseReleased(int mouseX, int mouseY, int state);

}
