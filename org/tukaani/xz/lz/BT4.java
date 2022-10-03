package org.tukaani.xz.lz;

import org.tukaani.xz.ArrayCache;

final class BT4 extends LZEncoder
{
    private final Hash234 hash;
    private final int[] tree;
    private final Matches matches;
    private final int depthLimit;
    private final int cyclicSize;
    private int cyclicPos;
    private int lzPos;
    
    static int getMemoryUsage(final int n) {
        return Hash234.getMemoryUsage(n) + n / 128 + 10;
    }
    
    BT4(final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final ArrayCache arrayCache) {
        super(n, n2, n3, n4, n5, arrayCache);
        this.cyclicPos = -1;
        this.cyclicSize = n + 1;
        this.lzPos = this.cyclicSize;
        this.hash = new Hash234(n, arrayCache);
        this.tree = arrayCache.getIntArray(this.cyclicSize * 2, false);
        this.matches = new Matches(n4 - 1);
        this.depthLimit = ((n6 > 0) ? n6 : (16 + n4 / 2));
    }
    
    @Override
    public void putArraysToCache(final ArrayCache arrayCache) {
        arrayCache.putArray(this.tree);
        this.hash.putArraysToCache(arrayCache);
        super.putArraysToCache(arrayCache);
    }
    
    private int movePos() {
        final int movePos = this.movePos(this.niceLen, 4);
        if (movePos != 0) {
            if (++this.lzPos == Integer.MAX_VALUE) {
                final int n = Integer.MAX_VALUE - this.cyclicSize;
                this.hash.normalize(n);
                LZEncoder.normalize(this.tree, this.cyclicSize * 2, n);
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
                this.skip(niceLen, hash4Pos);
                return this.matches;
            }
        }
        if (n3 < 3) {
            n3 = 3;
        }
        int depthLimit = this.depthLimit;
        int n4 = (this.cyclicPos << 1) + 1;
        int n5 = this.cyclicPos << 1;
        int n6 = 0;
        int n7 = 0;
        while (true) {
            final int n8 = this.lzPos - hash4Pos;
            if (depthLimit-- == 0 || n8 >= this.cyclicSize) {
                this.tree[n4] = 0;
                this.tree[n5] = 0;
                return this.matches;
            }
            final int n9 = this.cyclicPos - n8 + ((n8 > this.cyclicPos) ? this.cyclicSize : 0) << 1;
            int min = Math.min(n6, n7);
            if (this.buf[this.readPos + min - n8] == this.buf[this.readPos + min]) {
                while (++min < matchLenMax && this.buf[this.readPos + min - n8] == this.buf[this.readPos + min]) {}
                if (min > n3) {
                    n3 = min;
                    this.matches.len[this.matches.count] = min;
                    this.matches.dist[this.matches.count] = n8 - 1;
                    final Matches matches = this.matches;
                    ++matches.count;
                    if (min >= niceLen) {
                        this.tree[n5] = this.tree[n9];
                        this.tree[n4] = this.tree[n9 + 1];
                        return this.matches;
                    }
                }
            }
            if ((this.buf[this.readPos + min - n8] & 0xFF) < (this.buf[this.readPos + min] & 0xFF)) {
                this.tree[n5] = hash4Pos;
                n5 = n9 + 1;
                hash4Pos = this.tree[n5];
                n7 = min;
            }
            else {
                this.tree[n4] = hash4Pos;
                n4 = n9;
                hash4Pos = this.tree[n4];
                n6 = min;
            }
        }
    }
    
    private void skip(final int n, int n2) {
        int depthLimit = this.depthLimit;
        int n3 = (this.cyclicPos << 1) + 1;
        int n4 = this.cyclicPos << 1;
        int n5 = 0;
        int n6 = 0;
        while (true) {
            final int n7 = this.lzPos - n2;
            if (depthLimit-- == 0 || n7 >= this.cyclicSize) {
                this.tree[n3] = 0;
                this.tree[n4] = 0;
                return;
            }
            final int n8 = this.cyclicPos - n7 + ((n7 > this.cyclicPos) ? this.cyclicSize : 0) << 1;
            int min = Math.min(n5, n6);
            Label_0208: {
                if (this.buf[this.readPos + min - n7] == this.buf[this.readPos + min]) {
                    while (++min != n) {
                        if (this.buf[this.readPos + min - n7] != this.buf[this.readPos + min]) {
                            break Label_0208;
                        }
                    }
                    this.tree[n4] = this.tree[n8];
                    this.tree[n3] = this.tree[n8 + 1];
                    return;
                }
            }
            if ((this.buf[this.readPos + min - n7] & 0xFF) < (this.buf[this.readPos + min] & 0xFF)) {
                this.tree[n4] = n2;
                n4 = n8 + 1;
                n2 = this.tree[n4];
                n6 = min;
            }
            else {
                this.tree[n3] = n2;
                n3 = n8;
                n2 = this.tree[n3];
                n5 = min;
            }
        }
    }
    
    @Override
    public void skip(int n) {
        while (n-- > 0) {
            int niceLen = this.niceLen;
            final int movePos = this.movePos();
            if (movePos < niceLen) {
                if (movePos == 0) {
                    continue;
                }
                niceLen = movePos;
            }
            this.hash.calcHashes(this.buf, this.readPos);
            final int hash4Pos = this.hash.getHash4Pos();
            this.hash.updateTables(this.lzPos);
            this.skip(niceLen, hash4Pos);
        }
    }
}
