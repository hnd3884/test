package net.sf.jsqlparser.statement.select;

public interface SelectItemVisitor
{
    void visit(final AllColumns p0);
    
    void visit(final AllTableColumns p0);
    
    void visit(final SelectExpressionItem p0);
}
