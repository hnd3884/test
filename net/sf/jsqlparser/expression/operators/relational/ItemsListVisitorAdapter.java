package net.sf.jsqlparser.expression.operators.relational;

import java.util.Iterator;
import net.sf.jsqlparser.statement.select.SubSelect;

public class ItemsListVisitorAdapter implements ItemsListVisitor
{
    @Override
    public void visit(final SubSelect subSelect) {
    }
    
    @Override
    public void visit(final ExpressionList expressionList) {
    }
    
    @Override
    public void visit(final MultiExpressionList multiExprList) {
        for (final ExpressionList list : multiExprList.getExprList()) {
            this.visit(list);
        }
    }
}
