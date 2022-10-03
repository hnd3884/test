package com.sun.org.apache.xml.internal.security.utils;

public abstract class XPathFactory
{
    private static boolean xalanInstalled;
    
    protected static synchronized boolean isXalanInstalled() {
        return XPathFactory.xalanInstalled;
    }
    
    public static XPathFactory newInstance() {
        if (!isXalanInstalled()) {
            return new JDKXPathFactory();
        }
        if (XalanXPathAPI.isInstalled()) {
            return new XalanXPathFactory();
        }
        return new JDKXPathFactory();
    }
    
    public abstract XPathAPI newXPathAPI();
    
    static {
        try {
            if (ClassLoaderUtils.loadClass("com.sun.org.apache.xpath.internal.compiler.FunctionTable", XPathFactory.class) != null) {
                XPathFactory.xalanInstalled = true;
            }
        }
        catch (final Exception ex) {}
    }
}
