package com.sun.org.apache.xpath.internal.functions;

import javax.xml.transform.TransformerException;
import com.sun.org.apache.xpath.internal.objects.XString;
import com.sun.org.apache.xpath.internal.objects.XObject;
import com.sun.org.apache.xpath.internal.XPathContext;

public class FuncGenerateId extends FunctionDef1Arg
{
    static final long serialVersionUID = 973544842091724273L;
    
    @Override
    public XObject execute(final XPathContext xctxt) throws TransformerException {
        final int which = this.getArg0AsNode(xctxt);
        if (-1 != which) {
            return new XString("N" + Integer.toHexString(which).toUpperCase());
        }
        return XString.EMPTYSTRING;
    }
}
