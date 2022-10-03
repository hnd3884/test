package org.apache.poi.xssf.usermodel;

import org.openxmlformats.schemas.spreadsheetml.x2006.main.STPatternType;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTPatternFill;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTColor;
import org.apache.poi.ss.usermodel.Color;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTFill;
import org.apache.poi.ss.usermodel.PatternFormatting;

public class XSSFPatternFormatting implements PatternFormatting
{
    IndexedColorMap _colorMap;
    CTFill _fill;
    
    XSSFPatternFormatting(final CTFill fill, final IndexedColorMap colorMap) {
        this._fill = fill;
        this._colorMap = colorMap;
    }
    
    public XSSFColor getFillBackgroundColorColor() {
        if (!this._fill.isSetPatternFill()) {
            return null;
        }
        return XSSFColor.from(this._fill.getPatternFill().getBgColor(), this._colorMap);
    }
    
    public XSSFColor getFillForegroundColorColor() {
        if (!this._fill.isSetPatternFill() || !this._fill.getPatternFill().isSetFgColor()) {
            return null;
        }
        return XSSFColor.from(this._fill.getPatternFill().getFgColor(), this._colorMap);
    }
    
    public short getFillPattern() {
        if (!this._fill.isSetPatternFill() || !this._fill.getPatternFill().isSetPatternType()) {
            return 0;
        }
        return (short)(this._fill.getPatternFill().getPatternType().intValue() - 1);
    }
    
    public short getFillBackgroundColor() {
        final XSSFColor color = this.getFillBackgroundColorColor();
        if (color == null) {
            return 0;
        }
        return color.getIndexed();
    }
    
    public short getFillForegroundColor() {
        final XSSFColor color = this.getFillForegroundColorColor();
        if (color == null) {
            return 0;
        }
        return color.getIndexed();
    }
    
    public void setFillBackgroundColor(final Color bg) {
        final XSSFColor xcolor = XSSFColor.toXSSFColor(bg);
        if (xcolor == null) {
            this.setFillBackgroundColor((CTColor)null);
        }
        else {
            this.setFillBackgroundColor(xcolor.getCTColor());
        }
    }
    
    public void setFillBackgroundColor(final short bg) {
        final CTColor bgColor = CTColor.Factory.newInstance();
        bgColor.setIndexed((long)bg);
        this.setFillBackgroundColor(bgColor);
    }
    
    private void setFillBackgroundColor(final CTColor color) {
        final CTPatternFill ptrn = this._fill.isSetPatternFill() ? this._fill.getPatternFill() : this._fill.addNewPatternFill();
        if (color == null) {
            ptrn.unsetBgColor();
        }
        else {
            ptrn.setBgColor(color);
        }
    }
    
    public void setFillForegroundColor(final Color fg) {
        final XSSFColor xcolor = XSSFColor.toXSSFColor(fg);
        if (xcolor == null) {
            this.setFillForegroundColor((CTColor)null);
        }
        else {
            this.setFillForegroundColor(xcolor.getCTColor());
        }
    }
    
    public void setFillForegroundColor(final short fg) {
        final CTColor fgColor = CTColor.Factory.newInstance();
        fgColor.setIndexed((long)fg);
        this.setFillForegroundColor(fgColor);
    }
    
    private void setFillForegroundColor(final CTColor color) {
        final CTPatternFill ptrn = this._fill.isSetPatternFill() ? this._fill.getPatternFill() : this._fill.addNewPatternFill();
        if (color == null) {
            ptrn.unsetFgColor();
        }
        else {
            ptrn.setFgColor(color);
        }
    }
    
    public void setFillPattern(final short fp) {
        final CTPatternFill ptrn = this._fill.isSetPatternFill() ? this._fill.getPatternFill() : this._fill.addNewPatternFill();
        if (fp == 0) {
            ptrn.unsetPatternType();
        }
        else {
            ptrn.setPatternType(STPatternType.Enum.forInt(fp + 1));
        }
    }
}
