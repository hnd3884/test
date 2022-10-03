package org.apache.taglibs.standard.lang.jstl;

import java.util.Map;
import java.util.List;

public class UnaryOperatorExpression extends Expression
{
    UnaryOperator mOperator;
    List mOperators;
    Expression mExpression;
    
    public UnaryOperator getOperator() {
        return this.mOperator;
    }
    
    public void setOperator(final UnaryOperator pOperator) {
        this.mOperator = pOperator;
    }
    
    public List getOperators() {
        return this.mOperators;
    }
    
    public void setOperators(final List pOperators) {
        this.mOperators = pOperators;
    }
    
    public Expression getExpression() {
        return this.mExpression;
    }
    
    public void setExpression(final Expression pExpression) {
        this.mExpression = pExpression;
    }
    
    public UnaryOperatorExpression(final UnaryOperator pOperator, final List pOperators, final Expression pExpression) {
        this.mOperator = pOperator;
        this.mOperators = pOperators;
        this.mExpression = pExpression;
    }
    
    @Override
    public String getExpressionString() {
        final StringBuffer buf = new StringBuffer();
        buf.append("(");
        if (this.mOperator != null) {
            buf.append(this.mOperator.getOperatorSymbol());
            buf.append(" ");
        }
        else {
            for (int i = 0; i < this.mOperators.size(); ++i) {
                final UnaryOperator operator = this.mOperators.get(i);
                buf.append(operator.getOperatorSymbol());
                buf.append(" ");
            }
        }
        buf.append(this.mExpression.getExpressionString());
        buf.append(")");
        return buf.toString();
    }
    
    @Override
    public Object evaluate(final Object pContext, final VariableResolver pResolver, final Map functions, final String defaultPrefix, final Logger pLogger) throws ELException {
        Object value = this.mExpression.evaluate(pContext, pResolver, functions, defaultPrefix, pLogger);
        if (this.mOperator != null) {
            value = this.mOperator.apply(value, pContext, pLogger);
        }
        else {
            for (int i = this.mOperators.size() - 1; i >= 0; --i) {
                final UnaryOperator operator = this.mOperators.get(i);
                value = operator.apply(value, pContext, pLogger);
            }
        }
        return value;
    }
}
