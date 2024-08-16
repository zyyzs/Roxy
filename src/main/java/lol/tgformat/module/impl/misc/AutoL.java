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
    private int kills;

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
            "你现在有没有入权了?",
            "你告诉我，是不是这个情况?",
            "SilenceFix is God in QuickMacro",
            "SilenceFix花雨庭第一 获取去xinxin.fan",
            "你为什么翻来覆去的",
            "七上八下侮辱你了",
            "是漏防哥吗?",
            "咋了兄弟,要来偷刀?"
    };

    private static String getRandomText() {
        Random random = new Random();
        int index = random.nextInt(hytText.length);
        return hytText[index];
    }

}
