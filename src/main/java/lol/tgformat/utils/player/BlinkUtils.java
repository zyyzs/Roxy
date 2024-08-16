package lol.tgformat.utils.player;

import lol.tgformat.accessable.IMinecraft;
import lol.tgformat.events.packet.PacketSendEvent;
import lol.tgformat.utils.network.PacketUtil;
import lombok.Getter;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

@Getter
public class BlinkUtils implements IMinecraft {
    private final CopyOnWriteArrayList<Packet<?>> client = new CopyOnWriteArrayList<>();

    public void startBlink(PacketSendEvent event, Packet<?> packetIn) {
        Packet<?> packet = event.getPacket();
        if (packet == packetIn) {
            event.setCancelled();
            client.add(packet);
        }
    }

    public void release() {
        if (!client.isEmpty()) {
            ArrayList<Packet<?>> remove = new ArrayList<>();
            for (Packet<?> packet : client) {
                PacketUtil.sendPacketNoEvent(packet);
                remove.add(packet);
            }
            for (Packet<?> packet : remove) {
                client.remove(packet);
            }
            remove.clear();
        }
    }

    public static class Client {
        public static final CopyOnWriteArrayList<Packet<?>> client = new CopyOnWriteArrayList<>();

        public static boolean isBlinkPacket(Packet<?> packet) {
            return packet instanceof C03PacketPlayer ||
                    packet instanceof C0BPacketEntityAction ||
                    packet instanceof C02PacketUseEntity ||
                    packet instanceof C0APacketAnimation ||
                    packet instanceof C08PacketPlayerBlockPlacement ||
                    packet instanceof C09PacketHeldItemChange ||
                    packet instanceof C0FPacketConfirmTransaction;
        }

        public static void startBlink(PacketSendEvent event) {
            Packet<?> packet = event.getPacket();
            if (!event.isCancelled()) {
                if (Client.isBlinkPacket(packet)) {
                    event.setCancelled();
                    client.add(packet);
                }
            }
        }

        public static void release() {
            if (!client.isEmpty()) {
                ArrayList<Packet<?>> toRemove = new ArrayList<>();
                for (Packet<?> packet : client) {
                    if (Client.isBlinkPacket(packet)) {
                        PacketUtil.sendPacketNoEvent(packet);
                        toRemove.add(packet);
                    }
                }
                if (!toRemove.isEmpty()) {
                    for (Packet<?> p : toRemove) {
                        client.remove(p);
                    }
                }
                toRemove.clear();
            }
        }
    }
}
