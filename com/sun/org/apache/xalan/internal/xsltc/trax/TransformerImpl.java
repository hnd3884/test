package com.sun.org.apache.xalan.internal.xsltc.trax;

import java.util.Hashtable;
import com.sun.org.apache.xml.internal.utils.SystemIDResolver;
import java.util.HashMap;
import com.sun.org.apache.xml.internal.serializer.OutputPropertiesFactory;
import java.util.Vector;
import java.util.StringTokenizer;
import java.util.Enumeration;
import com.sun.org.apache.xalan.internal.xsltc.runtime.MessageHandler;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import com.sun.org.apache.xalan.internal.xsltc.TransletException;
import org.w3c.dom.Node;
import jdk.xml.internal.JdkXmlUtils;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLEventReader;
import org.xml.sax.XMLReader;
import java.io.Reader;
import java.io.InputStream;
import com.sun.org.apache.xalan.internal.xsltc.dom.SAXImpl;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stax.StAXSource;
import javax.xml.transform.sax.SAXSource;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import javax.xml.transform.stream.StreamSource;
import com.sun.org.apache.xml.internal.dtm.DTMWSFilter;
import com.sun.org.apache.xalan.internal.xsltc.dom.DOMWSFilter;
import com.sun.org.apache.xalan.internal.xsltc.StripFilter;
import java.net.URLConnection;
import java.io.Writer;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.ContentHandler;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import java.net.UnknownServiceException;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.net.URI;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.sax.SAXResult;
import com.sun.org.apache.xml.internal.serializer.SerializationHandler;
import javax.xml.transform.stax.StAXResult;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.TransformerException;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import com.sun.org.apache.xalan.internal.xsltc.Translet;
import java.util.Map;
import com.sun.org.apache.xalan.internal.utils.XMLSecurityManager;
import com.sun.org.apache.xml.internal.utils.XMLReaderManager;
import com.sun.org.apache.xalan.internal.xsltc.dom.XSLTCDTMManager;
import java.io.OutputStream;
import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.runtime.output.TransletOutputHandlerFactory;
import java.util.Properties;
import javax.xml.transform.URIResolver;
import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import javax.xml.transform.ErrorListener;
import com.sun.org.apache.xalan.internal.xsltc.DOMCache;
import javax.xml.transform.Transformer;

public final class TransformerImpl extends Transformer implements DOMCache, ErrorListener
{
    private static final String LEXICAL_HANDLER_PROPERTY = "http://xml.org/sax/properties/lexical-handler";
    private static final String NAMESPACE_PREFIXES_FEATURE = "http://xml.org/sax/features/namespace-prefixes";
    private AbstractTranslet _translet;
    private String _method;
    private String _encoding;
    private String _sourceSystemId;
    private ErrorListener _errorListener;
    private URIResolver _uriResolver;
    private Properties _properties;
    private Properties _propertiesClone;
    private TransletOutputHandlerFactory _tohFactory;
    private DOM _dom;
    private int _indentNumber;
    private TransformerFactoryImpl _tfactory;
    private OutputStream _ostream;
    private XSLTCDTMManager _dtmManager;
    private XMLReaderManager _readerManager;
    private boolean _isIdentity;
    private boolean _isSecureProcessing;
    private boolean _overrideDefaultParser;
    private String _accessExternalDTD;
    private XMLSecurityManager _securityManager;
    private Map<String, Object> _parameters;
    
    protected TransformerImpl(final Properties outputProperties, final int indentNumber, final TransformerFactoryImpl tfactory) {
        this(null, outputProperties, indentNumber, tfactory);
        this._isIdentity = true;
    }
    
