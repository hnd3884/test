package org.apache.poi.xssf.usermodel;

import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTColor;
import org.apache.poi.ss.usermodel.Color;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTBorderPr;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STBorderStyle;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTBorder;
import org.apache.poi.ss.usermodel.BorderFormatting;

public class XSSFBorderFormatting implements BorderFormatting
{
    IndexedColorMap _colorMap;
    CTBorder _border;
    
    XSSFBorderFormatting(final CTBorder border, final IndexedColorMap colorMap) {
        this._border = border;
        this._colorMap = colorMap;
    }
    
    public BorderStyle getBorderBottom() {
        return this.getBorderStyle(this._border.getBottom());
    }
    
    public BorderStyle getBorderDiagonal() {
        return this.getBorderStyle(this._border.getDiagonal());
    }
    
    public BorderStyle getBorderLeft() {
        return this.getBorderStyle(this._border.getLeft());
    }
    
    public BorderStyle getBorderRight() {
        return this.getBorderStyle(this._border.getRight());
    }
    
    public BorderStyle getBorderTopEnum() {
        return this.getBorderTop();
    }
    
    public BorderStyle getBorderBottomEnum() {
        return this.getBorderBottom();
    }
    
    public BorderStyle getBorderDiagonalEnum() {
        return this.getBorderDiagonal();
    }
    
    public BorderStyle getBorderLeftEnum() {
        return this.getBorderLeft();
    }
    
    public BorderStyle getBorderRightEnum() {
        return this.getBorderRight();
    }
    
    public BorderStyle getBorderTop() {
        return this.getBorderStyle(this._border.getTop());
    }
    
    public XSSFColor getBottomBorderColorColor() {
        return this.getColor(this._border.getBottom());
    }
    
    public short getBottomBorderColor() {
        return this.getIndexedColor(this.getBottomBorderColorColor());
    }
    
    public XSSFColor getDiagonalBorderColorColor() {
        return this.getColor(this._border.getDiagonal());
    }
    
    public short getDiagonalBorderColor() {
        return this.getIndexedColor(this.getDiagonalBorderColorColor());
    }
    
    public XSSFColor getLeftBorderColorColor() {
        return this.getColor(this._border.getLeft());
    }
    
    public short getLeftBorderColor() {
        return this.getIndexedColor(this.getLeftBorderColorColor());
    }
    
    public XSSFColor getRightBorderColorColor() {
        return this.getColor(this._border.getRight());
    }
    
    public short getRightBorderColor() {
        return this.getIndexedColor(this.getRightBorderColorColor());
    }
    
    public XSSFColor getTopBorderColorColor() {
        return this.getColor(this._border.getTop());
    }
    
    public short getTopBorderColor() {
        return this.getIndexedColor(this.getTopBorderColorColor());
    }
    
    public void setBorderBottom(final BorderStyle border) {
        final CTBorderPr pr = this._border.isSetBottom() ? this._border.getBottom() : this._border.addNewBottom();
        if (border == BorderStyle.NONE) {
            this._border.unsetBottom();
        }
        else {
            pr.setStyle(STBorderStyle.Enum.forInt(border.getCode() + 1));
        }
    }
    
    public void setBorderDiagonal(final BorderStyle border) {
        final CTBorderPr pr = this._border.isSetDiagonal() ? this._border.getDiagonal() : this._border.addNewDiagonal();
        if (border == BorderStyle.NONE) {
            this._border.unsetDiagonal();
        }
        else {
            pr.setStyle(STBorderStyle.Enum.forInt(border.getCode() + 1));
        }
    }
    
    public void setBorderLeft(final BorderStyle border) {
        final CTBorderPr pr = this._border.isSetLeft() ? this._border.getLeft() : this._border.addNewLeft();
        if (border == BorderStyle.NONE) {
            this._border.unsetLeft();
        }
        else {
            pr.setStyle(STBorderStyle.Enum.forInt(border.getCode() + 1));
        }
    }
    
    public void setBorderRight(final BorderStyle border) {
        final CTBorderPr pr = this._border.isSetRight() ? this._border.getRight() : this._border.addNewRight();
        if (border == BorderStyle.NONE) {
            this._border.unsetRight();
        }
        else {
            pr.setStyle(STBorderStyle.Enum.forInt(border.getCode() + 1));
        }
    }
    
