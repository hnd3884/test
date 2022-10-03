package org.apache.axiom.om.impl.serialize;

import org.apache.commons.logging.LogFactory;
import org.apache.axiom.ext.stax.DTDReader;
import java.io.IOException;
import javax.xml.namespace.NamespaceContext;
import java.util.ArrayList;
import org.apache.axiom.om.impl.util.OMSerializerUtil;
import org.apache.axiom.om.OMDataSource;
import org.apache.axiom.om.impl.OMXMLStreamReaderEx;
import org.apache.axiom.util.stax.XMLStreamWriterUtils;
import org.apache.axiom.util.stax.XMLStreamReaderUtils;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.XMLStreamReader;
import org.apache.axiom.ext.stax.datahandler.DataHandlerWriter;
import org.apache.axiom.ext.stax.datahandler.DataHandlerReader;
import org.apache.commons.logging.Log;
import org.apache.axiom.om.OMSerializer;
import javax.xml.stream.XMLStreamConstants;

public class StreamingOMSerializer implements XMLStreamConstants, OMSerializer
{
    private static final Log log;
    private static int namespaceSuffix;
    public static final String NAMESPACE_PREFIX = "ns";
    private static final String XSI_URI = "http://www.w3.org/2001/XMLSchema-instance";
    private static final String XSI_LOCAL_NAME = "type";
    private int depth;
    private DataHandlerReader dataHandlerReader;
    private DataHandlerWriter dataHandlerWriter;
    
    public StreamingOMSerializer() {
        this.depth = 0;
    }
    
    public void serialize(final XMLStreamReader reader, final XMLStreamWriter writer) throws XMLStreamException {
        this.serialize(reader, writer, true);
    }
    
    public void serialize(final XMLStreamReader reader, final XMLStreamWriter writer, final boolean startAtNext) throws XMLStreamException {
        this.dataHandlerReader = XMLStreamReaderUtils.getDataHandlerReader(reader);
        this.dataHandlerWriter = XMLStreamWriterUtils.getDataHandlerWriter(writer);
        if (reader instanceof OMXMLStreamReaderEx) {
            final int event = reader.getEventType();
            if (event <= 0 || event == 3 || event == 7) {
                if (StreamingOMSerializer.log.isDebugEnabled()) {
                    StreamingOMSerializer.log.debug((Object)"Enable OMDataSource events while serializing this document");
                }
                ((OMXMLStreamReaderEx)reader).enableDataSourceEvents(true);
            }
        }
        try {
            this.serializeNode(reader, writer, startAtNext);
        }
        finally {
            if (reader instanceof OMXMLStreamReaderEx) {
                ((OMXMLStreamReaderEx)reader).enableDataSourceEvents(false);
            }
        }
    }
    
    protected void serializeNode(final XMLStreamReader reader, final XMLStreamWriter writer) throws XMLStreamException {
        this.serializeNode(reader, writer, true);
    }
    
    protected void serializeNode(final XMLStreamReader reader, final XMLStreamWriter writer, final boolean startAtNext) throws XMLStreamException {
        boolean useCurrentEvent = !startAtNext;
        while (reader.hasNext() || useCurrentEvent) {
            int event = 0;
            OMDataSource ds = null;
            if (useCurrentEvent) {
                event = reader.getEventType();
                useCurrentEvent = false;
            }
            else {
                event = reader.next();
            }
            if (reader instanceof OMXMLStreamReaderEx) {
                ds = ((OMXMLStreamReaderEx)reader).getDataSource();
            }
            if (ds != null) {
                ds.serialize(writer);
            }
            else {
                switch (event) {
                    case 1: {
                        this.serializeElement(reader, writer);
                        ++this.depth;
                        break;
                    }
                    case 10: {
                        this.serializeAttributes(reader, writer);
                        break;
                    }
                    case 4: {
                        if (this.dataHandlerReader != null && this.dataHandlerReader.isBinary()) {
                            this.serializeDataHandler();
                            break;
                        }
                    }
                    case 6: {
                        this.serializeText(reader, writer);
                        break;
                    }
                    case 5: {
                        this.serializeComment(reader, writer);
                        break;
                    }
                    case 12: {
                        this.serializeCData(reader, writer);
                        break;
                    }
                    case 3: {
                        this.serializeProcessingInstruction(reader, writer);
                        break;
                    }
                    case 2: {
                        this.serializeEndElement(writer);
                        --this.depth;
                        break;
                    }
                    case 7: {
                        ++this.depth;
                        break;
                    }
                    case 8: {
                        if (this.depth != 0) {
                            --this.depth;
                        }
                        try {
                            this.serializeEndElement(writer);
                        }
                        catch (final Exception ex) {}
                        break;
                    }
                    case 11: {
                        this.serializeDTD(reader, writer);
                        break;
                    }
                    case 9: {
                        writer.writeEntityRef(reader.getLocalName());
                        break;
                    }
                }
            }
            if (this.depth == 0) {
                break;
            }
        }
    }
    
