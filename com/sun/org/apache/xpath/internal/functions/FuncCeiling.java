package com.sun.org.apache.xpath.internal.functions;

import javax.xml.transform.TransformerException;
import com.sun.org.apache.xpath.internal.objects.XNumber;
import com.sun.org.apache.xpath.internal.objects.XObject;
import com.sun.org.apache.xpath.internal.XPathContext;

public class FuncCeiling extends FunctionOneArg
{
    static final long serialVersionUID = -1275988936390464739L;
    
    @Override
    public XObject execute(final XPathContext xctxt) throws TransformerException {
        return new XNumber(Math.ceil(this.m_arg0.execute(xctxt).num()));
    }
}
