package io.opencensus.trace;

import java.util.Arrays;
import javax.annotation.Nullable;
import io.opencensus.internal.Utils;
import javax.annotation.concurrent.Immutable;

@Immutable
public final class TraceOptions
{
    private static final byte DEFAULT_OPTIONS = 0;
    private static final byte IS_SAMPLED = 1;
    public static final int SIZE = 1;
    private static final int BASE16_SIZE = 2;
    public static final TraceOptions DEFAULT;
    private final byte options;
    
    private TraceOptions(final byte options) {
        this.options = options;
    }
    
    @Deprecated
    public static TraceOptions fromBytes(final byte[] buffer) {
        Utils.checkNotNull(buffer, "buffer");
        Utils.checkArgument(buffer.length == 1, "Invalid size: expected %s, got %s", 1, buffer.length);
        return fromByte(buffer[0]);
    }
    
    @Deprecated
    public static TraceOptions fromBytes(final byte[] src, final int srcOffset) {
        Utils.checkIndex(srcOffset, src.length);
        return fromByte(src[srcOffset]);
    }
    
    public static TraceOptions fromByte(final byte src) {
        return new TraceOptions(src);
    }
    
    public static TraceOptions fromLowerBase16(final CharSequence src, final int srcOffset) {
        return new TraceOptions(BigendianEncoding.byteFromBase16String(src, srcOffset));
    }
    
    public byte getByte() {
        return this.options;
    }
    
    @Deprecated
    public byte[] getBytes() {
        final byte[] bytes = { this.options };
        return bytes;
    }
    
    public void copyBytesTo(final byte[] dest, final int destOffset) {
        Utils.checkIndex(destOffset, dest.length);
        dest[destOffset] = this.options;
    }
    
    public void copyLowerBase16To(final char[] dest, final int destOffset) {
        BigendianEncoding.byteToBase16String(this.options, dest, destOffset);
    }
    
    public String toLowerBase16() {
        final char[] chars = new char[2];
        this.copyLowerBase16To(chars, 0);
        return new String(chars);
    }
    
    public static Builder builder() {
        return new Builder((byte)0);
    }
    
    public static Builder builder(final TraceOptions traceOptions) {
        return new Builder(traceOptions.options);
    }
    
    public boolean isSampled() {
        return this.hasOption(1);
    }
    
    @Override
    public boolean equals(@Nullable final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof TraceOptions)) {
            return false;
        }
        final TraceOptions that = (TraceOptions)obj;
        return this.options == that.options;
    }
    
    @Override
    public int hashCode() {
        return Arrays.hashCode(new byte[] { this.options });
    }
    
    @Override
    public String toString() {
        return "TraceOptions{sampled=" + this.isSampled() + "}";
    }
    
    byte getOptions() {
        return this.options;
    }
    
    private boolean hasOption(final int mask) {
        return (this.options & mask) != 0x0;
    }
    
    static {
        DEFAULT = fromByte((byte)0);
    }
    
    public static final class Builder
    {
        private byte options;
        
        private Builder(final byte options) {
            this.options = options;
        }
        
        @Deprecated
        public Builder setIsSampled() {
            return this.setIsSampled(true);
        }
        
        public Builder setIsSampled(final boolean isSampled) {
            if (isSampled) {
                this.options |= 0x1;
            }
            else {
                this.options &= 0xFFFFFFFE;
            }
            return this;
        }
        
        public TraceOptions build() {
            return TraceOptions.fromByte(this.options);
        }
    }
}
