package lol.tgformat.module.impl.render;

import lol.tgformat.api.event.Listener;
import lol.tgformat.events.motion.PreMotionEvent;
import lol.tgformat.module.Module;
import lol.tgformat.module.ModuleType;
import lol.tgformat.module.values.impl.BooleanSetting;
import lol.tgformat.module.values.impl.ModeSetting;
import lol.tgformat.module.values.impl.NumberSetting;
import tech.skidonion.obfuscator.annotations.Renamer;
import tech.skidonion.obfuscator.annotations.StringEncryption;

/**
 * @Author KuChaZi
 * @Date 2024/7/2 12:05
 * @ClassName: Animations
 */
@Renamer
@StringEncryption
public class Animations extends Module {
    public final ModeSetting mode = new ModeSetting("mode", "1.7", "Sigma", "1.7", "Jello", "External", "Styles", "Leaked", "Exhibition", "Exhibition2");
    public final NumberSetting speed = new NumberSetting("SwingSpeed", 1.0, 1.5, 0.1, 0.1);
    public final NumberSetting x = new NumberSetting("X", 0.0, 1.0, -1.0, 0.05);
    public final NumberSetting y = new NumberSetting("Y", 0.0, 1.0, -1.0, 0.05);
    public final NumberSetting z = new NumberSetting("Z", 0.0, 1.0, -1.0, 0.05);
    public final NumberSetting blockingX = new NumberSetting("Blocking-X", 0.0, 1.0, -1.0, 0.05);
    public final NumberSetting blockingY = new NumberSetting("Blocking-Y", 0.0, 1.0, -1.0, 0.05);
    public final NumberSetting blockingZ = new NumberSetting("Blocking-Z", 0.0, 1.0, -1.0, 0.05);
    public final BooleanSetting smooth = new BooleanSetting("Smooth", false);
    public final BooleanSetting fake = new BooleanSetting("FakeBlock", false);

    public Animations() {
        super("Animations", ModuleType.Render);
    }

    @Listener
    private void onUpdate(PreMotionEvent event) {
        this.setSuffix(mode.getMode());
    }

}
