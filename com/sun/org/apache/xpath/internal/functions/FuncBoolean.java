package com.sun.org.apache.xpath.internal.functions;

import javax.xml.transform.TransformerException;
import com.sun.org.apache.xpath.internal.objects.XBoolean;
import com.sun.org.apache.xpath.internal.objects.XObject;
import com.sun.org.apache.xpath.internal.XPathContext;

public class FuncBoolean extends FunctionOneArg
{
    static final long serialVersionUID = 4328660760070034592L;
    
    @Override
    public XObject execute(final XPathContext xctxt) throws TransformerException {
        return this.m_arg0.execute(xctxt).bool() ? XBoolean.S_TRUE : XBoolean.S_FALSE;
    }
}
