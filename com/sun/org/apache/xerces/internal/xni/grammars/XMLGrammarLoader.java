package com.sun.org.apache.xerces.internal.xni.grammars;

import com.sun.org.apache.xerces.internal.xni.XNIException;
import java.io.IOException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import com.sun.org.apache.xerces.internal.xni.parser.XMLEntityResolver;
import com.sun.org.apache.xerces.internal.xni.parser.XMLErrorHandler;
import java.util.Locale;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;

public interface XMLGrammarLoader
{
    String[] getRecognizedFeatures();
    
    boolean getFeature(final String p0) throws XMLConfigurationException;
    
    void setFeature(final String p0, final boolean p1) throws XMLConfigurationException;
    
    String[] getRecognizedProperties();
    
    Object getProperty(final String p0) throws XMLConfigurationException;
    
    void setProperty(final String p0, final Object p1) throws XMLConfigurationException;
    
    void setLocale(final Locale p0);
    
    Locale getLocale();
    
    void setErrorHandler(final XMLErrorHandler p0);
    
    XMLErrorHandler getErrorHandler();
    
    void setEntityResolver(final XMLEntityResolver p0);
    
    XMLEntityResolver getEntityResolver();
    
    Grammar loadGrammar(final XMLInputSource p0) throws IOException, XNIException;
}
