package io.opencensus.trace;

import io.opencensus.internal.Utils;
import javax.annotation.concurrent.Immutable;

@Immutable
public abstract class MessageEvent extends BaseMessageEvent
{
    public static Builder builder(final Type type, final long messageId) {
        return new AutoValue_MessageEvent.Builder().setType(Utils.checkNotNull(type, "type")).setMessageId(messageId).setUncompressedMessageSize(0L).setCompressedMessageSize(0L);
    }
    
    public abstract Type getType();
    
    public abstract long getMessageId();
    
    public abstract long getUncompressedMessageSize();
    
    public abstract long getCompressedMessageSize();
    
    MessageEvent() {
    }
    
    public enum Type
    {
        SENT, 
        RECEIVED;
    }
    
    public abstract static class Builder
    {
        abstract Builder setType(final Type p0);
        
        abstract Builder setMessageId(final long p0);
        
        public abstract Builder setUncompressedMessageSize(final long p0);
        
        public abstract Builder setCompressedMessageSize(final long p0);
        
        public abstract MessageEvent build();
        
        Builder() {
        }
    }
}
