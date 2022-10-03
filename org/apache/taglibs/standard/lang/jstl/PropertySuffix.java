package org.apache.taglibs.standard.lang.jstl;

import java.util.Map;

public class PropertySuffix extends ArraySuffix
{
    String mName;
    
    public String getName() {
        return this.mName;
    }
    
    public void setName(final String pName) {
        this.mName = pName;
    }
    
    public PropertySuffix(final String pName) {
        super(null);
        this.mName = pName;
    }
    
    @Override
    Object evaluateIndex(final Object pContext, final VariableResolver pResolver, final Map functions, final String defaultPrefix, final Logger pLogger) throws ELException {
        return this.mName;
    }
    
    @Override
    String getOperatorSymbol() {
        return ".";
    }
    
    @Override
    public String getExpressionString() {
        return "." + StringLiteral.toIdentifierToken(this.mName);
    }
}
