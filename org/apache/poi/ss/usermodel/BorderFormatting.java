package org.apache.poi.ss.usermodel;

import org.apache.poi.util.Removal;

public interface BorderFormatting
{
    BorderStyle getBorderBottom();
    
    BorderStyle getBorderDiagonal();
    
    BorderStyle getBorderLeft();
    
    BorderStyle getBorderRight();
    
    BorderStyle getBorderTop();
    
    BorderStyle getBorderVertical();
    
    BorderStyle getBorderHorizontal();
    
    @Removal(version = "4.2")
    @Deprecated
    BorderStyle getBorderBottomEnum();
    
    @Removal(version = "4.2")
    @Deprecated
    BorderStyle getBorderDiagonalEnum();
    
    @Removal(version = "4.2")
    @Deprecated
    BorderStyle getBorderLeftEnum();
    
    @Removal(version = "4.2")
    @Deprecated
    BorderStyle getBorderRightEnum();
    
    @Removal(version = "4.2")
    @Deprecated
    BorderStyle getBorderTopEnum();
    
    @Removal(version = "4.2")
    @Deprecated
    BorderStyle getBorderVerticalEnum();
    
    @Removal(version = "4.2")
    @Deprecated
    BorderStyle getBorderHorizontalEnum();
    
    short getBottomBorderColor();
    
    Color getBottomBorderColorColor();
    
    short getDiagonalBorderColor();
    
    Color getDiagonalBorderColorColor();
    
    short getLeftBorderColor();
    
    Color getLeftBorderColorColor();
    
    short getRightBorderColor();
    
    Color getRightBorderColorColor();
    
    short getTopBorderColor();
    
    Color getTopBorderColorColor();
    
    short getVerticalBorderColor();
    
    Color getVerticalBorderColorColor();
    
    short getHorizontalBorderColor();
    
    Color getHorizontalBorderColorColor();
    
    void setBorderBottom(final BorderStyle p0);
    
    void setBorderDiagonal(final BorderStyle p0);
    
    void setBorderLeft(final BorderStyle p0);
    
    void setBorderRight(final BorderStyle p0);
    
    void setBorderTop(final BorderStyle p0);
    
    void setBorderHorizontal(final BorderStyle p0);
    
    void setBorderVertical(final BorderStyle p0);
    
    void setBottomBorderColor(final short p0);
    
    void setBottomBorderColor(final Color p0);
    
    void setDiagonalBorderColor(final short p0);
    
    void setDiagonalBorderColor(final Color p0);
    
    void setLeftBorderColor(final short p0);
    
    void setLeftBorderColor(final Color p0);
    
    void setRightBorderColor(final short p0);
    
    void setRightBorderColor(final Color p0);
    
    void setTopBorderColor(final short p0);
    
    void setTopBorderColor(final Color p0);
    
    void setHorizontalBorderColor(final short p0);
    
    void setHorizontalBorderColor(final Color p0);
    
    void setVerticalBorderColor(final short p0);
    
    void setVerticalBorderColor(final Color p0);
}
