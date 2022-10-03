package org.htmlparser.parserapplications.filterbuilder.wrappers;

import java.awt.event.ActionEvent;
import org.htmlparser.util.NodeList;
import org.htmlparser.tags.CompositeTag;
import org.htmlparser.Tag;
import org.htmlparser.Node;
import java.util.Iterator;
import org.htmlparser.util.NodeIterator;
import java.util.Set;
import org.htmlparser.util.ParserException;
import java.util.HashSet;
import org.htmlparser.Parser;
import org.htmlparser.NodeFilter;
import java.awt.Component;
import javax.swing.JComboBox;
import org.htmlparser.filters.TagNameFilter;
import java.awt.event.ActionListener;
import org.htmlparser.parserapplications.filterbuilder.Filter;

public class TagNameFilterWrapper extends Filter implements ActionListener
{
    protected TagNameFilter mFilter;
    protected JComboBox mName;
    
    public TagNameFilterWrapper() {
        this.mFilter = new TagNameFilter();
        (this.mName = new JComboBox()).setEditable(true);
        this.add(this.mName);
        this.mName.addItem(this.mFilter.getName());
        this.mName.addActionListener(this);
    }
    
    public String getDescription() {
        return "Tag named";
    }
    
    public String getIconSpec() {
        return "images/TagNameFilter.gif";
    }
    
    public NodeFilter getNodeFilter() {
        final TagNameFilter ret = new TagNameFilter();
        ret.setName(this.mFilter.getName());
        return ret;
    }
    
    public void setNodeFilter(final NodeFilter filter, final Parser context) {
        this.mFilter = (TagNameFilter)filter;
        final Set set = new HashSet();
        context.reset();
        try {
            final NodeIterator iterator = context.elements();
            while (iterator.hasMoreNodes()) {
                this.addName(set, iterator.nextNode());
            }
        }
        catch (final ParserException ex) {}
        final Iterator iterator2 = set.iterator();
        while (iterator2.hasNext()) {
            this.mName.addItem(iterator2.next());
        }
        this.mName.setSelectedItem(this.mFilter.getName());
    }
    
    public NodeFilter[] getSubNodeFilters() {
        return new NodeFilter[0];
    }
    
    public void setSubNodeFilters(final NodeFilter[] filters) {
    }
    
    public String toJavaCode(final StringBuffer out, final int[] context) {
        final String ret = "filter" + context[1]++;
        Filter.spaces(out, context[0]);
        out.append("TagNameFilter ");
        out.append(ret);
        out.append(" = new TagNameFilter ();");
        Filter.newline(out);
        Filter.spaces(out, context[0]);
        out.append(ret);
        out.append(".setName (\"");
        out.append(this.mFilter.getName());
        out.append("\");");
        Filter.newline(out);
        return ret;
    }
    
    protected void addName(final Set set, final Node node) {
        if (node instanceof Tag) {
            set.add(((Tag)node).getTagName());
            if (node instanceof CompositeTag) {
                final NodeList children = ((CompositeTag)node).getChildren();
                if (null != children) {
                    for (int i = 0; i < children.size(); ++i) {
                        this.addName(set, children.elementAt(i));
                    }
                }
            }
        }
    }
    
    public boolean accept(final Node node) {
        return this.mFilter.accept(node);
    }
    
    public void actionPerformed(final ActionEvent event) {
        final Object source = event.getSource();
        if (source == this.mName) {
            final Object[] selection = this.mName.getSelectedObjects();
            if (null != selection && 0 != selection.length) {
                this.mFilter.setName((String)selection[0]);
            }
        }
    }
}
