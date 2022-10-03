package io.opencensus.trace;

import io.opencensus.trace.internal.BaseMessageEventUtils;
import java.util.Collections;
import io.opencensus.internal.Utils;
import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Set;
import java.util.Map;

public abstract class Span
{
    private static final Map<String, AttributeValue> EMPTY_ATTRIBUTES;
    private final SpanContext context;
    private final Set<Options> options;
    private static final Set<Options> DEFAULT_OPTIONS;
    
    protected Span(final SpanContext context, @Nullable final EnumSet<Options> options) {
        this.context = Utils.checkNotNull(context, "context");
        this.options = ((options == null) ? Span.DEFAULT_OPTIONS : Collections.unmodifiableSet((Set<? extends Options>)EnumSet.copyOf(options)));
        Utils.checkArgument(!context.getTraceOptions().isSampled() || this.options.contains(Options.RECORD_EVENTS), (Object)"Span is sampled, but does not have RECORD_EVENTS set.");
    }
    
    public void putAttribute(final String key, final AttributeValue value) {
        Utils.checkNotNull(key, "key");
        Utils.checkNotNull(value, "value");
        this.putAttributes(Collections.singletonMap(key, value));
    }
    
    public void putAttributes(final Map<String, AttributeValue> attributes) {
        Utils.checkNotNull(attributes, "attributes");
        this.addAttributes(attributes);
    }
    
    @Deprecated
    public void addAttributes(final Map<String, AttributeValue> attributes) {
        this.putAttributes(attributes);
    }
    
    public final void addAnnotation(final String description) {
        Utils.checkNotNull(description, "description");
        this.addAnnotation(description, Span.EMPTY_ATTRIBUTES);
    }
    
    public abstract void addAnnotation(final String p0, final Map<String, AttributeValue> p1);
    
    public abstract void addAnnotation(final Annotation p0);
    
    @Deprecated
    public void addNetworkEvent(final NetworkEvent networkEvent) {
        this.addMessageEvent(BaseMessageEventUtils.asMessageEvent(networkEvent));
    }
    
    public void addMessageEvent(final MessageEvent messageEvent) {
        Utils.checkNotNull(messageEvent, "messageEvent");
        this.addNetworkEvent(BaseMessageEventUtils.asNetworkEvent(messageEvent));
    }
    
    public abstract void addLink(final Link p0);
    
    public void setStatus(final Status status) {
        Utils.checkNotNull(status, "status");
    }
    
    public abstract void end(final EndSpanOptions p0);
    
    public final void end() {
        this.end(EndSpanOptions.DEFAULT);
    }
    
    public final SpanContext getContext() {
        return this.context;
    }
    
    public final Set<Options> getOptions() {
        return this.options;
    }
    
    static {
        EMPTY_ATTRIBUTES = Collections.emptyMap();
        DEFAULT_OPTIONS = Collections.unmodifiableSet((Set<? extends Options>)EnumSet.noneOf(Options.class));
    }
    
    public enum Options
    {
        RECORD_EVENTS;
    }
    
    public enum Kind
    {
        SERVER, 
        CLIENT;
    }
}
