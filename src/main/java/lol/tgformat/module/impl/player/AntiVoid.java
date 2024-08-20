package lol.tgformat.module.impl.player;

import lol.tgformat.api.event.Listener;
import lol.tgformat.events.PreUpdateEvent;
import lol.tgformat.events.motion.PreMotionEvent;
import lol.tgformat.events.packet.PacketReceiveEvent;
import lol.tgformat.events.packet.PacketSendEvent;
import lol.tgformat.module.Module;
import lol.tgformat.module.ModuleManager;
import lol.tgformat.module.ModuleType;
import lol.tgformat.module.impl.world.Scaffold;
import lol.tgformat.module.values.impl.ModeSetting;
import lol.tgformat.module.values.impl.NumberSetting;
import lol.tgformat.utils.client.LogUtil;
import lol.tgformat.utils.network.PacketUtil;
import lol.tgformat.utils.timer.TimerUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import org.lwjgl.input.Keyboard;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;
import tech.skidonion.obfuscator.annotations.Renamer;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import java.util.ArrayList;

/**
 * @author TG_format
 * @since 2024/6/8 下午2:10
 */
@Renamer

@StringEncryption
public class AntiVoid extends Module {
    private final ModeSetting mode = new ModeSetting("Mode","GrimAC","GrimAC","Watchdog");
    public NumberSetting pullbackTime = new NumberSetting("Pullback Time", 1000.0, 2000.0, 1000.0, 100.0);
    public NumberSetting catcherDistance = new NumberSetting("Catcher Distance", 3, 50, 1, 1);
    public NumberSetting catcherTicks = new NumberSetting("Catcher Distance", 10, 100, 1, 1);
    public NumberSetting stuckDistance = new NumberSetting("Stuck Distance", 4, 50, 1, 1);
    public AntiVoid() {
        super("AntiVoid", ModuleType.Player);
    }
    private float[] last = new float[3];
    public TimerUtil timer = new TimerUtil();
    public ArrayList<Packet<?>> packets = new ArrayList<>();
    boolean lastSkip = false;
    @Listener
    public void onMotion(PreMotionEvent event) {
        this.setSuffix(mode.getMode());
        if (mode.is("Watchdog")) {
            if (!isAboveVoid()) {
                last = new float[]{(float) mc.thePlayer.posX, (float) mc.thePlayer.posY, (float) mc.thePlayer.posZ};
            }
        }
    }
    @Override
    public void onDisable() {
        if (mode.is("GrimAC")) {
            if (ModuleManager.getModule(Stuck.class).isState()) {
                ModuleManager.getModule(Stuck.class).setState(false);
            }
        }
    }
    @Listener
    public void onUpdate(PreUpdateEvent event) {
        if (isNull()) return;
        if (catcherDistance.getValue() >= stuckDistance.getValue()) {
            catcherDistance.setValue(stuckDistance.getValue() - 1);
            LogUtil.addChatMessage("Catcher Distance 必须大于 Stuck Distance, 已将Catcher Distance设置为" + (stuckDistance.getValue() - 1));
        }
        if (ModuleManager.getModule(Blink.class).isState()) return;
        if (mode.is("GrimAC")) {
            if (mc.thePlayer.fallDistance >= catcherDistance.getValue() && mc.thePlayer.fallDistance < stuckDistance.getValue() && !isBlockUnder()) {
                if (!lastSkip) {
                    mc.theWorld.skiptick = catcherTicks.getValue().intValue();
                    lastSkip = true;
                }
                ModuleManager.getModule(Scaffold.class).setState(true);
            }
            if (mc.thePlayer.fallDistance > stuckDistance.getValue()) {
                if (!ModuleManager.getModule(Stuck.class).isState() && !isBlockUnder()) {
                    ModuleManager.getModule(Stuck.class).setState(true);
                }
            }
        }
        if (!ModuleManager.getModule(Scaffold.class).isState()) {
            lastSkip = false;
        }
    }

    @Listener
    public void onSend(PacketSendEvent event) {
        if (mode.is("Watchdog")) {
            if (mc.thePlayer != null && mc.thePlayer.ticksExisted < 100) {
                packets.clear();
                return;
            }
            if (event.getPacket() instanceof C03PacketPlayer) {
                if (isAboveVoid() && !mc.thePlayer.onGround) {
                    event.setCancelled(true);
                    packets.add(event.getPacket());
                    double doublePrimitive = pullbackTime.getValue();
                    long longValue = (long) doublePrimitive;

                    if (timer.hasTimePassed(longValue)) {
                        mc.thePlayer.setPosition(last[0], last[1] + 0.1, last[2]);
                        mc.getNetHandler().getNetworkManager().sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(last[0], last[1], last[2], true));
                        mc.thePlayer.motionY = 0.0;
                        mc.thePlayer.motionX = -mc.thePlayer.motionX / 2;
                        mc.thePlayer.motionZ = -mc.thePlayer.motionZ / 2;
                        packets.clear();
                        timer.reset();
                    }
                } else {
                    if (!packets.isEmpty()) {
                        for (Packet<?> packet : packets) {
                            PacketUtil.sendPacketNoEvent(packet);
                        }
                        packets.clear();
                    }
                    timer.reset();
                }
            }
        }
    }

    @Listener
    public void onReceive(PacketReceiveEvent event) {
        if (event.getPacket() instanceof S08PacketPlayerPosLook packet && mode.is("Watchdog")) {
            packets.clear();
            float x = (float) packet.getX();
            float y = (float) packet.getY();
            float z = (float) packet.getZ();

            if (!isAboveVoid(x, y, z)) {
                last[0] = x;
                last[1] = y;
                last[2] = z;
            }
        }
        if (isBlockUnder() && mode.is("GrimAC")) {
            mc.thePlayer.fallDistance = 0F;
            if (ModuleManager.getModule(Stuck.class).isState()){
                ModuleManager.getModule(Stuck.class).setState(false);
            }
        }
    }

    
    public static boolean isBlockUnder() {
        if (mc.thePlayer.posY < 0) return false;
        for (int offset = 0; offset < (int) mc.thePlayer.posY + 2; offset += 2) {
            AxisAlignedBB bb = mc.thePlayer.getEntityBoundingBox().offset(0, -offset, 0);
            if (!mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, bb).isEmpty()) {
                return true;
            }
        }
        return false;
    }
    public boolean isAboveVoid(float x, float y, float z) {
        if (mc.thePlayer == null) {
            return false;
        }
        if (y < 0) {
            return true;
        }
        for (int i = (int) (y - 1); i >= 1; i--) {
            if (!(mc.theWorld.getBlockState(new BlockPos(x, i, z)).getBlock() instanceof BlockAir)) {
                return false;
            }
        }
        return !mc.thePlayer.onGround;
    }
    public boolean isAboveVoid() {
        if (mc.thePlayer == null) {
            return false;
        }
        if (mc.thePlayer.posY < 0) {
            return true;
        }
        for (int i = (int) (mc.thePlayer.posY - 1); i >= 1; i--) {
            if (!(mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX, i, mc.thePlayer.posZ)).getBlock() instanceof BlockAir)) {
                return false;
            }
        }
        return !mc.thePlayer.onGround;
    }
}
