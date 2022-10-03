package net.sf.jsqlparser.statement.select;

public interface PivotVisitor
{
    void visit(final Pivot p0);
    
    void visit(final PivotXml p0);
}
