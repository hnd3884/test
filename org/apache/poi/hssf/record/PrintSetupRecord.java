package org.apache.poi.hssf.record;

import org.apache.poi.util.BitFieldFactory;
import org.apache.poi.common.Duplicatable;
import org.apache.poi.util.Removal;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.util.BitField;

public final class PrintSetupRecord extends StandardRecord
{
    public static final short sid = 161;
    private static final BitField lefttoright;
    private static final BitField landscape;
    private static final BitField validsettings;
    private static final BitField nocolor;
    private static final BitField draft;
    private static final BitField notes;
    private static final BitField noOrientation;
    private static final BitField usepage;
    private short field_1_paper_size;
    private short field_2_scale;
    private short field_3_page_start;
    private short field_4_fit_width;
    private short field_5_fit_height;
    private short field_6_options;
    private short field_7_hresolution;
    private short field_8_vresolution;
    private double field_9_headermargin;
    private double field_10_footermargin;
    private short field_11_copies;
    
    public PrintSetupRecord() {
    }
    
    public PrintSetupRecord(final PrintSetupRecord other) {
        super(other);
        this.field_1_paper_size = other.field_1_paper_size;
        this.field_2_scale = other.field_2_scale;
        this.field_3_page_start = other.field_3_page_start;
        this.field_4_fit_width = other.field_4_fit_width;
        this.field_5_fit_height = other.field_5_fit_height;
        this.field_6_options = other.field_6_options;
        this.field_7_hresolution = other.field_7_hresolution;
        this.field_8_vresolution = other.field_8_vresolution;
        this.field_9_headermargin = other.field_9_headermargin;
        this.field_10_footermargin = other.field_10_footermargin;
        this.field_11_copies = other.field_11_copies;
    }
    
    public PrintSetupRecord(final RecordInputStream in) {
        this.field_1_paper_size = in.readShort();
        this.field_2_scale = in.readShort();
        this.field_3_page_start = in.readShort();
        this.field_4_fit_width = in.readShort();
        this.field_5_fit_height = in.readShort();
        this.field_6_options = in.readShort();
        this.field_7_hresolution = in.readShort();
        this.field_8_vresolution = in.readShort();
        this.field_9_headermargin = in.readDouble();
        this.field_10_footermargin = in.readDouble();
        this.field_11_copies = in.readShort();
    }
    
    public void setPaperSize(final short size) {
        this.field_1_paper_size = size;
    }
    
    public void setScale(final short scale) {
        this.field_2_scale = scale;
    }
    
    public void setPageStart(final short start) {
        this.field_3_page_start = start;
    }
    
    public void setFitWidth(final short width) {
        this.field_4_fit_width = width;
    }
    
    public void setFitHeight(final short height) {
        this.field_5_fit_height = height;
    }
    
    public void setOptions(final short options) {
        this.field_6_options = options;
    }
    
    public void setLeftToRight(final boolean ltor) {
        this.field_6_options = PrintSetupRecord.lefttoright.setShortBoolean(this.field_6_options, ltor);
    }
    
    public void setLandscape(final boolean ls) {
        this.field_6_options = PrintSetupRecord.landscape.setShortBoolean(this.field_6_options, ls);
    }
    
    public void setValidSettings(final boolean valid) {
        this.field_6_options = PrintSetupRecord.validsettings.setShortBoolean(this.field_6_options, valid);
    }
    
    public void setNoColor(final boolean mono) {
        this.field_6_options = PrintSetupRecord.nocolor.setShortBoolean(this.field_6_options, mono);
    }
    
    public void setDraft(final boolean d) {
        this.field_6_options = PrintSetupRecord.draft.setShortBoolean(this.field_6_options, d);
    }
    
    public void setNotes(final boolean printnotes) {
        this.field_6_options = PrintSetupRecord.notes.setShortBoolean(this.field_6_options, printnotes);
    }
    
    public void setNoOrientation(final boolean orientation) {
        this.field_6_options = PrintSetupRecord.noOrientation.setShortBoolean(this.field_6_options, orientation);
    }
    
    public void setUsePage(final boolean page) {
        this.field_6_options = PrintSetupRecord.usepage.setShortBoolean(this.field_6_options, page);
    }
    
    public void setHResolution(final short resolution) {
        this.field_7_hresolution = resolution;
    }
    
    public void setVResolution(final short resolution) {
        this.field_8_vresolution = resolution;
    }
    
    public void setHeaderMargin(final double headermargin) {
        this.field_9_headermargin = headermargin;
    }
    
    public void setFooterMargin(final double footermargin) {
        this.field_10_footermargin = footermargin;
    }
    
    public void setCopies(final short copies) {
        this.field_11_copies = copies;
    }
    
    public short getPaperSize() {
        return this.field_1_paper_size;
    }
    
