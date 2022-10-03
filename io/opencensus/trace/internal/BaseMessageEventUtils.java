package io.opencensus.trace.internal;

import io.opencensus.trace.NetworkEvent;
import io.opencensus.internal.Utils;
import io.opencensus.trace.MessageEvent;
import io.opencensus.trace.BaseMessageEvent;

public final class BaseMessageEventUtils
{
    public static MessageEvent asMessageEvent(final BaseMessageEvent event) {
        Utils.checkNotNull(event, "event");
        if (event instanceof MessageEvent) {
            return (MessageEvent)event;
        }
        final NetworkEvent networkEvent = (NetworkEvent)event;
        final MessageEvent.Type type = (networkEvent.getType() == NetworkEvent.Type.RECV) ? MessageEvent.Type.RECEIVED : MessageEvent.Type.SENT;
        return MessageEvent.builder(type, networkEvent.getMessageId()).setUncompressedMessageSize(networkEvent.getUncompressedMessageSize()).setCompressedMessageSize(networkEvent.getCompressedMessageSize()).build();
    }
    
    public static NetworkEvent asNetworkEvent(final BaseMessageEvent event) {
        Utils.checkNotNull(event, "event");
        if (event instanceof NetworkEvent) {
            return (NetworkEvent)event;
        }
        final MessageEvent messageEvent = (MessageEvent)event;
        final NetworkEvent.Type type = (messageEvent.getType() == MessageEvent.Type.RECEIVED) ? NetworkEvent.Type.RECV : NetworkEvent.Type.SENT;
        return NetworkEvent.builder(type, messageEvent.getMessageId()).setUncompressedMessageSize(messageEvent.getUncompressedMessageSize()).setCompressedMessageSize(messageEvent.getCompressedMessageSize()).build();
    }
    
    private BaseMessageEventUtils() {
    }
}
