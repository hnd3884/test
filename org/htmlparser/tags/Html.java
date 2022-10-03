package org.htmlparser.tags;

public class Html extends CompositeTag
{
    private static final String[] mIds;
    
    public String[] getIds() {
        return Html.mIds;
    }
    
    static {
        mIds = new String[] { "HTML" };
    }
}
