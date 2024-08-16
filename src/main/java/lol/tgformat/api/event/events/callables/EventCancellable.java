package lol.tgformat.api.event.events.callables;


import lol.tgformat.api.event.events.Cancellable;
import lol.tgformat.api.event.events.Event;

public abstract class EventCancellable implements Event, Cancellable
{
    private boolean cancelled;
    
    protected EventCancellable() {
    }
    
    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }
    
    @Override
    public void setCancelled(final boolean state) {
        this.cancelled = state;
    }
    
    @Override
    public void setCancelled() {
        this.cancelled = true;
    }
}
