package org.tukaani.xz.lzma;

import org.tukaani.xz.ArrayCache;
import org.tukaani.xz.rangecoder.RangeEncoder;
import org.tukaani.xz.lz.LZEncoder;
import org.tukaani.xz.lz.Matches;

final class LZMAEncoderNormal extends LZMAEncoder
{
    private static final int OPTS = 4096;
    private static final int EXTRA_SIZE_BEFORE = 4096;
    private static final int EXTRA_SIZE_AFTER = 4096;
    private final Optimum[] opts;
    private int optCur;
    private int optEnd;
    private Matches matches;
    private final int[] repLens;
    private final State nextState;
    
    static int getMemoryUsage(final int n, final int n2, final int n3) {
        return LZEncoder.getMemoryUsage(n, Math.max(n2, 4096), 4096, 273, n3) + 256;
    }
    
    LZMAEncoderNormal(final RangeEncoder rangeEncoder, final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final int n7, final int n8, final ArrayCache arrayCache) {
        super(rangeEncoder, LZEncoder.getInstance(n4, Math.max(n5, 4096), 4096, n6, 273, n7, n8, arrayCache), n, n2, n3, n4, n6);
        this.opts = new Optimum[4096];
        this.optCur = 0;
        this.optEnd = 0;
        this.repLens = new int[4];
        this.nextState = new State();
        for (int i = 0; i < 4096; ++i) {
            this.opts[i] = new Optimum();
        }
    }
    
    @Override
    public void reset() {
        this.optCur = 0;
        this.optEnd = 0;
        super.reset();
    }
    
    private int convertOpts() {
        this.optEnd = this.optCur;
        int n = this.opts[this.optCur].optPrev;
        do {
            final Optimum optimum = this.opts[this.optCur];
            if (optimum.prev1IsLiteral) {
                this.opts[n].optPrev = this.optCur;
                this.opts[n].backPrev = -1;
                this.optCur = n--;
                if (optimum.hasPrev2) {
                    this.opts[n].optPrev = n + 1;
                    this.opts[n].backPrev = optimum.backPrev2;
                    this.optCur = n;
                    n = optimum.optPrev2;
                }
            }
            final int optPrev = this.opts[n].optPrev;
            this.opts[n].optPrev = this.optCur;
            this.optCur = n;
            n = optPrev;
        } while (this.optCur > 0);
        this.optCur = this.opts[0].optPrev;
        this.back = this.opts[this.optCur].backPrev;
        return this.optCur;
    }
    
