package com.sun.org.apache.xpath.internal.objects;

import com.sun.org.apache.xpath.internal.XPathVisitor;
import com.sun.org.apache.xpath.internal.ExpressionOwner;
import com.sun.org.apache.xml.internal.utils.FastStringBuffer;
import java.util.Vector;
import com.sun.org.apache.xpath.internal.ExpressionNode;
import com.sun.org.apache.xpath.internal.XPathException;
import com.sun.org.apache.xpath.internal.res.XPATHMessages;
import com.sun.org.apache.xpath.internal.NodeSetDTM;
import org.w3c.dom.NodeList;
import org.w3c.dom.traversal.NodeIterator;
import com.sun.org.apache.xml.internal.dtm.DTMIterator;
import org.w3c.dom.DocumentFragment;
import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xml.internal.utils.XMLString;
import org.xml.sax.SAXException;
import org.xml.sax.ContentHandler;
import javax.xml.transform.TransformerException;
import com.sun.org.apache.xpath.internal.XPathContext;
import java.io.Serializable;
import com.sun.org.apache.xpath.internal.Expression;

public class XObject extends Expression implements Serializable, Cloneable
{
    static final long serialVersionUID = -821887098985662951L;
    protected Object m_obj;
    public static final int CLASS_NULL = -1;
    public static final int CLASS_UNKNOWN = 0;
    public static final int CLASS_BOOLEAN = 1;
    public static final int CLASS_NUMBER = 2;
    public static final int CLASS_STRING = 3;
    public static final int CLASS_NODESET = 4;
    public static final int CLASS_RTREEFRAG = 5;
    public static final int CLASS_UNRESOLVEDVARIABLE = 600;
    
    public XObject() {
    }
    
    public XObject(final Object obj) {
        this.setObject(obj);
    }
    
    protected void setObject(final Object obj) {
        this.m_obj = obj;
    }
    
    @Override
    public XObject execute(final XPathContext xctxt) throws TransformerException {
        return this;
    }
    
    public void allowDetachToRelease(final boolean allowRelease) {
    }
    
    public void detach() {
    }
    
    public void destruct() {
        if (null != this.m_obj) {
            this.allowDetachToRelease(true);
            this.detach();
            this.setObject(null);
        }
    }
    
    public void reset() {
    }
    
    public void dispatchCharactersEvents(final ContentHandler ch) throws SAXException {
        this.xstr().dispatchCharactersEvents(ch);
    }
    
    public static XObject create(final Object val) {
        return XObjectFactory.create(val);
    }
    
    public static XObject create(final Object val, final XPathContext xctxt) {
        return XObjectFactory.create(val, xctxt);
    }
    
    public int getType() {
        return 0;
    }
    
    public String getTypeString() {
        return "#UNKNOWN (" + this.object().getClass().getName() + ")";
    }
    
    public double num() throws TransformerException {
        this.error("ER_CANT_CONVERT_TO_NUMBER", new Object[] { this.getTypeString() });
        return 0.0;
    }
    
    public double numWithSideEffects() throws TransformerException {
        return this.num();
    }
    
    public boolean bool() throws TransformerException {
        this.error("ER_CANT_CONVERT_TO_NUMBER", new Object[] { this.getTypeString() });
        return false;
    }
    
    public boolean boolWithSideEffects() throws TransformerException {
        return this.bool();
    }
    
    public XMLString xstr() {
        return XMLStringFactoryImpl.getFactory().newstr(this.str());
    }
    
    public String str() {
        return (this.m_obj != null) ? this.m_obj.toString() : "";
    }
    
    @Override
    public String toString() {
        return this.str();
    }
    
    public int rtf(final XPathContext support) {
        int result = this.rtf();
        if (-1 == result) {
            final DTM frag = support.createDocumentFragment();
            frag.appendTextChild(this.str());
            result = frag.getDocument();
        }
        return result;
    }
    
