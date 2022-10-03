package com.sun.org.apache.xerces.internal.xinclude;

import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;
import com.sun.org.apache.xerces.internal.xni.XMLString;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import com.sun.org.apache.xerces.internal.xni.Augmentations;
import com.sun.org.apache.xerces.internal.xni.XMLAttributes;
import java.util.StringTokenizer;
import com.sun.org.apache.xerces.internal.util.MessageFormatter;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import java.util.Enumeration;
import com.sun.org.apache.xerces.internal.impl.Constants;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import java.util.Stack;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.util.ParserConfigurationSettings;
import com.sun.org.apache.xerces.internal.xni.parser.XMLEntityResolver;
import com.sun.org.apache.xerces.internal.impl.dtd.DTDGrammar;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarDescription;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarPool;
import com.sun.org.apache.xerces.internal.impl.XMLErrorReporter;
import com.sun.org.apache.xerces.internal.xni.XMLLocator;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDocumentSource;
import com.sun.org.apache.xerces.internal.xni.XMLDocumentHandler;

public class XPointerElementHandler implements XPointerSchema
{
    protected static final String ERROR_REPORTER = "http://apache.org/xml/properties/internal/error-reporter";
    protected static final String GRAMMAR_POOL = "http://apache.org/xml/properties/internal/grammar-pool";
    protected static final String ENTITY_RESOLVER = "http://apache.org/xml/properties/internal/entity-resolver";
    protected static final String XPOINTER_SCHEMA = "http://apache.org/xml/properties/xpointer-schema";
    private static final String[] RECOGNIZED_FEATURES;
    private static final Boolean[] FEATURE_DEFAULTS;
    private static final String[] RECOGNIZED_PROPERTIES;
    private static final Object[] PROPERTY_DEFAULTS;
    protected XMLDocumentHandler fDocumentHandler;
    protected XMLDocumentSource fDocumentSource;
    protected XIncludeHandler fParentXIncludeHandler;
    protected XMLLocator fDocLocation;
    protected XIncludeNamespaceSupport fNamespaceContext;
    protected XMLErrorReporter fErrorReporter;
    protected XMLGrammarPool fGrammarPool;
    protected XMLGrammarDescription fGrammarDesc;
    protected DTDGrammar fDTDGrammar;
    protected XMLEntityResolver fEntityResolver;
    protected ParserConfigurationSettings fSettings;
    protected StringBuffer fPointer;
    private int elemCount;
    private int fDepth;
    private int fRootDepth;
    private static final int INITIAL_SIZE = 8;
    private boolean[] fSawInclude;
    private boolean[] fSawFallback;
    private int[] fState;
    QName foundElement;
    boolean skip;
    String fSchemaName;
    String fSchemaPointer;
    boolean fSubResourceIdentified;
    Stack fPointerToken;
    int fCurrentTokenint;
    String fCurrentTokenString;
    int fCurrentTokenType;
    Stack ftempCurrentElement;
    int fElementCount;
    int fCurrentToken;
    boolean includeElement;
    
    public XPointerElementHandler() {
        this.elemCount = 0;
        this.fSawInclude = new boolean[8];
        this.fSawFallback = new boolean[8];
        this.fState = new int[8];
        this.foundElement = null;
        this.skip = false;
        this.fPointerToken = new Stack();
        this.fCurrentTokenint = 0;
        this.fCurrentTokenString = null;
        this.fCurrentTokenType = 0;
        this.ftempCurrentElement = new Stack();
        this.fElementCount = 0;
        this.fDepth = 0;
        this.fRootDepth = 0;
        this.fSawFallback[this.fDepth] = false;
        this.fSawInclude[this.fDepth] = false;
        this.fSchemaName = "element";
    }
    
    @Override
    public void reset() {
        this.elemCount = 0;
        this.fPointerToken = null;
        this.fCurrentTokenint = 0;
        this.fCurrentTokenString = null;
        this.fCurrentTokenType = 0;
        this.fElementCount = 0;
        this.fCurrentToken = 0;
        this.includeElement = false;
        this.foundElement = null;
        this.skip = false;
        this.fSubResourceIdentified = false;
    }
    
