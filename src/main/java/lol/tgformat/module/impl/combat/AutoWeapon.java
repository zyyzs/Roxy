package lol.tgformat.module.impl.combat;

import lol.tgformat.Client;
import lol.tgformat.api.event.Listener;
import lol.tgformat.events.AttackEvent;
import lol.tgformat.events.PreUpdateEvent;
import lol.tgformat.events.packet.PacketSendEvent;
import lol.tgformat.module.Module;
import lol.tgformat.module.ModuleType;
import lol.tgformat.module.values.impl.BooleanSetting;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import tech.skidonion.obfuscator.annotations.Renamer;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import java.util.Objects;

/**
 * @Author KuChaZi
 * @Date 2024/6/9 15:42
 * @ClassName: AutoWeapon
 */

@Renamer
@StringEncryption
public class AutoWeapon extends Module {
    private final BooleanSetting axe = new BooleanSetting("Axe", true);
    private boolean attackEnemy = false;

    public AutoWeapon() {
        super("AutoWeapon", ModuleType.Combat);
    }

    @Listener
    public void onAttack(AttackEvent event) {
        this.attackEnemy = true;
    }

    @Listener
    public void onPacketSend(PacketSendEvent event) {
        if (event.getPacket() instanceof C02PacketUseEntity && ((C02PacketUseEntity) event.getPacket()).getAction() == C02PacketUseEntity.Action.ATTACK && this.attackEnemy) {
            this.attackEnemy = false;
            int slot = -1;
            double maxDamage = 0.0;
            for (int i = 0; i < 9; ++i) {
                double damage;
                if (mc.thePlayer.inventory.getStackInSlot(i) == null || !(mc.thePlayer.inventory.getStackInSlot(i).getItem() instanceof ItemSword) && (!(mc.thePlayer.inventory.getStackInSlot(i).getItem() instanceof ItemTool) || !this.axe.isEnabled()) || !((damage = (mc.thePlayer.inventory.getStackInSlot(i).getAttributeModifiers().get(((Object) "generic.attackDamage").toString()).stream().findFirst().orElse(null) != null ? Objects.requireNonNull(mc.thePlayer.inventory.getStackInSlot(i).getAttributeModifiers().get("generic.attackDamage").stream().findFirst().orElse(null)).getAmount() : 0.0) + 1.25 * (double) getEnchantment(mc.thePlayer.inventory.getStackInSlot(i), Enchantment.sharpness)) > maxDamage))
                    continue;
                maxDamage = damage;
                slot = i;
            }
            if (slot == mc.thePlayer.inventory.currentItem || slot == -1) {
                return;
            }
            mc.thePlayer.inventory.currentItem = slot;
            mc.playerController.updateController();
            mc.getNetHandler().addToSendQueue(event.getPacket());
            event.setCancelled(true);
        }
    }
    public static int getEnchantment(final ItemStack itemStack, final Enchantment enchantment) {
        if (itemStack == null || itemStack.getEnchantmentTagList() == null || itemStack.getEnchantmentTagList().hasNoTags()) {
            return 0;
        }
        for (int i = 0; i < itemStack.getEnchantmentTagList().tagCount(); ++i) {
            final NBTTagCompound tagCompound = itemStack.getEnchantmentTagList().getCompoundTagAt(i);
            if (tagCompound.getShort("ench") == enchantment.effectId || tagCompound.getShort("id") == enchantment.effectId) {
                return tagCompound.getShort("lvl");
            }
        }
        return 0;
    }
}

