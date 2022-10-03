package org.apache.poi.hssf.record.common;

import org.apache.poi.util.Removal;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.common.Duplicatable;

public final class FtrHeader implements Duplicatable
{
    private short recordType;
    private short grbitFrt;
    private CellRangeAddress associatedRange;
    
    public FtrHeader() {
        this.associatedRange = new CellRangeAddress(0, 0, 0, 0);
    }
    
    public FtrHeader(final FtrHeader other) {
        this.recordType = other.recordType;
        this.grbitFrt = other.grbitFrt;
        this.associatedRange = other.associatedRange.copy();
    }
    
    public FtrHeader(final RecordInputStream in) {
        this.recordType = in.readShort();
        this.grbitFrt = in.readShort();
        this.associatedRange = new CellRangeAddress(in);
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append(" [FUTURE HEADER]\n");
        buffer.append("   Type " + this.recordType);
        buffer.append("   Flags " + this.grbitFrt);
        buffer.append(" [/FUTURE HEADER]\n");
        return buffer.toString();
    }
    
    public void serialize(final LittleEndianOutput out) {
        out.writeShort(this.recordType);
        out.writeShort(this.grbitFrt);
        this.associatedRange.serialize(out);
    }
    
    public static int getDataSize() {
        return 12;
    }
    
    public short getRecordType() {
        return this.recordType;
    }
    
    public void setRecordType(final short recordType) {
        this.recordType = recordType;
    }
    
    public short getGrbitFrt() {
        return this.grbitFrt;
    }
    
    public void setGrbitFrt(final short grbitFrt) {
        this.grbitFrt = grbitFrt;
    }
    
    public CellRangeAddress getAssociatedRange() {
        return this.associatedRange;
    }
    
    public void setAssociatedRange(final CellRangeAddress associatedRange) {
        this.associatedRange = associatedRange;
    }
    
    @Deprecated
    @Removal(version = "5.0.0")
    public FtrHeader clone() {
        return this.copy();
    }
    
    @Override
    public FtrHeader copy() {
        return new FtrHeader(this);
    }
}
