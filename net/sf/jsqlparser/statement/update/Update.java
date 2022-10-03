package net.sf.jsqlparser.statement.update;

import java.util.Iterator;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.StatementVisitor;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.Limit;
import net.sf.jsqlparser.statement.select.OrderByElement;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Table;
import java.util.List;
import net.sf.jsqlparser.statement.Statement;

public class Update implements Statement
{
    private List<Table> tables;
    private Expression where;
    private List<Column> columns;
    private List<Expression> expressions;
    private FromItem fromItem;
    private List<Join> joins;
    private Select select;
    private boolean useColumnsBrackets;
    private boolean useSelect;
    private List<OrderByElement> orderByElements;
    private Limit limit;
    private boolean returningAllColumns;
    private List<SelectExpressionItem> returningExpressionList;
    
    public Update() {
        this.useColumnsBrackets = true;
        this.useSelect = false;
        this.returningAllColumns = false;
        this.returningExpressionList = null;
    }
    
    @Override
    public void accept(final StatementVisitor statementVisitor) {
        statementVisitor.visit(this);
    }
    
    public List<Table> getTables() {
        return this.tables;
    }
    
    public Expression getWhere() {
        return this.where;
    }
    
    public void setTables(final List<Table> list) {
        this.tables = list;
    }
    
    public void setWhere(final Expression expression) {
        this.where = expression;
    }
    
    public List<Column> getColumns() {
        return this.columns;
    }
    
    public List<Expression> getExpressions() {
        return this.expressions;
    }
    
    public void setColumns(final List<Column> list) {
        this.columns = list;
    }
    
    public void setExpressions(final List<Expression> list) {
        this.expressions = list;
    }
    
    public FromItem getFromItem() {
        return this.fromItem;
    }
    
    public void setFromItem(final FromItem fromItem) {
        this.fromItem = fromItem;
    }
    
    public List<Join> getJoins() {
        return this.joins;
    }
    
    public void setJoins(final List<Join> joins) {
        this.joins = joins;
    }
    
    public Select getSelect() {
        return this.select;
    }
    
    public void setSelect(final Select select) {
        this.select = select;
    }
    
    public boolean isUseColumnsBrackets() {
        return this.useColumnsBrackets;
    }
    
    public void setUseColumnsBrackets(final boolean useColumnsBrackets) {
        this.useColumnsBrackets = useColumnsBrackets;
    }
    
    public boolean isUseSelect() {
        return this.useSelect;
    }
    
    public void setUseSelect(final boolean useSelect) {
        this.useSelect = useSelect;
    }
    
    public void setOrderByElements(final List<OrderByElement> orderByElements) {
        this.orderByElements = orderByElements;
    }
    
    public void setLimit(final Limit limit) {
        this.limit = limit;
    }
    
    public List<OrderByElement> getOrderByElements() {
        return this.orderByElements;
    }
    
    public Limit getLimit() {
        return this.limit;
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
    
    @Override
    public String toString() {
        final StringBuilder b = new StringBuilder("UPDATE ");
        b.append(PlainSelect.getStringList(this.getTables(), true, false)).append(" SET ");
        if (!this.useSelect) {
            for (int i = 0; i < this.getColumns().size(); ++i) {
                if (i != 0) {
                    b.append(", ");
                }
                b.append(this.columns.get(i)).append(" = ");
                b.append(this.expressions.get(i));
            }
        }
        else {
            if (this.useColumnsBrackets) {
                b.append("(");
            }
            for (int i = 0; i < this.getColumns().size(); ++i) {
                if (i != 0) {
                    b.append(", ");
                }
                b.append(this.columns.get(i));
            }
            if (this.useColumnsBrackets) {
                b.append(")");
            }
            b.append(" = ");
            b.append("(").append(this.select).append(")");
        }
        if (this.fromItem != null) {
            b.append(" FROM ").append(this.fromItem);
            if (this.joins != null) {
                for (final Join join : this.joins) {
                    if (join.isSimple()) {
                        b.append(", ").append(join);
                    }
                    else {
                        b.append(" ").append(join);
                    }
                }
            }
        }
        if (this.where != null) {
            b.append(" WHERE ");
            b.append(this.where);
        }
        if (this.orderByElements != null) {
            b.append(PlainSelect.orderByToString(this.orderByElements));
        }
        if (this.limit != null) {
            b.append(this.limit);
        }
        if (this.isReturningAllColumns()) {
            b.append(" RETURNING *");
        }
        else if (this.getReturningExpressionList() != null) {
            b.append(" RETURNING ").append(PlainSelect.getStringList(this.getReturningExpressionList(), true, false));
        }
        return b.toString();
    }
}
