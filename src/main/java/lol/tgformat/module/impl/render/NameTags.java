package lol.tgformat.module.impl.render;

import lol.tgformat.api.event.Listener;
import lol.tgformat.events.render.Render2DEvent;
import lol.tgformat.events.render.Render3DEvent;
import lol.tgformat.firend.FriendsCollection;
import lol.tgformat.module.Module;
import lol.tgformat.module.ModuleManager;
import lol.tgformat.module.ModuleType;
import lol.tgformat.module.impl.misc.Teams;
import lol.tgformat.module.values.impl.ModeSetting;
import lol.tgformat.ui.font.FontUtil;
import lol.tgformat.ui.utils.MathUtils;
import lol.tgformat.utils.player.PlayerUtil;
import lol.tgformat.utils.render.GlowUtils;
import lol.tgformat.utils.render.RenderUtils;
import lol.tgformat.utils.vector.Vector4f;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.netease.font.FontManager;
import net.netease.utils.RoundedUtils;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.vector.Vector3f;
import tech.skidonion.obfuscator.annotations.Renamer;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import java.awt.*;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;

import static net.netease.font.FontManager.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_VIEWPORT;

/**
 * @Author KuChaZi
 * @Date 2024/7/7 10:46
 * @ClassName: NameTags
 */
@Renamer

@StringEncryption
public class NameTags extends Module {
    private final ModeSetting mode = new ModeSetting("Mode","Default","Default","Old","Shit","Rise");
    private final Map<Entity, Vector4f> entityPosition = new HashMap<>();
    private final Frustum frustum = new Frustum();
    private final FloatBuffer windPos = BufferUtils.createFloatBuffer(4);
    private final IntBuffer intBuffer = GLAllocation.createDirectIntBuffer(16);
    private final FloatBuffer floatBuffer1 = GLAllocation.createDirectFloatBuffer(16);
    private final FloatBuffer floatBuffer2 = GLAllocation.createDirectFloatBuffer(16);
    private final DecimalFormat DF_1 = new DecimalFormat("0.0");
    private static DecimalFormat decimalFormat = new DecimalFormat("########0.00");

    public NameTags() {
        super("NameTags", ModuleType.Render);
    }

    @Listener
    private void onRender3D(Render3DEvent event) {
        entityPosition.clear();
        mc.theWorld.getLoadedEntityList().stream()
                .filter(entity -> entity instanceof EntityPlayer)
                .filter(this::isInView)
                .forEach(entity -> entityPosition.put(entity, this.getEntityPositionsOn2D(entity)));
    }

    Map<String, String> playerNamePrefixCache = new HashMap<>();

