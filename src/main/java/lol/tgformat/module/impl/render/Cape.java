package lol.tgformat.module.impl.render;

import lol.tgformat.api.event.Listener;
import lol.tgformat.events.PreUpdateEvent;
import lol.tgformat.module.Module;
import lol.tgformat.module.ModuleType;
import lol.tgformat.module.values.impl.ModeSetting;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;
import tech.skidonion.obfuscator.annotations.Renamer;
import tech.skidonion.obfuscator.annotations.StringEncryption;

/**
 * @Author KuChaZi
 * @Date 2024/6/9 10:07
 * @ClassName: Cape
 */
@Renamer

@StringEncryption
public class Cape extends Module {
    private final ModeSetting capemods = new ModeSetting("Style","Yuzaki", "JiaRan", "Chimera", "Hanabi", "Astolfo", "Yuzaki", "KuChaZi", "Furina", "Mika", "Nekocat", "Nekocat2", "Paimon");

    public Cape() {
        super("Cape", ModuleType.Render);
    }

    @Override
    public void onDisable() {
        mc.thePlayer.setLocationOfCape(null);
        super.onDisable();
    }

    @Listener
    
    public void onUpdate(PreUpdateEvent event) {
        if (isNull()) return;
        mc.thePlayer.setLocationOfCape(new ResourceLocation("bloodline/cape/" + capemods.getMode().toLowerCase() + ".png"));
    }

}
