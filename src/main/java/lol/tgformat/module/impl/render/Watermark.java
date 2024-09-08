package lol.tgformat.module.impl.render;

import com.mojang.realmsclient.gui.ChatFormatting;
import lol.tgformat.Client;
import lol.tgformat.api.event.Listener;
import lol.tgformat.events.PreUpdateEvent;
import lol.tgformat.events.render.Render2DEvent;
import lol.tgformat.module.Module;
import lol.tgformat.module.ModuleType;
import lol.tgformat.module.values.impl.BooleanSetting;
import lol.tgformat.module.values.impl.ModeSetting;
import lol.tgformat.ui.font.Pair;
import lol.tgformat.utils.network.ServerUtil;
import lol.tgformat.utils.render.BlurUtil;
import lol.tgformat.utils.render.DrawUtil;
import lol.tgformat.utils.render.GlowUtils;
import lol.tgformat.utils.render.GradientUtil;

import lol.tgformat.verify.GuiLogin;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;
import net.netease.font.FontManager;
import net.netease.utils.ColorUtil;
import net.netease.utils.RenderUtil;
import net.netease.utils.RoundedUtils;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

import static lol.tgformat.module.impl.render.HUD.clientName;
import static lol.tgformat.ui.clickgui.Utils.*;

/**
 * @Author KuChaZi
 * @Date 2024/8/8 21:54
 * @ClassName: Watermark
 */
@StringEncryption
public class Watermark extends Module {
    private final ModeSetting mode = new ModeSetting("Mode", "Text", "Text", "Test", "Test2", "Logo", "Xylitol", "Naven","Exhibition");
    private final BooleanSetting username = new BooleanSetting("UserName", true);
    private final BooleanSetting version = new BooleanSetting("Version", true);
    private final BooleanSetting times = new BooleanSetting("Time", true);
    private final BooleanSetting fpss = new BooleanSetting("FPS", true);
    private final BooleanSetting change = new BooleanSetting("Change", false);
    private long lastUpdateTime = 0;
    private int i = 0;


    public Watermark() {
        super("Watermark", ModuleType.Render);
        username.addParent(mode, m -> mode.is("Naven"));
        version.addParent(mode, m -> mode.is("Naven"));
        times.addParent(mode, m -> mode.is("Naven"));
        fpss.addParent(mode, m -> mode.is("Naven"));
        change.addParent(mode, s -> mode.is("Xylitol"));
    }

    @Listener
    public void onRender2D(Render2DEvent event) {
        if (isNull()) return;
        onRender();
    }

