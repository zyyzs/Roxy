package lol.tgformat.utils.player;

import lol.tgformat.accessable.IMinecraft;
import lol.tgformat.api.event.EventManager;
import lol.tgformat.api.event.Listener;
import lol.tgformat.events.TickEvent;
import lol.tgformat.events.packet.PacketSendEvent;
import lol.tgformat.module.ModuleManager;
import lol.tgformat.module.impl.player.Blink;
import lol.tgformat.utils.network.PacketUtil;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S00PacketKeepAlive;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * @author TG_format
 * @since 2024/7/28 下午9:59
 */
public class BlinkHandler implements IMinecraft {
    public static boolean blinking = false;

    private static final List<Class<?>> blackList = new ArrayList<>();
    private static final Map<Class<?>, Predicate<Packet<?>>> cancelReturnPredicateMap = new HashMap<>();
    private static final Map<Class<?>, Predicate<Packet<?>>> releaseReturnPredicateMap = new HashMap<>();
    private static final Map<Class<?>, Consumer<Packet<?>>> cancelActionMap = new HashMap<>();
    private static final Map<Class<?>, Consumer<Packet<?>>> releaseActionMap = new HashMap<>();
    private static final List<Class<?>> whitList = new ArrayList<>();

    public static LinkedBlockingQueue<Packet<?>> packets = new LinkedBlockingQueue<>();
    public static boolean passEvent = false;
    public static boolean blink(Class<?>... fliterPackets){
        Blink blink = ModuleManager.getModule(Blink.class);

        if(blinking || blink.isState())
            return false;

        Arrays.asList(fliterPackets).forEach(e -> {
            blackList.add(e);
            cancelReturnPredicateMap.put(e, f -> true);
        });

        blinking = true;
        return true;
    }

    /*e
     Fliter Packets
   */
    public static void addWhiteList(Class<?>... classes){
        whitList.addAll(Arrays.asList(classes));
    }
    public static void sendPacket(Packet<?> packet , boolean event){
        if(event){
            PacketSendEvent packetSendEvent = new PacketSendEvent(packet);
            EventManager.call(packetSendEvent);
            if(packetSendEvent.isCancelled())
                return;
        }
        passEvent = true;
        PacketUtil.sendPacketNoEvent(packet);
        passEvent = false;
    }
    public static void sendPacket(Packet<?> packet){
        passEvent = true;
        PacketUtil.sendPacketNoEvent(packet);
        passEvent = false;
    }
    public static void sendEventPacket(Packet<?> packet){
        passEvent = true;
        PacketUtil.sendPacket(packet);
        passEvent = false;
    }
    /*
      Action
    */
    public static void setCancelReturnPredicate(Class<?> clazz, Predicate<Packet<?>> predicate){
        boolean isIN = false;

        for (Class<?> classes : cancelReturnPredicateMap.keySet()){
            if (classes == clazz) {
                isIN = true;
                break;
            }
        }
        if(isIN) {
            cancelReturnPredicateMap.replace(clazz, predicate);
        } else {
            cancelReturnPredicateMap.put(clazz, predicate);
        }
    }

    public static void resetBlackList(){
        blackList.clear();
    }

    public static void setReleaseReturnPredicateMap(Class<?> clazz, Predicate<Packet<?>> predicate){
        boolean isIN = false;

        for (Class<?> classes : releaseReturnPredicateMap.keySet()){
            if (classes == clazz) {
                isIN = true;
                break;
            }
        }
        if(isIN) {
            releaseReturnPredicateMap.replace(clazz, predicate);
        } else {
            releaseReturnPredicateMap.put(clazz, predicate);
        }
    }
    public static void setCancelAction(Class<?> clazz, Consumer<Packet<?>> packetConsumer){
        boolean isIN = false;

        for (Class<?> classes : cancelActionMap.keySet()){
            if (classes == clazz) {
                isIN = true;
                break;
            }
        }
        if(isIN) {
            cancelActionMap.replace(clazz, packetConsumer);
        } else {
            cancelActionMap.put(clazz, packetConsumer);
        }
    }
    public static void setReleaseAction(Class<?> clazz, Consumer<Packet<?>> packetConsumer){
        boolean isIN = false;

        for (Class<?> classes : releaseActionMap.keySet()){
            if (classes == clazz) {
                isIN = true;
                break;
            }
        }
        if(isIN) {
            releaseActionMap.replace(clazz, packetConsumer);
        } else {
            releaseActionMap.put(clazz, packetConsumer);
        }
    }

    /*
      release Packet
     */
    public static void releasePacket(boolean sendOneTick){
        releasePacket(packets.size(),true,sendOneTick);
    }
    public static void releasePacket(){
        releasePacket(packets.size(),true);
    }
    public static void releasePacket(int sendPackets,boolean noEvent){
        releasePacket(sendPackets,noEvent,false);
    }
    public static void releasePacket(int sendPackets,boolean noEvent,boolean sendOneTick){
        int sends = 0;
        try {
            here:
            while (!packets.isEmpty()){
                Packet<?> packet = packets.take();

                if(packet instanceof S00PacketKeepAlive){
                    if(sendOneTick) {
                        sends = packets.size();
                        break;
                    }
                    continue;
                }

                for (Map.Entry<Class<?>, Predicate<Packet<?>>> entries : releaseReturnPredicateMap.entrySet()){
                    if(entries.getKey().isAssignableFrom(packet.getClass())){
                        if(entries.getValue().test(packet)){
                            continue here;
                        }
                    }
                }

                releaseActionMap.forEach((key,value) ->{
                    if(key.isAssignableFrom(packet.getClass())){
                        value.accept(packet);
                    }
                });

                sends++;
                if(noEvent) {
                    passEvent = true;
                    PacketUtil.sendPacketNoEvent(packet);
                    passEvent = false;
                } else {
                    passEvent = true;
                    PacketUtil.sendPacket(packet);
                    passEvent = false;
                }
                if(sends >= sendPackets)
                    break;
            }
        } catch (Exception exception){
            exception.printStackTrace();
        }
    }

    public static void stopBlink(){
        blinking = false;
        passEvent = false;

        releasePacket();

        blackList.clear();
        cancelReturnPredicateMap.clear();
        cancelActionMap.clear();
        releaseActionMap.clear();
        whitList.clear();
    }

    public static boolean onPacket(Packet<?> packet) {
        if (blinking && !passEvent) {

            cancelActionMap.forEach((aClass, packetConsumer) -> {
                if(aClass.isAssignableFrom(packet.getClass())){
                    packetConsumer.accept(packet);
                }
            });

            for (Class<?> clazz : blackList){
                if(clazz.isAssignableFrom(packet.getClass()))
                {
                    return true;
                }
            }

            for (Map.Entry<Class<?>, Predicate<Packet<?>>> entries : cancelReturnPredicateMap.entrySet()){
                if(entries.getKey().isAssignableFrom(packet.getClass())){
                    if(entries.getValue().test(packet)){
                        return true;
                    }
                }
            }

            if(!whitList.isEmpty() && !whitList.contains(packet.getClass()))
                return true;

            packets.add(packet);
            return false;
        }
        return true;
    }

    @Listener
    public void onTick(TickEvent event) {
        if (mc.thePlayer == null) return;
        if(mc.getNetHandler() == null) {
            stopBlink();
        }
        if (blinking)
            packets.add(new S00PacketKeepAlive());
    }
}
