package org.htmlparser.tags;

public class DefinitionListBullet extends CompositeTag
{
    private static final String[] mIds;
    private static final String[] mEndTagEnders;
    
    public String[] getIds() {
        return DefinitionListBullet.mIds;
    }
    
    public String[] getEnders() {
        return DefinitionListBullet.mIds;
    }
    
    public String[] getEndTagEnders() {
        return DefinitionListBullet.mEndTagEnders;
    }
    
    static {
        mIds = new String[] { "DD", "DT" };
        mEndTagEnders = new String[] { "DL", "BODY", "HTML" };
    }
}
