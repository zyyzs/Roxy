package lol.tgformat.module.impl.player;

import lol.tgformat.api.event.Listener;
import lol.tgformat.events.PreUpdateEvent;
import lol.tgformat.module.Module;
import lol.tgformat.module.ModuleType;
import tech.skidonion.obfuscator.annotations.Renamer;
import tech.skidonion.obfuscator.annotations.StringEncryption;

@Renamer
@StringEncryption
public class AllowEdit extends Module {
    public AllowEdit() {
        super("AllowEdit", ModuleType.Player);
    }

    @Listener
    public void onUpdate(PreUpdateEvent event) {
        if (mc.thePlayer != null) {
            mc.thePlayer.capabilities.allowEdit = true;
        }
    }
}
