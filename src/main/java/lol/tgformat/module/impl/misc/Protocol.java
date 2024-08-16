package lol.tgformat.module.impl.misc;

import lol.tgformat.api.event.Listener;
import lol.tgformat.events.PreUpdateEvent;
import lol.tgformat.events.motion.PreMotionEvent;
import lol.tgformat.events.packet.PacketReceiveEvent;
import lol.tgformat.events.packet.PacketSendEvent;
import lol.tgformat.module.Module;
import lol.tgformat.module.ModuleType;
import lol.tgformat.module.values.impl.ModeSetting;
import lol.tgformat.utils.client.LogUtil;
import lol.tgformat.utils.client.protocol.Heypixel;
import lol.tgformat.utils.timer.StopWatch;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C15PacketClientSettings;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.network.play.server.S01PacketJoinGame;
import net.minecraft.network.play.server.S3FPacketCustomPayload;
import tech.skidonion.obfuscator.annotations.Renamer;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import java.nio.charset.StandardCharsets;

/**
 * @Author KuChaZi/XiaoNaLa
 * @Date 2024/7/13 11:38
 * @ClassName: Protocol
 */
@Renamer
@StringEncryption
public class Protocol extends Module {
    public final ModeSetting mode = new ModeSetting("Mode", "HYT", "HYT", "Heypixel");
    private boolean isFirst = true;
    private final StopWatch stopWatch = new StopWatch();
    public Protocol() {
        super("Protocol", ModuleType.Misc);
    }
    @Listener
    public void onMotion(PreMotionEvent event) {
        this.setSuffix(mode.getMode());
    }

    @Listener
    public void onPacketEvent(PacketSendEvent event) {
        Packet<?> packet = event.getPacket();
        if (mode.is("Heypixel")) {
            if (packet instanceof C15PacketClientSettings) {
                event.setCancelled(true);
                Heypixel.sendMinecraftRegister();
                Heypixel.sendForgeBrand();
                Heypixel.sendJoinHeypixelCheck();
                isFirst = true;
                stopWatch.reset();
            }
            if (packet instanceof C17PacketCustomPayload customPayload) {
                String channelName = customPayload.getChannelName();
                System.out.println("Channel: " + channelName);
                LogUtil.addChatMessage("Channel: " + channelName);

                PacketBuffer data = customPayload.getBufferData();
                if (data != null && data.readableBytes() > 0) {
                    byte[] payloadBytes = new byte[data.readableBytes()];
                    data.readBytes(payloadBytes);
                    String payloadContent = new String(payloadBytes, StandardCharsets.UTF_8);
                    System.out.println("Payload content: " + payloadContent);
                    LogUtil.addChatMessage("Payload content: " + payloadContent);
                }
            }
        }
    }

    @Listener
    public void onTick(PreUpdateEvent event) {
        try {
            if (isFirst && isNull()) {
                Heypixel.sendJoinHeypixelCheck();
                stopWatch.reset();
            } else if (stopWatch.finished(5000)) {
                Heypixel.sendKeepAlive();
                stopWatch.reset();
            }
        }
        catch (Exception ex){

        }
    }

//    @Listener
//    public void onPacketSend(PacketSendEvent event) {
//        Packet<?> packet = event.getPacket();
//        if (mode.is("Heypixel")) {
//            if (packet instanceof S01PacketJoinGame) {
//                event.setCancelled();
//            }
//            if (packet instanceof S3FPacketCustomPayload) {
//                if (((S3FPacketCustomPayload) packet).getChannelName().equals("MC|Brand")) {
//                    event.setCancelled();
//                }
//            }
//            if (packet instanceof C17PacketCustomPayload) {
//                if (((C17PacketCustomPayload) packet).getChannelName().equals("MC|Brand")) {
//                    event.setCancelled();
//                }
//            }
//        }
//    }
}
