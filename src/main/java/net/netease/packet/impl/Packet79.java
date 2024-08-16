package net.netease.packet.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.network.PacketBuffer;
import net.netease.GsonUtil;
import net.netease.PacketProcessor;
import net.netease.gui.GermGameElement;
import net.netease.gui.GermGameGui;
import net.netease.gui.GermGameSubElement;
import net.netease.gui.party.GermPartyGui;
import net.netease.packet.GermPacket;
import net.netease.packet.impl.Packet04;

public class Packet79
        implements GermPacket {
   private String name;
   private int i;
   private String data;

   @Override
   public void process() {
      String prefix = this.data.split("<->")[0];
      if (prefix.equals("updateOption")) {
         String[] part = this.data.split("<->")[1].split("@");
         String type = part[1];
         String json = part[2];
         if (type.equals("variables")) {
            JsonObject object = GsonUtil.fromJson(json);
            String subject = object.get("subject").getAsString();
            for (GermGameElement element : GermGameGui.INSTANCE.getElements()) {
               if (!element.getName().replace("fight_team", "team_fight").split("subject_")[1].equalsIgnoreCase(subject)) continue;
               GermGameGui.INSTANCE.setCurrentElement(element);
               GermGameGui.INSTANCE.getSwitchScreenAnim().setState(true);
            }
         } else if (type.equals("data")) {
            if (!GermGameGui.INSTANCE.getCurrentElement().getSubElements().isEmpty()) {
               return;
            }
            JsonObject object = GsonUtil.fromJson(json);
            int index = 0;
            for (JsonElement element : object.getAsJsonArray("subs")) {
               if (!element.isJsonObject()) continue;
               JsonObject gameObj = element.getAsJsonObject();
               String sid = gameObj.get("sid").getAsString();
               String name = gameObj.get("name").getAsString().replace("&", "§");
               List<String> desc = Arrays.stream(gameObj.get("desc").getAsString().replace("&", "§").replace("✦", "☆").split("\n")).collect(Collectors.toList());
               GermGameSubElement gameSubElement = new GermGameSubElement(index, sid, name, desc);
               GermGameGui.INSTANCE.getCurrentElement().getSubElements().add(gameSubElement);
               ++index;
            }
         }
      } else if (prefix.equals("openChild")) {
         String[] part = this.data.split("<->")[1].split("_");
         if (part[0].equals("team")) {
            GermPartyGui.SubType type = GermPartyGui.SubType.valueOf(part[1].toUpperCase());
            Packet04 packet = new Packet04();
            packet.setMessage(this.name);
            packet.setMessage3(this.data.split("<->")[1]);
            packet.setN2(1);
            PacketProcessor.INSTANCE.sendPacket(packet);
            type.setCurrent();
         }
      } else if (prefix.equals("invalid")) {
         this.name = this.data.split("<->")[1].split("@")[0];
      }
   }

   @Override
   public void readPacketData(PacketBuffer packetBuffer) {
      this.name = packetBuffer.readStringFromBuffer(Short.MAX_VALUE);
      this.i = packetBuffer.readInt();
      this.data = packetBuffer.readStringFromBuffer(3276700);
   }

   @Override
   public int getPacketId() {
      return 79;
   }
}
