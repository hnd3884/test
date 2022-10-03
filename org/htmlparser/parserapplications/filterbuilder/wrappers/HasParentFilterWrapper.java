package org.htmlparser.parserapplications.filterbuilder.wrappers;

import java.awt.event.ActionEvent;
import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.NodeFilter;
import java.awt.Component;
import org.htmlparser.parserapplications.filterbuilder.SubFilterList;
import javax.swing.JCheckBox;
import org.htmlparser.filters.HasParentFilter;
import java.awt.event.ActionListener;
import org.htmlparser.parserapplications.filterbuilder.Filter;

public class HasParentFilterWrapper extends Filter implements ActionListener
{
    protected HasParentFilter mFilter;
    protected JCheckBox mRecursive;
    protected SubFilterList mContainer;
    
    public HasParentFilterWrapper() {
        this.mFilter = new HasParentFilter();
        this.add(this.mRecursive = new JCheckBox("Recursive"));
        this.mRecursive.addActionListener(this);
        this.mRecursive.setSelected(this.mFilter.getRecursive());
        this.add(this.mContainer = new SubFilterList(this, "Parent Filter", 1));
    }
    
    public String getDescription() {
        return "Has Parent";
    }
    
    public String getIconSpec() {
        return "images/HasParentFilter.gif";
    }
    
    public NodeFilter getNodeFilter() {
        final HasParentFilter ret = new HasParentFilter();
        ret.setRecursive(this.mFilter.getRecursive());
        final NodeFilter filter = this.mFilter.getParentFilter();
        if (null != filter) {
            ret.setParentFilter(((Filter)filter).getNodeFilter());
        }
        return ret;
    }
    
    public void setNodeFilter(final NodeFilter filter, final Parser context) {
        this.mFilter = (HasParentFilter)filter;
        this.mRecursive.setSelected(this.mFilter.getRecursive());
    }
    
    public NodeFilter[] getSubNodeFilters() {
        final NodeFilter filter = this.mFilter.getParentFilter();
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
            this.mFilter.setParentFilter(filters[0]);
        }
        else {
            this.mFilter.setParentFilter(null);
        }
    }
    
    public String toJavaCode(final StringBuffer out, final int[] context) {
        String name;
        if (null != this.mFilter.getParentFilter()) {
            name = ((Filter)this.mFilter.getParentFilter()).toJavaCode(out, context);
        }
        else {
            name = null;
        }
        final String ret = "filter" + context[1]++;
        Filter.spaces(out, context[0]);
        out.append("HasParentFilter ");
        out.append(ret);
        out.append(" = new HasParentFilter ();");
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
            out.append(".setParentFilter (");
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
