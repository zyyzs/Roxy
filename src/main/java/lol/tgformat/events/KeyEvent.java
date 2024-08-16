package lol.tgformat.events;

import lol.tgformat.api.event.events.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @author TG_format
 * @since 2024/5/31 22:42
 */
@Getter
@Setter
@AllArgsConstructor
public class KeyEvent implements Event {
    private int key;
}
