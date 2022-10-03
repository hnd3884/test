package org.htmlparser.tags;

public class TextareaTag extends CompositeTag
{
    private static final String[] mIds;
    private static final String[] mEnders;
    private static final String[] mEndTagEnders;
    
    public String[] getIds() {
        return TextareaTag.mIds;
    }
    
    public String[] getEnders() {
        return TextareaTag.mEnders;
    }
    
    public String[] getEndTagEnders() {
        return TextareaTag.mEndTagEnders;
    }
    
    public String getValue() {
        return this.toPlainTextString();
    }
    
    static {
        mIds = new String[] { "TEXTAREA" };
        mEnders = new String[] { "INPUT", "TEXTAREA", "SELECT", "OPTION" };
        mEndTagEnders = new String[] { "FORM", "BODY", "HTML" };
    }
}
