package io.opencensus.trace;

import java.util.Arrays;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

@Immutable
public final class SpanContext
{
    private static final Tracestate TRACESTATE_DEFAULT;
    private final TraceId traceId;
    private final SpanId spanId;
    private final TraceOptions traceOptions;
    private final Tracestate tracestate;
    public static final SpanContext INVALID;
    
    @Deprecated
    public static SpanContext create(final TraceId traceId, final SpanId spanId, final TraceOptions traceOptions) {
        return create(traceId, spanId, traceOptions, SpanContext.TRACESTATE_DEFAULT);
    }
    
    public static SpanContext create(final TraceId traceId, final SpanId spanId, final TraceOptions traceOptions, final Tracestate tracestate) {
        return new SpanContext(traceId, spanId, traceOptions, tracestate);
    }
    
    public TraceId getTraceId() {
        return this.traceId;
    }
    
    public SpanId getSpanId() {
        return this.spanId;
    }
    
    public TraceOptions getTraceOptions() {
        return this.traceOptions;
    }
    
    public Tracestate getTracestate() {
        return this.tracestate;
    }
    
    public boolean isValid() {
        return this.traceId.isValid() && this.spanId.isValid();
    }
    
    @Override
    public boolean equals(@Nullable final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof SpanContext)) {
            return false;
        }
        final SpanContext that = (SpanContext)obj;
        return this.traceId.equals(that.traceId) && this.spanId.equals(that.spanId) && this.traceOptions.equals(that.traceOptions);
    }
    
    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[] { this.traceId, this.spanId, this.traceOptions });
    }
    
    @Override
    public String toString() {
        return "SpanContext{traceId=" + this.traceId + ", spanId=" + this.spanId + ", traceOptions=" + this.traceOptions + "}";
    }
    
    private SpanContext(final TraceId traceId, final SpanId spanId, final TraceOptions traceOptions, final Tracestate tracestate) {
        this.traceId = traceId;
        this.spanId = spanId;
        this.traceOptions = traceOptions;
        this.tracestate = tracestate;
    }
    
    static {
        TRACESTATE_DEFAULT = Tracestate.builder().build();
        INVALID = new SpanContext(TraceId.INVALID, SpanId.INVALID, TraceOptions.DEFAULT, SpanContext.TRACESTATE_DEFAULT);
    }
}
