package net.netease.packet.impl;

import net.minecraft.network.PacketBuffer;
import net.netease.packet.GermPacket;

public class Packet03
        implements GermPacket {
   private int key;
   private boolean state;

   public Packet03() {
   }

   @Override
   public void writePacketData(PacketBuffer packetBuffer) {
      packetBuffer.writeInt(this.key);
      packetBuffer.writeBoolean(this.state);
   }

   @Override
   public int getPacketId() {
      return 3;
   }

   public Packet03(int key, boolean state) {
      this.key = key;
      this.state = state;
   }
}
