package lol.tgformat.module.impl.player;

import com.mojang.authlib.GameProfile;
import lol.tgformat.api.event.Listener;
import lol.tgformat.events.PreUpdateEvent;
import lol.tgformat.events.TickEvent;
import lol.tgformat.events.motion.PreMotionEvent;
import lol.tgformat.events.packet.PacketReceiveEvent;
import lol.tgformat.events.packet.PacketSendEvent;
import lol.tgformat.events.render.Render2DEvent;
import lol.tgformat.module.Module;
import lol.tgformat.module.ModuleType;
import lol.tgformat.module.values.impl.BooleanSetting;
import lol.tgformat.module.values.impl.ModeSetting;
import lol.tgformat.module.values.impl.NumberSetting;
import lol.tgformat.ui.font.FontUtil;
import lol.tgformat.utils.network.PacketUtil;
import lol.tgformat.utils.player.BlinkUtils;
import lol.tgformat.utils.render.GlowUtils;
import lol.tgformat.utils.timer.TimerUtil;
import lombok.Getter;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IProjectile;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S23PacketBlockChange;
import net.minecraft.network.play.server.S27PacketExplosion;
import net.minecraft.util.MathHelper;
import net.netease.font.FontManager;
import net.netease.utils.RoundedUtils;
import org.lwjgl.input.Keyboard;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;
import tech.skidonion.obfuscator.annotations.Renamer;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import java.awt.*;
import java.util.LinkedList;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @Author KuChaZi
 * @Date 2024/7/1 22:21
 * @ClassName: Blink
 */
@Renamer
@StringEncryption
public class Blink extends Module {
    @Getter
    private static EntityOtherPlayerMP fakePlayer;
    public final ModeSetting releasemode = new ModeSetting("Release Mode", "Latency", "Instant", "Latency");
    private final NumberSetting speed = new NumberSetting("Speed", 10.0, 100.0, 1.0, 1.0);
    private final BooleanSetting slowPoll = new BooleanSetting("Release Slow", true);
    private final NumberSetting pollDelay = new NumberSetting("Release Delay", 100.0, 100.0, 0.0, 1.0);
    private final NumberSetting startPollDelay = new NumberSetting("Start Release Delay", 2000.0, 8000.0, 1000.0, 200.0);
    private final BooleanSetting pulse = new BooleanSetting("Pulse", false);
    private final NumberSetting delay = new NumberSetting("Pulse Delay", 100.0, 5000.0, 0.0, 10.0);
    private final BooleanSetting confirmTransaction = new BooleanSetting("C0F", true);
    private final BooleanSetting action = new BooleanSetting("Action", true);
    private final BooleanSetting interact = new BooleanSetting("Interact", true);
    private final BooleanSetting itemChange = new BooleanSetting("ItemChange", true);
    private final BooleanSetting usingItem = new BooleanSetting("UsingItem", true);
    private final BooleanSetting debug = new BooleanSetting("Debug", true);
    private final BlinkUtils blinkUtils = new BlinkUtils();
    private final TimerUtil timer = new TimerUtil();
    private final LinkedList<Packet<INetHandler>> inBus = new LinkedList<>();
    private final ConcurrentLinkedQueue<Packet<?>> packets = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<Packet<?>> testPackets = new ConcurrentLinkedQueue<>();
    private final TimerUtil timer2 = new TimerUtil();
    private boolean sendPacket = false;
    private double x;
    private double y;
    private double z;
    private double motionX;
    private double motionY;
    private double motionZ;
    private boolean pressed;

    public Blink() {
        super("Blink", ModuleType.Player);
        speed.addParent(releasemode, modeSetting -> modeSetting.is("Latency"));
        slowPoll.addParent(releasemode, modeSetting -> modeSetting.is("Latency"));
        pollDelay.addParent(releasemode, modeSetting -> modeSetting.is("Latency"));
        startPollDelay.addParent(releasemode, modeSetting -> modeSetting.is("Latency"));

        confirmTransaction.addParent(releasemode, modeSetting -> modeSetting.is("Instant"));
        action.addParent(releasemode, modeSetting -> modeSetting.is("Instant"));
        interact.addParent(releasemode, modeSetting -> modeSetting.is("Instant"));
        itemChange.addParent(releasemode, modeSetting -> modeSetting.is("Instant"));
        usingItem.addParent(releasemode, modeSetting -> modeSetting.is("Instant"));

        delay.addParent(pulse, a -> pulse.isEnabled());

    }

