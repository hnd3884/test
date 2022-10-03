package org.htmlparser.tags;

public class ParagraphTag extends CompositeTag
{
    private static final String[] mIds;
    private static final String[] mEnders;
    private static final String[] mEndTagEnders;
    
    public String[] getIds() {
        return ParagraphTag.mIds;
    }
    
    public String[] getEnders() {
        return ParagraphTag.mEnders;
    }
    
    public String[] getEndTagEnders() {
        return ParagraphTag.mEndTagEnders;
    }
    
    static {
        mIds = new String[] { "P" };
        mEnders = new String[] { "ADDRESS", "BLOCKQUOTE", "CENTER", "DD", "DIR", "DIV", "DL", "DT", "FIELDSET", "FORM", "H1", "H2", "H3", "H4", "H5", "H6", "HR", "ISINDEX", "LI", "MENU", "NOFRAMES", "OL", "P", "PARAM", "PRE", "UL" };
        mEndTagEnders = new String[] { "BODY", "HTML" };
    }
}
