package org.htmlparser.beans;

import org.htmlparser.tags.LinkTag;
import org.htmlparser.Tag;
import org.htmlparser.util.Translate;
import org.htmlparser.Text;
import java.net.URLConnection;
import java.beans.PropertyChangeListener;
import org.htmlparser.util.EncodingChangeException;
import org.htmlparser.util.ParserException;
import org.htmlparser.Parser;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import org.htmlparser.visitors.NodeVisitor;

public class StringBean extends NodeVisitor implements Serializable
{
    public static final String PROP_STRINGS_PROPERTY = "strings";
    public static final String PROP_LINKS_PROPERTY = "links";
    public static final String PROP_URL_PROPERTY = "URL";
    public static final String PROP_REPLACE_SPACE_PROPERTY = "replaceNonBreakingSpaces";
    public static final String PROP_COLLAPSE_PROPERTY = "collapse";
    public static final String PROP_CONNECTION_PROPERTY = "connection";
    private static final String NEWLINE;
    private static final int NEWLINE_SIZE;
    protected PropertyChangeSupport mPropertySupport;
    protected Parser mParser;
    protected String mStrings;
    protected boolean mLinks;
    protected boolean mReplaceSpace;
    protected boolean mCollapse;
    protected int mCollapseState;
    protected StringBuffer mBuffer;
    protected boolean mIsScript;
    protected boolean mIsPre;
    protected boolean mIsStyle;
    
    public StringBean() {
        super(true, true);
        this.mPropertySupport = new PropertyChangeSupport(this);
        this.mParser = new Parser();
        this.mStrings = null;
        this.mLinks = false;
        this.mReplaceSpace = true;
        this.mCollapse = true;
        this.mCollapseState = 0;
        this.mBuffer = new StringBuffer(4096);
        this.mIsScript = false;
        this.mIsPre = false;
        this.mIsStyle = false;
    }
    
    protected void carriageReturn() {
        final int length = this.mBuffer.length();
        if (0 != length && StringBean.NEWLINE_SIZE <= length && !this.mBuffer.substring(length - StringBean.NEWLINE_SIZE, length).equals(StringBean.NEWLINE)) {
            this.mBuffer.append(StringBean.NEWLINE);
        }
        this.mCollapseState = 0;
    }
    
    protected void collapse(final StringBuffer buffer, final String string) {
        final int chars = string.length();
        if (0 != chars) {
            for (int i = 0; i < chars; ++i) {
                final char character = string.charAt(i);
                switch (character) {
                    case '\t':
                    case '\n':
                    case '\f':
                    case '\r':
                    case ' ':
                    case '\u200b': {
                        if (0 != this.mCollapseState) {
                            this.mCollapseState = 1;
                            break;
                        }
                        break;
                    }
                    default: {
                        if (1 == this.mCollapseState) {
                            buffer.append(' ');
                        }
                        this.mCollapseState = 2;
                        buffer.append(character);
                        break;
                    }
                }
            }
        }
    }
    
    protected String extractStrings() throws ParserException {
        this.mCollapseState = 0;
        this.mParser.visitAllNodesWith(this);
        final String ret = this.mBuffer.toString();
        this.mBuffer = new StringBuffer(4096);
        return ret;
    }
    
    protected void updateStrings(final String strings) {
        if (null == this.mStrings || !this.mStrings.equals(strings)) {
            final String oldValue = this.mStrings;
            this.mStrings = strings;
            this.mPropertySupport.firePropertyChange("strings", oldValue, strings);
        }
    }
    
    protected void setStrings() {
        this.mCollapseState = 0;
        if (null != this.getURL()) {
            try {
                try {
                    this.mParser.visitAllNodesWith(this);
                    this.updateStrings(this.mBuffer.toString());
                }
                finally {
                    this.mBuffer = new StringBuffer(4096);
                }
            }
            catch (final EncodingChangeException ece) {
                this.mIsPre = false;
                this.mIsScript = false;
                this.mIsStyle = false;
                try {
                    this.mParser.reset();
                    this.mBuffer = new StringBuffer(4096);
                    this.mCollapseState = 0;
                    this.mParser.visitAllNodesWith(this);
                    this.updateStrings(this.mBuffer.toString());
                }
                catch (final ParserException pe) {
                    this.updateStrings(pe.toString());
                    this.mBuffer = new StringBuffer(4096);
                }
                finally {
                    this.mBuffer = new StringBuffer(4096);
                }
            }
            catch (final ParserException pe2) {
                this.updateStrings(pe2.toString());
            }
        }
        else {
            this.mStrings = null;
            this.mBuffer = new StringBuffer(4096);
        }
    }
    
