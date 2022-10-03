package org.apache.poi.xssf.usermodel.extensions;

import java.util.Objects;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTBorderPr;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STBorderStyle;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.util.Internal;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTBorder;
import org.apache.poi.xssf.model.ThemesTable;
import org.apache.poi.xssf.usermodel.IndexedColorMap;

public class XSSFCellBorder
{
    private final IndexedColorMap _indexedColorMap;
    private ThemesTable _theme;
    private final CTBorder border;
    
    public XSSFCellBorder(final CTBorder border, final ThemesTable theme, final IndexedColorMap colorMap) {
        this.border = border;
        this._indexedColorMap = colorMap;
        this._theme = theme;
    }
    
    public XSSFCellBorder(final CTBorder border) {
        this(border, null, null);
    }
    
    public XSSFCellBorder(final CTBorder border, final IndexedColorMap colorMap) {
        this(border, null, colorMap);
    }
    
    public XSSFCellBorder() {
        this(CTBorder.Factory.newInstance(), null, null);
    }
    
    public void setThemesTable(final ThemesTable themes) {
        this._theme = themes;
    }
    
    @Internal
    public CTBorder getCTBorder() {
        return this.border;
    }
    
    public BorderStyle getBorderStyle(final BorderSide side) {
        final CTBorderPr ctBorder = this.getBorder(side);
        final STBorderStyle.Enum border = (ctBorder == null) ? STBorderStyle.NONE : ctBorder.getStyle();
        return BorderStyle.values()[border.intValue() - 1];
    }
    
    public void setBorderStyle(final BorderSide side, final BorderStyle style) {
        this.getBorder(side, true).setStyle(STBorderStyle.Enum.forInt(style.ordinal() + 1));
    }
    
    public XSSFColor getBorderColor(final BorderSide side) {
        final CTBorderPr borderPr = this.getBorder(side);
        if (borderPr != null && borderPr.isSetColor()) {
            final XSSFColor clr = XSSFColor.from(borderPr.getColor(), this._indexedColorMap);
            if (this._theme != null) {
                this._theme.inheritFromThemeAsRequired(clr);
            }
            return clr;
        }
        return null;
    }
    
    public void setBorderColor(final BorderSide side, final XSSFColor color) {
        final CTBorderPr borderPr = this.getBorder(side, true);
        if (color == null) {
            borderPr.unsetColor();
        }
        else {
            borderPr.setColor(color.getCTColor());
        }
    }
    
    private CTBorderPr getBorder(final BorderSide side) {
        return this.getBorder(side, false);
    }
    
    private CTBorderPr getBorder(final BorderSide side, final boolean ensure) {
        CTBorderPr borderPr = null;
        switch (side) {
            case TOP: {
                borderPr = this.border.getTop();
                if (ensure && borderPr == null) {
                    borderPr = this.border.addNewTop();
                    break;
                }
                break;
            }
            case RIGHT: {
                borderPr = this.border.getRight();
                if (ensure && borderPr == null) {
                    borderPr = this.border.addNewRight();
                    break;
                }
                break;
            }
            case BOTTOM: {
                borderPr = this.border.getBottom();
                if (ensure && borderPr == null) {
                    borderPr = this.border.addNewBottom();
                    break;
                }
                break;
            }
            case LEFT: {
                borderPr = this.border.getLeft();
                if (ensure && borderPr == null) {
                    borderPr = this.border.addNewLeft();
                    break;
                }
                break;
            }
            default: {
                throw new IllegalArgumentException("No suitable side specified for the border");
            }
        }
        return borderPr;
    }
    
    @Override
    public int hashCode() {
        return this.border.toString().hashCode();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof XSSFCellBorder)) {
            return false;
        }
        final XSSFCellBorder cf = (XSSFCellBorder)o;
        boolean equal = true;
        for (final BorderSide side : BorderSide.values()) {
            if (!Objects.equals(this.getBorderColor(side), cf.getBorderColor(side)) || !Objects.equals(this.getBorderStyle(side), cf.getBorderStyle(side))) {
                equal = false;
                break;
            }
        }
        return equal;
    }
    
    public enum BorderSide
    {
        TOP, 
        RIGHT, 
        BOTTOM, 
        LEFT;
    }
}
