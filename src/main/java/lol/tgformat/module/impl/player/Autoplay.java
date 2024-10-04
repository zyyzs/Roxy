package lol.tgformat.module.impl.player;

import lol.tgformat.api.event.Listener;
import lol.tgformat.events.packet.PacketReceiveEvent;
import lol.tgformat.module.Module;
import lol.tgformat.module.ModuleManager;
import lol.tgformat.module.ModuleType;
import lol.tgformat.module.impl.combat.AntiBot;
import lol.tgformat.module.impl.combat.KillAura;
import lol.tgformat.module.impl.movement.Speed;
import lol.tgformat.ui.notifications.NotificationManager;
import lol.tgformat.ui.notifications.NotificationType;
import lol.tgformat.utils.client.LogUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S45PacketTitle;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static lol.tgformat.module.impl.misc.IRC.transport;

public class Autoplay extends Module {
    public Autoplay() {
        super("Autoplay", ModuleType.Player);
    }

    boolean strA = false;
    boolean strB = false;
    boolean safe = false;


    @Listener
    public void onPacketReceiveEvent(PacketReceiveEvent event) {
        Object packet = event.getPacket();
        if (packet instanceof S02PacketChat) {
            S02PacketChat s02PacketChat = (S02PacketChat) packet;
            String text = s02PacketChat.getChatComponent().getUnformattedText();
            Matcher matcher4 = Pattern.compile("玩家(.*?)在本局游戏中行为异常").matcher(text);
            if (matcher4.find()) {
                if (transport.isUser(matcher4.group(1))){
                    LogUtil.addIRCMessage("我们伟大的"+transport.getIgn(matcher4.group(1))+"牺牲了他的账号"+matcher4.group(1));
                }
                NotificationManager.post(NotificationType.WARNING, "Ban Checker", "A Player Was Banned.", 5f);
            }

            if (text.contains("开始倒计时: 1 秒")) {
                NotificationManager.post(NotificationType.INFO, "Game Started", "Good Game!");
                ModuleManager.getModule(Stealer.class).setState(true);
                ModuleManager.getModule(InvManager.class).setState(true);

            }

            if (text.contains("你现在是观察者状态. 按E打开菜单.")) {
                safe = true;
                strA = true;
                NotificationManager.post(NotificationType.INFO, "Game Ending!", "Your Health:"+(int)mc.thePlayer.getHealth());
                ModuleManager.getModule(Stealer.class).setState(false);
                ModuleManager.getModule(InvManager.class).setState(false);
            }


            if (packet instanceof S45PacketTitle) {
                S45PacketTitle s45PacketTitle = (S45PacketTitle) packet;
                if (s45PacketTitle.getType() == S45PacketTitle.Type.TITLE) {
                    String title = s45PacketTitle.getMessage().getFormattedText();
                    Matcher matcher5 = Pattern.compile("花雨庭").matcher(title);
                    Matcher matcher6 = Pattern.compile("VICTORY").matcher(title);
                    if (matcher5.find()) {
                        safe = false;
                        strA = false;
                        strB = false;
                        ModuleManager.getModule(Stealer.class).setState(false);
                        ModuleManager.getModule(InvManager.class).setState(false);
                        ModuleManager.getModule(KillAura.class).setState(false);
                        ModuleManager.getModule(Speed.class).setState(false);
                    }
                    if (matcher6.find()) {
                        NotificationManager.post(NotificationType.INFO, "Game Ending!", "Your Health:"+mc.thePlayer.getHealth());
                        ModuleManager.getModule(Stealer.class).setState(false);
                        ModuleManager.getModule(InvManager.class).setState(false);
                        ModuleManager.getModule(KillAura.class).setState(false);
                        ModuleManager.getModule(Speed.class).setState(false);
                    }
                    if (title.equals("VICTORY")) {
                        safe = true;
                        strB = true;
                    }
                    if (safe && strA && !title.equals("VICTORY")) {
                        strA = false;
                        strB = false;
                    }
                }
            }

        }
    }
}



