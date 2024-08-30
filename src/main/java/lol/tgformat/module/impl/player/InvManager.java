package lol.tgformat.module.impl.player;

import lol.tgformat.api.event.Listener;
import lol.tgformat.events.TickEvent;
import lol.tgformat.events.motion.PostMotionEvent;
import lol.tgformat.events.packet.PacketReceiveEvent;
import lol.tgformat.events.packet.PacketSendEvent;
import lol.tgformat.module.Module;
import lol.tgformat.module.ModuleManager;
import lol.tgformat.module.ModuleType;
import lol.tgformat.module.impl.world.Scaffold;
import lol.tgformat.module.values.impl.BooleanSetting;
import lol.tgformat.module.values.impl.NumberSetting;
import lol.tgformat.utils.player.InventoryUtil;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.*;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C0EPacketClickWindow;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.network.play.server.S2DPacketOpenWindow;
import org.lwjgl.input.Keyboard;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;
import tech.skidonion.obfuscator.annotations.Renamer;
import tech.skidonion.obfuscator.annotations.StringEncryption;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
@Renamer

@StringEncryption
public class InvManager extends Module {
    private final NumberSetting delay = new NumberSetting("Delay", 0.0, 300.0, 0.0, 10.0);
    public final BooleanSetting instant = new BooleanSetting("Instant",false);
    public final BooleanSetting openinv = new BooleanSetting("OpenInv",false);
    private final BooleanSetting autodis = new BooleanSetting("AutoDisable", true);
    public String[] serverItems;
    private final int[] bestArmorPieces;
    private final List<Integer> trash;
    private final int[] bestToolSlots;
    private final List<Integer> gappleStackSlots;
    private int bestSwordSlot;
    private int bestsbSlot;
    private int bestPearlSlot;
    private int bestBowSlot;
    private boolean serverOpen;
    private boolean clientOpen;
    private boolean nextTickCloseInventory;
    private int ticksSinceLastClick;

    public InvManager() {
        super("InvManager", ModuleType.Player);
        this.serverItems = new String[] { "选择游戏",
                "加入游戏", "职业选择菜单", "离开对局",
                "再来一局", "selector", "tracking compass",
                "(right click)", "tienda ", "perfil", "salir",
                "shop", "collectibles", "game", "profil", "lobby",
                "show all", "hub", "friends only", "cofre", "(click",
                "teleport", "play", "exit", "hide all", "jeux", "gadget",
                " (activ", "emote", "amis", "bountique", "choisir", "choose " };
        this.bestArmorPieces = new int[4];
        this.trash = new ArrayList<>();
        this.bestToolSlots = new int[3];
        this.gappleStackSlots = new ArrayList<>();
    }

    @Listener
    public void onTick(TickEvent event){
        if (mc.thePlayer == null || mc.theWorld == null) return;
        if (mc.thePlayer.ticksExisted < 10 && autodis.isEnabled()) {
            this.setState(false);
        }
    }

    @Listener
    private void onPacket(PacketReceiveEvent event) {
        if (isGapple()) return;
        Packet<?> packet = (Packet<?>)event.getPacket();
        if (packet instanceof S2DPacketOpenWindow) {
            this.clientOpen = false;
            this.serverOpen = false;
        }
    }

    @Listener
    private void onPacketSend(PacketSendEvent event) {
        if (isGapple()) return;
        Packet<?> packet = (Packet<?>)event.getPacket();
        if (packet instanceof C16PacketClientStatus clientStatus) {
            if (clientStatus.getStatus() == C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT) {
                this.clientOpen = true;
                this.serverOpen = true;
            }
        }
        else if (packet instanceof C0DPacketCloseWindow packetCloseWindow) {
            if (packetCloseWindow.windowId == mc.thePlayer.inventoryContainer.windowId) {
                this.clientOpen = false;
                this.serverOpen = false;
            }
        }
        else if (packet instanceof C0EPacketClickWindow && !mc.thePlayer.isUsingItem()) {
            this.ticksSinceLastClick = 0;
        }
    }

    private boolean dropItem(List<Integer> listOfSlots) {
        if (!listOfSlots.isEmpty()) {
            int slot = listOfSlots.removeFirst();
            InventoryUtil.windowClick(mc, slot, 1, InventoryUtil.ClickType.DROP_ITEM);
            return true;
        }
        return false;
    }

    @Listener
    
