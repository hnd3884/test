package org.apache.taglibs.standard.lang.jstl;

public class FloatingPointLiteral extends Literal
{
    public FloatingPointLiteral(final String pToken) {
        super(getValueFromToken(pToken));
    }
    
    static Object getValueFromToken(final String pToken) {
        return new Double(pToken);
    }
    
    @Override
    public String getExpressionString() {
        return this.getValue().toString();
    }
}
