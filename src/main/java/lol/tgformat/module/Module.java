package lol.tgformat.module;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.mojang.realmsclient.gui.ChatFormatting;
import lol.tgformat.accessable.IMinecraft;
import lol.tgformat.api.event.EventManager;
import lol.tgformat.config.ConfigSetting;
import lol.tgformat.module.impl.combat.Gapple;
import lol.tgformat.module.impl.render.NotificationsMod;
import lol.tgformat.module.values.Setting;
import lol.tgformat.module.values.impl.KeybindSetting;
import lol.tgformat.ui.clickgui.Utils;
import lol.tgformat.ui.notifications.Notification;
import lol.tgformat.ui.notifications.NotificationManager;
import lol.tgformat.ui.notifications.NotificationType;
import lol.tgformat.ui.utils.Animation;
import lol.tgformat.ui.utils.DecelerateAnimation;
import lol.tgformat.ui.utils.Direction;
import lol.tgformat.utils.client.LogUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemSword;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author TG_format
 * @since 2024/5/31 20:14
 */
@Getter
@AllArgsConstructor
public class Module implements IMinecraft {
    @Expose
    @SerializedName("name")
    private String name;
    private ModuleType category;
    private String description = "";
    private int key;
    @Expose
    @SerializedName("state")
    private boolean state;
    private final CopyOnWriteArrayList<Setting> settingsList = new CopyOnWriteArrayList<>();
    private final Animation animation = new DecelerateAnimation(250, 1).setDirection(Direction.BACKWARDS);
    @Expose
    @SerializedName("settings")
    public ConfigSetting[] cfgSettings;
    private String suffix;

    public Module(String name, ModuleType category) {
        this.name = name;
        this.category = category;
        this.suffix = "";
    }
    public Module(String name, ModuleType category, int key) {
        this.name = name;
        this.category = category;
        this.key = key;
    }
    public Module(String name, ModuleType category, String description) {
        this.name = name;
        this.category = category;
        this.description = description;
    }
    public void state() {
        state = !state;
        if (mc.theWorld != null) {
            mc.theWorld.playSound(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, "random.click", 0.5f, state ? 0.6f : 0.5f, false);
        }
        if (state) {
            onEnable();
            EventManager.register(this);
            if (NotificationsMod.toggleNotifications.isEnabled()){
                NotificationManager.post(NotificationType.SUCCESS, this.name, "Enabled");
            }
        } else {
            EventManager.unregister(this);
            onDisable();
            if (NotificationsMod.toggleNotifications.isEnabled()) {
                NotificationManager.post(NotificationType.DISABLE, this.name, "Disabled");
            }
        }
    }
    public void setSuffix(Object suffix) {
        this.suffix = String.valueOf(suffix);
    }

    public final String getDisplayName() {
        return getDisplayName(ChatFormatting.GRAY);
    }

    public final String getDisplayName(ChatFormatting formatting) {
        String tag = getSuffix();

        if(tag == null || tag.isEmpty()) {
            return name;
        }

        return name + formatting + " " + tag;
    }
    public void setKey(int key) {
        this.key = key;
        for (Setting s : settingsList) {
            if (s instanceof KeybindSetting keybindSetting) {
                keybindSetting.setCode(key);
            }
        }
    }
    public void add(Setting... settings) {
        settingsList.addAll(Arrays.asList(settings));
    }

    public boolean isSword() {
        return mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword;
    }

    public boolean isFood() {
        return mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemFood;
    }
    public boolean isBow() {
        return mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemBow;
    }
    public boolean isBlock() {
        return mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemBlock;
    }
    public void setState(boolean state) {
        if (this.state == state) return;
        this.state = state;
        if (state) {
            onEnable();
            EventManager.register(this);
            if (NotificationsMod.toggleNotifications.isEnabled()){
                NotificationManager.post(NotificationType.SUCCESS, this.name, "Enabled");
            }
        } else {
            EventManager.unregister(this);
            onDisable();
            if (NotificationsMod.toggleNotifications.isEnabled()) {
                NotificationManager.post(NotificationType.DISABLE, this.name, "Disabled");
            }
        }
    }
    public boolean isGapple() {
        return ModuleManager.getModule(Gapple.class).isState();
    }
    public boolean isNull(){
        return mc.thePlayer == null || mc.theWorld == null;
    }
    public void onEnable() {

    }
    public void onDisable() {

    }

    public boolean hasMode() {
        return suffix != null;
    }
}
