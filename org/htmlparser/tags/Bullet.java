package org.htmlparser.tags;

public class Bullet extends CompositeTag
{
    private static final String[] mIds;
    private static final String[] mEndTagEnders;
    
    public String[] getIds() {
        return Bullet.mIds;
    }
    
    public String[] getEnders() {
        return Bullet.mIds;
    }
    
    public String[] getEndTagEnders() {
        return Bullet.mEndTagEnders;
    }
    
    static {
        mIds = new String[] { "LI" };
        mEndTagEnders = new String[] { "UL", "OL", "BODY", "HTML" };
    }
}
