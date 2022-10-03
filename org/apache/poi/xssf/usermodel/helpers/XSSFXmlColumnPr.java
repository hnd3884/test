package org.apache.poi.xssf.usermodel.helpers;

import org.openxmlformats.schemas.spreadsheetml.x2006.main.STXmlDataType;
import org.apache.poi.util.Removal;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTableColumn;
import org.apache.poi.util.Internal;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTXmlColumnPr;
import org.apache.poi.xssf.usermodel.XSSFTableColumn;
import org.apache.poi.xssf.usermodel.XSSFTable;

public class XSSFXmlColumnPr
{
    private XSSFTable table;
    private XSSFTableColumn tableColumn;
    private CTXmlColumnPr ctXmlColumnPr;
    
    @Internal
    public XSSFXmlColumnPr(final XSSFTableColumn tableColumn, final CTXmlColumnPr ctXmlColumnPr) {
        this.table = tableColumn.getTable();
        this.tableColumn = tableColumn;
        this.ctXmlColumnPr = ctXmlColumnPr;
    }
    
    @Deprecated
    @Removal(version = "4.2")
    public XSSFXmlColumnPr(final XSSFTable table, final CTTableColumn ctTableColum, final CTXmlColumnPr ctXmlColumnPr) {
        this.table = table;
        this.tableColumn = table.getColumns().get(table.findColumnIndex(ctTableColum.getName()));
        this.ctXmlColumnPr = ctXmlColumnPr;
    }
    
    public XSSFTableColumn getTableColumn() {
        return this.tableColumn;
    }
    
    public long getMapId() {
        return this.ctXmlColumnPr.getMapId();
    }
    
    public String getXPath() {
        return this.ctXmlColumnPr.getXpath();
    }
    
    @Deprecated
    @Removal(version = "4.2")
    public long getId() {
        return this.tableColumn.getId();
    }
    
    public String getLocalXPath() {
        final StringBuilder localXPath = new StringBuilder();
        final int numberOfCommonXPathAxis = this.table.getCommonXpath().split("/").length - 1;
        final String[] xPathTokens = this.ctXmlColumnPr.getXpath().split("/");
        for (int i = numberOfCommonXPathAxis; i < xPathTokens.length; ++i) {
            localXPath.append("/" + xPathTokens[i]);
        }
        return localXPath.toString();
    }
    
    public STXmlDataType.Enum getXmlDataType() {
        return this.ctXmlColumnPr.getXmlDataType();
    }
}
