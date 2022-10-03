package org.apache.poi.xssf.usermodel;

public enum XSSFWorkbookType
{
    XLSX(XSSFRelation.WORKBOOK.getContentType(), "xlsx"), 
    XLSM(XSSFRelation.MACROS_WORKBOOK.getContentType(), "xlsm");
    
    private final String _contentType;
    private final String _extension;
    
    private XSSFWorkbookType(final String contentType, final String extension) {
        this._contentType = contentType;
        this._extension = extension;
    }
    
    public String getContentType() {
        return this._contentType;
    }
    
    public String getExtension() {
        return this._extension;
    }
}
