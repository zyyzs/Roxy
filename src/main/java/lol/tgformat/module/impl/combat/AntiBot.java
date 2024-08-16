package lol.tgformat.module.impl.combat;

import lol.tgformat.api.event.Listener;
import lol.tgformat.events.PreUpdateEvent;
import lol.tgformat.events.WorldEvent;
import lol.tgformat.events.packet.PacketReceiveEvent;
import lol.tgformat.module.Module;
import lol.tgformat.module.ModuleType;
import lol.tgformat.module.values.impl.BooleanSetting;
import lol.tgformat.module.values.impl.ModeSetting;
import lol.tgformat.module.values.impl.NumberSetting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S38PacketPlayerListItem;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;
import tech.skidonion.obfuscator.annotations.Renamer;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author KuChaZi
 * @Date 2024/6/30 16:01
 * @ClassName: AntiBot
 */
@Renamer
@StringEncryption
public class AntiBot extends Module {
    private final ModeSetting mode = new ModeSetting("Mode", "Custom", "Hypixel", "Hyt", "Matrix", "RankCheck");

    private final BooleanSetting movedOnce = new BooleanSetting("Moved", false);
    private final BooleanSetting touchedGround = new BooleanSetting("TouchedGround", false);
    private final BooleanSetting wasInvisible = new BooleanSetting("Invisible", false);
    private final BooleanSetting enableTicksExsitedCheck = new BooleanSetting("EnableTicksExisted", false);
    private final NumberSetting ticksExisted = new NumberSetting("TicksExisted", 0.0D, 10000.0D, 0.0D, 50.0D);
    private final BooleanSetting enableIDCheck = new BooleanSetting("IDCheck", false);
    private final NumberSetting maximumID = new NumberSetting("MaximumID", 1500000.0D, 1.0E7D, 1000000.0D, 100000.0D);
    private final ModeSetting quickMacroMode = new ModeSetting("Hyt Mode", "BW4v4", "BW1v1", "BW32", "BW16", "BW4v4");
    private static final List<String> playerName = new ArrayList<>();
    private final ArrayList<Player> custombots = new ArrayList<>();
    private final ArrayList<UUID> matrixBot = new ArrayList<>();

    public AntiBot() {
        super("AntiBot", ModuleType.Combat);
    }

    @Override
    public void onDisable() {
        custombots.clear();
        matrixBot.clear();
        super.onDisable();
    }

    @Listener
    private void onWorldLoad(WorldEvent event) {
        custombots.clear();
        matrixBot.clear();
    }

    @Listener//答辩
    private void onUpdate(PreUpdateEvent event) {
        this.setSuffix(mode.getMode());
        switch (mode.getMode()) {
            case "Hypixel": {
                for (Entity entity : mc.theWorld.getLoadedEntityList()) {
                    if (!(entity instanceof EntityPlayer)) continue;
                    if (entity.getName().contains("\u00a7") || entity.hasCustomName() && entity.getCustomNameTag().contains(entity.getName())) {
                        mc.theWorld.removeEntity(entity);
                    }
                }
                break;
            }
            case "Custom": {
                this.removeOld();

                for (EntityPlayer entityPlayer : this.mc.theWorld.playerEntities) {
                    boolean exist = false;

                    for (Player player : custombots) {
                        if (player.player.getName().equalsIgnoreCase(entityPlayer.getName())) {
                            exist = true;
                        }
                    }

                    if (!exist) {
                        custombots.add(new Player(entityPlayer));
                    }
                }
                break;
            }
        }
    }

    @Listener
    private void onPacketReceive(PacketReceiveEvent event) {
        Packet<?> packet = event.getPacket();
        if (mode.is("Hyt") && packet instanceof S02PacketChat s02PacketChat) {
            if (s02PacketChat.getChatComponent().getUnformattedText().contains("获得胜利!") || s02PacketChat.getChatComponent().getUnformattedText().contains("游戏开始 ...")) {
                this.clearAll();
            }
            switch (quickMacroMode.getMode()) {
                case "BW4v4":
                case "BW1v1":
                case "BW32": {
                    handleKillMessages(s02PacketChat, "杀死了 (.*?)\\(", "起床战争>> (.*?) (\\((((.*?) 死了!)))");
                    break;
                }
                case "BW16": {
                    handleKillMessages(s02PacketChat, "击败了 (.*?)!", "玩家 (.*?)死了！");
                    break;
                }
            }
        }
        if (mode.is("Matrix")) {//精华
            if (packet instanceof S38PacketPlayerListItem) {
                for (S38PacketPlayerListItem.AddPlayerData data : ((S38PacketPlayerListItem) packet).getEntries()) {
                    if (((S38PacketPlayerListItem) packet).getAction().equals(S38PacketPlayerListItem.Action.ADD_PLAYER) &&
                            data.getProfile().getProperties().isEmpty() && ((S38PacketPlayerListItem) packet).getEntries().size() == 1
                            && mc.getNetHandler() != null && mc.getNetHandler().getPlayerInfo(data.getProfile().getName()) != null) {
                        if (!matrixBot.contains(data.getProfile().getId())) {
                            matrixBot.add(data.getProfile().getId());
                        }
                    }
                }
            }
        }
    }

