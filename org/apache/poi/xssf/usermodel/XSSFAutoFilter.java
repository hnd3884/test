package org.apache.poi.xssf.usermodel;

import org.apache.poi.ss.usermodel.AutoFilter;

public final class XSSFAutoFilter implements AutoFilter
{
    private XSSFSheet _sheet;
    
    XSSFAutoFilter(final XSSFSheet sheet) {
        this._sheet = sheet;
    }
}
