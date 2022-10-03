package org.apache.taglibs.standard.lang.jstl;

public class NullLiteral extends Literal
{
    public static final NullLiteral SINGLETON;
    
    public NullLiteral() {
        super(null);
    }
    
    @Override
    public String getExpressionString() {
        return "null";
    }
    
    static {
        SINGLETON = new NullLiteral();
    }
}
