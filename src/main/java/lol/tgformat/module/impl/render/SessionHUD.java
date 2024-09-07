package lol.tgformat.module.impl.render;

import lol.tgformat.api.event.Listener;
import lol.tgformat.events.PreUpdateEvent;
import lol.tgformat.events.WorldEvent;
import lol.tgformat.events.packet.PacketSendEvent;
import lol.tgformat.events.render.Render2DEvent;
import lol.tgformat.module.Module;
import lol.tgformat.module.ModuleType;
import lol.tgformat.module.impl.combat.KillAura;
import lol.tgformat.module.values.impl.NumberSetting;
import lol.tgformat.ui.font.FontUtil;
import lol.tgformat.ui.utils.RenderUtil;
import lol.tgformat.ui.utils.RoundedUtil;
import lol.tgformat.utils.render.GlowUtils;
import lol.tgformat.utils.timer.TimerUtil;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S02PacketChat;
import net.netease.font.FontManager;
import tech.skidonion.obfuscator.annotations.Renamer;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import java.awt.*;

/**
 * @Author KuChaZi
 * @Date 2024/8/2 15:47
 * @ClassName: SessionHUD
 */
@Renamer
@StringEncryption
public class SessionHUD extends Module {
    private final NumberSetting postx = new NumberSetting("PostX", 640, 640, -480, 0.1);
    private final NumberSetting posty = new NumberSetting("PostY", 290, 350, -280, 0.1);
    private int kills;
    EntityLivingBase target;
    private long startTime;

    public SessionHUD() {
        super("SessionHUD", ModuleType.Render);
        resetTimer();
    }

    public void onDisable() {
        kills = 0;
    }

    @Listener
    public void onUpdate(PreUpdateEvent event){
        target = KillAura.target;
        if(target.getHealth() <= 0.0f) {
            kills++;
        }
    }

    @Listener
    public void onRender2D(Render2DEvent event) {
        ScaledResolution sr = new ScaledResolution(mc);
        int x = sr.getScaledWidth() / 2 - postx.getValue().intValue();
        int y = sr.getScaledHeight() / 2 - posty.getValue().intValue();

        double width = 170;
        double height = 68;

        GlowUtils.drawGlow((float) x,(float) y,(float) width,(float) height, 6, new Color(0,0,0,90));

        RoundedUtil.drawRound((float) x,(float) y,(float) width,(float) height, 2, new Color(0,0,0,20));
        HUD.drawLine(x, y + 5, 2, 10, HUD.color(0));
        FontUtil.tenacityFont24.drawStringWithShadow("Session", x + 21, y + 6, Color.CYAN);
        FontUtil.iconFont26.drawStringWithShadow("s", x + 7, y + 7, Color.WHITE);
        RenderUtil.drawPlayerHead(mc.thePlayer.getLocationSkin(), (int)x + 13, (int)y + 24, 36,36);
        FontUtil.tenacityFont18.drawStringWithShadow(mc.thePlayer.getName(), x + 55, y + 28, Color.WHITE.getRGB());
        FontUtil.tenacityFont18.drawStringWithShadow(getTime(), x + 55, y + 42, Color.GRAY.getRGB());
        FontUtil.tenacityFont18.drawStringWithShadow("Kills: " + kills, x + 55, y + 52, Color.GRAY.getRGB());

    }
    private void resetTimer() {
        this.startTime = System.currentTimeMillis();
    }
    private String getTime() {
        long currentTime = System.currentTimeMillis();
        long elapsed = currentTime - startTime;

        int seconds = (int) (elapsed / 1000) % 60;
        int minutes = (int) (elapsed / (1000 * 60)) % 60;

        return String.format("%02dm %02ds", minutes, seconds);
    }
}
