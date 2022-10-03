package org.apache.lucene.util;

import java.io.IOException;
import org.apache.lucene.index.FilterLeafReader;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.FilteredTermsEnum;
import org.apache.lucene.index.TermsEnum;

public final class NumericUtils
{
    public static final int PRECISION_STEP_DEFAULT = 16;
    public static final int PRECISION_STEP_DEFAULT_32 = 8;
    public static final byte SHIFT_START_LONG = 32;
    public static final int BUF_SIZE_LONG = 11;
    public static final byte SHIFT_START_INT = 96;
    public static final int BUF_SIZE_INT = 6;
    
    private NumericUtils() {
    }
    
    @Deprecated
    public static void longToPrefixCodedBytes(final long val, final int shift, final BytesRefBuilder bytes) {
        longToPrefixCoded(val, shift, bytes);
    }
    
    @Deprecated
    public static void intToPrefixCodedBytes(final int val, final int shift, final BytesRefBuilder bytes) {
        intToPrefixCoded(val, shift, bytes);
    }
    
    public static void longToPrefixCoded(final long val, final int shift, final BytesRefBuilder bytes) {
        if ((shift & 0xFFFFFFC0) != 0x0) {
            throw new IllegalArgumentException("Illegal shift value, must be 0..63; got shift=" + shift);
        }
        int nChars = ((63 - shift) * 37 >> 8) + 1;
        bytes.setLength(nChars + 1);
        bytes.grow(11);
        bytes.setByteAt(0, (byte)(32 + shift));
        long sortableBits = val ^ Long.MIN_VALUE;
        sortableBits >>>= shift;
        while (nChars > 0) {
            bytes.setByteAt(nChars--, (byte)(sortableBits & 0x7FL));
            sortableBits >>>= 7;
        }
    }
    
    public static void intToPrefixCoded(final int val, final int shift, final BytesRefBuilder bytes) {
        if ((shift & 0xFFFFFFE0) != 0x0) {
            throw new IllegalArgumentException("Illegal shift value, must be 0..31; got shift=" + shift);
        }
        int nChars = ((31 - shift) * 37 >> 8) + 1;
        bytes.setLength(nChars + 1);
        bytes.grow(11);
        bytes.setByteAt(0, (byte)(96 + shift));
        int sortableBits = val ^ Integer.MIN_VALUE;
        sortableBits >>>= shift;
        while (nChars > 0) {
            bytes.setByteAt(nChars--, (byte)(sortableBits & 0x7F));
            sortableBits >>>= 7;
        }
    }
    
    public static int getPrefixCodedLongShift(final BytesRef val) {
        final int shift = val.bytes[val.offset] - 32;
        if (shift > 63 || shift < 0) {
            throw new NumberFormatException("Invalid shift value (" + shift + ") in prefixCoded bytes (is encoded value really an INT?)");
        }
        return shift;
    }
    
    public static int getPrefixCodedIntShift(final BytesRef val) {
        final int shift = val.bytes[val.offset] - 96;
        if (shift > 31 || shift < 0) {
            throw new NumberFormatException("Invalid shift value in prefixCoded bytes (is encoded value really an INT?)");
        }
        return shift;
    }
    
    public static long prefixCodedToLong(final BytesRef val) {
        long sortableBits = 0L;
        for (int i = val.offset + 1, limit = val.offset + val.length; i < limit; ++i) {
            sortableBits <<= 7;
            final byte b = val.bytes[i];
            if (b < 0) {
                throw new NumberFormatException("Invalid prefixCoded numerical value representation (byte " + Integer.toHexString(b & 0xFF) + " at position " + (i - val.offset) + " is invalid)");
            }
            sortableBits |= b;
        }
        return sortableBits << getPrefixCodedLongShift(val) ^ Long.MIN_VALUE;
    }
    
