package org.apache.taglibs.standard.lang.jstl;

import java.util.Map;

public abstract class Expression
{
    public abstract String getExpressionString();
    
    public abstract Object evaluate(final Object p0, final VariableResolver p1, final Map p2, final String p3, final Logger p4) throws ELException;
}
