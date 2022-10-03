package sun.text.normalizer;

import java.text.ParsePosition;

@Deprecated
public interface SymbolTable
{
    @Deprecated
    public static final char SYMBOL_REF = '$';
    
    @Deprecated
    char[] lookup(final String p0);
    
    @Deprecated
    UnicodeMatcher lookupMatcher(final int p0);
    
    @Deprecated
    String parseReference(final String p0, final ParsePosition p1, final int p2);
}
