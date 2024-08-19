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
        KillAura aura = ModuleManager.getModule(KillAura.class);
        target = aura.target;
        if(target.getHurtTime() <= 1) {
            if(ModuleManager.getModule(AutoL.class).isState() && time.hasTimeElapsed(100)){
                mc.thePlayer.sendChatMessage(getRandomText());
                kills++;
                time.reset();
            }
        }
    }

    private static String[] hytText = {
            "其实jnic是许锦良开发的 他一手创建了中国jnic",
            "你现在有没有入权了?",
            "你告诉我，是不是这个情况?",
            "SilenceFix is God in QuickMacro",
            "SilenceFix花雨庭第一 获取去xinxin.fan",
            "你为什么翻来覆去的",
            "七上八下侮辱你了",
            "是漏防哥吗?",
            "咋了兄弟,要来偷刀?",
            "自制投自制，真有你的，把自己的视频一剪，这就成你自己的视频了",
            "你是冯冯死了吗？",
            "我可不是穿甲欣欣",
            "你写的跟狮山一样,霸气但没用",
            "馆可以录，不能炸膛，这是故事。",
            "在嘛 ? 你的到爆卖吗？",
            "能帮我升级Via吗",
            "帮我构建水影可以不？我给你打钱",
            "我算什么耻辱，欣欣也不是吗",
            "快给idan加云黑,idan诈骗.",
            "SilenceFix经过花雨庭最强检测,曾经靠自己把naven打退网,把xylitol打退网检测出WNF存在后门",
            "我的师傅是deram大神，你们大不过我的",
            "现在拜\uFFF1我，我教\uFFF1你下海，一天\uFFF1草\uFFF1"+kills+"个\uFFF1比",
            "第"+kills+"个绷",
            "不要发你这\uFFF1坨\uFFF1屎了",
            "我有这个端的src。",
            "保证不买了",
            "是漏防哥吗？",
            "你那么舔欣欣干嘛？",
            "学生党，内部是60元哈 你给了欣欣哥40元 请在支付我20元售后费哈 因为欣欣哥是更新参数的 我呢是来搞售后滴 只要我在线 售后会特别的好呢 请你放心 一分价钱一分货 购买了我们的配置 我们是绝对不会让你吃亏的呢哈学生党！",
            "SilenceFix-9.95 获取最强工艺配置就来xinxin。cfd哦",
            "老弟敢不敢跟我的最强欣欣配置对刀一下呢?",
            "肯定觉得用rise改个名卖钱特别帅",
            "你这个瞄准像aimasiist。ops也像ac。别再开了知道吗。",
            "呵呵 能保证不卖吗？",
            "大人物老实几天就不老实了",
            "但是布吉岛kb很舒服，自己在hyt,d服里跟答辩一样,但是在布吉岛里面就经常赢[笑哭]",
            "我可是zs.FDPMore神",
            "能不能拉你公益内部,我特喵的认定你了",
            "许锦良特殊冒泡",
            "小南娘说话还挺逗",
            "你是花雨庭最强那我是什么，是你\uFFF1老\uFFF1豆\uFFF1吗？",
            "最强客户端S\uFFF1ile\uFFF1nc\uFFF1eFix-Bet\uFFF1a",
            "我徐锦良紧急通知，请大家相互转告，不要在下载WNF脱离盒子了，如果你不害怕电脑一系列的后果，那悉听尊便。",
            "我们的许锦良正在使用漏电黑解绕ID不可升级美版128GB金色后盖全碎屏幕蜘蛛网裂纹只能插1卡电池鼓包45电池健康并且主板断桥大修相机不可用面容损坏充电口进水屏幕漏液出线按键堵塞喇叭炸麦无麦克风听筒损坏的iPhone14ProMax。",
            "经过花雨庭欣欣最强检测,曾经靠自己把naven打退网,把xylitol打退网检测出WNF存在后门",
            "windows能骗你吗?360的那个大家都知道是假的。windows不会骗你的。"

    };

    private static String getRandomText() {
        Random random = new Random();
        int index = random.nextInt(hytText.length);
        return hytText[index];
    }

}
