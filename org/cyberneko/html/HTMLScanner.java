package org.cyberneko.html;

import java.io.FilterInputStream;
import org.apache.xerces.xni.NamespaceContext;
import org.cyberneko.html.xercesbridge.XercesBridge;
import org.apache.xerces.util.NamespaceSupport;
import org.apache.xerces.util.XMLAttributesImpl;
import org.apache.xerces.xni.QName;
import java.util.HashMap;
import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.XMLResourceIdentifier;
import org.apache.xerces.xni.XMLString;
import java.io.EOFException;
import java.io.File;
import org.apache.xerces.util.URI;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xni.XNIException;
import java.io.InputStream;
import org.apache.xerces.util.EncodingMap;
import java.util.Locale;
import java.net.URL;
import org.apache.xerces.xni.parser.XMLConfigurationException;
import org.apache.xerces.xni.parser.XMLComponentManager;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.InputStreamReader;
import java.io.Reader;
import org.apache.xerces.xni.parser.XMLInputSource;
import org.apache.xerces.util.XMLResourceIdentifierImpl;
import org.apache.xerces.util.XMLStringBuffer;
import org.apache.xerces.xni.XMLDocumentHandler;
import java.util.Stack;
import java.util.BitSet;
import java.util.Map;
import org.apache.xerces.xni.XMLLocator;
import org.apache.xerces.xni.parser.XMLDocumentScanner;

public class HTMLScanner implements XMLDocumentScanner, XMLLocator, HTMLComponent
{
    public static final String HTML_4_01_STRICT_PUBID = "-//W3C//DTD HTML 4.01//EN";
    public static final String HTML_4_01_STRICT_SYSID = "http://www.w3.org/TR/html4/strict.dtd";
    public static final String HTML_4_01_TRANSITIONAL_PUBID = "-//W3C//DTD HTML 4.01 Transitional//EN";
    public static final String HTML_4_01_TRANSITIONAL_SYSID = "http://www.w3.org/TR/html4/loose.dtd";
    public static final String HTML_4_01_FRAMESET_PUBID = "-//W3C//DTD HTML 4.01 Frameset//EN";
    public static final String HTML_4_01_FRAMESET_SYSID = "http://www.w3.org/TR/html4/frameset.dtd";
    public static final String HTML_LEGACY_SYSID = "about:legacy-compat";
    public static final Map<String, String> HTML_4_01_PUB_VS_SYS_ID_MAP;
    protected static final String AUGMENTATIONS = "http://cyberneko.org/html/features/augmentations";
    protected static final String REPORT_ERRORS = "http://cyberneko.org/html/features/report-errors";
    public static final String NOTIFY_CHAR_REFS = "http://apache.org/xml/features/scanner/notify-char-refs";
    public static final String NOTIFY_XML_BUILTIN_REFS = "http://apache.org/xml/features/scanner/notify-builtin-refs";
    public static final String NOTIFY_HTML_BUILTIN_REFS = "http://cyberneko.org/html/features/scanner/notify-builtin-refs";
    public static final String FIX_MSWINDOWS_REFS = "http://cyberneko.org/html/features/scanner/fix-mswindows-refs";
    public static final String SCRIPT_STRIP_COMMENT_DELIMS = "http://cyberneko.org/html/features/scanner/script/strip-comment-delims";
    public static final String SCRIPT_STRIP_CDATA_DELIMS = "http://cyberneko.org/html/features/scanner/script/strip-cdata-delims";
    public static final String STYLE_STRIP_COMMENT_DELIMS = "http://cyberneko.org/html/features/scanner/style/strip-comment-delims";
    public static final String STYLE_STRIP_CDATA_DELIMS = "http://cyberneko.org/html/features/scanner/style/strip-cdata-delims";
    public static final String IGNORE_SPECIFIED_CHARSET = "http://cyberneko.org/html/features/scanner/ignore-specified-charset";
    public static final String CDATA_SECTIONS = "http://cyberneko.org/html/features/scanner/cdata-sections";
    public static final String OVERRIDE_DOCTYPE = "http://cyberneko.org/html/features/override-doctype";
    public static final String INSERT_DOCTYPE = "http://cyberneko.org/html/features/insert-doctype";
    public static final String PARSE_NOSCRIPT_CONTENT = "http://cyberneko.org/html/features/parse-noscript-content";
    public static final String ALLOW_SELFCLOSING_IFRAME = "http://cyberneko.org/html/features/scanner/allow-selfclosing-iframe";
    public static final String ALLOW_SELFCLOSING_TAGS = "http://cyberneko.org/html/features/scanner/allow-selfclosing-tags";
    protected static final String NORMALIZE_ATTRIBUTES = "http://cyberneko.org/html/features/scanner/normalize-attrs";
    private static final String[] RECOGNIZED_FEATURES;
    private static final Boolean[] RECOGNIZED_FEATURES_DEFAULTS;
    protected static final String NAMES_ELEMS = "http://cyberneko.org/html/properties/names/elems";
    protected static final String NAMES_ATTRS = "http://cyberneko.org/html/properties/names/attrs";
    protected static final String DEFAULT_ENCODING = "http://cyberneko.org/html/properties/default-encoding";
    protected static final String ERROR_REPORTER = "http://cyberneko.org/html/properties/error-reporter";
    protected static final String DOCTYPE_PUBID = "http://cyberneko.org/html/properties/doctype/pubid";
    protected static final String DOCTYPE_SYSID = "http://cyberneko.org/html/properties/doctype/sysid";
    protected static final String ATTRIBUTE_LIMIT = "http://cyberneko.org/html/zoho/features/attribute-limit";
    private static final String[] RECOGNIZED_PROPERTIES;
    private static final Object[] RECOGNIZED_PROPERTIES_DEFAULTS;
    protected static final short STATE_CONTENT = 0;
    protected static final short STATE_MARKUP_BRACKET = 1;
    protected static final short STATE_START_DOCUMENT = 10;
    protected static final short STATE_END_DOCUMENT = 11;
    protected static final short NAMES_NO_CHANGE = 0;
    protected static final short NAMES_UPPERCASE = 1;
    protected static final short NAMES_LOWERCASE = 2;
    protected static final int DEFAULT_BUFFER_SIZE = 2048;
    private static final boolean DEBUG_SCANNER = false;
    private static final boolean DEBUG_SCANNER_STATE = false;
    private static final boolean DEBUG_BUFFER = false;
    private static final boolean DEBUG_CHARSET = false;
    protected static final boolean DEBUG_CALLBACKS = false;
    protected static final HTMLEventInfo SYNTHESIZED_ITEM;
    private static final BitSet ENTITY_CHARS;
    protected boolean fAugmentations;
    protected boolean fReportErrors;
    protected boolean fNotifyCharRefs;
    protected boolean fNotifyXmlBuiltinRefs;
    protected boolean fNotifyHtmlBuiltinRefs;
    protected boolean fFixWindowsCharRefs;
    protected boolean fScriptStripCDATADelims;
    protected boolean fScriptStripCommentDelims;
    protected boolean fStyleStripCDATADelims;
    protected boolean fStyleStripCommentDelims;
    protected boolean fIgnoreSpecifiedCharset;
    protected boolean fCDATASections;
    protected boolean fOverrideDoctype;
    protected boolean fInsertDoctype;
    protected boolean fNormalizeAttributes;
    protected boolean fParseNoScriptContent;
    protected boolean fParseNoFramesContent;
    protected boolean fAllowSelfclosingIframe;
    protected boolean fAllowSelfclosingTags;
    protected short fNamesElems;
    protected short fNamesAttrs;
    protected String fDefaultIANAEncoding;
    protected HTMLErrorReporter fErrorReporter;
    protected String fDoctypePubid;
    protected String fDoctypeSysid;
    protected int fBeginLineNumber;
    protected int fBeginColumnNumber;
    protected int fBeginCharacterOffset;
    protected int fEndLineNumber;
    protected int fEndColumnNumber;
    protected int fEndCharacterOffset;
    protected PlaybackInputStream fByteStream;
    protected CurrentEntity fCurrentEntity;
    protected final Stack fCurrentEntityStack;
    protected Scanner fScanner;
    protected short fScannerState;
    protected XMLDocumentHandler fDocumentHandler;
    protected String fIANAEncoding;
    protected String fJavaEncoding;
    protected boolean fIso8859Encoding;
    protected int fElementCount;
    protected int fElementDepth;
    private int fAttributeLimit;
    protected Scanner fContentScanner;
    protected SpecialScanner fSpecialScanner;
    protected final XMLStringBuffer fStringBuffer;
    private final XMLStringBuffer fStringBuffer2;
    private final XMLStringBuffer fNonNormAttr;
    private final HTMLAugmentations fInfosetAugs;
    private final LocationItem fLocationItem;
    private final boolean[] fSingleBoolean;
    private final XMLResourceIdentifierImpl fResourceId;
    private final char REPLACEMENT_CHARACTER = '\ufffd';
    private boolean skipDecode;
    
    public HTMLScanner() {
        this.fCurrentEntityStack = new Stack();
        this.fAttributeLimit = -1;
        this.fContentScanner = new ContentScanner();
        this.fSpecialScanner = new SpecialScanner();
        this.fStringBuffer = new XMLStringBuffer(1024);
        this.fStringBuffer2 = new XMLStringBuffer(1024);
        this.fNonNormAttr = new XMLStringBuffer(128);
        this.fInfosetAugs = new HTMLAugmentations();
        this.fLocationItem = new LocationItem();
        this.fSingleBoolean = new boolean[] { false };
        this.fResourceId = new XMLResourceIdentifierImpl();
        this.skipDecode = false;
    }
    
    public void pushInputSource(final XMLInputSource inputSource) {
        final Reader reader = this.getReader(inputSource);
        this.fCurrentEntityStack.push(this.fCurrentEntity);
        final String encoding = inputSource.getEncoding();
        final String publicId = inputSource.getPublicId();
        final String baseSystemId = inputSource.getBaseSystemId();
        final String literalSystemId = inputSource.getSystemId();
        final String expandedSystemId = expandSystemId(literalSystemId, baseSystemId);
        this.fCurrentEntity = new CurrentEntity(reader, encoding, publicId, baseSystemId, literalSystemId, expandedSystemId);
    }
    
    private Reader getReader(final XMLInputSource inputSource) {
        final Reader reader = inputSource.getCharacterStream();
        if (reader == null) {
            try {
                return new InputStreamReader(inputSource.getByteStream(), this.fJavaEncoding);
            }
            catch (final UnsupportedEncodingException ex) {}
        }
        return reader;
    }
    
    public void evaluateInputSource(final XMLInputSource inputSource) {
        final Scanner previousScanner = this.fScanner;
        final short previousScannerState = this.fScannerState;
        final CurrentEntity previousEntity = this.fCurrentEntity;
        final Reader reader = this.getReader(inputSource);
        final String encoding = inputSource.getEncoding();
        final String publicId = inputSource.getPublicId();
        final String baseSystemId = inputSource.getBaseSystemId();
        final String literalSystemId = inputSource.getSystemId();
        final String expandedSystemId = expandSystemId(literalSystemId, baseSystemId);
        this.fCurrentEntity = new CurrentEntity(reader, encoding, publicId, baseSystemId, literalSystemId, expandedSystemId);
        this.setScanner(this.fContentScanner);
        this.setScannerState((short)0);
        try {
            do {
                this.fScanner.scan(false);
            } while (this.fScannerState != 11);
        }
        catch (final IOException ex) {}
        this.setScanner(previousScanner);
        this.setScannerState(previousScannerState);
        this.fCurrentEntity = previousEntity;
    }
    
    public void cleanup(final boolean closeall) {
        final int size = this.fCurrentEntityStack.size();
        if (size > 0) {
            if (this.fCurrentEntity != null) {
                this.fCurrentEntity.closeQuietly();
            }
            for (int i = closeall ? 0 : 1; i < size; ++i) {
                (this.fCurrentEntity = this.fCurrentEntityStack.pop()).closeQuietly();
            }
        }
        else if (closeall && this.fCurrentEntity != null) {
            this.fCurrentEntity.closeQuietly();
        }
    }
    
    public String getEncoding() {
        return (this.fCurrentEntity != null) ? this.fCurrentEntity.encoding : null;
    }
    
    public String getPublicId() {
        return (this.fCurrentEntity != null) ? this.fCurrentEntity.publicId : null;
    }
    
    public String getBaseSystemId() {
        return (this.fCurrentEntity != null) ? this.fCurrentEntity.baseSystemId : null;
    }
    
    public String getLiteralSystemId() {
        return (this.fCurrentEntity != null) ? this.fCurrentEntity.literalSystemId : null;
    }
    
    public String getExpandedSystemId() {
        return (this.fCurrentEntity != null) ? this.fCurrentEntity.expandedSystemId : null;
    }
    
    public int getLineNumber() {
        return (this.fCurrentEntity != null) ? this.fCurrentEntity.getLineNumber() : -1;
    }
    
    public int getColumnNumber() {
        return (this.fCurrentEntity != null) ? this.fCurrentEntity.getColumnNumber() : -1;
    }
    
    public String getXMLVersion() {
        String s;
        if (this.fCurrentEntity != null) {
            this.fCurrentEntity.getClass();
            s = "1.0";
        }
        else {
            s = null;
        }
        return s;
    }
    
    public int getCharacterOffset() {
        return (this.fCurrentEntity != null) ? this.fCurrentEntity.getCharacterOffset() : -1;
    }
    
    public Boolean getFeatureDefault(final String featureId) {
        for (int length = (HTMLScanner.RECOGNIZED_FEATURES != null) ? HTMLScanner.RECOGNIZED_FEATURES.length : 0, i = 0; i < length; ++i) {
            if (HTMLScanner.RECOGNIZED_FEATURES[i].equals(featureId)) {
                return HTMLScanner.RECOGNIZED_FEATURES_DEFAULTS[i];
            }
        }
        return null;
    }
    
    public Object getPropertyDefault(final String propertyId) {
        for (int length = (HTMLScanner.RECOGNIZED_PROPERTIES != null) ? HTMLScanner.RECOGNIZED_PROPERTIES.length : 0, i = 0; i < length; ++i) {
            if (HTMLScanner.RECOGNIZED_PROPERTIES[i].equals(propertyId)) {
                return HTMLScanner.RECOGNIZED_PROPERTIES_DEFAULTS[i];
            }
        }
        return null;
    }
    
    public String[] getRecognizedFeatures() {
        return HTMLScanner.RECOGNIZED_FEATURES;
    }
    
