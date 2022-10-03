package org.apache.poi.hssf.record;

import org.apache.poi.common.Duplicatable;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.util.LittleEndianInput;
import org.apache.poi.util.StringUtil;

public final class NameCommentRecord extends StandardRecord
{
    public static final short sid = 2196;
    private final short field_1_record_type;
    private final short field_2_frt_cell_ref_flag;
    private final long field_3_reserved;
    private String field_6_name_text;
    private String field_7_comment_text;
    
    public NameCommentRecord(final NameCommentRecord other) {
        this.field_1_record_type = other.field_1_record_type;
        this.field_2_frt_cell_ref_flag = other.field_2_frt_cell_ref_flag;
        this.field_3_reserved = other.field_3_reserved;
        this.field_6_name_text = other.field_6_name_text;
        this.field_7_comment_text = other.field_7_comment_text;
    }
    
    public NameCommentRecord(final String name, final String comment) {
        this.field_1_record_type = 0;
        this.field_2_frt_cell_ref_flag = 0;
        this.field_3_reserved = 0L;
        this.field_6_name_text = name;
        this.field_7_comment_text = comment;
    }
    
    public NameCommentRecord(final RecordInputStream ris) {
        this.field_1_record_type = ris.readShort();
        this.field_2_frt_cell_ref_flag = ris.readShort();
        this.field_3_reserved = ris.readLong();
        final int field_4_name_length = ris.readShort();
        final int field_5_comment_length = ris.readShort();
        if (ris.readByte() == 0) {
            this.field_6_name_text = StringUtil.readCompressedUnicode(ris, field_4_name_length);
        }
        else {
            this.field_6_name_text = StringUtil.readUnicodeLE(ris, field_4_name_length);
        }
        if (ris.readByte() == 0) {
            this.field_7_comment_text = StringUtil.readCompressedUnicode(ris, field_5_comment_length);
        }
        else {
            this.field_7_comment_text = StringUtil.readUnicodeLE(ris, field_5_comment_length);
        }
    }
    
    public void serialize(final LittleEndianOutput out) {
        final int field_4_name_length = this.field_6_name_text.length();
        final int field_5_comment_length = this.field_7_comment_text.length();
        out.writeShort(this.field_1_record_type);
        out.writeShort(this.field_2_frt_cell_ref_flag);
        out.writeLong(this.field_3_reserved);
        out.writeShort(field_4_name_length);
        out.writeShort(field_5_comment_length);
        final boolean isNameMultiByte = StringUtil.hasMultibyte(this.field_6_name_text);
        out.writeByte(isNameMultiByte ? 1 : 0);
        if (isNameMultiByte) {
            StringUtil.putUnicodeLE(this.field_6_name_text, out);
        }
        else {
            StringUtil.putCompressedUnicode(this.field_6_name_text, out);
        }
        final boolean isCommentMultiByte = StringUtil.hasMultibyte(this.field_7_comment_text);
        out.writeByte(isCommentMultiByte ? 1 : 0);
        if (isCommentMultiByte) {
            StringUtil.putUnicodeLE(this.field_7_comment_text, out);
        }
        else {
            StringUtil.putCompressedUnicode(this.field_7_comment_text, out);
        }
    }
    
    @Override
    protected int getDataSize() {
        return 18 + (StringUtil.hasMultibyte(this.field_6_name_text) ? (this.field_6_name_text.length() * 2) : this.field_6_name_text.length()) + (StringUtil.hasMultibyte(this.field_7_comment_text) ? (this.field_7_comment_text.length() * 2) : this.field_7_comment_text.length());
    }
    
    @Override
    public short getSid() {
        return 2196;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("[NAMECMT]\n");
        sb.append("    .record type            = ").append(HexDump.shortToHex(this.field_1_record_type)).append("\n");
        sb.append("    .frt cell ref flag      = ").append(HexDump.byteToHex(this.field_2_frt_cell_ref_flag)).append("\n");
        sb.append("    .reserved               = ").append(this.field_3_reserved).append("\n");
        sb.append("    .name length            = ").append(this.field_6_name_text.length()).append("\n");
        sb.append("    .comment length         = ").append(this.field_7_comment_text.length()).append("\n");
        sb.append("    .name                   = ").append(this.field_6_name_text).append("\n");
        sb.append("    .comment                = ").append(this.field_7_comment_text).append("\n");
        sb.append("[/NAMECMT]\n");
        return sb.toString();
    }
    
    public String getNameText() {
        return this.field_6_name_text;
    }
    
    public void setNameText(final String newName) {
        this.field_6_name_text = newName;
    }
    
    public String getCommentText() {
        return this.field_7_comment_text;
    }
    
    public void setCommentText(final String comment) {
        this.field_7_comment_text = comment;
    }
    
    public short getRecordType() {
        return this.field_1_record_type;
    }
    
    @Override
    public NameCommentRecord copy() {
        return new NameCommentRecord(this);
    }
}
