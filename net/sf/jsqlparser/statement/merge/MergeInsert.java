package net.sf.jsqlparser.statement.merge;

import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Column;
import java.util.List;

public class MergeInsert
{
    private List<Column> columns;
    private List<Expression> values;
    
    public MergeInsert() {
        this.columns = null;
        this.values = null;
    }
    
    public List<Column> getColumns() {
        return this.columns;
    }
    
    public void setColumns(final List<Column> columns) {
        this.columns = columns;
    }
    
    public List<Expression> getValues() {
        return this.values;
    }
    
    public void setValues(final List<Expression> values) {
        this.values = values;
    }
    
    @Override
    public String toString() {
        return " WHEN NOT MATCHED THEN INSERT " + PlainSelect.getStringList(this.columns, true, true) + " VALUES " + PlainSelect.getStringList(this.values, true, true);
    }
}
