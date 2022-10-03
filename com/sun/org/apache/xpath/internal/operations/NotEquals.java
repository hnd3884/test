package com.sun.org.apache.xpath.internal.operations;

import javax.xml.transform.TransformerException;
import com.sun.org.apache.xpath.internal.objects.XBoolean;
import com.sun.org.apache.xpath.internal.objects.XObject;

public class NotEquals extends Operation
{
    static final long serialVersionUID = -7869072863070586900L;
    
    @Override
    public XObject operate(final XObject left, final XObject right) throws TransformerException {
        return left.notEquals(right) ? XBoolean.S_TRUE : XBoolean.S_FALSE;
    }
}
