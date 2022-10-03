package org.apache.axiom.util.stax;

import org.apache.axiom.ext.stax.datahandler.DataHandlerProvider;
import org.apache.axiom.ext.stax.datahandler.DataHandlerWriter;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import org.apache.axiom.util.base64.Base64EncodingWriterOutputStream;
import javax.activation.DataHandler;
import javax.xml.stream.XMLStreamWriter;

public class XMLStreamWriterUtils
{
    public static void writeBase64(final XMLStreamWriter writer, final DataHandler dh) throws IOException, XMLStreamException {
        final Base64EncodingWriterOutputStream out = new Base64EncodingWriterOutputStream(new XMLStreamWriterWriter(writer), 4096, true);
        try {
            dh.writeTo(out);
            out.close();
        }
        catch (final XMLStreamIOException ex) {
            throw ex.getXMLStreamException();
        }
    }
    
    private static DataHandlerWriter internalGetDataHandlerWriter(final XMLStreamWriter writer) {
        try {
            return (DataHandlerWriter)writer.getProperty(DataHandlerWriter.PROPERTY);
        }
        catch (final IllegalArgumentException ex) {
            return null;
        }
    }
    
    public static DataHandlerWriter getDataHandlerWriter(final XMLStreamWriter writer) {
        final DataHandlerWriter dataHandlerWriter = internalGetDataHandlerWriter(writer);
        if (dataHandlerWriter == null) {
            return new DataHandlerWriter() {
                public void writeDataHandler(final DataHandler dataHandler, final String contentID, final boolean optimize) throws IOException, XMLStreamException {
                    XMLStreamWriterUtils.writeBase64(writer, dataHandler);
                }
                
                public void writeDataHandler(final DataHandlerProvider dataHandlerProvider, final String contentID, final boolean optimize) throws IOException, XMLStreamException {
                    XMLStreamWriterUtils.writeBase64(writer, dataHandlerProvider.getDataHandler());
                }
            };
        }
        return dataHandlerWriter;
    }
    
    public static void writeDataHandler(final XMLStreamWriter writer, final DataHandler dataHandler, final String contentID, final boolean optimize) throws IOException, XMLStreamException {
        final DataHandlerWriter dataHandlerWriter = internalGetDataHandlerWriter(writer);
        if (dataHandlerWriter != null) {
            dataHandlerWriter.writeDataHandler(dataHandler, contentID, optimize);
        }
        else {
            writeBase64(writer, dataHandler);
        }
    }
    
    public static void writeDataHandler(final XMLStreamWriter writer, final DataHandlerProvider dataHandlerProvider, final String contentID, final boolean optimize) throws IOException, XMLStreamException {
        final DataHandlerWriter dataHandlerWriter = internalGetDataHandlerWriter(writer);
        if (dataHandlerWriter != null) {
            dataHandlerWriter.writeDataHandler(dataHandlerProvider, contentID, optimize);
        }
        else {
            writeBase64(writer, dataHandlerProvider.getDataHandler());
        }
    }
    
    public static void writeDTD(final XMLStreamWriter writer, final String rootName, final String publicId, final String systemId, final String internalSubset) throws XMLStreamException {
        final StringBuilder buffer = new StringBuilder("<!DOCTYPE ");
        buffer.append(rootName);
        if (publicId != null) {
            buffer.append(" PUBLIC \"");
            buffer.append(publicId);
            buffer.append("\" \"");
            buffer.append(systemId);
            buffer.append("\"");
        }
        else if (systemId != null) {
            buffer.append(" SYSTEM \"");
            buffer.append(systemId);
            buffer.append("\"");
        }
        if (internalSubset != null) {
            buffer.append(" [");
            buffer.append(internalSubset);
            buffer.append("]");
        }
        buffer.append(">");
        writer.writeDTD(buffer.toString());
    }
}
