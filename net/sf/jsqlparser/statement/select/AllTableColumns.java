package net.sf.jsqlparser.statement.select;

import net.sf.jsqlparser.schema.Table;

public class AllTableColumns implements SelectItem
{
    private Table table;
    
    public AllTableColumns() {
    }
    
    public AllTableColumns(final Table tableName) {
        this.table = tableName;
    }
    
    public Table getTable() {
        return this.table;
    }
    
    public void setTable(final Table table) {
        this.table = table;
    }
    
    @Override
    public void accept(final SelectItemVisitor selectItemVisitor) {
        selectItemVisitor.visit(this);
    }
    
    @Override
    public String toString() {
        return this.table + ".*";
    }
}
