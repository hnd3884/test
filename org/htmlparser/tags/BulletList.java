package org.htmlparser.tags;

public class BulletList extends CompositeTag
{
    private static final String[] mIds;
    private static final String[] mEndTagEnders;
    
    public String[] getIds() {
        return BulletList.mIds;
    }
    
    public String[] getEndTagEnders() {
        return BulletList.mEndTagEnders;
    }
    
    static {
        mIds = new String[] { "UL", "OL" };
        mEndTagEnders = new String[] { "BODY", "HTML" };
    }
}
