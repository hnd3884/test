package net.sf.jsqlparser.expression.operators.relational;

import net.sf.jsqlparser.expression.ExpressionVisitor;
import net.sf.jsqlparser.expression.BinaryExpression;

public class RegExpMatchOperator extends BinaryExpression
{
    private RegExpMatchOperatorType operatorType;
    
    public RegExpMatchOperator(final RegExpMatchOperatorType operatorType) {
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
                return "~";
            }
            case MATCH_CASEINSENSITIVE: {
                return "~*";
            }
            case NOT_MATCH_CASESENSITIVE: {
                return "!~";
            }
            case NOT_MATCH_CASEINSENSITIVE: {
                return "!~*";
            }
            default: {
                return null;
            }
        }
    }
}
