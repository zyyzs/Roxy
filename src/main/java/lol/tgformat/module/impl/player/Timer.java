package lol.tgformat.module.impl.player;

import lol.tgformat.api.event.Listener;
import lol.tgformat.events.PreUpdateEvent;
import lol.tgformat.events.WorldEvent;
import lol.tgformat.events.motion.PostMotionEvent;
import lol.tgformat.events.packet.PacketReceiveEvent;
import lol.tgformat.events.packet.PacketSendEvent;
import lol.tgformat.events.render.Render2DEvent;
import lol.tgformat.module.Module;
import lol.tgformat.module.ModuleManager;
import lol.tgformat.module.ModuleType;
import lol.tgformat.module.impl.world.Scaffold;
import lol.tgformat.module.values.impl.BooleanSetting;
import lol.tgformat.module.values.impl.ModeSetting;
import lol.tgformat.module.values.impl.NumberSetting;
import lol.tgformat.ui.font.FontUtil;
import lol.tgformat.utils.client.LogUtil;
import lol.tgformat.utils.move.MoveUtil;
import lol.tgformat.utils.network.PacketUtil;
import lol.tgformat.utils.render.GlowUtils;
import lol.tgformat.utils.timer.StopWatch;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.server.*;
import net.netease.font.FontManager;
import net.netease.utils.RoundedUtils;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;
import tech.skidonion.obfuscator.annotations.Renamer;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import java.awt.*;
import java.util.LinkedList;

/**
 * @Author KuChaZi
 * @Date 2024/6/8 21:08
 * @ClassName: BalanceTimer
 */

@Renamer

@StringEncryption
public class Timer extends Module {

    private final ModeSetting mode = new ModeSetting("Mode","Basic","Basic","Balance");
    private final NumberSetting speed = new NumberSetting("TimerSpeed", 3.0, 6.0, 1.0,0.1);
    private final BooleanSetting dis = new BooleanSetting("AutoDisable", true);
    private final BooleanSetting dissca = new BooleanSetting("AutoDisSca", true);
    private final BooleanSetting poslook = new BooleanSetting("PosLook", true);
    private final BooleanSetting debug = new BooleanSetting("DeBug", true);
    private final BooleanSetting render = new BooleanSetting("Render", true);
    private final LinkedList<Packet<INetHandler>> inBus = new LinkedList<>();
    private final StopWatch stopWatch = new StopWatch();
    private int balance = 0;
    private boolean disable;

    public Timer() {
        super("Timer", ModuleType.Player);
    }

    @Override
    public void onEnable() {
        balance = 0;
        stopWatch.reset();

        if (dis.isEnabled()) {
            disable = false;
        }
    }

    @Override
    public void onDisable() {
        if (mc.thePlayer == null) return;
        if (mode.is("Balance")) {
            inBus.forEach(packet -> packet.processPacket(mc.getNetHandler()));
            inBus.clear();
        }

        mc.timer.timerSpeed = 1F;
    }

    @Listener
    public void onPacket(PacketReceiveEvent event) {
        if (mode.is("Balance")) {
            Packet<?> packet = event.getPacket();
            if (packet instanceof S32PacketConfirmTransaction) {
                event.setCancelled();
                mc.getNetHandler().addToSendQueue(new C0FPacketConfirmTransaction(0, (short) 0, true));
            }
            if (packet instanceof S12PacketEntityVelocity) {
                inBus.add((Packet<INetHandler>) packet);
                event.setCancelled();
            }
            if (packet instanceof S27PacketExplosion) {
                inBus.add((Packet<INetHandler>) packet);
                event.setCancelled();
            }
            if (packet instanceof S23PacketBlockChange) {
                inBus.add((Packet<INetHandler>) packet);
                event.setCancelled();
            }
            if (packet instanceof S08PacketPlayerPosLook && poslook.isEnabled()) {
                setState(false);
            }
        }
    }

    @Listener
    public void onPacket(PacketSendEvent event) {
        if (mode.is("Balance")) {
            Packet<?> packet = event.getPacket();
            if (packet instanceof C03PacketPlayer c03) {
                if (!c03.rotating && !MoveUtil.isMoving()) {
                    event.setCancelled();
                }

                if (!event.isCancelled() || ModuleManager.getModule(Blink.class).isState()) {
                    balance -= 50;
                }

                balance += (int) stopWatch.getElapsedTime();
                stopWatch.reset();
            }
        }
    }

    @Listener
    public void onUpdate(PreUpdateEvent event) {
        if (mode.is("Balance")) {
            if (balance < 200) {
                mc.timer.timerSpeed = 1F;
                if (dis.isEnabled()) {
                    if (disable) {
                        setState(false);
                        if (dissca.isEnabled()) {
                            if (ModuleManager.getModule(Scaffold.class).isState()) {
                                ModuleManager.getModule(Scaffold.class).setState(false);
                            }
                        }
                    }
                }
            } else {
                mc.timer.timerSpeed = (float) speed.getValue().doubleValue();
                if (dis.isEnabled()) {
                    disable = true;
                }
            }
        }
    }

    @Listener
    public void onMotion(PostMotionEvent event) {
        if (mode.is("Basic")) {
            mc.timer.timerSpeed = speed.getValue().floatValue();
        }
        if (mc.thePlayer.ticksExisted % 20 == 0 && debug.isEnabled() && mode.is("Balance")) {
            LogUtil.addChatMessage("BalanceTimer: " + balance);
        }
    }

    @Listener
    public void onWorld(WorldEvent event) {
        if (event == null) return;

        setState(false);
        stopWatch.reset();
    }

    @Listener
    public void onRender2D(Render2DEvent event) {
        ScaledResolution sr = new ScaledResolution(mc);
        if (render.isEnabled() && mode.is("Balance")) {
            int startX = sr.getScaledWidth() / 2 - 68;
            int startY = sr.getScaledHeight() / 2 -20;
            int Packet = balance;
            GlStateManager.disableAlpha();
            String text = "" + Packet;
            FontUtil.tenacityFont18.drawString(text, startX + 10 + 60 - FontUtil.tenacityFont18.getStringWidth(text) / 2, startY + 20, new Color(225, 225, 225, 100).getRGB());
            RoundedUtils.drawGradientRound(startX + 10, (float) (startY + 7.5), 120.0f, 3.0f, 3.0f, new Color(0, 0, 0, 200), new Color(0, 0, 0, 150), new Color(0, 0, 0, 150), new Color(0, 0, 0, 150));
            RoundedUtils.drawGradientRound(startX + 10, (float) (startY + 7.5), Math.min(Packet / 50.0f, 120.0f), 3.0f, 3.0f,new Color(241, 59, 232, 170), new Color(241, 59, 232, 170), new Color(241, 59, 232, 170), new Color(241, 59, 232, 170));
            GlStateManager.disableAlpha();
        }
    }

}

