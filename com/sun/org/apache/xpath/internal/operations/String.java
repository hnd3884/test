package com.sun.org.apache.xpath.internal.operations;

import javax.xml.transform.TransformerException;
import com.sun.org.apache.xpath.internal.objects.XString;
import com.sun.org.apache.xpath.internal.objects.XObject;

public class String extends UnaryOperation
{
    static final long serialVersionUID = 2973374377453022888L;
    
    @Override
    public XObject operate(final XObject right) throws TransformerException {
        return (XString)right.xstr();
    }
}
