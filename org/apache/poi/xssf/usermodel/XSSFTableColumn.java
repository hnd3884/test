package org.apache.poi.xssf.usermodel;

import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTXmlColumnPr;
import org.apache.poi.util.Internal;
import org.apache.poi.xssf.usermodel.helpers.XSSFXmlColumnPr;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTableColumn;

public class XSSFTableColumn
{
    private final XSSFTable table;
    private final CTTableColumn ctTableColumn;
    private XSSFXmlColumnPr xmlColumnPr;
    
    @Internal
    protected XSSFTableColumn(final XSSFTable table, final CTTableColumn ctTableColumn) {
        this.table = table;
        this.ctTableColumn = ctTableColumn;
    }
    
    public XSSFTable getTable() {
        return this.table;
    }
    
    public long getId() {
        return this.ctTableColumn.getId();
    }
    
    public void setId(final long columnId) {
        this.ctTableColumn.setId(columnId);
    }
    
    public String getName() {
        return this.ctTableColumn.getName();
    }
    
    public void setName(final String columnName) {
        this.ctTableColumn.setName(columnName);
    }
    
    public XSSFXmlColumnPr getXmlColumnPr() {
        if (this.xmlColumnPr == null) {
            final CTXmlColumnPr ctXmlColumnPr = this.ctTableColumn.getXmlColumnPr();
            if (ctXmlColumnPr != null) {
                this.xmlColumnPr = new XSSFXmlColumnPr(this, ctXmlColumnPr);
            }
        }
        return this.xmlColumnPr;
    }
    
    public int getColumnIndex() {
        return this.table.findColumnIndex(this.getName());
    }
}
