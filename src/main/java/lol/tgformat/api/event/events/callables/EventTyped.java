package lol.tgformat.api.event.events.callables;


import lol.tgformat.api.event.events.Event;
import lol.tgformat.api.event.events.Typed;

public abstract class EventTyped implements Event, Typed
{
    private final byte type;
    
    protected EventTyped(final byte eventType) {
        this.type = eventType;
    }
    
    @Override
    public byte getType() {
        return this.type;
    }
}
