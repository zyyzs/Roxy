package lol.tgformat.module;

import lol.tgformat.accessable.IMinecraft;
import lol.tgformat.api.event.Listener;
import lol.tgformat.events.KeyEvent;
import lol.tgformat.events.TickEvent;
import lol.tgformat.module.impl.combat.*;
import lol.tgformat.module.impl.misc.*;
import lol.tgformat.module.impl.movement.*;
import lol.tgformat.module.impl.player.*;
import lol.tgformat.module.impl.render.*;
import lol.tgformat.module.impl.world.*;
import lol.tgformat.module.values.Setting;
import lol.tgformat.module.values.impl.KeybindSetting;
import lol.tgformat.ui.clickgui.ModernClickGui;
import lol.tgformat.utils.client.LogUtil;
import lol.tgformat.utils.render.ESPColor;
import lol.tgformat.utils.render.Nohurtcam;
import lombok.Getter;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.input.Keyboard;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;
import tech.skidonion.obfuscator.annotations.Renamer;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;

/**
 * @author TG_format
 * @since 2024/5/31 22:33
 */
@Renamer
@NativeObfuscation
@StringEncryption
public class ModuleManager implements IMinecraft {
    @Getter
    public static List<Module> modules = new java.util.ArrayList<>();

    @NativeObfuscation.Inline
    public void initialize() {
        addModule(new KillAura());
        addModule(new Velocity());
        addModule(new ArmorBreaker());
        addModule(new Gapple());
        addModule(new AutoWeapon());
        addModule(new TrowableAura());
        addModule(new AntiBot());
        addModule(new BackTrack());
        addModule(new Criticals());
        //Combat



        addModule(new Sprint());
        addModule(new Speed());
        addModule(new AntiWeb());
        addModule(new InventoryMove());
        addModule(new LongJump());
        addModule(new TestNoSlow());
        addModule(new NoSlow());
        //Movement



        addModule(new Scaffold());
        addModule(new FastPlace());
        addModule(new Eagle());
        addModule(new CivBreak());
        addModule(new ChestAura());
        addModule(new LegitScaffold());
        //World



        addModule(new Disabler());
        addModule(new Teams());
        addModule(new AutoL());
        addModule(new Protocol());
        addModule(new PingSpoof());
        addModule(new ForgeSpoof());
        addModule(new FakePlayer());
        addModule(new PacketDebug());
        addModule(new IRC());
        addModule(new Spammer());
        //Misc


        addModule(new AutoSkyWars());
        addModule(new AutoTool());
        addModule(new Timer());
        addModule(new InvManager());
        addModule(new Stealer());
        addModule(new Blink());
        addModule(new AntiVoid());
        addModule(new SpeedMine());
        addModule(new NoFall());
        addModule(new Stuck());
        addModule(new AutoHub());
        addModule(new Autoplay());
        //Player


        addModule(new NotificationsMod());
        addModule(new ItemPhysics());
        addModule(new PotionsInfo());
        addModule(new Breadcrumbs());
        addModule(new ChestESP());
        addModule(new Animations());
        addModule(new ArrayListMod());
        addModule(new SessionHUD());
        addModule(new Cape());
        addModule(new KillEffect());
        addModule(new MoBends());
        addModule(new FullBright());
        addModule(new NameTags());
        addModule(new HUD());
        addModule(new ESPChams());
        addModule(new Watermark());
        addModule(new Projectile());
        addModule(new ToolTipsAnim());
        addModule(new MotionBlur());
        addModule(new Nohurtcam());
        //Render
    }
    @Listener
    public void onKeyPress(KeyEvent event) {
        for (Module m : modules) {
            if (event.getKey() == Keyboard.KEY_RSHIFT) {
                mc.displayGuiScreen(new ModernClickGui());
            }
            if (m.getKey() == event.getKey()) {
                m.state();
            }
        }
    }
    @Listener
    public void onTick(TickEvent event) {
        if (!getModule(IRC.class).isState()) {
            getModule(IRC.class).state();
        }
    }
    public static <T>T getModule(Class<T> clazz) {
        for (Module m : modules) {
            if (m.getClass() == clazz) {
                return (T) m;
            }
        }
        return (T) new Module("Null", ModuleType.Combat);
    }
    private void addModule(Module module) {
        for (Field field : module.getClass().getDeclaredFields()) {
            try {
                field.setAccessible(true);
                Object obj = field.get(module);
                if (!(obj instanceof Setting)) continue;
                module.add((Setting)obj);
            }
            catch (IllegalAccessException e) {
                LogUtil.print(e.getMessage());
            }
        }
        module.add(new KeybindSetting(module.getKey()));
        modules.add(module);
    }
    public static Module getModuleByName(String name) {
        for (Module m : modules) {
            if (m.getName().equalsIgnoreCase(name)) return m;
        }
        return new Module("Null", ModuleType.Combat);
    }
}
