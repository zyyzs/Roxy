package net.netease.packet.impl;

import lol.tgformat.accessable.IMinecraft;
import net.minecraft.network.PacketBuffer;
import net.netease.PacketProcessor;
import net.netease.gui.GermGameGui;
import net.netease.gui.party.GermPartyGui;
import net.netease.packet.GermPacket;
import net.netease.packet.impl.Packet04;

public class Packet76
        implements GermPacket, IMinecraft {
   private String name;

   @Override
   public void process() {
      PacketProcessor.INSTANCE.sendPacket(new Packet04("germ_gui_loading"));
      PacketProcessor.INSTANCE.sendPacket(new Packet04(this.name));
      switch (this.name) {
         case "mainmenu": {
            GermGameGui.INSTANCE.setGuiName(this.name);
            mc.addScheduledTask(() -> mc.displayGuiScreen(GermGameGui.INSTANCE));
            break;
         }
         case "team_create": {
            GermPartyGui.INSTANCE.setCurrentType(GermPartyGui.Type.CREATE);
            mc.displayGuiScreen(GermPartyGui.INSTANCE);
            break;
         }
         case "team_main": {
            GermPartyGui.INSTANCE.setCurrentType(GermPartyGui.Type.MAIN);
            mc.displayGuiScreen(GermPartyGui.INSTANCE);
         }
      }
   }

   @Override
   public void readPacketData(PacketBuffer packetBuffer) {
      this.name = packetBuffer.readStringFromBuffer(Short.MAX_VALUE);
   }

   @Override
   public int getPacketId() {
      return 76;
   }
}
