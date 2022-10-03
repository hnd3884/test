package net.sf.jsqlparser.util.deparser;

import java.util.Iterator;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.expression.ExpressionVisitorAdapter;
import net.sf.jsqlparser.expression.ExpressionVisitor;

public class DeleteDeParser
{
    private StringBuilder buffer;
    private ExpressionVisitor expressionVisitor;
    
    public DeleteDeParser() {
        this.buffer = new StringBuilder();
        this.expressionVisitor = new ExpressionVisitorAdapter();
    }
    
    public DeleteDeParser(final ExpressionVisitor expressionVisitor, final StringBuilder buffer) {
        this.buffer = new StringBuilder();
        this.expressionVisitor = new ExpressionVisitorAdapter();
        this.buffer = buffer;
        this.expressionVisitor = expressionVisitor;
    }
    
    public StringBuilder getBuffer() {
        return this.buffer;
    }
    
    public void setBuffer(final StringBuilder buffer) {
        this.buffer = buffer;
    }
    
    public void deParse(final Delete delete) {
        this.buffer.append("DELETE");
        if (delete.getTables() != null && delete.getTables().size() > 0) {
            for (final Table table : delete.getTables()) {
                this.buffer.append(" ").append(table.getFullyQualifiedName());
            }
        }
        this.buffer.append(" FROM ").append(delete.getTable().toString());
        if (delete.getJoins() != null) {
            for (final Join join : delete.getJoins()) {
                if (join.isSimple()) {
                    this.buffer.append(", ").append(join);
                }
                else {
                    this.buffer.append(" ").append(join);
                }
            }
        }
        if (delete.getWhere() != null) {
            this.buffer.append(" WHERE ");
            delete.getWhere().accept(this.expressionVisitor);
        }
        if (delete.getOrderByElements() != null) {
            new OrderByDeParser(this.expressionVisitor, this.buffer).deParse(delete.getOrderByElements());
        }
        if (delete.getLimit() != null) {
            new LimitDeparser(this.buffer).deParse(delete.getLimit());
        }
    }
    
    public ExpressionVisitor getExpressionVisitor() {
        return this.expressionVisitor;
    }
    
    public void setExpressionVisitor(final ExpressionVisitor visitor) {
        this.expressionVisitor = visitor;
    }
}
