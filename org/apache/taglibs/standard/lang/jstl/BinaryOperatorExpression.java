package org.apache.taglibs.standard.lang.jstl;

import java.util.Map;
import java.util.List;

public class BinaryOperatorExpression extends Expression
{
    Expression mExpression;
    List mOperators;
    List mExpressions;
    
    public Expression getExpression() {
        return this.mExpression;
    }
    
    public void setExpression(final Expression pExpression) {
        this.mExpression = pExpression;
    }
    
    public List getOperators() {
        return this.mOperators;
    }
    
    public void setOperators(final List pOperators) {
        this.mOperators = pOperators;
    }
    
    public List getExpressions() {
        return this.mExpressions;
    }
    
    public void setExpressions(final List pExpressions) {
        this.mExpressions = pExpressions;
    }
    
    public BinaryOperatorExpression(final Expression pExpression, final List pOperators, final List pExpressions) {
        this.mExpression = pExpression;
        this.mOperators = pOperators;
        this.mExpressions = pExpressions;
    }
    
    @Override
    public String getExpressionString() {
        final StringBuffer buf = new StringBuffer();
        buf.append("(");
        buf.append(this.mExpression.getExpressionString());
        for (int i = 0; i < this.mOperators.size(); ++i) {
            final BinaryOperator operator = this.mOperators.get(i);
            final Expression expression = this.mExpressions.get(i);
            buf.append(" ");
            buf.append(operator.getOperatorSymbol());
            buf.append(" ");
            buf.append(expression.getExpressionString());
        }
        buf.append(")");
        return buf.toString();
    }
    
    @Override
    public Object evaluate(final Object pContext, final VariableResolver pResolver, final Map functions, final String defaultPrefix, final Logger pLogger) throws ELException {
        Object value = this.mExpression.evaluate(pContext, pResolver, functions, defaultPrefix, pLogger);
        for (int i = 0; i < this.mOperators.size(); ++i) {
            final BinaryOperator operator = this.mOperators.get(i);
            if (operator.shouldCoerceToBoolean()) {
                value = Coercions.coerceToBoolean(value, pLogger);
            }
            if (operator.shouldEvaluate(value)) {
                final Expression expression = this.mExpressions.get(i);
                final Object nextValue = expression.evaluate(pContext, pResolver, functions, defaultPrefix, pLogger);
                value = operator.apply(value, nextValue, pContext, pLogger);
            }
        }
        return value;
    }
}
