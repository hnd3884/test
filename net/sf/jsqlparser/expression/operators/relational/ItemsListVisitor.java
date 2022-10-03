package net.sf.jsqlparser.expression.operators.relational;

import net.sf.jsqlparser.statement.select.SubSelect;

public interface ItemsListVisitor
{
    void visit(final SubSelect p0);
    
    void visit(final ExpressionList p0);
    
    void visit(final MultiExpressionList p0);
}
