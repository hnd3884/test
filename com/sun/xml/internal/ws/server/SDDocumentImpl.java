package com.sun.xml.internal.ws.server;

import java.net.MalformedURLException;
import com.sun.xml.internal.ws.util.RuntimeVersion;
import java.util.Iterator;
import com.sun.xml.internal.ws.wsdl.writer.DocumentLocationResolver;
import com.sun.xml.internal.ws.wsdl.writer.WSDLPatcher;
import com.sun.xml.internal.ws.api.server.DocumentAddressResolver;
import com.sun.xml.internal.ws.api.server.PortAddressResolver;
import javax.xml.stream.XMLStreamWriter;
import com.sun.xml.internal.ws.util.xml.XMLStreamReaderToXMLStreamWriter;
import com.sun.xml.internal.ws.api.streaming.XMLStreamWriterFactory;
import java.io.OutputStream;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import javax.xml.ws.WebServiceException;
import java.util.HashSet;
import com.sun.xml.internal.ws.wsdl.parser.ParserUtil;
import com.sun.xml.internal.ws.wsdl.parser.WSDLConstants;
import com.sun.xml.internal.ws.streaming.XMLStreamReaderUtil;
import java.util.Set;
import java.net.URL;
import com.sun.xml.internal.ws.wsdl.SDDocumentResolver;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.server.SDDocumentFilter;
import java.util.List;
import javax.xml.namespace.QName;
import com.sun.xml.internal.ws.api.server.SDDocument;
import com.sun.xml.internal.ws.api.server.SDDocumentSource;

public class SDDocumentImpl extends SDDocumentSource implements SDDocument
{
    private static final String NS_XSD = "http://www.w3.org/2001/XMLSchema";
    private static final QName SCHEMA_INCLUDE_QNAME;
    private static final QName SCHEMA_IMPORT_QNAME;
    private static final QName SCHEMA_REDEFINE_QNAME;
    private static final String VERSION_COMMENT;
    private final QName rootName;
    private final SDDocumentSource source;
    @Nullable
    List<SDDocumentFilter> filters;
    @Nullable
    SDDocumentResolver sddocResolver;
    private final URL url;
    private final Set<String> imports;
    
    public static SDDocumentImpl create(final SDDocumentSource src, final QName serviceName, final QName portTypeName) {
        final URL systemId = src.getSystemId();
        try {
            final XMLStreamReader reader = src.read();
            try {
                XMLStreamReaderUtil.nextElementContent(reader);
                final QName rootName = reader.getName();
                if (rootName.equals(WSDLConstants.QNAME_SCHEMA)) {
                    final String tns = ParserUtil.getMandatoryNonEmptyAttribute(reader, "targetNamespace");
                    final Set<String> importedDocs = new HashSet<String>();
                    while (XMLStreamReaderUtil.nextContent(reader) != 8) {
                        if (reader.getEventType() != 1) {
                            continue;
                        }
                        final QName name = reader.getName();
                        if (!SDDocumentImpl.SCHEMA_INCLUDE_QNAME.equals(name) && !SDDocumentImpl.SCHEMA_IMPORT_QNAME.equals(name) && !SDDocumentImpl.SCHEMA_REDEFINE_QNAME.equals(name)) {
                            continue;
                        }
                        final String importedDoc = reader.getAttributeValue(null, "schemaLocation");
                        if (importedDoc == null) {
                            continue;
                        }
                        importedDocs.add(new URL(src.getSystemId(), importedDoc).toString());
                    }
                    return new SchemaImpl(rootName, systemId, src, tns, importedDocs);
                }
                if (rootName.equals(WSDLConstants.QNAME_DEFINITIONS)) {
                    final String tns = ParserUtil.getMandatoryNonEmptyAttribute(reader, "targetNamespace");
                    boolean hasPortType = false;
                    boolean hasService = false;
                    final Set<String> importedDocs2 = new HashSet<String>();
                    final Set<QName> allServices = new HashSet<QName>();
                    while (XMLStreamReaderUtil.nextContent(reader) != 8) {
                        if (reader.getEventType() != 1) {
                            continue;
                        }
                        final QName name2 = reader.getName();
                        if (WSDLConstants.QNAME_PORT_TYPE.equals(name2)) {
                            final String pn = ParserUtil.getMandatoryNonEmptyAttribute(reader, "name");
                            if (portTypeName == null || !portTypeName.getLocalPart().equals(pn) || !portTypeName.getNamespaceURI().equals(tns)) {
                                continue;
                            }
                            hasPortType = true;
                        }
                        else if (WSDLConstants.QNAME_SERVICE.equals(name2)) {
                            final String sn = ParserUtil.getMandatoryNonEmptyAttribute(reader, "name");
                            final QName sqn = new QName(tns, sn);
                            allServices.add(sqn);
                            if (!serviceName.equals(sqn)) {
                                continue;
                            }
                            hasService = true;
                        }
                        else if (WSDLConstants.QNAME_IMPORT.equals(name2)) {
                            final String importedDoc2 = reader.getAttributeValue(null, "location");
                            if (importedDoc2 == null) {
                                continue;
                            }
                            importedDocs2.add(new URL(src.getSystemId(), importedDoc2).toString());
                        }
                        else {
                            if (!SDDocumentImpl.SCHEMA_INCLUDE_QNAME.equals(name2) && !SDDocumentImpl.SCHEMA_IMPORT_QNAME.equals(name2) && !SDDocumentImpl.SCHEMA_REDEFINE_QNAME.equals(name2)) {
                                continue;
                            }
                            final String importedDoc2 = reader.getAttributeValue(null, "schemaLocation");
                            if (importedDoc2 == null) {
                                continue;
                            }
                            importedDocs2.add(new URL(src.getSystemId(), importedDoc2).toString());
                        }
                    }
                    return new WSDLImpl(rootName, systemId, src, tns, hasPortType, hasService, importedDocs2, allServices);
                }
                return new SDDocumentImpl(rootName, systemId, src);
            }
            finally {
                reader.close();
            }
        }
        catch (final WebServiceException e) {
            throw new ServerRtException("runtime.parser.wsdl", new Object[] { systemId, e });
        }
        catch (final IOException e2) {
            throw new ServerRtException("runtime.parser.wsdl", new Object[] { systemId, e2 });
        }
        catch (final XMLStreamException e3) {
            throw new ServerRtException("runtime.parser.wsdl", new Object[] { systemId, e3 });
        }
    }
    