    public String[] getRecognizedProperties() {
        return HTMLScanner.RECOGNIZED_PROPERTIES;
    }
    
    public void reset(final XMLComponentManager manager) throws XMLConfigurationException {
        this.fAugmentations = manager.getFeature("http://cyberneko.org/html/features/augmentations");
        this.fReportErrors = manager.getFeature("http://cyberneko.org/html/features/report-errors");
        this.fNotifyCharRefs = manager.getFeature("http://apache.org/xml/features/scanner/notify-char-refs");
        this.fNotifyXmlBuiltinRefs = manager.getFeature("http://apache.org/xml/features/scanner/notify-builtin-refs");
        this.fNotifyHtmlBuiltinRefs = manager.getFeature("http://cyberneko.org/html/features/scanner/notify-builtin-refs");
        this.fFixWindowsCharRefs = manager.getFeature("http://cyberneko.org/html/features/scanner/fix-mswindows-refs");
        this.fScriptStripCDATADelims = manager.getFeature("http://cyberneko.org/html/features/scanner/script/strip-cdata-delims");
        this.fScriptStripCommentDelims = manager.getFeature("http://cyberneko.org/html/features/scanner/script/strip-comment-delims");
        this.fStyleStripCDATADelims = manager.getFeature("http://cyberneko.org/html/features/scanner/style/strip-cdata-delims");
        this.fStyleStripCommentDelims = manager.getFeature("http://cyberneko.org/html/features/scanner/style/strip-comment-delims");
        this.fIgnoreSpecifiedCharset = manager.getFeature("http://cyberneko.org/html/features/scanner/ignore-specified-charset");
        this.fCDATASections = manager.getFeature("http://cyberneko.org/html/features/scanner/cdata-sections");
        this.fOverrideDoctype = manager.getFeature("http://cyberneko.org/html/features/override-doctype");
        this.fInsertDoctype = manager.getFeature("http://cyberneko.org/html/features/insert-doctype");
        this.fNormalizeAttributes = manager.getFeature("http://cyberneko.org/html/features/scanner/normalize-attrs");
        this.fParseNoScriptContent = manager.getFeature("http://cyberneko.org/html/features/parse-noscript-content");
        this.fAllowSelfclosingIframe = manager.getFeature("http://cyberneko.org/html/features/scanner/allow-selfclosing-iframe");
        this.fAllowSelfclosingTags = manager.getFeature("http://cyberneko.org/html/features/scanner/allow-selfclosing-tags");
        this.fNamesElems = getNamesValue(String.valueOf(manager.getProperty("http://cyberneko.org/html/properties/names/elems")));
        this.fNamesAttrs = getNamesValue(String.valueOf(manager.getProperty("http://cyberneko.org/html/properties/names/attrs")));
        this.fDefaultIANAEncoding = String.valueOf(manager.getProperty("http://cyberneko.org/html/properties/default-encoding"));
        this.fErrorReporter = (HTMLErrorReporter)manager.getProperty("http://cyberneko.org/html/properties/error-reporter");
        this.fDoctypePubid = String.valueOf(manager.getProperty("http://cyberneko.org/html/properties/doctype/pubid"));
        this.fDoctypeSysid = String.valueOf(manager.getProperty("http://cyberneko.org/html/properties/doctype/sysid"));
        this.fAttributeLimit = this.isValid(String.valueOf(manager.getProperty("http://cyberneko.org/html/zoho/features/attribute-limit")));
    }
    
    public void setFeature(final String featureId, final boolean state) {
        if (featureId.equals("http://cyberneko.org/html/features/augmentations")) {
            this.fAugmentations = state;
        }
        else if (featureId.equals("http://cyberneko.org/html/features/scanner/ignore-specified-charset")) {
            this.fIgnoreSpecifiedCharset = state;
        }
        else if (featureId.equals("http://apache.org/xml/features/scanner/notify-char-refs")) {
            this.fNotifyCharRefs = state;
        }
        else if (featureId.equals("http://apache.org/xml/features/scanner/notify-builtin-refs")) {
            this.fNotifyXmlBuiltinRefs = state;
        }
        else if (featureId.equals("http://cyberneko.org/html/features/scanner/notify-builtin-refs")) {
            this.fNotifyHtmlBuiltinRefs = state;
        }
        else if (featureId.equals("http://cyberneko.org/html/features/scanner/fix-mswindows-refs")) {
            this.fFixWindowsCharRefs = state;
        }
        else if (featureId.equals("http://cyberneko.org/html/features/scanner/script/strip-cdata-delims")) {
            this.fScriptStripCDATADelims = state;
        }
        else if (featureId.equals("http://cyberneko.org/html/features/scanner/script/strip-comment-delims")) {
            this.fScriptStripCommentDelims = state;
        }
        else if (featureId.equals("http://cyberneko.org/html/features/scanner/style/strip-cdata-delims")) {
            this.fStyleStripCDATADelims = state;
        }
        else if (featureId.equals("http://cyberneko.org/html/features/scanner/style/strip-comment-delims")) {
            this.fStyleStripCommentDelims = state;
        }
        else if (featureId.equals("http://cyberneko.org/html/features/parse-noscript-content")) {
            this.fParseNoScriptContent = state;
        }
        else if (featureId.equals("http://cyberneko.org/html/features/scanner/allow-selfclosing-iframe")) {
            this.fAllowSelfclosingIframe = state;
        }
        else if (featureId.equals("http://cyberneko.org/html/features/scanner/allow-selfclosing-tags")) {
            this.fAllowSelfclosingTags = state;
        }
    }
    
    public void setProperty(final String propertyId, final Object value) throws XMLConfigurationException {
        if (propertyId.equals("http://cyberneko.org/html/properties/names/elems")) {
            this.fNamesElems = getNamesValue(String.valueOf(value));
            return;
        }
        if (propertyId.equals("http://cyberneko.org/html/properties/names/attrs")) {
            this.fNamesAttrs = getNamesValue(String.valueOf(value));
            return;
        }
        if (propertyId.equals("http://cyberneko.org/html/properties/default-encoding")) {
            this.fDefaultIANAEncoding = String.valueOf(value);
            return;
        }
        if (propertyId.equals("http://cyberneko.org/html/zoho/features/attribute-limit")) {
            this.fAttributeLimit = this.isValid(String.valueOf(value));
        }
    }
    
    private int isValid(final String str) {
        int value = -1;
        try {
            value = Integer.valueOf(str);
        }
        catch (final NumberFormatException ex) {}
        return value;
    }
    
    public void setInputSource(final XMLInputSource source) throws IOException {
        this.fElementCount = 0;
        this.fElementDepth = -1;
        this.fByteStream = null;
        this.fCurrentEntityStack.removeAllElements();
        this.fBeginLineNumber = 1;
        this.fBeginColumnNumber = 1;
        this.fBeginCharacterOffset = 0;
        this.fEndLineNumber = this.fBeginLineNumber;
        this.fEndColumnNumber = this.fBeginColumnNumber;
        this.fEndCharacterOffset = this.fBeginCharacterOffset;
        this.fIANAEncoding = this.fDefaultIANAEncoding;
        this.fJavaEncoding = this.fIANAEncoding;
        String encoding = source.getEncoding();
        final String publicId = source.getPublicId();
        final String baseSystemId = source.getBaseSystemId();
        final String literalSystemId = source.getSystemId();
        final String expandedSystemId = expandSystemId(literalSystemId, baseSystemId);
        Reader reader = source.getCharacterStream();
        if (reader == null) {
            InputStream inputStream = source.getByteStream();
            if (inputStream == null) {
                final URL url = new URL(expandedSystemId);
                inputStream = url.openStream();
            }
            this.fByteStream = new PlaybackInputStream(inputStream);
            final String[] encodings = new String[2];
            if (encoding == null) {
                this.fByteStream.detectEncoding(encodings);
            }
            else {
                encodings[0] = encoding;
            }
            if (encodings[0] == null) {
                encodings[0] = this.fDefaultIANAEncoding;
                if (this.fReportErrors) {
                    this.fErrorReporter.reportWarning("HTML1000", null);
                }
            }
            if (encodings[1] == null) {
                encodings[1] = EncodingMap.getIANA2JavaMapping(encodings[0].toUpperCase(Locale.ENGLISH));
                if (encodings[1] == null) {
                    encodings[1] = encodings[0];
                    if (this.fReportErrors) {
                        this.fErrorReporter.reportWarning("HTML1001", new Object[] { encodings[0] });
                    }
                }
            }
            this.fIANAEncoding = encodings[0];
            this.fJavaEncoding = encodings[1];
            this.fIso8859Encoding = (this.fIANAEncoding == null || this.fIANAEncoding.toUpperCase(Locale.ENGLISH).startsWith("ISO-8859") || this.fIANAEncoding.equalsIgnoreCase(this.fDefaultIANAEncoding));
            encoding = this.fIANAEncoding;
            reader = new InputStreamReader(this.fByteStream, this.fJavaEncoding);
        }
        this.fCurrentEntity = new CurrentEntity(reader, encoding, publicId, baseSystemId, literalSystemId, expandedSystemId);
        this.setScanner(this.fContentScanner);
        this.setScannerState((short)10);
    }
    
    public boolean scanDocument(final boolean complete) throws XNIException, IOException {
        while (this.fScanner.scan(complete)) {
            if (!complete) {
                return true;
            }
        }
        return false;
    }
    
    public void setDocumentHandler(final XMLDocumentHandler handler) {
        this.fDocumentHandler = handler;
    }
    
    public XMLDocumentHandler getDocumentHandler() {
        return this.fDocumentHandler;
    }
    
    protected static String getValue(final XMLAttributes attrs, final String aname) {
        for (int length = (attrs != null) ? attrs.getLength() : 0, i = 0; i < length; ++i) {
            if (attrs.getQName(i).equalsIgnoreCase(aname)) {
                return attrs.getValue(i);
            }
        }
        return null;
    }
    
    public static String expandSystemId(final String systemId, final String baseSystemId) {
        if (systemId == null || systemId.length() == 0) {
            return systemId;
        }
        try {
            final URI uri = new URI(systemId);
            if (uri != null) {
                return systemId;
            }
        }
        catch (final URI.MalformedURIException ex) {}
        final String id = fixURI(systemId);
        URI base = null;
        URI uri2 = null;
        try {
            Label_0270: {
                if (baseSystemId != null && baseSystemId.length() != 0) {
                    if (!baseSystemId.equals(systemId)) {
                        try {
                            base = new URI(fixURI(baseSystemId));
                        }
                        catch (final URI.MalformedURIException e) {
                            String dir;
                            try {
                                dir = fixURI(System.getProperty("user.dir"));
                            }
                            catch (final SecurityException se) {
                                dir = "";
                            }
                            if (baseSystemId.indexOf(58) != -1) {
                                base = new URI("file", "", fixURI(baseSystemId), (String)null, (String)null);
                            }
                            else {
                                if (!dir.endsWith("/")) {
                                    dir += "/";
                                }
                                dir += fixURI(baseSystemId);
                                base = new URI("file", "", dir, (String)null, (String)null);
                            }
                        }
                        break Label_0270;
                    }
                }
                String dir2;
                try {
                    dir2 = fixURI(System.getProperty("user.dir"));
                }
                catch (final SecurityException se2) {
                    dir2 = "";
                }
                if (!dir2.endsWith("/")) {
                    dir2 += "/";
                }
                base = new URI("file", "", dir2, (String)null, (String)null);
            }
            uri2 = new URI(base, id);
        }
        catch (final URI.MalformedURIException ex2) {}
        if (uri2 == null) {
            return systemId;
        }
        return uri2.toString();
    }
    
    protected static String fixURI(String str) {
        str = str.replace(File.separatorChar, '/');
        if (str.length() >= 2) {
            final char ch1 = str.charAt(1);
            if (ch1 == ':') {
                final char ch2 = String.valueOf(str.charAt(0)).toUpperCase(Locale.ENGLISH).charAt(0);
                if (ch2 >= 'A' && ch2 <= 'Z') {
                    str = "/" + str;
                }
            }
            else if (ch1 == '/' && str.charAt(0) == '/') {
                str = "file:" + str;
            }
        }
        return str;
    }
    
    protected static final String modifyName(final String name, final short mode) {
        switch (mode) {
            case 1: {
                return name.toUpperCase(Locale.ENGLISH);
            }
            case 2: {
                return name.toLowerCase(Locale.ENGLISH);
            }
            default: {
                return name;
            }
        }
    }
    
    protected static final short getNamesValue(final String value) {
        if (value.equals("lower")) {
            return 2;
        }
        if (value.equals("upper")) {
            return 1;
        }
        return 0;
    }
    
    protected int fixWindowsCharacter(final int origChar) {
        switch (origChar) {
            case 130: {
                return 8218;
            }
            case 131: {
                return 402;
            }
            case 132: {
                return 8222;
            }
            case 133: {
                return 8230;
            }
            case 134: {
                return 8224;
            }
            case 135: {
                return 8225;
            }
            case 136: {
                return 710;
            }
            case 137: {
                return 8240;
            }
            case 138: {
                return 352;
            }
            case 139: {
                return 8249;
            }
            case 140: {
                return 338;
            }
            case 145: {
                return 8216;
            }
            case 146: {
                return 8217;
            }
            case 147: {
                return 8220;
            }
            case 148: {
                return 8221;
            }
            case 149: {
                return 8226;
            }
            case 150: {
                return 8211;
            }
            case 151: {
                return 8212;
            }
            case 152: {
                return 732;
            }
            case 153: {
                return 8482;
            }
            case 154: {
                return 353;
            }
            case 155: {
                return 8250;
            }
            case 156: {
                return 339;
            }
            case 159: {
                return 376;
            }
            default: {
                return origChar;
            }
        }
    }
    
    protected int read() throws IOException {
        return this.fCurrentEntity.read();
    }
    
    protected void setScanner(final Scanner scanner) {
        this.fScanner = scanner;
    }
    
    protected void setScannerState(final short state) {
        this.fScannerState = state;
    }
    