    @Listener
    public void onUpdate(PreUpdateEvent e){
        String n = HUD.name();
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastUpdateTime >= 900) {
            if (i >= n.length()) {
                i = 0;
            } else {
                i += 1;
            }

            lastUpdateTime = currentTime;
        }
    }

    private void onRender() {
        Pair<Color, Color> clientColors = HUD.getClientColors();
        switch (mode.getMode()) {
            case "Text": {
                float xVal = 6f;
                float yVal = 6f;
                float versionWidth = tenacityFont16.getStringWidth(Client.instance.getVersion() + "(" + GuiLogin.uid + ")");
                float versionX = xVal + tenacityBoldFont40.getStringWidth(HUD.name());
                float width = (versionX + versionWidth) - xVal;

                RenderUtil.resetColor();
                GradientUtil.applyGradientHorizontal(xVal, yVal, width, 20, 1, clientColors.getFirst(), clientColors.getSecond(), () -> {
                    RenderUtil.setAlphaLimit(0);
                    tenacityBoldFont40.drawString(HUD.name(), xVal, yVal, 0);

                    tenacityFont16.drawString(Client.instance.getVersion() + "(" + GuiLogin.uid + ")", versionX, yVal, 0);

                });
                break;
            }
            case "Test": {
                float x = 5;
                float y = 7;
                String clientName = HUD.name();
                RoundedUtils.drawRound(x, y, FontManager.posterama18.getStringWidth(HUD.clientName.getString()) + 5, 14, 1, new Color(0,0,0,130));
                RoundedUtils.drawRound(x + FontManager.edit20.getStringWidth(clientName) + 12, y, FontManager.edit16.getStringWidth("Speed") + 15, 14, 1, new Color(0,0,0,130));
                RoundedUtils.drawRound(x + FontManager.edit20.getStringWidth(clientName) + FontManager.edit16.getStringWidth("Speed") + 33, y, FontManager.edit16.getStringWidth("Fps") + 15, 14, 1, new Color(0,0,0,130));
                FontManager.posterama16.drawStringWithShadow(HUD.clientName.getString(), x + 5, y + 6, HUD.color(0).getRGB());

                FontManager.edit16.drawStringWithShadow("Speed", x + FontManager.edit20.getStringWidth(clientName) + 12, y + 4, Color.WHITE.getRGB());
                RoundedUtils.circle(x + FontManager.edit20.getStringWidth(clientName) + 10 + FontManager.edit16.getStringWidth("Speed") + 6, y + 3, 8, 360, false, new Color(0,0,0, 70));
                RoundedUtils.circle(x + FontManager.edit20.getStringWidth(clientName) + 10 + FontManager.edit16.getStringWidth("Speed") + 6, y + 3, 8, (calculateBPS() / 10) * 360, false, Color.WHITE);

                FontManager.edit16.drawStringWithShadow("Fps", x + FontManager.edit20.getStringWidth(clientName) + FontManager.edit16.getStringWidth("Speed") + 35, y + 4, Color.WHITE.getRGB());
                RoundedUtils.circle(x + FontManager.edit20.getStringWidth(clientName) + FontManager.edit16.getStringWidth("Speed") + 35 + FontManager.edit16.getStringWidth("Fps") + 3, y + 3, 8, 360, false, new Color(0,0,0,70));
                RoundedUtils.circle(x + FontManager.edit20.getStringWidth(clientName) + FontManager.edit16.getStringWidth("Speed") + 35 + FontManager.edit16.getStringWidth("Fps") + 3, y + 3, 8, Minecraft.getDebugFPS() * 0.8, false, Color.WHITE);
                break;
            }
            case "Test2": {
                Long dNow = new Date( ).getTime();
                SimpleDateFormat ft = new SimpleDateFormat("hh:mm:ss");
                String Time = ft.format(dNow);
                String clientName = HUD.name();
                String ip = ServerUtil.getIp().toLowerCase().contains("nyaproxy") ? "NyaProxy" : ServerUtil.getIp();
                String formattedClientName = clientName + ChatFormatting.WHITE;
                String watermark = formattedClientName + " | " + GuiLogin.uid + " | " + Time + " | " + ip;
                double watermarkWidth = FontManager.edit20.getStringWidth(watermark);
                float x = 5;
                float y = 5;
                GlowUtils.drawGlow((int)x, (int)(y + 2.0f), (int)(watermarkWidth + 10.0), 20.0f, 8, new Color(0, 0, 0, 100));
                FontManager.edit20.drawStringWithShadow(watermark, x + 5.0f, y + 8.0f,HUD.color(2).getRGB());
                break;
            }
            case "Xylitol": {
                String username = GuiLogin.uid;
                String n = HUD.name();
                StringBuilder nm = new StringBuilder(n);
                nm.delete(i, 4);

                float userwidth = FontManager.arial16.getStringWidth(username) + 6;
                float iconwidth = FontManager.icon22.getStringWidth("t") + 12;
                float namewidth = FontManager.arial24.getStringWidth(change.isEnabled() ? nm.toString() : n);
                float allwidth = namewidth + iconwidth;
                float maxwidth = Math.max(allwidth, userwidth);

                BlurUtil.blurArea(6.0f, 6.0f, maxwidth + 6, 36.8f,22);

                RoundedUtils.drawRound(6.0f, 6.0f, maxwidth, 30, 2, new Color(20,20,20, 100));
                HUD.drawLine(6, 11, 2, 8, HUD.color(0));
                FontManager.icon22.drawStringDynamic("t", 12, 12);
                FontManager.arial24.drawStringDynamic(change.isEnabled() ? nm.toString() : n, maxwidth - FontManager.arial24.getStringWidth(change.isEnabled() ? nm.toString() : n) + 4, 11);
                FontManager.arial16.drawCenteredString(username, 6.0f + maxwidth / 2.0f, 25.0f, Color.WHITE.getRGB());

                GlowUtils.drawGlow(6.0f, 6.0f, maxwidth, 30, 12, new Color(0, 0, 0, 20));
                break;
            }
            case "Naven": {
                float x = 5;
                float y = 7;
                String CLIENTNAME = HUD.name().toUpperCase();
                String CLIENTVERSION = Client.instance.getVersion();
                Long dNow = new Date( ).getTime();
                SimpleDateFormat ft = new SimpleDateFormat("hh:mm:ss");
                String Time = ft.format(dNow);
                String user = username.isEnabled() ? " | " + GuiLogin.uid : "";
                String ver = version.isEnabled() ? " | "+ CLIENTVERSION: "";
                String time = times.isEnabled() ? " | " + Time : "";
                String fps = fpss.isEnabled() ? " | " + Minecraft.getDebugFPS() + " FPS" : "";

                String water = user + ver +fps+ time;

                double waterwidth = FontManager.opensans18.getStringWidth(user) + FontManager.opensans18.getStringWidth(ver) + FontManager.opensans18.getStringWidth(time) + FontManager.opensans18.getStringWidth(fps);

                DrawUtil.drawRoundedRect(x, y - 1.6f, (waterwidth + FontManager.opensans20.getStringWidth(CLIENTNAME) + 12), y + 18, 10.0, Integer.MIN_VALUE);

                DrawUtil.drawRoundedRect(x, y, x, y, 0.0, new Color(255, 255, 255, 255).getRGB());

                GlowUtils.drawGlow(x, y, (float) (waterwidth + FontManager.arial22.getStringWidth(CLIENTNAME) + 7), 20, 12, new Color(25, 25, 25, 174));

                RoundedUtils.drawRound(x + 2.0F, y + 9 - 10.0F, x + FontManager.opensans20.getStringWidth(CLIENTNAME) + 160, 2.5F, 2.0F, new Color(160, 42, 42));

                FontManager.opensans20.drawString(CLIENTNAME, x + 4, y + 4, HUD.color(0).getRGB());
                FontManager.opensans18.drawString(water, x + FontManager.opensans20.getStringWidth(CLIENTNAME) + 4, y + 4, new Color(255,255,255).getRGB());
                break;
            }
            case "Logo": {
                RenderUtil.drawImageTest(new ResourceLocation("bloodline/icon/djh.png"),0, 0, 107, 142);
                break;
            }
            case "Exhibition":{
                //brown §7
                //white §f
                Pair<Color, Color> colors = HUD.getClientColors();
                Color textcolor = ColorUtil.interpolateColorsBackAndForth(15, 100, colors.getFirst(), colors.getSecond(), false);
                textcolor = ColorUtil.rainbow(15, 100, HUD.color1.getRainbow().getSaturation(), 1, 1);
                Long dNow = new Date( ).getTime();
                SimpleDateFormat ft = new SimpleDateFormat("hh:mm:ss");
                String Time = ft.format(dNow);
                mc.fontRendererObj.drawString(HUD.name()+" " + "[1.8.x] ["+Time+"]", 3, 3, new Color(0, 0, 0, 255).getRGB());
                mc.fontRendererObj.drawString(HUD.name()+" " + "§7[§f1.8.x§7] [§f"+Time+"§7]", 2, 2, new Color(255, 255, 255, 255).getRGB());
                mc.fontRendererObj.drawString(String.valueOf(HUD.name().charAt(0)),2,2,textcolor.getRGB());
                break;
            }
        }

    }



    private double calculateBPS() {
        double bps = (Math.hypot(mc.thePlayer.posX - mc.thePlayer.prevPosX, mc.thePlayer.posZ - mc.thePlayer.prevPosZ) * mc.timer.timerSpeed) * 20;
        return Math.round(bps * 100.0) / 100.0;
    }


}
