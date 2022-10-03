package net.sf.jsqlparser.util.deparser;

import net.sf.jsqlparser.statement.select.OrderByElement;
import java.util.Iterator;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Column;
import java.util.List;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.update.Update;
import net.sf.jsqlparser.statement.select.SelectVisitorAdapter;
import net.sf.jsqlparser.expression.ExpressionVisitorAdapter;
import net.sf.jsqlparser.statement.select.SelectVisitor;
import net.sf.jsqlparser.expression.ExpressionVisitor;
import net.sf.jsqlparser.statement.select.OrderByVisitor;

public class UpdateDeParser implements OrderByVisitor
{
    private StringBuilder buffer;
    private ExpressionVisitor expressionVisitor;
    private SelectVisitor selectVisitor;
    
    public UpdateDeParser() {
        this.buffer = new StringBuilder();
        this.expressionVisitor = new ExpressionVisitorAdapter();
        this.selectVisitor = new SelectVisitorAdapter();
    }
    
    public UpdateDeParser(final ExpressionVisitor expressionVisitor, final SelectVisitor selectVisitor, final StringBuilder buffer) {
        this.buffer = new StringBuilder();
        this.expressionVisitor = new ExpressionVisitorAdapter();
        this.selectVisitor = new SelectVisitorAdapter();
        this.buffer = buffer;
        this.expressionVisitor = expressionVisitor;
        this.selectVisitor = selectVisitor;
    }
    
    public StringBuilder getBuffer() {
        return this.buffer;
    }
    
    public void setBuffer(final StringBuilder buffer) {
        this.buffer = buffer;
    }
    
    public void deParse(final Update update) {
        this.buffer.append("UPDATE ").append(PlainSelect.getStringList(update.getTables(), true, false)).append(" SET ");
        if (!update.isUseSelect()) {
            for (int i = 0; i < update.getColumns().size(); ++i) {
                final Column column = update.getColumns().get(i);
                column.accept(this.expressionVisitor);
                this.buffer.append(" = ");
                final Expression expression = update.getExpressions().get(i);
                expression.accept(this.expressionVisitor);
                if (i < update.getColumns().size() - 1) {
                    this.buffer.append(", ");
                }
            }
        }
        else {
            if (update.isUseColumnsBrackets()) {
                this.buffer.append("(");
            }
            for (int i = 0; i < update.getColumns().size(); ++i) {
                if (i != 0) {
                    this.buffer.append(", ");
                }
                final Column column = update.getColumns().get(i);
                column.accept(this.expressionVisitor);
            }
            if (update.isUseColumnsBrackets()) {
                this.buffer.append(")");
            }
            this.buffer.append(" = ");
            this.buffer.append("(");
            final Select select = update.getSelect();
            select.getSelectBody().accept(this.selectVisitor);
            this.buffer.append(")");
        }
        if (update.getFromItem() != null) {
            this.buffer.append(" FROM ").append(update.getFromItem());
            if (update.getJoins() != null) {
                for (final Join join : update.getJoins()) {
                    if (join.isSimple()) {
                        this.buffer.append(", ").append(join);
                    }
                    else {
                        this.buffer.append(" ").append(join);
                    }
                }
            }
        }
        if (update.getWhere() != null) {
            this.buffer.append(" WHERE ");
            update.getWhere().accept(this.expressionVisitor);
        }
        if (update.getOrderByElements() != null) {
            new OrderByDeParser(this.expressionVisitor, this.buffer).deParse(update.getOrderByElements());
        }
        if (update.getLimit() != null) {
            new LimitDeparser(this.buffer).deParse(update.getLimit());
        }
        if (update.isReturningAllColumns()) {
            this.buffer.append(" RETURNING *");
        }
        else if (update.getReturningExpressionList() != null) {
            this.buffer.append(" RETURNING ");
            final Iterator<SelectExpressionItem> iter = update.getReturningExpressionList().iterator();
            while (iter.hasNext()) {
                this.buffer.append(iter.next().toString());
                if (iter.hasNext()) {
                    this.buffer.append(", ");
                }
            }
        }
    }
    
    public ExpressionVisitor getExpressionVisitor() {
        return this.expressionVisitor;
    }
    
    public void setExpressionVisitor(final ExpressionVisitor visitor) {
        this.expressionVisitor = visitor;
    }
    
    @Override
    public void visit(final OrderByElement orderBy) {
        orderBy.getExpression().accept(this.expressionVisitor);
        if (!orderBy.isAsc()) {
            this.buffer.append(" DESC");
        }
        else if (orderBy.isAscDescPresent()) {
            this.buffer.append(" ASC");
        }
        if (orderBy.getNullOrdering() != null) {
            this.buffer.append(' ');
            this.buffer.append((orderBy.getNullOrdering() == OrderByElement.NullOrdering.NULLS_FIRST) ? "NULLS FIRST" : "NULLS LAST");
        }
    }
}
