package lol.tgformat.module.impl.render;

import lol.tgformat.api.event.Listener;
import lol.tgformat.events.motion.PreMotionEvent;
import lol.tgformat.events.render.Render3DEvent;
import lol.tgformat.module.Module;
import lol.tgformat.module.ModuleType;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.item.*;
import net.minecraft.util.*;
import net.netease.utils.RenderUtil;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Cylinder;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author TG_format
 * @since 2024/6/25 上午10:29
 */
public class Projectile extends Module {
    float yaw;
    float pitch;

    public Projectile() {
        super("Projectile", ModuleType.Render);
    }

    @Listener
    public void onMotion(PreMotionEvent e) {
        this.yaw = e.getYaw();
        this.pitch = e.getPitch();
    }

    @Listener
    public void onR3D(Render3DEvent e) {
        boolean isBow = false;
        float pitchDifference = 0.0f;
        float motionFactor = 1.5f;
        float motionSlowdown = 0.99f;
        if (mc.thePlayer.getCurrentEquippedItem() != null) {
            float size;
            float gravity;
            Item heldItem = mc.thePlayer.getCurrentEquippedItem().getItem();
            if (heldItem instanceof ItemBow) {
                isBow = true;
                gravity = 0.05f;
                size = 0.3f;
                float power = (float)mc.thePlayer.getItemInUseDuration() / 20.0f;
                if ((power = (power * power + power * 2.0f) / 3.0f) < 0.1) {
                    return;
                }
                if (power > 1.0f) {
                    power = 1.0f;
                }
                motionFactor = power * 3.0f;
            } else if (heldItem instanceof ItemFishingRod) {
                gravity = 0.04f;
                size = 0.25f;
                motionSlowdown = 0.92f;
            } else if (ItemPotion.isSplash(mc.thePlayer.getCurrentEquippedItem().getMetadata())) {
                gravity = 0.05f;
                size = 0.25f;
                pitchDifference = -20.0f;
                motionFactor = 0.5f;
            } else {
                if (!(heldItem instanceof ItemSnowball || heldItem instanceof ItemEnderPearl || heldItem instanceof ItemEgg || heldItem.equals(Item.getItemById(46)))) {
                    return;
                }
                gravity = 0.03f;
                size = 0.25f;
            }
            double posX = RenderManager.renderPosX - (MathHelper.cos(this.yaw / 180.0f * (float)Math.PI) * 0.16f);
            double posY = RenderManager.renderPosY + mc.thePlayer.getEyeHeight() - 0.1f;
            double posZ = RenderManager.renderPosZ - (MathHelper.sin(this.yaw / 180.0f * (float)Math.PI) * 0.16f);
            double motionX = (-MathHelper.sin(this.yaw / 180.0f * (float)Math.PI) * MathHelper.cos(this.pitch / 180.0f * (float)Math.PI)) * (isBow ? 1.0 : 0.4);
            double motionY = (-MathHelper.sin((this.pitch + pitchDifference) / 180.0f * (float)Math.PI)) * (isBow ? 1.0 : 0.4);
            double motionZ = (MathHelper.cos(this.yaw / 180.0f * (float)Math.PI) * MathHelper.cos(this.pitch / 180.0f * (float)Math.PI)) * (isBow ? 1.0 : 0.4);
            float distance = MathHelper.sqrt_double(motionX * motionX + motionY * motionY + motionZ * motionZ);
            motionX /= distance;
            motionY /= distance;
            motionZ /= distance;
            motionX *= motionFactor;
            motionY *= motionFactor;
            motionZ *= motionFactor;
            MovingObjectPosition landingPosition = null;
            boolean hasLanded = false;
            boolean hitEntity = false;
            RenderUtil.enableRender3D(true);
            RenderUtil.color(new Color(145, 222, 0, 243).getRGB());
            GL11.glLineWidth(2.0f);
            GL11.glBegin(3);
            while (!hasLanded && posY > 0.0) {
                Vec3 posBefore = new Vec3(posX, posY, posZ);
                Vec3 posAfter = new Vec3(posX + motionX, posY + motionY, posZ + motionZ);
                landingPosition = mc.theWorld.rayTraceBlocks(posBefore, posAfter, false, true, false);
                posBefore = new Vec3(posX, posY, posZ);
                posAfter = new Vec3(posX + motionX, posY + motionY, posZ + motionZ);
                if (landingPosition != null) {
                    hasLanded = true;
                    posAfter = new Vec3(landingPosition.hitVec.xCoord, landingPosition.hitVec.yCoord, landingPosition.hitVec.zCoord);
                }
                AxisAlignedBB arrowBox = new AxisAlignedBB(posX - size, posY - size, posZ - size, posX + size, posY + size, posZ + size);
                List<?> entityList = this.getEntitiesWithinAABB(arrowBox.addCoord(motionX, motionY, motionZ).expand(1.0, 1.0, 1.0));
                for (Object o : entityList) {
                    MovingObjectPosition possibleEntityLanding;
                    Entity var18 = (Entity) o;
                    if (!var18.canBeCollidedWith() || var18 == mc.thePlayer || (possibleEntityLanding = var18.getEntityBoundingBox().expand(size, size, size).calculateIntercept(posBefore, posAfter)) == null)
                        continue;
                    hitEntity = true;
                    hasLanded = true;
                    landingPosition = possibleEntityLanding;
                }
                BlockPos var20 = new BlockPos(posX += motionX, posY += motionY, posZ += motionZ);
                Block var21 = mc.theWorld.getBlockState(var20).getBlock();
                if (var21.getMaterial() == Material.water) {
                    motionX *= 0.6;
                    motionY *= 0.6;
                    motionZ *= 0.6;
                } else {
                    motionX *= motionSlowdown;
                    motionY *= motionSlowdown;
                    motionZ *= motionSlowdown;
                }
                motionY -= gravity;
                GL11.glVertex3d((posX - RenderManager.renderPosX), (posY - RenderManager.renderPosY), (posZ - RenderManager.renderPosZ));
            }
            GL11.glEnd();
            GL11.glPushMatrix();
            GL11.glTranslated((posX - RenderManager.renderPosX), (posY - RenderManager.renderPosY), (posZ - RenderManager.renderPosZ));
            if (landingPosition != null) {
                int side = landingPosition.sideHit.getIndex();
                if (side == 1 && heldItem instanceof ItemEnderPearl) {
                    RenderUtil.color(new Color(35, 218, 255, 255).getRGB());
                } else if (side == 2) {
                    GlStateManager.rotate(90.0f, 1.0f, 0.0f, 0.0f);
                } else if (side == 3) {
                    GlStateManager.rotate(90.0f, 1.0f, 0.0f, 0.0f);
                } else if (side == 4) {
                    GlStateManager.rotate(90.0f, 0.0f, 0.0f, 1.0f);
                } else if (side == 5) {
                    GlStateManager.rotate(90.0f, 0.0f, 0.0f, 1.0f);
                }
                if (hitEntity) {
                    RenderUtil.color(new Color(35, 218, 255, 255).getRGB());
                }
            }
            this.renderPoint();
            GL11.glPopMatrix();
            RenderUtil.disableRender3D(true);
        }
    }

