package lol.tgformat.utils.mobends.animation;

import lol.tgformat.utils.mobends.data.EntityData;
import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.EntityLivingBase;

public abstract class Animation {
    public abstract void animate(EntityLivingBase argEntity, ModelBase argModel, EntityData argData);

    public abstract String getName();

}
