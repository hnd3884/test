package org.htmlparser.tags;

public class DefinitionList extends CompositeTag
{
    private static final String[] mIds;
    private static final String[] mEndTagEnders;
    
    public String[] getIds() {
        return DefinitionList.mIds;
    }
    
    public String[] getEndTagEnders() {
        return DefinitionList.mEndTagEnders;
    }
    
    static {
        mIds = new String[] { "DL" };
        mEndTagEnders = new String[] { "BODY", "HTML" };
    }
}
