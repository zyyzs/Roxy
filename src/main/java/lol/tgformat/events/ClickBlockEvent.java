package lol.tgformat.events;

import lol.tgformat.api.event.events.Event;
import lombok.Getter;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

/**
 * @Author KuChaZi
 * @Date 2024/6/27 7:44
 * @ClassName: ClickBlockEvent
 */
@Getter
public class ClickBlockEvent implements Event {
    private final BlockPos clickedBlock;
    private final EnumFacing enumFacing;

    public ClickBlockEvent(BlockPos clickedBlock, EnumFacing enumFacing) {
        this.clickedBlock = clickedBlock;
        this.enumFacing = enumFacing;
    }

}
