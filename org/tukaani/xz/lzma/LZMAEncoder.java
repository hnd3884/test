package org.tukaani.xz.lzma;

import org.tukaani.xz.lz.Matches;
import java.io.IOException;
import org.tukaani.xz.ArrayCache;
import org.tukaani.xz.lz.LZEncoder;
import org.tukaani.xz.rangecoder.RangeEncoder;

public abstract class LZMAEncoder extends LZMACoder
{
    public static final int MODE_FAST = 1;
    public static final int MODE_NORMAL = 2;
    private static final int LZMA2_UNCOMPRESSED_LIMIT = 2096879;
    private static final int LZMA2_COMPRESSED_LIMIT = 65510;
    private static final int DIST_PRICE_UPDATE_INTERVAL = 128;
    private static final int ALIGN_PRICE_UPDATE_INTERVAL = 16;
    private final RangeEncoder rc;
    final LZEncoder lz;
    final LiteralEncoder literalEncoder;
    final LengthEncoder matchLenEncoder;
    final LengthEncoder repLenEncoder;
    final int niceLen;
    private int distPriceCount;
    private int alignPriceCount;
    private final int distSlotPricesSize;
    private final int[][] distSlotPrices;
    private final int[][] fullDistPrices;
    private final int[] alignPrices;
    int back;
    int readAhead;
    private int uncompressedSize;
    
    public static int getMemoryUsage(final int n, final int n2, final int n3, final int n4) {
        final int n5 = 80;
        int n6 = 0;
        switch (n) {
            case 1: {
                n6 = n5 + LZMAEncoderFast.getMemoryUsage(n2, n3, n4);
                break;
            }
            case 2: {
                n6 = n5 + LZMAEncoderNormal.getMemoryUsage(n2, n3, n4);
                break;
            }
            default: {
                throw new IllegalArgumentException();
            }
        }
        return n6;
    }
    
    public static LZMAEncoder getInstance(final RangeEncoder rangeEncoder, final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final int n7, final int n8, final int n9, final ArrayCache arrayCache) {
        switch (n4) {
            case 1: {
                return new LZMAEncoderFast(rangeEncoder, n, n2, n3, n5, n6, n7, n8, n9, arrayCache);
            }
            case 2: {
                return new LZMAEncoderNormal(rangeEncoder, n, n2, n3, n5, n6, n7, n8, n9, arrayCache);
            }
            default: {
                throw new IllegalArgumentException();
            }
        }
    }
    
    public void putArraysToCache(final ArrayCache arrayCache) {
        this.lz.putArraysToCache(arrayCache);
    }
    
    public static int getDistSlot(final int n) {
        if (n <= 4 && n >= 0) {
            return n;
        }
        int n2 = n;
        int n3 = 31;
        if ((n2 & 0xFFFF0000) == 0x0) {
            n2 <<= 16;
            n3 = 15;
        }
        if ((n2 & 0xFF000000) == 0x0) {
            n2 <<= 8;
            n3 -= 8;
        }
        if ((n2 & 0xF0000000) == 0x0) {
            n2 <<= 4;
            n3 -= 4;
        }
        if ((n2 & 0xC0000000) == 0x0) {
            n2 <<= 2;
            n3 -= 2;
        }
        if ((n2 & Integer.MIN_VALUE) == 0x0) {
            --n3;
        }
        return (n3 << 1) + (n >>> n3 - 1 & 0x1);
    }
    
    abstract int getNextSymbol();
    
    LZMAEncoder(final RangeEncoder rc, final LZEncoder lz, final int n, final int n2, final int n3, final int n4, final int niceLen) {
        super(n3);
        this.distPriceCount = 0;
        this.alignPriceCount = 0;
        this.fullDistPrices = new int[4][128];
        this.alignPrices = new int[16];
        this.back = 0;
        this.readAhead = -1;
        this.uncompressedSize = 0;
        this.rc = rc;
        this.lz = lz;
        this.niceLen = niceLen;
        this.literalEncoder = new LiteralEncoder(n, n2);
        this.matchLenEncoder = new LengthEncoder(n3, niceLen);
        this.repLenEncoder = new LengthEncoder(n3, niceLen);
        this.distSlotPricesSize = getDistSlot(n4 - 1) + 1;
        this.distSlotPrices = new int[4][this.distSlotPricesSize];
        this.reset();
    }
    
