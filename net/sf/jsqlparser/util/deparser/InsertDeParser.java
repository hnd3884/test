package net.sf.jsqlparser.util.deparser;

import net.sf.jsqlparser.statement.select.SubSelect;
import net.sf.jsqlparser.expression.operators.relational.MultiExpressionList;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import java.util.Iterator;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.statement.select.WithItem;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.SelectVisitor;
import net.sf.jsqlparser.expression.ExpressionVisitor;
import net.sf.jsqlparser.expression.operators.relational.ItemsListVisitor;

public class InsertDeParser implements ItemsListVisitor
{
    private StringBuilder buffer;
    private ExpressionVisitor expressionVisitor;
    private SelectVisitor selectVisitor;
    
    public InsertDeParser() {
    }
    
    public InsertDeParser(final ExpressionVisitor expressionVisitor, final SelectVisitor selectVisitor, final StringBuilder buffer) {
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
    
    public void deParse(final Insert insert) {
        this.buffer.append("INSERT ");
        if (insert.getModifierPriority() != null) {
            this.buffer.append(insert.getModifierPriority()).append(" ");
        }
        if (insert.isModifierIgnore()) {
            this.buffer.append("IGNORE ");
        }
        this.buffer.append("INTO ");
        this.buffer.append(insert.getTable().getFullyQualifiedName());
        if (insert.getColumns() != null) {
            this.buffer.append(" (");
            final Iterator<Column> iter = insert.getColumns().iterator();
            while (iter.hasNext()) {
                final Column column = iter.next();
                this.buffer.append(column.getColumnName());
                if (iter.hasNext()) {
                    this.buffer.append(", ");
                }
            }
            this.buffer.append(")");
        }
        if (insert.getItemsList() != null) {
            insert.getItemsList().accept(this);
        }
        if (insert.getSelect() != null) {
            this.buffer.append(" ");
            if (insert.isUseSelectBrackets()) {
                this.buffer.append("(");
            }
            if (insert.getSelect().getWithItemsList() != null) {
                this.buffer.append("WITH ");
                for (final WithItem with : insert.getSelect().getWithItemsList()) {
                    with.accept(this.selectVisitor);
                }
                this.buffer.append(" ");
            }
            insert.getSelect().getSelectBody().accept(this.selectVisitor);
            if (insert.isUseSelectBrackets()) {
                this.buffer.append(")");
            }
        }
        if (insert.isUseDuplicate()) {
            this.buffer.append(" ON DUPLICATE KEY UPDATE ");
            for (int i = 0; i < insert.getDuplicateUpdateColumns().size(); ++i) {
                final Column column = insert.getDuplicateUpdateColumns().get(i);
                this.buffer.append(column.getFullyQualifiedName()).append(" = ");
                final Expression expression = insert.getDuplicateUpdateExpressionList().get(i);
                expression.accept(this.expressionVisitor);
                if (i < insert.getDuplicateUpdateColumns().size() - 1) {
                    this.buffer.append(", ");
                }
            }
        }
        if (insert.isReturningAllColumns()) {
            this.buffer.append(" RETURNING *");
        }
        else if (insert.getReturningExpressionList() != null) {
            this.buffer.append(" RETURNING ");
            final Iterator<SelectExpressionItem> iter2 = insert.getReturningExpressionList().iterator();
            while (iter2.hasNext()) {
                this.buffer.append(iter2.next().toString());
                if (iter2.hasNext()) {
                    this.buffer.append(", ");
                }
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
