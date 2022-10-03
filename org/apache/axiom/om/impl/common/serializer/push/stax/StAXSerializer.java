package org.apache.axiom.om.impl.common.serializer.push.stax;

import org.apache.axiom.ext.stax.datahandler.DataHandlerProvider;
import java.io.IOException;
import javax.activation.DataHandler;
import javax.xml.namespace.NamespaceContext;
import org.apache.axiom.om.OMException;
import org.apache.axiom.util.stax.XMLStreamWriterUtils;
import org.apache.axiom.om.impl.common.serializer.push.OutputException;
import javax.xml.stream.XMLStreamException;
import org.apache.axiom.om.OMDataSource;
import org.apache.axiom.om.OMSerializable;
import org.apache.commons.logging.LogFactory;
import org.apache.axiom.ext.stax.datahandler.DataHandlerWriter;
import javax.xml.stream.XMLStreamWriter;
import org.apache.commons.logging.Log;
import org.apache.axiom.om.impl.common.serializer.push.Serializer;

public class StAXSerializer extends Serializer
{
    private static final Log log;
    private final XMLStreamWriter writer;
    private DataHandlerWriter dataHandlerWriter;
    
    static {
        log = LogFactory.getLog((Class)StAXSerializer.class);
    }
    
    public StAXSerializer(final OMSerializable root, final XMLStreamWriter writer) {
        super(root, true, false);
        this.writer = writer;
    }
    
    @Override
    protected void serializePushOMDataSource(final OMDataSource dataSource) throws OutputException {
        try {
            dataSource.serialize(this.writer);
        }
        catch (final XMLStreamException ex) {
            throw new StAXOutputException(ex);
        }
    }
    
    @Override
    public void writeStartDocument(final String version) throws OutputException {
        try {
            this.writer.writeStartDocument(version);
        }
        catch (final XMLStreamException ex) {
            throw new StAXOutputException(ex);
        }
    }
    
    @Override
    public void writeStartDocument(final String encoding, final String version) throws OutputException {
        try {
            this.writer.writeStartDocument(encoding, version);
        }
        catch (final XMLStreamException ex) {
            throw new StAXOutputException(ex);
        }
    }
    
    @Override
    public void writeDTD(final String rootName, final String publicId, final String systemId, final String internalSubset) throws OutputException {
        try {
            XMLStreamWriterUtils.writeDTD(this.writer, rootName, publicId, systemId, internalSubset);
        }
        catch (final XMLStreamException ex) {
            throw new StAXOutputException(ex);
        }
    }
    
    @Override
    protected void beginStartElement(final String prefix, final String namespaceURI, final String localName) throws OutputException {
        try {
            this.writer.writeStartElement(prefix, localName, namespaceURI);
        }
        catch (final XMLStreamException ex) {
            throw new StAXOutputException(ex);
        }
    }
    
    @Override
    protected void addNamespace(final String prefix, final String namespaceURI) throws OutputException {
        try {
            if (prefix.length() != 0) {
                this.writer.writeNamespace(prefix, namespaceURI);
            }
            else {
                this.writer.writeDefaultNamespace(namespaceURI);
            }
        }
        catch (final XMLStreamException ex) {
            throw new StAXOutputException(ex);
        }
    }
    
    @Override
    protected void addAttribute(final String prefix, final String namespaceURI, final String localName, final String type, final String value) throws OutputException {
        try {
            this.writer.writeAttribute(prefix, namespaceURI, localName, value);
        }
        catch (final XMLStreamException ex) {
            throw new StAXOutputException(ex);
        }
    }
    
    @Override
    protected void finishStartElement() throws OutputException {
    }
    
