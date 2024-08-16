package lol.tgformat.utils.block;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

/**
 * @author TG_format
 * @since 2024/6/7 上午10:46
 */
@Getter
@AllArgsConstructor
public class BlockInfo {

    private BlockPos pos;
    private EnumFacing facing;

}
