package com.sun.org.apache.xpath.internal;

import javax.xml.transform.SourceLocator;
import javax.xml.transform.ErrorListener;
import com.sun.org.apache.xpath.internal.res.XPATHMessages;
import java.util.Vector;
import org.xml.sax.SAXException;
import org.xml.sax.ContentHandler;
import com.sun.org.apache.xpath.internal.objects.XNodeSet;
import com.sun.org.apache.xml.internal.dtm.DTMIterator;
import com.sun.org.apache.xml.internal.utils.XMLString;
import com.sun.org.apache.xml.internal.dtm.DTM;
import javax.xml.transform.TransformerException;
import com.sun.org.apache.xpath.internal.objects.XObject;
import java.io.Serializable;

public abstract class Expression implements Serializable, ExpressionNode, XPathVisitable
{
    static final long serialVersionUID = 565665869777906902L;
    private ExpressionNode m_parent;
    
    public boolean canTraverseOutsideSubtree() {
        return false;
    }
    
    public XObject execute(final XPathContext xctxt, final int currentNode) throws TransformerException {
        return this.execute(xctxt);
    }
    
    public XObject execute(final XPathContext xctxt, final int currentNode, final DTM dtm, final int expType) throws TransformerException {
        return this.execute(xctxt);
    }
    
    public abstract XObject execute(final XPathContext p0) throws TransformerException;
    
    public XObject execute(final XPathContext xctxt, final boolean destructiveOK) throws TransformerException {
        return this.execute(xctxt);
    }
    
    public double num(final XPathContext xctxt) throws TransformerException {
        return this.execute(xctxt).num();
    }
    
    public boolean bool(final XPathContext xctxt) throws TransformerException {
        return this.execute(xctxt).bool();
    }
    
    public XMLString xstr(final XPathContext xctxt) throws TransformerException {
        return this.execute(xctxt).xstr();
    }
    
    public boolean isNodesetExpr() {
        return false;
    }
    
    public int asNode(final XPathContext xctxt) throws TransformerException {
        final DTMIterator iter = this.execute(xctxt).iter();
        return iter.nextNode();
    }
    
    public DTMIterator asIterator(final XPathContext xctxt, final int contextNode) throws TransformerException {
        try {
            xctxt.pushCurrentNodeAndExpression(contextNode, contextNode);
            return this.execute(xctxt).iter();
        }
        finally {
            xctxt.popCurrentNodeAndExpression();
        }
    }
    
    public DTMIterator asIteratorRaw(final XPathContext xctxt, final int contextNode) throws TransformerException {
        try {
            xctxt.pushCurrentNodeAndExpression(contextNode, contextNode);
            final XNodeSet nodeset = (XNodeSet)this.execute(xctxt);
            return nodeset.iterRaw();
        }
        finally {
            xctxt.popCurrentNodeAndExpression();
        }
    }
    
    public void executeCharsToContentHandler(final XPathContext xctxt, final ContentHandler handler) throws TransformerException, SAXException {
        final XObject obj = this.execute(xctxt);
        obj.dispatchCharactersEvents(handler);
        obj.detach();
    }
    
    public boolean isStableNumber() {
        return false;
    }
    
    public abstract void fixupVariables(final Vector p0, final int p1);
    
    public abstract boolean deepEquals(final Expression p0);
    
    protected final boolean isSameClass(final Expression expr) {
        return null != expr && this.getClass() == expr.getClass();
    }
    
    public void warn(final XPathContext xctxt, final String msg, final Object[] args) throws TransformerException {
        final String fmsg = XPATHMessages.createXPATHWarning(msg, args);
        if (null != xctxt) {
            final ErrorListener eh = xctxt.getErrorListener();
            eh.warning(new TransformerException(fmsg, xctxt.getSAXLocator()));
        }
    }
    
    public void assertion(final boolean b, final String msg) {
        if (!b) {
            final String fMsg = XPATHMessages.createXPATHMessage("ER_INCORRECT_PROGRAMMER_ASSERTION", new Object[] { msg });
            throw new RuntimeException(fMsg);
        }
    }
    
    public void error(final XPathContext xctxt, final String msg, final Object[] args) throws TransformerException {
        final String fmsg = XPATHMessages.createXPATHMessage(msg, args);
        if (null != xctxt) {
            final ErrorListener eh = xctxt.getErrorListener();
            final TransformerException te = new TransformerException(fmsg, this);
            eh.fatalError(te);
        }
    }
    
    public ExpressionNode getExpressionOwner() {
        ExpressionNode parent;
        for (parent = this.exprGetParent(); null != parent && parent instanceof Expression; parent = parent.exprGetParent()) {}
        return parent;
    }
    
    @Override
    public void exprSetParent(final ExpressionNode n) {
        this.assertion(n != this, "Can not parent an expression to itself!");
        this.m_parent = n;
    }
    
    @Override
    public ExpressionNode exprGetParent() {
        return this.m_parent;
    }
    
    @Override
    public void exprAddChild(final ExpressionNode n, final int i) {
        this.assertion(false, "exprAddChild method not implemented!");
    }
    
    @Override
    public ExpressionNode exprGetChild(final int i) {
        return null;
    }
    
    @Override
    public int exprGetNumChildren() {
        return 0;
    }
    
    @Override
    public String getPublicId() {
        if (null == this.m_parent) {
            return null;
        }
        return this.m_parent.getPublicId();
    }
    
    @Override
    public String getSystemId() {
        if (null == this.m_parent) {
            return null;
        }
        return this.m_parent.getSystemId();
    }
    
    @Override
    public int getLineNumber() {
        if (null == this.m_parent) {
            return 0;
        }
        return this.m_parent.getLineNumber();
    }
    
    @Override
    public int getColumnNumber() {
        if (null == this.m_parent) {
            return 0;
        }
        return this.m_parent.getColumnNumber();
    }
}
