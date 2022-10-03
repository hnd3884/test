package org.cyberneko.html;

import org.apache.xerces.xni.XMLLocator;
import org.apache.xerces.xni.parser.XMLParseException;
import java.util.MissingResourceException;
import java.text.MessageFormat;
import java.util.ResourceBundle;
import java.lang.reflect.Field;
import org.cyberneko.html.xercesbridge.XercesBridge;
import org.apache.xerces.xni.parser.XMLDocumentSource;
import org.apache.xerces.xni.parser.XMLComponentManager;
import java.io.IOException;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.parser.XMLDocumentFilter;
import org.apache.xerces.xni.parser.XMLConfigurationException;
import org.apache.xerces.xni.parser.XMLInputSource;
import org.apache.xerces.util.DefaultErrorHandler;
import org.cyberneko.html.filters.NamespaceBinder;
import java.util.Vector;
import java.util.Locale;
import org.apache.xerces.xni.parser.XMLEntityResolver;
import org.apache.xerces.xni.parser.XMLErrorHandler;
import org.apache.xerces.xni.XMLDTDContentModelHandler;
import org.apache.xerces.xni.XMLDTDHandler;
import org.apache.xerces.xni.XMLDocumentHandler;
import org.apache.xerces.xni.parser.XMLPullParserConfiguration;
import org.apache.xerces.util.ParserConfigurationSettings;

public class HTMLConfiguration extends ParserConfigurationSettings implements XMLPullParserConfiguration
{
    protected static final String NAMESPACES = "http://xml.org/sax/features/namespaces";
    protected static final String AUGMENTATIONS = "http://cyberneko.org/html/features/augmentations";
    protected static final String REPORT_ERRORS = "http://cyberneko.org/html/features/report-errors";
    protected static final String SIMPLE_ERROR_FORMAT = "http://cyberneko.org/html/features/report-errors/simple";
    protected static final String BALANCE_TAGS = "http://cyberneko.org/html/features/balance-tags";
    protected static final String NAMES_ELEMS = "http://cyberneko.org/html/properties/names/elems";
    protected static final String NAMES_ATTRS = "http://cyberneko.org/html/properties/names/attrs";
    protected static final String FILTERS = "http://cyberneko.org/html/properties/filters";
    protected static final String ERROR_REPORTER = "http://cyberneko.org/html/properties/error-reporter";
    protected static final String ERROR_DOMAIN = "http://cyberneko.org/html";
    private static final Class[] DOCSOURCE;
    protected XMLDocumentHandler fDocumentHandler;
    protected XMLDTDHandler fDTDHandler;
    protected XMLDTDContentModelHandler fDTDContentModelHandler;
    protected XMLErrorHandler fErrorHandler;
    protected XMLEntityResolver fEntityResolver;
    protected Locale fLocale;
    protected boolean fCloseStream;
    protected final Vector fHTMLComponents;
    protected final HTMLScanner fDocumentScanner;
    protected final HTMLTagBalancer fTagBalancer;
    protected final NamespaceBinder fNamespaceBinder;
    protected final HTMLErrorReporter fErrorReporter;
    protected static boolean XERCES_2_0_0;
    protected static boolean XERCES_2_0_1;
    protected static boolean XML4J_4_0_x;
    
