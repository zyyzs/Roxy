package lol.tgformat.events.render;

import lol.tgformat.api.event.events.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

/**
 * @author TG_format
 * @since 2024/6/7 下午2:12
 */
@AllArgsConstructor
@Getter
@Setter
public class Render2DEvent implements Event {
    private float partialTicks;
    private final ScaledResolution resolution;
    public ScaledResolution getScaledResolution() {
        return new ScaledResolution(Minecraft.getMinecraft());
    }
}