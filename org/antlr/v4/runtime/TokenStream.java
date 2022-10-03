package org.antlr.v4.runtime;

import org.antlr.v4.runtime.misc.Interval;

public interface TokenStream extends IntStream
{
    Token LT(final int p0);
    
    Token get(final int p0);
    
    TokenSource getTokenSource();
    
    String getText(final Interval p0);
    
    String getText();
    
    String getText(final RuleContext p0);
    
    String getText(final Token p0, final Token p1);
}
