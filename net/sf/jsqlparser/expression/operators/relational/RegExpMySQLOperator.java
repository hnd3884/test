package net.sf.jsqlparser.expression.operators.relational;

import net.sf.jsqlparser.expression.ExpressionVisitor;
import net.sf.jsqlparser.expression.BinaryExpression;

public class RegExpMySQLOperator extends BinaryExpression
{
    private RegExpMatchOperatorType operatorType;
    
    public RegExpMySQLOperator(final RegExpMatchOperatorType operatorType) {
        if (operatorType == null) {
            throw new NullPointerException();
        }
        this.operatorType = operatorType;
    }
    
    public RegExpMatchOperatorType getOperatorType() {
        return this.operatorType;
    }
    
    @Override
    public void accept(final ExpressionVisitor expressionVisitor) {
        expressionVisitor.visit(this);
    }
    
    @Override
    public String getStringExpression() {
        switch (this.operatorType) {
            case MATCH_CASESENSITIVE: {
                return "REGEXP BINARY";
            }
            case MATCH_CASEINSENSITIVE: {
                return "REGEXP";
            }
            default: {
                return null;
            }
        }
    }
}
