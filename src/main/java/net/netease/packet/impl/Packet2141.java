package net.netease.packet.impl;

import com.google.gson.JsonObject;
import lombok.Getter;
import net.minecraft.network.PacketBuffer;
import net.netease.GsonUtil;
import net.netease.packet.GermPacket;

@Getter
public class Packet2141
        implements GermPacket {
   private String data;

   @Override
   public void process() {
      JsonObject object = GsonUtil.fromJson(this.data);
      if (object.get("hudMsgType").getAsString().equals("CENTER_UP_SCROLL")) {
         int priority = object.get("priority").getAsInt();
         String string = object.get("contents").getAsString();
      }
   }

   @Override
   public void readPacketData(PacketBuffer packetBuffer) {
      this.data = packetBuffer.readStringFromBuffer(Short.MAX_VALUE);
   }

   @Override
   public int getPacketId() {
      return 2141;
   }

}
