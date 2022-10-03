package org.apache.poi.hssf.record.chart;

import org.apache.poi.util.BitFieldFactory;
import org.apache.poi.common.Duplicatable;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.util.Removal;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.util.HexDump;
import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.util.BitField;
import org.apache.poi.hssf.record.StandardRecord;

public final class TextRecord extends StandardRecord
{
    public static final short sid = 4133;
    private static final BitField dataLabelPlacement;
    private static final BitField autoColor;
    private static final BitField showKey;
    private static final BitField showValue;
    private static final BitField vertical;
    private static final BitField autoGeneratedText;
    private static final BitField generated;
    private static final BitField autoLabelDeleted;
    private static final BitField autoBackground;
    private static final BitField rotation;
    private static final BitField showCategoryLabelAsPercentage;
    private static final BitField showValueAsPercentage;
    private static final BitField showBubbleSizes;
    private static final BitField showLabel;
    public static final byte HORIZONTAL_ALIGNMENT_LEFT = 1;
    public static final byte HORIZONTAL_ALIGNMENT_CENTER = 2;
    public static final byte HORIZONTAL_ALIGNMENT_BOTTOM = 3;
    public static final byte HORIZONTAL_ALIGNMENT_JUSTIFY = 4;
    public static final byte VERTICAL_ALIGNMENT_TOP = 1;
    public static final byte VERTICAL_ALIGNMENT_CENTER = 2;
    public static final byte VERTICAL_ALIGNMENT_BOTTOM = 3;
    public static final byte VERTICAL_ALIGNMENT_JUSTIFY = 4;
    public static final short DISPLAY_MODE_TRANSPARENT = 1;
    public static final short DISPLAY_MODE_OPAQUE = 2;
    public static final short ROTATION_NONE = 0;
    public static final short ROTATION_TOP_TO_BOTTOM = 1;
    public static final short ROTATION_ROTATED_90_DEGREES = 2;
    public static final short ROTATION_ROTATED_90_DEGREES_CLOCKWISE = 3;
    public static final short DATA_LABEL_PLACEMENT_CHART_DEPENDENT = 0;
    public static final short DATA_LABEL_PLACEMENT_OUTSIDE = 1;
    public static final short DATA_LABEL_PLACEMENT_INSIDE = 2;
    public static final short DATA_LABEL_PLACEMENT_CENTER = 3;
    public static final short DATA_LABEL_PLACEMENT_AXIS = 4;
    public static final short DATA_LABEL_PLACEMENT_ABOVE = 5;
    public static final short DATA_LABEL_PLACEMENT_BELOW = 6;
    public static final short DATA_LABEL_PLACEMENT_LEFT = 7;
    public static final short DATA_LABEL_PLACEMENT_RIGHT = 8;
    public static final short DATA_LABEL_PLACEMENT_AUTO = 9;
    public static final short DATA_LABEL_PLACEMENT_USER_MOVED = 10;
    private byte field_1_horizontalAlignment;
    private byte field_2_verticalAlignment;
    private short field_3_displayMode;
    private int field_4_rgbColor;
    private int field_5_x;
    private int field_6_y;
    private int field_7_width;
    private int field_8_height;
    private short field_9_options1;
    private short field_10_indexOfColorValue;
    private short field_11_options2;
    private short field_12_textRotation;
    
    public TextRecord() {
    }
    
    public TextRecord(final TextRecord other) {
        super(other);
        this.field_1_horizontalAlignment = other.field_1_horizontalAlignment;
        this.field_2_verticalAlignment = other.field_2_verticalAlignment;
        this.field_3_displayMode = other.field_3_displayMode;
        this.field_4_rgbColor = other.field_4_rgbColor;
        this.field_5_x = other.field_5_x;
        this.field_6_y = other.field_6_y;
        this.field_7_width = other.field_7_width;
        this.field_8_height = other.field_8_height;
        this.field_9_options1 = other.field_9_options1;
        this.field_10_indexOfColorValue = other.field_10_indexOfColorValue;
        this.field_11_options2 = other.field_11_options2;
        this.field_12_textRotation = other.field_12_textRotation;
    }
    
