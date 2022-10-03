package org.htmlparser.tags;

import org.htmlparser.nodes.TagNode;

public class DoctypeTag extends TagNode
{
    private static final String[] mIds;
    
    public String[] getIds() {
        return DoctypeTag.mIds;
    }
    
    public String toString() {
        String guts = this.toHtml();
        guts = guts.substring(1, guts.length() - 2);
        return "Doctype Tag : " + guts + "; begins at : " + this.getStartPosition() + "; ends at : " + this.getEndPosition();
    }
    
    static {
        mIds = new String[] { "!DOCTYPE" };
    }
}