    @Override
    int getNextSymbol() {
        if (this.optCur < this.optEnd) {
            final int n = this.opts[this.optCur].optPrev - this.optCur;
            this.optCur = this.opts[this.optCur].optPrev;
            this.back = this.opts[this.optCur].backPrev;
            return n;
        }
        assert this.optCur == this.optEnd;
        this.optCur = 0;
        this.optEnd = 0;
        this.back = -1;
        if (this.readAhead == -1) {
            this.matches = this.getMatches();
        }
        final int min = Math.min(this.lz.getAvail(), 273);
        if (min < 2) {
            return 1;
        }
        int back = 0;
        for (int i = 0; i < 4; ++i) {
            this.repLens[i] = this.lz.getMatchLen(this.reps[i], min);
            if (this.repLens[i] < 2) {
                this.repLens[i] = 0;
            }
            else if (this.repLens[i] > this.repLens[back]) {
                back = i;
            }
        }
        if (this.repLens[back] >= this.niceLen) {
            this.back = back;
            this.skip(this.repLens[back] - 1);
            return this.repLens[back];
        }
        int n2 = 0;
        if (this.matches.count > 0) {
            n2 = this.matches.len[this.matches.count - 1];
            final int n3 = this.matches.dist[this.matches.count - 1];
            if (n2 >= this.niceLen) {
                this.back = n3 + 4;
                this.skip(n2 - 1);
                return n2;
            }
        }
        final int byte1 = this.lz.getByte(0);
        final int byte2 = this.lz.getByte(this.reps[0] + 1);
        if (n2 < 2 && byte1 != byte2 && this.repLens[back] < 2) {
            return 1;
        }
        int pos = this.lz.getPos();
        final int n4 = pos & this.posMask;
        this.opts[1].set1(this.literalEncoder.getPrice(byte1, byte2, this.lz.getByte(1), pos, this.state), 0, -1);
        final int anyMatchPrice = this.getAnyMatchPrice(this.state, n4);
        final int anyRepPrice = this.getAnyRepPrice(anyMatchPrice, this.state);
        if (byte2 == byte1) {
            final int shortRepPrice = this.getShortRepPrice(anyRepPrice, this.state, n4);
            if (shortRepPrice < this.opts[1].price) {
                this.opts[1].set1(shortRepPrice, 0, 0);
            }
        }
        this.optEnd = Math.max(n2, this.repLens[back]);
        if (this.optEnd >= 2) {
            this.updatePrices();
            this.opts[0].state.set(this.state);
            System.arraycopy(this.reps, 0, this.opts[0].reps, 0, 4);
            for (int j = this.optEnd; j >= 2; --j) {
                this.opts[j].reset();
            }
            for (int k = 0; k < 4; ++k) {
                int n5 = this.repLens[k];
                if (n5 >= 2) {
                    final int longRepPrice = this.getLongRepPrice(anyRepPrice, k, this.state, n4);
                    do {
                        final int n6 = longRepPrice + this.repLenEncoder.getPrice(n5, n4);
                        if (n6 < this.opts[n5].price) {
                            this.opts[n5].set1(n6, 0, k);
                        }
                    } while (--n5 >= 2);
                }
            }
            int l = Math.max(this.repLens[0] + 1, 2);
            if (l <= n2) {
                final int normalMatchPrice = this.getNormalMatchPrice(anyMatchPrice, this.state);
                int n7;
                for (n7 = 0; l > this.matches.len[n7]; ++n7) {}
                while (true) {
                    final int n8 = this.matches.dist[n7];
                    final int matchAndLenPrice = this.getMatchAndLenPrice(normalMatchPrice, n8, l, n4);
                    if (matchAndLenPrice < this.opts[l].price) {
                        this.opts[l].set1(matchAndLenPrice, 0, n8 + 4);
                    }
                    if (l == this.matches.len[n7] && ++n7 == this.matches.count) {
                        break;
                    }
                    ++l;
                }
            }
            int min2 = Math.min(this.lz.getAvail(), 4095);
            while (++this.optCur < this.optEnd) {
                this.matches = this.getMatches();
                if (this.matches.count > 0 && this.matches.len[this.matches.count - 1] >= this.niceLen) {
                    break;
                }
                --min2;
                final int n9 = ++pos & this.posMask;
                this.updateOptStateAndReps();
                final int n10 = this.opts[this.optCur].price + this.getAnyMatchPrice(this.opts[this.optCur].state, n9);
                final int anyRepPrice2 = this.getAnyRepPrice(n10, this.opts[this.optCur].state);
                this.calc1BytePrices(pos, n9, min2, anyRepPrice2);
                if (min2 < 2) {
                    continue;
                }
                final int calcLongRepPrices = this.calcLongRepPrices(pos, n9, min2, anyRepPrice2);
                if (this.matches.count <= 0) {
                    continue;
                }
                this.calcNormalMatchPrices(pos, n9, min2, n10, calcLongRepPrices);
            }
            return this.convertOpts();
        }
        assert this.optEnd == 0 : this.optEnd;
        this.back = this.opts[1].backPrev;
        return 1;
    }
    
