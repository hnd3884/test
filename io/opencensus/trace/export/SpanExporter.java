package io.opencensus.trace.export;

import java.util.Collection;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public abstract class SpanExporter
{
    private static final SpanExporter NOOP_SPAN_EXPORTER;
    
    public static SpanExporter getNoopSpanExporter() {
        return SpanExporter.NOOP_SPAN_EXPORTER;
    }
    
    public abstract void registerHandler(final String p0, final Handler p1);
    
    public abstract void unregisterHandler(final String p0);
    
    static {
        NOOP_SPAN_EXPORTER = new NoopSpanExporter();
    }
    
    public abstract static class Handler
    {
        public abstract void export(final Collection<SpanData> p0);
    }
    
    private static final class NoopSpanExporter extends SpanExporter
    {
        @Override
        public void registerHandler(final String name, final Handler handler) {
        }
        
        @Override
        public void unregisterHandler(final String name) {
        }
    }
}
