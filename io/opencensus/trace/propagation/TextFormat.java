package io.opencensus.trace.propagation;

import io.opencensus.internal.Utils;
import java.util.Collections;
import javax.annotation.Nullable;
import io.opencensus.trace.SpanContext;
import java.util.List;

public abstract class TextFormat
{
    private static final NoopTextFormat NOOP_TEXT_FORMAT;
    
    public abstract List<String> fields();
    
    public abstract <C> void inject(final SpanContext p0, final C p1, final Setter<C> p2);
    
    public abstract <C> SpanContext extract(final C p0, final Getter<C> p1) throws SpanContextParseException;
    
    static TextFormat getNoopTextFormat() {
        return TextFormat.NOOP_TEXT_FORMAT;
    }
    
    static {
        NOOP_TEXT_FORMAT = new NoopTextFormat();
    }
    
    public abstract static class Setter<C>
    {
        public abstract void put(final C p0, final String p1, final String p2);
    }
    
    public abstract static class Getter<C>
    {
        @Nullable
        public abstract String get(final C p0, final String p1);
    }
    
    private static final class NoopTextFormat extends TextFormat
    {
        @Override
        public List<String> fields() {
            return Collections.emptyList();
        }
        
        @Override
        public <C> void inject(final SpanContext spanContext, final C carrier, final Setter<C> setter) {
            Utils.checkNotNull(spanContext, "spanContext");
            Utils.checkNotNull(carrier, "carrier");
            Utils.checkNotNull(setter, "setter");
        }
        
        @Override
        public <C> SpanContext extract(final C carrier, final Getter<C> getter) {
            Utils.checkNotNull(carrier, "carrier");
            Utils.checkNotNull(getter, "getter");
            return SpanContext.INVALID;
        }
    }
}