    public DocumentFragment rtree(final XPathContext support) {
        DocumentFragment docFrag = null;
        final int result = this.rtf();
        if (-1 == result) {
            final DTM frag = support.createDocumentFragment();
            frag.appendTextChild(this.str());
            docFrag = (DocumentFragment)frag.getNode(frag.getDocument());
        }
        else {
            final DTM frag = support.getDTM(result);
            docFrag = (DocumentFragment)frag.getNode(frag.getDocument());
        }
        return docFrag;
    }
    
    public DocumentFragment rtree() {
        return null;
    }
    
    public int rtf() {
        return -1;
    }
    
    public Object object() {
        return this.m_obj;
    }
    
    public DTMIterator iter() throws TransformerException {
        this.error("ER_CANT_CONVERT_TO_NODELIST", new Object[] { this.getTypeString() });
        return null;
    }
    
    public XObject getFresh() {
        return this;
    }
    
    public NodeIterator nodeset() throws TransformerException {
        this.error("ER_CANT_CONVERT_TO_NODELIST", new Object[] { this.getTypeString() });
        return null;
    }
    
    public NodeList nodelist() throws TransformerException {
        this.error("ER_CANT_CONVERT_TO_NODELIST", new Object[] { this.getTypeString() });
        return null;
    }
    
    public NodeSetDTM mutableNodeset() throws TransformerException {
        this.error("ER_CANT_CONVERT_TO_MUTABLENODELIST", new Object[] { this.getTypeString() });
        return (NodeSetDTM)this.m_obj;
    }
    
    public Object castToType(final int t, final XPathContext support) throws TransformerException {
        Object result = null;
        switch (t) {
            case 3: {
                result = this.str();
                break;
            }
            case 2: {
                result = new Double(this.num());
                break;
            }
            case 4: {
                result = this.iter();
                break;
            }
            case 1: {
                result = new Boolean(this.bool());
                break;
            }
            case 0: {
                result = this.m_obj;
                break;
            }
            default: {
                this.error("ER_CANT_CONVERT_TO_TYPE", new Object[] { this.getTypeString(), Integer.toString(t) });
                result = null;
                break;
            }
        }
        return result;
    }
    
    public boolean lessThan(final XObject obj2) throws TransformerException {
        if (obj2.getType() == 4) {
            return obj2.greaterThan(this);
        }
        return this.num() < obj2.num();
    }
    
    public boolean lessThanOrEqual(final XObject obj2) throws TransformerException {
        if (obj2.getType() == 4) {
            return obj2.greaterThanOrEqual(this);
        }
        return this.num() <= obj2.num();
    }
    
    public boolean greaterThan(final XObject obj2) throws TransformerException {
        if (obj2.getType() == 4) {
            return obj2.lessThan(this);
        }
        return this.num() > obj2.num();
    }
    
    public boolean greaterThanOrEqual(final XObject obj2) throws TransformerException {
        if (obj2.getType() == 4) {
            return obj2.lessThanOrEqual(this);
        }
        return this.num() >= obj2.num();
    }
    
    public boolean equals(final XObject obj2) {
        if (obj2.getType() == 4) {
            return obj2.equals(this);
        }
        if (null != this.m_obj) {
            return this.m_obj.equals(obj2.m_obj);
        }
        return obj2.m_obj == null;
    }
    
    public boolean notEquals(final XObject obj2) throws TransformerException {
        if (obj2.getType() == 4) {
            return obj2.notEquals(this);
        }
        return !this.equals(obj2);
    }
    
    protected void error(final String msg) throws TransformerException {
        this.error(msg, null);
    }
    
    protected void error(final String msg, final Object[] args) throws TransformerException {
        final String fmsg = XPATHMessages.createXPATHMessage(msg, args);
        throw new XPathException(fmsg, this);
    }
    
    @Override
    public void fixupVariables(final Vector vars, final int globalsSize) {
    }
    
    public void appendToFsb(final FastStringBuffer fsb) {
        fsb.append(this.str());
    }
    
    @Override
    public void callVisitors(final ExpressionOwner owner, final XPathVisitor visitor) {
        this.assertion(false, "callVisitors should not be called for this object!!!");
    }
    
    @Override
    public boolean deepEquals(final Expression expr) {
        return this.isSameClass(expr) && this.equals((XObject)expr);
    }
}
