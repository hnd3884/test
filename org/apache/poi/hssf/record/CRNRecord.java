package org.apache.poi.hssf.record;

import org.apache.poi.common.Duplicatable;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.util.LittleEndianInput;
import org.apache.poi.ss.formula.constant.ConstantValueParser;

public final class CRNRecord extends StandardRecord
{
    public static final short sid = 90;
    private int field_1_last_column_index;
    private int field_2_first_column_index;
    private int field_3_row_index;
    private Object[] field_4_constant_values;
    
    private CRNRecord() {
    }
    
    public CRNRecord(final CRNRecord other) {
        super(other);
        this.field_1_last_column_index = other.field_1_last_column_index;
        this.field_2_first_column_index = other.field_2_first_column_index;
        this.field_3_row_index = other.field_3_row_index;
        this.field_4_constant_values = (Object[])((other.field_4_constant_values == null) ? null : ((Object[])other.field_4_constant_values.clone()));
    }
    
    public CRNRecord(final RecordInputStream in) {
        this.field_1_last_column_index = in.readUByte();
        this.field_2_first_column_index = in.readUByte();
        this.field_3_row_index = in.readShort();
        final int nValues = this.field_1_last_column_index - this.field_2_first_column_index + 1;
        this.field_4_constant_values = ConstantValueParser.parse(in, nValues);
    }
    
    public int getNumberOfCRNs() {
        return this.field_1_last_column_index;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(this.getClass().getName()).append(" [CRN");
        sb.append(" rowIx=").append(this.field_3_row_index);
        sb.append(" firstColIx=").append(this.field_2_first_column_index);
        sb.append(" lastColIx=").append(this.field_1_last_column_index);
        sb.append("]");
        return sb.toString();
    }
    
    @Override
    protected int getDataSize() {
        return 4 + ConstantValueParser.getEncodedSize(this.field_4_constant_values);
    }
    
    public void serialize(final LittleEndianOutput out) {
        out.writeByte(this.field_1_last_column_index);
        out.writeByte(this.field_2_first_column_index);
        out.writeShort(this.field_3_row_index);
        ConstantValueParser.encode(out, this.field_4_constant_values);
    }
    
    @Override
    public short getSid() {
        return 90;
    }
    
    @Override
    public CRNRecord copy() {
        return new CRNRecord(this);
    }
}
