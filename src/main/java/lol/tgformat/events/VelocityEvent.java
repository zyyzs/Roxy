package lol.tgformat.events;

import lol.tgformat.api.event.events.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @author TG_format
 * @since 2024/6/1 13:04
 */
@Getter
@Setter
@AllArgsConstructor
public class VelocityEvent implements Event {
    private double reduceAmount;
}
