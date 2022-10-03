package org.htmlparser.tags;

import org.htmlparser.Text;
import org.htmlparser.visitors.NodeVisitor;
import org.htmlparser.nodes.AbstractNode;
import org.htmlparser.NodeFilter;
import org.htmlparser.filters.NodeClassFilter;
import java.util.Locale;
import org.htmlparser.Node;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.SimpleNodeIterator;
import org.htmlparser.scanners.Scanner;
import org.htmlparser.scanners.CompositeTagScanner;
import org.htmlparser.Tag;
import org.htmlparser.nodes.TagNode;

public class CompositeTag extends TagNode
{
    protected Tag mEndTag;
    protected static final CompositeTagScanner mDefaultCompositeScanner;
    
    public CompositeTag() {
        this.setThisScanner(CompositeTag.mDefaultCompositeScanner);
    }
    
    public SimpleNodeIterator children() {
        SimpleNodeIterator ret;
        if (null != this.getChildren()) {
            ret = this.getChildren().elements();
        }
        else {
            ret = new NodeList().elements();
        }
        return ret;
    }
    
    public Node getChild(final int index) {
        return (null == this.getChildren()) ? null : this.getChildren().elementAt(index);
    }
    
    public Node[] getChildrenAsNodeArray() {
        return (null == this.getChildren()) ? new Node[0] : this.getChildren().toNodeArray();
    }
    
    public void removeChild(final int i) {
        if (null != this.getChildren()) {
            this.getChildren().remove(i);
        }
    }
    
    public SimpleNodeIterator elements() {
        return (null == this.getChildren()) ? new NodeList().elements() : this.getChildren().elements();
    }
    
    public String toPlainTextString() {
        final StringBuffer stringRepresentation = new StringBuffer();
        final SimpleNodeIterator e = this.children();
        while (e.hasMoreNodes()) {
            stringRepresentation.append(e.nextNode().toPlainTextString());
        }
        return stringRepresentation.toString();
    }
    
    protected void putChildrenInto(final StringBuffer sb, final boolean verbatim) {
        final SimpleNodeIterator e = this.children();
        while (e.hasMoreNodes()) {
            final Node node = e.nextNode();
            if (!verbatim || node.getStartPosition() != node.getEndPosition()) {
                sb.append(node.toHtml());
            }
        }
    }
    
    protected void putEndTagInto(final StringBuffer sb, final boolean verbatim) {
        if (!verbatim || this.mEndTag.getStartPosition() != this.mEndTag.getEndPosition()) {
            sb.append(this.getEndTag().toHtml());
        }
    }
    
    public String toHtml(final boolean verbatim) {
        final StringBuffer ret = new StringBuffer();
        ret.append(super.toHtml(verbatim));
        if (!this.isEmptyXmlTag()) {
            this.putChildrenInto(ret, verbatim);
            if (null != this.getEndTag()) {
                this.putEndTagInto(ret, verbatim);
            }
        }
        return ret.toString();
    }
    
    public Tag searchByName(final String name) {
        Tag tag = null;
        boolean found = false;
        final SimpleNodeIterator e = this.children();
        while (e.hasMoreNodes() && !found) {
            final Node node = e.nextNode();
            if (node instanceof Tag) {
                tag = (Tag)node;
                final String nameAttribute = tag.getAttribute("NAME");
                if (nameAttribute == null || !nameAttribute.equals(name)) {
                    continue;
                }
                found = true;
            }
        }
        if (found) {
            return tag;
        }
        return null;
    }
    
    public NodeList searchFor(final String searchString) {
        return this.searchFor(searchString, false);
    }
    
    public NodeList searchFor(final String searchString, final boolean caseSensitive) {
        return this.searchFor(searchString, caseSensitive, Locale.ENGLISH);
    }
    
    public NodeList searchFor(String searchString, final boolean caseSensitive, final Locale locale) {
        final NodeList ret = new NodeList();
        if (!caseSensitive) {
            searchString = searchString.toUpperCase(locale);
        }
        final SimpleNodeIterator e = this.children();
        while (e.hasMoreNodes()) {
            final Node node = e.nextNode();
            String text = node.toPlainTextString();
            if (!caseSensitive) {
                text = text.toUpperCase(locale);
            }
            if (-1 != text.indexOf(searchString)) {
                ret.add(node);
            }
        }
        return ret;
    }
    
    public NodeList searchFor(final Class classType, final boolean recursive) {
        final NodeList children = this.getChildren();
        NodeList ret;
        if (null == children) {
            ret = new NodeList();
        }
        else {
            ret = children.extractAllNodesThatMatch(new NodeClassFilter(classType), recursive);
        }
        return ret;
    }
    
    public int findPositionOf(final String text) {
        return this.findPositionOf(text, Locale.ENGLISH);
    }
    
