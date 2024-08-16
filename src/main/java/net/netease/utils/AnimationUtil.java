package net.netease.utils;

import lol.tgformat.utils.render.SimpleRender;

/**
 * @author TG_format
 * @since 2024/6/1 15:48
 */
public class AnimationUtil {
    private long previousTime = System.nanoTime() / 10000L;
    public static long deltaTime = 500L;

    public void resetTime() {
        long currentTime = System.nanoTime() / 10000L;
        deltaTime = currentTime - this.previousTime;
        this.previousTime = currentTime;
    }
    public static float moveUD(final float current, final float end, final float smoothSpeed, final float minSpeed) {
        float movement = (end - current) * smoothSpeed;
        if (movement > 0.0f) {
            movement = Math.max(minSpeed, movement);
            movement = Math.min(end - current, movement);
        } else if (movement < 0.0f) {
            movement = Math.min(-minSpeed, movement);
            movement = Math.max(end - current, movement);
        }
        return current + movement;
    }
    public static float animate1(final float value, final float target, float speed) {
        if (AnimationUtil.deltaTime <= 1L) {
            AnimationUtil.deltaTime = 500L;
        }
        speed *= 70.0f;
        final float increment = speed * AnimationUtil.deltaTime / 500.0f;
        final float returnValue = value + (target - value) * increment / 200.0f;
        if (Math.abs(target - returnValue) < 0.05 * (4.0 / 5.0)) {
            return target;
        }
        return returnValue;
    }

    public double animate(double value, double target, double speed, boolean tBTV) {
        if (deltaTime <= 1L) {
            deltaTime = 500L;
        }

        speed = speed * (Double)10D / 4.0D;
        double increment = speed * (double)deltaTime / 500.0D;
        double returnValue = value + (target - value) * increment / 200.0D;
        if (!(Math.abs(target - returnValue) < 0.05D * (4.0D / (Double)10D))) {
            if (tBTV) {
                if (returnValue > target) {
                    return target;
                }
            } else if (target > returnValue) {
                return target;
            }

            return returnValue;
        } else {
            return target;
        }
    }
    public static float moveUDFaster(final float current, final float end) {
        float smoothSpeed = SimpleRender.processFPS(0.025F);
        float minSpeed = SimpleRender.processFPS(0.023F);
        final boolean larger = end > current;
        if (smoothSpeed < 0.0f) {
            smoothSpeed = 0.0f;
        } else if (smoothSpeed > 1.0f) {
            smoothSpeed = 1.0f;
        }
        if (minSpeed < 0.0f) {
            minSpeed = 0.0f;
        } else if (minSpeed > 1.0f) {
            minSpeed = 1.0f;
        }
        float movement = (end - current) * smoothSpeed;
        if (movement > 0) {
            movement = Math.max(minSpeed, movement);
            movement = Math.min(end - current, movement);
        } else if (movement < 0) {
            movement = Math.min(-minSpeed, movement);
            movement = Math.max(end - current, movement);
        }
        if (larger) {
            if (end <= current + movement) {
                return end;
            }
        } else {
            if (end >= current + movement) {
                return end;
            }
        }
        return current + movement;
    }
    public double animateNoFast(double value, double target, double speed) {
        if (deltaTime <= 1L) {
            deltaTime = 500L;
        }

        speed = speed * (Double)10D / 4.0D;
        double increment = speed * (double)deltaTime / 500.0D;
        double returnValue = value + (target - value) * increment / 200.0D;
        return Math.abs(target - returnValue) < 0.05D * (4.0D / (Double)10D) ? target : returnValue;
    }

    public static float animate(float value, float target, float speed) {
        if (deltaTime <= 1L) {
            deltaTime = 500L;
        }

        speed *= 70.0F;
        float increment = speed * (float)deltaTime / 500.0F;
        float returnValue = value + (target - value) * increment / 200.0F;
        return (double)Math.abs(target - returnValue) < 0.05D * (4.0D / (Double)10D) ? target : returnValue;
    }

    public double animateRO(double value, double target, double speed) {
        if (deltaTime <= 1L) {
            deltaTime = 500L;
        }

        double increment = speed * (double)deltaTime / 500.0D;
        double returnValue = value + (target - value) * increment / 200.0D;
        return Math.abs(target - returnValue) < 0.05D ? target : returnValue;
    }

    public static float animateSmooth(float current, float target, float speed) {
        if (current == target) {
            return current;
        } else {
            boolean larger = target > current;
            if (speed < 0.0F) {
                speed = 0.0F;
            } else if (speed > 1.0F) {
                speed = 1.0F;
            }

            double dif = Math.max((double)target, (double)current) - Math.min((double)target, (double)current);
            double factor = dif * (double)speed;
            if (factor < 0.1D) {
                factor = 0.1D;
            }

            if (larger) {
                current += (float)factor;
                if (current >= target) {
                    current = target;
                }
            } else if (target < current) {
                current -= (float)factor;
                if (current <= target) {
                    current = target;
                }
            }

            return current;
        }
    }
}

