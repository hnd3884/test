package org.htmlparser.tags;

public class TableColumn extends CompositeTag
{
    private static final String[] mIds;
    private static final String[] mEnders;
    private static final String[] mEndTagEnders;
    
    public String[] getIds() {
        return TableColumn.mIds;
    }
    
    public String[] getEnders() {
        return TableColumn.mEnders;
    }
    
    public String[] getEndTagEnders() {
        return TableColumn.mEndTagEnders;
    }
    
    static {
        mIds = new String[] { "TD" };
        mEnders = new String[] { "TD", "TR", "TBODY", "TFOOT", "THEAD" };
        mEndTagEnders = new String[] { "TR", "TBODY", "TFOOT", "THEAD", "TABLE" };
    }
}
