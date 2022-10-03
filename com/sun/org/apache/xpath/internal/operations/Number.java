package com.sun.org.apache.xpath.internal.operations;

import com.sun.org.apache.xpath.internal.XPathContext;
import javax.xml.transform.TransformerException;
import com.sun.org.apache.xpath.internal.objects.XNumber;
import com.sun.org.apache.xpath.internal.objects.XObject;

public class Number extends UnaryOperation
{
    static final long serialVersionUID = 7196954482871619765L;
    
    @Override
    public XObject operate(final XObject right) throws TransformerException {
        if (2 == right.getType()) {
            return right;
        }
        return new XNumber(right.num());
    }
    
    @Override
    public double num(final XPathContext xctxt) throws TransformerException {
        return this.m_right.num(xctxt);
    }
}
