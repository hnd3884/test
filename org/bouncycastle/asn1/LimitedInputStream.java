package org.bouncycastle.asn1;

import java.io.InputStream;

abstract class LimitedInputStream extends InputStream
{
    protected final InputStream _in;
    private int _limit;
    
    LimitedInputStream(final InputStream in, final int limit) {
        this._in = in;
        this._limit = limit;
    }
    
    int getRemaining() {
        return this._limit;
    }
    
    protected void setParentEofDetect(final boolean eofOn00) {
        if (this._in instanceof IndefiniteLengthInputStream) {
            ((IndefiniteLengthInputStream)this._in).setEofOn00(eofOn00);
        }
    }
}
