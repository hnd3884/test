package net.sf.jsqlparser.statement.alter;

import java.util.Iterator;
import net.sf.jsqlparser.statement.StatementVisitor;
import java.util.ArrayList;
import java.util.List;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;

public class Alter implements Statement
{
    private Table table;
    private List<AlterExpression> alterExpressions;
    
    public Table getTable() {
        return this.table;
    }
    
    public void setTable(final Table table) {
        this.table = table;
    }
    
    public void addAlterExpression(final AlterExpression alterExpression) {
        if (this.alterExpressions == null) {
            this.alterExpressions = new ArrayList<AlterExpression>();
        }
        this.alterExpressions.add(alterExpression);
    }
    
    public List<AlterExpression> getAlterExpressions() {
        return this.alterExpressions;
    }
    
    public void setAlterExpressions(final List<AlterExpression> alterExpressions) {
        this.alterExpressions = alterExpressions;
    }
    
    @Override
    public void accept(final StatementVisitor statementVisitor) {
        statementVisitor.visit(this);
    }
    
    @Override
    public String toString() {
        final StringBuilder b = new StringBuilder();
        b.append("ALTER TABLE ").append(this.table.getFullyQualifiedName()).append(" ");
        final Iterator<AlterExpression> altIter = this.alterExpressions.iterator();
        while (altIter.hasNext()) {
            b.append(altIter.next().toString());
            if (altIter.hasNext()) {
                b.append(", ");
            }
        }
        return b.toString();
    }
}
