package io.opencensus.trace.samplers;

import java.util.Iterator;
import io.opencensus.trace.Span;
import java.util.List;
import io.opencensus.trace.SpanId;
import io.opencensus.trace.TraceId;
import javax.annotation.Nullable;
import io.opencensus.trace.SpanContext;
import io.opencensus.internal.Utils;
import javax.annotation.concurrent.Immutable;
import io.opencensus.trace.Sampler;

@Immutable
abstract class ProbabilitySampler extends Sampler
{
    abstract double getProbability();
    
    abstract long getIdUpperBound();
    
    static ProbabilitySampler create(final double probability) {
        Utils.checkArgument(probability >= 0.0 && probability <= 1.0, (Object)"probability must be in range [0.0, 1.0]");
        long idUpperBound;
        if (probability == 0.0) {
            idUpperBound = Long.MIN_VALUE;
        }
        else if (probability == 1.0) {
            idUpperBound = Long.MAX_VALUE;
        }
        else {
            idUpperBound = (long)(probability * 9.223372036854776E18);
        }
        return new AutoValue_ProbabilitySampler(probability, idUpperBound);
    }
    
    @Override
    public final boolean shouldSample(@Nullable final SpanContext parentContext, @Nullable final Boolean hasRemoteParent, final TraceId traceId, final SpanId spanId, final String name, @Nullable final List<Span> parentLinks) {
        if (parentContext != null && parentContext.getTraceOptions().isSampled()) {
            return true;
        }
        if (parentLinks != null) {
            for (final Span parentLink : parentLinks) {
                if (parentLink.getContext().getTraceOptions().isSampled()) {
                    return true;
                }
            }
        }
        return Math.abs(traceId.getLowerLong()) < this.getIdUpperBound();
    }
    
    @Override
    public final String getDescription() {
        return String.format("ProbabilitySampler{%.6f}", this.getProbability());
    }
}
