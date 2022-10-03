package net.sf.jsqlparser.statement.select;

public class TableFunction extends FunctionItem implements FromItem
{
    @Override
    public void accept(final FromItemVisitor fromItemVisitor) {
        fromItemVisitor.visit(this);
    }
    
    @Override
    public Pivot getPivot() {
        return null;
    }
    
    @Override
    public void setPivot(final Pivot pivot) {
    }
}
