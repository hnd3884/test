package org.apache.poi.xssf.usermodel;

import java.util.Objects;
import org.apache.poi.ss.usermodel.FontFamily;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STFontScheme;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTFontScheme;
import org.apache.poi.ss.usermodel.FontScheme;
import org.apache.poi.xssf.model.StylesTable;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STUnderlineValues;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STVerticalAlignRun;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTUnderlineProperty;
import org.apache.poi.ss.usermodel.FontUnderline;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTVerticalAlignFontProperty;
import org.apache.poi.ooxml.POIXMLException;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTFontName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTFontSize;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTColor;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTIntProperty;
import org.apache.poi.ss.usermodel.FontCharset;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTBooleanProperty;
import org.apache.poi.util.Internal;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTFont;
import org.apache.poi.xssf.model.ThemesTable;
import org.apache.poi.ss.usermodel.Font;

public class XSSFFont implements Font
{
    public static final String DEFAULT_FONT_NAME = "Calibri";
    public static final short DEFAULT_FONT_SIZE = 11;
    public static final short DEFAULT_FONT_COLOR;
    private IndexedColorMap _indexedColorMap;
    private ThemesTable _themes;
    private CTFont _ctFont;
    private int _index;
    
    @Internal
    public XSSFFont(final CTFont font) {
        this._ctFont = font;
        this._index = 0;
    }
    
    @Internal
    public XSSFFont(final CTFont font, final int index, final IndexedColorMap colorMap) {
        this._ctFont = font;
        this._index = (short)index;
        this._indexedColorMap = colorMap;
    }
    
    public XSSFFont() {
        this._ctFont = CTFont.Factory.newInstance();
        this.setFontName("Calibri");
        this.setFontHeight(11.0);
    }
    
    @Internal
    public CTFont getCTFont() {
        return this._ctFont;
    }
    
    public boolean getBold() {
        final CTBooleanProperty bold = (this._ctFont.sizeOfBArray() == 0) ? null : this._ctFont.getBArray(0);
        return bold != null && bold.getVal();
    }
    
    public int getCharSet() {
        final CTIntProperty charset = (this._ctFont.sizeOfCharsetArray() == 0) ? null : this._ctFont.getCharsetArray(0);
        return (charset == null) ? FontCharset.ANSI.getValue() : FontCharset.valueOf(charset.getVal()).getValue();
    }
    
    public short getColor() {
        final CTColor color = (this._ctFont.sizeOfColorArray() == 0) ? null : this._ctFont.getColorArray(0);
        if (color == null) {
            return IndexedColors.BLACK.getIndex();
        }
        final long index = color.getIndexed();
        if (index == XSSFFont.DEFAULT_FONT_COLOR) {
            return IndexedColors.BLACK.getIndex();
        }
        if (index == IndexedColors.RED.getIndex()) {
            return IndexedColors.RED.getIndex();
        }
        return (short)index;
    }
    
    public XSSFColor getXSSFColor() {
        final CTColor ctColor = (this._ctFont.sizeOfColorArray() == 0) ? null : this._ctFont.getColorArray(0);
        if (ctColor != null) {
            final XSSFColor color = XSSFColor.from(ctColor, this._indexedColorMap);
            if (this._themes != null) {
                this._themes.inheritFromThemeAsRequired(color);
            }
            return color;
        }
        return null;
    }
    
    public short getThemeColor() {
        final CTColor color = (this._ctFont.sizeOfColorArray() == 0) ? null : this._ctFont.getColorArray(0);
        final long index = (color == null) ? 0L : color.getTheme();
        return (short)index;
    }
    
    public short getFontHeight() {
        return (short)(this.getFontHeightRaw() * 20.0);
    }
    
    public short getFontHeightInPoints() {
        return (short)this.getFontHeightRaw();
    }
    
    private double getFontHeightRaw() {
        final CTFontSize size = (this._ctFont.sizeOfSzArray() == 0) ? null : this._ctFont.getSzArray(0);
        if (size != null) {
            return size.getVal();
        }
        return 11.0;
    }
    
    public String getFontName() {
        final CTFontName name = (this._ctFont.sizeOfNameArray() == 0) ? null : this._ctFont.getNameArray(0);
        return (name == null) ? "Calibri" : name.getVal();
    }
    
    public boolean getItalic() {
        final CTBooleanProperty italic = (this._ctFont.sizeOfIArray() == 0) ? null : this._ctFont.getIArray(0);
        return italic != null && italic.getVal();
    }
    
    public boolean getStrikeout() {
        final CTBooleanProperty strike = (this._ctFont.sizeOfStrikeArray() == 0) ? null : this._ctFont.getStrikeArray(0);
        return strike != null && strike.getVal();
    }
    
