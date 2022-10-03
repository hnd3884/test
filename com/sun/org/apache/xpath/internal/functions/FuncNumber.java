package com.sun.org.apache.xpath.internal.functions;

import javax.xml.transform.TransformerException;
import com.sun.org.apache.xpath.internal.objects.XNumber;
import com.sun.org.apache.xpath.internal.objects.XObject;
import com.sun.org.apache.xpath.internal.XPathContext;

public class FuncNumber extends FunctionDef1Arg
{
    static final long serialVersionUID = 7266745342264153076L;
    
    @Override
    public XObject execute(final XPathContext xctxt) throws TransformerException {
        return new XNumber(this.getArg0AsNumber(xctxt));
    }
}
