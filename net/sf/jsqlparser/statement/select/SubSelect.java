package net.sf.jsqlparser.statement.select;

import java.util.Iterator;
import net.sf.jsqlparser.expression.operators.relational.ItemsListVisitor;
import net.sf.jsqlparser.expression.ExpressionVisitor;
import java.util.List;
import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.expression.operators.relational.ItemsList;
import net.sf.jsqlparser.expression.Expression;

public class SubSelect implements FromItem, Expression, ItemsList
{
    private SelectBody selectBody;
    private Alias alias;
    private boolean useBrackets;
    private List<WithItem> withItemsList;
    private Pivot pivot;
    
    public SubSelect() {
        this.useBrackets = true;
    }
    
    @Override
    public void accept(final FromItemVisitor fromItemVisitor) {
        fromItemVisitor.visit(this);
    }
    
    public SelectBody getSelectBody() {
        return this.selectBody;
    }
    
    public void setSelectBody(final SelectBody body) {
        this.selectBody = body;
    }
    
    @Override
    public void accept(final ExpressionVisitor expressionVisitor) {
        expressionVisitor.visit(this);
    }
    
    @Override
    public Alias getAlias() {
        return this.alias;
    }
    
    @Override
    public void setAlias(final Alias alias) {
        this.alias = alias;
    }
    
    @Override
    public Pivot getPivot() {
        return this.pivot;
    }
    
    @Override
    public void setPivot(final Pivot pivot) {
        this.pivot = pivot;
    }
    
    public boolean isUseBrackets() {
        return this.useBrackets;
    }
    
    public void setUseBrackets(final boolean useBrackets) {
        this.useBrackets = useBrackets;
    }
    
    public List<WithItem> getWithItemsList() {
        return this.withItemsList;
    }
    
    public void setWithItemsList(final List<WithItem> withItemsList) {
        this.withItemsList = withItemsList;
    }
    
    @Override
    public void accept(final ItemsListVisitor itemsListVisitor) {
        itemsListVisitor.visit(this);
    }
    
    @Override
    public String toString() {
        final StringBuilder retval = new StringBuilder();
        if (this.useBrackets) {
            retval.append("(");
        }
        if (this.withItemsList != null && !this.withItemsList.isEmpty()) {
            retval.append("WITH ");
            final Iterator<WithItem> iter = this.withItemsList.iterator();
            while (iter.hasNext()) {
                final WithItem withItem = iter.next();
                retval.append(withItem);
                if (iter.hasNext()) {
                    retval.append(",");
                }
                retval.append(" ");
            }
        }
        retval.append(this.selectBody);
        if (this.useBrackets) {
            retval.append(")");
        }
        if (this.pivot != null) {
            retval.append(" ").append(this.pivot);
        }
        if (this.alias != null) {
            retval.append(this.alias.toString());
        }
        return retval.toString();
    }
}
