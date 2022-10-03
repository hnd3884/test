package org.apache.poi.wp.usermodel;

public interface Paragraph
{
    int getIndentFromRight();
    
    void setIndentFromRight(final int p0);
    
    int getIndentFromLeft();
    
    void setIndentFromLeft(final int p0);
    
    int getFirstLineIndent();
    
    void setFirstLineIndent(final int p0);
    
    int getFontAlignment();
    
    void setFontAlignment(final int p0);
    
    boolean isWordWrapped();
    
    void setWordWrapped(final boolean p0);
}
