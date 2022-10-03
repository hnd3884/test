package com.sun.corba.se.impl.encoding;

import org.omg.CORBA.CompletionStatus;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;

public class CDROutputStream_1_1 extends CDROutputStream_1_0
{
    protected int fragmentOffset;
    
    public CDROutputStream_1_1() {
        this.fragmentOffset = 0;
    }
    
    @Override
    protected void alignAndReserve(final int n, final int n2) {
        int n3 = this.computeAlignment(n);
        if (this.bbwi.position() + n2 + n3 > this.bbwi.buflen) {
            this.grow(n, n2);
            n3 = this.computeAlignment(n);
        }
        this.bbwi.position(this.bbwi.position() + n3);
    }
    
    @Override
    protected void grow(final int n, final int n2) {
        final int position = this.bbwi.position();
        super.grow(n, n2);
        if (this.bbwi.fragmented) {
            this.bbwi.fragmented = false;
            this.fragmentOffset += position - this.bbwi.position();
        }
    }
    
    @Override
    public int get_offset() {
        return this.bbwi.position() + this.fragmentOffset;
    }
    
    @Override
    public GIOPVersion getGIOPVersion() {
        return GIOPVersion.V1_1;
    }
    
    @Override
    public void write_wchar(final char c) {
        final CodeSetConversion.CTBConverter wCharConverter = this.getWCharConverter();
        wCharConverter.convert(c);
        if (wCharConverter.getNumBytes() != 2) {
            throw this.wrapper.badGiop11Ctb(CompletionStatus.COMPLETED_MAYBE);
        }
        this.alignAndReserve(wCharConverter.getAlignment(), wCharConverter.getNumBytes());
        this.parent.write_octet_array(wCharConverter.getBytes(), 0, wCharConverter.getNumBytes());
    }
    
    @Override
    public void write_wstring(final String s) {
        if (s == null) {
            throw this.wrapper.nullParam(CompletionStatus.COMPLETED_MAYBE);
        }
        this.write_long(s.length() + 1);
        final CodeSetConversion.CTBConverter wCharConverter = this.getWCharConverter();
        wCharConverter.convert(s);
        this.internalWriteOctetArray(wCharConverter.getBytes(), 0, wCharConverter.getNumBytes());
        this.write_short((short)0);
    }
}
