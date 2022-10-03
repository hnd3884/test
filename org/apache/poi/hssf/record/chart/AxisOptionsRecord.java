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

public final class AxisOptionsRecord extends StandardRecord
{
    public static final short sid = 4194;
    private static final BitField defaultMinimum;
    private static final BitField defaultMaximum;
    private static final BitField defaultMajor;
    private static final BitField defaultMinorUnit;
    private static final BitField isDate;
    private static final BitField defaultBase;
    private static final BitField defaultCross;
    private static final BitField defaultDateSettings;
    private short field_1_minimumCategory;
    private short field_2_maximumCategory;
    private short field_3_majorUnitValue;
    private short field_4_majorUnit;
    private short field_5_minorUnitValue;
    private short field_6_minorUnit;
    private short field_7_baseUnit;
    private short field_8_crossingPoint;
    private short field_9_options;
    
    public AxisOptionsRecord() {
    }
    
    public AxisOptionsRecord(final AxisOptionsRecord other) {
        super(other);
        this.field_1_minimumCategory = other.field_1_minimumCategory;
        this.field_2_maximumCategory = other.field_2_maximumCategory;
        this.field_3_majorUnitValue = other.field_3_majorUnitValue;
        this.field_4_majorUnit = other.field_4_majorUnit;
        this.field_5_minorUnitValue = other.field_5_minorUnitValue;
        this.field_6_minorUnit = other.field_6_minorUnit;
        this.field_7_baseUnit = other.field_7_baseUnit;
        this.field_8_crossingPoint = other.field_8_crossingPoint;
        this.field_9_options = other.field_9_options;
    }
    
    public AxisOptionsRecord(final RecordInputStream in) {
        this.field_1_minimumCategory = in.readShort();
        this.field_2_maximumCategory = in.readShort();
        this.field_3_majorUnitValue = in.readShort();
        this.field_4_majorUnit = in.readShort();
        this.field_5_minorUnitValue = in.readShort();
        this.field_6_minorUnit = in.readShort();
        this.field_7_baseUnit = in.readShort();
        this.field_8_crossingPoint = in.readShort();
        this.field_9_options = in.readShort();
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("[AXCEXT]\n");
        buffer.append("    .minimumCategory      = ").append("0x").append(HexDump.toHex(this.getMinimumCategory())).append(" (").append(this.getMinimumCategory()).append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("    .maximumCategory      = ").append("0x").append(HexDump.toHex(this.getMaximumCategory())).append(" (").append(this.getMaximumCategory()).append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("    .majorUnitValue       = ").append("0x").append(HexDump.toHex(this.getMajorUnitValue())).append(" (").append(this.getMajorUnitValue()).append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("    .majorUnit            = ").append("0x").append(HexDump.toHex(this.getMajorUnit())).append(" (").append(this.getMajorUnit()).append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("    .minorUnitValue       = ").append("0x").append(HexDump.toHex(this.getMinorUnitValue())).append(" (").append(this.getMinorUnitValue()).append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("    .minorUnit            = ").append("0x").append(HexDump.toHex(this.getMinorUnit())).append(" (").append(this.getMinorUnit()).append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("    .baseUnit             = ").append("0x").append(HexDump.toHex(this.getBaseUnit())).append(" (").append(this.getBaseUnit()).append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("    .crossingPoint        = ").append("0x").append(HexDump.toHex(this.getCrossingPoint())).append(" (").append(this.getCrossingPoint()).append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("    .options              = ").append("0x").append(HexDump.toHex(this.getOptions())).append(" (").append(this.getOptions()).append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("         .defaultMinimum           = ").append(this.isDefaultMinimum()).append('\n');
        buffer.append("         .defaultMaximum           = ").append(this.isDefaultMaximum()).append('\n');
        buffer.append("         .defaultMajor             = ").append(this.isDefaultMajor()).append('\n');
        buffer.append("         .defaultMinorUnit         = ").append(this.isDefaultMinorUnit()).append('\n');
        buffer.append("         .isDate                   = ").append(this.isIsDate()).append('\n');
        buffer.append("         .defaultBase              = ").append(this.isDefaultBase()).append('\n');
        buffer.append("         .defaultCross             = ").append(this.isDefaultCross()).append('\n');
        buffer.append("         .defaultDateSettings      = ").append(this.isDefaultDateSettings()).append('\n');
        buffer.append("[/AXCEXT]\n");
        return buffer.toString();
    }
    
    public void serialize(final LittleEndianOutput out) {
        out.writeShort(this.field_1_minimumCategory);
        out.writeShort(this.field_2_maximumCategory);
        out.writeShort(this.field_3_majorUnitValue);
        out.writeShort(this.field_4_majorUnit);
        out.writeShort(this.field_5_minorUnitValue);
        out.writeShort(this.field_6_minorUnit);
        out.writeShort(this.field_7_baseUnit);
        out.writeShort(this.field_8_crossingPoint);
        out.writeShort(this.field_9_options);
    }
    
    @Override
    protected int getDataSize() {
        return 18;
    }
    
    @Override
    public short getSid() {
        return 4194;
    }
    
    @Deprecated
    @Removal(version = "5.0.0")
    public AxisOptionsRecord clone() {
        return this.copy();
    }
    
