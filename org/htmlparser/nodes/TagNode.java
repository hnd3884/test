package org.htmlparser.nodes;

import org.htmlparser.visitors.NodeVisitor;
import org.htmlparser.lexer.Cursor;
import org.htmlparser.util.ParserException;
import org.htmlparser.lexer.Lexer;
import java.util.Locale;
import org.htmlparser.Attribute;
import org.htmlparser.scanners.TagScanner;
import org.htmlparser.lexer.Page;
import java.util.Hashtable;
import java.util.Vector;
import org.htmlparser.scanners.Scanner;
import org.htmlparser.Tag;

public class TagNode extends AbstractNode implements Tag
{
    private static final String[] NONE;
    private Scanner mScanner;
    protected static final Scanner mDefaultScanner;
    protected Vector mAttributes;
    protected static Hashtable breakTags;
    
    public TagNode() {
        this(null, -1, -1, new Vector());
    }
    
    public TagNode(final Page page, final int start, final int end, final Vector attributes) {
        super(page, start, end);
        this.mScanner = TagNode.mDefaultScanner;
        this.mAttributes = attributes;
        if (null == this.mAttributes || 0 == this.mAttributes.size()) {
            final String[] names = this.getIds();
            if (null != names && 0 != names.length) {
                this.setTagName(names[0]);
            }
            else {
                this.setTagName("");
            }
        }
    }
    
    public TagNode(final TagNode tag, final TagScanner scanner) {
        this(tag.getPage(), tag.getTagBegin(), tag.getTagEnd(), tag.getAttributesEx());
        this.setThisScanner(scanner);
    }
    
    public String getAttribute(final String name) {
        String ret = null;
        final Attribute attribute = this.getAttributeEx(name);
        if (null != attribute) {
            ret = attribute.getValue();
        }
        return ret;
    }
    
    public void setAttribute(final String key, String value) {
        boolean needed = false;
        boolean singleq = true;
        boolean doubleq = true;
        if (null != value) {
            for (int i = 0; i < value.length(); ++i) {
                final char ch = value.charAt(i);
                if (Character.isWhitespace(ch)) {
                    needed = true;
                }
                else if ('\'' == ch) {
                    singleq = false;
                }
                else if ('\"' == ch) {
                    doubleq = false;
                }
            }
        }
        char quote;
        if (needed) {
            if (doubleq) {
                quote = '\"';
            }
            else if (singleq) {
                quote = '\'';
            }
            else {
                quote = '\"';
                final String ref = "&quot;";
                final StringBuffer buffer = new StringBuffer(value.length() * 5);
                for (int i = 0; i < value.length(); ++i) {
                    final char ch = value.charAt(i);
                    if (quote == ch) {
                        buffer.append(ref);
                    }
                    else {
                        buffer.append(ch);
                    }
                }
                value = buffer.toString();
            }
        }
        else {
            quote = '\0';
        }
        final Attribute attribute = this.getAttributeEx(key);
        if (null != attribute) {
            attribute.setValue(value);
            if ('\0' != quote) {
                attribute.setQuote(quote);
            }
        }
        else {
            this.setAttribute(key, value, quote);
        }
    }
    
    public void removeAttribute(final String key) {
        final Attribute attribute = this.getAttributeEx(key);
        if (null != attribute) {
            this.getAttributesEx().remove(attribute);
        }
    }
    
    public void setAttribute(final String key, final String value, final char quote) {
        this.setAttribute(new Attribute(key, value, quote));
    }
    
    public Attribute getAttributeEx(final String name) {
        Attribute ret = null;
        final Vector attributes = this.getAttributesEx();
        if (null != attributes) {
            for (int size = attributes.size(), i = 0; i < size; ++i) {
                final Attribute attribute = attributes.elementAt(i);
                final String string = attribute.getName();
                if (null != string && name.equalsIgnoreCase(string)) {
                    ret = attribute;
                    i = size;
                }
            }
        }
        return ret;
    }
    
    public void setAttributeEx(final Attribute attribute) {
        this.setAttribute(attribute);
    }
    
    public void setAttribute(final Attribute attribute) {
        boolean replaced = false;
        final Vector attributes = this.getAttributesEx();
        final int length = attributes.size();
        if (0 < length) {
            final String name = attribute.getName();
            for (int i = 1; i < attributes.size(); ++i) {
                final Attribute test = attributes.elementAt(i);
                final String test_name = test.getName();
                if (null != test_name && test_name.equalsIgnoreCase(name)) {
                    attributes.setElementAt(attribute, i);
                    replaced = true;
                }
            }
        }
        if (!replaced) {
            if (0 != length && !attributes.elementAt(length - 1).isWhitespace()) {
                attributes.addElement(new Attribute(" "));
            }
            attributes.addElement(attribute);
        }
    }
    
