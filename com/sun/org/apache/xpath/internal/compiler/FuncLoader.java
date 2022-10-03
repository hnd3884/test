package com.sun.org.apache.xpath.internal.compiler;

import com.sun.org.apache.xalan.internal.utils.ConfigurationError;
import com.sun.org.apache.xalan.internal.utils.ObjectFactory;
import javax.xml.transform.TransformerException;
import com.sun.org.apache.xpath.internal.functions.Function;

public class FuncLoader
{
    private int m_funcID;
    private String m_funcName;
    
    public String getName() {
        return this.m_funcName;
    }
    
    public FuncLoader(final String funcName, final int funcID) {
        this.m_funcID = funcID;
        this.m_funcName = funcName;
    }
    
    Function getFunction() throws TransformerException {
        try {
            String className = this.m_funcName;
            if (className.indexOf(".") < 0) {
                className = "com.sun.org.apache.xpath.internal.functions." + className;
            }
            final String subString = className.substring(0, className.lastIndexOf(46));
            if (!subString.equals("com.sun.org.apache.xalan.internal.templates") && !subString.equals("com.sun.org.apache.xpath.internal.functions")) {
                throw new TransformerException("Application can't install his own xpath function.");
            }
            return (Function)ObjectFactory.newInstance(className, true);
        }
        catch (final ConfigurationError e) {
            throw new TransformerException(e.getException());
        }
    }
}
