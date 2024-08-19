package lol.tgformat.module.impl.combat;

import lol.tgformat.api.event.Listener;
import lol.tgformat.events.CriticalsEvent;
import lol.tgformat.events.WorldEvent;
import lol.tgformat.events.movement.MoveInputEvent;
import lol.tgformat.events.movement.StrafeEvent;
import lol.tgformat.module.Module;
import lol.tgformat.module.ModuleManager;
import lol.tgformat.module.ModuleType;
import lol.tgformat.module.values.impl.BooleanSetting;
import lol.tgformat.utils.client.LogUtil;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.EntityLivingBase;

/**
 * @author TG_format
 * @since 2024/8/18 下午5:20
 */
public class Criticals extends Module {
    public Criticals() {
        super("Criticals", ModuleType.Combat);
    }
    private final BooleanSetting display = new BooleanSetting("Display Criticals", true);
    @Listener
    public void onWorld(WorldEvent event) {
        mc.theWorld.skiptick = 0;
    }
    @Listener
    public void onMoveInput(MoveInputEvent event) {
        if (isGapple()) setState(false);
        if (KillAura.target == null) return;
        if (cantCrit(KillAura.target)) {
            reset();
        } else {
            KillAura aura = ModuleManager.getModule(KillAura.class);
            if (KillAura.target != null) {
                if (!isNull() && mc.thePlayer.motionY < 0 && !mc.thePlayer.onGround && aura.isState() && mc.thePlayer.getClosestDistanceToEntity(KillAura.target) <= 3.0f) {
                    mc.theWorld.skiptick++;
                } else {
                    if (!isNull() && (!aura.isState())) {
                        reset();
                    }
                }
            }
        }
    }
    @Listener
    public void onStrafe(StrafeEvent event) {
        if (mc.thePlayer.onGround && !mc.gameSettings.keyBindJump.pressed) {
            if (KillAura.target != null && mc.thePlayer.getClosestDistanceToEntity(KillAura.target) <= 3.0f) {
                mc.thePlayer.jump();
            }
        }
    }
    @Listener
    public void onCritical(CriticalsEvent event) {
        if (event.getEntity() == KillAura.target && display.isEnabled()) {
            LogUtil.addChatMessage("Critical Success");
        }
    }
    public boolean cantCrit(EntityLivingBase targetEntity) {
        EntityPlayerSP player = mc.thePlayer;
        return player.isOnLadder() || player.isInWeb || player.isInWater() || player.isInLava() || player.ridingEntity != null
                || targetEntity.hurtTime > 10 || targetEntity.getHealth() <= 0 || isGapple();
    }
    private void reset() {
        mc.theWorld.skiptick = 0;
    }
}