    public LZEncoder getLZEncoder() {
        return this.lz;
    }
    
    public void reset() {
        super.reset();
        this.literalEncoder.reset();
        this.matchLenEncoder.reset();
        this.repLenEncoder.reset();
        this.distPriceCount = 0;
        this.alignPriceCount = 0;
        this.uncompressedSize += this.readAhead + 1;
        this.readAhead = -1;
    }
    
    public int getUncompressedSize() {
        return this.uncompressedSize;
    }
    
    public void resetUncompressedSize() {
        this.uncompressedSize = 0;
    }
    
    public void encodeForLZMA1() throws IOException {
        if (!this.lz.isStarted() && !this.encodeInit()) {
            return;
        }
        while (this.encodeSymbol()) {}
    }
    
    public void encodeLZMA1EndMarker() throws IOException {
        final int n = this.lz.getPos() - this.readAhead & this.posMask;
        this.rc.encodeBit(this.isMatch[this.state.get()], n, 1);
        this.rc.encodeBit(this.isRep, this.state.get(), 0);
        this.encodeMatch(-1, 2, n);
    }
    
    public boolean encodeForLZMA2() {
        try {
            if (!this.lz.isStarted() && !this.encodeInit()) {
                return false;
            }
            while (this.uncompressedSize <= 2096879 && this.rc.getPendingSize() <= 65510) {
                if (!this.encodeSymbol()) {
                    return false;
                }
            }
        }
        catch (final IOException ex) {
            throw new Error();
        }
        return true;
    }
    
    private boolean encodeInit() throws IOException {
        assert this.readAhead == -1;
        if (!this.lz.hasEnoughData(0)) {
            return false;
        }
        this.skip(1);
        this.rc.encodeBit(this.isMatch[this.state.get()], 0, 0);
        this.literalEncoder.encodeInit();
        --this.readAhead;
        assert this.readAhead == -1;
        ++this.uncompressedSize;
        assert this.uncompressedSize == 1;
        return true;
    }
    
    private boolean encodeSymbol() throws IOException {
        if (!this.lz.hasEnoughData(this.readAhead + 1)) {
            return false;
        }
        final int nextSymbol = this.getNextSymbol();
        assert this.readAhead >= 0;
        final int n = this.lz.getPos() - this.readAhead & this.posMask;
        if (this.back == -1) {
            assert nextSymbol == 1;
            this.rc.encodeBit(this.isMatch[this.state.get()], n, 0);
            this.literalEncoder.encode();
        }
        else {
            this.rc.encodeBit(this.isMatch[this.state.get()], n, 1);
            if (this.back < 4) {
                assert this.lz.getMatchLen(-this.readAhead, this.reps[this.back], nextSymbol) == nextSymbol;
                this.rc.encodeBit(this.isRep, this.state.get(), 1);
                this.encodeRepMatch(this.back, nextSymbol, n);
            }
            else {
                assert this.lz.getMatchLen(-this.readAhead, this.back - 4, nextSymbol) == nextSymbol;
                this.rc.encodeBit(this.isRep, this.state.get(), 0);
                this.encodeMatch(this.back - 4, nextSymbol, n);
            }
        }
        this.readAhead -= nextSymbol;
        this.uncompressedSize += nextSymbol;
        return true;
    }
    
    private void encodeMatch(final int n, final int n2, final int n3) throws IOException {
        this.state.updateMatch();
        this.matchLenEncoder.encode(n2, n3);
        final int distSlot = getDistSlot(n);
        this.rc.encodeBitTree(this.distSlots[LZMACoder.getDistState(n2)], distSlot);
        if (distSlot >= 4) {
            final int n4 = (distSlot >>> 1) - 1;
            final int n5 = n - ((0x2 | (distSlot & 0x1)) << n4);
            if (distSlot < 14) {
                this.rc.encodeReverseBitTree(this.distSpecial[distSlot - 4], n5);
            }
            else {
                this.rc.encodeDirectBits(n5 >>> 4, n4 - 4);
                this.rc.encodeReverseBitTree(this.distAlign, n5 & 0xF);
                --this.alignPriceCount;
            }
        }
        this.reps[3] = this.reps[2];
        this.reps[2] = this.reps[1];
        this.reps[1] = this.reps[0];
        this.reps[0] = n;
        --this.distPriceCount;
    }
    
