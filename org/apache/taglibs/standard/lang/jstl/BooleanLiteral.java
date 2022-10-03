package org.apache.taglibs.standard.lang.jstl;

public class BooleanLiteral extends Literal
{
    public static final BooleanLiteral TRUE;
    public static final BooleanLiteral FALSE;
    
    public BooleanLiteral(final String pToken) {
        super(getValueFromToken(pToken));
    }
    
    static Object getValueFromToken(final String pToken) {
        return "true".equals(pToken) ? Boolean.TRUE : Boolean.FALSE;
    }
    
    @Override
    public String getExpressionString() {
        return (this.getValue() == Boolean.TRUE) ? "true" : "false";
    }
    
    static {
        TRUE = new BooleanLiteral("true");
        FALSE = new BooleanLiteral("false");
    }
}
