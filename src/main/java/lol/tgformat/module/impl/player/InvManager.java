package lol.tgformat.module.impl.player;

import lol.tgformat.api.event.Listener;
import lol.tgformat.events.WorldEvent;
import lol.tgformat.events.motion.PreMotionEvent;
import lol.tgformat.events.packet.PacketReceiveEvent;
import lol.tgformat.events.packet.PacketSendEvent;
import lol.tgformat.module.Module;
import lol.tgformat.module.ModuleType;
import lol.tgformat.module.impl.combat.KillAura;
import lol.tgformat.module.values.impl.BooleanSetting;
import lol.tgformat.module.values.impl.ModeSetting;
import lol.tgformat.module.values.impl.NumberSetting;
import lol.tgformat.utils.player.InventoryUtil;
import lol.tgformat.utils.timer.TimerUtil;
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
import tech.skidonion.obfuscator.annotations.Renamer;
import tech.skidonion.obfuscator.annotations.StringEncryption;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static lol.tgformat.module.ModuleManager.getModule;
import static lol.tgformat.utils.player.InventoryUtil.windowClick;

@Renamer

@StringEncryption
public class InvManager extends Module {

    public InvManager() {
        super("InvManager", ModuleType.Player);
    }

    public static final BooleanSetting noWoodAndGold = new BooleanSetting("NoWoodAndGold", true);

    private final ModeSetting mode = new ModeSetting("Mode", "Spoof", "OpenInv", "Spoof");
    private final NumberSetting delay = new NumberSetting("SlotDelay", 5, 300, 0, 10);
    private final NumberSetting armorDelay = new NumberSetting("WearDelay", 20, 300, 0, 10);

    public final NumberSetting sword = new NumberSetting("Weapon", 1, 9, 1, 1);
    public final NumberSetting bow = new NumberSetting("Bow", 6, 9, 1, 1);
    public final NumberSetting pearl = new NumberSetting("Pearl", 8, 9, 1, 1);
    public final NumberSetting pick_axe = new NumberSetting("Pickaxe", 2, 9, 1, 1);
    public final NumberSetting axe = new NumberSetting("Axe", 3, 9, 1, 1);
    public final NumberSetting block = new NumberSetting("Block", 7, 9, 1, 1);
    public final NumberSetting gApple = new NumberSetting("GApple", 4, 9, 1, 1);
    public final NumberSetting shovel = new NumberSetting("Shovel", 5, 9, 1, 1);

    public final String[] serverItems = {"选择游戏", "加入游戏", "职业选择菜单", "离开对局", "再来一局", "selector", "tracking compass", "(right click)", "tienda ", "perfil", "salir", "shop", "collectibles", "game", "profil", "lobby", "show all", "hub", "friends only", "cofre", "(click", "teleport", "play", "exit", "hide all", "jeux", "gadget", " (activ", "emote", "amis", "bountique", "choisir", "choose "};

    private final int[] bestArmorPieces = new int[4];
    private final List<Integer> trash = new ArrayList<>();
    private final int[] bestToolSlots = new int[3];
    private final List<Integer> gappleStackSlots = new ArrayList<>();
    private int bestSwordSlot;
    private int bestPearlSlot;

    private int bestBowSlot;
    private boolean serverOpen;
    private boolean clientOpen;

    private int ticksSinceLastClick;

    private boolean nextTickCloseInventory;
    private final TimerUtil timer = new TimerUtil();


    @Listener
    private void onPacket(PacketReceiveEvent event) {
        final Packet<?> packet = event.getPacket();
        if (packet instanceof S2DPacketOpenWindow) {
            this.clientOpen = false;
            this.serverOpen = false;
        }

    }

    @Listener
    private void onPacketSend(PacketSendEvent event) {
        final Packet<?> packet = event.getPacket();

        if (packet instanceof C16PacketClientStatus clientStatus) {

            if (clientStatus.getStatus() == C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT) {
                this.clientOpen = true;
                this.serverOpen = true;
            }
        } else if (packet instanceof C0DPacketCloseWindow packetCloseWindow) {

            if (packetCloseWindow.windowId == mc.thePlayer.inventoryContainer.windowId) {
                this.clientOpen = false;
                this.serverOpen = false;
            }
        } else if (packet instanceof C0EPacketClickWindow && !mc.thePlayer.isUsingItem()) {
            this.ticksSinceLastClick = 0;

        }
    }

    @Listener
    public void onWorld(WorldEvent event) {
        setState(false);
    }
    private boolean dropItem(final List<Integer> listOfSlots) {

        if (!listOfSlots.isEmpty()) {
            int slot = listOfSlots.removeFirst();
            windowClick(mc, slot, 1, InventoryUtil.ClickType.DROP_ITEM);
            return true;
        }
        return false;
    }


