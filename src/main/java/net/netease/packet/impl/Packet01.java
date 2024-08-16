package net.netease.packet.impl;

import lombok.Getter;
import net.minecraft.network.PacketBuffer;
import net.netease.packet.GermPacket;

@Getter
public class Packet01
        implements GermPacket {
   private String key;

   public Packet01() {
   }

   @Override
   public void writePacketData(PacketBuffer packetBuffer) {
      packetBuffer.writeString(this.key);
   }

   @Override
   public int getPacketId() {
      return 1;
   }

   public Packet01(String key) {
      this.key = key;
   }

}
