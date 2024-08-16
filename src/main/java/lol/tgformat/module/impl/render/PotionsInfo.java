package lol.tgformat.module.impl.render;

import lol.tgformat.api.event.Listener;
import lol.tgformat.events.render.Render2DEvent;
import lol.tgformat.module.Module;
import lol.tgformat.module.ModuleType;
import lol.tgformat.ui.font.FontUtil;
import lol.tgformat.ui.utils.RenderUtil;
import lol.tgformat.utils.render.GlowUtils;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.netease.font.FontManager;
import net.netease.utils.ContinualAnimation;
import net.netease.utils.Direction;
import net.netease.utils.EaseBackIn;
import net.netease.utils.RoundedUtils;
import tech.skidonion.obfuscator.annotations.Renamer;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

@Renamer
@StringEncryption
public class PotionsInfo extends Module {
    private int maxString = 0;
    private final Map<Integer, Integer> potionMaxDurations = new HashMap<>();
    private final ContinualAnimation widthanimation = new ContinualAnimation();
    private final ContinualAnimation heightanimation = new ContinualAnimation();
    private final EaseBackIn animation = new EaseBackIn(200, 1.0, 1.3f);
    List<PotionEffect> effects = new ArrayList<>();

    public PotionsInfo() {
        super("PotionsInfo", ModuleType.Render);
    }

    public int getHieght(){
        int h;
        if (mc.thePlayer.getActivePotionEffects().size() == 1){
            h = 48;
        }else {
            h = 38;
        }
        return h * mc.thePlayer.getActivePotionEffects().size();
    }

    @Listener
    
    public void onRender2D(Render2DEvent event) {
        this.effects = mc.thePlayer.getActivePotionEffects().stream().sorted(Comparator.comparingInt(it -> (int) FontUtil.tenacityFont18.getStringWidth(this.get(it)))).collect(Collectors.toList());
        int x = 5;
        int y2 = 350;
        int offsetX = 21;
        int offsetY = 14;
        int i2 = 16;
        ArrayList<Integer> needRemove = new ArrayList<>();
        for (Map.Entry<Integer, Integer> entry : this.potionMaxDurations.entrySet()) {
            if (mc.thePlayer.getActivePotionEffect(Potion.potionTypes[entry.getKey()]) != null) continue;
            needRemove.add(entry.getKey());
        }
        for (int id : needRemove) {
            this.potionMaxDurations.remove(id);
        }
        for (PotionEffect effect : this.effects) {
            if (this.potionMaxDurations.containsKey(effect.getPotionID()) && this.potionMaxDurations.get(effect.getPotionID()) >= effect.getDuration()) continue;
            this.potionMaxDurations.put(effect.getPotionID(), effect.getDuration());
        }
        float width = !this.effects.isEmpty() ? (float)Math.max(50 + FontUtil.tenacityFont18.getStringWidth(this.get(this.effects.get(this.effects.size() - 1))), 60 + FontUtil.tenacityFont18.getStringWidth(this.get(this.effects.get(this.effects.size() - 1)))) : 0.0f;
        float height = this.effects.size() * 25;
        this.widthanimation.animate(width, 20);
        this.heightanimation.animate(height, 20);
        if (mc.currentScreen instanceof GuiChat && this.effects.isEmpty()) {
            this.animation.setDirection(Direction.FORWARDS);
        } else if (!(mc.currentScreen instanceof GuiChat)) {
            this.animation.setDirection(Direction.BACKWARDS);
        }
        RenderUtil.scaleStart(x + 50, y2 + 15, (float)this.animation.getOutput());
        FontUtil.tenacityFont18.drawStringWithShadow("Potion Example", x + 52.0f - (float) FontUtil.tenacityFont18.getStringWidth("Potion Example") / 2, (float)(y2 + 18 - FontUtil.tenacityFont18.getHeight() / 2), new Color(255, 255, 255, 80).getRGB());
        RenderUtil.scaleEnd();
        if (this.effects.isEmpty()) {
            this.maxString = 0;
        }
        if (!this.effects.isEmpty()) {
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            GlStateManager.disableLighting();
            int l2 = 24;
            FontManager.arial24.drawStringWithShadow("PotionList", x + 6, y2 - 14, HUD.color(0).getRGB());
            RoundedUtils.drawRound(x, y2 - 20, this.widthanimation.getOutput() - 10, getHieght(), 3, new Color(25,25,25,30));
            GlowUtils.drawGlow(x, y2 - 20, this.widthanimation.getOutput() - 10, getHieght(), 6, new Color(25,25,25,90));
            RoundedUtils.drawRound(x, y2 - 13, 2, 7, 0, HUD.getClientColors().getFirst());
            for (PotionEffect potioneffect : this.effects) {
                //RoundedUtils.drawRound(x, y2 + i2 - offsetY, this.widthanimation.getOutput(), 1.0f, 1.0f, new Color(126,0, 252, 255));
                //RoundedUtils.drawRound(x, y2 + i2 - offsetY + 1.5f, (int)this.widthanimation.getOutput(), 24.0f, 0.0f, new Color(19, 19, 19, 102));

                Potion potion = Potion.potionTypes[potioneffect.getPotionID()];
                GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
                if (potion.hasStatusIcon()) {
                    mc.getTextureManager().bindTexture(new ResourceLocation("textures/gui/container/inventory.png"));
                    int i1 = potion.getStatusIconIndex();
                    GlStateManager.enableBlend();
                    mc.ingameGUI.drawTexturedModalRect(x + offsetX - 17, y2 + i2 - offsetY + 2, 0 + i1 % 8 * 18, 198 + i1 / 8 * 18, 18, 18);
                }
                float potionDurationRatio = (float)potioneffect.getDuration() / (potionMaxDurations.get(potioneffect.getPotionID()) != null ? potionMaxDurations.get(potioneffect.getPotionID()) : 1);
                String s2 = Potion.getDurationString(potioneffect);
                String s1 = this.get(potioneffect);
                FontUtil.tenacityFont18.drawStringWithShadow(s1, x + offsetX + 7, y2 + i2 - offsetY + 2, -1);
                FontUtil.tenacityFont18.drawStringWithShadow(s2, x + offsetX + 7, y2 + i2 + 11 - offsetY + 2, -1);
                RoundedUtils.circle(x + offsetX - 20, y2 + i2 - offsetY - 1, 24, 360, false, new Color(0, 0, 0, 70));
                RoundedUtils.circle(x + offsetX - 20, y2 + i2 - offsetY - 1, 24, potionDurationRatio * 360, false, Color.white);
                i2 = (int)((double)i2 + (double)l2 * 1.2);
                if (this.maxString >= mc.fontRendererObj.getStringWidth(s1)) continue;
                this.maxString = mc.fontRendererObj.getStringWidth(s1);
            }
        }
    }

    
    private String get(PotionEffect potioneffect) {
        Potion potion = Potion.potionTypes[potioneffect.getPotionID()];
        String s1 = I18n.format(potion.getName());
        s1 = s1 + " " + this.intToRomanByGreedy(potioneffect.getAmplifier() + 1);
        return s1;
    }

    
    private String intToRomanByGreedy(int num) {
        int[] values = new int[]{1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};
        String[] symbols = new String[]{"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"};
        StringBuilder stringBuilder = new StringBuilder();
        for (int i2 = 0; i2 < values.length && num >= 0; ++i2) {
            while (values[i2] <= num) {
                num -= values[i2];
                stringBuilder.append(symbols[i2]);
            }
        }
        return stringBuilder.toString();
    }
}

