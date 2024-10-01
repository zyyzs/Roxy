package lol.tgformat.module.impl.player;

import lol.tgformat.api.event.Listener;
import lol.tgformat.events.motion.PreMotionEvent;
import lol.tgformat.events.packet.PacketSendEvent;
import lol.tgformat.events.render.Render2DEvent;
import lol.tgformat.module.Module;
import lol.tgformat.module.ModuleType;
import lol.tgformat.module.values.impl.BooleanSetting;
import lol.tgformat.module.values.impl.NumberSetting;
import lol.tgformat.utils.math.MathUtil;
import lol.tgformat.utils.player.InventoryUtil;
import lol.tgformat.utils.vector.Vector3d;
import lol.tgformat.utils.vector.Vector4d;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.init.Items;
import net.minecraft.inventory.*;
import net.minecraft.inventory.Container;
import net.minecraft.item.*;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.BlockPos;
import net.netease.utils.RoundedUtils;
import net.netease.utils.TimeUtil;
import org.apache.commons.lang3.ArrayUtils;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.glu.GLU;
import tech.skidonion.obfuscator.annotations.Renamer;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import java.awt.*;
import java.util.List;
import java.util.Optional;
@Renamer
@StringEncryption
public class Stealer extends Module {
    private final NumberSetting delay = new NumberSetting("Delay", 0.0, 1000.0, 0.0, 10.0);
    public BooleanSetting silent = new BooleanSetting("Silent", true);
    public final BooleanSetting chestView = new BooleanSetting("Stealing View", false);
    private final BooleanSetting autodis = new BooleanSetting("AutoDisable", true);

    private int nextDelay;
    static TimeUtil timer;
    static TimeUtil openChestTimer;
    private BlockPos currentContainerPos;
    public static int willTake = 0;
    public static ContainerChest lastChest;
    private static final int[] itemHelmet;
    private static final int[] itemChestPlate;
    private static final int[] itemLeggings;
    private static final int[] itemBoots;

    public Stealer() {
        super("Stealer", ModuleType.Player);
        this.nextDelay = 0;
    }

    @Listener
    public void onRender2D(Render2DEvent event) {
        if (chestView.isEnabled()) {
            if (mc.thePlayer.openContainer == null || mc.currentScreen == null) return;
            Container container = mc.thePlayer.openContainer;
            if (!(container instanceof ContainerChest || container instanceof ContainerFurnace || container instanceof ContainerBeacon || container instanceof ContainerDispenser || container instanceof ContainerHopper || container instanceof ContainerHorseInventory || container instanceof ContainerBrewingStand)) {
                return;
            }
            int slots = container.inventorySlots.size();

            int scaleFactor = event.getScaledResolution().getScaleFactor();

            if (slots > 0) {
                Vector4d projection = calculate(currentContainerPos, scaleFactor);
                if (projection == null) return;

                float roundX = (float) projection.x - (164 / 2F);
                float roundY = (float) projection.y / 1.5F;

                GlStateManager.pushMatrix();
                GlStateManager.translate(roundX + 82, roundY + 30, 0);
                GlStateManager.translate(-(roundX + 82), -(roundY + 30), 0);

                RoundedUtils.drawRound(roundX, roundY, 164, 60, 3, new Color(0, 0, 0, 120));

                double startX = roundX + 5;
                double startY = roundY + 5;

                RenderItem itemRender = mc.getRenderItem();

                GlStateManager.pushMatrix();
                RenderHelper.enableGUIStandardItemLighting();
                itemRender.zLevel = 200.0F;

                for (Slot slot : container.inventorySlots) {
                    if (!slot.inventory.equals(mc.thePlayer.inventory)) {
                        int x = (int) (startX + (slot.slotNumber % 9) * 18);
                        int y = (int) (startY + ((double) slot.slotNumber / 9) * 18);

                        itemRender.renderItemAndEffectIntoGUI(slot.getStack(), x, y);
                    }
                }
                GlStateManager.popMatrix();

                itemRender.zLevel = 0.0F;
                GlStateManager.popMatrix();
                GlStateManager.disableLighting();
            }
        }
    }

