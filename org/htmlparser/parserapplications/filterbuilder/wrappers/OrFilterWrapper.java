package org.htmlparser.parserapplications.filterbuilder.wrappers;

import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.NodeFilter;
import java.awt.Component;
import org.htmlparser.filters.OrFilter;
import org.htmlparser.parserapplications.filterbuilder.SubFilterList;
import org.htmlparser.parserapplications.filterbuilder.Filter;

public class OrFilterWrapper extends Filter
{
    protected SubFilterList mContainer;
    protected OrFilter mFilter;
    
    public OrFilterWrapper() {
        this.mFilter = new OrFilter();
        this.add(this.mContainer = new SubFilterList(this, "Predicates", 0));
    }
    
    public String getDescription() {
        return "Or";
    }
    
    public String getIconSpec() {
        return "images/OrFilter.gif";
    }
    
    public NodeFilter getNodeFilter() {
        final OrFilter ret = new OrFilter();
        final NodeFilter[] predicates = this.mFilter.getPredicates();
        final NodeFilter[] temp = new NodeFilter[predicates.length];
        for (int i = 0; i < predicates.length; ++i) {
            temp[i] = ((Filter)predicates[i]).getNodeFilter();
        }
        ret.setPredicates(temp);
        return ret;
    }
    
    public void setNodeFilter(final NodeFilter filter, final Parser context) {
        this.mFilter = (OrFilter)filter;
    }
    
    public NodeFilter[] getSubNodeFilters() {
        return this.mFilter.getPredicates();
    }
    
    public void setSubNodeFilters(final NodeFilter[] filters) {
        this.mFilter.setPredicates(filters);
    }
    
    public String toJavaCode(final StringBuffer out, final int[] context) {
        final NodeFilter[] predicates = this.mFilter.getPredicates();
        String array = null;
        if (0 != predicates.length) {
            final String[] names = new String[predicates.length];
            for (int i = 0; i < predicates.length; ++i) {
                names[i] = ((Filter)predicates[i]).toJavaCode(out, context);
            }
            array = "array" + context[2]++;
            Filter.spaces(out, context[0]);
            out.append("NodeFilter[] ");
            out.append(array);
            out.append(" = new NodeFilter[");
            out.append(predicates.length);
            out.append("];");
            Filter.newline(out);
            for (int i = 0; i < predicates.length; ++i) {
                Filter.spaces(out, context[0]);
                out.append(array);
                out.append("[");
                out.append(i);
                out.append("] = ");
                out.append(names[i]);
                out.append(";");
                Filter.newline(out);
            }
        }
        final String ret = "filter" + context[1]++;
        Filter.spaces(out, context[0]);
        out.append("OrFilter ");
        out.append(ret);
        out.append(" = new OrFilter ();");
        Filter.newline(out);
        if (0 != predicates.length) {
            Filter.spaces(out, context[0]);
            out.append(ret);
            out.append(".setPredicates (");
            out.append(array);
            out.append(");");
            Filter.newline(out);
        }
        return ret;
    }
    
    public boolean accept(final Node node) {
        return this.mFilter.accept(node);
    }
}
