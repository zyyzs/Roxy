package lol.tgformat.events;

import lol.tgformat.api.event.events.callables.EventCancellable;
import lombok.Getter;
import net.minecraft.entity.Entity;

/**
 * @author TG_format
 * @since 2024/6/9 上午8:53
 */
@Getter
public class AttackEvent extends EventCancellable {
    private final Entity target;

    public AttackEvent(Entity target) {
        this.target = target;
    }
}
