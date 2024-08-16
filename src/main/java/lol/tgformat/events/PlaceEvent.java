package lol.tgformat.events;

import lol.tgformat.api.event.events.callables.EventCancellable;
import lombok.Getter;
import lombok.Setter;

/**
 * @author TG_format
 * @since 2024/6/1 1:14
 */
@Setter
@Getter
public class PlaceEvent extends EventCancellable {
    private boolean shouldRightClick;
    private int slot;

    public PlaceEvent(int slot) {
        this.slot = slot;
    }

}
