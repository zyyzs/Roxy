package lol.tgformat.events.movement;

import lol.tgformat.api.event.events.callables.EventCancellable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @author TG_format
 * @since 2024/5/31 23:24
 */
@Getter
@Setter
@AllArgsConstructor
public class JumpEvent extends EventCancellable {
    private float jumpMotion;
    private float yaw;
}
