package lol.tgformat.irc.network.packets.server.handlers;

import com.mojang.realmsclient.gui.ChatFormatting;
import lol.tgformat.irc.network.packets.PacketHandler;
import lol.tgformat.irc.network.packets.server.ServerChatMessagePacket;
import lol.tgformat.utils.client.LogUtil;
import net.minecraft.util.EnumChatFormatting;
import tech.skidonion.obfuscator.annotations.StringEncryption;

/**
 * @author DiaoLing
 * @since 2/20/2024
 */
@StringEncryption
public class ChatMessagePacketHandler implements PacketHandler<ServerChatMessagePacket> {
    @Override
    public void handle(ServerChatMessagePacket packet) {
        String formattedMessage = buildMessage(packet.getRank(), packet.getUsername(), packet.getMessage());
        LogUtil.addIRCMessage(ChatFormatting.DARK_AQUA + "[" + "IRC" + "]" + formattedMessage);
    }

    private String buildMessage(String rank, String username, String message) {
        EnumChatFormatting rankColor = getRankColor(rank);
        return rankColor + "[" + rank + "] " + EnumChatFormatting.WHITE + "(" + username + ") " + EnumChatFormatting.GRAY + message;
    }

    private EnumChatFormatting getRankColor(String rank) {
        return switch (rank.toLowerCase()) {
            case "admin" -> EnumChatFormatting.RED;
            case "dev" -> EnumChatFormatting.LIGHT_PURPLE;
            case "user" -> EnumChatFormatting.BLUE;
            default -> EnumChatFormatting.WHITE;
        };
    }
}
