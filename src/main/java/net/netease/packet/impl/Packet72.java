package net.netease.packet.impl;

import net.netease.PacketProcessor;
import net.netease.packet.GermPacket;
import net.netease.packet.impl.Packet16;
import net.netease.packet.impl.Packet731;

public class Packet72
        implements GermPacket {
   public static String getRandomString(int length) {
      String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
      StringBuilder sb = new StringBuilder();
      for (int i = 0; i < length; ++i) {
         int number = (int)(Math.random() * (double)str.length());
         sb.append(str.charAt(number));
      }
      return sb.toString();
   }

   @Override
   public void process() {
      if (Packet731.flag) {
         PacketProcessor.INSTANCE.sendPacket(new Packet16("3.4.2", Packet72.getRandomString(20)));
         Packet731.flag = false;
      }
   }

   @Override
   public int getPacketId() {
      return 72;
   }
}
