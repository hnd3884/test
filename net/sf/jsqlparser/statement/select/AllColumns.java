package net.sf.jsqlparser.statement.select;

public class AllColumns implements SelectItem
{
    @Override
    public void accept(final SelectItemVisitor selectItemVisitor) {
        selectItemVisitor.visit(this);
    }
    
    @Override
    public String toString() {
        return "*";
    }
}
