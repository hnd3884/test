package org.apache.poi.hssf.record;

import org.apache.poi.common.Duplicatable;
import org.apache.poi.util.Removal;

public final class HeaderRecord extends HeaderFooterBase
{
    public static final short sid = 20;
    
    public HeaderRecord(final String text) {
        super(text);
    }
    
    public HeaderRecord(final HeaderRecord other) {
        super(other);
    }
    
    public HeaderRecord(final RecordInputStream in) {
        super(in);
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("[HEADER]\n");
        buffer.append("    .header = ").append(this.getText()).append("\n");
        buffer.append("[/HEADER]\n");
        return buffer.toString();
    }
    
    @Override
    public short getSid() {
        return 20;
    }
    
    @Deprecated
    @Removal(version = "5.0.0")
    public HeaderRecord clone() {
        return this.copy();
    }
    
    @Override
    public HeaderRecord copy() {
        return new HeaderRecord(this);
    }
}
