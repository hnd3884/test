package org.apache.poi.ss.usermodel;

import org.apache.poi.util.Removal;

public interface Font
{
    public static final short COLOR_NORMAL = Short.MAX_VALUE;
    public static final short COLOR_RED = 10;
    public static final short SS_NONE = 0;
    public static final short SS_SUPER = 1;
    public static final short SS_SUB = 2;
    public static final byte U_NONE = 0;
    public static final byte U_SINGLE = 1;
    public static final byte U_DOUBLE = 2;
    public static final byte U_SINGLE_ACCOUNTING = 33;
    public static final byte U_DOUBLE_ACCOUNTING = 34;
    public static final byte ANSI_CHARSET = 0;
    public static final byte DEFAULT_CHARSET = 1;
    public static final byte SYMBOL_CHARSET = 2;
    
    void setFontName(final String p0);
    
    String getFontName();
    
    void setFontHeight(final short p0);
    
    void setFontHeightInPoints(final short p0);
    
    short getFontHeight();
    
    short getFontHeightInPoints();
    
    void setItalic(final boolean p0);
    
    boolean getItalic();
    
    void setStrikeout(final boolean p0);
    
    boolean getStrikeout();
    
    void setColor(final short p0);
    
    short getColor();
    
    void setTypeOffset(final short p0);
    
    short getTypeOffset();
    
    void setUnderline(final byte p0);
    
    byte getUnderline();
    
    int getCharSet();
    
    void setCharSet(final byte p0);
    
    void setCharSet(final int p0);
    
    @Removal(version = "4.2")
    @Deprecated
    short getIndex();
    
    int getIndexAsInt();
    
    void setBold(final boolean p0);
    
    boolean getBold();
}
