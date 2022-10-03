package org.htmlparser.beans;

import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.visitors.NodeVisitor;
import java.net.URLConnection;
import java.beans.PropertyChangeListener;
import org.htmlparser.util.EncodingChangeException;
import org.htmlparser.util.ParserException;
import org.htmlparser.util.NodeList;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;

public class FilterBean implements Serializable
{
    public static final String PROP_NODES_PROPERTY = "nodes";
    public static final String PROP_TEXT_PROPERTY = "text";
    public static final String PROP_URL_PROPERTY = "URL";
    public static final String PROP_CONNECTION_PROPERTY = "connection";
    protected PropertyChangeSupport mPropertySupport;
    protected Parser mParser;
    protected NodeFilter[] mFilters;
    protected NodeList mNodes;
    protected boolean mRecursive;
    
    public FilterBean() {
        this.mPropertySupport = new PropertyChangeSupport(this);
        this.mParser = new Parser();
        this.mFilters = null;
        this.mNodes = null;
        this.mRecursive = true;
    }
    
    protected void updateNodes(final NodeList nodes) {
        if (null == this.mNodes || !this.mNodes.equals(nodes)) {
            final NodeList oldValue = this.mNodes;
            String oldText;
            if (null != oldValue) {
                oldText = this.getText();
            }
            else {
                oldText = "";
            }
            if (null == oldText) {
                oldText = "";
            }
            this.mNodes = nodes;
            String newText;
            if (null != this.mNodes) {
                newText = this.getText();
            }
            else {
                newText = "";
            }
            if (null == newText) {
                newText = "";
            }
            this.mPropertySupport.firePropertyChange("nodes", oldValue, nodes);
            if (!newText.equals(oldText)) {
                this.mPropertySupport.firePropertyChange("text", oldText, newText);
            }
        }
    }
    
    protected NodeList applyFilters() throws ParserException {
        NodeList ret = this.mParser.parse(null);
        final NodeFilter[] filters = this.getFilters();
        if (null != filters) {
            for (int i = 0; i < filters.length; ++i) {
                ret = ret.extractAllNodesThatMatch(filters[i], this.mRecursive);
            }
        }
        return ret;
    }
    
    protected void setNodes() {
        if (null != this.getURL()) {
            try {
                final NodeList list = this.applyFilters();
                this.updateNodes(list);
            }
            catch (final EncodingChangeException ece) {
                try {
                    this.mParser.reset();
                    final NodeList list = this.applyFilters();
                    this.updateNodes(list);
                }
                catch (final ParserException pe) {
                    this.updateNodes(new NodeList());
                }
            }
            catch (final ParserException pe2) {
                this.updateNodes(new NodeList());
            }
        }
    }
    
    public void addPropertyChangeListener(final PropertyChangeListener listener) {
        this.mPropertySupport.addPropertyChangeListener(listener);
    }
    
    public void removePropertyChangeListener(final PropertyChangeListener listener) {
        this.mPropertySupport.removePropertyChangeListener(listener);
    }
    
    public NodeList getNodes() {
        if (null == this.mNodes) {
            this.setNodes();
        }
        return this.mNodes;
    }
    
    public String getURL() {
        return (null != this.mParser) ? this.mParser.getURL() : null;
    }
    
    public void setURL(final String url) {
        final String old = this.getURL();
        final URLConnection conn = this.getConnection();
        if (null != old || null == url) {
            if (null == old || old.equals(url)) {
                return;
            }
        }
        try {
            if (null == this.mParser) {
                this.mParser = new Parser(url);
            }
            else {
                this.mParser.setURL(url);
            }
            this.mPropertySupport.firePropertyChange("URL", old, this.getURL());
            this.mPropertySupport.firePropertyChange("connection", conn, this.mParser.getConnection());
            this.setNodes();
        }
        catch (final ParserException pe) {
            this.updateNodes(new NodeList());
        }
    }
    
    public URLConnection getConnection() {
        return (null != this.mParser) ? this.mParser.getConnection() : null;
    }
    
    public void setConnection(final URLConnection connection) {
        final String url = this.getURL();
        final URLConnection conn = this.getConnection();
        if (null != conn || null == connection) {
            if (null == conn || conn.equals(connection)) {
                return;
            }
        }
        try {
            if (null == this.mParser) {
                this.mParser = new Parser(connection);
            }
            else {
                this.mParser.setConnection(connection);
            }
            this.mPropertySupport.firePropertyChange("URL", url, this.getURL());
            this.mPropertySupport.firePropertyChange("connection", conn, this.mParser.getConnection());
            this.setNodes();
        }
        catch (final ParserException pe) {
            this.updateNodes(new NodeList());
        }
    }
    
    public NodeFilter[] getFilters() {
        return this.mFilters;
    }
    
    public void setFilters(final NodeFilter[] filters) {
        this.mFilters = filters;
        if (null != this.getParser()) {
            this.getParser().reset();
            this.setNodes();
        }
    }
    
    public Parser getParser() {
        return this.mParser;
    }
    
    public void setParser(final Parser parser) {
        this.mParser = parser;
        if (null != this.getFilters()) {
            this.setNodes();
        }
    }
    
    public String getText() {
        final NodeList list = this.getNodes();
        String ret;
        if (0 != list.size()) {
            final StringBean sb = new StringBean();
            for (int i = 0; i < list.size(); ++i) {
                list.elementAt(i).accept(sb);
            }
            ret = sb.getStrings();
        }
        else {
            ret = "";
        }
        return ret;
    }
    
    public boolean getRecursive() {
        return this.mRecursive;
    }
    
    public void setRecursive(final boolean recursive) {
        this.mRecursive = recursive;
    }
    
    public static void main(final String[] args) {
        if (0 >= args.length) {
            System.out.println("Usage: java -classpath htmlparser.jar org.htmlparser.beans.FilterBean <http://whatever_url> [node name]");
        }
        else {
            final FilterBean fb = new FilterBean();
            if (1 < args.length) {
                fb.setFilters(new NodeFilter[] { new TagNameFilter(args[1]) });
            }
            fb.setURL(args[0]);
            System.out.println(fb.getText());
        }
    }
}
