package lol.tgformat.irc.utils.logger;

import tech.skidonion.obfuscator.annotations.StringEncryption;

/**
 * @author TG_format
 * @since 2024/7/13 下午5:24
 */
@StringEncryption
public class Logger {
    public static void info(String message) {
        System.out.println("[*] " + message);
    }

    public static void warn(String message) {
        System.out.println("[~] " + message);
    }

    public static void success(String message) {
        System.out.println("[+] " + message);
    }

    public static void error(String message) {
        System.out.println("[!] " + message);
    }
}