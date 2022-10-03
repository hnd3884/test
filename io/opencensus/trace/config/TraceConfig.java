package io.opencensus.trace.config;

public abstract class TraceConfig
{
    private static final NoopTraceConfig NOOP_TRACE_CONFIG;
    
    public abstract TraceParams getActiveTraceParams();
    
    public abstract void updateActiveTraceParams(final TraceParams p0);
    
    public static TraceConfig getNoopTraceConfig() {
        return TraceConfig.NOOP_TRACE_CONFIG;
    }
    
    static {
        NOOP_TRACE_CONFIG = new NoopTraceConfig();
    }
    
    private static final class NoopTraceConfig extends TraceConfig
    {
        @Override
        public TraceParams getActiveTraceParams() {
            return TraceParams.DEFAULT;
        }
        
        @Override
        public void updateActiveTraceParams(final TraceParams traceParams) {
        }
    }
}
