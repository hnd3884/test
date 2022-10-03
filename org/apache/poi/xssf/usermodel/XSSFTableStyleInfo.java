package org.apache.poi.xssf.usermodel;

import org.apache.poi.ss.usermodel.TableStyle;
import org.apache.poi.xssf.model.StylesTable;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTableStyleInfo;
import org.apache.poi.ss.usermodel.TableStyleInfo;

public class XSSFTableStyleInfo implements TableStyleInfo
{
    private final CTTableStyleInfo styleInfo;
    private final StylesTable stylesTable;
    private TableStyle style;
    private boolean columnStripes;
    private boolean rowStripes;
    private boolean firstColumn;
    private boolean lastColumn;
    
    public XSSFTableStyleInfo(final StylesTable stylesTable, final CTTableStyleInfo tableStyleInfo) {
        this.columnStripes = tableStyleInfo.getShowColumnStripes();
        this.rowStripes = tableStyleInfo.getShowRowStripes();
        this.firstColumn = tableStyleInfo.getShowFirstColumn();
        this.lastColumn = tableStyleInfo.getShowLastColumn();
        this.style = stylesTable.getTableStyle(tableStyleInfo.getName());
        this.stylesTable = stylesTable;
        this.styleInfo = tableStyleInfo;
    }
    
    public boolean isShowColumnStripes() {
        return this.columnStripes;
    }
    
    public void setShowColumnStripes(final boolean show) {
        this.columnStripes = show;
        this.styleInfo.setShowColumnStripes(show);
    }
    
    public boolean isShowRowStripes() {
        return this.rowStripes;
    }
    
    public void setShowRowStripes(final boolean show) {
        this.rowStripes = show;
        this.styleInfo.setShowRowStripes(show);
    }
    
    public boolean isShowFirstColumn() {
        return this.firstColumn;
    }
    
    public void setFirstColumn(final boolean showFirstColumn) {
        this.firstColumn = showFirstColumn;
        this.styleInfo.setShowFirstColumn(showFirstColumn);
    }
    
    public boolean isShowLastColumn() {
        return this.lastColumn;
    }
    
    public void setLastColumn(final boolean showLastColumn) {
        this.lastColumn = showLastColumn;
        this.styleInfo.setShowLastColumn(showLastColumn);
    }
    
    public String getName() {
        return this.style.getName();
    }
    
    public void setName(final String name) {
        this.styleInfo.setName(name);
        this.style = this.stylesTable.getTableStyle(name);
    }
    
    public TableStyle getStyle() {
        return this.style;
    }
}
