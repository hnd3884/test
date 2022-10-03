package org.apache.poi.hssf.record;

import org.apache.poi.util.BitFieldFactory;
import org.apache.poi.common.Duplicatable;
import org.apache.poi.util.Removal;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.util.BitField;

public final class WindowTwoRecord extends StandardRecord
{
    public static final short sid = 574;
    private static final BitField displayFormulas;
    private static final BitField displayGridlines;
    private static final BitField displayRowColHeadings;
    private static final BitField freezePanes;
    private static final BitField displayZeros;
    private static final BitField defaultHeader;
    private static final BitField arabic;
    private static final BitField displayGuts;
    private static final BitField freezePanesNoSplit;
    private static final BitField selected;
    private static final BitField active;
    private static final BitField savedInPageBreakPreview;
    private short field_1_options;
    private short field_2_top_row;
    private short field_3_left_col;
    private int field_4_header_color;
    private short field_5_page_break_zoom;
    private short field_6_normal_zoom;
    private int field_7_reserved;
    
    public WindowTwoRecord() {
    }
    
    public WindowTwoRecord(final WindowTwoRecord other) {
        super(other);
        this.field_1_options = other.field_1_options;
        this.field_2_top_row = other.field_2_top_row;
        this.field_3_left_col = other.field_3_left_col;
        this.field_4_header_color = other.field_4_header_color;
        this.field_5_page_break_zoom = other.field_5_page_break_zoom;
        this.field_6_normal_zoom = other.field_6_normal_zoom;
        this.field_7_reserved = other.field_7_reserved;
    }
    
    public WindowTwoRecord(final RecordInputStream in) {
        final int size = in.remaining();
        this.field_1_options = in.readShort();
        this.field_2_top_row = in.readShort();
        this.field_3_left_col = in.readShort();
        this.field_4_header_color = in.readInt();
        if (size > 10) {
            this.field_5_page_break_zoom = in.readShort();
            this.field_6_normal_zoom = in.readShort();
        }
        if (size > 14) {
            this.field_7_reserved = in.readInt();
        }
    }
    
    public void setOptions(final short options) {
        this.field_1_options = options;
    }
    
    public void setDisplayFormulas(final boolean formulas) {
        this.field_1_options = WindowTwoRecord.displayFormulas.setShortBoolean(this.field_1_options, formulas);
    }
    
    public void setDisplayGridlines(final boolean gridlines) {
        this.field_1_options = WindowTwoRecord.displayGridlines.setShortBoolean(this.field_1_options, gridlines);
    }
    
    public void setDisplayRowColHeadings(final boolean headings) {
        this.field_1_options = WindowTwoRecord.displayRowColHeadings.setShortBoolean(this.field_1_options, headings);
    }
    
    public void setFreezePanes(final boolean freezepanes) {
        this.field_1_options = WindowTwoRecord.freezePanes.setShortBoolean(this.field_1_options, freezepanes);
    }
    
    public void setDisplayZeros(final boolean zeros) {
        this.field_1_options = WindowTwoRecord.displayZeros.setShortBoolean(this.field_1_options, zeros);
    }
    
    public void setDefaultHeader(final boolean header) {
        this.field_1_options = WindowTwoRecord.defaultHeader.setShortBoolean(this.field_1_options, header);
    }
    
    public void setArabic(final boolean isarabic) {
        this.field_1_options = WindowTwoRecord.arabic.setShortBoolean(this.field_1_options, isarabic);
    }
    
    public void setDisplayGuts(final boolean guts) {
        this.field_1_options = WindowTwoRecord.displayGuts.setShortBoolean(this.field_1_options, guts);
    }
    
    public void setFreezePanesNoSplit(final boolean freeze) {
        this.field_1_options = WindowTwoRecord.freezePanesNoSplit.setShortBoolean(this.field_1_options, freeze);
    }
    
    public void setSelected(final boolean sel) {
        this.field_1_options = WindowTwoRecord.selected.setShortBoolean(this.field_1_options, sel);
    }
    
    public void setActive(final boolean p) {
        this.field_1_options = WindowTwoRecord.active.setShortBoolean(this.field_1_options, p);
    }
    
    public void setSavedInPageBreakPreview(final boolean p) {
        this.field_1_options = WindowTwoRecord.savedInPageBreakPreview.setShortBoolean(this.field_1_options, p);
    }
    
    public void setTopRow(final short topRow) {
        this.field_2_top_row = topRow;
    }
    
    public void setLeftCol(final short leftCol) {
        this.field_3_left_col = leftCol;
    }
    
    public void setHeaderColor(final int color) {
        this.field_4_header_color = color;
    }
    
    public void setPageBreakZoom(final short zoom) {
        this.field_5_page_break_zoom = zoom;
    }
    
    public void setNormalZoom(final short zoom) {
        this.field_6_normal_zoom = zoom;
    }
    
    public void setReserved(final int reserved) {
        this.field_7_reserved = reserved;
    }
    
    public short getOptions() {
        return this.field_1_options;
    }
    
