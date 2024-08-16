package net.viamcp;

import lol.tgformat.utils.client.LogUtil;
import net.viamcp.viamcp.ViaMCP;
import tech.skidonion.obfuscator.annotations.Renamer;
import tech.skidonion.obfuscator.annotations.StringEncryption;

/**
 * @Author KuChaZi
 * @Date 2024/7/8 19:06
 * @ClassName: ViaLoading
 */
@Renamer
@StringEncryption
public class ViaLoading {
    public static void load() {
        try {
            ViaMCP.create();
            ViaMCP.INSTANCE.initAsyncSlider();
        } catch (Exception e) {
            LogUtil.print(e.getMessage());
        }
    }
}
