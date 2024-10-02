package lol.tgformat.module.impl.combat;

import lol.tgformat.api.event.Listener;
import lol.tgformat.events.TickEvent;
import lol.tgformat.events.packet.PacketSendEvent;
import lol.tgformat.events.render.Render2DEvent;
import lol.tgformat.module.Module;
import lol.tgformat.module.ModuleType;
import lol.tgformat.module.values.impl.BooleanSetting;
import lol.tgformat.module.values.impl.NumberSetting;
import lol.tgformat.ui.font.FontUtil;
import lol.tgformat.utils.client.LogUtil;
import lol.tgformat.component.MovementComponent;
import lol.tgformat.utils.network.PacketUtil;
import lol.tgformat.utils.player.BlinkUtils;
import lol.tgformat.utils.player.InventoryUtil;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.*;
import net.netease.utils.RoundedUtils;
import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import java.awt.*;

import static lol.tgformat.module.impl.combat.KillAura.target;

/**
 * @author TG_format
 * @since 2024/7/28 下午8:26
 */
@NativeObfuscation
@StringEncryption
@ControlFlowObfuscation
public class Gapple extends Module {
    public NumberSetting duringSendTicks = new NumberSetting("DuringSendTicks", 1, 10,0,1);
    public NumberSetting delay = new NumberSetting("Delay", 9, 10,0,1);
    public BooleanSetting auto = new BooleanSetting("Auto", false);
    private final boolean stopMove = true;
    private int slot = -1;
    public static int storedC03 = 0;
    public static boolean eating = false;
    public static boolean pulsing = false;

    public static boolean restart = false;

    public Gapple() {
        super("Gapple", ModuleType.Misc);
    }

    @NativeObfuscation(verificationLock = "User")
    @Override
    public void onEnable() {
        storedC03 = 0;
        this.slot = InventoryUtil.findItem(36, 45, Items.golden_apple);
        if (this.slot != -1) {
            this.slot -= 36;
        }
    }


    @NativeObfuscation(verificationLock = "User")
    @Override
    public void onDisable() {
        eating = false;

        pulsing = false;
        BlinkUtils.stopBlink();

        if (this.stopMove) {
            MovementComponent.resetMove();
        }
    }


    @NativeObfuscation(verificationLock = "User")
    @Listener
    public void onTick(TickEvent event) {
        if (mc.thePlayer == null || mc.thePlayer.isDead) {
            BlinkUtils.stopBlink();
            this.setState(false);
            return;
        }
        if (this.slot == -1) {
            LogUtil.addChatMessage("没苹果");
            this.setState(false);
            return;
        }
        if (eating) {
            if (this.stopMove) {
                MovementComponent.cancelMove();
            }
            if (!BlinkUtils.isBlinking()) {
                BlinkUtils.startBlink();
            }
        } else {
            eating = true;
        }
        if (storedC03 >= 32) {
            eating = false;
            pulsing = true;
            PacketUtil.sendPacketNoEvent(new C09PacketHeldItemChange(this.slot));
            PacketUtil.sendPacketNoEvent(new C08PacketPlayerBlockPlacement(mc.thePlayer.inventoryContainer.getSlot(this.slot + 36).getStack()));
            BlinkUtils.stopBlink();
            PacketUtil.sendPacketNoEvent(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
            pulsing = false;
            this.setState(false);
            LogUtil.addChatMessage("Eat");
            if (auto.isEnabled()){
                if (target.getName()!=null){
                    LogUtil.addChatMessage("Stop");
                    restart = true;
                    setState(true);
                    restart = false;
                    LogUtil.addChatMessage("Restart");
                }
            }else {
                restart = false;
            }
            return;
        }
        if ((mc.thePlayer.ticksExisted % delay.getValue().intValue()) == 0) {
            BlinkUtils.releaseC03(duringSendTicks.getValue().intValue());
        }
    }

    @NativeObfuscation(verificationLock = "User")
    @Listener
    public void onPacketSend(PacketSendEvent event) {
        if (event.getPacket() instanceof C07PacketPlayerDigging c07 && c07.getStatus().equals(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM)) {
            event.setCancelled();
        }
    }


    @NativeObfuscation(verificationLock = "User")
    @Listener
    public void onRender2D(Render2DEvent event) {
        ScaledResolution sr = new ScaledResolution(mc);
        float target = (float)(120.0f * (storedC03 / 32.0)) * ((float) 100 / 120);
        int startX = sr.getScaledWidth() / 2 - 68;
        int startY = sr.getScaledHeight() / 2 - 20;
        String text = "Gapple...";
        FontUtil.tenacityFont18.drawString(text, startX + 10 + 60 - FontUtil.tenacityFont18.getStringWidth(text) / 2, startY + 20, new Color(225, 225, 225, 100).getRGB());
        RoundedUtils.drawGradientRound(startX + 10, (float) (startY + 7.5), 120.0f, 2.0f, 3.0f, new Color(0, 0, 0, 200), new Color(0, 0, 0, 150), new Color(0, 0, 0, 150), new Color(0, 0, 0, 150));
        RoundedUtils.drawGradientRound(startX + 10, (float) (startY + 7.5), Math.min(target, 120.0f), 2.0f, 3.0f,new Color(241, 59, 232, 170), new Color(241, 59, 232, 170), new Color(241, 59, 232, 170), new Color(241, 59, 232, 170));
    }
}