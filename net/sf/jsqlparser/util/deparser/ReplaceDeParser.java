package net.sf.jsqlparser.util.deparser;

import net.sf.jsqlparser.expression.operators.relational.MultiExpressionList;
import net.sf.jsqlparser.statement.select.SubSelect;
import java.util.Iterator;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.replace.Replace;
import net.sf.jsqlparser.statement.select.SelectVisitor;
import net.sf.jsqlparser.expression.ExpressionVisitor;
import net.sf.jsqlparser.expression.operators.relational.ItemsListVisitor;

public class ReplaceDeParser implements ItemsListVisitor
{
    private StringBuilder buffer;
    private ExpressionVisitor expressionVisitor;
    private SelectVisitor selectVisitor;
    
    public ReplaceDeParser() {
    }
    
    public ReplaceDeParser(final ExpressionVisitor expressionVisitor, final SelectVisitor selectVisitor, final StringBuilder buffer) {
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
    
    public void deParse(final Replace replace) {
        this.buffer.append("REPLACE ");
        if (replace.isUseIntoTables()) {
            this.buffer.append("INTO ");
        }
        this.buffer.append(replace.getTable().getFullyQualifiedName());
        if (replace.getItemsList() != null) {
            if (replace.getColumns() != null) {
                this.buffer.append(" (");
                for (int i = 0; i < replace.getColumns().size(); ++i) {
                    final Column column = replace.getColumns().get(i);
                    this.buffer.append(column.getFullyQualifiedName());
                    if (i < replace.getColumns().size() - 1) {
                        this.buffer.append(", ");
                    }
                }
                this.buffer.append(") ");
            }
            else {
                this.buffer.append(" ");
            }
        }
        else {
            this.buffer.append(" SET ");
            for (int i = 0; i < replace.getColumns().size(); ++i) {
                final Column column = replace.getColumns().get(i);
                this.buffer.append(column.getFullyQualifiedName()).append("=");
                final Expression expression = replace.getExpressions().get(i);
                expression.accept(this.expressionVisitor);
                if (i < replace.getColumns().size() - 1) {
                    this.buffer.append(", ");
                }
            }
        }
        if (replace.getItemsList() != null) {
            replace.getItemsList().accept(this);
        }
    }
    
    @Override
    public void visit(final ExpressionList expressionList) {
        this.buffer.append("VALUES (");
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
    
    @Override
    public void visit(final MultiExpressionList multiExprList) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
