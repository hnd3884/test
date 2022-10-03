package com.sun.org.apache.xpath.internal.functions;

import javax.xml.transform.TransformerException;
import com.sun.org.apache.xml.internal.utils.XMLString;
import com.sun.org.apache.xpath.internal.objects.XString;
import com.sun.org.apache.xpath.internal.objects.XObject;
import com.sun.org.apache.xpath.internal.XPathContext;

public class FuncSubstringAfter extends Function2Args
{
    static final long serialVersionUID = -8119731889862512194L;
    
    @Override
    public XObject execute(final XPathContext xctxt) throws TransformerException {
        final XMLString s1 = this.m_arg0.execute(xctxt).xstr();
        final XMLString s2 = this.m_arg1.execute(xctxt).xstr();
        final int index = s1.indexOf(s2);
        return (XObject)((-1 == index) ? XString.EMPTYSTRING : s1.substring(index + s2.length()));
    }
}
