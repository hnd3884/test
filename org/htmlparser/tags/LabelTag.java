package org.htmlparser.tags;

public class LabelTag extends CompositeTag
{
    private static final String[] mIds;
    
    public String[] getIds() {
        return LabelTag.mIds;
    }
    
    public String[] getEnders() {
        return LabelTag.mIds;
    }
    
    public String getLabel() {
        return this.toPlainTextString();
    }
    
    public String toString() {
        return "LABEL: " + this.getLabel();
    }
    
    static {
        mIds = new String[] { "LABEL" };
    }
}
