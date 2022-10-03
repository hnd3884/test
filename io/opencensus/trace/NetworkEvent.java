package io.opencensus.trace;

import javax.annotation.Nullable;
import io.opencensus.common.Timestamp;
import io.opencensus.internal.Utils;
import javax.annotation.concurrent.Immutable;

@Deprecated
@Immutable
public abstract class NetworkEvent extends BaseMessageEvent
{
    public static Builder builder(final Type type, final long messageId) {
        return new AutoValue_NetworkEvent.Builder().setType(Utils.checkNotNull(type, "type")).setMessageId(messageId).setUncompressedMessageSize(0L).setCompressedMessageSize(0L);
    }
    
    @Nullable
    public abstract Timestamp getKernelTimestamp();
    
    public abstract Type getType();
    
    public abstract long getMessageId();
    
    public abstract long getUncompressedMessageSize();
    
    public abstract long getCompressedMessageSize();
    
    @Deprecated
    public long getMessageSize() {
        return this.getUncompressedMessageSize();
    }
    
    NetworkEvent() {
    }
    
    public enum Type
    {
        SENT, 
        RECV;
    }
    
    @Deprecated
    public abstract static class Builder
    {
        abstract Builder setType(final Type p0);
        
        abstract Builder setMessageId(final long p0);
        
        public abstract Builder setKernelTimestamp(@Nullable final Timestamp p0);
        
        @Deprecated
        public Builder setMessageSize(final long messageSize) {
            return this.setUncompressedMessageSize(messageSize);
        }
        
        public abstract Builder setUncompressedMessageSize(final long p0);
        
        public abstract Builder setCompressedMessageSize(final long p0);
        
        public abstract NetworkEvent build();
        
        Builder() {
        }
    }
}
