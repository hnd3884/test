package org.apache.poi.hssf.record;

import org.apache.poi.util.BitFieldFactory;
import org.apache.poi.common.Duplicatable;
import java.util.Objects;
import org.apache.poi.util.StringUtil;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.BitField;

public final class FontRecord extends StandardRecord
{
    public static final short sid = 49;
    public static final short SS_NONE = 0;
    public static final short SS_SUPER = 1;
    public static final short SS_SUB = 2;
    public static final byte U_NONE = 0;
    public static final byte U_SINGLE = 1;
    public static final byte U_DOUBLE = 2;
    public static final byte U_SINGLE_ACCOUNTING = 33;
    public static final byte U_DOUBLE_ACCOUNTING = 34;
    private static final BitField italic;
    private static final BitField strikeout;
    private static final BitField macoutline;
    private static final BitField macshadow;
    private short field_1_font_height;
    private short field_2_attributes;
    private short field_3_color_palette_index;
    private short field_4_bold_weight;
    private short field_5_super_sub_script;
    private byte field_6_underline;
    private byte field_7_family;
    private byte field_8_charset;
    private byte field_9_zero;
    private String field_11_font_name;
    
    public FontRecord() {
    }
    
    public FontRecord(final FontRecord other) {
        super(other);
        this.field_1_font_height = other.field_1_font_height;
        this.field_2_attributes = other.field_2_attributes;
        this.field_3_color_palette_index = other.field_3_color_palette_index;
        this.field_4_bold_weight = other.field_4_bold_weight;
        this.field_5_super_sub_script = other.field_5_super_sub_script;
        this.field_6_underline = other.field_6_underline;
        this.field_7_family = other.field_7_family;
        this.field_8_charset = other.field_8_charset;
        this.field_9_zero = other.field_9_zero;
        this.field_11_font_name = other.field_11_font_name;
    }
    
    public FontRecord(final RecordInputStream in) {
        this.field_1_font_height = in.readShort();
        this.field_2_attributes = in.readShort();
        this.field_3_color_palette_index = in.readShort();
        this.field_4_bold_weight = in.readShort();
        this.field_5_super_sub_script = in.readShort();
        this.field_6_underline = in.readByte();
        this.field_7_family = in.readByte();
        this.field_8_charset = in.readByte();
        this.field_9_zero = in.readByte();
        final int field_10_font_name_len = in.readUByte();
        final int unicodeFlags = in.readUByte();
        if (field_10_font_name_len > 0) {
            if (unicodeFlags == 0) {
                this.field_11_font_name = in.readCompressedUnicode(field_10_font_name_len);
            }
            else {
                this.field_11_font_name = in.readUnicodeLEString(field_10_font_name_len);
            }
        }
        else {
            this.field_11_font_name = "";
        }
    }
    
    public void setFontHeight(final short height) {
        this.field_1_font_height = height;
    }
    
    public void setAttributes(final short attributes) {
        this.field_2_attributes = attributes;
    }
    
    public void setItalic(final boolean italics) {
        this.field_2_attributes = FontRecord.italic.setShortBoolean(this.field_2_attributes, italics);
    }
    
    public void setStrikeout(final boolean strike) {
        this.field_2_attributes = FontRecord.strikeout.setShortBoolean(this.field_2_attributes, strike);
    }
    
    public void setMacoutline(final boolean mac) {
        this.field_2_attributes = FontRecord.macoutline.setShortBoolean(this.field_2_attributes, mac);
    }
    
    public void setMacshadow(final boolean mac) {
        this.field_2_attributes = FontRecord.macshadow.setShortBoolean(this.field_2_attributes, mac);
    }
    
    public void setColorPaletteIndex(final short cpi) {
        this.field_3_color_palette_index = cpi;
    }
    
    public void setBoldWeight(final short bw) {
        this.field_4_bold_weight = bw;
    }
    
    public void setSuperSubScript(final short sss) {
        this.field_5_super_sub_script = sss;
    }
    
    public void setUnderline(final byte u) {
        this.field_6_underline = u;
    }
    
    public void setFamily(final byte f) {
        this.field_7_family = f;
    }
    
    public void setCharset(final byte charset) {
        this.field_8_charset = charset;
    }
    
    public void setFontName(final String fn) {
        this.field_11_font_name = fn;
    }
    
    public short getFontHeight() {
        return this.field_1_font_height;
    }
    
    public short getAttributes() {
        return this.field_2_attributes;
    }
    
    public boolean isItalic() {
        return FontRecord.italic.isSet(this.field_2_attributes);
    }
    
    public boolean isStruckout() {
        return FontRecord.strikeout.isSet(this.field_2_attributes);
    }
    
    public boolean isMacoutlined() {
        return FontRecord.macoutline.isSet(this.field_2_attributes);
    }
    
    public boolean isMacshadowed() {
        return FontRecord.macshadow.isSet(this.field_2_attributes);
    }
    
