package net.sf.jsqlparser.statement.select;

import net.sf.jsqlparser.expression.Alias;

public class SubJoin implements FromItem
{
    private FromItem left;
    private Join join;
    private Alias alias;
    private Pivot pivot;
    
    @Override
    public void accept(final FromItemVisitor fromItemVisitor) {
        fromItemVisitor.visit(this);
    }
    
    public FromItem getLeft() {
        return this.left;
    }
    
    public void setLeft(final FromItem l) {
        this.left = l;
    }
    
    public Join getJoin() {
        return this.join;
    }
    
    public void setJoin(final Join j) {
        this.join = j;
    }
    
    @Override
    public Pivot getPivot() {
        return this.pivot;
    }
    
    @Override
    public void setPivot(final Pivot pivot) {
        this.pivot = pivot;
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
    public String toString() {
        return "(" + this.left + " " + this.join + ")" + ((this.pivot != null) ? (" " + this.pivot) : "") + ((this.alias != null) ? this.alias.toString() : "");
    }
}