    public Vector getAttributesEx() {
        return this.mAttributes;
    }
    
    public String getTagName() {
        String ret = this.getRawTagName();
        if (null != ret) {
            ret = ret.toUpperCase(Locale.ENGLISH);
            if (ret.startsWith("/")) {
                ret = ret.substring(1);
            }
            if (ret.endsWith("/")) {
                ret = ret.substring(0, ret.length() - 1);
            }
        }
        return ret;
    }
    
    public String getRawTagName() {
        String ret = null;
        final Vector attributes = this.getAttributesEx();
        if (0 != attributes.size()) {
            ret = attributes.elementAt(0).getName();
        }
        return ret;
    }
    
    public void setTagName(final String name) {
        final Attribute attribute = new Attribute(name, null, '\0');
        Vector attributes = this.getAttributesEx();
        if (null == attributes) {
            attributes = new Vector();
            this.setAttributesEx(attributes);
        }
        if (0 == attributes.size()) {
            attributes.addElement(attribute);
        }
        else {
            final Attribute zeroth = attributes.elementAt(0);
            if (null == zeroth.getValue() && '\0' == zeroth.getQuote()) {
                attributes.setElementAt(attribute, 0);
            }
            else {
                attributes.insertElementAt(attribute, 0);
            }
        }
    }
    
    public String getText() {
        String ret = this.toHtml();
        ret = ret.substring(1, ret.length() - 1);
        return ret;
    }
    
    public void setAttributesEx(final Vector attribs) {
        this.mAttributes = attribs;
    }
    
    public void setTagBegin(final int tagBegin) {
        super.nodeBegin = tagBegin;
    }
    
    public int getTagBegin() {
        return super.nodeBegin;
    }
    
    public void setTagEnd(final int tagEnd) {
        super.nodeEnd = tagEnd;
    }
    
    public int getTagEnd() {
        return super.nodeEnd;
    }
    
    public void setText(final String text) {
        final Lexer lexer = new Lexer(text);
        try {
            final TagNode output = (TagNode)lexer.nextNode();
            super.mPage = output.getPage();
            super.nodeBegin = output.getStartPosition();
            super.nodeEnd = output.getEndPosition();
            this.mAttributes = output.getAttributesEx();
        }
        catch (final ParserException pe) {
            throw new IllegalArgumentException(pe.getMessage());
        }
    }
    
    public String toPlainTextString() {
        return "";
    }
    
    public String toHtml(final boolean verbatim) {
        int length = 2;
        final Vector attributes = this.getAttributesEx();
        final int size = attributes.size();
        for (int i = 0; i < size; ++i) {
            final Attribute attribute = attributes.elementAt(i);
            length += attribute.getLength();
        }
        final StringBuffer ret = new StringBuffer(length);
        ret.append("<");
        for (int i = 0; i < size; ++i) {
            final Attribute attribute = attributes.elementAt(i);
            attribute.toString(ret);
        }
        ret.append(">");
        return ret.toString();
    }
    
    public String toString() {
        String text = this.getText();
        final StringBuffer ret = new StringBuffer(20 + text.length());
        String type;
        if (this.isEndTag()) {
            type = "End";
        }
        else {
            type = "Tag";
        }
        final Cursor start = new Cursor(this.getPage(), this.getStartPosition());
        final Cursor end = new Cursor(this.getPage(), this.getEndPosition());
        ret.append(type);
        ret.append(" (");
        ret.append(start);
        ret.append(",");
        ret.append(end);
        ret.append("): ");
        if (80 < ret.length() + text.length()) {
            text = text.substring(0, 77 - ret.length());
            ret.append(text);
            ret.append("...");
        }
        else {
            ret.append(text);
        }
        return ret.toString();
    }
    
    public boolean breaksFlow() {
        return TagNode.breakTags.containsKey(this.getTagName());
    }
    
    public void accept(final NodeVisitor visitor) {
        if (this.isEndTag()) {
            visitor.visitEndTag(this);
        }
        else {
            visitor.visitTag(this);
        }
    }
    
    public boolean isEmptyXmlTag() {
        boolean ret = false;
        final Vector attributes = this.getAttributesEx();
        final int size = attributes.size();
        if (0 < size) {
            final Attribute attribute = attributes.elementAt(size - 1);
            final String name = attribute.getName();
            if (null != name) {
                final int length = name.length();
                ret = (name.charAt(length - 1) == '/');
            }
        }
        return ret;
    }
    