    public short getMinimumCategory() {
        return this.field_1_minimumCategory;
    }
    
    public void setMinimumCategory(final short field_1_minimumCategory) {
        this.field_1_minimumCategory = field_1_minimumCategory;
    }
    
    public short getMaximumCategory() {
        return this.field_2_maximumCategory;
    }
    
    public void setMaximumCategory(final short field_2_maximumCategory) {
        this.field_2_maximumCategory = field_2_maximumCategory;
    }
    
    public short getMajorUnitValue() {
        return this.field_3_majorUnitValue;
    }
    
    public void setMajorUnitValue(final short field_3_majorUnitValue) {
        this.field_3_majorUnitValue = field_3_majorUnitValue;
    }
    
    public short getMajorUnit() {
        return this.field_4_majorUnit;
    }
    
    public void setMajorUnit(final short field_4_majorUnit) {
        this.field_4_majorUnit = field_4_majorUnit;
    }
    
    public short getMinorUnitValue() {
        return this.field_5_minorUnitValue;
    }
    
    public void setMinorUnitValue(final short field_5_minorUnitValue) {
        this.field_5_minorUnitValue = field_5_minorUnitValue;
    }
    
    public short getMinorUnit() {
        return this.field_6_minorUnit;
    }
    
    public void setMinorUnit(final short field_6_minorUnit) {
        this.field_6_minorUnit = field_6_minorUnit;
    }
    
    public short getBaseUnit() {
        return this.field_7_baseUnit;
    }
    
    public void setBaseUnit(final short field_7_baseUnit) {
        this.field_7_baseUnit = field_7_baseUnit;
    }
    
    public short getCrossingPoint() {
        return this.field_8_crossingPoint;
    }
    
    public void setCrossingPoint(final short field_8_crossingPoint) {
        this.field_8_crossingPoint = field_8_crossingPoint;
    }
    
    public short getOptions() {
        return this.field_9_options;
    }
    
    public void setOptions(final short field_9_options) {
        this.field_9_options = field_9_options;
    }
    
    public void setDefaultMinimum(final boolean value) {
        this.field_9_options = AxisOptionsRecord.defaultMinimum.setShortBoolean(this.field_9_options, value);
    }
    
    public boolean isDefaultMinimum() {
        return AxisOptionsRecord.defaultMinimum.isSet(this.field_9_options);
    }
    
    public void setDefaultMaximum(final boolean value) {
        this.field_9_options = AxisOptionsRecord.defaultMaximum.setShortBoolean(this.field_9_options, value);
    }
    
    public boolean isDefaultMaximum() {
        return AxisOptionsRecord.defaultMaximum.isSet(this.field_9_options);
    }
    
    public void setDefaultMajor(final boolean value) {
        this.field_9_options = AxisOptionsRecord.defaultMajor.setShortBoolean(this.field_9_options, value);
    }
    
    public boolean isDefaultMajor() {
        return AxisOptionsRecord.defaultMajor.isSet(this.field_9_options);
    }
    
    public void setDefaultMinorUnit(final boolean value) {
        this.field_9_options = AxisOptionsRecord.defaultMinorUnit.setShortBoolean(this.field_9_options, value);
    }
    
    public boolean isDefaultMinorUnit() {
        return AxisOptionsRecord.defaultMinorUnit.isSet(this.field_9_options);
    }
    
    public void setIsDate(final boolean value) {
        this.field_9_options = AxisOptionsRecord.isDate.setShortBoolean(this.field_9_options, value);
    }
    
    public boolean isIsDate() {
        return AxisOptionsRecord.isDate.isSet(this.field_9_options);
    }
    
    public void setDefaultBase(final boolean value) {
        this.field_9_options = AxisOptionsRecord.defaultBase.setShortBoolean(this.field_9_options, value);
    }
    
    public boolean isDefaultBase() {
        return AxisOptionsRecord.defaultBase.isSet(this.field_9_options);
    }
    
    public void setDefaultCross(final boolean value) {
        this.field_9_options = AxisOptionsRecord.defaultCross.setShortBoolean(this.field_9_options, value);
    }
    
    public boolean isDefaultCross() {
        return AxisOptionsRecord.defaultCross.isSet(this.field_9_options);
    }
    
    public void setDefaultDateSettings(final boolean value) {
        this.field_9_options = AxisOptionsRecord.defaultDateSettings.setShortBoolean(this.field_9_options, value);
    }
    
    public boolean isDefaultDateSettings() {
        return AxisOptionsRecord.defaultDateSettings.isSet(this.field_9_options);
    }
    
    @Override
    public AxisOptionsRecord copy() {
        return new AxisOptionsRecord(this);
    }
    
    static {
        defaultMinimum = BitFieldFactory.getInstance(1);
        defaultMaximum = BitFieldFactory.getInstance(2);
        defaultMajor = BitFieldFactory.getInstance(4);
        defaultMinorUnit = BitFieldFactory.getInstance(8);
        isDate = BitFieldFactory.getInstance(16);
        defaultBase = BitFieldFactory.getInstance(32);
        defaultCross = BitFieldFactory.getInstance(64);
        defaultDateSettings = BitFieldFactory.getInstance(128);
    }
}
