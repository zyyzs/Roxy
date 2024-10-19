package lol.tgformat.module.impl.misc;

import lol.tgformat.api.event.Listener;
import lol.tgformat.events.TickEvent;
import lol.tgformat.events.WorldEvent;
import lol.tgformat.events.motion.PostMotionEvent;
import lol.tgformat.events.packet.PacketReceiveEvent;
import lol.tgformat.module.Module;
import lol.tgformat.module.ModuleManager;
import lol.tgformat.module.ModuleType;
import lol.tgformat.module.impl.world.Scaffold;
import lol.tgformat.module.values.impl.BooleanSetting;
import lol.tgformat.module.values.impl.NumberSetting;
import lol.tgformat.utils.timer.TimerUtil;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;

import java.util.ArrayList;
import java.util.List;

/**
 * @author TG_format
 * @since 2024/9/15 11:20
 */
public class GhostBlocksMaker extends Module {
    private List<Packet<INetHandler>> packets = new ArrayList<>();
    private final BooleanSetting delayedPlush = new BooleanSetting("DelayedPlush", true);
    private final NumberSetting plushDelayTime = new NumberSetting("PlushDelayTime", 500, 2000, 100, 10);
    private final TimerUtil timer = new TimerUtil();
    public GhostBlocksMaker() {
        super("GhostBlocksMaker", ModuleType.Misc);
    }

    @Override
    public void onEnable() {
        packets.clear();
        timer.reset();
    }

    @Override
    public void onDisable() {
        process();
        timer.reset();
    }

    @Listener
    public void onReceive(PacketReceiveEvent event) {
        if (event.getPacket() instanceof S08PacketPlayerPosLook) {
            setState(false);
            return;
        }
        packets.add((Packet<INetHandler>) event.getPacket());
        event.setCancelled();
    }

    @Listener
    public void onTick(TickEvent event) {
        if (isNull()) setState(false);
    }

    @Listener
    public void onWorld(WorldEvent event) {
        setState(false);
    }

    @Listener
    public void onPost(PostMotionEvent event) {
        if (timer.hasReached(plushDelayTime.getValue().intValue()) && delayedPlush.isEnabled()) {
            process();
            timer.reset();
        }
    }

    public void process() {
        packets.forEach(packet -> {
            packet.processPacket(Disabler.packetListener);
        });
        packets.clear();
    }

    @Override
    public void setState(boolean state) {
        super.setState(state);
        ModuleManager.getModule(Scaffold.class).setState(state);
    }
}
