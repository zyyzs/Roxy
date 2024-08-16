package net.netease.gui;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import net.netease.font.FontManager;
import net.netease.utils.*;
import net.optifine.util.MathUtils;
import org.lwjgl.input.Mouse;

@Getter
@Setter
public class GermGameGui extends GuiScreen {
   public static GermGameGui INSTANCE = new GermGameGui();
   private float x;
   private float y;
   private float width;
   private float height;
   private final ResourceLocation germLogo = new ResourceLocation("quicksand/germ.png");
   private final List<GermGameElement> elements = new ArrayList<>();
   private GermGameElement currentElement;
   private String guiName;
   private final Animation switchScreenAnim = new DecelerateAnimation(350, 1.0).setDirection(Direction.BACKWARDS);
   private final Animation backHoverAnim = new DecelerateAnimation(270, 1.0).setDirection(Direction.BACKWARDS);
   private final DragComponent dragComponent = new DragComponent();
   private final Scroll scroll = new Scroll();
   private ScaledResolution scaledResolution;
   private RippleAnimation backButtonRipple = new RippleAnimation();

   @Override
   public void initGui() {
      this.scaledResolution = new ScaledResolution(this.mc);
      this.width = 470.0f;
      this.height = 290.0f;
      this.x = (float)this.scaledResolution.getScaledWidth() / 2.0f - this.width / 2.0f;
      this.y = (float)this.scaledResolution.getScaledHeight() / 2.0f - this.height / 2.0f;
   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.dragComponent.setX(this.x);
      this.dragComponent.setY(this.y);
      this.dragComponent.setWidth(this.width);
      this.dragComponent.setHeight(this.height);
      this.dragComponent.setLimitHeight(this.height);
      this.dragComponent.handleDrag(mouseX, mouseY, 0, false);
      this.x = this.dragComponent.getX();
      this.y = this.dragComponent.getY();
      RoundedUtils.drawRound(this.x, this.y, this.width, this.height, 7.0f, new Color(0, 0, 0, 120));
      float switchOutput = (float)this.switchScreenAnim.getOutput();
      float originalX = this.x;
      this.x = MathUtils.interpolateFloat(originalX, originalX - this.width, switchOutput);
      RenderUtil.startGlScissor((int)originalX, (int)this.y, (int)this.width, (int)this.height);
      FontManager.arial64.drawStringWithShadow("Germ Game", this.x + 20.0f, this.y + 8.0f, -1);
      int count = 0;
      float offsetX = 0.0f;
      float offsetY = 0.0f;
      for (GermGameElement element : this.elements) {
         float x = this.x + 20.0f + offsetX;
         float y = this.y + 60.0f + offsetY;
         float width = 110.0f;
         float height = 100.0f;
         boolean hovering = RenderUtil.isHovering(x, y, width, height, mouseX, mouseY);
         element.setRunnable(() -> {
            if (RenderUtil.isHovering(x, y, width, height, mouseX, mouseY)) {
               element.click(this.guiName);
            }
         });
         if (element.getDefaultImage() != null && element.getGifImage() != null) {
            this.mc.getTextureManager().bindTexture(hovering ? element.getCurrentGifImage() : element.getDefaultImage());
            RoundedUtils.drawRoundTextured(x, y, width, height, 5.0f, 1.0f);
         }
         if (hovering && !this.switchScreenAnim.isState()) {
            float textOffsetY = 0.0f;
            for (String hoverDos : element.getHoverDos()) {
               FontManager.arial18.drawStringWithShadow(hoverDos, mouseX + 15, (float)(mouseY - (FontManager.arial18.getHeight() + 4) * 2) + textOffsetY, -1);
               textOffsetY += (float)(FontManager.arial18.getHeight() + 4);
            }
         }
         if (++count % 3 == 0) {
            offsetY += height + 15.0f;
            offsetX = 0.0f;
            continue;
         }
         offsetX += width + 50.0f;
      }
      RenderUtil.stopGlScissor();
      float doubleX = this.x + this.width;
      if (this.switchScreenAnim.isState() || !this.switchScreenAnim.isDone()) {
         RenderUtil.startGlScissor((int)originalX, (int)this.y, (int)this.width, (int)this.height);
         RoundedUtils.drawRound(doubleX + 10.0f, this.y + 10.0f, 20.0f, 20.0f, 10.0f, new Color(0, 0, 0, 150));
         this.backButtonRipple.draw(() -> RoundedUtils.drawRound(doubleX + 10.0f, this.y + 10.0f, 20.0f, 20.0f, 10.0f, Color.WHITE));
         this.backHoverAnim.setState(RenderUtil.isHovering(doubleX + 20.0f - (float)FontManager.icon22.getStringWidth("y") / 2.0f, this.y + 20.0f - (float)(FontManager.icon22.getHeight() / 2), FontManager.icon22.getStringWidth("y"), FontManager.icon22.getHeight(), mouseX, mouseY));
         Color hoverColor = ColorUtil.interpolateColorC(Color.WHITE, ColorUtil.darker(Color.WHITE, 0.6f), (float)this.backHoverAnim.getOutput());
         if (this.backHoverAnim.isState() && this.switchScreenAnim.isState() && Mouse.isButtonDown(0)) {
            this.backButtonRipple.mouseClicked(mouseX, mouseY);
            this.switchScreenAnim.setState(false);
         }
         FontManager.icon22.drawStringWithShadow("y", doubleX + 20.0f - (float)FontManager.icon22.getStringWidth("y") / 2.0f, this.y + 22.0f - (float)(FontManager.icon22.getHeight() / 2), hoverColor.getRGB());
         FontManager.arial22.drawCenteredStringWithShadow("Sub Game", doubleX + this.width / 2.0f, this.y + 10.0f, -1);
         if (this.currentElement != null) {
            float startX = doubleX + 30.0f;
            for (GermGameSubElement element : this.currentElement.getSubElements()) {
               float x = startX + this.scroll.getAnimationTarget();
               float y = this.y + 50.0f;
               float width = 100.0f;
               float height = 180.0f;
               boolean hovering = RenderUtil.isHovering(x, y, width, height, mouseX, mouseY);
               element.getHoverAnim().setState(hovering);
               element.setRunnable(() -> {
                  if (RenderUtil.isHovering(x, y, width, height, mouseX, mouseY)) {
                     element.joinGame(this.guiName);
                  }
               });
               Color color = new Color(0, 0, 0, 150);
               Color hovColor = ColorUtil.interpolateColorC(color, new Color(0, 0, 0, 200), (float)element.getHoverAnim().getOutput());
               RoundedUtils.drawRound(x, y, width, height, 7.0f, hovColor);
               FontManager.arial18.drawCenteredStringWithShadow(element.getName(), x + width / 2.0f, y + 12.0f, -1);
               float descOffsetY = 0.0f;
               for (String s : element.getDesc()) {
                  FontManager.arial10.drawStringWithShadow(s, x + 5.0f, y + height - (float)(element.getDesc().size() * 10) + descOffsetY, -1);
                  descOffsetY += 10.0f;
               }
               element.getAnimation().draw(() -> RoundedUtils.drawRound(x, y, width, height, 7.0f, Color.WHITE));
               startX += width + 20.0f;
            }
            float max = startX - doubleX - this.width;
            this.scroll.setMaxTarget(max);
            if (RenderUtil.isHovering(doubleX, this.y, this.width, this.height, mouseX, mouseY)) {
               this.scroll.use();
            }
            this.scroll.animate();
            float padding = 10.0f;
            float spacingWidth = this.width - 5.0f;
            float scrollWidth = max <= 0.0f ? spacingWidth : 50.0f * (spacingWidth / this.scroll.getMaxTarget()) - padding;
            float scrollX = -this.scroll.getAnimationTarget() / this.scroll.getMaxTarget() * (spacingWidth - scrollWidth) + padding / 4.0f;
            RoundedUtils.drawRound(doubleX + scrollX, this.y + this.height - 8.0f, scrollWidth, 5.0f, 2.0f, new Color(0, 0, 0, 150));
         }
         RenderUtil.stopGlScissor();
      }
      this.x = originalX;
   }

   @Override
   protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
      this.dragComponent.handleDrag(mouseX, mouseY, mouseButton, true);
      if (RenderUtil.isHovering(this.x, this.y, this.width, this.height, mouseX, mouseY)) {
         for (GermGameElement germGameElement : this.elements) {
            if (germGameElement.getRunnable() == null) continue;
            germGameElement.getRunnable().run();
         }
         if (this.currentElement != null) {
            for (GermGameSubElement germGameSubElement : this.currentElement.getSubElements()) {
               germGameSubElement.getAnimation().mouseClicked(mouseX, mouseY);
               if (germGameSubElement.getRunnable() == null) continue;
               germGameSubElement.getRunnable().run();
            }
         }
      }
   }

}
