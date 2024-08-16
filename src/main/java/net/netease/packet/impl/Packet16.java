package net.netease.packet.impl;

import net.minecraft.network.PacketBuffer;
import net.netease.packet.GermPacket;
import net.netease.packet.impl.Packet731;

public class Packet16
        implements GermPacket {
   private String version;
   private String message;

   public Packet16() {
   }

   @Override
   public void process() {
      if (Packet731.flag) {
         // empty if block
      }
   }

   @Override
   public void writePacketData(PacketBuffer packetBuffer) {
      packetBuffer.writeString(this.version);
      packetBuffer.writeString(this.message);
   }

   @Override
   public int getPacketId() {
      return 16;
   }

   public Packet16(String version, String message) {
      this.version = version;
      this.message = message;
   }
}
