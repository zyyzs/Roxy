package net.netease.utils;

/**
 * @author TG_format
 * @since 2024/6/1 15:55
 */
public class SmoothStepAnimation extends Animation {
    public SmoothStepAnimation(int ms, double endPoint) {
        super(ms, endPoint);
    }

    public SmoothStepAnimation(int ms, double endPoint, Direction direction) {
        super(ms, endPoint, direction);
    }

    protected double getEquation(double x) {
        return -2.0D * Math.pow(x, 3.0D) + 3.0D * Math.pow(x, 2.0D);
    }
}
