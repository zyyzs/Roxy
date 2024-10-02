package lol.tgformat.module.impl.combat;

import lol.tgformat.api.event.Listener;
import lol.tgformat.events.AttackEvent;
import lol.tgformat.module.Module;
import lol.tgformat.module.ModuleType;
import lol.tgformat.utils.network.PacketUtil;
import net.minecraft.network.play.client.C0BPacketEntityAction;

/**
 * @author TG_format
 * @since 2024/10/1 21:16
 */
public class SuperKB extends Module {
    public SuperKB() {
        super("SuperKB", ModuleType.Combat);
    }
    @Listener
    public void onAttack(AttackEvent event) {
        PacketUtil.sendPacket(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING));
        PacketUtil.sendPacket(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING));
        PacketUtil.sendPacket(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING));
        PacketUtil.sendPacket(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING));
    }
}
