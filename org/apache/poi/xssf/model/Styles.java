package org.apache.poi.xssf.model;

import org.apache.poi.xssf.usermodel.extensions.XSSFCellFill;
import org.apache.poi.xssf.usermodel.extensions.XSSFCellBorder;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;

public interface Styles
{
    String getNumberFormatAt(final short p0);
    
    int putNumberFormat(final String p0);
    
    void putNumberFormat(final short p0, final String p1);
    
    boolean removeNumberFormat(final short p0);
    
    boolean removeNumberFormat(final String p0);
    
    XSSFFont getFontAt(final int p0);
    
    int putFont(final XSSFFont p0, final boolean p1);
    
    int putFont(final XSSFFont p0);
    
    XSSFCellStyle getStyleAt(final int p0);
    
    int putStyle(final XSSFCellStyle p0);
    
    XSSFCellBorder getBorderAt(final int p0);
    
    int putBorder(final XSSFCellBorder p0);
    
    XSSFCellFill getFillAt(final int p0);
    
    int putFill(final XSSFCellFill p0);
    
    int getNumCellStyles();
    
    int getNumDataFormats();
}
