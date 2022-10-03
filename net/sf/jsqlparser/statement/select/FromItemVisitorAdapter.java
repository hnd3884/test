package net.sf.jsqlparser.statement.select;

import net.sf.jsqlparser.schema.Table;

public class FromItemVisitorAdapter implements FromItemVisitor
{
    @Override
    public void visit(final Table table) {
    }
    
    @Override
    public void visit(final SubSelect subSelect) {
    }
    
    @Override
    public void visit(final SubJoin subjoin) {
    }
    
    @Override
    public void visit(final LateralSubSelect lateralSubSelect) {
    }
    
    @Override
    public void visit(final ValuesList valuesList) {
    }
    
    @Override
    public void visit(final TableFunction valuesList) {
    }
}
