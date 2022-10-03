package org.apache.poi.hssf.record;

import org.apache.poi.common.Duplicatable;
import java.util.Objects;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.util.BitFieldFactory;
import org.apache.poi.util.BitField;

public final class ExtendedFormatRecord extends StandardRecord
{
    public static final short sid = 224;
    public static final short NULL = -16;
    public static final short XF_STYLE = 1;
    public static final short XF_CELL = 0;
    public static final short NONE = 0;
    public static final short THIN = 1;
    public static final short MEDIUM = 2;
    public static final short DASHED = 3;
    public static final short DOTTED = 4;
    public static final short THICK = 5;
    public static final short DOUBLE = 6;
    public static final short HAIR = 7;
    public static final short MEDIUM_DASHED = 8;
    public static final short DASH_DOT = 9;
    public static final short MEDIUM_DASH_DOT = 10;
    public static final short DASH_DOT_DOT = 11;
    public static final short MEDIUM_DASH_DOT_DOT = 12;
    public static final short SLANTED_DASH_DOT = 13;
    public static final short GENERAL = 0;
    public static final short LEFT = 1;
    public static final short CENTER = 2;
    public static final short RIGHT = 3;
    public static final short FILL = 4;
    public static final short JUSTIFY = 5;
    public static final short CENTER_SELECTION = 6;
    public static final short VERTICAL_TOP = 0;
    public static final short VERTICAL_CENTER = 1;
    public static final short VERTICAL_BOTTOM = 2;
    public static final short VERTICAL_JUSTIFY = 3;
    public static final short NO_FILL = 0;
    public static final short SOLID_FILL = 1;
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
    private static final BitField _locked;
    private static final BitField _hidden;
    private static final BitField _xf_type;
    private static final BitField _123_prefix;
    private static final BitField _parent_index;
    private static final BitField _alignment;
    private static final BitField _wrap_text;
    private static final BitField _vertical_alignment;
    private static final BitField _justify_last;
    private static final BitField _rotation;
    private static final BitField _indent;
    private static final BitField _shrink_to_fit;
    private static final BitField _merge_cells;
    private static final BitField _reading_order;
    private static final BitField _indent_not_parent_format;
    private static final BitField _indent_not_parent_font;
    private static final BitField _indent_not_parent_alignment;
    private static final BitField _indent_not_parent_border;
    private static final BitField _indent_not_parent_pattern;
    private static final BitField _indent_not_parent_cell_options;
    private static final BitField _border_left;
    private static final BitField _border_right;
    private static final BitField _border_top;
    private static final BitField _border_bottom;
    private static final BitField _left_border_palette_idx;
    private static final BitField _right_border_palette_idx;
    private static final BitField _diag;
    private static final BitField _top_border_palette_idx;
    private static final BitField _bottom_border_palette_idx;
    private static final BitField _adtl_diag;
    private static final BitField _adtl_diag_line_style;
    private static final BitField _adtl_fill_pattern;
    private static final BitField _fill_foreground;
    private static final BitField _fill_background;
    private short field_1_font_index;
    private short field_2_format_index;
    private short field_3_cell_options;
    private short field_4_alignment_options;
    private short field_5_indention_options;
    private short field_6_border_options;
    private short field_7_palette_options;
    private int field_8_adtl_palette_options;
    private short field_9_fill_palette_options;
    
    private static BitField bf(final int i) {
        return BitFieldFactory.getInstance(i);
    }
    
    public ExtendedFormatRecord() {
    }
    
    public ExtendedFormatRecord(final ExtendedFormatRecord other) {
        super(other);
        this.field_1_font_index = other.field_1_font_index;
        this.field_2_format_index = other.field_2_format_index;
        this.field_3_cell_options = other.field_3_cell_options;
        this.field_4_alignment_options = other.field_4_alignment_options;
        this.field_5_indention_options = other.field_5_indention_options;
        this.field_6_border_options = other.field_6_border_options;
        this.field_7_palette_options = other.field_7_palette_options;
        this.field_8_adtl_palette_options = other.field_8_adtl_palette_options;
        this.field_9_fill_palette_options = other.field_9_fill_palette_options;
    }
    
