package io.netty.handler.codec.compression;

import net.jpountz.xxhash.XXHashFactory;
import io.netty.buffer.ByteBuf;
import net.jpountz.xxhash.XXHash32;

public final class Lz4XXHash32 extends ByteBufChecksum
{
    private static final XXHash32 XXHASH32;
    private final int seed;
    private boolean used;
    private int value;
    
    public Lz4XXHash32(final int seed) {
        this.seed = seed;
    }
    
    @Override
    public void update(final int b) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void update(final byte[] b, final int off, final int len) {
        if (this.used) {
            throw new IllegalStateException();
        }
        this.value = Lz4XXHash32.XXHASH32.hash(b, off, len, this.seed);
        this.used = true;
    }
    
    @Override
    public void update(final ByteBuf b, final int off, final int len) {
        if (this.used) {
            throw new IllegalStateException();
        }
        if (b.hasArray()) {
            this.value = Lz4XXHash32.XXHASH32.hash(b.array(), b.arrayOffset() + off, len, this.seed);
        }
        else {
            this.value = Lz4XXHash32.XXHASH32.hash(CompressionUtil.safeNioBuffer(b, off, len), this.seed);
        }
        this.used = true;
    }
    
    @Override
    public long getValue() {
        if (!this.used) {
            throw new IllegalStateException();
        }
        return (long)this.value & 0xFFFFFFFL;
    }
    
    @Override
    public void reset() {
        this.used = false;
    }
    
    static {
        XXHASH32 = XXHashFactory.fastestInstance().hash32();
    }
}
