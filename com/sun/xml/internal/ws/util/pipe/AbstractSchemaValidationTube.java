package com.sun.xml.internal.ws.util.pipe;

import java.io.InputStream;
import java.net.URI;
import org.w3c.dom.ls.LSInput;
import java.net.MalformedURLException;
import com.sun.xml.internal.ws.server.SDDocumentImpl;
import javax.xml.namespace.QName;
import com.sun.xml.internal.stream.buffer.XMLStreamBuffer;
import com.sun.xml.internal.ws.api.server.SDDocumentSource;
import java.net.URL;
import com.sun.xml.internal.stream.buffer.XMLStreamBufferResult;
import org.w3c.dom.ls.LSResourceResolver;
import com.sun.xml.internal.ws.wsdl.SDDocumentResolver;
import com.sun.istack.internal.NotNull;
import org.xml.sax.SAXException;
import com.sun.xml.internal.ws.api.message.Message;
import org.xml.sax.ErrorHandler;
import com.sun.xml.internal.ws.developer.ValidationErrorHandler;
import com.sun.xml.internal.ws.api.message.Packet;
import java.io.StringReader;
import java.io.Reader;
import java.util.Enumeration;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Attr;
import com.sun.istack.internal.Nullable;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.xml.sax.helpers.NamespaceSupport;
import org.w3c.dom.Element;
import java.util.Iterator;
import java.util.Collection;
import java.util.logging.Level;
import javax.xml.transform.dom.DOMSource;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.Result;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import javax.xml.ws.WebServiceException;
import java.io.OutputStream;
import com.sun.xml.internal.ws.api.server.PortAddressResolver;
import com.sun.xml.internal.ws.util.ByteArrayBuffer;
import org.w3c.dom.Document;
import com.sun.xml.internal.ws.api.server.SDDocument;
import javax.xml.validation.Validator;
import com.sun.xml.internal.ws.api.pipe.TubeCloner;
import com.sun.xml.internal.ws.util.xml.XmlUtil;
import com.sun.xml.internal.ws.api.pipe.Tube;
import javax.xml.validation.SchemaFactory;
import com.sun.xml.internal.ws.api.server.DocumentAddressResolver;
import com.sun.xml.internal.ws.developer.SchemaValidationFeature;
import com.sun.xml.internal.ws.api.WSBinding;
import java.util.logging.Logger;
import com.sun.xml.internal.ws.api.pipe.helper.AbstractFilterTubeImpl;

public abstract class AbstractSchemaValidationTube extends AbstractFilterTubeImpl
{
    private static final Logger LOGGER;
    protected final WSBinding binding;
    protected final SchemaValidationFeature feature;
    protected final DocumentAddressResolver resolver;
    protected final SchemaFactory sf;
    
