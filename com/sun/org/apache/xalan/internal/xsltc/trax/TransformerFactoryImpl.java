package com.sun.org.apache.xalan.internal.xsltc.trax;

import com.sun.org.apache.xalan.internal.xsltc.dom.XSLTCDTMManager;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.io.FilenameFilter;
import com.sun.org.apache.xalan.internal.utils.SecuritySupport;
import java.io.InputStream;
import java.io.FileNotFoundException;
import java.io.FileInputStream;
import java.util.Vector;
import org.xml.sax.XMLFilter;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.sax.TemplatesHandler;
import java.io.File;
import com.sun.org.apache.xalan.internal.xsltc.compiler.XSLTC;
import com.sun.org.apache.xalan.internal.utils.ObjectFactory;
import javax.xml.transform.TransformerException;
import java.util.ArrayList;
import javax.xml.transform.Templates;
import java.util.Properties;
import javax.xml.transform.Transformer;
import org.xml.sax.InputSource;
import org.w3c.dom.Node;
import org.xml.sax.XMLReader;
import java.io.IOException;
import org.xml.sax.SAXException;
import com.sun.org.apache.xml.internal.utils.StopParseException;
import jdk.xml.internal.JdkXmlUtils;
import javax.xml.transform.sax.SAXSource;
import org.xml.sax.ContentHandler;
import javax.xml.transform.dom.DOMSource;
import com.sun.org.apache.xml.internal.utils.StylesheetPIHandler;
import com.sun.org.apache.xalan.internal.XalanConstants;
import javax.xml.transform.TransformerConfigurationException;
import com.sun.org.apache.xalan.internal.utils.FeaturePropertyBase;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import jdk.xml.internal.JdkXmlFeatures;
import com.sun.org.apache.xalan.internal.utils.XMLSecurityManager;
import com.sun.org.apache.xalan.internal.utils.XMLSecurityPropertyManager;
import javax.xml.transform.Source;
import java.util.Map;
import javax.xml.transform.URIResolver;
import javax.xml.transform.ErrorListener;
import com.sun.org.apache.xalan.internal.xsltc.compiler.SourceLoader;
import javax.xml.transform.sax.SAXTransformerFactory;

public class TransformerFactoryImpl extends SAXTransformerFactory implements SourceLoader, ErrorListener
{
    public static final String TRANSLET_NAME = "translet-name";
    public static final String DESTINATION_DIRECTORY = "destination-directory";
    public static final String PACKAGE_NAME = "package-name";
    public static final String JAR_NAME = "jar-name";
    public static final String GENERATE_TRANSLET = "generate-translet";
    public static final String AUTO_TRANSLET = "auto-translet";
    public static final String USE_CLASSPATH = "use-classpath";
    public static final String DEBUG = "debug";
    public static final String ENABLE_INLINING = "enable-inlining";
    public static final String INDENT_NUMBER = "indent-number";
    private ErrorListener _errorListener;
    private URIResolver _uriResolver;
    protected static final String DEFAULT_TRANSLET_NAME = "GregorSamsa";
    private String _transletName;
    private String _destinationDirectory;
    private String _packageName;
    private String _jarFileName;
    private Map<Source, PIParamWrapper> _piParams;
    private boolean _debug;
    private boolean _enableInlining;
    private boolean _generateTranslet;
    private boolean _autoTranslet;
    private boolean _useClasspath;
    private int _indentNumber;
    private boolean _isNotSecureProcessing;
    private boolean _isSecureMode;
    private boolean _overrideDefaultParser;
    private String _accessExternalStylesheet;
    private String _accessExternalDTD;
    private XMLSecurityPropertyManager _xmlSecurityPropertyMgr;
    private XMLSecurityManager _xmlSecurityManager;
    private final JdkXmlFeatures _xmlFeatures;
    private ClassLoader _extensionClassLoader;
    private Map<String, Class> _xsltcExtensionFunctions;
    
