package org.apache.poi.hssf.record;

import org.apache.poi.util.BitFieldFactory;
import org.apache.poi.common.Duplicatable;
import org.apache.poi.util.Removal;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.BitField;

public final class RowRecord extends StandardRecord
{
    public static final short sid = 520;
    public static final int ENCODED_SIZE = 20;
    private static final int OPTION_BITS_ALWAYS_SET = 256;
    private static final BitField outlineLevel;
    private static final BitField colapsed;
    private static final BitField zeroHeight;
    private static final BitField badFontHeight;
    private static final BitField formatted;
    private static final BitField xfIndex;
    private static final BitField topBorder;
    private static final BitField bottomBorder;
    private static final BitField phoeneticGuide;
    private int field_1_row_number;
    private int field_2_first_col;
    private int field_3_last_col;
    private short field_4_height;
    private short field_5_optimize;
    private short field_6_reserved;
    private int field_7_option_flags;
    private int field_8_option_flags;
    
    public RowRecord(final RowRecord other) {
        super(other);
        this.field_1_row_number = other.field_1_row_number;
        this.field_2_first_col = other.field_2_first_col;
        this.field_3_last_col = other.field_3_last_col;
        this.field_4_height = other.field_4_height;
        this.field_5_optimize = other.field_5_optimize;
        this.field_6_reserved = other.field_6_reserved;
        this.field_7_option_flags = other.field_7_option_flags;
        this.field_8_option_flags = other.field_8_option_flags;
    }
    
    public RowRecord(final int rowNumber) {
        if (rowNumber < 0) {
            throw new IllegalArgumentException("Invalid row number (" + rowNumber + ")");
        }
        this.field_1_row_number = rowNumber;
        this.field_4_height = 255;
        this.field_5_optimize = 0;
        this.field_6_reserved = 0;
        this.field_7_option_flags = 256;
        this.field_8_option_flags = 15;
        this.setEmpty();
    }
    
    public RowRecord(final RecordInputStream in) {
        this.field_1_row_number = in.readUShort();
        if (this.field_1_row_number < 0) {
            throw new IllegalArgumentException("Invalid row number " + this.field_1_row_number + " found in InputStream");
        }
        this.field_2_first_col = in.readShort();
        this.field_3_last_col = in.readShort();
        this.field_4_height = in.readShort();
        this.field_5_optimize = in.readShort();
        this.field_6_reserved = in.readShort();
        this.field_7_option_flags = in.readShort();
        this.field_8_option_flags = in.readShort();
    }
    
    public void setEmpty() {
        this.field_2_first_col = 0;
        this.field_3_last_col = 0;
    }
    
    public boolean isEmpty() {
        return (this.field_2_first_col | this.field_3_last_col) == 0x0;
    }
    
    public void setRowNumber(final int row) {
        this.field_1_row_number = row;
    }
    
    public void setFirstCol(final int col) {
        this.field_2_first_col = col;
    }
    
    public void setLastCol(final int col) {
        this.field_3_last_col = col;
    }
    
    public void setHeight(final short height) {
        this.field_4_height = height;
    }
    
    public void setOptimize(final short optimize) {
        this.field_5_optimize = optimize;
    }
    
    public void setOutlineLevel(final short ol) {
        this.field_7_option_flags = RowRecord.outlineLevel.setValue(this.field_7_option_flags, ol);
    }
    
    public void setColapsed(final boolean c) {
        this.field_7_option_flags = RowRecord.colapsed.setBoolean(this.field_7_option_flags, c);
    }
    
    public void setZeroHeight(final boolean z) {
        this.field_7_option_flags = RowRecord.zeroHeight.setBoolean(this.field_7_option_flags, z);
    }
    
    public void setBadFontHeight(final boolean f) {
        this.field_7_option_flags = RowRecord.badFontHeight.setBoolean(this.field_7_option_flags, f);
    }
    
    public void setFormatted(final boolean f) {
        this.field_7_option_flags = RowRecord.formatted.setBoolean(this.field_7_option_flags, f);
    }
    
    public void setXFIndex(final short index) {
        this.field_8_option_flags = RowRecord.xfIndex.setValue(this.field_8_option_flags, index);
    }
    
    public void setTopBorder(final boolean f) {
        this.field_8_option_flags = RowRecord.topBorder.setBoolean(this.field_8_option_flags, f);
    }
    
    public void setBottomBorder(final boolean f) {
        this.field_8_option_flags = RowRecord.bottomBorder.setBoolean(this.field_8_option_flags, f);
    }
    
    public void setPhoeneticGuide(final boolean f) {
        this.field_8_option_flags = RowRecord.phoeneticGuide.setBoolean(this.field_8_option_flags, f);
    }
    
    public int getRowNumber() {
        return this.field_1_row_number;
    }
    
    public int getFirstCol() {
        return this.field_2_first_col;
    }
    