    @Override
    public void onEnable() {
        if(mc.thePlayer == null) return;
        if (releasemode.is("Latency")) {
            if (!packets.isEmpty() && sendPacket) {
                return;
            }
            sendPacket = false;
            x = mc.thePlayer.posX;
            y = mc.thePlayer.posY;
            z = mc.thePlayer.posZ;
            motionX = mc.thePlayer.motionX;
            motionY = mc.thePlayer.motionY;
            motionZ = mc.thePlayer.motionZ;
            pressed = mc.gameSettings.keyBindSneak.isPressed();

            packets.clear();
            timer2.reset();
            fakePlayer = new EntityOtherPlayerMP(mc.theWorld, new GameProfile(new UUID(69L, 96L), "[Blink]" + mc.thePlayer.getName()));
            fakePlayer.copyLocationAndAnglesFrom(mc.thePlayer);
            fakePlayer.rotationYawHead = mc.thePlayer.rotationYawHead;
            mc.theWorld.addEntityToWorld(-1337, fakePlayer);
        }
    }

    @Override
    public void onDisable() {
        if (releasemode.is("Latency")) {
            timer.reset();
            if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
                mc.thePlayer.setPosition(x, y, z);
                mc.thePlayer.motionX = motionX;
                mc.thePlayer.motionY = motionY;
                mc.thePlayer.motionZ = motionZ;
                sendPacket = false;

                for (Packet<?> packet : packets) {
                    if (packet instanceof C08PacketPlayerBlockPlacement c08) {
                        if (mc.theWorld.getBlockState(c08.getPosition()).getBlock() != null) {
                            mc.theWorld.setBlockToAir(c08.getPosition());
                        }
                    }
                }
                mc.gameSettings.keyBindSneak.setPressed(pressed);
                packets.clear();
                return;
            }
            if (!packets.isEmpty()) {
                sendPacket = true;
                this.setState(true);
                return;
            }
            if (fakePlayer != null) {
                mc.theWorld.removeEntityFromWorld(fakePlayer.getEntityId());
                fakePlayer = null;
            }
        } else {
            blinkUtils.release();
        }
        inBus.forEach(packet -> packet.processPacket(mc.getNetHandler()));
        inBus.clear();
    }

    @Listener
    public void onMotion(PreMotionEvent event) {
        this.setSuffix(releasemode.getMode());
    }

    @Listener
    private void onRender2D(Render2DEvent event) {
        if (releasemode.is("Latency") && debug.isEnabled()) {
            ScaledResolution sr = new ScaledResolution(mc);
            int startX = sr.getScaledWidth() / 2 - 68;
            int startY = sr.getScaledHeight() / 2 + 30;
            GlStateManager.disableAlpha();
            String text = "" + packets.size();
            FontUtil.tenacityFont18.drawString(text, startX + 10 + 60 - FontUtil.tenacityFont18.getStringWidth(text) / 2, startY + 20, new Color(225, 225, 225, 100).getRGB());
            RoundedUtils.drawGradientRound(startX + 10, (float) (startY + 7.5), 120.0f, 3.0f, 3.0f, new Color(0, 0, 0, 200), new Color(0, 0, 0, 150), new Color(0, 0, 0, 150), new Color(0, 0, 0, 150));
            RoundedUtils.drawGradientRound(startX + 10, (float) (startY + 7.5), Math.min(packets.size() / 10.0f, 60.0f), 3.0f, 3.0f,new Color(241, 59, 232, 170), new Color(241, 59, 232, 170), new Color(241, 59, 232, 170), new Color(241, 59, 232, 170));
            GlStateManager.disableAlpha();
        }
        if (releasemode.is("Instant") && debug.isEnabled()) {
            ScaledResolution sr = new ScaledResolution(mc);
            mc.fontRendererObj.drawStringWithShadow("Stored Packets: " + blinkUtils.getClient().size(), sr.getScaledWidth() / 2F - 50F, sr.getScaledHeight() / 2F + 40F, -1);

        }
    }

    @Listener
    private void onT1ck(TickEvent event) {
        if (releasemode.is("Latency")) {
            if (mc.thePlayer == null) {
                packets.clear();
                testPackets.clear();
                this.setState(false);
            }
            for (Entity entity : mc.theWorld.loadedEntityList) {
                if (entity instanceof IProjectile) {
                    if (entity.onGround) continue;
                    float dX = (float) (x - entity.posX);
                    float dY = (float) (y - entity.posY);
                    float dZ = (float) (z - entity.posZ);
                    float distance = dX * dX + dY * dY + dZ * dZ;
                    if (MathHelper.sqrt_float(distance) < 8) {
                        while (!packets.isEmpty()) {
                            poll(2);
                        }
                    }
                }
            }
            if (timer2.hasTimePassed(startPollDelay.getValue().longValue())) {
                if (slowPoll.isEnabled() && timer.hasTimePassed(pollDelay.getValue().longValue()) && packets.size() >= 100) {
                    poll(2);
                    timer.reset();
                }
            }
        } else {
            if (pulse.isEnabled()) {
                this.setSuffix(timer.getTimePassed());
            } else {
                this.setSuffix("");
            }
            if (timer.hasTimePassed(delay.getValue().intValue())) {
                if (pulse.isEnabled()) {
                    blinkUtils.release();
                }
                timer.reset();
            }
        }
    }

    
    private void poll(int count) {
        for (int i = 0; i < count; i++) {
            Packet<?> packet = this.packets.poll();
            if (packet instanceof C03PacketPlayer.C04PacketPlayerPosition || packet instanceof C03PacketPlayer.C06PacketPlayerPosLook) {
                C03PacketPlayer wrapper = (C03PacketPlayer) packet;
                fakePlayer.posX = wrapper.getX();
                fakePlayer.posY = wrapper.getY();
                fakePlayer.posZ = wrapper.getZ();
                fakePlayer.onGround = wrapper.isOnGround();
                fakePlayer.rotationPitch = wrapper.getPitch();
                fakePlayer.rotationYaw = wrapper.getYaw();
            }
            PacketUtil.sendPacketNoEvent(packet);
        }
    }

    @Listener
    private void onPacketSend(PacketSendEvent event) {
        if (mc.thePlayer == null || mc.thePlayer.isDead || mc.isSingleplayer() || mc.thePlayer.ticksExisted < 50) {
            packets.clear();
            return;
        }
        if (releasemode.is("Instant")) {
            Packet<?> packet = event.getPacket();
            if (
                    packet instanceof C03PacketPlayer ||
                            packet instanceof C0FPacketConfirmTransaction && confirmTransaction.isEnabled() ||
                            packet instanceof C0BPacketEntityAction && action.isEnabled() ||
                            (packet instanceof C02PacketUseEntity || packet instanceof C0APacketAnimation) && interact.isEnabled() ||
                            packet instanceof C09PacketHeldItemChange && itemChange.isEnabled() ||
                            packet instanceof C08PacketPlayerBlockPlacement && usingItem.isEnabled()
            ) {
                blinkUtils.startBlink(event, packet);
            }
        } else {
            if (event.getPacket() instanceof C01PacketChatMessage) {
                return;
            }
            if (sendPacket) {
                if (event.getPacket() instanceof C02PacketUseEntity) {
                    event.setCancelled();
                    return;
                }
                testPackets.add(event.getPacket());
                event.setCancelled(true);
                return;
            }
            packets.add(event.getPacket());
            event.setCancelled(true);
        }
    }

    @Listener
    private void onPacketReceive(PacketReceiveEvent event) {
        if (releasemode.is("Latency")) {
            if (event.getPacket() instanceof S12PacketEntityVelocity) {
                if (((S12PacketEntityVelocity) event.getPacket()).getEntityID() == mc.thePlayer.getEntityId()) {
                    this.setState(false);
                }
            }
        }
    }

    @Listener
    public void onTick(TickEvent event) {
        if (mc.currentScreen != null) {
            this.setState(false);
            return;
        }

        if (releasemode.is("Latency") && sendPacket) {
            if (packets.isEmpty()) {
                while (!this.testPackets.isEmpty()) {
                    PacketUtil.sendPacketNoEvent(this.testPackets.poll());
                }
                if (fakePlayer != null) {
                    mc.theWorld.removeEntityFromWorld(fakePlayer.getEntityId());
                    fakePlayer = null;
                }
                this.setState(false);
                return;
            }

            double test = 0;
            while (!this.packets.isEmpty()) {
                test++;
                if (test >= speed.getValue())
                    break;
                poll(1);
            }
        }
    }
}
