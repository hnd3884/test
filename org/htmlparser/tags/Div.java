package org.htmlparser.tags;

public class Div extends CompositeTag
{
    private static final String[] mIds;
    private static final String[] mEndTagEnders;
    
    public String[] getIds() {
        return Div.mIds;
    }
    
    public String[] getEndTagEnders() {
        return Div.mEndTagEnders;
    }
    
    static {
        mIds = new String[] { "DIV" };
        mEndTagEnders = new String[] { "BODY", "HTML" };
    }
}
