package net.sf.jsqlparser.statement.upsert;

import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.StatementVisitor;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.expression.operators.relational.ItemsList;
import net.sf.jsqlparser.schema.Column;
import java.util.List;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;

public class Upsert implements Statement
{
    private Table table;
    private List<Column> columns;
    private ItemsList itemsList;
    private boolean useValues;
    private Select select;
    private boolean useSelectBrackets;
    private boolean useDuplicate;
    private List<Column> duplicateUpdateColumns;
    private List<Expression> duplicateUpdateExpressionList;
    
    public Upsert() {
        this.useValues = true;
        this.useSelectBrackets = true;
        this.useDuplicate = false;
    }
    
    @Override
    public void accept(final StatementVisitor statementVisitor) {
        statementVisitor.visit(this);
    }
    
    public void setTable(final Table name) {
        this.table = name;
    }
    
    public Table getTable() {
        return this.table;
    }
    
    public void setColumns(final List<Column> list) {
        this.columns = list;
    }
    
    public List<Column> getColumns() {
        return this.columns;
    }
    
    public void setItemsList(final ItemsList list) {
        this.itemsList = list;
    }
    
    public ItemsList getItemsList() {
        return this.itemsList;
    }
    
    public void setUseValues(final boolean useValues) {
        this.useValues = useValues;
    }
    
    public boolean isUseValues() {
        return this.useValues;
    }
    
    public void setSelect(final Select select) {
        this.select = select;
    }
    
    public Select getSelect() {
        return this.select;
    }
    
    public void setUseSelectBrackets(final boolean useSelectBrackets) {
        this.useSelectBrackets = useSelectBrackets;
    }
    
    public boolean isUseSelectBrackets() {
        return this.useSelectBrackets;
    }
    
    public void setUseDuplicate(final boolean useDuplicate) {
        this.useDuplicate = useDuplicate;
    }
    
    public boolean isUseDuplicate() {
        return this.useDuplicate;
    }
    
    public void setDuplicateUpdateColumns(final List<Column> duplicateUpdateColumns) {
        this.duplicateUpdateColumns = duplicateUpdateColumns;
    }
    
    public List<Column> getDuplicateUpdateColumns() {
        return this.duplicateUpdateColumns;
    }
    
    public void setDuplicateUpdateExpressionList(final List<Expression> duplicateUpdateExpressionList) {
        this.duplicateUpdateExpressionList = duplicateUpdateExpressionList;
    }
    
    public List<Expression> getDuplicateUpdateExpressionList() {
        return this.duplicateUpdateExpressionList;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("UPSERT INTO ");
        sb.append(this.table).append(" ");
        if (this.columns != null) {
            sb.append(PlainSelect.getStringList(this.columns, true, true)).append(" ");
        }
        if (this.useValues) {
            sb.append("VALUES ");
        }
        if (this.itemsList != null) {
            sb.append(this.itemsList);
        }
        else {
            if (this.useSelectBrackets) {
                sb.append("(");
            }
            if (this.select != null) {
                sb.append(this.select);
            }
            if (this.useSelectBrackets) {
                sb.append(")");
            }
        }
        if (this.useDuplicate) {
            sb.append(" ON DUPLICATE KEY UPDATE ");
            for (int i = 0; i < this.getDuplicateUpdateColumns().size(); ++i) {
                if (i != 0) {
                    sb.append(", ");
                }
                sb.append(this.duplicateUpdateColumns.get(i)).append(" = ");
                sb.append(this.duplicateUpdateExpressionList.get(i));
            }
        }
        return sb.toString();
    }
}
