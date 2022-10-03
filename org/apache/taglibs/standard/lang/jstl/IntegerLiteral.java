package org.apache.taglibs.standard.lang.jstl;

public class IntegerLiteral extends Literal
{
    public IntegerLiteral(final String pToken) {
        super(getValueFromToken(pToken));
    }
    
    static Object getValueFromToken(final String pToken) {
        return new Long(pToken);
    }
    
    @Override
    public String getExpressionString() {
        return this.getValue().toString();
    }
}
