package org.htmlparser.tags;

public class HeadingTag extends CompositeTag
{
    private static final String[] mIds;
    private static final String[] mEnders;
    private static final String[] mEndTagEnders;
    
    public String[] getIds() {
        return HeadingTag.mIds;
    }
    
    public String[] getEnders() {
        return HeadingTag.mEnders;
    }
    
    public String[] getEndTagEnders() {
        return HeadingTag.mEndTagEnders;
    }
    
    static {
        mIds = new String[] { "H1", "H2", "H3", "H4", "H5", "H6" };
        mEnders = new String[] { "H1", "H2", "H3", "H4", "H5", "H6", "PARAM" };
        mEndTagEnders = new String[] { "BODY", "HTML" };
    }
}