    public HTMLConfiguration() {
        this.fErrorHandler = (XMLErrorHandler)new DefaultErrorHandler();
        this.fLocale = Locale.getDefault();
        this.fHTMLComponents = new Vector(2);
        this.fDocumentScanner = this.createDocumentScanner();
        this.fTagBalancer = new HTMLTagBalancer();
        this.fNamespaceBinder = new NamespaceBinder();
        this.fErrorReporter = new ErrorReporter();
        this.addComponent(this.fDocumentScanner);
        this.addComponent(this.fTagBalancer);
        this.addComponent(this.fNamespaceBinder);
        final String VALIDATION = "http://xml.org/sax/features/validation";
        String[] recognizedFeatures = { "http://cyberneko.org/html/features/augmentations", "http://xml.org/sax/features/namespaces", VALIDATION, "http://cyberneko.org/html/features/report-errors", "http://cyberneko.org/html/features/report-errors/simple", "http://cyberneko.org/html/features/balance-tags" };
        this.addRecognizedFeatures(recognizedFeatures);
        this.setFeature("http://cyberneko.org/html/features/augmentations", false);
        this.setFeature("http://xml.org/sax/features/namespaces", true);
        this.setFeature(VALIDATION, false);
        this.setFeature("http://cyberneko.org/html/features/report-errors", false);
        this.setFeature("http://cyberneko.org/html/features/report-errors/simple", false);
        this.setFeature("http://cyberneko.org/html/features/balance-tags", true);
        if (HTMLConfiguration.XERCES_2_0_0) {
            recognizedFeatures = new String[] { "http://apache.org/xml/features/scanner/notify-builtin-refs" };
            this.addRecognizedFeatures(recognizedFeatures);
        }
        if (HTMLConfiguration.XERCES_2_0_0 || HTMLConfiguration.XERCES_2_0_1 || HTMLConfiguration.XML4J_4_0_x) {
            recognizedFeatures = new String[] { "http://apache.org/xml/features/validation/schema/normalized-value", "http://apache.org/xml/features/scanner/notify-char-refs" };
            this.addRecognizedFeatures(recognizedFeatures);
        }
        String[] recognizedProperties = { "http://cyberneko.org/html/properties/names/elems", "http://cyberneko.org/html/properties/names/attrs", "http://cyberneko.org/html/properties/filters", "http://cyberneko.org/html/properties/error-reporter" };
        this.addRecognizedProperties(recognizedProperties);
        this.setProperty("http://cyberneko.org/html/properties/names/elems", "upper");
        this.setProperty("http://cyberneko.org/html/properties/names/attrs", "lower");
        this.setProperty("http://cyberneko.org/html/properties/error-reporter", this.fErrorReporter);
        if (HTMLConfiguration.XERCES_2_0_0) {
            final String SYMBOL_TABLE = "http://apache.org/xml/properties/internal/symbol-table";
            recognizedProperties = new String[] { SYMBOL_TABLE };
            this.addRecognizedProperties(recognizedProperties);
            final Object symbolTable = ObjectFactory.createObject("org.apache.xerces.util.SymbolTable", "org.apache.xerces.util.SymbolTable");
            this.setProperty(SYMBOL_TABLE, symbolTable);
        }
    }
    
    protected HTMLScanner createDocumentScanner() {
        return new HTMLScanner();
    }
    
    public void setSkipDecode(final boolean skip) {
        if (this.fDocumentScanner != null) {
            this.fDocumentScanner.setSkipDecode(skip);
        }
    }
    
    public void pushInputSource(final XMLInputSource inputSource) {
        this.fDocumentScanner.pushInputSource(inputSource);
    }
    
    public void evaluateInputSource(final XMLInputSource inputSource) {
        this.fDocumentScanner.evaluateInputSource(inputSource);
    }
    
    public void setFeature(final String featureId, final boolean state) throws XMLConfigurationException {
        super.setFeature(featureId, state);
        for (int size = this.fHTMLComponents.size(), i = 0; i < size; ++i) {
            final HTMLComponent component = this.fHTMLComponents.elementAt(i);
            component.setFeature(featureId, state);
        }
    }
    
    public void setProperty(final String propertyId, final Object value) throws XMLConfigurationException {
        super.setProperty(propertyId, value);
        if (propertyId.equals("http://cyberneko.org/html/properties/filters")) {
            final XMLDocumentFilter[] filters = (XMLDocumentFilter[])this.getProperty("http://cyberneko.org/html/properties/filters");
            if (filters != null) {
                for (int i = 0; i < filters.length; ++i) {
                    final XMLDocumentFilter filter = filters[i];
                    if (filter instanceof HTMLComponent) {
                        this.addComponent((HTMLComponent)filter);
                    }
                }
            }
        }
        for (int size = this.fHTMLComponents.size(), i = 0; i < size; ++i) {
            final HTMLComponent component = this.fHTMLComponents.elementAt(i);
            component.setProperty(propertyId, value);
        }
    }
    
