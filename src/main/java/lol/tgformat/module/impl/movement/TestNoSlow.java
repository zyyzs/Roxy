package lol.tgformat.module.impl.movement;

import com.viaversion.viarewind.protocol.protocol1_8to1_9.Protocol1_8To1_9;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.type.Type;
import lol.tgformat.api.event.Listener;
import lol.tgformat.events.PreUpdateEvent;
import lol.tgformat.events.WorldChangeEvent;
import lol.tgformat.events.WorldEvent;
import lol.tgformat.events.motion.PostMotionEvent;
import lol.tgformat.events.movement.SlowEvent;
import lol.tgformat.events.packet.PacketReceiveEvent;
import lol.tgformat.events.packet.PacketSendEvent;
import lol.tgformat.events.packet.PacketSendHigherEvent;
import lol.tgformat.module.Module;
import lol.tgformat.module.ModuleType;
import lol.tgformat.module.impl.misc.Disabler;
import lol.tgformat.module.values.impl.BooleanSetting;
import lol.tgformat.utils.client.LogUtil;
import lol.tgformat.utils.network.PacketUtil;
import lol.tgformat.utils.player.BlinkUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0EPacketClickWindow;
import net.minecraft.network.play.server.S32PacketConfirmTransaction;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @Author KuChaZi
 * @Date 2024/6/28 16:32
 * @ClassName: TestNoSlow
 */
@StringEncryption
public class TestNoSlow extends Module {
    public TestNoSlow() {
        super("TestNoSlow", ModuleType.Movement);
    }
    boolean eating = false;
    @Listener
    public void onSlow(SlowEvent event) {
        if (isFood()) {
            event.setCancelled();
        }
    }
    @Listener
    public void onSend(PacketSendEvent event) {
        if (isFood()) {
            if (event.getPacket() instanceof C08PacketPlayerBlockPlacement c08 && c08.getPlacedBlockDirection() == 255) {
                eating = true;
                BlinkUtils.startBlink();
            }
            if (event.getPacket() instanceof C09PacketHeldItemChange && !event.isCancelled() || event.getPacket() instanceof C07PacketPlayerDigging c07 && c07.getStatus().equals(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM)) {
                eating = false;
                if (event.getPacket() instanceof C07PacketPlayerDigging c07 && c07.getStatus().equals(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM)) {
                    BlinkUtils.stopBlink();
                    LogUtil.addChatMessage("pros");
                }
            }
        }
    }
    @Listener
    public void onWorld(WorldEvent event) {
        eating = false;
    }
    
}
