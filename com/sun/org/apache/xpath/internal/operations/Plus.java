package com.sun.org.apache.xpath.internal.operations;

import com.sun.org.apache.xpath.internal.XPathContext;
import javax.xml.transform.TransformerException;
import com.sun.org.apache.xpath.internal.objects.XNumber;
import com.sun.org.apache.xpath.internal.objects.XObject;

public class Plus extends Operation
{
    static final long serialVersionUID = -4492072861616504256L;
    
    @Override
    public XObject operate(final XObject left, final XObject right) throws TransformerException {
        return new XNumber(left.num() + right.num());
    }
    
    @Override
    public double num(final XPathContext xctxt) throws TransformerException {
        return this.m_right.num(xctxt) + this.m_left.num(xctxt);
    }
}
