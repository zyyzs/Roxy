package lol.tgformat.module.impl.render;

import lol.tgformat.api.event.Listener;
import lol.tgformat.events.TickEvent;
import lol.tgformat.module.Module;
import lol.tgformat.module.ModuleType;
import tech.skidonion.obfuscator.annotations.Renamer;
import tech.skidonion.obfuscator.annotations.StringEncryption;

/**
 * @Author KuChaZi
 * @Date 2024/7/7 16:59
 * @ClassName: FullBright
 */
@Renamer
@StringEncryption
public class FullBright extends Module {
    private float old;

    public FullBright() {
        super("FullBright", ModuleType.Render);
    }

    @Override
    public void onEnable() {
        this.old = this.mc.gameSettings.gammaSetting;
    }

    @Listener
    private void onTick(TickEvent event) {
        this.mc.gameSettings.gammaSetting = 1.5999999E7f;
    }

    @Override
    public void onDisable() {
        this.mc.gameSettings.gammaSetting = this.old;
    }
}
