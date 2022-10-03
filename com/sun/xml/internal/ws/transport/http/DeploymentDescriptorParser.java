package com.sun.xml.internal.ws.transport.http;

import java.util.Arrays;
import com.sun.xml.internal.ws.util.HandlerAnnotationInfo;
import javax.xml.ws.soap.SOAPBinding;
import com.sun.xml.internal.ws.handler.HandlerChainsModel;
import javax.xml.ws.WebServiceException;
import com.sun.xml.internal.ws.util.xml.XmlUtil;
import org.xml.sax.EntityResolver;
import com.sun.xml.internal.ws.util.exception.LocatableWebServiceException;
import com.oracle.webservices.internal.api.databinding.DatabindingModeFeature;
import com.sun.xml.internal.ws.binding.WebServiceFeatureList;
import com.sun.xml.internal.ws.resources.ServerMessages;
import com.sun.xml.internal.ws.api.BindingID;
import javax.xml.ws.soap.MTOMFeature;
import com.sun.xml.internal.ws.api.WSBinding;
import com.oracle.webservices.internal.api.databinding.ExternalMetadataFeature;
import com.sun.xml.internal.ws.api.databinding.MetadataReader;
import com.sun.xml.internal.ws.streaming.Attributes;
import java.util.Collection;
import com.sun.xml.internal.ws.api.server.Invoker;
import com.sun.xml.internal.ws.api.server.WSEndpoint;
import javax.xml.ws.WebServiceFeature;
import com.sun.xml.internal.ws.server.EndpointFactory;
import com.sun.xml.internal.ws.resources.WsservletMessages;
import java.util.ArrayList;
import java.net.URL;
import java.util.Iterator;
import java.io.FileInputStream;
import java.io.File;
import com.sun.istack.internal.NotNull;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;
import javax.xml.stream.XMLStreamException;
import com.sun.xml.internal.ws.server.ServerRtException;
import com.sun.xml.internal.ws.streaming.XMLStreamReaderUtil;
import java.io.Closeable;
import com.sun.xml.internal.ws.streaming.TidyXMLStreamReader;
import com.sun.xml.internal.ws.api.streaming.XMLStreamReaderFactory;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.HashMap;
import java.util.HashSet;
import com.sun.xml.internal.ws.api.server.SDDocumentSource;
import java.util.Map;
import java.util.Set;
import com.sun.xml.internal.ws.api.server.Container;
import java.util.logging.Logger;
import java.util.List;
import javax.xml.namespace.QName;

public class DeploymentDescriptorParser<A>
{
    public static final String NS_RUNTIME = "http://java.sun.com/xml/ns/jax-ws/ri/runtime";
    public static final String JAXWS_WSDL_DD_DIR = "WEB-INF/wsdl";
    public static final QName QNAME_ENDPOINTS;
    public static final QName QNAME_ENDPOINT;
    public static final QName QNAME_EXT_METADA;
    public static final String ATTR_FILE = "file";
    public static final String ATTR_RESOURCE = "resource";
    public static final String ATTR_VERSION = "version";
    public static final String ATTR_NAME = "name";
    public static final String ATTR_IMPLEMENTATION = "implementation";
    public static final String ATTR_WSDL = "wsdl";
    public static final String ATTR_SERVICE = "service";
    public static final String ATTR_PORT = "port";
    public static final String ATTR_URL_PATTERN = "url-pattern";
    public static final String ATTR_ENABLE_MTOM = "enable-mtom";
    public static final String ATTR_MTOM_THRESHOLD_VALUE = "mtom-threshold-value";
    public static final String ATTR_BINDING = "binding";
    public static final String ATTR_DATABINDING = "databinding";
    public static final List<String> ATTRVALUE_SUPPORTED_VERSIONS;
    private static final Logger logger;
    private final Container container;
    private final ClassLoader classLoader;
    private final ResourceLoader loader;
    private final AdapterFactory<A> adapterFactory;
    private final Set<String> names;
    private final Map<String, SDDocumentSource> docs;
    
