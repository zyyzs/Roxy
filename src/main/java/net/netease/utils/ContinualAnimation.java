package net.netease.utils;

/**
 * @author TG_format
 * @since 2024/6/1 15:55
 */
public class ContinualAnimation {
    private float output;
    private float endpoint;
    private Animation animation;

    public ContinualAnimation() {
        this.animation = new SmoothStepAnimation(0, 0.0D, Direction.BACKWARDS);
    }

    public void animate(float destination, int ms) {
        this.output = (float)((double)this.endpoint - this.animation.getOutput());
        this.endpoint = destination;
        if (this.output != this.endpoint - destination) {
            this.animation = new SmoothStepAnimation(ms, (double)(this.endpoint - this.output), Direction.BACKWARDS);
        }

    }

    public boolean isDone() {
        return this.output == this.endpoint || this.animation.isDone();
    }

    public float getOutput() {
        this.output = (float)((double)this.endpoint - this.animation.getOutput());
        return this.output;
    }

    public Animation getAnimation() {
        return this.animation;
    }
}

