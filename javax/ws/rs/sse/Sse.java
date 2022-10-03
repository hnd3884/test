package javax.ws.rs.sse;

public interface Sse
{
    OutboundSseEvent.Builder newEventBuilder();
    
    default OutboundSseEvent newEvent(final String data) {
        if (data == null) {
            throw new IllegalArgumentException("Parameter 'data' must not be null.");
        }
        return this.newEventBuilder().data(String.class, data).build();
    }
    
    default OutboundSseEvent newEvent(final String name, final String data) {
        if (data == null) {
            throw new IllegalArgumentException("Parameter 'data' must not be null.");
        }
        if (name == null) {
            throw new IllegalArgumentException("Parameter 'name' must not be null.");
        }
        return this.newEventBuilder().data(String.class, data).name(name).build();
    }
    
    SseBroadcaster newBroadcaster();
}
