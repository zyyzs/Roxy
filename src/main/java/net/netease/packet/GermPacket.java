package net.netease.packet;

import net.minecraft.network.PacketBuffer;

public interface GermPacket {
   default public void process() {
   }

   default public void writePacketData(PacketBuffer packetBuffer) {
   }

   default public void readPacketData(PacketBuffer packetBuffer) {
   }

   public int getPacketId();
}
