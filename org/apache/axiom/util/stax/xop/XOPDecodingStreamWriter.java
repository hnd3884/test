package org.apache.axiom.util.stax.xop;

import javax.activation.DataHandler;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.apache.axiom.ext.stax.datahandler.DataHandlerWriter;
import org.apache.axiom.util.stax.wrapper.XMLStreamWriterWrapper;

public class XOPDecodingStreamWriter extends XMLStreamWriterWrapper
{
    private final MimePartProvider mimePartProvider;
    private final DataHandlerWriter dataHandlerWriter;
    private boolean inXOPInclude;
    private String contentID;
    
    public XOPDecodingStreamWriter(final XMLStreamWriter parent, final MimePartProvider mimePartProvider) {
        super(parent);
        this.mimePartProvider = mimePartProvider;
        this.dataHandlerWriter = (DataHandlerWriter)parent.getProperty(DataHandlerWriter.PROPERTY);
    }
    
    @Override
    public void writeStartElement(final String prefix, final String localName, final String namespaceURI) throws XMLStreamException {
        if (localName.equals("Include") && namespaceURI.equals("http://www.w3.org/2004/08/xop/include")) {
            this.inXOPInclude = true;
        }
        else {
            super.writeStartElement(prefix, localName, namespaceURI);
        }
    }
    
    @Override
    public void writeStartElement(final String namespaceURI, final String localName) throws XMLStreamException {
        if (localName.equals("Include") && namespaceURI.equals("http://www.w3.org/2004/08/xop/include")) {
            this.inXOPInclude = true;
        }
        else {
            super.writeStartElement(namespaceURI, localName);
        }
    }
    
    @Override
    public void writeAttribute(final String prefix, final String namespaceURI, final String localName, final String value) throws XMLStreamException {
        if (this.inXOPInclude) {
            this.processAttribute(namespaceURI, localName, value);
        }
        else {
            super.writeAttribute(prefix, namespaceURI, localName, value);
        }
    }
    
    @Override
    public void writeAttribute(final String namespaceURI, final String localName, final String value) throws XMLStreamException {
        if (this.inXOPInclude) {
            this.processAttribute(namespaceURI, localName, value);
        }
        else {
            super.writeAttribute(namespaceURI, localName, value);
        }
    }
    
    @Override
    public void writeAttribute(final String localName, final String value) throws XMLStreamException {
        if (this.inXOPInclude) {
            this.processAttribute(null, localName, value);
        }
        else {
            super.writeAttribute(localName, value);
        }
    }
    
    private void processAttribute(final String namespaceURI, final String localName, final String value) throws XMLStreamException {
        if ((namespaceURI == null || namespaceURI.length() == 0) && localName.equals("href")) {
            if (!value.startsWith("cid:")) {
                throw new XMLStreamException("Expected href attribute containing a URL in the cid scheme");
            }
            try {
                this.contentID = URLDecoder.decode(value.substring(4), "ascii");
                return;
            }
            catch (final UnsupportedEncodingException ex) {
                throw new XMLStreamException(ex);
            }
            throw new XMLStreamException("Expected xop:Include element information item with a (single) href attribute");
        }
        throw new XMLStreamException("Expected xop:Include element information item with a (single) href attribute");
    }
    
    @Override
    public void writeEndElement() throws XMLStreamException {
        if (this.inXOPInclude) {
            if (this.contentID == null) {
                throw new XMLStreamException("Encountered an xop:Include element without href attribute");
            }
            DataHandler dh;
            try {
                dh = this.mimePartProvider.getDataHandler(this.contentID);
            }
            catch (final IOException ex) {
                throw new XMLStreamException("Error while fetching data handler", ex);
            }
            try {
                this.dataHandlerWriter.writeDataHandler(dh, this.contentID, true);
            }
            catch (final IOException ex) {
                throw new XMLStreamException("Error while writing data handler", ex);
            }
            this.inXOPInclude = false;
            this.contentID = null;
        }
        else {
            super.writeEndElement();
        }
    }
}