    public void setEmptyXmlTag(final boolean emptyXmlTag) {
        final Vector attributes = this.getAttributesEx();
        final int size = attributes.size();
        if (0 < size) {
            Attribute attribute = attributes.elementAt(size - 1);
            String name = attribute.getName();
            if (null != name) {
                final int length = name.length();
                final String value = attribute.getValue();
                if (null == value) {
                    if (name.charAt(length - 1) == '/') {
                        if (!emptyXmlTag) {
                            if (1 == length) {
                                attributes.removeElementAt(size - 1);
                            }
                            else {
                                name = name.substring(0, length - 1);
                                attribute = new Attribute(name, null);
                                attributes.removeElementAt(size - 1);
                                attributes.addElement(attribute);
                            }
                        }
                    }
                    else if (emptyXmlTag) {
                        attribute = new Attribute(" ");
                        attributes.addElement(attribute);
                        attribute = new Attribute("/", null);
                        attributes.addElement(attribute);
                    }
                }
                else if (emptyXmlTag) {
                    attribute = new Attribute(" ");
                    attributes.addElement(attribute);
                    attribute = new Attribute("/", null);
                    attributes.addElement(attribute);
                }
            }
            else if (emptyXmlTag) {
                attribute = new Attribute("/", null);
                attributes.addElement(attribute);
            }
        }
        else if (emptyXmlTag) {
            final Attribute attribute = new Attribute("/", null);
            attributes.addElement(attribute);
        }
    }
    
    public boolean isEndTag() {
        final String raw = this.getRawTagName();
        return null != raw && (0 != raw.length() && '/' == raw.charAt(0));
    }
    
    public int getStartingLineNumber() {
        return this.getPage().row(this.getStartPosition());
    }
    
    public int getEndingLineNumber() {
        return this.getPage().row(this.getEndPosition());
    }
    
    public String[] getIds() {
        return TagNode.NONE;
    }
    
    public String[] getEnders() {
        return TagNode.NONE;
    }
    
    public String[] getEndTagEnders() {
        return TagNode.NONE;
    }
    
    public Scanner getThisScanner() {
        return this.mScanner;
    }
    
    public void setThisScanner(final Scanner scanner) {
        this.mScanner = scanner;
    }
    
    public Tag getEndTag() {
        return null;
    }
    
    public void setEndTag(final Tag end) {
    }
    
    static {
        NONE = new String[0];
        mDefaultScanner = new TagScanner();
        (TagNode.breakTags = new Hashtable(30)).put("BLOCKQUOTE", Boolean.TRUE);
        TagNode.breakTags.put("BODY", Boolean.TRUE);
        TagNode.breakTags.put("BR", Boolean.TRUE);
        TagNode.breakTags.put("CENTER", Boolean.TRUE);
        TagNode.breakTags.put("DD", Boolean.TRUE);
        TagNode.breakTags.put("DIR", Boolean.TRUE);
        TagNode.breakTags.put("DIV", Boolean.TRUE);
        TagNode.breakTags.put("DL", Boolean.TRUE);
        TagNode.breakTags.put("DT", Boolean.TRUE);
        TagNode.breakTags.put("FORM", Boolean.TRUE);
        TagNode.breakTags.put("H1", Boolean.TRUE);
        TagNode.breakTags.put("H2", Boolean.TRUE);
        TagNode.breakTags.put("H3", Boolean.TRUE);
        TagNode.breakTags.put("H4", Boolean.TRUE);
        TagNode.breakTags.put("H5", Boolean.TRUE);
        TagNode.breakTags.put("H6", Boolean.TRUE);
        TagNode.breakTags.put("HEAD", Boolean.TRUE);
        TagNode.breakTags.put("HR", Boolean.TRUE);
        TagNode.breakTags.put("HTML", Boolean.TRUE);
        TagNode.breakTags.put("ISINDEX", Boolean.TRUE);
        TagNode.breakTags.put("LI", Boolean.TRUE);
        TagNode.breakTags.put("MENU", Boolean.TRUE);
        TagNode.breakTags.put("NOFRAMES", Boolean.TRUE);
        TagNode.breakTags.put("OL", Boolean.TRUE);
        TagNode.breakTags.put("P", Boolean.TRUE);
        TagNode.breakTags.put("PRE", Boolean.TRUE);
        TagNode.breakTags.put("TD", Boolean.TRUE);
        TagNode.breakTags.put("TH", Boolean.TRUE);
        TagNode.breakTags.put("TITLE", Boolean.TRUE);
        TagNode.breakTags.put("UL", Boolean.TRUE);
    }
}
