package org.apache.poi.ss.usermodel;

import org.apache.poi.util.Removal;

public interface CellStyle
{
    short getIndex();
    
    void setDataFormat(final short p0);
    
    short getDataFormat();
    
    String getDataFormatString();
    
    void setFont(final Font p0);
    
    @Removal(version = "4.2")
    @Deprecated
    short getFontIndex();
    
    int getFontIndexAsInt();
    
    void setHidden(final boolean p0);
    
    boolean getHidden();
    
    void setLocked(final boolean p0);
    
    boolean getLocked();
    
    void setQuotePrefixed(final boolean p0);
    
    boolean getQuotePrefixed();
    
    void setAlignment(final HorizontalAlignment p0);
    
    HorizontalAlignment getAlignment();
    
    @Removal(version = "4.2")
    @Deprecated
    HorizontalAlignment getAlignmentEnum();
    
    void setWrapText(final boolean p0);
    
    boolean getWrapText();
    
    void setVerticalAlignment(final VerticalAlignment p0);
    
    VerticalAlignment getVerticalAlignment();
    
    @Removal(version = "4.2")
    @Deprecated
    VerticalAlignment getVerticalAlignmentEnum();
    
    void setRotation(final short p0);
    
    short getRotation();
    
    void setIndention(final short p0);
    
    short getIndention();
    
    void setBorderLeft(final BorderStyle p0);
    
    BorderStyle getBorderLeft();
    
    @Removal(version = "4.2")
    @Deprecated
    BorderStyle getBorderLeftEnum();
    
    void setBorderRight(final BorderStyle p0);
    
    BorderStyle getBorderRight();
    
    @Removal(version = "4.2")
    @Deprecated
    BorderStyle getBorderRightEnum();
    
    void setBorderTop(final BorderStyle p0);
    
    BorderStyle getBorderTop();
    
    @Removal(version = "4.2")
    @Deprecated
    BorderStyle getBorderTopEnum();
    
    void setBorderBottom(final BorderStyle p0);
    
    BorderStyle getBorderBottom();
    
    @Removal(version = "4.2")
    @Deprecated
    BorderStyle getBorderBottomEnum();
    
    void setLeftBorderColor(final short p0);
    
    short getLeftBorderColor();
    
    void setRightBorderColor(final short p0);
    
    short getRightBorderColor();
    
    void setTopBorderColor(final short p0);
    
    short getTopBorderColor();
    
    void setBottomBorderColor(final short p0);
    
    short getBottomBorderColor();
    
    void setFillPattern(final FillPatternType p0);
    
    FillPatternType getFillPattern();
    
    @Removal(version = "4.2")
    @Deprecated
    FillPatternType getFillPatternEnum();
    
    void setFillBackgroundColor(final short p0);
    
    short getFillBackgroundColor();
    
    Color getFillBackgroundColorColor();
    
    void setFillForegroundColor(final short p0);
    
    short getFillForegroundColor();
    
    Color getFillForegroundColorColor();
    
    void cloneStyleFrom(final CellStyle p0);
    
    void setShrinkToFit(final boolean p0);
    
    boolean getShrinkToFit();
}