    protected void scanDoctype() throws IOException {
        String root = null;
        String pubid = null;
        String sysid = null;
        if (this.skipSpaces()) {
            root = this.scanName(true);
            if (root == null) {
                if (this.fReportErrors) {
                    this.fErrorReporter.reportError("HTML1014", null);
                }
            }
            else {
                root = modifyName(root, this.fNamesElems);
            }
            if (this.skipSpaces()) {
                if (this.skip("PUBLIC", false)) {
                    this.skipSpaces();
                    pubid = this.scanLiteral();
                    if (this.skipSpaces()) {
                        sysid = this.scanLiteral();
                    }
                }
                else if (this.skip("SYSTEM", false)) {
                    this.skipSpaces();
                    sysid = this.scanLiteral();
                }
            }
        }
        int c;
        while ((c = this.fCurrentEntity.read()) != -1 && c != 62) {}
        if (this.fDocumentHandler != null) {
            if (this.fOverrideDoctype) {
                pubid = this.fDoctypePubid;
                sysid = this.fDoctypeSysid;
            }
            this.fEndLineNumber = this.fCurrentEntity.getLineNumber();
            this.fEndColumnNumber = this.fCurrentEntity.getColumnNumber();
            this.fEndCharacterOffset = this.fCurrentEntity.getCharacterOffset();
            this.fDocumentHandler.doctypeDecl(root, pubid, sysid, this.locationAugs());
        }
    }
    
    protected String scanLiteral() throws IOException {
        final int quote = this.fCurrentEntity.read();
        if (quote != 39 && quote != 34) {
            this.fCurrentEntity.rewind();
            return null;
        }
        final StringBuffer str = new StringBuffer();
        int c;
        while ((c = this.fCurrentEntity.read()) != -1) {
            if (c == quote) {
                break;
            }
            if (c == 13 || c == 10) {
                this.fCurrentEntity.rewind();
                this.skipNewlines();
                str.append(' ');
            }
            else {
                if (c == 62) {
                    this.fCurrentEntity.rewind();
                    break;
                }
                this.appendChar(str, c);
            }
        }
        if (c == -1) {
            if (this.fReportErrors) {
                this.fErrorReporter.reportError("HTML1007", null);
            }
            throw new EOFException();
        }
        return str.toString();
    }
    
    protected String scanName(final boolean strict) throws IOException {
        if (this.fCurrentEntity.offset == this.fCurrentEntity.length && this.fCurrentEntity.load(0) == -1) {
            return null;
        }
        int offset = this.fCurrentEntity.offset;
        while (true) {
            if (this.fCurrentEntity.hasNext()) {
                final char c = this.fCurrentEntity.getNextChar();
                if ((strict || Character.isLetterOrDigit(c) || c == '-' || c == '.' || c == ':' || c == '_') && (strict || (!Character.isWhitespace(c) && c != '=' && c != '/' && c != '>'))) {
                    continue;
                }
                this.fCurrentEntity.rewind();
            }
            if (this.fCurrentEntity.offset != this.fCurrentEntity.length) {
                break;
            }
            final int length = this.fCurrentEntity.length - offset;
            System.arraycopy(this.fCurrentEntity.buffer, offset, this.fCurrentEntity.buffer, 0, length);
            final int count = this.fCurrentEntity.load(length);
            offset = 0;
            if (count == -1) {
                break;
            }
        }
        final int length = this.fCurrentEntity.offset - offset;
        final String name = (length > 0) ? new String(this.fCurrentEntity.buffer, offset, length) : null;
        return name;
    }
    
    void setSkipDecode(final boolean skip) {
        this.skipDecode = skip;
    }
    
    protected int scanEntityRef(final XMLStringBuffer str, final boolean content) throws IOException {
        str.clear();
        str.append('&');
        boolean endsWithSemicolon = false;
        while (true) {
            final int c = this.fCurrentEntity.read();
            if (c == 59) {
                str.append(';');
                endsWithSemicolon = true;
                break;
            }
            if (c == -1) {
                break;
            }
            if (!HTMLScanner.ENTITY_CHARS.get(c) && c != 35) {
                this.fCurrentEntity.rewind();
                break;
            }
            this.appendChar(str, c);
        }
        if (!endsWithSemicolon && this.fReportErrors) {
            this.fErrorReporter.reportWarning("HTML1004", null);
        }
        if (str.length == 1) {
            if (content && this.fDocumentHandler != null && this.fElementCount >= this.fElementDepth) {
                this.fEndLineNumber = this.fCurrentEntity.getLineNumber();
                this.fEndColumnNumber = this.fCurrentEntity.getColumnNumber();
                this.fEndCharacterOffset = this.fCurrentEntity.getCharacterOffset();
                this.fDocumentHandler.characters((XMLString)str, this.locationAugs());
            }
            return -1;
        }
        String name;
        if (endsWithSemicolon) {
            name = str.toString().substring(1, str.length - 1);
        }
        else {
            name = str.toString().substring(1);
        }
        if (name.startsWith("#")) {
            int value = -1;
            try {
                if (name.startsWith("#x") || name.startsWith("#X")) {
                    value = Integer.parseInt(name.substring(2), 16);
                }
                else {
                    value = Integer.parseInt(name.substring(1));
                }
                if (this.fFixWindowsCharRefs && this.fIso8859Encoding) {
                    value = this.fixWindowsCharacter(value);
                }
                if (content && this.fDocumentHandler != null && this.fElementCount >= this.fElementDepth) {
                    this.fEndLineNumber = this.fCurrentEntity.getLineNumber();
                    this.fEndColumnNumber = this.fCurrentEntity.getColumnNumber();
                    this.fEndCharacterOffset = this.fCurrentEntity.getCharacterOffset();
                    if (this.fNotifyCharRefs) {
                        final XMLResourceIdentifier id = this.resourceId();
                        final String encoding = null;
                        this.fDocumentHandler.startGeneralEntity(name, id, encoding, this.locationAugs());
                    }
                    if (!this.skipDecode) {
                        str.clear();
                        try {
                            this.appendChar(str, value);
                        }
                        catch (final IllegalArgumentException e) {
                            if (this.fReportErrors) {
                                this.fErrorReporter.reportError("HTML1005", new Object[] { name });
                            }
                            str.append('\ufffd');
                        }
                    }
                    this.fDocumentHandler.characters((XMLString)str, this.locationAugs());
                    if (this.fNotifyCharRefs) {
                        this.fDocumentHandler.endGeneralEntity(name, this.locationAugs());
                    }
                }
            }
            catch (final NumberFormatException e2) {
                if (this.fReportErrors) {
                    value = -2;
                    this.fErrorReporter.reportError("HTML1005", new Object[] { name });
                }
                if (content && this.fDocumentHandler != null && this.fElementCount >= this.fElementDepth) {
                    this.fEndLineNumber = this.fCurrentEntity.getLineNumber();
                    this.fEndColumnNumber = this.fCurrentEntity.getColumnNumber();
                    this.fEndCharacterOffset = this.fCurrentEntity.getCharacterOffset();
                    this.fDocumentHandler.characters((XMLString)str, this.locationAugs());
                }
            }
            return value;
        }
        final int c2 = this.skipDecode ? -1 : HTMLEntities.get(name);
        final boolean invalidEntityInAttribute = !content && !endsWithSemicolon && c2 > 256;
        if (c2 == -1 || invalidEntityInAttribute) {
            if (this.fReportErrors) {
                this.fErrorReporter.reportWarning("HTML1006", new Object[] { name });
            }
            if (content && this.fDocumentHandler != null && this.fElementCount >= this.fElementDepth) {
                this.fEndLineNumber = this.fCurrentEntity.getLineNumber();
                this.fEndColumnNumber = this.fCurrentEntity.getColumnNumber();
                this.fEndCharacterOffset = this.fCurrentEntity.getCharacterOffset();
                this.fDocumentHandler.characters((XMLString)str, this.locationAugs());
            }
            return -1;
        }
        if (content && this.fDocumentHandler != null && this.fElementCount >= this.fElementDepth) {
            this.fEndLineNumber = this.fCurrentEntity.getLineNumber();
            this.fEndColumnNumber = this.fCurrentEntity.getColumnNumber();
            this.fEndCharacterOffset = this.fCurrentEntity.getCharacterOffset();
            final boolean notify = this.fNotifyHtmlBuiltinRefs || (this.fNotifyXmlBuiltinRefs && builtinXmlRef(name));
            if (notify) {
                final XMLResourceIdentifier id2 = this.resourceId();
                final String encoding2 = null;
                this.fDocumentHandler.startGeneralEntity(name, id2, encoding2, this.locationAugs());
            }
            str.clear();
            this.appendChar(str, c2);
            this.fDocumentHandler.characters((XMLString)str, this.locationAugs());
            if (notify) {
                this.fDocumentHandler.endGeneralEntity(name, this.locationAugs());
            }
        }
        return c2;
    }
    
    protected boolean skip(final String s, final boolean caseSensitive) throws IOException {
        for (int length = (s != null) ? s.length() : 0, i = 0; i < length; ++i) {
            if (this.fCurrentEntity.offset == this.fCurrentEntity.length) {
                System.arraycopy(this.fCurrentEntity.buffer, this.fCurrentEntity.offset - i, this.fCurrentEntity.buffer, 0, i);
                if (this.fCurrentEntity.load(i) == -1) {
                    this.fCurrentEntity.offset = 0;
                    return false;
                }
            }
            char c0 = s.charAt(i);
            char c2 = this.fCurrentEntity.getNextChar();
            if (!caseSensitive) {
                c0 = String.valueOf(c0).toUpperCase(Locale.ENGLISH).charAt(0);
                c2 = String.valueOf(c2).toUpperCase(Locale.ENGLISH).charAt(0);
            }
            if (c0 != c2) {
                this.fCurrentEntity.rewind(i + 1);
                return false;
            }
        }
        return true;
    }
    
    protected boolean skipMarkup(final boolean balance) throws IOException {
        int depth = 1;
        boolean slashgt = false;
        while (this.fCurrentEntity.offset != this.fCurrentEntity.length || this.fCurrentEntity.load(0) != -1) {
            Label_0195: {
                while (this.fCurrentEntity.hasNext()) {
                    char c = this.fCurrentEntity.getNextChar();
                    if (balance && c == '<') {
                        ++depth;
                    }
                    else if (c == '>') {
                        if (--depth == 0) {
                            break Label_0195;
                        }
                        continue;
                    }
                    else if (c == '/') {
                        if (this.fCurrentEntity.offset == this.fCurrentEntity.length && this.fCurrentEntity.load(0) == -1) {
                            break Label_0195;
                        }
                        c = this.fCurrentEntity.getNextChar();
                        if (c == '>') {
                            slashgt = true;
                            if (--depth == 0) {
                                break Label_0195;
                            }
                            continue;
                        }
                        else {
                            this.fCurrentEntity.rewind();
                        }
                    }
                    else {
                        if (c != '\r' && c != '\n') {
                            continue;
                        }
                        this.fCurrentEntity.rewind();
                        this.skipNewlines();
                    }
                }
                continue;
            }
            return slashgt;
        }
        return slashgt;
    }
    
    protected boolean skipSpaces() throws IOException {
        boolean spaces = false;
        while (this.fCurrentEntity.offset != this.fCurrentEntity.length || this.fCurrentEntity.load(0) != -1) {
            final char c = this.fCurrentEntity.getNextChar();
            if (!Character.isWhitespace(c)) {
                this.fCurrentEntity.rewind();
                return spaces;
            }
            spaces = true;
            if (c != '\r' && c != '\n') {
                continue;
            }
            this.fCurrentEntity.rewind();
            this.skipNewlines();
        }
        return spaces;
    }
    
    protected int skipNewlines() throws IOException {
        if (!this.fCurrentEntity.hasNext() && this.fCurrentEntity.load(0) == -1) {
            return 0;
        }
        char c = this.fCurrentEntity.getCurrentChar();
        int newlines = 0;
        int offset = this.fCurrentEntity.offset;
        if (c == '\n' || c == '\r') {
            do {
                c = this.fCurrentEntity.getNextChar();
                if (c == '\r') {
                    ++newlines;
                    if (this.fCurrentEntity.offset == this.fCurrentEntity.length) {
                        offset = 0;
                        this.fCurrentEntity.offset = newlines;
                        if (this.fCurrentEntity.load(newlines) == -1) {
                            break;
                        }
                    }
                    if (this.fCurrentEntity.getCurrentChar() != '\n') {
                        continue;
                    }
                    final CurrentEntity fCurrentEntity = this.fCurrentEntity;
                    ++fCurrentEntity.offset;
                    final CurrentEntity fCurrentEntity2 = this.fCurrentEntity;
                    ++fCurrentEntity2.characterOffset_;
                    ++offset;
                }
                else {
                    if (c != '\n') {
                        this.fCurrentEntity.rewind();
                        break;
                    }
                    ++newlines;
                    if (this.fCurrentEntity.offset != this.fCurrentEntity.length) {
                        continue;
                    }
                    offset = 0;
                    this.fCurrentEntity.offset = newlines;
                    if (this.fCurrentEntity.load(newlines) == -1) {
                        break;
                    }
                    continue;
                }
            } while (this.fCurrentEntity.offset < this.fCurrentEntity.length - 1);
            this.fCurrentEntity.incLine(newlines);
        }
        return newlines;
    }
    
    protected final Augmentations locationAugs() {
        HTMLAugmentations augs = null;
        if (this.fAugmentations) {
            this.fLocationItem.setValues(this.fBeginLineNumber, this.fBeginColumnNumber, this.fBeginCharacterOffset, this.fEndLineNumber, this.fEndColumnNumber, this.fEndCharacterOffset);
            augs = this.fInfosetAugs;
            augs.removeAllItems();
            augs.putItem("http://cyberneko.org/html/features/augmentations", this.fLocationItem);
        }
        return (Augmentations)augs;
    }
    
    protected final Augmentations synthesizedAugs() {
        HTMLAugmentations augs = null;
        if (this.fAugmentations) {
            augs = this.fInfosetAugs;
            augs.removeAllItems();
            augs.putItem("http://cyberneko.org/html/features/augmentations", HTMLScanner.SYNTHESIZED_ITEM);
        }
        return (Augmentations)augs;
    }
    
    protected final XMLResourceIdentifier resourceId() {
        this.fResourceId.clear();
        return (XMLResourceIdentifier)this.fResourceId;
    }
    
    protected static boolean builtinXmlRef(final String name) {
        return name.equals("amp") || name.equals("lt") || name.equals("gt") || name.equals("quot") || name.equals("apos");
    }
    
    private void appendChar(final XMLStringBuffer str, final int value) {
        if (value > 65535) {
            final char[] chars = Character.toChars(value);
            str.append(chars, 0, chars.length);
        }
        else {
            str.append((char)value);
        }
    }
    
    private void appendChar(final StringBuffer str, final int value) {
        if (value > 65535) {
            final char[] chars = Character.toChars(value);
            str.append(chars, 0, chars.length);
        }
        else {
            str.append((char)value);
        }
    }
    
