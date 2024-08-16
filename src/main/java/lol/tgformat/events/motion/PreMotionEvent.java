package lol.tgformat.events.motion;

import lol.tgformat.api.event.events.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @author TG_format
 * @since 2024/5/31 23:28
 */
@Getter
@Setter
@AllArgsConstructor
public class PreMotionEvent implements Event {
    private float yaw;
    private float pitch;
    private boolean onGround;
}