    public ExtendedFormatRecord(final RecordInputStream in) {
        this.field_1_font_index = in.readShort();
        this.field_2_format_index = in.readShort();
        this.field_3_cell_options = in.readShort();
        this.field_4_alignment_options = in.readShort();
        this.field_5_indention_options = in.readShort();
        this.field_6_border_options = in.readShort();
        this.field_7_palette_options = in.readShort();
        this.field_8_adtl_palette_options = in.readInt();
        this.field_9_fill_palette_options = in.readShort();
    }
    
    public void setFontIndex(final short index) {
        this.field_1_font_index = index;
    }
    
    public void setFormatIndex(final short index) {
        this.field_2_format_index = index;
    }
    
    public void setCellOptions(final short options) {
        this.field_3_cell_options = options;
    }
    
    public void setLocked(final boolean locked) {
        this.field_3_cell_options = ExtendedFormatRecord._locked.setShortBoolean(this.field_3_cell_options, locked);
    }
    
    public void setHidden(final boolean hidden) {
        this.field_3_cell_options = ExtendedFormatRecord._hidden.setShortBoolean(this.field_3_cell_options, hidden);
    }
    
    public void setXFType(final short type) {
        this.field_3_cell_options = ExtendedFormatRecord._xf_type.setShortValue(this.field_3_cell_options, type);
    }
    
    public void set123Prefix(final boolean prefix) {
        this.field_3_cell_options = ExtendedFormatRecord._123_prefix.setShortBoolean(this.field_3_cell_options, prefix);
    }
    
    public void setParentIndex(final short parent) {
        this.field_3_cell_options = ExtendedFormatRecord._parent_index.setShortValue(this.field_3_cell_options, parent);
    }
    
    public void setAlignmentOptions(final short options) {
        this.field_4_alignment_options = options;
    }
    
    public void setAlignment(final short align) {
        this.field_4_alignment_options = ExtendedFormatRecord._alignment.setShortValue(this.field_4_alignment_options, align);
    }
    
    public void setWrapText(final boolean wrapped) {
        this.field_4_alignment_options = ExtendedFormatRecord._wrap_text.setShortBoolean(this.field_4_alignment_options, wrapped);
    }
    
    public void setVerticalAlignment(final short align) {
        this.field_4_alignment_options = ExtendedFormatRecord._vertical_alignment.setShortValue(this.field_4_alignment_options, align);
    }
    
    public void setJustifyLast(final short justify) {
        this.field_4_alignment_options = ExtendedFormatRecord._justify_last.setShortValue(this.field_4_alignment_options, justify);
    }
    
    public void setRotation(final short rotation) {
        this.field_4_alignment_options = ExtendedFormatRecord._rotation.setShortValue(this.field_4_alignment_options, rotation);
    }
    
    public void setIndentionOptions(final short options) {
        this.field_5_indention_options = options;
    }
    
    public void setIndent(final short indent) {
        this.field_5_indention_options = ExtendedFormatRecord._indent.setShortValue(this.field_5_indention_options, indent);
    }
    
    public void setShrinkToFit(final boolean shrink) {
        this.field_5_indention_options = ExtendedFormatRecord._shrink_to_fit.setShortBoolean(this.field_5_indention_options, shrink);
    }
    
    public void setMergeCells(final boolean merge) {
        this.field_5_indention_options = ExtendedFormatRecord._merge_cells.setShortBoolean(this.field_5_indention_options, merge);
    }
    
    public void setReadingOrder(final short order) {
        this.field_5_indention_options = ExtendedFormatRecord._reading_order.setShortValue(this.field_5_indention_options, order);
    }
    
