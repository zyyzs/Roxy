package lol.tgformat.events;

import lol.tgformat.api.event.events.callables.EventCancellable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.world.World;


@Getter
@Setter
@AllArgsConstructor
public class WorldChangeEvent extends EventCancellable {
    World oldWorld, newWorld;
}
