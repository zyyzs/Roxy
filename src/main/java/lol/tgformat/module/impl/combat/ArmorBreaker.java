package lol.tgformat.module.impl.combat;

import lol.tgformat.api.event.Listener;
import lol.tgformat.events.PreUpdateEvent;
import lol.tgformat.events.WorldEvent;
import lol.tgformat.events.packet.PacketSendEvent;
import lol.tgformat.module.Module;
import lol.tgformat.module.ModuleManager;
import lol.tgformat.module.ModuleType;
import lol.tgformat.module.impl.player.InvManager;
import lol.tgformat.utils.client.LogUtil;
import lol.tgformat.utils.network.PacketUtil;
import lol.tgformat.utils.player.InventoryUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C02PacketUseEntity;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author KuChaZi
 * @Date 2024/8/12 16:45
 * @ClassName: ArmorBreaker
 */
@StringEncryption
public class ArmorBreaker extends Module {
    public static boolean eating;
    private EntityLivingBase target;
    private int currentItem = 0;
    public ArmorBreaker() {
        super("ArmorBreaker", ModuleType.Combat);
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @Listener
    public void onWorld(WorldEvent event) {
        this.setSuffix("");
    }

    @Listener
    public void onUpdate(PreUpdateEvent event) {
        if (target != null) {
            Entity entity = KillAura.target;
            InvManager invManager = ModuleManager.getModule(InvManager.class);
            if (entity == target) {
                if (!Gapple.eating) {
                    List<Slot> slots = new ArrayList<>(mc.thePlayer.inventoryContainer.inventorySlots.stream()
                            .filter(slot -> slot.getHasStack() && slot.getStack().getItem() instanceof ItemSword)
                            .sorted(Comparator.comparingDouble(value -> InventoryUtil.getSwordStrength(((Slot) value).getStack())).reversed())
                            .collect(Collectors.toList()));

                    if (currentItem >= slots.size())
                        currentItem = 0;

                    eating = true;

                    ItemStack selectedWeapon = slots.get(currentItem).getStack();
                    String weaponName = selectedWeapon.getDisplayName();

                    if (target != null) {
                        this.setSuffix("");
                    } else {
                        this.setSuffix(weaponName);
                    }


                    PacketUtil.send1_12Block();
                    mc.playerController.windowClick(0, slots.get(currentItem).slotNumber, mc.thePlayer.inventory.currentItem, 2, mc.thePlayer);
                    mc.thePlayer.swingItem();

                    currentItem++;
                }
            } else {
                target = null;
                eating = false;
            }
        }
    }

    @Listener
    public void onPacketSend(PacketSendEvent event) {
        if (event.getPacket() instanceof C02PacketUseEntity packetUseEntity && (((C02PacketUseEntity) event.getPacket()).getAction() == C02PacketUseEntity.Action.ATTACK)) {
            if (packetUseEntity.getEntityFromWorld(mc.theWorld) instanceof EntityLivingBase) {
                if (target == null)
                    currentItem = 0;
                target = (EntityLivingBase) packetUseEntity.getEntityFromWorld(mc.theWorld);

            }
        }
    }


}
