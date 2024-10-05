package lol.tgformat.module.impl.combat;

import lol.tgformat.api.event.Listener;
import lol.tgformat.component.RotationComponent;
import lol.tgformat.events.WorldEvent;
import lol.tgformat.events.motion.PreMotionEvent;
import lol.tgformat.module.Module;
import lol.tgformat.module.ModuleManager;
import lol.tgformat.module.ModuleType;
import lol.tgformat.module.impl.misc.IRC;
import lol.tgformat.module.impl.misc.Teams;
import lol.tgformat.module.impl.player.Blink;
import lol.tgformat.module.impl.world.Scaffold;
import lol.tgformat.module.values.impl.BooleanSetting;
import lol.tgformat.module.values.impl.NumberSetting;
import lol.tgformat.utils.enums.MovementFix;
import lol.tgformat.utils.rotation.RotationUtil;
import lol.tgformat.utils.vector.Vector2f;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import tech.skidonion.obfuscator.annotations.Renamer;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @Author KuChaZi
 * @Date 2024/7/27 17:46
 * @ClassName: TrowableAura
 */
@Renamer
@StringEncryption
public class TrowableAura extends Module {
    private final NumberSetting fov = new NumberSetting("Fov", 180.0, 180.0, 90.0, 0.1);
    private final NumberSetting range = new NumberSetting("Range", 8.0, 10.0, 3.0, 0.1);
    private final BooleanSetting fishRod = new BooleanSetting("FishRod", false);
    private final NumberSetting rotspeed = new NumberSetting("RotationSpeed", 10, 10, 0, 1);
    private final NumberSetting ticks = new NumberSetting("Ticks", 3, 10, 1, 1);

    public static final List<EntityPlayer> targets = new ArrayList<>();
    public static EntityPlayer target;
    public static int tick = 0;
    public static boolean isThrowOut = false;

    public TrowableAura() {
        super("TrowableAura", ModuleType.Combat);
    }

    @Listener
    public void onWorld(WorldEvent event) {
        target = null;
    }

    @Listener
    public void onMotionEvent(PreMotionEvent event) {
        if (mc.thePlayer == null || mc.theWorld == null) {
            return;
        }

        targets.sort(Comparator.comparingDouble(mc.thePlayer::getDistanceToEntity));
        if (!targets.isEmpty()) {
            if (IRC.transport.isUser(targets.getFirst().getName())){
                if (targets.get(2)!=null){
                    target = targets.get(2);
                }else {
                    target = null;
                }
            }else {
                target = targets.getFirst();
            }
        } else {
            target = null;
        }


        for (Entity entity : mc.theWorld.loadedEntityList) {
            if (entity instanceof EntityFishHook fishHook && isThrowOut) {
                if ((fishHook.caughtEntity != null && fishHook.caughtEntity == target) || entity.onGround) {
                    isThrowOut = false;
                    mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
                }
            }
        }
        if (isThrowOut) {
            if (KillAura.target != null || ModuleManager.getModule(Scaffold.class).isState() || ModuleManager.getModule(Blink.class).isState()) {
                isThrowOut = false;
                mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
            }
        }

        if (isThrowOut || findBall() == -1 && (findFishRod() == -1 || !fishRod.isEnabled()) || KillAura.target != null || ModuleManager.getModule(Scaffold.class).isState()|| ModuleManager.getModule(Blink.class).isState()) {
            return;
        }

        for (Entity entity : mc.theWorld.loadedEntityList) {
            if (entity instanceof EntityPlayer && mc.thePlayer.getDistanceToEntity(entity) <= range.getValue() && mc.thePlayer != entity && !Teams.isSameTeam(entity)) {
                targets.add((EntityPlayer) entity);
            }
        }

        long delay = ticks.getValue().longValue() + 1;

        if (target != null && mc.thePlayer.getDistanceToEntity(target) <= range.getValue() && RotationUtil.getRotationDifference(target) <= fov.getValue() && mc.thePlayer.canEntityBeSeen(target)) {
            Vector2f rotation = RotationUtil.getThrowRotation(target, range.getValue().floatValue());
            RotationComponent.setRotations(rotation, rotspeed.getValue(), MovementFix.NORMAL);
            if (++tick > delay) {
                if (findBall() != -1) {
                    mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(findBall() - 36));
                    mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getStackInSlot(findBall() - 36)));
                    if (mc.thePlayer.isUsingItem()) {
                        mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
                    }
                } else if (findFishRod() != -1 && !isThrowOut && fishRod.isEnabled()) {
                    mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(findFishRod() - 36));
                    mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getStackInSlot(findFishRod() - 36)));
                    isThrowOut = true;
                }
                target = null;
                targets.clear();
                tick = 0;
            }
        } else {
            tick = 0;
        }
    }

    public static int findFishRod() {
        for (int i = 36; i < 45; ++i) {
            ItemStack itemStack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
            if (itemStack != null && itemStack.getItem() == Items.fishing_rod) {
                return i;
            }
        }
        return -1;
    }

    public static int findBall() {
        for (int i = 36; i < 45; ++i) {
            ItemStack itemStack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
            if (itemStack != null && (itemStack.getItem() == Items.snowball || itemStack.getItem() == Items.egg) && itemStack.stackSize > 0) {
                return i;
            }
        }
        return -1;
    }

}
