package org.tukaani.xz.lz;

import org.tukaani.xz.ArrayCache;

final class HC4 extends LZEncoder
{
    private final Hash234 hash;
    private final int[] chain;
    private final Matches matches;
    private final int depthLimit;
    private final int cyclicSize;
    private int cyclicPos;
    private int lzPos;
    
    static int getMemoryUsage(final int n) {
        return Hash234.getMemoryUsage(n) + n / 256 + 10;
    }
    
    HC4(final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final ArrayCache arrayCache) {
        super(n, n2, n3, n4, n5, arrayCache);
        this.cyclicPos = -1;
        this.hash = new Hash234(n, arrayCache);
        this.cyclicSize = n + 1;
        this.chain = arrayCache.getIntArray(this.cyclicSize, false);
        this.lzPos = this.cyclicSize;
        this.matches = new Matches(n4 - 1);
        this.depthLimit = ((n6 > 0) ? n6 : (4 + n4 / 4));
    }
    
    @Override
    public void putArraysToCache(final ArrayCache arrayCache) {
        arrayCache.putArray(this.chain);
        this.hash.putArraysToCache(arrayCache);
        super.putArraysToCache(arrayCache);
    }
    
    private int movePos() {
        final int movePos = this.movePos(4, 4);
        if (movePos != 0) {
            if (++this.lzPos == Integer.MAX_VALUE) {
                final int n = Integer.MAX_VALUE - this.cyclicSize;
                this.hash.normalize(n);
                LZEncoder.normalize(this.chain, this.cyclicSize, n);
                this.lzPos -= n;
            }
            if (++this.cyclicPos == this.cyclicSize) {
                this.cyclicPos = 0;
            }
        }
        return movePos;
    }
    
    @Override
    public Matches getMatches() {
        this.matches.count = 0;
        int matchLenMax = this.matchLenMax;
        int niceLen = this.niceLen;
        final int movePos = this.movePos();
        if (movePos < matchLenMax) {
            if (movePos == 0) {
                return this.matches;
            }
            if (niceLen > (matchLenMax = movePos)) {
                niceLen = movePos;
            }
        }
        this.hash.calcHashes(this.buf, this.readPos);
        int n = this.lzPos - this.hash.getHash2Pos();
        final int n2 = this.lzPos - this.hash.getHash3Pos();
        int hash4Pos = this.hash.getHash4Pos();
        this.hash.updateTables(this.lzPos);
        this.chain[this.cyclicPos] = hash4Pos;
        int n3 = 0;
        if (n < this.cyclicSize && this.buf[this.readPos - n] == this.buf[this.readPos]) {
            n3 = 2;
            this.matches.len[0] = 2;
            this.matches.dist[0] = n - 1;
            this.matches.count = 1;
        }
        if (n != n2 && n2 < this.cyclicSize && this.buf[this.readPos - n2] == this.buf[this.readPos]) {
            n3 = 3;
            this.matches.dist[this.matches.count++] = n2 - 1;
            n = n2;
        }
        if (this.matches.count > 0) {
            while (n3 < matchLenMax && this.buf[this.readPos + n3 - n] == this.buf[this.readPos + n3]) {
                ++n3;
            }
            if ((this.matches.len[this.matches.count - 1] = n3) >= niceLen) {
                return this.matches;
            }
        }
        if (n3 < 3) {
            n3 = 3;
        }
        int depthLimit = this.depthLimit;
        while (true) {
            final int n4 = this.lzPos - hash4Pos;
            if (depthLimit-- == 0 || n4 >= this.cyclicSize) {
                return this.matches;
            }
            hash4Pos = this.chain[this.cyclicPos - n4 + ((n4 > this.cyclicPos) ? this.cyclicSize : 0)];
            if (this.buf[this.readPos + n3 - n4] != this.buf[this.readPos + n3] || this.buf[this.readPos - n4] != this.buf[this.readPos]) {
                continue;
            }
            int n5 = 0;
            while (++n5 < matchLenMax && this.buf[this.readPos + n5 - n4] == this.buf[this.readPos + n5]) {}
            if (n5 <= n3) {
                continue;
            }
            n3 = n5;
            this.matches.len[this.matches.count] = n5;
            this.matches.dist[this.matches.count] = n4 - 1;
            final Matches matches = this.matches;
            ++matches.count;
            if (n5 >= niceLen) {
                return this.matches;
            }
        }
    }
    
    @Override
    public void skip(int n) {
        assert n >= 0;
        while (n-- > 0) {
            if (this.movePos() != 0) {
                this.hash.calcHashes(this.buf, this.readPos);
                this.chain[this.cyclicPos] = this.hash.getHash4Pos();
                this.hash.updateTables(this.lzPos);
            }
        }
    }
}