    public short getScale() {
        return this.field_2_scale;
    }
    
    public short getPageStart() {
        return this.field_3_page_start;
    }
    
    public short getFitWidth() {
        return this.field_4_fit_width;
    }
    
    public short getFitHeight() {
        return this.field_5_fit_height;
    }
    
    public short getOptions() {
        return this.field_6_options;
    }
    
    public boolean getLeftToRight() {
        return PrintSetupRecord.lefttoright.isSet(this.field_6_options);
    }
    
    public boolean getLandscape() {
        return PrintSetupRecord.landscape.isSet(this.field_6_options);
    }
    
    public boolean getValidSettings() {
        return PrintSetupRecord.validsettings.isSet(this.field_6_options);
    }
    
    public boolean getNoColor() {
        return PrintSetupRecord.nocolor.isSet(this.field_6_options);
    }
    
    public boolean getDraft() {
        return PrintSetupRecord.draft.isSet(this.field_6_options);
    }
    
    public boolean getNotes() {
        return PrintSetupRecord.notes.isSet(this.field_6_options);
    }
    
    public boolean getNoOrientation() {
        return PrintSetupRecord.noOrientation.isSet(this.field_6_options);
    }
    
    public boolean getUsePage() {
        return PrintSetupRecord.usepage.isSet(this.field_6_options);
    }
    
    public short getHResolution() {
        return this.field_7_hresolution;
    }
    
    public short getVResolution() {
        return this.field_8_vresolution;
    }
    
    public double getHeaderMargin() {
        return this.field_9_headermargin;
    }
    
    public double getFooterMargin() {
        return this.field_10_footermargin;
    }
    
    public short getCopies() {
        return this.field_11_copies;
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("[PRINTSETUP]\n");
        buffer.append("    .papersize      = ").append(this.getPaperSize()).append("\n");
        buffer.append("    .scale          = ").append(this.getScale()).append("\n");
        buffer.append("    .pagestart      = ").append(this.getPageStart()).append("\n");
        buffer.append("    .fitwidth       = ").append(this.getFitWidth()).append("\n");
        buffer.append("    .fitheight      = ").append(this.getFitHeight()).append("\n");
        buffer.append("    .options        = ").append(this.getOptions()).append("\n");
        buffer.append("        .ltor       = ").append(this.getLeftToRight()).append("\n");
        buffer.append("        .landscape  = ").append(this.getLandscape()).append("\n");
        buffer.append("        .valid      = ").append(this.getValidSettings()).append("\n");
        buffer.append("        .mono       = ").append(this.getNoColor()).append("\n");
        buffer.append("        .draft      = ").append(this.getDraft()).append("\n");
        buffer.append("        .notes      = ").append(this.getNotes()).append("\n");
        buffer.append("        .noOrientat = ").append(this.getNoOrientation()).append("\n");
        buffer.append("        .usepage    = ").append(this.getUsePage()).append("\n");
        buffer.append("    .hresolution    = ").append(this.getHResolution()).append("\n");
        buffer.append("    .vresolution    = ").append(this.getVResolution()).append("\n");
        buffer.append("    .headermargin   = ").append(this.getHeaderMargin()).append("\n");
        buffer.append("    .footermargin   = ").append(this.getFooterMargin()).append("\n");
        buffer.append("    .copies         = ").append(this.getCopies()).append("\n");
        buffer.append("[/PRINTSETUP]\n");
        return buffer.toString();
    }
    
    public void serialize(final LittleEndianOutput out) {
        out.writeShort(this.getPaperSize());
        out.writeShort(this.getScale());
        out.writeShort(this.getPageStart());
        out.writeShort(this.getFitWidth());
        out.writeShort(this.getFitHeight());
        out.writeShort(this.getOptions());
        out.writeShort(this.getHResolution());
        out.writeShort(this.getVResolution());
        out.writeDouble(this.getHeaderMargin());
        out.writeDouble(this.getFooterMargin());
        out.writeShort(this.getCopies());
    }
    
    @Override
    protected int getDataSize() {
        return 34;
    }
    
    @Override
    public short getSid() {
        return 161;
    }
    
    @Deprecated
    @Removal(version = "5.0.0")
    public PrintSetupRecord clone() {
        return this.copy();
    }
    
    @Override
    public PrintSetupRecord copy() {
        return new PrintSetupRecord(this);
    }
    
    static {
        lefttoright = BitFieldFactory.getInstance(1);
        landscape = BitFieldFactory.getInstance(2);
        validsettings = BitFieldFactory.getInstance(4);
        nocolor = BitFieldFactory.getInstance(8);
        draft = BitFieldFactory.getInstance(16);
        notes = BitFieldFactory.getInstance(32);
        noOrientation = BitFieldFactory.getInstance(64);
        usepage = BitFieldFactory.getInstance(128);
    }
}
