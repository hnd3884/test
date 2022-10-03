package net.sf.jsqlparser.statement.create.table;

import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.StatementVisitor;
import net.sf.jsqlparser.statement.select.Select;
import java.util.List;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;

public class CreateTable implements Statement
{
    private Table table;
    private boolean unlogged;
    private List<String> createOptionsStrings;
    private List<String> tableOptionsStrings;
    private List<ColumnDefinition> columnDefinitions;
    private List<Index> indexes;
    private Select select;
    private boolean selectParenthesis;
    private boolean ifNotExists;
    
    public CreateTable() {
        this.unlogged = false;
        this.ifNotExists = false;
    }
    
    @Override
    public void accept(final StatementVisitor statementVisitor) {
        statementVisitor.visit(this);
    }
    
    public Table getTable() {
        return this.table;
    }
    
    public void setTable(final Table table) {
        this.table = table;
    }
    
    public boolean isUnlogged() {
        return this.unlogged;
    }
    
    public void setUnlogged(final boolean unlogged) {
        this.unlogged = unlogged;
    }
    
    public List<ColumnDefinition> getColumnDefinitions() {
        return this.columnDefinitions;
    }
    
    public void setColumnDefinitions(final List<ColumnDefinition> list) {
        this.columnDefinitions = list;
    }
    
    public List<?> getTableOptionsStrings() {
        return this.tableOptionsStrings;
    }
    
    public void setTableOptionsStrings(final List<String> list) {
        this.tableOptionsStrings = list;
    }
    
    public List<String> getCreateOptionsStrings() {
        return this.createOptionsStrings;
    }
    
    public void setCreateOptionsStrings(final List<String> createOptionsStrings) {
        this.createOptionsStrings = createOptionsStrings;
    }
    
    public List<Index> getIndexes() {
        return this.indexes;
    }
    
    public void setIndexes(final List<Index> list) {
        this.indexes = list;
    }
    
    public Select getSelect() {
        return this.select;
    }
    
    public void setSelect(final Select select, final boolean parenthesis) {
        this.select = select;
        this.selectParenthesis = parenthesis;
    }
    
    public boolean isIfNotExists() {
        return this.ifNotExists;
    }
    
    public void setIfNotExists(final boolean ifNotExists) {
        this.ifNotExists = ifNotExists;
    }
    
    public boolean isSelectParenthesis() {
        return this.selectParenthesis;
    }
    
    public void setSelectParenthesis(final boolean selectParenthesis) {
        this.selectParenthesis = selectParenthesis;
    }
    
    @Override
    public String toString() {
        final String createOps = PlainSelect.getStringList(this.createOptionsStrings, false, false);
        String sql = "CREATE " + (this.unlogged ? "UNLOGGED " : "") + ("".equals(createOps) ? "" : (createOps + " ")) + "TABLE " + (this.ifNotExists ? "IF NOT EXISTS " : "") + this.table;
        if (this.select != null) {
            sql = sql + " AS " + (this.selectParenthesis ? "(" : "") + this.select.toString() + (this.selectParenthesis ? ")" : "");
        }
        else {
            sql += " (";
            sql += PlainSelect.getStringList(this.columnDefinitions, true, false);
            if (this.indexes != null && !this.indexes.isEmpty()) {
                sql += ", ";
                sql += PlainSelect.getStringList(this.indexes);
            }
            sql += ")";
            final String options = PlainSelect.getStringList(this.tableOptionsStrings, false, false);
            if (options != null && options.length() > 0) {
                sql = sql + " " + options;
            }
        }
        return sql;
    }
}
