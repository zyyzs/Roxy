package lol.tgformat.module.impl.player;

import lol.tgformat.api.event.Listener;
import lol.tgformat.config.ConfigSetting;
import lol.tgformat.events.PreUpdateEvent;
import lol.tgformat.events.motion.PreMotionEvent;
import lol.tgformat.events.packet.PacketSendEvent;
import lol.tgformat.module.Module;
import lol.tgformat.module.ModuleType;
import lol.tgformat.module.values.impl.NumberSetting;
import lol.tgformat.utils.network.PacketUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;
import tech.skidonion.obfuscator.annotations.Renamer;
import tech.skidonion.obfuscator.annotations.StringEncryption;

/**
 * @Author KuChaZi
 * @Date 2024/6/19 17:39
 * @ClassName: SpeedMine
 */
@Renamer
@StringEncryption
public class SpeedMine extends Module {
    private final NumberSetting speed = new NumberSetting("Speed", 1.0, 3.0, 1.0, 0.1);
    public SpeedMine() {
        super("SpeedMine", ModuleType.Player);
    }
    @Listener
    private void onUpdate(PreUpdateEvent event) {
        if (isNull()) return;
        mc.playerController.blockHitDelay = 0;
        if (mc.playerController.curBlockDamageMP > 1.0f / speed.getValue()) {
            mc.playerController.curBlockDamageMP = 1.0f;
        }
    }
}
