package net.sf.jsqlparser.statement.merge;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Column;
import java.util.List;

public class MergeUpdate
{
    private List<Column> columns;
    private List<Expression> values;
    private Expression whereCondition;
    private Expression deleteWhereCondition;
    
    public MergeUpdate() {
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
    
    public Expression getWhereCondition() {
        return this.whereCondition;
    }
    
    public void setWhereCondition(final Expression whereCondition) {
        this.whereCondition = whereCondition;
    }
    
    public Expression getDeleteWhereCondition() {
        return this.deleteWhereCondition;
    }
    
    public void setDeleteWhereCondition(final Expression deleteWhereCondition) {
        this.deleteWhereCondition = deleteWhereCondition;
    }
    
    @Override
    public String toString() {
        final StringBuilder b = new StringBuilder();
        b.append(" WHEN MATCHED THEN UPDATE SET ");
        for (int i = 0; i < this.columns.size(); ++i) {
            if (i != 0) {
                b.append(", ");
            }
            b.append(this.columns.get(i).toString()).append(" = ").append(this.values.get(i).toString());
        }
        if (this.whereCondition != null) {
            b.append(" WHERE ").append(this.whereCondition.toString());
        }
        if (this.deleteWhereCondition != null) {
            b.append(" DELETE WHERE ").append(this.deleteWhereCondition.toString());
        }
        return b.toString();
    }
}
