package org.apache.el.lang;

import javax.el.ValueExpression;
import org.apache.el.util.MessageFactory;
import javax.el.VariableMapper;

public class VariableMapperFactory extends VariableMapper
{
    private final VariableMapper target;
    private VariableMapper momento;
    
    public VariableMapperFactory(final VariableMapper target) {
        if (target == null) {
            throw new NullPointerException(MessageFactory.get("error.noVariableMapperTarget"));
        }
        this.target = target;
    }
    
    public VariableMapper create() {
        return this.momento;
    }
    
    public ValueExpression resolveVariable(final String variable) {
        final ValueExpression expr = this.target.resolveVariable(variable);
        if (expr != null) {
            if (this.momento == null) {
                this.momento = new VariableMapperImpl();
            }
            this.momento.setVariable(variable, expr);
        }
        return expr;
    }
    
    public ValueExpression setVariable(final String variable, final ValueExpression expression) {
        throw new UnsupportedOperationException(MessageFactory.get("error.cannotSetVariables"));
    }
}
