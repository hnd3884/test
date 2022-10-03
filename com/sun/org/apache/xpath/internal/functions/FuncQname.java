package com.sun.org.apache.xpath.internal.functions;

import javax.xml.transform.TransformerException;
import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xpath.internal.objects.XString;
import com.sun.org.apache.xpath.internal.objects.XObject;
import com.sun.org.apache.xpath.internal.XPathContext;

public class FuncQname extends FunctionDef1Arg
{
    static final long serialVersionUID = -1532307875532617380L;
    
    @Override
    public XObject execute(final XPathContext xctxt) throws TransformerException {
        final int context = this.getArg0AsNode(xctxt);
        XObject val;
        if (-1 != context) {
            final DTM dtm = xctxt.getDTM(context);
            final String qname = dtm.getNodeNameX(context);
            val = ((null == qname) ? XString.EMPTYSTRING : new XString(qname));
        }
        else {
            val = XString.EMPTYSTRING;
        }
        return val;
    }
}
