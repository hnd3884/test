package io.opencensus.trace.samplers;

import io.opencensus.trace.Span;
import java.util.List;
import io.opencensus.trace.SpanId;
import io.opencensus.trace.TraceId;
import javax.annotation.Nullable;
import io.opencensus.trace.SpanContext;
import javax.annotation.concurrent.Immutable;
import io.opencensus.trace.Sampler;

@Immutable
final class NeverSampleSampler extends Sampler
{
    @Override
    public boolean shouldSample(@Nullable final SpanContext parentContext, @Nullable final Boolean hasRemoteParent, final TraceId traceId, final SpanId spanId, final String name, final List<Span> parentLinks) {
        return false;
    }
    
    @Override
    public String getDescription() {
        return this.toString();
    }
    
    @Override
    public String toString() {
        return "NeverSampleSampler";
    }
}
