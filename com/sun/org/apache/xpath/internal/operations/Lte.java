package com.sun.org.apache.xpath.internal.operations;

import javax.xml.transform.TransformerException;
import com.sun.org.apache.xpath.internal.objects.XBoolean;
import com.sun.org.apache.xpath.internal.objects.XObject;

public class Lte extends Operation
{
    static final long serialVersionUID = 6945650810527140228L;
    
    @Override
    public XObject operate(final XObject left, final XObject right) throws TransformerException {
        return left.lessThanOrEqual(right) ? XBoolean.S_TRUE : XBoolean.S_FALSE;
    }
}
