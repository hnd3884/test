package io.opencensus.trace.unsafe;

import io.opencensus.trace.BlankSpan;
import io.opencensus.internal.Utils;
import javax.annotation.Nullable;
import io.opencensus.trace.Span;
import io.grpc.Context;

public final class ContextUtils
{
    private static final Context.Key<Span> CONTEXT_SPAN_KEY;
    
    private ContextUtils() {
    }
    
    public static Context withValue(final Context context, @Nullable final Span span) {
        return Utils.checkNotNull(context, "context").withValue((Context.Key)ContextUtils.CONTEXT_SPAN_KEY, (Object)span);
    }
    
    public static Span getValue(final Context context) {
        final Span span = (Span)ContextUtils.CONTEXT_SPAN_KEY.get((Context)Utils.checkNotNull(context, "context"));
        return (span == null) ? BlankSpan.INSTANCE : span;
    }
    
    static {
        CONTEXT_SPAN_KEY = Context.key("opencensus-trace-span-key");
    }
}
