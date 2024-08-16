package net.netease.packet.impl;

import net.minecraft.network.PacketBuffer;
import net.netease.packet.GermPacket;

public class Packet04
        implements GermPacket {
   private String message;
   private String message3;
   private int n;
   private int n2;

   public Packet04(String message) {
      this.message = message;
      this.message3 = message;
   }

   public Packet04(String message, int n) {
      this.message = message;
      this.message3 = message;
      this.n = n;
   }

   public Packet04() {
   }

   @Override
   public void writePacketData(PacketBuffer packetBuffer) {
      packetBuffer.writeInt(this.n);
      packetBuffer.writeInt(this.n2);
      packetBuffer.writeString(this.message);
      packetBuffer.writeString(this.message);
      packetBuffer.writeString(this.message3);
   }

   @Override
   public int getPacketId() {
      return 4;
   }

   public Packet04(String message, String message3, int n, int n2) {
      this.message = message;
      this.message3 = message3;
      this.n = n;
      this.n2 = n2;
   }

   public void setMessage(String message) {
      this.message = message;
   }

   public void setMessage3(String message3) {
      this.message3 = message3;
   }

   public void setN(int n) {
      this.n = n;
   }

   public void setN2(int n2) {
      this.n2 = n2;
   }
}
