package lol.tgformat.api.event;

public final class EventAPI
{
    public static final String VERSION;
    public static final String[] AUTHORS;
    
    private EventAPI() {
    }
    
    static {
        VERSION = String.format("%s-%s", "0.7", "beta");
        AUTHORS = new String[] { "黑暗魔术师6" };
    }
}
