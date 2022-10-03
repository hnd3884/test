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

public final class ValueRangeRecord extends StandardRecord
{
    public static final short sid = 4127;
    private static final BitField automaticMinimum;
    private static final BitField automaticMaximum;
    private static final BitField automaticMajor;
    private static final BitField automaticMinor;
    private static final BitField automaticCategoryCrossing;
    private static final BitField logarithmicScale;
    private static final BitField valuesInReverse;
    private static final BitField crossCategoryAxisAtMaximum;
    private static final BitField reserved;
    private double field_1_minimumAxisValue;
    private double field_2_maximumAxisValue;
    private double field_3_majorIncrement;
    private double field_4_minorIncrement;
    private double field_5_categoryAxisCross;
    private short field_6_options;
    
    public ValueRangeRecord() {
    }
    
    public ValueRangeRecord(final ValueRangeRecord other) {
        super(other);
        this.field_1_minimumAxisValue = other.field_1_minimumAxisValue;
        this.field_2_maximumAxisValue = other.field_2_maximumAxisValue;
        this.field_3_majorIncrement = other.field_3_majorIncrement;
        this.field_4_minorIncrement = other.field_4_minorIncrement;
        this.field_5_categoryAxisCross = other.field_5_categoryAxisCross;
        this.field_6_options = other.field_6_options;
    }
    
    public ValueRangeRecord(final RecordInputStream in) {
        this.field_1_minimumAxisValue = in.readDouble();
        this.field_2_maximumAxisValue = in.readDouble();
        this.field_3_majorIncrement = in.readDouble();
        this.field_4_minorIncrement = in.readDouble();
        this.field_5_categoryAxisCross = in.readDouble();
        this.field_6_options = in.readShort();
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("[VALUERANGE]\n");
        buffer.append("    .minimumAxisValue     = ").append(" (").append(this.getMinimumAxisValue()).append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("    .maximumAxisValue     = ").append(" (").append(this.getMaximumAxisValue()).append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("    .majorIncrement       = ").append(" (").append(this.getMajorIncrement()).append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("    .minorIncrement       = ").append(" (").append(this.getMinorIncrement()).append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("    .categoryAxisCross    = ").append(" (").append(this.getCategoryAxisCross()).append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("    .options              = ").append("0x").append(HexDump.toHex(this.getOptions())).append(" (").append(this.getOptions()).append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("         .automaticMinimum         = ").append(this.isAutomaticMinimum()).append('\n');
        buffer.append("         .automaticMaximum         = ").append(this.isAutomaticMaximum()).append('\n');
        buffer.append("         .automaticMajor           = ").append(this.isAutomaticMajor()).append('\n');
        buffer.append("         .automaticMinor           = ").append(this.isAutomaticMinor()).append('\n');
        buffer.append("         .automaticCategoryCrossing     = ").append(this.isAutomaticCategoryCrossing()).append('\n');
        buffer.append("         .logarithmicScale         = ").append(this.isLogarithmicScale()).append('\n');
        buffer.append("         .valuesInReverse          = ").append(this.isValuesInReverse()).append('\n');
        buffer.append("         .crossCategoryAxisAtMaximum     = ").append(this.isCrossCategoryAxisAtMaximum()).append('\n');
        buffer.append("         .reserved                 = ").append(this.isReserved()).append('\n');
        buffer.append("[/VALUERANGE]\n");
        return buffer.toString();
    }
    
    public void serialize(final LittleEndianOutput out) {
        out.writeDouble(this.field_1_minimumAxisValue);
        out.writeDouble(this.field_2_maximumAxisValue);
        out.writeDouble(this.field_3_majorIncrement);
        out.writeDouble(this.field_4_minorIncrement);
        out.writeDouble(this.field_5_categoryAxisCross);
        out.writeShort(this.field_6_options);
    }
    
    @Override
    protected int getDataSize() {
        return 42;
    }
    
    @Override
    public short getSid() {
        return 4127;
    }
    
    @Deprecated
    @Removal(version = "5.0.0")
    public ValueRangeRecord clone() {
        return this.copy();
    }
    
    @Override
    public ValueRangeRecord copy() {
        return new ValueRangeRecord(this);
    }
    
    public double getMinimumAxisValue() {
        return this.field_1_minimumAxisValue;
    }
    
    public void setMinimumAxisValue(final double field_1_minimumAxisValue) {
        this.field_1_minimumAxisValue = field_1_minimumAxisValue;
    }
    
    public double getMaximumAxisValue() {
        return this.field_2_maximumAxisValue;
    }
    
