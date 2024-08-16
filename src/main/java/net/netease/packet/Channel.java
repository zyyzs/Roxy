package net.netease.packet;

import lol.tgformat.accessable.IMinecraft;
import lombok.Getter;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C17PacketCustomPayload;

@Getter
public class Channel implements IMinecraft {
   private final String name;

   public void sendToServer(String name, PacketBuffer buffer) {
      mc.thePlayer.sendQueue.addToSendQueue(new C17PacketCustomPayload(name, buffer));
   }

   public Channel(String name) {
      this.name = name;
   }

}
