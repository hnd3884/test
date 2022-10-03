package org.apache.axiom.util.stax;

import org.apache.commons.logging.LogFactory;
import org.apache.axiom.ext.stax.DelegatingXMLStreamReader;
import java.io.Reader;
import org.apache.axiom.ext.stax.CharacterDataReader;
import java.io.Writer;
import org.apache.axiom.blob.MemoryBlob;
import org.apache.axiom.blob.Blob;
import org.apache.axiom.blob.BlobDataSource;
import java.io.IOException;
import org.apache.axiom.util.base64.Base64DecodingOutputStreamWriter;
import org.apache.axiom.blob.Blobs;
import javax.activation.DataSource;
import org.apache.axiom.util.activation.EmptyDataSource;
import javax.xml.stream.XMLStreamException;
import org.apache.axiom.ext.stax.datahandler.DataHandlerProvider;
import javax.activation.DataHandler;
import org.apache.axiom.ext.stax.datahandler.DataHandlerReader;
import javax.xml.stream.XMLStreamReader;
import org.apache.commons.logging.Log;

public class XMLStreamReaderUtils
{
    private static final String IS_BINARY = "Axiom.IsBinary";
    private static final String DATA_HANDLER = "Axiom.DataHandler";
    private static final String IS_DATA_HANDLERS_AWARE = "IsDatahandlersAwareParsing";
    private static final Log log;
    
    private XMLStreamReaderUtils() {
    }
    
    public static DataHandlerReader getDataHandlerReader(final XMLStreamReader reader) {
        try {
            final DataHandlerReader dhr = (DataHandlerReader)reader.getProperty(DataHandlerReader.PROPERTY);
            if (dhr != null) {
                return dhr;
            }
        }
        catch (final IllegalArgumentException ex2) {}
        Boolean isDataHandlerAware;
        try {
            isDataHandlerAware = (Boolean)reader.getProperty("IsDatahandlersAwareParsing");
        }
        catch (final IllegalArgumentException ex) {
            return null;
        }
        if (isDataHandlerAware != null && isDataHandlerAware) {
            return new DataHandlerReader() {
                public boolean isBinary() {
                    return (boolean)reader.getProperty("Axiom.IsBinary");
                }
                
                public boolean isOptimized() {
                    return true;
                }
                
                public boolean isDeferred() {
                    return false;
                }
                
                public String getContentID() {
                    return null;
                }
                
                public DataHandler getDataHandler() {
                    return (DataHandler)reader.getProperty("Axiom.DataHandler");
                }
                
                public DataHandlerProvider getDataHandlerProvider() {
                    throw new UnsupportedOperationException();
                }
            };
        }
        return null;
    }
    
    public static Object processGetProperty(final DataHandlerReader extension, final String propertyName) {
        if (extension == null || propertyName == null) {
            throw new IllegalArgumentException();
        }
        if (propertyName.equals(DataHandlerReader.PROPERTY)) {
            return extension;
        }
        if (propertyName.equals("IsDatahandlersAwareParsing")) {
            return Boolean.TRUE;
        }
        if (propertyName.equals("Axiom.IsBinary")) {
            return extension.isBinary();
        }
        if (propertyName.equals("Axiom.DataHandler")) {
            try {
                return extension.getDataHandler();
            }
            catch (final XMLStreamException ex) {
                throw new RuntimeException(ex);
            }
        }
        return null;
    }
    
    public static DataHandler getDataHandlerFromElement(final XMLStreamReader reader) throws XMLStreamException {
        final int event = reader.next();
        if (event == 2) {
            return new DataHandler(new EmptyDataSource("application/octet-stream"));
        }
        if (event != 4) {
            throw new XMLStreamException("Expected a CHARACTER event");
        }
        final DataHandlerReader dhr = getDataHandlerReader(reader);
        if (dhr != null && dhr.isBinary()) {
            final DataHandler dh = dhr.getDataHandler();
            reader.next();
            return dh;
        }
        final MemoryBlob blob = Blobs.createMemoryBlob();
        final Writer out = new Base64DecodingOutputStreamWriter(blob.getOutputStream());
        Label_0180: {
            try {
                writeTextTo(reader, out);
                while (true) {
                    switch (reader.next()) {
                        case 4: {
                            writeTextTo(reader, out);
                            continue;
                        }
                        case 2: {
                            out.close();
                            break Label_0180;
                        }
                        default: {
                            throw new XMLStreamException("Expected a CHARACTER event");
                        }
                    }
                }
            }
            catch (final IOException ex) {
                throw new XMLStreamException("Error during base64 decoding", ex);
            }
        }
        return new DataHandler(new BlobDataSource(blob, "application/octet-string"));
    }
    
    public static void writeTextTo(final XMLStreamReader reader, final Writer writer) throws XMLStreamException, IOException {
        CharacterDataReader cdataReader;
        try {
            cdataReader = (CharacterDataReader)reader.getProperty(CharacterDataReader.PROPERTY);
        }
        catch (final IllegalArgumentException ex) {
            cdataReader = null;
        }
        if (cdataReader != null) {
            cdataReader.writeTextTo(writer);
        }
        else {
            writer.write(reader.getText());
        }
    }
    
    public static Reader getElementTextAsStream(final XMLStreamReader reader, final boolean allowNonTextChildren) {
        if (reader.getEventType() != 1) {
            throw new IllegalStateException("Reader must be on a START_ELEMENT event");
        }
        return new TextFromElementReader(reader, allowNonTextChildren);
    }
    
    public static XMLStreamReader getOriginalXMLStreamReader(XMLStreamReader parser) {
        if (XMLStreamReaderUtils.log.isDebugEnabled()) {
            final String clsName = (parser != null) ? parser.getClass().toString() : "null";
            XMLStreamReaderUtils.log.debug((Object)("Entry getOriginalXMLStreamReader: " + clsName));
        }
        while (parser instanceof DelegatingXMLStreamReader) {
            parser = ((DelegatingXMLStreamReader)parser).getParent();
            if (XMLStreamReaderUtils.log.isDebugEnabled()) {
                final String clsName = (parser != null) ? parser.getClass().toString() : "null";
                XMLStreamReaderUtils.log.debug((Object)("  parent: " + clsName));
            }
        }
        if (XMLStreamReaderUtils.log.isDebugEnabled()) {
            final String clsName = (parser != null) ? parser.getClass().toString() : "null";
            XMLStreamReaderUtils.log.debug((Object)("Exit getOriginalXMLStreamReader: " + clsName));
        }
        return parser;
    }
    
    static {
        log = LogFactory.getLog((Class)XMLStreamReaderUtils.class);
    }
}