    boolean isEncodingCompatible(final String encoding1, final String encoding2) {
        final String reference = "<html><head><meta http-equiv=\"Content-Type\" content=\"text/html;charset=";
        try {
            final byte[] bytesEncoding1 = "<html><head><meta http-equiv=\"Content-Type\" content=\"text/html;charset=".getBytes(encoding1);
            final String referenceWithEncoding2 = new String(bytesEncoding1, encoding2);
            return "<html><head><meta http-equiv=\"Content-Type\" content=\"text/html;charset=".equals(referenceWithEncoding2);
        }
        catch (final UnsupportedEncodingException e) {
            return false;
        }
    }
    
    private boolean endsWith(final XMLStringBuffer buffer, final String string) {
        final int l = string.length();
        if (buffer.length < l) {
            return false;
        }
        final String s = new String(buffer.ch, buffer.length - l, l);
        return string.equals(s);
    }
    
    protected int readPreservingBufferContent() throws IOException {
        if (this.fCurrentEntity.offset == this.fCurrentEntity.length && this.fCurrentEntity.load(this.fCurrentEntity.length) < 1) {
            return -1;
        }
        final char c = this.fCurrentEntity.getNextChar();
        return c;
    }
    
    private boolean endCommentAvailable() throws IOException {
        int nbCaret = 0;
        final int originalOffset = this.fCurrentEntity.offset;
        final int originalColumnNumber = this.fCurrentEntity.getColumnNumber();
        final int originalCharacterOffset = this.fCurrentEntity.getCharacterOffset();
        while (true) {
            final int c = this.readPreservingBufferContent();
            if (c == -1) {
                this.fCurrentEntity.restorePosition(originalOffset, originalColumnNumber, originalCharacterOffset);
                return false;
            }
            if (c == 62 && nbCaret >= 2) {
                this.fCurrentEntity.restorePosition(originalOffset, originalColumnNumber, originalCharacterOffset);
                return true;
            }
            if (c == 45) {
                ++nbCaret;
            }
            else {
                nbCaret = 0;
            }
        }
    }
    
    static void reduceToContent(final XMLStringBuffer buffer, final String startMarker, final String endMarker) {
        int i = 0;
        int startContent = -1;
        final int l1 = startMarker.length();
        final int l2 = endMarker.length();
        while (i < buffer.length - l1 - l2) {
            final char c = buffer.ch[buffer.offset + i];
            if (Character.isWhitespace(c)) {
                ++i;
            }
            else {
                if (c == startMarker.charAt(0) && startMarker.equals(new String(buffer.ch, buffer.offset + i, l1))) {
                    startContent = buffer.offset + i + l1;
                    break;
                }
                return;
            }
        }
        if (startContent == -1) {
            return;
        }
        i = buffer.length - 1;
        while (i > startContent + l2) {
            final char c = buffer.ch[buffer.offset + i];
            if (Character.isWhitespace(c)) {
                --i;
            }
            else if (c == endMarker.charAt(l2 - 1) && endMarker.equals(new String(buffer.ch, buffer.offset + i - l2 + 1, l2))) {
                buffer.length = buffer.offset + i - startContent - 2;
                buffer.offset = startContent;
            }
        }
    }
    
    static {
        RECOGNIZED_FEATURES = new String[] { "http://cyberneko.org/html/features/augmentations", "http://cyberneko.org/html/features/report-errors", "http://apache.org/xml/features/scanner/notify-char-refs", "http://apache.org/xml/features/scanner/notify-builtin-refs", "http://cyberneko.org/html/features/scanner/notify-builtin-refs", "http://cyberneko.org/html/features/scanner/fix-mswindows-refs", "http://cyberneko.org/html/features/scanner/script/strip-cdata-delims", "http://cyberneko.org/html/features/scanner/script/strip-comment-delims", "http://cyberneko.org/html/features/scanner/style/strip-cdata-delims", "http://cyberneko.org/html/features/scanner/style/strip-comment-delims", "http://cyberneko.org/html/features/scanner/ignore-specified-charset", "http://cyberneko.org/html/features/scanner/cdata-sections", "http://cyberneko.org/html/features/override-doctype", "http://cyberneko.org/html/features/insert-doctype", "http://cyberneko.org/html/features/scanner/normalize-attrs", "http://cyberneko.org/html/features/parse-noscript-content", "http://cyberneko.org/html/features/scanner/allow-selfclosing-iframe", "http://cyberneko.org/html/features/scanner/allow-selfclosing-tags" };
        RECOGNIZED_FEATURES_DEFAULTS = new Boolean[] { null, null, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.TRUE, Boolean.FALSE, Boolean.FALSE };
        RECOGNIZED_PROPERTIES = new String[] { "http://cyberneko.org/html/properties/names/elems", "http://cyberneko.org/html/properties/names/attrs", "http://cyberneko.org/html/properties/default-encoding", "http://cyberneko.org/html/properties/error-reporter", "http://cyberneko.org/html/properties/doctype/pubid", "http://cyberneko.org/html/properties/doctype/sysid", "http://cyberneko.org/html/zoho/features/attribute-limit" };
        RECOGNIZED_PROPERTIES_DEFAULTS = new Object[] { null, null, "Windows-1252", null, "-//W3C//DTD HTML 4.01 Transitional//EN", "http://www.w3.org/TR/html4/loose.dtd", "-1" };
        SYNTHESIZED_ITEM = new HTMLEventInfo.SynthesizedItem();
        ENTITY_CHARS = new BitSet();
        final String str = "-.0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ_abcdefghijklmnopqrstuvwxyz";
        for (int i = 0; i < "-.0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ_abcdefghijklmnopqrstuvwxyz".length(); ++i) {
            final char c = "-.0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ_abcdefghijklmnopqrstuvwxyz".charAt(i);
            HTMLScanner.ENTITY_CHARS.set(c);
        }
        (HTML_4_01_PUB_VS_SYS_ID_MAP = new HashMap<String, String>()).put("-//W3C//DTD HTML 4.01//EN".toLowerCase(), "http://www.w3.org/TR/html4/strict.dtd");
        HTMLScanner.HTML_4_01_PUB_VS_SYS_ID_MAP.put("-//W3C//DTD HTML 4.01 Transitional//EN".toLowerCase(), "http://www.w3.org/TR/html4/loose.dtd");
        HTMLScanner.HTML_4_01_PUB_VS_SYS_ID_MAP.put("-//W3C//DTD HTML 4.01 Frameset//EN".toLowerCase(), "http://www.w3.org/TR/html4/frameset.dtd");
    }
    
    public static class CurrentEntity
    {
        private Reader stream_;
        private String encoding;
        public final String publicId;
        public final String baseSystemId;
        public final String literalSystemId;
        public final String expandedSystemId;
        public final String version = "1.0";
        private int lineNumber_;
        private int columnNumber_;
        public int characterOffset_;
        public char[] buffer;
        public int offset;
        public int length;
        private boolean endReached_;
        
        public CurrentEntity(final Reader stream, final String encoding, final String publicId, final String baseSystemId, final String literalSystemId, final String expandedSystemId) {
            this.lineNumber_ = 1;
            this.columnNumber_ = 1;
            this.characterOffset_ = 0;
            this.buffer = new char[2048];
            this.offset = 0;
            this.length = 0;
            this.endReached_ = false;
            this.stream_ = stream;
            this.encoding = encoding;
            this.publicId = publicId;
            this.baseSystemId = baseSystemId;
            this.literalSystemId = literalSystemId;
            this.expandedSystemId = expandedSystemId;
        }
        
        private char getCurrentChar() {
            return this.buffer[this.offset];
        }
        
        private char getNextChar() {
            ++this.characterOffset_;
            ++this.columnNumber_;
            return this.buffer[this.offset++];
        }
        
        private void closeQuietly() {
            try {
                this.stream_.close();
            }
            catch (final IOException ex) {}
        }
        
        boolean hasNext() {
            return this.offset < this.length;
        }
        
        protected int load(final int offset) throws IOException {
            if (offset == this.buffer.length) {
                final int adjust = this.buffer.length / 4;
                final char[] array = new char[this.buffer.length + adjust];
                System.arraycopy(this.buffer, 0, array, 0, this.length);
                this.buffer = array;
            }
            final int count = this.stream_.read(this.buffer, offset, this.buffer.length - offset);
            if (count == -1) {
                this.endReached_ = true;
            }
            this.length = ((count != -1) ? (count + offset) : offset);
            this.offset = offset;
            return count;
        }
        
        protected int read() throws IOException {
            if (this.offset == this.length) {
                if (this.endReached_) {
                    return -1;
                }
                if (this.load(0) == -1) {
                    return -1;
                }
            }
            final char c = this.buffer[this.offset++];
            ++this.characterOffset_;
            ++this.columnNumber_;
            return c;
        }
        
        private void debugBufferIfNeeded(final String prefix) {
            this.debugBufferIfNeeded(prefix, "");
        }
        
        private void debugBufferIfNeeded(final String prefix, final String suffix) {
        }
        
        private void setStream(final InputStreamReader inputStreamReader) {
            this.stream_ = inputStreamReader;
            final int offset = 0;
            this.characterOffset_ = offset;
            this.length = offset;
            this.offset = offset;
            final int n = 1;
            this.columnNumber_ = n;
            this.lineNumber_ = n;
            this.encoding = inputStreamReader.getEncoding();
        }
        
        private void rewind() {
            --this.offset;
            --this.characterOffset_;
            --this.columnNumber_;
        }
        
        private void rewind(final int i) {
            this.offset -= i;
            this.characterOffset_ -= i;
            this.columnNumber_ -= i;
        }
        
        private void incLine() {
            ++this.lineNumber_;
            this.columnNumber_ = 1;
        }
        
        private void incLine(final int nbLines) {
            this.lineNumber_ += nbLines;
            this.columnNumber_ = 1;
        }
        
        public int getLineNumber() {
            return this.lineNumber_;
        }
        
        private void resetBuffer(final XMLStringBuffer buffer, final int lineNumber, final int columnNumber, final int characterOffset) {
            this.lineNumber_ = lineNumber;
            this.columnNumber_ = columnNumber;
            this.characterOffset_ = characterOffset;
            this.buffer = buffer.ch;
            this.offset = buffer.offset;
            this.length = buffer.length;
        }
        
        private int getColumnNumber() {
            return this.columnNumber_;
        }
        
        private void restorePosition(final int originalOffset, final int originalColumnNumber, final int originalCharacterOffset) {
            this.offset = originalOffset;
            this.columnNumber_ = originalColumnNumber;
            this.characterOffset_ = originalCharacterOffset;
        }
        
        private int getCharacterOffset() {
            return this.characterOffset_;
        }
    }
    
    public class ContentScanner implements Scanner
    {
        private final QName fQName;
        private final XMLAttributesImpl fAttributes;
        
        public ContentScanner() {
            this.fQName = new QName();
            this.fAttributes = new XMLAttributesImpl();
        }
        