    public boolean getDisplayFormulas() {
        return WindowTwoRecord.displayFormulas.isSet(this.field_1_options);
    }
    
    public boolean getDisplayGridlines() {
        return WindowTwoRecord.displayGridlines.isSet(this.field_1_options);
    }
    
    public boolean getDisplayRowColHeadings() {
        return WindowTwoRecord.displayRowColHeadings.isSet(this.field_1_options);
    }
    
    public boolean getFreezePanes() {
        return WindowTwoRecord.freezePanes.isSet(this.field_1_options);
    }
    
    public boolean getDisplayZeros() {
        return WindowTwoRecord.displayZeros.isSet(this.field_1_options);
    }
    
    public boolean getDefaultHeader() {
        return WindowTwoRecord.defaultHeader.isSet(this.field_1_options);
    }
    
    public boolean getArabic() {
        return WindowTwoRecord.arabic.isSet(this.field_1_options);
    }
    
    public boolean getDisplayGuts() {
        return WindowTwoRecord.displayGuts.isSet(this.field_1_options);
    }
    
    public boolean getFreezePanesNoSplit() {
        return WindowTwoRecord.freezePanesNoSplit.isSet(this.field_1_options);
    }
    
    public boolean getSelected() {
        return WindowTwoRecord.selected.isSet(this.field_1_options);
    }
    
    public boolean isActive() {
        return WindowTwoRecord.active.isSet(this.field_1_options);
    }
    
    public boolean getSavedInPageBreakPreview() {
        return WindowTwoRecord.savedInPageBreakPreview.isSet(this.field_1_options);
    }
    
    public short getTopRow() {
        return this.field_2_top_row;
    }
    
    public short getLeftCol() {
        return this.field_3_left_col;
    }
    
    public int getHeaderColor() {
        return this.field_4_header_color;
    }
    
    public short getPageBreakZoom() {
        return this.field_5_page_break_zoom;
    }
    
    public short getNormalZoom() {
        return this.field_6_normal_zoom;
    }
    
    public int getReserved() {
        return this.field_7_reserved;
    }
    
    @Override
    public String toString() {
        return "[WINDOW2]\n    .options        = " + Integer.toHexString(this.getOptions()) + "\n       .dispformulas= " + this.getDisplayFormulas() + "\n       .dispgridlins= " + this.getDisplayGridlines() + "\n       .disprcheadin= " + this.getDisplayRowColHeadings() + "\n       .freezepanes = " + this.getFreezePanes() + "\n       .displayzeros= " + this.getDisplayZeros() + "\n       .defaultheadr= " + this.getDefaultHeader() + "\n       .arabic      = " + this.getArabic() + "\n       .displayguts = " + this.getDisplayGuts() + "\n       .frzpnsnosplt= " + this.getFreezePanesNoSplit() + "\n       .selected    = " + this.getSelected() + "\n       .active       = " + this.isActive() + "\n       .svdinpgbrkpv= " + this.getSavedInPageBreakPreview() + "\n    .toprow         = " + Integer.toHexString(this.getTopRow()) + "\n    .leftcol        = " + Integer.toHexString(this.getLeftCol()) + "\n    .headercolor    = " + Integer.toHexString(this.getHeaderColor()) + "\n    .pagebreakzoom  = " + Integer.toHexString(this.getPageBreakZoom()) + "\n    .normalzoom     = " + Integer.toHexString(this.getNormalZoom()) + "\n    .reserved       = " + Integer.toHexString(this.getReserved()) + "\n[/WINDOW2]\n";
    }
    
    public void serialize(final LittleEndianOutput out) {
        out.writeShort(this.getOptions());
        out.writeShort(this.getTopRow());
        out.writeShort(this.getLeftCol());
        out.writeInt(this.getHeaderColor());
        out.writeShort(this.getPageBreakZoom());
        out.writeShort(this.getNormalZoom());
        out.writeInt(this.getReserved());
    }
    
    @Override
    protected int getDataSize() {
        return 18;
    }
    
    @Override
    public short getSid() {
        return 574;
    }
    
    @Deprecated
    @Removal(version = "5.0.0")
    public WindowTwoRecord clone() {
        return this.copy();
    }
    
    @Override
    public WindowTwoRecord copy() {
        return new WindowTwoRecord(this);
    }
    
    static {
        displayFormulas = BitFieldFactory.getInstance(1);
        displayGridlines = BitFieldFactory.getInstance(2);
        displayRowColHeadings = BitFieldFactory.getInstance(4);
        freezePanes = BitFieldFactory.getInstance(8);
        displayZeros = BitFieldFactory.getInstance(16);
        defaultHeader = BitFieldFactory.getInstance(32);
        arabic = BitFieldFactory.getInstance(64);
        displayGuts = BitFieldFactory.getInstance(128);
        freezePanesNoSplit = BitFieldFactory.getInstance(256);
        selected = BitFieldFactory.getInstance(512);
        active = BitFieldFactory.getInstance(1024);
        savedInPageBreakPreview = BitFieldFactory.getInstance(2048);
    }
}
