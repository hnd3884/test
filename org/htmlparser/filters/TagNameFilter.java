package org.htmlparser.filters;

import org.htmlparser.Tag;
import org.htmlparser.Node;
import java.util.Locale;
import org.htmlparser.NodeFilter;

public class TagNameFilter implements NodeFilter
{
    protected String mName;
    
    public TagNameFilter() {
        this("");
    }
    
    public TagNameFilter(final String name) {
        this.mName = name.toUpperCase(Locale.ENGLISH);
    }
    
    public String getName() {
        return this.mName;
    }
    
    public void setName(final String name) {
        this.mName = name;
    }
    
    public boolean accept(final Node node) {
        return node instanceof Tag && !((Tag)node).isEndTag() && ((Tag)node).getTagName().equals(this.mName);
    }
}
