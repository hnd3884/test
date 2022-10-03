package io.opencensus.trace;

import javax.annotation.Nullable;
import java.util.Random;
import io.opencensus.internal.Utils;
import javax.annotation.concurrent.Immutable;

@Immutable
public final class TraceId implements Comparable<TraceId>
{
    public static final int SIZE = 16;
    private static final int BASE16_SIZE = 32;
    private static final long INVALID_ID = 0L;
    public static final TraceId INVALID;
    private final long idHi;
    private final long idLo;
    
    private TraceId(final long idHi, final long idLo) {
        this.idHi = idHi;
        this.idLo = idLo;
    }
    
    public static TraceId fromBytes(final byte[] src) {
        Utils.checkNotNull(src, "src");
        Utils.checkArgument(src.length == 16, "Invalid size: expected %s, got %s", 16, src.length);
        return fromBytes(src, 0);
    }
    
    public static TraceId fromBytes(final byte[] src, final int srcOffset) {
        Utils.checkNotNull(src, "src");
        return new TraceId(BigendianEncoding.longFromByteArray(src, srcOffset), BigendianEncoding.longFromByteArray(src, srcOffset + 8));
    }
    
    public static TraceId fromLowerBase16(final CharSequence src) {
        Utils.checkNotNull(src, "src");
        Utils.checkArgument(src.length() == 32, "Invalid size: expected %s, got %s", 32, src.length());
        return fromLowerBase16(src, 0);
    }
    
    public static TraceId fromLowerBase16(final CharSequence src, final int srcOffset) {
        Utils.checkNotNull(src, "src");
        return new TraceId(BigendianEncoding.longFromBase16String(src, srcOffset), BigendianEncoding.longFromBase16String(src, srcOffset + 16));
    }
    
    public static TraceId generateRandomId(final Random random) {
        long idHi;
        long idLo;
        do {
            idHi = random.nextLong();
            idLo = random.nextLong();
        } while (idHi == 0L && idLo == 0L);
        return new TraceId(idHi, idLo);
    }
    
    public byte[] getBytes() {
        final byte[] bytes = new byte[16];
        BigendianEncoding.longToByteArray(this.idHi, bytes, 0);
        BigendianEncoding.longToByteArray(this.idLo, bytes, 8);
        return bytes;
    }
    
    public void copyBytesTo(final byte[] dest, final int destOffset) {
        BigendianEncoding.longToByteArray(this.idHi, dest, destOffset);
        BigendianEncoding.longToByteArray(this.idLo, dest, destOffset + 8);
    }
    
    public void copyLowerBase16To(final char[] dest, final int destOffset) {
        BigendianEncoding.longToBase16String(this.idHi, dest, destOffset);
        BigendianEncoding.longToBase16String(this.idLo, dest, destOffset + 16);
    }
    
    public boolean isValid() {
        return this.idHi != 0L || this.idLo != 0L;
    }
    
    public String toLowerBase16() {
        final char[] chars = new char[32];
        this.copyLowerBase16To(chars, 0);
        return new String(chars);
    }
    
    public long getLowerLong() {
        return (this.idHi < 0L) ? (-this.idHi) : this.idHi;
    }
    
    @Override
    public boolean equals(@Nullable final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof TraceId)) {
            return false;
        }
        final TraceId that = (TraceId)obj;
        return this.idHi == that.idHi && this.idLo == that.idLo;
    }
    
    @Override
    public int hashCode() {
        int result = 1;
        result = 31 * result + (int)(this.idHi ^ this.idHi >>> 32);
        result = 31 * result + (int)(this.idLo ^ this.idLo >>> 32);
        return result;
    }
    
    @Override
    public String toString() {
        return "TraceId{traceId=" + this.toLowerBase16() + "}";
    }
    
    @Override
    public int compareTo(final TraceId that) {
        if (this.idHi != that.idHi) {
            return (this.idHi < that.idHi) ? -1 : 1;
        }
        if (this.idLo == that.idLo) {
            return 0;
        }
        return (this.idLo < that.idLo) ? -1 : 1;
    }
    
    static {
        INVALID = new TraceId(0L, 0L);
    }
}