    protected TransformerImpl(final Translet translet, final Properties outputProperties, final int indentNumber, final TransformerFactoryImpl tfactory) {
        this._translet = null;
        this._method = null;
        this._encoding = null;
        this._sourceSystemId = null;
        this._errorListener = this;
        this._uriResolver = null;
        this._tohFactory = null;
        this._dom = null;
        this._tfactory = null;
        this._ostream = null;
        this._dtmManager = null;
        this._isIdentity = false;
        this._isSecureProcessing = false;
        this._accessExternalDTD = "all";
        this._parameters = null;
        this._translet = (AbstractTranslet)translet;
        this._properties = this.createOutputProperties(outputProperties);
        this._propertiesClone = (Properties)this._properties.clone();
        this._indentNumber = indentNumber;
        this._tfactory = tfactory;
        this._overrideDefaultParser = this._tfactory.overrideDefaultParser();
        this._accessExternalDTD = (String)this._tfactory.getAttribute("http://javax.xml.XMLConstants/property/accessExternalDTD");
        this._securityManager = (XMLSecurityManager)this._tfactory.getAttribute("http://apache.org/xml/properties/security-manager");
        (this._readerManager = XMLReaderManager.getInstance(this._overrideDefaultParser)).setProperty("http://javax.xml.XMLConstants/property/accessExternalDTD", this._accessExternalDTD);
        this._readerManager.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", this._isSecureProcessing);
        this._readerManager.setProperty("http://apache.org/xml/properties/security-manager", this._securityManager);
    }
    
    public boolean isSecureProcessing() {
        return this._isSecureProcessing;
    }
    
    public void setSecureProcessing(final boolean flag) {
        this._isSecureProcessing = flag;
        this._readerManager.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", this._isSecureProcessing);
    }
    
    public boolean overrideDefaultParser() {
        return this._overrideDefaultParser;
    }
    
    public void setOverrideDefaultParser(final boolean flag) {
        this._overrideDefaultParser = flag;
    }
    
    protected AbstractTranslet getTranslet() {
        return this._translet;
    }
    
    public boolean isIdentity() {
        return this._isIdentity;
    }
    
    @Override
    public void transform(final Source source, final Result result) throws TransformerException {
        if (!this._isIdentity) {
            if (this._translet == null) {
                final ErrorMsg err = new ErrorMsg("JAXP_NO_TRANSLET_ERR");
                throw new TransformerException(err.toString());
            }
            this.transferOutputProperties(this._translet);
        }
        final SerializationHandler toHandler = this.getOutputHandler(result);
        if (toHandler == null) {
            final ErrorMsg err2 = new ErrorMsg("JAXP_NO_HANDLER_ERR");
            throw new TransformerException(err2.toString());
        }
        if (this._uriResolver != null && !this._isIdentity) {
            this._translet.setDOMCache(this);
        }
        if (this._isIdentity) {
            this.transferOutputProperties(toHandler);
        }
        this.transform(source, toHandler, this._encoding);
        try {
            if (result instanceof DOMResult) {
                ((DOMResult)result).setNode(this._tohFactory.getNode());
            }
            else if (result instanceof StAXResult) {
                if (((StAXResult)result).getXMLEventWriter() != null) {
                    this._tohFactory.getXMLEventWriter().flush();
                }
                else if (((StAXResult)result).getXMLStreamWriter() != null) {
                    this._tohFactory.getXMLStreamWriter().flush();
                }
            }
        }
        catch (final Exception e) {
            System.out.println("Result writing error");
        }
    }
    
