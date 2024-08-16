package net.netease.utils;

/**
 * @author TG_format
 * @since 2024/6/1 13:32
 */
public abstract class Animation {
    public AnimTimeUtil timerUtil;
    protected int duration;
    protected double endPoint;
    protected Direction direction;

    public Animation(int ms, double endPoint) {
        this(ms, endPoint, Direction.FORWARDS);
    }

    public Animation(int ms, double endPoint, Direction direction) {
        this.timerUtil = new AnimTimeUtil();
        this.duration = ms;
        this.endPoint = endPoint;
        this.direction = direction;
    }

    public boolean finished(Direction direction) {
        return this.isDone() && this.direction.equals(direction);
    }

    public double getLinearOutput() {
        return 1.0D - (double)this.timerUtil.getTime() / (double)this.duration * this.endPoint;
    }

    public double getEndPoint() {
        return this.endPoint;
    }

    public void setEndPoint(double endPoint) {
        this.endPoint = endPoint;
    }

    public void reset() {
        this.timerUtil.reset();
    }

    public boolean isDone() {
        return this.timerUtil.hasTimeElapsed((long)this.duration);
    }

    public void changeDirection() {
        this.setDirection(this.direction.opposite());
    }

    public Direction getDirection() {
        return this.direction;
    }

    public void setState(boolean sb) {
        if (sb) {
            this.setDirection(Direction.FORWARDS);
        } else {
            this.setDirection(Direction.BACKWARDS);
        }

    }

    public boolean isState() {
        return this.direction.forwards();
    }

    public Animation setDirection(Direction direction) {
        if (this.direction != direction) {
            this.direction = direction;
            this.timerUtil.setTime(System.currentTimeMillis() - ((long)this.duration - Math.min((long)this.duration, this.timerUtil.getTime())));
        }

        return this;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    protected boolean correctOutput() {
        return false;
    }

    public double getOutput() {
        if (this.direction.forwards()) {
            return this.isDone() ? this.endPoint : this.getEquation((double)this.timerUtil.getTime() / (double)this.duration) * this.endPoint;
        } else if (this.isDone()) {
            return 0.0D;
        } else if (this.correctOutput()) {
            double revTime = (double)Math.min((long)this.duration, Math.max(0L, (long)this.duration - this.timerUtil.getTime()));
            return this.getEquation(revTime / (double)this.duration) * this.endPoint;
        } else {
            return (1.0D - this.getEquation((double)this.timerUtil.getTime() / (double)this.duration)) * this.endPoint;
        }
    }

    protected abstract double getEquation(double var1);
}
