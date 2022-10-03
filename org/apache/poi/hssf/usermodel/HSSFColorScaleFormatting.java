package org.apache.poi.hssf.usermodel;

import org.apache.poi.hssf.record.cf.ColorGradientThreshold;
import org.apache.poi.ss.usermodel.ConditionalFormattingThreshold;
import org.apache.poi.hssf.record.cf.Threshold;
import org.apache.poi.ss.usermodel.Color;
import org.apache.poi.hssf.record.common.ExtendedColor;
import org.apache.poi.hssf.record.cf.ColorGradientFormatting;
import org.apache.poi.hssf.record.CFRule12Record;
import org.apache.poi.ss.usermodel.ColorScaleFormatting;

public final class HSSFColorScaleFormatting implements ColorScaleFormatting
{
    private final HSSFSheet sheet;
    private final CFRule12Record cfRule12Record;
    private final ColorGradientFormatting colorFormatting;
    
    protected HSSFColorScaleFormatting(final CFRule12Record cfRule12Record, final HSSFSheet sheet) {
        this.sheet = sheet;
        this.cfRule12Record = cfRule12Record;
        this.colorFormatting = this.cfRule12Record.getColorGradientFormatting();
    }
    
    @Override
    public int getNumControlPoints() {
        return this.colorFormatting.getNumControlPoints();
    }
    
    @Override
    public void setNumControlPoints(final int num) {
        this.colorFormatting.setNumControlPoints(num);
    }
    
    @Override
    public HSSFExtendedColor[] getColors() {
        final ExtendedColor[] colors = this.colorFormatting.getColors();
        final HSSFExtendedColor[] hcolors = new HSSFExtendedColor[colors.length];
        for (int i = 0; i < colors.length; ++i) {
            hcolors[i] = new HSSFExtendedColor(colors[i]);
        }
        return hcolors;
    }
    
    @Override
    public void setColors(final Color[] colors) {
        final ExtendedColor[] cr = new ExtendedColor[colors.length];
        for (int i = 0; i < colors.length; ++i) {
            cr[i] = ((HSSFExtendedColor)colors[i]).getExtendedColor();
        }
        this.colorFormatting.setColors(cr);
    }
    
    @Override
    public HSSFConditionalFormattingThreshold[] getThresholds() {
        final Threshold[] t = this.colorFormatting.getThresholds();
        final HSSFConditionalFormattingThreshold[] ht = new HSSFConditionalFormattingThreshold[t.length];
        for (int i = 0; i < t.length; ++i) {
            ht[i] = new HSSFConditionalFormattingThreshold(t[i], this.sheet);
        }
        return ht;
    }
    
    @Override
    public void setThresholds(final ConditionalFormattingThreshold[] thresholds) {
        final ColorGradientThreshold[] t = new ColorGradientThreshold[thresholds.length];
        for (int i = 0; i < t.length; ++i) {
            final HSSFConditionalFormattingThreshold hssfT = (HSSFConditionalFormattingThreshold)thresholds[i];
            t[i] = (ColorGradientThreshold)hssfT.getThreshold();
        }
        this.colorFormatting.setThresholds(t);
    }
    
    @Override
    public HSSFConditionalFormattingThreshold createThreshold() {
        return new HSSFConditionalFormattingThreshold(new ColorGradientThreshold(), this.sheet);
    }
}
