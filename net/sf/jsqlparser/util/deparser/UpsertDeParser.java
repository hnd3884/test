package net.sf.jsqlparser.util.deparser;

import net.sf.jsqlparser.statement.select.SubSelect;
import net.sf.jsqlparser.expression.operators.relational.MultiExpressionList;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.statement.select.WithItem;
import java.util.Iterator;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.upsert.Upsert;
import net.sf.jsqlparser.statement.select.SelectVisitor;
import net.sf.jsqlparser.expression.ExpressionVisitor;
import net.sf.jsqlparser.expression.operators.relational.ItemsListVisitor;

public class UpsertDeParser implements ItemsListVisitor
{
    private StringBuilder buffer;
    private ExpressionVisitor expressionVisitor;
    private SelectVisitor selectVisitor;
    
    public UpsertDeParser(final ExpressionVisitor expressionVisitor, final SelectVisitor selectVisitor, final StringBuilder buffer) {
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
    
    public void deParse(final Upsert upsert) {
        this.buffer.append("UPSERT INTO ");
        this.buffer.append(upsert.getTable().getFullyQualifiedName());
        if (upsert.getColumns() != null) {
            this.appendColumns(upsert);
        }
        if (upsert.getItemsList() != null) {
            upsert.getItemsList().accept(this);
        }
        if (upsert.getSelect() != null) {
            this.appendSelect(upsert);
        }
        if (upsert.isUseDuplicate()) {
            this.appendDuplicate(upsert);
        }
    }
    
    private void appendColumns(final Upsert upsert) {
        this.buffer.append(" (");
        final Iterator<Column> iter = upsert.getColumns().iterator();
        while (iter.hasNext()) {
            final Column column = iter.next();
            this.buffer.append(column.getColumnName());
            if (iter.hasNext()) {
                this.buffer.append(", ");
            }
        }
        this.buffer.append(")");
    }
    
    private void appendSelect(final Upsert upsert) {
        this.buffer.append(" ");
        if (upsert.isUseSelectBrackets()) {
            this.buffer.append("(");
        }
        if (upsert.getSelect().getWithItemsList() != null) {
            this.buffer.append("WITH ");
            for (final WithItem with : upsert.getSelect().getWithItemsList()) {
                with.accept(this.selectVisitor);
            }
            this.buffer.append(" ");
        }
        upsert.getSelect().getSelectBody().accept(this.selectVisitor);
        if (upsert.isUseSelectBrackets()) {
            this.buffer.append(")");
        }
    }
    
    private void appendDuplicate(final Upsert upsert) {
        this.buffer.append(" ON DUPLICATE KEY UPDATE ");
        for (int i = 0; i < upsert.getDuplicateUpdateColumns().size(); ++i) {
            final Column column = upsert.getDuplicateUpdateColumns().get(i);
            this.buffer.append(column.getFullyQualifiedName()).append(" = ");
            final Expression expression = upsert.getDuplicateUpdateExpressionList().get(i);
            expression.accept(this.expressionVisitor);
            if (i < upsert.getDuplicateUpdateColumns().size() - 1) {
                this.buffer.append(", ");
            }
        }
    }
    
    @Override
    public void visit(final ExpressionList expressionList) {
        this.buffer.append(" VALUES (");
        final Iterator<Expression> iter = expressionList.getExpressions().iterator();
        while (iter.hasNext()) {
            final Expression expression = iter.next();
            expression.accept(this.expressionVisitor);
            if (iter.hasNext()) {
                this.buffer.append(", ");
            }
        }
        this.buffer.append(")");
    }
    
    @Override
    public void visit(final MultiExpressionList multiExprList) {
        this.buffer.append(" VALUES ");
        final Iterator<ExpressionList> it = multiExprList.getExprList().iterator();
        while (it.hasNext()) {
            this.buffer.append("(");
            final Iterator<Expression> iter = it.next().getExpressions().iterator();
            while (iter.hasNext()) {
                final Expression expression = iter.next();
                expression.accept(this.expressionVisitor);
                if (iter.hasNext()) {
                    this.buffer.append(", ");
                }
            }
            this.buffer.append(")");
            if (it.hasNext()) {
                this.buffer.append(", ");
            }
        }
    }
    
    @Override
    public void visit(final SubSelect subSelect) {
        subSelect.getSelectBody().accept(this.selectVisitor);
    }
    
    public ExpressionVisitor getExpressionVisitor() {
        return this.expressionVisitor;
    }
    
    public SelectVisitor getSelectVisitor() {
        return this.selectVisitor;
    }
    
    public void setExpressionVisitor(final ExpressionVisitor visitor) {
        this.expressionVisitor = visitor;
    }
    
    public void setSelectVisitor(final SelectVisitor visitor) {
        this.selectVisitor = visitor;
    }
}