    public void setIndentNotParentFormat(final boolean parent) {
        this.field_5_indention_options = ExtendedFormatRecord._indent_not_parent_format.setShortBoolean(this.field_5_indention_options, parent);
    }
    
    public void setIndentNotParentFont(final boolean font) {
        this.field_5_indention_options = ExtendedFormatRecord._indent_not_parent_font.setShortBoolean(this.field_5_indention_options, font);
    }
    
    public void setIndentNotParentAlignment(final boolean alignment) {
        this.field_5_indention_options = ExtendedFormatRecord._indent_not_parent_alignment.setShortBoolean(this.field_5_indention_options, alignment);
    }
    
    public void setIndentNotParentBorder(final boolean border) {
        this.field_5_indention_options = ExtendedFormatRecord._indent_not_parent_border.setShortBoolean(this.field_5_indention_options, border);
    }
    
    public void setIndentNotParentPattern(final boolean pattern) {
        this.field_5_indention_options = ExtendedFormatRecord._indent_not_parent_pattern.setShortBoolean(this.field_5_indention_options, pattern);
    }
    
    public void setIndentNotParentCellOptions(final boolean options) {
        this.field_5_indention_options = ExtendedFormatRecord._indent_not_parent_cell_options.setShortBoolean(this.field_5_indention_options, options);
    }
    
    public void setBorderOptions(final short options) {
        this.field_6_border_options = options;
    }
    
    public void setBorderLeft(final short border) {
        this.field_6_border_options = ExtendedFormatRecord._border_left.setShortValue(this.field_6_border_options, border);
    }
    
    public void setBorderRight(final short border) {
        this.field_6_border_options = ExtendedFormatRecord._border_right.setShortValue(this.field_6_border_options, border);
    }
    
    public void setBorderTop(final short border) {
        this.field_6_border_options = ExtendedFormatRecord._border_top.setShortValue(this.field_6_border_options, border);
    }
    
    public void setBorderBottom(final short border) {
        this.field_6_border_options = ExtendedFormatRecord._border_bottom.setShortValue(this.field_6_border_options, border);
    }
    
    public void setPaletteOptions(final short options) {
        this.field_7_palette_options = options;
    }
    
    public void setLeftBorderPaletteIdx(final short border) {
        this.field_7_palette_options = ExtendedFormatRecord._left_border_palette_idx.setShortValue(this.field_7_palette_options, border);
    }
    
    public void setRightBorderPaletteIdx(final short border) {
        this.field_7_palette_options = ExtendedFormatRecord._right_border_palette_idx.setShortValue(this.field_7_palette_options, border);
    }
    
    public void setDiag(final short diag) {
        this.field_7_palette_options = ExtendedFormatRecord._diag.setShortValue(this.field_7_palette_options, diag);
    }
    
    public void setAdtlPaletteOptions(final short options) {
        this.field_8_adtl_palette_options = options;
    }
    
    public void setTopBorderPaletteIdx(final short border) {
        this.field_8_adtl_palette_options = ExtendedFormatRecord._top_border_palette_idx.setValue(this.field_8_adtl_palette_options, border);
    }
    
    public void setBottomBorderPaletteIdx(final short border) {
        this.field_8_adtl_palette_options = ExtendedFormatRecord._bottom_border_palette_idx.setValue(this.field_8_adtl_palette_options, border);
    }
    
    public void setAdtlDiag(final short diag) {
        this.field_8_adtl_palette_options = ExtendedFormatRecord._adtl_diag.setValue(this.field_8_adtl_palette_options, diag);
    }
    
    public void setAdtlDiagLineStyle(final short diag) {
        this.field_8_adtl_palette_options = ExtendedFormatRecord._adtl_diag_line_style.setValue(this.field_8_adtl_palette_options, diag);
    }
    