    private void onMotion(PostMotionEvent event) {
        if (isGapple()) return;
        if (!mc.thePlayer.isUsingItem() && (mc.currentScreen == null || mc.currentScreen instanceof GuiChat || mc.currentScreen instanceof GuiInventory || mc.currentScreen instanceof GuiIngameMenu)) {
            ++this.ticksSinceLastClick;
            if (this.ticksSinceLastClick < Math.floor(this.delay.getValue() / 50.0) && !instant.isEnabled()) {
                return;
            }
            if (this.clientOpen || (mc.currentScreen == null && !openinv.isEnabled())) {
                this.clear();
                for (int slot = 5; slot < 45; ++slot) {
                    ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(slot).getStack();
                    if (stack != null) {
                        if (stack.getItem() instanceof ItemSnowball || stack.getItem() instanceof ItemEgg) {
                            this.bestsbSlot = slot;
                        }
                        if (stack.getItem() instanceof ItemSword && InventoryUtil.isBestSword(mc.thePlayer, stack)) {
                            this.bestSwordSlot = slot;
                        }
                        else if (stack.getItem() instanceof ItemTool && InventoryUtil.isBestTool(mc.thePlayer, stack)) {
                            int toolType = InventoryUtil.getToolType(stack);
                            if (toolType != -1 && slot != this.bestToolSlots[toolType]) {
                                this.bestToolSlots[toolType] = slot;
                            }
                        }
                        else if (stack.getItem() instanceof ItemArmor armor && InventoryUtil.isBestArmor(mc.thePlayer, stack)) {
                            int pieceSlot = this.bestArmorPieces[armor.armorType];
                            if (pieceSlot == -1 || slot != pieceSlot) {
                                this.bestArmorPieces[armor.armorType] = slot;
                            }
                        }
                        else if (stack.getItem() instanceof ItemBow && InventoryUtil.isBestBow(mc.thePlayer, stack)) {
                            if (slot != this.bestBowSlot) {
                                this.bestBowSlot = slot;
                            }
                        }
                        else if (stack.getItem() instanceof ItemAppleGold) {
                            this.gappleStackSlots.add(slot);
                        }
                        else if (stack.getItem() instanceof ItemEnderPearl) {
                            this.bestPearlSlot = slot;
                        }
                        else if (!this.trash.contains(slot) && !isValidStack(stack)) {
                            if (Arrays.stream(this.serverItems).noneMatch(stack.getDisplayName()::contains)) {
                                this.trash.add(slot);
                            }
                        }
                    }
                }
                boolean busy = !this.trash.isEmpty() || this.equipArmor(false) || this.sortItems(false) || ModuleManager.getModule(Scaffold.class).isState();
                if (!busy) {
                    if (this.nextTickCloseInventory) {
                        this.close();
                        this.nextTickCloseInventory = false;
                    }
                    else {
                        this.nextTickCloseInventory = true;
                    }
                    return;
                }
                boolean waitUntilNextTick = !this.serverOpen;
                this.open();
                if (this.nextTickCloseInventory) {
                    this.nextTickCloseInventory = false;
                }
                if (waitUntilNextTick) {
                    return;
                }
                if (this.equipArmor(true)) {
                    return;
                }
                if (this.dropItem(this.trash)) {
                    return;
                }
                this.sortItems(true);
            }
        }
    }

    private boolean sortItems(boolean moveItems) {
        int goodsbSlot = 9 + 35;
        if (this.bestsbSlot != -1 && this.bestsbSlot != goodsbSlot) {
            if (moveItems) {
                this.putItemInSlot(goodsbSlot, this.bestsbSlot);
                this.bestsbSlot = goodsbSlot;
            }
            return true;
        }
        int goodSwordSlot = 1 + 35;
        if (this.bestSwordSlot != -1 && this.bestSwordSlot != goodSwordSlot) {
            if (moveItems) {
                this.putItemInSlot(goodSwordSlot, this.bestSwordSlot);
                this.bestSwordSlot = goodSwordSlot;
            }
            return true;
        }
        int goodBowSlot = 5 + 35;
        if (this.bestBowSlot != -1 && this.bestBowSlot != goodBowSlot) {
            if (moveItems) {
                this.putItemInSlot(goodBowSlot, this.bestBowSlot);
                this.bestBowSlot = goodBowSlot;
            }
            return true;
        }
        int goodGappleSlot = 3 + 35;
        if (!this.gappleStackSlots.isEmpty()) {
            this.gappleStackSlots.sort(Comparator.comparingInt(slot -> mc.thePlayer.inventoryContainer.getSlot(slot).getStack().stackSize));
            int bestGappleSlot = this.gappleStackSlots.getFirst();
            if (bestGappleSlot != goodGappleSlot) {
                if (moveItems) {
                    this.putItemInSlot(goodGappleSlot, bestGappleSlot);
                    this.gappleStackSlots.set(0, goodGappleSlot);
                }
                return true;
            }
        }
        int[] toolSlots = { 4 + 35, 6 + 35, 7 + 35 };
        for (int toolSlot : this.bestToolSlots) {
            if (toolSlot != -1) {
                int type = InventoryUtil.getToolType(mc.thePlayer.inventoryContainer.getSlot(toolSlot).getStack());
                if (type != -1 && toolSlot != toolSlots[type]) {
                    if (moveItems) {
                        this.putToolsInSlot(type, toolSlots);
                    }
                    return true;
                }
            }
        }
        int goodBlockSlot = 2 + 35;
        int mostBlocksSlot = this.getMostBlocks();
        if (mostBlocksSlot != -1 && mostBlocksSlot != goodBlockSlot) {
            Slot dss = mc.thePlayer.inventoryContainer.getSlot(goodBlockSlot);
            ItemStack dsis = dss.getStack();
            if (dsis == null || !(dsis.getItem() instanceof ItemBlock) || dsis.stackSize < mc.thePlayer.inventoryContainer.getSlot(mostBlocksSlot).getStack().stackSize || !Arrays.stream(this.serverItems).noneMatch(dsis.getDisplayName().toLowerCase()::contains)) {
                this.putItemInSlot(goodBlockSlot, mostBlocksSlot);
            }
        }
        int goodPearlSlot = 8 + 35;
        if (this.bestPearlSlot != -1 && this.bestPearlSlot != goodPearlSlot) {
            if (moveItems) {
                this.putItemInSlot(goodPearlSlot, this.bestPearlSlot);
                this.bestPearlSlot = goodPearlSlot;
            }
            return true;
        }
        return false;
    }

