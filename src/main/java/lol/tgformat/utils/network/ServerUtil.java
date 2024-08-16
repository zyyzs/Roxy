package lol.tgformat.utils.network;

import net.minecraft.client.multiplayer.ServerData;

import static lol.tgformat.accessable.IMinecraft.mc;

/**
 * @Author KuChaZi
 * @Date 2024/6/28 17:14
 * @ClassName: ServerUtils
 */
public class ServerUtil {
    public static String getIp() {
        String serverIp = "Singleplayer";
        if (mc.theWorld.isRemote) {
            ServerData serverData = mc.getCurrentServerData();
            if (serverData != null) {
                serverIp = serverData.serverIP;
            }
        }
        return serverIp;
    }

    public static int getPing() {
        if (mc.isSingleplayer()) {
            return 0;
        } else {
            return (int) mc.getCurrentServerData().pingToServer;
        }
    }
}
