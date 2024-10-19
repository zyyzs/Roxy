package lol.tgformat;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import lol.tgformat.utils.client.LogUtil;
import net.minecraft.client.multiplayer.ServerData;
import net.viamcp.vialoadingbase.ViaLoadingBase;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;
import tech.skidonion.obfuscator.annotations.Renamer;
import tech.skidonion.obfuscator.annotations.StringEncryption;

/**
 * @author TG_format
 * @since 04/11/2023
 */

@Renamer
@StringEncryption
public class PacketFixer {
    public static double fixMotionJump() {
        if (ViaLoadingBase.getInstance().getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_8)) {
            return 0.005D;
        } else {
            return 0.003D;
        }
    }

    public static float fixRightClick() {
        if (ViaLoadingBase.getInstance().getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_8)) {
            return 16.0F;
        } else {
            return 1.0F;
        }
    }

    public static float fixLadder() {
        if (ViaLoadingBase.getInstance().getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_8)) {
            return 0.125F;
        } else {
            return 0.1875F;
        }
    }

    public static double fixMinX(double minX) {
        if (ViaLoadingBase.getInstance().getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_8)) {
            return minX;
        } else {
            return 0.0625D;
        }
    }

    public static double fixMinY(double minY) {
        if (ViaLoadingBase.getInstance().getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_8)) {
            return minY;
        } else {
            return 0.0D;
        }
    }

    public static double fixMinZ(double minZ) {
        if (ViaLoadingBase.getInstance().getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_8)) {
            return minZ;
        } else {
            return 0.0625D;
        }
    }

    public static double fixMaxX(double maxX) {
        if (ViaLoadingBase.getInstance().getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_8)) {
            return maxX;
        } else {
            return 0.9375D;
        }
    }

    public static double fixMaxY(double maxY) {
        if (ViaLoadingBase.getInstance().getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_8)) {
            return maxY;
        } else {
            return 0.09375D;
        }
    }

    public static double fixMaxZ(double maxZ) {
        if (ViaLoadingBase.getInstance().getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_8)) {
            return maxZ;
        } else {
            return 0.9375D;
        }
    }

    public static void checkHypixel(ServerData serverData) {
        if (serverData.serverIP.toLowerCase().contains("hypixel.net")) {
            LogUtil.print("真的Hypixel");
        } else {
            LogUtil.print("假的Hypixel");
        }
    }
}


