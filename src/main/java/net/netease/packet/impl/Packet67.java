package net.netease.packet.impl;

import net.minecraft.network.PacketBuffer;
import net.netease.packet.GermPacket;

public class Packet67 implements GermPacket {
   private String message;
   private String message2;

   public void process() {
   }

   public void readPacketData(PacketBuffer packetBuffer) {
      this.message = packetBuffer.readStringFromBuffer(32767);
      this.message2 = packetBuffer.readStringFromBuffer(32767);
   }

   public int getPacketId() {
      return 67;
   }
}