    private void resetStrings() {
        if (null != this.mStrings) {
            try {
                this.mParser.setURL(this.getURL());
                this.setStrings();
            }
            catch (final ParserException pe) {
                this.updateStrings(pe.toString());
            }
        }
    }
    
    public void addPropertyChangeListener(final PropertyChangeListener listener) {
        this.mPropertySupport.addPropertyChangeListener(listener);
    }
    
    public void removePropertyChangeListener(final PropertyChangeListener listener) {
        this.mPropertySupport.removePropertyChangeListener(listener);
    }
    
    public String getStrings() {
        if (null == this.mStrings) {
            if (0 == this.mBuffer.length()) {
                this.setStrings();
            }
            else {
                this.updateStrings(this.mBuffer.toString());
            }
        }
        return this.mStrings;
    }
    
    public boolean getLinks() {
        return this.mLinks;
    }
    
    public void setLinks(final boolean links) {
        final boolean oldValue = this.mLinks;
        if (oldValue != links) {
            this.mLinks = links;
            this.mPropertySupport.firePropertyChange("links", oldValue, links);
            this.resetStrings();
        }
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
            this.setStrings();
        }
        catch (final ParserException pe) {
            this.updateStrings(pe.toString());
        }
    }
    
    public boolean getReplaceNonBreakingSpaces() {
        return this.mReplaceSpace;
    }
    
    public void setReplaceNonBreakingSpaces(final boolean replace) {
        final boolean oldValue = this.mReplaceSpace;
        if (oldValue != replace) {
            this.mReplaceSpace = replace;
            this.mPropertySupport.firePropertyChange("replaceNonBreakingSpaces", oldValue, replace);
            this.resetStrings();
        }
    }
    
    public boolean getCollapse() {
        return this.mCollapse;
    }
    
    public void setCollapse(final boolean collapse) {
        this.mCollapseState = 0;
        final boolean oldValue = this.mCollapse;
        if (oldValue != collapse) {
            this.mCollapse = collapse;
            this.mPropertySupport.firePropertyChange("collapse", oldValue, collapse);
            this.resetStrings();
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
            this.setStrings();
        }
        catch (final ParserException pe) {
            this.updateStrings(pe.toString());
        }
    }
    
    public void visitStringNode(final Text string) {
        if (!this.mIsScript && !this.mIsStyle) {
            String text = string.getText();
            if (!this.mIsPre) {
                text = Translate.decode(text);
                if (this.getReplaceNonBreakingSpaces()) {
                    text = text.replace(' ', ' ');
                }
                if (this.getCollapse()) {
                    this.collapse(this.mBuffer, text);
                }
                else {
                    this.mBuffer.append(text);
                }
            }
            else {
                this.mBuffer.append(text);
            }
        }
    }
    
    public void visitTag(final Tag tag) {
        if (tag instanceof LinkTag && this.getLinks()) {
            this.mBuffer.append("<");
            this.mBuffer.append(((LinkTag)tag).getLink());
            this.mBuffer.append(">");
        }
        final String name = tag.getTagName();
        if (name.equalsIgnoreCase("PRE")) {
            this.mIsPre = true;
        }
        else if (name.equalsIgnoreCase("SCRIPT")) {
            this.mIsScript = true;
        }
        else if (name.equalsIgnoreCase("STYLE")) {
            this.mIsStyle = true;
        }
        if (tag.breaksFlow()) {
            this.carriageReturn();
        }
    }
    
    public void visitEndTag(final Tag tag) {
        final String name = tag.getTagName();
        if (name.equalsIgnoreCase("PRE")) {
            this.mIsPre = false;
        }
        else if (name.equalsIgnoreCase("SCRIPT")) {
            this.mIsScript = false;
        }
        else if (name.equalsIgnoreCase("STYLE")) {
            this.mIsStyle = false;
        }
    }
    
    public static void main(final String[] args) {
        if (0 >= args.length) {
            System.out.println("Usage: java -classpath htmlparser.jar org.htmlparser.beans.StringBean <http://whatever_url>");
        }
        else {
            final StringBean sb = new StringBean();
            sb.setLinks(false);
            sb.setReplaceNonBreakingSpaces(true);
            sb.setCollapse(true);
            sb.setURL(args[0]);
            System.out.println(sb.getStrings());
        }
    }
    
    static {
        NEWLINE = System.getProperty("line.separator");
        NEWLINE_SIZE = StringBean.NEWLINE.length();
    }
}