    public void setBorderTop(final BorderStyle border) {
        final CTBorderPr pr = this._border.isSetTop() ? this._border.getTop() : this._border.addNewTop();
        if (border == BorderStyle.NONE) {
            this._border.unsetTop();
        }
        else {
            pr.setStyle(STBorderStyle.Enum.forInt(border.getCode() + 1));
        }
    }
    
    public void setBottomBorderColor(final Color color) {
        final XSSFColor xcolor = XSSFColor.toXSSFColor(color);
        if (xcolor == null) {
            this.setBottomBorderColor((CTColor)null);
        }
        else {
            this.setBottomBorderColor(xcolor.getCTColor());
        }
    }
    
    public void setBottomBorderColor(final short color) {
        final CTColor ctColor = CTColor.Factory.newInstance();
        ctColor.setIndexed((long)color);
        this.setBottomBorderColor(ctColor);
    }
    
    public void setBottomBorderColor(final CTColor color) {
        final CTBorderPr pr = this._border.isSetBottom() ? this._border.getBottom() : this._border.addNewBottom();
        if (color == null) {
            pr.unsetColor();
        }
        else {
            pr.setColor(color);
        }
    }
    
    public void setDiagonalBorderColor(final Color color) {
        final XSSFColor xcolor = XSSFColor.toXSSFColor(color);
        if (xcolor == null) {
            this.setDiagonalBorderColor((CTColor)null);
        }
        else {
            this.setDiagonalBorderColor(xcolor.getCTColor());
        }
    }
    
    public void setDiagonalBorderColor(final short color) {
        final CTColor ctColor = CTColor.Factory.newInstance();
        ctColor.setIndexed((long)color);
        this.setDiagonalBorderColor(ctColor);
    }
    
    public void setDiagonalBorderColor(final CTColor color) {
        final CTBorderPr pr = this._border.isSetDiagonal() ? this._border.getDiagonal() : this._border.addNewDiagonal();
        if (color == null) {
            pr.unsetColor();
        }
        else {
            pr.setColor(color);
        }
    }
    
    public void setLeftBorderColor(final Color color) {
        final XSSFColor xcolor = XSSFColor.toXSSFColor(color);
        if (xcolor == null) {
            this.setLeftBorderColor((CTColor)null);
        }
        else {
            this.setLeftBorderColor(xcolor.getCTColor());
        }
    }
    
    public void setLeftBorderColor(final short color) {
        final CTColor ctColor = CTColor.Factory.newInstance();
        ctColor.setIndexed((long)color);
        this.setLeftBorderColor(ctColor);
    }
    
    public void setLeftBorderColor(final CTColor color) {
        final CTBorderPr pr = this._border.isSetLeft() ? this._border.getLeft() : this._border.addNewLeft();
        if (color == null) {
            pr.unsetColor();
        }
        else {
            pr.setColor(color);
        }
    }
    
    public void setRightBorderColor(final Color color) {
        final XSSFColor xcolor = XSSFColor.toXSSFColor(color);
        if (xcolor == null) {
            this.setRightBorderColor((CTColor)null);
        }
        else {
            this.setRightBorderColor(xcolor.getCTColor());
        }
    }
    
    public void setRightBorderColor(final short color) {
        final CTColor ctColor = CTColor.Factory.newInstance();
        ctColor.setIndexed((long)color);
        this.setRightBorderColor(ctColor);
    }
    
    public void setRightBorderColor(final CTColor color) {
        final CTBorderPr pr = this._border.isSetRight() ? this._border.getRight() : this._border.addNewRight();
        if (color == null) {
            pr.unsetColor();
        }
        else {
            pr.setColor(color);
        }
    }
    
    public void setTopBorderColor(final Color color) {
        final XSSFColor xcolor = XSSFColor.toXSSFColor(color);
        if (xcolor == null) {
            this.setTopBorderColor((CTColor)null);
        }
        else {
            this.setTopBorderColor(xcolor.getCTColor());
        }
    }
    
    public void setTopBorderColor(final short color) {
        final CTColor ctColor = CTColor.Factory.newInstance();
        ctColor.setIndexed((long)color);
        this.setTopBorderColor(ctColor);
    }
    
