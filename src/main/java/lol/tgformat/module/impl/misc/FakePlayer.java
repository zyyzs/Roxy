package lol.tgformat.module.impl.misc;

import lol.tgformat.module.Module;
import lol.tgformat.module.ModuleType;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import java.util.LinkedList;

@StringEncryption
public class FakePlayer extends Module {
    private EntityOtherPlayerMP fakePlayer;
    private final LinkedList<double[]> positions;
    
    public FakePlayer() {
        super("FakePlayer", ModuleType.Misc);
        this.fakePlayer = null;
        this.positions = new LinkedList<>();
    }
    
    public void onEnable() {
        if (FakePlayer.mc.thePlayer == null) {
            return;
        }
        (this.fakePlayer = new EntityOtherPlayerMP(FakePlayer.mc.theWorld, FakePlayer.mc.thePlayer.getGameProfile())).clonePlayer(FakePlayer.mc.thePlayer, true);
        this.fakePlayer.copyLocationAndAnglesFrom(FakePlayer.mc.thePlayer);
        this.fakePlayer.rotationYawHead = FakePlayer.mc.thePlayer.rotationYawHead;
        FakePlayer.mc.theWorld.addEntityToWorld(-1337, this.fakePlayer);
        synchronized (this.positions) {
            this.positions.add(new double[] { FakePlayer.mc.thePlayer.posX, FakePlayer.mc.thePlayer.getEntityBoundingBox().minY + FakePlayer.mc.thePlayer.getEyeHeight() / 2.0f, FakePlayer.mc.thePlayer.posZ });
            this.positions.add(new double[] { FakePlayer.mc.thePlayer.posX, FakePlayer.mc.thePlayer.getEntityBoundingBox().minY, FakePlayer.mc.thePlayer.posZ });
        }
    }
    
    public void onDisable() {
        if (FakePlayer.mc.thePlayer == null) {
            return;
        }
        if (this.fakePlayer != null) {
            FakePlayer.mc.theWorld.removeEntityFromWorld(this.fakePlayer.getEntityId());
            this.fakePlayer = null;
        }
    }
}