        @Override
        public boolean scan(final boolean complete) throws IOException {
            boolean next;
            do {
                try {
                    next = false;
                    switch (HTMLScanner.this.fScannerState) {
                        case 0: {
                            HTMLScanner.this.fBeginLineNumber = HTMLScanner.this.fCurrentEntity.getLineNumber();
                            HTMLScanner.this.fBeginColumnNumber = HTMLScanner.this.fCurrentEntity.getColumnNumber();
                            HTMLScanner.this.fBeginCharacterOffset = HTMLScanner.this.fCurrentEntity.getCharacterOffset();
                            final int c = HTMLScanner.this.fCurrentEntity.read();
                            if (c == 60) {
                                HTMLScanner.this.setScannerState((short)1);
                                next = true;
                                break;
                            }
                            if (c == 38) {
                                HTMLScanner.this.scanEntityRef(HTMLScanner.this.fStringBuffer, true);
                                break;
                            }
                            if (c == -1) {
                                throw new EOFException();
                            }
                            HTMLScanner.this.fCurrentEntity.rewind();
                            this.scanCharacters();
                            break;
                        }
                        case 1: {
                            final int c = HTMLScanner.this.fCurrentEntity.read();
                            if (c == 33) {
                                if (HTMLScanner.this.skip("--", false)) {
                                    this.scanComment();
                                }
                                else if (HTMLScanner.this.skip("[CDATA[", false)) {
                                    this.scanCDATA();
                                }
                                else if (HTMLScanner.this.skip("DOCTYPE", false)) {
                                    HTMLScanner.this.scanDoctype();
                                }
                                else {
                                    if (HTMLScanner.this.fReportErrors) {
                                        HTMLScanner.this.fErrorReporter.reportError("HTML1002", null);
                                    }
                                    HTMLScanner.this.skipMarkup(true);
                                }
                            }
                            else if (c == 63) {
                                HTMLScanner.this.fStringBuffer.clear();
                                HTMLScanner.this.fStringBuffer.append('?');
                                this.scanUntilGreaterThanSign(HTMLScanner.this.fStringBuffer);
                                if (HTMLScanner.this.fDocumentHandler != null && HTMLScanner.this.fElementCount >= HTMLScanner.this.fElementDepth) {
                                    HTMLScanner.this.fDocumentHandler.comment((XMLString)HTMLScanner.this.fStringBuffer, (Augmentations)null);
                                }
                            }
                            else if (c == 47) {
                                this.scanEndElement();
                            }
                            else {
                                if (c == -1) {
                                    if (HTMLScanner.this.fReportErrors) {
                                        HTMLScanner.this.fErrorReporter.reportError("HTML1003", null);
                                    }
                                    if (HTMLScanner.this.fDocumentHandler != null && HTMLScanner.this.fElementCount >= HTMLScanner.this.fElementDepth) {
                                        HTMLScanner.this.fStringBuffer.clear();
                                        HTMLScanner.this.fStringBuffer.append('<');
                                        HTMLScanner.this.fDocumentHandler.characters((XMLString)HTMLScanner.this.fStringBuffer, (Augmentations)null);
                                    }
                                    throw new EOFException();
                                }
                                HTMLScanner.this.fCurrentEntity.rewind();
                                final HTMLScanner this$0 = HTMLScanner.this;
                                ++this$0.fElementCount;
                                HTMLScanner.this.fSingleBoolean[0] = false;
                                final String ename = this.scanStartElement(HTMLScanner.this.fSingleBoolean);
                                final String enameLC = (ename == null) ? null : ename.toLowerCase();
                                HTMLScanner.this.fBeginLineNumber = HTMLScanner.this.fCurrentEntity.getLineNumber();
                                HTMLScanner.this.fBeginColumnNumber = HTMLScanner.this.fCurrentEntity.getColumnNumber();
                                HTMLScanner.this.fBeginCharacterOffset = HTMLScanner.this.fCurrentEntity.getCharacterOffset();
                                if ("script".equals(enameLC)) {
                                    if (!HTMLScanner.this.fSingleBoolean[0]) {
                                        this.scanScriptContent();
                                    }
                                }
                                else if (!HTMLScanner.this.fAllowSelfclosingTags && !HTMLScanner.this.fAllowSelfclosingIframe && "iframe".equals(enameLC)) {
                                    this.scanUntilEndTag("iframe");
                                }
                                else if (!HTMLScanner.this.fParseNoScriptContent && ("noscript".equals(enameLC) || "noembed".equals(enameLC))) {
                                    this.scanUntilEndTag(enameLC);
                                }
                                else if (!HTMLScanner.this.fParseNoFramesContent && "noframes".equals(enameLC)) {
                                    this.scanUntilEndTag("noframes");
                                }
                                else if (ename != null && !HTMLScanner.this.fSingleBoolean[0] && HTMLElements.getElement(enameLC).isSpecial()) {
                                    HTMLScanner.this.setScanner(HTMLScanner.this.fSpecialScanner.setElementName(ename));
                                    HTMLScanner.this.setScannerState((short)0);
                                    return true;
                                }
                            }
                            HTMLScanner.this.setScannerState((short)0);
                            break;
                        }
                        case 10: {
                            if (HTMLScanner.this.fDocumentHandler != null && HTMLScanner.this.fElementCount >= HTMLScanner.this.fElementDepth) {
                                final XMLLocator locator = (XMLLocator)HTMLScanner.this;
                                final String encoding = HTMLScanner.this.fIANAEncoding;
                                final Augmentations augs = HTMLScanner.this.locationAugs();
                                final NamespaceContext nscontext = (NamespaceContext)new NamespaceSupport();
                                XercesBridge.getInstance().XMLDocumentHandler_startDocument(HTMLScanner.this.fDocumentHandler, locator, encoding, nscontext, augs);
                            }
                            if (HTMLScanner.this.fInsertDoctype && HTMLScanner.this.fDocumentHandler != null) {
                                String root = HTMLElements.getElement((short)46).name;
                                root = HTMLScanner.modifyName(root, HTMLScanner.this.fNamesElems);
                                final String pubid = HTMLScanner.this.fDoctypePubid;
                                final String sysid = HTMLScanner.this.fDoctypeSysid;
                                HTMLScanner.this.fDocumentHandler.doctypeDecl(root, pubid, sysid, HTMLScanner.this.synthesizedAugs());
                            }
                            HTMLScanner.this.setScannerState((short)0);
                            break;
                        }
                        case 11: {
                            if (HTMLScanner.this.fDocumentHandler != null && HTMLScanner.this.fElementCount >= HTMLScanner.this.fElementDepth && complete) {
                                HTMLScanner.this.fEndLineNumber = HTMLScanner.this.fCurrentEntity.getLineNumber();
                                HTMLScanner.this.fEndColumnNumber = HTMLScanner.this.fCurrentEntity.getColumnNumber();
                                HTMLScanner.this.fEndCharacterOffset = HTMLScanner.this.fCurrentEntity.getCharacterOffset();
                                HTMLScanner.this.fDocumentHandler.endDocument(HTMLScanner.this.locationAugs());
                            }
                            return false;
                        }
                        default: {
                            throw new RuntimeException("unknown scanner state: " + HTMLScanner.this.fScannerState);
                        }
                    }
                }
                catch (final EOFException e) {
                    if (HTMLScanner.this.fCurrentEntityStack.empty()) {
                        HTMLScanner.this.setScannerState((short)11);
                    }
                    else {
                        HTMLScanner.this.fCurrentEntity = HTMLScanner.this.fCurrentEntityStack.pop();
                    }
                    next = true;
                }
            } while (next || complete);
            return true;
        }
        
        protected boolean scanUntilGreaterThanSign(final XMLStringBuffer buffer) throws IOException {
            int c = -1;
            while (true) {
                c = HTMLScanner.this.fCurrentEntity.read();
                if (c == 62 || c == -1) {
                    break;
                }
                buffer.append((char)c);
            }
            return c == -1;
        }
        
        private void scanUntilEndTag(final String tagName) throws IOException {
            final XMLStringBuffer buffer = new XMLStringBuffer();
            final String end = "/" + tagName;
            final int lengthToScan = tagName.length() + 2;
            while (true) {
                final int c = HTMLScanner.this.fCurrentEntity.read();
                if (c == -1) {
                    break;
                }
                if (c == 60) {
                    final String next = this.nextContent(lengthToScan) + " ";
                    if (next.length() >= lengthToScan && end.equalsIgnoreCase(next.substring(0, end.length())) && ('>' == next.charAt(lengthToScan - 1) || Character.isWhitespace(next.charAt(lengthToScan - 1)))) {
                        HTMLScanner.this.fCurrentEntity.rewind();
                        break;
                    }
                }
                if (c == 13 || c == 10) {
                    HTMLScanner.this.fCurrentEntity.rewind();
                    for (int newlines = HTMLScanner.this.skipNewlines(), i = 0; i < newlines; ++i) {
                        buffer.append('\n');
                    }
                }
                else {
                    HTMLScanner.this.appendChar(buffer, c);
                }
            }
            if (buffer.length > 0 && HTMLScanner.this.fDocumentHandler != null) {
                HTMLScanner.this.fEndLineNumber = HTMLScanner.this.fCurrentEntity.getLineNumber();
                HTMLScanner.this.fEndColumnNumber = HTMLScanner.this.fCurrentEntity.getColumnNumber();
                HTMLScanner.this.fEndCharacterOffset = HTMLScanner.this.fCurrentEntity.getCharacterOffset();
                HTMLScanner.this.fDocumentHandler.characters((XMLString)buffer, HTMLScanner.this.locationAugs());
            }
        }
        
        private void scanScriptContent() throws IOException {
            final XMLStringBuffer buffer = new XMLStringBuffer();
            boolean waitForEndComment = false;
            while (true) {
                final int c = HTMLScanner.this.fCurrentEntity.read();
                if (c == -1) {
                    break;
                }
                if (c == 45 && HTMLScanner.this.endsWith(buffer, "<!-")) {
                    waitForEndComment = HTMLScanner.this.endCommentAvailable();
                }
                else if (!waitForEndComment && c == 60) {
                    final String next = this.nextContent(8) + " ";
                    if (next.length() >= 8 && "/script".equalsIgnoreCase(next.substring(0, 7)) && ('>' == next.charAt(7) || Character.isWhitespace(next.charAt(7)))) {
                        HTMLScanner.this.fCurrentEntity.rewind();
                        break;
                    }
                }
                else if (c == 62 && HTMLScanner.this.endsWith(buffer, "--")) {
                    waitForEndComment = false;
                }
                if (c == 13 || c == 10) {
                    HTMLScanner.this.fCurrentEntity.rewind();
                    for (int newlines = HTMLScanner.this.skipNewlines(), i = 0; i < newlines; ++i) {
                        buffer.append('\n');
                    }
                }
                else {
                    HTMLScanner.this.appendChar(buffer, c);
                }
            }
            if (HTMLScanner.this.fScriptStripCommentDelims) {
                HTMLScanner.reduceToContent(buffer, "<!--", "-->");
            }
            if (HTMLScanner.this.fScriptStripCDATADelims) {
                HTMLScanner.reduceToContent(buffer, "<![CDATA[", "]]>");
            }
            if (buffer.length > 0 && HTMLScanner.this.fDocumentHandler != null && HTMLScanner.this.fElementCount >= HTMLScanner.this.fElementDepth) {
                HTMLScanner.this.fEndLineNumber = HTMLScanner.this.fCurrentEntity.getLineNumber();
                HTMLScanner.this.fEndColumnNumber = HTMLScanner.this.fCurrentEntity.getColumnNumber();
                HTMLScanner.this.fEndCharacterOffset = HTMLScanner.this.fCurrentEntity.getCharacterOffset();
                HTMLScanner.this.fDocumentHandler.characters((XMLString)buffer, HTMLScanner.this.locationAugs());
            }
        }
        
        protected String nextContent(final int len) throws IOException {
            final int originalOffset = HTMLScanner.this.fCurrentEntity.offset;
            final int originalColumnNumber = HTMLScanner.this.fCurrentEntity.getColumnNumber();
            final int originalCharacterOffset = HTMLScanner.this.fCurrentEntity.getCharacterOffset();
            final char[] buff = new char[len];
            int nbRead;
            int c;
            for (nbRead = 0, nbRead = 0; nbRead < len; ++nbRead) {
                if (HTMLScanner.this.fCurrentEntity.offset == HTMLScanner.this.fCurrentEntity.length) {
                    if (HTMLScanner.this.fCurrentEntity.length != HTMLScanner.this.fCurrentEntity.buffer.length) {
                        break;
                    }
                    HTMLScanner.this.fCurrentEntity.load(HTMLScanner.this.fCurrentEntity.buffer.length);
                }
                c = HTMLScanner.this.fCurrentEntity.read();
                if (c == -1) {
                    break;
                }
                buff[nbRead] = (char)c;
            }
            HTMLScanner.this.fCurrentEntity.restorePosition(originalOffset, originalColumnNumber, originalCharacterOffset);
            return new String(buff, 0, nbRead);
        }
        
        protected void scanCharacters() throws IOException {
            HTMLScanner.this.fStringBuffer.clear();
            int next;
            do {
                final int newlines = HTMLScanner.this.skipNewlines();
                if (newlines == 0 && HTMLScanner.this.fCurrentEntity.offset == HTMLScanner.this.fCurrentEntity.length) {
                    break;
                }
                int i;
                int offset;
                for (offset = (i = HTMLScanner.this.fCurrentEntity.offset - newlines); i < HTMLScanner.this.fCurrentEntity.offset; ++i) {
                    HTMLScanner.this.fCurrentEntity.buffer[i] = '\n';
                }
                while (HTMLScanner.this.fCurrentEntity.hasNext()) {
                    final char c = HTMLScanner.this.fCurrentEntity.getNextChar();
                    if (c == '<' || c == '&' || c == '\n' || c == '\r') {
                        HTMLScanner.this.fCurrentEntity.rewind();
                        break;
                    }
                }
                if (HTMLScanner.this.fCurrentEntity.offset > offset && HTMLScanner.this.fDocumentHandler != null && HTMLScanner.this.fElementCount >= HTMLScanner.this.fElementDepth) {
                    HTMLScanner.this.fEndLineNumber = HTMLScanner.this.fCurrentEntity.getLineNumber();
                    HTMLScanner.this.fEndColumnNumber = HTMLScanner.this.fCurrentEntity.getColumnNumber();
                    HTMLScanner.this.fEndCharacterOffset = HTMLScanner.this.fCurrentEntity.getCharacterOffset();
                    HTMLScanner.this.fStringBuffer.append(HTMLScanner.this.fCurrentEntity.buffer, offset, HTMLScanner.this.fCurrentEntity.offset - offset);
                }
                final boolean hasNext = HTMLScanner.this.fCurrentEntity.offset < HTMLScanner.this.fCurrentEntity.buffer.length;
                next = (hasNext ? HTMLScanner.this.fCurrentEntity.getCurrentChar() : -1);
                if (next == 38 || next == 60) {
                    break;
                }
            } while (next != -1);
            if (HTMLScanner.this.fStringBuffer.length != 0) {
                HTMLScanner.this.fDocumentHandler.characters((XMLString)HTMLScanner.this.fStringBuffer, HTMLScanner.this.locationAugs());
            }
        }
        
        protected void scanCDATA() throws IOException {
            HTMLScanner.this.fStringBuffer.clear();
            if (HTMLScanner.this.fCDATASections) {
                if (HTMLScanner.this.fDocumentHandler != null && HTMLScanner.this.fElementCount >= HTMLScanner.this.fElementDepth) {
                    HTMLScanner.this.fEndLineNumber = HTMLScanner.this.fCurrentEntity.getLineNumber();
                    HTMLScanner.this.fEndColumnNumber = HTMLScanner.this.fCurrentEntity.getColumnNumber();
                    HTMLScanner.this.fEndCharacterOffset = HTMLScanner.this.fCurrentEntity.getCharacterOffset();
                    HTMLScanner.this.fDocumentHandler.startCDATA(HTMLScanner.this.locationAugs());
                }
            }
            else {
                HTMLScanner.this.fStringBuffer.append("[CDATA[");
            }
            final boolean eof = HTMLScanner.this.fCDATASections ? this.scanMarkupContent(HTMLScanner.this.fStringBuffer, ']', false) : this.scanUntilGreaterThanSign(HTMLScanner.this.fStringBuffer);
            if (HTMLScanner.this.fDocumentHandler != null && HTMLScanner.this.fElementCount >= HTMLScanner.this.fElementDepth) {
                HTMLScanner.this.fEndLineNumber = HTMLScanner.this.fCurrentEntity.getLineNumber();
                HTMLScanner.this.fEndColumnNumber = HTMLScanner.this.fCurrentEntity.getColumnNumber();
                HTMLScanner.this.fEndCharacterOffset = HTMLScanner.this.fCurrentEntity.getCharacterOffset();
                if (HTMLScanner.this.fCDATASections) {
                    HTMLScanner.this.fDocumentHandler.characters((XMLString)HTMLScanner.this.fStringBuffer, HTMLScanner.this.locationAugs());
                    HTMLScanner.this.fDocumentHandler.endCDATA(HTMLScanner.this.locationAugs());
                }
                else {
                    HTMLScanner.this.fDocumentHandler.comment((XMLString)HTMLScanner.this.fStringBuffer, HTMLScanner.this.locationAugs());
                }
            }
            if (eof) {
                throw new EOFException();
            }
        }
        
        protected void scanComment() throws IOException {
            HTMLScanner.this.fEndLineNumber = HTMLScanner.this.fCurrentEntity.getLineNumber();
            HTMLScanner.this.fEndColumnNumber = HTMLScanner.this.fCurrentEntity.getColumnNumber();
            HTMLScanner.this.fEndCharacterOffset = HTMLScanner.this.fCurrentEntity.getCharacterOffset();
            final XMLStringBuffer buffer = new XMLStringBuffer();
            final boolean eof = this.scanMarkupContent(buffer, '-', true);
            if (HTMLScanner.this.fDocumentHandler != null && HTMLScanner.this.fElementCount >= HTMLScanner.this.fElementDepth) {
                HTMLScanner.this.fEndLineNumber = HTMLScanner.this.fCurrentEntity.getLineNumber();
                HTMLScanner.this.fEndColumnNumber = HTMLScanner.this.fCurrentEntity.getColumnNumber();
                HTMLScanner.this.fEndCharacterOffset = HTMLScanner.this.fCurrentEntity.getCharacterOffset();
                HTMLScanner.this.fDocumentHandler.comment((XMLString)buffer, HTMLScanner.this.locationAugs());
            }
            if (eof) {
                throw new EOFException();
            }
        }
        
