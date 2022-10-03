package javax.ws.rs.sse;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.GenericType;

public interface InboundSseEvent extends SseEvent
{
    boolean isEmpty();
    
    String readData();
    
     <T> T readData(final Class<T> p0);
    
     <T> T readData(final GenericType<T> p0);
    
     <T> T readData(final Class<T> p0, final MediaType p1);
    
     <T> T readData(final GenericType<T> p0, final MediaType p1);
}
