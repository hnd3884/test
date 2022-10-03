package com.lowagie.text.pdf.fonts.cmaps;

public class CodespaceRange
{
    private byte[] start;
    private byte[] end;
    
    public byte[] getEnd() {
        return this.end;
    }
    
    public void setEnd(final byte[] endBytes) {
        this.end = endBytes;
    }
    
    public byte[] getStart() {
        return this.start;
    }
    
    public void setStart(final byte[] startBytes) {
        this.start = startBytes;
    }
}
