package org.htmlparser.tags;

public class TitleTag extends CompositeTag
{
    private static final String[] mIds;
    private static final String[] mEnders;
    private static final String[] mEndTagEnders;
    
    public String[] getIds() {
        return TitleTag.mIds;
    }
    
    public String[] getEnders() {
        return TitleTag.mEnders;
    }
    
    public String[] getEndTagEnders() {
        return TitleTag.mEndTagEnders;
    }
    
    public String getTitle() {
        return this.toPlainTextString();
    }
    
    public String toString() {
        return "TITLE: " + this.getTitle();
    }
    
    static {
        mIds = new String[] { "TITLE" };
        mEnders = new String[] { "TITLE", "BODY" };
        mEndTagEnders = new String[] { "HEAD", "HTML" };
    }
}
