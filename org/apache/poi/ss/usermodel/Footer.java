package org.apache.poi.ss.usermodel;

public interface Footer extends HeaderFooter
{
    String getLeft();
    
    void setLeft(final String p0);
    
    String getCenter();
    
    void setCenter(final String p0);
    
    String getRight();
    
    void setRight(final String p0);
}
