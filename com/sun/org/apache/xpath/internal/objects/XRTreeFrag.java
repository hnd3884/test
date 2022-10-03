package com.sun.org.apache.xpath.internal.objects;

import com.sun.org.apache.xml.internal.utils.WrappedRuntimeException;
import com.sun.org.apache.xml.internal.dtm.ref.DTMNodeList;
import org.w3c.dom.NodeList;
import com.sun.org.apache.xpath.internal.axes.RTFIterator;
import com.sun.org.apache.xml.internal.utils.FastStringBuffer;
import javax.xml.transform.TransformerException;
import com.sun.org.apache.xpath.internal.Expression;
import com.sun.org.apache.xml.internal.dtm.DTMIterator;
import com.sun.org.apache.xml.internal.dtm.ref.DTMNodeIterator;
import com.sun.org.apache.xpath.internal.NodeSetDTM;
import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xpath.internal.ExpressionNode;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xml.internal.utils.XMLString;

public class XRTreeFrag extends XObject implements Cloneable
{
    static final long serialVersionUID = -3201553822254911567L;
    private DTMXRTreeFrag m_DTMXRTreeFrag;
    private int m_dtmRoot;
    protected boolean m_allowRelease;
    private XMLString m_xmlStr;
    
    public XRTreeFrag(final int root, final XPathContext xctxt, final ExpressionNode parent) {
        super(null);
        this.m_dtmRoot = -1;
        this.m_allowRelease = false;
        this.m_xmlStr = null;
        this.exprSetParent(parent);
        this.initDTM(root, xctxt);
    }
    
    public XRTreeFrag(final int root, final XPathContext xctxt) {
        super(null);
        this.m_dtmRoot = -1;
        this.m_allowRelease = false;
        this.m_xmlStr = null;
        this.initDTM(root, xctxt);
    }
    
    private final void initDTM(final int root, final XPathContext xctxt) {
        this.m_dtmRoot = root;
        final DTM dtm = xctxt.getDTM(root);
        if (dtm != null) {
            this.m_DTMXRTreeFrag = xctxt.getDTMXRTreeFrag(xctxt.getDTMIdentity(dtm));
        }
    }
    
    @Override
    public Object object() {
        if (this.m_DTMXRTreeFrag.getXPathContext() != null) {
            return new DTMNodeIterator(new NodeSetDTM(this.m_dtmRoot, this.m_DTMXRTreeFrag.getXPathContext().getDTMManager()));
        }
        return super.object();
    }
    
    public XRTreeFrag(final Expression expr) {
        super(expr);
        this.m_dtmRoot = -1;
        this.m_allowRelease = false;
        this.m_xmlStr = null;
    }
    
    @Override
    public void allowDetachToRelease(final boolean allowRelease) {
        this.m_allowRelease = allowRelease;
    }
    
    @Override
    public void detach() {
        if (this.m_allowRelease) {
            this.m_DTMXRTreeFrag.destruct();
            this.setObject(null);
        }
    }
    
    @Override
    public int getType() {
        return 5;
    }
    
    @Override
    public String getTypeString() {
        return "#RTREEFRAG";
    }
    
    @Override
    public double num() throws TransformerException {
        final XMLString s = this.xstr();
        return s.toDouble();
    }
    
    @Override
    public boolean bool() {
        return true;
    }
    
    @Override
    public XMLString xstr() {
        if (null == this.m_xmlStr) {
            this.m_xmlStr = this.m_DTMXRTreeFrag.getDTM().getStringValue(this.m_dtmRoot);
        }
        return this.m_xmlStr;
    }
    
    @Override
    public void appendToFsb(final FastStringBuffer fsb) {
        final XString xstring = (XString)this.xstr();
        xstring.appendToFsb(fsb);
    }
    
    @Override
    public String str() {
        final String str = this.m_DTMXRTreeFrag.getDTM().getStringValue(this.m_dtmRoot).toString();
        return (null == str) ? "" : str;
    }
    
    @Override
    public int rtf() {
        return this.m_dtmRoot;
    }
    
    public DTMIterator asNodeIterator() {
        return new RTFIterator(this.m_dtmRoot, this.m_DTMXRTreeFrag.getXPathContext().getDTMManager());
    }
    
    public NodeList convertToNodeset() {
        if (this.m_obj instanceof NodeList) {
            return (NodeList)this.m_obj;
        }
        return new DTMNodeList(this.asNodeIterator());
    }
    
    @Override
    public boolean equals(final XObject obj2) {
        try {
            if (4 == obj2.getType()) {
                return obj2.equals(this);
            }
            if (1 == obj2.getType()) {
                return this.bool() == obj2.bool();
            }
            if (2 == obj2.getType()) {
                return this.num() == obj2.num();
            }
            if (4 == obj2.getType()) {
                return this.xstr().equals(obj2.xstr());
            }
            if (3 == obj2.getType()) {
                return this.xstr().equals(obj2.xstr());
            }
            if (5 == obj2.getType()) {
                return this.xstr().equals(obj2.xstr());
            }
            return super.equals(obj2);
        }
        catch (final TransformerException te) {
            throw new WrappedRuntimeException(te);
        }
    }
}
