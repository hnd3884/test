package org.htmlparser.filters;

import java.util.regex.Matcher;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.Node;
import java.util.regex.Pattern;
import org.htmlparser.NodeFilter;

public class LinkRegexFilter implements NodeFilter
{
    protected Pattern mRegex;
    
    public LinkRegexFilter(final String regexPattern) {
        this(regexPattern, true);
    }
    
    public LinkRegexFilter(final String regexPattern, final boolean caseSensitive) {
        if (caseSensitive) {
            this.mRegex = Pattern.compile(regexPattern);
        }
        else {
            this.mRegex = Pattern.compile(regexPattern, 66);
        }
    }
    
    public boolean accept(final Node node) {
        boolean ret = false;
        if (LinkTag.class.isAssignableFrom(node.getClass())) {
            final String link = ((LinkTag)node).getLink();
            final Matcher matcher = this.mRegex.matcher(link);
            ret = matcher.find();
        }
        return ret;
    }
}
