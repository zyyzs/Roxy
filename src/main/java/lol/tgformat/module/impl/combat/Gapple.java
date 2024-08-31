package lol.tgformat.module.impl.combat;

import lol.tgformat.api.event.Listener;
import lol.tgformat.events.TickEvent;
import lol.tgformat.events.render.Render2DEvent;
import lol.tgformat.module.Module;
import lol.tgformat.module.ModuleType;
import lol.tgformat.module.values.impl.BooleanSetting;
import lol.tgformat.module.values.impl.NumberSetting;
import lol.tgformat.ui.font.FontUtil;
import lol.tgformat.utils.client.LogUtil;
import lol.tgformat.component.MovementComponent;
import lol.tgformat.component.PacketStoringComponent;
import lol.tgformat.utils.player.InventoryUtil;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.*;
import net.netease.utils.RoundedUtils;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import java.awt.*;
import java.text.DecimalFormat;

import static lol.tgformat.component.PacketStoringComponent.*;
import static lol.tgformat.module.impl.combat.KillAura.target;

/**
 * @author TG_format
 * @since 2024/7/28 下午8:26
 */
@StringEncryption
public class Gapple extends Module {
    public NumberSetting duringSendTicks = new NumberSetting("DuringSendTicks", 1, 10,0,1);
    public NumberSetting delay = new NumberSetting("Delay", 9, 10,0,1);
    public BooleanSetting auto = new BooleanSetting("Auto", false);
    private final boolean stopMove = true;
    public boolean noC02 = false;
    private int slot = -1;
    private int c03s = 0;
    private int c02s = 0;
    private boolean canStart = false;
    public static boolean eating = false;
    public static boolean pulsing = false;

    public static boolean restart = false;

    public Gapple() {
        super("Gapple", ModuleType.Misc);
    }

    @Override
    public void onEnable() {
        this.c03s = 0;
        this.slot = InventoryUtil.findItem(36, 45, Items.golden_apple);
        if (this.slot != -1) {
            this.slot -= 36;
        }
    }

    @Override
    public void onDisable() {
        eating = false;

        if (this.canStart) {
            pulsing = false;
            PacketStoringComponent.stopBlink();
        }
        if (this.stopMove) {
            MovementComponent.resetMove();
        }
    }

    @Listener
    public void onTick(TickEvent event) {
        if (mc.thePlayer == null || mc.thePlayer.isDead) {
            PacketStoringComponent.stopBlink();
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
            if (!blinking) {
                PacketStoringComponent.blink(C09PacketHeldItemChange.class, C0EPacketClickWindow.class, C0DPacketCloseWindow.class);
                PacketStoringComponent.setCancelReturnPredicate(C07PacketPlayerDigging.class, it -> ((C07PacketPlayerDigging) it).getStatus() == C07PacketPlayerDigging.Action.RELEASE_USE_ITEM);
                PacketStoringComponent.setCancelReturnPredicate(C08PacketPlayerBlockPlacement.class, it -> (((C08PacketPlayerBlockPlacement) it).getPosition().getY() == -1));
                PacketStoringComponent.setCancelReturnPredicate(C02PacketUseEntity.class, it -> false);
                PacketStoringComponent.setCancelReturnPredicate(C0APacketAnimation.class, it -> false);
                PacketStoringComponent.setCancelAction(C03PacketPlayer.class, (packet) -> c03s++);
                PacketStoringComponent.setReleaseAction(C03PacketPlayer.class, (packet) -> c03s--);
                PacketStoringComponent.setReleaseReturnPredicateMap(C02PacketUseEntity.class, (packet) -> !eating && noC02);
                PacketStoringComponent.setCancelAction(C02PacketUseEntity.class, (packet) -> c02s++);
                PacketStoringComponent.setReleaseAction(C02PacketUseEntity.class, (packet) -> c02s--);
                this.canStart = true;
            }
        } else {
            eating = true;
        }
        if (this.c03s >= 32) {
            eating = false;
            pulsing = true;
            PacketStoringComponent.resetBlackList();
            PacketStoringComponent.sendPacket(new C09PacketHeldItemChange(this.slot), false);
            PacketStoringComponent.sendPacket(new C08PacketPlayerBlockPlacement(mc.thePlayer.inventoryContainer.getSlot(this.slot + 36).getStack()), false);
            PacketStoringComponent.stopBlink();
            PacketStoringComponent.sendPacket(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem), false);
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
            for (int i = 0; i < duringSendTicks.getValue().intValue(); i++) {
                PacketStoringComponent.releasePacket(true);
                LogUtil.print("1");
            }
        }
    }


    @Listener
    public void onRender2D(Render2DEvent event) {
        ScaledResolution sr = new ScaledResolution(mc);
        float target = (float)(120.0f * (this.c03s / 32.0)) * ((float) 100 / 120);
        int startX = sr.getScaledWidth() / 2 - 68;
        int startY = sr.getScaledHeight() / 2 - 20;
        String text = "Gapple...";
        FontUtil.tenacityFont18.drawString(text, startX + 10 + 60 - FontUtil.tenacityFont18.getStringWidth(text) / 2, startY + 20, new Color(225, 225, 225, 100).getRGB());
        RoundedUtils.drawGradientRound(startX + 10, (float) (startY + 7.5), 120.0f, 2.0f, 3.0f, new Color(0, 0, 0, 200), new Color(0, 0, 0, 150), new Color(0, 0, 0, 150), new Color(0, 0, 0, 150));
        RoundedUtils.drawGradientRound(startX + 10, (float) (startY + 7.5), Math.min(target, 120.0f), 2.0f, 3.0f,new Color(241, 59, 232, 170), new Color(241, 59, 232, 170), new Color(241, 59, 232, 170), new Color(241, 59, 232, 170));
    }
}