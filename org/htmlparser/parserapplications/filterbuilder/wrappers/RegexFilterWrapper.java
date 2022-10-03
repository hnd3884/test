package org.htmlparser.parserapplications.filterbuilder.wrappers;

import javax.swing.text.Document;
import javax.swing.text.BadLocationException;
import javax.swing.event.DocumentEvent;
import java.awt.event.ActionEvent;
import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.NodeFilter;
import java.awt.Component;
import javax.swing.border.Border;
import javax.swing.border.BevelBorder;
import javax.swing.JComboBox;
import javax.swing.JTextArea;
import org.htmlparser.filters.RegexFilter;
import javax.swing.event.DocumentListener;
import java.awt.event.ActionListener;
import org.htmlparser.parserapplications.filterbuilder.Filter;

public class RegexFilterWrapper extends Filter implements ActionListener, DocumentListener
{
    public static Object[][] mMap;
    protected RegexFilter mFilter;
    protected JTextArea mPattern;
    protected JComboBox mStrategy;
    
    public RegexFilterWrapper() {
        this.mFilter = new RegexFilter();
        (this.mPattern = new JTextArea(2, 20)).setBorder(new BevelBorder(1));
        this.add(this.mPattern);
        this.mPattern.getDocument().addDocumentListener(this);
        this.mPattern.setText(this.mFilter.getPattern());
        (this.mStrategy = new JComboBox()).addItem("MATCH");
        this.mStrategy.addItem("LOOKINGAT");
        this.mStrategy.addItem("FIND");
        this.add(this.mStrategy);
        this.mStrategy.addActionListener(this);
        this.mStrategy.setSelectedIndex(this.strategyToIndex(this.mFilter.getStrategy()));
    }
    
    public String getDescription() {
        return "Nodes containing regex";
    }
    
    public String getIconSpec() {
        return "images/RegexFilter.gif";
    }
    
    public NodeFilter getNodeFilter() {
        final RegexFilter ret = new RegexFilter();
        ret.setStrategy(this.mFilter.getStrategy());
        ret.setPattern(this.mFilter.getPattern());
        return ret;
    }
    
    public void setNodeFilter(final NodeFilter filter, final Parser context) {
        this.mFilter = (RegexFilter)filter;
        this.mPattern.setText(this.mFilter.getPattern());
        this.mStrategy.setSelectedIndex(this.strategyToIndex(this.mFilter.getStrategy()));
    }
    
    public NodeFilter[] getSubNodeFilters() {
        return new NodeFilter[0];
    }
    
    public void setSubNodeFilters(final NodeFilter[] filters) {
    }
    
    public String toJavaCode(final StringBuffer out, final int[] context) {
        final String ret = "filter" + context[1]++;
        Filter.spaces(out, context[0]);
        out.append("RegexFilter ");
        out.append(ret);
        out.append(" = new RegexFilter ();");
        Filter.newline(out);
        Filter.spaces(out, context[0]);
        out.append(ret);
        out.append(".setStrategy (RegexFilter.");
        out.append(this.strategyToString(this.mFilter.getStrategy()));
        out.append(");");
        Filter.newline(out);
        Filter.spaces(out, context[0]);
        out.append(ret);
        out.append(".setPattern (\"");
        out.append(this.mFilter.getPattern());
        out.append("\");");
        Filter.newline(out);
        return ret;
    }
    
    public String strategyToString(final int strategy) {
        for (int i = 0; i < RegexFilterWrapper.mMap.length; ++i) {
            if (strategy == (int)RegexFilterWrapper.mMap[i][1]) {
                return (String)RegexFilterWrapper.mMap[i][0];
            }
        }
        throw new IllegalArgumentException("unknown strategy constant - " + strategy);
    }
    
    public int stringToStrategy(final String strategy) {
        for (int i = 0; i < RegexFilterWrapper.mMap.length; ++i) {
            if (strategy.equalsIgnoreCase((String)RegexFilterWrapper.mMap[i][0])) {
                return (int)RegexFilterWrapper.mMap[i][1];
            }
        }
        throw new IllegalArgumentException("unknown strategy constant - " + strategy);
    }
    
    public int strategyToIndex(final int strategy) {
        for (int i = 0; i < RegexFilterWrapper.mMap.length; ++i) {
            if (strategy == (int)RegexFilterWrapper.mMap[i][1]) {
                return i;
            }
        }
        throw new IllegalArgumentException("unknown strategy constant - " + strategy);
    }
    
    public int indexToStrategy(final int index) {
        return (int)RegexFilterWrapper.mMap[index][1];
    }
    
    public boolean accept(final Node node) {
        return this.mFilter.accept(node);
    }
    
    public void actionPerformed(final ActionEvent event) {
        final Object source = event.getSource();
        if (source == this.mStrategy) {
            this.mFilter.setStrategy(this.indexToStrategy(this.mStrategy.getSelectedIndex()));
        }
    }
    
    public void insertUpdate(final DocumentEvent e) {
        final Document doc = e.getDocument();
        try {
            this.mFilter.setPattern(doc.getText(0, doc.getLength()));
        }
        catch (final BadLocationException ble) {
            ble.printStackTrace();
        }
    }
    
    public void removeUpdate(final DocumentEvent e) {
        final Document doc = e.getDocument();
        try {
            this.mFilter.setPattern(doc.getText(0, doc.getLength()));
        }
        catch (final BadLocationException ble) {
            ble.printStackTrace();
        }
    }
    
    public void changedUpdate(final DocumentEvent e) {
    }
    
    static {
        RegexFilterWrapper.mMap = new Object[3][];
        (RegexFilterWrapper.mMap[0] = new Object[2])[0] = "MATCH";
        RegexFilterWrapper.mMap[0][1] = new Integer(1);
        (RegexFilterWrapper.mMap[1] = new Object[2])[0] = "LOOKINGAT";
        RegexFilterWrapper.mMap[1][1] = new Integer(2);
        (RegexFilterWrapper.mMap[2] = new Object[2])[0] = "FIND";
        RegexFilterWrapper.mMap[2][1] = new Integer(3);
    }
}
