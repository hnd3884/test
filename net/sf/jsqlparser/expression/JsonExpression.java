package net.sf.jsqlparser.expression;

import java.util.ArrayList;
import java.util.List;
import net.sf.jsqlparser.schema.Column;

public class JsonExpression implements Expression
{
    private Column column;
    private List<String> idents;
    private List<String> operators;
    
    public JsonExpression() {
        this.idents = new ArrayList<String>();
        this.operators = new ArrayList<String>();
    }
    
    @Override
    public void accept(final ExpressionVisitor expressionVisitor) {
        expressionVisitor.visit(this);
    }
    
    public Column getColumn() {
        return this.column;
    }
    
    public void setColumn(final Column column) {
        this.column = column;
    }
    
    public void addIdent(final String ident, final String operator) {
        this.idents.add(ident);
        this.operators.add(operator);
    }
    
    @Override
    public String toString() {
        final StringBuilder b = new StringBuilder();
        b.append(this.column.toString());
        for (int i = 0; i < this.idents.size(); ++i) {
            b.append(this.operators.get(i)).append(this.idents.get(i));
        }
        return b.toString();
    }
}
