package org.apache.taglibs.standard.lang.jstl;

import java.util.Map;
import java.util.List;

public class ComplexValue extends Expression
{
    Expression mPrefix;
    List mSuffixes;
    
    public Expression getPrefix() {
        return this.mPrefix;
    }
    
    public void setPrefix(final Expression pPrefix) {
        this.mPrefix = pPrefix;
    }
    
    public List getSuffixes() {
        return this.mSuffixes;
    }
    
    public void setSuffixes(final List pSuffixes) {
        this.mSuffixes = pSuffixes;
    }
    
    public ComplexValue(final Expression pPrefix, final List pSuffixes) {
        this.mPrefix = pPrefix;
        this.mSuffixes = pSuffixes;
    }
    
    @Override
    public String getExpressionString() {
        final StringBuffer buf = new StringBuffer();
        buf.append(this.mPrefix.getExpressionString());
        for (int i = 0; this.mSuffixes != null && i < this.mSuffixes.size(); ++i) {
            final ValueSuffix suffix = this.mSuffixes.get(i);
            buf.append(suffix.getExpressionString());
        }
        return buf.toString();
    }
    
    @Override
    public Object evaluate(final Object pContext, final VariableResolver pResolver, final Map functions, final String defaultPrefix, final Logger pLogger) throws ELException {
        Object ret = this.mPrefix.evaluate(pContext, pResolver, functions, defaultPrefix, pLogger);
        for (int i = 0; this.mSuffixes != null && i < this.mSuffixes.size(); ++i) {
            final ValueSuffix suffix = this.mSuffixes.get(i);
            ret = suffix.evaluate(ret, pContext, pResolver, functions, defaultPrefix, pLogger);
        }
        return ret;
    }
}
