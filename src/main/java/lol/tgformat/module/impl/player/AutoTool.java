package lol.tgformat.module.impl.player;

import lol.tgformat.Client;
import lol.tgformat.api.event.Listener;
import lol.tgformat.events.PreUpdateEvent;
import lol.tgformat.events.TickEvent;
import lol.tgformat.module.Module;
import lol.tgformat.module.ModuleType;
import lol.tgformat.module.values.impl.BooleanSetting;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import org.lwjgl.input.Mouse;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;
import tech.skidonion.obfuscator.annotations.Renamer;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import java.util.Objects;

/**
 * @Author KuChaZi
 * @Date 2024/7/7 17:03
 * @ClassName: AutoTool
 */
@Renamer
@StringEncryption
public class AutoTool extends Module {
    private final BooleanSetting spoof = new BooleanSetting("Item spoof", true);
    private int oldSlot;
    private boolean wasDigging;

    public AutoTool() {
        super("AutoTool", ModuleType.Player);
    }

    @Override
    public void onDisable() {
        if (this.wasDigging) {
            AutoTool.mc.thePlayer.inventory.currentItem = this.oldSlot;
            this.wasDigging = false;
        }
        Client.instance.getSlotSpoofComponent().stopSpoofing();
    }

    @Listener(value=3)
    public void onTick(TickEvent event) {
        if ((Mouse.isButtonDown((int)0) || AutoTool.mc.gameSettings.keyBindAttack.isKeyDown()) && AutoTool.mc.objectMouseOver != null && AutoTool.mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
            Block block = AutoTool.mc.theWorld.getBlockState(AutoTool.mc.objectMouseOver.getBlockPos()).getBlock();
            float strength = 0.0f;
            if (!this.wasDigging) {
                this.oldSlot = AutoTool.mc.thePlayer.inventory.currentItem;
                if (this.spoof.isEnabled()) {
                    Client.instance.getSlotSpoofComponent().startSpoofing(this.oldSlot);
                }
            }
            for (int i = 0; i <= 8; ++i) {
                float slotStrength;
                ItemStack stack = AutoTool.mc.thePlayer.inventory.getStackInSlot(i);
                if (stack == null || !((slotStrength = stack.getStrVsBlock(block)) > strength)) continue;
                AutoTool.mc.thePlayer.inventory.currentItem = i;
                strength = slotStrength;
            }
            this.wasDigging = true;
        } else if (this.wasDigging) {
            AutoTool.mc.thePlayer.inventory.currentItem = this.oldSlot;
            Client.instance.getSlotSpoofComponent().stopSpoofing();
            this.wasDigging = false;
        } else {
            this.oldSlot = AutoTool.mc.thePlayer.inventory.currentItem;
        }
    }
}
