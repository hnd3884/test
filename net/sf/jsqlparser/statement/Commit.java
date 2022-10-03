package net.sf.jsqlparser.statement;

public class Commit implements Statement
{
    @Override
    public void accept(final StatementVisitor statementVisitor) {
        statementVisitor.visit(this);
    }
    
    @Override
    public String toString() {
        return "COMMIT";
    }
}
