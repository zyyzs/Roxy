package lol.tgformat.events.render;

import lol.tgformat.api.event.events.callables.EventCancellable;
import lombok.Getter;

/**
 * @author TG_format
 * @since 2024/7/28 下午4:56
 */
@Getter
public class ShaderEvent extends EventCancellable {

    private final boolean bloom;

    public ShaderEvent(boolean bloom) {
        this.bloom = bloom;
    }

}
