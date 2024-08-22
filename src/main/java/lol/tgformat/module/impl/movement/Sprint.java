package lol.tgformat.module.impl.movement;

import lol.tgformat.api.event.Listener;
import lol.tgformat.events.PreUpdateEvent;
import lol.tgformat.module.Module;
import lol.tgformat.module.ModuleManager;
import lol.tgformat.module.ModuleType;
import tech.skidonion.obfuscator.annotations.StringEncryption;

/**
 * @author TG_format
 * @since 2024/5/31 22:39
 */
@StringEncryption
public class Sprint extends Module {
    public static boolean sprint = true;
    public Sprint() {
        super("Sprint", ModuleType.Movement);
    }
    @Listener
    public void onUpdate(PreUpdateEvent event) {
        mc.gameSettings.keyBindSprint.setPressed(true);
        if (mc.thePlayer.sprintToggleTimer <= 0) {
            if (!mc.thePlayer.isInWeb) {
                if (mc.gameSettings.keyBindForward.isKeyDown()) {
                    if (mc.thePlayer.isUsingItem()) {
                        mc.thePlayer.setSprinting(true);
                    }
                }
            }
        }
    }
}
