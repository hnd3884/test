package com.sun.org.apache.xpath.internal.functions;

import javax.xml.transform.TransformerException;
import com.sun.org.apache.xml.internal.utils.XMLString;
import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xml.internal.dtm.DTMIterator;
import com.sun.org.apache.xpath.internal.objects.XNumber;
import com.sun.org.apache.xpath.internal.objects.XObject;
import com.sun.org.apache.xpath.internal.XPathContext;

public class FuncSum extends FunctionOneArg
{
    static final long serialVersionUID = -2719049259574677519L;
    
    @Override
    public XObject execute(final XPathContext xctxt) throws TransformerException {
        final DTMIterator nodes = this.m_arg0.asIterator(xctxt, xctxt.getCurrentNode());
        double sum = 0.0;
        int pos;
        while (-1 != (pos = nodes.nextNode())) {
            final DTM dtm = nodes.getDTM(pos);
            final XMLString s = dtm.getStringValue(pos);
            if (null != s) {
                sum += s.toDouble();
            }
        }
        nodes.detach();
        return new XNumber(sum);
    }
}
