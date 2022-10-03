package org.apache.axiom.om;

public interface OMDocument extends OMContainer
{
    public static final String XML_10 = "1.0";
    public static final String XML_11 = "1.1";
    
    OMElement getOMDocumentElement();
    
    void setOMDocumentElement(final OMElement p0);
    
    String getCharsetEncoding();
    
    void setCharsetEncoding(final String p0);
    
    String getXMLVersion();
    
    void setXMLVersion(final String p0);
    
    String getXMLEncoding();
    
    void setXMLEncoding(final String p0);
    
    String isStandalone();
    
    void setStandalone(final String p0);
}
