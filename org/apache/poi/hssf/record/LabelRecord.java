package org.apache.poi.hssf.record;

import org.apache.poi.util.POILogFactory;
import org.apache.poi.common.Duplicatable;
import org.apache.poi.util.Removal;
import org.apache.poi.util.RecordFormatException;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.POILogger;

public final class LabelRecord extends Record implements CellValueRecordInterface
{
    private static final POILogger logger;
    public static final short sid = 516;
    private int field_1_row;
    private short field_2_column;
    private short field_3_xf_index;
    private short field_4_string_len;
    private byte field_5_unicode_flag;
    private String field_6_value;
    
    public LabelRecord() {
    }
    
    public LabelRecord(final LabelRecord other) {
        super(other);
        this.field_1_row = other.field_1_row;
        this.field_2_column = other.field_2_column;
        this.field_3_xf_index = other.field_3_xf_index;
        this.field_4_string_len = other.field_4_string_len;
        this.field_5_unicode_flag = other.field_5_unicode_flag;
        this.field_6_value = other.field_6_value;
    }
    
    public LabelRecord(final RecordInputStream in) {
        this.field_1_row = in.readUShort();
        this.field_2_column = in.readShort();
        this.field_3_xf_index = in.readShort();
        this.field_4_string_len = in.readShort();
        this.field_5_unicode_flag = in.readByte();
        if (this.field_4_string_len > 0) {
            if (this.isUnCompressedUnicode()) {
                this.field_6_value = in.readUnicodeLEString(this.field_4_string_len);
            }
            else {
                this.field_6_value = in.readCompressedUnicode(this.field_4_string_len);
            }
        }
        else {
            this.field_6_value = "";
        }
        if (in.remaining() > 0) {
            LabelRecord.logger.log(3, "LabelRecord data remains: " + in.remaining() + " : " + HexDump.toHex(in.readRemainder()));
        }
    }
    
    @Override
    public int getRow() {
        return this.field_1_row;
    }
    
    @Override
    public short getColumn() {
        return this.field_2_column;
    }
    
    @Override
    public short getXFIndex() {
        return this.field_3_xf_index;
    }
    
    public short getStringLength() {
        return this.field_4_string_len;
    }
    
    public boolean isUnCompressedUnicode() {
        return (this.field_5_unicode_flag & 0x1) != 0x0;
    }
    
    public String getValue() {
        return this.field_6_value;
    }
    
    @Override
    public int serialize(final int offset, final byte[] data) {
        throw new RecordFormatException("Label Records are supported READ ONLY...convert to LabelSST");
    }
    
    @Override
    public int getRecordSize() {
        throw new RecordFormatException("Label Records are supported READ ONLY...convert to LabelSST");
    }
    
    @Override
    public short getSid() {
        return 516;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("[LABEL]\n");
        sb.append("    .row       = ").append(HexDump.shortToHex(this.getRow())).append("\n");
        sb.append("    .column    = ").append(HexDump.shortToHex(this.getColumn())).append("\n");
        sb.append("    .xfindex   = ").append(HexDump.shortToHex(this.getXFIndex())).append("\n");
        sb.append("    .string_len= ").append(HexDump.shortToHex(this.field_4_string_len)).append("\n");
        sb.append("    .unicode_flag= ").append(HexDump.byteToHex(this.field_5_unicode_flag)).append("\n");
        sb.append("    .value       = ").append(this.getValue()).append("\n");
        sb.append("[/LABEL]\n");
        return sb.toString();
    }
    
    @Override
    public void setColumn(final short col) {
    }
    
    @Override
    public void setRow(final int row) {
    }
    
    @Override
    public void setXFIndex(final short xf) {
    }
    
    @Deprecated
    @Removal(version = "5.0.0")
    public LabelRecord clone() {
        return this.copy();
    }
    
    @Override
    public LabelRecord copy() {
        return new LabelRecord(this);
    }
    
    static {
        logger = POILogFactory.getLogger(LabelRecord.class);
    }
}