    public void setDocumentHandler(final XMLDocumentHandler handler) {
        this.fDocumentHandler = handler;
        if (handler instanceof HTMLTagBalancingListener) {
            this.fTagBalancer.setTagBalancingListener((HTMLTagBalancingListener)handler);
        }
    }
    
    public XMLDocumentHandler getDocumentHandler() {
        return this.fDocumentHandler;
    }
    
    public void setDTDHandler(final XMLDTDHandler handler) {
        this.fDTDHandler = handler;
    }
    
    public XMLDTDHandler getDTDHandler() {
        return this.fDTDHandler;
    }
    
    public void setDTDContentModelHandler(final XMLDTDContentModelHandler handler) {
        this.fDTDContentModelHandler = handler;
    }
    
    public XMLDTDContentModelHandler getDTDContentModelHandler() {
        return this.fDTDContentModelHandler;
    }
    
    public void setErrorHandler(final XMLErrorHandler handler) {
        this.fErrorHandler = handler;
    }
    
    public XMLErrorHandler getErrorHandler() {
        return this.fErrorHandler;
    }
    
    public void setEntityResolver(final XMLEntityResolver resolver) {
        this.fEntityResolver = resolver;
    }
    
    public XMLEntityResolver getEntityResolver() {
        return this.fEntityResolver;
    }
    
    public void setLocale(Locale locale) {
        if (locale == null) {
            locale = Locale.getDefault();
        }
        this.fLocale = locale;
    }
    
    public Locale getLocale() {
        return this.fLocale;
    }
    
    public void parse(final XMLInputSource source) throws XNIException, IOException {
        this.setInputSource(source);
        this.parse(true);
    }
    
    public void setInputSource(final XMLInputSource inputSource) throws XMLConfigurationException, IOException {
        this.reset();
        this.fCloseStream = (inputSource.getByteStream() == null && inputSource.getCharacterStream() == null);
        this.fDocumentScanner.setInputSource(inputSource);
    }
    
    public boolean parse(final boolean complete) throws XNIException, IOException {
        try {
            final boolean more = this.fDocumentScanner.scanDocument(complete);
            if (!more) {
                this.cleanup();
            }
            return more;
        }
        catch (final XNIException e) {
            this.cleanup();
            throw e;
        }
        catch (final IOException e2) {
            this.cleanup();
            throw e2;
        }
    }
    
    public void cleanup() {
        this.fDocumentScanner.cleanup(this.fCloseStream);
    }
    
    protected void addComponent(final HTMLComponent component) {
        this.fHTMLComponents.addElement(component);
        final String[] features = component.getRecognizedFeatures();
        this.addRecognizedFeatures(features);
        for (int featureCount = (features != null) ? features.length : 0, i = 0; i < featureCount; ++i) {
            final Boolean state = component.getFeatureDefault(features[i]);
            if (state != null) {
                this.setFeature(features[i], state);
            }
        }
        final String[] properties = component.getRecognizedProperties();
        this.addRecognizedProperties(properties);
        for (int propertyCount = (properties != null) ? properties.length : 0, j = 0; j < propertyCount; ++j) {
            final Object value = component.getPropertyDefault(properties[j]);
            if (value != null) {
                this.setProperty(properties[j], value);
            }
        }
    }
    
    public void clearFilterComponents(final XMLDocumentFilter[] filterComponents) {
        this.reset();
        for (final XMLDocumentFilter filter : filterComponents) {
            if (filter != null) {
                this.fHTMLComponents.remove(filter);
            }
        }
    }
    
