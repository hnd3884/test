package org.apache.poi.hssf.record;

import org.apache.poi.common.Duplicatable;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.util.HexDump;

public final class InterfaceHdrRecord extends StandardRecord
{
    public static final short sid = 225;
    public static final int CODEPAGE = 1200;
    private final int _codepage;
    
    public InterfaceHdrRecord(final InterfaceHdrRecord other) {
        super(other);
        this._codepage = other._codepage;
    }
    
    public InterfaceHdrRecord(final int codePage) {
        this._codepage = codePage;
    }
    
    public InterfaceHdrRecord(final RecordInputStream in) {
        this._codepage = in.readShort();
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("[INTERFACEHDR]\n");
        buffer.append("    .codepage = ").append(HexDump.shortToHex(this._codepage)).append("\n");
        buffer.append("[/INTERFACEHDR]\n");
        return buffer.toString();
    }
    
    public void serialize(final LittleEndianOutput out) {
        out.writeShort(this._codepage);
    }
    
    @Override
    protected int getDataSize() {
        return 2;
    }
    
    @Override
    public short getSid() {
        return 225;
    }
    
    @Override
    public InterfaceHdrRecord copy() {
        return new InterfaceHdrRecord(this);
    }
}