    public void setAdtlFillPattern(final short fill) {
        this.field_8_adtl_palette_options = ExtendedFormatRecord._adtl_fill_pattern.setValue(this.field_8_adtl_palette_options, fill);
    }
    
    public void setFillPaletteOptions(final short options) {
        this.field_9_fill_palette_options = options;
    }
    
    public void setFillForeground(final short color) {
        this.field_9_fill_palette_options = ExtendedFormatRecord._fill_foreground.setShortValue(this.field_9_fill_palette_options, color);
    }
    
    public void setFillBackground(final short color) {
        this.field_9_fill_palette_options = ExtendedFormatRecord._fill_background.setShortValue(this.field_9_fill_palette_options, color);
    }
    
    public short getFontIndex() {
        return this.field_1_font_index;
    }
    
    public short getFormatIndex() {
        return this.field_2_format_index;
    }
    
    public short getCellOptions() {
        return this.field_3_cell_options;
    }
    
    public boolean isLocked() {
        return ExtendedFormatRecord._locked.isSet(this.field_3_cell_options);
    }
    
    public boolean isHidden() {
        return ExtendedFormatRecord._hidden.isSet(this.field_3_cell_options);
    }
    
    public short getXFType() {
        return ExtendedFormatRecord._xf_type.getShortValue(this.field_3_cell_options);
    }
    
    public boolean get123Prefix() {
        return ExtendedFormatRecord._123_prefix.isSet(this.field_3_cell_options);
    }
    
    public short getParentIndex() {
        return ExtendedFormatRecord._parent_index.getShortValue(this.field_3_cell_options);
    }
    
    public short getAlignmentOptions() {
        return this.field_4_alignment_options;
    }
    
    public short getAlignment() {
        return ExtendedFormatRecord._alignment.getShortValue(this.field_4_alignment_options);
    }
    
    public boolean getWrapText() {
        return ExtendedFormatRecord._wrap_text.isSet(this.field_4_alignment_options);
    }
    
    public short getVerticalAlignment() {
        return ExtendedFormatRecord._vertical_alignment.getShortValue(this.field_4_alignment_options);
    }
    
    public short getJustifyLast() {
        return ExtendedFormatRecord._justify_last.getShortValue(this.field_4_alignment_options);
    }
    
    public short getRotation() {
        return ExtendedFormatRecord._rotation.getShortValue(this.field_4_alignment_options);
    }
    
    public short getIndentionOptions() {
        return this.field_5_indention_options;
    }
    
    public short getIndent() {
        return ExtendedFormatRecord._indent.getShortValue(this.field_5_indention_options);
    }
    
    public boolean getShrinkToFit() {
        return ExtendedFormatRecord._shrink_to_fit.isSet(this.field_5_indention_options);
    }
    
    public boolean getMergeCells() {
        return ExtendedFormatRecord._merge_cells.isSet(this.field_5_indention_options);
    }
    
    public short getReadingOrder() {
        return ExtendedFormatRecord._reading_order.getShortValue(this.field_5_indention_options);
    }
    
    public boolean isIndentNotParentFormat() {
        return ExtendedFormatRecord._indent_not_parent_format.isSet(this.field_5_indention_options);
    }
    
    public boolean isIndentNotParentFont() {
        return ExtendedFormatRecord._indent_not_parent_font.isSet(this.field_5_indention_options);
    }
    
    public boolean isIndentNotParentAlignment() {
        return ExtendedFormatRecord._indent_not_parent_alignment.isSet(this.field_5_indention_options);
    }
    
    public boolean isIndentNotParentBorder() {
        return ExtendedFormatRecord._indent_not_parent_border.isSet(this.field_5_indention_options);
    }
    
    public boolean isIndentNotParentPattern() {
        return ExtendedFormatRecord._indent_not_parent_pattern.isSet(this.field_5_indention_options);
    }
    
    public boolean isIndentNotParentCellOptions() {
        return ExtendedFormatRecord._indent_not_parent_cell_options.isSet(this.field_5_indention_options);
    }
    
