package lol.tgformat.module.impl.movement;

import io.netty.buffer.Unpooled;
import lol.tgformat.api.event.Listener;
import lol.tgformat.component.PacketStoringComponent;
import lol.tgformat.events.motion.PostMotionEvent;
import lol.tgformat.events.motion.PreMotionEvent;
import lol.tgformat.events.movement.SlowEvent;
import lol.tgformat.events.packet.PacketReceiveEvent;
import lol.tgformat.events.packet.PacketSendEvent;
import lol.tgformat.module.Module;
import lol.tgformat.module.ModuleManager;
import lol.tgformat.module.ModuleType;
import lol.tgformat.module.impl.combat.KillAura;
import lol.tgformat.module.values.impl.BooleanSetting;
import lol.tgformat.module.values.impl.ModeSetting;
import lol.tgformat.utils.network.PacketUtil;
import lol.tgformat.utils.player.BlinkUtils;
import net.minecraft.item.*;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.S2FPacketSetSlot;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import tech.skidonion.obfuscator.annotations.Renamer;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import java.util.Objects;

/**
 * @author TG_format
 * @since 2024/6/1 7:41
 */
@Renamer

@StringEncryption
public class NoSlow extends Module {
    private final ModeSetting mode = new ModeSetting("Mode", "Vanilla","Vanilla", "Grim");
    private final BooleanSetting sword = new BooleanSetting("Sword", true);
    private final BooleanSetting food = new BooleanSetting("Food", false);
    private final BooleanSetting bow = new BooleanSetting("Bow", false);
    boolean slow;
    public NoSlow(){
        super("NoSlowDown",ModuleType.Movement);
    }
    private boolean isHoldingPotionAndSword(ItemStack stack) {
        if (stack == null) {
            return false;
        } else if (stack.getItem() instanceof ItemSword) {
            return true;
        } else if (stack.getItem() instanceof ItemBow) {
            return false;
        } else return ModuleManager.getModule(KillAura.class).isBlocking();
    }
    @Listener
    public void onSlowDown(SlowEvent event) {
        if (isGapple()) return;
        ItemStack itemStack = mc.thePlayer.getHeldItem();
        event.setCancelled(itemStack.getItem() instanceof ItemAppleGold && !itemStack.getItem().hasEffect(itemStack) && !this.slow || this.isHoldingPotionAndSword(mc.thePlayer.getHeldItem()));
        if (mc.thePlayer.isUsingItem() && mc.thePlayer.moveForward > 0.0F) {
            mc.thePlayer.setSprinting(true);
        }
    }

    @Listener
    public void onPre(PreMotionEvent event) {
        if (isGapple()) {
            return;
        };
        if (isNull()) return;
        this.setSuffix("Drop");
        if (this.slow && !mc.thePlayer.isUsingItem()) {
            this.slow = false;
        }
        if (Objects.requireNonNull(mode.getMode()).equals("Grim") && !isFood()) {
            if (isSlow() && !isFood()) {
                PacketUtil.sendPacket(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem % 8 + 1));
                PacketUtil.sendPacketNoEvent(new C17PacketCustomPayload("NoSlowPatcher", new PacketBuffer(Unpooled.buffer())));
                PacketUtil.sendPacket(new C09PacketHeldItemChange((mc.thePlayer.inventory.currentItem)));
            }
        }
    }
    @Listener
    public void onPost(PostMotionEvent event) {
        if (isGapple()) {
            PacketUtil.send1_12Block();
            return;
        }
        if (!isFood()) {
            if (mode.getMode().equals("Grim")) {
                if (isSlow() && !isFood() && !isBow()) {
                    if (mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword) {
                        PacketUtil.send1_12Block();
                    }
                }
            }
        }
    }

    @Listener
    public void onPacketReceive(PacketReceiveEvent event) {
        if (isGapple()) return;
        Packet<?> packet = event.getPacket();
        ItemStack itemStack = mc.thePlayer.getHeldItem();
        if (mc.thePlayer != null && mc.theWorld != null && mc.theWorld.isRemote && mc.thePlayer.getHeldItem() != null) {
            if (this.mode.is("Grim") && packet instanceof S2FPacketSetSlot s2f && itemStack.getItem() instanceof ItemAppleGold && !itemStack.getItem().hasEffect(itemStack)) {
                if (s2f.getWindowId() == 0 && s2f.getItem().getItem() instanceof ItemAppleGold) {
                    mc.thePlayer.inventory.getCurrentItem().stackSize = s2f.getItem().stackSize;
                    event.setCancelled(true);
                }
            }

        }
    }

    @Listener
    public void onPacketSend(PacketSendEvent event) {
        if (isGapple()) return;
        Packet<?> packet = event.getPacket();
        if (this.mode.is("Grim")) {
            if (mc.thePlayer == null || mc.theWorld == null || !mc.theWorld.isRemote || mc.thePlayer.getHeldItem() == null) {
                return;
            }

            ItemStack itemStack = mc.thePlayer.getHeldItem();
            if (itemStack != null && itemStack.getItem() instanceof ItemAppleGold && !itemStack.getItem().hasEffect(itemStack)) {
                if (packet instanceof C08PacketPlayerBlockPlacement && !this.slow) {
                    mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.DROP_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                    this.slow = true;
                }

                if (packet instanceof C07PacketPlayerDigging && ((C07PacketPlayerDigging)packet).getStatus() == C07PacketPlayerDigging.Action.RELEASE_USE_ITEM) {
                    event.setCancelled(true);
                    this.slow = true;
                }
            }
        }

    }


    public boolean isSlow(){
        if (isGapple()) return false;
        ItemStack heldItem = mc.thePlayer.getHeldItem();
        Item item = heldItem.getItem();
        return (mc.thePlayer.isUsingItem()) && ((item instanceof ItemSword && sword.isEnabled()) || (item instanceof ItemFood && food.isEnabled()) || (item instanceof ItemBow && bow.isEnabled()));
    }
}