    protected SDDocumentImpl(final QName rootName, final URL url, final SDDocumentSource source) {
        this(rootName, url, source, new HashSet<String>());
    }
    
    protected SDDocumentImpl(final QName rootName, final URL url, final SDDocumentSource source, final Set<String> imports) {
        if (url == null) {
            throw new IllegalArgumentException("Cannot construct SDDocument with null URL.");
        }
        this.rootName = rootName;
        this.source = source;
        this.url = url;
        this.imports = imports;
    }
    
    void setFilters(final List<SDDocumentFilter> filters) {
        this.filters = filters;
    }
    
    void setResolver(final SDDocumentResolver sddocResolver) {
        this.sddocResolver = sddocResolver;
    }
    
    @Override
    public QName getRootName() {
        return this.rootName;
    }
    
    @Override
    public boolean isWSDL() {
        return false;
    }
    
    @Override
    public boolean isSchema() {
        return false;
    }
    
    @Override
    public URL getURL() {
        return this.url;
    }
    
    @Override
    public XMLStreamReader read(final XMLInputFactory xif) throws IOException, XMLStreamException {
        return this.source.read(xif);
    }
    
    @Override
    public XMLStreamReader read() throws IOException, XMLStreamException {
        return this.source.read();
    }
    
    @Override
    public URL getSystemId() {
        return this.url;
    }
    
    @Override
    public Set<String> getImports() {
        return this.imports;
    }
    
    public void writeTo(final OutputStream os) throws IOException {
        XMLStreamWriter w = null;
        try {
            w = XMLStreamWriterFactory.create(os, "UTF-8");
            w.writeStartDocument("UTF-8", "1.0");
            new XMLStreamReaderToXMLStreamWriter().bridge(this.source.read(), w);
            w.writeEndDocument();
        }
        catch (final XMLStreamException e) {
            final IOException ioe = new IOException(e.getMessage());
            ioe.initCause(e);
            throw ioe;
        }
        finally {
            try {
                if (w != null) {
                    w.close();
                }
            }
            catch (final XMLStreamException e2) {
                final IOException ioe2 = new IOException(e2.getMessage());
                ioe2.initCause(e2);
                throw ioe2;
            }
        }
    }
    
