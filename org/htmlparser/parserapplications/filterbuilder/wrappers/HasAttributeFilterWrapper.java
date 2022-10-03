package org.htmlparser.parserapplications.filterbuilder.wrappers;

import javax.swing.text.Document;
import javax.swing.text.BadLocationException;
import javax.swing.event.DocumentEvent;
import java.awt.event.ActionEvent;
import java.util.Iterator;
import org.htmlparser.util.NodeIterator;
import org.htmlparser.util.ParserException;
import java.util.HashSet;
import org.htmlparser.Parser;
import org.htmlparser.NodeFilter;
import org.htmlparser.util.NodeList;
import java.util.Vector;
import org.htmlparser.tags.CompositeTag;
import org.htmlparser.Attribute;
import org.htmlparser.Tag;
import org.htmlparser.Node;
import java.util.Set;
import javax.swing.border.Border;
import javax.swing.border.BevelBorder;
import java.awt.Component;
import javax.swing.JTextArea;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import org.htmlparser.filters.HasAttributeFilter;
import javax.swing.event.DocumentListener;
import java.awt.event.ActionListener;
import org.htmlparser.parserapplications.filterbuilder.Filter;

public class HasAttributeFilterWrapper extends Filter implements ActionListener, DocumentListener
{
    protected HasAttributeFilter mFilter;
    protected JComboBox mAttributeName;
    protected JCheckBox mValued;
    protected JTextArea mAttributeValue;
    
    public HasAttributeFilterWrapper() {
        this.mFilter = new HasAttributeFilter();
        (this.mAttributeName = new JComboBox()).setEditable(true);
        this.add(this.mAttributeName);
        this.mAttributeName.addItem(this.mFilter.getAttributeName());
        this.mAttributeName.addActionListener(this);
        final String value = this.mFilter.getAttributeValue();
        this.add(this.mValued = new JCheckBox("Has Value"));
        this.mValued.setSelected(null != value);
        this.mValued.addActionListener(this);
        (this.mAttributeValue = new JTextArea(2, 20)).setBorder(new BevelBorder(1));
        this.add(this.mAttributeValue);
        if (null != value) {
            this.mAttributeValue.setText(value);
        }
        else {
            this.mAttributeValue.setVisible(false);
        }
        this.mAttributeValue.getDocument().addDocumentListener(this);
    }
    
    protected void addAttributes(final Set set, final Node node) {
        if (node instanceof Tag) {
            final Vector attributes = ((Tag)node).getAttributesEx();
            for (int i = 1; i < attributes.size(); ++i) {
                final Attribute attribute = attributes.elementAt(i);
                final String name = attribute.getName();
                if (null != name) {
                    set.add(name);
                }
            }
            if (node instanceof CompositeTag) {
                final NodeList children = ((CompositeTag)node).getChildren();
                if (null != children) {
                    for (int i = 0; i < children.size(); ++i) {
                        this.addAttributes(set, children.elementAt(i));
                    }
                }
            }
        }
    }
    
    protected void addAttributeValues(final Set set, final Node node) {
        if (node instanceof Tag) {
            final Vector attributes = ((Tag)node).getAttributesEx();
            for (int i = 1; i < attributes.size(); ++i) {
                final Attribute attribute = attributes.elementAt(i);
                if (null != attribute.getName()) {
                    final String value = attribute.getValue();
                    if (null != value) {
                        set.add(value);
                    }
                }
            }
            if (node instanceof CompositeTag) {
                final NodeList children = ((CompositeTag)node).getChildren();
                if (null != children) {
                    for (int i = 0; i < children.size(); ++i) {
                        this.addAttributeValues(set, children.elementAt(i));
                    }
                }
            }
        }
    }
    
    public String getDescription() {
        return "Has attribute";
    }
    
    public String getIconSpec() {
        return "images/HasAttributeFilter.gif";
    }
    
    public NodeFilter getNodeFilter() {
        final HasAttributeFilter ret = new HasAttributeFilter();
        ret.setAttributeName(this.mFilter.getAttributeName());
        ret.setAttributeValue(this.mFilter.getAttributeValue());
        return ret;
    }
    
    public void setNodeFilter(final NodeFilter filter, final Parser context) {
        this.mFilter = (HasAttributeFilter)filter;
        final Set set = new HashSet();
        context.reset();
        try {
            final NodeIterator iterator = context.elements();
            while (iterator.hasMoreNodes()) {
                this.addAttributes(set, iterator.nextNode());
            }
        }
        catch (final ParserException ex) {}
        final Iterator iterator2 = set.iterator();
        while (iterator2.hasNext()) {
            this.mAttributeName.addItem(iterator2.next());
        }
        final String name = this.mFilter.getAttributeName();
        if (!name.equals("")) {
            this.mAttributeName.setSelectedItem(name);
        }
        final String value = this.mFilter.getAttributeValue();
        if (null != value) {
            this.mValued.setSelected(true);
            this.mAttributeValue.setVisible(true);
            this.mAttributeValue.setText(value);
        }
        else {
            this.mValued.setSelected(false);
            this.mAttributeValue.setVisible(false);
        }
    }
    
    public NodeFilter[] getSubNodeFilters() {
        return new NodeFilter[0];
    }
    
    public void setSubNodeFilters(final NodeFilter[] filters) {
    }
    
    public String toJavaCode(final StringBuffer out, final int[] context) {
        final String ret = "filter" + context[1]++;
        Filter.spaces(out, context[0]);
        out.append("HasAttributeFilter ");
        out.append(ret);
        out.append(" = new HasAttributeFilter ();");
        Filter.newline(out);
        Filter.spaces(out, context[0]);
        out.append(ret);
        out.append(".setAttributeName (\"");
        out.append(this.mFilter.getAttributeName());
        out.append("\");");
        Filter.newline(out);
        if (null != this.mFilter.getAttributeValue()) {
            Filter.spaces(out, context[0]);
            out.append(ret);
            out.append(".setAttributeValue (\"");
            out.append(this.mFilter.getAttributeValue());
            out.append("\");");
            Filter.newline(out);
        }
        return ret;
    }
    
    public boolean accept(final Node node) {
        return this.mFilter.accept(node);
    }
    
    public void actionPerformed(final ActionEvent event) {
        final Object source = event.getSource();
        if (source == this.mAttributeName) {
            final Object[] selection = this.mAttributeName.getSelectedObjects();
            if (null != selection && 0 != selection.length) {
                this.mFilter.setAttributeName((String)selection[0]);
            }
        }
        else if (source == this.mValued) {
            final boolean valued = this.mValued.isSelected();
            if (valued) {
                this.mFilter.setAttributeValue(this.mAttributeValue.getText());
                this.mAttributeValue.setVisible(true);
            }
            else {
                this.mAttributeValue.setVisible(false);
                this.mAttributeValue.setText("");
                this.mFilter.setAttributeValue(null);
            }
        }
    }
    
    public void insertUpdate(final DocumentEvent e) {
        final Document doc = e.getDocument();
        try {
            this.mFilter.setAttributeValue(doc.getText(0, doc.getLength()));
        }
        catch (final BadLocationException ble) {
            ble.printStackTrace();
        }
    }
    
    public void removeUpdate(final DocumentEvent e) {
        final Document doc = e.getDocument();
        try {
            this.mFilter.setAttributeValue(doc.getText(0, doc.getLength()));
        }
        catch (final BadLocationException ble) {
            ble.printStackTrace();
        }
    }
    
    public void changedUpdate(final DocumentEvent e) {
    }
}
