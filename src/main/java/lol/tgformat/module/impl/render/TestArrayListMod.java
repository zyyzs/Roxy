package lol.tgformat.module.impl.render;

import lol.tgformat.Client;
import lol.tgformat.api.event.Listener;
import lol.tgformat.events.render.Render2DEvent;
import lol.tgformat.module.Module;
import lol.tgformat.module.ModuleManager;
import lol.tgformat.module.ModuleType;
import lol.tgformat.module.values.impl.BooleanSetting;
import lol.tgformat.module.values.impl.ColorSetting;
import lol.tgformat.module.values.impl.NumberSetting;
import lol.tgformat.ui.drag.Dragging;
import lol.tgformat.ui.utils.RoundedUtil;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.EnumChatFormatting;
import net.netease.font.FontManager;
import net.netease.utils.ColorUtil;
import net.netease.utils.RapeMasterFontManager;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;

/**
 * @author LangYa
 * @since 2024/10/18 23:35
 */
public class TestArrayListMod extends Module {
    public final BooleanSetting importantModules = new BooleanSetting("Important", false);
    private final NumberSetting spacing = new NumberSetting("Spacing", 3, 5, 1, 1);
    private final BooleanSetting background = new BooleanSetting("BackHround",true);
    private final NumberSetting opacity = new NumberSetting("BgAlpha", 0.25, 1, 0.0, .05);
    private final NumberSetting radius = new NumberSetting("BgRadius", 3, 17.5, 1, .5);
    private final ColorSetting bgColor = new ColorSetting("BgColor",new Color(0,0,0,80));
    private final ColorSetting textColor = new ColorSetting("TextColor",new Color(0,0,0));
    private final NumberSetting textFadeSpeed = new NumberSetting("TextColorFadeSpeed", 15, 30, 1, 1);

    private final Dragging arraylistDrag = Client.instance.createDrag(this,"testarraylist", 2, 1);

    public TestArrayListMod() {
        super("TestArrayList", ModuleType.Render);
        opacity.addParent(background, BooleanSetting::isEnabled);
        radius.addParent(background, BooleanSetting::isEnabled);
        bgColor.addParent(background, BooleanSetting::isEnabled);
    }

    @Listener
    public void onRender2D(Render2DEvent event) {
        int y1 = 4;
        int count = 0;
        ScaledResolution sr = event.getScaledResolution();
        float y = arraylistDrag.getY();
        double xValue = sr.getScaledWidth() - (arraylistDrag.getX());
        
        boolean flip = xValue <= sr.getScaledWidth() / 2f;
        RapeMasterFontManager font = FontManager.Tahoma20;
   
        ArrayList<Module> enabledModules = new ArrayList<>();
        for (Module module : ModuleManager.getModules()) {
            if (importantModules.isEnabled() && module.getCategory() == ModuleType.Render) continue;
            if (module.isState()) enabledModules.add(module);
        }
        enabledModules.sort(Comparator.comparingInt(o -> -font.getStringWidth(o.getDisplayName())));

        for (Module module : enabledModules) {
            String displayText = module.getDisplayName();
            float textWidth = font.getStringWidth(displayText);
            float x = (float) (flip ? xValue : sr.getScaledWidth() - (textWidth + arraylistDrag.getX()));
            if (background.isEnabled()) {
                float offset = 5;
                Color color = bgColor.getColor();
                float width2 = font.getStringWidth(displayText);
                RoundedUtil.drawRound(x - 2, y + y1, width2 + offset, font.getHeight(),radius.getValue().intValue(), ColorUtil.applyOpacity(color, opacity.getValue().floatValue()));
            }
            font.drawStringWithShadow(displayText, x , y + y1 + 2F, ColorUtil.fade(textFadeSpeed.getValue().intValue(),count,textColor.getColor(),255).getRGB());
            count++;
            y1 += font.getHeight() + spacing.getValue().intValue();
        }
    }

}
