package org.apache.poi.hssf.record;

import org.apache.poi.util.BitFieldFactory;
import org.apache.poi.common.Duplicatable;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.util.BitField;

public final class WindowOneRecord extends StandardRecord
{
    public static final short sid = 61;
    private short field_1_h_hold;
    private short field_2_v_hold;
    private short field_3_width;
    private short field_4_height;
    private short field_5_options;
    private static final BitField hidden;
    private static final BitField iconic;
    private static final BitField reserved;
    private static final BitField hscroll;
    private static final BitField vscroll;
    private static final BitField tabs;
    private int field_6_active_sheet;
    private int field_7_first_visible_tab;
    private short field_8_num_selected_tabs;
    private short field_9_tab_width_ratio;
    
    public WindowOneRecord() {
    }
    
    public WindowOneRecord(final WindowOneRecord other) {
        super(other);
        this.field_1_h_hold = other.field_1_h_hold;
        this.field_2_v_hold = other.field_2_v_hold;
        this.field_3_width = other.field_3_width;
        this.field_4_height = other.field_4_height;
        this.field_5_options = other.field_5_options;
        this.field_6_active_sheet = other.field_6_active_sheet;
        this.field_7_first_visible_tab = other.field_7_first_visible_tab;
        this.field_8_num_selected_tabs = other.field_8_num_selected_tabs;
        this.field_9_tab_width_ratio = other.field_9_tab_width_ratio;
    }
    
    public WindowOneRecord(final RecordInputStream in) {
        this.field_1_h_hold = in.readShort();
        this.field_2_v_hold = in.readShort();
        this.field_3_width = in.readShort();
        this.field_4_height = in.readShort();
        this.field_5_options = in.readShort();
        this.field_6_active_sheet = in.readShort();
        this.field_7_first_visible_tab = in.readShort();
        this.field_8_num_selected_tabs = in.readShort();
        this.field_9_tab_width_ratio = in.readShort();
    }
    
    public void setHorizontalHold(final short h) {
        this.field_1_h_hold = h;
    }
    
    public void setVerticalHold(final short v) {
        this.field_2_v_hold = v;
    }
    
    public void setWidth(final short w) {
        this.field_3_width = w;
    }
    
    public void setHeight(final short h) {
        this.field_4_height = h;
    }
    
    public void setOptions(final short o) {
        this.field_5_options = o;
    }
    
    public void setHidden(final boolean ishidden) {
        this.field_5_options = WindowOneRecord.hidden.setShortBoolean(this.field_5_options, ishidden);
    }
    
    public void setIconic(final boolean isiconic) {
        this.field_5_options = WindowOneRecord.iconic.setShortBoolean(this.field_5_options, isiconic);
    }
    
    public void setDisplayHorizonalScrollbar(final boolean scroll) {
        this.field_5_options = WindowOneRecord.hscroll.setShortBoolean(this.field_5_options, scroll);
    }
    
    public void setDisplayVerticalScrollbar(final boolean scroll) {
        this.field_5_options = WindowOneRecord.vscroll.setShortBoolean(this.field_5_options, scroll);
    }
    
    public void setDisplayTabs(final boolean disptabs) {
        this.field_5_options = WindowOneRecord.tabs.setShortBoolean(this.field_5_options, disptabs);
    }
    
    public void setActiveSheetIndex(final int index) {
        this.field_6_active_sheet = index;
    }
    
    public void setFirstVisibleTab(final int t) {
        this.field_7_first_visible_tab = t;
    }
    
    public void setNumSelectedTabs(final short n) {
        this.field_8_num_selected_tabs = n;
    }
    
    public void setTabWidthRatio(final short r) {
        this.field_9_tab_width_ratio = r;
    }
    
    public short getHorizontalHold() {
        return this.field_1_h_hold;
    }
    
    public short getVerticalHold() {
        return this.field_2_v_hold;
    }
    
    public short getWidth() {
        return this.field_3_width;
    }
    
    public short getHeight() {
        return this.field_4_height;
    }
    
    public short getOptions() {
        return this.field_5_options;
    }
    
    public boolean getHidden() {
        return WindowOneRecord.hidden.isSet(this.field_5_options);
    }
    
    public boolean getIconic() {
        return WindowOneRecord.iconic.isSet(this.field_5_options);
    }
    
    public boolean getDisplayHorizontalScrollbar() {
        return WindowOneRecord.hscroll.isSet(this.field_5_options);
    }
    
    public boolean getDisplayVerticalScrollbar() {
        return WindowOneRecord.vscroll.isSet(this.field_5_options);
    }
    
    public boolean getDisplayTabs() {
        return WindowOneRecord.tabs.isSet(this.field_5_options);
    }
    
    public int getActiveSheetIndex() {
        return this.field_6_active_sheet;
    }
    
    public int getFirstVisibleTab() {
        return this.field_7_first_visible_tab;
    }
    
    public short getNumSelectedTabs() {
        return this.field_8_num_selected_tabs;
    }
    
    public short getTabWidthRatio() {
        return this.field_9_tab_width_ratio;
    }
    
    @Override
    public String toString() {
        return "[WINDOW1]\n    .h_hold          = " + Integer.toHexString(this.getHorizontalHold()) + "\n    .v_hold          = " + Integer.toHexString(this.getVerticalHold()) + "\n    .width           = " + Integer.toHexString(this.getWidth()) + "\n    .height          = " + Integer.toHexString(this.getHeight()) + "\n    .options         = " + Integer.toHexString(this.getOptions()) + "\n        .hidden      = " + this.getHidden() + "\n        .iconic      = " + this.getIconic() + "\n        .hscroll     = " + this.getDisplayHorizontalScrollbar() + "\n        .vscroll     = " + this.getDisplayVerticalScrollbar() + "\n        .tabs        = " + this.getDisplayTabs() + "\n    .activeSheet     = " + Integer.toHexString(this.getActiveSheetIndex()) + "\n    .firstVisibleTab    = " + Integer.toHexString(this.getFirstVisibleTab()) + "\n    .numselectedtabs = " + Integer.toHexString(this.getNumSelectedTabs()) + "\n    .tabwidthratio   = " + Integer.toHexString(this.getTabWidthRatio()) + "\n[/WINDOW1]\n";
    }
    
    public void serialize(final LittleEndianOutput out) {
        out.writeShort(this.getHorizontalHold());
        out.writeShort(this.getVerticalHold());
        out.writeShort(this.getWidth());
        out.writeShort(this.getHeight());
        out.writeShort(this.getOptions());
        out.writeShort(this.getActiveSheetIndex());
        out.writeShort(this.getFirstVisibleTab());
        out.writeShort(this.getNumSelectedTabs());
        out.writeShort(this.getTabWidthRatio());
    }
    
    @Override
    protected int getDataSize() {
        return 18;
    }
    
    @Override
    public short getSid() {
        return 61;
    }
    
    @Override
    public WindowOneRecord copy() {
        return new WindowOneRecord(this);
    }
    
    static {
        hidden = BitFieldFactory.getInstance(1);
        iconic = BitFieldFactory.getInstance(2);
        reserved = BitFieldFactory.getInstance(4);
        hscroll = BitFieldFactory.getInstance(8);
        vscroll = BitFieldFactory.getInstance(16);
        tabs = BitFieldFactory.getInstance(32);
    }
}
