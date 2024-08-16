package lol.tgformat.module.impl.movement;

import lol.tgformat.api.event.Listener;
import lol.tgformat.events.PreUpdateEvent;
import lol.tgformat.module.Module;
import lol.tgformat.module.ModuleType;
import lol.tgformat.module.values.impl.ModeSetting;
import lol.tgformat.module.values.impl.NumberSetting;
import lol.tgformat.utils.move.MoveUtil;
import tech.skidonion.obfuscator.annotations.StringEncryption;

/**
 * @Author KuChaZi
 * @Date 2024/7/13 15:05
 * @ClassName: LongJump
 */
@StringEncryption
public class LongJump extends Module {
    private final ModeSetting mode = new ModeSetting("Mode", "Packet", "Packet");
    private final NumberSetting speed = new NumberSetting("Speed", 0.45, 10.0, 0.1, 0.1);
    public LongJump() {
        super("LongJump", ModuleType.Movement);
    }

    @Listener
    public void onUpdate(PreUpdateEvent event) {
        this.setSuffix(mode.getMode());
        if (mode.is("Packet")) {
            if (mc.thePlayer.onGround && MoveUtil.isMoving()) {
                mc.thePlayer.jump();
                MoveUtil.moveFlying(speed.getValue());
            } if (mc.thePlayer.onGround) {
                setState(false);
            }
        }
    }
}
