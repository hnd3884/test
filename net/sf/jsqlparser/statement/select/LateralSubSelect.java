package net.sf.jsqlparser.statement.select;

import net.sf.jsqlparser.expression.Alias;

public class LateralSubSelect implements FromItem
{
    private SubSelect subSelect;
    private Alias alias;
    private Pivot pivot;
    
    public void setSubSelect(final SubSelect subSelect) {
        this.subSelect = subSelect;
    }
    
    public SubSelect getSubSelect() {
        return this.subSelect;
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
        return this.pivot;
    }
    
    @Override
    public void setPivot(final Pivot pivot) {
        this.pivot = pivot;
    }
    
    @Override
    public String toString() {
        return "LATERAL" + this.subSelect.toString() + ((this.pivot != null) ? (" " + this.pivot) : "") + ((this.alias != null) ? this.alias.toString() : "");
    }
}
