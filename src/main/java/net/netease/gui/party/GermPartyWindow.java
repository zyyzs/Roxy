package net.netease.gui.party;

import java.awt.Color;
import java.io.IOException;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.netease.font.FontManager;
import net.netease.gui.DragComponent;
import net.netease.gui.Scroll;
import net.netease.gui.party.GermPartyGui;
import net.netease.utils.CustomMenuButton;
import net.netease.utils.RapeMasterFontManager;
import net.netease.utils.RenderUtil;
import net.netease.utils.RoundedUtils;

@Getter
@Setter
public class GermPartyWindow {
   private final String text;
   private final GermPartyGui.SubType type;
   private final List<CustomMenuButton> buttons;
   private float x;
   private float y;
   private float width;
   private float height;
   private final DragComponent dragComponent = new DragComponent();
   private final Scroll scroll = new Scroll();
   private GuiScreen prevGui;

   public GermPartyWindow(String text, GermPartyGui.SubType type, List<CustomMenuButton> buttons, GuiScreen prevGui) {
      this.text = text;
      this.type = type;
      this.buttons = buttons;
      this.prevGui = prevGui;
   }

   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      RapeMasterFontManager font16 = FontManager.arial16;
      RapeMasterFontManager bold18 = FontManager.arial18;
      this.dragComponent.setX(this.x);
      this.dragComponent.setY(this.y);
      this.dragComponent.setWidth(this.width);
      this.dragComponent.setHeight(this.height);
      this.dragComponent.setLimitHeight(this.height);
      this.dragComponent.handleDrag(mouseX, mouseY, 0, false);
      this.x = this.dragComponent.getX();
      this.y = this.dragComponent.getY();
      RoundedUtils.drawRound(this.x, this.y, this.width, this.height, 7.0f, new Color(0, 0, 0, 120));
      bold18.drawString(this.text, this.x + 6.0f, this.y + 8.0f, -1);
      bold18.drawString("●", this.x + this.width - 14.0f, this.y + 8.0f, new Color(233, 30, 99).getRGB());
      RenderUtil.startGlScissor((int)this.x, (int)this.y + 22, (int)this.width, (int)this.height - 22);
      float offsetY = 0.0f;
      for (CustomMenuButton button : this.buttons) {
         button.setFont(font16);
         button.setWidth(font16.getStringWidth(button.getText()) + 15);
         button.setHeight(font16.getHeight() + 10);
         button.setX(this.x + this.width / 2.0f - button.getWidth() / 2.0f);
         button.setY(this.y + 22.0f + offsetY + this.scroll.getAnimationTarget());
         button.drawScreen(mouseX, mouseY, partialTicks);
         offsetY += button.getHeight() + 5.0f;
      }
      this.scroll.setMaxTarget(offsetY - this.height);
      if (RenderUtil.isHovering(this.x, this.y, this.width, this.height, mouseX, mouseY)) {
         this.scroll.use();
      }
      this.scroll.animate();
      this.scroll.use();
      RenderUtil.stopGlScissor();
   }

   protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
      this.dragComponent.handleDrag(mouseX, mouseY, mouseButton, true);
      if (RenderUtil.isHovering(this.x + this.width - 14.0f, this.y + 8.0f, 12.0f, 12.0f, mouseX, mouseY)) {
         Minecraft.getMinecraft().displayGuiScreen(this.prevGui);
      }
      if (!RenderUtil.isHovering(this.x, this.y + 22.0f, this.width, this.height, mouseX, mouseY)) {
         return;
      }
      for (CustomMenuButton button : this.buttons) {
         button.mouseClicked(mouseX, mouseY, mouseButton);
      }
   }

}
