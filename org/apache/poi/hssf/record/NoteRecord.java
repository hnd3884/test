package org.apache.poi.hssf.record;

import org.apache.poi.common.Duplicatable;
import org.apache.poi.util.Removal;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.util.LittleEndianInput;
import org.apache.poi.util.StringUtil;

public final class NoteRecord extends StandardRecord
{
    public static final short sid = 28;
    public static final NoteRecord[] EMPTY_ARRAY;
    public static final short NOTE_HIDDEN = 0;
    public static final short NOTE_VISIBLE = 2;
    private static final Byte DEFAULT_PADDING;
    private int field_1_row;
    private int field_2_col;
    private short field_3_flags;
    private int field_4_shapeid;
    private boolean field_5_hasMultibyte;
    private String field_6_author;
    private Byte field_7_padding;
    
    public NoteRecord() {
        this.field_6_author = "";
        this.field_3_flags = 0;
        this.field_7_padding = NoteRecord.DEFAULT_PADDING;
    }
    
    public NoteRecord(final NoteRecord other) {
        super(other);
        this.field_1_row = other.field_1_row;
        this.field_2_col = other.field_2_col;
        this.field_3_flags = other.field_3_flags;
        this.field_4_shapeid = other.field_4_shapeid;
        this.field_5_hasMultibyte = other.field_5_hasMultibyte;
        this.field_6_author = other.field_6_author;
        this.field_7_padding = other.field_7_padding;
    }
    
    @Override
    public short getSid() {
        return 28;
    }
    
    public NoteRecord(final RecordInputStream in) {
        this.field_1_row = in.readUShort();
        this.field_2_col = in.readShort();
        this.field_3_flags = in.readShort();
        this.field_4_shapeid = in.readUShort();
        final int length = in.readShort();
        this.field_5_hasMultibyte = (in.readByte() != 0);
        if (this.field_5_hasMultibyte) {
            this.field_6_author = StringUtil.readUnicodeLE(in, length);
        }
        else {
            this.field_6_author = StringUtil.readCompressedUnicode(in, length);
        }
        if (in.available() == 1) {
            this.field_7_padding = in.readByte();
        }
        else if (in.available() == 2 && length == 0) {
            this.field_7_padding = in.readByte();
            in.readByte();
        }
    }
    
    public void serialize(final LittleEndianOutput out) {
        out.writeShort(this.field_1_row);
        out.writeShort(this.field_2_col);
        out.writeShort(this.field_3_flags);
        out.writeShort(this.field_4_shapeid);
        out.writeShort(this.field_6_author.length());
        out.writeByte(this.field_5_hasMultibyte ? 1 : 0);
        if (this.field_5_hasMultibyte) {
            StringUtil.putUnicodeLE(this.field_6_author, out);
        }
        else {
            StringUtil.putCompressedUnicode(this.field_6_author, out);
        }
        if (this.field_7_padding != null) {
            out.writeByte(this.field_7_padding);
        }
    }
    
    @Override
    protected int getDataSize() {
        return 11 + this.field_6_author.length() * (this.field_5_hasMultibyte ? 2 : 1) + ((this.field_7_padding != null) ? 1 : 0);
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("[NOTE]\n");
        buffer.append("    .row    = ").append(this.field_1_row).append("\n");
        buffer.append("    .col    = ").append(this.field_2_col).append("\n");
        buffer.append("    .flags  = ").append(this.field_3_flags).append("\n");
        buffer.append("    .shapeid= ").append(this.field_4_shapeid).append("\n");
        buffer.append("    .author = ").append(this.field_6_author).append("\n");
        buffer.append("[/NOTE]\n");
        return buffer.toString();
    }
    
    public int getRow() {
        return this.field_1_row;
    }
    
    public void setRow(final int row) {
        this.field_1_row = row;
    }
    
    public int getColumn() {
        return this.field_2_col;
    }
    
    public void setColumn(final int col) {
        this.field_2_col = col;
    }
    
    public short getFlags() {
        return this.field_3_flags;
    }
    
    public void setFlags(final short flags) {
        this.field_3_flags = flags;
    }
    
    protected boolean authorIsMultibyte() {
        return this.field_5_hasMultibyte;
    }
    
    public int getShapeId() {
        return this.field_4_shapeid;
    }
    
    public void setShapeId(final int id) {
        this.field_4_shapeid = id;
    }
    
    public String getAuthor() {
        return this.field_6_author;
    }
    
    public void setAuthor(final String author) {
        this.field_6_author = author;
        this.field_5_hasMultibyte = StringUtil.hasMultibyte(author);
    }
    
    @Deprecated
    @Removal(version = "5.0.0")
    public NoteRecord clone() {
        return this.copy();
    }
    
    @Override
    public NoteRecord copy() {
        return new NoteRecord(this);
    }
    
    static {
        EMPTY_ARRAY = new NoteRecord[0];
        DEFAULT_PADDING = 0;
    }
}