    @Override
    public void writeTo(final PortAddressResolver portAddressResolver, final DocumentAddressResolver resolver, final OutputStream os) throws IOException {
        XMLStreamWriter w = null;
        try {
            w = XMLStreamWriterFactory.create(os, "UTF-8");
            w.writeStartDocument("UTF-8", "1.0");
            this.writeTo(portAddressResolver, resolver, w);
            w.writeEndDocument();
        }
        catch (final XMLStreamException e) {
            final IOException ioe = new IOException(e.getMessage());
            ioe.initCause(e);
            throw ioe;
        }
        finally {
            try {
                if (w != null) {
                    w.close();
                }
            }
            catch (final XMLStreamException e2) {
                final IOException ioe2 = new IOException(e2.getMessage());
                ioe2.initCause(e2);
                throw ioe2;
            }
        }
    }
    
    @Override
    public void writeTo(final PortAddressResolver portAddressResolver, final DocumentAddressResolver resolver, XMLStreamWriter out) throws XMLStreamException, IOException {
        if (this.filters != null) {
            for (final SDDocumentFilter f : this.filters) {
                out = f.filter(this, out);
            }
        }
        final XMLStreamReader xsr = this.source.read();
        try {
            out.writeComment(SDDocumentImpl.VERSION_COMMENT);
            new WSDLPatcher(portAddressResolver, new DocumentLocationResolverImpl(resolver)).bridge(xsr, out);
        }
        finally {
            xsr.close();
        }
    }
    
    static {
        SCHEMA_INCLUDE_QNAME = new QName("http://www.w3.org/2001/XMLSchema", "include");
        SCHEMA_IMPORT_QNAME = new QName("http://www.w3.org/2001/XMLSchema", "import");
        SCHEMA_REDEFINE_QNAME = new QName("http://www.w3.org/2001/XMLSchema", "redefine");
        VERSION_COMMENT = " Published by JAX-WS RI (http://jax-ws.java.net). RI's version is " + RuntimeVersion.VERSION + ". ";
    }
    
    private static final class SchemaImpl extends SDDocumentImpl implements Schema
    {
        private final String targetNamespace;
        
        public SchemaImpl(final QName rootName, final URL url, final SDDocumentSource source, final String targetNamespace, final Set<String> imports) {
            super(rootName, url, source, imports);
            this.targetNamespace = targetNamespace;
        }
        
        @Override
        public String getTargetNamespace() {
            return this.targetNamespace;
        }
        
        @Override
        public boolean isSchema() {
            return true;
        }
    }
    
    private static final class WSDLImpl extends SDDocumentImpl implements WSDL
    {
        private final String targetNamespace;
        private final boolean hasPortType;
        private final boolean hasService;
        private final Set<QName> allServices;
        
        public WSDLImpl(final QName rootName, final URL url, final SDDocumentSource source, final String targetNamespace, final boolean hasPortType, final boolean hasService, final Set<String> imports, final Set<QName> allServices) {
            super(rootName, url, source, imports);
            this.targetNamespace = targetNamespace;
            this.hasPortType = hasPortType;
            this.hasService = hasService;
            this.allServices = allServices;
        }
        
        @Override
        public String getTargetNamespace() {
            return this.targetNamespace;
        }
        
        @Override
        public boolean hasPortType() {
            return this.hasPortType;
        }
        
        @Override
        public boolean hasService() {
            return this.hasService;
        }
        
        @Override
        public Set<QName> getAllServices() {
            return this.allServices;
        }
        
        @Override
        public boolean isWSDL() {
            return true;
        }
    }
    
    private class DocumentLocationResolverImpl implements DocumentLocationResolver
    {
        private DocumentAddressResolver delegate;
        
        DocumentLocationResolverImpl(final DocumentAddressResolver delegate) {
            this.delegate = delegate;
        }
        
        @Override
        public String getLocationFor(final String namespaceURI, final String systemId) {
            if (SDDocumentImpl.this.sddocResolver == null) {
                return systemId;
            }
            try {
                final URL ref = new URL(SDDocumentImpl.this.getURL(), systemId);
                final SDDocument refDoc = SDDocumentImpl.this.sddocResolver.resolve(ref.toExternalForm());
                if (refDoc == null) {
                    return systemId;
                }
                return this.delegate.getRelativeAddressFor(SDDocumentImpl.this, refDoc);
            }
            catch (final MalformedURLException mue) {
                return null;
            }
        }
    }
}