    public short getBorderOptions() {
        return this.field_6_border_options;
    }
    
    public short getBorderLeft() {
        return ExtendedFormatRecord._border_left.getShortValue(this.field_6_border_options);
    }
    
    public short getBorderRight() {
        return ExtendedFormatRecord._border_right.getShortValue(this.field_6_border_options);
    }
    
    public short getBorderTop() {
        return ExtendedFormatRecord._border_top.getShortValue(this.field_6_border_options);
    }
    
    public short getBorderBottom() {
        return ExtendedFormatRecord._border_bottom.getShortValue(this.field_6_border_options);
    }
    
    public short getPaletteOptions() {
        return this.field_7_palette_options;
    }
    
    public short getLeftBorderPaletteIdx() {
        return ExtendedFormatRecord._left_border_palette_idx.getShortValue(this.field_7_palette_options);
    }
    
    public short getRightBorderPaletteIdx() {
        return ExtendedFormatRecord._right_border_palette_idx.getShortValue(this.field_7_palette_options);
    }
    
    public short getDiag() {
        return ExtendedFormatRecord._diag.getShortValue(this.field_7_palette_options);
    }
    
    public int getAdtlPaletteOptions() {
        return this.field_8_adtl_palette_options;
    }
    
    public short getTopBorderPaletteIdx() {
        return (short)ExtendedFormatRecord._top_border_palette_idx.getValue(this.field_8_adtl_palette_options);
    }
    
    public short getBottomBorderPaletteIdx() {
        return (short)ExtendedFormatRecord._bottom_border_palette_idx.getValue(this.field_8_adtl_palette_options);
    }
    
    public short getAdtlDiag() {
        return (short)ExtendedFormatRecord._adtl_diag.getValue(this.field_8_adtl_palette_options);
    }
    
    public short getAdtlDiagLineStyle() {
        return (short)ExtendedFormatRecord._adtl_diag_line_style.getValue(this.field_8_adtl_palette_options);
    }
    
    public short getAdtlFillPattern() {
        return (short)ExtendedFormatRecord._adtl_fill_pattern.getValue(this.field_8_adtl_palette_options);
    }
    
    public short getFillPaletteOptions() {
        return this.field_9_fill_palette_options;
    }
    
    public short getFillForeground() {
        return ExtendedFormatRecord._fill_foreground.getShortValue(this.field_9_fill_palette_options);
    }
    
