package lol.tgformat.module.values.impl;

import lol.tgformat.module.values.Setting;
import lombok.Setter;
import org.lwjgl.input.Keyboard;

@Setter
public class KeybindSetting extends Setting {
    private int code;

    public KeybindSetting(int code) {
        this.name = "Keybind";
        this.code = code;
    }

    public int getCode() {
        return code == -1 ? Keyboard.KEY_NONE : code;
    }

    @Override
    public Integer getConfigValue() {
        return this.getCode();
    }
}
