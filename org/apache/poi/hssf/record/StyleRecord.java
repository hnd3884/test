package org.apache.poi.hssf.record;

import org.apache.poi.util.BitFieldFactory;
import org.apache.poi.common.Duplicatable;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndianInput;
import org.apache.poi.util.StringUtil;
import org.apache.poi.util.RecordFormatException;
import org.apache.poi.util.BitField;

public final class StyleRecord extends StandardRecord
{
    public static final short sid = 659;
    private static final BitField styleIndexMask;
    private static final BitField isBuiltinFlag;
    private int field_1_xf_index;
    private int field_2_builtin_style;
    private int field_3_outline_style_level;
    private boolean field_3_stringHasMultibyte;
    private String field_4_name;
    
    public StyleRecord() {
        this.field_1_xf_index = StyleRecord.isBuiltinFlag.set(0);
    }
    
    public StyleRecord(final StyleRecord other) {
        super(other);
        this.field_1_xf_index = other.field_1_xf_index;
        this.field_2_builtin_style = other.field_2_builtin_style;
        this.field_3_outline_style_level = other.field_3_outline_style_level;
        this.field_3_stringHasMultibyte = other.field_3_stringHasMultibyte;
        this.field_4_name = other.field_4_name;
    }
    
    public StyleRecord(final RecordInputStream in) {
        this.field_1_xf_index = in.readShort();
        if (this.isBuiltin()) {
            this.field_2_builtin_style = in.readByte();
            this.field_3_outline_style_level = in.readByte();
        }
        else {
            final int field_2_name_length = in.readShort();
            if (in.remaining() < 1) {
                if (field_2_name_length != 0) {
                    throw new RecordFormatException("Ran out of data reading style record");
                }
                this.field_4_name = "";
            }
            else {
                this.field_3_stringHasMultibyte = (in.readByte() != 0);
                if (this.field_3_stringHasMultibyte) {
                    this.field_4_name = StringUtil.readUnicodeLE(in, field_2_name_length);
                }
                else {
                    this.field_4_name = StringUtil.readCompressedUnicode(in, field_2_name_length);
                }
            }
        }
    }
    
    public void setXFIndex(final int xfIndex) {
        this.field_1_xf_index = StyleRecord.styleIndexMask.setValue(this.field_1_xf_index, xfIndex);
    }
    
    public int getXFIndex() {
        return StyleRecord.styleIndexMask.getValue(this.field_1_xf_index);
    }
    
    public void setName(final String name) {
        this.field_4_name = name;
        this.field_3_stringHasMultibyte = StringUtil.hasMultibyte(name);
        this.field_1_xf_index = StyleRecord.isBuiltinFlag.clear(this.field_1_xf_index);
    }
    
    public void setBuiltinStyle(final int builtinStyleId) {
        this.field_1_xf_index = StyleRecord.isBuiltinFlag.set(this.field_1_xf_index);
        this.field_2_builtin_style = builtinStyleId;
    }
    
    public void setOutlineStyleLevel(final int level) {
        this.field_3_outline_style_level = (level & 0xFF);
    }
    
    public boolean isBuiltin() {
        return StyleRecord.isBuiltinFlag.isSet(this.field_1_xf_index);
    }
    
    public String getName() {
        return this.field_4_name;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("[STYLE]\n");
        sb.append("    .xf_index_raw =").append(HexDump.shortToHex(this.field_1_xf_index)).append("\n");
        sb.append("        .type     =").append(this.isBuiltin() ? "built-in" : "user-defined").append("\n");
        sb.append("        .xf_index =").append(HexDump.shortToHex(this.getXFIndex())).append("\n");
        if (this.isBuiltin()) {
            sb.append("    .builtin_style=").append(HexDump.byteToHex(this.field_2_builtin_style)).append("\n");
            sb.append("    .outline_level=").append(HexDump.byteToHex(this.field_3_outline_style_level)).append("\n");
        }
        else {
            sb.append("    .name        =").append(this.getName()).append("\n");
        }
        sb.append("[/STYLE]\n");
        return sb.toString();
    }
    
    @Override
    protected int getDataSize() {
        if (this.isBuiltin()) {
            return 4;
        }
        return 5 + this.field_4_name.length() * (this.field_3_stringHasMultibyte ? 2 : 1);
    }
    
    public void serialize(final LittleEndianOutput out) {
        out.writeShort(this.field_1_xf_index);
        if (this.isBuiltin()) {
            out.writeByte(this.field_2_builtin_style);
            out.writeByte(this.field_3_outline_style_level);
        }
        else {
            out.writeShort(this.field_4_name.length());
            out.writeByte(this.field_3_stringHasMultibyte ? 1 : 0);
            if (this.field_3_stringHasMultibyte) {
                StringUtil.putUnicodeLE(this.getName(), out);
            }
            else {
                StringUtil.putCompressedUnicode(this.getName(), out);
            }
        }
    }
    
    @Override
    public short getSid() {
        return 659;
    }
    
    @Override
    public StyleRecord copy() {
        return new StyleRecord(this);
    }
    
    static {
        styleIndexMask = BitFieldFactory.getInstance(4095);
        isBuiltinFlag = BitFieldFactory.getInstance(32768);
    }
}