    public short getColorPaletteIndex() {
        return this.field_3_color_palette_index;
    }
    
    public short getBoldWeight() {
        return this.field_4_bold_weight;
    }
    
    public short getSuperSubScript() {
        return this.field_5_super_sub_script;
    }
    
    public byte getUnderline() {
        return this.field_6_underline;
    }
    
    public byte getFamily() {
        return this.field_7_family;
    }
    
    public byte getCharset() {
        return this.field_8_charset;
    }
    
    public String getFontName() {
        return this.field_11_font_name;
    }
    
    @Override
    public String toString() {
        return "[FONT]\n    .fontheight    = " + HexDump.shortToHex(this.getFontHeight()) + "\n    .attributes    = " + HexDump.shortToHex(this.getAttributes()) + "\n       .italic     = " + this.isItalic() + "\n       .strikout   = " + this.isStruckout() + "\n       .macoutlined= " + this.isMacoutlined() + "\n       .macshadowed= " + this.isMacshadowed() + "\n    .colorpalette  = " + HexDump.shortToHex(this.getColorPaletteIndex()) + "\n    .boldweight    = " + HexDump.shortToHex(this.getBoldWeight()) + "\n    .supersubscript= " + HexDump.shortToHex(this.getSuperSubScript()) + "\n    .underline     = " + HexDump.byteToHex(this.getUnderline()) + "\n    .family        = " + HexDump.byteToHex(this.getFamily()) + "\n    .charset       = " + HexDump.byteToHex(this.getCharset()) + "\n    .fontname      = " + this.getFontName() + "\n[/FONT]\n";
    }
    
    public void serialize(final LittleEndianOutput out) {
        out.writeShort(this.getFontHeight());
        out.writeShort(this.getAttributes());
        out.writeShort(this.getColorPaletteIndex());
        out.writeShort(this.getBoldWeight());
        out.writeShort(this.getSuperSubScript());
        out.writeByte(this.getUnderline());
        out.writeByte(this.getFamily());
        out.writeByte(this.getCharset());
        out.writeByte(this.field_9_zero);
        final int fontNameLen = this.field_11_font_name.length();
        out.writeByte(fontNameLen);
        final boolean hasMultibyte = StringUtil.hasMultibyte(this.field_11_font_name);
        out.writeByte(hasMultibyte ? 1 : 0);
        if (fontNameLen > 0) {
            if (hasMultibyte) {
                StringUtil.putUnicodeLE(this.field_11_font_name, out);
            }
            else {
                StringUtil.putCompressedUnicode(this.field_11_font_name, out);
            }
        }
    }
    
    @Override
    protected int getDataSize() {
        final int size = 16;
        final int fontNameLen = this.field_11_font_name.length();
        if (fontNameLen < 1) {
            return size;
        }
        final boolean hasMultibyte = StringUtil.hasMultibyte(this.field_11_font_name);
        return size + fontNameLen * (hasMultibyte ? 2 : 1);
    }
    
    @Override
    public short getSid() {
        return 49;
    }
    
    public void cloneStyleFrom(final FontRecord source) {
        this.field_1_font_height = source.field_1_font_height;
        this.field_2_attributes = source.field_2_attributes;
        this.field_3_color_palette_index = source.field_3_color_palette_index;
        this.field_4_bold_weight = source.field_4_bold_weight;
        this.field_5_super_sub_script = source.field_5_super_sub_script;
        this.field_6_underline = source.field_6_underline;
        this.field_7_family = source.field_7_family;
        this.field_8_charset = source.field_8_charset;
        this.field_9_zero = source.field_9_zero;
        this.field_11_font_name = source.field_11_font_name;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(this.field_1_font_height, this.field_2_attributes, this.field_3_color_palette_index, this.field_4_bold_weight, this.field_5_super_sub_script, this.field_6_underline, this.field_7_family, this.field_8_charset, this.field_9_zero, this.field_11_font_name);
    }
    
    public boolean sameProperties(final FontRecord other) {
        return this.field_1_font_height == other.field_1_font_height && this.field_2_attributes == other.field_2_attributes && this.field_3_color_palette_index == other.field_3_color_palette_index && this.field_4_bold_weight == other.field_4_bold_weight && this.field_5_super_sub_script == other.field_5_super_sub_script && this.field_6_underline == other.field_6_underline && this.field_7_family == other.field_7_family && this.field_8_charset == other.field_8_charset && this.field_9_zero == other.field_9_zero && Objects.equals(this.field_11_font_name, other.field_11_font_name);
    }
    
    @Override
    public boolean equals(final Object o) {
        return o instanceof FontRecord && this.sameProperties((FontRecord)o);
    }
    
    @Override
    public FontRecord copy() {
        return new FontRecord(this);
    }
    
    static {
        italic = BitFieldFactory.getInstance(2);
        strikeout = BitFieldFactory.getInstance(8);
        macoutline = BitFieldFactory.getInstance(16);
        macshadow = BitFieldFactory.getInstance(32);
    }
}