    private void encodeRepMatch(final int n, final int n2, final int n3) throws IOException {
        if (n == 0) {
            this.rc.encodeBit(this.isRep0, this.state.get(), 0);
            this.rc.encodeBit(this.isRep0Long[this.state.get()], n3, (n2 != 1) ? 1 : 0);
        }
        else {
            final int n4 = this.reps[n];
            this.rc.encodeBit(this.isRep0, this.state.get(), 1);
            if (n == 1) {
                this.rc.encodeBit(this.isRep1, this.state.get(), 0);
            }
            else {
                this.rc.encodeBit(this.isRep1, this.state.get(), 1);
                this.rc.encodeBit(this.isRep2, this.state.get(), n - 2);
                if (n == 3) {
                    this.reps[3] = this.reps[2];
                }
                this.reps[2] = this.reps[1];
            }
            this.reps[1] = this.reps[0];
            this.reps[0] = n4;
        }
        if (n2 == 1) {
            this.state.updateShortRep();
        }
        else {
            this.repLenEncoder.encode(n2, n3);
            this.state.updateLongRep();
        }
    }
    
    Matches getMatches() {
        ++this.readAhead;
        final Matches matches = this.lz.getMatches();
        assert this.lz.verifyMatches(matches);
        return matches;
    }
    
    void skip(final int n) {
        this.readAhead += n;
        this.lz.skip(n);
    }
    
    int getAnyMatchPrice(final State state, final int n) {
        return RangeEncoder.getBitPrice(this.isMatch[state.get()][n], 1);
    }
    
    int getNormalMatchPrice(final int n, final State state) {
        return n + RangeEncoder.getBitPrice(this.isRep[state.get()], 0);
    }
    
    int getAnyRepPrice(final int n, final State state) {
        return n + RangeEncoder.getBitPrice(this.isRep[state.get()], 1);
    }
    
    int getShortRepPrice(final int n, final State state, final int n2) {
        return n + RangeEncoder.getBitPrice(this.isRep0[state.get()], 0) + RangeEncoder.getBitPrice(this.isRep0Long[state.get()][n2], 0);
    }
    
    int getLongRepPrice(final int n, final int n2, final State state, final int n3) {
        int n4;
        if (n2 == 0) {
            n4 = n + (RangeEncoder.getBitPrice(this.isRep0[state.get()], 0) + RangeEncoder.getBitPrice(this.isRep0Long[state.get()][n3], 1));
        }
        else {
            final int n5 = n + RangeEncoder.getBitPrice(this.isRep0[state.get()], 1);
            if (n2 == 1) {
                n4 = n5 + RangeEncoder.getBitPrice(this.isRep1[state.get()], 0);
            }
            else {
                n4 = n5 + (RangeEncoder.getBitPrice(this.isRep1[state.get()], 1) + RangeEncoder.getBitPrice(this.isRep2[state.get()], n2 - 2));
            }
        }
        return n4;
    }
    
    int getLongRepAndLenPrice(final int n, final int n2, final State state, final int n3) {
        return this.getLongRepPrice(this.getAnyRepPrice(this.getAnyMatchPrice(state, n3), state), n, state, n3) + this.repLenEncoder.getPrice(n2, n3);
    }
    
    int getMatchAndLenPrice(final int n, final int n2, final int n3, final int n4) {
        final int n5 = n + this.matchLenEncoder.getPrice(n3, n4);
        final int distState = LZMACoder.getDistState(n3);
        int n6;
        if (n2 < 128) {
            n6 = n5 + this.fullDistPrices[distState][n2];
        }
        else {
            n6 = n5 + (this.distSlotPrices[distState][getDistSlot(n2)] + this.alignPrices[n2 & 0xF]);
        }
        return n6;
    }
    
