package com.sun.org.apache.xpath.internal.operations;

import com.sun.org.apache.xpath.internal.XPathVisitor;
import com.sun.org.apache.xpath.internal.ExpressionOwner;
import com.sun.org.apache.xml.internal.utils.PrefixResolver;
import com.sun.org.apache.xpath.internal.objects.XNodeSet;
import com.sun.org.apache.xpath.internal.objects.XObject;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xml.internal.utils.WrappedRuntimeException;
import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;
import com.sun.org.apache.xpath.internal.res.XPATHMessages;
import java.util.Vector;
import com.sun.org.apache.xml.internal.utils.QName;
import com.sun.org.apache.xpath.internal.axes.PathComponent;
import com.sun.org.apache.xpath.internal.Expression;

public class Variable extends Expression implements PathComponent
{
    static final long serialVersionUID = -4334975375609297049L;
    private boolean m_fixUpWasCalled;
    protected QName m_qname;
    protected int m_index;
    protected boolean m_isGlobal;
    static final String PSUEDOVARNAMESPACE = "http://xml.apache.org/xalan/psuedovar";
    
    public Variable() {
        this.m_fixUpWasCalled = false;
        this.m_isGlobal = false;
    }
    
    public void setIndex(final int index) {
        this.m_index = index;
    }
    
    public int getIndex() {
        return this.m_index;
    }
    
    public void setIsGlobal(final boolean isGlobal) {
        this.m_isGlobal = isGlobal;
    }
    
    public boolean getGlobal() {
        return this.m_isGlobal;
    }
    
    @Override
    public void fixupVariables(final Vector vars, final int globalsSize) {
        this.m_fixUpWasCalled = true;
        final int sz = vars.size();
        for (int i = vars.size() - 1; i >= 0; --i) {
            final QName qn = vars.elementAt(i);
            if (qn.equals(this.m_qname)) {
                if (i < globalsSize) {
                    this.m_isGlobal = true;
                    this.m_index = i;
                }
                else {
                    this.m_index = i - globalsSize;
                }
                return;
            }
        }
        final String msg = XPATHMessages.createXPATHMessage("ER_COULD_NOT_FIND_VAR", new Object[] { this.m_qname.toString() });
        final TransformerException te = new TransformerException(msg, this);
        throw new WrappedRuntimeException(te);
    }
    
    public void setQName(final QName qname) {
        this.m_qname = qname;
    }
    
    public QName getQName() {
        return this.m_qname;
    }
    
    @Override
    public XObject execute(final XPathContext xctxt) throws TransformerException {
        return this.execute(xctxt, false);
    }
    
    @Override
    public XObject execute(final XPathContext xctxt, final boolean destructiveOK) throws TransformerException {
        final PrefixResolver xprefixResolver = xctxt.getNamespaceContext();
        XObject result;
        if (this.m_fixUpWasCalled) {
            if (this.m_isGlobal) {
                result = xctxt.getVarStack().getGlobalVariable(xctxt, this.m_index, destructiveOK);
            }
            else {
                result = xctxt.getVarStack().getLocalVariable(xctxt, this.m_index, destructiveOK);
            }
        }
        else {
            result = xctxt.getVarStack().getVariableOrParam(xctxt, this.m_qname);
        }
        if (null == result) {
            this.warn(xctxt, "WG_ILLEGAL_VARIABLE_REFERENCE", new Object[] { this.m_qname.getLocalPart() });
            result = new XNodeSet(xctxt.getDTMManager());
        }
        return result;
    }
    
    @Override
    public boolean isStableNumber() {
        return true;
    }
    
    @Override
    public int getAnalysisBits() {
        return 67108864;
    }
    
    @Override
    public void callVisitors(final ExpressionOwner owner, final XPathVisitor visitor) {
        visitor.visitVariableRef(owner, this);
    }
    
    @Override
    public boolean deepEquals(final Expression expr) {
        return this.isSameClass(expr) && this.m_qname.equals(((Variable)expr).m_qname);
    }
    
    public boolean isPsuedoVarRef() {
        final String ns = this.m_qname.getNamespaceURI();
        return null != ns && ns.equals("http://xml.apache.org/xalan/psuedovar") && this.m_qname.getLocalName().startsWith("#");
    }
}
