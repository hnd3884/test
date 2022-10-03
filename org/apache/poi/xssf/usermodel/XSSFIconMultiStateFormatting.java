package org.apache.poi.xssf.usermodel;

import org.apache.poi.ss.usermodel.ConditionalFormattingThreshold;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCfvo;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STIconSetType;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTIconSet;
import org.apache.poi.ss.usermodel.IconMultiStateFormatting;

public class XSSFIconMultiStateFormatting implements IconMultiStateFormatting
{
    CTIconSet _iconset;
    
    XSSFIconMultiStateFormatting(final CTIconSet iconset) {
        this._iconset = iconset;
    }
    
    public IconMultiStateFormatting.IconSet getIconSet() {
        final String set = this._iconset.getIconSet().toString();
        return IconMultiStateFormatting.IconSet.byName(set);
    }
    
    public void setIconSet(final IconMultiStateFormatting.IconSet set) {
        final STIconSetType.Enum xIconSet = STIconSetType.Enum.forString(set.name);
        this._iconset.setIconSet(xIconSet);
    }
    
    public boolean isIconOnly() {
        return this._iconset.isSetShowValue() && !this._iconset.getShowValue();
    }
    
    public void setIconOnly(final boolean only) {
        this._iconset.setShowValue(!only);
    }
    
    public boolean isReversed() {
        return this._iconset.isSetReverse() && this._iconset.getReverse();
    }
    
    public void setReversed(final boolean reversed) {
        this._iconset.setReverse(reversed);
    }
    
    public XSSFConditionalFormattingThreshold[] getThresholds() {
        final CTCfvo[] cfvos = this._iconset.getCfvoArray();
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
        this._iconset.setCfvoArray(cfvos);
    }
    
    public XSSFConditionalFormattingThreshold createThreshold() {
        return new XSSFConditionalFormattingThreshold(this._iconset.addNewCfvo());
    }
}