    public TransformerFactoryImpl() {
        this._errorListener = this;
        this._uriResolver = null;
        this._transletName = "GregorSamsa";
        this._destinationDirectory = null;
        this._packageName = null;
        this._jarFileName = null;
        this._piParams = null;
        this._debug = false;
        this._enableInlining = false;
        this._generateTranslet = false;
        this._autoTranslet = false;
        this._useClasspath = false;
        this._indentNumber = -1;
        this._isNotSecureProcessing = true;
        this._isSecureMode = false;
        this._accessExternalStylesheet = "all";
        this._accessExternalDTD = "all";
        this._extensionClassLoader = null;
        if (System.getSecurityManager() != null) {
            this._isSecureMode = true;
            this._isNotSecureProcessing = false;
        }
        this._xmlFeatures = new JdkXmlFeatures(!this._isNotSecureProcessing);
        this._overrideDefaultParser = this._xmlFeatures.getFeature(JdkXmlFeatures.XmlFeature.JDK_OVERRIDE_PARSER);
        this._xmlSecurityPropertyMgr = new XMLSecurityPropertyManager();
        this._accessExternalDTD = this._xmlSecurityPropertyMgr.getValue(XMLSecurityPropertyManager.Property.ACCESS_EXTERNAL_DTD);
        this._accessExternalStylesheet = this._xmlSecurityPropertyMgr.getValue(XMLSecurityPropertyManager.Property.ACCESS_EXTERNAL_STYLESHEET);
        this._xmlSecurityManager = new XMLSecurityManager(true);
        this._xsltcExtensionFunctions = null;
    }
    
    public Map<String, Class> getExternalExtensionsMap() {
        return this._xsltcExtensionFunctions;
    }
    
    @Override
    public void setErrorListener(final ErrorListener listener) throws IllegalArgumentException {
        if (listener == null) {
            final ErrorMsg err = new ErrorMsg("ERROR_LISTENER_NULL_ERR", "TransformerFactory");
            throw new IllegalArgumentException(err.toString());
        }
        this._errorListener = listener;
    }
    
    @Override
    public ErrorListener getErrorListener() {
        return this._errorListener;
    }
    
    @Override
    public Object getAttribute(final String name) throws IllegalArgumentException {
        if (name.equals("translet-name")) {
            return this._transletName;
        }
        if (name.equals("generate-translet")) {
            return new Boolean(this._generateTranslet);
        }
        if (name.equals("auto-translet")) {
            return new Boolean(this._autoTranslet);
        }
        if (name.equals("enable-inlining")) {
            if (this._enableInlining) {
                return Boolean.TRUE;
            }
            return Boolean.FALSE;
        }
        else {
            if (name.equals("http://apache.org/xml/properties/security-manager")) {
                return this._xmlSecurityManager;
            }
            if (name.equals("jdk.xml.transform.extensionClassLoader")) {
                return this._extensionClassLoader;
            }
            String propertyValue = (this._xmlSecurityManager != null) ? this._xmlSecurityManager.getLimitAsString(name) : null;
            if (propertyValue != null) {
                return propertyValue;
            }
            propertyValue = ((this._xmlSecurityPropertyMgr != null) ? this._xmlSecurityPropertyMgr.getValue(name) : null);
            if (propertyValue != null) {
                return propertyValue;
            }
            final ErrorMsg err = new ErrorMsg("JAXP_INVALID_ATTR_ERR", name);
            throw new IllegalArgumentException(err.toString());
        }
    }
    