    @Listener
    public void onSend(PacketSendEvent event) {
        if (chestView.isEnabled()) {
            Packet<?> packet = event.getPacket();
            if (packet instanceof C08PacketPlayerBlockPlacement wrapper) {
                if (wrapper.getPosition() != null) {
                    Block block = mc.theWorld.getBlockState(wrapper.getPosition()).getBlock();
                    if (block instanceof BlockContainer) {
                        currentContainerPos = wrapper.getPosition();
                    }
                }
            }
        }
    }

    @Listener
    public void onMotion(PreMotionEvent event) {
        if (isGapple()) return;
        if(isNull())return;
        if(mc.thePlayer.ticksExisted < 10 && autodis.isEnabled()) {
            this.setState(false);
        }
        if (mc.thePlayer.openContainer == null) {
            willTake = 0;
            return;
        }
        if (mc.thePlayer.openContainer instanceof ContainerFurnace containerFurnace) {
            if (this.isFurnaceEmpty(containerFurnace) && openChestTimer.delay(100.0f) && timer.delay(100.0f)) {
                mc.thePlayer.closeScreen();
                return;
            }
            for (int i = 0; i < containerFurnace.tileFurnace.getSizeInventory(); ++i) {
                if (containerFurnace.tileFurnace.getStackInSlot(i) != null && timer.delay((float)this.nextDelay)) {
                    mc.playerController.windowClick(containerFurnace.windowId, i, 0, 1, mc.thePlayer);
                    this.nextDelay = (int)(this.delay.getValue() * MathUtil.getRandomInRange(0.75,1.25));
                    timer.reset();
                }
            }
        }
        if (mc.thePlayer.openContainer instanceof ContainerBrewingStand brewingStand) {
            for (int i = 0; i < 3; ++i) {
                InventoryUtil.windowClick(mc, brewingStand.windowId ,i , 0, InventoryUtil.ClickType.SHIFT_CLICK);
            }
            if (openChestTimer.delay(100.0f)) {
                mc.thePlayer.closeScreen();
                return;
            }
        }
        if (mc.thePlayer.openContainer instanceof ContainerChest chest) {
            if (lastChest == null || lastChest != chest) {
                lastChest = chest;
                willTake = itemWillTake(chest);
            }
            if (this.isChestEmpty(chest) && openChestTimer.delay(100.0f) && timer.delay(100.0f)) {
                mc.thePlayer.closeScreen();
                willTake = 0;
                return;
            }
            for (int i = 0; i < chest.getLowerChestInventory().getSizeInventory(); ++i) {
                if (chest.getLowerChestInventory().getStackInSlot(i) != null && timer.delay((float)this.nextDelay) && (isItemUseful(chest, i))) {
                    mc.playerController.windowClick(chest.windowId, i, 0, 1, mc.thePlayer);
                    this.nextDelay = (int)(this.delay.getValue() * MathUtil.getRandomInRange(0.75,1.25));
                    timer.reset();
                }
            }
        }

    }

    private boolean isChestEmpty(ContainerChest c) {
        for (int i = 0; i < c.getLowerChestInventory().getSizeInventory(); ++i) {
            if (c.getLowerChestInventory().getStackInSlot(i) != null && (isItemUseful(c, i))) {
                return false;
            }
        }
        return true;
    }

    private boolean isFurnaceEmpty(ContainerFurnace c) {
        for (int i = 0; i < c.tileFurnace.getSizeInventory(); ++i) {
            if (c.tileFurnace.getStackInSlot(i) != null) {
                return false;
            }
        }
        return true;
    }

