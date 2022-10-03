package com.sun.corba.se.impl.encoding;

import com.sun.corba.se.spi.ior.iiop.GIOPVersion;

public class CDRInputStream_1_1 extends CDRInputStream_1_0
{
    protected int fragmentOffset;
    
    public CDRInputStream_1_1() {
        this.fragmentOffset = 0;
    }
    
    @Override
    public GIOPVersion getGIOPVersion() {
        return GIOPVersion.V1_1;
    }
    
    @Override
    public CDRInputStreamBase dup() {
        final CDRInputStreamBase dup = super.dup();
        ((CDRInputStream_1_1)dup).fragmentOffset = this.fragmentOffset;
        return dup;
    }
    
    @Override
    protected int get_offset() {
        return this.bbwi.position() + this.fragmentOffset;
    }
    
    @Override
    protected void alignAndCheck(final int n, final int n2) {
        this.checkBlockLength(n, n2);
        int n3 = this.computeAlignment(this.bbwi.position(), n);
        if (this.bbwi.position() + n2 + n3 > this.bbwi.buflen) {
            if (this.bbwi.position() + n3 == this.bbwi.buflen) {
                this.bbwi.position(this.bbwi.position() + n3);
            }
            this.grow(n, n2);
            n3 = this.computeAlignment(this.bbwi.position(), n);
        }
        this.bbwi.position(this.bbwi.position() + n3);
    }
    
    @Override
    protected void grow(final int n, final int needed) {
        this.bbwi.needed = needed;
        final int position = this.bbwi.position();
        this.bbwi = this.bufferManagerRead.underflow(this.bbwi);
        if (this.bbwi.fragmented) {
            this.fragmentOffset += position - this.bbwi.position();
            this.markAndResetHandler.fragmentationOccured(this.bbwi);
            this.bbwi.fragmented = false;
        }
    }
    
    @Override
    public Object createStreamMemento() {
        return new FragmentableStreamMemento();
    }
    
    @Override
    public void restoreInternalState(final Object o) {
        super.restoreInternalState(o);
        this.fragmentOffset = ((FragmentableStreamMemento)o).fragmentOffset_;
    }
    
    @Override
    public char read_wchar() {
        this.alignAndCheck(2, 2);
        final char[] convertedChars = this.getConvertedChars(2, this.getWCharConverter());
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
        final char[] convertedChars = this.getConvertedChars((read_long - 1) * 2, this.getWCharConverter());
        this.read_short();
        return new String(convertedChars, 0, this.getWCharConverter().getNumChars());
    }
    
    private class FragmentableStreamMemento extends StreamMemento
    {
        private int fragmentOffset_;
        
        public FragmentableStreamMemento() {
            this.fragmentOffset_ = CDRInputStream_1_1.this.fragmentOffset;
        }
    }
}