    public SerializationHandler getOutputHandler(final Result result) throws TransformerException {
        this._method = ((Hashtable<K, String>)this._properties).get("method");
        this._encoding = this._properties.getProperty("encoding");
        (this._tohFactory = TransletOutputHandlerFactory.newInstance(this._overrideDefaultParser)).setEncoding(this._encoding);
        if (this._method != null) {
            this._tohFactory.setOutputMethod(this._method);
        }
        if (this._indentNumber >= 0) {
            this._tohFactory.setIndentNumber(this._indentNumber);
        }
        try {
            if (result instanceof SAXResult) {
                final SAXResult target = (SAXResult)result;
                final ContentHandler handler = target.getHandler();
                this._tohFactory.setHandler(handler);
                final LexicalHandler lexicalHandler = target.getLexicalHandler();
                if (lexicalHandler != null) {
                    this._tohFactory.setLexicalHandler(lexicalHandler);
                }
                this._tohFactory.setOutputType(1);
                return this._tohFactory.getSerializationHandler();
            }
            if (result instanceof StAXResult) {
                if (((StAXResult)result).getXMLEventWriter() != null) {
                    this._tohFactory.setXMLEventWriter(((StAXResult)result).getXMLEventWriter());
                }
                else if (((StAXResult)result).getXMLStreamWriter() != null) {
                    this._tohFactory.setXMLStreamWriter(((StAXResult)result).getXMLStreamWriter());
                }
                this._tohFactory.setOutputType(3);
                return this._tohFactory.getSerializationHandler();
            }
            if (result instanceof DOMResult) {
                this._tohFactory.setNode(((DOMResult)result).getNode());
                this._tohFactory.setNextSibling(((DOMResult)result).getNextSibling());
                this._tohFactory.setOutputType(2);
                return this._tohFactory.getSerializationHandler();
            }
            if (result instanceof StreamResult) {
                final StreamResult target2 = (StreamResult)result;
                this._tohFactory.setOutputType(0);
                final Writer writer = target2.getWriter();
                if (writer != null) {
                    this._tohFactory.setWriter(writer);
                    return this._tohFactory.getSerializationHandler();
                }
                final OutputStream ostream = target2.getOutputStream();
                if (ostream != null) {
                    this._tohFactory.setOutputStream(ostream);
                    return this._tohFactory.getSerializationHandler();
                }
                String systemId = result.getSystemId();
                if (systemId == null) {
                    final ErrorMsg err = new ErrorMsg("JAXP_NO_RESULT_ERR");
                    throw new TransformerException(err.toString());
                }
                if (systemId.startsWith("file:")) {
                    try {
                        final URI uri = new URI(systemId);
                        systemId = "file:";
                        final String host = uri.getHost();
                        String path = uri.getPath();
                        if (path == null) {
                            path = "";
                        }
                        if (host != null) {
                            systemId = systemId + "//" + host + path;
                        }
                        else {
                            systemId = systemId + "//" + path;
                        }
                    }
                    catch (final Exception ex) {}
                    final URL url = new URL(systemId);
                    this._ostream = new FileOutputStream(url.getFile());
                    this._tohFactory.setOutputStream(this._ostream);
                    return this._tohFactory.getSerializationHandler();
                }
                if (systemId.startsWith("http:")) {
                    final URL url = new URL(systemId);
                    final URLConnection connection = url.openConnection();
                    this._tohFactory.setOutputStream(this._ostream = connection.getOutputStream());
                    return this._tohFactory.getSerializationHandler();
                }
                this._tohFactory.setOutputStream(this._ostream = new FileOutputStream(new File(systemId)));
                return this._tohFactory.getSerializationHandler();
            }
        }
        catch (final UnknownServiceException e) {
            throw new TransformerException(e);
        }
        catch (final ParserConfigurationException e2) {
            throw new TransformerException(e2);
        }
        catch (final IOException e3) {
            throw new TransformerException(e3);
        }
        return null;
    }
    
    protected void setDOM(final DOM dom) {
        this._dom = dom;
    }
    
    private DOM getDOM(final Source source) throws TransformerException {
        try {
            DOM dom;
            if (source != null) {
                DTMWSFilter wsfilter;
                if (this._translet != null && this._translet instanceof StripFilter) {
                    wsfilter = new DOMWSFilter(this._translet);
                }
                else {
                    wsfilter = null;
                }
                final boolean hasIdCall = this._translet != null && this._translet.hasIdCall();
                if (this._dtmManager == null) {
                    (this._dtmManager = this._tfactory.createNewDTMManagerInstance()).setOverrideDefaultParser(this._overrideDefaultParser);
                }
                dom = (DOM)this._dtmManager.getDTM(source, false, wsfilter, true, false, false, 0, hasIdCall);
            }
            else {
                if (this._dom == null) {
                    return null;
                }
                dom = this._dom;
                this._dom = null;
            }
            if (!this._isIdentity) {
                this._translet.prepassDocument(dom);
            }
            return dom;
        }
        catch (final Exception e) {
            if (this._errorListener != null) {
                this.postErrorToListener(e.getMessage());
            }
            throw new TransformerException(e);
        }
    }
    
    protected TransformerFactoryImpl getTransformerFactory() {
        return this._tfactory;
    }
    
    protected TransletOutputHandlerFactory getTransletOutputHandlerFactory() {
        return this._tohFactory;
    }
    
