package org.apache.poi.xssf.usermodel;

import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STUnderlineValues;
import org.apache.poi.ss.usermodel.FontUnderline;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTUnderlineProperty;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTFontSize;
import org.apache.poi.ss.usermodel.Color;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTColor;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTBooleanProperty;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STVerticalAlignRun;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTVerticalAlignFontProperty;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTFont;
import org.apache.poi.ss.usermodel.FontFormatting;

public class XSSFFontFormatting implements FontFormatting
{
    private IndexedColorMap _colorMap;
    private CTFont _font;
    
    XSSFFontFormatting(final CTFont font, final IndexedColorMap colorMap) {
        this._font = font;
        this._colorMap = colorMap;
    }
    
    public short getEscapementType() {
        if (this._font.sizeOfVertAlignArray() == 0) {
            return 0;
        }
        final CTVerticalAlignFontProperty prop = this._font.getVertAlignArray(0);
        return (short)(prop.getVal().intValue() - 1);
    }
    
    public void setEscapementType(final short escapementType) {
        this._font.setVertAlignArray((CTVerticalAlignFontProperty[])null);
        if (escapementType != 0) {
            this._font.addNewVertAlign().setVal(STVerticalAlignRun.Enum.forInt(escapementType + 1));
        }
    }
    
    public boolean isStruckout() {
        final CTBooleanProperty[] strikeArray = this._font.getStrikeArray();
        final int length = strikeArray.length;
        final int n = 0;
        if (n < length) {
            final CTBooleanProperty bProp = strikeArray[n];
            return bProp.getVal();
        }
        return false;
    }
    
    public short getFontColorIndex() {
        if (this._font.sizeOfColorArray() == 0) {
            return -1;
        }
        int idx = 0;
        final CTColor color = this._font.getColorArray(0);
        if (color.isSetIndexed()) {
            idx = (int)color.getIndexed();
        }
        return (short)idx;
    }
    
    public void setFontColorIndex(final short color) {
        this._font.setColorArray((CTColor[])null);
        if (color != -1) {
            this._font.addNewColor().setIndexed((long)color);
        }
    }
    
    public XSSFColor getFontColor() {
        if (this._font.sizeOfColorArray() == 0) {
            return null;
        }
        return XSSFColor.from(this._font.getColorArray(0), this._colorMap);
    }
    
    public void setFontColor(final Color color) {
        final XSSFColor xcolor = XSSFColor.toXSSFColor(color);
        if (xcolor == null) {
            this._font.getColorList().clear();
        }
        else if (this._font.sizeOfColorArray() == 0) {
            this._font.addNewColor().setRgb(xcolor.getRGB());
        }
        else {
            this._font.setColorArray(0, xcolor.getCTColor());
        }
    }
    
    public int getFontHeight() {
        if (this._font.sizeOfSzArray() == 0) {
            return -1;
        }
        final CTFontSize sz = this._font.getSzArray(0);
        return (int)(20.0 * sz.getVal());
    }
    
    public void setFontHeight(final int height) {
        this._font.setSzArray((CTFontSize[])null);
        if (height != -1) {
            this._font.addNewSz().setVal(height / 20.0);
        }
    }
    
    public short getUnderlineType() {
        if (this._font.sizeOfUArray() == 0) {
            return 0;
        }
        final CTUnderlineProperty u = this._font.getUArray(0);
        switch (u.getVal().intValue()) {
            case 1: {
                return 1;
            }
            case 2: {
                return 2;
            }
            case 3: {
                return 33;
            }
            case 4: {
                return 34;
            }
            default: {
                return 0;
            }
        }
    }
    
    public void setUnderlineType(final short underlineType) {
        this._font.setUArray((CTUnderlineProperty[])null);
        if (underlineType != 0) {
            final FontUnderline fenum = FontUnderline.valueOf((int)underlineType);
            final STUnderlineValues.Enum val = STUnderlineValues.Enum.forInt(fenum.getValue());
            this._font.addNewU().setVal(val);
        }
    }
    
    public boolean isBold() {
        return this._font.sizeOfBArray() == 1 && this._font.getBArray(0).getVal();
    }
    
    public boolean isItalic() {
        return this._font.sizeOfIArray() == 1 && this._font.getIArray(0).getVal();
    }
    
    public void setFontStyle(final boolean italic, final boolean bold) {
        this._font.setIArray((CTBooleanProperty[])null);
        this._font.setBArray((CTBooleanProperty[])null);
        if (italic) {
            this._font.addNewI().setVal(true);
        }
        if (bold) {
            this._font.addNewB().setVal(true);
        }
    }
    
    public void resetFontStyle() {
        this._font.set((XmlObject)CTFont.Factory.newInstance());
    }
}
