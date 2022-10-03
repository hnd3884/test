package org.htmlparser.tags;

public class BodyTag extends CompositeTag
{
    private static final String[] mIds;
    private static final String[] mEndTagEnders;
    
    public String[] getIds() {
        return BodyTag.mIds;
    }
    
    public String[] getEnders() {
        return BodyTag.mIds;
    }
    
    public String[] getEndTagEnders() {
        return BodyTag.mEndTagEnders;
    }
    
    public String getBody() {
        return this.toPlainTextString();
    }
    
    static {
        mIds = new String[] { "BODY" };
        mEndTagEnders = new String[] { "HTML" };
    }
}
