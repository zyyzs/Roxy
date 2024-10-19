package lol.tgformat.module.impl.misc;

import lol.tgformat.api.event.Listener;
import lol.tgformat.events.packet.PacketReceiveEvent;
import lol.tgformat.events.packet.PacketSendEvent;
import lol.tgformat.module.Module;
import lol.tgformat.module.ModuleType;
import lol.tgformat.module.values.impl.BooleanSetting;
import lol.tgformat.utils.client.LogUtil;
import net.minecraft.item.ItemSword;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C0EPacketClickWindow;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.server.S19PacketEntityStatus;
import tech.skidonion.obfuscator.annotations.Renamer;
import tech.skidonion.obfuscator.annotations.StringEncryption;

/**
 * @Author KuChaZi
 * @Date 2024/7/21 22:31
 * @ClassName: PacketDebug
 */
@Renamer
@StringEncryption
public class PacketDebug extends Module {
    private final BooleanSetting r = new BooleanSetting("Receive", false);
    private final BooleanSetting s = new BooleanSetting("Send", false);
    private final BooleanSetting c07 = new BooleanSetting("C07", false);
    private final BooleanSetting c0e = new BooleanSetting("All C0E", false);
    private final BooleanSetting c0f = new BooleanSetting("All C0F", false);
    private final BooleanSetting c08 = new BooleanSetting("C08 only sword", false);
    private final BooleanSetting s19 = new BooleanSetting("S19", false);

    public PacketDebug() {
        super("PacketDebug", ModuleType.Misc);
    }

    @Listener
    public void onPacketSend(PacketSendEvent event) {
        Packet<?> packet = event.getPacket();

        if (s.isEnabled()) {
            LogUtil.addChatMessage(packet.getClass().getSimpleName());
            return;
        }

        if (packet instanceof C07PacketPlayerDigging && ((C07PacketPlayerDigging)packet).getStatus() == C07PacketPlayerDigging.Action.RELEASE_USE_ITEM && c07.isEnabled()) {
            LogUtil.addChatMessage("C07");
        }
        if (packet instanceof C08PacketPlayerBlockPlacement && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword && c08.isEnabled()) {
            LogUtil.addChatMessage("C08");
        }
        if (packet instanceof C0EPacketClickWindow && c0e.isEnabled()) {
            LogUtil.addChatMessage("C0E");
        }
        if (packet instanceof C0FPacketConfirmTransaction && c0f.isEnabled()) {
            LogUtil.addChatMessage("C0F");
        }
    }

    @Listener
    public void onPacketR(PacketReceiveEvent event) {
        Packet<?> packet = event.getPacket();

        if (r.isEnabled()) {
            LogUtil.addChatMessage(packet.getClass().getSimpleName());
            return;
        }

        if (packet instanceof S19PacketEntityStatus && s19.isEnabled()) {
            LogUtil.addChatMessage("S19 - " + ((S19PacketEntityStatus) packet).getOpCode());
        }
    }
}
