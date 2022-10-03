package org.apache.poi.ss.usermodel;

import org.apache.poi.ss.util.CellAddress;

public interface Comment
{
    void setVisible(final boolean p0);
    
    boolean isVisible();
    
    CellAddress getAddress();
    
    void setAddress(final CellAddress p0);
    
    void setAddress(final int p0, final int p1);
    
    int getRow();
    
    void setRow(final int p0);
    
    int getColumn();
    
    void setColumn(final int p0);
    
    String getAuthor();
    
    void setAuthor(final String p0);
    
    RichTextString getString();
    
    void setString(final RichTextString p0);
    
    ClientAnchor getClientAnchor();
}
