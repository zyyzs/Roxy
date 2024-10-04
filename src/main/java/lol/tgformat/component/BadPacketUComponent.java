package lol.tgformat.component;

import lol.tgformat.accessable.IMinecraft;
import lol.tgformat.api.event.Listener;
import lol.tgformat.events.WorldEvent;
import lol.tgformat.events.packet.PacketReceiveEvent;
import lol.tgformat.module.impl.misc.Disabler;
import lol.tgformat.utils.client.LogUtil;
import lol.tgformat.utils.network.PacketUtil;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;

/**
 * @author TG_format
 * @since 2024/8/14 下午7:44
 */
public class BadPacketUComponent implements IMinecraft {
    private static boolean c03Check = false;
    private static boolean shouldFix = false;
    public static boolean onPacket(Packet<?> packet) {
        if (!Disabler.badPacketsU.isEnabled()) {
            return false;
        }
        if (packet instanceof C0BPacketEntityAction c0b) {
            if (c0b.getAction().equals(C0BPacketEntityAction.Action.START_SPRINTING)) {
                c03Check = true;
            }
            if (c0b.getAction().equals(C0BPacketEntityAction.Action.STOP_SPRINTING) && c03Check) {
                shouldFix = true;
                return true;
            }
        }
        if (packet instanceof C03PacketPlayer) {
            if (shouldFix && !c03Check) {
                shouldFix = false;
                mc.thePlayer.serverSprintState = true;
            }
            c03Check = false;
        }
        return false;
    }
    @Listener
    public void onWorld(WorldEvent event) {
        c03Check = false;
        shouldFix = false;
    }
}
