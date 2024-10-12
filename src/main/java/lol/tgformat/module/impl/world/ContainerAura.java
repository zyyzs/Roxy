package lol.tgformat.module.impl.world;

import lol.tgformat.api.event.Listener;
import lol.tgformat.component.RotationComponent;
import lol.tgformat.events.PlaceEvent;
import lol.tgformat.events.WorldEvent;
import lol.tgformat.events.motion.PostMotionEvent;
import lol.tgformat.events.motion.PreMotionEvent;
import lol.tgformat.events.render.Render3DEvent;
import lol.tgformat.module.Module;
import lol.tgformat.module.ModuleManager;
import lol.tgformat.module.ModuleType;
import lol.tgformat.module.impl.combat.KillAura;
import lol.tgformat.module.impl.player.Blink;
import lol.tgformat.module.impl.player.Stealer;
import lol.tgformat.module.values.impl.BooleanSetting;
import lol.tgformat.module.values.impl.NumberSetting;
import lol.tgformat.utils.enums.MovementFix;
import lol.tgformat.utils.network.PacketUtil;
import lol.tgformat.utils.rotation.RayCastUtil;
import lol.tgformat.utils.rotation.RotationUtil;
import lol.tgformat.utils.timer.TimeHelper;
import lol.tgformat.utils.vector.Vector2f;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBrewingStand;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockFurnace;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.util.BlockPos;
import net.netease.utils.RenderUtil;
import tech.skidonion.obfuscator.annotations.Renamer;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import java.util.ArrayList;
import java.util.List;

/**
 * @author TG_format
 * @since 2024/6/15 下午11:53
 */
@Renamer

@StringEncryption
public class ContainerAura extends Module {
    private final NumberSetting range = new NumberSetting("Range", 3.0, 7.0, 1.0, 0.1);
    public static BooleanSetting interactOnce = new BooleanSetting("InteractOnce", false);
    public TimeHelper waitBoxOpenTimer = new TimeHelper();
    public static boolean isWaitingOpen = false;
    private BlockPos globalPos;
    private BlockPos openingPos;
    public static List<BlockPos> list = new ArrayList<>();
    public ContainerAura() {
        super("ContainerAura", ModuleType.World);
    }

    @Override
    public void onDisable() {
        list.clear();
    }

    @Listener
    public void onPre(PreMotionEvent e) {
        if (isGapple() || !ModuleManager.getModule(Stealer.class).isState()) return;
        if (ModuleManager.getModule(Scaffold.class).isState()) return;
        float radius;
        this.globalPos = null;
        if (mc.thePlayer.ticksExisted % 20 == 0 || KillAura.target != null || mc.currentScreen instanceof GuiContainer || ModuleManager.getModule(Scaffold.class).isState() || ModuleManager.getModule(Blink.class).isState() || isFood()) {
            return;
        }

        for (float y = radius = this.range.getValue().floatValue(); y >= -radius; y -= 1.0f) {
            for (float x = -radius; x <= radius; x += 1.0f) {
                for (float z = -radius; z <= radius; z += 1.0f) {
                    BlockPos pos = new BlockPos(mc.thePlayer.posX - 0.5 + (double)x, mc.thePlayer.posY - 0.5 + (double)y, mc.thePlayer.posZ - 0.5 + (double)z);
                    Block block = mc.theWorld.getBlockState(pos).getBlock();
                    BlockPos targetPos = new BlockPos(mc.thePlayer.posX + (double)x, mc.thePlayer.posY + (double)y, mc.thePlayer.posZ + (double)z);
                    if (!(mc.thePlayer.getDistance(targetPos.getX(), targetPos.getY(), targetPos.getZ()) < (double)mc.playerController.getBlockReachDistance()) || !(block instanceof BlockChest || block instanceof BlockFurnace || block instanceof BlockBrewingStand) || list.contains(pos)) continue;
                    float[] rotations = RotationUtil.getBlockRotations(pos.getX(), pos.getY() - 1, pos.getZ());
                    if (RayCastUtil.overBlock(new Vector2f(rotations[0], rotations[1]), mc.objectMouseOver.sideHit, pos, false)) {
                        RotationComponent.setRotations(new Vector2f(rotations[0], rotations[1]), 360.0f, MovementFix.NORMAL, true);
                        this.globalPos = pos;
                        return;
                    }
                }
            }
        }
    }

    @Listener
    public void onPost(PostMotionEvent event) {
        if (isGapple() || !ModuleManager.getModule(Stealer.class).isState()) return;
        if (ModuleManager.getModule(Scaffold.class).isState()) return;
        if (isWaitingOpen) {
            if (this.waitBoxOpenTimer.isDelayComplete(600.0)) {
                isWaitingOpen = false;
            } else if (this.openingPos != null && mc.thePlayer.openContainer instanceof ContainerChest) {
                list.add(this.openingPos);
                this.openingPos = null;
                isWaitingOpen = false;
            }
        }
    }

    @Listener
    public void onPlace(PlaceEvent event) {
        if (isGapple() || !ModuleManager.getModule(Stealer.class).isState()) return;
        if (ModuleManager.getModule(Scaffold.class).isState()) return;
        if (!(this.globalPos == null || mc.currentScreen instanceof GuiContainer || list.size() >= 50 || isWaitingOpen || list.contains(this.globalPos))) {
            if (RayCastUtil.overBlock(RotationComponent.rotations, mc.objectMouseOver.sideHit, globalPos, false)){
                this.sendClick(this.globalPos);
                PacketUtil.sendPacketNoEvent(new C0APacketAnimation());
                event.setShouldRightClick(false);
            }
        }
    }

    @Listener
    public void onWorld(WorldEvent e) {
        list.clear();
    }
    public void sendClick(BlockPos pos) {
        C08PacketPlayerBlockPlacement packet = new C08PacketPlayerBlockPlacement(pos, (double)pos.getY() + 0.5 < mc.thePlayer.posY + 1.7 ? 1 : 0, mc.thePlayer.getCurrentEquippedItem(), 0.0f, 0.0f, 0.0f);
        mc.thePlayer.sendQueue.addToSendQueue(packet);
        this.waitBoxOpenTimer.reset();
        isWaitingOpen = true;
        this.openingPos = this.globalPos;
        if (interactOnce.isEnabled()) {
            list.add(pos);
        }
    }
}
