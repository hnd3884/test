package org.apache.poi.hssf.record.cf;

import org.apache.poi.util.BitFieldFactory;
import org.apache.poi.util.POILogFactory;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.util.Removal;
import org.apache.poi.util.LittleEndianInput;
import java.util.function.Function;
import java.util.stream.Stream;
import org.apache.poi.util.BitField;
import org.apache.poi.util.POILogger;
import org.apache.poi.common.Duplicatable;

public final class IconMultiStateFormatting implements Duplicatable
{
    private static final POILogger log;
    private org.apache.poi.ss.usermodel.IconMultiStateFormatting.IconSet iconSet;
    private byte options;
    private Threshold[] thresholds;
    private static BitField iconOnly;
    private static BitField reversed;
    
    public IconMultiStateFormatting() {
        this.iconSet = org.apache.poi.ss.usermodel.IconMultiStateFormatting.IconSet.GYR_3_TRAFFIC_LIGHTS;
        this.options = 0;
        this.thresholds = new Threshold[this.iconSet.num];
    }
    
    public IconMultiStateFormatting(final IconMultiStateFormatting other) {
        this.iconSet = other.iconSet;
        this.options = other.options;
        if (other.thresholds != null) {
            this.thresholds = Stream.of(other.thresholds).map((Function<? super Threshold, ?>)Threshold::copy).toArray(Threshold[]::new);
        }
    }
    
    public IconMultiStateFormatting(final LittleEndianInput in) {
        in.readShort();
        in.readByte();
        final int num = in.readByte();
        final int set = in.readByte();
        this.iconSet = org.apache.poi.ss.usermodel.IconMultiStateFormatting.IconSet.byId(set);
        if (this.iconSet.num != num) {
            IconMultiStateFormatting.log.log(5, "Inconsistent Icon Set defintion, found " + this.iconSet + " but defined as " + num + " entries");
        }
        this.options = in.readByte();
        this.thresholds = new Threshold[this.iconSet.num];
        for (int i = 0; i < this.thresholds.length; ++i) {
            this.thresholds[i] = new IconMultiStateThreshold(in);
        }
    }
    
    public org.apache.poi.ss.usermodel.IconMultiStateFormatting.IconSet getIconSet() {
        return this.iconSet;
    }
    
    public void setIconSet(final org.apache.poi.ss.usermodel.IconMultiStateFormatting.IconSet set) {
        this.iconSet = set;
    }
    
    public Threshold[] getThresholds() {
        return this.thresholds;
    }
    
    public void setThresholds(final Threshold[] thresholds) {
        this.thresholds = (Threshold[])((thresholds == null) ? null : ((Threshold[])thresholds.clone()));
    }
    
    public boolean isIconOnly() {
        return this.getOptionFlag(IconMultiStateFormatting.iconOnly);
    }
    
    public void setIconOnly(final boolean only) {
        this.setOptionFlag(only, IconMultiStateFormatting.iconOnly);
    }
    
    public boolean isReversed() {
        return this.getOptionFlag(IconMultiStateFormatting.reversed);
    }
    
    public void setReversed(final boolean rev) {
        this.setOptionFlag(rev, IconMultiStateFormatting.reversed);
    }
    
    private boolean getOptionFlag(final BitField field) {
        final int value = field.getValue(this.options);
        return value != 0;
    }
    
    private void setOptionFlag(final boolean option, final BitField field) {
        this.options = field.setByteBoolean(this.options, option);
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("    [Icon Formatting]\n");
        buffer.append("          .icon_set = ").append(this.iconSet).append("\n");
        buffer.append("          .icon_only= ").append(this.isIconOnly()).append("\n");
        buffer.append("          .reversed = ").append(this.isReversed()).append("\n");
        for (final Threshold t : this.thresholds) {
            buffer.append(t);
        }
        buffer.append("    [/Icon Formatting]\n");
        return buffer.toString();
    }
    
    @Deprecated
    @Removal(version = "5.0.0")
    public IconMultiStateFormatting clone() {
        return this.copy();
    }
    
    @Override
    public IconMultiStateFormatting copy() {
        return new IconMultiStateFormatting(this);
    }
    
    public int getDataLength() {
        int len = 6;
        for (final Threshold t : this.thresholds) {
            len += t.getDataLength();
        }
        return len;
    }
    
    public void serialize(final LittleEndianOutput out) {
        out.writeShort(0);
        out.writeByte(0);
        out.writeByte(this.iconSet.num);
        out.writeByte(this.iconSet.id);
        out.writeByte(this.options);
        for (final Threshold t : this.thresholds) {
            t.serialize(out);
        }
    }
    
    static {
        log = POILogFactory.getLogger(IconMultiStateFormatting.class);
        IconMultiStateFormatting.iconOnly = BitFieldFactory.getInstance(1);
        IconMultiStateFormatting.reversed = BitFieldFactory.getInstance(4);
    }
}
