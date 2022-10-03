package org.htmlparser.tags;

public class TableHeader extends CompositeTag
{
    private static final String[] mIds;
    private static final String[] mEnders;
    private static final String[] mEndTagEnders;
    
    public String[] getIds() {
        return TableHeader.mIds;
    }
    
    public String[] getEnders() {
        return TableHeader.mEnders;
    }
    
    public String[] getEndTagEnders() {
        return TableHeader.mEndTagEnders;
    }
    
    static {
        mIds = new String[] { "TH" };
        mEnders = new String[] { "TH", "TR", "TBODY", "TFOOT", "THEAD" };
        mEndTagEnders = new String[] { "TR", "TBODY", "TFOOT", "THEAD", "TABLE" };
    }
}
