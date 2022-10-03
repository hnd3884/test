package com.sun.org.apache.xpath.internal.functions;

import java.util.Vector;
import javax.xml.transform.TransformerException;
import com.sun.org.apache.xpath.internal.objects.XNumber;
import com.sun.org.apache.xpath.internal.objects.XObject;
import com.sun.org.apache.xml.internal.dtm.DTMIterator;
import com.sun.org.apache.xpath.internal.axes.SubContextList;
import com.sun.org.apache.xml.internal.utils.WrappedRuntimeException;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.compiler.Compiler;

public class FuncPosition extends Function
{
    static final long serialVersionUID = -9092846348197271582L;
    private boolean m_isTopLevel;
    
    @Override
    public void postCompileStep(final Compiler compiler) {
        this.m_isTopLevel = (compiler.getLocationPathDepth() == -1);
    }
    
    public int getPositionInContextNodeList(final XPathContext xctxt) {
        final SubContextList iter = this.m_isTopLevel ? null : xctxt.getSubContextList();
        if (null != iter) {
            final int prox = iter.getProximityPosition(xctxt);
            return prox;
        }
        DTMIterator cnl = xctxt.getContextNodeList();
        if (null != cnl) {
            int n = cnl.getCurrentNode();
            if (n == -1) {
                if (cnl.getCurrentPos() == 0) {
                    return 0;
                }
                try {
                    cnl = cnl.cloneWithReset();
                }
                catch (final CloneNotSupportedException cnse) {
                    throw new WrappedRuntimeException(cnse);
                }
                final int currentNode = xctxt.getContextNode();
                while (-1 != (n = cnl.nextNode()) && n != currentNode) {}
            }
            return cnl.getCurrentPos();
        }
        return -1;
    }
    
    @Override
    public XObject execute(final XPathContext xctxt) throws TransformerException {
        final double pos = this.getPositionInContextNodeList(xctxt);
        return new XNumber(pos);
    }
    
    @Override
    public void fixupVariables(final Vector vars, final int globalsSize) {
    }
}
