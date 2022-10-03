package com.sun.org.apache.xpath.internal.operations;

import javax.xml.transform.TransformerException;
import com.sun.org.apache.xml.internal.dtm.DTMManager;
import com.sun.org.apache.xpath.internal.Expression;
import com.sun.org.apache.xpath.internal.objects.XNodeSet;
import com.sun.org.apache.xpath.internal.objects.XObject;
import com.sun.org.apache.xpath.internal.XPathContext;

public class VariableSafeAbsRef extends Variable
{
    static final long serialVersionUID = -9174661990819967452L;
    
    @Override
    public XObject execute(final XPathContext xctxt, final boolean destructiveOK) throws TransformerException {
        XNodeSet xns = (XNodeSet)super.execute(xctxt, destructiveOK);
        final DTMManager dtmMgr = xctxt.getDTMManager();
        final int context = xctxt.getContextNode();
        if (dtmMgr.getDTM(xns.getRoot()).getDocument() != dtmMgr.getDTM(context).getDocument()) {
            final Expression expr = (Expression)xns.getContainedIter();
            xns = (XNodeSet)expr.asIterator(xctxt, context);
        }
        return xns;
    }
}
