package com.sun.org.apache.xerces.internal.xni.parser;

import java.util.Locale;
import com.sun.org.apache.xerces.internal.xni.XMLDTDContentModelHandler;
import com.sun.org.apache.xerces.internal.xni.XMLDTDHandler;
import com.sun.org.apache.xerces.internal.xni.XMLDocumentHandler;
import java.io.IOException;
import com.sun.org.apache.xerces.internal.xni.XNIException;

public interface XMLParserConfiguration extends XMLComponentManager
{
    void parse(final XMLInputSource p0) throws XNIException, IOException;
    
    void addRecognizedFeatures(final String[] p0);
    
    void setFeature(final String p0, final boolean p1) throws XMLConfigurationException;
    
    boolean getFeature(final String p0) throws XMLConfigurationException;
    
    void addRecognizedProperties(final String[] p0);
    
    void setProperty(final String p0, final Object p1) throws XMLConfigurationException;
    
    Object getProperty(final String p0) throws XMLConfigurationException;
    
    void setErrorHandler(final XMLErrorHandler p0);
    
    XMLErrorHandler getErrorHandler();
    
    void setDocumentHandler(final XMLDocumentHandler p0);
    
    XMLDocumentHandler getDocumentHandler();
    
    void setDTDHandler(final XMLDTDHandler p0);
    
    XMLDTDHandler getDTDHandler();
    
    void setDTDContentModelHandler(final XMLDTDContentModelHandler p0);
    
    XMLDTDContentModelHandler getDTDContentModelHandler();
    
    void setEntityResolver(final XMLEntityResolver p0);
    
    XMLEntityResolver getEntityResolver();
    
    void setLocale(final Locale p0) throws XNIException;
    
    Locale getLocale();
}