    public int getLastCol() {
        return this.field_3_last_col;
    }
    
    public short getHeight() {
        return this.field_4_height;
    }
    
    public short getOptimize() {
        return this.field_5_optimize;
    }
    
    public short getOptionFlags() {
        return (short)this.field_7_option_flags;
    }
    
    public short getOutlineLevel() {
        return (short)RowRecord.outlineLevel.getValue(this.field_7_option_flags);
    }
    
    public boolean getColapsed() {
        return RowRecord.colapsed.isSet(this.field_7_option_flags);
    }
    
    public boolean getZeroHeight() {
        return RowRecord.zeroHeight.isSet(this.field_7_option_flags);
    }
    
    public boolean getBadFontHeight() {
        return RowRecord.badFontHeight.isSet(this.field_7_option_flags);
    }
    
    public boolean getFormatted() {
        return RowRecord.formatted.isSet(this.field_7_option_flags);
    }
    
    public short getOptionFlags2() {
        return (short)this.field_8_option_flags;
    }
    
    public short getXFIndex() {
        return RowRecord.xfIndex.getShortValue((short)this.field_8_option_flags);
    }
    
    public boolean getTopBorder() {
        return RowRecord.topBorder.isSet(this.field_8_option_flags);
    }
    
    public boolean getBottomBorder() {
        return RowRecord.bottomBorder.isSet(this.field_8_option_flags);
    }
    
    public boolean getPhoeneticGuide() {
        return RowRecord.phoeneticGuide.isSet(this.field_8_option_flags);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("[ROW]\n");
        sb.append("    .rownumber      = ").append(Integer.toHexString(this.getRowNumber())).append("\n");
        sb.append("    .firstcol       = ").append(HexDump.shortToHex(this.getFirstCol())).append("\n");
        sb.append("    .lastcol        = ").append(HexDump.shortToHex(this.getLastCol())).append("\n");
        sb.append("    .height         = ").append(HexDump.shortToHex(this.getHeight())).append("\n");
        sb.append("    .optimize       = ").append(HexDump.shortToHex(this.getOptimize())).append("\n");
        sb.append("    .reserved       = ").append(HexDump.shortToHex(this.field_6_reserved)).append("\n");
        sb.append("    .optionflags    = ").append(HexDump.shortToHex(this.getOptionFlags())).append("\n");
        sb.append("        .outlinelvl = ").append(Integer.toHexString(this.getOutlineLevel())).append("\n");
        sb.append("        .colapsed   = ").append(this.getColapsed()).append("\n");
        sb.append("        .zeroheight = ").append(this.getZeroHeight()).append("\n");
        sb.append("        .badfontheig= ").append(this.getBadFontHeight()).append("\n");
        sb.append("        .formatted  = ").append(this.getFormatted()).append("\n");
        sb.append("    .optionsflags2  = ").append(HexDump.shortToHex(this.getOptionFlags2())).append("\n");
        sb.append("        .xfindex       = ").append(Integer.toHexString(this.getXFIndex())).append("\n");
        sb.append("        .topBorder     = ").append(this.getTopBorder()).append("\n");
        sb.append("        .bottomBorder  = ").append(this.getBottomBorder()).append("\n");
        sb.append("        .phoeneticGuide= ").append(this.getPhoeneticGuide()).append("\n");
        sb.append("[/ROW]\n");
        return sb.toString();
    }
    
    public void serialize(final LittleEndianOutput out) {
        out.writeShort(this.getRowNumber());
        out.writeShort((this.getFirstCol() == -1) ? 0 : this.getFirstCol());
        out.writeShort((this.getLastCol() == -1) ? 0 : this.getLastCol());
        out.writeShort(this.getHeight());
        out.writeShort(this.getOptimize());
        out.writeShort(this.field_6_reserved);
        out.writeShort(this.getOptionFlags());
        out.writeShort(this.getOptionFlags2());
    }
    
    @Override
    protected int getDataSize() {
        return 16;
    }
    
    @Override
    public short getSid() {
        return 520;
    }
    
    @Deprecated
    @Removal(version = "5.0.0")
    public RowRecord clone() {
        return this.copy();
    }
    
    @Override
    public RowRecord copy() {
        return new RowRecord(this);
    }
    
    static {
        outlineLevel = BitFieldFactory.getInstance(7);
        colapsed = BitFieldFactory.getInstance(16);
        zeroHeight = BitFieldFactory.getInstance(32);
        badFontHeight = BitFieldFactory.getInstance(64);
        formatted = BitFieldFactory.getInstance(128);
        xfIndex = BitFieldFactory.getInstance(4095);
        topBorder = BitFieldFactory.getInstance(4096);
        bottomBorder = BitFieldFactory.getInstance(8192);
        phoeneticGuide = BitFieldFactory.getInstance(16384);
    }
}
