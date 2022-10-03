package org.antlr.v4.runtime;

import org.antlr.v4.runtime.misc.Pair;

public interface TokenFactory<Symbol extends Token>
{
    Symbol create(final Pair<TokenSource, CharStream> p0, final int p1, final String p2, final int p3, final int p4, final int p5, final int p6, final int p7);
    
    Symbol create(final int p0, final String p1);
}