    protected void reset() throws XMLConfigurationException {
        for (int size = this.fHTMLComponents.size(), i = 0; i < size; ++i) {
            final HTMLComponent component = this.fHTMLComponents.elementAt(i);
            component.reset((XMLComponentManager)this);
        }
        XMLDocumentSource lastSource = (XMLDocumentSource)this.fDocumentScanner;
        if (this.getFeature("http://xml.org/sax/features/namespaces")) {
            lastSource.setDocumentHandler((XMLDocumentHandler)this.fNamespaceBinder);
            this.fNamespaceBinder.setDocumentSource((XMLDocumentSource)this.fTagBalancer);
            lastSource = (XMLDocumentSource)this.fNamespaceBinder;
        }
        if (this.getFeature("http://cyberneko.org/html/features/balance-tags")) {
            lastSource.setDocumentHandler((XMLDocumentHandler)this.fTagBalancer);
            this.fTagBalancer.setDocumentSource((XMLDocumentSource)this.fDocumentScanner);
            lastSource = (XMLDocumentSource)this.fTagBalancer;
        }
        final XMLDocumentFilter[] filters = (XMLDocumentFilter[])this.getProperty("http://cyberneko.org/html/properties/filters");
        if (filters != null) {
            for (int j = 0; j < filters.length; ++j) {
                final XMLDocumentFilter filter = filters[j];
                XercesBridge.getInstance().XMLDocumentFilter_setDocumentSource(filter, lastSource);
                lastSource.setDocumentHandler((XMLDocumentHandler)filter);
                lastSource = (XMLDocumentSource)filter;
            }
        }
        lastSource.setDocumentHandler(this.fDocumentHandler);
    }
    
    static {
        DOCSOURCE = new Class[] { XMLDocumentSource.class };
        HTMLConfiguration.XERCES_2_0_0 = false;
        HTMLConfiguration.XERCES_2_0_1 = false;
        HTMLConfiguration.XML4J_4_0_x = false;
        try {
            final String VERSION = "org.apache.xerces.impl.Version";
            final Object version = ObjectFactory.createObject(VERSION, VERSION);
            final Field field = version.getClass().getField("fVersion");
            final String versionStr = String.valueOf(field.get(version));
            HTMLConfiguration.XERCES_2_0_0 = versionStr.equals("Xerces-J 2.0.0");
            HTMLConfiguration.XERCES_2_0_1 = versionStr.equals("Xerces-J 2.0.1");
            HTMLConfiguration.XML4J_4_0_x = versionStr.startsWith("XML4J 4.0.");
        }
        catch (final Throwable t) {}
    }
    
    protected class ErrorReporter implements HTMLErrorReporter
    {
        protected Locale fLastLocale;
        protected ResourceBundle fErrorMessages;
        
        @Override
        public String formatMessage(final String key, final Object[] args) {
            if (!HTMLConfiguration.this.getFeature("http://cyberneko.org/html/features/report-errors/simple")) {
                if (!HTMLConfiguration.this.fLocale.equals(this.fLastLocale)) {
                    this.fErrorMessages = null;
                    this.fLastLocale = HTMLConfiguration.this.fLocale;
                }
                if (this.fErrorMessages == null) {
                    this.fErrorMessages = ResourceBundle.getBundle("org/cyberneko/html/res/ErrorMessages", HTMLConfiguration.this.fLocale);
                }
                try {
                    final String value = this.fErrorMessages.getString(key);
                    final String message = MessageFormat.format(value, args);
                    return message;
                }
                catch (final MissingResourceException ex) {}
            }
            return this.formatSimpleMessage(key, args);
        }
        
        @Override
        public void reportWarning(final String key, final Object[] args) throws XMLParseException {
            if (HTMLConfiguration.this.fErrorHandler != null) {
                HTMLConfiguration.this.fErrorHandler.warning("http://cyberneko.org/html", key, this.createException(key, args));
            }
        }
        
        @Override
        public void reportError(final String key, final Object[] args) throws XMLParseException {
            if (HTMLConfiguration.this.fErrorHandler != null) {
                HTMLConfiguration.this.fErrorHandler.error("http://cyberneko.org/html", key, this.createException(key, args));
            }
        }
        
        protected XMLParseException createException(final String key, final Object[] args) {
            final String message = this.formatMessage(key, args);
            return new XMLParseException((XMLLocator)HTMLConfiguration.this.fDocumentScanner, message);
        }
        
        protected String formatSimpleMessage(final String key, final Object[] args) {
            final StringBuffer str = new StringBuffer();
            str.append("http://cyberneko.org/html");
            str.append('#');
            str.append(key);
            if (args != null && args.length > 0) {
                str.append('\t');
                for (int i = 0; i < args.length; ++i) {
                    if (i > 0) {
                        str.append('\t');
                    }
                    str.append(String.valueOf(args[i]));
                }
            }
            return str.toString();
        }
    }
}
