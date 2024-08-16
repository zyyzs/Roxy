package net.minecraft.client.main;

import tech.skidonion.obfuscator.annotations.Renamer;
import tech.skidonion.obfuscator.annotations.StringEncryption;

/**
 * @Author KuChaZi
 * @Date 2024/7/27 19:04
 * @ClassName: JavaCheck
 */
@Renamer
@StringEncryption
public class JavaCheck {
    public static void run() {
        String java = System.getProperty("java.version");
        System.out.println("你的抓哇版本: " + java);

        if (java.startsWith("21")) {
            System.out.println("恭喜主播脑子正常运转");
        } else {
            System.out.println("主播没用抓哇21玩你老母呢");
            Main.onStop();
        }
    }
}

