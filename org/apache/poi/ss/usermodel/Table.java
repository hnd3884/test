package org.apache.poi.ss.usermodel;

import org.apache.poi.ss.util.CellReference;
import java.util.regex.Pattern;

public interface Table
{
    public static final Pattern isStructuredReference = Pattern.compile("[a-zA-Z_\\\\][a-zA-Z0-9._]*\\[.*\\]");
    
    int getStartColIndex();
    
    int getStartRowIndex();
    
    int getEndColIndex();
    
    int getEndRowIndex();
    
    String getName();
    
    String getStyleName();
    
    int findColumnIndex(final String p0);
    
    String getSheetName();
    
    boolean isHasTotalsRow();
    
    int getTotalsRowCount();
    
    int getHeaderRowCount();
    
    TableStyleInfo getStyle();
    
    default boolean contains(final Cell cell) {
        return cell != null && this.contains(new CellReference(cell.getSheet().getSheetName(), cell.getRowIndex(), cell.getColumnIndex(), true, true));
    }
    
    boolean contains(final CellReference p0);
}
