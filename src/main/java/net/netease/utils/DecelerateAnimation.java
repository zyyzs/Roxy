package net.netease.utils;

/**
 * @author TG_format
 * @since 2024/6/1 15:32
 */
public class DecelerateAnimation extends Animation {
    public DecelerateAnimation(int ms, double endPoint) {
        super(ms, endPoint);
    }

    public DecelerateAnimation(int ms, double endPoint, Direction direction) {
        super(ms, endPoint, direction);
    }

    protected double getEquation(double x) {
        return 1.0D - (x - 1.0D) * (x - 1.0D);
    }
}
