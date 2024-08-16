package lol.tgformat.ui.utils;

/**
 * @author TG_format
 * @since 2024/6/9 下午7:01
 */
public enum Direction {
    FORWARDS,
    BACKWARDS;

    public Direction opposite() {
        if (this == Direction.FORWARDS) {
            return Direction.BACKWARDS;
        } else return Direction.FORWARDS;
    }


    public boolean forwards() {
        return this == Direction.FORWARDS;
    }

    public boolean backwards() {
        return this == Direction.BACKWARDS;
    }

}

