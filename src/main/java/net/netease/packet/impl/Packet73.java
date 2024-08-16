package net.netease.packet.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import lol.tgformat.accessable.IMinecraft;
import net.minecraft.network.PacketBuffer;
import net.netease.GsonUtil;
import net.netease.PacketProcessor;
import net.netease.gui.GermGameElement;
import net.netease.gui.GermGameGui;
import net.netease.gui.party.GermPartyData;
import net.netease.gui.party.GermPartyGui;
import net.netease.image.ImageLoader;
import net.netease.packet.GermPacket;
import net.netease.packet.impl.Packet26;
import net.netease.utils.CustomMenuButton;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

public class Packet73
        implements GermPacket , IMinecraft {
   private String type;
   private String name;
   private String data;

   @Override
   public void process() {
      Yaml yaml = new Yaml(new Constructor(Map.class, new LoaderOptions()));
      Map data = (Map)yaml.load(this.data);
      JsonElement element = GsonUtil.fromJson(GsonUtil.toJson(data), JsonElement.class);
      if (!element.isJsonObject()) {
         return;
      }
      JsonObject jsonObject = element.getAsJsonObject().getAsJsonObject(this.name);
      if ("mainmenu".equals(this.name)) {
         JsonObject relativeParts = jsonObject.getAsJsonObject("自适应背景").getAsJsonObject("relativeParts");
         JsonObject mainRelativeParts = relativeParts.getAsJsonObject("主分类").getAsJsonObject("relativeParts");
         List<GermGameElement> elements = GermGameGui.INSTANCE.getElements();
         if (elements.isEmpty()) {
            for (Map.Entry<String, JsonElement> entry : mainRelativeParts.entrySet()) {
               if (!entry.getKey().startsWith("subject")) continue;
               JsonObject subjectObj = entry.getValue().getAsJsonObject();
               String string = entry.getKey();
               String defaultPath = subjectObj.get("defaultPath").getAsString();
               defaultPath = defaultPath.substring(defaultPath.indexOf("https"));
               String hoverPath = subjectObj.get("hoverPath").getAsString();
               hoverPath = hoverPath.substring(hoverPath.indexOf("https"));
               String hoverDos = subjectObj.get("hoverDos").getAsString();
               String clickName = subjectObj.get("clickScript").getAsString();
               clickName = clickName.split("'")[1];
               List<String> hoverDoes = Arrays.stream(hoverDos.split("\n")).filter(s -> s.startsWith("§9")).collect(Collectors.toList());
               GermGameElement gameElement = new GermGameElement(string, defaultPath, hoverPath, hoverDoes, clickName);
               elements.add(gameElement);
            }
         }
         mc.threadPool.execute(() -> {
            Packet73 packet73 = this;
            synchronized (packet73) {
               try {
                  ImageLoader.loadImage();
               }
               catch (IOException e) {
                  throw new RuntimeException(e);
               }
            }
         });
      } else if (this.name.endsWith("_effect")) {
         JsonObject guiObject = jsonObject.getAsJsonObject("gui");
         JsonObject amountObject = null;
         JsonObject lastObject = null;
         int index = 0;
         for (Map.Entry<String, JsonElement> entry : guiObject.entrySet()) {
            if (entry.getValue() instanceof JsonObject) {
               if (index == 2) {
                  amountObject = (JsonObject)entry.getValue();
               }
               lastObject = (JsonObject)entry.getValue();
            }
            ++index;
         }
         if (amountObject != null) {
            this.putText(amountObject.getAsJsonArray("texts"));
         }
         if (lastObject == null) {
            return;
         }
         JsonObject relativeParts = lastObject.getAsJsonObject("relativeParts");
         for (Map.Entry entry : relativeParts.entrySet()) {
            JsonObject inRelativeParts = ((JsonElement)entry.getValue()).getAsJsonObject().getAsJsonObject("relativeParts");
            for (Map.Entry<String, JsonElement> inEntry : inRelativeParts.entrySet()) {
               this.putText(inEntry.getValue().getAsJsonObject().getAsJsonArray("texts"));
            }
         }
      } else if (this.name.startsWith("team_")) {
         if ("team_create".equals(this.name)) {
            GermPartyGui.Type type = GermPartyGui.Type.valueOf(this.name.split("_")[1].toUpperCase());
            if (this.check(type)) {
               return;
            }
            for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
               if (!entry.getKey().equals("create") && !entry.getKey().equals("join")) continue;
               JsonObject object = entry.getValue().getAsJsonObject();
               String text = object.getAsJsonArray("texts").get(0).getAsString();
               this.addPartyButton(type, text, object.get("clickScript").getAsString());
            }
         } else if ("team_main".equals(this.name)) {
            GermPartyGui.Type type = GermPartyGui.Type.valueOf(this.name.split("_")[1].toUpperCase());
            if (this.check(type)) {
               return;
            }
            JsonObject buttons = jsonObject.getAsJsonObject("buttons").getAsJsonObject("relativeParts");
            for (Map.Entry<String, JsonElement> entry : buttons.entrySet()) {
               JsonObject object = entry.getValue().getAsJsonObject();
               String text = object.getAsJsonArray("texts").get(0).getAsString();
               this.addPartyButton(type, text, object.get("clickScript").getAsString());
            }
         } else {
            GermPartyGui.SubType type = GermPartyGui.SubType.valueOf(this.name.split("_")[1].toUpperCase());
            Map<GermPartyGui.SubType, GermPartyData> dataMap = GermPartyGui.INSTANCE.getDataMap();
            if (!dataMap.containsKey((Object)type)) {
               dataMap.put(type, new GermPartyData());
            }
            String text = jsonObject.getAsJsonObject("title").getAsJsonArray("texts").get(0).getAsString();
            List<CustomMenuButton> buttons = dataMap.get((Object)type).getButtons();
            buttons.clear();
            dataMap.get((Object)type).setText(text);
            if (jsonObject.has("scroll")) {
               JsonObject scroll = jsonObject.getAsJsonObject("scroll");
               if (!scroll.has("scrollableParts")) {
                  return;
               }
               JsonObject object = scroll.getAsJsonObject("scrollableParts");
               for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
                  JsonObject relativeParts = entry.getValue().getAsJsonObject().getAsJsonObject("relativeParts");
                  JsonArray texts = relativeParts.getAsJsonObject("name").getAsJsonArray("texts");
                  if (texts.size() <= 0) continue;
                  String name = texts.get(0).getAsString();
                  for (Map.Entry<String, JsonElement> e : relativeParts.entrySet()) {
                     if (!e.getKey().startsWith("bt")) continue;
                     String tooltip = "";
                     if (e.getValue().getAsJsonObject().has("tooltip")) {
                        tooltip = e.getValue().getAsJsonObject().getAsJsonArray("tooltip").get(0).getAsString();
                     }
                     if (!e.getValue().getAsJsonObject().has("clickScript")) continue;
                     Runnable clickAction = this.getClickAction(e.getValue().getAsJsonObject().get("clickScript").getAsString());
                     buttons.add(new CustomMenuButton(tooltip + " " + name, clickAction));
                  }
               }
            } else if (jsonObject.has("input")) {
               // empty if block
            }
         }
      }
   }

   private boolean check(GermPartyGui.Type type) {
      if (!GermPartyGui.INSTANCE.getButtons().containsKey((Object)type)) {
         GermPartyGui.INSTANCE.getButtons().put(type, new ArrayList());
      }
      return !GermPartyGui.INSTANCE.getButtons().get((Object)type).isEmpty();
   }

   private void addPartyButton(GermPartyGui.Type type, String text, String script) {
      GermPartyGui.INSTANCE.getButtons().get((Object)type).add(new CustomMenuButton(text, this.getClickAction(script)));
   }

   private Runnable getClickAction(String script) {
      Matcher matcher = this.getMatcher(script);
      if (matcher.find()) {
         String string = new StringBuilder().insert(0, "GUI$").append(this.name).append("@").append(matcher.group(1)).toString();
         return () -> PacketProcessor.INSTANCE.sendPacket(new Packet26(string, matcher.group(2)));
      }
      return null;
   }

   private Matcher getMatcher(String script) {
      String regex = "GuiScreen\\.post\\('([^']+)',\\s*(\\{[^}]+\\})\\);";
      Pattern pattern = Pattern.compile(regex);
      return pattern.matcher(script);
   }

   private void putText(JsonArray texts) {
   }

   @Override
   public void readPacketData(PacketBuffer packetBuffer) {
      this.type = packetBuffer.readStringFromBuffer(Short.MAX_VALUE);
      this.name = packetBuffer.readStringFromBuffer(Short.MAX_VALUE);
      this.data = packetBuffer.readStringFromBuffer(9999999);
   }

   @Override
   public int getPacketId() {
      return 73;
   }
}
