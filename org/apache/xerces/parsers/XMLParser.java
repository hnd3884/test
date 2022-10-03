package org.apache.xerces.parsers;

import java.io.IOException;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.parser.XMLInputSource;
import org.apache.xerces.xni.parser.XMLParserConfiguration;

public abstract class XMLParser
{
    protected static final String ENTITY_RESOLVER = "http://apache.org/xml/properties/internal/entity-resolver";
    protected static final String ERROR_HANDLER = "http://apache.org/xml/properties/internal/error-handler";
    private static final String[] RECOGNIZED_PROPERTIES;
    protected final XMLParserConfiguration fConfiguration;
    
    protected XMLParser(final XMLParserConfiguration fConfiguration) {
        (this.fConfiguration = fConfiguration).addRecognizedProperties(XMLParser.RECOGNIZED_PROPERTIES);
    }
    
    public void parse(final XMLInputSource xmlInputSource) throws XNIException, IOException {
        this.reset();
        this.fConfiguration.parse(xmlInputSource);
    }
    
    protected void reset() throws XNIException {
    }
    
    static {
        RECOGNIZED_PROPERTIES = new String[] { "http://apache.org/xml/properties/internal/entity-resolver", "http://apache.org/xml/properties/internal/error-handler" };
    }
}
