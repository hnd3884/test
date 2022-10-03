package org.htmlparser.filters;

import org.htmlparser.tags.LinkTag;
import org.htmlparser.Node;
import org.htmlparser.NodeFilter;

public class LinkStringFilter implements NodeFilter
{
    protected String mPattern;
    protected boolean mCaseSensitive;
    
    public LinkStringFilter(final String pattern) {
        this(pattern, false);
    }
    
    public LinkStringFilter(final String pattern, final boolean caseSensitive) {
        this.mPattern = pattern;
        this.mCaseSensitive = caseSensitive;
    }
    
    public boolean accept(final Node node) {
        boolean ret = false;
        if (LinkTag.class.isAssignableFrom(node.getClass())) {
            final String link = ((LinkTag)node).getLink();
            if (this.mCaseSensitive) {
                if (link.indexOf(this.mPattern) > -1) {
                    ret = true;
                }
            }
            else if (link.toUpperCase().indexOf(this.mPattern.toUpperCase()) > -1) {
                ret = true;
            }
        }
        return ret;
    }
}
