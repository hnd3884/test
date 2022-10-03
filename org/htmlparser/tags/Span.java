package org.htmlparser.tags;

public class Span extends CompositeTag
{
    private static final String[] mIds;
    
    public String[] getIds() {
        return Span.mIds;
    }
    
    static {
        mIds = new String[] { "SPAN" };
    }
}
