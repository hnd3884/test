package org.bouncycastle.crypto.tls;

class DTLSReplayWindow
{
    private static final long VALID_SEQ_MASK = 281474976710655L;
    private static final long WINDOW_SIZE = 64L;
    private long latestConfirmedSeq;
    private long bitmap;
    
    DTLSReplayWindow() {
        this.latestConfirmedSeq = -1L;
        this.bitmap = 0L;
    }
    
    boolean shouldDiscard(final long n) {
        if ((n & 0xFFFFFFFFFFFFL) != n) {
            return true;
        }
        if (n <= this.latestConfirmedSeq) {
            final long n2 = this.latestConfirmedSeq - n;
            if (n2 >= 64L) {
                return true;
            }
            if ((this.bitmap & 1L << (int)n2) != 0x0L) {
                return true;
            }
        }
        return false;
    }
    
    void reportAuthenticated(final long latestConfirmedSeq) {
        if ((latestConfirmedSeq & 0xFFFFFFFFFFFFL) != latestConfirmedSeq) {
            throw new IllegalArgumentException("'seq' out of range");
        }
        if (latestConfirmedSeq <= this.latestConfirmedSeq) {
            final long n = this.latestConfirmedSeq - latestConfirmedSeq;
            if (n < 64L) {
                this.bitmap |= 1L << (int)n;
            }
        }
        else {
            final long n2 = latestConfirmedSeq - this.latestConfirmedSeq;
            if (n2 >= 64L) {
                this.bitmap = 1L;
            }
            else {
                this.bitmap <<= (int)n2;
                this.bitmap |= 0x1L;
            }
            this.latestConfirmedSeq = latestConfirmedSeq;
        }
    }
    
    void reset() {
        this.latestConfirmedSeq = -1L;
        this.bitmap = 0L;
    }
}
