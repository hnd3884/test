package com.sun.org.apache.xpath.internal.functions;

import javax.xml.transform.TransformerException;
import com.sun.org.apache.xpath.internal.objects.XString;
import com.sun.org.apache.xpath.internal.objects.XObject;
import com.sun.org.apache.xpath.internal.XPathContext;

public class FuncString extends FunctionDef1Arg
{
    static final long serialVersionUID = -2206677149497712883L;
    
    @Override
    public XObject execute(final XPathContext xctxt) throws TransformerException {
        return (XString)this.getArg0AsString(xctxt);
    }
}
