package lol.tgformat.module.impl.misc;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import lol.tgformat.api.event.EventManager;
import lol.tgformat.api.event.Listener;
import lol.tgformat.component.RotationComponent;
import lol.tgformat.events.PreUpdateEvent;
import lol.tgformat.events.WorldEvent;
import lol.tgformat.events.motion.PreMotionEvent;
import lol.tgformat.events.packet.PacketReceiveEvent;
import lol.tgformat.events.packet.PacketSendEvent;
import lol.tgformat.events.packet.PacketSendHigherEvent;
import lol.tgformat.module.Module;
import lol.tgformat.module.ModuleManager;
import lol.tgformat.module.ModuleType;
import lol.tgformat.module.values.impl.BooleanSetting;
import lol.tgformat.module.values.impl.ModeSetting;
import lol.tgformat.utils.client.LogUtil;
import lol.tgformat.utils.math.MathUtil;
import lol.tgformat.component.PacketStoringComponent;
import lol.tgformat.utils.network.PacketUtil;
import lol.tgformat.utils.player.BlinkUtils;
import net.minecraft.client.gui.GuiDownloadTerrain;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.login.server.S00PacketDisconnect;
import net.minecraft.network.login.server.S01PacketEncryptionRequest;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.*;
import net.minecraft.network.status.server.S00PacketServerInfo;
import net.minecraft.network.status.server.S01PacketPong;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.viamcp.vialoadingbase.ViaLoadingBase;
import tech.skidonion.obfuscator.annotations.Renamer;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author TG_format
 * @since 2024/6/1 0:27
 */
@Renamer
@StringEncryption
public class Disabler extends Module {
    private static final ModeSetting mode = new ModeSetting("Mode", "GrimAC","WatchDog","GrimAC");
    public static INetHandler packetListener;
    private final BooleanSetting badPacketsF = new BooleanSetting("BadPacketF", false);
    public static final BooleanSetting badPacketsU = new BooleanSetting("BadPacketU", false);
    private final BooleanSetting test = new BooleanSetting("Test", false);
    private final BooleanSetting higherVersion = new BooleanSetting("Move 1.17+", false);
    private final BooleanSetting fastBreak = new BooleanSetting("Fast Break", false);
    private final BooleanSetting fabricatedPlace = new BooleanSetting("FabricatedPlace", true);
    private static boolean lastResult;
    int lastSlot;
    boolean lastSprinting , c03Check, shouldFix;

    public static List<Packet<INetHandler>> storedPackets;
    public static ConcurrentLinkedDeque<Integer> pingPackets;

    public Disabler(){
        super("Disabler", ModuleType.Misc);
    }

    @Listener
    public void onWorld(WorldEvent event){
        if(isNull()) return;

        this.lastSlot = -1;
        this.lastSprinting = false;
        this.c03Check = false;
        this.shouldFix = false;
    }

    @Listener
    public void onUpdate(PreUpdateEvent event) {
        if (isNull())return;
        if (!getGrimPost()) {
            processPackets();
        }
    }

    @Listener
    public void onMotion(PreMotionEvent event) {
        this.setSuffix(mode.getMode());
    }

