package lol.tgformat.module.impl.movement;

import lol.tgformat.api.event.Listener;
import lol.tgformat.events.PreUpdateEvent;
import lol.tgformat.events.TickEvent;
import lol.tgformat.events.WorldEvent;
import lol.tgformat.module.Module;
import lol.tgformat.module.ModuleType;
import lol.tgformat.module.values.impl.ModeSetting;
import lol.tgformat.utils.block.BlockUtil;
import lol.tgformat.utils.network.PacketUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockWeb;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Author KuChaZi
 * @Date 2024/6/15 9:55
 * @ClassName: AntiWeb
 */
@StringEncryption
public class AntiWeb extends Module {
    private final ModeSetting mode = new ModeSetting("Mode", "Vanilla", "Vanilla", "GrimAC");
    private List<BlockPos> pos = new ArrayList<>();
    private boolean pass = true;

    public AntiWeb() {
        super("AntiWeb", ModuleType.Movement);
    }

    @Listener
    public void onUpdate(PreUpdateEvent event) {
        if (mc.thePlayer.isInWeb) {
            mc.thePlayer.isInWeb = false;
        }
    }

    @Listener
    public void onWorld(WorldEvent event) {
        pos.clear();
    }
    @Listener
    public void onTick(TickEvent event) {
        if (isNull()) return;
        if (mode.getMode().equals("GrimAC")) {
            for (int i = -2; i <= 2; ++i) {
                for (int j = -2; j < 2; ++j) {
                    for (int k = -2; k < 2; ++k) {
                        BlockPos pos = mc.thePlayer.getPosition().add(i, j, k);
                        if (mc.theWorld.getBlockState(pos) == null || !(mc.theWorld.getBlockState(pos).getBlock() instanceof BlockWeb) || this.pos.contains(pos))
                            continue;
                        mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK, pos, EnumFacing.DOWN));
                        mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, pos, EnumFacing.DOWN));
                        mc.theWorld.setBlockToAir(pos);
                        this.pass = true;
                    }
                }
            }
        }

    }
}

