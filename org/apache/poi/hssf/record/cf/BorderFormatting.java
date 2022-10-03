package org.apache.poi.hssf.record.cf;

import org.apache.poi.util.BitFieldFactory;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.util.LittleEndian;
import org.apache.poi.util.Removal;
import org.apache.poi.util.LittleEndianInput;
import org.apache.poi.util.BitField;
import org.apache.poi.common.Duplicatable;

public final class BorderFormatting implements Duplicatable
{
    public static final short BORDER_NONE = 0;
    public static final short BORDER_THIN = 1;
    public static final short BORDER_MEDIUM = 2;
    public static final short BORDER_DASHED = 3;
    public static final short BORDER_HAIR = 4;
    public static final short BORDER_THICK = 5;
    public static final short BORDER_DOUBLE = 6;
    public static final short BORDER_DOTTED = 7;
    public static final short BORDER_MEDIUM_DASHED = 8;
    public static final short BORDER_DASH_DOT = 9;
    public static final short BORDER_MEDIUM_DASH_DOT = 10;
    public static final short BORDER_DASH_DOT_DOT = 11;
    public static final short BORDER_MEDIUM_DASH_DOT_DOT = 12;
    public static final short BORDER_SLANTED_DASH_DOT = 13;
    private static final BitField bordLeftLineStyle;
    private static final BitField bordRightLineStyle;
    private static final BitField bordTopLineStyle;
    private static final BitField bordBottomLineStyle;
    private static final BitField bordLeftLineColor;
    private static final BitField bordRightLineColor;
    private static final BitField bordTlBrLineOnOff;
    private static final BitField bordBlTrtLineOnOff;
    private static final BitField bordTopLineColor;
    private static final BitField bordBottomLineColor;
    private static final BitField bordDiagLineColor;
    private static final BitField bordDiagLineStyle;
    private int field_13_border_styles1;
    private int field_14_border_styles2;
    
    public BorderFormatting() {
        this.field_13_border_styles1 = 0;
        this.field_14_border_styles2 = 0;
    }
    
    public BorderFormatting(final BorderFormatting other) {
        this.field_13_border_styles1 = other.field_13_border_styles1;
        this.field_14_border_styles2 = other.field_14_border_styles2;
    }
    
    public BorderFormatting(final LittleEndianInput in) {
        this.field_13_border_styles1 = in.readInt();
        this.field_14_border_styles2 = in.readInt();
    }
    
    public int getDataLength() {
        return 8;
    }
    
    public void setBorderLeft(final int border) {
        this.field_13_border_styles1 = BorderFormatting.bordLeftLineStyle.setValue(this.field_13_border_styles1, border);
    }
    
    public int getBorderLeft() {
        return BorderFormatting.bordLeftLineStyle.getValue(this.field_13_border_styles1);
    }
    
    public void setBorderRight(final int border) {
        this.field_13_border_styles1 = BorderFormatting.bordRightLineStyle.setValue(this.field_13_border_styles1, border);
    }
    
    public int getBorderRight() {
        return BorderFormatting.bordRightLineStyle.getValue(this.field_13_border_styles1);
    }
    
    public void setBorderTop(final int border) {
        this.field_13_border_styles1 = BorderFormatting.bordTopLineStyle.setValue(this.field_13_border_styles1, border);
    }
    
    public int getBorderTop() {
        return BorderFormatting.bordTopLineStyle.getValue(this.field_13_border_styles1);
    }
    
    public void setBorderBottom(final int border) {
        this.field_13_border_styles1 = BorderFormatting.bordBottomLineStyle.setValue(this.field_13_border_styles1, border);
    }
    
    public int getBorderBottom() {
        return BorderFormatting.bordBottomLineStyle.getValue(this.field_13_border_styles1);
    }
    
    public void setBorderDiagonal(final int border) {
        this.field_14_border_styles2 = BorderFormatting.bordDiagLineStyle.setValue(this.field_14_border_styles2, border);
    }
    
    public int getBorderDiagonal() {
        return BorderFormatting.bordDiagLineStyle.getValue(this.field_14_border_styles2);
    }
    
    public void setLeftBorderColor(final int color) {
        this.field_13_border_styles1 = BorderFormatting.bordLeftLineColor.setValue(this.field_13_border_styles1, color);
    }
    
    public int getLeftBorderColor() {
        return BorderFormatting.bordLeftLineColor.getValue(this.field_13_border_styles1);
    }
    
    public void setRightBorderColor(final int color) {
        this.field_13_border_styles1 = BorderFormatting.bordRightLineColor.setValue(this.field_13_border_styles1, color);
    }
    
