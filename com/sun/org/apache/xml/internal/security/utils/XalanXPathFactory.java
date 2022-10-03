package com.sun.org.apache.xml.internal.security.utils;

public class XalanXPathFactory extends XPathFactory
{
    @Override
    public XPathAPI newXPathAPI() {
        return new XalanXPathAPI();
    }
}
