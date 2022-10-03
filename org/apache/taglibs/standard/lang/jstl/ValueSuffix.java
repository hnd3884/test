package org.apache.taglibs.standard.lang.jstl;

import java.util.Map;

public abstract class ValueSuffix
{
    public abstract String getExpressionString();
    
    public abstract Object evaluate(final Object p0, final Object p1, final VariableResolver p2, final Map p3, final String p4, final Logger p5) throws ELException;
}
