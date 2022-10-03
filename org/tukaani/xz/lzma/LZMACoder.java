package org.tukaani.xz.lzma;

import org.tukaani.xz.rangecoder.RangeCoder;

abstract class LZMACoder
{
    static final int POS_STATES_MAX = 16;
    static final int MATCH_LEN_MIN = 2;
    static final int MATCH_LEN_MAX = 273;
    static final int DIST_STATES = 4;
    static final int DIST_SLOTS = 64;
    static final int DIST_MODEL_START = 4;
    static final int DIST_MODEL_END = 14;
    static final int FULL_DISTANCES = 128;
    static final int ALIGN_BITS = 4;
    static final int ALIGN_SIZE = 16;
    static final int ALIGN_MASK = 15;
    static final int REPS = 4;
    final int posMask;
    final int[] reps;
    final State state;
    final short[][] isMatch;
    final short[] isRep;
    final short[] isRep0;
    final short[] isRep1;
    final short[] isRep2;
    final short[][] isRep0Long;
    final short[][] distSlots;
    final short[][] distSpecial;
    final short[] distAlign;
    
    static final int getDistState(final int n) {
        return (n < 6) ? (n - 2) : 3;
    }
    
    LZMACoder(final int n) {
        this.reps = new int[4];
        this.state = new State();
        this.isMatch = new short[12][16];
        this.isRep = new short[12];
        this.isRep0 = new short[12];
        this.isRep1 = new short[12];
        this.isRep2 = new short[12];
        this.isRep0Long = new short[12][16];
        this.distSlots = new short[4][64];
        this.distSpecial = new short[][] { new short[2], new short[2], new short[4], new short[4], new short[8], new short[8], new short[16], new short[16], new short[32], new short[32] };
        this.distAlign = new short[16];
        this.posMask = (1 << n) - 1;
    }
    
    void reset() {
        this.reps[0] = 0;
        this.reps[1] = 0;
        this.reps[2] = 0;
        this.reps[3] = 0;
        this.state.reset();
        for (int i = 0; i < this.isMatch.length; ++i) {
            RangeCoder.initProbs(this.isMatch[i]);
        }
        RangeCoder.initProbs(this.isRep);
        RangeCoder.initProbs(this.isRep0);
        RangeCoder.initProbs(this.isRep1);
        RangeCoder.initProbs(this.isRep2);
        for (int j = 0; j < this.isRep0Long.length; ++j) {
            RangeCoder.initProbs(this.isRep0Long[j]);
        }
        for (int k = 0; k < this.distSlots.length; ++k) {
            RangeCoder.initProbs(this.distSlots[k]);
        }
        for (int l = 0; l < this.distSpecial.length; ++l) {
            RangeCoder.initProbs(this.distSpecial[l]);
        }
        RangeCoder.initProbs(this.distAlign);
    }
    
    abstract class LengthCoder
    {
        static final int LOW_SYMBOLS = 8;
        static final int MID_SYMBOLS = 8;
        static final int HIGH_SYMBOLS = 256;
        final short[] choice;
        final short[][] low;
        final short[][] mid;
        final short[] high;
        
        LengthCoder() {
            this.choice = new short[2];
            this.low = new short[16][8];
            this.mid = new short[16][8];
            this.high = new short[256];
        }
        
        void reset() {
            RangeCoder.initProbs(this.choice);
            for (int i = 0; i < this.low.length; ++i) {
                RangeCoder.initProbs(this.low[i]);
            }
            for (int j = 0; j < this.low.length; ++j) {
                RangeCoder.initProbs(this.mid[j]);
            }
            RangeCoder.initProbs(this.high);
        }
    }
    
    abstract class LiteralCoder
    {
        private final int lc;
        private final int literalPosMask;
        
        LiteralCoder(final int lc, final int n) {
            this.lc = lc;
            this.literalPosMask = (1 << n) - 1;
        }
        
        final int getSubcoderIndex(final int n, final int n2) {
            return (n >> 8 - this.lc) + ((n2 & this.literalPosMask) << this.lc);
        }
        
        abstract class LiteralSubcoder
        {
            final short[] probs;
            
            LiteralSubcoder() {
                this.probs = new short[768];
            }
            
            void reset() {
                RangeCoder.initProbs(this.probs);
            }
        }
    }
}
