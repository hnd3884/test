package com.btr.proxy.selector.pac;

public interface PacScriptParser
{
    PacScriptSource getScriptSource();
    
    String evaluate(final String p0, final String p1) throws ProxyEvaluationException;
}