    public TextRecord(final RecordInputStream in) {
        this.field_1_horizontalAlignment = in.readByte();
        this.field_2_verticalAlignment = in.readByte();
        this.field_3_displayMode = in.readShort();
        this.field_4_rgbColor = in.readInt();
        this.field_5_x = in.readInt();
        this.field_6_y = in.readInt();
        this.field_7_width = in.readInt();
        this.field_8_height = in.readInt();
        this.field_9_options1 = in.readShort();
        this.field_10_indexOfColorValue = in.readShort();
        this.field_11_options2 = in.readShort();
        this.field_12_textRotation = in.readShort();
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("[TEXT]\n");
        buffer.append("    .horizontalAlignment  = ").append("0x").append(HexDump.toHex(this.getHorizontalAlignment())).append(" (").append(this.getHorizontalAlignment()).append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("    .verticalAlignment    = ").append("0x").append(HexDump.toHex(this.getVerticalAlignment())).append(" (").append(this.getVerticalAlignment()).append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("    .displayMode          = ").append("0x").append(HexDump.toHex(this.getDisplayMode())).append(" (").append(this.getDisplayMode()).append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("    .rgbColor             = ").append("0x").append(HexDump.toHex(this.getRgbColor())).append(" (").append(this.getRgbColor()).append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("    .x                    = ").append("0x").append(HexDump.toHex(this.getX())).append(" (").append(this.getX()).append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("    .y                    = ").append("0x").append(HexDump.toHex(this.getY())).append(" (").append(this.getY()).append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("    .width                = ").append("0x").append(HexDump.toHex(this.getWidth())).append(" (").append(this.getWidth()).append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("    .height               = ").append("0x").append(HexDump.toHex(this.getHeight())).append(" (").append(this.getHeight()).append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("    .options1             = ").append("0x").append(HexDump.toHex(this.getOptions1())).append(" (").append(this.getOptions1()).append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("         .autoColor                = ").append(this.isAutoColor()).append('\n');
        buffer.append("         .showKey                  = ").append(this.isShowKey()).append('\n');
        buffer.append("         .showValue                = ").append(this.isShowValue()).append('\n');
        buffer.append("         .vertical                 = ").append(this.isVertical()).append('\n');
        buffer.append("         .autoGeneratedText        = ").append(this.isAutoGeneratedText()).append('\n');
        buffer.append("         .generated                = ").append(this.isGenerated()).append('\n');
        buffer.append("         .autoLabelDeleted         = ").append(this.isAutoLabelDeleted()).append('\n');
        buffer.append("         .autoBackground           = ").append(this.isAutoBackground()).append('\n');
        buffer.append("         .rotation                 = ").append(this.getRotation()).append('\n');
        buffer.append("         .showCategoryLabelAsPercentage     = ").append(this.isShowCategoryLabelAsPercentage()).append('\n');
        buffer.append("         .showValueAsPercentage     = ").append(this.isShowValueAsPercentage()).append('\n');
        buffer.append("         .showBubbleSizes          = ").append(this.isShowBubbleSizes()).append('\n');
        buffer.append("         .showLabel                = ").append(this.isShowLabel()).append('\n');
        buffer.append("    .indexOfColorValue    = ").append("0x").append(HexDump.toHex(this.getIndexOfColorValue())).append(" (").append(this.getIndexOfColorValue()).append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("    .options2             = ").append("0x").append(HexDump.toHex(this.getOptions2())).append(" (").append(this.getOptions2()).append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("         .dataLabelPlacement       = ").append(this.getDataLabelPlacement()).append('\n');
        buffer.append("    .textRotation         = ").append("0x").append(HexDump.toHex(this.getTextRotation())).append(" (").append(this.getTextRotation()).append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("[/TEXT]\n");
        return buffer.toString();
    }
    
