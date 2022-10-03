package org.htmlparser.parserapplications.filterbuilder.wrappers;

import java.awt.event.ActionEvent;
import org.htmlparser.Node;
import org.htmlparser.Tag;
import java.util.Iterator;
import java.util.Set;
import org.htmlparser.NodeFactory;
import org.htmlparser.PrototypicalNodeFactory;
import org.htmlparser.Parser;
import org.htmlparser.NodeFilter;
import java.awt.Component;
import javax.swing.JComboBox;
import org.htmlparser.filters.NodeClassFilter;
import java.awt.event.ActionListener;
import org.htmlparser.parserapplications.filterbuilder.Filter;

public class NodeClassFilterWrapper extends Filter implements ActionListener
{
    protected NodeClassFilter mFilter;
    protected JComboBox mClass;
    
    public NodeClassFilterWrapper() {
        this.mFilter = new NodeClassFilter();
        (this.mClass = new JComboBox()).addItem("");
        this.add(this.mClass);
        this.mClass.addActionListener(this);
    }
    
    public String getDescription() {
        return "Nodes of class";
    }
    
    public String getIconSpec() {
        return "images/NodeClassFilter.gif";
    }
    
    public NodeFilter getNodeFilter() {
        final NodeClassFilter ret = new NodeClassFilter();
        ret.setMatchClass(this.mFilter.getMatchClass());
        return ret;
    }
    
    public void setNodeFilter(final NodeFilter filter, final Parser context) {
        this.mFilter = (NodeClassFilter)filter;
        final NodeFactory factory = context.getNodeFactory();
        if (factory instanceof PrototypicalNodeFactory) {
            final PrototypicalNodeFactory proto = (PrototypicalNodeFactory)factory;
            final Set names = proto.getTagNames();
            final Iterator iterator = names.iterator();
            while (iterator.hasNext()) {
                final String name = iterator.next();
                final Tag tag = proto.get(name);
                this.mClass.addItem(tag.getClass().getName());
            }
        }
        this.mClass.setSelectedItem(this.mFilter.getMatchClass().getName());
    }
    
    public NodeFilter[] getSubNodeFilters() {
        return new NodeFilter[0];
    }
    
    public void setSubNodeFilters(final NodeFilter[] filters) {
    }
    
    public String toJavaCode(final StringBuffer out, final int[] context) {
        final String ret = "filter" + context[1]++;
        Filter.spaces(out, context[0]);
        out.append("NodeClassFilter ");
        out.append(ret);
        out.append(" = new NodeClassFilter ();");
        Filter.newline(out);
        Filter.spaces(out, context[0]);
        out.append("try { ");
        out.append(ret);
        out.append(".setMatchClass (Class.forName (\"");
        out.append(this.mFilter.getMatchClass().getName());
        out.append("\")); } catch (ClassNotFoundException cnfe) { cnfe.printStackTrace (); }");
        Filter.newline(out);
        return ret;
    }
    
    public boolean accept(final Node node) {
        return this.mFilter.accept(node);
    }
    
    public void actionPerformed(final ActionEvent event) {
        final Object source = event.getSource();
        if (source == this.mClass) {
            try {
                this.mFilter.setMatchClass(Class.forName((String)this.mClass.getSelectedItem()));
            }
            catch (final ClassNotFoundException cnfe) {
                cnfe.printStackTrace();
            }
        }
    }
}
