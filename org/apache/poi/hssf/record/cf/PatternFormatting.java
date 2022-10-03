package org.apache.poi.hssf.record.cf;

import org.apache.poi.util.BitFieldFactory;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.util.Removal;
import org.apache.poi.util.LittleEndianInput;
import org.apache.poi.util.BitField;
import org.apache.poi.common.Duplicatable;

public final class PatternFormatting implements Duplicatable
{
    public static final short NO_FILL = 0;
    public static final short SOLID_FOREGROUND = 1;
    public static final short FINE_DOTS = 2;
    public static final short ALT_BARS = 3;
    public static final short SPARSE_DOTS = 4;
    public static final short THICK_HORZ_BANDS = 5;
    public static final short THICK_VERT_BANDS = 6;
    public static final short THICK_BACKWARD_DIAG = 7;
    public static final short THICK_FORWARD_DIAG = 8;
    public static final short BIG_SPOTS = 9;
    public static final short BRICKS = 10;
    public static final short THIN_HORZ_BANDS = 11;
    public static final short THIN_VERT_BANDS = 12;
    public static final short THIN_BACKWARD_DIAG = 13;
    public static final short THIN_FORWARD_DIAG = 14;
    public static final short SQUARES = 15;
    public static final short DIAMONDS = 16;
    public static final short LESS_DOTS = 17;
    public static final short LEAST_DOTS = 18;
    private static final BitField fillPatternStyle;
    private static final BitField patternColorIndex;
    private static final BitField patternBackgroundColorIndex;
    private int field_15_pattern_style;
    private int field_16_pattern_color_indexes;
    
    public PatternFormatting() {
        this.field_15_pattern_style = 0;
        this.field_16_pattern_color_indexes = 0;
    }
    
    public PatternFormatting(final PatternFormatting other) {
        this.field_15_pattern_style = other.field_15_pattern_style;
        this.field_16_pattern_color_indexes = other.field_16_pattern_color_indexes;
    }
    
    public PatternFormatting(final LittleEndianInput in) {
        this.field_15_pattern_style = in.readUShort();
        this.field_16_pattern_color_indexes = in.readUShort();
    }
    
    public int getDataLength() {
        return 4;
    }
    
    public void setFillPattern(final int fp) {
        this.field_15_pattern_style = PatternFormatting.fillPatternStyle.setValue(this.field_15_pattern_style, fp);
    }
    
    public int getFillPattern() {
        return PatternFormatting.fillPatternStyle.getValue(this.field_15_pattern_style);
    }
    
    public void setFillBackgroundColor(final int bg) {
        this.field_16_pattern_color_indexes = PatternFormatting.patternBackgroundColorIndex.setValue(this.field_16_pattern_color_indexes, bg);
    }
    
    public int getFillBackgroundColor() {
        return PatternFormatting.patternBackgroundColorIndex.getValue(this.field_16_pattern_color_indexes);
    }
    
    public void setFillForegroundColor(final int fg) {
        this.field_16_pattern_color_indexes = PatternFormatting.patternColorIndex.setValue(this.field_16_pattern_color_indexes, fg);
    }
    
    public int getFillForegroundColor() {
        return PatternFormatting.patternColorIndex.getValue(this.field_16_pattern_color_indexes);
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("    [Pattern Formatting]\n");
        buffer.append("          .fillpattern= ").append(Integer.toHexString(this.getFillPattern())).append("\n");
        buffer.append("          .fgcoloridx= ").append(Integer.toHexString(this.getFillForegroundColor())).append("\n");
        buffer.append("          .bgcoloridx= ").append(Integer.toHexString(this.getFillBackgroundColor())).append("\n");
        buffer.append("    [/Pattern Formatting]\n");
        return buffer.toString();
    }
    
    @Deprecated
    @Removal(version = "5.0.0")
    public PatternFormatting clone() {
        return this.copy();
    }
    
    @Override
    public PatternFormatting copy() {
        return new PatternFormatting(this);
    }
    
    public void serialize(final LittleEndianOutput out) {
        out.writeShort(this.field_15_pattern_style);
        out.writeShort(this.field_16_pattern_color_indexes);
    }
    
    static {
        fillPatternStyle = BitFieldFactory.getInstance(64512);
        patternColorIndex = BitFieldFactory.getInstance(127);
        patternBackgroundColorIndex = BitFieldFactory.getInstance(16256);
    }
}
