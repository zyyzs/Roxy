package net.netease.packet.impl;

import net.minecraft.network.PacketBuffer;
import net.netease.packet.GermPacket;

public class Packet26
        implements GermPacket {
   private String a;
   private String json;

   public Packet26() {
   }

   @Override
   public void writePacketData(PacketBuffer packetBuffer) {
      packetBuffer.writeString(this.a);
      packetBuffer.writeString(this.json);
   }

   @Override
   public int getPacketId() {
      return 26;
   }

   public Packet26(String string, String json) {
      this.a = string;
      this.json = json;
   }
}
