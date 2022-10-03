package io.opencensus.trace;

import io.opencensus.internal.Utils;
import java.util.concurrent.Callable;
import com.google.errorprone.annotations.MustBeClosed;
import io.opencensus.common.Scope;
import javax.annotation.Nullable;
import java.util.List;

public abstract class SpanBuilder
{
    public abstract SpanBuilder setSampler(final Sampler p0);
    
    public abstract SpanBuilder setParentLinks(final List<Span> p0);
    
    public abstract SpanBuilder setRecordEvents(final boolean p0);
    
    public SpanBuilder setSpanKind(@Nullable final Span.Kind spanKind) {
        return this;
    }
    
    public abstract Span startSpan();
    
    @MustBeClosed
    public final Scope startScopedSpan() {
        return CurrentSpanUtils.withSpan(this.startSpan(), true);
    }
    
    public final void startSpanAndRun(final Runnable runnable) {
        final Span span = this.startSpan();
        CurrentSpanUtils.withSpan(span, true, runnable).run();
    }
    
    public final <V> V startSpanAndCall(final Callable<V> callable) throws Exception {
        final Span span = this.startSpan();
        return CurrentSpanUtils.withSpan(span, true, callable).call();
    }
    
    static final class NoopSpanBuilder extends SpanBuilder
    {
        static NoopSpanBuilder createWithParent(final String spanName, @Nullable final Span parent) {
            return new NoopSpanBuilder(spanName);
        }
        
        static NoopSpanBuilder createWithRemoteParent(final String spanName, @Nullable final SpanContext remoteParentSpanContext) {
            return new NoopSpanBuilder(spanName);
        }
        
        @Override
        public Span startSpan() {
            return BlankSpan.INSTANCE;
        }
        
        @Override
        public SpanBuilder setSampler(@Nullable final Sampler sampler) {
            return this;
        }
        
        @Override
        public SpanBuilder setParentLinks(final List<Span> parentLinks) {
            return this;
        }
        
        @Override
        public SpanBuilder setRecordEvents(final boolean recordEvents) {
            return this;
        }
        
        @Override
        public SpanBuilder setSpanKind(@Nullable final Span.Kind spanKind) {
            return this;
        }
        
        private NoopSpanBuilder(final String name) {
            Utils.checkNotNull(name, "name");
        }
    }
}
