package org.htmlparser.tags;

public class HeadTag extends CompositeTag
{
    private static final String[] mIds;
    private static final String[] mEnders;
    private static final String[] mEndTagEnders;
    
    public String[] getIds() {
        return HeadTag.mIds;
    }
    
    public String[] getEnders() {
        return HeadTag.mEnders;
    }
    
    public String[] getEndTagEnders() {
        return HeadTag.mEndTagEnders;
    }
    
    public String toString() {
        return "HEAD: " + super.toString();
    }
    
    static {
        mIds = new String[] { "HEAD" };
        mEnders = new String[] { "HEAD", "BODY" };
        mEndTagEnders = new String[] { "HTML" };
    }
}
