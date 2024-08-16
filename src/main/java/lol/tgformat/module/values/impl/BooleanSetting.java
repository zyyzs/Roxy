package lol.tgformat.module.values.impl;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lol.tgformat.module.values.Setting;
import lombok.Setter;

@Setter
public class BooleanSetting extends Setting {

    @Expose
    @SerializedName("name")
    private boolean state;

    public BooleanSetting(String name, boolean state) {
        this.name = name;
        this.state = state;
    }

    public boolean isEnabled() {
        return state;
    }

    public void toggle() {
        setState(!isEnabled());
    }

    @Override
    public Boolean getConfigValue() {
        return isEnabled();
    }

}
