package org.apache.poi.xssf.usermodel;

import org.apache.poi.ss.usermodel.ConditionalFormattingThreshold;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCfvo;
import org.apache.poi.ss.usermodel.Color;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTColor;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTColorScale;
import org.apache.poi.ss.usermodel.ColorScaleFormatting;

public class XSSFColorScaleFormatting implements ColorScaleFormatting
{
    private CTColorScale _scale;
    private IndexedColorMap _indexedColorMap;
    
    XSSFColorScaleFormatting(final CTColorScale scale, final IndexedColorMap colorMap) {
        this._scale = scale;
        this._indexedColorMap = colorMap;
    }
    
    public int getNumControlPoints() {
        return this._scale.sizeOfCfvoArray();
    }
    
    public void setNumControlPoints(final int num) {
        while (num < this._scale.sizeOfCfvoArray()) {
            this._scale.removeCfvo(this._scale.sizeOfCfvoArray() - 1);
            this._scale.removeColor(this._scale.sizeOfColorArray() - 1);
        }
        while (num > this._scale.sizeOfCfvoArray()) {
            this._scale.addNewCfvo();
            this._scale.addNewColor();
        }
    }
    
    public XSSFColor[] getColors() {
        final CTColor[] ctcols = this._scale.getColorArray();
        final XSSFColor[] c = new XSSFColor[ctcols.length];
        for (int i = 0; i < ctcols.length; ++i) {
            c[i] = XSSFColor.from(ctcols[i], this._indexedColorMap);
        }
        return c;
    }
    
    public void setColors(final Color[] colors) {
        final CTColor[] ctcols = new CTColor[colors.length];
        for (int i = 0; i < colors.length; ++i) {
            ctcols[i] = ((XSSFColor)colors[i]).getCTColor();
        }
        this._scale.setColorArray(ctcols);
    }
    
    public XSSFConditionalFormattingThreshold[] getThresholds() {
        final CTCfvo[] cfvos = this._scale.getCfvoArray();
        final XSSFConditionalFormattingThreshold[] t = new XSSFConditionalFormattingThreshold[cfvos.length];
        for (int i = 0; i < cfvos.length; ++i) {
            t[i] = new XSSFConditionalFormattingThreshold(cfvos[i]);
        }
        return t;
    }
    
    public void setThresholds(final ConditionalFormattingThreshold[] thresholds) {
        final CTCfvo[] cfvos = new CTCfvo[thresholds.length];
        for (int i = 0; i < thresholds.length; ++i) {
            cfvos[i] = ((XSSFConditionalFormattingThreshold)thresholds[i]).getCTCfvo();
        }
        this._scale.setCfvoArray(cfvos);
    }
    
    public XSSFColor createColor() {
        return XSSFColor.from(this._scale.addNewColor(), this._indexedColorMap);
    }
    
    public XSSFConditionalFormattingThreshold createThreshold() {
        return new XSSFConditionalFormattingThreshold(this._scale.addNewCfvo());
    }
}
