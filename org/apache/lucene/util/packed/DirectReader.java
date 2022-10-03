package org.apache.lucene.util.packed;

import java.io.IOException;
import org.apache.lucene.util.LongValues;
import org.apache.lucene.store.RandomAccessInput;

public class DirectReader
{
    public static LongValues getInstance(final RandomAccessInput slice, final int bitsPerValue) {
        return getInstance(slice, bitsPerValue, 0L);
    }
    
    public static LongValues getInstance(final RandomAccessInput slice, final int bitsPerValue, final long offset) {
        switch (bitsPerValue) {
            case 1: {
                return new DirectPackedReader1(slice, offset);
            }
            case 2: {
                return new DirectPackedReader2(slice, offset);
            }
            case 4: {
                return new DirectPackedReader4(slice, offset);
            }
            case 8: {
                return new DirectPackedReader8(slice, offset);
            }
            case 12: {
                return new DirectPackedReader12(slice, offset);
            }
            case 16: {
                return new DirectPackedReader16(slice, offset);
            }
            case 20: {
                return new DirectPackedReader20(slice, offset);
            }
            case 24: {
                return new DirectPackedReader24(slice, offset);
            }
            case 28: {
                return new DirectPackedReader28(slice, offset);
            }
            case 32: {
                return new DirectPackedReader32(slice, offset);
            }
            case 40: {
                return new DirectPackedReader40(slice, offset);
            }
            case 48: {
                return new DirectPackedReader48(slice, offset);
            }
            case 56: {
                return new DirectPackedReader56(slice, offset);
            }
            case 64: {
                return new DirectPackedReader64(slice, offset);
            }
            default: {
                throw new IllegalArgumentException("unsupported bitsPerValue: " + bitsPerValue);
            }
        }
    }
    
    static final class DirectPackedReader1 extends LongValues
    {
        final RandomAccessInput in;
        final long offset;
        
        DirectPackedReader1(final RandomAccessInput in, final long offset) {
            this.in = in;
            this.offset = offset;
        }
        
