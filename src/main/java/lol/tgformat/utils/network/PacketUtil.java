package lol.tgformat.utils.network;

import com.viaversion.viarewind.protocol.protocol1_8to1_9.Protocol1_8To1_9;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.protocol.Protocol;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.exception.CancelException;
import lol.tgformat.accessable.IMinecraft;
import lol.tgformat.utils.math.MathUtil;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.login.client.C00PacketLoginStart;
import net.minecraft.network.login.client.C01PacketEncryptionResponse;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.server.*;
import net.minecraft.network.status.client.C00PacketServerQuery;
import net.minecraft.network.status.client.C01PacketPing;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.viamcp.vialoadingbase.ViaLoadingBase;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;
import tech.skidonion.obfuscator.annotations.Renamer;
import tech.skidonion.obfuscator.annotations.StringEncryption;

/**
 * @author TG_format
 * @since 2024/6/1 0:21
 */
@Renamer
@StringEncryption
public class PacketUtil implements IMinecraft {

    public static void sendPacket(Packet packet) {
        mc.getNetHandler().addToSendQueue(packet);
    }

    public static void send1_12Block(){
        if(ViaLoadingBase.getInstance().getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_8)){
            PacketUtil.sendPacket(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
            return;
        }
        PacketWrapper useItem = PacketWrapper.create(29, null, Via.getManager().getConnectionManager().getConnections().iterator().next());
        useItem.write(Type.VAR_INT, 1);
        com.viaversion.viarewind.utils.PacketUtil.sendToServer(useItem, Protocol1_8To1_9.class, true, true);
    }

    public static void send1_12Block0(){
        if(ViaLoadingBase.getInstance().getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_8)){
            PacketUtil.sendPacket(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
            return;
        }
        PacketWrapper useItem = PacketWrapper.create(29, null, Via.getManager().getConnectionManager().getConnections().iterator().next());
        useItem.write(Type.VAR_INT, 0);
        com.viaversion.viarewind.utils.PacketUtil.sendToServer(useItem, Protocol1_8To1_9.class, true, true);
    }

    public static void sendPacketNoEvent(Packet<?> packet) {
        mc.getNetHandler().addToSendQueueUnregistered(packet);
    }

