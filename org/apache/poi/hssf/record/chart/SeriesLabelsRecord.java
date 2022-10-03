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

public final class SeriesLabelsRecord extends StandardRecord
{
    public static final short sid = 4108;
    private static final BitField showActual;
    private static final BitField showPercent;
    private static final BitField labelAsPercentage;
    private static final BitField smoothedLine;
    private static final BitField showLabel;
    private static final BitField showBubbleSizes;
    private short field_1_formatFlags;
    
    public SeriesLabelsRecord() {
    }
    
    public SeriesLabelsRecord(final SeriesLabelsRecord other) {
        super(other);
        this.field_1_formatFlags = other.field_1_formatFlags;
    }
    
    public SeriesLabelsRecord(final RecordInputStream in) {
        this.field_1_formatFlags = in.readShort();
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("[ATTACHEDLABEL]\n");
        buffer.append("    .formatFlags          = ").append("0x").append(HexDump.toHex(this.getFormatFlags())).append(" (").append(this.getFormatFlags()).append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("         .showActual               = ").append(this.isShowActual()).append('\n');
        buffer.append("         .showPercent              = ").append(this.isShowPercent()).append('\n');
        buffer.append("         .labelAsPercentage        = ").append(this.isLabelAsPercentage()).append('\n');
        buffer.append("         .smoothedLine             = ").append(this.isSmoothedLine()).append('\n');
        buffer.append("         .showLabel                = ").append(this.isShowLabel()).append('\n');
        buffer.append("         .showBubbleSizes          = ").append(this.isShowBubbleSizes()).append('\n');
        buffer.append("[/ATTACHEDLABEL]\n");
        return buffer.toString();
    }
    
    public void serialize(final LittleEndianOutput out) {
        out.writeShort(this.field_1_formatFlags);
    }
    
    @Override
    protected int getDataSize() {
        return 2;
    }
    
    @Override
    public short getSid() {
        return 4108;
    }
    
    @Deprecated
    @Removal(version = "5.0.0")
    public SeriesLabelsRecord clone() {
        return this.copy();
    }
    
    @Override
    public SeriesLabelsRecord copy() {
        return new SeriesLabelsRecord(this);
    }
    
    public short getFormatFlags() {
        return this.field_1_formatFlags;
    }
    
    public void setFormatFlags(final short field_1_formatFlags) {
        this.field_1_formatFlags = field_1_formatFlags;
    }
    
    public void setShowActual(final boolean value) {
        this.field_1_formatFlags = SeriesLabelsRecord.showActual.setShortBoolean(this.field_1_formatFlags, value);
    }
    
    public boolean isShowActual() {
        return SeriesLabelsRecord.showActual.isSet(this.field_1_formatFlags);
    }
    
    public void setShowPercent(final boolean value) {
        this.field_1_formatFlags = SeriesLabelsRecord.showPercent.setShortBoolean(this.field_1_formatFlags, value);
    }
    
    public boolean isShowPercent() {
        return SeriesLabelsRecord.showPercent.isSet(this.field_1_formatFlags);
    }
    
    public void setLabelAsPercentage(final boolean value) {
        this.field_1_formatFlags = SeriesLabelsRecord.labelAsPercentage.setShortBoolean(this.field_1_formatFlags, value);
    }
    
    public boolean isLabelAsPercentage() {
        return SeriesLabelsRecord.labelAsPercentage.isSet(this.field_1_formatFlags);
    }
    
    public void setSmoothedLine(final boolean value) {
        this.field_1_formatFlags = SeriesLabelsRecord.smoothedLine.setShortBoolean(this.field_1_formatFlags, value);
    }
    
    public boolean isSmoothedLine() {
        return SeriesLabelsRecord.smoothedLine.isSet(this.field_1_formatFlags);
    }
    
    public void setShowLabel(final boolean value) {
        this.field_1_formatFlags = SeriesLabelsRecord.showLabel.setShortBoolean(this.field_1_formatFlags, value);
    }
    
    public boolean isShowLabel() {
        return SeriesLabelsRecord.showLabel.isSet(this.field_1_formatFlags);
    }
    
    public void setShowBubbleSizes(final boolean value) {
        this.field_1_formatFlags = SeriesLabelsRecord.showBubbleSizes.setShortBoolean(this.field_1_formatFlags, value);
    }
    
    public boolean isShowBubbleSizes() {
        return SeriesLabelsRecord.showBubbleSizes.isSet(this.field_1_formatFlags);
    }
    
    static {
        showActual = BitFieldFactory.getInstance(1);
        showPercent = BitFieldFactory.getInstance(2);
        labelAsPercentage = BitFieldFactory.getInstance(4);
        smoothedLine = BitFieldFactory.getInstance(8);
        showLabel = BitFieldFactory.getInstance(16);
        showBubbleSizes = BitFieldFactory.getInstance(32);
    }
}
