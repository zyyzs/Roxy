package lol.tgformat.utils.player;

import com.google.common.collect.Multimap;
import lol.tgformat.accessable.IMinecraft;
import lol.tgformat.module.ModuleManager;
import lol.tgformat.module.impl.player.InvManager;
import lol.tgformat.utils.network.PacketUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.*;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C0EPacketClickWindow;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

/**
 * @author TG_format
 * @since 2024/6/1 1:07
 */
public class InventoryUtil implements IMinecraft {
    public static final int INCLUDE_ARMOR_BEGIN = 5;
    public static final int EXCLUDE_ARMOR_BEGIN = 9;
    public static final int ONLY_HOT_BAR_BEGIN = 36;
    public static final int END = 45;

    private InventoryUtil() {
    }
    public static boolean hasSpaceHotbar() {
        for (int i = 36; i < 45; ++i) {
            ItemStack itemStack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
            if (itemStack != null) continue;
            return true;
        }
        return false;
    }
    public static int findSlotMatching(final EntityPlayerSP player, final Predicate<ItemStack> cond) {
        for (int i = 44; i >= 9; --i) {
            final ItemStack stack = player.inventoryContainer.getSlot(i).getStack();
            if (cond.test(stack)) {
                return i;
            }
        }
        return -1;
    }
    public static int getItemFromHotbar(int id) {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.thePlayer.inventory.getStackInSlot(i);
            if (stack != null && Item.getIdFromItem(stack.getItem()) == id) {
                return i;
            }
        }
        return -1;
    }
    public static int findItem(int startSlot, int endSlot, Item item) {
        for (int i = startSlot; i < endSlot; ++i) {
            ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
            if (stack == null || stack.getItem() != item) continue;
            return i;
        }
        return -1;
    }
    public static float getSwordStrength(ItemStack stack) {
        if (stack.getItem() instanceof ItemSword sword) {
            float sharpness = (float)EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, stack) * 1.25F;
            float fireAspect = (float)EnchantmentHelper.getEnchantmentLevel(Enchantment.fireAspect.effectId, stack) * 1.5F;
            return sword.getDamageVsEntity() + sharpness + fireAspect;
        } else {
            return 0.0F;
        }
    }

    public static boolean hasFreeSlots(final EntityPlayerSP player) {
        for (int i = 9; i < 45; ++i) {
            if (!player.inventoryContainer.getSlot(i).getHasStack()) {
                return true;
            }
        }
        return false;
    }

    public static boolean isValidStack(final EntityPlayerSP player, final ItemStack stack) {
        if (stack == null) {
            return false;
        }
        final Item item = stack.getItem();
        if (item instanceof ItemSword) {
            return isBestSword(player, stack);
        }
        if (item instanceof ItemArmor) {
            return isBestArmor(player, stack);
        }
        if (item instanceof ItemTool) {
            return isBestTool(player, stack);
        }
        if (item instanceof ItemBow) {
            return isBestBow(player, stack);
        }
        if (item instanceof ItemFood) {
            return isGoodFood(stack);
        }
        if (item instanceof ItemBlock) {
            return isStackValidToPlace(stack);
        }
        if (item instanceof ItemPotion) {
            return isBuffPotion(stack);
        }
        return isGoodItem(item);
    }

    public static void swap(final int slot, final int switchSlot) {
        Minecraft.getMinecraft().playerController.windowClick(Minecraft.getMinecraft().thePlayer.inventoryContainer.windowId, slot, switchSlot, 2, (EntityPlayer)Minecraft.getMinecraft().thePlayer);
    }

    public static void click(int slot) {
        mc.playerController.windowClick(mc.thePlayer.openContainer.windowId, slot, 0, 1, mc.thePlayer);
    }

    public static void swapSilent(int slot, int switchSlot) {
        short short1 = mc.thePlayer.openContainer.getNextTransactionID(mc.thePlayer.inventory);
        PacketUtil.sendPacket(new C0EPacketClickWindow(mc.thePlayer.inventoryContainer.windowId, slot, switchSlot, 2, mc.thePlayer.inventory.getStackInSlot(slot), short1));
    }

    public static boolean isGoodItem(final Item item) {
        return item instanceof ItemEnderPearl || item == Items.snowball || item == Items.egg || item == Items.arrow || item == Items.lava_bucket || item == Items.water_bucket;
    }

    public static boolean isBestSword(final EntityPlayerSP player, final ItemStack itemStack) {
        double damage = 0.0;
        ItemStack bestStack = null;
        InvManager invManager = ModuleManager.getModule(InvManager.class);
        if (invManager.noWoodAndGold.isEnabled()) {
            if (itemStack.getItem() instanceof ItemSword sword && (sword.getMaterial().equals(Item.ToolMaterial.GOLD) || sword.getMaterial().equals(Item.ToolMaterial.WOOD))) {
                return false;
            }
        }
        for (int i = 9; i < 45; ++i) {
            final ItemStack stack = player.inventoryContainer.getSlot(i).getStack();
            if (stack != null && stack.getItem() instanceof ItemSword) {
                final double newDamage = getItemDamage(stack);
                if (newDamage > damage) {
                    damage = newDamage;
                    bestStack = stack;
                }
            }
        }
        return bestStack == itemStack || getItemDamage(itemStack) > damage;
    }

    public static boolean isBestArmor(final EntityPlayerSP player, final ItemStack itemStack) {
        final ItemArmor itemArmor = (ItemArmor)itemStack.getItem();
        double reduction = 0.0;
        ItemStack bestStack = null;
        for (int i = 5; i < 45; ++i) {
            final ItemStack stack = player.inventoryContainer.getSlot(i).getStack();
            if (stack != null && stack.getItem() instanceof ItemArmor && !stack.getItem().getUnlocalizedName().equalsIgnoreCase("item.helmetChain") && !stack.getItem().getUnlocalizedName().equalsIgnoreCase("item.leggingsChain")) {
                final ItemArmor stackArmor = (ItemArmor)stack.getItem();
                if (stackArmor.armorType == itemArmor.armorType) {
                    final double newReduction = getDamageReduction(stack);
                    if (newReduction > reduction) {
                        reduction = newReduction;
                        bestStack = stack;
                    }
                }
            }
        }
        return bestStack == itemStack || getDamageReduction(itemStack) > reduction;
    }

    public static int getToolType(final ItemStack stack) {
        final ItemTool tool = (ItemTool)stack.getItem();
        if (tool instanceof ItemPickaxe) {
            return 0;
        }
        if (tool instanceof ItemAxe) {
            return 1;
        }
        if (tool instanceof ItemSpade) {
            return 2;
        }
        return -1;
    }

    public static boolean isBestTool(final EntityPlayerSP player, final ItemStack itemStack) {
        final int type = getToolType(itemStack);
        Tool bestTool = new Tool(-1, -1.0, null);
        for (int i = 9; i < 45; ++i) {
            final ItemStack stack = player.inventoryContainer.getSlot(i).getStack();
            if (stack != null && stack.getItem() instanceof ItemTool && type == getToolType(stack)) {
                final double efficiency = getToolEfficiency(stack);
                if (efficiency > bestTool.getEfficiency()) {
                    bestTool = new Tool(i, efficiency, stack);
                }
            }
        }
        return bestTool.getStack() == itemStack || getToolEfficiency(itemStack) > bestTool.getEfficiency();
    }

    public static boolean isBestBow(final EntityPlayerSP player, final ItemStack itemStack) {
        double bestBowDmg = -1.0;
        ItemStack bestBow = null;
        for (int i = 9; i < 45; ++i) {
            final ItemStack stack = player.inventoryContainer.getSlot(i).getStack();
            if (stack != null && stack.getItem() instanceof ItemBow) {
                final double damage = getBowDamage(stack);
                if (damage > bestBowDmg) {
                    bestBow = stack;
                    bestBowDmg = damage;
                }
            }
        }
        return itemStack == bestBow || getBowDamage(itemStack) > bestBowDmg;
    }

    public static double getDamageReduction(final ItemStack stack) {
        double reduction = 0.0;
        final ItemArmor armor = (ItemArmor)stack.getItem();
        reduction += armor.damageReduceAmount;
        if (stack.isItemEnchanted()) {
            reduction += EnchantmentHelper.getEnchantmentLevel(Enchantment.protection.effectId, stack) * 0.25;
        }
        return reduction;
    }

    public static boolean isBuffPotion(final ItemStack stack) {
        final ItemPotion potion = (ItemPotion)stack.getItem();
        final List<PotionEffect> effects = (List<PotionEffect>)potion.getEffects(stack);
        for (final PotionEffect effect : effects) {
            if (Potion.potionTypes[effect.getPotionID()].isBadEffect()) {
                return false;
            }
        }
        return true;
    }

    public static double getBowDamage(final ItemStack stack) {
        double damage = 0.0;
        if (stack.getItem() instanceof ItemBow && stack.isItemEnchanted()) {
            damage += EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, stack);
        }
        return damage;
    }

    public static boolean isGoodFood(final ItemStack stack) {
        final ItemFood food = (ItemFood)stack.getItem();
        return food instanceof ItemAppleGold || (food.getHealAmount(stack) >= 4 && food.getSaturationModifier(stack) >= 0.3f);
    }

    public static float getToolEfficiency(final ItemStack itemStack) {
        final ItemTool tool = (ItemTool)itemStack.getItem();
        float efficiency = tool.getToolMaterial().getEfficiencyOnProperMaterial();
        final int lvl = EnchantmentHelper.getEnchantmentLevel(Enchantment.efficiency.effectId, itemStack);
        if (efficiency > 1.0f && lvl > 0) {
            efficiency += lvl * lvl + 1;
        }
        return efficiency;
    }

    public static double getItemDamage(final ItemStack stack) {
        double damage = 0.0;
        final Multimap<String, AttributeModifier> attributeModifierMap = (Multimap<String, AttributeModifier>)stack.getAttributeModifiers();
        for (final String attributeName : attributeModifierMap.keySet()) {
            if (attributeName.equals("generic.attackDamage")) {
                final Iterator<AttributeModifier> attributeModifiers = attributeModifierMap.get(attributeName).iterator();
                if (attributeModifiers.hasNext()) {
                    damage += attributeModifiers.next().getAmount();
                    break;
                }
                break;
            }
        }
        if (stack.isItemEnchanted()) {
            damage += EnchantmentHelper.getEnchantmentLevel(Enchantment.fireAspect.effectId, stack);
            damage += EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, stack) * 1.25;
        }
        return damage;
    }

    public static void windowClick(final Minecraft mc, final int windowId, final int slotId, final int mouseButtonClicked, final ClickType mode) {
        mc.playerController.windowClick(windowId, slotId, mouseButtonClicked, mode.ordinal(), mc.thePlayer);
    }

    public static void windowClick(final Minecraft mc, final int slotId, final int mouseButtonClicked, final ClickType mode) {
        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slotId, mouseButtonClicked, mode.ordinal(), mc.thePlayer);
    }

    public static boolean isStackValidToPlace(final ItemStack stack) {
        return stack.stackSize >= 1 && validateBlock(Block.getBlockFromItem(stack.getItem()), BlockAction.PLACE);
    }

    public static boolean validateBlock(final Block block, final BlockAction action) {
        if (block instanceof BlockContainer) {
            return false;
        }
        final Material material = block.getMaterial();
        return switch (action) {
            case PLACE -> !(block instanceof BlockFalling) && block.isFullBlock() && block.isFullCube();
            case REPLACE -> material.isReplaceable();
            case PLACE_ON -> block.isFullBlock() && block.isFullCube();
        };
    }


    public enum BlockAction
    {
        PLACE,
        REPLACE,
        PLACE_ON;
    }

    public enum ClickType
    {
        CLICK,
        SHIFT_CLICK,
        SWAP_WITH_HOT_BAR_SLOT,
        PLACEHOLDER,
        DROP_ITEM;
    }

    private static class Tool
    {
        private final int slot;
        private final double efficiency;
        private final ItemStack stack;

        public Tool(final int slot, final double efficiency, final ItemStack stack) {
            this.slot = slot;
            this.efficiency = efficiency;
            this.stack = stack;
        }

        public int getSlot() {
            return this.slot;
        }

        public double getEfficiency() {
            return this.efficiency;
        }

        public ItemStack getStack() {
            return this.stack;
        }
    }
    public static int getGappleSlot()
    {
        int item = -1;

        if(mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemAppleGold)return mc.thePlayer.inventory.currentItem;
        for (int i = 36; i < 45; ++i)
        {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getStack() != null && mc.thePlayer.inventoryContainer.getSlot(i).getStack().getItem() instanceof ItemAppleGold)
            {
                item = i - 36;
            }
        }

        return item;
    }
}