    public static int prefixCodedToInt(final BytesRef val) {
        int sortableBits = 0;
        for (int i = val.offset + 1, limit = val.offset + val.length; i < limit; ++i) {
            sortableBits <<= 7;
            final byte b = val.bytes[i];
            if (b < 0) {
                throw new NumberFormatException("Invalid prefixCoded numerical value representation (byte " + Integer.toHexString(b & 0xFF) + " at position " + (i - val.offset) + " is invalid)");
            }
            sortableBits |= b;
        }
        return sortableBits << getPrefixCodedIntShift(val) ^ Integer.MIN_VALUE;
    }
    
    public static long doubleToSortableLong(final double val) {
        return sortableDoubleBits(Double.doubleToLongBits(val));
    }
    
    public static double sortableLongToDouble(final long val) {
        return Double.longBitsToDouble(sortableDoubleBits(val));
    }
    
    public static int floatToSortableInt(final float val) {
        return sortableFloatBits(Float.floatToIntBits(val));
    }
    
    public static float sortableIntToFloat(final int val) {
        return Float.intBitsToFloat(sortableFloatBits(val));
    }
    
    public static long sortableDoubleBits(final long bits) {
        return bits ^ (bits >> 63 & Long.MAX_VALUE);
    }
    
    public static int sortableFloatBits(final int bits) {
        return bits ^ (bits >> 31 & Integer.MAX_VALUE);
    }
    
    public static void splitLongRange(final LongRangeBuilder builder, final int precisionStep, final long minBound, final long maxBound) {
        splitRange(builder, 64, precisionStep, minBound, maxBound);
    }
    
    public static void splitIntRange(final IntRangeBuilder builder, final int precisionStep, final int minBound, final int maxBound) {
        splitRange(builder, 32, precisionStep, minBound, maxBound);
    }
    
    private static void splitRange(final Object builder, final int valSize, final int precisionStep, long minBound, long maxBound) {
        if (precisionStep < 1) {
            throw new IllegalArgumentException("precisionStep must be >=1");
        }
        if (minBound > maxBound) {
            return;
        }
        int shift = 0;
        while (true) {
            final long diff = 1L << shift + precisionStep;
            final long mask = (1L << precisionStep) - 1L << shift;
            final boolean hasLower = (minBound & mask) != 0x0L;
            final boolean hasUpper = (maxBound & mask) != mask;
            final long nextMinBound = (hasLower ? (minBound + diff) : minBound) & ~mask;
            final long nextMaxBound = (hasUpper ? (maxBound - diff) : maxBound) & ~mask;
            final boolean lowerWrapped = nextMinBound < minBound;
            final boolean upperWrapped = nextMaxBound > maxBound;
            if (shift + precisionStep >= valSize || nextMinBound > nextMaxBound || lowerWrapped || upperWrapped) {
                break;
            }
            if (hasLower) {
                addRange(builder, valSize, minBound, minBound | mask, shift);
            }
            if (hasUpper) {
                addRange(builder, valSize, maxBound & ~mask, maxBound, shift);
            }
            minBound = nextMinBound;
            maxBound = nextMaxBound;
            shift += precisionStep;
        }
        addRange(builder, valSize, minBound, maxBound, shift);
    }
    
    private static void addRange(final Object builder, final int valSize, final long minBound, long maxBound, final int shift) {
        maxBound |= (1L << shift) - 1L;
        switch (valSize) {
            case 64: {
                ((LongRangeBuilder)builder).addRange(minBound, maxBound, shift);
                break;
            }
            case 32: {
                ((IntRangeBuilder)builder).addRange((int)minBound, (int)maxBound, shift);
                break;
            }
            default: {
                throw new IllegalArgumentException("valSize must be 32 or 64.");
            }
        }
    }
    
    public static TermsEnum filterPrefixCodedLongs(final TermsEnum termsEnum) {
        return new SeekingNumericFilteredTermsEnum(termsEnum) {
            @Override
            protected AcceptStatus accept(final BytesRef term) {
                return (NumericUtils.getPrefixCodedLongShift(term) == 0) ? AcceptStatus.YES : AcceptStatus.END;
            }
        };
    }
    
