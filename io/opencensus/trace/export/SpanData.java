package io.opencensus.trace.export;

import io.opencensus.trace.Link;
import java.util.HashMap;
import io.opencensus.trace.AttributeValue;
import java.util.Map;
import java.util.Collections;
import java.util.Collection;
import io.opencensus.trace.NetworkEvent;
import java.util.Iterator;
import java.util.List;
import io.opencensus.trace.internal.BaseMessageEventUtils;
import io.opencensus.trace.MessageEvent;
import java.util.ArrayList;
import io.opencensus.internal.Utils;
import io.opencensus.trace.Span;
import io.opencensus.trace.Status;
import io.opencensus.trace.BaseMessageEvent;
import io.opencensus.trace.Annotation;
import io.opencensus.common.Timestamp;
import javax.annotation.Nullable;
import io.opencensus.trace.SpanId;
import io.opencensus.trace.SpanContext;
import javax.annotation.concurrent.Immutable;

@Immutable
public abstract class SpanData
{
    @Deprecated
    public static SpanData create(final SpanContext context, @Nullable final SpanId parentSpanId, @Nullable final Boolean hasRemoteParent, final String name, final Timestamp startTimestamp, final Attributes attributes, final TimedEvents<Annotation> annotations, final TimedEvents<? extends BaseMessageEvent> messageOrNetworkEvents, final Links links, @Nullable final Integer childSpanCount, @Nullable final Status status, @Nullable final Timestamp endTimestamp) {
        return create(context, parentSpanId, hasRemoteParent, name, null, startTimestamp, attributes, annotations, messageOrNetworkEvents, links, childSpanCount, status, endTimestamp);
    }
    
    public static SpanData create(final SpanContext context, @Nullable final SpanId parentSpanId, @Nullable final Boolean hasRemoteParent, final String name, @Nullable final Span.Kind kind, final Timestamp startTimestamp, final Attributes attributes, final TimedEvents<Annotation> annotations, final TimedEvents<? extends BaseMessageEvent> messageOrNetworkEvents, final Links links, @Nullable final Integer childSpanCount, @Nullable final Status status, @Nullable final Timestamp endTimestamp) {
        Utils.checkNotNull(messageOrNetworkEvents, "messageOrNetworkEvents");
        final List<TimedEvent<MessageEvent>> messageEventsList = new ArrayList<TimedEvent<MessageEvent>>();
        for (final TimedEvent<? extends BaseMessageEvent> timedEvent : messageOrNetworkEvents.getEvents()) {
            final BaseMessageEvent event = (BaseMessageEvent)timedEvent.getEvent();
            if (event instanceof MessageEvent) {
                final TimedEvent<MessageEvent> timedMessageEvent = (TimedEvent<MessageEvent>)timedEvent;
                messageEventsList.add(timedMessageEvent);
            }
            else {
                messageEventsList.add(TimedEvent.create(timedEvent.getTimestamp(), BaseMessageEventUtils.asMessageEvent(event)));
            }
        }
        final TimedEvents<MessageEvent> messageEvents = TimedEvents.create(messageEventsList, messageOrNetworkEvents.getDroppedEventsCount());
        return new AutoValue_SpanData(context, parentSpanId, hasRemoteParent, name, kind, startTimestamp, attributes, annotations, messageEvents, links, childSpanCount, status, endTimestamp);
    }
    
    public abstract SpanContext getContext();
    
    @Nullable
    public abstract SpanId getParentSpanId();
    
    @Nullable
    public abstract Boolean getHasRemoteParent();
    
    public abstract String getName();
    
    @Nullable
    public abstract Span.Kind getKind();
    
    public abstract Timestamp getStartTimestamp();
    
    public abstract Attributes getAttributes();
    
    public abstract TimedEvents<Annotation> getAnnotations();
    
    @Deprecated
    public TimedEvents<NetworkEvent> getNetworkEvents() {
        final TimedEvents<MessageEvent> timedEvents = this.getMessageEvents();
        final List<TimedEvent<NetworkEvent>> networkEventsList = new ArrayList<TimedEvent<NetworkEvent>>();
        for (final TimedEvent<MessageEvent> timedEvent : timedEvents.getEvents()) {
            networkEventsList.add(TimedEvent.create(timedEvent.getTimestamp(), BaseMessageEventUtils.asNetworkEvent(timedEvent.getEvent())));
        }
        return TimedEvents.create(networkEventsList, timedEvents.getDroppedEventsCount());
    }
    
    public abstract TimedEvents<MessageEvent> getMessageEvents();
    
    public abstract Links getLinks();
    
    @Nullable
    public abstract Integer getChildSpanCount();
    
    @Nullable
    public abstract Status getStatus();
    
    @Nullable
    public abstract Timestamp getEndTimestamp();
    
    SpanData() {
    }
    
    @Immutable
    public abstract static class TimedEvent<T>
    {
        public static <T> TimedEvent<T> create(final Timestamp timestamp, final T event) {
            return new AutoValue_SpanData_TimedEvent<T>(timestamp, event);
        }
        
        public abstract Timestamp getTimestamp();
        
        public abstract T getEvent();
        
        TimedEvent() {
        }
    }
    
    @Immutable
    public abstract static class TimedEvents<T>
    {
        public static <T> TimedEvents<T> create(final List<TimedEvent<T>> events, final int droppedEventsCount) {
            return new AutoValue_SpanData_TimedEvents<T>(Collections.unmodifiableList((List<? extends TimedEvent<T>>)new ArrayList<TimedEvent<T>>(Utils.checkNotNull(events, "events"))), droppedEventsCount);
        }
        
        public abstract List<TimedEvent<T>> getEvents();
        
        public abstract int getDroppedEventsCount();
        
        TimedEvents() {
        }
    }
    
    @Immutable
    public abstract static class Attributes
    {
        public static Attributes create(final Map<String, AttributeValue> attributeMap, final int droppedAttributesCount) {
            return new AutoValue_SpanData_Attributes(Collections.unmodifiableMap((Map<? extends String, ? extends AttributeValue>)new HashMap<String, AttributeValue>(Utils.checkNotNull(attributeMap, "attributeMap"))), droppedAttributesCount);
        }
        
        public abstract Map<String, AttributeValue> getAttributeMap();
        
        public abstract int getDroppedAttributesCount();
        
        Attributes() {
        }
    }
    
    @Immutable
    public abstract static class Links
    {
        public static Links create(final List<Link> links, final int droppedLinksCount) {
            return new AutoValue_SpanData_Links(Collections.unmodifiableList((List<? extends Link>)new ArrayList<Link>(Utils.checkNotNull(links, "links"))), droppedLinksCount);
        }
        
        public abstract List<Link> getLinks();
        
        public abstract int getDroppedLinksCount();
        
        Links() {
        }
    }
}
