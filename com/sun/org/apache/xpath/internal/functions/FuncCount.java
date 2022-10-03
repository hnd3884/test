package com.sun.org.apache.xpath.internal.functions;

import javax.xml.transform.TransformerException;
import com.sun.org.apache.xml.internal.dtm.DTMIterator;
import com.sun.org.apache.xpath.internal.objects.XNumber;
import com.sun.org.apache.xpath.internal.objects.XObject;
import com.sun.org.apache.xpath.internal.XPathContext;

public class FuncCount extends FunctionOneArg
{
    static final long serialVersionUID = -7116225100474153751L;
    
    @Override
    public XObject execute(final XPathContext xctxt) throws TransformerException {
        final DTMIterator nl = this.m_arg0.asIterator(xctxt, xctxt.getCurrentNode());
        final int i = nl.getLength();
        nl.detach();
        return new XNumber(i);
    }
}
