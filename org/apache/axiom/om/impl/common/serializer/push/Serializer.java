package org.apache.axiom.om.impl.common.serializer.push;

import org.apache.axiom.ext.stax.datahandler.DataHandlerProvider;
import javax.activation.DataHandler;
import org.apache.axiom.util.stax.XMLStreamReaderUtils;
import org.apache.axiom.om.impl.common.util.OMDataSourceUtil;
import org.apache.axiom.om.OMDataSource;
import org.apache.axiom.om.DeferredParsingException;
import javax.xml.stream.XMLStreamException;
import org.apache.axiom.ext.stax.DTDReader;
import org.apache.axiom.ext.stax.datahandler.DataHandlerReader;
import javax.xml.stream.XMLStreamReader;
import java.util.Iterator;
import java.util.Set;
import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMNamespace;
import java.util.HashSet;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMSerializable;

public abstract class Serializer
{
    private static final String XSI_URI = "http://www.w3.org/2001/XMLSchema-instance";
    private static final String XSI_LOCAL_NAME = "type";
    private final OMSerializable root;
    private final OMElement contextElement;
    private final boolean namespaceRepairing;
    private final boolean preserveNamespaceContext;
    
    public Serializer(final OMSerializable root, final boolean namespaceRepairing, final boolean preserveNamespaceContext) {
        this.root = root;
        if (root instanceof OMNode) {
            final OMContainer parent = ((OMNode)root).getParent();
            if (parent instanceof OMElement) {
                this.contextElement = (OMElement)parent;
            }
            else {
                this.contextElement = null;
            }
        }
        else {
            this.contextElement = null;
        }
        this.namespaceRepairing = namespaceRepairing;
        this.preserveNamespaceContext = preserveNamespaceContext;
    }
    
    public final void serializeStartpart(final OMElement element) throws OutputException {
        OMNamespace ns = element.getNamespace();
        if (ns == null) {
            this.internalBeginStartElement("", "", element.getLocalName());
        }
        else {
            this.internalBeginStartElement(ns.getPrefix(), ns.getNamespaceURI(), element.getLocalName());
        }
        if (this.preserveNamespaceContext && element == this.root) {
            final Set seenPrefixes = new HashSet();
            OMElement current = element;
            while (true) {
                final Iterator it = current.getAllDeclaredNamespaces();
                while (it.hasNext()) {
                    ns = it.next();
                    if (seenPrefixes.add(ns.getPrefix())) {
                        this.mapNamespace(ns.getPrefix(), ns.getNamespaceURI(), true, false);
                    }
                }
                final OMContainer parent = current.getParent();
                if (!(parent instanceof OMElement)) {
                    break;
                }
                current = (OMElement)parent;
            }
        }
        else {
            final Iterator it2 = element.getAllDeclaredNamespaces();
            while (it2.hasNext()) {
                ns = it2.next();
                this.mapNamespace(ns.getPrefix(), ns.getNamespaceURI(), true, false);
            }
        }
        final Iterator it2 = element.getAllAttributes();
        while (it2.hasNext()) {
            final OMAttribute attr = it2.next();
            ns = attr.getNamespace();
            if (ns == null) {
                this.processAttribute("", "", attr.getLocalName(), attr.getAttributeType(), attr.getAttributeValue());
            }
            else {
                this.processAttribute(ns.getPrefix(), ns.getNamespaceURI(), attr.getLocalName(), attr.getAttributeType(), attr.getAttributeValue());
            }
        }
        this.finishStartElement();
    }
    
    public final void copyEvent(final XMLStreamReader reader, final DataHandlerReader dataHandlerReader) throws OutputException {
        try {
            final int eventType = reader.getEventType();
            switch (eventType) {
                case 11: {
                    DTDReader dtdReader;
                    try {
                        dtdReader = (DTDReader)reader.getProperty(DTDReader.PROPERTY);
                    }
                    catch (final IllegalArgumentException ex2) {
                        dtdReader = null;
                    }
                    if (dtdReader == null) {
                        throw new XMLStreamException("Cannot serialize the DTD because the XMLStreamReader doesn't support the DTDReader extension");
                    }
                    this.writeDTD(dtdReader.getRootName(), dtdReader.getPublicId(), dtdReader.getSystemId(), reader.getText());
                    break;
                }
                case 1: {
                    this.internalBeginStartElement(normalize(reader.getPrefix()), normalize(reader.getNamespaceURI()), reader.getLocalName());
                    for (int i = 0, count = reader.getNamespaceCount(); i < count; ++i) {
                        this.mapNamespace(normalize(reader.getNamespacePrefix(i)), normalize(reader.getNamespaceURI(i)), true, false);
                    }
                    for (int i = 0, count = reader.getAttributeCount(); i < count; ++i) {
                        this.processAttribute(normalize(reader.getAttributePrefix(i)), normalize(reader.getAttributeNamespace(i)), reader.getAttributeLocalName(i), reader.getAttributeType(i), reader.getAttributeValue(i));
                    }
                    this.finishStartElement();
                    break;
                }
                case 2: {
                    this.writeEndElement();
                    break;
                }
                case 4: {
                    if (dataHandlerReader == null || !dataHandlerReader.isBinary())
                    if (dataHandlerReader.isDeferred()) {
                        this.writeDataHandler(dataHandlerReader.getDataHandlerProvider(), dataHandlerReader.getContentID(), dataHandlerReader.isOptimized());
                        break;
                    }
                    this.writeDataHandler(dataHandlerReader.getDataHandler(), dataHandlerReader.getContentID(), dataHandlerReader.isOptimized());
                    break;
                }
                case 6:
                case 12: {
                    this.writeText(eventType, reader.getText());
                    break;
                }
                case 3: {
                    this.writeProcessingInstruction(reader.getPITarget(), reader.getPIData());
                    break;
                }
                case 5: {
                    this.writeComment(reader.getText());
                    break;
                }
                case 9: {
                    this.writeEntityRef(reader.getLocalName());
                    break;
                }
                default: {
                    throw new IllegalStateException();
                }
            }
        }
        catch (final XMLStreamException ex) {
            throw new DeferredParsingException((Throwable)ex);
        }
    }
    
