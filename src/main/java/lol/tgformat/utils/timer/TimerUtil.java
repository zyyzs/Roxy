package lol.tgformat.utils.timer;

import java.util.Random;

public class TimerUtil {
    private long time = -1L;
    private long lastMS;
    private boolean run = true;
    public long getCurrentMS() {
        return System.nanoTime() / 1000000L;
    }
    public long getTimeElapsed() {
        return System.currentTimeMillis() - lastMS;
    }
    public void setTimeElapsed(long time) {
        this.lastMS = System.currentTimeMillis() - time;
    }
    public long getTimePassed() {
        return System.currentTimeMillis() - this.lastMS;
    }
    public boolean hasTimePassed(long time) {
        return System.currentTimeMillis() - lastMS >= time;
    }
    public boolean hasReached(final double milliseconds) {
        return this.getCurrentMS() - this.lastMS >= milliseconds;
    }
    public long hasTimeLeft(long ms) {
        return ms + lastMS - System.currentTimeMillis();
    }

    public static long randomDelay(final int minDelay, final int maxDelay) {
        return nextInt(minDelay, maxDelay);
    }

    public static int nextInt(final int startInclusive, final int endExclusive) {
        return (endExclusive - startInclusive <= 0) ? startInclusive : (startInclusive + new Random().nextInt(endExclusive - startInclusive));
    }

    public int convertToMS(final int d) {
        return 1000 / d;
    }

    public long getElapsedTime() {
        return System.currentTimeMillis() - this.lastMS;
    }

    public void reset() {
        this.lastMS = this.getCurrentMS();
    }

    public boolean delay(final float milliSec) {
        return this.getCurrentMS() - this.lastMS >= milliSec;
    }

    public boolean isDelayComplete(final double valueState) {
        return System.currentTimeMillis() - this.lastMS >= valueState;
    }

    public double getLastDelay() {
        return (double)(this.getCurrentMS() - this.getLastMS());
    }

    public long getLastMS() {
        return this.lastMS;
    }

    public final long getDifference() {
        return this.getCurrentMS() - this.lastMS;
    }

    public final boolean hasPassed(final long milliseconds) {
        return this.getCurrentMS() - this.lastMS > milliseconds;
    }

    public boolean hasTimeElapsed(final long time, final boolean reset) {
        if (System.currentTimeMillis() - this.lastMS > time) {
            if (reset) {
                this.reset();
            }
            return true;
        }
        return false;
    }

    public boolean hasTimeElapsed2(long milliseconds) {
        return this.run && this.getElapsedTime() >= milliseconds;
    }

    public boolean hasTimeElapsed(final long time) {
        return System.currentTimeMillis() - this.lastMS > time;
    }

    public long getLastMs() {
        return this.lastMS;
    }

    public void setTime(final long time) {
        this.lastMS = time;
    }
}

