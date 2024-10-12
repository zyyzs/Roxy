package lol.tgformat.module.impl.player;

import com.mojang.authlib.GameProfile;
import lol.tgformat.api.event.Listener;
import lol.tgformat.events.TickEvent;
import lol.tgformat.events.motion.PostMotionEvent;
import lol.tgformat.module.Module;
import lol.tgformat.module.ModuleType;
import lol.tgformat.module.values.impl.BooleanSetting;
import lol.tgformat.module.values.impl.NumberSetting;
import lol.tgformat.utils.player.BlinkUtils;
import lol.tgformat.utils.timer.TimerUtil;
import lombok.Getter;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import tech.skidonion.obfuscator.annotations.Renamer;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import java.util.UUID;

/**
 * @Author KuChaZi
 * @Date 2024/7/1 22:21
 * @ClassName: Blink
 */
@Renamer
@StringEncryption
public class Blink extends Module {
    @Getter
    private static EntityOtherPlayerMP fakePlayer;
    private final BooleanSetting slowRelease = new BooleanSetting("SlowRelease", false);
    private final NumberSetting releaseDelay = new NumberSetting("ReleaseDelay",1000, 10000 ,300 ,10);
    private final TimerUtil timer = new TimerUtil();
    public Blink() {
        super("Blink", ModuleType.Player);
    }

    @Override
    public void onEnable() {
        timer.reset();
        BlinkUtils.startBlink();
        fakePlayer = new EntityOtherPlayerMP(mc.theWorld, new GameProfile(new UUID(69L, 96L), "[Blink]" + mc.thePlayer.getName()));
        fakePlayer.copyLocationAndAnglesFrom(mc.thePlayer);
        fakePlayer.rotationYawHead = mc.thePlayer.rotationYawHead;
        mc.theWorld.addEntityToWorld(-1337, fakePlayer);
    }

    @Override
    public void onDisable() {
        BlinkUtils.stopBlink();
        timer.reset();
        if (fakePlayer != null) {
            mc.theWorld.removeEntityFromWorld(fakePlayer.getEntityId());
            fakePlayer = null;
        }
    }

    @Listener
    private void onTick(TickEvent event) {
        if (slowRelease.isEnabled() && BlinkUtils.isBlinking() && timer.hasReached(releaseDelay.getValue().longValue())) {
            BlinkUtils.releaseC03(1);
        }
    }
}