    @Override
    public void reset(final XMLComponentManager componentManager) throws XNIException {
        this.fNamespaceContext = null;
        this.elemCount = 0;
        this.fDepth = 0;
        this.fRootDepth = 0;
        this.fPointerToken = null;
        this.fCurrentTokenint = 0;
        this.fCurrentTokenString = null;
        this.fCurrentTokenType = 0;
        this.foundElement = null;
        this.includeElement = false;
        this.skip = false;
        this.fSubResourceIdentified = false;
        try {
            this.setErrorReporter((XMLErrorReporter)componentManager.getProperty("http://apache.org/xml/properties/internal/error-reporter"));
        }
        catch (final XMLConfigurationException e) {
            this.fErrorReporter = null;
        }
        try {
            this.fGrammarPool = (XMLGrammarPool)componentManager.getProperty("http://apache.org/xml/properties/internal/grammar-pool");
        }
        catch (final XMLConfigurationException e) {
            this.fGrammarPool = null;
        }
        try {
            this.fEntityResolver = (XMLEntityResolver)componentManager.getProperty("http://apache.org/xml/properties/internal/entity-resolver");
        }
        catch (final XMLConfigurationException e) {
            this.fEntityResolver = null;
        }
        this.fSettings = new ParserConfigurationSettings();
        final Enumeration xercesFeatures = Constants.getXercesFeatures();
        while (xercesFeatures.hasMoreElements()) {
            final String featureId = xercesFeatures.nextElement();
            this.fSettings.addRecognizedFeatures(new String[] { featureId });
            try {
                this.fSettings.setFeature(featureId, componentManager.getFeature(featureId));
            }
            catch (final XMLConfigurationException ex) {}
        }
    }
    
    @Override
    public String[] getRecognizedFeatures() {
        return XPointerElementHandler.RECOGNIZED_FEATURES;
    }
    
    @Override
    public void setFeature(final String featureId, final boolean state) throws XMLConfigurationException {
        if (this.fSettings != null) {
            this.fSettings.setFeature(featureId, state);
        }
    }
    
    @Override
    public String[] getRecognizedProperties() {
        return XPointerElementHandler.RECOGNIZED_PROPERTIES;
    }
    
    @Override
    public void setProperty(final String propertyId, final Object value) throws XMLConfigurationException {
        if (propertyId.equals("http://apache.org/xml/properties/internal/error-reporter")) {
            this.setErrorReporter((XMLErrorReporter)value);
        }
        if (propertyId.equals("http://apache.org/xml/properties/internal/grammar-pool")) {
            this.fGrammarPool = (XMLGrammarPool)value;
        }
        if (propertyId.equals("http://apache.org/xml/properties/internal/entity-resolver")) {
            this.fEntityResolver = (XMLEntityResolver)value;
        }
    }
    
    @Override
    public Boolean getFeatureDefault(final String featureId) {
        for (int i = 0; i < XPointerElementHandler.RECOGNIZED_FEATURES.length; ++i) {
            if (XPointerElementHandler.RECOGNIZED_FEATURES[i].equals(featureId)) {
                return XPointerElementHandler.FEATURE_DEFAULTS[i];
            }
        }
        return null;
    }
    
    @Override
    public Object getPropertyDefault(final String propertyId) {
        for (int i = 0; i < XPointerElementHandler.RECOGNIZED_PROPERTIES.length; ++i) {
            if (XPointerElementHandler.RECOGNIZED_PROPERTIES[i].equals(propertyId)) {
                return XPointerElementHandler.PROPERTY_DEFAULTS[i];
            }
        }
        return null;
    }
    
    private void setErrorReporter(final XMLErrorReporter reporter) {
        this.fErrorReporter = reporter;
        if (this.fErrorReporter != null) {
            this.fErrorReporter.putMessageFormatter("http://www.w3.org/TR/xinclude", new XIncludeMessageFormatter());
        }
    }
    
    @Override
    public void setDocumentHandler(final XMLDocumentHandler handler) {
        this.fDocumentHandler = handler;
    }
    
    @Override
    public XMLDocumentHandler getDocumentHandler() {
        return this.fDocumentHandler;
    }
    
    @Override
    public void setXPointerSchemaName(final String schemaName) {
        this.fSchemaName = schemaName;
    }
    
    @Override
    public String getXpointerSchemaName() {
        return this.fSchemaName;
    }
    
    @Override
    public void setParent(final Object parent) {
        this.fParentXIncludeHandler = (XIncludeHandler)parent;
    }
    
    @Override
    public Object getParent() {
        return this.fParentXIncludeHandler;
    }
    
    @Override
    public void setXPointerSchemaPointer(final String content) {
        this.fSchemaPointer = content;
    }
    
    @Override
    public String getXPointerSchemaPointer() {
        return this.fSchemaPointer;
    }
    
    @Override
    public boolean isSubResourceIndentified() {
        return this.fSubResourceIdentified;
    }
    
