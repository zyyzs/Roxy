package net.netease.image;

import java.io.IOException;
import net.netease.gui.GermGameElement;
import net.netease.gui.GermGameGui;

public class ImageLoader {
   public static boolean loadedImage;

   public static void loadImage() throws IOException {
      if (!loadedImage) {
         for (GermGameElement element : GermGameGui.INSTANCE.getElements()) {
            element.loadTexture();
            element.loadHoverTexture();
         }
         loadedImage = true;
      }
   }
}