    public DeploymentDescriptorParser(final ClassLoader cl, final ResourceLoader loader, final Container container, final AdapterFactory<A> adapterFactory) throws MalformedURLException {
        this.names = new HashSet<String>();
        this.docs = new HashMap<String, SDDocumentSource>();
        this.classLoader = cl;
        this.loader = loader;
        this.container = container;
        this.adapterFactory = adapterFactory;
        this.collectDocs("/WEB-INF/wsdl/");
        DeploymentDescriptorParser.logger.log(Level.FINE, "war metadata={0}", this.docs);
    }
    
    @NotNull
    public List<A> parse(final String systemId, final InputStream is) {
        XMLStreamReader reader = null;
        try {
            reader = new TidyXMLStreamReader(XMLStreamReaderFactory.create(systemId, is, true), is);
            XMLStreamReaderUtil.nextElementContent(reader);
            return this.parseAdapters(reader);
        }
        finally {
            if (reader != null) {
                try {
                    reader.close();
                }
                catch (final XMLStreamException e) {
                    throw new ServerRtException("runtime.parser.xmlReader", new Object[] { e });
                }
            }
            try {
                is.close();
            }
            catch (final IOException ex) {}
        }
    }
    
    @NotNull
    public List<A> parse(final File f) throws IOException {
        final FileInputStream in = new FileInputStream(f);
        try {
            return this.parse(f.getPath(), in);
        }
        finally {
            in.close();
        }
    }
    
    private void collectDocs(final String dirPath) throws MalformedURLException {
        final Set<String> paths = this.loader.getResourcePaths(dirPath);
        if (paths != null) {
            for (final String path : paths) {
                if (path.endsWith("/")) {
                    if (path.endsWith("/CVS/")) {
                        continue;
                    }
                    if (path.endsWith("/.svn/")) {
                        continue;
                    }
                    this.collectDocs(path);
                }
                else {
                    final URL res = this.loader.getResource(path);
                    this.docs.put(res.toString(), SDDocumentSource.create(res));
                }
            }
        }
    }
    
    private List<A> parseAdapters(final XMLStreamReader reader) {
        if (!reader.getName().equals(DeploymentDescriptorParser.QNAME_ENDPOINTS)) {
            failWithFullName("runtime.parser.invalidElement", reader);
        }
        final List<A> adapters = new ArrayList<A>();
        Attributes attrs = XMLStreamReaderUtil.getAttributes(reader);
        final String version = this.getMandatoryNonEmptyAttribute(reader, attrs, "version");
        if (!DeploymentDescriptorParser.ATTRVALUE_SUPPORTED_VERSIONS.contains(version)) {
            failWithLocalName("runtime.parser.invalidVersionNumber", reader, version);
        }
        while (XMLStreamReaderUtil.nextElementContent(reader) != 2) {
            if (reader.getName().equals(DeploymentDescriptorParser.QNAME_ENDPOINT)) {
                attrs = XMLStreamReaderUtil.getAttributes(reader);
                final String name = this.getMandatoryNonEmptyAttribute(reader, attrs, "name");
                if (!this.names.add(name)) {
                    DeploymentDescriptorParser.logger.warning(WsservletMessages.SERVLET_WARNING_DUPLICATE_ENDPOINT_NAME());
                }
                final String implementationName = this.getMandatoryNonEmptyAttribute(reader, attrs, "implementation");
                final Class<?> implementorClass = this.getImplementorClass(implementationName, reader);
                MetadataReader metadataReader = null;
                ExternalMetadataFeature externalMetadataFeature = null;
                XMLStreamReaderUtil.nextElementContent(reader);
                if (reader.getEventType() != 2) {
                    externalMetadataFeature = this.configureExternalMetadataReader(reader);
                    if (externalMetadataFeature != null) {
                        metadataReader = externalMetadataFeature.getMetadataReader(implementorClass.getClassLoader(), false);
                    }
                }
                QName serviceName = this.getQNameAttribute(attrs, "service");
                if (serviceName == null) {
                    serviceName = EndpointFactory.getDefaultServiceName(implementorClass, metadataReader);
                }
                QName portName = this.getQNameAttribute(attrs, "port");
                if (portName == null) {
                    portName = EndpointFactory.getDefaultPortName(serviceName, implementorClass, metadataReader);
                }
                final String enable_mtom = this.getAttribute(attrs, "enable-mtom");
                final String mtomThreshold = this.getAttribute(attrs, "mtom-threshold-value");
                final String dbMode = this.getAttribute(attrs, "databinding");
                String bindingId = this.getAttribute(attrs, "binding");
                if (bindingId != null) {
                    bindingId = getBindingIdForToken(bindingId);
                }
                final WSBinding binding = createBinding(bindingId, implementorClass, enable_mtom, mtomThreshold, dbMode);
                if (externalMetadataFeature != null) {
                    binding.getFeatures().mergeFeatures(new WebServiceFeature[] { externalMetadataFeature }, true);
                }
                final String urlPattern = this.getMandatoryNonEmptyAttribute(reader, attrs, "url-pattern");
                final boolean handlersSetInDD = this.setHandlersAndRoles(binding, reader, serviceName, portName);
                EndpointFactory.verifyImplementorClass(implementorClass, metadataReader);
                final SDDocumentSource primaryWSDL = this.getPrimaryWSDL(reader, attrs, implementorClass, metadataReader);
                final WSEndpoint<?> endpoint = WSEndpoint.create(implementorClass, !handlersSetInDD, null, serviceName, portName, this.container, binding, primaryWSDL, this.docs.values(), this.createEntityResolver(), false);
                adapters.add(this.adapterFactory.createAdapter(name, urlPattern, endpoint));
            }
            else {
                failWithLocalName("runtime.parser.invalidElement", reader);
            }
        }
        return adapters;
    }
    