    private void clearAll() {
        playerName.clear();
    }

    
    private void handleKillMessages(S02PacketChat s02PacketChat, String pattern1, String pattern2) {
        Matcher matcher1 = Pattern.compile(pattern1).matcher(s02PacketChat.getChatComponent().getUnformattedText());
        Matcher matcher2 = Pattern.compile(pattern2).matcher(s02PacketChat.getChatComponent().getUnformattedText());

        if (matcher1.find()) {
            String name = matcher1.group(1).trim();
            if ((!s02PacketChat.getChatComponent().getUnformattedText().contains(": 起床战争>>") || !s02PacketChat.getChatComponent().getUnformattedText().contains(": 杀死了")) && !name.isEmpty()) {
                addPlayerName(name);
            }
        }

        if (matcher2.find()) {
            String name = matcher2.group(1).trim();
            if ((!s02PacketChat.getChatComponent().getUnformattedText().contains(": 起床战争>>") && s02PacketChat.getChatComponent().getUnformattedText().contains(": 杀死了")) && !name.isEmpty()) {
                addPlayerName(name);
            }
        }
    }

    
    private void addPlayerName(String name) {
        playerName.add(name);
        new Thread(() -> {
            try {
                Thread.sleep(6000L);
                playerName.remove(name);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }).start();
    }

    
    public boolean isServerBot(Entity entity) {
        if (!this.isState()) {
            return false;
        }
        if (mode.is("Hypixel")) {
            return !(entity.getDisplayName().getFormattedText().startsWith("§") && !entity.isInvisible() && !entity.getDisplayName().getFormattedText().toLowerCase().contains("npc"));
        } else if (mode.is("RankCheck")) {
            return !(
                    entity.getDisplayName().getUnformattedText().contains("Marcel") ||
                            entity.getDisplayName().getUnformattedText().contains("红 ") ||
                            entity.getDisplayName().getUnformattedText().contains("黄 ") ||
                            entity.getDisplayName().getUnformattedText().contains("蓝 ") ||
                            entity.getDisplayName().getUnformattedText().contains("绿 ") ||
                            entity.getDisplayName().getUnformattedText().contains("R ") ||
                            entity.getDisplayName().getUnformattedText().contains("B ") ||
                            entity.getDisplayName().getUnformattedText().contains("G ") ||
                            entity.getDisplayName().getUnformattedText().contains("Y ") ||
                            entity.getDisplayName().getUnformattedText().contains("VIP") ||
                            entity.getDisplayName().getUnformattedText().contains("MVP") ||
                            entity.getDisplayName().getUnformattedText().contains("vip") ||
                            entity.getDisplayName().getUnformattedText().contains("mvp") ||
                            entity.getDisplayName().getUnformattedText().contains("Vip") ||
                            entity.getDisplayName().getUnformattedText().contains("Mvp") ||
                            entity.getDisplayName().getUnformattedText().contains("督察")
            );
        } else if ((mode.is("Hyt") && playerName.contains(entity.getName()))) {
            return true;
        } else if (mode.is("Custom")) {
            for (Player custom : custombots) {
                if (entity instanceof EntityPlayer) {
                    if (custom.player.getName().equalsIgnoreCase(entity.getName())) {
                        if (movedOnce.isEnabled() && !custom.moved()) {
                            return true;
                        }
                        if (touchedGround.isEnabled() && !entity.onGround) {
                            return true;
                        }
                        if (wasInvisible.isEnabled() && entity.isInvisible()) {
                            return true;
                        }
                        if (enableTicksExsitedCheck.isEnabled() && entity.ticksExisted <= ticksExisted.getValue()) {
                            return true;
                        }
                        if (enableIDCheck.isEnabled() && entity.getEntityId() >= maximumID.getValue()) {
                            return true;
                        }
                    }
                }
            }
            return false;
        } else if (mode.is("Matrix")) {
            return matrixBot.contains(entity.getUniqueID());
        }
        return false;
    }

    
    private void removeOld() {
        Iterator<Player> it = custombots.iterator();
        Set<String> playerNames = new HashSet<>();
        for (EntityPlayer player : this.mc.theWorld.playerEntities) {
            playerNames.add(player.getName());
        }

        while (it.hasNext()) {
            AntiBot.Player player = it.next();
            if (!playerNames.contains(player.player.getName())) {
                it.remove();
            }
        }
    }

    private static class Player {
        private final EntityPlayer player;
        private final double firstX;
        private final double firstY;
        private final double firstZ;

        public Player(EntityPlayer player) {
            this.player = player;
            this.firstX = player.posX;
            this.firstY = player.posY;
            this.firstZ = player.posZ;
        }

        boolean moved() {
            return this.firstX != this.player.posX || this.firstY != this.player.posY || this.firstZ != this.player.posZ;
        }
    }
}
