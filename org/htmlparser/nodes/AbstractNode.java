package org.htmlparser.nodes;

import org.htmlparser.util.ParserException;
import org.htmlparser.visitors.NodeVisitor;
import org.htmlparser.NodeFilter;
import org.htmlparser.util.NodeList;
import org.htmlparser.lexer.Page;
import java.io.Serializable;
import org.htmlparser.Node;

public abstract class AbstractNode implements Node, Serializable
{
    protected Page mPage;
    protected int nodeBegin;
    protected int nodeEnd;
    protected Node parent;
    protected NodeList children;
    
    public AbstractNode(final Page page, final int start, final int end) {
        this.mPage = page;
        this.nodeBegin = start;
        this.nodeEnd = end;
        this.parent = null;
        this.children = null;
    }
    
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
    
    public abstract String toPlainTextString();
    
    public String toHtml() {
        return this.toHtml(false);
    }
    
    public abstract String toHtml(final boolean p0);
    
    public abstract String toString();
    
    public void collectInto(final NodeList list, final NodeFilter filter) {
        if (filter.accept(this)) {
            list.add(this);
        }
    }
    
    public Page getPage() {
        return this.mPage;
    }
    
    public void setPage(final Page page) {
        this.mPage = page;
    }
    
    public int getStartPosition() {
        return this.nodeBegin;
    }
    
    public void setStartPosition(final int position) {
        this.nodeBegin = position;
    }
    
    public int getEndPosition() {
        return this.nodeEnd;
    }
    
    public void setEndPosition(final int position) {
        this.nodeEnd = position;
    }
    
    public abstract void accept(final NodeVisitor p0);
    
    public Node getParent() {
        return this.parent;
    }
    
    public void setParent(final Node node) {
        this.parent = node;
    }
    
    public NodeList getChildren() {
        return this.children;
    }
    
    public void setChildren(final NodeList children) {
        this.children = children;
    }
    
    public Node getFirstChild() {
        if (this.children == null) {
            return null;
        }
        if (this.children.size() == 0) {
            return null;
        }
        return this.children.elementAt(0);
    }
    
    public Node getLastChild() {
        if (this.children == null) {
            return null;
        }
        final int numChildren = this.children.size();
        if (numChildren == 0) {
            return null;
        }
        return this.children.elementAt(numChildren - 1);
    }
    
    public Node getPreviousSibling() {
        final Node parentNode = this.getParent();
        if (parentNode == null) {
            return null;
        }
        final NodeList siblings = parentNode.getChildren();
        if (siblings == null) {
            return null;
        }
        final int numSiblings = siblings.size();
        if (numSiblings < 2) {
            return null;
        }
        int positionInParent = -1;
        for (int i = 0; i < numSiblings; ++i) {
            if (siblings.elementAt(i) == this) {
                positionInParent = i;
                break;
            }
        }
        if (positionInParent < 1) {
            return null;
        }
        return siblings.elementAt(positionInParent - 1);
    }
    
    public Node getNextSibling() {
        final Node parentNode = this.getParent();
        if (parentNode == null) {
            return null;
        }
        final NodeList siblings = parentNode.getChildren();
        if (siblings == null) {
            return null;
        }
        final int numSiblings = siblings.size();
        if (numSiblings < 2) {
            return null;
        }
        int positionInParent = -1;
        for (int i = 0; i < numSiblings; ++i) {
            if (siblings.elementAt(i) == this) {
                positionInParent = i;
                break;
            }
        }
        if (positionInParent == -1) {
            return null;
        }
        if (positionInParent == numSiblings - 1) {
            return null;
        }
        return siblings.elementAt(positionInParent + 1);
    }
    
    public String getText() {
        return null;
    }
    
    public void setText(final String text) {
    }
    
    public void doSemanticAction() throws ParserException {
    }
}
