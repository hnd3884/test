package org.apache.axiom.om;

public interface OMXMLParserWrapper
{
    int next() throws OMException;
    
    @Deprecated
    void discard(final OMElement p0) throws OMException;
    
    void setCache(final boolean p0) throws OMException;
    
    boolean isCache();
    
    Object getParser();
    
    boolean isCompleted();
    
    OMDocument getDocument();
    
    OMElement getDocumentElement();
    
    OMElement getDocumentElement(final boolean p0);
    
    @Deprecated
    short getBuilderType();
    
    @Deprecated
    void registerExternalContentHandler(final Object p0);
    
    @Deprecated
    Object getRegisteredContentHandler();
    
    String getCharacterEncoding();
    
    void close();
    
    void detach();
}
