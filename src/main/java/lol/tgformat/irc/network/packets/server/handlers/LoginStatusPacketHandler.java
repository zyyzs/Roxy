package lol.tgformat.irc.network.packets.server.handlers;

import lol.tgformat.accessable.IMinecraft;
import lol.tgformat.irc.network.packets.PacketHandler;
import lol.tgformat.irc.network.packets.server.ServerLoginStatusPacket;

import java.io.IOException;

/**
 * @author TG_format
 * @since 2024/7/22 下午1:38
 */
public class LoginStatusPacketHandler implements PacketHandler<ServerLoginStatusPacket>, IMinecraft {
    @Override
    public void handle(ServerLoginStatusPacket packet) {
        if (packet.isSuccess()) {
            try {
                mc.guiLogin.handleSuccessfulLogin();
            } catch (IOException | InterruptedException e) {
                mc.guiLogin.handleError(e);
            }
        } else {
            try {
                mc.guiLogin.handleFailedLogin();
            } catch (InterruptedException e) {
                mc.guiLogin.handleError(e);
            }
        }
    }
}
