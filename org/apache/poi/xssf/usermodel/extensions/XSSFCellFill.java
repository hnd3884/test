package org.apache.poi.xssf.usermodel.extensions;

import java.util.Objects;
import org.apache.poi.util.Internal;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STPatternType;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTColor;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTPatternFill;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTFill;
import org.apache.poi.xssf.usermodel.IndexedColorMap;

public final class XSSFCellFill
{
    private IndexedColorMap _indexedColorMap;
    private CTFill _fill;
    
    public XSSFCellFill(final CTFill fill, final IndexedColorMap colorMap) {
        this._fill = fill;
        this._indexedColorMap = colorMap;
    }
    
    public XSSFCellFill() {
        this._fill = CTFill.Factory.newInstance();
    }
    
    public XSSFColor getFillBackgroundColor() {
        final CTPatternFill ptrn = this._fill.getPatternFill();
        if (ptrn == null) {
            return null;
        }
        final CTColor ctColor = ptrn.getBgColor();
        return XSSFColor.from(ctColor, this._indexedColorMap);
    }
    
    public void setFillBackgroundColor(final int index) {
        final CTPatternFill ptrn = this.ensureCTPatternFill();
        final CTColor ctColor = ptrn.isSetBgColor() ? ptrn.getBgColor() : ptrn.addNewBgColor();
        ctColor.setIndexed((long)index);
    }
    
    public void setFillBackgroundColor(final XSSFColor color) {
        final CTPatternFill ptrn = this.ensureCTPatternFill();
        if (color == null) {
            ptrn.unsetBgColor();
        }
        else {
            ptrn.setBgColor(color.getCTColor());
        }
    }
    
    public XSSFColor getFillForegroundColor() {
        final CTPatternFill ptrn = this._fill.getPatternFill();
        if (ptrn == null) {
            return null;
        }
        final CTColor ctColor = ptrn.getFgColor();
        return XSSFColor.from(ctColor, this._indexedColorMap);
    }
    
    public void setFillForegroundColor(final int index) {
        final CTPatternFill ptrn = this.ensureCTPatternFill();
        final CTColor ctColor = ptrn.isSetFgColor() ? ptrn.getFgColor() : ptrn.addNewFgColor();
        ctColor.setIndexed((long)index);
    }
    
    public void setFillForegroundColor(final XSSFColor color) {
        final CTPatternFill ptrn = this.ensureCTPatternFill();
        if (color == null) {
            ptrn.unsetFgColor();
        }
        else {
            ptrn.setFgColor(color.getCTColor());
        }
    }
    
    public STPatternType.Enum getPatternType() {
        final CTPatternFill ptrn = this._fill.getPatternFill();
        return (ptrn == null) ? null : ptrn.getPatternType();
    }
    
    public void setPatternType(final STPatternType.Enum patternType) {
        final CTPatternFill ptrn = this.ensureCTPatternFill();
        ptrn.setPatternType(patternType);
    }
    
    private CTPatternFill ensureCTPatternFill() {
        CTPatternFill patternFill = this._fill.getPatternFill();
        if (patternFill == null) {
            patternFill = this._fill.addNewPatternFill();
        }
        return patternFill;
    }
    
    @Internal
    public CTFill getCTFill() {
        return this._fill;
    }
    
    @Override
    public int hashCode() {
        return this._fill.toString().hashCode();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof XSSFCellFill)) {
            return false;
        }
        final XSSFCellFill cf = (XSSFCellFill)o;
        return Objects.equals(this.getFillBackgroundColor(), cf.getFillBackgroundColor()) && Objects.equals(this.getFillForegroundColor(), cf.getFillForegroundColor()) && Objects.equals(this.getPatternType(), cf.getPatternType());
    }
}
