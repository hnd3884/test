package org.apache.poi.wp.usermodel;

public interface CharacterRun
{
    boolean isBold();
    
    void setBold(final boolean p0);
    
    boolean isItalic();
    
    void setItalic(final boolean p0);
    
    boolean isSmallCaps();
    
    void setSmallCaps(final boolean p0);
    
    boolean isCapitalized();
    
    void setCapitalized(final boolean p0);
    
    boolean isStrikeThrough();
    
    void setStrikeThrough(final boolean p0);
    
    boolean isDoubleStrikeThrough();
    
    void setDoubleStrikethrough(final boolean p0);
    
    boolean isShadowed();
    
    void setShadow(final boolean p0);
    
    boolean isEmbossed();
    
    void setEmbossed(final boolean p0);
    
    boolean isImprinted();
    
    void setImprinted(final boolean p0);
    
    int getFontSize();
    
    void setFontSize(final int p0);
    
    int getCharacterSpacing();
    
    void setCharacterSpacing(final int p0);
    
    int getKerning();
    
    void setKerning(final int p0);
    
    boolean isHighlighted();
    
    String getFontName();
    
    String text();
}
