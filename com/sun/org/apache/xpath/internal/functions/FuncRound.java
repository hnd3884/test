package com.sun.org.apache.xpath.internal.functions;

import javax.xml.transform.TransformerException;
import com.sun.org.apache.xpath.internal.objects.XNumber;
import com.sun.org.apache.xpath.internal.objects.XObject;
import com.sun.org.apache.xpath.internal.XPathContext;

public class FuncRound extends FunctionOneArg
{
    static final long serialVersionUID = -7970583902573826611L;
    
    @Override
    public XObject execute(final XPathContext xctxt) throws TransformerException {
        final XObject obj = this.m_arg0.execute(xctxt);
        final double val = obj.num();
        if (val >= -0.5 && val < 0.0) {
            return new XNumber(-0.0);
        }
        if (val == 0.0) {
            return new XNumber(val);
        }
        return new XNumber(Math.floor(val + 0.5));
    }
}
