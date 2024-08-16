package lol.tgformat.component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;
import java.util.function.Predicate;

import lol.tgformat.accessable.IMinecraft;
import lol.tgformat.api.event.EventManager;
import lol.tgformat.api.event.Listener;
import lol.tgformat.events.TickEvent;
import lol.tgformat.events.packet.PacketSendEvent;
import lol.tgformat.utils.client.LogUtil;
import lol.tgformat.utils.network.PacketUtil;
import lombok.Setter;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S00PacketKeepAlive;

public class PacketStoringComponent implements IMinecraft {
    private static final Map<Class<?>, Consumer<Packet<?>>> cancelAction = new HashMap<>();
    private static final Map<Class<?>, Consumer<Packet<?>>> releaseAction = new HashMap<>();
    public static boolean storing = false;
    private static final List<Class<?>> blackList = new ArrayList<>();
    @Setter
    private static Map<Class<?>, Predicate<Packet<?>>> addReturnMap = new HashMap<>();
    private static final Map<Class<?>, Predicate<Packet<?>>> cancelPacketMap = new HashMap<>();
    private static final Map<Class<?>, Predicate<Packet<?>>> releaseMap = new HashMap<>();
    private static final List<Class<?>> whitList = new ArrayList<>();
    public static LinkedBlockingQueue<Packet<?>> packets = new LinkedBlockingQueue<>();
    public static boolean noEvt = false;
    public static void send(Packet<?> packet, boolean event) {
        if (event) {
            PacketSendEvent packetSendEvent = new PacketSendEvent(packet);
            EventManager.call(packetSendEvent);
            if (packetSendEvent.isCancelled()) {
                return;
            }
        }
        noEvt = true;
        PacketUtil.sendPacketNoEvent(packet);
        noEvt = false;
    }
    public static void startStoringPackets(Class<?>... fliterPackets) {
        if (storing) {
            return;
        }
        Arrays.asList(fliterPackets).forEach(e -> {
            blackList.add(e);
            cancelPacketMap.put(e, f -> true);
        });
        storing = true;

    }
    public static void setCancelAction(Class<?> clazz, Consumer<Packet<?>> packetConsumer) {
        boolean isIN = false;

        for(Class<?> classes : cancelAction.keySet()) {
            if (classes == clazz) {
                isIN = true;
                break;
            }
        }

        if (isIN) {
            cancelAction.replace(clazz, packetConsumer);
        } else {
            cancelAction.put(clazz, packetConsumer);
        }
    }
    public static void setCanelPackets(Class<?> clazz, Predicate<Packet<?>> predicate) {
        boolean isIN = false;

        for(Class<?> classes : cancelPacketMap.keySet()) {
            if (classes == clazz) {
                isIN = true;
                break;
            }
        }

        if (isIN) {
            cancelPacketMap.replace(clazz, predicate);
        } else {
            cancelPacketMap.put(clazz, predicate);
        }
    }
    public static void resetBlackList() {
        blackList.clear();
    }

    public static void setReleaseAction(Class<?> clazz, Consumer<Packet<?>> packetConsumer) {
        boolean isIN = false;

        for(Class<?> classes : releaseAction.keySet()) {
            if (classes == clazz) {
                isIN = true;
                break;
            }
        }

        if (isIN) {
            releaseAction.replace(clazz, packetConsumer);
        } else {
            releaseAction.put(clazz, packetConsumer);
        }
    }


    public static void setReleaseMap(Class<?> clazz, Predicate<Packet<?>> predicate) {
        boolean isIN = false;

        for(Class<?> classes : releaseMap.keySet()) {
            if (classes == clazz) {
                isIN = true;
                break;
            }
        }

        if (isIN) {
            releaseMap.replace(clazz, predicate);
        } else {
            releaseMap.put(clazz, predicate);
        }
    }

    public static void releasePacket(int sendPackets, boolean noEvent) {
        releasePacket(sendPackets, noEvent, false);
    }
    public static void releasePacket(boolean sendOneTick) {
        releasePacket(packets.size(), true, sendOneTick);
    }
    public static void releasePacket() {
        releasePacket(packets.size(), true);
    }
    public static void releasePacket(int sendPackets, boolean noEvent, boolean sendOneTick) {
        int sends = 0;

        try {
            label41:
            while(!packets.isEmpty()) {
                Packet<?> packet = packets.take();
                if (packet instanceof S00PacketKeepAlive) {
                    if (sendOneTick) {
                        break;
                    }
                } else {
                    for(Entry<Class<?>, Predicate<Packet<?>>> entries : releaseMap.entrySet()) {
                        if ((entries.getKey()).isAssignableFrom(packet.getClass()) && (entries.getValue()).test(packet)) {
                            continue label41;
                        }
                    }

                    releaseAction.forEach((key, value) -> {
                        if (key.isAssignableFrom(packet.getClass())) {
                            value.accept(packet);
                        }
                    });
                    ++sends;
                    if (noEvent) {
                        noEvt = true;
                        PacketUtil.sendPacketNoEvent(packet);
                        noEvt = false;
                    } else {
                        noEvt = true;
                        mc.getNetHandler().addToSendQueue(packet);
                        noEvt = false;
                    }

                    if (sends >= sendPackets) {
                        break;
                    }
                }
            }
        } catch (Exception e) {
            LogUtil.print(e.getMessage());
        }
    }

    public static void stopStoringPackets() {
        storing = false;
        noEvt = false;
        releasePacket();
        blackList.clear();
        cancelPacketMap.clear();
        cancelAction.clear();
        releaseAction.clear();
        releaseMap.clear();
        whitList.clear();
        packets.clear();
    }

    public static boolean onStorePacket(Packet<?> packet) {
        if (storing && !noEvt) {
            cancelAction.forEach((aClass, packetConsumer) -> {
                if (aClass.isAssignableFrom(packet.getClass())) {
                    packetConsumer.accept(packet);
                }
            });

            for(Class<?> clazz : blackList) {
                if (clazz.isAssignableFrom(packet.getClass())) {
                    return true;
                }
            }

            for(Entry<Class<?>, Predicate<Packet<?>>> entries : cancelPacketMap.entrySet()) {
                if ((entries.getKey()).isAssignableFrom(packet.getClass()) && (entries.getValue()).test(packet)) {
                    return true;
                }
            }

            if (!whitList.isEmpty() && !whitList.contains(packet.getClass())) {
                return true;
            } else {
                boolean needAdd = true;

                for(Entry<Class<?>, Predicate<Packet<?>>> entries : addReturnMap.entrySet()) {
                    if ((entries.getKey()).isAssignableFrom(packet.getClass()) && (entries.getValue()).test(packet)) {
                        needAdd = false;
                    }
                }

                if (needAdd) {
                    packets.add(packet);
                }

                return false;
            }
        } else {
            return true;
        }
    }
    @Listener
    public void onTick(TickEvent event) {
        if (storing) {
            packets.add(new S00PacketKeepAlive()); //Fixxxxxxxxxxxxxxxxxxxxxx
        }
        if (mc.getNetHandler() == null) {
            stopStoringPackets();
        }
    }
}
