package net.sf.jsqlparser.statement.create.table;

import net.sf.jsqlparser.statement.select.PlainSelect;
import java.util.List;

public class ColumnDefinition
{
    private String columnName;
    private ColDataType colDataType;
    private List<String> columnSpecStrings;
    
    public List<String> getColumnSpecStrings() {
        return this.columnSpecStrings;
    }
    
    public void setColumnSpecStrings(final List<String> list) {
        this.columnSpecStrings = list;
    }
    
    public ColDataType getColDataType() {
        return this.colDataType;
    }
    
    public void setColDataType(final ColDataType type) {
        this.colDataType = type;
    }
    
    public String getColumnName() {
        return this.columnName;
    }
    
    public void setColumnName(final String string) {
        this.columnName = string;
    }
    
    @Override
    public String toString() {
        return this.columnName + " " + this.colDataType + ((this.columnSpecStrings != null) ? (" " + PlainSelect.getStringList(this.columnSpecStrings, false, false)) : "");
    }
}
