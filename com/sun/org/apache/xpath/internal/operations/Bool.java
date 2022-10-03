package com.sun.org.apache.xpath.internal.operations;

import com.sun.org.apache.xpath.internal.XPathContext;
import javax.xml.transform.TransformerException;
import com.sun.org.apache.xpath.internal.objects.XBoolean;
import com.sun.org.apache.xpath.internal.objects.XObject;

public class Bool extends UnaryOperation
{
    static final long serialVersionUID = 44705375321914635L;
    
    @Override
    public XObject operate(final XObject right) throws TransformerException {
        if (1 == right.getType()) {
            return right;
        }
        return right.bool() ? XBoolean.S_TRUE : XBoolean.S_FALSE;
    }
    
    @Override
    public boolean bool(final XPathContext xctxt) throws TransformerException {
        return this.m_right.bool(xctxt);
    }
}