    @Override
    public void setAttribute(final String name, final Object value) throws IllegalArgumentException {
        if (name.equals("translet-name") && value instanceof String) {
            this._transletName = (String)value;
            return;
        }
        if (name.equals("destination-directory") && value instanceof String) {
            this._destinationDirectory = (String)value;
            return;
        }
        if (name.equals("package-name") && value instanceof String) {
            this._packageName = (String)value;
            return;
        }
        if (name.equals("jar-name") && value instanceof String) {
            this._jarFileName = (String)value;
            return;
        }
        Label_0445: {
            if (name.equals("generate-translet")) {
                if (value instanceof Boolean) {
                    this._generateTranslet = (boolean)value;
                    return;
                }
                if (value instanceof String) {
                    this._generateTranslet = ((String)value).equalsIgnoreCase("true");
                    return;
                }
            }
            else if (name.equals("auto-translet")) {
                if (value instanceof Boolean) {
                    this._autoTranslet = (boolean)value;
                    return;
                }
                if (value instanceof String) {
                    this._autoTranslet = ((String)value).equalsIgnoreCase("true");
                    return;
                }
            }
            else if (name.equals("use-classpath")) {
                if (value instanceof Boolean) {
                    this._useClasspath = (boolean)value;
                    return;
                }
                if (value instanceof String) {
                    this._useClasspath = ((String)value).equalsIgnoreCase("true");
                    return;
                }
            }
            else if (name.equals("debug")) {
                if (value instanceof Boolean) {
                    this._debug = (boolean)value;
                    return;
                }
                if (value instanceof String) {
                    this._debug = ((String)value).equalsIgnoreCase("true");
                    return;
                }
            }
            else if (name.equals("enable-inlining")) {
                if (value instanceof Boolean) {
                    this._enableInlining = (boolean)value;
                    return;
                }
                if (value instanceof String) {
                    this._enableInlining = ((String)value).equalsIgnoreCase("true");
                    return;
                }
            }
            else if (name.equals("indent-number")) {
                if (value instanceof String) {
                    try {
                        this._indentNumber = Integer.parseInt((String)value);
                        return;
                    }
                    catch (final NumberFormatException ex) {
                        break Label_0445;
                    }
                }
                if (value instanceof Integer) {
                    this._indentNumber = (int)value;
                    return;
                }
            }
            else if (name.equals("jdk.xml.transform.extensionClassLoader")) {
                if (value instanceof ClassLoader) {
                    this._extensionClassLoader = (ClassLoader)value;
                    return;
                }
                final ErrorMsg err = new ErrorMsg("JAXP_INVALID_ATTR_VALUE_ERR", "Extension Functions ClassLoader");
                throw new IllegalArgumentException(err.toString());
            }
        }
        if (this._xmlSecurityManager != null && this._xmlSecurityManager.setLimit(name, XMLSecurityManager.State.APIPROPERTY, value)) {
            return;
        }
        if (this._xmlSecurityPropertyMgr != null && this._xmlSecurityPropertyMgr.setValue(name, FeaturePropertyBase.State.APIPROPERTY, value)) {
            this._accessExternalDTD = this._xmlSecurityPropertyMgr.getValue(XMLSecurityPropertyManager.Property.ACCESS_EXTERNAL_DTD);
            this._accessExternalStylesheet = this._xmlSecurityPropertyMgr.getValue(XMLSecurityPropertyManager.Property.ACCESS_EXTERNAL_STYLESHEET);
            return;
        }
        final ErrorMsg err = new ErrorMsg("JAXP_INVALID_ATTR_ERR", name);
        throw new IllegalArgumentException(err.toString());
    }
    
    @Override
    public void setFeature(final String name, final boolean value) throws TransformerConfigurationException {
        if (name == null) {
            final ErrorMsg err = new ErrorMsg("JAXP_SET_FEATURE_NULL_NAME");
            throw new NullPointerException(err.toString());
        }
        if (name.equals("http://javax.xml.XMLConstants/feature/secure-processing")) {
            if (this._isSecureMode && !value) {
                final ErrorMsg err = new ErrorMsg("JAXP_SECUREPROCESSING_FEATURE");
                throw new TransformerConfigurationException(err.toString());
            }
            this._isNotSecureProcessing = !value;
            this._xmlSecurityManager.setSecureProcessing(value);
            if (value && XalanConstants.IS_JDK8_OR_ABOVE) {
                this._xmlSecurityPropertyMgr.setValue(XMLSecurityPropertyManager.Property.ACCESS_EXTERNAL_DTD, FeaturePropertyBase.State.FSP, "");
                this._xmlSecurityPropertyMgr.setValue(XMLSecurityPropertyManager.Property.ACCESS_EXTERNAL_STYLESHEET, FeaturePropertyBase.State.FSP, "");
                this._accessExternalDTD = this._xmlSecurityPropertyMgr.getValue(XMLSecurityPropertyManager.Property.ACCESS_EXTERNAL_DTD);
                this._accessExternalStylesheet = this._xmlSecurityPropertyMgr.getValue(XMLSecurityPropertyManager.Property.ACCESS_EXTERNAL_STYLESHEET);
            }
            if (value && this._xmlFeatures != null) {
                this._xmlFeatures.setFeature(JdkXmlFeatures.XmlFeature.ENABLE_EXTENSION_FUNCTION, JdkXmlFeatures.State.FSP, false);
            }
        }
        else {
            if (name.equals("http://www.oracle.com/feature/use-service-mechanism") && this._isSecureMode) {
                return;
            }
            if (this._xmlFeatures != null && this._xmlFeatures.setFeature(name, JdkXmlFeatures.State.APIPROPERTY, value)) {
                if (name.equals("jdk.xml.overrideDefaultParser") || name.equals("http://www.oracle.com/feature/use-service-mechanism")) {
                    this._overrideDefaultParser = this._xmlFeatures.getFeature(JdkXmlFeatures.XmlFeature.JDK_OVERRIDE_PARSER);
                }
                return;
            }
            final ErrorMsg err = new ErrorMsg("JAXP_UNSUPPORTED_FEATURE", name);
            throw new TransformerConfigurationException(err.toString());
        }
    }
    