    public void setMaximumAxisValue(final double field_2_maximumAxisValue) {
        this.field_2_maximumAxisValue = field_2_maximumAxisValue;
    }
    
    public double getMajorIncrement() {
        return this.field_3_majorIncrement;
    }
    
    public void setMajorIncrement(final double field_3_majorIncrement) {
        this.field_3_majorIncrement = field_3_majorIncrement;
    }
    
    public double getMinorIncrement() {
        return this.field_4_minorIncrement;
    }
    
    public void setMinorIncrement(final double field_4_minorIncrement) {
        this.field_4_minorIncrement = field_4_minorIncrement;
    }
    
    public double getCategoryAxisCross() {
        return this.field_5_categoryAxisCross;
    }
    
    public void setCategoryAxisCross(final double field_5_categoryAxisCross) {
        this.field_5_categoryAxisCross = field_5_categoryAxisCross;
    }
    
    public short getOptions() {
        return this.field_6_options;
    }
    
    public void setOptions(final short field_6_options) {
        this.field_6_options = field_6_options;
    }
    
    public void setAutomaticMinimum(final boolean value) {
        this.field_6_options = ValueRangeRecord.automaticMinimum.setShortBoolean(this.field_6_options, value);
    }
    
    public boolean isAutomaticMinimum() {
        return ValueRangeRecord.automaticMinimum.isSet(this.field_6_options);
    }
    
    public void setAutomaticMaximum(final boolean value) {
        this.field_6_options = ValueRangeRecord.automaticMaximum.setShortBoolean(this.field_6_options, value);
    }
    
    public boolean isAutomaticMaximum() {
        return ValueRangeRecord.automaticMaximum.isSet(this.field_6_options);
    }
    
    public void setAutomaticMajor(final boolean value) {
        this.field_6_options = ValueRangeRecord.automaticMajor.setShortBoolean(this.field_6_options, value);
    }
    
    public boolean isAutomaticMajor() {
        return ValueRangeRecord.automaticMajor.isSet(this.field_6_options);
    }
    
    public void setAutomaticMinor(final boolean value) {
        this.field_6_options = ValueRangeRecord.automaticMinor.setShortBoolean(this.field_6_options, value);
    }
    
    public boolean isAutomaticMinor() {
        return ValueRangeRecord.automaticMinor.isSet(this.field_6_options);
    }
    
    public void setAutomaticCategoryCrossing(final boolean value) {
        this.field_6_options = ValueRangeRecord.automaticCategoryCrossing.setShortBoolean(this.field_6_options, value);
    }
    
    public boolean isAutomaticCategoryCrossing() {
        return ValueRangeRecord.automaticCategoryCrossing.isSet(this.field_6_options);
    }
    
    public void setLogarithmicScale(final boolean value) {
        this.field_6_options = ValueRangeRecord.logarithmicScale.setShortBoolean(this.field_6_options, value);
    }
    
    public boolean isLogarithmicScale() {
        return ValueRangeRecord.logarithmicScale.isSet(this.field_6_options);
    }
    
    public void setValuesInReverse(final boolean value) {
        this.field_6_options = ValueRangeRecord.valuesInReverse.setShortBoolean(this.field_6_options, value);
    }
    
    public boolean isValuesInReverse() {
        return ValueRangeRecord.valuesInReverse.isSet(this.field_6_options);
    }
    
    public void setCrossCategoryAxisAtMaximum(final boolean value) {
        this.field_6_options = ValueRangeRecord.crossCategoryAxisAtMaximum.setShortBoolean(this.field_6_options, value);
    }
    
    public boolean isCrossCategoryAxisAtMaximum() {
        return ValueRangeRecord.crossCategoryAxisAtMaximum.isSet(this.field_6_options);
    }
    
    public void setReserved(final boolean value) {
        this.field_6_options = ValueRangeRecord.reserved.setShortBoolean(this.field_6_options, value);
    }
    
    public boolean isReserved() {
        return ValueRangeRecord.reserved.isSet(this.field_6_options);
    }
    
    static {
        automaticMinimum = BitFieldFactory.getInstance(1);
        automaticMaximum = BitFieldFactory.getInstance(2);
        automaticMajor = BitFieldFactory.getInstance(4);
        automaticMinor = BitFieldFactory.getInstance(8);
        automaticCategoryCrossing = BitFieldFactory.getInstance(16);
        logarithmicScale = BitFieldFactory.getInstance(32);
        valuesInReverse = BitFieldFactory.getInstance(64);
        crossCategoryAxisAtMaximum = BitFieldFactory.getInstance(128);
        reserved = BitFieldFactory.getInstance(256);
    }
}
