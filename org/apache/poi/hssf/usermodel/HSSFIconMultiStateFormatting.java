package org.apache.poi.hssf.usermodel;

import org.apache.poi.hssf.record.cf.IconMultiStateThreshold;
import org.apache.poi.ss.usermodel.ConditionalFormattingThreshold;
import org.apache.poi.hssf.record.cf.Threshold;
import org.apache.poi.hssf.record.CFRule12Record;
import org.apache.poi.ss.usermodel.IconMultiStateFormatting;

public final class HSSFIconMultiStateFormatting implements IconMultiStateFormatting
{
    private final HSSFSheet sheet;
    private final CFRule12Record cfRule12Record;
    private final org.apache.poi.hssf.record.cf.IconMultiStateFormatting iconFormatting;
    
    protected HSSFIconMultiStateFormatting(final CFRule12Record cfRule12Record, final HSSFSheet sheet) {
        this.sheet = sheet;
        this.cfRule12Record = cfRule12Record;
        this.iconFormatting = this.cfRule12Record.getMultiStateFormatting();
    }
    
    @Override
    public IconSet getIconSet() {
        return this.iconFormatting.getIconSet();
    }
    
    @Override
    public void setIconSet(final IconSet set) {
        this.iconFormatting.setIconSet(set);
    }
    
    @Override
    public boolean isIconOnly() {
        return this.iconFormatting.isIconOnly();
    }
    
    @Override
    public void setIconOnly(final boolean only) {
        this.iconFormatting.setIconOnly(only);
    }
    
    @Override
    public boolean isReversed() {
        return this.iconFormatting.isReversed();
    }
    
    @Override
    public void setReversed(final boolean reversed) {
        this.iconFormatting.setReversed(reversed);
    }
    
    @Override
    public HSSFConditionalFormattingThreshold[] getThresholds() {
        final Threshold[] t = this.iconFormatting.getThresholds();
        final HSSFConditionalFormattingThreshold[] ht = new HSSFConditionalFormattingThreshold[t.length];
        for (int i = 0; i < t.length; ++i) {
            ht[i] = new HSSFConditionalFormattingThreshold(t[i], this.sheet);
        }
        return ht;
    }
    
    @Override
    public void setThresholds(final ConditionalFormattingThreshold[] thresholds) {
        final Threshold[] t = new Threshold[thresholds.length];
        for (int i = 0; i < t.length; ++i) {
            t[i] = ((HSSFConditionalFormattingThreshold)thresholds[i]).getThreshold();
        }
        this.iconFormatting.setThresholds(t);
    }
    
    @Override
    public HSSFConditionalFormattingThreshold createThreshold() {
        return new HSSFConditionalFormattingThreshold(new IconMultiStateThreshold(), this.sheet);
    }
}