    private void renderPoint() {
        GL11.glBegin(1);
        GL11.glVertex3d(-0.5, 0.0, 0.0);
        GL11.glVertex3d(0.0, 0.0, 0.0);
        GL11.glVertex3d(0.0, 0.0, -0.5);
        GL11.glVertex3d(0.0, 0.0, 0.0);
        GL11.glVertex3d(0.5, 0.0, 0.0);
        GL11.glVertex3d(0.0, 0.0, 0.0);
        GL11.glVertex3d(0.0, 0.0, 0.5);
        GL11.glVertex3d(0.0, 0.0, 0.0);
        GL11.glEnd();
        Cylinder c = new Cylinder();
        GlStateManager.rotate(90.0f, 1.0f, 0.0f, 0.0f);
        c.setDrawStyle(100011);
        c.draw(0.5f, 0.5f, 0.0f, 256, 27);
    }

    private List<?> getEntitiesWithinAABB(AxisAlignedBB axisalignedBB) {
        ArrayList<Entity> list = new ArrayList<>();
        int chunkMinX = MathHelper.floor_double((axisalignedBB.minX - 2.0) / 16.0);
        int chunkMaxX = MathHelper.floor_double((axisalignedBB.maxX + 2.0) / 16.0);
        int chunkMinZ = MathHelper.floor_double((axisalignedBB.minZ - 2.0) / 16.0);
        int chunkMaxZ = MathHelper.floor_double((axisalignedBB.maxZ + 2.0) / 16.0);
        for (int x = chunkMinX; x <= chunkMaxX; ++x) {
            for (int z = chunkMinZ; z <= chunkMaxZ; ++z) {
                if (!mc.theWorld.getChunkProvider().chunkExists(x, z)) continue;
                mc.theWorld.getChunkFromChunkCoords(x, z).getEntitiesWithinAABBForEntity(mc.thePlayer, axisalignedBB, list, EntitySelectors.selectAnything);
            }
        }
        return list;
    }
}
