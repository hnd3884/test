package com.sun.org.apache.xpath.internal.functions;

import javax.xml.transform.TransformerException;
import com.sun.org.apache.xpath.internal.objects.XBoolean;
import com.sun.org.apache.xpath.internal.objects.XObject;
import com.sun.org.apache.xpath.internal.XPathContext;

public class FuncNot extends FunctionOneArg
{
    static final long serialVersionUID = 7299699961076329790L;
    
    @Override
    public XObject execute(final XPathContext xctxt) throws TransformerException {
        return this.m_arg0.execute(xctxt).bool() ? XBoolean.S_FALSE : XBoolean.S_TRUE;
    }
}
