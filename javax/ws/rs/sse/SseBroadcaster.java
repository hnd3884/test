package javax.ws.rs.sse;

import java.util.concurrent.CompletionStage;
import java.util.function.Consumer;
import java.util.function.BiConsumer;

public interface SseBroadcaster extends AutoCloseable
{
    void onError(final BiConsumer<SseEventSink, Throwable> p0);
    
    void onClose(final Consumer<SseEventSink> p0);
    
    void register(final SseEventSink p0);
    
    CompletionStage<?> broadcast(final OutboundSseEvent p0);
    
    void close();
}
