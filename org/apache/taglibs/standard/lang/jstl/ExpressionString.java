package org.apache.taglibs.standard.lang.jstl;

import java.util.Map;

public class ExpressionString
{
    Object[] mElements;
    
    public Object[] getElements() {
        return this.mElements;
    }
    
    public void setElements(final Object[] pElements) {
        this.mElements = pElements;
    }
    
    public ExpressionString(final Object[] pElements) {
        this.mElements = pElements;
    }
    
    public String evaluate(final Object pContext, final VariableResolver pResolver, final Map functions, final String defaultPrefix, final Logger pLogger) throws ELException {
        final StringBuffer buf = new StringBuffer();
        for (int i = 0; i < this.mElements.length; ++i) {
            final Object elem = this.mElements[i];
            if (elem instanceof String) {
                buf.append((String)elem);
            }
            else if (elem instanceof Expression) {
                final Object val = ((Expression)elem).evaluate(pContext, pResolver, functions, defaultPrefix, pLogger);
                if (val != null) {
                    buf.append(val.toString());
                }
            }
        }
        return buf.toString();
    }
    
    public String getExpressionString() {
        final StringBuffer buf = new StringBuffer();
        for (int i = 0; i < this.mElements.length; ++i) {
            final Object elem = this.mElements[i];
            if (elem instanceof String) {
                buf.append((String)elem);
            }
            else if (elem instanceof Expression) {
                buf.append("${");
                buf.append(((Expression)elem).getExpressionString());
                buf.append("}");
            }
        }
        return buf.toString();
    }
}
