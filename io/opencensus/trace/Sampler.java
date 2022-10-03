package io.opencensus.trace;

import java.util.List;
import javax.annotation.Nullable;

public abstract class Sampler
{
    public abstract boolean shouldSample(@Nullable final SpanContext p0, @Nullable final Boolean p1, final TraceId p2, final SpanId p3, final String p4, final List<Span> p5);
    
    public abstract String getDescription();
}
