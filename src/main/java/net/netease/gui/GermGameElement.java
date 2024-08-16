package net.netease.gui;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import lol.tgformat.utils.client.LogUtil;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.util.ResourceLocation;
import net.netease.GsonUtil;
import net.netease.PacketProcessor;
import net.netease.packet.impl.Packet26;
import net.netease.utils.TimeUtil;
import org.apache.commons.io.IOUtils;

@Getter
public class GermGameElement {
   private final List<GermGameSubElement> subElements = new ArrayList<GermGameSubElement>();
   private final String name;
   private final String defaultPath;
   private final String hoverPath;
   private final List<String> hoverDos;
   private ResourceLocation defaultImage;
   private ResourceLocation hoverImage;
   private GifDecoder.GifImage gifImage;
   private final List<Integer> delayList = new ArrayList<>();
   private final TimeUtil updateTimer = new TimeUtil();
   private int imageCount;
   @Setter
   private Runnable runnable;
   private final String clickName;

   public GermGameElement(String name, String defaultPath, String hoverPath, List<String> hoverDos, String clickName) {
      this.name = name;
      this.defaultPath = defaultPath;
      this.hoverPath = hoverPath;
      this.hoverDos = hoverDos;
      this.clickName = clickName;
   }

   public synchronized void loadTexture() throws IOException {
      this.defaultImage = TextureUtil.loadTextureFormURL(this.defaultPath);
   }

   public synchronized void loadHoverTexture() {
      try {
         URI url = new URI(this.hoverPath);
         URLConnection urlConnection = url.toURL().openConnection();
         InputStream inputStream = urlConnection.getInputStream();
         this.gifImage = GifDecoder.read(IOUtils.toByteArray(inputStream));
         for (int i = 0; i < this.gifImage.getFrameCount(); ++i) {
            BufferedImage bufferedImage = this.gifImage.getFrame(i);
            ResourceLocation resourceLocation = new ResourceLocation(String.valueOf(this.hoverPath.hashCode() + i));
            Minecraft.getMinecraft().addScheduledTask(() -> Minecraft.getMinecraft().getTextureManager().loadTexture(resourceLocation, new DynamicTexture(bufferedImage)));
            this.delayList.add(this.gifImage.getDelay(i));
         }
         inputStream.close();
      }
      catch (Exception e) {
         LogUtil.print(e.getMessage());
      }
   }

   public ResourceLocation getCurrentGifImage() {
      if (this.imageCount >= this.gifImage.getFrameCount()) {
         this.imageCount = 0;
      }
      if (!this.delayList.isEmpty() && this.updateTimer.hasTimeElapsed(this.delayList.get(this.imageCount) * 10)) {
         this.hoverImage = new ResourceLocation(String.valueOf(this.hoverPath.hashCode() + this.imageCount));
         this.updateTimer.reset();
         ++this.imageCount;
      }
      return this.hoverImage;
   }

   public void click(String guiName) {
      HashMap<String, Integer> data = new HashMap<>();
      data.put("click", 1);
      String json = GsonUtil.toJson(data);
      String message = new StringBuilder().insert(0, "GUI$").append(guiName).append("@").append(this.clickName).toString();
      PacketProcessor.INSTANCE.sendPacket(new Packet26(message, json));
   }

}
