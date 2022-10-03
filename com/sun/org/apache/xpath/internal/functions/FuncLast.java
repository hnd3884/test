package com.sun.org.apache.xpath.internal.functions;

import java.util.Vector;
import com.sun.org.apache.xpath.internal.objects.XNumber;
import com.sun.org.apache.xpath.internal.objects.XObject;
import javax.xml.transform.TransformerException;
import com.sun.org.apache.xml.internal.dtm.DTMIterator;
import com.sun.org.apache.xpath.internal.axes.SubContextList;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.compiler.Compiler;

public class FuncLast extends Function
{
    static final long serialVersionUID = 9205812403085432943L;
    private boolean m_isTopLevel;
    
    @Override
    public void postCompileStep(final Compiler compiler) {
        this.m_isTopLevel = (compiler.getLocationPathDepth() == -1);
    }
    
    public int getCountOfContextNodeList(final XPathContext xctxt) throws TransformerException {
        final SubContextList iter = this.m_isTopLevel ? null : xctxt.getSubContextList();
        if (null != iter) {
            return iter.getLastPos(xctxt);
        }
        final DTMIterator cnl = xctxt.getContextNodeList();
        int count;
        if (null != cnl) {
            count = cnl.getLength();
        }
        else {
            count = 0;
        }
        return count;
    }
    
    @Override
    public XObject execute(final XPathContext xctxt) throws TransformerException {
        final XNumber xnum = new XNumber(this.getCountOfContextNodeList(xctxt));
        return xnum;
    }
    
    @Override
    public void fixupVariables(final Vector vars, final int globalsSize) {
    }
}
