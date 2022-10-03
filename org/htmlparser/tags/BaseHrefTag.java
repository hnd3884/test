package org.htmlparser.tags;

import org.htmlparser.util.ParserException;
import org.htmlparser.lexer.Page;
import org.htmlparser.nodes.TagNode;

public class BaseHrefTag extends TagNode
{
    private static final String[] mIds;
    
    public String[] getIds() {
        return BaseHrefTag.mIds;
    }
    
    public String getBaseUrl() {
        String base = this.getAttribute("HREF");
        if (base != null && base.length() > 0) {
            base = base.trim();
        }
        base = ((null == base) ? "" : base);
        return base;
    }
    
    public void setBaseUrl(final String base) {
        this.setAttribute("HREF", base);
    }
    
    public void doSemanticAction() throws ParserException {
        final Page page = this.getPage();
        if (null != page) {
            page.setBaseUrl(this.getBaseUrl());
        }
    }
    
    static {
        mIds = new String[] { "BASE" };
    }
}
