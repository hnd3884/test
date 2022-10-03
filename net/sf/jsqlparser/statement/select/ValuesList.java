package net.sf.jsqlparser.statement.select;

import java.util.Iterator;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import java.util.List;
import net.sf.jsqlparser.expression.operators.relational.MultiExpressionList;
import net.sf.jsqlparser.expression.Alias;

public class ValuesList implements FromItem
{
    private Alias alias;
    private MultiExpressionList multiExpressionList;
    private boolean noBrackets;
    private List<String> columnNames;
    
    public ValuesList() {
        this.noBrackets = false;
    }
    
    public ValuesList(final MultiExpressionList multiExpressionList) {
        this.noBrackets = false;
        this.multiExpressionList = multiExpressionList;
    }
    
    @Override
    public void accept(final FromItemVisitor fromItemVisitor) {
        fromItemVisitor.visit(this);
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
        return null;
    }
    
    @Override
    public void setPivot(final Pivot pivot) {
    }
    
    public MultiExpressionList getMultiExpressionList() {
        return this.multiExpressionList;
    }
    
    public void setMultiExpressionList(final MultiExpressionList multiExpressionList) {
        this.multiExpressionList = multiExpressionList;
    }
    
    public boolean isNoBrackets() {
        return this.noBrackets;
    }
    
    public void setNoBrackets(final boolean noBrackets) {
        this.noBrackets = noBrackets;
    }
    
    @Override
    public String toString() {
        final StringBuilder b = new StringBuilder();
        b.append("(VALUES ");
        final Iterator<ExpressionList> it = this.getMultiExpressionList().getExprList().iterator();
        while (it.hasNext()) {
            b.append(PlainSelect.getStringList(it.next().getExpressions(), true, !this.isNoBrackets()));
            if (it.hasNext()) {
                b.append(", ");
            }
        }
        b.append(")");
        if (this.alias != null) {
            b.append(this.alias.toString());
            if (this.columnNames != null) {
                b.append("(");
                final Iterator<String> it2 = this.columnNames.iterator();
                while (it2.hasNext()) {
                    b.append(it2.next());
                    if (it2.hasNext()) {
                        b.append(", ");
                    }
                }
                b.append(")");
            }
        }
        return b.toString();
    }
    
    public List<String> getColumnNames() {
        return this.columnNames;
    }
    
    public void setColumnNames(final List<String> columnNames) {
        this.columnNames = columnNames;
    }
}
