package lol.tgformat.events.movement;

import lol.tgformat.api.event.events.callables.EventCancellable;
import lombok.AllArgsConstructor;
import lombok.Setter;

/**
 * @author TG_format
 * @since 2024/8/9 下午9:08
 */
@AllArgsConstructor
@Setter
public class MoveEvent extends EventCancellable {
    public double x;
    public double y;
    public double z;
}
