package com.sun.org.apache.xml.internal.utils;

public class XMLStringFactoryDefault extends XMLStringFactory
{
    private static final XMLStringDefault EMPTY_STR;
    
    @Override
    public XMLString newstr(final String string) {
        return new XMLStringDefault(string);
    }
    
    @Override
    public XMLString newstr(final FastStringBuffer fsb, final int start, final int length) {
        return new XMLStringDefault(fsb.getString(start, length));
    }
    
    @Override
    public XMLString newstr(final char[] string, final int start, final int length) {
        return new XMLStringDefault(new String(string, start, length));
    }
    
    @Override
    public XMLString emptystr() {
        return XMLStringFactoryDefault.EMPTY_STR;
    }
    
    static {
        EMPTY_STR = new XMLStringDefault("");
    }
}
