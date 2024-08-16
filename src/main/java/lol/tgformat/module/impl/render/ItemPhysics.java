package lol.tgformat.module.impl.render;

import lol.tgformat.module.Module;
import lol.tgformat.module.ModuleType;
import lol.tgformat.utils.timer.TimerUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderEntityItem;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import org.lwjgl.input.Keyboard;


public class ItemPhysics extends Module {
    public static final TimerUtil delayTimer = new TimerUtil();
    public ItemPhysics(){
        super("ItemPhysics", ModuleType.Render);
    }
    public static int func_177077_a(EntityItem itemIn, double x, double y, double z, float p_177077_8_, IBakedModel p_177077_9_) {
        ItemStack itemstack = itemIn.getEntityItem();
        Item item = itemstack.getItem();
        boolean var12 = p_177077_9_.isAmbientOcclusion();
        int var13 = RenderEntityItem.func_177078_a(itemstack);
        if (!(item instanceof ItemBlock))
            GlStateManager.translate((float) x, (float) y + 0.1, (float) z);
        else
            GlStateManager.translate((float) x, (float) y + 0.2, (float) z);

        float var16;

        float pitch = itemIn.onGround ? 90 : itemIn.rotationPitch;

        if(delayTimer.hasReached(5)) {
            itemIn.rotationPitch += 1;
        }

        if (itemIn.rotationPitch > 180)
            itemIn.rotationPitch = -180;

        GlStateManager.rotate(pitch, 1, 0, 0);

        GlStateManager.rotate(itemIn.rotationYaw, 0, 0, 1);

        if (!var12) {
            var16 = -0.0F * (float) (var13 - 1) * 0.5F;
            float var17 = -0.0F * (float) (var13 - 1) * 0.5F;
            float var18 = -0.046875F * (float) (var13 - 1) * 0.5F;
            GlStateManager.translate(var16, var17, var18);
        }

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        return var13;
    }
}