    private void updateOptStateAndReps() {
        int n = this.opts[this.optCur].optPrev;
        assert n < this.optCur;
        if (this.opts[this.optCur].prev1IsLiteral) {
            --n;
            if (this.opts[this.optCur].hasPrev2) {
                this.opts[this.optCur].state.set(this.opts[this.opts[this.optCur].optPrev2].state);
                if (this.opts[this.optCur].backPrev2 < 4) {
                    this.opts[this.optCur].state.updateLongRep();
                }
                else {
                    this.opts[this.optCur].state.updateMatch();
                }
            }
            else {
                this.opts[this.optCur].state.set(this.opts[n].state);
            }
            this.opts[this.optCur].state.updateLiteral();
        }
        else {
            this.opts[this.optCur].state.set(this.opts[n].state);
        }
        if (n == this.optCur - 1) {
            assert this.opts[this.optCur].backPrev == -1;
            if (this.opts[this.optCur].backPrev == 0) {
                this.opts[this.optCur].state.updateShortRep();
            }
            else {
                this.opts[this.optCur].state.updateLiteral();
            }
            System.arraycopy(this.opts[n].reps, 0, this.opts[this.optCur].reps, 0, 4);
        }
        else {
            int n2;
            if (this.opts[this.optCur].prev1IsLiteral && this.opts[this.optCur].hasPrev2) {
                n = this.opts[this.optCur].optPrev2;
                n2 = this.opts[this.optCur].backPrev2;
                this.opts[this.optCur].state.updateLongRep();
            }
            else {
                n2 = this.opts[this.optCur].backPrev;
                if (n2 < 4) {
                    this.opts[this.optCur].state.updateLongRep();
                }
                else {
                    this.opts[this.optCur].state.updateMatch();
                }
            }
            if (n2 < 4) {
                this.opts[this.optCur].reps[0] = this.opts[n].reps[n2];
                int i;
                for (i = 1; i <= n2; ++i) {
                    this.opts[this.optCur].reps[i] = this.opts[n].reps[i - 1];
                }
                while (i < 4) {
                    this.opts[this.optCur].reps[i] = this.opts[n].reps[i];
                    ++i;
                }
            }
            else {
                this.opts[this.optCur].reps[0] = n2 - 4;
                System.arraycopy(this.opts[n].reps, 0, this.opts[this.optCur].reps, 1, 3);
            }
        }
    }
    
    private void calc1BytePrices(final int n, final int n2, final int n3, final int n4) {
        boolean b = false;
        final int byte1 = this.lz.getByte(0);
        final int byte2 = this.lz.getByte(this.opts[this.optCur].reps[0] + 1);
        final int n5 = this.opts[this.optCur].price + this.literalEncoder.getPrice(byte1, byte2, this.lz.getByte(1), n, this.opts[this.optCur].state);
        if (n5 < this.opts[this.optCur + 1].price) {
            this.opts[this.optCur + 1].set1(n5, this.optCur, -1);
            b = true;
        }
        if (byte2 == byte1 && (this.opts[this.optCur + 1].optPrev == this.optCur || this.opts[this.optCur + 1].backPrev != 0)) {
            final int shortRepPrice = this.getShortRepPrice(n4, this.opts[this.optCur].state, n2);
            if (shortRepPrice <= this.opts[this.optCur + 1].price) {
                this.opts[this.optCur + 1].set1(shortRepPrice, this.optCur, 0);
                b = true;
            }
        }
        if (!b && byte2 != byte1 && n3 > 2) {
            final int matchLen = this.lz.getMatchLen(1, this.opts[this.optCur].reps[0], Math.min(this.niceLen, n3 - 1));
            if (matchLen >= 2) {
                this.nextState.set(this.opts[this.optCur].state);
                this.nextState.updateLiteral();
                final int n6 = n5 + this.getLongRepAndLenPrice(0, matchLen, this.nextState, n + 1 & this.posMask);
                final int n7 = this.optCur + 1 + matchLen;
                while (this.optEnd < n7) {
                    this.opts[++this.optEnd].reset();
                }
                if (n6 < this.opts[n7].price) {
                    this.opts[n7].set2(n6, this.optCur, 0);
                }
            }
        }
    }
    
