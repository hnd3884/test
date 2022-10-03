package com.lowagie.text;

public interface DocListener extends ElementListener
{
    void open();
    
    void close();
    
    boolean newPage();
    
    boolean setPageSize(final Rectangle p0);
    
    boolean setMargins(final float p0, final float p1, final float p2, final float p3);
    
    boolean setMarginMirroring(final boolean p0);
    
    boolean setMarginMirroringTopBottom(final boolean p0);
    
    void setPageCount(final int p0);
    
    void resetPageCount();
    
    void setHeader(final HeaderFooter p0);
    
    void resetHeader();
    
    void setFooter(final HeaderFooter p0);
    
    void resetFooter();
}
