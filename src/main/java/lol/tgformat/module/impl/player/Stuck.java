package lol.tgformat.module.impl.player;

import lol.tgformat.api.event.Listener;
import lol.tgformat.component.MovementComponent;
import lol.tgformat.events.PreUpdateEvent;
import lol.tgformat.events.movement.MoveEvent;
import lol.tgformat.events.packet.PacketReceiveEvent;
import lol.tgformat.events.packet.PacketSendEvent;
import lol.tgformat.module.Module;
import lol.tgformat.module.ModuleManager;
import lol.tgformat.module.ModuleType;
import lol.tgformat.module.impl.world.Scaffold;
import lol.tgformat.module.values.impl.BooleanSetting;
import lol.tgformat.utils.network.PacketUtil;
import lol.tgformat.utils.vector.Vector2f;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.server.*;
import org.lwjgl.input.Keyboard;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;
import tech.skidonion.obfuscator.annotations.Renamer;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import java.util.LinkedList;

/**
 * @author TG_format
 * @since 2024/6/8 下午2:08
 */

@Renamer
@StringEncryption
public class Stuck extends Module {
    public BooleanSetting antiSB;
    private Vector2f rotation;
    private final LinkedList<Packet<INetHandler>> inBus = new LinkedList<>();

    public Stuck() {
        super("Stuck", ModuleType.Player);
        this.antiSB = new BooleanSetting("Anti SB", false);
    }

    public void onEnable() {
        if (mc.thePlayer == null) {
            return;
        }
        this.rotation = new Vector2f(mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch);
        float f = mc.gameSettings.mouseSensitivity * 0.6f + 0.2f;
        float gcd = f * f * f * 1.2f;
        Vector2f rotation = this.rotation;
        rotation.x -= this.rotation.x % gcd;
        Vector2f rotation2 = this.rotation;
        rotation2.y -= this.rotation.y % gcd;
    }

    public void onDisable() {
        inBus.forEach(packet -> packet.processPacket(mc.getNetHandler()));
        inBus.clear();
        PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 1337, mc.thePlayer.posZ, mc.thePlayer.onGround));
        if (this.antiSB.isEnabled() && !mc.thePlayer.onGround) {
            this.setState(true);
        }
    }
    @Listener
    public void onSend(PacketSendEvent event) {
        if (event.getPacket() instanceof C08PacketPlayerBlockPlacement c08) {
            final Vector2f current = new Vector2f(mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch);
            final float f = mc.gameSettings.mouseSensitivity * 0.6f + 0.2f;
            final float gcd = f * f * f * 1.2f;
            current.x -= current.x % gcd;
            current.y -= current.y % gcd;
            if (this.rotation.x == current.x && this.rotation.y == current.y) return;
            this.rotation = current;
            event.setCancelled(true);
            PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C05PacketPlayerLook(current.x, current.y, mc.thePlayer.onGround));
            if (isBlock()) {
                PacketUtil.sendPacketNoEvent(c08);
            }
            else {
                PacketUtil.sendPacketNoEvent(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
            }
        }
        if (event.getPacket() instanceof C03PacketPlayer) {
            Scaffold scaffold = ModuleManager.getModule(Scaffold.class);
            if(scaffold.isState()){
                scaffold.setState(false);
            }
            event.setCancelled(true);
        }
    }
    @Listener
    public void onMove(MoveEvent event) {
        event.setCancelled();
    }
}
