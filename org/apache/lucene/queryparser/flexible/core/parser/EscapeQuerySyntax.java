package org.apache.lucene.queryparser.flexible.core.parser;

import java.util.Locale;

public interface EscapeQuerySyntax
{
    CharSequence escape(final CharSequence p0, final Locale p1, final Type p2);
    
    public enum Type
    {
        STRING, 
        NORMAL;
    }
}
