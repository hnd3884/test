package net.sf.jsqlparser.expression.operators.relational;

import java.util.Iterator;
import java.util.Arrays;
import net.sf.jsqlparser.expression.Expression;
import java.util.ArrayList;
import java.util.List;

public class MultiExpressionList implements ItemsList
{
    private List<ExpressionList> exprList;
    
    public MultiExpressionList() {
        this.exprList = new ArrayList<ExpressionList>();
    }
    
    @Override
    public void accept(final ItemsListVisitor itemsListVisitor) {
        itemsListVisitor.visit(this);
    }
    
    public List<ExpressionList> getExprList() {
        return this.exprList;
    }
    
    public void addExpressionList(final ExpressionList el) {
        if (!this.exprList.isEmpty() && this.exprList.get(0).getExpressions().size() != el.getExpressions().size()) {
            throw new IllegalArgumentException("different count of parameters");
        }
        this.exprList.add(el);
    }
    
    public void addExpressionList(final List<Expression> list) {
        this.addExpressionList(new ExpressionList(list));
    }
    
    public void addExpressionList(final Expression expr) {
        this.addExpressionList(new ExpressionList(Arrays.asList(expr)));
    }
    
    @Override
    public String toString() {
        final StringBuilder b = new StringBuilder();
        final Iterator<ExpressionList> it = this.exprList.iterator();
        while (it.hasNext()) {
            b.append(it.next().toString());
            if (it.hasNext()) {
                b.append(", ");
            }
        }
        return b.toString();
    }
}
