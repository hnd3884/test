package io.opencensus.trace;

import java.util.Map;

final class AutoValue_Link extends Link
{
    private final TraceId traceId;
    private final SpanId spanId;
    private final Type type;
    private final Map<String, AttributeValue> attributes;
    
    AutoValue_Link(final TraceId traceId, final SpanId spanId, final Type type, final Map<String, AttributeValue> attributes) {
        if (traceId == null) {
            throw new NullPointerException("Null traceId");
        }
        this.traceId = traceId;
        if (spanId == null) {
            throw new NullPointerException("Null spanId");
        }
        this.spanId = spanId;
        if (type == null) {
            throw new NullPointerException("Null type");
        }
        this.type = type;
        if (attributes == null) {
            throw new NullPointerException("Null attributes");
        }
        this.attributes = attributes;
    }
    
    @Override
    public TraceId getTraceId() {
        return this.traceId;
    }
    
    @Override
    public SpanId getSpanId() {
        return this.spanId;
    }
    
    @Override
    public Type getType() {
        return this.type;
    }
    
    @Override
    public Map<String, AttributeValue> getAttributes() {
        return this.attributes;
    }
    
    @Override
    public String toString() {
        return "Link{traceId=" + this.traceId + ", spanId=" + this.spanId + ", type=" + this.type + ", attributes=" + this.attributes + "}";
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof Link) {
            final Link that = (Link)o;
            return this.traceId.equals(that.getTraceId()) && this.spanId.equals(that.getSpanId()) && this.type.equals(that.getType()) && this.attributes.equals(that.getAttributes());
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        int h = 1;
        h *= 1000003;
        h ^= this.traceId.hashCode();
        h *= 1000003;
        h ^= this.spanId.hashCode();
        h *= 1000003;
        h ^= this.type.hashCode();
        h *= 1000003;
        h ^= this.attributes.hashCode();
        return h;
    }
}