    private void transformIdentity(final Source source, final SerializationHandler handler) throws Exception {
        if (source != null) {
            this._sourceSystemId = source.getSystemId();
        }
        if (source instanceof StreamSource) {
            final StreamSource stream = (StreamSource)source;
            final InputStream streamInput = stream.getInputStream();
            final Reader streamReader = stream.getReader();
            final XMLReader reader = this._readerManager.getXMLReader();
            try {
                try {
                    reader.setProperty("http://xml.org/sax/properties/lexical-handler", handler);
                    reader.setFeature("http://xml.org/sax/features/namespace-prefixes", true);
                }
                catch (final SAXException ex) {}
                reader.setContentHandler(handler);
                InputSource input;
                if (streamInput != null) {
                    input = new InputSource(streamInput);
                    input.setSystemId(this._sourceSystemId);
                }
                else if (streamReader != null) {
                    input = new InputSource(streamReader);
                    input.setSystemId(this._sourceSystemId);
                }
                else {
                    if (this._sourceSystemId == null) {
                        final ErrorMsg err = new ErrorMsg("JAXP_NO_SOURCE_ERR");
                        throw new TransformerException(err.toString());
                    }
                    input = new InputSource(this._sourceSystemId);
                }
                reader.parse(input);
            }
            finally {
                this._readerManager.releaseXMLReader(reader);
            }
        }
        else if (source instanceof SAXSource) {
            final SAXSource sax = (SAXSource)source;
            XMLReader reader2 = sax.getXMLReader();
            final InputSource input2 = sax.getInputSource();
            boolean userReader = true;
            try {
                if (reader2 == null) {
                    reader2 = this._readerManager.getXMLReader();
                    userReader = false;
                }
                try {
                    reader2.setProperty("http://xml.org/sax/properties/lexical-handler", handler);
                    reader2.setFeature("http://xml.org/sax/features/namespace-prefixes", true);
                }
                catch (final SAXException ex2) {}
                reader2.setContentHandler(handler);
                reader2.parse(input2);
            }
            finally {
                if (!userReader) {
                    this._readerManager.releaseXMLReader(reader2);
                }
            }
        }
        else if (source instanceof StAXSource) {
            final StAXSource staxSource = (StAXSource)source;
            if (staxSource.getXMLEventReader() != null) {
                final XMLEventReader xmlEventReader = staxSource.getXMLEventReader();
                final StAXEvent2SAX staxevent2sax = new StAXEvent2SAX(xmlEventReader);
                staxevent2sax.setContentHandler(handler);
                staxevent2sax.parse();
                handler.flushPending();
            }
            else if (staxSource.getXMLStreamReader() != null) {
                final XMLStreamReader xmlStreamReader = staxSource.getXMLStreamReader();
                final StAXStream2SAX staxStream2SAX = new StAXStream2SAX(xmlStreamReader);
                staxStream2SAX.setContentHandler(handler);
                staxStream2SAX.parse();
                handler.flushPending();
            }
        }
        else if (source instanceof DOMSource) {
            final DOMSource domsrc = (DOMSource)source;
            new DOM2TO(domsrc.getNode(), handler).parse();
        }
        else {
            if (!(source instanceof XSLTCSource)) {
                final ErrorMsg err2 = new ErrorMsg("JAXP_NO_SOURCE_ERR");
                throw new TransformerException(err2.toString());
            }
            final DOM dom = ((XSLTCSource)source).getDOM(null, this._translet);
            ((SAXImpl)dom).copy(handler);
        }
    }
    
    private void transform(Source source, final SerializationHandler handler, final String encoding) throws TransformerException {
        try {
            if ((source instanceof StreamSource && source.getSystemId() == null && ((StreamSource)source).getInputStream() == null && ((StreamSource)source).getReader() == null) || (source instanceof SAXSource && ((SAXSource)source).getInputSource() == null && ((SAXSource)source).getXMLReader() == null) || (source instanceof DOMSource && ((DOMSource)source).getNode() == null)) {
                final DocumentBuilderFactory builderF = JdkXmlUtils.getDOMFactory(this._overrideDefaultParser);
                final DocumentBuilder builder = builderF.newDocumentBuilder();
                final String systemID = source.getSystemId();
                source = new DOMSource(builder.newDocument());
                if (systemID != null) {
                    source.setSystemId(systemID);
                }
            }
            if (this._isIdentity) {
                this.transformIdentity(source, handler);
            }
            else {
                this._translet.transform(this.getDOM(source), handler);
            }
        }
        catch (final TransletException e) {
            if (this._errorListener != null) {
                this.postErrorToListener(e.getMessage());
            }
            throw new TransformerException(e);
        }
        catch (final RuntimeException e2) {
            if (this._errorListener != null) {
                this.postErrorToListener(e2.getMessage());
            }
            throw new TransformerException(e2);
        }
        catch (final Exception e3) {
            if (this._errorListener != null) {
                this.postErrorToListener(e3.getMessage());
            }
            throw new TransformerException(e3);
        }
        finally {
            this._dtmManager = null;
        }
        if (this._ostream != null) {
            try {
                this._ostream.close();
            }
            catch (final IOException ex) {}
            this._ostream = null;
        }
    }
    
