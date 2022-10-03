package org.apache.lucene.util.packed;

import java.io.IOException;
import org.apache.lucene.store.IndexInput;

class DirectPackedReader extends PackedInts.ReaderImpl
{
    final IndexInput in;
    final int bitsPerValue;
    final long startPointer;
    final long valueMask;
    
    DirectPackedReader(final int bitsPerValue, final int valueCount, final IndexInput in) {
        super(valueCount);
        this.in = in;
        this.bitsPerValue = bitsPerValue;
        this.startPointer = in.getFilePointer();
        if (bitsPerValue == 64) {
            this.valueMask = -1L;
        }
        else {
            this.valueMask = (1L << bitsPerValue) - 1L;
        }
    }
    
    @Override
    public long get(final int index) {
        final long majorBitPos = index * (long)this.bitsPerValue;
        final long elementPos = majorBitPos >>> 3;
        try {
            this.in.seek(this.startPointer + elementPos);
            final int bitPos = (int)(majorBitPos & 0x7L);
            final int roundedBits = bitPos + this.bitsPerValue + 7 & 0xFFFFFFF8;
            int shiftRightBits = roundedBits - bitPos - this.bitsPerValue;
            long rawValue = 0L;
            switch (roundedBits >>> 3) {
                case 1: {
                    rawValue = this.in.readByte();
                    break;
                }
                case 2: {
                    rawValue = this.in.readShort();
                    break;
                }
                case 3: {
                    rawValue = ((long)this.in.readShort() << 8 | ((long)this.in.readByte() & 0xFFL));
                    break;
                }
                case 4: {
                    rawValue = this.in.readInt();
                    break;
                }
                case 5: {
                    rawValue = ((long)this.in.readInt() << 8 | ((long)this.in.readByte() & 0xFFL));
                    break;
                }
                case 6: {
                    rawValue = ((long)this.in.readInt() << 16 | ((long)this.in.readShort() & 0xFFFFL));
                    break;
                }
                case 7: {
                    rawValue = ((long)this.in.readInt() << 24 | ((long)this.in.readShort() & 0xFFFFL) << 8 | ((long)this.in.readByte() & 0xFFL));
                    break;
                }
                case 8: {
                    rawValue = this.in.readLong();
                    break;
                }
                case 9: {
                    rawValue = (this.in.readLong() << 8 - shiftRightBits | ((long)this.in.readByte() & 0xFFL) >>> shiftRightBits);
                    shiftRightBits = 0;
                    break;
                }
                default: {
                    throw new AssertionError((Object)("bitsPerValue too large: " + this.bitsPerValue));
                }
            }
            return rawValue >>> shiftRightBits & this.valueMask;
        }
        catch (final IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }
    
    @Override
    public long ramBytesUsed() {
        return 0L;
    }
}
