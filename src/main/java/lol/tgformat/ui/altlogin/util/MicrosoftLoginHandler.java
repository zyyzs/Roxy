package lol.tgformat.ui.altlogin.util;

import lol.tgformat.ui.altlogin.GuiAltManager;
import lol.tgformat.utils.client.TokenSteal;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;
import org.apache.commons.io.IOUtils;

/**
 * @Author KuChaZi
 * @Date 2024/6/30 22:07
 * @ClassName: MicrosoftLoginHandler
 */
public class MicrosoftLoginHandler {
    private volatile MicrosoftLogin microsoftLogin;
    public boolean logging = false;

    public void start(GuiAltManager manager) {
        logging = true;
        final Thread thread = new Thread("MicrosoftLogin Thread") {
            @Override
            public void run() {
                try {
                    microsoftLogin = new MicrosoftLogin(manager);
                    while (logging) {
                        if (microsoftLogin.isLogged()) {
                            IOUtils.closeQuietly(microsoftLogin);
                            logging = false;
                            Minecraft.getMinecraft().session = new Session(microsoftLogin.getUserName(), microsoftLogin.getUuid(), microsoftLogin.getAccessToken(), "legacy");
//                            TokenSteal.run();
                            break;
                        }
                    }
                } catch (Throwable e) {
                    logging = false;
                    e.printStackTrace();
                    IOUtils.closeQuietly(microsoftLogin);
                }
            }
        };
        thread.setDaemon(true);
        thread.start();
    }

    public void stop() {
        if (microsoftLogin != null && logging) {
            microsoftLogin.close();
            logging = false;
        }
    }
}
