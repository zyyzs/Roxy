package lol.tgformat.events;

import lol.tgformat.api.event.events.callables.EventCancellable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @author TG_format
 * @since 2024/6/9 下午9:16
 */
@Getter
@Setter
@AllArgsConstructor
public class ChatInputEvent extends EventCancellable {
    private String message;
}
