package org.htmlparser.parserapplications.filterbuilder.wrappers;

import java.awt.event.ActionEvent;
import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.NodeFilter;
import java.awt.Component;
import org.htmlparser.parserapplications.filterbuilder.SubFilterList;
import javax.swing.JCheckBox;
import org.htmlparser.filters.HasChildFilter;
import java.awt.event.ActionListener;
import org.htmlparser.parserapplications.filterbuilder.Filter;

public class HasChildFilterWrapper extends Filter implements ActionListener
{
    protected HasChildFilter mFilter;
    protected JCheckBox mRecursive;
    protected SubFilterList mContainer;
    
    public HasChildFilterWrapper() {
        this.mFilter = new HasChildFilter();
        this.add(this.mRecursive = new JCheckBox("Recursive"));
        this.mRecursive.addActionListener(this);
        this.mRecursive.setSelected(this.mFilter.getRecursive());
        this.add(this.mContainer = new SubFilterList(this, "Child Filter", 1));
    }
    
    public String getDescription() {
        return "Has Child";
    }
    
    public String getIconSpec() {
        return "images/HasChildFilter.gif";
    }
    
    public NodeFilter getNodeFilter() {
        final HasChildFilter ret = new HasChildFilter();
        ret.setRecursive(this.mFilter.getRecursive());
        final NodeFilter filter = this.mFilter.getChildFilter();
        if (null != filter) {
            ret.setChildFilter(((Filter)filter).getNodeFilter());
        }
        return ret;
    }
    
    public void setNodeFilter(final NodeFilter filter, final Parser context) {
        this.mFilter = (HasChildFilter)filter;
        this.mRecursive.setSelected(this.mFilter.getRecursive());
    }
    
    public NodeFilter[] getSubNodeFilters() {
        final NodeFilter filter = this.mFilter.getChildFilter();
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
            this.mFilter.setChildFilter(filters[0]);
        }
        else {
            this.mFilter.setChildFilter(null);
        }
    }
    
    public String toJavaCode(final StringBuffer out, final int[] context) {
        String name;
        if (null != this.mFilter.getChildFilter()) {
            name = ((Filter)this.mFilter.getChildFilter()).toJavaCode(out, context);
        }
        else {
            name = null;
        }
        final String ret = "filter" + context[1]++;
        Filter.spaces(out, context[0]);
        out.append("HasChildFilter ");
        out.append(ret);
        out.append(" = new HasChildFilter ();");
        Filter.newline(out);
        Filter.spaces(out, context[0]);
        out.append(ret);
        out.append(".setRecursive (");
        out.append(this.mFilter.getRecursive() ? "true" : "false");
        out.append(");");
        Filter.newline(out);
        if (null != name) {
            Filter.spaces(out, context[0]);
            out.append(ret);
            out.append(".setChildFilter (");
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
        final Object source = event.getSource();
        if (source == this.mRecursive) {
            final boolean recursive = this.mRecursive.isSelected();
            this.mFilter.setRecursive(recursive);
        }
    }
}
