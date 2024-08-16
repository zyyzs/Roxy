package lol.tgformat.utils.keyboard;

import lol.tgformat.accessable.IMinecraft;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;

/**
 * @author TG_format
 * @since 2024/6/1 12:57
 */
public class KeyBoardUtil implements IMinecraft {

    public static boolean isPressed(KeyBinding key) {
        return Keyboard.isKeyDown(key.getKeyCode());
    }

    public static void resetKeybinding(KeyBinding key) {
        if(mc.currentScreen != null) {
            key.pressed = false;
        } else {
            key.pressed = isPressed(key);
        }
    }

    public static void resetKeybindings(KeyBinding... keys) {
        for(KeyBinding key : keys) {
            resetKeybinding(key);
        }
    }

}
