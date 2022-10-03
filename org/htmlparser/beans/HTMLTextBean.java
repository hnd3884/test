package org.htmlparser.beans;

import java.beans.PropertyChangeEvent;
import java.net.URLConnection;
import java.awt.FontMetrics;
import java.awt.Dimension;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import javax.swing.JTextArea;

public class HTMLTextBean extends JTextArea implements Serializable, PropertyChangeListener
{
    protected StringBean mBean;
    
    public HTMLTextBean() {
        this.getBean().addPropertyChangeListener(this);
    }
    
    public Dimension getMinimumSize() {
        final FontMetrics met = this.getFontMetrics(this.getFont());
        final int width = met.stringWidth("Hello World");
        final int height = met.getLeading() + met.getHeight() + met.getDescent();
        return new Dimension(width, height);
    }
    
    public void addPropertyChangeListener(final PropertyChangeListener listener) {
        super.addPropertyChangeListener(listener);
        this.getBean().addPropertyChangeListener(listener);
    }
    
    public void removePropertyChangeListener(final PropertyChangeListener listener) {
        super.addPropertyChangeListener(listener);
        this.getBean().removePropertyChangeListener(listener);
    }
    
    public StringBean getBean() {
        if (null == this.mBean) {
            this.mBean = new StringBean();
        }
        return this.mBean;
    }
    
    public String getStrings() {
        return this.getBean().getStrings();
    }
    
    public boolean getLinks() {
        return this.getBean().getLinks();
    }
    
    public void setLinks(final boolean links) {
        this.getBean().setLinks(links);
    }
    
    public String getURL() {
        return this.getBean().getURL();
    }
    
    public void setURL(final String url) {
        this.getBean().setURL(url);
    }
    
    public boolean getReplaceNonBreakingSpaces() {
        return this.getBean().getReplaceNonBreakingSpaces();
    }
    
    public void setReplaceNonBreakingSpaces(final boolean replace) {
        this.getBean().setReplaceNonBreakingSpaces(replace);
    }
    
    public boolean getCollapse() {
        return this.getBean().getCollapse();
    }
    
    public void setCollapse(final boolean collapse) {
        this.getBean().setCollapse(collapse);
    }
    
    public URLConnection getConnection() {
        return this.getBean().getConnection();
    }
    
    public void setConnection(final URLConnection connection) {
        this.getBean().setConnection(connection);
    }
    
    public void propertyChange(final PropertyChangeEvent event) {
        if (event.getPropertyName().equals("strings")) {
            this.setText(this.getBean().getStrings());
            this.setCaretPosition(0);
        }
    }
}
