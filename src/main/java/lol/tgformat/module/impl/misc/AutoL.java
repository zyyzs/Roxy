package lol.tgformat.module.impl.misc;

import lol.tgformat.api.event.Listener;
import lol.tgformat.events.PreUpdateEvent;
import lol.tgformat.events.WorldEvent;
import lol.tgformat.events.motion.PreMotionEvent;
import lol.tgformat.module.Module;
import lol.tgformat.module.ModuleManager;
import lol.tgformat.module.ModuleType;
import lol.tgformat.module.impl.combat.KillAura;
import lol.tgformat.utils.timer.TimerUtil;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;
import tech.skidonion.obfuscator.annotations.Renamer;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import java.util.Random;

import static lol.tgformat.utils.math.MathUtil.random;

/**
 * @Author KuChaZi
 * @Date 2024/6/15 17:11
 * @ClassName: AutoL
 */
@Renamer
@StringEncryption
public class AutoL extends Module {

    public AutoL() {
        super("AutoL", ModuleType.Misc);
    }
    private EntityLivingBase target;
    private static int kills;

    TimerUtil time;
    @Listener
    public void onWorldLoad(WorldEvent event) {
        kills = 0;
    }

    @Listener
    public void onMotion(PreMotionEvent event) {
        this.setSuffix(kills);
    }

    @Listener
    public void onUpdate(PreUpdateEvent event){

        if(ModuleManager.getModule(AutoL.class).isState() && time.hasTimeElapsed(100)){
                mc.thePlayer.sendChatMessage(getRandomText());
                kills++;
                time.reset();
           }

    }

    private static String[] hytText = {
            "其实jnic是许锦良开发的 他一手创建了中国jnic",
            "你现在有没有入权了?",
            "SilenceFix is God in QuickMacro",
            "SilenceFix花雨庭第一 获取去xinxin.fan",
            "是漏防哥吗?",
            "咋了兄弟,要来偷刀?",
            "你是冯冯死了吗？",
            "我可不是穿甲欣欣",
            "我算什么耻辱，欣欣也不是吗",
            "快给idan加云黑,idan诈骗.",
            "SilenceFix经过花雨庭最强检测,曾经靠自己把naven打退网,把xylitol打退网检测出WNF存在后门",
            "你那么舔欣欣干嘛？",
            "学生党，内部是60元哈 你给了欣欣哥40元 请在支付我20元售后费哈 因为欣欣哥是更新参数的 我呢是来搞售后滴 只要我在线 售后会特别的好呢 请你放心 一分价钱一分货 购买了我们的配置 我们是绝对不会让你吃亏的呢哈学生党！",
            "SilenceFix-9.95 获取最强工艺配置就来xinxin。cfd哦",
            "老弟敢不敢跟我的最强欣欣配置对刀一下呢?",
            "许锦良特殊冒泡",
            "最强客户端S\uFFF1ile\uFFF1nc\uFFF1eFix-Bet\uFFF1a",
            "我徐锦良紧急通知，请大家相互转告，不要在下载WNF脱离盒子了，如果你不害怕电脑一系列的后果，那悉听尊便。",
            "我们的许锦良正在使用漏电黑解绕ID不可升级美版128GB金色后盖全碎屏幕蜘蛛网裂纹只能插1卡电池鼓包45电池健康并且主板断桥大修相机不可用面容损坏充电口进水屏幕漏液出线按键堵塞喇叭炸麦无麦克风听筒损坏的iPhone14ProMax。",
            "经过花雨庭欣欣最强检测,曾经靠自己把naven打退网,把xylitol打退网检测出WNF存在后门",
            "windows能骗你吗?360的那个大家都知道是假的。windows不会骗你的。"

    };

    public static String getRandomText() {
        Random random = new Random();
        int index = random.nextInt(hytText.length);
        return hytText[index];
    }

}
