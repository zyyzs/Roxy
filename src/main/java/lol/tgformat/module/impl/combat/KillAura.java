package lol.tgformat.module.impl.combat;

import lol.tgformat.api.event.Listener;
import lol.tgformat.component.RotationComponent;
import lol.tgformat.events.PreUpdateEvent;
import lol.tgformat.events.TickEvent;
import lol.tgformat.events.WorldEvent;
import lol.tgformat.events.motion.PostMotionEvent;
import lol.tgformat.events.packet.PacketReceiveEvent;
import lol.tgformat.events.render.Render3DEvent;
import lol.tgformat.firend.FriendsCollection;
import lol.tgformat.module.Module;
import lol.tgformat.module.ModuleManager;
import lol.tgformat.module.ModuleType;
import lol.tgformat.module.impl.misc.Teams;
import lol.tgformat.module.impl.player.Blink;
import lol.tgformat.module.impl.player.Stuck;
import lol.tgformat.module.impl.player.Timer;
import lol.tgformat.module.impl.world.Scaffold;
import lol.tgformat.module.values.impl.BooleanSetting;
import lol.tgformat.module.values.impl.ModeSetting;
import lol.tgformat.module.values.impl.NumberSetting;
import lol.tgformat.ui.utils.Animation;
import lol.tgformat.ui.utils.DecelerateAnimation;
import lol.tgformat.ui.utils.Direction;
import lol.tgformat.ui.utils.RenderUtil;
import lol.tgformat.utils.enums.MovementFix;
import lol.tgformat.utils.math.MathUtil;
import lol.tgformat.utils.network.PacketUtil;
import lol.tgformat.utils.player.CurrentRotationUtil;
import lol.tgformat.utils.rotation.RotationUtil;
import lol.tgformat.utils.timer.TimerUtil;
import lol.tgformat.utils.vector.Vector2f;
import lombok.Getter;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.server.S2FPacketSetSlot;
import net.minecraft.util.*;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;
import tech.skidonion.obfuscator.annotations.Renamer;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import java.awt.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.glPopMatrix;

/**
 * @author TG_format
 * @since 2024/5/31 23:56
 */
@Renamer
@StringEncryption
@NativeObfuscation
public class KillAura extends Module {
    private final ModeSetting attackmode = new ModeSetting("AttackMode", "Post", "Post", "Pre", "Tick");
    private final NumberSetting maxcps = new NumberSetting("MaxCPS", 8,20,0,1);
    private final NumberSetting mincps = new NumberSetting("MinCPS", 6,20,0,1);
    private final NumberSetting startrange = new NumberSetting("StartRange",3.3f,6.0f,1.0f,0.1f);
    private final NumberSetting range = new NumberSetting("Range",3.0f,6.0f,1.0f,0.1f);
    private final NumberSetting rotationspeed = new NumberSetting("RotationSpeed",10f,10f,0f,1f);
    private final ModeSetting autoblockmods = new ModeSetting("AutoBlockMods", "Off", "GrimAC", "Packet", "Off");
    private final ModeSetting moveFix = new ModeSetting("MovementFix", "Silent", "Silent", "Strict", "None", "BackSprint");
    private final ModeSetting espmode = new ModeSetting("ESPMode", "None", "Jello", "Box", "None", "Nursultan","Exhi");
    private final BooleanSetting polygon = new BooleanSetting("Polygon",false);
    private final BooleanSetting keepsprint = new BooleanSetting("KeepSprint", true);
    private final BooleanSetting autodis = new BooleanSetting("AutoDisable", true);
    public KillAura() {
        super("KillAura", ModuleType.Combat);
    }

    public AtomicBoolean block = new AtomicBoolean();
    public final TimerUtil attackTimer = new TimerUtil();
    private final Animation Anim = new DecelerateAnimation(600, 1);
    @Getter
    public static EntityLivingBase target;
    private EntityLivingBase ESPTarget;

