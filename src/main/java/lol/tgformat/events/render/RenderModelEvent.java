package lol.tgformat.events.render;

import lol.tgformat.api.event.events.Event;
import net.minecraft.entity.EntityLivingBase;

public class RenderModelEvent implements Event {

    private final EntityLivingBase entity;
    private final Runnable modelRenderer;
    private final Runnable layerRenderer;

    public RenderModelEvent(EntityLivingBase entity, Runnable modelRenderer, Runnable layerRenderer) {
        this.entity = entity;
        this.modelRenderer = modelRenderer;
        this.layerRenderer = layerRenderer;
    }

    public EntityLivingBase getEntity() {
        return entity;
    }

    public void drawModel() {
        this.modelRenderer.run();
    }

    public void drawLayers() {
        this.layerRenderer.run();
    }

    private boolean pre = true;

    public boolean isPost() {
            return !pre;
    }

    public void setPost() {
            pre = false;
        }

}
