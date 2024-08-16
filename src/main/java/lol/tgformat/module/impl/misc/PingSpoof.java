package lol.tgformat.module.impl.misc;

import lol.tgformat.api.event.Listener;
import lol.tgformat.events.packet.PacketSendEvent;
import lol.tgformat.module.Module;
import lol.tgformat.module.ModuleType;
import lol.tgformat.module.values.impl.NumberSetting;
import lol.tgformat.utils.network.PacketUtil;
import lol.tgformat.utils.timer.TimerUtil;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;
import tech.skidonion.obfuscator.annotations.Renamer;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @Author KuChaZi
 * @Date 2024/7/7 18:15
 * @ClassName: PingSpoof
 */
@Renamer

@StringEncryption
public class PingSpoof extends Module {
    private final NumberSetting delay = new NumberSetting("Delay", 1000.0, 5000.0, 30.0, 1.0);

    private final List<Packet<?>> packetList = new CopyOnWriteArrayList<>();
    private final TimerUtil timer = new TimerUtil();

    public PingSpoof() {
        super("PingSpoof", ModuleType.Misc);
    }

    @Listener
    
    private void onPacketSend(PacketSendEvent event) {
        if (mc.thePlayer != null && mc.theWorld != null && mc.thePlayer.isServerWorld()) {
            if (event.getPacket() instanceof C00PacketKeepAlive && mc.thePlayer.isEntityAlive()) {
                this.packetList.add(event.getPacket());
                event.setCancelled();
            }
            if (this.timer.hasTimePassed(delay.getValue().intValue())) {
                if (!this.packetList.isEmpty()) {
                    for (Packet<?> packet : this.packetList) {
                        PacketUtil.sendPacketNoEvent(packet);
                        this.packetList.remove(packet);
                    }
                }
                PacketUtil.sendPacketNoEvent(new C00PacketKeepAlive(10000));
                this.timer.reset();
            }
        }
    }
}