    @Override
    public boolean getFeature(final String name) {
        final String[] features = { "http://javax.xml.transform.dom.DOMSource/feature", "http://javax.xml.transform.dom.DOMResult/feature", "http://javax.xml.transform.sax.SAXSource/feature", "http://javax.xml.transform.sax.SAXResult/feature", "http://javax.xml.transform.stax.StAXSource/feature", "http://javax.xml.transform.stax.StAXResult/feature", "http://javax.xml.transform.stream.StreamSource/feature", "http://javax.xml.transform.stream.StreamResult/feature", "http://javax.xml.transform.sax.SAXTransformerFactory/feature", "http://javax.xml.transform.sax.SAXTransformerFactory/feature/xmlfilter", "http://www.oracle.com/feature/use-service-mechanism" };
        if (name == null) {
            final ErrorMsg err = new ErrorMsg("JAXP_GET_FEATURE_NULL_NAME");
            throw new NullPointerException(err.toString());
        }
        for (int i = 0; i < features.length; ++i) {
            if (name.equals(features[i])) {
                return true;
            }
        }
        if (name.equals("http://javax.xml.XMLConstants/feature/secure-processing")) {
            return !this._isNotSecureProcessing;
        }
        final int index = this._xmlFeatures.getIndex(name);
        return index > -1 && this._xmlFeatures.getFeature(index);
    }
    
    public boolean overrideDefaultParser() {
        return this._overrideDefaultParser;
    }
    