    public static void sendBlocking(boolean callEvent, boolean place) {
        C08PacketPlayerBlockPlacement packet = place ?
                new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 255, mc.thePlayer.getHeldItem(), 0, 0, 0) :
                new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem());

        if(callEvent) {
            sendPacket(packet);
        } else {
            sendPacketNoEvent(packet);
        }
    }

    public static void sendC0F(){
        PacketUtil.sendPacket(new C0FPacketConfirmTransaction(114514, (short) 191981000, true));
    }
    public static void sendC0FNoEvent() {
        PacketUtil.sendPacketNoEvent(new C0FPacketConfirmTransaction(MathUtil.getRandom(114514, 1919810), (short)MathUtil.getRandom(102, 1000024123), true));
    }

    public static void releaseUseItem(boolean callEvent) {
        C07PacketPlayerDigging packet = new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN);

        if(callEvent) {
            sendPacket(packet);
        } else {
            sendPacketNoEvent(packet);
        }
    }

    public static boolean shouldIgnorePacket(Packet packet) {
        return packet instanceof C00PacketLoginStart || packet instanceof C01PacketEncryptionResponse || packet instanceof C00Handshake || packet instanceof C00PacketServerQuery || packet instanceof C01PacketPing;
    }

    public static void sendToServer(PacketWrapper packet, Class<? extends Protocol> packetProtocol, boolean skipCurrentPipeline, boolean currentThread) {
        try {
            if (currentThread) {
                packet.sendToServer(packetProtocol, skipCurrentPipeline);
            } else {
                packet.scheduleSendToServer(packetProtocol, skipCurrentPipeline);
            }
        } catch (CancelException var5) {
        } catch (Exception var6) {
            var6.printStackTrace();
        }

    }

    public static void handlePacket(final Packet<INetHandlerPlayClient> packet) {
        final INetHandlerPlayClient netHandler = mc.getNetHandler();
        if (packet instanceof S00PacketKeepAlive) {
            netHandler.handleKeepAlive((S00PacketKeepAlive)packet);
        }
        else if (packet instanceof S01PacketJoinGame) {
            netHandler.handleJoinGame((S01PacketJoinGame)packet);
        }
        else if (packet instanceof S02PacketChat) {
            netHandler.handleChat((S02PacketChat)packet);
        }
        else if (packet instanceof S03PacketTimeUpdate) {
            netHandler.handleTimeUpdate((S03PacketTimeUpdate)packet);
        }
        else if (packet instanceof S04PacketEntityEquipment) {
            netHandler.handleEntityEquipment((S04PacketEntityEquipment)packet);
        }
        else if (packet instanceof S05PacketSpawnPosition) {
            netHandler.handleSpawnPosition((S05PacketSpawnPosition)packet);
        }
        else if (packet instanceof S06PacketUpdateHealth) {
            netHandler.handleUpdateHealth((S06PacketUpdateHealth)packet);
        }
        else if (packet instanceof S07PacketRespawn) {
            netHandler.handleRespawn((S07PacketRespawn)packet);
        }
        else if (packet instanceof S08PacketPlayerPosLook) {
            netHandler.handlePlayerPosLook((S08PacketPlayerPosLook)packet);
        }
        else if (packet instanceof S09PacketHeldItemChange) {
            netHandler.handleHeldItemChange((S09PacketHeldItemChange)packet);
        }
        else if (packet instanceof S10PacketSpawnPainting) {
            netHandler.handleSpawnPainting((S10PacketSpawnPainting)packet);
        }
        else if (packet instanceof S0APacketUseBed) {
            netHandler.handleUseBed((S0APacketUseBed)packet);
        }
        else if (packet instanceof S0BPacketAnimation) {
            netHandler.handleAnimation((S0BPacketAnimation)packet);
        }
        else if (packet instanceof S0CPacketSpawnPlayer) {
            netHandler.handleSpawnPlayer((S0CPacketSpawnPlayer)packet);
        }
        else if (packet instanceof S0DPacketCollectItem) {
            netHandler.handleCollectItem((S0DPacketCollectItem)packet);
        }
        else if (packet instanceof S0EPacketSpawnObject) {
            netHandler.handleSpawnObject((S0EPacketSpawnObject)packet);
        }
        else if (packet instanceof S0FPacketSpawnMob) {
            netHandler.handleSpawnMob((S0FPacketSpawnMob)packet);
        }
        else if (packet instanceof S11PacketSpawnExperienceOrb) {
            netHandler.handleSpawnExperienceOrb((S11PacketSpawnExperienceOrb)packet);
        }
        else if (packet instanceof S12PacketEntityVelocity) {
            netHandler.handleEntityVelocity((S12PacketEntityVelocity)packet);
        }
        else if (packet instanceof S13PacketDestroyEntities) {
            netHandler.handleDestroyEntities((S13PacketDestroyEntities)packet);
        }
        else if (packet instanceof S14PacketEntity) {
            netHandler.handleEntityMovement((S14PacketEntity)packet);
        }
        else if (packet instanceof S18PacketEntityTeleport) {
            netHandler.handleEntityTeleport((S18PacketEntityTeleport)packet);
        }
        else if (packet instanceof S19PacketEntityStatus) {
            netHandler.handleEntityStatus((S19PacketEntityStatus)packet);
        }
        else if (packet instanceof S19PacketEntityHeadLook) {
            netHandler.handleEntityHeadLook((S19PacketEntityHeadLook)packet);
        }
        else if (packet instanceof S1BPacketEntityAttach) {
            netHandler.handleEntityAttach((S1BPacketEntityAttach)packet);
        }
        else if (packet instanceof S1CPacketEntityMetadata) {
            netHandler.handleEntityMetadata((S1CPacketEntityMetadata)packet);
        }
        else if (packet instanceof S1DPacketEntityEffect) {
            netHandler.handleEntityEffect((S1DPacketEntityEffect)packet);
        }
        else if (packet instanceof S1EPacketRemoveEntityEffect) {
            netHandler.handleRemoveEntityEffect((S1EPacketRemoveEntityEffect)packet);
        }
        else if (packet instanceof S1FPacketSetExperience) {
            netHandler.handleSetExperience((S1FPacketSetExperience)packet);
        }
        else if (packet instanceof S20PacketEntityProperties) {
            netHandler.handleEntityProperties((S20PacketEntityProperties)packet);
        }
        else if (packet instanceof S21PacketChunkData) {
            netHandler.handleChunkData((S21PacketChunkData)packet);
        }
        else if (packet instanceof S22PacketMultiBlockChange) {
            netHandler.handleMultiBlockChange((S22PacketMultiBlockChange)packet);
        }
        else if (packet instanceof S23PacketBlockChange) {
            netHandler.handleBlockChange((S23PacketBlockChange)packet);
        }
        else if (packet instanceof S24PacketBlockAction) {
            netHandler.handleBlockAction((S24PacketBlockAction)packet);
        }
        else if (packet instanceof S25PacketBlockBreakAnim) {
            netHandler.handleBlockBreakAnim((S25PacketBlockBreakAnim)packet);
        }
        else if (packet instanceof S26PacketMapChunkBulk) {
            netHandler.handleMapChunkBulk((S26PacketMapChunkBulk)packet);
        }
        else if (packet instanceof S27PacketExplosion) {
            netHandler.handleExplosion((S27PacketExplosion)packet);
        }
        else if (packet instanceof S28PacketEffect) {
            netHandler.handleEffect((S28PacketEffect)packet);
        }
        else if (packet instanceof S29PacketSoundEffect) {
            netHandler.handleSoundEffect((S29PacketSoundEffect)packet);
        }
        else if (packet instanceof S2APacketParticles) {
            netHandler.handleParticles((S2APacketParticles)packet);
        }
        else if (packet instanceof S2BPacketChangeGameState) {
            netHandler.handleChangeGameState((S2BPacketChangeGameState)packet);
        }
        else if (packet instanceof S2CPacketSpawnGlobalEntity) {
            netHandler.handleSpawnGlobalEntity((S2CPacketSpawnGlobalEntity)packet);
        }
        else if (packet instanceof S2DPacketOpenWindow) {
            netHandler.handleOpenWindow((S2DPacketOpenWindow)packet);
        }
        else if (packet instanceof S2EPacketCloseWindow) {
            netHandler.handleCloseWindow((S2EPacketCloseWindow)packet);
        }
        else if (packet instanceof S2FPacketSetSlot) {
            netHandler.handleSetSlot((S2FPacketSetSlot)packet);
        }
        else if (packet instanceof S30PacketWindowItems) {
            netHandler.handleWindowItems((S30PacketWindowItems)packet);
        }
        else if (packet instanceof S31PacketWindowProperty) {
            netHandler.handleWindowProperty((S31PacketWindowProperty)packet);
        }
        else if (packet instanceof S32PacketConfirmTransaction) {
            netHandler.handleConfirmTransaction((S32PacketConfirmTransaction)packet);
        }
        else if (packet instanceof S33PacketUpdateSign) {
            netHandler.handleUpdateSign((S33PacketUpdateSign)packet);
        }
        else if (packet instanceof S34PacketMaps) {
            netHandler.handleMaps((S34PacketMaps)packet);
        }
        else if (packet instanceof S35PacketUpdateTileEntity) {
            netHandler.handleUpdateTileEntity((S35PacketUpdateTileEntity)packet);
        }
        else if (packet instanceof S36PacketSignEditorOpen) {
            netHandler.handleSignEditorOpen((S36PacketSignEditorOpen)packet);
        }
        else if (packet instanceof S37PacketStatistics) {
            netHandler.handleStatistics((S37PacketStatistics)packet);
        }
        else if (packet instanceof S38PacketPlayerListItem) {
            netHandler.handlePlayerListItem((S38PacketPlayerListItem)packet);
        }
        else if (packet instanceof S39PacketPlayerAbilities) {
            netHandler.handlePlayerAbilities((S39PacketPlayerAbilities)packet);
        }
        else if (packet instanceof S3APacketTabComplete) {
            netHandler.handleTabComplete((S3APacketTabComplete)packet);
        }
        else if (packet instanceof S3BPacketScoreboardObjective) {
            netHandler.handleScoreboardObjective((S3BPacketScoreboardObjective)packet);
        }
        else if (packet instanceof S3CPacketUpdateScore) {
            netHandler.handleUpdateScore((S3CPacketUpdateScore)packet);
        }
        else if (packet instanceof S3DPacketDisplayScoreboard) {
            netHandler.handleDisplayScoreboard((S3DPacketDisplayScoreboard)packet);
        }
        else if (packet instanceof S3EPacketTeams) {
            netHandler.handleTeams((S3EPacketTeams)packet);
        }
        else if (packet instanceof S3FPacketCustomPayload) {
            netHandler.handleCustomPayload((S3FPacketCustomPayload)packet);
        }
        else if (packet instanceof S40PacketDisconnect) {
            netHandler.handleDisconnect((S40PacketDisconnect)packet);
        }
        else if (packet instanceof S41PacketServerDifficulty) {
            netHandler.handleServerDifficulty((S41PacketServerDifficulty)packet);
        }
        else if (packet instanceof S42PacketCombatEvent) {
            netHandler.handleCombatEvent((S42PacketCombatEvent)packet);
        }
        else if (packet instanceof S43PacketCamera) {
            netHandler.handleCamera((S43PacketCamera)packet);
        }
        else if (packet instanceof S44PacketWorldBorder) {
            netHandler.handleWorldBorder((S44PacketWorldBorder)packet);
        }
        else if (packet instanceof S45PacketTitle) {
            netHandler.handleTitle((S45PacketTitle)packet);
        }
        else if (packet instanceof S46PacketSetCompressionLevel) {
            netHandler.handleSetCompressionLevel((S46PacketSetCompressionLevel)packet);
        }
        else if (packet instanceof S47PacketPlayerListHeaderFooter) {
            netHandler.handlePlayerListHeaderFooter((S47PacketPlayerListHeaderFooter)packet);
        }
        else if (packet instanceof S48PacketResourcePackSend) {
            netHandler.handleResourcePack((S48PacketResourcePackSend)packet);
        }
        else {
            if (!(packet instanceof S49PacketUpdateEntityNBT)) {
                throw new IllegalArgumentException("Unable to match packet type to handle: " + packet.getClass());
            }
            netHandler.handleEntityNBT((S49PacketUpdateEntityNBT)packet);
        }
    }

    public static void readPacket(Packet<INetHandler> packet, INetHandler netHandler) {
        packet.processPacket(netHandler);
    }
}

