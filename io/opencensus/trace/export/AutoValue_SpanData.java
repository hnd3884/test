package io.opencensus.trace.export;

import javax.annotation.Nullable;
import io.opencensus.trace.Status;
import io.opencensus.trace.MessageEvent;
import io.opencensus.trace.Annotation;
import io.opencensus.common.Timestamp;
import io.opencensus.trace.Span;
import io.opencensus.trace.SpanId;
import io.opencensus.trace.SpanContext;

final class AutoValue_SpanData extends SpanData
{
    private final SpanContext context;
    private final SpanId parentSpanId;
    private final Boolean hasRemoteParent;
    private final String name;
    private final Span.Kind kind;
    private final Timestamp startTimestamp;
    private final Attributes attributes;
    private final TimedEvents<Annotation> annotations;
    private final TimedEvents<MessageEvent> messageEvents;
    private final Links links;
    private final Integer childSpanCount;
    private final Status status;
    private final Timestamp endTimestamp;
    
    AutoValue_SpanData(final SpanContext context, @Nullable final SpanId parentSpanId, @Nullable final Boolean hasRemoteParent, final String name, @Nullable final Span.Kind kind, final Timestamp startTimestamp, final Attributes attributes, final TimedEvents<Annotation> annotations, final TimedEvents<MessageEvent> messageEvents, final Links links, @Nullable final Integer childSpanCount, @Nullable final Status status, @Nullable final Timestamp endTimestamp) {
        if (context == null) {
            throw new NullPointerException("Null context");
        }
        this.context = context;
        this.parentSpanId = parentSpanId;
        this.hasRemoteParent = hasRemoteParent;
        if (name == null) {
            throw new NullPointerException("Null name");
        }
        this.name = name;
        this.kind = kind;
        if (startTimestamp == null) {
            throw new NullPointerException("Null startTimestamp");
        }
        this.startTimestamp = startTimestamp;
        if (attributes == null) {
            throw new NullPointerException("Null attributes");
        }
        this.attributes = attributes;
        if (annotations == null) {
            throw new NullPointerException("Null annotations");
        }
        this.annotations = annotations;
        if (messageEvents == null) {
            throw new NullPointerException("Null messageEvents");
        }
        this.messageEvents = messageEvents;
        if (links == null) {
            throw new NullPointerException("Null links");
        }
        this.links = links;
        this.childSpanCount = childSpanCount;
        this.status = status;
        this.endTimestamp = endTimestamp;
    }
    
    @Override
    public SpanContext getContext() {
        return this.context;
    }
    
    @Nullable
    @Override
    public SpanId getParentSpanId() {
        return this.parentSpanId;
    }
    
    @Nullable
    @Override
    public Boolean getHasRemoteParent() {
        return this.hasRemoteParent;
    }
    
    @Override
    public String getName() {
        return this.name;
    }
    
    @Nullable
    @Override
    public Span.Kind getKind() {
        return this.kind;
    }
    
    @Override
    public Timestamp getStartTimestamp() {
        return this.startTimestamp;
    }
    
    @Override
    public Attributes getAttributes() {
        return this.attributes;
    }
    
    @Override
    public TimedEvents<Annotation> getAnnotations() {
        return this.annotations;
    }
    
    @Override
    public TimedEvents<MessageEvent> getMessageEvents() {
        return this.messageEvents;
    }
    
    @Override
    public Links getLinks() {
        return this.links;
    }
    
    @Nullable
    @Override
    public Integer getChildSpanCount() {
        return this.childSpanCount;
    }
    
    @Nullable
    @Override
    public Status getStatus() {
        return this.status;
    }
    
    @Nullable
    @Override
    public Timestamp getEndTimestamp() {
        return this.endTimestamp;
    }
    
    @Override
    public String toString() {
        return "SpanData{context=" + this.context + ", parentSpanId=" + this.parentSpanId + ", hasRemoteParent=" + this.hasRemoteParent + ", name=" + this.name + ", kind=" + this.kind + ", startTimestamp=" + this.startTimestamp + ", attributes=" + this.attributes + ", annotations=" + this.annotations + ", messageEvents=" + this.messageEvents + ", links=" + this.links + ", childSpanCount=" + this.childSpanCount + ", status=" + this.status + ", endTimestamp=" + this.endTimestamp + "}";
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof SpanData) {
            final SpanData that = (SpanData)o;
            if (this.context.equals(that.getContext())) {
                if (this.parentSpanId == null) {
                    if (that.getParentSpanId() != null) {
                        return false;
                    }
                }
                else if (!this.parentSpanId.equals(that.getParentSpanId())) {
                    return false;
                }
                if (this.hasRemoteParent == null) {
                    if (that.getHasRemoteParent() != null) {
                        return false;
                    }
                }
                else if (!this.hasRemoteParent.equals(that.getHasRemoteParent())) {
                    return false;
                }
                if (this.name.equals(that.getName())) {
                    if (this.kind == null) {
                        if (that.getKind() != null) {
                            return false;
                        }
                    }
                    else if (!this.kind.equals(that.getKind())) {
                        return false;
                    }
                    if (this.startTimestamp.equals(that.getStartTimestamp()) && this.attributes.equals(that.getAttributes()) && this.annotations.equals(that.getAnnotations()) && this.messageEvents.equals(that.getMessageEvents()) && this.links.equals(that.getLinks())) {
                        if (this.childSpanCount == null) {
                            if (that.getChildSpanCount() != null) {
                                return false;
                            }
                        }
                        else if (!this.childSpanCount.equals(that.getChildSpanCount())) {
                            return false;
                        }
                        if (this.status == null) {
                            if (that.getStatus() != null) {
                                return false;
                            }
                        }
                        else if (!this.status.equals(that.getStatus())) {
                            return false;
                        }
                        if ((this.endTimestamp != null) ? this.endTimestamp.equals(that.getEndTimestamp()) : (that.getEndTimestamp() == null)) {
                            return true;
                        }
                    }
                }
            }
            return false;
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        int h = 1;
        h *= 1000003;
        h ^= this.context.hashCode();
        h *= 1000003;
        h ^= ((this.parentSpanId == null) ? 0 : this.parentSpanId.hashCode());
        h *= 1000003;
        h ^= ((this.hasRemoteParent == null) ? 0 : this.hasRemoteParent.hashCode());
        h *= 1000003;
        h ^= this.name.hashCode();
        h *= 1000003;
        h ^= ((this.kind == null) ? 0 : this.kind.hashCode());
        h *= 1000003;
        h ^= this.startTimestamp.hashCode();
        h *= 1000003;
        h ^= this.attributes.hashCode();
        h *= 1000003;
        h ^= this.annotations.hashCode();
        h *= 1000003;
        h ^= this.messageEvents.hashCode();
        h *= 1000003;
        h ^= this.links.hashCode();
        h *= 1000003;
        h ^= ((this.childSpanCount == null) ? 0 : this.childSpanCount.hashCode());
        h *= 1000003;
        h ^= ((this.status == null) ? 0 : this.status.hashCode());
        h *= 1000003;
        h ^= ((this.endTimestamp == null) ? 0 : this.endTimestamp.hashCode());
        return h;
    }
}
