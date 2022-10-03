package net.sf.jsqlparser.statement.truncate;

import net.sf.jsqlparser.statement.StatementVisitor;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;

public class Truncate implements Statement
{
    private Table table;
    
    @Override
    public void accept(final StatementVisitor statementVisitor) {
        statementVisitor.visit(this);
    }
    
    public Table getTable() {
        return this.table;
    }
    
    public void setTable(final Table table) {
        this.table = table;
    }
    
    @Override
    public String toString() {
        return "TRUNCATE TABLE " + this.table;
    }
}