    protected void serializeElement(final XMLStreamReader reader, final XMLStreamWriter writer) throws XMLStreamException {
        ArrayList writePrefixList = null;
        ArrayList writeNSList = null;
        String ePrefix = reader.getPrefix();
        ePrefix = ((ePrefix != null && ePrefix.length() == 0) ? null : ePrefix);
        String eNamespace = reader.getNamespaceURI();
        eNamespace = ((eNamespace != null && eNamespace.length() == 0) ? null : eNamespace);
        if (eNamespace != null) {
            if (ePrefix == null) {
                if (!OMSerializerUtil.isAssociated("", eNamespace, writer)) {
                    if (writePrefixList == null) {
                        writePrefixList = new ArrayList();
                        writeNSList = new ArrayList();
                    }
                    writePrefixList.add("");
                    writeNSList.add(eNamespace);
                }
                writer.writeStartElement("", reader.getLocalName(), eNamespace);
            }
            else {
                if (!OMSerializerUtil.isAssociated(ePrefix, eNamespace, writer)) {
                    if (writePrefixList == null) {
                        writePrefixList = new ArrayList();
                        writeNSList = new ArrayList();
                    }
                    writePrefixList.add(ePrefix);
                    writeNSList.add(eNamespace);
                }
                writer.writeStartElement(ePrefix, reader.getLocalName(), eNamespace);
            }
        }
        else {
            writer.writeStartElement(reader.getLocalName());
        }
        for (int count = reader.getNamespaceCount(), i = 0; i < count; ++i) {
            String prefix = reader.getNamespacePrefix(i);
            prefix = ((prefix != null && prefix.length() == 0) ? null : prefix);
            String namespace = reader.getNamespaceURI(i);
            namespace = ((namespace != null && namespace.length() == 0) ? null : namespace);
            final String newPrefix = OMSerializerUtil.generateSetPrefix(prefix, namespace, writer, false);
            if (newPrefix != null) {
                if (writePrefixList == null) {
                    writePrefixList = new ArrayList();
                    writeNSList = new ArrayList();
                }
                if (!writePrefixList.contains(newPrefix)) {
                    writePrefixList.add(newPrefix);
                    writeNSList.add(namespace);
                }
            }
        }
        String newPrefix2 = OMSerializerUtil.generateSetPrefix(ePrefix, eNamespace, writer, false);
        if (newPrefix2 != null) {
            if (writePrefixList == null) {
                writePrefixList = new ArrayList();
                writeNSList = new ArrayList();
            }
            if (!writePrefixList.contains(newPrefix2)) {
                writePrefixList.add(newPrefix2);
                writeNSList.add(eNamespace);
            }
        }
        for (int count = reader.getAttributeCount(), j = 0; j < count; ++j) {
            String prefix2 = reader.getAttributePrefix(j);
            prefix2 = ((prefix2 != null && prefix2.length() == 0) ? null : prefix2);
            String namespace2 = reader.getAttributeNamespace(j);
            namespace2 = ((namespace2 != null && namespace2.length() == 0) ? null : namespace2);
            if (prefix2 == null && namespace2 != null) {
                String writerPrefix = writer.getPrefix(namespace2);
                writerPrefix = ((writerPrefix != null && writerPrefix.length() == 0) ? null : writerPrefix);
                prefix2 = ((writerPrefix != null) ? writerPrefix : this.generateUniquePrefix(writer.getNamespaceContext()));
            }
            newPrefix2 = OMSerializerUtil.generateSetPrefix(prefix2, namespace2, writer, true);
            if (newPrefix2 != null) {
                if (writePrefixList == null) {
                    writePrefixList = new ArrayList();
                    writeNSList = new ArrayList();
                }
                if (!writePrefixList.contains(newPrefix2)) {
                    writePrefixList.add(newPrefix2);
                    writeNSList.add(namespace2);
                }
            }
        }
        for (int count = reader.getAttributeCount(), j = 0; j < count; ++j) {
            String prefix2 = reader.getAttributePrefix(j);
            prefix2 = ((prefix2 != null && prefix2.length() == 0) ? null : prefix2);
            String namespace2 = reader.getAttributeNamespace(j);
            namespace2 = ((namespace2 != null && namespace2.length() == 0) ? null : namespace2);
            final String localName = reader.getAttributeLocalName(j);
            if ("http://www.w3.org/2001/XMLSchema-instance".equals(namespace2) && "type".equals(localName)) {
                String value = reader.getAttributeValue(j);
                if (StreamingOMSerializer.log.isDebugEnabled()) {
                    StreamingOMSerializer.log.debug((Object)("The value of xsi:type is " + value));
                }
                if (value != null) {
                    value = value.trim();
                    if (value.indexOf(":") > 0) {
                        final String refPrefix = value.substring(0, value.indexOf(":"));
                        final String refNamespace = reader.getNamespaceURI(refPrefix);
                        if (refNamespace != null && refNamespace.length() > 0) {
                            newPrefix2 = OMSerializerUtil.generateSetPrefix(refPrefix, refNamespace, writer, true);
                            if (newPrefix2 != null) {
                                if (StreamingOMSerializer.log.isDebugEnabled()) {
                                    StreamingOMSerializer.log.debug((Object)("An xmlns:" + newPrefix2 + "=\"" + refNamespace + "\" will be written"));
                                }
                                if (writePrefixList == null) {
                                    writePrefixList = new ArrayList();
                                    writeNSList = new ArrayList();
                                }
                                if (!writePrefixList.contains(newPrefix2)) {
                                    writePrefixList.add(newPrefix2);
                                    writeNSList.add(refNamespace);
                                }
                            }
                        }
                    }
                }
            }
        }
        if (writePrefixList != null) {
            for (int j = 0; j < writePrefixList.size(); ++j) {
                final String prefix2 = writePrefixList.get(j);
                final String namespace2 = writeNSList.get(j);
                if (prefix2 != null) {
                    if (namespace2 == null) {
                        writer.writeNamespace(prefix2, "");
                    }
                    else {
                        writer.writeNamespace(prefix2, namespace2);
                    }
                }
                else {
                    writer.writeDefaultNamespace(namespace2);
                }
            }
        }
        for (int count = reader.getAttributeCount(), j = 0; j < count; ++j) {
            String prefix2 = reader.getAttributePrefix(j);
            prefix2 = ((prefix2 != null && prefix2.length() == 0) ? null : prefix2);
            String namespace2 = reader.getAttributeNamespace(j);
            namespace2 = ((namespace2 != null && namespace2.length() == 0) ? null : namespace2);
            if (prefix2 == null && namespace2 != null) {
                prefix2 = writer.getPrefix(namespace2);
                if (prefix2 == null || "".equals(prefix2)) {
                    for (int k = 0; k < writePrefixList.size(); ++k) {
                        if (namespace2.equals(writeNSList.get(k))) {
                            prefix2 = writePrefixList.get(k);
                        }
                    }
                }
            }
            else if (namespace2 != null && !prefix2.equals("xml")) {
                final String writerPrefix = writer.getPrefix(namespace2);
                if (!prefix2.equals(writerPrefix) && !"".equals(writerPrefix)) {
                    prefix2 = writerPrefix;
                }
            }
            if (namespace2 != null) {
                writer.writeAttribute(prefix2, namespace2, reader.getAttributeLocalName(j), reader.getAttributeValue(j));
            }
            else {
                writer.writeAttribute(reader.getAttributeLocalName(j), reader.getAttributeValue(j));
            }
        }
    }
    
