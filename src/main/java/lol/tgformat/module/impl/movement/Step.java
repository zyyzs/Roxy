package lol.tgformat.module.impl.movement;

import lol.tgformat.api.event.Listener;
import lol.tgformat.events.motion.PreMotionEvent;
import lol.tgformat.events.movement.MoveEvent;
import lol.tgformat.module.Module;
import lol.tgformat.module.ModuleType;
import lol.tgformat.module.values.impl.NumberSetting;
import lol.tgformat.utils.block.BlockUtil;
import lol.tgformat.utils.move.MoveUtil;
import net.minecraft.potion.Potion;

public class Step extends Module {
    private final NumberSetting boost = new NumberSetting("Boost", 0, 0.4, 0, 0.1);
    private final NumberSetting delay = new NumberSetting("Delay", 0, 5000, 0, 250);

    private int offGroundTicks = -1;
    private boolean stepping = false;
    private long lastStep = 0;

    public Step() {
        super("Step", ModuleType.Movement);
    }

    @Listener
    public void onPreMotion(PreMotionEvent event){
        final long time = System.currentTimeMillis();
        if(mc.thePlayer.onGround && mc.thePlayer.isCollidedHorizontally && MoveUtil.isMoving() && time-lastStep >= delay.getValue()) {
            stepping = true;
            lastStep = time;
        }
    }

    @Listener
    public void onMove(MoveEvent event){
        if (mc.thePlayer.onGround) {
            offGroundTicks = 0;
        } else if (offGroundTicks != -1) {
            offGroundTicks++;
        }

        if (stepping) {
            if (!MoveUtil.isMoving() || mc.gameSettings.keyBindJump.isKeyDown() || (!mc.thePlayer.isCollidedHorizontally && offGroundTicks > 5)) {
                stepping = false;
                return;
            }

            if (mc.thePlayer.isPotionActive(Potion.jump)) return;
            final boolean airUnder = !BlockUtil.insideBlock(
                    mc.thePlayer.getEntityBoundingBox()
                            .offset(0, -1, 0)
                            .expand(0.239, 0, 0.239)
            );;
            final float speed = Math.abs(mc.thePlayer.motionX) > 0.1 && Math.abs(mc.thePlayer.motionZ) > 0.1 ? 0.22F : 0.29888888F;

            switch (offGroundTicks) {
                case 0:
                    event.setY(mc.thePlayer.motionY = 0.4198479950428009);
                    MoveUtil.strafe(speed -  8.0E-4 + Math.random() * 0.008);
                    break;
                case 1:
                    event.setY(Math.floor(mc.thePlayer.posY + 1.0) - mc.thePlayer.posY);
                    break;
                case 5:
                    if (mc.thePlayer.isCollidedHorizontally || !BlockUtil.blockRelativeToPlayer(0, -1, 0).isFullCube())
                        return;
                    MoveUtil.moveFlying(boost.getValue());
                    mc.thePlayer.motionY = MoveUtil.predictedMotion(mc.thePlayer.motionY, 2);
                    break;
            }
        }
    }
}
