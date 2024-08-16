package net.netease.packet.impl;

import net.minecraft.network.PacketBuffer;
import net.netease.PacketProcessor;
import net.netease.packet.GermPacket;

public class Packet723
        implements GermPacket {
   private int key;

   @Override
   public void process() {
      PacketProcessor.INSTANCE.getOutstandingKeys().add(this.key);
   }

   @Override
   public void writePacketData(PacketBuffer packetBuffer) {
      packetBuffer.writeInt(this.key);
   }

   @Override
   public void readPacketData(PacketBuffer packetBuffer) {
      this.key = packetBuffer.readInt();
   }

   @Override
   public int getPacketId() {
      return 723;
   }
}
