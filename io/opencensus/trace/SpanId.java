package io.opencensus.trace;

import javax.annotation.Nullable;
import java.util.Random;
import io.opencensus.internal.Utils;
import javax.annotation.concurrent.Immutable;

@Immutable
public final class SpanId implements Comparable<SpanId>
{
    public static final int SIZE = 8;
    public static final SpanId INVALID;
    private static final int BASE16_SIZE = 16;
    private static final long INVALID_ID = 0L;
    private final long id;
    
    private SpanId(final long id) {
        this.id = id;
    }
    
    public static SpanId fromBytes(final byte[] src) {
        Utils.checkNotNull(src, "src");
        Utils.checkArgument(src.length == 8, "Invalid size: expected %s, got %s", 8, src.length);
        return fromBytes(src, 0);
    }
    
    public static SpanId fromBytes(final byte[] src, final int srcOffset) {
        Utils.checkNotNull(src, "src");
        return new SpanId(BigendianEncoding.longFromByteArray(src, srcOffset));
    }
    
    public static SpanId fromLowerBase16(final CharSequence src) {
        Utils.checkNotNull(src, "src");
        Utils.checkArgument(src.length() == 16, "Invalid size: expected %s, got %s", 16, src.length());
        return fromLowerBase16(src, 0);
    }
    
    public static SpanId fromLowerBase16(final CharSequence src, final int srcOffset) {
        Utils.checkNotNull(src, "src");
        return new SpanId(BigendianEncoding.longFromBase16String(src, srcOffset));
    }
    
    public static SpanId generateRandomId(final Random random) {
        long id;
        do {
            id = random.nextLong();
        } while (id == 0L);
        return new SpanId(id);
    }
    
    public byte[] getBytes() {
        final byte[] bytes = new byte[8];
        BigendianEncoding.longToByteArray(this.id, bytes, 0);
        return bytes;
    }
    
    public void copyBytesTo(final byte[] dest, final int destOffset) {
        BigendianEncoding.longToByteArray(this.id, dest, destOffset);
    }
    
    public void copyLowerBase16To(final char[] dest, final int destOffset) {
        BigendianEncoding.longToBase16String(this.id, dest, destOffset);
    }
    
    public boolean isValid() {
        return this.id != 0L;
    }
    
    public String toLowerBase16() {
        final char[] chars = new char[16];
        this.copyLowerBase16To(chars, 0);
        return new String(chars);
    }
    
    @Override
    public boolean equals(@Nullable final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof SpanId)) {
            return false;
        }
        final SpanId that = (SpanId)obj;
        return this.id == that.id;
    }
    
    @Override
    public int hashCode() {
        return (int)(this.id ^ this.id >>> 32);
    }
    
    @Override
    public String toString() {
        return "SpanId{spanId=" + this.toLowerBase16() + "}";
    }
    
    @Override
    public int compareTo(final SpanId that) {
        return (this.id < that.id) ? -1 : ((this.id == that.id) ? 0 : 1);
    }
    
    static {
        INVALID = new SpanId(0L);
    }
}
