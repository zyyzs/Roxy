package lol.tgformat.module.impl.player;

import lol.tgformat.api.event.Listener;
import lol.tgformat.events.TickEvent;
import lol.tgformat.events.motion.PreMotionEvent;
import lol.tgformat.module.Module;
import lol.tgformat.module.ModuleType;
import lol.tgformat.module.values.impl.ModeSetting;
import lol.tgformat.utils.network.PacketUtil;
import net.minecraft.network.play.client.C03PacketPlayer;

/**
 * @Author KuChaZi
 * @Date 2024/6/28 16:07
 * @ClassName: NoFall
 */
public class NoFall extends Module {
    private final ModeSetting mode = new ModeSetting("Mode", "Spoof","Vulcan","Spoof");
    public NoFall() {
        super("NoFall", ModuleType.Player);
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Listener
    public void onMotion(PreMotionEvent event) {
        this.setSuffix(mode.getMode());
        switch (mode.getMode()) {
            case "Spoof": {
                if (mc.thePlayer.fallDistance > 3) {
                    event.setOnGround(true);
                }
                break;
            }
            case "Vulcan": {
                if (mc.thePlayer.fallDistance >= 3) {
                    mc.thePlayer.motionY = -0.07;
                    event.setOnGround(true);
                    event.setYaw((float) (event.getYaw() + 0.07));
                    mc.thePlayer.fallDistance = 0;
                }
                break;
            }
        }
    }
}
