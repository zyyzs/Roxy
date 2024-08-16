package lol.tgformat.events.packet;

import lol.tgformat.api.event.events.callables.EventCancellable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.network.Packet;

/**
 * @author TG_format
 * @since 2024/8/11 下午5:11
 */
@AllArgsConstructor
@Getter
@Setter
public class PacketSendHigherEvent extends EventCancellable {
    private Packet<?> packet;
    private boolean isNoEvent;
}
