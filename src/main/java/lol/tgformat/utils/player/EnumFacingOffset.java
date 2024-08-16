package lol.tgformat.utils.player;

import lombok.Getter;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;

/**
 * @author TG_format
 * @since 2024/7/25 下午7:48
 */
@Getter
public class EnumFacingOffset {
    public EnumFacing enumFacing;
    private final Vec3 offset;

    public EnumFacingOffset(final EnumFacing enumFacing, final Vec3 offset) {
        this.enumFacing = enumFacing;
        this.offset = offset;
    }
}