    private static WSBinding createBinding(final String ddBindingId, final Class implClass, final String mtomEnabled, final String mtomThreshold, final String dataBindingMode) {
        MTOMFeature mtomfeature = null;
        if (mtomEnabled != null) {
            if (mtomThreshold != null) {
                mtomfeature = new MTOMFeature(Boolean.valueOf(mtomEnabled), Integer.valueOf(mtomThreshold));
            }
            else {
                mtomfeature = new MTOMFeature(Boolean.valueOf(mtomEnabled));
            }
        }
        BindingID bindingID;
        WebServiceFeatureList features;
        if (ddBindingId != null) {
            bindingID = BindingID.parse(ddBindingId);
            features = bindingID.createBuiltinFeatureList();
            if (checkMtomConflict(features.get(MTOMFeature.class), mtomfeature)) {
                throw new ServerRtException(ServerMessages.DD_MTOM_CONFLICT(ddBindingId, mtomEnabled), new Object[0]);
            }
        }
        else {
            bindingID = BindingID.parse(implClass);
            features = new WebServiceFeatureList();
            if (mtomfeature != null) {
                features.add(mtomfeature);
            }
            features.addAll(bindingID.createBuiltinFeatureList());
        }
        if (dataBindingMode != null) {
            features.add(new DatabindingModeFeature(dataBindingMode));
        }
        return bindingID.createBinding(features.toArray());
    }
    
    private static boolean checkMtomConflict(final MTOMFeature lhs, final MTOMFeature rhs) {
        return lhs != null && rhs != null && (lhs.isEnabled() ^ rhs.isEnabled());
    }
    
    @NotNull
    public static String getBindingIdForToken(@NotNull final String lexical) {
        if (lexical.equals("##SOAP11_HTTP")) {
            return "http://schemas.xmlsoap.org/wsdl/soap/http";
        }
        if (lexical.equals("##SOAP11_HTTP_MTOM")) {
            return "http://schemas.xmlsoap.org/wsdl/soap/http?mtom=true";
        }
        if (lexical.equals("##SOAP12_HTTP")) {
            return "http://www.w3.org/2003/05/soap/bindings/HTTP/";
        }
        if (lexical.equals("##SOAP12_HTTP_MTOM")) {
            return "http://www.w3.org/2003/05/soap/bindings/HTTP/?mtom=true";
        }
        if (lexical.equals("##XML_HTTP")) {
            return "http://www.w3.org/2004/08/wsdl/http";
        }
        return lexical;
    }
    
