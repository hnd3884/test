package io.opencensus.trace;

import javax.annotation.Nullable;
import io.opencensus.common.Timestamp;
import javax.annotation.concurrent.Immutable;

@Deprecated
@Immutable
final class AutoValue_NetworkEvent extends NetworkEvent
{
    private final Timestamp kernelTimestamp;
    private final Type type;
    private final long messageId;
    private final long uncompressedMessageSize;
    private final long compressedMessageSize;
    
    private AutoValue_NetworkEvent(@Nullable final Timestamp kernelTimestamp, final Type type, final long messageId, final long uncompressedMessageSize, final long compressedMessageSize) {
        this.kernelTimestamp = kernelTimestamp;
        this.type = type;
        this.messageId = messageId;
        this.uncompressedMessageSize = uncompressedMessageSize;
        this.compressedMessageSize = compressedMessageSize;
    }
    
    @Nullable
    @Override
    public Timestamp getKernelTimestamp() {
        return this.kernelTimestamp;
    }
    
    @Override
    public Type getType() {
        return this.type;
    }
    
    @Override
    public long getMessageId() {
        return this.messageId;
    }
    
    @Override
    public long getUncompressedMessageSize() {
        return this.uncompressedMessageSize;
    }
    
    @Override
    public long getCompressedMessageSize() {
        return this.compressedMessageSize;
    }
    
    @Override
    public String toString() {
        return "NetworkEvent{kernelTimestamp=" + this.kernelTimestamp + ", type=" + this.type + ", messageId=" + this.messageId + ", uncompressedMessageSize=" + this.uncompressedMessageSize + ", compressedMessageSize=" + this.compressedMessageSize + "}";
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof NetworkEvent) {
            final NetworkEvent that = (NetworkEvent)o;
            if (this.kernelTimestamp == null) {
                if (that.getKernelTimestamp() != null) {
                    return false;
                }
            }
            else if (!this.kernelTimestamp.equals(that.getKernelTimestamp())) {
                return false;
            }
            if (this.type.equals(that.getType()) && this.messageId == that.getMessageId() && this.uncompressedMessageSize == that.getUncompressedMessageSize() && this.compressedMessageSize == that.getCompressedMessageSize()) {
                return true;
            }
            return false;
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        int h = 1;
        h *= 1000003;
        h ^= ((this.kernelTimestamp == null) ? 0 : this.kernelTimestamp.hashCode());
        h *= 1000003;
        h ^= this.type.hashCode();
        h *= 1000003;
        h = (int)((long)h ^ (this.messageId >>> 32 ^ this.messageId));
        h *= 1000003;
        h = (int)((long)h ^ (this.uncompressedMessageSize >>> 32 ^ this.uncompressedMessageSize));
        h *= 1000003;
        h = (int)((long)h ^ (this.compressedMessageSize >>> 32 ^ this.compressedMessageSize));
        return h;
    }
    
    static final class Builder extends NetworkEvent.Builder
    {
        private Timestamp kernelTimestamp;
        private Type type;
        private Long messageId;
        private Long uncompressedMessageSize;
        private Long compressedMessageSize;
        
        @Override
        public NetworkEvent.Builder setKernelTimestamp(@Nullable final Timestamp kernelTimestamp) {
            this.kernelTimestamp = kernelTimestamp;
            return this;
        }
        
        @Override
        NetworkEvent.Builder setType(final Type type) {
            if (type == null) {
                throw new NullPointerException("Null type");
            }
            this.type = type;
            return this;
        }
        
        @Override
        NetworkEvent.Builder setMessageId(final long messageId) {
            this.messageId = messageId;
            return this;
        }
        
        @Override
        public NetworkEvent.Builder setUncompressedMessageSize(final long uncompressedMessageSize) {
            this.uncompressedMessageSize = uncompressedMessageSize;
            return this;
        }
        
        @Override
        public NetworkEvent.Builder setCompressedMessageSize(final long compressedMessageSize) {
            this.compressedMessageSize = compressedMessageSize;
            return this;
        }
        
        @Override
        public NetworkEvent build() {
            String missing = "";
            if (this.type == null) {
                missing += " type";
            }
            if (this.messageId == null) {
                missing += " messageId";
            }
            if (this.uncompressedMessageSize == null) {
                missing += " uncompressedMessageSize";
            }
            if (this.compressedMessageSize == null) {
                missing += " compressedMessageSize";
            }
            if (!missing.isEmpty()) {
                throw new IllegalStateException("Missing required properties:" + missing);
            }
            return new AutoValue_NetworkEvent(this.kernelTimestamp, this.type, this.messageId, this.uncompressedMessageSize, this.compressedMessageSize, null);
        }
    }
}