        protected boolean scanMarkupContent(final XMLStringBuffer buffer, final char cend, final boolean isComment) throws IOException {
            int reckon = 0;
            int c = -1;
            while (true) {
                ++reckon;
                c = HTMLScanner.this.fCurrentEntity.read();
                if (isComment && c == 62 && reckon == 1) {
                    break;
                }
                if (c == cend) {
                    int count = 1;
                    while (true) {
                        c = HTMLScanner.this.fCurrentEntity.read();
                        if (c != cend) {
                            break;
                        }
                        ++count;
                    }
                    if (c == -1) {
                        if (HTMLScanner.this.fReportErrors) {
                            HTMLScanner.this.fErrorReporter.reportError("HTML1007", null);
                            break;
                        }
                        break;
                    }
                    else {
                        if (isComment && c == 62 && reckon == 1) {
                            break;
                        }
                        if (count < 2) {
                            buffer.append(cend);
                            HTMLScanner.this.fCurrentEntity.rewind();
                        }
                        else {
                            if (isComment && c == 33) {
                                c = HTMLScanner.this.fCurrentEntity.read();
                                if (c == 10 || c == 13) {
                                    HTMLScanner.this.skipNewlines();
                                    c = HTMLScanner.this.fCurrentEntity.read();
                                }
                                if (c == 62) {
                                    for (int i = 0; i < count - 2; ++i) {
                                        buffer.append(cend);
                                    }
                                    break;
                                }
                                HTMLScanner.this.fCurrentEntity.rewind();
                            }
                            if (c == 62) {
                                for (int i = 0; i < count - 2; ++i) {
                                    buffer.append(cend);
                                }
                                break;
                            }
                            for (int i = 0; i < count; ++i) {
                                buffer.append(cend);
                            }
                            HTMLScanner.this.fCurrentEntity.rewind();
                        }
                    }
                }
                else if (c == 10 || c == 13) {
                    HTMLScanner.this.fCurrentEntity.rewind();
                    for (int newlines = HTMLScanner.this.skipNewlines(), i = 0; i < newlines; ++i) {
                        buffer.append('\n');
                    }
                }
                else if (c == -1) {
                    if (HTMLScanner.this.fReportErrors) {
                        HTMLScanner.this.fErrorReporter.reportError("HTML1007", null);
                        break;
                    }
                    break;
                }
                else {
                    HTMLScanner.this.appendChar(buffer, c);
                }
            }
            return c == -1;
        }
        
        protected void scanPI() throws IOException {
            if (HTMLScanner.this.fReportErrors) {
                HTMLScanner.this.fErrorReporter.reportWarning("HTML1008", null);
            }
            final String target = HTMLScanner.this.scanName(true);
            if (target != null && !target.equalsIgnoreCase("xml")) {
                while (true) {
                    int c = HTMLScanner.this.fCurrentEntity.read();
                    if (c == 13 || c == 10) {
                        if (c == 13) {
                            c = HTMLScanner.this.fCurrentEntity.read();
                            if (c != 10) {
                                final CurrentEntity fCurrentEntity = HTMLScanner.this.fCurrentEntity;
                                --fCurrentEntity.offset;
                                final CurrentEntity fCurrentEntity2 = HTMLScanner.this.fCurrentEntity;
                                --fCurrentEntity2.characterOffset_;
                            }
                        }
                        HTMLScanner.this.fCurrentEntity.incLine();
                    }
                    else {
                        if (c == -1) {
                            break;
                        }
                        if (c != 32 && c != 9) {
                            HTMLScanner.this.fCurrentEntity.rewind();
                            break;
                        }
                        continue;
                    }
                }
                HTMLScanner.this.fStringBuffer.clear();
                while (true) {
                    int c = HTMLScanner.this.fCurrentEntity.read();
                    if (c == 63 || c == 47) {
                        final char c2 = (char)c;
                        c = HTMLScanner.this.fCurrentEntity.read();
                        if (c == 62) {
                            break;
                        }
                        HTMLScanner.this.fStringBuffer.append(c2);
                        HTMLScanner.this.fCurrentEntity.rewind();
                    }
                    else if (c == 13 || c == 10) {
                        HTMLScanner.this.fStringBuffer.append('\n');
                        if (c == 13) {
                            c = HTMLScanner.this.fCurrentEntity.read();
                            if (c != 10) {
                                final CurrentEntity fCurrentEntity3 = HTMLScanner.this.fCurrentEntity;
                                --fCurrentEntity3.offset;
                                final CurrentEntity fCurrentEntity4 = HTMLScanner.this.fCurrentEntity;
                                --fCurrentEntity4.characterOffset_;
                            }
                        }
                        HTMLScanner.this.fCurrentEntity.incLine();
                    }
                    else {
                        if (c == -1) {
                            break;
                        }
                        HTMLScanner.this.appendChar(HTMLScanner.this.fStringBuffer, c);
                    }
                }
                final XMLString data = (XMLString)HTMLScanner.this.fStringBuffer;
                if (HTMLScanner.this.fDocumentHandler != null) {
                    HTMLScanner.this.fEndLineNumber = HTMLScanner.this.fCurrentEntity.getLineNumber();
                    HTMLScanner.this.fEndColumnNumber = HTMLScanner.this.fCurrentEntity.getColumnNumber();
                    HTMLScanner.this.fEndCharacterOffset = HTMLScanner.this.fCurrentEntity.getCharacterOffset();
                    HTMLScanner.this.fDocumentHandler.processingInstruction(target, data, HTMLScanner.this.locationAugs());
                }
            }
            else {
                final int beginLineNumber = HTMLScanner.this.fBeginLineNumber;
                final int beginColumnNumber = HTMLScanner.this.fBeginColumnNumber;
                final int beginCharacterOffset = HTMLScanner.this.fBeginCharacterOffset;
                this.fAttributes.removeAllAttributes();
                int aindex = 0;
                while (this.scanPseudoAttribute(this.fAttributes)) {
                    if (this.fAttributes.getValue(aindex).length() == 0) {
                        this.fAttributes.removeAttributeAt(aindex);
                    }
                    else {
                        this.fAttributes.getName(aindex, this.fQName);
                        this.fQName.rawname = this.fQName.rawname.toLowerCase();
                        this.fAttributes.setName(aindex, this.fQName);
                        ++aindex;
                    }
                }
                if (HTMLScanner.this.fDocumentHandler != null) {
                    final String version = this.fAttributes.getValue("version");
                    final String encoding = this.fAttributes.getValue("encoding");
                    final String standalone = this.fAttributes.getValue("standalone");
                    final boolean xmlDeclNow = HTMLScanner.this.fIgnoreSpecifiedCharset || !this.changeEncoding(encoding);
                    if (xmlDeclNow) {
                        HTMLScanner.this.fBeginLineNumber = beginLineNumber;
                        HTMLScanner.this.fBeginColumnNumber = beginColumnNumber;
                        HTMLScanner.this.fBeginCharacterOffset = beginCharacterOffset;
                        HTMLScanner.this.fEndLineNumber = HTMLScanner.this.fCurrentEntity.getLineNumber();
                        HTMLScanner.this.fEndColumnNumber = HTMLScanner.this.fCurrentEntity.getColumnNumber();
                        HTMLScanner.this.fEndCharacterOffset = HTMLScanner.this.fCurrentEntity.getCharacterOffset();
                        HTMLScanner.this.fDocumentHandler.xmlDecl(version, encoding, standalone, HTMLScanner.this.locationAugs());
                    }
                }
            }
        }
        
        protected String scanStartElement(final boolean[] empty) throws IOException {
            String ename = HTMLScanner.this.scanName(true);
            final int length = (ename != null) ? ename.length() : 0;
            final int c = (length > 0) ? ename.charAt(0) : -1;
            if (length == 0 || ((c < 97 || c > 122) && (c < 65 || c > 90))) {
                if (HTMLScanner.this.fReportErrors) {
                    HTMLScanner.this.fErrorReporter.reportError("HTML1009", null);
                }
                if (HTMLScanner.this.fDocumentHandler != null && HTMLScanner.this.fElementCount >= HTMLScanner.this.fElementDepth) {
                    HTMLScanner.this.fStringBuffer.clear();
                    HTMLScanner.this.fStringBuffer.append('<');
                    if (length > 0) {
                        HTMLScanner.this.fStringBuffer.append(ename);
                    }
                    HTMLScanner.this.fDocumentHandler.characters((XMLString)HTMLScanner.this.fStringBuffer, (Augmentations)null);
                }
                return null;
            }
            ename = HTMLScanner.modifyName(ename, HTMLScanner.this.fNamesElems);
            this.fAttributes.removeAllAttributes();
            final int beginLineNumber = HTMLScanner.this.fBeginLineNumber;
            final int beginColumnNumber = HTMLScanner.this.fBeginColumnNumber;
            final int beginCharacterOffset = HTMLScanner.this.fBeginCharacterOffset;
            while (this.scanAttribute(this.fAttributes, empty)) {}
            HTMLScanner.this.fBeginLineNumber = beginLineNumber;
            HTMLScanner.this.fBeginColumnNumber = beginColumnNumber;
            HTMLScanner.this.fBeginCharacterOffset = beginCharacterOffset;
            if (HTMLScanner.this.fByteStream != null && HTMLScanner.this.fElementDepth == -1) {
                if (ename.equalsIgnoreCase("META") && !HTMLScanner.this.fIgnoreSpecifiedCharset) {
                    final String httpEquiv = HTMLScanner.getValue((XMLAttributes)this.fAttributes, "http-equiv");
                    if (httpEquiv != null && httpEquiv.equalsIgnoreCase("content-type")) {
                        String content = HTMLScanner.getValue((XMLAttributes)this.fAttributes, "content");
                        if (content != null) {
                            content = this.removeSpaces(content);
                            final int index1 = content.toLowerCase().indexOf("charset=");
                            if (index1 != -1) {
                                final int index2 = content.indexOf(59, index1);
                                final String charset = (index2 != -1) ? content.substring(index1 + 8, index2) : content.substring(index1 + 8);
                                this.changeEncoding(charset);
                            }
                        }
                    }
                    else {
                        final String metaCharset = HTMLScanner.getValue((XMLAttributes)this.fAttributes, "charset");
                        if (metaCharset != null) {
                            this.changeEncoding(metaCharset);
                        }
                    }
                }
                else if (ename.equalsIgnoreCase("BODY")) {
                    HTMLScanner.this.fByteStream.clear();
                    HTMLScanner.this.fByteStream = null;
                }
                else {
                    final HTMLElements.Element element = HTMLElements.getElement(ename);
                    if (element.parent != null && element.parent.length > 0 && element.parent[0].code == 14) {
                        HTMLScanner.this.fByteStream.clear();
                        HTMLScanner.this.fByteStream = null;
                    }
                }
            }
            if (HTMLScanner.this.fDocumentHandler != null && HTMLScanner.this.fElementCount >= HTMLScanner.this.fElementDepth) {
                this.fQName.setValues((String)null, ename, ename, (String)null);
                HTMLScanner.this.fEndLineNumber = HTMLScanner.this.fCurrentEntity.getLineNumber();
                HTMLScanner.this.fEndColumnNumber = HTMLScanner.this.fCurrentEntity.getColumnNumber();
                HTMLScanner.this.fEndCharacterOffset = HTMLScanner.this.fCurrentEntity.getCharacterOffset();
                if (empty[0]) {
                    HTMLScanner.this.fDocumentHandler.emptyElement(this.fQName, (XMLAttributes)this.fAttributes, HTMLScanner.this.locationAugs());
                }
                else {
                    HTMLScanner.this.fDocumentHandler.startElement(this.fQName, (XMLAttributes)this.fAttributes, HTMLScanner.this.locationAugs());
                }
            }
            return ename;
        }
        
        private String removeSpaces(final String content) {
            StringBuffer sb = null;
            for (int i = content.length() - 1; i >= 0; --i) {
                if (Character.isWhitespace(content.charAt(i))) {
                    if (sb == null) {
                        sb = new StringBuffer(content);
                    }
                    sb.deleteCharAt(i);
                }
            }
            return (sb == null) ? content : sb.toString();
        }
        
        private boolean changeEncoding(String charset) {
            if (charset == null || HTMLScanner.this.fByteStream == null) {
                return false;
            }
            charset = charset.trim();
            boolean encodingChanged = false;
            try {
                final String ianaEncoding = charset;
                String javaEncoding = EncodingMap.getIANA2JavaMapping(ianaEncoding.toUpperCase(Locale.ENGLISH));
                if (javaEncoding == null) {
                    javaEncoding = ianaEncoding;
                    if (HTMLScanner.this.fReportErrors) {
                        HTMLScanner.this.fErrorReporter.reportError("HTML1001", new Object[] { ianaEncoding });
                    }
                }
                if (!javaEncoding.equals(HTMLScanner.this.fJavaEncoding)) {
                    if (!HTMLScanner.this.isEncodingCompatible(javaEncoding, HTMLScanner.this.fJavaEncoding)) {
                        if (HTMLScanner.this.fReportErrors) {
                            HTMLScanner.this.fErrorReporter.reportError("HTML1015", new Object[] { javaEncoding, HTMLScanner.this.fJavaEncoding });
                        }
                    }
                    else {
                        HTMLScanner.this.fIso8859Encoding = (ianaEncoding == null || ianaEncoding.toUpperCase(Locale.ENGLISH).startsWith("ISO-8859") || ianaEncoding.equalsIgnoreCase(HTMLScanner.this.fDefaultIANAEncoding));
                        HTMLScanner.this.fJavaEncoding = javaEncoding;
                        HTMLScanner.this.fCurrentEntity.setStream(new InputStreamReader(HTMLScanner.this.fByteStream, javaEncoding));
                        HTMLScanner.this.fByteStream.playback();
                        HTMLScanner.this.fElementDepth = HTMLScanner.this.fElementCount;
                        HTMLScanner.this.fElementCount = 0;
                        encodingChanged = true;
                    }
                }
            }
            catch (final UnsupportedEncodingException e) {
                if (HTMLScanner.this.fReportErrors) {
                    HTMLScanner.this.fErrorReporter.reportError("HTML1010", new Object[] { charset });
                }
                HTMLScanner.this.fByteStream.clear();
                HTMLScanner.this.fByteStream = null;
            }
            return encodingChanged;
        }
        
