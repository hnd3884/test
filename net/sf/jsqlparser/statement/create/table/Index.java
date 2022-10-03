package net.sf.jsqlparser.statement.create.table;

import net.sf.jsqlparser.statement.select.PlainSelect;
import java.util.List;

public class Index
{
    private String type;
    private List<String> columnsNames;
    private String name;
    private List<String> idxSpec;
    
    public List<String> getColumnsNames() {
        return this.columnsNames;
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getType() {
        return this.type;
    }
    
    public void setColumnsNames(final List<String> list) {
        this.columnsNames = list;
    }
    
    public void setName(final String string) {
        this.name = string;
    }
    
    public void setType(final String string) {
        this.type = string;
    }
    
    public List<String> getIndexSpec() {
        return this.idxSpec;
    }
    
    public void setIndexSpec(final List<String> idxSpec) {
        this.idxSpec = idxSpec;
    }
    
    @Override
    public String toString() {
        final String idxSpecText = PlainSelect.getStringList(this.idxSpec, false, false);
        return this.type + ((this.name != null) ? (" " + this.name) : "") + " " + PlainSelect.getStringList(this.columnsNames, true, true) + ("".equals(idxSpecText) ? "" : (" " + idxSpecText));
    }
}
