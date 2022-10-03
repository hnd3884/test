package org.apache.poi.hssf.record;

import org.apache.poi.common.Duplicatable;
import org.apache.poi.util.Removal;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.ss.util.NumberToTextConverter;

public final class NumberRecord extends CellRecord
{
    public static final short sid = 515;
    private double field_4_value;
    
    public NumberRecord() {
    }
    
    public NumberRecord(final NumberRecord other) {
        super(other);
        this.field_4_value = other.field_4_value;
    }
    
    public NumberRecord(final RecordInputStream in) {
        super(in);
        this.field_4_value = in.readDouble();
    }
    
    public void setValue(final double value) {
        this.field_4_value = value;
    }
    
    public double getValue() {
        return this.field_4_value;
    }
    
    @Override
    protected String getRecordName() {
        return "NUMBER";
    }
    
    @Override
    protected void appendValueText(final StringBuilder sb) {
        sb.append("  .value= ").append(NumberToTextConverter.toText(this.field_4_value));
    }
    
    @Override
    protected void serializeValue(final LittleEndianOutput out) {
        out.writeDouble(this.getValue());
    }
    
    @Override
    protected int getValueDataSize() {
        return 8;
    }
    
    @Override
    public short getSid() {
        return 515;
    }
    
    @Deprecated
    @Removal(version = "5.0.0")
    public NumberRecord clone() {
        return this.copy();
    }
    
    @Override
    public NumberRecord copy() {
        return new NumberRecord(this);
    }
}
