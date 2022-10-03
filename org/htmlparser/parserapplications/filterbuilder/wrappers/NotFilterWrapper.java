package org.htmlparser.parserapplications.filterbuilder.wrappers;

import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.NodeFilter;
import java.awt.Component;
import org.htmlparser.filters.NotFilter;
import org.htmlparser.parserapplications.filterbuilder.SubFilterList;
import org.htmlparser.parserapplications.filterbuilder.Filter;

public class NotFilterWrapper extends Filter
{
    protected SubFilterList mContainer;
    protected NotFilter mFilter;
    
    public NotFilterWrapper() {
        this.mFilter = new NotFilter();
        this.add(this.mContainer = new SubFilterList(this, "Predicate", 1));
    }
    
    public String getDescription() {
        return "Not";
    }
    
    public String getIconSpec() {
        return "images/NotFilter.gif";
    }
    
    public NodeFilter getNodeFilter() {
        final NotFilter ret = new NotFilter();
        final NodeFilter predicate = this.mFilter.getPredicate();
        if (null != predicate) {
            ret.setPredicate(((Filter)predicate).getNodeFilter());
        }
        return ret;
    }
    
    public void setNodeFilter(final NodeFilter filter, final Parser context) {
        this.mFilter = (NotFilter)filter;
    }
    
    public NodeFilter[] getSubNodeFilters() {
        final NodeFilter predicate = this.mFilter.getPredicate();
        NodeFilter[] ret;
        if (null != predicate) {
            ret = new NodeFilter[] { predicate };
        }
        else {
            ret = new NodeFilter[0];
        }
        return ret;
    }
    
    public void setSubNodeFilters(final NodeFilter[] filters) {
        if (0 != filters.length) {
            this.mFilter.setPredicate(filters[0]);
        }
        else {
            this.mFilter.setPredicate(null);
        }
    }
    
    public String toJavaCode(final StringBuffer out, final int[] context) {
        String name;
        if (null != this.mFilter.getPredicate()) {
            name = ((Filter)this.mFilter.getPredicate()).toJavaCode(out, context);
        }
        else {
            name = null;
        }
        final String ret = "filter" + context[1]++;
        Filter.spaces(out, context[0]);
        out.append("NotFilter ");
        out.append(ret);
        out.append(" = new NotFilter ();");
        Filter.newline(out);
        if (null != name) {
            Filter.spaces(out, context[0]);
            out.append(ret);
            out.append(".setPredicate (");
            out.append(name);
            out.append(");");
            Filter.newline(out);
        }
        return ret;
    }
    
    public boolean accept(final Node node) {
        return this.mFilter.accept(node);
    }
}
