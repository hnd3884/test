package net.sf.jsqlparser.statement.select;

import net.sf.jsqlparser.schema.Table;

public interface FromItemVisitor
{
    void visit(final Table p0);
    
    void visit(final SubSelect p0);
    
    void visit(final SubJoin p0);
    
    void visit(final LateralSubSelect p0);
    
    void visit(final ValuesList p0);
    
    void visit(final TableFunction p0);
}
