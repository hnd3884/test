package org.apache.poi.hssf.record.chart;

import org.apache.poi.common.Duplicatable;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.util.Removal;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.util.HexDump;
import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.hssf.record.StandardRecord;

public final class DefaultDataLabelTextPropertiesRecord extends StandardRecord
{
    public static final short sid = 4132;
    public static final short CATEGORY_DATA_TYPE_SHOW_LABELS_CHARACTERISTIC = 0;
    public static final short CATEGORY_DATA_TYPE_VALUE_AND_PERCENTAGE_CHARACTERISTIC = 1;
    public static final short CATEGORY_DATA_TYPE_ALL_TEXT_CHARACTERISTIC = 2;
    private short field_1_categoryDataType;
    
    public DefaultDataLabelTextPropertiesRecord() {
    }
    
    public DefaultDataLabelTextPropertiesRecord(final DefaultDataLabelTextPropertiesRecord other) {
        super(other);
        this.field_1_categoryDataType = other.field_1_categoryDataType;
    }
    
    public DefaultDataLabelTextPropertiesRecord(final RecordInputStream in) {
        this.field_1_categoryDataType = in.readShort();
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("[DEFAULTTEXT]\n");
        buffer.append("    .categoryDataType     = ").append("0x").append(HexDump.toHex(this.getCategoryDataType())).append(" (").append(this.getCategoryDataType()).append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("[/DEFAULTTEXT]\n");
        return buffer.toString();
    }
    
    public void serialize(final LittleEndianOutput out) {
        out.writeShort(this.field_1_categoryDataType);
    }
    
    @Override
    protected int getDataSize() {
        return 2;
    }
    
    @Override
    public short getSid() {
        return 4132;
    }
    
    @Deprecated
    @Removal(version = "5.0.0")
    public DefaultDataLabelTextPropertiesRecord clone() {
        return this.copy();
    }
    
    @Override
    public DefaultDataLabelTextPropertiesRecord copy() {
        return new DefaultDataLabelTextPropertiesRecord(this);
    }
    
    public short getCategoryDataType() {
        return this.field_1_categoryDataType;
    }
    
    public void setCategoryDataType(final short field_1_categoryDataType) {
        this.field_1_categoryDataType = field_1_categoryDataType;
    }
}
