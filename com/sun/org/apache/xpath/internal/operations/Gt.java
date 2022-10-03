package com.sun.org.apache.xpath.internal.operations;

import javax.xml.transform.TransformerException;
import com.sun.org.apache.xpath.internal.objects.XBoolean;
import com.sun.org.apache.xpath.internal.objects.XObject;

public class Gt extends Operation
{
    static final long serialVersionUID = 8927078751014375950L;
    
    @Override
    public XObject operate(final XObject left, final XObject right) throws TransformerException {
        return left.greaterThan(right) ? XBoolean.S_TRUE : XBoolean.S_FALSE;
    }
}
