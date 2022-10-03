package org.tukaani.xz.lzma;

import java.io.IOException;
import org.tukaani.xz.rangecoder.RangeDecoder;
import org.tukaani.xz.lz.LZDecoder;

public final class LZMADecoder extends LZMACoder
{
    private final LZDecoder lz;
    private final RangeDecoder rc;
    private final LiteralDecoder literalDecoder;
    private final LengthDecoder matchLenDecoder;
    private final LengthDecoder repLenDecoder;
    
    public LZMADecoder(final LZDecoder lz, final RangeDecoder rc, final int n, final int n2, final int n3) {
        super(n3);
        this.matchLenDecoder = new LengthDecoder();
        this.repLenDecoder = new LengthDecoder();
        this.lz = lz;
        this.rc = rc;
        this.literalDecoder = new LiteralDecoder(n, n2);
        this.reset();
    }
    
    public void reset() {
        super.reset();
        this.literalDecoder.reset();
        this.matchLenDecoder.reset();
        this.repLenDecoder.reset();
    }
    
    public boolean endMarkerDetected() {
        return this.reps[0] == -1;
    }
    
    public void decode() throws IOException {
        this.lz.repeatPending();
        while (this.lz.hasSpace()) {
            final int n = this.lz.getPos() & this.posMask;
            if (this.rc.decodeBit(this.isMatch[this.state.get()], n) == 0) {
                this.literalDecoder.decode();
            }
            else {
                this.lz.repeat(this.reps[0], (this.rc.decodeBit(this.isRep, this.state.get()) == 0) ? this.decodeMatch(n) : this.decodeRepMatch(n));
            }
        }
        this.rc.normalize();
    }
    
    private int decodeMatch(final int n) throws IOException {
        this.state.updateMatch();
        this.reps[3] = this.reps[2];
        this.reps[2] = this.reps[1];
        this.reps[1] = this.reps[0];
        final int decode = this.matchLenDecoder.decode(n);
        final int decodeBitTree = this.rc.decodeBitTree(this.distSlots[LZMACoder.getDistState(decode)]);
        if (decodeBitTree < 4) {
            this.reps[0] = decodeBitTree;
        }
        else {
            final int n2 = (decodeBitTree >> 1) - 1;
            this.reps[0] = (0x2 | (decodeBitTree & 0x1)) << n2;
            if (decodeBitTree < 14) {
                final int[] reps = this.reps;
                final int n3 = 0;
                reps[n3] |= this.rc.decodeReverseBitTree(this.distSpecial[decodeBitTree - 4]);
            }
            else {
                final int[] reps2 = this.reps;
                final int n4 = 0;
                reps2[n4] |= this.rc.decodeDirectBits(n2 - 4) << 4;
                final int[] reps3 = this.reps;
                final int n5 = 0;
                reps3[n5] |= this.rc.decodeReverseBitTree(this.distAlign);
            }
        }
        return decode;
    }
    
    private int decodeRepMatch(final int n) throws IOException {
        if (this.rc.decodeBit(this.isRep0, this.state.get()) == 0) {
            if (this.rc.decodeBit(this.isRep0Long[this.state.get()], n) == 0) {
                this.state.updateShortRep();
                return 1;
            }
        }
        else {
            int n2;
            if (this.rc.decodeBit(this.isRep1, this.state.get()) == 0) {
                n2 = this.reps[1];
            }
            else {
                if (this.rc.decodeBit(this.isRep2, this.state.get()) == 0) {
                    n2 = this.reps[2];
                }
                else {
                    n2 = this.reps[3];
                    this.reps[3] = this.reps[2];
                }
                this.reps[2] = this.reps[1];
            }
            this.reps[1] = this.reps[0];
            this.reps[0] = n2;
        }
        this.state.updateLongRep();
        return this.repLenDecoder.decode(n);
    }
    
    private class LengthDecoder extends LengthCoder
    {
        int decode(final int n) throws IOException {
            if (LZMADecoder.this.rc.decodeBit(this.choice, 0) == 0) {
                return LZMADecoder.this.rc.decodeBitTree(this.low[n]) + 2;
            }
            if (LZMADecoder.this.rc.decodeBit(this.choice, 1) == 0) {
                return LZMADecoder.this.rc.decodeBitTree(this.mid[n]) + 2 + 8;
            }
            return LZMADecoder.this.rc.decodeBitTree(this.high) + 2 + 8 + 8;
        }
    }
    
    private class LiteralDecoder extends LiteralCoder
    {
        private final LiteralSubdecoder[] subdecoders;
        
        LiteralDecoder(final int n, final int n2) {
            super(n, n2);
            this.subdecoders = new LiteralSubdecoder[1 << n + n2];
            for (int i = 0; i < this.subdecoders.length; ++i) {
                this.subdecoders[i] = new LiteralSubdecoder();
            }
        }
        
        void reset() {
            for (int i = 0; i < this.subdecoders.length; ++i) {
                this.subdecoders[i].reset();
            }
        }
        
        void decode() throws IOException {
            this.subdecoders[this.getSubcoderIndex(LZMADecoder.this.lz.getByte(0), LZMADecoder.this.lz.getPos())].decode();
        }
        
        private class LiteralSubdecoder extends LiteralSubcoder
        {
            void decode() throws IOException {
                int i = 1;
                if (LZMADecoder.this.state.isLiteral()) {
                    do {
                        i = (i << 1 | LZMADecoder.this.rc.decodeBit(this.probs, i));
                    } while (i < 256);
                }
                else {
                    int byte1 = LZMADecoder.this.lz.getByte(LZMADecoder.this.reps[0]);
                    int n = 256;
                    do {
                        byte1 <<= 1;
                        final int n2 = byte1 & n;
                        final int decodeBit = LZMADecoder.this.rc.decodeBit(this.probs, n + n2 + i);
                        i = (i << 1 | decodeBit);
                        n &= (0 - decodeBit ^ ~n2);
                    } while (i < 256);
                }
                LZMADecoder.this.lz.putByte((byte)i);
                LZMADecoder.this.state.updateLiteral();
            }
        }
    }
}