        protected boolean scanAttribute(final XMLAttributesImpl attributes, final boolean[] empty) throws IOException {
            return this.scanAttribute(attributes, empty, '/');
        }
        
        protected boolean scanPseudoAttribute(final XMLAttributesImpl attributes) throws IOException {
            return this.scanAttribute(attributes, HTMLScanner.this.fSingleBoolean, '?');
        }
        
        protected boolean scanAttribute(final XMLAttributesImpl attributes, final boolean[] empty, final char endc) throws IOException {
            final boolean skippedSpaces = HTMLScanner.this.skipSpaces();
            HTMLScanner.this.fBeginLineNumber = HTMLScanner.this.fCurrentEntity.getLineNumber();
            HTMLScanner.this.fBeginColumnNumber = HTMLScanner.this.fCurrentEntity.getColumnNumber();
            HTMLScanner.this.fBeginCharacterOffset = HTMLScanner.this.fCurrentEntity.getCharacterOffset();
            int c = HTMLScanner.this.fCurrentEntity.read();
            if (c == -1) {
                if (HTMLScanner.this.fReportErrors) {
                    HTMLScanner.this.fErrorReporter.reportError("HTML1007", null);
                }
                return false;
            }
            if (c == 62) {
                return false;
            }
            HTMLScanner.this.fCurrentEntity.rewind();
            if (HTMLScanner.this.fAttributeLimit != -1 && HTMLScanner.this.fAttributeLimit == attributes.getLength()) {
                this.skipAttribute();
                return false;
            }
            String aname = HTMLScanner.this.scanName(false);
            if (aname == null) {
                if (HTMLScanner.this.fReportErrors) {
                    final int offset = HTMLScanner.this.fCurrentEntity.offset;
                    final char currentChar = HTMLScanner.this.fCurrentEntity.buffer[offset];
                    if (currentChar != '/' || offset >= HTMLScanner.this.fCurrentEntity.buffer.length - 1 || HTMLScanner.this.fCurrentEntity.buffer[offset + 1] != '>') {
                        HTMLScanner.this.fErrorReporter.reportError("HTML1011", null);
                    }
                }
                empty[0] = HTMLScanner.this.skipMarkup(false);
                return false;
            }
            if (!skippedSpaces && HTMLScanner.this.fReportErrors) {
                HTMLScanner.this.fErrorReporter.reportError("HTML1013", new Object[] { aname });
            }
            aname = HTMLScanner.modifyName(aname, HTMLScanner.this.fNamesAttrs);
            HTMLScanner.this.skipSpaces();
            c = HTMLScanner.this.fCurrentEntity.read();
            if (c == -1) {
                if (HTMLScanner.this.fReportErrors) {
                    HTMLScanner.this.fErrorReporter.reportError("HTML1007", null);
                }
                throw new EOFException();
            }
            if (c == 47 || c == 62) {
                this.fQName.setValues((String)null, aname, aname, (String)null);
                attributes.addAttribute(this.fQName, "CDATA", "");
                attributes.setSpecified(attributes.getLength() - 1, true);
                if (HTMLScanner.this.fAugmentations) {
                    this.addLocationItem((XMLAttributes)attributes, attributes.getLength() - 1);
                }
                if (c == 47) {
                    if (HTMLScanner.this.fReportErrors) {
                        HTMLScanner.this.fErrorReporter.reportError("HTML1006", new Object[] { aname });
                    }
                    HTMLScanner.this.fCurrentEntity.rewind();
                    empty[0] = HTMLScanner.this.skipMarkup(false);
                }
                return false;
            }
            if (c == 61) {
                HTMLScanner.this.skipSpaces();
                c = HTMLScanner.this.fCurrentEntity.read();
                if (c == -1) {
                    if (HTMLScanner.this.fReportErrors) {
                        HTMLScanner.this.fErrorReporter.reportError("HTML1007", null);
                    }
                    throw new EOFException();
                }
                if (c == 62) {
                    this.fQName.setValues((String)null, aname, aname, (String)null);
                    attributes.addAttribute(this.fQName, "CDATA", "");
                    attributes.setSpecified(attributes.getLength() - 1, true);
                    if (HTMLScanner.this.fAugmentations) {
                        this.addLocationItem((XMLAttributes)attributes, attributes.getLength() - 1);
                    }
                    return false;
                }
                HTMLScanner.this.fStringBuffer.clear();
                HTMLScanner.this.fNonNormAttr.clear();
                if (c != 39 && c != 34) {
                    HTMLScanner.this.fCurrentEntity.rewind();
                    while (true) {
                        c = HTMLScanner.this.fCurrentEntity.read();
                        if (Character.isWhitespace((char)c) || c == 62) {
                            HTMLScanner.this.fCurrentEntity.rewind();
                            this.fQName.setValues((String)null, aname, aname, (String)null);
                            final String avalue = HTMLScanner.this.fStringBuffer.toString();
                            attributes.addAttribute(this.fQName, "CDATA", avalue);
                            final int lastattr = attributes.getLength() - 1;
                            attributes.setSpecified(lastattr, true);
                            attributes.setNonNormalizedValue(lastattr, HTMLScanner.this.fNonNormAttr.toString());
                            if (HTMLScanner.this.fAugmentations) {
                                this.addLocationItem((XMLAttributes)attributes, attributes.getLength() - 1);
                            }
                            return true;
                        }
                        if (c == -1) {
                            if (HTMLScanner.this.fReportErrors) {
                                HTMLScanner.this.fErrorReporter.reportError("HTML1007", null);
                            }
                            throw new EOFException();
                        }
                        if (c == 38) {
                            final int ce = HTMLScanner.this.scanEntityRef(HTMLScanner.this.fStringBuffer2, false);
                            if (ce == -2) {
                                HTMLScanner.this.fStringBuffer2.clear();
                            }
                            else if (ce != -1) {
                                try {
                                    HTMLScanner.this.appendChar(HTMLScanner.this.fStringBuffer, ce);
                                }
                                catch (final IllegalArgumentException e) {
                                    if (HTMLScanner.this.fReportErrors) {
                                        HTMLScanner.this.fErrorReporter.reportError("HTML1005", new Object[] { ce });
                                    }
                                    HTMLScanner.this.fStringBuffer.append('\ufffd');
                                }
                            }
                            else {
                                HTMLScanner.this.fStringBuffer.append((XMLString)HTMLScanner.this.fStringBuffer2);
                            }
                            HTMLScanner.this.fNonNormAttr.append((XMLString)HTMLScanner.this.fStringBuffer2);
                        }
                        else {
                            HTMLScanner.this.appendChar(HTMLScanner.this.fStringBuffer, c);
                            HTMLScanner.this.appendChar(HTMLScanner.this.fNonNormAttr, c);
                        }
                    }
                }
                else {
                    final char quote = (char)c;
                    boolean isStart = true;
                    boolean prevSpace = false;
                    do {
                        final boolean acceptSpace = !HTMLScanner.this.fNormalizeAttributes || (!isStart && !prevSpace);
                        c = HTMLScanner.this.fCurrentEntity.read();
                        if (c == -1) {
                            if (HTMLScanner.this.fReportErrors) {
                                HTMLScanner.this.fErrorReporter.reportError("HTML1007", null);
                                break;
                            }
                            break;
                        }
                        else {
                            if (c == 38) {
                                isStart = false;
                                final int ce2 = HTMLScanner.this.scanEntityRef(HTMLScanner.this.fStringBuffer2, false);
                                if (ce2 == -2) {
                                    HTMLScanner.this.fStringBuffer2.clear();
                                }
                                else if (ce2 != -1) {
                                    try {
                                        HTMLScanner.this.appendChar(HTMLScanner.this.fStringBuffer, ce2);
                                    }
                                    catch (final IllegalArgumentException e2) {
                                        if (HTMLScanner.this.fReportErrors) {
                                            HTMLScanner.this.fErrorReporter.reportError("HTML1005", new Object[] { ce2 });
                                        }
                                        HTMLScanner.this.fStringBuffer.append('\ufffd');
                                    }
                                }
                                else {
                                    HTMLScanner.this.fStringBuffer.append((XMLString)HTMLScanner.this.fStringBuffer2);
                                }
                                HTMLScanner.this.fNonNormAttr.append((XMLString)HTMLScanner.this.fStringBuffer2);
                            }
                            else if (c == 32 || c == 9) {
                                if (acceptSpace) {
                                    HTMLScanner.this.fStringBuffer.append(HTMLScanner.this.fNormalizeAttributes ? ' ' : ((char)c));
                                }
                                HTMLScanner.this.fNonNormAttr.append((char)c);
                            }
                            else if (c == 13 || c == 10) {
                                if (c == 13) {
                                    final int c2 = HTMLScanner.this.fCurrentEntity.read();
                                    if (c2 == 10) {
                                        HTMLScanner.this.fNonNormAttr.append('\r');
                                        c = c2;
                                    }
                                    else if (c2 != -1) {
                                        HTMLScanner.this.fCurrentEntity.rewind();
                                    }
                                }
                                if (acceptSpace) {
                                    HTMLScanner.this.fStringBuffer.append(HTMLScanner.this.fNormalizeAttributes ? ' ' : '\n');
                                }
                                HTMLScanner.this.fCurrentEntity.incLine();
                                HTMLScanner.this.fNonNormAttr.append((char)c);
                            }
                            else if (c != quote) {
                                isStart = false;
                                HTMLScanner.this.appendChar(HTMLScanner.this.fStringBuffer, c);
                                HTMLScanner.this.appendChar(HTMLScanner.this.fNonNormAttr, c);
                            }
                            prevSpace = (c == 32 || c == 9 || c == 13 || c == 10);
                            isStart = (isStart && prevSpace);
                        }
                    } while (c != quote);
                    if (HTMLScanner.this.fNormalizeAttributes && HTMLScanner.this.fStringBuffer.length > 0 && HTMLScanner.this.fStringBuffer.ch[HTMLScanner.this.fStringBuffer.length - 1] == ' ') {
                        final XMLStringBuffer fStringBuffer = HTMLScanner.this.fStringBuffer;
                        --fStringBuffer.length;
                    }
                    this.fQName.setValues((String)null, aname, aname, (String)null);
                    final String avalue2 = HTMLScanner.this.fStringBuffer.toString();
                    attributes.addAttribute(this.fQName, "CDATA", avalue2);
                    final int lastattr2 = attributes.getLength() - 1;
                    attributes.setSpecified(lastattr2, true);
                    attributes.setNonNormalizedValue(lastattr2, HTMLScanner.this.fNonNormAttr.toString());
                    if (HTMLScanner.this.fAugmentations) {
                        this.addLocationItem((XMLAttributes)attributes, attributes.getLength() - 1);
                    }
                }
            }
            else {
                this.fQName.setValues((String)null, aname, aname, (String)null);
                attributes.addAttribute(this.fQName, "CDATA", "");
                attributes.setSpecified(attributes.getLength() - 1, true);
                HTMLScanner.this.fCurrentEntity.rewind();
                if (HTMLScanner.this.fAugmentations) {
                    this.addLocationItem((XMLAttributes)attributes, attributes.getLength() - 1);
                }
            }
            return true;
        }
        
        private void skipAttribute() throws IOException {
            int c = -1;
            if (HTMLScanner.this.fReportErrors) {
                HTMLScanner.this.fErrorReporter.reportError("HTML1008", null);
            }
        Label_0027:
            while (true) {
                HTMLScanner.this.skipSpaces();
                c = HTMLScanner.this.fCurrentEntity.read();
                if (c == 62) {
                    return;
                }
                if (c == -1) {
                    if (HTMLScanner.this.fReportErrors) {
                        HTMLScanner.this.fErrorReporter.reportError("HTML1007", null);
                    }
                    throw new EOFException();
                }
                if (c != 61) {
                    continue;
                }
                HTMLScanner.this.skipSpaces();
                c = HTMLScanner.this.fCurrentEntity.read();
                if (c != 39 && c != 34) {
                    while (c != -1) {
                        c = HTMLScanner.this.fCurrentEntity.read();
                        if (c == 32) {
                            continue Label_0027;
                        }
                        if (c == 62) {
                            continue Label_0027;
                        }
                    }
                    if (HTMLScanner.this.fReportErrors) {
                        HTMLScanner.this.fErrorReporter.reportError("HTML1007", null);
                    }
                    throw new EOFException();
                }
                final char quote = (char)c;
                do {
                    c = HTMLScanner.this.fCurrentEntity.read();
                    if (c == -1) {
                        if (HTMLScanner.this.fReportErrors) {
                            HTMLScanner.this.fErrorReporter.reportError("HTML1007", null);
                        }
                        throw new EOFException();
                    }
                } while (c != quote);
            }
        }
        
        protected void addLocationItem(final XMLAttributes attributes, final int index) {
            HTMLScanner.this.fEndLineNumber = HTMLScanner.this.fCurrentEntity.getLineNumber();
            HTMLScanner.this.fEndColumnNumber = HTMLScanner.this.fCurrentEntity.getColumnNumber();
            HTMLScanner.this.fEndCharacterOffset = HTMLScanner.this.fCurrentEntity.getCharacterOffset();
            final LocationItem locationItem = new LocationItem();
            locationItem.setValues(HTMLScanner.this.fBeginLineNumber, HTMLScanner.this.fBeginColumnNumber, HTMLScanner.this.fBeginCharacterOffset, HTMLScanner.this.fEndLineNumber, HTMLScanner.this.fEndColumnNumber, HTMLScanner.this.fEndCharacterOffset);
            final Augmentations augs = attributes.getAugmentations(index);
            augs.putItem("http://cyberneko.org/html/features/augmentations", (Object)locationItem);
        }
        
        protected void scanEndElement() throws IOException {
            String ename = HTMLScanner.this.scanName(true);
            if (HTMLScanner.this.fReportErrors && ename == null) {
                HTMLScanner.this.fErrorReporter.reportError("HTML1012", null);
            }
            HTMLScanner.this.skipMarkup(false);
            if (ename != null) {
                ename = HTMLScanner.modifyName(ename, HTMLScanner.this.fNamesElems);
                if (HTMLScanner.this.fDocumentHandler != null && HTMLScanner.this.fElementCount >= HTMLScanner.this.fElementDepth) {
                    this.fQName.setValues((String)null, ename, ename, (String)null);
                    HTMLScanner.this.fEndLineNumber = HTMLScanner.this.fCurrentEntity.getLineNumber();
                    HTMLScanner.this.fEndColumnNumber = HTMLScanner.this.fCurrentEntity.getColumnNumber();
                    HTMLScanner.this.fEndCharacterOffset = HTMLScanner.this.fCurrentEntity.getCharacterOffset();
                    HTMLScanner.this.fDocumentHandler.endElement(this.fQName, HTMLScanner.this.locationAugs());
                }
            }
        }
        