    public int getRightBorderColor() {
        return BorderFormatting.bordRightLineColor.getValue(this.field_13_border_styles1);
    }
    
    public void setTopBorderColor(final int color) {
        this.field_14_border_styles2 = BorderFormatting.bordTopLineColor.setValue(this.field_14_border_styles2, color);
    }
    
    public int getTopBorderColor() {
        return BorderFormatting.bordTopLineColor.getValue(this.field_14_border_styles2);
    }
    
    public void setBottomBorderColor(final int color) {
        this.field_14_border_styles2 = BorderFormatting.bordBottomLineColor.setValue(this.field_14_border_styles2, color);
    }
    
    public int getBottomBorderColor() {
        return BorderFormatting.bordBottomLineColor.getValue(this.field_14_border_styles2);
    }
    
    public void setDiagonalBorderColor(final int color) {
        this.field_14_border_styles2 = BorderFormatting.bordDiagLineColor.setValue(this.field_14_border_styles2, color);
    }
    
    public int getDiagonalBorderColor() {
        return BorderFormatting.bordDiagLineColor.getValue(this.field_14_border_styles2);
    }
    
    public void setForwardDiagonalOn(final boolean on) {
        this.field_13_border_styles1 = BorderFormatting.bordBlTrtLineOnOff.setBoolean(this.field_13_border_styles1, on);
    }
    
    public void setBackwardDiagonalOn(final boolean on) {
        this.field_13_border_styles1 = BorderFormatting.bordTlBrLineOnOff.setBoolean(this.field_13_border_styles1, on);
    }
    
    public boolean isForwardDiagonalOn() {
        return BorderFormatting.bordBlTrtLineOnOff.isSet(this.field_13_border_styles1);
    }
    
    public boolean isBackwardDiagonalOn() {
        return BorderFormatting.bordTlBrLineOnOff.isSet(this.field_13_border_styles1);
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("    [Border Formatting]\n");
        buffer.append("          .lftln     = ").append(Integer.toHexString(this.getBorderLeft())).append("\n");
        buffer.append("          .rgtln     = ").append(Integer.toHexString(this.getBorderRight())).append("\n");
        buffer.append("          .topln     = ").append(Integer.toHexString(this.getBorderTop())).append("\n");
        buffer.append("          .btmln     = ").append(Integer.toHexString(this.getBorderBottom())).append("\n");
        buffer.append("          .leftborder= ").append(Integer.toHexString(this.getLeftBorderColor())).append("\n");
        buffer.append("          .rghtborder= ").append(Integer.toHexString(this.getRightBorderColor())).append("\n");
        buffer.append("          .topborder= ").append(Integer.toHexString(this.getTopBorderColor())).append("\n");
        buffer.append("          .bottomborder= ").append(Integer.toHexString(this.getBottomBorderColor())).append("\n");
        buffer.append("          .fwdiag= ").append(this.isForwardDiagonalOn()).append("\n");
        buffer.append("          .bwdiag= ").append(this.isBackwardDiagonalOn()).append("\n");
        buffer.append("    [/Border Formatting]\n");
        return buffer.toString();
    }
    
    @Deprecated
    @Removal(version = "5.0.0")
    public BorderFormatting clone() {
        return this.copy();
    }
    
    @Override
    public BorderFormatting copy() {
        return new BorderFormatting(this);
    }
    
    public int serialize(final int offset, final byte[] data) {
        LittleEndian.putInt(data, offset + 0, this.field_13_border_styles1);
        LittleEndian.putInt(data, offset + 4, this.field_14_border_styles2);
        return 8;
    }
    
    public void serialize(final LittleEndianOutput out) {
        out.writeInt(this.field_13_border_styles1);
        out.writeInt(this.field_14_border_styles2);
    }
    
    static {
        bordLeftLineStyle = BitFieldFactory.getInstance(15);
        bordRightLineStyle = BitFieldFactory.getInstance(240);
        bordTopLineStyle = BitFieldFactory.getInstance(3840);
        bordBottomLineStyle = BitFieldFactory.getInstance(61440);
        bordLeftLineColor = BitFieldFactory.getInstance(8323072);
        bordRightLineColor = BitFieldFactory.getInstance(1065353216);
        bordTlBrLineOnOff = BitFieldFactory.getInstance(1073741824);
        bordBlTrtLineOnOff = BitFieldFactory.getInstance(Integer.MIN_VALUE);
        bordTopLineColor = BitFieldFactory.getInstance(127);
        bordBottomLineColor = BitFieldFactory.getInstance(16256);
        bordDiagLineColor = BitFieldFactory.getInstance(2080768);
        bordDiagLineStyle = BitFieldFactory.getInstance(31457280);
    }
}
