package org.apache.poi.ss.formula.ptg;

public interface Pxg
{
    int getExternalWorkbookNumber();
    
    String getSheetName();
    
    void setSheetName(final String p0);
    
    String toFormulaString();
}
