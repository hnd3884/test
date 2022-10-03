package com.sun.org.apache.xpath.internal.functions;

import javax.xml.transform.TransformerException;
import com.sun.org.apache.xpath.internal.objects.XBoolean;
import com.sun.org.apache.xpath.internal.objects.XObject;
import com.sun.org.apache.xpath.internal.XPathContext;

public class FuncContains extends Function2Args
{
    static final long serialVersionUID = 5084753781887919723L;
    
    @Override
    public XObject execute(final XPathContext xctxt) throws TransformerException {
        final String s1 = this.m_arg0.execute(xctxt).str();
        final String s2 = this.m_arg1.execute(xctxt).str();
        if (s1.length() == 0 && s2.length() == 0) {
            return XBoolean.S_TRUE;
        }
        final int index = s1.indexOf(s2);
        return (index > -1) ? XBoolean.S_TRUE : XBoolean.S_FALSE;
    }
}