    @Override
    public void onDisable() {
        target = null;
    }
    @Override
    public void onEnable() {
        target = null;
    }
    @Listener
    public void onWorld(WorldEvent event) {
        target = null;
    }
    @Listener
    public void onPreUpdate(PreUpdateEvent event){
        if (isNull()) return;
        if(mc.thePlayer.ticksExisted < 10 && autodis.isEnabled()) {
            this.setState(false);
        }
        for (Entity entity : mc.theWorld.loadedEntityList) {
            if (notAttack(entity)) continue;
            target = (EntityLivingBase) entity;
        }
        if (isPlayerNear()) {
            switch (autoblockmods.getMode()) {
                case "Packet": {
                    if (isSword()) {
                        PacketUtil.sendPacket(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem % 8 + 1));
                        mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getCurrentItem()));
                        PacketUtil.send1_12Block();
                        PacketUtil.sendPacket(new C09PacketHeldItemChange((mc.thePlayer.inventory.currentItem)));
                    }
                    break;
                }
                case "Off": {
                    break;
                }
                case "GrimAC":{
                    if (isSword()) {
                        mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, mc.thePlayer.getHeldItem());
                    }
                }
            }
        }
        if (target == null) return;
        if (notAttack(target)) {
            target = null;
            PacketUtil.releaseUseItem(true);
            mc.thePlayer.stopUsingItem();
            return;
        }
        if (mc.thePlayer.getClosestDistanceToEntity(target) <= range.getValue() + 0.02F) {
            Vector2f rotation = RotationUtil.getRotations(target);
            RotationComponent.setRotations(rotation, rotationspeed.getValue(), getMovementFixType());
        }
        if (attackmode.is("Pre")) return;
        attack();
    }
    @Listener
    public void onPost(PostMotionEvent event) {
        this.setSuffix(attackmode.getMode());
        if (isNull()) return;
        if(mc.thePlayer.ticksExisted < 10 && autodis.isEnabled()) {
            this.setState(false);
        }
        for (Entity entity : mc.theWorld.loadedEntityList) {
            if (notAttack(entity)) continue;
            target = (EntityLivingBase) entity;
        }
        if (target == null) return;
        if (notAttack(target)) {
            if (target != null) {
                mc.gameSettings.keyBindUseItem.pressed = false;
            }
            target = null;
            return;
        }
        if (autoblockmods.is("Packet")) {
            if (isSword()) {
                mc.thePlayer.setItemInUse(mc.thePlayer.getHeldItem(), 72000);
                if (mc.thePlayer.isUsingItem() && isSword()) {
                    mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getCurrentItem()));
                    for (int i = 1; i < 5; i++) {
                        PacketUtil.send1_12Block();
                    }
                }
            }
        }
        if (attackmode.is("Post")) return;
        attack();
    }
    public boolean isPlayerNear() {
        for (Entity entity : mc.theWorld.loadedEntityList) {
            if (entityCant(entity) || !(entity instanceof EntityPlayer)) continue;
            return true;
        }
        return false;
    }
    @Listener
    public void onReceive(PacketReceiveEvent event) {
        if (isNull()) return;
        if (event.getPacket() instanceof S2FPacketSetSlot s2f && target != null && autoblockmods.is("GrimAC") && s2f.getItem().getItem() instanceof ItemSword) {
            if (isSword() && mc.thePlayer.isUsingItem()) {
                event.setCancelled();
            }
        }
    }
    public void attack() {
        if (isLookingAtEntity(isGapple() ? RotationComponent.rotations : CurrentRotationUtil.currentRotation , target) && shouldAttack()) {
            if(keepsprint.isEnabled()) {
                mc.playerController.attackEntityNoSlow(target);
                this.attackTimer.reset();
            } else {
                mc.playerController.attackEntity(mc.thePlayer, target);
                this.attackTimer.reset();
            }
        }
    }