    @Override
    protected boolean isAssociated(String prefix, String namespace) throws OutputException {
        try {
            if ("xml".equals(prefix)) {
                return true;
            }
            prefix = ((prefix == null) ? "" : prefix);
            namespace = ((namespace == null) ? "" : namespace);
            if (namespace.length() > 0) {
                final String writerPrefix = this.writer.getPrefix(namespace);
                if (prefix.equals(writerPrefix)) {
                    return true;
                }
                if (writerPrefix != null) {
                    final NamespaceContext nsContext = this.writer.getNamespaceContext();
                    if (nsContext != null) {
                        final String writerNS = nsContext.getNamespaceURI(prefix);
                        return namespace.equals(writerNS);
                    }
                }
                return false;
            }
            else {
                if (prefix.length() > 0) {
                    throw new OMException("Invalid namespace declaration: Prefixed namespace bindings may not be empty.");
                }
                try {
                    final String writerPrefix = this.writer.getPrefix("");
                    if (writerPrefix != null && writerPrefix.length() == 0) {
                        return true;
                    }
                }
                catch (final Throwable t) {
                    if (StAXSerializer.log.isDebugEnabled()) {
                        StAXSerializer.log.debug((Object)("Caught exception from getPrefix(\"\"). Processing continues: " + t));
                    }
                }
                final NamespaceContext nsContext2 = this.writer.getNamespaceContext();
                if (nsContext2 != null) {
                    final String writerNS2 = nsContext2.getNamespaceURI("");
                    if (writerNS2 != null && writerNS2.length() > 0) {
                        return false;
                    }
                }
                return true;
            }
        }
        catch (final XMLStreamException ex) {
            throw new StAXOutputException(ex);
        }
    }
    
    @Override
    public void writeEndElement() throws OutputException {
        try {
            this.writer.writeEndElement();
        }
        catch (final XMLStreamException ex) {
            throw new StAXOutputException(ex);
        }
    }
    
    @Override
    public void writeText(final int type, final String data) throws OutputException {
        try {
            if (type == 12) {
                this.writer.writeCData(data);
            }
            else {
                this.writer.writeCharacters(data);
            }
        }
        catch (final XMLStreamException ex) {
            throw new StAXOutputException(ex);
        }
    }
    
    @Override
    public void writeComment(final String data) throws OutputException {
        try {
            this.writer.writeComment(data);
        }
        catch (final XMLStreamException ex) {
            throw new StAXOutputException(ex);
        }
    }
    
    @Override
    public void writeProcessingInstruction(final String target, final String data) throws OutputException {
        try {
            this.writer.writeProcessingInstruction(target, data);
        }
        catch (final XMLStreamException ex) {
            throw new StAXOutputException(ex);
        }
    }
    
    @Override
    public void writeEntityRef(final String name) throws OutputException {
        try {
            this.writer.writeEntityRef(name);
        }
        catch (final XMLStreamException ex) {
            throw new StAXOutputException(ex);
        }
    }
    
    private DataHandlerWriter getDataHandlerWriter() {
        if (this.dataHandlerWriter == null) {
            this.dataHandlerWriter = XMLStreamWriterUtils.getDataHandlerWriter(this.writer);
        }
        return this.dataHandlerWriter;
    }
    
    @Override
    public void writeDataHandler(final DataHandler dataHandler, final String contentID, final boolean optimize) throws OutputException {
        try {
            this.getDataHandlerWriter().writeDataHandler(dataHandler, contentID, optimize);
        }
        catch (final IOException ex) {
            throw new StAXOutputException(new XMLStreamException("Error while reading data handler", ex));
        }
        catch (final XMLStreamException ex2) {
            throw new StAXOutputException(ex2);
        }
    }
    
    @Override
    public void writeDataHandler(final DataHandlerProvider dataHandlerProvider, final String contentID, final boolean optimize) throws OutputException {
        try {
            this.getDataHandlerWriter().writeDataHandler(dataHandlerProvider, contentID, optimize);
        }
        catch (final IOException ex) {
            throw new StAXOutputException(new XMLStreamException("Error while reading data handler", ex));
        }
        catch (final XMLStreamException ex2) {
            throw new StAXOutputException(ex2);
        }
    }
    
    @Override
    public void writeEndDocument() throws OutputException {
    }
}