    @Override
    public ErrorListener getErrorListener() {
        return this._errorListener;
    }
    
    @Override
    public void setErrorListener(final ErrorListener listener) throws IllegalArgumentException {
        if (listener == null) {
            final ErrorMsg err = new ErrorMsg("ERROR_LISTENER_NULL_ERR", "Transformer");
            throw new IllegalArgumentException(err.toString());
        }
        this._errorListener = listener;
        if (this._translet != null) {
            this._translet.setMessageHandler(new MessageHandler(this._errorListener));
        }
    }
    
    private void postErrorToListener(final String message) {
        try {
            this._errorListener.error(new TransformerException(message));
        }
        catch (final TransformerException ex) {}
    }
    
    private void postWarningToListener(final String message) {
        try {
            this._errorListener.warning(new TransformerException(message));
        }
        catch (final TransformerException ex) {}
    }
    
    @Override
    public Properties getOutputProperties() {
        return (Properties)this._properties.clone();
    }
    
    @Override
    public String getOutputProperty(final String name) throws IllegalArgumentException {
        if (!this.validOutputProperty(name)) {
            final ErrorMsg err = new ErrorMsg("JAXP_UNKNOWN_PROP_ERR", name);
            throw new IllegalArgumentException(err.toString());
        }
        return this._properties.getProperty(name);
    }
    
    @Override
    public void setOutputProperties(final Properties properties) throws IllegalArgumentException {
        if (properties != null) {
            final Enumeration names = properties.propertyNames();
            while (names.hasMoreElements()) {
                final String name = names.nextElement();
                if (this.isDefaultProperty(name, properties)) {
                    continue;
                }
                if (!this.validOutputProperty(name)) {
                    final ErrorMsg err = new ErrorMsg("JAXP_UNKNOWN_PROP_ERR", name);
                    throw new IllegalArgumentException(err.toString());
                }
                this._properties.setProperty(name, properties.getProperty(name));
            }
        }
        else {
            this._properties = this._propertiesClone;
        }
    }
    
    @Override
    public void setOutputProperty(final String name, final String value) throws IllegalArgumentException {
        if (!this.validOutputProperty(name)) {
            final ErrorMsg err = new ErrorMsg("JAXP_UNKNOWN_PROP_ERR", name);
            throw new IllegalArgumentException(err.toString());
        }
        this._properties.setProperty(name, value);
    }
    
    private void transferOutputProperties(final AbstractTranslet translet) {
        if (this._properties == null) {
            return;
        }
        final Enumeration names = this._properties.propertyNames();
        while (names.hasMoreElements()) {
            final String name = names.nextElement();
            final String value = ((Hashtable<K, String>)this._properties).get(name);
            if (value == null) {
                continue;
            }
            if (name.equals("encoding")) {
                translet._encoding = value;
            }
            else if (name.equals("method")) {
                translet._method = value;
            }
            else if (name.equals("doctype-public")) {
                translet._doctypePublic = value;
            }
            else if (name.equals("doctype-system")) {
                translet._doctypeSystem = value;
            }
            else if (name.equals("media-type")) {
                translet._mediaType = value;
            }
            else if (name.equals("standalone")) {
                translet._standalone = value;
            }
            else if (name.equals("version")) {
                translet._version = value;
            }
            else if (name.equals("omit-xml-declaration")) {
                translet._omitHeader = (value != null && value.toLowerCase().equals("yes"));
            }
            else if (name.equals("indent")) {
                translet._indent = (value != null && value.toLowerCase().equals("yes"));
            }
            else if (name.equals("{http://xml.apache.org/xslt}indent-amount")) {
                if (value == null) {
                    continue;
                }
                translet._indentamount = Integer.parseInt(value);
            }
            else if (name.equals("{http://xml.apache.org/xalan}indent-amount")) {
                if (value == null) {
                    continue;
                }
                translet._indentamount = Integer.parseInt(value);
            }
            else if (name.equals("cdata-section-elements")) {
                if (value == null) {
                    continue;
                }
                translet._cdata = null;
                final StringTokenizer e = new StringTokenizer(value);
                while (e.hasMoreTokens()) {
                    translet.addCdataElement(e.nextToken());
                }
            }
            else {
                if (!name.equals("http://www.oracle.com/xml/is-standalone") || value == null || !value.equals("yes")) {
                    continue;
                }
                translet._isStandalone = true;
            }
        }
    }
    
