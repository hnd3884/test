package com.sun.org.apache.xpath.internal.functions;

import javax.xml.transform.TransformerException;
import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xpath.internal.objects.XString;
import com.sun.org.apache.xpath.internal.objects.XObject;
import com.sun.org.apache.xpath.internal.XPathContext;

public class FuncUnparsedEntityURI extends FunctionOneArg
{
    static final long serialVersionUID = 845309759097448178L;
    
    @Override
    public XObject execute(final XPathContext xctxt) throws TransformerException {
        final String name = this.m_arg0.execute(xctxt).str();
        final int context = xctxt.getCurrentNode();
        final DTM dtm = xctxt.getDTM(context);
        final int doc = dtm.getDocument();
        final String uri = dtm.getUnparsedEntityURI(name);
        return new XString(uri);
    }
}
