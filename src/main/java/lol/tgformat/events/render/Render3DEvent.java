package lol.tgformat.events.render;

import lol.tgformat.api.event.events.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @author TG_format
 * @since 2024/6/7 下午2:02
 */
@AllArgsConstructor
@Getter
@Setter
public class Render3DEvent implements Event {
    private float partialTicks;
}

