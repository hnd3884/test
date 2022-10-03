package org.htmlparser.beans;

import java.net.URLConnection;
import java.beans.PropertyChangeListener;
import org.htmlparser.util.ParserException;
import org.htmlparser.util.NodeList;
import org.htmlparser.NodeFilter;
import java.net.MalformedURLException;
import org.htmlparser.tags.LinkTag;
import java.util.Vector;
import org.htmlparser.util.EncodingChangeException;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.Parser;
import java.net.URL;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;

public class LinkBean implements Serializable
{
    public static final String PROP_LINKS_PROPERTY = "links";
    public static final String PROP_URL_PROPERTY = "URL";
    protected PropertyChangeSupport mPropertySupport;
    protected URL[] mLinks;
    protected Parser mParser;
    
    public LinkBean() {
        this.mPropertySupport = new PropertyChangeSupport(this);
        this.mLinks = null;
        this.mParser = new Parser();
    }
    
    protected URL[] extractLinks() throws ParserException {
        this.mParser.reset();
        final NodeFilter filter = new NodeClassFilter(LinkTag.class);
        NodeList list;
        try {
            list = this.mParser.extractAllNodesThatMatch(filter);
        }
        catch (final EncodingChangeException ece) {
            this.mParser.reset();
            list = this.mParser.extractAllNodesThatMatch(filter);
        }
        final Vector vector = new Vector();
        for (int i = 0; i < list.size(); ++i) {
            try {
                final LinkTag link = (LinkTag)list.elementAt(i);
                vector.add(new URL(link.getLink()));
            }
            catch (final MalformedURLException ex) {}
        }
        final URL[] ret = new URL[vector.size()];
        vector.copyInto(ret);
        return ret;
    }
    
    protected boolean equivalent(final URL[] array1, final URL[] array2) {
        boolean ret = false;
        if (null == array1 && null == array2) {
            ret = true;
        }
        else if (null != array1 && null != array2 && array1.length == array2.length) {
            ret = true;
            for (int i = 0; i < array1.length && ret; ++i) {
                if (array1[i] != array2[i]) {
                    ret = false;
                }
            }
        }
        return ret;
    }
    
    public void addPropertyChangeListener(final PropertyChangeListener listener) {
        this.mPropertySupport.addPropertyChangeListener(listener);
    }
    
    public void removePropertyChangeListener(final PropertyChangeListener listener) {
        this.mPropertySupport.removePropertyChangeListener(listener);
    }
    
    private void setLinks() {
        final String url = this.getURL();
        if (null != url) {
            try {
                final URL[] urls = this.extractLinks();
                if (!this.equivalent(this.mLinks, urls)) {
                    final URL[] oldValue = this.mLinks;
                    this.mLinks = urls;
                    this.mPropertySupport.firePropertyChange("links", oldValue, this.mLinks);
                }
            }
            catch (final ParserException hpe) {
                this.mLinks = null;
            }
        }
    }
    
    public URL[] getLinks() {
        if (null == this.mLinks) {
            try {
                this.mLinks = this.extractLinks();
                this.mPropertySupport.firePropertyChange("links", null, this.mLinks);
            }
            catch (final ParserException hpe) {
                this.mLinks = null;
            }
        }
        return this.mLinks;
    }
    
    public String getURL() {
        return this.mParser.getURL();
    }
    
    public void setURL(final String url) {
        final String old = this.getURL();
        if (null != old || null == url) {
            if (null == old || old.equals(url)) {
                return;
            }
        }
        try {
            this.mParser.setURL(url);
            this.mPropertySupport.firePropertyChange("URL", old, this.getURL());
            this.setLinks();
        }
        catch (final ParserException ex) {}
    }
    
    public URLConnection getConnection() {
        return this.mParser.getConnection();
    }
    
    public void setConnection(final URLConnection connection) {
        try {
            this.mParser.setConnection(connection);
            this.setLinks();
        }
        catch (final ParserException ex) {}
    }
    
    public static void main(final String[] args) {
        if (0 >= args.length) {
            System.out.println("Usage: java -classpath htmlparser.jar org.htmlparser.beans.LinkBean <http://whatever_url>");
        }
        else {
            final LinkBean lb = new LinkBean();
            lb.setURL(args[0]);
            final URL[] urls = lb.getLinks();
            for (int i = 0; i < urls.length; ++i) {
                System.out.println(urls[i]);
            }
        }
    }
}
