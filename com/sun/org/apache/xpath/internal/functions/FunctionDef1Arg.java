package com.sun.org.apache.xpath.internal.functions;

import com.sun.org.apache.xpath.internal.res.XPATHMessages;
import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xpath.internal.objects.XString;
import com.sun.org.apache.xml.internal.utils.XMLString;
import javax.xml.transform.TransformerException;
import com.sun.org.apache.xpath.internal.XPathContext;

public class FunctionDef1Arg extends FunctionOneArg
{
    static final long serialVersionUID = 2325189412814149264L;
    
    protected int getArg0AsNode(final XPathContext xctxt) throws TransformerException {
        return (null == this.m_arg0) ? xctxt.getCurrentNode() : this.m_arg0.asNode(xctxt);
    }
    
    public boolean Arg0IsNodesetExpr() {
        return null == this.m_arg0 || this.m_arg0.isNodesetExpr();
    }
    
    protected XMLString getArg0AsString(final XPathContext xctxt) throws TransformerException {
        if (null != this.m_arg0) {
            return this.m_arg0.execute(xctxt).xstr();
        }
        final int currentNode = xctxt.getCurrentNode();
        if (-1 == currentNode) {
            return XString.EMPTYSTRING;
        }
        final DTM dtm = xctxt.getDTM(currentNode);
        return dtm.getStringValue(currentNode);
    }
    
    protected double getArg0AsNumber(final XPathContext xctxt) throws TransformerException {
        if (null != this.m_arg0) {
            return this.m_arg0.execute(xctxt).num();
        }
        final int currentNode = xctxt.getCurrentNode();
        if (-1 == currentNode) {
            return 0.0;
        }
        final DTM dtm = xctxt.getDTM(currentNode);
        final XMLString str = dtm.getStringValue(currentNode);
        return str.toDouble();
    }
    
    @Override
    public void checkNumberArgs(final int argNum) throws WrongNumberArgsException {
        if (argNum > 1) {
            this.reportWrongNumberArgs();
        }
    }
    
    @Override
    protected void reportWrongNumberArgs() throws WrongNumberArgsException {
        throw new WrongNumberArgsException(XPATHMessages.createXPATHMessage("ER_ZERO_OR_ONE", null));
    }
    
    @Override
    public boolean canTraverseOutsideSubtree() {
        return null != this.m_arg0 && super.canTraverseOutsideSubtree();
    }
}