    public short getFillBackground() {
        return ExtendedFormatRecord._fill_background.getShortValue(this.field_9_fill_palette_options);
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("[EXTENDEDFORMAT]\n");
        if (this.getXFType() == 1) {
            buffer.append(" STYLE_RECORD_TYPE\n");
        }
        else if (this.getXFType() == 0) {
            buffer.append(" CELL_RECORD_TYPE\n");
        }
        buffer.append("    .fontindex       = ").append(Integer.toHexString(this.getFontIndex())).append("\n");
        buffer.append("    .formatindex     = ").append(Integer.toHexString(this.getFormatIndex())).append("\n");
        buffer.append("    .celloptions     = ").append(Integer.toHexString(this.getCellOptions())).append("\n");
        buffer.append("          .islocked  = ").append(this.isLocked()).append("\n");
        buffer.append("          .ishidden  = ").append(this.isHidden()).append("\n");
        buffer.append("          .recordtype= ").append(Integer.toHexString(this.getXFType())).append("\n");
        buffer.append("          .parentidx = ").append(Integer.toHexString(this.getParentIndex())).append("\n");
        buffer.append("    .alignmentoptions= ").append(Integer.toHexString(this.getAlignmentOptions())).append("\n");
        buffer.append("          .alignment = ").append(this.getAlignment()).append("\n");
        buffer.append("          .wraptext  = ").append(this.getWrapText()).append("\n");
        buffer.append("          .valignment= ").append(Integer.toHexString(this.getVerticalAlignment())).append("\n");
        buffer.append("          .justlast  = ").append(Integer.toHexString(this.getJustifyLast())).append("\n");
        buffer.append("          .rotation  = ").append(Integer.toHexString(this.getRotation())).append("\n");
        buffer.append("    .indentionoptions= ").append(Integer.toHexString(this.getIndentionOptions())).append("\n");
        buffer.append("          .indent    = ").append(Integer.toHexString(this.getIndent())).append("\n");
        buffer.append("          .shrinktoft= ").append(this.getShrinkToFit()).append("\n");
        buffer.append("          .mergecells= ").append(this.getMergeCells()).append("\n");
        buffer.append("          .readngordr= ").append(Integer.toHexString(this.getReadingOrder())).append("\n");
        buffer.append("          .formatflag= ").append(this.isIndentNotParentFormat()).append("\n");
        buffer.append("          .fontflag  = ").append(this.isIndentNotParentFont()).append("\n");
        buffer.append("          .prntalgnmt= ").append(this.isIndentNotParentAlignment()).append("\n");
        buffer.append("          .borderflag= ").append(this.isIndentNotParentBorder()).append("\n");
        buffer.append("          .paternflag= ").append(this.isIndentNotParentPattern()).append("\n");
        buffer.append("          .celloption= ").append(this.isIndentNotParentCellOptions()).append("\n");
        buffer.append("    .borderoptns     = ").append(Integer.toHexString(this.getBorderOptions())).append("\n");
        buffer.append("          .lftln     = ").append(Integer.toHexString(this.getBorderLeft())).append("\n");
        buffer.append("          .rgtln     = ").append(Integer.toHexString(this.getBorderRight())).append("\n");
        buffer.append("          .topln     = ").append(Integer.toHexString(this.getBorderTop())).append("\n");
        buffer.append("          .btmln     = ").append(Integer.toHexString(this.getBorderBottom())).append("\n");
        buffer.append("    .paleteoptns     = ").append(Integer.toHexString(this.getPaletteOptions())).append("\n");
        buffer.append("          .leftborder= ").append(Integer.toHexString(this.getLeftBorderPaletteIdx())).append("\n");
        buffer.append("          .rghtborder= ").append(Integer.toHexString(this.getRightBorderPaletteIdx())).append("\n");
        buffer.append("          .diag      = ").append(Integer.toHexString(this.getDiag())).append("\n");
        buffer.append("    .paleteoptn2     = ").append(Integer.toHexString(this.getAdtlPaletteOptions())).append("\n");
        buffer.append("          .topborder = ").append(Integer.toHexString(this.getTopBorderPaletteIdx())).append("\n");
        buffer.append("          .botmborder= ").append(Integer.toHexString(this.getBottomBorderPaletteIdx())).append("\n");
        buffer.append("          .adtldiag  = ").append(Integer.toHexString(this.getAdtlDiag())).append("\n");
        buffer.append("          .diaglnstyl= ").append(Integer.toHexString(this.getAdtlDiagLineStyle())).append("\n");
        buffer.append("          .fillpattrn= ").append(Integer.toHexString(this.getAdtlFillPattern())).append("\n");
        buffer.append("    .fillpaloptn     = ").append(Integer.toHexString(this.getFillPaletteOptions())).append("\n");
        buffer.append("          .foreground= ").append(Integer.toHexString(this.getFillForeground())).append("\n");
        buffer.append("          .background= ").append(Integer.toHexString(this.getFillBackground())).append("\n");
        buffer.append("[/EXTENDEDFORMAT]\n");
        return buffer.toString();
    }
    