    @Listener(0)
    public void onSend(PacketSendEvent event){
        Packet<?> packet = event.getPacket();
        if (mode.is("GrimAC")) {
            if (test.isEnabled() && event.getPacket() instanceof C0EPacketClickWindow pkt) {
                if (pkt.getWindowId() <= 0 || pkt.getSlotId() >= 100 || pkt.getUsedButton() < 0) {
                    event.setCancelled(true);
                }
            }
            if (higherVersion.isEnabled() && packet instanceof C03PacketPlayer wrapped && !(packet instanceof C03PacketPlayer.C06PacketPlayerPosLook)) {
                if (wrapped.isMoving()) {
                    mc.getNetHandler().addToSendQueueUnregistered(new C03PacketPlayer.C06PacketPlayerPosLook(
                            wrapped.getX(),
                            wrapped.getY(),
                            wrapped.getZ(),
                            RotationComponent.rotations.x,
                            RotationComponent.rotations.y,
                            wrapped.isOnGround()
                    ));
                } else if (wrapped.rotating) {
                    mc.getNetHandler().addToSendQueueUnregistered(new C03PacketPlayer.C06PacketPlayerPosLook(
                            mc.thePlayer.posX,
                            mc.thePlayer.posY,
                            mc.thePlayer.posZ,
                            wrapped.getYaw(),
                            wrapped.getPitch(),
                            wrapped.isOnGround()
                    ));
                } else {
                    mc.getNetHandler().addToSendQueueUnregistered(new C03PacketPlayer.C06PacketPlayerPosLook(
                            mc.thePlayer.posX,
                            mc.thePlayer.posY,
                            mc.thePlayer.posZ,
                            RotationComponent.rotations.x,
                            RotationComponent.rotations.y,
                            wrapped.isOnGround()
                    ));
                }
            }
            if (this.fabricatedPlace.isEnabled()) {
                if (event.getPacket() instanceof C08PacketPlayerBlockPlacement) {
                    ((C08PacketPlayerBlockPlacement) event.getPacket()).facingX = 0.5f;
                    ((C08PacketPlayerBlockPlacement) event.getPacket()).facingY = 0.5f;
                    ((C08PacketPlayerBlockPlacement) event.getPacket()).facingZ = 0.5f;
                }
            }
            if (packet instanceof C02PacketUseEntity c02) {
                if (c02.getAction() == C02PacketUseEntity.Action.INTERACT) {
                    if (c02.getEntityFromWorld(mc.theWorld) instanceof EntityPlayer) {
                        event.setCancelled(true);
                    }
                }
                if (c02.getAction() == C02PacketUseEntity.Action.INTERACT_AT) {
                    if (c02.getEntityFromWorld(mc.theWorld) instanceof EntityPlayer) {
                        event.setCancelled(true);
                    }
                }
            }
            if (ViaLoadingBase.getInstance().getTargetVersion().isNewerThanOrEqualTo(ProtocolVersion.v1_9) && (packet instanceof C0EPacketClickWindow || packet instanceof C08PacketPlayerBlockPlacement || packet instanceof C0BPacketEntityAction)) {
                PacketUtil.sendC0F();
            }

            if (fastBreak.isEnabled() && event.getPacket() instanceof C07PacketPlayerDigging && ((C07PacketPlayerDigging)event.getPacket()).getStatus() == net.minecraft.network.play.client.C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK) {
                BlockPos blockPos = ((C07PacketPlayerDigging)event.getPacket()).getPosition();
                mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(net.minecraft.network.play.client.C07PacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, blockPos, EnumFacing.DOWN));
            }
            if (RotationComponent.active && packet instanceof C03PacketPlayer c03 && c03.rotating) {
                c03.setYaw(getRandomYaw(c03.getYaw()));
            }

        }
    }

    @Listener
    public void onHigher(PacketSendHigherEvent event) {
        if (badPacketsF.isEnabled() && event.getPacket() instanceof C0BPacketEntityAction c0b && !event.isCancelled()) {
            if (c0b.getAction() == C0BPacketEntityAction.Action.START_SPRINTING) {
                if (this.lastSprinting) {
                    event.setCancelled(true);
                }
                this.lastSprinting = true;
            }
            if (c0b.getAction() == C0BPacketEntityAction.Action.STOP_SPRINTING) {
                if (!this.lastSprinting) {
                    event.setCancelled(true);
                }
                this.lastSprinting = false;
            }
        }
        if (event.getPacket() instanceof C09PacketHeldItemChange c09) {
            int slot = c09.getSlotId();
            if (slot == this.lastSlot && slot != -1) {
                event.setCancelled(true);
            }
            this.lastSlot = c09.getSlotId();
        }
    }

    public static float getRandomYaw(float requestedYaw){
        int rand = MathUtil.getRandomInRange(1,200);
        return requestedYaw + (360 * rand);
    }

    
    public static boolean getGrimPost() {
        Disabler dis = ModuleManager.getModule(Disabler.class);
        boolean result = mode.is("GrimAC") && dis.isState() && mc.thePlayer != null && !(mc.currentScreen instanceof GuiDownloadTerrain);//&& !noPost();
        if (lastResult && !result) {
            lastResult = false;
            mc.addScheduledTask(Disabler::processPackets);
        }
        return lastResult = result;
    }

    
    public static boolean grimPostDelay(final Packet<?> packet) {
        if (mc.thePlayer == null) {
            return false;
        }
        if (mc.currentScreen instanceof GuiDownloadTerrain) {
            return false;
        }
        if (packet instanceof S12PacketEntityVelocity sPacketEntityVelocity) {
            return sPacketEntityVelocity.getEntityID() == mc.thePlayer.getEntityId();
        }
        return packet instanceof S27PacketExplosion || packet instanceof S32PacketConfirmTransaction || packet instanceof S08PacketPlayerPosLook || packet instanceof S18PacketEntityTeleport || packet instanceof S19PacketEntityStatus || packet instanceof S04PacketEntityEquipment || packet instanceof S23PacketBlockChange || packet instanceof S22PacketMultiBlockChange || packet instanceof S13PacketDestroyEntities || packet instanceof S00PacketKeepAlive || packet instanceof S06PacketUpdateHealth || packet instanceof S14PacketEntity || packet instanceof S0FPacketSpawnMob|| packet instanceof S3FPacketCustomPayload;
    }

    
    public static void processPackets() {
        if (mc.getCurrentServerData() == null) return;

        if (!storedPackets.isEmpty()) {
            for (Packet<INetHandler> packet : storedPackets) {
                PacketReceiveEvent event = new PacketReceiveEvent(packet, packetListener);
                EventManager.call(event);
                if (event.isCancelled()) {
                    continue;
                }
                packet.processPacket(packetListener);
            }
            storedPackets.clear();
        }
    }

//    public static boolean noPost() {
//        return PacketStoringComponent.blinking || BlinkUtils.isBlinking();
//    }
    
    public static void fixC0F(C0FPacketConfirmTransaction packet) {
        int id = packet.getUid();
        if (id >= 0 || pingPackets.isEmpty()) {
            PacketUtil.sendPacket(packet);
        }
        else {
            do {
                int current = pingPackets.getFirst();
                PacketUtil.sendPacket(new C0FPacketConfirmTransaction(packet.getWindowId(), (short)current, true));
                pingPackets.pollFirst();
                if (current == id) {
                    break;
                }
            } while (!pingPackets.isEmpty());
        }
    }

    static {
        lastResult = false;
        storedPackets = new CopyOnWriteArrayList<>();
        pingPackets = new ConcurrentLinkedDeque<>();
    }
}
