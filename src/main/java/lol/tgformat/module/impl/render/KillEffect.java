package lol.tgformat.module.impl.render;

import lol.tgformat.api.event.Listener;
import lol.tgformat.events.AttackEvent;
import lol.tgformat.events.PreUpdateEvent;
import lol.tgformat.events.WorldEvent;
import lol.tgformat.module.Module;
import lol.tgformat.module.ModuleType;
import lol.tgformat.module.impl.combat.KillAura;
import lol.tgformat.module.values.impl.ModeSetting;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumParticleTypes;
import net.netease.utils.ContinualAnimation;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;
import tech.skidonion.obfuscator.annotations.Renamer;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.Objects;

/**
 * @Author KuChaZi
 * @Date 2024/7/1 6:34
 * @ClassName: KillEffect
 */
@Renamer
@StringEncryption
public class KillEffect extends Module {
    private final ModeSetting killEffectValue = new ModeSetting("KillEffect", "Squid", "LightningBolt", "Totem", "Flame", "Smoke", "Love", "Blood", "Off", "Squid");
    private final ModeSetting killSoundValue = new ModeSetting("KillSound", "Squid", "Off", "Squid", "MNSJ");
    private final ContinualAnimation animations = new ContinualAnimation();
    private EntityLivingBase target;
    private EntitySquid squid;
    private double percent = 0.0;
    private int kills = 0;

    public KillEffect() {
        super("KillEffect", ModuleType.Render);
    }

    public static float nextFloat(float startInclusive, float endInclusive) {
        if (startInclusive == endInclusive || endInclusive - startInclusive <= 0.0f) {
            return startInclusive;
        }
        return (float) ((double) startInclusive + (double) (endInclusive - startInclusive) * Math.random());
    }

    public double easeInOutCirc(double x) {
        return x < 0.5 ? (1.0 - Math.sqrt(1.0 - Math.pow(2.0 * x, 2.0))) / 2.0 : (Math.sqrt(1.0 - Math.pow(-2.0 * x + 2.0, 2.0)) + 1.0) / 2.0;
    }

    @Listener
    public void onUpdate(PreUpdateEvent event) {
        if (killEffectValue.is("Squid") && squid != null) {
            if (mc.theWorld.loadedEntityList.contains(squid)) {
                if (percent < 1.0) {
                    percent += Math.random() * 0.048;
                }
                if (percent >= 1.0) {
                    percent = 0.0;
                    for (int i = 0; i <= 8; ++i) {
                        mc.effectRenderer.emitParticleAtEntity(squid, EnumParticleTypes.FLAME);
                    }
                    mc.theWorld.removeEntity(squid);
                    squid = null;
                    return;
                }
            } else {
                percent = 0.0;
            }
            double easeInOutCirc = easeInOutCirc(1.0 - percent);
            animations.animate((float) easeInOutCirc, 450);
            squid.setPositionAndUpdate(squid.posX, squid.posY + (double) animations.getOutput() * 0.9, squid.posZ);
        }
        if (squid != null && killEffectValue.is("Squid")) {
            squid.squidPitch = 0.0f;
            squid.prevSquidPitch = 0.0f;
            squid.squidYaw = 0.0f;
            squid.squidRotation = 90.0f;
        }
        if (target != null && target.getHealth() <= 0.0f && !mc.theWorld.loadedEntityList.contains(target)) {
            if (killSoundValue.is("MNSJ")) {
                kills++;
                playSound(getSoundType(), 0.6f);
            }
            if (killSoundValue.is("Squid")) {
                playSound(SoundType.MTC, 0.6f);
            }
            if (killEffectValue.is("LightningBolt")) {
                EntityLightningBolt entityLightningBolt = new EntityLightningBolt(mc.theWorld, target.posX, target.posY, target.posZ);
                mc.theWorld.addEntityToWorld((int) (-Math.random() * 100000.0), entityLightningBolt);
                mc.theWorld.playSound(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, "ambient.weather.thunder", 1.0f, 1.0f, false);
                mc.theWorld.playSound(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, "random.explode", 1.0f, 1.0f, false);
            }
            if (killEffectValue.is("Squid")) {
                squid = new EntitySquid(mc.theWorld);
                mc.theWorld.addEntityToWorld(-8, squid);
                squid.setPosition(target.posX, target.posY, target.posZ);
            }
            target = null;
        }
        if (target != null && target.getHealth() <= 0.0f) {
            switch (killEffectValue.getMode()) {
                case "Totem": {
                    triggerTotemEffect((EntityPlayer) target);
                    target = null;
                    break;
                }
                case "Flame": {
                    mc.effectRenderer.emitParticleAtEntity(target, EnumParticleTypes.FLAME);
                    target = null;
                    break;
                }
                case "Smoke": {
                    mc.effectRenderer.emitParticleAtEntity(target, EnumParticleTypes.SMOKE_LARGE);
                    target = null;
                    break;
                }
                case "Water": {
                    mc.effectRenderer.emitParticleAtEntity(target, EnumParticleTypes.WATER_DROP);
                    target = null;
                    break;
                }
                case "Love": {
                    mc.effectRenderer.emitParticleAtEntity(target, EnumParticleTypes.HEART);
                    mc.effectRenderer.emitParticleAtEntity(target, EnumParticleTypes.WATER_DROP);
                    target = null;
                    break;
                }
                case "Blood": {
                    for (int i = 0; i < 10; ++i) {
                        mc.effectRenderer.spawnEffectParticle(EnumParticleTypes.BLOCK_CRACK.getParticleID(), target.posX, target.posY + (double) (target.height / 2.0f), target.posZ, target.motionX + (double) KillEffect.nextFloat(-0.5f, 0.5f), target.motionY + (double) KillEffect.nextFloat(-0.5f, 0.5f), target.motionZ + (double) KillEffect.nextFloat(-0.5f, 0.5f), Block.getStateId(Blocks.redstone_block.getDefaultState()));
                    }
                    target = null;
                }
            }
        }
    }

