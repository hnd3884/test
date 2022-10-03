package io.opencensus.trace;

import java.util.Map;
import io.opencensus.internal.Utils;
import java.util.EnumSet;
import javax.annotation.concurrent.Immutable;

@Immutable
public final class BlankSpan extends Span
{
    public static final BlankSpan INSTANCE;
    
    private BlankSpan() {
        super(SpanContext.INVALID, null);
    }
    
    @Override
    public void putAttribute(final String key, final AttributeValue value) {
        Utils.checkNotNull(key, "key");
        Utils.checkNotNull(value, "value");
    }
    
    @Override
    public void putAttributes(final Map<String, AttributeValue> attributes) {
        Utils.checkNotNull(attributes, "attributes");
    }
    
    @Override
    public void addAnnotation(final String description, final Map<String, AttributeValue> attributes) {
        Utils.checkNotNull(description, "description");
        Utils.checkNotNull(attributes, "attributes");
    }
    
    @Override
    public void addAnnotation(final Annotation annotation) {
        Utils.checkNotNull(annotation, "annotation");
    }
    
    @Deprecated
    @Override
    public void addNetworkEvent(final NetworkEvent networkEvent) {
    }
    
    @Override
    public void addMessageEvent(final MessageEvent messageEvent) {
        Utils.checkNotNull(messageEvent, "messageEvent");
    }
    
    @Override
    public void addLink(final Link link) {
        Utils.checkNotNull(link, "link");
    }
    
    @Override
    public void setStatus(final Status status) {
        Utils.checkNotNull(status, "status");
    }
    
    @Override
    public void end(final EndSpanOptions options) {
        Utils.checkNotNull(options, "options");
    }
    
    @Override
    public String toString() {
        return "BlankSpan";
    }
    
    static {
        INSTANCE = new BlankSpan();
    }
}
