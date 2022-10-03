package org.apache.poi.xssf.model;

import org.apache.poi.xssf.usermodel.XSSFColor;

public interface Themes
{
    XSSFColor getThemeColor(final int p0);
    
    void inheritFromThemeAsRequired(final XSSFColor p0);
}