    public static TermsEnum filterPrefixCodedInts(final TermsEnum termsEnum) {
        return new SeekingNumericFilteredTermsEnum(termsEnum) {
            @Override
            protected AcceptStatus accept(final BytesRef term) {
                return (NumericUtils.getPrefixCodedIntShift(term) == 0) ? AcceptStatus.YES : AcceptStatus.END;
            }
        };
    }
    
    private static Terms intTerms(final Terms terms) {
        return new FilterLeafReader.FilterTerms(terms) {
            @Override
            public TermsEnum iterator() throws IOException {
                return NumericUtils.filterPrefixCodedInts(this.in.iterator());
            }
        };
    }
    
    private static Terms longTerms(final Terms terms) {
        return new FilterLeafReader.FilterTerms(terms) {
            @Override
            public TermsEnum iterator() throws IOException {
                return NumericUtils.filterPrefixCodedLongs(this.in.iterator());
            }
        };
    }
    
    public static Integer getMinInt(final Terms terms) throws IOException {
        final BytesRef min = terms.getMin();
        return (min != null) ? Integer.valueOf(prefixCodedToInt(min)) : null;
    }
    
    public static Integer getMaxInt(final Terms terms) throws IOException {
        final BytesRef max = intTerms(terms).getMax();
        return (max != null) ? Integer.valueOf(prefixCodedToInt(max)) : null;
    }
    
    public static Long getMinLong(final Terms terms) throws IOException {
        final BytesRef min = terms.getMin();
        return (min != null) ? Long.valueOf(prefixCodedToLong(min)) : null;
    }
    
    public static Long getMaxLong(final Terms terms) throws IOException {
        final BytesRef max = longTerms(terms).getMax();
        return (max != null) ? Long.valueOf(prefixCodedToLong(max)) : null;
    }
    
    public abstract static class LongRangeBuilder
    {
        public void addRange(final BytesRef minPrefixCoded, final BytesRef maxPrefixCoded) {
            throw new UnsupportedOperationException();
        }
        
        public void addRange(final long min, final long max, final int shift) {
            final BytesRefBuilder minBytes = new BytesRefBuilder();
            final BytesRefBuilder maxBytes = new BytesRefBuilder();
            NumericUtils.longToPrefixCoded(min, shift, minBytes);
            NumericUtils.longToPrefixCoded(max, shift, maxBytes);
            this.addRange(minBytes.get(), maxBytes.get());
        }
    }
    
    public abstract static class IntRangeBuilder
    {
        public void addRange(final BytesRef minPrefixCoded, final BytesRef maxPrefixCoded) {
            throw new UnsupportedOperationException();
        }
        
        public void addRange(final int min, final int max, final int shift) {
            final BytesRefBuilder minBytes = new BytesRefBuilder();
            final BytesRefBuilder maxBytes = new BytesRefBuilder();
            NumericUtils.intToPrefixCoded(min, shift, minBytes);
            NumericUtils.intToPrefixCoded(max, shift, maxBytes);
            this.addRange(minBytes.get(), maxBytes.get());
        }
    }
    
    private abstract static class SeekingNumericFilteredTermsEnum extends FilteredTermsEnum
    {
        public SeekingNumericFilteredTermsEnum(final TermsEnum tenum) {
            super(tenum, false);
        }
        
        @Override
        public SeekStatus seekCeil(final BytesRef term) throws IOException {
            final SeekStatus status = this.tenum.seekCeil(term);
            if (status == SeekStatus.END) {
                return SeekStatus.END;
            }
            this.actualTerm = this.tenum.term();
            if (this.accept(this.actualTerm) == AcceptStatus.YES) {
                return status;
            }
            return SeekStatus.END;
        }
    }
}
