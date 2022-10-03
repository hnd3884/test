package net.sf.jsqlparser.expression.operators.relational;

public abstract class ComparisonOperator extends OldOracleJoinBinaryExpression
{
    private final String operator;
    
    public ComparisonOperator(final String operator) {
        this.operator = operator;
    }
    
    @Override
    public String getStringExpression() {
        return this.operator;
    }
}
