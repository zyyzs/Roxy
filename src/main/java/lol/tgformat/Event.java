package lol.tgformat;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Event {
    private boolean cancelled;

    public void cancel() {
        this.cancelled = true;
    }

    public static class StateEvent extends Event {
        private boolean pre = true;

        public boolean isPost() {
            return !pre;
        }

        public void setPost() {
            pre = false;
        }
    }
}
