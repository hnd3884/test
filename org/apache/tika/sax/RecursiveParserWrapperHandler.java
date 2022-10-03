package org.apache.tika.sax;

import org.apache.tika.metadata.TikaCoreProperties;
import org.xml.sax.helpers.DefaultHandler;
import org.apache.tika.utils.ParserUtils;
import org.apache.tika.exception.TikaException;
import org.xml.sax.SAXException;
import org.xml.sax.ContentHandler;
import java.util.LinkedList;
import org.apache.tika.metadata.filter.NoOpFilter;
import org.apache.tika.metadata.filter.MetadataFilter;
import org.apache.tika.metadata.Metadata;
import java.util.List;

public class RecursiveParserWrapperHandler extends AbstractRecursiveParserWrapperHandler
{
    protected final List<Metadata> metadataList;
    private final MetadataFilter metadataFilter;
    
    public RecursiveParserWrapperHandler(final ContentHandlerFactory contentHandlerFactory) {
        this(contentHandlerFactory, -1, NoOpFilter.NOOP_FILTER);
    }
    
    public RecursiveParserWrapperHandler(final ContentHandlerFactory contentHandlerFactory, final int maxEmbeddedResources) {
        this(contentHandlerFactory, maxEmbeddedResources, NoOpFilter.NOOP_FILTER);
    }
    
    public RecursiveParserWrapperHandler(final ContentHandlerFactory contentHandlerFactory, final int maxEmbeddedResources, final MetadataFilter metadataFilter) {
        super(contentHandlerFactory, maxEmbeddedResources);
        this.metadataList = new LinkedList<Metadata>();
        this.metadataFilter = metadataFilter;
    }
    
    @Override
    public void startEmbeddedDocument(final ContentHandler contentHandler, final Metadata metadata) throws SAXException {
        super.startEmbeddedDocument(contentHandler, metadata);
    }
    
    @Override
    public void endEmbeddedDocument(final ContentHandler contentHandler, final Metadata metadata) throws SAXException {
        super.endEmbeddedDocument(contentHandler, metadata);
        this.addContent(contentHandler, metadata);
        try {
            this.metadataFilter.filter(metadata);
        }
        catch (final TikaException e) {
            throw new SAXException(e);
        }
        if (metadata.size() > 0) {
            this.metadataList.add(ParserUtils.cloneMetadata(metadata));
        }
    }
    
    @Override
    public void endDocument(final ContentHandler contentHandler, final Metadata metadata) throws SAXException {
        super.endDocument(contentHandler, metadata);
        this.addContent(contentHandler, metadata);
        try {
            this.metadataFilter.filter(metadata);
        }
        catch (final TikaException e) {
            throw new SAXException(e);
        }
        if (metadata.size() > 0) {
            this.metadataList.add(0, ParserUtils.cloneMetadata(metadata));
        }
    }
    
    public List<Metadata> getMetadataList() {
        return this.metadataList;
    }
    
    void addContent(final ContentHandler handler, final Metadata metadata) {
        if (!handler.getClass().equals(DefaultHandler.class)) {
            final String content = handler.toString();
            if (content != null && content.trim().length() > 0) {
                metadata.add(TikaCoreProperties.TIKA_CONTENT, content);
                metadata.add(TikaCoreProperties.TIKA_CONTENT_HANDLER, handler.getClass().getSimpleName());
            }
        }
    }
}
