package org.htmlparser;

import org.htmlparser.util.ParserException;
import org.htmlparser.visitors.NodeVisitor;
import org.htmlparser.lexer.Page;
import org.htmlparser.util.NodeList;

public interface Node extends Cloneable
{
    String toPlainTextString();
    
    String toHtml();
    
    String toHtml(final boolean p0);
    
    String toString();
    
    void collectInto(final NodeList p0, final NodeFilter p1);
    
    int getStartPosition();
    
    void setStartPosition(final int p0);
    
    int getEndPosition();
    
    void setEndPosition(final int p0);
    
    Page getPage();
    
    void setPage(final Page p0);
    
    void accept(final NodeVisitor p0);
    
    Node getParent();
    
    void setParent(final Node p0);
    
    NodeList getChildren();
    
    void setChildren(final NodeList p0);
    
    Node getFirstChild();
    
    Node getLastChild();
    
    Node getPreviousSibling();
    
    Node getNextSibling();
    
    String getText();
    
    void setText(final String p0);
    
    void doSemanticAction() throws ParserException;
    
    Object clone() throws CloneNotSupportedException;
}