    private void triggerTotemEffect(EntityPlayer player) {
        for (int i = 0; i < 100; i++) {
            double offsetX = player.worldObj.rand.nextGaussian() * 0.02D;
            double offsetY = player.worldObj.rand.nextGaussian() * 0.02D;
            double offsetZ = player.worldObj.rand.nextGaussian() * 0.02D;
            player.worldObj.spawnParticle(EnumParticleTypes.SPELL,
                    player.posX + (player.worldObj.rand.nextDouble() - 0.5D) * (double)player.width,
                    player.posY + player.worldObj.rand.nextDouble() * (double)player.height - 0.25D,
                    player.posZ + (player.worldObj.rand.nextDouble() - 0.5D) * (double)player.width,
                    offsetX, offsetY, offsetZ);
        }
    }

    private SoundType getSoundType() {
        switch (kills) {
            case 1 :
                return SoundType.KILL;
            case 2 :
                return SoundType.Two_KILL;
            case 3 :
                return SoundType.Three_KILL;
            case 4 :
                return SoundType.Four_KILL;
            case 5:
                return SoundType.Five_KILL;
        }
        if (kills >= 6) return SoundType.Five_KILL;
        return SoundType.KILL;
    }

    public void playSound(SoundType st, float volume) {
        new Thread(() -> {
            try {
                AudioInputStream as = AudioSystem.getAudioInputStream(new BufferedInputStream(Objects.requireNonNull(getClass().getResourceAsStream("/assets/minecraft/bloodline/sound/" + st.getName()))));
                Clip clip = AudioSystem.getClip();
                clip.open(as);
                clip.start();
                FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                gainControl.setValue(volume);
                clip.start();
            } catch (IOException | LineUnavailableException | UnsupportedAudioFileException e) {
                e.printStackTrace();
            }
        }).start();
    }
    @Listener
    public void onWorld(WorldEvent event) {
        kills = 0;
    }
    @Listener
    public void onAttack(AttackEvent event) {
        if (event.getTarget() != null && !(event.getTarget() instanceof EntityTNTPrimed)) {
            target = (EntityLivingBase) event.getTarget();
        }
    }

    public enum SoundType {
        MTC("kills.wav"),
        KILL("kill.wav"),
        Two_KILL("two_kill.wav"),
        Three_KILL("three_kill.wav"),
        Four_KILL("four_kill.wav"),
        Five_KILL("five_kill.wav");
        final String music;

        SoundType(String fileName) {
            music = fileName;
        }

        String getName() {
            return music;
        }
    }
}