    public short getTypeOffset() {
        final CTVerticalAlignFontProperty vAlign = (this._ctFont.sizeOfVertAlignArray() == 0) ? null : this._ctFont.getVertAlignArray(0);
        if (vAlign == null) {
            return 0;
        }
        final int val = vAlign.getVal().intValue();
        switch (val) {
            case 1: {
                return 0;
            }
            case 3: {
                return 2;
            }
            case 2: {
                return 1;
            }
            default: {
                throw new POIXMLException("Wrong offset value " + val);
            }
        }
    }
    
    public byte getUnderline() {
        final CTUnderlineProperty underline = (this._ctFont.sizeOfUArray() == 0) ? null : this._ctFont.getUArray(0);
        if (underline != null) {
            final FontUnderline val = FontUnderline.valueOf(underline.getVal().intValue());
            return val.getByteValue();
        }
        return 0;
    }
    
    public void setBold(final boolean bold) {
        if (bold) {
            final CTBooleanProperty ctBold = (this._ctFont.sizeOfBArray() == 0) ? this._ctFont.addNewB() : this._ctFont.getBArray(0);
            ctBold.setVal(true);
        }
        else {
            this._ctFont.setBArray((CTBooleanProperty[])null);
        }
    }
    
    public void setCharSet(final byte charset) {
        final int cs = charset & 0xFF;
        this.setCharSet(cs);
    }
    
    public void setCharSet(final int charset) {
        final FontCharset fontCharset = FontCharset.valueOf(charset);
        if (fontCharset != null) {
            this.setCharSet(fontCharset);
            return;
        }
        throw new POIXMLException("Attention: an attempt to set a type of unknow charset and charset");
    }
    
    public void setCharSet(final FontCharset charSet) {
        CTIntProperty charsetProperty;
        if (this._ctFont.sizeOfCharsetArray() == 0) {
            charsetProperty = this._ctFont.addNewCharset();
        }
        else {
            charsetProperty = this._ctFont.getCharsetArray(0);
        }
        charsetProperty.setVal(charSet.getValue());
    }
    
    public void setColor(final short color) {
        final CTColor ctColor = (this._ctFont.sizeOfColorArray() == 0) ? this._ctFont.addNewColor() : this._ctFont.getColorArray(0);
        switch (color) {
            case Short.MAX_VALUE: {
                ctColor.setIndexed((long)XSSFFont.DEFAULT_FONT_COLOR);
                break;
            }
            case 10: {
                ctColor.setIndexed((long)IndexedColors.RED.getIndex());
                break;
            }
            default: {
                ctColor.setIndexed((long)color);
                break;
            }
        }
    }
    
    public void setColor(final XSSFColor color) {
        if (color == null) {
            this._ctFont.setColorArray((CTColor[])null);
        }
        else {
            final CTColor ctColor = (this._ctFont.sizeOfColorArray() == 0) ? this._ctFont.addNewColor() : this._ctFont.getColorArray(0);
            if (ctColor.isSetIndexed()) {
                ctColor.unsetIndexed();
            }
            ctColor.setRgb(color.getRGB());
        }
    }
    
    public void setFontHeight(final short height) {
        this.setFontHeight(height / 20.0);
    }
    
    public void setFontHeight(final double height) {
        final CTFontSize fontSize = (this._ctFont.sizeOfSzArray() == 0) ? this._ctFont.addNewSz() : this._ctFont.getSzArray(0);
        fontSize.setVal(height);
    }
    
    public void setFontHeightInPoints(final short height) {
        this.setFontHeight((double)height);
    }
    
    public void setThemeColor(final short theme) {
        final CTColor ctColor = (this._ctFont.sizeOfColorArray() == 0) ? this._ctFont.addNewColor() : this._ctFont.getColorArray(0);
        ctColor.setTheme((long)theme);
    }
    
    public void setFontName(final String name) {
        final CTFontName fontName = (this._ctFont.sizeOfNameArray() == 0) ? this._ctFont.addNewName() : this._ctFont.getNameArray(0);
        fontName.setVal((name == null) ? "Calibri" : name);
    }
    
    public void setItalic(final boolean italic) {
        if (italic) {
            final CTBooleanProperty bool = (this._ctFont.sizeOfIArray() == 0) ? this._ctFont.addNewI() : this._ctFont.getIArray(0);
            bool.setVal(true);
        }
        else {
            this._ctFont.setIArray((CTBooleanProperty[])null);
        }
    }
    
    public void setStrikeout(final boolean strikeout) {
        if (strikeout) {
            final CTBooleanProperty strike = (this._ctFont.sizeOfStrikeArray() == 0) ? this._ctFont.addNewStrike() : this._ctFont.getStrikeArray(0);
            strike.setVal(true);
        }
        else {
            this._ctFont.setStrikeArray((CTBooleanProperty[])null);
        }
    }
    
