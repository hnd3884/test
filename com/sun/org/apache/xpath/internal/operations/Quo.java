package com.sun.org.apache.xpath.internal.operations;

import javax.xml.transform.TransformerException;
import com.sun.org.apache.xpath.internal.objects.XNumber;
import com.sun.org.apache.xpath.internal.objects.XObject;

public class Quo extends Operation
{
    static final long serialVersionUID = 693765299196169905L;
    
    @Override
    public XObject operate(final XObject left, final XObject right) throws TransformerException {
        return new XNumber((int)(left.num() / right.num()));
    }
}
