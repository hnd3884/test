package org.apache.poi.hssf.record.chart;

import org.apache.poi.util.BitFieldFactory;
import org.apache.poi.common.Duplicatable;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.util.Removal;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.util.HexDump;
import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.util.BitField;
import org.apache.poi.hssf.record.StandardRecord;

public final class DataFormatRecord extends StandardRecord
{
    public static final short sid = 4102;
    private static final BitField useExcel4Colors;
    private short field_1_pointNumber;
    private short field_2_seriesIndex;
    private short field_3_seriesNumber;
    private short field_4_formatFlags;
    
    public DataFormatRecord() {
    }
    
    public DataFormatRecord(final DataFormatRecord other) {
        super(other);
        this.field_1_pointNumber = other.field_1_pointNumber;
        this.field_2_seriesIndex = other.field_2_seriesIndex;
        this.field_3_seriesNumber = other.field_3_seriesNumber;
        this.field_4_formatFlags = other.field_4_formatFlags;
    }
    
    public DataFormatRecord(final RecordInputStream in) {
        this.field_1_pointNumber = in.readShort();
        this.field_2_seriesIndex = in.readShort();
        this.field_3_seriesNumber = in.readShort();
        this.field_4_formatFlags = in.readShort();
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("[DATAFORMAT]\n");
        buffer.append("    .pointNumber          = ").append("0x").append(HexDump.toHex(this.getPointNumber())).append(" (").append(this.getPointNumber()).append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("    .seriesIndex          = ").append("0x").append(HexDump.toHex(this.getSeriesIndex())).append(" (").append(this.getSeriesIndex()).append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("    .seriesNumber         = ").append("0x").append(HexDump.toHex(this.getSeriesNumber())).append(" (").append(this.getSeriesNumber()).append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("    .formatFlags          = ").append("0x").append(HexDump.toHex(this.getFormatFlags())).append(" (").append(this.getFormatFlags()).append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("         .useExcel4Colors          = ").append(this.isUseExcel4Colors()).append('\n');
        buffer.append("[/DATAFORMAT]\n");
        return buffer.toString();
    }
    
    public void serialize(final LittleEndianOutput out) {
        out.writeShort(this.field_1_pointNumber);
        out.writeShort(this.field_2_seriesIndex);
        out.writeShort(this.field_3_seriesNumber);
        out.writeShort(this.field_4_formatFlags);
    }
    
    @Override
    protected int getDataSize() {
        return 8;
    }
    
    @Override
    public short getSid() {
        return 4102;
    }
    
    @Deprecated
    @Removal(version = "5.0.0")
    public DataFormatRecord clone() {
        return this.copy();
    }
    
    @Override
    public DataFormatRecord copy() {
        return new DataFormatRecord(this);
    }
    
    public short getPointNumber() {
        return this.field_1_pointNumber;
    }
    
    public void setPointNumber(final short field_1_pointNumber) {
        this.field_1_pointNumber = field_1_pointNumber;
    }
    
    public short getSeriesIndex() {
        return this.field_2_seriesIndex;
    }
    
    public void setSeriesIndex(final short field_2_seriesIndex) {
        this.field_2_seriesIndex = field_2_seriesIndex;
    }
    
    public short getSeriesNumber() {
        return this.field_3_seriesNumber;
    }
    
    public void setSeriesNumber(final short field_3_seriesNumber) {
        this.field_3_seriesNumber = field_3_seriesNumber;
    }
    
    public short getFormatFlags() {
        return this.field_4_formatFlags;
    }
    
    public void setFormatFlags(final short field_4_formatFlags) {
        this.field_4_formatFlags = field_4_formatFlags;
    }
    
    public void setUseExcel4Colors(final boolean value) {
        this.field_4_formatFlags = DataFormatRecord.useExcel4Colors.setShortBoolean(this.field_4_formatFlags, value);
    }
    
    public boolean isUseExcel4Colors() {
        return DataFormatRecord.useExcel4Colors.isSet(this.field_4_formatFlags);
    }
    
    static {
        useExcel4Colors = BitFieldFactory.getInstance(1);
    }
}
