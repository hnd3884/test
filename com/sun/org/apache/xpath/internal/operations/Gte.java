package com.sun.org.apache.xpath.internal.operations;

import javax.xml.transform.TransformerException;
import com.sun.org.apache.xpath.internal.objects.XBoolean;
import com.sun.org.apache.xpath.internal.objects.XObject;

public class Gte extends Operation
{
    static final long serialVersionUID = 9142945909906680220L;
    
    @Override
    public XObject operate(final XObject left, final XObject right) throws TransformerException {
        return left.greaterThanOrEqual(right) ? XBoolean.S_TRUE : XBoolean.S_FALSE;
    }
}
