package org.apache.tika.sax;

import org.apache.tika.metadata.TikaCoreProperties;
import org.xml.sax.SAXException;
import org.apache.tika.metadata.Metadata;
import java.nio.charset.Charset;
import java.io.OutputStream;
import org.xml.sax.ContentHandler;
import org.apache.tika.metadata.Property;
import java.io.Serializable;
import org.xml.sax.helpers.DefaultHandler;

public abstract class AbstractRecursiveParserWrapperHandler extends DefaultHandler implements Serializable
{
    public static final Property EMBEDDED_RESOURCE_LIMIT_REACHED;
    private static final int MAX_DEPTH = 100;
    private final ContentHandlerFactory contentHandlerFactory;
    private final int maxEmbeddedResources;
    private int embeddedResources;
    private int embeddedDepth;
    
    public AbstractRecursiveParserWrapperHandler(final ContentHandlerFactory contentHandlerFactory) {
        this(contentHandlerFactory, -1);
    }
    
    public AbstractRecursiveParserWrapperHandler(final ContentHandlerFactory contentHandlerFactory, final int maxEmbeddedResources) {
        this.embeddedResources = 0;
        this.embeddedDepth = 0;
        this.contentHandlerFactory = contentHandlerFactory;
        this.maxEmbeddedResources = maxEmbeddedResources;
    }
    
    public ContentHandler getNewContentHandler() {
        return this.contentHandlerFactory.getNewContentHandler();
    }
    
    public ContentHandler getNewContentHandler(final OutputStream os, final Charset charset) {
        return this.contentHandlerFactory.getNewContentHandler(os, charset);
    }
    
    public void startEmbeddedDocument(final ContentHandler contentHandler, final Metadata metadata) throws SAXException {
        ++this.embeddedResources;
        ++this.embeddedDepth;
        if (this.embeddedDepth >= 100) {
            throw new SAXException("Max embedded depth reached: " + this.embeddedDepth);
        }
        metadata.set(TikaCoreProperties.EMBEDDED_DEPTH, this.embeddedDepth);
    }
    
    public void endEmbeddedDocument(final ContentHandler contentHandler, final Metadata metadata) throws SAXException {
        --this.embeddedDepth;
    }
    
    public void endDocument(final ContentHandler contentHandler, final Metadata metadata) throws SAXException {
        if (this.hasHitMaximumEmbeddedResources()) {
            metadata.set(AbstractRecursiveParserWrapperHandler.EMBEDDED_RESOURCE_LIMIT_REACHED, "true");
        }
        metadata.set(TikaCoreProperties.EMBEDDED_DEPTH, 0);
    }
    
    public boolean hasHitMaximumEmbeddedResources() {
        return this.maxEmbeddedResources > -1 && this.embeddedResources >= this.maxEmbeddedResources;
    }
    
    public ContentHandlerFactory getContentHandlerFactory() {
        return this.contentHandlerFactory;
    }
    
    static {
        EMBEDDED_RESOURCE_LIMIT_REACHED = Property.internalBoolean("X-TIKA:EXCEPTION:embedded_resource_limit_reached");
    }
}