    public void serialize(final LittleEndianOutput out) {
        out.writeByte(this.field_1_horizontalAlignment);
        out.writeByte(this.field_2_verticalAlignment);
        out.writeShort(this.field_3_displayMode);
        out.writeInt(this.field_4_rgbColor);
        out.writeInt(this.field_5_x);
        out.writeInt(this.field_6_y);
        out.writeInt(this.field_7_width);
        out.writeInt(this.field_8_height);
        out.writeShort(this.field_9_options1);
        out.writeShort(this.field_10_indexOfColorValue);
        out.writeShort(this.field_11_options2);
        out.writeShort(this.field_12_textRotation);
    }
    
    @Override
    protected int getDataSize() {
        return 32;
    }
    
    @Override
    public short getSid() {
        return 4133;
    }
    
    @Deprecated
    @Removal(version = "5.0.0")
    public TextRecord clone() {
        return this.copy();
    }
    
    @Override
    public TextRecord copy() {
        return new TextRecord(this);
    }
    
    public byte getHorizontalAlignment() {
        return this.field_1_horizontalAlignment;
    }
    
    public void setHorizontalAlignment(final byte field_1_horizontalAlignment) {
        this.field_1_horizontalAlignment = field_1_horizontalAlignment;
    }
    
    public byte getVerticalAlignment() {
        return this.field_2_verticalAlignment;
    }
    
    public void setVerticalAlignment(final byte field_2_verticalAlignment) {
        this.field_2_verticalAlignment = field_2_verticalAlignment;
    }
    
    public short getDisplayMode() {
        return this.field_3_displayMode;
    }
    
    public void setDisplayMode(final short field_3_displayMode) {
        this.field_3_displayMode = field_3_displayMode;
    }
    
    public int getRgbColor() {
        return this.field_4_rgbColor;
    }
    
    public void setRgbColor(final int field_4_rgbColor) {
        this.field_4_rgbColor = field_4_rgbColor;
    }
    
    public int getX() {
        return this.field_5_x;
    }
    
    public void setX(final int field_5_x) {
        this.field_5_x = field_5_x;
    }
    
    public int getY() {
        return this.field_6_y;
    }
    
    public void setY(final int field_6_y) {
        this.field_6_y = field_6_y;
    }
    
    public int getWidth() {
        return this.field_7_width;
    }
    
    public void setWidth(final int field_7_width) {
        this.field_7_width = field_7_width;
    }
    
    public int getHeight() {
        return this.field_8_height;
    }
    
    public void setHeight(final int field_8_height) {
        this.field_8_height = field_8_height;
    }
    
    public short getOptions1() {
        return this.field_9_options1;
    }
    
    public void setOptions1(final short field_9_options1) {
        this.field_9_options1 = field_9_options1;
    }
    
    public short getIndexOfColorValue() {
        return this.field_10_indexOfColorValue;
    }
    
    public void setIndexOfColorValue(final short field_10_indexOfColorValue) {
        this.field_10_indexOfColorValue = field_10_indexOfColorValue;
    }
    
    public short getOptions2() {
        return this.field_11_options2;
    }
    
    public void setOptions2(final short field_11_options2) {
        this.field_11_options2 = field_11_options2;
    }
    
    public short getTextRotation() {
        return this.field_12_textRotation;
    }
    
    public void setTextRotation(final short field_12_textRotation) {
        this.field_12_textRotation = field_12_textRotation;
    }
    
    public void setAutoColor(final boolean value) {
        this.field_9_options1 = TextRecord.autoColor.setShortBoolean(this.field_9_options1, value);
    }
    
    public boolean isAutoColor() {
        return TextRecord.autoColor.isSet(this.field_9_options1);
    }
    
    public void setShowKey(final boolean value) {
        this.field_9_options1 = TextRecord.showKey.setShortBoolean(this.field_9_options1, value);
    }
    
    public boolean isShowKey() {
        return TextRecord.showKey.isSet(this.field_9_options1);
    }
    
    public void setShowValue(final boolean value) {
        this.field_9_options1 = TextRecord.showValue.setShortBoolean(this.field_9_options1, value);
    }
    
    public boolean isShowValue() {
        return TextRecord.showValue.isSet(this.field_9_options1);
    }
    
    public void setVertical(final boolean value) {
        this.field_9_options1 = TextRecord.vertical.setShortBoolean(this.field_9_options1, value);
    }
    