    protected void serializeEndElement(final XMLStreamWriter writer) throws XMLStreamException {
        writer.writeEndElement();
    }
    
    protected void serializeText(final XMLStreamReader reader, final XMLStreamWriter writer) throws XMLStreamException {
        writer.writeCharacters(reader.getText());
    }
    
    protected void serializeCData(final XMLStreamReader reader, final XMLStreamWriter writer) throws XMLStreamException {
        writer.writeCData(reader.getText());
    }
    
    protected void serializeComment(final XMLStreamReader reader, final XMLStreamWriter writer) throws XMLStreamException {
        writer.writeComment(reader.getText());
    }
    
    protected void serializeProcessingInstruction(final XMLStreamReader reader, final XMLStreamWriter writer) throws XMLStreamException {
        writer.writeProcessingInstruction(reader.getPITarget(), reader.getPIData());
    }
    
    protected void serializeAttributes(final XMLStreamReader reader, final XMLStreamWriter writer) throws XMLStreamException {
        final int count = reader.getAttributeCount();
        String prefix = null;
        String namespaceName = null;
        String writerPrefix = null;
        for (int i = 0; i < count; ++i) {
            prefix = reader.getAttributePrefix(i);
            namespaceName = reader.getAttributeNamespace(i);
            namespaceName = ((namespaceName == null) ? "" : namespaceName);
            writerPrefix = writer.getPrefix(namespaceName);
            if (!"".equals(namespaceName)) {
                if (writerPrefix != null && (prefix == null || prefix.equals(""))) {
                    writer.writeAttribute(writerPrefix, namespaceName, reader.getAttributeLocalName(i), reader.getAttributeValue(i));
                }
                else if (prefix != null && !"".equals(prefix) && !prefix.equals(writerPrefix)) {
                    writer.writeNamespace(prefix, namespaceName);
                    writer.writeAttribute(prefix, namespaceName, reader.getAttributeLocalName(i), reader.getAttributeValue(i));
                }
                else {
                    prefix = this.generateUniquePrefix(writer.getNamespaceContext());
                    writer.writeNamespace(prefix, namespaceName);
                    writer.writeAttribute(prefix, namespaceName, reader.getAttributeLocalName(i), reader.getAttributeValue(i));
                }
            }
            else {
                writer.writeAttribute(reader.getAttributeLocalName(i), reader.getAttributeValue(i));
            }
        }
    }
    
