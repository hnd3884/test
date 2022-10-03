package com.sun.org.apache.xpath.internal.functions;

import javax.xml.transform.TransformerException;
import com.sun.org.apache.xpath.internal.objects.XBoolean;
import com.sun.org.apache.xpath.internal.objects.XObject;
import com.sun.org.apache.xpath.internal.XPathContext;

public class FuncStartsWith extends Function2Args
{
    static final long serialVersionUID = 2194585774699567928L;
    
    @Override
    public XObject execute(final XPathContext xctxt) throws TransformerException {
        return this.m_arg0.execute(xctxt).xstr().startsWith(this.m_arg1.execute(xctxt).xstr()) ? XBoolean.S_TRUE : XBoolean.S_FALSE;
    }
}
