package org.apache.poi.sl.usermodel;

import org.apache.poi.util.Internal;
import org.apache.poi.common.usermodel.fonts.FontInfo;
import org.apache.poi.common.usermodel.fonts.FontGroup;
import java.awt.Color;

public interface TextRun
{
    String getRawText();
    
    void setText(final String p0);
    
    TextCap getTextCap();
    
    PaintStyle getFontColor();
    
    void setFontColor(final Color p0);
    
    void setFontColor(final PaintStyle p0);
    
    Double getFontSize();
    
    void setFontSize(final Double p0);
    
    String getFontFamily();
    
    String getFontFamily(final FontGroup p0);
    
    void setFontFamily(final String p0);
    
    void setFontFamily(final String p0, final FontGroup p1);
    
    FontInfo getFontInfo(final FontGroup p0);
    
    void setFontInfo(final FontInfo p0, final FontGroup p1);
    
    boolean isBold();
    
    void setBold(final boolean p0);
    
    boolean isItalic();
    
    void setItalic(final boolean p0);
    
    boolean isUnderlined();
    
    void setUnderlined(final boolean p0);
    
    boolean isStrikethrough();
    
    void setStrikethrough(final boolean p0);
    
    boolean isSubscript();
    
    boolean isSuperscript();
    
    byte getPitchAndFamily();
    
    Hyperlink<?, ?> getHyperlink();
    
    Hyperlink<?, ?> createHyperlink();
    
    @Internal
    FieldType getFieldType();
    
    TextParagraph<?, ?, ?> getParagraph();
    
    public enum TextCap
    {
        NONE, 
        SMALL, 
        ALL;
    }
    
    public enum FieldType
    {
        SLIDE_NUMBER, 
        DATE_TIME;
    }
}
