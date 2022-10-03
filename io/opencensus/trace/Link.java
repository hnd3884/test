package io.opencensus.trace;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.concurrent.Immutable;

@Immutable
public abstract class Link
{
    private static final Map<String, AttributeValue> EMPTY_ATTRIBUTES;
    
    public static Link fromSpanContext(final SpanContext context, final Type type) {
        return new AutoValue_Link(context.getTraceId(), context.getSpanId(), type, Link.EMPTY_ATTRIBUTES);
    }
    
    public static Link fromSpanContext(final SpanContext context, final Type type, final Map<String, AttributeValue> attributes) {
        return new AutoValue_Link(context.getTraceId(), context.getSpanId(), type, Collections.unmodifiableMap((Map<? extends String, ? extends AttributeValue>)new HashMap<String, AttributeValue>(attributes)));
    }
    
    public abstract TraceId getTraceId();
    
    public abstract SpanId getSpanId();
    
    public abstract Type getType();
    
    public abstract Map<String, AttributeValue> getAttributes();
    
    Link() {
    }
    
    static {
        EMPTY_ATTRIBUTES = Collections.emptyMap();
    }
    
    public enum Type
    {
        CHILD_LINKED_SPAN, 
        PARENT_LINKED_SPAN;
    }
}
