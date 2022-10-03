package org.apache.poi.xssf.model;

import java.util.Iterator;
import org.apache.poi.xssf.usermodel.XSSFComment;
import org.apache.poi.ss.util.CellAddress;

public interface Comments
{
    int getNumberOfComments();
    
    int getNumberOfAuthors();
    
    String getAuthor(final long p0);
    
    int findAuthor(final String p0);
    
    XSSFComment findCellComment(final CellAddress p0);
    
    boolean removeComment(final CellAddress p0);
    
    Iterator<CellAddress> getCellAddresses();
}
