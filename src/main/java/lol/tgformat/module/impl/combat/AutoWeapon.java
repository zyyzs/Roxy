package lol.tgformat.module.impl.combat;

import lol.tgformat.api.event.Listener;
import lol.tgformat.events.PreUpdateEvent;
import lol.tgformat.events.TickEvent;
import lol.tgformat.events.packet.PacketReceiveEvent;
import lol.tgformat.events.packet.PacketSendEvent;
import lol.tgformat.module.Module;
import lol.tgformat.module.ModuleManager;
import lol.tgformat.module.ModuleType;
import lol.tgformat.module.values.impl.BooleanSetting;
import lol.tgformat.module.values.impl.NumberSetting;
import lol.tgformat.utils.network.PacketUtil;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import org.lwjgl.input.Keyboard;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;
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

    public AutoWeapon() {
        super("AutoWeapon", ModuleType.Combat);
    }

    private boolean attackEnemy;
    @Override
    public void onEnable(){
        this.attackEnemy = false;
    }

    @Listener
    
    public void onAttack(TickEvent event) {
        KillAura aura = ModuleManager.getModule(KillAura.class);
        if (aura.target instanceof EntityPlayer) {
            this.attackEnemy = true;
        }
    }

    @Listener
    
    public void onPacketReceive(PacketReceiveEvent event) {
        if (this.attackEnemy) {
            this.attackEnemy = false;
            int slot = -1;
            double maxDamage = 0.0;
            for (int i = 0; i < 9; ++i) {
                if (mc.thePlayer.inventory.getStackInSlot(i) != null && (mc.thePlayer.inventory.getStackInSlot(i).getItem() instanceof ItemSword)) {
                    final double damage = ((mc.thePlayer.inventory.getStackInSlot(i).getAttributeModifiers().get("generic.attackDamage").stream().findFirst().orElse(null) != null) ? Objects.requireNonNull((AttributeModifier)mc.thePlayer.inventory.getStackInSlot(i).getAttributeModifiers().get("generic.attackDamage").stream().findFirst().orElse(null)).getAmount() : 0.0) + 1.25 * getEnchantment(mc.thePlayer.inventory.getStackInSlot(i), Enchantment.sharpness);
                    if (damage > maxDamage) {
                        maxDamage = damage;
                        slot = i;
                    }
                }
            }
            if (slot == mc.thePlayer.inventory.currentItem || slot == -1) {
                return;
            }
            mc.thePlayer.inventory.currentItem = slot;
            if(event.getPacket() instanceof C02PacketUseEntity c02){
                if(c02.getAction() == C02PacketUseEntity.Action.ATTACK){
                    event.setCancelled(true);
                    PacketUtil.sendPacket(c02);
                }
            }
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