    public AbstractSchemaValidationTube(final WSBinding binding, final Tube next) {
        super(next);
        this.resolver = new ValidationDocumentAddressResolver();
        this.binding = binding;
        this.feature = binding.getFeature(SchemaValidationFeature.class);
        this.sf = XmlUtil.allowExternalAccess(SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema"), "file", false);
    }
    
    protected AbstractSchemaValidationTube(final AbstractSchemaValidationTube that, final TubeCloner cloner) {
        super(that, cloner);
        this.resolver = new ValidationDocumentAddressResolver();
        this.binding = that.binding;
        this.feature = that.feature;
        this.sf = that.sf;
    }
    
    protected abstract Validator getValidator();
    
    protected abstract boolean isNoValidation();
    
    private Document createDOM(final SDDocument doc) {
        final ByteArrayBuffer bab = new ByteArrayBuffer();
        try {
            doc.writeTo(null, this.resolver, bab);
        }
        catch (final IOException ioe) {
            throw new WebServiceException(ioe);
        }
        final Transformer trans = XmlUtil.newTransformer();
        final Source source = new StreamSource(bab.newInputStream(), null);
        final DOMResult result = new DOMResult();
        try {
            trans.transform(source, result);
        }
        catch (final TransformerException te) {
            throw new WebServiceException(te);
        }
        return (Document)result.getNode();
    }
    
    private void updateMultiSchemaForTns(final String tns, final String systemId, final Map<String, List<String>> schemas) {
        List<String> docIdList = schemas.get(tns);
        if (docIdList == null) {
            docIdList = new ArrayList<String>();
            schemas.put(tns, docIdList);
        }
        docIdList.add(systemId);
    }
    
    protected Source[] getSchemaSources(final Iterable<SDDocument> docs, final MetadataResolverImpl mdresolver) {
        final Map<String, DOMSource> inlinedSchemas = new HashMap<String, DOMSource>();
        final Map<String, List<String>> multiSchemaForTns = new HashMap<String, List<String>>();
        for (final SDDocument sdoc : docs) {
            if (sdoc.isWSDL()) {
                final Document dom = this.createDOM(sdoc);
                this.addSchemaFragmentSource(dom, sdoc.getURL().toExternalForm(), inlinedSchemas);
            }
            else {
                if (!sdoc.isSchema()) {
                    continue;
                }
                this.updateMultiSchemaForTns(((SDDocument.Schema)sdoc).getTargetNamespace(), sdoc.getURL().toExternalForm(), multiSchemaForTns);
            }
        }
        if (AbstractSchemaValidationTube.LOGGER.isLoggable(Level.FINE)) {
            AbstractSchemaValidationTube.LOGGER.log(Level.FINE, "WSDL inlined schema fragment documents(these are used to create a pseudo schema) = {0}", inlinedSchemas.keySet());
        }
        for (final DOMSource src : inlinedSchemas.values()) {
            final String tns = this.getTargetNamespace(src);
            this.updateMultiSchemaForTns(tns, src.getSystemId(), multiSchemaForTns);
        }
        if (multiSchemaForTns.isEmpty()) {
            return new Source[0];
        }
        if (multiSchemaForTns.size() == 1 && multiSchemaForTns.values().iterator().next().size() == 1) {
            final String systemId = multiSchemaForTns.values().iterator().next().get(0);
            return new Source[] { inlinedSchemas.get(systemId) };
        }
        mdresolver.addSchemas(inlinedSchemas.values());
        final Map<String, String> oneSchemaForTns = new HashMap<String, String>();
        int i = 0;
        for (final Map.Entry<String, List<String>> e : multiSchemaForTns.entrySet()) {
            final List<String> sameTnsSchemas = e.getValue();
            String systemId2;
            if (sameTnsSchemas.size() > 1) {
                systemId2 = "file:x-jax-ws-include-" + i++;
                final Source src2 = this.createSameTnsPseudoSchema(e.getKey(), sameTnsSchemas, systemId2);
                mdresolver.addSchema(src2);
            }
            else {
                systemId2 = sameTnsSchemas.get(0);
            }
            oneSchemaForTns.put(e.getKey(), systemId2);
        }
        final Source pseudoSchema = this.createMasterPseudoSchema(oneSchemaForTns);
        return new Source[] { pseudoSchema };
    }
    
    @Nullable
    private void addSchemaFragmentSource(final Document doc, final String systemId, final Map<String, DOMSource> map) {
        final Element e = doc.getDocumentElement();
        assert e.getNamespaceURI().equals("http://schemas.xmlsoap.org/wsdl/");
        assert e.getLocalName().equals("definitions");
        final NodeList typesList = e.getElementsByTagNameNS("http://schemas.xmlsoap.org/wsdl/", "types");
        for (int i = 0; i < typesList.getLength(); ++i) {
            final NodeList schemaList = ((Element)typesList.item(i)).getElementsByTagNameNS("http://www.w3.org/2001/XMLSchema", "schema");
            for (int j = 0; j < schemaList.getLength(); ++j) {
                final Element elem = (Element)schemaList.item(j);
                final NamespaceSupport nss = new NamespaceSupport();
                this.buildNamespaceSupport(nss, elem);
                this.patchDOMFragment(nss, elem);
                final String docId = systemId + "#schema" + j;
                map.put(docId, new DOMSource(elem, docId));
            }
        }
    }
    
    private void buildNamespaceSupport(final NamespaceSupport nss, final Node node) {
        if (node == null || node.getNodeType() != 1) {
            return;
        }
        this.buildNamespaceSupport(nss, node.getParentNode());
        nss.pushContext();
        final NamedNodeMap atts = node.getAttributes();
        for (int i = 0; i < atts.getLength(); ++i) {
            final Attr a = (Attr)atts.item(i);
            if ("xmlns".equals(a.getPrefix())) {
                nss.declarePrefix(a.getLocalName(), a.getValue());
            }
            else if ("xmlns".equals(a.getName())) {
                nss.declarePrefix("", a.getValue());
            }
        }
    }
    
    @Nullable
    private void patchDOMFragment(final NamespaceSupport nss, final Element elem) {
        final NamedNodeMap atts = elem.getAttributes();
        final Enumeration en = nss.getPrefixes();
        while (en.hasMoreElements()) {
            final String prefix = en.nextElement();
            for (int i = 0; i < atts.getLength(); ++i) {
                final Attr a = (Attr)atts.item(i);
                if (!"xmlns".equals(a.getPrefix()) || !a.getLocalName().equals(prefix)) {
                    if (AbstractSchemaValidationTube.LOGGER.isLoggable(Level.FINE)) {
                        AbstractSchemaValidationTube.LOGGER.log(Level.FINE, "Patching with xmlns:{0}={1}", new Object[] { prefix, nss.getURI(prefix) });
                    }
                    elem.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:" + prefix, nss.getURI(prefix));
                }
            }
        }
    }
    
    @Nullable
    private Source createSameTnsPseudoSchema(final String tns, final Collection<String> docs, final String pseudoSystemId) {
        assert docs.size() > 1;
        final StringBuilder sb = new StringBuilder("<xsd:schema xmlns:xsd='http://www.w3.org/2001/XMLSchema'");
        if (!tns.equals("")) {
            sb.append(" targetNamespace='").append(tns).append("'");
        }
        sb.append(">\n");
        for (final String systemId : docs) {
            sb.append("<xsd:include schemaLocation='").append(systemId).append("'/>\n");
        }
        sb.append("</xsd:schema>\n");
        if (AbstractSchemaValidationTube.LOGGER.isLoggable(Level.FINE)) {
            AbstractSchemaValidationTube.LOGGER.log(Level.FINE, "Pseudo Schema for the same tns={0}is {1}", new Object[] { tns, sb });
        }
        return new StreamSource(pseudoSystemId) {
            @Override
            public Reader getReader() {
                return new StringReader(sb.toString());
            }
        };
    }
    
    private Source createMasterPseudoSchema(final Map<String, String> docs) {
        final StringBuilder sb = new StringBuilder("<xsd:schema xmlns:xsd='http://www.w3.org/2001/XMLSchema' targetNamespace='urn:x-jax-ws-master'>\n");
        for (final Map.Entry<String, String> e : docs.entrySet()) {
            final String systemId = e.getValue();
            final String ns = e.getKey();
            sb.append("<xsd:import schemaLocation='").append(systemId).append("'");
            if (!ns.equals("")) {
                sb.append(" namespace='").append(ns).append("'");
            }
            sb.append("/>\n");
        }
        sb.append("</xsd:schema>");
        if (AbstractSchemaValidationTube.LOGGER.isLoggable(Level.FINE)) {
            AbstractSchemaValidationTube.LOGGER.log(Level.FINE, "Master Pseudo Schema = {0}", sb);
        }
        return new StreamSource("file:x-jax-ws-master-doc") {
            @Override
            public Reader getReader() {
                return new StringReader(sb.toString());
            }
        };
    }
    
    protected void doProcess(final Packet packet) throws SAXException {
        this.getValidator().reset();
        final Class<? extends ValidationErrorHandler> handlerClass = this.feature.getErrorHandler();
        ValidationErrorHandler handler;
        try {
            handler = (ValidationErrorHandler)handlerClass.newInstance();
        }
        catch (final Exception e) {
            throw new WebServiceException(e);
        }
        handler.setPacket(packet);
        this.getValidator().setErrorHandler(handler);
        final Message msg = packet.getMessage().copy();
        final Source source = msg.readPayloadAsSource();
        try {
            this.getValidator().validate(source);
        }
        catch (final IOException e2) {
            throw new WebServiceException(e2);
        }
    }
    
    private String getTargetNamespace(final DOMSource src) {
        final Element elem = (Element)src.getNode();
        return elem.getAttribute("targetNamespace");
    }
    
    static {
        LOGGER = Logger.getLogger(AbstractSchemaValidationTube.class.getName());
    }
    
    private static class ValidationDocumentAddressResolver implements DocumentAddressResolver
    {
        @Nullable
        @Override
        public String getRelativeAddressFor(@NotNull final SDDocument current, @NotNull final SDDocument referenced) {
            AbstractSchemaValidationTube.LOGGER.log(Level.FINE, "Current = {0} resolved relative={1}", new Object[] { current.getURL(), referenced.getURL() });
            return referenced.getURL().toExternalForm();
        }
    }
    
    protected class MetadataResolverImpl implements SDDocumentResolver, LSResourceResolver
    {
        final Map<String, SDDocument> docs;
        final Map<String, SDDocument> nsMapping;
        
        public MetadataResolverImpl() {
            this.docs = new HashMap<String, SDDocument>();
            this.nsMapping = new HashMap<String, SDDocument>();
        }
        
        public MetadataResolverImpl(final Iterable<SDDocument> it) {
            this.docs = new HashMap<String, SDDocument>();
            this.nsMapping = new HashMap<String, SDDocument>();
            for (final SDDocument doc : it) {
                if (doc.isSchema()) {
                    this.docs.put(doc.getURL().toExternalForm(), doc);
                    this.nsMapping.put(((SDDocument.Schema)doc).getTargetNamespace(), doc);
                }
            }
        }
        
        void addSchema(final Source schema) {
            assert schema.getSystemId() != null;
            final String systemId = schema.getSystemId();
            try {
                final XMLStreamBufferResult xsbr = XmlUtil.identityTransform(schema, new XMLStreamBufferResult());
                final SDDocumentSource sds = SDDocumentSource.create(new URL(systemId), xsbr.getXMLStreamBuffer());
                final SDDocument sdoc = SDDocumentImpl.create(sds, new QName(""), new QName(""));
                this.docs.put(systemId, sdoc);
                this.nsMapping.put(((SDDocument.Schema)sdoc).getTargetNamespace(), sdoc);
            }
            catch (final Exception ex) {
                AbstractSchemaValidationTube.LOGGER.log(Level.WARNING, "Exception in adding schemas to resolver", ex);
            }
        }
        
        void addSchemas(final Collection<? extends Source> schemas) {
            for (final Source src : schemas) {
                this.addSchema(src);
            }
        }
        
        @Override
        public SDDocument resolve(final String systemId) {
            SDDocument sdi = this.docs.get(systemId);
            if (sdi == null) {
                SDDocumentSource sds;
                try {
                    sds = SDDocumentSource.create(new URL(systemId));
                }
                catch (final MalformedURLException e) {
                    throw new WebServiceException(e);
                }
                sdi = SDDocumentImpl.create(sds, new QName(""), new QName(""));
                this.docs.put(systemId, sdi);
            }
            return sdi;
        }
        
        @Override
        public LSInput resolveResource(final String type, final String namespaceURI, final String publicId, final String systemId, final String baseURI) {
            if (AbstractSchemaValidationTube.LOGGER.isLoggable(Level.FINE)) {
                AbstractSchemaValidationTube.LOGGER.log(Level.FINE, "type={0} namespaceURI={1} publicId={2} systemId={3} baseURI={4}", new Object[] { type, namespaceURI, publicId, systemId, baseURI });
            }
            try {
                SDDocument doc;
                if (systemId == null) {
                    doc = this.nsMapping.get(namespaceURI);
                }
                else {
                    final URI rel = (baseURI != null) ? new URI(baseURI).resolve(systemId) : new URI(systemId);
                    doc = this.docs.get(rel.toString());
                }
                if (doc != null) {
                    return new LSInput() {
                        @Override
                        public Reader getCharacterStream() {
                            return null;
                        }
                        
                        @Override
                        public void setCharacterStream(final Reader characterStream) {
                            throw new UnsupportedOperationException();
                        }
                        
                        @Override
                        public InputStream getByteStream() {
                            final ByteArrayBuffer bab = new ByteArrayBuffer();
                            try {
                                doc.writeTo(null, AbstractSchemaValidationTube.this.resolver, bab);
                            }
                            catch (final IOException ioe) {
                                throw new WebServiceException(ioe);
                            }
                            return bab.newInputStream();
                        }
                        
                        @Override
                        public void setByteStream(final InputStream byteStream) {
                            throw new UnsupportedOperationException();
                        }
                        
                        @Override
                        public String getStringData() {
                            return null;
                        }
                        
                        @Override
                        public void setStringData(final String stringData) {
                            throw new UnsupportedOperationException();
                        }
                        
                        @Override
                        public String getSystemId() {
                            return doc.getURL().toExternalForm();
                        }
                        
                        @Override
                        public void setSystemId(final String systemId) {
                            throw new UnsupportedOperationException();
                        }
                        
                        @Override
                        public String getPublicId() {
                            return null;
                        }
                        
                        @Override
                        public void setPublicId(final String publicId) {
                            throw new UnsupportedOperationException();
                        }
                        
                        @Override
                        public String getBaseURI() {
                            return doc.getURL().toExternalForm();
                        }
                        
                        @Override
                        public void setBaseURI(final String baseURI) {
                            throw new UnsupportedOperationException();
                        }
                        
                        @Override
                        public String getEncoding() {
                            return null;
                        }
                        
                        @Override
                        public void setEncoding(final String encoding) {
                            throw new UnsupportedOperationException();
                        }
                        
                        @Override
                        public boolean getCertifiedText() {
                            return false;
                        }
                        
                        @Override
                        public void setCertifiedText(final boolean certifiedText) {
                            throw new UnsupportedOperationException();
                        }
                    };
                }
            }
            catch (final Exception e) {
                AbstractSchemaValidationTube.LOGGER.log(Level.WARNING, "Exception in LSResourceResolver impl", e);
            }
            if (AbstractSchemaValidationTube.LOGGER.isLoggable(Level.FINE)) {
                AbstractSchemaValidationTube.LOGGER.log(Level.FINE, "Don''t know about systemId={0} baseURI={1}", new Object[] { systemId, baseURI });
            }
            return null;
        }
    }
}
