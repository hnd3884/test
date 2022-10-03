package net.sf.jsqlparser.statement.select;

public interface SelectVisitor
{
    void visit(final PlainSelect p0);
    
    void visit(final SetOperationList p0);
    
    void visit(final WithItem p0);
}
