package com.sun.org.apache.xpath.internal.operations;

import com.sun.org.apache.xpath.internal.XPathContext;
import javax.xml.transform.TransformerException;
import com.sun.org.apache.xpath.internal.objects.XBoolean;
import com.sun.org.apache.xpath.internal.objects.XObject;

public class Equals extends Operation
{
    static final long serialVersionUID = -2658315633903426134L;
    
    @Override
    public XObject operate(final XObject left, final XObject right) throws TransformerException {
        return left.equals(right) ? XBoolean.S_TRUE : XBoolean.S_FALSE;
    }
    
    @Override
    public boolean bool(final XPathContext xctxt) throws TransformerException {
        final XObject left = this.m_left.execute(xctxt, true);
        final XObject right = this.m_right.execute(xctxt, true);
        final boolean result = left.equals(right);
        left.detach();
        right.detach();
        return result;
    }
}