    @Listener
    private void onMotion(PreMotionEvent event) {
            if (!mc.thePlayer.isOnLadder() && !(getModule(Blink.class).isState()) && !mc.thePlayer.isUsingItem() && (mc.currentScreen == null || mc.currentScreen instanceof GuiChat || mc.currentScreen instanceof GuiInventory || !mc.thePlayer.isSpectator() || mc.currentScreen instanceof GuiIngameMenu) && KillAura.target == null) {

                this.ticksSinceLastClick++;

                if (this.ticksSinceLastClick < Math.floor(this.delay.getValue() / 50)) return;

                if (this.clientOpen || (mc.currentScreen == null && !this.mode.getMode().equals("OpenInv"))) {
                    this.clear();

                    for (int slot = InventoryUtil.INCLUDE_ARMOR_BEGIN; slot < InventoryUtil.END; slot++) {
                        final ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(slot).getStack();

                        if (stack != null) {
                            if (stack.getItem() instanceof ItemSword && InventoryUtil.isBestSword(mc.thePlayer, stack)) {
                                this.bestSwordSlot = slot;
                            } else if (stack.getItem() instanceof ItemTool && InventoryUtil.isBestTool(mc.thePlayer, stack)) {
                                final int toolType = InventoryUtil.getToolType(stack);
                                if (toolType != -1 && slot != this.bestToolSlots[toolType])
                                    this.bestToolSlots[toolType] = slot;
                            } else if (stack.getItem() instanceof ItemArmor armor && InventoryUtil.isBestArmor(mc.thePlayer, stack)) {

                                final int pieceSlot = this.bestArmorPieces[armor.armorType];

                                if (pieceSlot == -1 || slot != pieceSlot)
                                    this.bestArmorPieces[armor.armorType] = slot;
                            } else if (stack.getItem() instanceof ItemBow && InventoryUtil.isBestBow(mc.thePlayer, stack)) {
                                if (slot != this.bestBowSlot)
                                    this.bestBowSlot = slot;
                            } else if (stack.getItem() instanceof ItemAppleGold) {
                                this.gappleStackSlots.add(slot);
                            } else if (stack.getItem() instanceof ItemEnderPearl) {
                                this.bestPearlSlot = slot;
                            } else if (!this.trash.contains(slot) && !isValidStack(stack)) {
                                if (Arrays.stream(serverItems).anyMatch(stack.getDisplayName()::contains)) continue;
                                if (stack.getItem() instanceof ItemSkull) continue;
                                this.trash.add(slot);
                            }
                        }
                    }

                    final boolean busy = (!this.trash.isEmpty()) || this.equipArmor(false) || this.sortItems(false);

                    if (!busy) {
                        if (this.nextTickCloseInventory) {
                            if (mode.is("Spoof")) this.close();
                            this.nextTickCloseInventory = false;
                        } else {
                            this.nextTickCloseInventory = true;
                        }
                        return;
                    } else {
                        boolean waitUntilNextTick = !this.serverOpen;

                        if (mode.is("Spoof")) this.open();

                        if (this.nextTickCloseInventory)
                            this.nextTickCloseInventory = false;

                        if (waitUntilNextTick) return;
                    }


                    if (timer.hasTimeElapsed(this.armorDelay.getValue().longValue()) && this.equipArmor(true))
                        return;
                    if (this.dropItem(this.trash)) return;
                    this.sortItems(true);
                }
            }

    }

