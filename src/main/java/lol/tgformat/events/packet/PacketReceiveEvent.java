package lol.tgformat.events.packet;

import lol.tgformat.api.event.events.callables.EventCancellable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;

/**
 * @author TG_format
 * @since 2024/6/1 0:26
 */
@Getter
@AllArgsConstructor
public class PacketReceiveEvent extends EventCancellable {
    private Packet<?> packet;
    private INetHandler netHandler;
}
