package org.apache.poi.xssf.usermodel;

import org.apache.poi.ss.usermodel.ConditionalFormattingThreshold;
import org.apache.poi.ss.usermodel.Color;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTDataBar;
import org.apache.poi.ss.usermodel.DataBarFormatting;

public class XSSFDataBarFormatting implements DataBarFormatting
{
    IndexedColorMap _colorMap;
    CTDataBar _databar;
    
    XSSFDataBarFormatting(final CTDataBar databar, final IndexedColorMap colorMap) {
        this._databar = databar;
        this._colorMap = colorMap;
    }
    
    public boolean isIconOnly() {
        return this._databar.isSetShowValue() && !this._databar.getShowValue();
    }
    
    public void setIconOnly(final boolean only) {
        this._databar.setShowValue(!only);
    }
    
    public boolean isLeftToRight() {
        return true;
    }
    
    public void setLeftToRight(final boolean ltr) {
    }
    
    public int getWidthMin() {
        return 0;
    }
    
    public void setWidthMin(final int width) {
    }
    
    public int getWidthMax() {
        return 100;
    }
    
    public void setWidthMax(final int width) {
    }
    
    public XSSFColor getColor() {
        return XSSFColor.from(this._databar.getColor(), this._colorMap);
    }
    
    public void setColor(final Color color) {
        this._databar.setColor(((XSSFColor)color).getCTColor());
    }
    
    public XSSFConditionalFormattingThreshold getMinThreshold() {
        return new XSSFConditionalFormattingThreshold(this._databar.getCfvoArray(0));
    }
    
    public XSSFConditionalFormattingThreshold getMaxThreshold() {
        return new XSSFConditionalFormattingThreshold(this._databar.getCfvoArray(1));
    }
    
    public XSSFConditionalFormattingThreshold createThreshold() {
        return new XSSFConditionalFormattingThreshold(this._databar.addNewCfvo());
    }
}
