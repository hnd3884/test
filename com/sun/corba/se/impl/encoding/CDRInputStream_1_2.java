package com.sun.corba.se.impl.encoding;

import com.sun.corba.se.spi.ior.iiop.GIOPVersion;

public class CDRInputStream_1_2 extends CDRInputStream_1_1
{
    protected boolean headerPadding;
    protected boolean restoreHeaderPadding;
    
    @Override
    void setHeaderPadding(final boolean headerPadding) {
        this.headerPadding = headerPadding;
    }
    
    @Override
    public void mark(final int n) {
        super.mark(n);
        this.restoreHeaderPadding = this.headerPadding;
    }
    
    @Override
    public void reset() {
        super.reset();
        this.headerPadding = this.restoreHeaderPadding;
        this.restoreHeaderPadding = false;
    }
    
    @Override
    public CDRInputStreamBase dup() {
        final CDRInputStreamBase dup = super.dup();
        ((CDRInputStream_1_2)dup).headerPadding = this.headerPadding;
        return dup;
    }
    
    @Override
    protected void alignAndCheck(final int n, final int n2) {
        if (this.headerPadding) {
            this.headerPadding = false;
            this.alignOnBoundary(8);
        }
        this.checkBlockLength(n, n2);
        this.bbwi.position(this.bbwi.position() + this.computeAlignment(this.bbwi.position(), n));
        if (this.bbwi.position() + n2 > this.bbwi.buflen) {
            this.grow(1, n2);
        }
    }
    
    @Override
    public GIOPVersion getGIOPVersion() {
        return GIOPVersion.V1_2;
    }
    
    @Override
    public char read_wchar() {
        final char[] convertedChars = this.getConvertedChars(this.read_octet(), this.getWCharConverter());
        if (this.getWCharConverter().getNumChars() > 1) {
            throw this.wrapper.btcResultMoreThanOneChar();
        }
        return convertedChars[0];
    }
    
    @Override
    public String read_wstring() {
        final int read_long = this.read_long();
        if (read_long == 0) {
            return new String("");
        }
        this.checkForNegativeLength(read_long);
        return new String(this.getConvertedChars(read_long, this.getWCharConverter()), 0, this.getWCharConverter().getNumChars());
    }
}
