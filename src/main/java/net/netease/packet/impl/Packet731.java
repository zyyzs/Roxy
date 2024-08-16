package net.netease.packet.impl;

import net.netease.packet.GermPacket;

public class Packet731
        implements GermPacket {
   public static volatile boolean flag;

   @Override
   public void process() {
      flag = true;
   }

   @Override
   public int getPacketId() {
      return 731;
   }
}