    public void transferOutputProperties(final SerializationHandler handler) {
        if (this._properties == null) {
            return;
        }
        String doctypePublic = null;
        String doctypeSystem = null;
        final Enumeration names = this._properties.propertyNames();
        while (names.hasMoreElements()) {
            final String name = names.nextElement();
            final String value = ((Hashtable<K, String>)this._properties).get(name);
            if (value == null) {
                continue;
            }
            if (name.equals("doctype-public")) {
                doctypePublic = value;
            }
            else if (name.equals("doctype-system")) {
                doctypeSystem = value;
            }
            else if (name.equals("media-type")) {
                handler.setMediaType(value);
            }
            else if (name.equals("standalone")) {
                handler.setStandalone(value);
            }
            else if (name.equals("version")) {
                handler.setVersion(value);
            }
            else if (name.equals("omit-xml-declaration")) {
                handler.setOmitXMLDeclaration(value != null && value.toLowerCase().equals("yes"));
            }
            else if (name.equals("indent")) {
                handler.setIndent(value != null && value.toLowerCase().equals("yes"));
            }
            else if (name.equals("{http://xml.apache.org/xslt}indent-amount")) {
                if (value == null) {
                    continue;
                }
                handler.setIndentAmount(Integer.parseInt(value));
            }
            else if (name.equals("{http://xml.apache.org/xalan}indent-amount")) {
                if (value == null) {
                    continue;
                }
                handler.setIndentAmount(Integer.parseInt(value));
            }
            else if (name.equals("http://www.oracle.com/xml/is-standalone")) {
                if (value == null || !value.equals("yes")) {
                    continue;
                }
                handler.setIsStandalone(true);
            }
            else {
                if (!name.equals("cdata-section-elements") || value == null) {
                    continue;
                }
                final StringTokenizer e = new StringTokenizer(value);
                Vector uriAndLocalNames = null;
                while (e.hasMoreTokens()) {
                    final String token = e.nextToken();
                    final int lastcolon = token.lastIndexOf(58);
                    String uri;
                    String localName;
                    if (lastcolon > 0) {
                        uri = token.substring(0, lastcolon);
                        localName = token.substring(lastcolon + 1);
                    }
                    else {
                        uri = null;
                        localName = token;
                    }
                    if (uriAndLocalNames == null) {
                        uriAndLocalNames = new Vector();
                    }
                    uriAndLocalNames.addElement(uri);
                    uriAndLocalNames.addElement(localName);
                }
                handler.setCdataSectionElements(uriAndLocalNames);
            }
        }
        if (doctypePublic != null || doctypeSystem != null) {
            handler.setDoctype(doctypeSystem, doctypePublic);
        }
    }
    
    private Properties createOutputProperties(final Properties outputProperties) {
        final Properties defaults = new Properties();
        this.setDefaults(defaults, "xml");
        final Properties base = new Properties(defaults);
        if (outputProperties != null) {
            final Enumeration names = outputProperties.propertyNames();
            while (names.hasMoreElements()) {
                final String name = names.nextElement();
                base.setProperty(name, outputProperties.getProperty(name));
            }
        }
        else {
            base.setProperty("encoding", this._translet._encoding);
            if (this._translet._method != null) {
                base.setProperty("method", this._translet._method);
            }
        }
        final String method = base.getProperty("method");
        if (method != null) {
            if (method.equals("html")) {
                this.setDefaults(defaults, "html");
            }
            else if (method.equals("text")) {
                this.setDefaults(defaults, "text");
            }
        }
        return base;
    }
    
    private void setDefaults(final Properties props, final String method) {
        final Properties method_props = OutputPropertiesFactory.getDefaultMethodProperties(method);
        final Enumeration names = method_props.propertyNames();
        while (names.hasMoreElements()) {
            final String name = names.nextElement();
            props.setProperty(name, method_props.getProperty(name));
        }
    }
    
