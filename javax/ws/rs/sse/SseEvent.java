package javax.ws.rs.sse;

public interface SseEvent
{
    public static final long RECONNECT_NOT_SET = -1L;
    
    String getId();
    
    String getName();
    
    String getComment();
    
    long getReconnectDelay();
    
    boolean isReconnectDelaySet();
}
