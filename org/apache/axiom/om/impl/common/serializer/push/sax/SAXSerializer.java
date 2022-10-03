package org.apache.axiom.om.impl.common.serializer.push.sax;

import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.XMLStreamException;
import org.apache.axiom.om.OMDataSource;
import org.apache.axiom.ext.stax.datahandler.DataHandlerProvider;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import org.apache.axiom.util.base64.Base64EncodingWriterOutputStream;
import javax.activation.DataHandler;
import org.xml.sax.SAXException;
import org.apache.axiom.om.impl.common.serializer.push.OutputException;
import org.apache.axiom.om.OMSerializable;
import org.apache.axiom.util.namespace.ScopedNamespaceContext;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.ContentHandler;
import org.apache.axiom.om.impl.common.serializer.push.Serializer;

public class SAXSerializer extends Serializer
{
    private final ContentHandler contentHandler;
    private final LexicalHandler lexicalHandler;
    private final ScopedNamespaceContext nsContext;
    private boolean startDocumentWritten;
    private boolean autoStartDocument;
    private int depth;
    private final SAXHelper helper;
    
    public SAXSerializer(final OMSerializable root, final ContentHandler contentHandler, final LexicalHandler lexicalHandler) {
        super(root, false, true);
        this.nsContext = new ScopedNamespaceContext();
        this.helper = new SAXHelper();
        this.contentHandler = contentHandler;
        this.lexicalHandler = lexicalHandler;
    }
    
    @Override
    protected boolean isAssociated(final String prefix, final String namespace) throws OutputException {
        return this.nsContext.getNamespaceURI(prefix).equals(namespace);
    }
    
    private void writeStartDocument() throws OutputException {
        try {
            this.contentHandler.startDocument();
            this.startDocumentWritten = true;
        }
        catch (final SAXException ex) {
            throw new SAXOutputException(ex);
        }
    }
    
    @Override
    public void writeStartDocument(final String version) throws OutputException {
        this.writeStartDocument();
    }
    
    @Override
    public void writeStartDocument(final String encoding, final String version) throws OutputException {
        this.writeStartDocument();
    }
    
    @Override
    public void writeDTD(final String rootName, final String publicId, final String systemId, final String internalSubset) throws OutputException {
        if (this.lexicalHandler != null) {
            try {
                this.lexicalHandler.startDTD(rootName, publicId, systemId);
                this.lexicalHandler.endDTD();
            }
            catch (final SAXException ex) {
                throw new SAXOutputException(ex);
            }
        }
    }
    
    @Override
    protected void beginStartElement(final String prefix, final String namespaceURI, final String localName) throws OutputException {
        if (!this.startDocumentWritten) {
            this.writeStartDocument();
            this.autoStartDocument = true;
        }
        this.helper.beginStartElement(prefix, namespaceURI, localName);
        this.nsContext.startScope();
        ++this.depth;
    }
    
    @Override
    protected void addNamespace(final String prefix, final String namespaceURI) throws OutputException {
        this.nsContext.setPrefix(prefix, namespaceURI);
        try {
            this.contentHandler.startPrefixMapping(prefix, namespaceURI);
        }
        catch (final SAXException ex) {
            throw new SAXOutputException(ex);
        }
    }
    
    @Override
    protected void addAttribute(final String prefix, final String namespaceURI, final String localName, final String type, final String value) throws OutputException {
        this.helper.addAttribute(prefix, namespaceURI, localName, type, value);
    }
    
    @Override
    protected void finishStartElement() throws OutputException {
        try {
            this.helper.finishStartElement(this.contentHandler);
        }
        catch (final SAXException ex) {
            throw new SAXOutputException(ex);
        }
    }
    
    @Override
    public void writeEndElement() throws OutputException {
        try {
            this.helper.writeEndElement(this.contentHandler, this.nsContext);
            final int depth = this.depth - 1;
            this.depth = depth;
            if (depth == 0 && this.autoStartDocument) {
                this.contentHandler.endDocument();
            }
        }
        catch (final SAXException ex) {
            throw new SAXOutputException(ex);
        }
    }
    
    @Override
    public void writeText(final int type, final String data) throws OutputException {
        final char[] ch = data.toCharArray();
        try {
            switch (type) {
                case 4: {
                    this.contentHandler.characters(ch, 0, ch.length);
                    break;
                }
                case 12: {
                    if (this.lexicalHandler != null) {
                        this.lexicalHandler.startCDATA();
                    }
                    this.contentHandler.characters(ch, 0, ch.length);
                    if (this.lexicalHandler != null) {
                        this.lexicalHandler.endCDATA();
                        break;
                    }
                    break;
                }
                case 6: {
                    this.contentHandler.ignorableWhitespace(ch, 0, ch.length);
                    break;
                }
            }
        }
        catch (final SAXException ex) {
            throw new SAXOutputException(ex);
        }
    }
    
    @Override
    public void writeComment(final String data) throws OutputException {
        if (this.lexicalHandler != null) {
            final char[] ch = data.toCharArray();
            try {
                this.lexicalHandler.comment(ch, 0, ch.length);
            }
            catch (final SAXException ex) {
                throw new SAXOutputException(ex);
            }
        }
    }
    
    @Override
    public void writeProcessingInstruction(final String target, final String data) throws OutputException {
        try {
            this.contentHandler.processingInstruction(target, data);
        }
        catch (final SAXException ex) {
            throw new SAXOutputException(ex);
        }
    }
    
    @Override
    public void writeEntityRef(final String name) throws OutputException {
        try {
            this.contentHandler.skippedEntity(name);
        }
        catch (final SAXException ex) {
            throw new SAXOutputException(ex);
        }
    }
    
    @Override
    public void writeDataHandler(final DataHandler dataHandler, final String contentID, final boolean optimize) throws OutputException {
        final Base64EncodingWriterOutputStream out = new Base64EncodingWriterOutputStream((Writer)new ContentHandlerWriter(this.contentHandler), 4096, true);
        try {
            dataHandler.writeTo((OutputStream)out);
            out.complete();
        }
        catch (final IOException ex) {
            final Throwable cause = ex.getCause();
            SAXException saxException;
            if (cause instanceof SAXException) {
                saxException = (SAXException)cause;
            }
            else {
                saxException = new SAXException(ex);
            }
            throw new SAXOutputException(saxException);
        }
    }
    
    @Override
    public void writeDataHandler(final DataHandlerProvider dataHandlerProvider, final String contentID, final boolean optimize) throws OutputException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    protected void serializePushOMDataSource(final OMDataSource dataSource) throws OutputException {
        try {
            final XMLStreamWriter writer = new ContentHandlerXMLStreamWriter(this.helper, this.contentHandler, this.lexicalHandler, this.nsContext);
            if (this.startDocumentWritten) {
                dataSource.serialize(writer);
            }
            else {
                this.contentHandler.startDocument();
                dataSource.serialize(writer);
                this.contentHandler.endDocument();
            }
        }
        catch (final SAXException ex) {
            throw new SAXOutputException(ex);
        }
        catch (final SAXExceptionWrapper ex2) {
            throw new SAXOutputException((SAXException)ex2.getCause());
        }
        catch (final XMLStreamException ex3) {
            throw new SAXOutputException(new SAXException(ex3));
        }
    }
    
    @Override
    public void writeEndDocument() throws OutputException {
        try {
            this.contentHandler.endDocument();
        }
        catch (final SAXException ex) {
            throw new SAXOutputException(ex);
        }
    }
}
