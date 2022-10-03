package io.opencensus.trace;

import javax.annotation.Nullable;
import java.util.concurrent.Callable;
import com.google.errorprone.annotations.MustBeClosed;
import io.opencensus.internal.Utils;
import io.opencensus.common.Scope;

public abstract class Tracer
{
    private static final NoopTracer noopTracer;
    
    static Tracer getNoopTracer() {
        return Tracer.noopTracer;
    }
    
    public final Span getCurrentSpan() {
        final Span currentSpan = CurrentSpanUtils.getCurrentSpan();
        return (currentSpan != null) ? currentSpan : BlankSpan.INSTANCE;
    }
    
    @MustBeClosed
    public final Scope withSpan(final Span span) {
        return CurrentSpanUtils.withSpan(Utils.checkNotNull(span, "span"), false);
    }
    
    public final Runnable withSpan(final Span span, final Runnable runnable) {
        return CurrentSpanUtils.withSpan(span, false, runnable);
    }
    
    public final <C> Callable<C> withSpan(final Span span, final Callable<C> callable) {
        return CurrentSpanUtils.withSpan(span, false, callable);
    }
    
    public final SpanBuilder spanBuilder(final String spanName) {
        return this.spanBuilderWithExplicitParent(spanName, CurrentSpanUtils.getCurrentSpan());
    }
    
    public abstract SpanBuilder spanBuilderWithExplicitParent(final String p0, @Nullable final Span p1);
    
    public abstract SpanBuilder spanBuilderWithRemoteParent(final String p0, @Nullable final SpanContext p1);
    
    protected Tracer() {
    }
    
    static {
        noopTracer = new NoopTracer();
    }
    
    private static final class NoopTracer extends Tracer
    {
        @Override
        public SpanBuilder spanBuilderWithExplicitParent(final String spanName, @Nullable final Span parent) {
            return SpanBuilder.NoopSpanBuilder.createWithParent(spanName, parent);
        }
        
        @Override
        public SpanBuilder spanBuilderWithRemoteParent(final String spanName, @Nullable final SpanContext remoteParentSpanContext) {
            return SpanBuilder.NoopSpanBuilder.createWithRemoteParent(spanName, remoteParentSpanContext);
        }
    }
}
