package org.htmlparser.beans;

import java.beans.PropertyChangeEvent;
import java.net.URLConnection;
import java.net.URL;
import java.awt.FontMetrics;
import java.awt.Dimension;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import javax.swing.JList;

public class HTMLLinkBean extends JList implements Serializable, PropertyChangeListener
{
    protected LinkBean mBean;
    
    public HTMLLinkBean() {
        this.getBean().addPropertyChangeListener(this);
    }
    
    protected LinkBean getBean() {
        if (null == this.mBean) {
            this.mBean = new LinkBean();
        }
        return this.mBean;
    }
    
    public Dimension getMinimumSize() {
        final FontMetrics met = this.getFontMetrics(this.getFont());
        final int width = met.stringWidth("http://localhost");
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
    
    public URL[] getLinks() {
        return this.getBean().getLinks();
    }
    
    public String getURL() {
        return this.getBean().getURL();
    }
    
    public void setURL(final String url) {
        this.getBean().setURL(url);
    }
    
    public URLConnection getConnection() {
        return this.getBean().getConnection();
    }
    
    public void setConnection(final URLConnection connection) {
        this.getBean().setConnection(connection);
    }
    
    public void propertyChange(final PropertyChangeEvent event) {
        if (event.getPropertyName().equals("links")) {
            this.setListData(this.getBean().getLinks());
        }
    }
}
