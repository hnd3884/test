package net.sf.jsqlparser.statement;

import java.util.Iterator;
import java.util.List;

public class Statements
{
    private List<Statement> statements;
    
    public List<Statement> getStatements() {
        return this.statements;
    }
    
    public void setStatements(final List<Statement> statements) {
        this.statements = statements;
    }
    
    public void accept(final StatementVisitor statementVisitor) {
        statementVisitor.visit(this);
    }
    
    @Override
    public String toString() {
        final StringBuilder b = new StringBuilder();
        for (final Statement stmt : this.statements) {
            b.append(stmt.toString()).append(";\n");
        }
        return b.toString();
    }
}
