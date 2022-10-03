package com.sun.org.apache.xpath.internal.objects;

import com.sun.org.apache.xml.internal.utils.FastStringBuffer;
import com.sun.org.apache.xml.internal.utils.XMLString;
import com.sun.org.apache.xml.internal.utils.XMLStringFactory;

public class XMLStringFactoryImpl extends XMLStringFactory
{
    private static XMLStringFactory m_xstringfactory;
    
    public static XMLStringFactory getFactory() {
        return XMLStringFactoryImpl.m_xstringfactory;
    }
    
    @Override
    public XMLString newstr(final String string) {
        return new XString(string);
    }
    
    @Override
    public XMLString newstr(final FastStringBuffer fsb, final int start, final int length) {
        return new XStringForFSB(fsb, start, length);
    }
    
    @Override
    public XMLString newstr(final char[] string, final int start, final int length) {
        return new XStringForChars(string, start, length);
    }
    
    @Override
    public XMLString emptystr() {
        return XString.EMPTYSTRING;
    }
    
    static {
        XMLStringFactoryImpl.m_xstringfactory = new XMLStringFactoryImpl();
    }
}
