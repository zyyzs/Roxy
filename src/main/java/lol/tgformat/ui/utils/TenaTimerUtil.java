package lol.tgformat.ui.utils;

/**
 * @author TG_format
 * @since 2024/6/9 下午7:00
 */
public class TenaTimerUtil {

    public long lastMS = System.currentTimeMillis();

    public void reset() {
        lastMS = System.currentTimeMillis();
    }

    public boolean hasTimeElapsed(long time, boolean reset) {
        if (System.currentTimeMillis() - lastMS > time) {
            if (reset) reset();
            return true;
        }

        return false;
    }

    public boolean hasTimeElapsed(long time) {
        return System.currentTimeMillis() - lastMS > time;
    }

    public boolean hasTimeElapsed(double time) {
        return hasTimeElapsed((long) time);
    }


    public long getTime() {
        return System.currentTimeMillis() - lastMS;
    }


    public void setTime(long time) {
        lastMS = time;
    }

}