        private boolean isEnded(final String ename) {
            final String content = new String(HTMLScanner.this.fCurrentEntity.buffer, HTMLScanner.this.fCurrentEntity.offset, HTMLScanner.this.fCurrentEntity.length - HTMLScanner.this.fCurrentEntity.offset);
            return content.toLowerCase().indexOf("</" + ename.toLowerCase() + ">") != -1;
        }
    }
    
    public class SpecialScanner implements Scanner
    {
        protected String fElementName;
        protected boolean fStyle;
        protected boolean fTextarea;
        protected boolean fTitle;
        private final QName fQName;
        private final XMLStringBuffer fStringBuffer;
        
        public SpecialScanner() {
            this.fQName = new QName();
            this.fStringBuffer = new XMLStringBuffer();
        }
        
        public Scanner setElementName(final String ename) {
            this.fElementName = ename;
            this.fStyle = this.fElementName.equalsIgnoreCase("STYLE");
            this.fTextarea = this.fElementName.equalsIgnoreCase("TEXTAREA");
            this.fTitle = this.fElementName.equalsIgnoreCase("TITLE");
            return this;
        }
        
        @Override
        public boolean scan(final boolean complete) throws IOException {
            boolean next;
            do {
                try {
                    next = false;
                    switch (HTMLScanner.this.fScannerState) {
                        case 0: {
                            HTMLScanner.this.fBeginLineNumber = HTMLScanner.this.fCurrentEntity.getLineNumber();
                            HTMLScanner.this.fBeginColumnNumber = HTMLScanner.this.fCurrentEntity.getColumnNumber();
                            HTMLScanner.this.fBeginCharacterOffset = HTMLScanner.this.fCurrentEntity.getCharacterOffset();
                            final int c = HTMLScanner.this.fCurrentEntity.read();
                            if (c == 60) {
                                HTMLScanner.this.setScannerState((short)1);
                                break;
                            }
                            if (c == 38) {
                                if (this.fTextarea || this.fTitle) {
                                    HTMLScanner.this.scanEntityRef(this.fStringBuffer, true);
                                    break;
                                }
                                this.fStringBuffer.clear();
                                this.fStringBuffer.append('&');
                            }
                            else {
                                if (c == -1) {
                                    if (HTMLScanner.this.fReportErrors) {
                                        HTMLScanner.this.fErrorReporter.reportError("HTML1007", null);
                                    }
                                    throw new EOFException();
                                }
                                HTMLScanner.this.fCurrentEntity.rewind();
                                this.fStringBuffer.clear();
                            }
                            this.scanCharacters(this.fStringBuffer, -1);
                            break;
                        }
                        case 1: {
                            final int delimiter = -1;
                            int c2 = HTMLScanner.this.fCurrentEntity.read();
                            if (c2 == 47) {
                                String ename = HTMLScanner.this.scanName(true);
                                if (ename != null) {
                                    Label_0515: {
                                        if (ename.equalsIgnoreCase(this.fElementName)) {
                                            do {
                                                c2 = HTMLScanner.this.fCurrentEntity.read();
                                                if (c2 == 62) {
                                                    if (c2 == 62) {
                                                        ename = HTMLScanner.modifyName(ename, HTMLScanner.this.fNamesElems);
                                                        if (HTMLScanner.this.fDocumentHandler != null && HTMLScanner.this.fElementCount >= HTMLScanner.this.fElementDepth) {
                                                            this.fQName.setValues((String)null, ename, ename, (String)null);
                                                            HTMLScanner.this.fEndLineNumber = HTMLScanner.this.fCurrentEntity.getLineNumber();
                                                            HTMLScanner.this.fEndColumnNumber = HTMLScanner.this.fCurrentEntity.getColumnNumber();
                                                            HTMLScanner.this.fEndCharacterOffset = HTMLScanner.this.fCurrentEntity.getCharacterOffset();
                                                            HTMLScanner.this.fDocumentHandler.endElement(this.fQName, HTMLScanner.this.locationAugs());
                                                        }
                                                        HTMLScanner.this.setScanner(HTMLScanner.this.fContentScanner);
                                                        HTMLScanner.this.setScannerState((short)0);
                                                        return true;
                                                    }
                                                    HTMLScanner.this.fCurrentEntity.rewind();
                                                    break Label_0515;
                                                }
                                            } while (c2 != -1);
                                            if (HTMLScanner.this.fReportErrors) {
                                                HTMLScanner.this.fErrorReporter.reportError("HTML1007", null);
                                            }
                                            throw new EOFException();
                                        }
                                    }
                                    this.fStringBuffer.clear();
                                    this.fStringBuffer.append("</");
                                    this.fStringBuffer.append(ename);
                                }
                                else {
                                    this.fStringBuffer.clear();
                                    this.fStringBuffer.append("</");
                                }
                            }
                            else {
                                this.fStringBuffer.clear();
                                this.fStringBuffer.append('<');
                                HTMLScanner.this.appendChar(this.fStringBuffer, c2);
                            }
                            this.scanCharacters(this.fStringBuffer, delimiter);
                            HTMLScanner.this.setScannerState((short)0);
                            break;
                        }
                    }
                }
                catch (final EOFException e) {
                    HTMLScanner.this.setScanner(HTMLScanner.this.fContentScanner);
                    if (HTMLScanner.this.fCurrentEntityStack.empty()) {
                        HTMLScanner.this.setScannerState((short)11);
                    }
                    else {
                        HTMLScanner.this.fCurrentEntity = HTMLScanner.this.fCurrentEntityStack.pop();
                        HTMLScanner.this.setScannerState((short)0);
                    }
                    return true;
                }
            } while (next || complete);
            return true;
        }
        
        protected void scanCharacters(final XMLStringBuffer buffer, final int delimiter) throws IOException {
            int c;
            while (true) {
                c = HTMLScanner.this.fCurrentEntity.read();
                if (c == -1 || c == 60 || c == 38) {
                    break;
                }
                if (c == 13 || c == 10) {
                    HTMLScanner.this.fCurrentEntity.rewind();
                    for (int newlines = HTMLScanner.this.skipNewlines(), i = 0; i < newlines; ++i) {
                        buffer.append('\n');
                    }
                }
                else {
                    HTMLScanner.this.appendChar(buffer, c);
                    if (c != 10) {
                        continue;
                    }
                    HTMLScanner.this.fCurrentEntity.incLine();
                }
            }
            if (c != -1) {
                HTMLScanner.this.fCurrentEntity.rewind();
            }
            if (this.fStyle) {
                if (HTMLScanner.this.fStyleStripCommentDelims) {
                    HTMLScanner.reduceToContent(buffer, "<!--", "-->");
                }
                if (HTMLScanner.this.fStyleStripCDATADelims) {
                    HTMLScanner.reduceToContent(buffer, "<![CDATA[", "]]>");
                }
            }
            if (buffer.length > 0 && HTMLScanner.this.fDocumentHandler != null && HTMLScanner.this.fElementCount >= HTMLScanner.this.fElementDepth) {
                HTMLScanner.this.fEndLineNumber = HTMLScanner.this.fCurrentEntity.getLineNumber();
                HTMLScanner.this.fEndColumnNumber = HTMLScanner.this.fCurrentEntity.getColumnNumber();
                HTMLScanner.this.fEndCharacterOffset = HTMLScanner.this.fCurrentEntity.getCharacterOffset();
                HTMLScanner.this.fDocumentHandler.characters((XMLString)buffer, HTMLScanner.this.locationAugs());
            }
        }
    }
    
    public static class PlaybackInputStream extends FilterInputStream
    {
        private static final boolean DEBUG_PLAYBACK = false;
        protected boolean fPlayback;
        protected boolean fCleared;
        protected boolean fDetected;
        protected byte[] fByteBuffer;
        protected int fByteOffset;
        protected int fByteLength;
        public int fPushbackOffset;
        public int fPushbackLength;
        
        public PlaybackInputStream(final InputStream in) {
            super(in);
            this.fPlayback = false;
            this.fCleared = false;
            this.fDetected = false;
            this.fByteBuffer = new byte[1024];
            this.fByteOffset = 0;
            this.fByteLength = 0;
            this.fPushbackOffset = 0;
            this.fPushbackLength = 0;
        }
        
        public void detectEncoding(final String[] encodings) throws IOException {
            if (this.fDetected) {
                throw new IOException("Should not detect encoding twice.");
            }
            this.fDetected = true;
            final int b1 = this.read();
            if (b1 == -1) {
                return;
            }
            final int b2 = this.read();
            if (b2 == -1) {
                this.fPushbackLength = 1;
                return;
            }
            if (b1 == 239 && b2 == 187) {
                final int b3 = this.read();
                if (b3 == 191) {
                    this.fPushbackOffset = 3;
                    encodings[0] = "UTF-8";
                    encodings[1] = "UTF8";
                    return;
                }
                this.fPushbackLength = 3;
            }
            if (b1 == 255 && b2 == 254) {
                encodings[0] = "UTF-16";
                encodings[1] = "UnicodeLittleUnmarked";
                return;
            }
            if (b1 == 254 && b2 == 255) {
                encodings[0] = "UTF-16";
                encodings[1] = "UnicodeBigUnmarked";
                return;
            }
            this.fPushbackLength = 2;
        }
        
        public void playback() {
            this.fPlayback = true;
        }
        
        public void clear() {
            if (!this.fPlayback) {
                this.fCleared = true;
                this.fByteBuffer = null;
            }
        }
        
        @Override
        public int read() throws IOException {
            if (this.fPushbackOffset < this.fPushbackLength) {
                return this.fByteBuffer[this.fPushbackOffset++];
            }
            if (this.fCleared) {
                return this.in.read();
            }
            if (this.fPlayback) {
                final int c = this.fByteBuffer[this.fByteOffset++];
                if (this.fByteOffset == this.fByteLength) {
                    this.fCleared = true;
                    this.fByteBuffer = null;
                }
                return c;
            }
            final int c = this.in.read();
            if (c != -1) {
                if (this.fByteLength == this.fByteBuffer.length) {
                    final byte[] newarray = new byte[this.fByteLength + 1024];
                    System.arraycopy(this.fByteBuffer, 0, newarray, 0, this.fByteLength);
                    this.fByteBuffer = newarray;
                }
                this.fByteBuffer[this.fByteLength++] = (byte)c;
            }
            return c;
        }
        
        @Override
        public int read(final byte[] array) throws IOException {
            return this.read(array, 0, array.length);
        }
        
        @Override
        public int read(final byte[] array, final int offset, int length) throws IOException {
            if (this.fPushbackOffset < this.fPushbackLength) {
                int count = this.fPushbackLength - this.fPushbackOffset;
                if (count > length) {
                    count = length;
                }
                System.arraycopy(this.fByteBuffer, this.fPushbackOffset, array, offset, count);
                this.fPushbackOffset += count;
                return count;
            }
            if (this.fCleared) {
                return this.in.read(array, offset, length);
            }
            if (this.fPlayback) {
                if (this.fByteOffset + length > this.fByteLength) {
                    length = this.fByteLength - this.fByteOffset;
                }
                System.arraycopy(this.fByteBuffer, this.fByteOffset, array, offset, length);
                this.fByteOffset += length;
                if (this.fByteOffset == this.fByteLength) {
                    this.fCleared = true;
                    this.fByteBuffer = null;
                }
                return length;
            }
            int count = this.in.read(array, offset, length);
            if (count != -1) {
                if (this.fByteLength + count > this.fByteBuffer.length) {
                    final byte[] newarray = new byte[this.fByteLength + count + 512];
                    System.arraycopy(this.fByteBuffer, 0, newarray, 0, this.fByteLength);
                    this.fByteBuffer = newarray;
                }
                System.arraycopy(array, offset, this.fByteBuffer, this.fByteLength, count);
                this.fByteLength += count;
            }
            return count;
        }
    }
    
    protected static class LocationItem implements HTMLEventInfo, Cloneable
    {
        protected int fBeginLineNumber;
        protected int fBeginColumnNumber;
        protected int fBeginCharacterOffset;
        protected int fEndLineNumber;
        protected int fEndColumnNumber;
        protected int fEndCharacterOffset;
        
        public LocationItem() {
        }
        
        LocationItem(final LocationItem other) {
            this.setValues(other.fBeginLineNumber, other.fBeginColumnNumber, other.fBeginCharacterOffset, other.fEndLineNumber, other.fEndColumnNumber, other.fEndCharacterOffset);
        }
        
        public void setValues(final int beginLine, final int beginColumn, final int beginOffset, final int endLine, final int endColumn, final int endOffset) {
            this.fBeginLineNumber = beginLine;
            this.fBeginColumnNumber = beginColumn;
            this.fBeginCharacterOffset = beginOffset;
            this.fEndLineNumber = endLine;
            this.fEndColumnNumber = endColumn;
            this.fEndCharacterOffset = endOffset;
        }
        
        @Override
        public int getBeginLineNumber() {
            return this.fBeginLineNumber;
        }
        
        @Override
        public int getBeginColumnNumber() {
            return this.fBeginColumnNumber;
        }
        
        @Override
        public int getBeginCharacterOffset() {
            return this.fBeginCharacterOffset;
        }
        
        @Override
        public int getEndLineNumber() {
            return this.fEndLineNumber;
        }
        
        @Override
        public int getEndColumnNumber() {
            return this.fEndColumnNumber;
        }
        
        @Override
        public int getEndCharacterOffset() {
            return this.fEndCharacterOffset;
        }
        
        @Override
        public boolean isSynthesized() {
            return false;
        }
        
        @Override
        public String toString() {
            final StringBuffer str = new StringBuffer();
            str.append(this.fBeginLineNumber);
            str.append(':');
            str.append(this.fBeginColumnNumber);
            str.append(':');
            str.append(this.fBeginCharacterOffset);
            str.append(':');
            str.append(this.fEndLineNumber);
            str.append(':');
            str.append(this.fEndColumnNumber);
            str.append(':');
            str.append(this.fEndCharacterOffset);
            return str.toString();
        }
    }
    
    public interface Scanner
    {
        boolean scan(final boolean p0) throws IOException;
    }
}
