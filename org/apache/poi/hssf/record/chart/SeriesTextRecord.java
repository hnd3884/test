package org.apache.poi.hssf.record.chart;

import org.apache.poi.common.Duplicatable;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.util.Removal;
import org.apache.poi.util.StringUtil;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.util.HexDump;
import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.hssf.record.StandardRecord;

public final class SeriesTextRecord extends StandardRecord
{
    public static final short sid = 4109;
    private static final int MAX_LEN = 255;
    private int field_1_id;
    private boolean is16bit;
    private String field_4_text;
    
    public SeriesTextRecord() {
        this.field_4_text = "";
        this.is16bit = false;
    }
    
    public SeriesTextRecord(final SeriesTextRecord other) {
        super(other);
        this.field_1_id = other.field_1_id;
        this.is16bit = other.is16bit;
        this.field_4_text = other.field_4_text;
    }
    
    public SeriesTextRecord(final RecordInputStream in) {
        this.field_1_id = in.readUShort();
        final int field_2_textLength = in.readUByte();
        this.is16bit = ((in.readUByte() & 0x1) != 0x0);
        if (this.is16bit) {
            this.field_4_text = in.readUnicodeLEString(field_2_textLength);
        }
        else {
            this.field_4_text = in.readCompressedUnicode(field_2_textLength);
        }
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("[SERIESTEXT]\n");
        sb.append("  .id     =").append(HexDump.shortToHex(this.getId())).append('\n');
        sb.append("  .textLen=").append(this.field_4_text.length()).append('\n');
        sb.append("  .is16bit=").append(this.is16bit).append('\n');
        sb.append("  .text   =").append(" (").append(this.getText()).append(" )").append('\n');
        sb.append("[/SERIESTEXT]\n");
        return sb.toString();
    }
    
    public void serialize(final LittleEndianOutput out) {
        out.writeShort(this.field_1_id);
        out.writeByte(this.field_4_text.length());
        if (this.is16bit) {
            out.writeByte(1);
            StringUtil.putUnicodeLE(this.field_4_text, out);
        }
        else {
            out.writeByte(0);
            StringUtil.putCompressedUnicode(this.field_4_text, out);
        }
    }
    
    @Override
    protected int getDataSize() {
        return 4 + this.field_4_text.length() * (this.is16bit ? 2 : 1);
    }
    
    @Override
    public short getSid() {
        return 4109;
    }
    
    @Deprecated
    @Removal(version = "5.0.0")
    public SeriesTextRecord clone() {
        return this.copy();
    }
    
    @Override
    public SeriesTextRecord copy() {
        return new SeriesTextRecord(this);
    }
    
    public int getId() {
        return this.field_1_id;
    }
    
    public void setId(final int id) {
        this.field_1_id = id;
    }
    
    public String getText() {
        return this.field_4_text;
    }
    
    public void setText(final String text) {
        if (text.length() > 255) {
            throw new IllegalArgumentException("Text is too long (" + text.length() + ">" + 255 + ")");
        }
        this.field_4_text = text;
        this.is16bit = StringUtil.hasMultibyte(text);
    }
}