    public void getTokens() {
        this.fSchemaPointer = this.fSchemaPointer.substring(this.fSchemaPointer.indexOf("(") + 1, this.fSchemaPointer.length());
        final StringTokenizer st = new StringTokenizer(this.fSchemaPointer, "/");
        Integer integerToken = null;
        final Stack tempPointerToken = new Stack();
        if (this.fPointerToken == null) {
            this.fPointerToken = new Stack();
        }
        while (st.hasMoreTokens()) {
            final String tempToken = st.nextToken();
            try {
                integerToken = Integer.valueOf(tempToken);
                tempPointerToken.push(integerToken);
            }
            catch (final NumberFormatException e) {
                tempPointerToken.push(tempToken);
            }
        }
        while (!tempPointerToken.empty()) {
            this.fPointerToken.push(tempPointerToken.pop());
        }
    }
    
    public boolean hasMoreToken() {
        return !this.fPointerToken.isEmpty();
    }
    
    public boolean getNextToken() {
        if (!this.fPointerToken.isEmpty()) {
            final Object currentToken = this.fPointerToken.pop();
            if (currentToken instanceof Integer) {
                this.fCurrentTokenint = (int)currentToken;
                this.fCurrentTokenType = 1;
            }
            else {
                this.fCurrentTokenString = ((String)currentToken).toString();
                this.fCurrentTokenType = 2;
            }
            return true;
        }
        return false;
    }
    
    private boolean isIdAttribute(final XMLAttributes attributes, final Augmentations augs, final int index) {
        final Object o = augs.getItem("ID_ATTRIBUTE");
        if (o instanceof Boolean) {
            return (boolean)o;
        }
        return "ID".equals(attributes.getType(index));
    }
    
    public boolean checkStringToken(final QName element, final XMLAttributes attributes) {
        final QName cacheQName = null;
        final String id = null;
        final String rawname = null;
        final QName attrName = new QName();
        String attrType = null;
        String attrValue = null;
        final int attrCount = attributes.getLength();
        int i = 0;
        while (i < attrCount) {
            final Augmentations aaugs = attributes.getAugmentations(i);
            attributes.getName(i, attrName);
            attrType = attributes.getType(i);
            attrValue = attributes.getValue(i);
            if (attrType != null && attrValue != null && this.isIdAttribute(attributes, aaugs, i) && attrValue.equals(this.fCurrentTokenString)) {
                if (this.hasMoreToken()) {
                    this.fCurrentTokenType = 0;
                    this.fCurrentTokenString = null;
                    return true;
                }
                this.foundElement = element;
                this.includeElement = true;
                this.fCurrentTokenType = 0;
                this.fCurrentTokenString = null;
                return this.fSubResourceIdentified = true;
            }
            else {
                ++i;
            }
        }
        return false;
    }
    
    public boolean checkIntegerToken(final QName element) {
        if (this.skip) {
            return false;
        }
        ++this.fElementCount;
        if (this.fCurrentTokenint != this.fElementCount) {
            this.addQName(element);
            this.skip = true;
            return false;
        }
        if (this.hasMoreToken()) {
            this.fElementCount = 0;
            this.fCurrentTokenType = 0;
            return true;
        }
        this.foundElement = element;
        this.includeElement = true;
        this.fCurrentTokenType = 0;
        this.fElementCount = 0;
        return this.fSubResourceIdentified = true;
    }
    
    public void addQName(final QName element) {
        final QName cacheQName = new QName(element);
        this.ftempCurrentElement.push(cacheQName);
    }
    
    @Override
    public void startDocument(final XMLLocator locator, final String encoding, final NamespaceContext namespaceContext, final Augmentations augs) throws XNIException {
        this.getTokens();
    }
    
    @Override
    public void doctypeDecl(final String rootElement, final String publicId, final String systemId, final Augmentations augs) throws XNIException {
    }
    
    @Override
    public void xmlDecl(final String version, final String encoding, final String standalone, final Augmentations augs) throws XNIException {
    }
    
    @Override
    public void comment(final XMLString text, final Augmentations augs) throws XNIException {
        if (this.fDocumentHandler != null && this.includeElement) {
            this.fDocumentHandler.comment(text, augs);
        }
    }
    
    @Override
    public void processingInstruction(final String target, final XMLString data, final Augmentations augs) throws XNIException {
        if (this.fDocumentHandler != null && this.includeElement) {
            this.fDocumentHandler.processingInstruction(target, data, augs);
        }
    }
    
