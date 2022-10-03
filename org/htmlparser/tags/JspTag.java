package org.htmlparser.tags;

import org.htmlparser.nodes.TagNode;

public class JspTag extends TagNode
{
    private static final String[] mIds;
    
    public String[] getIds() {
        return JspTag.mIds;
    }
    
    public String toString() {
        String guts = this.toHtml();
        guts = guts.substring(1, guts.length() - 2);
        return "JSP/ASP Tag : " + guts + "; begins at : " + this.getStartPosition() + "; ends at : " + this.getEndPosition();
    }
    
    static {
        mIds = new String[] { "%", "%=", "%@" };
    }
}