    @Listener
    private void onRender(Render2DEvent event) {
        for (Entity entity : entityPosition.keySet()) {
            final EntityLivingBase renderingEntity = (EntityLivingBase) entity;
            String WarnRank = "";
            if (renderingEntity == NameTags.mc.thePlayer) {
                WarnRank = "§a[You] ";
            }
            if (ModuleManager.getModule(Teams.class).isState() && Teams.isSameTeam(renderingEntity)) {
                WarnRank = "§a[Team] ";
            } else if (PlayerUtil.hyt.isStrength((EntityPlayer) renderingEntity) > 0 && renderingEntity != mc.thePlayer && !Teams.isSameTeam(renderingEntity)) {
                WarnRank = "§4[Strength] ";
            } else if (PlayerUtil.hyt.isRegen((EntityPlayer) renderingEntity) > 0 && renderingEntity != mc.thePlayer && !Teams.isSameTeam(renderingEntity)) {
                WarnRank = "§4[Regen] ";
            } else if (PlayerUtil.hyt.isHoldingGodAxe((EntityPlayer) renderingEntity) && renderingEntity != mc.thePlayer && !Teams.isSameTeam(renderingEntity)) {
                WarnRank = "§4[GodAxe] ";
            } else if (PlayerUtil.hyt.isKBBall(renderingEntity.getHeldItem()) && renderingEntity != mc.thePlayer && !Teams.isSameTeam(renderingEntity)) {
                WarnRank = "§4[KBBall] ";
            } else if (PlayerUtil.hyt.hasEatenGoldenApple((EntityPlayer) renderingEntity) > 0 && renderingEntity != mc.thePlayer && !Teams.isSameTeam(renderingEntity)) {
                WarnRank = "§4[GApple] ";
            } else if (FriendsCollection.isFriend(renderingEntity)) {
                WarnRank = "§a[Friend] ";
            }

            if (mc.gameSettings.thirdPersonView == 0 && entity == mc.thePlayer) {
                continue;
            }
            Vector4f pos = entityPosition.get(entity);
            float x = pos.getX(),
                    y = pos.getY(),
                    right = pos.getZ();

            EntityPlayer player = (EntityPlayer) entity;
            if (player.getName().isEmpty()) {
                continue;
            }
            String playerName = player.getName();
            int health = (int) player.getHealth();
            String prefix = playerNamePrefixCache.getOrDefault(playerName, "");
            float healthValue = player.getHealth() / player.getMaxHealth();
            Color healthColor = healthValue > .75 ? new Color(66, 246, 123) : healthValue > .5 ? new Color(228, 255, 105) : healthValue > .35 ? new Color(236, 100, 64) : new Color(255, 65, 68);
            String name = WarnRank + "§f" + prefix + player.getDisplayName().getFormattedText();



            StringBuilder text = new StringBuilder(("§f") + name);

            switch (mode.getMode()){
                case "Default":{
                    double fontScale = 1;
                    float middle = x + ((right - x) / 2);
                    double fontHeight = FontManager.tenacityBold16.getHeight() * fontScale;
                    float textWidth = FontManager.tenacityBold16.getStringWidth(text.toString());
                    middle -= (float) ((textWidth * fontScale) / 2f);

                    glPushMatrix();
                    glTranslated(middle, y - (fontHeight + 2), 0);
                    glScaled(fontScale, fontScale, 1);
                    glTranslated(-middle, -(y - (fontHeight + 2)), 0);

                    if (!Objects.equals(getDisplayName(), playerName)){
                        GlowUtils.drawGlow(middle - 3, (float) (y - (fontHeight + 7)), textWidth + 6, (float) ((fontHeight / fontScale)), 50,new Color(10, 10, 10, 120));
                        RoundedUtils.drawRound(middle - 3, (float) (y - (fontHeight + 7)), textWidth + 6, (float) ((fontHeight / fontScale)), 2, new Color(10, 10, 10, 60));
                        RenderUtils.resetColor();
                        FontUtil.tenacityFont16.drawStringWithShadow(text.toString(), middle, (float) (y - (fontHeight + 4)), healthColor.getRGB());
                    }
                    glPopMatrix();
                    break;
                }
                case "Old":{
                    float x2 = pos.getX();
                    float y2 = pos.getY();
                    double fontScale = 0.8;
                    float middle = x2 + (right - x2) / 2.0f;
                    double fontHeight = (double)arial20.getHeight() * fontScale;
                    float textWidth = arial20.getStringWidth(text.toString());
                    middle = (float)((double)middle - (double)textWidth * fontScale / 2.0);
                    GlStateManager.pushMatrix();
                    GlStateManager.translate((double)middle, (double)y2 - (fontHeight + 2.0), 0.0);
                    GlStateManager.scale(fontScale, fontScale, 1.0);
                    GlStateManager.translate((double)(-middle), -((double)y2 - (fontHeight + 2.0)), 0.0);
                    RoundedUtils.drawRound(middle - 3.0f - 2.0f, (float)((double)y2 - (fontHeight + 7.0)) - 2.0f, textWidth + 6.0f + 6.0f, 1.0f, 1.0f, HUD.color(8));
                    RoundedUtils.drawRound(middle - 3.0f - 2.0f, (float)((double)y2 - (fontHeight + 7.0)), textWidth + 6.0f + 6.0f, (float)(fontHeight / fontScale), 1.0f, new Color(19, 19, 19, 200));
                    RenderUtils.resetColor();
                    GL11.glPopMatrix();
                    FontUtil.tenacityFont16.drawStringWithShadow(text.toString(), middle, (float)((double)y2 - (fontHeight + 5.0)) + 1.0f, -1);
                    break;
                }
                case "Shit":{
                    String Health = String.format("%.0f", renderingEntity.getHealth());
                    String Distance = String.format("%.1f",mc.thePlayer.getClosestDistanceToEntity(renderingEntity));
                    String Exhitext = Distance+"  "+ WarnRank + "§f" + prefix + player.getDisplayName().getFormattedText()+" ";
                    if (renderingEntity == NameTags.mc.thePlayer){
                        Exhitext = WarnRank + "§f" + prefix + player.getDisplayName().getFormattedText()+" ";
                    }
                    float x2 = pos.getX();
                    float y2 = pos.getY();
                    final float Renderinghealth = renderingEntity.getHealth();
                    final float RenderingMaxhealth = renderingEntity.getMaxHealth();
                    final float healthPercentage = Renderinghealth/RenderingMaxhealth;
                    final String healthSting = decimalFormat.format(player.getHealth()/2.0f);
                    double fontScale = 0.8;
                    float middle = x2 + (right - x2) / 2.0f;
                    double fontHeight = (double)arial20.getHeight() * fontScale;
                    float textWidth = arial20.getStringWidth(Exhitext.toString())+arial20.getStringWidth(Health);
                    float Allwidth = textWidth + 6.0f + 6.0f;
                    final float halfWidth = Allwidth / 2.0f;
                    final double theleft = middle - halfWidth;
                    final float theright = middle+halfWidth;
                    middle = (float)((double)middle - (double)textWidth * fontScale / 2.0);
                    GlStateManager.pushMatrix();
                    GlStateManager.translate((double)middle, (double)y2 - (fontHeight + 2.0), 0.0);
                    GlStateManager.scale(fontScale, fontScale, 1.0);
                    GlStateManager.translate((double)(-middle), -((double)y2 - (fontHeight + 2.0)), 0.0);
                    float healthBarWidth = Math.min((float) (renderingEntity.getHealth() / renderingEntity.getMaxHealth()) * Allwidth, Allwidth);
                    RoundedUtils.drawRound(middle - 3.0f - 2.0f, (float)((double)y2 - (fontHeight + 7.0)), textWidth + 6.0f + 6.0f, (float)(fontHeight / fontScale), 1.0f, new Color(19, 19, 19, 50));
                    RoundedUtils.drawRound(middle - 3.0f - 2.0f, (float)((double)y2 - (fontHeight + 7.0)) - 2.0f + (float) fontHeight +4f, healthBarWidth, 1.0f, 1.0f, healthColor);
                    RenderUtils.resetColor();
                    GL11.glPopMatrix();
                    FontUtil.tenacityFont16.drawStringWithShadow(Exhitext.toString(), middle, (float)((double)y2 - (fontHeight + 5.0)) + 1.0f, -1);
                    FontUtil.tenacityFont16.drawStringWithShadow(Health, middle+ arial16.getStringWidth(Exhitext), (float)((double)y2 - (fontHeight + 5.0)) + 1.0f, healthColor.getRGB());
                    break;
                }
                case "Rise":{
                    final float margin = 2;
                    float x2 = pos.getX();
                    float y2 = pos.getY();
                    final float multiplier = 2;
                    final float nH = (float) (tenacityBold16.getHeight()  + margin * multiplier);
                    final float nY = y2 - nH;
                    final float nameWidth = tenacityBold16.getStringWidth(name);
                    RenderUtils.drawRoundedRectangle(x2 - margin - nameWidth / 2, nY, nameWidth + margin * multiplier, nH, 4, new Color(0, 0, 0, 100).getRGB());
                    tenacityBold16.drawCenteredString(name, x2, nY + margin * 2, new Color(79, 199, 200).getRGB());
                    RenderUtils.drawRoundedRectangle(x2 - margin - nameWidth / 2, nY, nameWidth + margin * multiplier, nH,4, new Color(0, 0, 0, 160).getRGB());


                }

            }

        }
    }

    
    public Vector3f projectOn2D(float x, float y, float z, int scaleFactor) {
        GL11.glGetFloat(GL_MODELVIEW_MATRIX, floatBuffer1);
        GL11.glGetFloat(GL_PROJECTION_MATRIX, floatBuffer2);
        GL11.glGetInteger(GL_VIEWPORT, intBuffer);
        if (GLU.gluProject(x, y, z, floatBuffer1, floatBuffer2, intBuffer, windPos)) {
            return new Vector3f(windPos.get(0) / scaleFactor, (mc.displayHeight - windPos.get(1)) / scaleFactor, windPos.get(2));
        }
        return null;
    }

    
    public boolean isInView(Entity ent) {
        frustum.setPosition(mc.getRenderViewEntity().posX, mc.getRenderViewEntity().posY, mc.getRenderViewEntity().posZ);
        return frustum.isBoundingBoxInFrustum(ent.getEntityBoundingBox()) || ent.ignoreFrustumCheck;
    }

    
    public double[] getInterpolatedPos(Entity entity) {
        float ticks = mc.timer.renderPartialTicks;
        return new double[]{
                MathUtils.interpolate(entity.lastTickPosX, entity.posX, ticks) - mc.getRenderManager().viewerPosX,
                MathUtils.interpolate(entity.lastTickPosY, entity.posY, ticks) - mc.getRenderManager().viewerPosY,
                MathUtils.interpolate(entity.lastTickPosZ, entity.posZ, ticks) - mc.getRenderManager().viewerPosZ
        };
    }

    
    public AxisAlignedBB getInterpolatedBoundingBox(Entity entity) {
        final double[] renderingEntityPos = getInterpolatedPos(entity);
        final double entityRenderWidth = entity.width / 1.5;
        return new AxisAlignedBB(renderingEntityPos[0] - entityRenderWidth,
                renderingEntityPos[1], renderingEntityPos[2] - entityRenderWidth, renderingEntityPos[0] + entityRenderWidth,
                renderingEntityPos[1] + entity.height + (entity.isSneaking() ? -0.3 : 0.18), renderingEntityPos[2] + entityRenderWidth).expand(0.15, 0.15, 0.15);
    }

    
    public Vector4f getEntityPositionsOn2D(Entity entity) {
        final AxisAlignedBB bb = getInterpolatedBoundingBox(entity);

        final List<Vector3f> vectors = Arrays.asList(
                new Vector3f((float) bb.minX, (float) bb.minY, (float) bb.minZ),
                new Vector3f((float) bb.minX, (float) bb.maxY, (float) bb.minZ),
                new Vector3f((float) bb.maxX, (float) bb.minY, (float) bb.minZ),
                new Vector3f((float) bb.maxX, (float) bb.maxY, (float) bb.minZ),
                new Vector3f((float) bb.minX, (float) bb.minY, (float) bb.maxZ),
                new Vector3f((float) bb.minX, (float) bb.maxY, (float) bb.maxZ),
                new Vector3f((float) bb.maxX, (float) bb.minY, (float) bb.maxZ),
                new Vector3f((float) bb.maxX, (float) bb.maxY, (float) bb.maxZ));

        Vector4f entityPos = new Vector4f(Float.MAX_VALUE, Float.MAX_VALUE, -1.0f, -1.0f);
        ScaledResolution sr = new ScaledResolution(mc);
        for (Vector3f vector3f : vectors) {
            vector3f = projectOn2D(vector3f.x, vector3f.y, vector3f.z, sr.getScaleFactor());
            if (vector3f != null && vector3f.z >= 0.0 && vector3f.z < 1.0) {
                entityPos.x = Math.min(vector3f.x, entityPos.x);
                entityPos.y = Math.min(vector3f.y, entityPos.y);
                entityPos.z = Math.max(vector3f.x, entityPos.z);
                entityPos.w = Math.max(vector3f.y, entityPos.w);
            }
        }
        return entityPos;
    }
}
