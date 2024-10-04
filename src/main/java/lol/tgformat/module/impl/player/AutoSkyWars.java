package lol.tgformat.module.impl.player;

import lol.tgformat.api.event.Listener;
import lol.tgformat.events.PreUpdateEvent;
import lol.tgformat.events.TickEvent;
import lol.tgformat.events.motion.PreMotionEvent;
import lol.tgformat.events.packet.PacketReceiveEvent;
import lol.tgformat.module.Module;
import lol.tgformat.module.ModuleManager;
import lol.tgformat.module.ModuleType;
import lol.tgformat.module.values.impl.BooleanSetting;
import lol.tgformat.module.values.impl.ModeSetting;
import lol.tgformat.module.values.impl.NumberSetting;
import lol.tgformat.utils.block.BlockUtil;
import lol.tgformat.utils.client.LogUtil;
import lol.tgformat.utils.network.PacketUtil;
import lol.tgformat.utils.player.BlinkUtils;
import lol.tgformat.utils.player.InventoryUtil;
import lol.tgformat.utils.timer.TimerUtil;
import net.minecraft.block.BlockGlass;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import tech.skidonion.obfuscator.annotations.Renamer;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import java.util.Map;


/**
 * @Author KuChaZi
 * @Date 2024/6/8 13:24
 * @ClassName: AutoSkyWars
 */

@Renamer
@StringEncryption
public class AutoSkyWars extends Module {

    private final ModeSetting modes = new ModeSetting("Mode","HYT","HYT","Hypixel");
    private final ModeSetting mode = new ModeSetting("Mode","Solo insane","Solo normal","Solo insane");
    private final NumberSetting delays = new NumberSetting("HypDelay", 1500, 4000, 0, 50);
    private final NumberSetting delay = new NumberSetting("HytDelay",25.0,30.0,20.0,1.0);
    private final BooleanSetting autoKit = new BooleanSetting("AutoKit", true);

    public AutoSkyWars() {
        super("AutoSkyWars", ModuleType.Player);
    }

    private final BooleanSetting debug = new BooleanSetting("Debug",true);
    private final TimerUtil timers = new TimerUtil();

    private boolean waiting;
    private int timer = 0;
    private boolean game = false;

    @Override
    public void onEnable() {
        waiting = false;
        timers.reset();
        game = false;
        timer = 0;
    }

    @Override
    public void onDisable() {
        game = false;
        timer = 0;
    }

    @Listener
    public void onMotion(PreMotionEvent event) {
        this.setSuffix(modes.getMode());
    }
    @Listener
    public void onUpdate(PreUpdateEvent event) {
        if (modes.is("HYT")) {
            if (ModuleManager.getModule(Blink.class).isState()) {
                for (Map.Entry<BlockPos, ?> block : BlockUtil.searchBlocks(3).entrySet()) {
                    BlockPos blockpos = block.getKey();
                    if (block.getValue() instanceof BlockGlass) {
                        PacketUtil.sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, blockpos, EnumFacing.DOWN));
                        PacketUtil.sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, blockpos, EnumFacing.DOWN));
                        mc.theWorld.setBlockState(blockpos, Blocks.air.getDefaultState(), 2);
                        BlinkUtils.setCantSlowRelease(true);
                    }
                }
            }
            if (timer >= delay.getValue()) {
                game = false;
                timer = 0;
                BlinkUtils.setCantSlowRelease(false);
                ModuleManager.getModule(Blink.class).setState(false);
            }
            if (game) timer++;
            if (debug.isEnabled() && game) LogUtil.addChatMessage(String.valueOf(timer));
        }
    }

    @Listener
    public void onTick(TickEvent event) {
        if (modes.is("Hypixel")) {
            if (waiting && timers.getTimeElapsed() >= delays.getValue()) {
                String command = "";

                switch (mode.getMode()) {
                    case "Solo normal":
                        command = "/play solo_normal";
                        break;
                    case "Solo insane":
                        command = "/play solo_insane";
                        break;
                }

                mc.thePlayer.sendChatMessage(command);

                timers.reset();
                waiting = false;
            }
        }
        if(modes.is("HYT")&&autoKit.isEnabled()){
            if(mc.currentScreen!=null){
                if(mc.currentScreen instanceof GuiChest chest){
                    mc.playerController.windowClick(chest.inventorySlots.windowId, 6, 0, 0, mc.thePlayer);
                }
            }
        }
    }



    @Listener
    public void onPacket(PacketReceiveEvent event) {
        Object packet = event.getPacket();
        if (packet instanceof S02PacketChat) {
            String text = ((S02PacketChat) packet).getChatComponent().getUnformattedText();
            String loseMessage = "You died! Want to play again? Click here!";
            String winMessage = "You won! Want to play again? Click here!";
            if((text.contains(winMessage) && text.length() < winMessage.length() + 3) || (text.contains(loseMessage) && text.length() < loseMessage.length() + 3)) {
                waiting = true;
                timers.reset();
            }
            if (text.contains("开始倒计时: 3 秒")) {
                BlinkUtils.setCantSlowRelease(true);
                ModuleManager.getModule(Blink.class).setState(true);
            }
            if(text.contains("开始倒计时: 2 秒") && autoKit.isEnabled()){
                int slot = InventoryUtil.findItem(0, 9, Items.ender_eye);
                int nslot = mc.thePlayer.inventory.currentItem;
                mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(slot));
                mc.rightClickMouse();
                mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(nslot));
            }
            if (text.contains("开始倒计时: 1 秒")) {
                game = true;
            }
        }
    }
}