        @Override
        public long get(final long index) {
            try {
                final int shift = 7 - (int)(index & 0x7L);
                return this.in.readByte(this.offset + (index >>> 3)) >>> shift & 0x1;
            }
            catch (final IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    
    static final class DirectPackedReader2 extends LongValues
    {
        final RandomAccessInput in;
        final long offset;
        
        DirectPackedReader2(final RandomAccessInput in, final long offset) {
            this.in = in;
            this.offset = offset;
        }
        
        @Override
        public long get(final long index) {
            try {
                final int shift = 3 - (int)(index & 0x3L) << 1;
                return this.in.readByte(this.offset + (index >>> 2)) >>> shift & 0x3;
            }
            catch (final IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    
    static final class DirectPackedReader4 extends LongValues
    {
        final RandomAccessInput in;
        final long offset;
        
        DirectPackedReader4(final RandomAccessInput in, final long offset) {
            this.in = in;
            this.offset = offset;
        }
        
        @Override
        public long get(final long index) {
            try {
                final int shift = (int)(index + 1L & 0x1L) << 2;
                return this.in.readByte(this.offset + (index >>> 1)) >>> shift & 0xF;
            }
            catch (final IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    
    static final class DirectPackedReader8 extends LongValues
    {
        final RandomAccessInput in;
        final long offset;
        
        DirectPackedReader8(final RandomAccessInput in, final long offset) {
            this.in = in;
            this.offset = offset;
        }
        
        @Override
        public long get(final long index) {
            try {
                return this.in.readByte(this.offset + index) & 0xFF;
            }
            catch (final IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    
    static final class DirectPackedReader12 extends LongValues
    {
        final RandomAccessInput in;
        final long offset;
        
        DirectPackedReader12(final RandomAccessInput in, final long offset) {
            this.in = in;
            this.offset = offset;
        }
        
        @Override
        public long get(final long index) {
            try {
                final long offset = index * 12L >>> 3;
                final int shift = (int)(index + 1L & 0x1L) << 2;
                return this.in.readShort(this.offset + offset) >>> shift & 0xFFF;
            }
            catch (final IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    
    static final class DirectPackedReader16 extends LongValues
    {
        final RandomAccessInput in;
        final long offset;
        
        DirectPackedReader16(final RandomAccessInput in, final long offset) {
            this.in = in;
            this.offset = offset;
        }
        
        @Override
        public long get(final long index) {
            try {
                return this.in.readShort(this.offset + (index << 1)) & 0xFFFF;
            }
            catch (final IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    
    static final class DirectPackedReader20 extends LongValues
    {
        final RandomAccessInput in;
        final long offset;
        
        DirectPackedReader20(final RandomAccessInput in, final long offset) {
            this.in = in;
            this.offset = offset;
        }
        
        @Override
        public long get(final long index) {
            try {
                final long offset = index * 20L >>> 3;
                final int v = this.in.readInt(this.offset + offset) >>> 8;
                final int shift = (int)(index + 1L & 0x1L) << 2;
                return v >>> shift & 0xFFFFF;
            }
            catch (final IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    
    static final class DirectPackedReader24 extends LongValues
    {
        final RandomAccessInput in;
        final long offset;
        
        DirectPackedReader24(final RandomAccessInput in, final long offset) {
            this.in = in;
            this.offset = offset;
        }
        
        @Override
        public long get(final long index) {
            try {
                return this.in.readInt(this.offset + index * 3L) >>> 8;
            }
            catch (final IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    
    static final class DirectPackedReader28 extends LongValues
    {
        final RandomAccessInput in;
        final long offset;
        
        DirectPackedReader28(final RandomAccessInput in, final long offset) {
            this.in = in;
            this.offset = offset;
        }
        
        @Override
        public long get(final long index) {
            try {
                final long offset = index * 28L >>> 3;
                final int shift = (int)(index + 1L & 0x1L) << 2;
                return (long)(this.in.readInt(this.offset + offset) >>> shift) & 0xFFFFFFFL;
            }
            catch (final IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    
    static final class DirectPackedReader32 extends LongValues
    {
        final RandomAccessInput in;
        final long offset;
        
        DirectPackedReader32(final RandomAccessInput in, final long offset) {
            this.in = in;
            this.offset = offset;
        }
        
        @Override
        public long get(final long index) {
            try {
                return (long)this.in.readInt(this.offset + (index << 2)) & 0xFFFFFFFFL;
            }
            catch (final IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    
    static final class DirectPackedReader40 extends LongValues
    {
        final RandomAccessInput in;
        final long offset;
        
        DirectPackedReader40(final RandomAccessInput in, final long offset) {
            this.in = in;
            this.offset = offset;
        }
        
        @Override
        public long get(final long index) {
            try {
                return this.in.readLong(this.offset + index * 5L) >>> 24;
            }
            catch (final IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    
    static final class DirectPackedReader48 extends LongValues
    {
        final RandomAccessInput in;
        final long offset;
        
        DirectPackedReader48(final RandomAccessInput in, final long offset) {
            this.in = in;
            this.offset = offset;
        }
        
        @Override
        public long get(final long index) {
            try {
                return this.in.readLong(this.offset + index * 6L) >>> 16;
            }
            catch (final IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    
    static final class DirectPackedReader56 extends LongValues
    {
        final RandomAccessInput in;
        final long offset;
        
        DirectPackedReader56(final RandomAccessInput in, final long offset) {
            this.in = in;
            this.offset = offset;
        }
        
        @Override
        public long get(final long index) {
            try {
                return this.in.readLong(this.offset + index * 7L) >>> 8;
            }
            catch (final IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    
    static final class DirectPackedReader64 extends LongValues
    {
        final RandomAccessInput in;
        final long offset;
        
        DirectPackedReader64(final RandomAccessInput in, final long offset) {
            this.in = in;
            this.offset = offset;
        }
        
        @Override
        public long get(final long index) {
            try {
                return this.in.readLong(this.offset + (index << 3));
            }
            catch (final IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