    public int getMostBlocks() {
        int stack = 0;
        int biggestSlot = -1;
        for (int i = 9; i < 45; ++i) {
            Slot slot = mc.thePlayer.inventoryContainer.getSlot(i);
            ItemStack is = slot.getStack();
            if (is != null && is.getItem() instanceof ItemBlock && is.stackSize > stack && Arrays.stream(this.serverItems).noneMatch(is.getDisplayName().toLowerCase()::contains)) {
                stack = is.stackSize;
                biggestSlot = i;
            }
        }
        return biggestSlot;
    }

    private boolean equipArmor(boolean moveItems) {
        for (int i = 0; i < this.bestArmorPieces.length; ++i) {
            int piece = this.bestArmorPieces[i];
            if (piece != -1) {
                int armorPieceSlot = i + 5;
                ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(armorPieceSlot).getStack();
                if (stack == null) {
                    if (moveItems) {
                        InventoryUtil.windowClick(mc, piece, 0, InventoryUtil.ClickType.SHIFT_CLICK);
                    }
                    return true;
                }
            }
        }
        return false;
    }

    private void putItemInSlot(int slot, int slotIn) {
        InventoryUtil.windowClick(mc, slotIn, slot - 36, InventoryUtil.ClickType.SWAP_WITH_HOT_BAR_SLOT);
    }

    private void putToolsInSlot(int tool, int[] toolSlots) {
        int toolSlot = toolSlots[tool];
        InventoryUtil.windowClick(mc, this.bestToolSlots[tool], toolSlot - 36, InventoryUtil.ClickType.SWAP_WITH_HOT_BAR_SLOT);
        this.bestToolSlots[tool] = toolSlot;
    }

    private static boolean isValidStack(ItemStack stack) {
        return (stack.getItem() instanceof ItemBlock && InventoryUtil.isStackValidToPlace(stack)) || (stack.getItem() instanceof ItemPotion && InventoryUtil.isBuffPotion(stack)) || (stack.getItem() instanceof ItemFood && InventoryUtil.isGoodFood(stack)) || InventoryUtil.isGoodItem(stack.getItem());
    }

    public void onEnable() {
        this.ticksSinceLastClick = 0;
        this.clientOpen = (mc.currentScreen instanceof GuiInventory);
        this.serverOpen = this.clientOpen;
    }

    public void onDisable() {
        this.close();
        this.clear();
    }

    private void open() {
        if (!this.clientOpen && !this.serverOpen) {
            mc.thePlayer.sendQueue.addToSendQueue(new C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT));
            this.serverOpen = true;
        }
    }

    private void close() {
        if (!this.clientOpen && this.serverOpen) {
            mc.thePlayer.sendQueue.addToSendQueue(new C0DPacketCloseWindow(mc.thePlayer.inventoryContainer.windowId));
            this.serverOpen = false;
        }
    }

    private void clear() {
        this.trash.clear();
        this.bestBowSlot = -1;
        this.bestSwordSlot = -1;
        this.bestsbSlot = -1;
        this.gappleStackSlots.clear();
        Arrays.fill(this.bestArmorPieces, -1);
        Arrays.fill(this.bestToolSlots, -1);
    }
}
