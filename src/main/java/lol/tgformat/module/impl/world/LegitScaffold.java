package lol.tgformat.module.impl.world;

import lol.tgformat.module.Module;
import lol.tgformat.module.ModuleType;
import lol.tgformat.utils.player.PlayerUtil;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

/**
 * @Author KuChaZi
 * @Date 2024/7/14 18:13
 * @ClassName: LegitScaffold
 */
public class LegitScaffold extends Module {
    public LegitScaffold() {
        super("LegitScaffold", ModuleType.World);
    }

    private void pickBlock() {
        for(int i = 8; i >= 0; i--) {
            ItemStack stack = mc.thePlayer.inventory.getStackInSlot(i);
            if(stack != null && stack.getItem() instanceof ItemBlock && !PlayerUtil.isBlockBlacklisted(stack.getItem()) && stack.stackSize > 0) {
                mc.thePlayer.inventory.currentItem = i;
                break;
            }
        }
    }

}