    private int calcLongRepPrices(final int n, final int n2, final int n3, final int n4) {
        int n5 = 2;
        final int min = Math.min(n3, this.niceLen);
        for (int i = 0; i < 4; ++i) {
            final int matchLen = this.lz.getMatchLen(this.opts[this.optCur].reps[i], min);
            if (matchLen >= 2) {
                while (this.optEnd < this.optCur + matchLen) {
                    this.opts[++this.optEnd].reset();
                }
                final int longRepPrice = this.getLongRepPrice(n4, i, this.opts[this.optCur].state, n2);
                for (int j = matchLen; j >= 2; --j) {
                    final int n6 = longRepPrice + this.repLenEncoder.getPrice(j, n2);
                    if (n6 < this.opts[this.optCur + j].price) {
                        this.opts[this.optCur + j].set1(n6, this.optCur, i);
                    }
                }
                if (i == 0) {
                    n5 = matchLen + 1;
                }
                final int matchLen2 = this.lz.getMatchLen(matchLen + 1, this.opts[this.optCur].reps[i], Math.min(this.niceLen, n3 - matchLen - 1));
                if (matchLen2 >= 2) {
                    final int n7 = longRepPrice + this.repLenEncoder.getPrice(matchLen, n2);
                    this.nextState.set(this.opts[this.optCur].state);
                    this.nextState.updateLongRep();
                    final int n8 = n7 + this.literalEncoder.getPrice(this.lz.getByte(matchLen, 0), this.lz.getByte(0), this.lz.getByte(matchLen, 1), n + matchLen, this.nextState);
                    this.nextState.updateLiteral();
                    final int n9 = n8 + this.getLongRepAndLenPrice(0, matchLen2, this.nextState, n + matchLen + 1 & this.posMask);
                    final int n10 = this.optCur + matchLen + 1 + matchLen2;
                    while (this.optEnd < n10) {
                        this.opts[++this.optEnd].reset();
                    }
                    if (n9 < this.opts[n10].price) {
                        this.opts[n10].set3(n9, this.optCur, i, matchLen, 0);
                    }
                }
            }
        }
        return n5;
    }
    
    private void calcNormalMatchPrices(final int n, final int n2, final int n3, final int n4, final int i) {
        if (this.matches.len[this.matches.count - 1] > n3) {
            this.matches.count = 0;
            while (this.matches.len[this.matches.count] < n3) {
                final Matches matches = this.matches;
                ++matches.count;
            }
            this.matches.len[this.matches.count++] = n3;
        }
        if (this.matches.len[this.matches.count - 1] < i) {
            return;
        }
        while (this.optEnd < this.optCur + this.matches.len[this.matches.count - 1]) {
            this.opts[++this.optEnd].reset();
        }
        final int normalMatchPrice = this.getNormalMatchPrice(n4, this.opts[this.optCur].state);
        int n5;
        for (n5 = 0; i > this.matches.len[n5]; ++n5) {}
        int n6 = i;
        while (true) {
            final int n7 = this.matches.dist[n5];
            final int matchAndLenPrice = this.getMatchAndLenPrice(normalMatchPrice, n7, n6, n2);
            if (matchAndLenPrice < this.opts[this.optCur + n6].price) {
                this.opts[this.optCur + n6].set1(matchAndLenPrice, this.optCur, n7 + 4);
            }
            if (n6 == this.matches.len[n5]) {
                final int matchLen = this.lz.getMatchLen(n6 + 1, n7, Math.min(this.niceLen, n3 - n6 - 1));
                if (matchLen >= 2) {
                    this.nextState.set(this.opts[this.optCur].state);
                    this.nextState.updateMatch();
                    final int n8 = matchAndLenPrice + this.literalEncoder.getPrice(this.lz.getByte(n6, 0), this.lz.getByte(0), this.lz.getByte(n6, 1), n + n6, this.nextState);
                    this.nextState.updateLiteral();
                    final int n9 = n8 + this.getLongRepAndLenPrice(0, matchLen, this.nextState, n + n6 + 1 & this.posMask);
                    final int n10 = this.optCur + n6 + 1 + matchLen;
                    while (this.optEnd < n10) {
                        this.opts[++this.optEnd].reset();
                    }
                    if (n9 < this.opts[n10].price) {
                        this.opts[n10].set3(n9, this.optCur, n7 + 4, n6, 0);
                    }
                }
                if (++n5 == this.matches.count) {
                    break;
                }
            }
            ++n6;
        }
    }
}