    private void updateDistPrices() {
        this.distPriceCount = 128;
        for (int i = 0; i < 4; ++i) {
            for (int j = 0; j < this.distSlotPricesSize; ++j) {
                this.distSlotPrices[i][j] = RangeEncoder.getBitTreePrice(this.distSlots[i], j);
            }
            for (int k = 14; k < this.distSlotPricesSize; ++k) {
                final int n = (k >>> 1) - 1 - 4;
                final int[] array = this.distSlotPrices[i];
                final int n2 = k;
                array[n2] += RangeEncoder.getDirectBitsPrice(n);
            }
            for (int l = 0; l < 4; ++l) {
                this.fullDistPrices[i][l] = this.distSlotPrices[i][l];
            }
        }
        int n3 = 4;
        for (int n4 = 4; n4 < 14; ++n4) {
            final int n5 = (0x2 | (n4 & 0x1)) << (n4 >>> 1) - 1;
            for (int length = this.distSpecial[n4 - 4].length, n6 = 0; n6 < length; ++n6) {
                final int reverseBitTreePrice = RangeEncoder.getReverseBitTreePrice(this.distSpecial[n4 - 4], n3 - n5);
                for (int n7 = 0; n7 < 4; ++n7) {
                    this.fullDistPrices[n7][n3] = this.distSlotPrices[n7][n4] + reverseBitTreePrice;
                }
                ++n3;
            }
        }
        assert n3 == 128;
    }
    
    private void updateAlignPrices() {
        this.alignPriceCount = 16;
        for (int i = 0; i < 16; ++i) {
            this.alignPrices[i] = RangeEncoder.getReverseBitTreePrice(this.distAlign, i);
        }
    }
    
    void updatePrices() {
        if (this.distPriceCount <= 0) {
            this.updateDistPrices();
        }
        if (this.alignPriceCount <= 0) {
            this.updateAlignPrices();
        }
        this.matchLenEncoder.updatePrices();
        this.repLenEncoder.updatePrices();
    }
    
    class LiteralEncoder extends LiteralCoder
    {
        private final LiteralSubencoder[] subencoders;
        
        LiteralEncoder(final int n, final int n2) {
            super(n, n2);
            this.subencoders = new LiteralSubencoder[1 << n + n2];
            for (int i = 0; i < this.subencoders.length; ++i) {
                this.subencoders[i] = new LiteralSubencoder();
            }
        }
        
        void reset() {
            for (int i = 0; i < this.subencoders.length; ++i) {
                this.subencoders[i].reset();
            }
        }
        
        void encodeInit() throws IOException {
            assert LZMAEncoder.this.readAhead >= 0;
            this.subencoders[0].encode();
        }
        
        void encode() throws IOException {
            assert LZMAEncoder.this.readAhead >= 0;
            this.subencoders[this.getSubcoderIndex(LZMAEncoder.this.lz.getByte(1 + LZMAEncoder.this.readAhead), LZMAEncoder.this.lz.getPos() - LZMAEncoder.this.readAhead)].encode();
        }
        
        int getPrice(final int n, final int n2, final int n3, final int n4, final State state) {
            final int bitPrice = RangeEncoder.getBitPrice(LZMAEncoder.this.isMatch[state.get()][n4 & LZMAEncoder.this.posMask], 0);
            final int subcoderIndex = this.getSubcoderIndex(n3, n4);
            return bitPrice + (state.isLiteral() ? this.subencoders[subcoderIndex].getNormalPrice(n) : this.subencoders[subcoderIndex].getMatchedPrice(n, n2));
        }
        
