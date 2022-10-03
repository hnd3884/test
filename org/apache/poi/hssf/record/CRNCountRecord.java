package org.apache.poi.hssf.record;

import org.apache.poi.common.Duplicatable;
import org.apache.poi.util.LittleEndianOutput;

public final class CRNCountRecord extends StandardRecord
{
    public static final short sid = 89;
    private static final short DATA_SIZE = 4;
    private int field_1_number_crn_records;
    private int field_2_sheet_table_index;
    
    private CRNCountRecord() {
    }
    
    public CRNCountRecord(final CRNCountRecord other) {
        super(other);
        this.field_1_number_crn_records = other.field_1_number_crn_records;
        this.field_2_sheet_table_index = other.field_2_sheet_table_index;
    }
    
    public CRNCountRecord(final RecordInputStream in) {
        this.field_1_number_crn_records = in.readShort();
        if (this.field_1_number_crn_records < 0) {
            this.field_1_number_crn_records = (short)(-this.field_1_number_crn_records);
        }
        this.field_2_sheet_table_index = in.readShort();
    }
    
    public int getNumberOfCRNs() {
        return this.field_1_number_crn_records;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(this.getClass().getName()).append(" [XCT");
        sb.append(" nCRNs=").append(this.field_1_number_crn_records);
        sb.append(" sheetIx=").append(this.field_2_sheet_table_index);
        sb.append("]");
        return sb.toString();
    }
    
    public void serialize(final LittleEndianOutput out) {
        out.writeShort((short)this.field_1_number_crn_records);
        out.writeShort((short)this.field_2_sheet_table_index);
    }
    
    @Override
    protected int getDataSize() {
        return 4;
    }
    
    @Override
    public short getSid() {
        return 89;
    }
    
    @Override
    public CRNCountRecord copy() {
        return new CRNCountRecord(this);
    }
}