    private static String normalize(final String s) {
        return (s == null) ? "" : s;
    }
    
    private void internalBeginStartElement(final String prefix, final String namespaceURI, final String localName) throws OutputException {
        this.beginStartElement(prefix, namespaceURI, localName);
        this.mapNamespace(prefix, namespaceURI, false, false);
    }
    
    private void processAttribute(final String prefix, final String namespaceURI, final String localName, final String type, final String value) throws OutputException {
        this.mapNamespace(prefix, namespaceURI, false, true);
        if (this.namespaceRepairing && this.contextElement != null && namespaceURI.equals("http://www.w3.org/2001/XMLSchema-instance") && localName.equals("type")) {
            final String trimmedValue = value.trim();
            if (trimmedValue.indexOf(":") > 0) {
                final String refPrefix = trimmedValue.substring(0, trimmedValue.indexOf(":"));
                final OMNamespace ns = this.contextElement.findNamespaceURI(refPrefix);
                if (ns != null) {
                    this.mapNamespace(refPrefix, ns.getNamespaceURI(), false, true);
                }
            }
        }
        this.addAttribute(prefix, namespaceURI, localName, type, value);
    }
    
    private void mapNamespace(final String prefix, final String namespaceURI, final boolean fromDecl, final boolean attr) throws OutputException {
        if (this.namespaceRepairing) {
            if (this.isAssociated(prefix, namespaceURI)) {
                return;
            }
            if (prefix.length() == 0 && namespaceURI.length() == 0 && attr) {
                return;
            }
            this.addNamespace(prefix, namespaceURI);
        }
        else if (fromDecl) {
            this.addNamespace(prefix, namespaceURI);
        }
    }
    
    public final void serialize(final OMDataSource dataSource) throws OutputException {
        if (OMDataSourceUtil.isPullDataSource(dataSource)) {
            try {
                final XMLStreamReader reader = dataSource.getReader();
                final DataHandlerReader dataHandlerReader = XMLStreamReaderUtils.getDataHandlerReader(reader);
                int depth = 0;
                int eventType;
                while ((eventType = reader.next()) != 8) {
                    if (eventType == 1) {
                        ++depth;
                    }
                    if (depth > 0) {
                        this.copyEvent(reader, dataHandlerReader);
                    }
                    if (eventType == 2) {
                        --depth;
                    }
                }
                reader.close();
                return;
            }
            catch (final XMLStreamException ex) {
                throw new DeferredParsingException((Throwable)ex);
            }
        }
        this.serializePushOMDataSource(dataSource);
    }
    
    protected abstract boolean isAssociated(final String p0, final String p1) throws OutputException;
    
    public abstract void writeStartDocument(final String p0) throws OutputException;
    
    public abstract void writeStartDocument(final String p0, final String p1) throws OutputException;
    
    public abstract void writeDTD(final String p0, final String p1, final String p2, final String p3) throws OutputException;
    
    protected abstract void beginStartElement(final String p0, final String p1, final String p2) throws OutputException;
    
    protected abstract void addNamespace(final String p0, final String p1) throws OutputException;
    
    protected abstract void addAttribute(final String p0, final String p1, final String p2, final String p3, final String p4) throws OutputException;
    
    protected abstract void finishStartElement() throws OutputException;
    
    public abstract void writeEndElement() throws OutputException;
    
    public abstract void writeText(final int p0, final String p1) throws OutputException;
    
    public abstract void writeComment(final String p0) throws OutputException;
    
    public abstract void writeProcessingInstruction(final String p0, final String p1) throws OutputException;
    
    public abstract void writeEntityRef(final String p0) throws OutputException;
    
    public abstract void writeDataHandler(final DataHandler p0, final String p1, final boolean p2) throws OutputException;
    
    public abstract void writeDataHandler(final DataHandlerProvider p0, final String p1, final boolean p2) throws OutputException;
    
    protected abstract void serializePushOMDataSource(final OMDataSource p0) throws OutputException;
    
    public abstract void writeEndDocument() throws OutputException;
}
