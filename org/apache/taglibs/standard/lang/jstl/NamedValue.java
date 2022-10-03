package org.apache.taglibs.standard.lang.jstl;

import java.util.Map;

public class NamedValue extends Expression
{
    String mName;
    
    public String getName() {
        return this.mName;
    }
    
    public NamedValue(final String pName) {
        this.mName = pName;
    }
    
    @Override
    public String getExpressionString() {
        return StringLiteral.toIdentifierToken(this.mName);
    }
    
    @Override
    public Object evaluate(final Object pContext, final VariableResolver pResolver, final Map functions, final String defaultPrefix, final Logger pLogger) throws ELException {
        if (pResolver == null) {
            return null;
        }
        return pResolver.resolveVariable(this.mName, pContext);
    }
}