    public void setTopBorderColor(final CTColor color) {
        final CTBorderPr pr = this._border.isSetTop() ? this._border.getTop() : this._border.addNewTop();
        if (color == null) {
            pr.unsetColor();
        }
        else {
            pr.setColor(color);
        }
    }
    
    public BorderStyle getBorderVertical() {
        return this.getBorderStyle(this._border.getVertical());
    }
    
    public BorderStyle getBorderHorizontal() {
        return this.getBorderStyle(this._border.getHorizontal());
    }
    
    public BorderStyle getBorderVerticalEnum() {
        return this.getBorderVertical();
    }
    
    public BorderStyle getBorderHorizontalEnum() {
        return this.getBorderHorizontal();
    }
    
    public short getVerticalBorderColor() {
        return this.getIndexedColor(this.getVerticalBorderColorColor());
    }
    
    public XSSFColor getVerticalBorderColorColor() {
        return this.getColor(this._border.getVertical());
    }
    
    public short getHorizontalBorderColor() {
        return this.getIndexedColor(this.getHorizontalBorderColorColor());
    }
    
    public XSSFColor getHorizontalBorderColorColor() {
        return this.getColor(this._border.getHorizontal());
    }
    
    public void setBorderHorizontal(final BorderStyle border) {
        final CTBorderPr pr = this._border.isSetHorizontal() ? this._border.getHorizontal() : this._border.addNewHorizontal();
        if (border == BorderStyle.NONE) {
            this._border.unsetHorizontal();
        }
        else {
            pr.setStyle(STBorderStyle.Enum.forInt(border.getCode() + 1));
        }
    }
    
    public void setBorderVertical(final BorderStyle border) {
        final CTBorderPr pr = this._border.isSetVertical() ? this._border.getVertical() : this._border.addNewVertical();
        if (border == BorderStyle.NONE) {
            this._border.unsetVertical();
        }
        else {
            pr.setStyle(STBorderStyle.Enum.forInt(border.getCode() + 1));
        }
    }
    
    public void setHorizontalBorderColor(final short color) {
        final CTColor ctColor = CTColor.Factory.newInstance();
        ctColor.setIndexed((long)color);
        this.setHorizontalBorderColor(ctColor);
    }
    
    public void setHorizontalBorderColor(final Color color) {
        final XSSFColor xcolor = XSSFColor.toXSSFColor(color);
        if (xcolor == null) {
            this.setBottomBorderColor((CTColor)null);
        }
        else {
            this.setHorizontalBorderColor(xcolor.getCTColor());
        }
    }
    
    public void setHorizontalBorderColor(final CTColor color) {
        final CTBorderPr pr = this._border.isSetHorizontal() ? this._border.getHorizontal() : this._border.addNewHorizontal();
        if (color == null) {
            pr.unsetColor();
        }
        else {
            pr.setColor(color);
        }
    }
    
    public void setVerticalBorderColor(final short color) {
        final CTColor ctColor = CTColor.Factory.newInstance();
        ctColor.setIndexed((long)color);
        this.setVerticalBorderColor(ctColor);
    }
    
    public void setVerticalBorderColor(final Color color) {
        final XSSFColor xcolor = XSSFColor.toXSSFColor(color);
        if (xcolor == null) {
            this.setBottomBorderColor((CTColor)null);
        }
        else {
            this.setVerticalBorderColor(xcolor.getCTColor());
        }
    }
    
    public void setVerticalBorderColor(final CTColor color) {
        final CTBorderPr pr = this._border.isSetVertical() ? this._border.getVertical() : this._border.addNewVertical();
        if (color == null) {
            pr.unsetColor();
        }
        else {
            pr.setColor(color);
        }
    }
    
    private BorderStyle getBorderStyle(final CTBorderPr borderPr) {
        if (borderPr == null) {
            return BorderStyle.NONE;
        }
        final STBorderStyle.Enum ptrn = borderPr.getStyle();
        return (ptrn == null) ? BorderStyle.NONE : BorderStyle.valueOf((short)(ptrn.intValue() - 1));
    }
    
    private short getIndexedColor(final XSSFColor color) {
        return (short)((color == null) ? 0 : color.getIndexed());
    }
    
    private XSSFColor getColor(final CTBorderPr pr) {
        return (pr == null) ? null : XSSFColor.from(pr.getColor(), this._colorMap);
    }
}
