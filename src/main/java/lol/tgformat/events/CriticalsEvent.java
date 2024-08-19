package lol.tgformat.events;

import lol.tgformat.api.event.events.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.entity.Entity;

/**
 * @author TG_format
 * @since 2024/8/19 下午10:16
 */
@Getter
@AllArgsConstructor
public class CriticalsEvent implements Event {
    private Entity entity;
}
