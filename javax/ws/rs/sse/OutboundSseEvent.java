package javax.ws.rs.sse;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import java.lang.reflect.Type;

public interface OutboundSseEvent extends SseEvent
{
    Class<?> getType();
    
    Type getGenericType();
    
    MediaType getMediaType();
    
    Object getData();
    
    public interface Builder
    {
        Builder id(final String p0);
        
        Builder name(final String p0);
        
        Builder reconnectDelay(final long p0);
        
        Builder mediaType(final MediaType p0);
        
        Builder comment(final String p0);
        
        Builder data(final Class p0, final Object p1);
        
        Builder data(final GenericType p0, final Object p1);
        
        Builder data(final Object p0);
        
        OutboundSseEvent build();
    }
}