    public JdkXmlFeatures getJdkXmlFeatures() {
        return this._xmlFeatures;
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
    public Source getAssociatedStylesheet(final Source source, final String media, final String title, final String charset) throws TransformerConfigurationException {
        XMLReader reader = null;
        final StylesheetPIHandler _stylesheetPIHandler = new StylesheetPIHandler(null, media, title, charset);
        try {
            if (source instanceof DOMSource) {
                final DOMSource domsrc = (DOMSource)source;
                final String baseId = domsrc.getSystemId();
                final Node node = domsrc.getNode();
                final DOM2SAX dom2sax = new DOM2SAX(node);
                _stylesheetPIHandler.setBaseId(baseId);
                dom2sax.setContentHandler(_stylesheetPIHandler);
                dom2sax.parse();
            }
            else {
                if (source instanceof SAXSource) {
                    reader = ((SAXSource)source).getXMLReader();
                }
                final InputSource isource = SAXSource.sourceToInputSource(source);
                final String baseId = isource.getSystemId();
                if (reader == null) {
                    reader = JdkXmlUtils.getXMLReader(this._overrideDefaultParser, !this._isNotSecureProcessing);
                }
                _stylesheetPIHandler.setBaseId(baseId);
                reader.setContentHandler(_stylesheetPIHandler);
                reader.parse(isource);
            }
            if (this._uriResolver != null) {
                _stylesheetPIHandler.setURIResolver(this._uriResolver);
            }
        }
        catch (final StopParseException ex) {}
        catch (final SAXException se) {
            throw new TransformerConfigurationException("getAssociatedStylesheets failed", se);
        }
        catch (final IOException ioe) {
            throw new TransformerConfigurationException("getAssociatedStylesheets failed", ioe);
        }
        return _stylesheetPIHandler.getAssociatedStylesheet();
    }
    
    @Override
    public Transformer newTransformer() throws TransformerConfigurationException {
        final TransformerImpl result = new TransformerImpl(new Properties(), this._indentNumber, this);
        if (this._uriResolver != null) {
            result.setURIResolver(this._uriResolver);
        }
        if (!this._isNotSecureProcessing) {
            result.setSecureProcessing(true);
        }
        return result;
    }
    
    @Override
    public Transformer newTransformer(final Source source) throws TransformerConfigurationException {
        final Templates templates = this.newTemplates(source);
        final Transformer transformer = templates.newTransformer();
        if (this._uriResolver != null) {
            transformer.setURIResolver(this._uriResolver);
        }
        return transformer;
    }
    
    private void passWarningsToListener(final ArrayList<ErrorMsg> messages) throws TransformerException {
        if (this._errorListener == null || messages == null) {
            return;
        }
        for (int count = messages.size(), pos = 0; pos < count; ++pos) {
            final ErrorMsg msg = messages.get(pos);
            if (msg.isWarningError()) {
                this._errorListener.error(new TransformerConfigurationException(msg.toString()));
            }
            else {
                this._errorListener.warning(new TransformerConfigurationException(msg.toString()));
            }
        }
    }
    
    private void passErrorsToListener(final ArrayList<ErrorMsg> messages) {
        try {
            if (this._errorListener == null || messages == null) {
                return;
            }
            for (int count = messages.size(), pos = 0; pos < count; ++pos) {
                final String message = messages.get(pos).toString();
                this._errorListener.error(new TransformerException(message));
            }
        }
        catch (final TransformerException ex) {}
    }
    
    @Override
    public Templates newTemplates(final Source source) throws TransformerConfigurationException {
        if (this._useClasspath) {
            String transletName = this.getTransletBaseName(source);
            if (this._packageName != null) {
                transletName = this._packageName + "." + transletName;
            }
            try {
                final Class clazz = ObjectFactory.findProviderClass(transletName, true);
                this.resetTransientAttributes();
                return new TemplatesImpl(new Class[] { clazz }, transletName, null, this._indentNumber, this);
            }
            catch (final ClassNotFoundException cnfe) {
                final ErrorMsg err = new ErrorMsg("CLASS_NOT_FOUND_ERR", transletName);
                throw new TransformerConfigurationException(err.toString());
            }
            catch (final Exception e) {
                final ErrorMsg err = new ErrorMsg(new ErrorMsg("RUNTIME_ERROR_KEY") + e.getMessage());
                throw new TransformerConfigurationException(err.toString());
            }
        }
        if (this._autoTranslet) {
            String transletClassName = this.getTransletBaseName(source);
            if (this._packageName != null) {
                transletClassName = this._packageName + "." + transletClassName;
            }
            byte[][] bytecodes;
            if (this._jarFileName != null) {
                bytecodes = this.getBytecodesFromJar(source, transletClassName);
            }
            else {
                bytecodes = this.getBytecodesFromClasses(source, transletClassName);
            }
            if (bytecodes != null) {
                if (this._debug) {
                    if (this._jarFileName != null) {
                        System.err.println(new ErrorMsg("TRANSFORM_WITH_JAR_STR", transletClassName, this._jarFileName));
                    }
                    else {
                        System.err.println(new ErrorMsg("TRANSFORM_WITH_TRANSLET_STR", transletClassName));
                    }
                }
                this.resetTransientAttributes();
                return new TemplatesImpl(bytecodes, transletClassName, null, this._indentNumber, this);
            }
        }
        final XSLTC xsltc = new XSLTC(this._xmlFeatures);
        if (this._debug) {
            xsltc.setDebug(true);
        }
        if (this._enableInlining) {
            xsltc.setTemplateInlining(true);
        }
        else {
            xsltc.setTemplateInlining(false);
        }
        if (!this._isNotSecureProcessing) {
            xsltc.setSecureProcessing(true);
        }
        xsltc.setProperty("http://javax.xml.XMLConstants/property/accessExternalStylesheet", this._accessExternalStylesheet);
        xsltc.setProperty("http://javax.xml.XMLConstants/property/accessExternalDTD", this._accessExternalDTD);
        xsltc.setProperty("http://apache.org/xml/properties/security-manager", this._xmlSecurityManager);
        xsltc.setProperty("jdk.xml.transform.extensionClassLoader", this._extensionClassLoader);
        xsltc.init();
        if (!this._isNotSecureProcessing) {
            this._xsltcExtensionFunctions = xsltc.getExternalExtensionFunctions();
        }
        if (this._uriResolver != null) {
            xsltc.setSourceLoader(this);
        }
        if (this._piParams != null && this._piParams.get(source) != null) {
            final PIParamWrapper p = this._piParams.get(source);
            if (p != null) {
                xsltc.setPIParameters(p._media, p._title, p._charset);
            }
        }
        int outputType = 2;
        if (this._generateTranslet || this._autoTranslet) {
            xsltc.setClassName(this.getTransletBaseName(source));
            if (this._destinationDirectory != null) {
                xsltc.setDestDirectory(this._destinationDirectory);
            }
            else {
                final String xslName = this.getStylesheetFileName(source);
                if (xslName != null) {
                    final File xslFile = new File(xslName);
                    final String xslDir = xslFile.getParent();
                    if (xslDir != null) {
                        xsltc.setDestDirectory(xslDir);
                    }
                }
            }
            if (this._packageName != null) {
                xsltc.setPackageName(this._packageName);
            }
            if (this._jarFileName != null) {
                xsltc.setJarFileName(this._jarFileName);
                outputType = 5;
            }
            else {
                outputType = 4;
            }
        }
        final InputSource input = Util.getInputSource(xsltc, source);
        final byte[][] bytecodes2 = xsltc.compile(null, input, outputType);
        final String transletName2 = xsltc.getClassName();
        if ((this._generateTranslet || this._autoTranslet) && bytecodes2 != null && this._jarFileName != null) {
            try {
                xsltc.outputToJar();
            }
            catch (final IOException ex) {}
        }
        this.resetTransientAttributes();
        Label_0707: {
            if (this._errorListener != this) {
                try {
                    this.passWarningsToListener(xsltc.getWarnings());
                    break Label_0707;
                }
                catch (final TransformerException e2) {
                    throw new TransformerConfigurationException(e2);
                }
            }
            xsltc.printWarnings();
        }
        if (bytecodes2 == null) {
            final ArrayList<ErrorMsg> errs = xsltc.getErrors();
            ErrorMsg err2;
            if (errs != null) {
                err2 = errs.get(errs.size() - 1);
            }
            else {
                err2 = new ErrorMsg("JAXP_COMPILE_ERR");
            }
            final Throwable cause = err2.getCause();
            TransformerConfigurationException exc;
            if (cause != null) {
                exc = new TransformerConfigurationException(cause.getMessage(), cause);
            }
            else {
                exc = new TransformerConfigurationException(err2.toString());
            }
            if (this._errorListener != null) {
                this.passErrorsToListener(xsltc.getErrors());
                try {
                    this._errorListener.fatalError(exc);
                }
                catch (final TransformerException ex2) {}
            }
            else {
                xsltc.printErrors();
            }
            throw exc;
        }
        return new TemplatesImpl(bytecodes2, transletName2, xsltc.getOutputProperties(), this._indentNumber, this);
    }
    
    @Override
    public TemplatesHandler newTemplatesHandler() throws TransformerConfigurationException {
        final TemplatesHandlerImpl handler = new TemplatesHandlerImpl(this._indentNumber, this);
        if (this._uriResolver != null) {
            handler.setURIResolver(this._uriResolver);
        }
        return handler;
    }
    
    @Override
    public TransformerHandler newTransformerHandler() throws TransformerConfigurationException {
        final Transformer transformer = this.newTransformer();
        if (this._uriResolver != null) {
            transformer.setURIResolver(this._uriResolver);
        }
        return new TransformerHandlerImpl((TransformerImpl)transformer);
    }
    
    @Override
    public TransformerHandler newTransformerHandler(final Source src) throws TransformerConfigurationException {
        final Transformer transformer = this.newTransformer(src);
        if (this._uriResolver != null) {
            transformer.setURIResolver(this._uriResolver);
        }
        return new TransformerHandlerImpl((TransformerImpl)transformer);
    }
    
    @Override
    public TransformerHandler newTransformerHandler(final Templates templates) throws TransformerConfigurationException {
        final Transformer transformer = templates.newTransformer();
        final TransformerImpl internal = (TransformerImpl)transformer;
        return new TransformerHandlerImpl(internal);
    }
    
    @Override
    public XMLFilter newXMLFilter(final Source src) throws TransformerConfigurationException {
        final Templates templates = this.newTemplates(src);
        if (templates == null) {
            return null;
        }
        return this.newXMLFilter(templates);
    }
    
    @Override
    public XMLFilter newXMLFilter(final Templates templates) throws TransformerConfigurationException {
        try {
            return new TrAXFilter(templates);
        }
        catch (final TransformerConfigurationException e1) {
            if (this._errorListener != null) {
                try {
                    this._errorListener.fatalError(e1);
                    return null;
                }
                catch (final TransformerException e2) {
                    new TransformerConfigurationException(e2);
                }
            }
            throw e1;
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
    public InputSource loadSource(final String href, final String context, final XSLTC xsltc) {
        try {
            if (this._uriResolver != null) {
                final Source source = this._uriResolver.resolve(href, context);
                if (source != null) {
                    return Util.getInputSource(xsltc, source);
                }
            }
        }
        catch (final TransformerException e) {
            final ErrorMsg msg = new ErrorMsg("INVALID_URI_ERR", href + "\n" + e.getMessage(), this);
            xsltc.getParser().reportError(2, msg);
        }
        return null;
    }
    
    private void resetTransientAttributes() {
        this._transletName = "GregorSamsa";
        this._destinationDirectory = null;
        this._packageName = null;
        this._jarFileName = null;
    }
    
    private byte[][] getBytecodesFromClasses(final Source source, final String fullClassName) {
        if (fullClassName == null) {
            return null;
        }
        final String xslFileName = this.getStylesheetFileName(source);
        File xslFile = null;
        if (xslFileName != null) {
            xslFile = new File(xslFileName);
        }
        final int lastDotIndex = fullClassName.lastIndexOf(46);
        String transletName;
        if (lastDotIndex > 0) {
            transletName = fullClassName.substring(lastDotIndex + 1);
        }
        else {
            transletName = fullClassName;
        }
        String transletPath = fullClassName.replace('.', '/');
        if (this._destinationDirectory != null) {
            transletPath = this._destinationDirectory + "/" + transletPath + ".class";
        }
        else if (xslFile != null && xslFile.getParent() != null) {
            transletPath = xslFile.getParent() + "/" + transletPath + ".class";
        }
        else {
            transletPath += ".class";
        }
        final File transletFile = new File(transletPath);
        if (!transletFile.exists()) {
            return null;
        }
        if (xslFile != null && xslFile.exists()) {
            final long xslTimestamp = xslFile.lastModified();
            final long transletTimestamp = transletFile.lastModified();
            if (transletTimestamp < xslTimestamp) {
                return null;
            }
        }
        final Vector bytecodes = new Vector();
        final int fileLength = (int)transletFile.length();
        if (fileLength <= 0) {
            return null;
        }
        FileInputStream input;
        try {
            input = new FileInputStream(transletFile);
        }
        catch (final FileNotFoundException e) {
            return null;
        }
        final byte[] bytes = new byte[fileLength];
        try {
            this.readFromInputStream(bytes, input, fileLength);
            input.close();
        }
        catch (final IOException e2) {
            return null;
        }
        bytecodes.addElement(bytes);
        String transletParentDir = transletFile.getParent();
        if (transletParentDir == null) {
            transletParentDir = SecuritySupport.getSystemProperty("user.dir");
        }
        final File transletParentFile = new File(transletParentDir);
        final String transletAuxPrefix = transletName + "$";
        final File[] auxfiles = transletParentFile.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(final File dir, final String name) {
                return name.endsWith(".class") && name.startsWith(transletAuxPrefix);
            }
        });
        for (int i = 0; i < auxfiles.length; ++i) {
            final File auxfile = auxfiles[i];
            final int auxlength = (int)auxfile.length();
            if (auxlength > 0) {
                FileInputStream auxinput = null;
                try {
                    auxinput = new FileInputStream(auxfile);
                }
                catch (final FileNotFoundException e3) {
                    continue;
                }
                final byte[] bytes2 = new byte[auxlength];
                try {
                    this.readFromInputStream(bytes2, auxinput, auxlength);
                    auxinput.close();
                }
                catch (final IOException e4) {
                    continue;
                }
                bytecodes.addElement(bytes2);
            }
        }
        final int count = bytecodes.size();
        if (count > 0) {
            final byte[][] result = new byte[count][1];
            for (int j = 0; j < count; ++j) {
                result[j] = bytecodes.elementAt(j);
            }
            return result;
        }
        return null;
    }
    
    private byte[][] getBytecodesFromJar(final Source source, final String fullClassName) {
        final String xslFileName = this.getStylesheetFileName(source);
        File xslFile = null;
        if (xslFileName != null) {
            xslFile = new File(xslFileName);
        }
        String jarPath;
        if (this._destinationDirectory != null) {
            jarPath = this._destinationDirectory + "/" + this._jarFileName;
        }
        else if (xslFile != null && xslFile.getParent() != null) {
            jarPath = xslFile.getParent() + "/" + this._jarFileName;
        }
        else {
            jarPath = this._jarFileName;
        }
        final File file = new File(jarPath);
        if (!file.exists()) {
            return null;
        }
        if (xslFile != null && xslFile.exists()) {
            final long xslTimestamp = xslFile.lastModified();
            final long transletTimestamp = file.lastModified();
            if (transletTimestamp < xslTimestamp) {
                return null;
            }
        }
        ZipFile jarFile;
        try {
            jarFile = new ZipFile(file);
        }
        catch (final IOException e) {
            return null;
        }
        final String transletPath = fullClassName.replace('.', '/');
        final String transletAuxPrefix = transletPath + "$";
        final String transletFullName = transletPath + ".class";
        final Vector bytecodes = new Vector();
        final Enumeration entries = jarFile.entries();
        while (entries.hasMoreElements()) {
            final ZipEntry entry = entries.nextElement();
            final String entryName = entry.getName();
            if (entry.getSize() > 0L) {
                if (!entryName.equals(transletFullName)) {
                    if (!entryName.endsWith(".class") || !entryName.startsWith(transletAuxPrefix)) {
                        continue;
                    }
                }
                try {
                    final InputStream input = jarFile.getInputStream(entry);
                    final int size = (int)entry.getSize();
                    final byte[] bytes = new byte[size];
                    this.readFromInputStream(bytes, input, size);
                    input.close();
                    bytecodes.addElement(bytes);
                }
                catch (final IOException e2) {
                    return null;
                }
            }
        }
        final int count = bytecodes.size();
        if (count > 0) {
            final byte[][] result = new byte[count][1];
            for (int i = 0; i < count; ++i) {
                result[i] = bytecodes.elementAt(i);
            }
            return result;
        }
        return null;
    }
    
    private void readFromInputStream(final byte[] bytes, final InputStream input, final int size) throws IOException {
        for (int n = 0, offset = 0, length = size; length > 0 && (n = input.read(bytes, offset, length)) > 0; offset += n, length -= n) {}
    }
    
    private String getTransletBaseName(final Source source) {
        String transletBaseName = null;
        if (!this._transletName.equals("GregorSamsa")) {
            return this._transletName;
        }
        final String systemId = source.getSystemId();
        if (systemId != null) {
            String baseName = Util.baseName(systemId);
            if (baseName != null) {
                baseName = Util.noExtName(baseName);
                transletBaseName = Util.toJavaName(baseName);
            }
        }
        return (transletBaseName != null) ? transletBaseName : "GregorSamsa";
    }
    
    private String getStylesheetFileName(final Source source) {
        final String systemId = source.getSystemId();
        if (systemId == null) {
            return null;
        }
        final File file = new File(systemId);
        if (file.exists()) {
            return systemId;
        }
        URL url;
        try {
            url = new URL(systemId);
        }
        catch (final MalformedURLException e) {
            return null;
        }
        if ("file".equals(url.getProtocol())) {
            return url.getFile();
        }
        return null;
    }
    
    protected final XSLTCDTMManager createNewDTMManagerInstance() {
        return XSLTCDTMManager.createNewDTMManagerInstance();
    }
    
    private static class PIParamWrapper
    {
        public String _media;
        public String _title;
        public String _charset;
        
        public PIParamWrapper(final String media, final String title, final String charset) {
            this._media = null;
            this._title = null;
            this._charset = null;
            this._media = media;
            this._title = title;
            this._charset = charset;
        }
    }
}