    private static boolean isItemUseful(ContainerChest c, int i) {
        ItemStack itemStack = c.getLowerChestInventory().getStackInSlot(i);
        Item item = itemStack.getItem();
        return item instanceof ItemAxe || item instanceof ItemPickaxe || item instanceof ItemFood || (item instanceof ItemBow || item == Items.arrow) || (item instanceof ItemPotion && !isPotionNegative(itemStack)) || item instanceof ItemSword || (item instanceof ItemArmor && isBestArmor(c, itemStack)) || item instanceof ItemBlock || item instanceof ItemEnderPearl || item instanceof ItemSnowball || item instanceof ItemEgg;
    }
    public static int itemWillTake(ContainerChest container) {
        int items = 0;
        for (int i = 0; i < container.getLowerChestInventory().getSizeInventory(); i++) {
            if (container.getLowerChestInventory().getStackInSlot(i) != null && isItemUseful(container, i)) {
                items++;
            }
        }
        return items;
    }
    public static boolean isPotionNegative(ItemStack itemStack) {
        ItemPotion potion = (ItemPotion)itemStack.getItem();
        List<PotionEffect> potionEffectList = potion.getEffects(itemStack);
        return potionEffectList.stream().map(potionEffect -> Potion.potionTypes[potionEffect.getPotionID()]).anyMatch(Potion::isBadEffect);
    }
    public static boolean isBestArmor(ContainerChest c, ItemStack item) {
        float itempro1 = (float)((ItemArmor)item.getItem()).damageReduceAmount;
        float itempro2 = 0.0f;
        if (isContain(itemHelmet, Item.getIdFromItem(item.getItem()))) {
            for (int i = 0; i < 45; ++i) {
                if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack() && isContain(itemHelmet, Item.getIdFromItem(mc.thePlayer.inventoryContainer.getSlot(i).getStack().getItem()))) {
                    float temppro = (float)((ItemArmor)mc.thePlayer.inventoryContainer.getSlot(i).getStack().getItem()).damageReduceAmount;
                    if (temppro > itempro2) {
                        itempro2 = temppro;
                    }
                }
            }
            for (int i = 0; i < c.getLowerChestInventory().getSizeInventory(); ++i) {
                if (c.getLowerChestInventory().getStackInSlot(i) != null && isContain(itemHelmet, Item.getIdFromItem(c.getLowerChestInventory().getStackInSlot(i).getItem()))) {
                    float temppro = (float)((ItemArmor)c.getLowerChestInventory().getStackInSlot(i).getItem()).damageReduceAmount;
                    if (temppro > itempro2) {
                        itempro2 = temppro;
                    }
                }
            }
        }
        if (isContain(itemChestPlate, Item.getIdFromItem(item.getItem()))) {
            for (int i = 0; i < 45; ++i) {
                if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack() && isContain(itemChestPlate, Item.getIdFromItem(mc.thePlayer.inventoryContainer.getSlot(i).getStack().getItem()))) {
                    float temppro = (float)((ItemArmor)mc.thePlayer.inventoryContainer.getSlot(i).getStack().getItem()).damageReduceAmount;
                    if (temppro > itempro2) {
                        itempro2 = temppro;
                    }
                }
            }
            for (int i = 0; i < c.getLowerChestInventory().getSizeInventory(); ++i) {
                if (c.getLowerChestInventory().getStackInSlot(i) != null && isContain(itemChestPlate, Item.getIdFromItem(c.getLowerChestInventory().getStackInSlot(i).getItem()))) {
                    float temppro = (float)((ItemArmor)c.getLowerChestInventory().getStackInSlot(i).getItem()).damageReduceAmount;
                    if (temppro > itempro2) {
                        itempro2 = temppro;
                    }
                }
            }
        }
        if (isContain(itemLeggings, Item.getIdFromItem(item.getItem()))) {
            for (int i = 0; i < 45; ++i) {
                if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack() && isContain(itemLeggings, Item.getIdFromItem(mc.thePlayer.inventoryContainer.getSlot(i).getStack().getItem()))) {
                    float temppro = (float)((ItemArmor)mc.thePlayer.inventoryContainer.getSlot(i).getStack().getItem()).damageReduceAmount;
                    if (temppro > itempro2) {
                        itempro2 = temppro;
                    }
                }
            }
            for (int i = 0; i < c.getLowerChestInventory().getSizeInventory(); ++i) {
                if (c.getLowerChestInventory().getStackInSlot(i) != null && isContain(itemLeggings, Item.getIdFromItem(c.getLowerChestInventory().getStackInSlot(i).getItem()))) {
                    float temppro = (float)((ItemArmor)c.getLowerChestInventory().getStackInSlot(i).getItem()).damageReduceAmount;
                    if (temppro > itempro2) {
                        itempro2 = temppro;
                    }
                }
            }
        }
        if (isContain(itemBoots, Item.getIdFromItem(item.getItem()))) {
            for (int i = 0; i < 45; ++i) {
                if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack() && isContain(itemBoots, Item.getIdFromItem(mc.thePlayer.inventoryContainer.getSlot(i).getStack().getItem()))) {
                    float temppro = (float)((ItemArmor)mc.thePlayer.inventoryContainer.getSlot(i).getStack().getItem()).damageReduceAmount;
                    if (temppro > itempro2) {
                        itempro2 = temppro;
                    }
                }
            }
            for (int i = 0; i < c.getLowerChestInventory().getSizeInventory(); ++i) {
                if (c.getLowerChestInventory().getStackInSlot(i) != null && isContain(itemBoots, Item.getIdFromItem(c.getLowerChestInventory().getStackInSlot(i).getItem()))) {
                    float temppro = (float)((ItemArmor)c.getLowerChestInventory().getStackInSlot(i).getItem()).damageReduceAmount;
                    if (temppro > itempro2) {
                        itempro2 = temppro;
                    }
                }
            }
        }
        return itempro1 == itempro2;
    }
    public static boolean isContain(int[] arr, int targetValue) {
        return ArrayUtils.contains(arr, targetValue);
    }

    public Vector4d calculate(BlockPos blockPos, int factor) {
        try {
            double renderX = RenderManager.getRenderPosX();
            double renderY = RenderManager.getRenderPosY();
            double renderZ = RenderManager.getRenderPosZ();

            double x = blockPos.getX() + 0.5 - renderX;
            double y = blockPos.getY() + 0.5 - renderY;
            double z = blockPos.getZ() + 0.5 - renderZ;

            Vector3d projectedCenter = project(x, y, z, factor);
            if (projectedCenter != null && projectedCenter.getZ() >= 0.0D && projectedCenter.getZ() < 1.0D) {
                return new Vector4d(projectedCenter.getX(), projectedCenter.getY(), projectedCenter.getX(), projectedCenter.getY());
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    private static Vector3d project(double x, double y, double z, int factor) {
        if (GLU.gluProject((float) x, (float) y, (float) z, ActiveRenderInfo.MODELVIEW, ActiveRenderInfo.PROJECTION, ActiveRenderInfo.VIEWPORT, ActiveRenderInfo.OBJECTCOORDS)) {
            return new Vector3d((ActiveRenderInfo.OBJECTCOORDS.get(0) / factor), ((Display.getHeight() - ActiveRenderInfo.OBJECTCOORDS.get(1)) / factor), ActiveRenderInfo.OBJECTCOORDS.get(2));
        }
        return null;
    }

    static {
        timer = new TimeUtil();
        openChestTimer = new TimeUtil();
        itemHelmet = new int[] { 298, 302, 306, 310, 314 };
        itemChestPlate = new int[] { 299, 303, 307, 311, 315 };
        itemLeggings = new int[] { 300, 304, 308, 312, 316 };
        itemBoots = new int[] { 301, 305, 309, 313, 317 };
    }
}
