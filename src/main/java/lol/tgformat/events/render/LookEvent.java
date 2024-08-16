package lol.tgformat.events.render;

import lol.tgformat.api.event.events.Event;
import lol.tgformat.utils.vector.Vector2f;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @author TG_format
 * @since 2024/5/31 23:19
 */
@Getter
@Setter
@AllArgsConstructor
public final class LookEvent implements Event {
    private Vector2f rotation;
}
