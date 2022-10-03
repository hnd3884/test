package org.apache.axiom.util.sax;

import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.ErrorHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.DTDHandler;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.XMLReader;

public abstract class AbstractXMLReader implements XMLReader
{
    private static final String URI_NAMESPACES = "http://xml.org/sax/features/namespaces";
    private static final String URI_NAMESPACE_PREFIXES = "http://xml.org/sax/features/namespace-prefixes";
    private static final String URI_EXTERNAL_GENERAL_ENTITIES = "http://xml.org/sax/features/external-general-entities";
    private static final String URI_LEXICAL_HANDLER = "http://xml.org/sax/properties/lexical-handler";
    protected boolean namespaces;
    protected boolean namespacePrefixes;
    protected boolean externalGeneralEntities;
    protected ContentHandler contentHandler;
    protected LexicalHandler lexicalHandler;
    protected DTDHandler dtdHandler;
    protected EntityResolver entityResolver;
    protected ErrorHandler errorHandler;
    
    public AbstractXMLReader() {
        this.namespaces = true;
        this.namespacePrefixes = false;
        this.externalGeneralEntities = true;
    }
    
    public ContentHandler getContentHandler() {
        return this.contentHandler;
    }
    
    public void setContentHandler(final ContentHandler contentHandler) {
        this.contentHandler = contentHandler;
    }
    
    public DTDHandler getDTDHandler() {
        return this.dtdHandler;
    }
    
    public void setDTDHandler(final DTDHandler dtdHandler) {
        this.dtdHandler = dtdHandler;
    }
    
    public EntityResolver getEntityResolver() {
        return this.entityResolver;
    }
    
    public void setEntityResolver(final EntityResolver entityResolver) {
        this.entityResolver = entityResolver;
    }
    
    public ErrorHandler getErrorHandler() {
        return this.errorHandler;
    }
    
    public void setErrorHandler(final ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }
    
    public boolean getFeature(final String name) throws SAXNotRecognizedException, SAXNotSupportedException {
        if ("http://xml.org/sax/features/namespaces".equals(name)) {
            return this.namespaces;
        }
        if ("http://xml.org/sax/features/namespace-prefixes".equals(name)) {
            return this.namespacePrefixes;
        }
        if ("http://xml.org/sax/features/external-general-entities".equals(name)) {
            return this.externalGeneralEntities;
        }
        throw new SAXNotRecognizedException(name);
    }
    
    public void setFeature(final String name, final boolean value) throws SAXNotRecognizedException, SAXNotSupportedException {
        if ("http://xml.org/sax/features/namespaces".equals(name)) {
            this.namespaces = value;
        }
        else if ("http://xml.org/sax/features/namespace-prefixes".equals(name)) {
            this.namespacePrefixes = value;
        }
        else {
            if (!"http://xml.org/sax/features/external-general-entities".equals(name)) {
                throw new SAXNotRecognizedException(name);
            }
            this.externalGeneralEntities = value;
        }
    }
    
    public Object getProperty(final String name) throws SAXNotRecognizedException, SAXNotSupportedException {
        if ("http://xml.org/sax/properties/lexical-handler".equals(name)) {
            return this.lexicalHandler;
        }
        throw new SAXNotRecognizedException(name);
    }
    
    public void setProperty(final String name, final Object value) throws SAXNotRecognizedException, SAXNotSupportedException {
        if ("http://xml.org/sax/properties/lexical-handler".equals(name)) {
            this.lexicalHandler = (LexicalHandler)value;
            return;
        }
        throw new SAXNotRecognizedException(name);
    }
}
