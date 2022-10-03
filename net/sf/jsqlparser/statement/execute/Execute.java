package net.sf.jsqlparser.statement.execute;

import java.util.List;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.StatementVisitor;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.statement.Statement;

public class Execute implements Statement
{
    private String name;
    private ExpressionList exprList;
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public ExpressionList getExprList() {
        return this.exprList;
    }
    
    public void setExprList(final ExpressionList exprList) {
        this.exprList = exprList;
    }
    
    @Override
    public void accept(final StatementVisitor statementVisitor) {
        statementVisitor.visit(this);
    }
    
    @Override
    public String toString() {
        return "EXECUTE " + this.name + " " + PlainSelect.getStringList(this.exprList.getExpressions(), true, false);
    }
}
