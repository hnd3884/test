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
import java.util.Locale;
import javax.swing.JComboBox;
import javax.swing.JCheckBox;
import javax.swing.JTextArea;
import org.htmlparser.filters.StringFilter;
import javax.swing.event.DocumentListener;
import java.awt.event.ActionListener;
import org.htmlparser.parserapplications.filterbuilder.Filter;

public class StringFilterWrapper extends Filter implements ActionListener, DocumentListener, Runnable
{
    protected StringFilter mFilter;
    protected JTextArea mPattern;
    protected JCheckBox mCaseSensitivity;
    protected JComboBox mLocale;
    protected static Locale[] mLocales;
    
    public StringFilterWrapper() {
        (this.mFilter = new StringFilter()).setCaseSensitive(true);
        (this.mPattern = new JTextArea(2, 20)).setBorder(new BevelBorder(1));
        this.add(this.mPattern);
        this.mPattern.getDocument().addDocumentListener(this);
        this.mPattern.setText(this.mFilter.getPattern());
        this.add(this.mCaseSensitivity = new JCheckBox("Case Sensitive"));
        this.mCaseSensitivity.addActionListener(this);
        this.mCaseSensitivity.setSelected(this.mFilter.getCaseSensitive());
        synchronized (this.mLocale = new JComboBox()) {
            this.mLocale.addItem(this.mFilter.getLocale().getDisplayName());
            final Thread thread = new Thread(this);
            thread.setName("locale_getter");
            thread.setPriority(1);
            thread.run();
        }
        this.add(this.mLocale);
        this.mLocale.addActionListener(this);
        this.mLocale.setSelectedIndex(0);
        this.mLocale.setVisible(!this.mFilter.getCaseSensitive());
    }
    
    public String getDescription() {
        return "Nodes containing string";
    }
    
    public String getIconSpec() {
        return "images/StringFilter.gif";
    }
    
    public NodeFilter getNodeFilter() {
        final StringFilter ret = new StringFilter();
        ret.setCaseSensitive(this.mFilter.getCaseSensitive());
        ret.setLocale(this.mFilter.getLocale());
        ret.setPattern(this.mFilter.getPattern());
        return ret;
    }
    
    public void setNodeFilter(final NodeFilter filter, final Parser context) {
        this.mFilter = (StringFilter)filter;
        this.mPattern.setText(this.mFilter.getPattern());
        this.mCaseSensitivity.setSelected(this.mFilter.getCaseSensitive());
        this.mLocale.setVisible(!this.mFilter.getCaseSensitive());
        this.mLocale.setSelectedItem(this.mFilter.getLocale().getDisplayName());
    }
    
    public NodeFilter[] getSubNodeFilters() {
        return new NodeFilter[0];
    }
    
    public void setSubNodeFilters(final NodeFilter[] filters) {
    }
    
    public String toJavaCode(final StringBuffer out, final int[] context) {
        final String ret = "filter" + context[1]++;
        Filter.spaces(out, context[0]);
        out.append("StringFilter ");
        out.append(ret);
        out.append(" = new StringFilter ();");
        Filter.newline(out);
        Filter.spaces(out, context[0]);
        out.append(ret);
        out.append(".setCaseSensitive (");
        out.append(this.mFilter.getCaseSensitive() ? "true" : "false");
        out.append(");");
        Filter.newline(out);
        Filter.spaces(out, context[0]);
        out.append(ret);
        out.append(".setLocale (new java.util.Locale (\"");
        out.append(this.mFilter.getLocale().getLanguage());
        out.append("\", \"");
        out.append(this.mFilter.getLocale().getCountry());
        out.append("\", \"");
        out.append(this.mFilter.getLocale().getVariant());
        out.append("\"));");
        Filter.newline(out);
        Filter.spaces(out, context[0]);
        out.append(ret);
        out.append(".setPattern (\"");
        out.append(this.mFilter.getPattern());
        out.append("\");");
        Filter.newline(out);
        return ret;
    }
    
    public boolean accept(final Node node) {
        return this.mFilter.accept(node);
    }
    
    public void actionPerformed(final ActionEvent event) {
        final Object source = event.getSource();
        if (source == this.mCaseSensitivity) {
            final boolean sensitive = this.mCaseSensitivity.isSelected();
            this.mFilter.setCaseSensitive(sensitive);
            this.mLocale.setVisible(!sensitive);
            this.mLocale.setSelectedItem(this.mFilter.getLocale().getDisplayName());
        }
        else if (source == this.mLocale) {
            synchronized (this.mLocale) {
                final Object[] selection = this.mLocale.getSelectedObjects();
                if (null != selection && 0 != selection.length) {
                    final String locale = (String)selection[0];
                    for (int i = 0; i < StringFilterWrapper.mLocales.length; ++i) {
                        if (locale.equals(StringFilterWrapper.mLocales[i].getDisplayName())) {
                            this.mFilter.setLocale(StringFilterWrapper.mLocales[i]);
                        }
                    }
                }
            }
        }
    }
    
    public void run() {
        synchronized (this.mLocale) {
            StringFilterWrapper.mLocales = Locale.getAvailableLocales();
            final String locale = this.mFilter.getLocale().getDisplayName();
            for (int i = 0; i < StringFilterWrapper.mLocales.length; ++i) {
                if (!locale.equals(StringFilterWrapper.mLocales[i].getDisplayName())) {
                    this.mLocale.addItem(StringFilterWrapper.mLocales[i].getDisplayName());
                }
            }
            this.mLocale.invalidate();
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
        StringFilterWrapper.mLocales = null;
    }
}
