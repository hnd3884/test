package org.apache.poi.hssf.record;

import org.apache.poi.common.Duplicatable;
import org.apache.poi.util.Removal;

public final class FooterRecord extends HeaderFooterBase
{
    public static final short sid = 21;
    
    public FooterRecord(final String text) {
        super(text);
    }
    
    public FooterRecord(final FooterRecord other) {
        super(other);
    }
    
    public FooterRecord(final RecordInputStream in) {
        super(in);
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("[FOOTER]\n");
        buffer.append("    .footer = ").append(this.getText()).append("\n");
        buffer.append("[/FOOTER]\n");
        return buffer.toString();
    }
    
    @Override
    public short getSid() {
        return 21;
    }
    
    @Deprecated
    @Removal(version = "5.0.0")
    public FooterRecord clone() {
        return this.copy();
    }
    
    @Override
    public FooterRecord copy() {
        return new FooterRecord(this);
    }
}
