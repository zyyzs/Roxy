package lol.tgformat.component;

import lol.tgformat.accessable.IMinecraft;
import lol.tgformat.api.event.Listener;
import lol.tgformat.api.event.types.Priority;
import lol.tgformat.events.PostUpdateEvent;
import lol.tgformat.events.motion.PostMotionEvent;
import lol.tgformat.events.packet.PacketReceiveEvent;
import lol.tgformat.events.packet.PacketSendEvent;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.server.S09PacketHeldItemChange;
import net.minecraft.network.play.server.S0CPacketSpawnPlayer;

/**
 * @Author KuChaZi
 * @Date 2024/6/29 8:36
 * @ClassName: BadPacketsComponent
 */
public class BadPacketsComponent implements IMinecraft {
    public boolean C08;
    public boolean C07;
    private boolean C02;
    public boolean C09;
    public boolean delayAttack;
    public boolean delay;
    public int playerSlot = -1;
    public int serverSlot = -1;

    @Listener(Priority.HIGHEST)
    public void onSendPacket(PacketSendEvent e) {
        if (e.isCancelled()) {
            return;
        }
        if (e.getPacket() instanceof C02PacketUseEntity) { // sending a C07 on the same tick as C02 can ban, this usually happens when you unblock and attack on the same tick
            if (C07) {
                e.setCancelled(true);
                return;
            }
            C02 = true;
        }
        else if (e.getPacket() instanceof C08PacketPlayerBlockPlacement) {
            C08 = true;
        }
        else if (e.getPacket() instanceof C07PacketPlayerDigging) {
            C07 = true;
        }
        else if (e.getPacket() instanceof C09PacketHeldItemChange) {
            if (((C09PacketHeldItemChange) e.getPacket()).getSlotId() == playerSlot && ((C09PacketHeldItemChange) e.getPacket()).getSlotId() == serverSlot) {
                e.setCancelled(true);
                return;
            }
            C09 = true;
            serverSlot = playerSlot = ((C09PacketHeldItemChange) e.getPacket()).getSlotId();
        }
    }

    @Listener
    public void onReceivePacket(PacketReceiveEvent e) {
        if (e.getPacket() instanceof S09PacketHeldItemChange packet) {
            if (packet.getHeldItemHotbarIndex() >= 0 && packet.getHeldItemHotbarIndex() < InventoryPlayer.getHotbarSize()) {
                serverSlot = packet.getHeldItemHotbarIndex();
            }
        }
        else if (e.getPacket() instanceof S0CPacketSpawnPlayer && mc.thePlayer != null) {
            if (((S0CPacketSpawnPlayer) e.getPacket()).getEntityID() != mc.thePlayer.getEntityId()) {
                return;
            }
            this.playerSlot = -1;
        }
    }

    @Listener(Priority.HIGHEST)
    public void onPostUpdate(PostUpdateEvent e) {
        if (delay) {
            delayAttack = false;
            delay = false;
        }
        if (C08 || C09) {
            delay = true;
            delayAttack = true;
        }
        C08 = C07 = C02 = C09 = false;
    }
}