    @Override
    public void startElement(final QName element, final XMLAttributes attributes, final Augmentations augs) throws XNIException {
        boolean requiredToken = false;
        if (this.fCurrentTokenType == 0) {
            this.getNextToken();
        }
        if (this.fCurrentTokenType == 1) {
            requiredToken = this.checkIntegerToken(element);
        }
        else if (this.fCurrentTokenType == 2) {
            requiredToken = this.checkStringToken(element, attributes);
        }
        if (requiredToken && this.hasMoreToken()) {
            this.getNextToken();
        }
        if (this.fDocumentHandler != null && this.includeElement) {
            ++this.elemCount;
            this.fDocumentHandler.startElement(element, attributes, augs);
        }
    }
    
    @Override
    public void endElement(final QName element, final Augmentations augs) throws XNIException {
        if (this.includeElement && this.foundElement != null) {
            if (this.elemCount > 0) {
                --this.elemCount;
            }
            this.fDocumentHandler.endElement(element, augs);
            if (this.elemCount == 0) {
                this.includeElement = false;
            }
        }
        else if (!this.ftempCurrentElement.empty()) {
            final QName name = this.ftempCurrentElement.peek();
            if (name.equals(element)) {
                this.ftempCurrentElement.pop();
                this.skip = false;
            }
        }
    }
    
    @Override
    public void emptyElement(final QName element, final XMLAttributes attributes, final Augmentations augs) throws XNIException {
        if (this.fDocumentHandler != null && this.includeElement) {
            this.fDocumentHandler.emptyElement(element, attributes, augs);
        }
    }
    
    @Override
    public void startGeneralEntity(final String name, final XMLResourceIdentifier resId, final String encoding, final Augmentations augs) throws XNIException {
        if (this.fDocumentHandler != null && this.includeElement) {
            this.fDocumentHandler.startGeneralEntity(name, resId, encoding, augs);
        }
    }
    
    @Override
    public void textDecl(final String version, final String encoding, final Augmentations augs) throws XNIException {
        if (this.fDocumentHandler != null && this.includeElement) {
            this.fDocumentHandler.textDecl(version, encoding, augs);
        }
    }
    
    @Override
    public void endGeneralEntity(final String name, final Augmentations augs) throws XNIException {
        if (this.fDocumentHandler != null) {
            this.fDocumentHandler.endGeneralEntity(name, augs);
        }
    }
    
    @Override
    public void characters(final XMLString text, final Augmentations augs) throws XNIException {
        if (this.fDocumentHandler != null && this.includeElement) {
            this.fDocumentHandler.characters(text, augs);
        }
    }
    
    @Override
    public void ignorableWhitespace(final XMLString text, final Augmentations augs) throws XNIException {
        if (this.fDocumentHandler != null && this.includeElement) {
            this.fDocumentHandler.ignorableWhitespace(text, augs);
        }
    }
    
    @Override
    public void startCDATA(final Augmentations augs) throws XNIException {
        if (this.fDocumentHandler != null && this.includeElement) {
            this.fDocumentHandler.startCDATA(augs);
        }
    }
    
    @Override
    public void endCDATA(final Augmentations augs) throws XNIException {
        if (this.fDocumentHandler != null && this.includeElement) {
            this.fDocumentHandler.endCDATA(augs);
        }
    }
    
    @Override
    public void endDocument(final Augmentations augs) throws XNIException {
    }
    
    @Override
    public void setDocumentSource(final XMLDocumentSource source) {
        this.fDocumentSource = source;
    }
    
    @Override
    public XMLDocumentSource getDocumentSource() {
        return this.fDocumentSource;
    }
    
    protected void reportFatalError(final String key) {
        this.reportFatalError(key, null);
    }
    
    protected void reportFatalError(final String key, final Object[] args) {
        if (this.fErrorReporter != null) {
            this.fErrorReporter.reportError(this.fDocLocation, "http://www.w3.org/TR/xinclude", key, args, (short)2);
        }
    }
    
    protected boolean isRootDocument() {
        return this.fParentXIncludeHandler == null;
    }
    
    static {
        RECOGNIZED_FEATURES = new String[0];
        FEATURE_DEFAULTS = new Boolean[0];
        RECOGNIZED_PROPERTIES = new String[] { "http://apache.org/xml/properties/internal/error-reporter", "http://apache.org/xml/properties/internal/grammar-pool", "http://apache.org/xml/properties/internal/entity-resolver", "http://apache.org/xml/properties/xpointer-schema" };
        PROPERTY_DEFAULTS = new Object[] { null, null, null, null };
    }
}
