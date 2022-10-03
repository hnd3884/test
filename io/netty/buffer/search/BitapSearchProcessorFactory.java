package io.netty.buffer.search;

import io.netty.util.internal.PlatformDependent;

public class BitapSearchProcessorFactory extends AbstractSearchProcessorFactory
{
    private final long[] bitMasks;
    private final long successBit;
    
    BitapSearchProcessorFactory(final byte[] needle) {
        this.bitMasks = new long[256];
        if (needle.length > 64) {
            throw new IllegalArgumentException("Maximum supported search pattern length is 64, got " + needle.length);
        }
        long bit = 1L;
        for (final byte c : needle) {
            final long[] bitMasks = this.bitMasks;
            final int n = c & 0xFF;
            bitMasks[n] |= bit;
            bit <<= 1;
        }
        this.successBit = 1L << needle.length - 1;
    }
    
    @Override
    public Processor newSearchProcessor() {
        return new Processor(this.bitMasks, this.successBit);
    }
    
    public static class Processor implements SearchProcessor
    {
        private final long[] bitMasks;
        private final long successBit;
        private long currentMask;
        
        Processor(final long[] bitMasks, final long successBit) {
            this.bitMasks = bitMasks;
            this.successBit = successBit;
        }
        
        @Override
        public boolean process(final byte value) {
            this.currentMask = ((this.currentMask << 1 | 0x1L) & PlatformDependent.getLong(this.bitMasks, (long)value & 0xFFL));
            return (this.currentMask & this.successBit) == 0x0L;
        }
        
        @Override
        public void reset() {
            this.currentMask = 0L;
        }
    }
}
