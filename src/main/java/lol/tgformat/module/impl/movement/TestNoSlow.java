package lol.tgformat.module.impl.movement;

import com.viaversion.viarewind.protocol.protocol1_8to1_9.Protocol1_8To1_9;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.type.Type;
import lol.tgformat.api.event.Listener;
import lol.tgformat.events.PreUpdateEvent;
import lol.tgformat.events.WorldChangeEvent;
import lol.tgformat.events.motion.PostMotionEvent;
import lol.tgformat.events.movement.SlowEvent;
import lol.tgformat.events.packet.PacketSendEvent;
import lol.tgformat.module.Module;
import lol.tgformat.module.ModuleType;
import lol.tgformat.module.values.impl.BooleanSetting;
import lol.tgformat.utils.network.PacketUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0EPacketClickWindow;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import java.util.Iterator;

/**
 * @Author KuChaZi
 * @Date 2024/6/28 16:32
 * @ClassName: TestNoSlow
 */
@StringEncryption
public class TestNoSlow extends Module {
    private final BooleanSetting sword = new BooleanSetting("Sword", true);
    private final BooleanSetting food = new BooleanSetting("Food", false);
    private final BooleanSetting bow = new BooleanSetting("Bow", false);
    public TestNoSlow() {
        super("NoSlow", ModuleType.Movement);
    }

    public boolean dropped = false;

    @Listener
    public void onSlow(SlowEvent event) {
        if (isNull()) return;
        if (isSlow()) {
            event.setCancelled(true);
        }
        mc.thePlayer.setSprinting(true);
    }

    @Listener
    public void onWorld(WorldChangeEvent event){
        if (isNull()) return;

        dropped = false;
    }

    @Listener
    public void onUpdate(PreUpdateEvent event) {
        if (isSlow()) {
            if (!isBow() && !isFood()) {
                PacketUtil.sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                PacketUtil.sendPacket(new C0EPacketClickWindow(0, 36, 0, 2, new ItemStack(Block.getBlockById(166)), (short)0));
            }
            if (isBow()) {
                PacketUtil.sendPacket(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem % 8 + 1));
                PacketUtil.sendPacket(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
                PacketUtil.sendPacket(new C09PacketHeldItemChange((mc.thePlayer.inventory.currentItem)));
            }
        }
        if (dropped) {
            mc.gameSettings.keyBindUseItem.setPressed(false);
        }

        if (!mc.thePlayer.isUsingItem()) {
            dropped = false;
        }
    }

    @Listener
    public void onPostMotion(PostMotionEvent e) {
        if (isSlow() && !isFood()) {
            PacketUtil.sendPacket(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
            mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, mc.thePlayer.getHeldItem());
            if (mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword && mc.thePlayer.isUsingItem()) {
                for (int i = 0; i < 8; i++) {
                    PacketUtil.send1_12Block();
                }
            }
        }
    }
    @Listener
    public void onPacketSend(PacketSendEvent event){
        Packet<?> packet = event.getPacket();
        if (packet instanceof C08PacketPlayerBlockPlacement wrapper) {
            if (food.isEnabled()) {
                if (wrapper.getStack() != null && !(mc.theWorld.getBlockState(wrapper.getPosition()).getBlock() instanceof BlockContainer)) {
                    if (wrapper.getStack().getItem() instanceof ItemFood && !dropped) {
                        if (wrapper.getStack().getStackSize() > 1) {
                            event.setCancelled(true);
                            PacketUtil.sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.DROP_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                            PacketUtil.sendPacketNoEvent(new C08PacketPlayerBlockPlacement(wrapper.getStack()));
                            dropped = true;
                        }
                    }
                }
            }
        }
        if (food.isEnabled()) {
            if (dropped) {
                if (packet instanceof C07PacketPlayerDigging wrapper) {
                    if (wrapper.getStatus() == C07PacketPlayerDigging.Action.RELEASE_USE_ITEM) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    public boolean isSlow(){
        ItemStack heldItem = mc.thePlayer.getHeldItem();
        Item item = heldItem.getItem();
        return (mc.thePlayer.isUsingItem()) && ((item instanceof ItemSword && sword.isEnabled()) || (item instanceof ItemFood && food.isEnabled()) || (item instanceof ItemBow && bow.isEnabled()));
    }

}
