package com.sun.org.apache.xerces.internal.parsers;

import java.io.IOException;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityPropertyManager;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLParserConfiguration;

public abstract class XMLParser
{
    protected static final String ENTITY_RESOLVER = "http://apache.org/xml/properties/internal/entity-resolver";
    protected static final String ERROR_HANDLER = "http://apache.org/xml/properties/internal/error-handler";
    private static final String[] RECOGNIZED_PROPERTIES;
    protected XMLParserConfiguration fConfiguration;
    XMLSecurityManager securityManager;
    XMLSecurityPropertyManager securityPropertyManager;
    
    public boolean getFeature(final String featureId) throws SAXNotSupportedException, SAXNotRecognizedException {
        return this.fConfiguration.getFeature(featureId);
    }
    
    protected XMLParser(final XMLParserConfiguration config) {
        (this.fConfiguration = config).addRecognizedProperties(XMLParser.RECOGNIZED_PROPERTIES);
    }
    
    public void parse(final XMLInputSource inputSource) throws XNIException, IOException {
        if (this.securityManager == null) {
            this.securityManager = new XMLSecurityManager(true);
            this.fConfiguration.setProperty("http://apache.org/xml/properties/security-manager", this.securityManager);
        }
        if (this.securityPropertyManager == null) {
            this.securityPropertyManager = new XMLSecurityPropertyManager();
            this.fConfiguration.setProperty("http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager", this.securityPropertyManager);
        }
        this.reset();
        this.fConfiguration.parse(inputSource);
    }
    
    protected void reset() throws XNIException {
    }
    
    static {
        RECOGNIZED_PROPERTIES = new String[] { "http://apache.org/xml/properties/internal/entity-resolver", "http://apache.org/xml/properties/internal/error-handler" };
    }
}
