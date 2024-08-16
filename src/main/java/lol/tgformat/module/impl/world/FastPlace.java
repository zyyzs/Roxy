package lol.tgformat.module.impl.world;

import lol.tgformat.api.event.Listener;
import lol.tgformat.events.TickEvent;
import lol.tgformat.module.Module;
import lol.tgformat.module.ModuleType;
import lol.tgformat.module.values.impl.NumberSetting;
import net.minecraft.item.ItemBlock;

/**
 * @Author KuChaZi
 * @Date 2024/6/10 11:24
 * @ClassName: FastPlace
 */
public class FastPlace extends Module {
    public NumberSetting tickDelay = new NumberSetting("Tick delay", 0.0, 3.0, 0.0, 1.0);
    private boolean noblock;
    public FastPlace() {
        super("FastPlace", ModuleType.World);
    }
    @Listener
    public void onTick(final TickEvent e) {
        if (only()) {
            mc.rightClickDelayTimer = tickDelay.getValue().intValue();
            noblock = false;
        } else if (mc.rightClickDelayTimer < 6 && !noblock){
            mc.rightClickDelayTimer = 6;
            noblock = true;
        }
    }

    private boolean only() {
        return mc.thePlayer.getHeldItem().getItem() instanceof ItemBlock;
    }
}
