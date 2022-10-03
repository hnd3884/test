package com.sun.org.apache.xpath.internal.operations;

import javax.xml.transform.TransformerException;
import com.sun.org.apache.xpath.internal.objects.XBoolean;
import com.sun.org.apache.xpath.internal.objects.XObject;
import com.sun.org.apache.xpath.internal.XPathContext;

public class And extends Operation
{
    static final long serialVersionUID = 392330077126534022L;
    
    @Override
    public XObject execute(final XPathContext xctxt) throws TransformerException {
        final XObject expr1 = this.m_left.execute(xctxt);
        if (expr1.bool()) {
            final XObject expr2 = this.m_right.execute(xctxt);
            return expr2.bool() ? XBoolean.S_TRUE : XBoolean.S_FALSE;
        }
        return XBoolean.S_FALSE;
    }
    
    @Override
    public boolean bool(final XPathContext xctxt) throws TransformerException {
        return this.m_left.bool(xctxt) && this.m_right.bool(xctxt);
    }
}
