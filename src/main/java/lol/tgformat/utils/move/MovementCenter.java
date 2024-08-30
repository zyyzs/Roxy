package lol.tgformat.utils.move;

import lol.tgformat.accessable.IMinecraft;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Genius
 * @since 2024/8/29 下午2:22
 * IntelliJ IDEA
 */

@Getter
@Setter
public class MovementCenter implements IMinecraft {

    public static boolean start = false;

    public static void cancelMove() {
        if (start) {
            MovementUtil.cancelMove();
        } else if (MovementUtil.cancelMove){
            MovementUtil.resetMove();
        }
    }

}
