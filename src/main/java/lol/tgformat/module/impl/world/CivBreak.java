package lol.tgformat.module.impl.world;

import lol.tgformat.api.event.Listener;
import lol.tgformat.events.ClickBlockEvent;
import lol.tgformat.events.PreUpdateEvent;
import lol.tgformat.events.motion.PostMotionEvent;
import lol.tgformat.events.render.Render2DEvent;
import lol.tgformat.events.render.Render3DEvent;
import lol.tgformat.module.Module;
import lol.tgformat.module.ModuleType;
import lol.tgformat.module.values.impl.NumberSetting;
import lol.tgformat.utils.block.BlockUtil;
import lol.tgformat.utils.network.PacketUtil;
import lol.tgformat.utils.render.DrawUtil;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.netease.font.FontManager;
import net.netease.utils.AnimationUtil;
import net.netease.utils.RenderUtil;
import net.netease.utils.RoundedUtils;

import java.awt.*;

/**
 * @Author KuChaZi
 * @Date 2024/6/27 7:39
 * @ClassName: CivBreak
 */

public class CivBreak extends Module {
    private final NumberSetting range = new NumberSetting("Range", 4.0, 6.0, 1.0, 0.1);
    private BlockPos blockPos = null;
    private EnumFacing enumFacing = null;
    private boolean breaking = false;
    private float breakPercent = 0.0f;
    private float widthAnim = 0.0f;
    private float alphaAnim = 0.0f;
    private float moveinAnim = 0.0f;
    private boolean canBreak = false;

    public CivBreak() {
        super("CivBreak", ModuleType.World);
    }
    @Listener
    public void onBlockClick(ClickBlockEvent event) {
        this.breaking = true;
        this.blockPos = event.getClickedBlock();
        this.enumFacing = event.getEnumFacing();
    }

    @Listener
    public void onUpdate(PreUpdateEvent event) {
        if (this.blockPos == null || this.enumFacing == null) {
            return;
        }
        this.canBreak = this.breakPercent * 50.0f >= 100.0f ? BlockUtil.getCenterDistance(this.blockPos) < this.range.getValue() : false;
        if (this.canBreak) {
            PacketUtil.sendPacketNoEvent(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, this.blockPos, this.enumFacing));
            this.blockPos = null;
            this.enumFacing = null;
            this.breaking = false;
            this.breakPercent = 0.0f;
        }
        if (this.breaking) {
            this.breakPercent += CivBreak.mc.theWorld.getBlockState(this.blockPos).getBlock().getPlayerRelativeBlockHardness(CivBreak.mc.thePlayer, CivBreak.mc.theWorld, this.blockPos);
        }
    }

    @Listener
    public void onRender2D(Render2DEvent event) {
        ScaledResolution sr = new ScaledResolution(mc);
        int x = sr.getScaledWidth() / 2 - 80;
        int y = sr.getScaledHeight() / 2 + 192;
        if (this.breaking) {
            float progress = Math.min(this.breakPercent / CivBreak.mc.theWorld.getBlockState(this.blockPos).getBlock().getBlockHardness(CivBreak.mc.theWorld, this.blockPos), 1.0f);
            String string = String.format("%.1f", progress * 100.0f) + "%";
            float x1 = (float)(sr.getScaledWidth() / 2) - 72.0f - (float) FontManager.arial16.getStringWidth("100.0%") + 140.0f - 36.0f;
            //FontManager.arial16.drawCenteredStringWithShadow(string, x1, (float)sr.getScaledHeight() - 140.0f + 2.0f, -1);
            this.widthAnim = AnimationUtil.animateSmooth(this.widthAnim, progress * 140.0f, 8.0f / (float)mc.getDebugFPS());
            this.moveinAnim = AnimationUtil.animateSmooth(this.moveinAnim, 18.0f, 4.0f / (float)mc.getDebugFPS());
            this.alphaAnim = AnimationUtil.animateSmooth(this.alphaAnim, 255.0f, 2.0f / (float)mc.getDebugFPS());
        } else {
            this.widthAnim = AnimationUtil.animateSmooth(this.widthAnim, 0.0f, 8.0f / (float)mc.getDebugFPS());
            this.moveinAnim = AnimationUtil.animateSmooth(this.moveinAnim, 0.0f, 4.0f / (float)mc.getDebugFPS());
            this.alphaAnim = AnimationUtil.animateSmooth(this.alphaAnim, 0.0f, 2.0f / (float)mc.getDebugFPS());
        }
       // RoundedUtils.drawGradientRound(sr.getScaledWidth(), + 10, (float) (sr.getScaledHeight() + 7.5), 120.0f, 5.0f, new Color(0, 0, 0, 200), new Color(0, 0, 0, 150), new Color(0, 0, 0, 150), new Color(0, 0, 0, 150));
      //  DrawUtil.drawRoundedRect(x, y, x + 155, y + 15.5, 9.0, RenderUtil.reAlpha(new Color(0,0,0,40), (int)this.alphaAnim).getRGB());
        RoundedUtils.drawRound((float)(sr.getScaledWidth() / 2) - 72.0f, (float)(sr.getScaledHeight() - 120 - 10) - this.moveinAnim, this.widthAnim, 6.0f, 3.0f, RenderUtil.reAlpha(Color.WHITE, (int)this.alphaAnim));

    }

    @Listener
    public void onRender3D(Render3DEvent event) {
        RenderUtil.drawBlockBox(this.blockPos, Color.WHITE, true);
    }

    @Listener
    public void onMotion(PostMotionEvent event) {
        if (this.breaking) {
            PacketUtil.sendC0F();
            PacketUtil.sendPacketNoEvent(new C0APacketAnimation());
        }
    }
}