//    public boolean isGrimBlocking() {
//        return target != null && autoblockmods.is("GrimAC") && isSword() && isState();
//    }
    private boolean notAttack(Entity entity) {
        Scaffold sca = ModuleManager.getModule(Scaffold.class);
        Blink blink = ModuleManager.getModule(Blink.class);
        Timer timer = ModuleManager.getModule(Timer.class);
        AntiBot antiBot = ModuleManager.getModule(AntiBot.class);
        return !(entity instanceof EntityLivingBase)
                || entity == mc.thePlayer
                || !entity.isEntityAlive()
                || !(mc.thePlayer.getClosestDistanceToEntity(entity) < startrange.getValue())
                || sca.isState()
                || entity == Blink.getFakePlayer()
                || blink.isState()
                || ModuleManager.getModule(Stuck.class).isState()
                || antiBot.isServerBot(entity)
                || Teams.isSameTeam(entity)
                || timer.isState()
                || FriendsCollection.isFriend(entity)
                || entity instanceof EntityVillager
                || entity instanceof EntitySquid;
    }
    private boolean entityCant(Entity entity) {
        Scaffold sca = ModuleManager.getModule(Scaffold.class);
        Blink blink = ModuleManager.getModule(Blink.class);
        Timer timer = ModuleManager.getModule(Timer.class);
        AntiBot antiBot = ModuleManager.getModule(AntiBot.class);
        return !(entity instanceof EntityLivingBase)
                || entity == mc.thePlayer
                || !entity.isEntityAlive()
                || !(mc.thePlayer.getClosestDistanceToEntity(entity) < 6)
                || sca.isState()
                || entity == Blink.getFakePlayer()
                || blink.isState()
                || ModuleManager.getModule(Stuck.class).isState()
                || antiBot.isServerBot(entity)
                || Teams.isSameTeam(entity)
                || timer.isState()
                || FriendsCollection.isFriend(entity);
    }


    private MovementFix getMovementFixType() {
        return switch (moveFix.getMode()) {
            case "None" -> MovementFix.OFF;
            case "Silent" -> MovementFix.NORMAL;
            case "Strict" -> MovementFix.TRADITIONAL;
            case "BackSprint" -> MovementFix.BACKWARDS_SPRINT;
            default -> throw new IllegalStateException("Unexpected value: " + moveFix.getMode());
        };
    }
    @Listener
    public void onTick(TickEvent event) {
        if (attackmode.is("Tick")) {
            attack();
        }
    }
    @Listener
    public void onRender3D(Render3DEvent event) {
        AxisAlignedBB axisAlignedBB = target.getEntityBoundingBox();
        double partialTicks = event.getPartialTicks();
        double baseX = KillAura.target.prevPosX + (KillAura.target.posX - KillAura.target.prevPosX) * partialTicks - RenderManager.renderPosX;
        double baseY = KillAura.target.prevPosY + (KillAura.target.posY - KillAura.target.prevPosY) * partialTicks - RenderManager.renderPosY;
        double baseZ = KillAura.target.prevPosZ + (KillAura.target.posZ - KillAura.target.prevPosZ) * partialTicks - RenderManager.renderPosZ;
        double tickOffsetX = (KillAura.target.posX - KillAura.target.prevPosX) * (double)event.getPartialTicks();
        double tickOffsetY = (KillAura.target.posY - KillAura.target.prevPosY) * (double)event.getPartialTicks();
        double tickOffsetZ = (KillAura.target.posZ - KillAura.target.prevPosZ) * (double)event.getPartialTicks();
        double playerMotionOffsetX = KillAura.mc.thePlayer.motionX + 0.01;
        double playerMotionOffsetY = KillAura.mc.thePlayer.motionY - 0.005;
        double playerMotionOffsetZ = KillAura.mc.thePlayer.motionZ + 0.01;
        double translateX = baseX + tickOffsetX + playerMotionOffsetX;
        double translateY = baseY + tickOffsetY + playerMotionOffsetY;
        double translateZ = baseZ + tickOffsetZ + playerMotionOffsetZ;
        int color2 = KillAura.target.hurtTime > 3 ? new Color(235, 40, 40, 75).getRGB() : (KillAura.target.hurtTime < 3 ? new Color(150, 255, 40, 35).getRGB() : new Color(255, 255, 255, 75).getRGB());
        if (polygon.isEnabled()){
            if (target == null) return;
            Anim.setDirection(target != null ? Direction.FORWARDS : Direction.BACKWARDS);
            if (target != null) {
                ESPTarget = target;
            }
            if (Anim.finished(Direction.BACKWARDS)) {
                ESPTarget = null;
            }
            if (ESPTarget != null) {
                drawCircle(6,new Color(0,0,0).getRGB());
                drawCircle(4,new Color(255,255,255).getRGB());
            }
        }
        switch (espmode.getMode()) {
            case "Box": {
                Anim.setDirection(target != null ? Direction.FORWARDS : Direction.BACKWARDS);
                if (target != null) {
                    ESPTarget = target;
                }
                if (Anim.finished(Direction.BACKWARDS)) {
                    ESPTarget = null;
                }
                if (ESPTarget != null) {
                    RenderUtil.renderBoundingBox(ESPTarget, new Color(255, 255, 255, 134), Anim.getOutput().floatValue());
                }
                break;
            }
            case "Jello": {
                if (target == null) return;
                RenderUtil.drawTargetCapsule(target, 0.5, true, new Color(126,0, 252, 203));
                break;
            }
            case "Nursultan": {
                if (target == null) return;
                float dst = mc.thePlayer.getDistanceToEntity(target);
                javax.vecmath.Vector2f vector2f = RenderUtil.targetESPSPos(target);
                if (vector2f != null) RenderUtil.drawTargetESP2D(vector2f.x, vector2f.y, Color.RED, Color.BLUE, Color.GREEN, Color.PINK, (1.0F - MathHelper.clamp_float(Math.abs(dst - 6.0F) / 60.0F, 0.0F, 0.75F)), 1);
            }
            case "Exhi": {
                Anim.setDirection(target != null ? Direction.FORWARDS : Direction.BACKWARDS);
                if (target != null) {
                    ESPTarget = target;
                }
                if (Anim.finished(Direction.BACKWARDS)) {
                    ESPTarget = null;
                }
                if (ESPTarget != null) {
                    GlStateManager.translate(translateX, translateY, translateZ);
                    AxisAlignedBB offsetBB = axisAlignedBB.offset(-KillAura.target.posX, -KillAura.target.posY, -KillAura.target.posZ).expand(0.1, 0.1, 0.1).offset(0.0, 0.1, 0.0);
                    RenderUtil.drawAxisAlignedBB(offsetBB, true, color2);
                }
                RenderUtil.resetColor();
                GlStateManager.popMatrix();
                break;
            }
        }
    }

    public float cps(){
        return (float) MathUtil.getRandom(mincps.getValue(),maxcps.getValue());
    }
    public boolean shouldAttack() {
        return this.attackTimer.hasReached(1000.0D / (cps() * 1.5D));
    }
    public static boolean isLookingAtEntity(Vector2f rotations, Entity target) {
        double range = 3.0f;
        Vec3 src = mc.thePlayer.getPositionEyes(1.0f);
        Vec3 rotationVec = getVectorForRotation(rotations.y, rotations.x);
        Vec3 dest = src.addVector(rotationVec.xCoord * range, rotationVec.yCoord * range, rotationVec.zCoord * range);
        MovingObjectPosition obj = mc.theWorld.rayTraceBlocks(src, dest, false, false, true);
        if (obj == null) {
            return false;
        }
        return target.getEntityBoundingBox().expand(0.1f, 0.1f, 0.1f).calculateIntercept(src, dest) != null;
    }

    protected static Vec3 getVectorForRotation(float p_getVectorForRotation_1_, float p_getVectorForRotation_2_) {
        float f = MathHelper.cos(-p_getVectorForRotation_2_ * ((float)Math.PI / 180) - (float)Math.PI);
        float f1 = MathHelper.sin(-p_getVectorForRotation_2_ * ((float)Math.PI / 180) - (float)Math.PI);
        float f2 = -MathHelper.cos(-p_getVectorForRotation_1_ * ((float)Math.PI / 180));
        float f3 = MathHelper.sin(-p_getVectorForRotation_1_ * ((float)Math.PI / 180));
        return new Vec3(f1 * f2, f3, f * f2);
    }
    private void drawCircle(float lineWidth, int color) {
        if (target == null) return;
        glPushMatrix();
        RenderUtil.color(color, (float) ((Anim.getOutput().floatValue() / 1) / 2F));
        glDisable(GL_TEXTURE_2D);
        glDisable(GL_DEPTH_TEST);
        glDepthMask(false);
        glLineWidth(lineWidth);
        glEnable(GL_BLEND);
        glEnable(GL_LINE_SMOOTH);

        glBegin(GL_LINE_STRIP);
        float partialTicks = net.minecraft.util.Timer.elapsedPartialTicks;
        double rad = 1;
        double d = (Math.PI * 2.0) / 8;

        double posX = target.posX, posY = target.posY, posZ = target.posZ;
        double lastTickX = target.lastTickPosX, lastTickY = target.lastTickPosY, lastTickZ = target.lastTickPosZ;
        double renderPosX = mc.getRenderManager().renderPosX, renderPosY = mc.getRenderManager().renderPosY, renderPosZ = mc.getRenderManager().renderPosZ;

        double y = lastTickY + (posY - lastTickY) * partialTicks - renderPosY;
        for (double i = 0; i < Math.PI * 2.0; i += d) {
            double x = lastTickX + (posX - lastTickX) * partialTicks + StrictMath.sin(i) * rad - renderPosX,
                    z = lastTickZ + (posZ - lastTickZ) * partialTicks + StrictMath.cos(i) * rad - renderPosZ;
            glVertex3d(x, y, z);
        }
        double x = lastTickX + (posX - lastTickX) * partialTicks - renderPosX,
                z = lastTickZ + (posZ - lastTickZ) * partialTicks + rad - renderPosZ;
        glVertex3d(x, y, z);
        glEnd();

        glDisable(GL_BLEND);
        glDisable(GL_LINE_SMOOTH);
        glDepthMask(true);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_TEXTURE_2D);
        glColor4f(1, 1, 1, 1);
        glPopMatrix();
    }

}