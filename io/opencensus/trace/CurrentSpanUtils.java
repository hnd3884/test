package io.opencensus.trace;

import java.util.concurrent.Callable;
import io.opencensus.common.Scope;
import javax.annotation.Nullable;
import io.opencensus.trace.unsafe.ContextUtils;
import io.grpc.Context;

final class CurrentSpanUtils
{
    private CurrentSpanUtils() {
    }
    
    @Nullable
    static Span getCurrentSpan() {
        return ContextUtils.getValue(Context.current());
    }
    
    static Scope withSpan(final Span span, final boolean endSpan) {
        return new ScopeInSpan(span, endSpan);
    }
    
    static Runnable withSpan(final Span span, final boolean endSpan, final Runnable runnable) {
        return new RunnableInSpan(span, runnable, endSpan);
    }
    
    static <C> Callable<C> withSpan(final Span span, final boolean endSpan, final Callable<C> callable) {
        return new CallableInSpan<C>(span, (Callable)callable, endSpan);
    }
    
    private static void setErrorStatus(final Span span, final Throwable t) {
        span.setStatus(Status.UNKNOWN.withDescription((t.getMessage() == null) ? t.getClass().getSimpleName() : t.getMessage()));
    }
    
    private static final class ScopeInSpan implements Scope
    {
        private final Context origContext;
        private final Span span;
        private final boolean endSpan;
        
        private ScopeInSpan(final Span span, final boolean endSpan) {
            this.span = span;
            this.endSpan = endSpan;
            this.origContext = ContextUtils.withValue(Context.current(), span).attach();
        }
        
        @Override
        public void close() {
            Context.current().detach(this.origContext);
            if (this.endSpan) {
                this.span.end();
            }
        }
    }
    
    private static final class RunnableInSpan implements Runnable
    {
        private final Span span;
        private final Runnable runnable;
        private final boolean endSpan;
        
        private RunnableInSpan(final Span span, final Runnable runnable, final boolean endSpan) {
            this.span = span;
            this.runnable = runnable;
            this.endSpan = endSpan;
        }
        
        @Override
        public void run() {
            final Context origContext = ContextUtils.withValue(Context.current(), this.span).attach();
            try {
                this.runnable.run();
            }
            catch (final Throwable t) {
                setErrorStatus(this.span, t);
                if (t instanceof RuntimeException) {
                    throw (RuntimeException)t;
                }
                if (t instanceof Error) {
                    throw (Error)t;
                }
                throw new RuntimeException("unexpected", t);
            }
            finally {
                Context.current().detach(origContext);
                if (this.endSpan) {
                    this.span.end();
                }
            }
        }
    }
    
    private static final class CallableInSpan<V> implements Callable<V>
    {
        private final Span span;
        private final Callable<V> callable;
        private final boolean endSpan;
        
        private CallableInSpan(final Span span, final Callable<V> callable, final boolean endSpan) {
            this.span = span;
            this.callable = callable;
            this.endSpan = endSpan;
        }
        
        @Override
        public V call() throws Exception {
            final Context origContext = ContextUtils.withValue(Context.current(), this.span).attach();
            try {
                return this.callable.call();
            }
            catch (final Exception e) {
                setErrorStatus(this.span, e);
                throw e;
            }
            catch (final Throwable t) {
                setErrorStatus(this.span, t);
                if (t instanceof Error) {
                    throw (Error)t;
                }
                throw new RuntimeException("unexpected", t);
            }
            finally {
                Context.current().detach(origContext);
                if (this.endSpan) {
                    this.span.end();
                }
            }
        }
    }
}