    private SDDocumentSource getPrimaryWSDL(final XMLStreamReader xsr, final Attributes attrs, final Class<?> implementorClass, final MetadataReader metadataReader) {
        String wsdlFile = this.getAttribute(attrs, "wsdl");
        if (wsdlFile == null) {
            wsdlFile = EndpointFactory.getWsdlLocation(implementorClass, metadataReader);
        }
        if (wsdlFile == null) {
            return null;
        }
        if (!wsdlFile.startsWith("WEB-INF/wsdl")) {
            DeploymentDescriptorParser.logger.log(Level.WARNING, "Ignoring wrong wsdl={0}. It should start with {1}. Going to generate and publish a new WSDL.", new Object[] { wsdlFile, "WEB-INF/wsdl" });
            return null;
        }
        URL wsdl;
        try {
            wsdl = this.loader.getResource('/' + wsdlFile);
        }
        catch (final MalformedURLException e) {
            throw new LocatableWebServiceException(ServerMessages.RUNTIME_PARSER_WSDL_NOT_FOUND(wsdlFile), e, xsr);
        }
        if (wsdl == null) {
            throw new LocatableWebServiceException(ServerMessages.RUNTIME_PARSER_WSDL_NOT_FOUND(wsdlFile), xsr);
        }
        final SDDocumentSource docInfo = this.docs.get(wsdl.toExternalForm());
        assert docInfo != null;
        return docInfo;
    }
    
    private EntityResolver createEntityResolver() {
        try {
            return XmlUtil.createEntityResolver(this.loader.getCatalogFile());
        }
        catch (final MalformedURLException e) {
            throw new WebServiceException(e);
        }
    }
    
    protected String getAttribute(final Attributes attrs, final String name) {
        String value = attrs.getValue(name);
        if (value != null) {
            value = value.trim();
        }
        return value;
    }
    
    protected QName getQNameAttribute(final Attributes attrs, final String name) {
        final String value = this.getAttribute(attrs, name);
        if (value == null || value.equals("")) {
            return null;
        }
        return QName.valueOf(value);
    }
    
    protected String getNonEmptyAttribute(final XMLStreamReader reader, final Attributes attrs, final String name) {
        final String value = this.getAttribute(attrs, name);
        if (value != null && value.equals("")) {
            failWithLocalName("runtime.parser.invalidAttributeValue", reader, name);
        }
        return value;
    }
    
    protected String getMandatoryAttribute(final XMLStreamReader reader, final Attributes attrs, final String name) {
        final String value = this.getAttribute(attrs, name);
        if (value == null) {
            failWithLocalName("runtime.parser.missing.attribute", reader, name);
        }
        return value;
    }
    
    protected String getMandatoryNonEmptyAttribute(final XMLStreamReader reader, final Attributes attributes, final String name) {
        final String value = this.getAttribute(attributes, name);
        if (value == null) {
            failWithLocalName("runtime.parser.missing.attribute", reader, name);
        }
        else if (value.equals("")) {
            failWithLocalName("runtime.parser.invalidAttributeValue", reader, name);
        }
        return value;
    }
    
    protected boolean setHandlersAndRoles(final WSBinding binding, final XMLStreamReader reader, final QName serviceName, final QName portName) {
        if (reader.getEventType() == 2 || !reader.getName().equals(HandlerChainsModel.QNAME_HANDLER_CHAINS)) {
            return false;
        }
        final HandlerAnnotationInfo handlerInfo = HandlerChainsModel.parseHandlerFile(reader, this.classLoader, serviceName, portName, binding);
        binding.setHandlerChain(handlerInfo.getHandlers());
        if (binding instanceof SOAPBinding) {
            ((SOAPBinding)binding).setRoles(handlerInfo.getRoles());
        }
        XMLStreamReaderUtil.nextContent(reader);
        return true;
    }
    
