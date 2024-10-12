package net.minecraft.client.main;

import tech.skidonion.obfuscator.annotations.Renamer;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * @Author KuChaZi
 * @Date 2024/7/25 18:18
 * @ClassName: PingCheck
 */
@Renamer
@StringEncryption
public class PingCheck {
    public static boolean isReachable = false;

    public static void run() {
        String host = "103.40.13.87";
        int port = 14250;
        int maxPing = 100;
        int checkInterval = 6000; // check time

        try {
            // Show progress
            for (int i = 0; i < checkInterval / 1000; i++) {
                System.out.print("真的喜欢白织" + "(" + (i + 1) + "/" + (checkInterval / 1000) + ")" + " seconds\r");
                System.out.flush();
                Thread.sleep(600);
            }
            System.out.println();
            System.out.print("Hello World");
            System.out.println();

            long startTime = System.currentTimeMillis();
            try (Socket socket = new Socket()) {
                socket.connect(new InetSocketAddress(host, port), maxPing);
                isReachable = true;
            } catch (IOException ignored) {
                System.out.println("Cannot access the server.");
                isReachable = false;
                return;
            }
            long ping = System.currentTimeMillis() - startTime;

            System.out.println("Ping: " + ping + " ms");

            if (ping > maxPing) {
                System.out.println("Congratulations to the anchor for having a high Ping!");
            } else {
                System.out.println("Congratulations, you are an Earthling!");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
