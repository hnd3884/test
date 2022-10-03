package org.apache.taglibs.standard.lang.jstl;

import java.util.Map;

public abstract class Literal extends Expression
{
    Object mValue;
    
    public Object getValue() {
        return this.mValue;
    }
    
    public void setValue(final Object pValue) {
        this.mValue = pValue;
    }
    
    public Literal(final Object pValue) {
        this.mValue = pValue;
    }
    
    @Override
    public Object evaluate(final Object pContext, final VariableResolver pResolver, final Map functions, final String defaultPrefix, final Logger pLogger) throws ELException {
        return this.mValue;
    }
}