    public void serialize(final LittleEndianOutput out) {
        out.writeShort(this.getFontIndex());
        out.writeShort(this.getFormatIndex());
        out.writeShort(this.getCellOptions());
        out.writeShort(this.getAlignmentOptions());
        out.writeShort(this.getIndentionOptions());
        out.writeShort(this.getBorderOptions());
        out.writeShort(this.getPaletteOptions());
        out.writeInt(this.getAdtlPaletteOptions());
        out.writeShort(this.getFillPaletteOptions());
    }
    
    @Override
    protected int getDataSize() {
        return 20;
    }
    
    @Override
    public short getSid() {
        return 224;
    }
    
    public void cloneStyleFrom(final ExtendedFormatRecord source) {
        this.field_1_font_index = source.field_1_font_index;
        this.field_2_format_index = source.field_2_format_index;
        this.field_3_cell_options = source.field_3_cell_options;
        this.field_4_alignment_options = source.field_4_alignment_options;
        this.field_5_indention_options = source.field_5_indention_options;
        this.field_6_border_options = source.field_6_border_options;
        this.field_7_palette_options = source.field_7_palette_options;
        this.field_8_adtl_palette_options = source.field_8_adtl_palette_options;
        this.field_9_fill_palette_options = source.field_9_fill_palette_options;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(this.field_1_font_index, this.field_2_format_index, this.field_3_cell_options, this.field_4_alignment_options, this.field_5_indention_options, this.field_6_border_options, this.field_7_palette_options, this.field_8_adtl_palette_options, this.field_9_fill_palette_options);
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (obj instanceof ExtendedFormatRecord) {
            final ExtendedFormatRecord other = (ExtendedFormatRecord)obj;
            return this.field_1_font_index == other.field_1_font_index && this.field_2_format_index == other.field_2_format_index && this.field_3_cell_options == other.field_3_cell_options && this.field_4_alignment_options == other.field_4_alignment_options && this.field_5_indention_options == other.field_5_indention_options && this.field_6_border_options == other.field_6_border_options && this.field_7_palette_options == other.field_7_palette_options && this.field_8_adtl_palette_options == other.field_8_adtl_palette_options && this.field_9_fill_palette_options == other.field_9_fill_palette_options;
        }
        return false;
    }
    
    public int[] stateSummary() {
        return new int[] { this.field_1_font_index, this.field_2_format_index, this.field_3_cell_options, this.field_4_alignment_options, this.field_5_indention_options, this.field_6_border_options, this.field_7_palette_options, this.field_8_adtl_palette_options, this.field_9_fill_palette_options };
    }
    
    @Override
    public ExtendedFormatRecord copy() {
        return new ExtendedFormatRecord(this);
    }
    
    static {
        _locked = bf(1);
        _hidden = bf(2);
        _xf_type = bf(4);
        _123_prefix = bf(8);
        _parent_index = bf(65520);
        _alignment = bf(7);
        _wrap_text = bf(8);
        _vertical_alignment = bf(112);
        _justify_last = bf(128);
        _rotation = bf(65280);
        _indent = bf(15);
        _shrink_to_fit = bf(16);
        _merge_cells = bf(32);
        _reading_order = bf(192);
        _indent_not_parent_format = bf(1024);
        _indent_not_parent_font = bf(2048);
        _indent_not_parent_alignment = bf(4096);
        _indent_not_parent_border = bf(8192);
        _indent_not_parent_pattern = bf(16384);
        _indent_not_parent_cell_options = bf(32768);
        _border_left = bf(15);
        _border_right = bf(240);
        _border_top = bf(3840);
        _border_bottom = bf(61440);
        _left_border_palette_idx = bf(127);
        _right_border_palette_idx = bf(16256);
        _diag = bf(49152);
        _top_border_palette_idx = bf(127);
        _bottom_border_palette_idx = bf(16256);
        _adtl_diag = bf(2080768);
        _adtl_diag_line_style = bf(31457280);
        _adtl_fill_pattern = bf(-67108864);
        _fill_foreground = bf(127);
        _fill_background = bf(16256);
    }
}
