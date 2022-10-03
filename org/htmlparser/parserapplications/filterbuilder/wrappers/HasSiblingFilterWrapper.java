package org.htmlparser.parserapplications.filterbuilder.wrappers;

import java.awt.event.ActionEvent;
import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.NodeFilter;
import java.awt.Component;
import org.htmlparser.parserapplications.filterbuilder.SubFilterList;
import org.htmlparser.filters.HasSiblingFilter;
import java.awt.event.ActionListener;
import org.htmlparser.parserapplications.filterbuilder.Filter;

public class HasSiblingFilterWrapper extends Filter implements ActionListener
{
    protected HasSiblingFilter mFilter;
    protected SubFilterList mContainer;
    
    public HasSiblingFilterWrapper() {
        this.mFilter = new HasSiblingFilter();
        this.add(this.mContainer = new SubFilterList(this, "Sibling Filter", 1));
    }
    
    public String getDescription() {
        return "Has Sibling";
    }
    
    public String getIconSpec() {
        return "images/HasSiblingFilter.gif";
    }
    
    public NodeFilter getNodeFilter() {
        final HasSiblingFilter ret = new HasSiblingFilter();
        final NodeFilter filter = this.mFilter.getSiblingFilter();
        if (null != filter) {
            ret.setSiblingFilter(((Filter)filter).getNodeFilter());
        }
        return ret;
    }
    
    public void setNodeFilter(final NodeFilter filter, final Parser context) {
        this.mFilter = (HasSiblingFilter)filter;
    }
    
    public NodeFilter[] getSubNodeFilters() {
        final NodeFilter filter = this.mFilter.getSiblingFilter();
        NodeFilter[] ret;
        if (null != filter) {
            ret = new NodeFilter[] { filter };
        }
        else {
            ret = new NodeFilter[0];
        }
        return ret;
    }
    
    public void setSubNodeFilters(final NodeFilter[] filters) {
        if (0 != filters.length) {
            this.mFilter.setSiblingFilter(filters[0]);
        }
        else {
            this.mFilter.setSiblingFilter(null);
        }
    }
    
    public String toJavaCode(final StringBuffer out, final int[] context) {
        String name;
        if (null != this.mFilter.getSiblingFilter()) {
            name = ((Filter)this.mFilter.getSiblingFilter()).toJavaCode(out, context);
        }
        else {
            name = null;
        }
        final String ret = "filter" + context[1]++;
        Filter.spaces(out, context[0]);
        out.append("HasSiblingFilter ");
        out.append(ret);
        out.append(" = new HasSiblingFilter ();");
        Filter.newline(out);
        if (null != name) {
            Filter.spaces(out, context[0]);
            out.append(ret);
            out.append(".setSiblingFilter (");
            out.append(name);
            out.append(");");
            Filter.newline(out);
        }
        return ret;
    }
    
    public boolean accept(final Node node) {
        return this.mFilter.accept(node);
    }
    
    public void actionPerformed(final ActionEvent event) {
    }
}