    private String generateUniquePrefix(final NamespaceContext nsCtxt) {
        String prefix;
        for (prefix = "ns" + StreamingOMSerializer.namespaceSuffix++; nsCtxt.getNamespaceURI(prefix) != null; prefix = "ns" + StreamingOMSerializer.namespaceSuffix++) {}
        return prefix;
    }
    
    private void serializeNamespace(final String prefix, final String URI, final XMLStreamWriter writer) throws XMLStreamException {
        final String prefix2 = writer.getPrefix(URI);
        if (prefix2 == null) {
            writer.writeNamespace(prefix, URI);
            writer.setPrefix(prefix, URI);
        }
    }
    
    private void serializeDataHandler() throws XMLStreamException {
        try {
            if (this.dataHandlerReader.isDeferred()) {
                this.dataHandlerWriter.writeDataHandler(this.dataHandlerReader.getDataHandlerProvider(), this.dataHandlerReader.getContentID(), this.dataHandlerReader.isOptimized());
            }
            else {
                this.dataHandlerWriter.writeDataHandler(this.dataHandlerReader.getDataHandler(), this.dataHandlerReader.getContentID(), this.dataHandlerReader.isOptimized());
            }
        }
        catch (final IOException ex) {
            throw new XMLStreamException("Error while reading data handler", ex);
        }
    }
    
    private void serializeDTD(final XMLStreamReader reader, final XMLStreamWriter writer) throws XMLStreamException {
        DTDReader dtdReader;
        try {
            dtdReader = (DTDReader)reader.getProperty(DTDReader.PROPERTY);
        }
        catch (final IllegalArgumentException ex) {
            dtdReader = null;
        }
        if (dtdReader == null) {
            throw new XMLStreamException("Cannot serialize the DTD because the XMLStreamReader doesn't support the DTDReader extension");
        }
        XMLStreamWriterUtils.writeDTD(writer, dtdReader.getRootName(), dtdReader.getPublicId(), dtdReader.getSystemId(), reader.getText());
    }
    
    static {
        log = LogFactory.getLog((Class)StreamingOMSerializer.class);
        StreamingOMSerializer.namespaceSuffix = 0;
    }
}
