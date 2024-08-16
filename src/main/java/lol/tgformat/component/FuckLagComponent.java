package lol.tgformat.component;

import lol.tgformat.accessable.IMinecraft;
import lol.tgformat.api.event.Listener;
import lol.tgformat.events.packet.PacketReceiveEvent;
import lol.tgformat.utils.network.PacketUtil;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;

/**
 * @author TG_format
 * @since 2024/8/14 下午7:44
 */
public class FuckLagComponent implements IMinecraft {
    @Listener
    public void onPacket(PacketReceiveEvent event) {
        if (event.getPacket() instanceof S08PacketPlayerPosLook && mc.thePlayer.ticksExisted > 50) {
            PacketUtil.sendPacket(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem % 8 + 1));
            PacketUtil.sendC0F();
            PacketUtil.sendPacket(new C09PacketHeldItemChange((mc.thePlayer.inventory.currentItem)));
        }
    }
}