    private boolean sortItems(final boolean moveItems) {
        int goodSwordSlot = this.sword.getValue().intValue() + 35;

        if (this.bestSwordSlot != -1) {
            if (this.bestSwordSlot != goodSwordSlot) {
                if (moveItems) {
                    this.putItemInSlot(goodSwordSlot, this.bestSwordSlot);
                    this.bestSwordSlot = goodSwordSlot;
                }

                return true;
            }
        }
        int goodBowSlot = this.bow.getValue().intValue() + 35;

        if (this.bestBowSlot != -1) {
            if (this.bestBowSlot != goodBowSlot) {
                if (moveItems) {
                    this.putItemInSlot(goodBowSlot, this.bestBowSlot);
                    this.bestBowSlot = goodBowSlot;
                }
                return true;
            }
        }
        int goodGappleSlot = this.gApple.getValue().intValue() + 35;

        if (!this.gappleStackSlots.isEmpty()) {
            this.gappleStackSlots.sort(Comparator.comparingInt(slot -> mc.thePlayer.inventoryContainer.getSlot(slot).getStack().stackSize));

            final int bestGappleSlot = this.gappleStackSlots.getFirst();

            if (bestGappleSlot != goodGappleSlot) {
                if (moveItems) {
                    this.putItemInSlot(goodGappleSlot, bestGappleSlot);
                    this.gappleStackSlots.set(0, goodGappleSlot);
                }
                return true;
            }
        }


        final int[] toolSlots = {
                pick_axe.getValue().intValue() + 35,
                axe.getValue().intValue() + 35,
                shovel.getValue().intValue() + 35};

        for (final int toolSlot : this.bestToolSlots) {
            if (toolSlot != -1) {
                final int type = InventoryUtil.getToolType(mc.thePlayer.inventoryContainer.getSlot(toolSlot).getStack());

                if (type != -1) {
                    if (toolSlot != toolSlots[type]) {
                        if (moveItems) {
                            this.putToolsInSlot(type, toolSlots);
                        }
                        return true;
                    }
                }
            }
        }

        int goodBlockSlot = this.block.getValue().intValue() + 35;
        int mostBlocksSlot = getMostBlocks();
        if (mostBlocksSlot != -1 && mostBlocksSlot != goodBlockSlot) {
            Slot dss = mc.thePlayer.inventoryContainer.getSlot(goodBlockSlot);
            ItemStack dsis = dss.getStack();
            if (!(dsis != null && dsis.getItem() instanceof ItemBlock && dsis.stackSize >= mc.thePlayer.inventoryContainer.getSlot(mostBlocksSlot).getStack().stackSize && Arrays.stream(serverItems).noneMatch(dsis.getDisplayName().toLowerCase()::contains))) {
                this.putItemInSlot(goodBlockSlot, mostBlocksSlot);
            }
        }

        int goodPearlSlot = this.pearl.getValue().intValue() + 35;

        if (this.bestPearlSlot != -1) {
            if (this.bestPearlSlot != goodPearlSlot) {
                if (moveItems) {
                    this.putItemInSlot(goodPearlSlot, this.bestPearlSlot);
                    this.bestPearlSlot = goodPearlSlot;
                }
                return true;
            }
        }
        return false;
    }

    public int getMostBlocks() {
        int stack = 0;
        int biggestSlot = -1;
        for (int i = 9; i < 45; i++) {
            Slot slot = mc.thePlayer.inventoryContainer.getSlot(i);
            ItemStack is = slot.getStack();
            if (is != null && is.getItem() instanceof ItemBlock && is.stackSize > stack && Arrays.stream(serverItems).noneMatch(is.getDisplayName().toLowerCase()::contains)) {
                stack = is.stackSize;
                biggestSlot = i;
            }
        }
        return biggestSlot;
    }

    private boolean equipArmor(boolean moveItems) {
        for (int i = 0; i < this.bestArmorPieces.length; i++) {
            final int piece = this.bestArmorPieces[i];

            if (piece != -1) {
                int armorPieceSlot = i + 5;
                final ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(armorPieceSlot).getStack();
                if (stack != null)
                    continue;

                if (moveItems) {
                    windowClick(mc, piece, 0, InventoryUtil.ClickType.SHIFT_CLICK);
                }
                timer.reset();
                return true;
            }
        }
        return false;
    }

    private void putItemInSlot(final int slot, final int slotIn) {
        windowClick(mc, slotIn,
                slot - 36,
                InventoryUtil.ClickType.SWAP_WITH_HOT_BAR_SLOT);
    }

    private void putToolsInSlot(final int tool, final int[] toolSlots) {
        final int toolSlot = toolSlots[tool];

        windowClick(mc, this.bestToolSlots[tool],
                toolSlot - 36,
                InventoryUtil.ClickType.SWAP_WITH_HOT_BAR_SLOT);
        this.bestToolSlots[tool] = toolSlot;
    }

    private static boolean isValidStack(final ItemStack stack) {
        return switch (stack.getItem()) {
            case ItemBlock ignored when InventoryUtil.isStackValidToPlace(stack) -> true;
            case ItemPotion ignored when InventoryUtil.isBuffPotion(stack) -> true;
            case ItemFood ignored when InventoryUtil.isGoodFood(stack) -> true;
            case null, default -> InventoryUtil.isGoodItem(stack.getItem());
        };
    }

    @Override
    public void onEnable() {
        this.ticksSinceLastClick = 0;

        this.clientOpen = mc.currentScreen instanceof GuiInventory;
        this.serverOpen = this.clientOpen;
    }

    @Override
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
        this.gappleStackSlots.clear();
        Arrays.fill(this.bestArmorPieces, -1);
        Arrays.fill(this.bestToolSlots, -1);
    }
}
