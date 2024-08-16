package net.minecraft.client.main;

import lol.tgformat.utils.client.SoundUtil;
import net.minecraft.util.ResourceLocation;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

@StringEncryption
public class Progress extends JFrame {
//      
//      private final JProgressBar progressBar;
      public static boolean done = false;
      public Progress() {
            this.setSize(860, 576);
            this.setAlwaysOnTop(true);
            this.setLayout(new BorderLayout());
            this.setLocationRelativeTo(null);
            this.setUndecorated(true);
            final ImageIcon backgroundImage = new ImageIcon(Objects.requireNonNull(this.getClass().getResource("/assets/minecraft/bloodline/szy.png")));
            final JLabel backgroundLabel = new JLabel(backgroundImage);
            this.add(backgroundLabel, "Center");
            this.setVisible(true);
            for (int i = 0; i <= 100; i++) {
                  try {
                        Thread.sleep(20);
                  } catch (InterruptedException e) {
                        e.printStackTrace();
                  }

            }
            this.setVisible(false);

//            this.progressBar = new JProgressBar(0, 100);
//            this.progressBar.setSize(10000,40);
//            this.progressBar.setStringPainted(true);
//            add(progressBar, BorderLayout.SOUTH);
//            simulateProgress();
      }

//      public static void start() {
//            done = false;
//            Progress shield = new Progress();
//            shield.setVisible(true);
////            Main.remain(Main.jvmoptions);
//      }

//      
//      private void simulateProgress() {
//            new Thread(() -> {
//                  try {
//                        for (int i = 0; i <= 100; i++) {
//                              Thread.sleep(30); // 模拟耗时操作
//                              final int progress = i;
//                              SwingUtilities.invokeLater(() -> {
//                                    progressBar.setValue(progress);
//                                    progressBar.setString("Client Loading: " + progress + "%"); // 自定义文本
//
//                                    if (progress == 100) {
//
//                                          progressBar.setString("Done");
//                                          new Thread(() -> {
//                                                try {
//                                                      Thread.sleep(500); // 等待0.5秒
//                                                      while (progressBar.getValue() > 0) {
//                                                            progressBar.setValue(progressBar.getValue() - 1);
//                                                            Thread.sleep(20);
//                                                      }
//                                                      done = true;
//                                                      dispose(); // 关闭窗口
////                                                      Main.remain(Main.jvmoptions);
//                                                } catch (InterruptedException ex) {
//                                                      ex.printStackTrace();
//                                                }
//                                          }).start();
//
//                                    }
//                              });
//                        }
//                  } catch (InterruptedException e) {
//                        e.printStackTrace();
//                  }
//            }).start();
//      }
}
