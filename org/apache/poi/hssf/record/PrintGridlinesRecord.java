package org.apache.poi.hssf.record;

import org.apache.poi.common.Duplicatable;
import org.apache.poi.util.Removal;
import org.apache.poi.util.LittleEndianOutput;

public final class PrintGridlinesRecord extends StandardRecord
{
    public static final short sid = 43;
    private short field_1_print_gridlines;
    
    public PrintGridlinesRecord() {
    }
    
    public PrintGridlinesRecord(final PrintGridlinesRecord other) {
        super(other);
        this.field_1_print_gridlines = other.field_1_print_gridlines;
    }
    
    public PrintGridlinesRecord(final RecordInputStream in) {
        this.field_1_print_gridlines = in.readShort();
    }
    
    public void setPrintGridlines(final boolean pg) {
        this.field_1_print_gridlines = (short)(pg ? 1 : 0);
    }
    
    public boolean getPrintGridlines() {
        return this.field_1_print_gridlines == 1;
    }
    
    @Override
    public String toString() {
        return "[PRINTGRIDLINES]\n    .printgridlines = " + this.getPrintGridlines() + "\n[/PRINTGRIDLINES]\n";
    }
    
    public void serialize(final LittleEndianOutput out) {
        out.writeShort(this.field_1_print_gridlines);
    }
    
    @Override
    protected int getDataSize() {
        return 2;
    }
    
    @Override
    public short getSid() {
        return 43;
    }
    
    @Deprecated
    @Removal(version = "5.0.0")
    public PrintGridlinesRecord clone() {
        return this.copy();
    }
    
    @Override
    public PrintGridlinesRecord copy() {
        return new PrintGridlinesRecord(this);
    }
}
