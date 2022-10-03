package net.sf.jsqlparser.statement.insert;

import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.StatementVisitor;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.expression.operators.relational.ItemsList;
import net.sf.jsqlparser.schema.Column;
import java.util.List;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;

public class Insert implements Statement
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
    private InsertModifierPriority modifierPriority;
    private boolean modifierIgnore;
    private boolean returningAllColumns;
    private List<SelectExpressionItem> returningExpressionList;
    
    public Insert() {
        this.useValues = true;
        this.useSelectBrackets = true;
        this.useDuplicate = false;
        this.modifierPriority = null;
        this.modifierIgnore = false;
        this.returningAllColumns = false;
        this.returningExpressionList = null;
    }
    
    @Override
    public void accept(final StatementVisitor statementVisitor) {
        statementVisitor.visit(this);
    }
    
    public Table getTable() {
        return this.table;
    }
    
    public void setTable(final Table name) {
        this.table = name;
    }
    
    public List<Column> getColumns() {
        return this.columns;
    }
    
    public void setColumns(final List<Column> list) {
        this.columns = list;
    }
    
    public ItemsList getItemsList() {
        return this.itemsList;
    }
    
    public void setItemsList(final ItemsList list) {
        this.itemsList = list;
    }
    
    public boolean isUseValues() {
        return this.useValues;
    }
    
    public void setUseValues(final boolean useValues) {
        this.useValues = useValues;
    }
    
    public boolean isReturningAllColumns() {
        return this.returningAllColumns;
    }
    
    public void setReturningAllColumns(final boolean returningAllColumns) {
        this.returningAllColumns = returningAllColumns;
    }
    
    public List<SelectExpressionItem> getReturningExpressionList() {
        return this.returningExpressionList;
    }
    
    public void setReturningExpressionList(final List<SelectExpressionItem> returningExpressionList) {
        this.returningExpressionList = returningExpressionList;
    }
    
    public Select getSelect() {
        return this.select;
    }
    
    public void setSelect(final Select select) {
        this.select = select;
    }
    
    public boolean isUseSelectBrackets() {
        return this.useSelectBrackets;
    }
    
    public void setUseSelectBrackets(final boolean useSelectBrackets) {
        this.useSelectBrackets = useSelectBrackets;
    }
    
    public boolean isUseDuplicate() {
        return this.useDuplicate;
    }
    
    public void setUseDuplicate(final boolean useDuplicate) {
        this.useDuplicate = useDuplicate;
    }
    
    public List<Column> getDuplicateUpdateColumns() {
        return this.duplicateUpdateColumns;
    }
    
    public void setDuplicateUpdateColumns(final List<Column> duplicateUpdateColumns) {
        this.duplicateUpdateColumns = duplicateUpdateColumns;
    }
    
    public List<Expression> getDuplicateUpdateExpressionList() {
        return this.duplicateUpdateExpressionList;
    }
    
    public void setDuplicateUpdateExpressionList(final List<Expression> duplicateUpdateExpressionList) {
        this.duplicateUpdateExpressionList = duplicateUpdateExpressionList;
    }
    
    public InsertModifierPriority getModifierPriority() {
        return this.modifierPriority;
    }
    
    public void setModifierPriority(final InsertModifierPriority modifierPriority) {
        this.modifierPriority = modifierPriority;
    }
    
    public boolean isModifierIgnore() {
        return this.modifierIgnore;
    }
    
    public void setModifierIgnore(final boolean modifierIgnore) {
        this.modifierIgnore = modifierIgnore;
    }
    
    @Override
    public String toString() {
        final StringBuilder sql = new StringBuilder();
        sql.append("INSERT ");
        if (this.modifierPriority != null) {
            sql.append(this.modifierPriority.name()).append(" ");
        }
        if (this.modifierIgnore) {
            sql.append("IGNORE ");
        }
        sql.append("INTO ");
        sql.append(this.table).append(" ");
        if (this.columns != null) {
            sql.append(PlainSelect.getStringList(this.columns, true, true)).append(" ");
        }
        if (this.useValues) {
            sql.append("VALUES ");
        }
        if (this.itemsList != null) {
            sql.append(this.itemsList);
        }
        else {
            if (this.useSelectBrackets) {
                sql.append("(");
            }
            if (this.select != null) {
                sql.append(this.select);
            }
            if (this.useSelectBrackets) {
                sql.append(")");
            }
        }
        if (this.useDuplicate) {
            sql.append(" ON DUPLICATE KEY UPDATE ");
            for (int i = 0; i < this.getDuplicateUpdateColumns().size(); ++i) {
                if (i != 0) {
                    sql.append(", ");
                }
                sql.append(this.duplicateUpdateColumns.get(i)).append(" = ");
                sql.append(this.duplicateUpdateExpressionList.get(i));
            }
        }
        if (this.isReturningAllColumns()) {
            sql.append(" RETURNING *");
        }
        else if (this.getReturningExpressionList() != null) {
            sql.append(" RETURNING ").append(PlainSelect.getStringList(this.getReturningExpressionList(), true, false));
        }
        return sql.toString();
    }
}