        private class LiteralSubencoder extends LiteralSubcoder
        {
            void encode() throws IOException {
                int i = LZMAEncoder.this.lz.getByte(LZMAEncoder.this.readAhead) | 0x100;
                if (LZMAEncoder.this.state.isLiteral()) {
                    do {
                        LZMAEncoder.this.rc.encodeBit(this.probs, i >>> 8, i >>> 7 & 0x1);
                        i <<= 1;
                    } while (i < 65536);
                }
                else {
                    int byte1 = LZMAEncoder.this.lz.getByte(LZMAEncoder.this.reps[0] + 1 + LZMAEncoder.this.readAhead);
                    int n = 256;
                    do {
                        byte1 <<= 1;
                        LZMAEncoder.this.rc.encodeBit(this.probs, n + (byte1 & n) + (i >>> 8), i >>> 7 & 0x1);
                        i <<= 1;
                        n &= ~(byte1 ^ i);
                    } while (i < 65536);
                }
                LZMAEncoder.this.state.updateLiteral();
            }
            
            int getNormalPrice(int i) {
                int n = 0;
                i |= 0x100;
                do {
                    n += RangeEncoder.getBitPrice(this.probs[i >>> 8], i >>> 7 & 0x1);
                    i <<= 1;
                } while (i < 65536);
                return n;
            }
            
            int getMatchedPrice(int i, int n) {
                int n2 = 0;
                int n3 = 256;
                i |= 0x100;
                do {
                    n <<= 1;
                    n2 += RangeEncoder.getBitPrice(this.probs[n3 + (n & n3) + (i >>> 8)], i >>> 7 & 0x1);
                    i <<= 1;
                    n3 &= ~(n ^ i);
                } while (i < 65536);
                return n2;
            }
        }
    }
    
    class LengthEncoder extends LengthCoder
    {
        private static final int PRICE_UPDATE_INTERVAL = 32;
        private final int[] counters;
        private final int[][] prices;
        
        LengthEncoder(final int n, final int n2) {
            final int n3 = 1 << n;
            this.counters = new int[n3];
            this.prices = new int[n3][Math.max(n2 - 2 + 1, 16)];
        }
        
        @Override
        void reset() {
            super.reset();
            for (int i = 0; i < this.counters.length; ++i) {
                this.counters[i] = 0;
            }
        }
        
        void encode(int n, final int n2) throws IOException {
            n -= 2;
            if (n < 8) {
                LZMAEncoder.this.rc.encodeBit(this.choice, 0, 0);
                LZMAEncoder.this.rc.encodeBitTree(this.low[n2], n);
            }
            else {
                LZMAEncoder.this.rc.encodeBit(this.choice, 0, 1);
                n -= 8;
                if (n < 8) {
                    LZMAEncoder.this.rc.encodeBit(this.choice, 1, 0);
                    LZMAEncoder.this.rc.encodeBitTree(this.mid[n2], n);
                }
                else {
                    LZMAEncoder.this.rc.encodeBit(this.choice, 1, 1);
                    LZMAEncoder.this.rc.encodeBitTree(this.high, n - 8);
                }
            }
            final int[] counters = this.counters;
            --counters[n2];
        }
        
        int getPrice(final int n, final int n2) {
            return this.prices[n2][n - 2];
        }
        
        void updatePrices() {
            for (int i = 0; i < this.counters.length; ++i) {
                if (this.counters[i] <= 0) {
                    this.counters[i] = 32;
                    this.updatePrices(i);
                }
            }
        }
        
        private void updatePrices(final int n) {
            final int bitPrice = RangeEncoder.getBitPrice(this.choice[0], 0);
            int i;
            for (i = 0; i < 8; ++i) {
                this.prices[n][i] = bitPrice + RangeEncoder.getBitTreePrice(this.low[n], i);
            }
            final int bitPrice2 = RangeEncoder.getBitPrice(this.choice[0], 1);
            final int bitPrice3 = RangeEncoder.getBitPrice(this.choice[1], 0);
            while (i < 16) {
                this.prices[n][i] = bitPrice2 + bitPrice3 + RangeEncoder.getBitTreePrice(this.mid[n], i - 8);
                ++i;
            }
            final int bitPrice4 = RangeEncoder.getBitPrice(this.choice[1], 1);
            while (i < this.prices[n].length) {
                this.prices[n][i] = bitPrice2 + bitPrice4 + RangeEncoder.getBitTreePrice(this.high, i - 8 - 8);
                ++i;
            }
        }
    }
}