    protected ExternalMetadataFeature configureExternalMetadataReader(final XMLStreamReader reader) {
        ExternalMetadataFeature.Builder featureBuilder = null;
        while (DeploymentDescriptorParser.QNAME_EXT_METADA.equals(reader.getName())) {
            if (reader.getEventType() == 1) {
                final Attributes attrs = XMLStreamReaderUtil.getAttributes(reader);
                final String file = this.getAttribute(attrs, "file");
                if (file != null) {
                    if (featureBuilder == null) {
                        featureBuilder = ExternalMetadataFeature.builder();
                    }
                    featureBuilder.addFiles(new File(file));
                }
                final String res = this.getAttribute(attrs, "resource");
                if (res != null) {
                    if (featureBuilder == null) {
                        featureBuilder = ExternalMetadataFeature.builder();
                    }
                    featureBuilder.addResources(res);
                }
            }
            XMLStreamReaderUtil.nextElementContent(reader);
        }
        return this.buildFeature(featureBuilder);
    }
    
    private ExternalMetadataFeature buildFeature(final ExternalMetadataFeature.Builder builder) {
        return (builder != null) ? builder.build() : null;
    }
    
    protected static void fail(final String key, final XMLStreamReader reader) {
        DeploymentDescriptorParser.logger.log(Level.SEVERE, "{0}{1}", new Object[] { key, reader.getLocation().getLineNumber() });
        throw new ServerRtException(key, new Object[] { Integer.toString(reader.getLocation().getLineNumber()) });
    }
    
    protected static void failWithFullName(final String key, final XMLStreamReader reader) {
        throw new ServerRtException(key, new Object[] { reader.getLocation().getLineNumber(), reader.getName() });
    }
    
    protected static void failWithLocalName(final String key, final XMLStreamReader reader) {
        throw new ServerRtException(key, new Object[] { reader.getLocation().getLineNumber(), reader.getLocalName() });
    }
    
    protected static void failWithLocalName(final String key, final XMLStreamReader reader, final String arg) {
        throw new ServerRtException(key, new Object[] { reader.getLocation().getLineNumber(), reader.getLocalName(), arg });
    }
    
    protected Class loadClass(final String name) {
        try {
            return Class.forName(name, true, this.classLoader);
        }
        catch (final ClassNotFoundException e) {
            DeploymentDescriptorParser.logger.log(Level.SEVERE, e.getMessage(), e);
            throw new ServerRtException("runtime.parser.classNotFound", new Object[] { name });
        }
    }
    
    private Class getImplementorClass(final String name, final XMLStreamReader xsr) {
        try {
            return Class.forName(name, true, this.classLoader);
        }
        catch (final ClassNotFoundException e) {
            DeploymentDescriptorParser.logger.log(Level.SEVERE, e.getMessage(), e);
            throw new LocatableWebServiceException(ServerMessages.RUNTIME_PARSER_CLASS_NOT_FOUND(name), e, xsr);
        }
    }
    
    static {
        QNAME_ENDPOINTS = new QName("http://java.sun.com/xml/ns/jax-ws/ri/runtime", "endpoints");
        QNAME_ENDPOINT = new QName("http://java.sun.com/xml/ns/jax-ws/ri/runtime", "endpoint");
        QNAME_EXT_METADA = new QName("http://java.sun.com/xml/ns/jax-ws/ri/runtime", "external-metadata");
        ATTRVALUE_SUPPORTED_VERSIONS = Arrays.asList("2.0", "2.1");
        logger = Logger.getLogger("com.sun.xml.internal.ws.server.http");
    }
    
    public interface AdapterFactory<A>
    {
        A createAdapter(final String p0, final String p1, final WSEndpoint<?> p2);
    }
}
