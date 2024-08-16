package net.netease.packet.impl;

import net.minecraft.network.PacketBuffer;
import net.netease.PacketProcessor;
import net.netease.packet.GermPacket;
import net.netease.packet.impl.Packet04;

public class Packet714
        implements GermPacket {
   private static String message;

   @Override
   public void process() {
      Packet714.send();
   }

   public static void send() {
      PacketProcessor.INSTANCE.sendPacket(new Packet04(message, 2));
   }

   @Override
   public void readPacketData(PacketBuffer packetBuffer) {
      message = packetBuffer.readStringFromBuffer(Short.MAX_VALUE);
   }

   @Override
   public int getPacketId() {
      return 714;
   }
}
