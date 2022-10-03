package com.sun.org.apache.xpath.internal.functions;

import javax.xml.transform.TransformerException;
import com.sun.org.apache.xpath.internal.objects.XNumber;
import com.sun.org.apache.xpath.internal.objects.XObject;
import com.sun.org.apache.xpath.internal.XPathContext;

public class FuncFloor extends FunctionOneArg
{
    static final long serialVersionUID = 2326752233236309265L;
    
    @Override
    public XObject execute(final XPathContext xctxt) throws TransformerException {
        return new XNumber(Math.floor(this.m_arg0.execute(xctxt).num()));
    }
}