    public void setTypeOffset(final short offset) {
        if (offset == 0) {
            this._ctFont.setVertAlignArray((CTVerticalAlignFontProperty[])null);
        }
        else {
            final CTVerticalAlignFontProperty offsetProperty = (this._ctFont.sizeOfVertAlignArray() == 0) ? this._ctFont.addNewVertAlign() : this._ctFont.getVertAlignArray(0);
            switch (offset) {
                case 0: {
                    offsetProperty.setVal(STVerticalAlignRun.BASELINE);
                    break;
                }
                case 2: {
                    offsetProperty.setVal(STVerticalAlignRun.SUBSCRIPT);
                    break;
                }
                case 1: {
                    offsetProperty.setVal(STVerticalAlignRun.SUPERSCRIPT);
                    break;
                }
                default: {
                    throw new IllegalStateException("Invalid type offset: " + offset);
                }
            }
        }
    }
    
    public void setUnderline(final byte underline) {
        this.setUnderline(FontUnderline.valueOf(underline));
    }
    
    public void setUnderline(final FontUnderline underline) {
        if (underline == FontUnderline.NONE && this._ctFont.sizeOfUArray() > 0) {
            this._ctFont.setUArray((CTUnderlineProperty[])null);
        }
        else {
            final CTUnderlineProperty ctUnderline = (this._ctFont.sizeOfUArray() == 0) ? this._ctFont.addNewU() : this._ctFont.getUArray(0);
            final STUnderlineValues.Enum val = STUnderlineValues.Enum.forInt(underline.getValue());
            ctUnderline.setVal(val);
        }
    }
    
    @Override
    public String toString() {
        return this._ctFont.toString();
    }
    
    public long registerTo(final StylesTable styles) {
        return this.registerTo(styles, true);
    }
    
    public long registerTo(final StylesTable styles, final boolean force) {
        this._themes = styles.getTheme();
        this._index = styles.putFont(this, force);
        return this._index;
    }
    
    public void setThemesTable(final ThemesTable themes) {
        this._themes = themes;
    }
    
    public FontScheme getScheme() {
        final CTFontScheme scheme = (this._ctFont.sizeOfSchemeArray() == 0) ? null : this._ctFont.getSchemeArray(0);
        return (scheme == null) ? FontScheme.NONE : FontScheme.valueOf(scheme.getVal().intValue());
    }
    
    public void setScheme(final FontScheme scheme) {
        final CTFontScheme ctFontScheme = (this._ctFont.sizeOfSchemeArray() == 0) ? this._ctFont.addNewScheme() : this._ctFont.getSchemeArray(0);
        final STFontScheme.Enum val = STFontScheme.Enum.forInt(scheme.getValue());
        ctFontScheme.setVal(val);
    }
    
    public int getFamily() {
        final CTIntProperty family = (this._ctFont.sizeOfFamilyArray() == 0) ? null : this._ctFont.getFamilyArray(0);
        return (family == null) ? FontFamily.NOT_APPLICABLE.getValue() : FontFamily.valueOf(family.getVal()).getValue();
    }
    
    public void setFamily(final int value) {
        final CTIntProperty family = (this._ctFont.sizeOfFamilyArray() == 0) ? this._ctFont.addNewFamily() : this._ctFont.getFamilyArray(0);
        family.setVal(value);
    }
    
    public void setFamily(final FontFamily family) {
        this.setFamily(family.getValue());
    }
    
    @Deprecated
    public short getIndex() {
        return (short)this.getIndexAsInt();
    }
    
    public int getIndexAsInt() {
        return this._index;
    }
    
    @Override
    public int hashCode() {
        return this._ctFont.toString().hashCode();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof XSSFFont)) {
            return false;
        }
        final XSSFFont cf = (XSSFFont)o;
        return Objects.equals(this.getItalic(), cf.getItalic()) && Objects.equals(this.getBold(), cf.getBold()) && Objects.equals(this.getStrikeout(), cf.getStrikeout()) && Objects.equals(this.getCharSet(), cf.getCharSet()) && Objects.equals(this.getItalic(), cf.getItalic()) && Objects.equals(this.getColor(), cf.getColor()) && Objects.equals(this.getFamily(), cf.getFamily()) && Objects.equals(this.getFontHeight(), cf.getFontHeight()) && Objects.equals(this.getFontName(), cf.getFontName()) && Objects.equals(this.getScheme(), cf.getScheme()) && Objects.equals(this.getThemeColor(), cf.getThemeColor()) && Objects.equals(this.getTypeOffset(), cf.getTypeOffset()) && Objects.equals(this.getUnderline(), cf.getUnderline()) && Objects.equals(this.getXSSFColor(), cf.getXSSFColor());
    }
    
    static {
        DEFAULT_FONT_COLOR = IndexedColors.BLACK.getIndex();
    }
}