    public int findPositionOf(String text, final Locale locale) {
        int loc = 0;
        text = text.toUpperCase(locale);
        final SimpleNodeIterator e = this.children();
        while (e.hasMoreNodes()) {
            final Node node = e.nextNode();
            if (-1 != node.toPlainTextString().toUpperCase(locale).indexOf(text)) {
                return loc;
            }
            ++loc;
        }
        return -1;
    }
    
    public int findPositionOf(final Node searchNode) {
        int loc = 0;
        final SimpleNodeIterator e = this.children();
        while (e.hasMoreNodes()) {
            final Node node = e.nextNode();
            if (node == searchNode) {
                return loc;
            }
            ++loc;
        }
        return -1;
    }
    
    public Node childAt(final int index) {
        return (null == this.getChildren()) ? null : this.getChildren().elementAt(index);
    }
    
    public void collectInto(final NodeList list, final NodeFilter filter) {
        super.collectInto(list, filter);
        final SimpleNodeIterator e = this.children();
        while (e.hasMoreNodes()) {
            e.nextNode().collectInto(list, filter);
        }
        if (null != this.getEndTag() && this != this.getEndTag()) {
            this.getEndTag().collectInto(list, filter);
        }
    }
    
    public String getChildrenHTML() {
        final StringBuffer buff = new StringBuffer();
        final SimpleNodeIterator e = this.children();
        while (e.hasMoreNodes()) {
            final AbstractNode node = (AbstractNode)e.nextNode();
            buff.append(node.toHtml());
        }
        return buff.toString();
    }
    
    public void accept(final NodeVisitor visitor) {
        if (visitor.shouldRecurseSelf()) {
            visitor.visitTag(this);
        }
        if (visitor.shouldRecurseChildren()) {
            if (null != this.getChildren()) {
                final SimpleNodeIterator children = this.children();
                while (children.hasMoreNodes()) {
                    final Node child = children.nextNode();
                    child.accept(visitor);
                }
            }
            if (null != this.getEndTag() && this != this.getEndTag()) {
                this.getEndTag().accept(visitor);
            }
        }
    }
    
    public int getChildCount() {
        final NodeList children = this.getChildren();
        return (null == children) ? 0 : children.size();
    }
    
    public Tag getEndTag() {
        return this.mEndTag;
    }
    
    public void setEndTag(final Tag tag) {
        this.mEndTag = tag;
    }
    
    public Text[] digupStringNode(final String searchText) {
        final NodeList nodeList = this.searchFor(searchText);
        final NodeList stringNodes = new NodeList();
        for (int i = 0; i < nodeList.size(); ++i) {
            final Node node = nodeList.elementAt(i);
            if (node instanceof Text) {
                stringNodes.add(node);
            }
            else if (node instanceof CompositeTag) {
                final CompositeTag ctag = (CompositeTag)node;
                final Text[] nodes = ctag.digupStringNode(searchText);
                for (int j = 0; j < nodes.length; ++j) {
                    stringNodes.add(nodes[j]);
                }
            }
        }
        final Text[] stringNode = new Text[stringNodes.size()];
        for (int k = 0; k < stringNode.length; ++k) {
            stringNode[k] = (Text)stringNodes.elementAt(k);
        }
        return stringNode;
    }
    
    public String toString() {
        final StringBuffer ret = new StringBuffer(1024);
        this.toString(0, ret);
        return ret.toString();
    }
    
    public String getText() {
        String ret = super.toHtml(true);
        ret = ret.substring(1, ret.length() - 1);
        return ret;
    }
    
    public String getStringText() {
        final int start = this.getEndPosition();
        final int end = this.mEndTag.getStartPosition();
        final String ret = this.getPage().getText(start, end);
        return ret;
    }
    
    public void toString(final int level, final StringBuffer buffer) {
        for (int i = 0; i < level; ++i) {
            buffer.append("  ");
        }
        buffer.append(super.toString());
        buffer.append(System.getProperty("line.separator"));
        final SimpleNodeIterator e = this.children();
        while (e.hasMoreNodes()) {
            final Node node = e.nextNode();
            if (node instanceof CompositeTag) {
                ((CompositeTag)node).toString(level + 1, buffer);
            }
            else {
                for (int j = 0; j <= level; ++j) {
                    buffer.append("  ");
                }
                buffer.append(node);
                buffer.append(System.getProperty("line.separator"));
            }
        }
        if (null != this.getEndTag() && this != this.getEndTag()) {
            for (int i = 0; i <= level; ++i) {
                buffer.append("  ");
            }
            buffer.append(this.getEndTag().toString());
            buffer.append(System.getProperty("line.separator"));
        }
    }
    
    static {
        mDefaultCompositeScanner = new CompositeTagScanner();
    }
}
