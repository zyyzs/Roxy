package lol.tgformat.module.impl.render;

import lol.tgformat.Client;
import lol.tgformat.api.event.Listener;
import lol.tgformat.events.TickEvent;
import lol.tgformat.events.render.Render3DEvent;
import lol.tgformat.module.Module;
import lol.tgformat.module.ModuleManager;
import lol.tgformat.module.ModuleType;
import lol.tgformat.module.values.impl.BooleanSetting;
import lol.tgformat.utils.mobends.AnimatedEntity;
import lol.tgformat.utils.mobends.client.renderer.entity.RenderBendsPlayer;
import lol.tgformat.utils.mobends.data.Data_Player;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

public class MoBends extends Module {
    public static final ResourceLocation texture_NULL = new ResourceLocation("mobends/textures/white.png");
    public final BooleanSetting swordTrail = new BooleanSetting("Sword Trail", true);
    public final BooleanSetting spinAttack = new BooleanSetting("Spin attack", false);
    private boolean register;

    public MoBends() {
        super("MoreBends", ModuleType.Render);
        register = false;
    }

    public float ticks = 0.0f;
    public float ticksPerFrame = 0.0f;

    @Override
    public void onEnable() {
        if (!register) {
            AnimatedEntity.register();
            register = true;
        }
    }

    @Listener
    public void onRender3D(Render3DEvent event) {
        if (!this.isState()) {
            return;
        }
        if (mc.theWorld == null) {
            return;
        }

        for (int i = 0; i < Data_Player.dataList.size(); i++) {
            Data_Player.dataList.get(i).update(event.getPartialTicks());
        }

        if (mc.thePlayer != null) {
            float newTicks = mc.thePlayer.ticksExisted + event.getPartialTicks();
            if (!(mc.theWorld.isRemote && mc.isGamePaused())) {
                ticksPerFrame = Math.min(Math.max(0F, newTicks - ticks), 1F);
                ticks = newTicks;
            } else {
                ticksPerFrame = 0F;
            }
        }
    }

    @Listener
    public void onTick(TickEvent event) {
        if (!this.isState()) {
            return;
        }
        if (mc.theWorld == null) {
            return;
        }

        for (int i = 0; i < Data_Player.dataList.size(); i++) {
            Data_Player data = Data_Player.dataList.get(i);
            Entity entity = mc.theWorld.getEntityByID(data.entityID);
            if (entity != null) {
                if (!data.entityType.equalsIgnoreCase(entity.getName())) {
                    Data_Player.dataList.remove(data);
                    Data_Player.add(new Data_Player(entity.getEntityId()));
                    //BendsLogger.log("Reset entity",BendsLogger.DEBUG);
                } else {

                    data.motion_prev.set(data.motion);

                    data.motion.x = (float) entity.posX - data.position.x;
                    data.motion.y = (float) entity.posY - data.position.y;
                    data.motion.z = (float) entity.posZ - data.position.z;

                    data.position = new Vector3f((float) entity.posX, (float) entity.posY, (float) entity.posZ);
                }
            } else {
                Data_Player.dataList.remove(data);
            }
        }
    }

    public boolean onRenderLivingEvent(RendererLivingEntity renderer, EntityLivingBase entity, double x, double y, double z, float entityYaw, float partialTicks) {
        if (!this.isState() || renderer instanceof RenderBendsPlayer) {
            return false;
        }

        AnimatedEntity animatedEntity = AnimatedEntity.getByEntity(entity);

        if (animatedEntity != null && entity instanceof EntityPlayer) {
            AbstractClientPlayer player = (AbstractClientPlayer) entity;
            if (ModuleManager.getModule(Chams.class).isState()) {
                GL11.glEnable(32823);
                GL11.glPolygonOffset(1.0f, -1100000.0f);
            }
            AnimatedEntity.getPlayerRenderer(player).doRender(player, x, y, z, entityYaw, partialTicks);
            if (ModuleManager.getModule(Chams.class).isState()) {
                GL11.glDisable(32823);
                GL11.glPolygonOffset(1.0f, 1100000.0f);
            }
            return true;
        }
        return false;
    }
}