    public boolean isVertical() {
        return TextRecord.vertical.isSet(this.field_9_options1);
    }
    
    public void setAutoGeneratedText(final boolean value) {
        this.field_9_options1 = TextRecord.autoGeneratedText.setShortBoolean(this.field_9_options1, value);
    }
    
    public boolean isAutoGeneratedText() {
        return TextRecord.autoGeneratedText.isSet(this.field_9_options1);
    }
    
    public void setGenerated(final boolean value) {
        this.field_9_options1 = TextRecord.generated.setShortBoolean(this.field_9_options1, value);
    }
    
    public boolean isGenerated() {
        return TextRecord.generated.isSet(this.field_9_options1);
    }
    
    public void setAutoLabelDeleted(final boolean value) {
        this.field_9_options1 = TextRecord.autoLabelDeleted.setShortBoolean(this.field_9_options1, value);
    }
    
    public boolean isAutoLabelDeleted() {
        return TextRecord.autoLabelDeleted.isSet(this.field_9_options1);
    }
    
    public void setAutoBackground(final boolean value) {
        this.field_9_options1 = TextRecord.autoBackground.setShortBoolean(this.field_9_options1, value);
    }
    
    public boolean isAutoBackground() {
        return TextRecord.autoBackground.isSet(this.field_9_options1);
    }
    
    public void setRotation(final short value) {
        this.field_9_options1 = TextRecord.rotation.setShortValue(this.field_9_options1, value);
    }
    
    public short getRotation() {
        return TextRecord.rotation.getShortValue(this.field_9_options1);
    }
    
    public void setShowCategoryLabelAsPercentage(final boolean value) {
        this.field_9_options1 = TextRecord.showCategoryLabelAsPercentage.setShortBoolean(this.field_9_options1, value);
    }
    
    public boolean isShowCategoryLabelAsPercentage() {
        return TextRecord.showCategoryLabelAsPercentage.isSet(this.field_9_options1);
    }
    
    public void setShowValueAsPercentage(final boolean value) {
        this.field_9_options1 = TextRecord.showValueAsPercentage.setShortBoolean(this.field_9_options1, value);
    }
    
    public boolean isShowValueAsPercentage() {
        return TextRecord.showValueAsPercentage.isSet(this.field_9_options1);
    }
    
    public void setShowBubbleSizes(final boolean value) {
        this.field_9_options1 = TextRecord.showBubbleSizes.setShortBoolean(this.field_9_options1, value);
    }
    
    public boolean isShowBubbleSizes() {
        return TextRecord.showBubbleSizes.isSet(this.field_9_options1);
    }
    
    public void setShowLabel(final boolean value) {
        this.field_9_options1 = TextRecord.showLabel.setShortBoolean(this.field_9_options1, value);
    }
    
    public boolean isShowLabel() {
        return TextRecord.showLabel.isSet(this.field_9_options1);
    }
    
    public void setDataLabelPlacement(final short value) {
        this.field_11_options2 = TextRecord.dataLabelPlacement.setShortValue(this.field_11_options2, value);
    }
    
    public short getDataLabelPlacement() {
        return TextRecord.dataLabelPlacement.getShortValue(this.field_11_options2);
    }
    
    static {
        dataLabelPlacement = BitFieldFactory.getInstance(15);
        autoColor = BitFieldFactory.getInstance(1);
        showKey = BitFieldFactory.getInstance(2);
        showValue = BitFieldFactory.getInstance(4);
        vertical = BitFieldFactory.getInstance(8);
        autoGeneratedText = BitFieldFactory.getInstance(16);
        generated = BitFieldFactory.getInstance(32);
        autoLabelDeleted = BitFieldFactory.getInstance(64);
        autoBackground = BitFieldFactory.getInstance(128);
        rotation = BitFieldFactory.getInstance(1792);
        showCategoryLabelAsPercentage = BitFieldFactory.getInstance(2048);
        showValueAsPercentage = BitFieldFactory.getInstance(4096);
        showBubbleSizes = BitFieldFactory.getInstance(8192);
        showLabel = BitFieldFactory.getInstance(16384);
    }
}