package lol.tgformat.module.impl.combat;

import lol.tgformat.api.event.Listener;
import lol.tgformat.events.AttackEvent;
import lol.tgformat.events.WorldEvent;
import lol.tgformat.module.Module;
import lol.tgformat.module.ModuleManager;
import lol.tgformat.module.ModuleType;
import lol.tgformat.utils.network.PacketUtil;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.play.client.C03PacketPlayer;

/**
 * @author TG_format
 * @since 2024/8/18 下午5:20
 */
public class Criticals extends Module {
    public Criticals() {
        super("Critcals", ModuleType.Combat);
    }
    @Listener
    public void onWorld(WorldEvent event) {
        mc.theWorld.skiptick = 0;
    }
    @Listener
    public void onAttack(AttackEvent event) {
        if (cantCrit((EntityLivingBase) event.getTarget())) {
            reset();
        } else {
            KillAura aura = ModuleManager.getModule(KillAura.class);
            if (KillAura.target != null) {
                if (mc.thePlayer.onGround) {
                    aura.attackTimer.reset();
                }
                if (!isNull() && mc.thePlayer.motionY < 0 && !mc.thePlayer.onGround && aura.isState()) {
                    mc.theWorld.skiptick++;
                    PacketUtil.sendPacket(new C03PacketPlayer(false));
                } else {
                    if (!isNull() && (!aura.isState())) {
                        reset();
                    }
                }
            }
        }
    }
    public boolean cantCrit(EntityLivingBase targetEntity) {
        EntityPlayerSP player = mc.thePlayer;
        return player.isOnLadder() || player.isInWeb || player.isInWater() || player.isInLava() || player.ridingEntity != null
                || targetEntity.hurtTime > 10 || targetEntity.getHealth() <= 0;
    }
    private void reset() {
        mc.theWorld.skiptick = 0;
    }
}