    private boolean validOutputProperty(final String name) {
        return name.equals("encoding") || name.equals("method") || name.equals("indent") || name.equals("doctype-public") || name.equals("doctype-system") || name.equals("cdata-section-elements") || name.equals("media-type") || name.equals("omit-xml-declaration") || name.equals("standalone") || name.equals("version") || name.equals("http://www.oracle.com/xml/is-standalone") || name.charAt(0) == '{';
    }
    
    private boolean isDefaultProperty(final String name, final Properties properties) {
        return properties.get(name) == null;
    }
    
    @Override
    public void setParameter(final String name, final Object value) {
        if (value == null) {
            final ErrorMsg err = new ErrorMsg("JAXP_INVALID_SET_PARAM_VALUE", name);
            throw new IllegalArgumentException(err.toString());
        }
        if (this._isIdentity) {
            if (this._parameters == null) {
                this._parameters = new HashMap<String, Object>();
            }
            this._parameters.put(name, value);
        }
        else {
            this._translet.addParameter(name, value);
        }
    }
    
    @Override
    public void clearParameters() {
        if (this._isIdentity && this._parameters != null) {
            this._parameters.clear();
        }
        else {
            this._translet.clearParameters();
        }
    }
    
    @Override
    public final Object getParameter(final String name) {
        if (this._isIdentity) {
            return (this._parameters != null) ? this._parameters.get(name) : null;
        }
        return this._translet.getParameter(name);
    }
    
    @Override
    public URIResolver getURIResolver() {
        return this._uriResolver;
    }
    
    @Override
    public void setURIResolver(final URIResolver resolver) {
        this._uriResolver = resolver;
    }
    
    @Override
    public DOM retrieveDocument(final String baseURI, String href, final Translet translet) {
        try {
            if (href.length() == 0) {
                href = baseURI;
            }
            final Source resolvedSource = this._uriResolver.resolve(href, baseURI);
            if (resolvedSource == null) {
                final StreamSource streamSource = new StreamSource(SystemIDResolver.getAbsoluteURI(href, baseURI));
                return this.getDOM(streamSource);
            }
            return this.getDOM(resolvedSource);
        }
        catch (final TransformerException e) {
            if (this._errorListener != null) {
                this.postErrorToListener("File not found: " + e.getMessage());
            }
            return null;
        }
    }
    
    @Override
    public void error(final TransformerException e) throws TransformerException {
        final Throwable wrapped = e.getException();
        if (wrapped != null) {
            System.err.println(new ErrorMsg("ERROR_PLUS_WRAPPED_MSG", e.getMessageAndLocation(), wrapped.getMessage()));
        }
        else {
            System.err.println(new ErrorMsg("ERROR_MSG", e.getMessageAndLocation()));
        }
        throw e;
    }
    
    @Override
    public void fatalError(final TransformerException e) throws TransformerException {
        final Throwable wrapped = e.getException();
        if (wrapped != null) {
            System.err.println(new ErrorMsg("FATAL_ERR_PLUS_WRAPPED_MSG", e.getMessageAndLocation(), wrapped.getMessage()));
        }
        else {
            System.err.println(new ErrorMsg("FATAL_ERR_MSG", e.getMessageAndLocation()));
        }
        throw e;
    }
    
    @Override
    public void warning(final TransformerException e) throws TransformerException {
        final Throwable wrapped = e.getException();
        if (wrapped != null) {
            System.err.println(new ErrorMsg("WARNING_PLUS_WRAPPED_MSG", e.getMessageAndLocation(), wrapped.getMessage()));
        }
        else {
            System.err.println(new ErrorMsg("WARNING_MSG", e.getMessageAndLocation()));
        }
    }
    
    @Override
    public void reset() {
        this._method = null;
        this._encoding = null;
        this._sourceSystemId = null;
        this._errorListener = this;
        this._uriResolver = null;
        this._dom = null;
        this._parameters = null;
        this._indentNumber = 0;
        this.setOutputProperties(null);
        this._tohFactory = null;
        this._ostream = null;
    }
    
    static class MessageHandler extends com.sun.org.apache.xalan.internal.xsltc.runtime.MessageHandler
    {
        private ErrorListener _errorListener;
        
        public MessageHandler(final ErrorListener errorListener) {
            this._errorListener = errorListener;
        }
        
        @Override
        public void displayMessage(final String msg) {
            if (this._errorListener == null) {
                System.err.println(msg);
            }
            else {
                try {
                    this._errorListener.warning(new TransformerException(msg));
                }
                catch (final TransformerException ex) {}
            }
        }
    }
}
