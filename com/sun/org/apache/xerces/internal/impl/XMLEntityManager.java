package com.sun.org.apache.xerces.internal.impl;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import com.sun.org.apache.xerces.internal.util.EncodingMap;
import com.sun.org.apache.xerces.internal.util.XMLChar;
import com.sun.org.apache.xerces.internal.impl.io.UCSReader;
import com.sun.org.apache.xerces.internal.impl.io.ASCIIReader;
import com.sun.org.apache.xerces.internal.impl.io.UTF8Reader;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.io.UnsupportedEncodingException;
import java.io.File;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityPropertyManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import java.io.EOFException;
import java.io.StringReader;
import com.sun.org.apache.xerces.internal.utils.SecuritySupport;
import com.sun.org.apache.xerces.internal.xni.XMLLocator;
import com.sun.xml.internal.stream.StaxXMLInputSource;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import java.util.Iterator;
import java.net.URLConnection;
import java.io.InputStream;
import java.util.Locale;
import com.sun.org.apache.xerces.internal.util.HTTPInputSource;
import java.net.HttpURLConnection;
import java.net.URL;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import java.io.IOException;
import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;
import com.sun.org.apache.xerces.internal.util.XMLEntityDescriptionImpl;
import com.sun.org.apache.xerces.internal.util.AugmentationsImpl;
import java.util.HashMap;
import com.sun.org.apache.xerces.internal.util.URI;
import java.io.Reader;
import com.sun.org.apache.xerces.internal.xni.Augmentations;
import com.sun.org.apache.xerces.internal.util.XMLResourceIdentifierImpl;
import com.sun.xml.internal.stream.XMLEntityStorage;
import java.util.Stack;
import com.sun.xml.internal.stream.Entity;
import java.util.Map;
import com.sun.org.apache.xerces.internal.utils.XMLLimitAnalyzer;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityManager;
import com.sun.org.apache.xerces.internal.impl.validation.ValidationManager;
import com.sun.xml.internal.stream.StaxEntityResolverWrapper;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.xni.parser.XMLEntityResolver;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponent;

public class XMLEntityManager implements XMLComponent, XMLEntityResolver
{
    public static final int DEFAULT_BUFFER_SIZE = 8192;
    public static final int DEFAULT_XMLDECL_BUFFER_SIZE = 64;
    public static final int DEFAULT_INTERNAL_BUFFER_SIZE = 1024;
    protected static final String VALIDATION = "http://xml.org/sax/features/validation";
    protected boolean fStrictURI;
    protected static final String EXTERNAL_GENERAL_ENTITIES = "http://xml.org/sax/features/external-general-entities";
    protected static final String EXTERNAL_PARAMETER_ENTITIES = "http://xml.org/sax/features/external-parameter-entities";
    protected static final String ALLOW_JAVA_ENCODINGS = "http://apache.org/xml/features/allow-java-encodings";
    protected static final String WARN_ON_DUPLICATE_ENTITYDEF = "http://apache.org/xml/features/warn-on-duplicate-entitydef";
    protected static final String LOAD_EXTERNAL_DTD = "http://apache.org/xml/features/nonvalidating/load-external-dtd";
    protected static final String SYMBOL_TABLE = "http://apache.org/xml/properties/internal/symbol-table";
    protected static final String ERROR_REPORTER = "http://apache.org/xml/properties/internal/error-reporter";
    protected static final String STANDARD_URI_CONFORMANT = "http://apache.org/xml/features/standard-uri-conformant";
    protected static final String ENTITY_RESOLVER = "http://apache.org/xml/properties/internal/entity-resolver";
    protected static final String STAX_ENTITY_RESOLVER = "http://apache.org/xml/properties/internal/stax-entity-resolver";
    protected static final String VALIDATION_MANAGER = "http://apache.org/xml/properties/internal/validation-manager";
    protected static final String BUFFER_SIZE = "http://apache.org/xml/properties/input-buffer-size";
    protected static final String SECURITY_MANAGER = "http://apache.org/xml/properties/security-manager";
    protected static final String PARSER_SETTINGS = "http://apache.org/xml/features/internal/parser-settings";
    private static final String XML_SECURITY_PROPERTY_MANAGER = "http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager";
    static final String EXTERNAL_ACCESS_DEFAULT = "all";
    private static final String[] RECOGNIZED_FEATURES;
    private static final Boolean[] FEATURE_DEFAULTS;
    private static final String[] RECOGNIZED_PROPERTIES;
    private static final Object[] PROPERTY_DEFAULTS;
    private static final String XMLEntity;
    private static final String DTDEntity;
    private static final boolean DEBUG_BUFFER = false;
    protected boolean fWarnDuplicateEntityDef;
    private static final boolean DEBUG_ENTITIES = false;
    private static final boolean DEBUG_ENCODINGS = false;
    private static final boolean DEBUG_RESOLVER = false;
    protected boolean fValidation;
    protected boolean fExternalGeneralEntities;
    protected boolean fExternalParameterEntities;
    protected boolean fAllowJavaEncodings;
    protected boolean fLoadExternalDTD;
    protected SymbolTable fSymbolTable;
    protected XMLErrorReporter fErrorReporter;
    protected XMLEntityResolver fEntityResolver;
    protected StaxEntityResolverWrapper fStaxEntityResolver;
    protected PropertyManager fPropertyManager;
    boolean fSupportDTD;
    boolean fReplaceEntityReferences;
    boolean fSupportExternalEntities;
    protected String fAccessExternalDTD;
    protected ValidationManager fValidationManager;
    protected int fBufferSize;
    protected XMLSecurityManager fSecurityManager;
    protected XMLLimitAnalyzer fLimitAnalyzer;
    protected int entityExpansionIndex;
    protected boolean fStandalone;
    protected boolean fInExternalSubset;
    protected XMLEntityHandler fEntityHandler;
    protected XMLEntityScanner fEntityScanner;
    protected XMLEntityScanner fXML10EntityScanner;
    protected XMLEntityScanner fXML11EntityScanner;
    protected int fEntityExpansionCount;
    protected Map<String, Entity> fEntities;
    protected Stack<Entity> fEntityStack;
    protected Entity.ScannedEntity fCurrentEntity;
    boolean fISCreatedByResolver;
    protected XMLEntityStorage fEntityStorage;
    protected final Object[] defaultEncoding;
    private final XMLResourceIdentifierImpl fResourceIdentifier;
    private final Augmentations fEntityAugs;
    private CharacterBufferPool fBufferPool;
    protected Stack<Reader> fReaderStack;
    private static String gUserDir;
    private static URI gUserDirURI;
    private static boolean[] gNeedEscaping;
    private static char[] gAfterEscaping1;
    private static char[] gAfterEscaping2;
    private static char[] gHexChs;
    
    public XMLEntityManager() {
        this.fAllowJavaEncodings = true;
        this.fLoadExternalDTD = true;
        this.fSupportDTD = true;
        this.fReplaceEntityReferences = true;
        this.fSupportExternalEntities = true;
        this.fAccessExternalDTD = "all";
        this.fBufferSize = 8192;
        this.fSecurityManager = null;
        this.fLimitAnalyzer = null;
        this.fInExternalSubset = false;
        this.fEntityExpansionCount = 0;
        this.fEntities = new HashMap<String, Entity>();
        this.fEntityStack = new Stack<Entity>();
        this.fCurrentEntity = null;
        this.fISCreatedByResolver = false;
        this.defaultEncoding = new Object[] { "UTF-8", null };
        this.fResourceIdentifier = new XMLResourceIdentifierImpl();
        this.fEntityAugs = new AugmentationsImpl();
        this.fBufferPool = new CharacterBufferPool(this.fBufferSize, 1024);
        this.fReaderStack = new Stack<Reader>();
        this.fSecurityManager = new XMLSecurityManager(true);
        this.fEntityStorage = new XMLEntityStorage(this);
        this.setScannerVersion((short)1);
    }
    
    public XMLEntityManager(final PropertyManager propertyManager) {
        this.fAllowJavaEncodings = true;
        this.fLoadExternalDTD = true;
        this.fSupportDTD = true;
        this.fReplaceEntityReferences = true;
        this.fSupportExternalEntities = true;
        this.fAccessExternalDTD = "all";
        this.fBufferSize = 8192;
        this.fSecurityManager = null;
        this.fLimitAnalyzer = null;
        this.fInExternalSubset = false;
        this.fEntityExpansionCount = 0;
        this.fEntities = new HashMap<String, Entity>();
        this.fEntityStack = new Stack<Entity>();
        this.fCurrentEntity = null;
        this.fISCreatedByResolver = false;
        this.defaultEncoding = new Object[] { "UTF-8", null };
        this.fResourceIdentifier = new XMLResourceIdentifierImpl();
        this.fEntityAugs = new AugmentationsImpl();
        this.fBufferPool = new CharacterBufferPool(this.fBufferSize, 1024);
        this.fReaderStack = new Stack<Reader>();
        this.fPropertyManager = propertyManager;
        this.fEntityStorage = new XMLEntityStorage(this);
        this.fEntityScanner = new XMLEntityScanner(propertyManager, this);
        this.reset(propertyManager);
    }
    
    public void addInternalEntity(final String name, final String text) {
        if (!this.fEntities.containsKey(name)) {
            final Entity entity = new Entity.InternalEntity(name, text, this.fInExternalSubset);
            this.fEntities.put(name, entity);
        }
        else if (this.fWarnDuplicateEntityDef) {
            this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_DUPLICATE_ENTITY_DEFINITION", new Object[] { name }, (short)0);
        }
    }
    
    public void addExternalEntity(final String name, final String publicId, final String literalSystemId, String baseSystemId) throws IOException {
        if (!this.fEntities.containsKey(name)) {
            if (baseSystemId == null) {
                final int size = this.fEntityStack.size();
                if (size == 0 && this.fCurrentEntity != null && this.fCurrentEntity.entityLocation != null) {
                    baseSystemId = this.fCurrentEntity.entityLocation.getExpandedSystemId();
                }
                for (int i = size - 1; i >= 0; --i) {
                    final Entity.ScannedEntity externalEntity = this.fEntityStack.elementAt(i);
                    if (externalEntity.entityLocation != null && externalEntity.entityLocation.getExpandedSystemId() != null) {
                        baseSystemId = externalEntity.entityLocation.getExpandedSystemId();
                        break;
                    }
                }
            }
            final Entity entity = new Entity.ExternalEntity(name, new XMLEntityDescriptionImpl(name, publicId, literalSystemId, baseSystemId, expandSystemId(literalSystemId, baseSystemId, false)), null, this.fInExternalSubset);
            this.fEntities.put(name, entity);
        }
        else if (this.fWarnDuplicateEntityDef) {
            this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_DUPLICATE_ENTITY_DEFINITION", new Object[] { name }, (short)0);
        }
    }
    
    public void addUnparsedEntity(final String name, final String publicId, final String systemId, final String baseSystemId, final String notation) {
        if (!this.fEntities.containsKey(name)) {
            final Entity.ExternalEntity entity = new Entity.ExternalEntity(name, new XMLEntityDescriptionImpl(name, publicId, systemId, baseSystemId, null), notation, this.fInExternalSubset);
            this.fEntities.put(name, entity);
        }
        else if (this.fWarnDuplicateEntityDef) {
            this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_DUPLICATE_ENTITY_DEFINITION", new Object[] { name }, (short)0);
        }
    }
    
    public XMLEntityStorage getEntityStore() {
        return this.fEntityStorage;
    }
    
    public XMLEntityScanner getEntityScanner() {
        if (this.fEntityScanner == null) {
            if (this.fXML10EntityScanner == null) {
                this.fXML10EntityScanner = new XMLEntityScanner();
            }
            this.fXML10EntityScanner.reset(this.fSymbolTable, this, this.fErrorReporter);
            this.fEntityScanner = this.fXML10EntityScanner;
        }
        return this.fEntityScanner;
    }
    
    public void setScannerVersion(final short version) {
        if (version == 1) {
            if (this.fXML10EntityScanner == null) {
                this.fXML10EntityScanner = new XMLEntityScanner();
            }
            this.fXML10EntityScanner.reset(this.fSymbolTable, this, this.fErrorReporter);
            (this.fEntityScanner = this.fXML10EntityScanner).setCurrentEntity(this.fCurrentEntity);
        }
        else {
            if (this.fXML11EntityScanner == null) {
                this.fXML11EntityScanner = new XML11EntityScanner();
            }
            this.fXML11EntityScanner.reset(this.fSymbolTable, this, this.fErrorReporter);
            (this.fEntityScanner = this.fXML11EntityScanner).setCurrentEntity(this.fCurrentEntity);
        }
    }
    
    public String setupCurrentEntity(final boolean reference, final String name, final XMLInputSource xmlInputSource, final boolean literal, final boolean isExternal) throws IOException, XNIException {
        final String publicId = xmlInputSource.getPublicId();
        String literalSystemId = xmlInputSource.getSystemId();
        String baseSystemId = xmlInputSource.getBaseSystemId();
        String encoding = xmlInputSource.getEncoding();
        final boolean encodingExternallySpecified = encoding != null;
        Boolean isBigEndian = null;
        InputStream stream = null;
        Reader reader = xmlInputSource.getCharacterStream();
        String expandedSystemId = expandSystemId(literalSystemId, baseSystemId, this.fStrictURI);
        if (baseSystemId == null) {
            baseSystemId = expandedSystemId;
        }
        if (reader == null) {
            stream = xmlInputSource.getByteStream();
            if (stream == null) {
                final URL location = new URL(expandedSystemId);
                final URLConnection connect = location.openConnection();
                if (!(connect instanceof HttpURLConnection)) {
                    stream = connect.getInputStream();
                }
                else {
                    boolean followRedirects = true;
                    if (xmlInputSource instanceof HTTPInputSource) {
                        final HttpURLConnection urlConnection = (HttpURLConnection)connect;
                        final HTTPInputSource httpInputSource = (HTTPInputSource)xmlInputSource;
                        final Iterator<Map.Entry<String, String>> propIter = httpInputSource.getHTTPRequestProperties();
                        while (propIter.hasNext()) {
                            final Map.Entry<String, String> entry = propIter.next();
                            urlConnection.setRequestProperty(entry.getKey(), entry.getValue());
                        }
                        followRedirects = httpInputSource.getFollowHTTPRedirects();
                        if (!followRedirects) {
                            setInstanceFollowRedirects(urlConnection, followRedirects);
                        }
                    }
                    stream = connect.getInputStream();
                    if (followRedirects) {
                        final String redirect = connect.getURL().toString();
                        if (!redirect.equals(expandedSystemId)) {
                            literalSystemId = redirect;
                            expandedSystemId = redirect;
                        }
                    }
                }
            }
            stream = new RewindableInputStream(stream);
            if (encoding == null) {
                final byte[] b4 = new byte[4];
                int count;
                for (count = 0; count < 4; ++count) {
                    b4[count] = (byte)stream.read();
                }
                if (count == 4) {
                    final Object[] encodingDesc = this.getEncodingName(b4, count);
                    encoding = (String)encodingDesc[0];
                    isBigEndian = (Boolean)encodingDesc[1];
                    stream.reset();
                    if (count > 2 && encoding.equals("UTF-8")) {
                        final int b5 = b4[0] & 0xFF;
                        final int b6 = b4[1] & 0xFF;
                        final int b7 = b4[2] & 0xFF;
                        if (b5 == 239 && b6 == 187 && b7 == 191) {
                            stream.skip(3L);
                        }
                    }
                    reader = this.createReader(stream, encoding, isBigEndian);
                }
                else {
                    reader = this.createReader(stream, encoding, isBigEndian);
                }
            }
            else {
                encoding = encoding.toUpperCase(Locale.ENGLISH);
                if (encoding.equals("UTF-8")) {
                    final int[] b8 = new int[3];
                    int count;
                    for (count = 0; count < 3; ++count) {
                        b8[count] = stream.read();
                        if (b8[count] == -1) {
                            break;
                        }
                    }
                    if (count == 3) {
                        if (b8[0] != 239 || b8[1] != 187 || b8[2] != 191) {
                            stream.reset();
                        }
                    }
                    else {
                        stream.reset();
                    }
                }
                else if (encoding.equals("UTF-16")) {
                    final int[] b9 = new int[4];
                    int count;
                    for (count = 0; count < 4; ++count) {
                        b9[count] = stream.read();
                        if (b9[count] == -1) {
                            break;
                        }
                    }
                    stream.reset();
                    String utf16Encoding = "UTF-16";
                    if (count >= 2) {
                        final int b5 = b9[0];
                        final int b6 = b9[1];
                        if (b5 == 254 && b6 == 255) {
                            utf16Encoding = "UTF-16BE";
                            isBigEndian = Boolean.TRUE;
                        }
                        else if (b5 == 255 && b6 == 254) {
                            utf16Encoding = "UTF-16LE";
                            isBigEndian = Boolean.FALSE;
                        }
                        else if (count == 4) {
                            final int b7 = b9[2];
                            final int b10 = b9[3];
                            if (b5 == 0 && b6 == 60 && b7 == 0 && b10 == 63) {
                                utf16Encoding = "UTF-16BE";
                                isBigEndian = Boolean.TRUE;
                            }
                            if (b5 == 60 && b6 == 0 && b7 == 63 && b10 == 0) {
                                utf16Encoding = "UTF-16LE";
                                isBigEndian = Boolean.FALSE;
                            }
                        }
                    }
                    reader = this.createReader(stream, utf16Encoding, isBigEndian);
                }
                else if (encoding.equals("ISO-10646-UCS-4")) {
                    final int[] b9 = new int[4];
                    int count;
                    for (count = 0; count < 4; ++count) {
                        b9[count] = stream.read();
                        if (b9[count] == -1) {
                            break;
                        }
                    }
                    stream.reset();
                    if (count == 4) {
                        if (b9[0] == 0 && b9[1] == 0 && b9[2] == 0 && b9[3] == 60) {
                            isBigEndian = Boolean.TRUE;
                        }
                        else if (b9[0] == 60 && b9[1] == 0 && b9[2] == 0 && b9[3] == 0) {
                            isBigEndian = Boolean.FALSE;
                        }
                    }
                }
                else if (encoding.equals("ISO-10646-UCS-2")) {
                    final int[] b9 = new int[4];
                    int count;
                    for (count = 0; count < 4; ++count) {
                        b9[count] = stream.read();
                        if (b9[count] == -1) {
                            break;
                        }
                    }
                    stream.reset();
                    if (count == 4) {
                        if (b9[0] == 0 && b9[1] == 60 && b9[2] == 0 && b9[3] == 63) {
                            isBigEndian = Boolean.TRUE;
                        }
                        else if (b9[0] == 60 && b9[1] == 0 && b9[2] == 63 && b9[3] == 0) {
                            isBigEndian = Boolean.FALSE;
                        }
                    }
                }
                reader = this.createReader(stream, encoding, isBigEndian);
            }
        }
        this.fReaderStack.push(reader);
        if (this.fCurrentEntity != null) {
            this.fEntityStack.push(this.fCurrentEntity);
        }
        (this.fCurrentEntity = new Entity.ScannedEntity(reference, name, new XMLResourceIdentifierImpl(publicId, literalSystemId, baseSystemId, expandedSystemId), stream, reader, encoding, literal, encodingExternallySpecified, isExternal)).setEncodingExternallySpecified(encodingExternallySpecified);
        this.fEntityScanner.setCurrentEntity(this.fCurrentEntity);
        this.fResourceIdentifier.setValues(publicId, literalSystemId, baseSystemId, expandedSystemId);
        if (this.fLimitAnalyzer != null) {
            this.fLimitAnalyzer.startEntity(name);
        }
        return encoding;
    }
    
    public boolean isExternalEntity(final String entityName) {
        final Entity entity = this.fEntities.get(entityName);
        return entity != null && entity.isExternal();
    }
    
    public boolean isEntityDeclInExternalSubset(final String entityName) {
        final Entity entity = this.fEntities.get(entityName);
        return entity != null && entity.isEntityDeclInExternalSubset();
    }
    
    public void setStandalone(final boolean standalone) {
        this.fStandalone = standalone;
    }
    
    public boolean isStandalone() {
        return this.fStandalone;
    }
    
    public boolean isDeclaredEntity(final String entityName) {
        final Entity entity = this.fEntities.get(entityName);
        return entity != null;
    }
    
    public boolean isUnparsedEntity(final String entityName) {
        final Entity entity = this.fEntities.get(entityName);
        return entity != null && entity.isUnparsed();
    }
    
    public XMLResourceIdentifier getCurrentResourceIdentifier() {
        return this.fResourceIdentifier;
    }
    
    public void setEntityHandler(final XMLEntityHandler entityHandler) {
        this.fEntityHandler = entityHandler;
    }
    
    public StaxXMLInputSource resolveEntityAsPerStax(final XMLResourceIdentifier resourceIdentifier) throws IOException {
        if (resourceIdentifier == null) {
            return null;
        }
        final String publicId = resourceIdentifier.getPublicId();
        final String literalSystemId = resourceIdentifier.getLiteralSystemId();
        String baseSystemId = resourceIdentifier.getBaseSystemId();
        String expandedSystemId = resourceIdentifier.getExpandedSystemId();
        boolean needExpand = expandedSystemId == null;
        if (baseSystemId == null && this.fCurrentEntity != null && this.fCurrentEntity.entityLocation != null) {
            baseSystemId = this.fCurrentEntity.entityLocation.getExpandedSystemId();
            if (baseSystemId != null) {
                needExpand = true;
            }
        }
        if (needExpand) {
            expandedSystemId = expandSystemId(literalSystemId, baseSystemId, false);
        }
        StaxXMLInputSource staxInputSource = null;
        XMLInputSource xmlInputSource = null;
        XMLResourceIdentifierImpl ri = null;
        if (resourceIdentifier instanceof XMLResourceIdentifierImpl) {
            ri = (XMLResourceIdentifierImpl)resourceIdentifier;
        }
        else {
            this.fResourceIdentifier.clear();
            ri = this.fResourceIdentifier;
        }
        ri.setValues(publicId, literalSystemId, baseSystemId, expandedSystemId);
        this.fISCreatedByResolver = false;
        if (this.fStaxEntityResolver != null) {
            staxInputSource = this.fStaxEntityResolver.resolveEntity(ri);
            if (staxInputSource != null) {
                this.fISCreatedByResolver = true;
            }
        }
        if (this.fEntityResolver != null) {
            xmlInputSource = this.fEntityResolver.resolveEntity(ri);
            if (xmlInputSource != null) {
                this.fISCreatedByResolver = true;
            }
        }
        if (xmlInputSource != null) {
            staxInputSource = new StaxXMLInputSource(xmlInputSource, this.fISCreatedByResolver);
        }
        if (staxInputSource == null) {
            staxInputSource = new StaxXMLInputSource(new XMLInputSource(publicId, literalSystemId, baseSystemId));
        }
        else if (staxInputSource.hasXMLStreamOrXMLEventReader()) {}
        return staxInputSource;
    }
    
    @Override
    public XMLInputSource resolveEntity(final XMLResourceIdentifier resourceIdentifier) throws IOException, XNIException {
        if (resourceIdentifier == null) {
            return null;
        }
        final String publicId = resourceIdentifier.getPublicId();
        final String literalSystemId = resourceIdentifier.getLiteralSystemId();
        String baseSystemId = resourceIdentifier.getBaseSystemId();
        String expandedSystemId = resourceIdentifier.getExpandedSystemId();
        boolean needExpand = expandedSystemId == null;
        if (baseSystemId == null && this.fCurrentEntity != null && this.fCurrentEntity.entityLocation != null) {
            baseSystemId = this.fCurrentEntity.entityLocation.getExpandedSystemId();
            if (baseSystemId != null) {
                needExpand = true;
            }
        }
        if (needExpand) {
            expandedSystemId = expandSystemId(literalSystemId, baseSystemId, false);
        }
        XMLInputSource xmlInputSource = null;
        if (this.fEntityResolver != null) {
            resourceIdentifier.setBaseSystemId(baseSystemId);
            resourceIdentifier.setExpandedSystemId(expandedSystemId);
            xmlInputSource = this.fEntityResolver.resolveEntity(resourceIdentifier);
        }
        if (xmlInputSource == null) {
            xmlInputSource = new XMLInputSource(publicId, literalSystemId, baseSystemId);
        }
        return xmlInputSource;
    }
    
    public void startEntity(final boolean isGE, final String entityName, final boolean literal) throws IOException, XNIException {
        final Entity entity = this.fEntityStorage.getEntity(entityName);
        if (entity == null) {
            if (this.fEntityHandler != null) {
                final String encoding = null;
                this.fResourceIdentifier.clear();
                this.fEntityAugs.removeAllItems();
                this.fEntityAugs.putItem("ENTITY_SKIPPED", Boolean.TRUE);
                this.fEntityHandler.startEntity(entityName, this.fResourceIdentifier, encoding, this.fEntityAugs);
                this.fEntityAugs.removeAllItems();
                this.fEntityAugs.putItem("ENTITY_SKIPPED", Boolean.TRUE);
                this.fEntityHandler.endEntity(entityName, this.fEntityAugs);
            }
            return;
        }
        final boolean external = entity.isExternal();
        Entity.ExternalEntity externalEntity = null;
        String extLitSysId = null;
        String extBaseSysId = null;
        String expandedSystemId = null;
        if (external) {
            externalEntity = (Entity.ExternalEntity)entity;
            extLitSysId = ((externalEntity.entityLocation != null) ? externalEntity.entityLocation.getLiteralSystemId() : null);
            extBaseSysId = ((externalEntity.entityLocation != null) ? externalEntity.entityLocation.getBaseSystemId() : null);
            expandedSystemId = expandSystemId(extLitSysId, extBaseSysId);
            final boolean unparsed = entity.isUnparsed();
            final boolean parameter = entityName.startsWith("%");
            final boolean general = !parameter;
            if (unparsed || (general && !this.fExternalGeneralEntities) || (parameter && !this.fExternalParameterEntities) || !this.fSupportDTD || !this.fSupportExternalEntities) {
                if (this.fEntityHandler != null) {
                    this.fResourceIdentifier.clear();
                    final String encoding2 = null;
                    this.fResourceIdentifier.setValues((externalEntity.entityLocation != null) ? externalEntity.entityLocation.getPublicId() : null, extLitSysId, extBaseSysId, expandedSystemId);
                    this.fEntityAugs.removeAllItems();
                    this.fEntityAugs.putItem("ENTITY_SKIPPED", Boolean.TRUE);
                    this.fEntityHandler.startEntity(entityName, this.fResourceIdentifier, encoding2, this.fEntityAugs);
                    this.fEntityAugs.removeAllItems();
                    this.fEntityAugs.putItem("ENTITY_SKIPPED", Boolean.TRUE);
                    this.fEntityHandler.endEntity(entityName, this.fEntityAugs);
                }
                return;
            }
        }
        int i;
        for (int size = i = this.fEntityStack.size(); i >= 0; --i) {
            Entity activeEntity = (i == size) ? this.fCurrentEntity : this.fEntityStack.elementAt(i);
            if (activeEntity.name == entityName) {
                String path = entityName;
                for (int j = i + 1; j < size; ++j) {
                    activeEntity = this.fEntityStack.elementAt(j);
                    path = path + " -> " + activeEntity.name;
                }
                path = path + " -> " + this.fCurrentEntity.name;
                path = path + " -> " + entityName;
                this.fErrorReporter.reportError(this.getEntityScanner(), "http://www.w3.org/TR/1998/REC-xml-19980210", "RecursiveReference", new Object[] { entityName, path }, (short)2);
                if (this.fEntityHandler != null) {
                    this.fResourceIdentifier.clear();
                    final String encoding3 = null;
                    if (external) {
                        this.fResourceIdentifier.setValues((externalEntity.entityLocation != null) ? externalEntity.entityLocation.getPublicId() : null, extLitSysId, extBaseSysId, expandedSystemId);
                    }
                    this.fEntityAugs.removeAllItems();
                    this.fEntityAugs.putItem("ENTITY_SKIPPED", Boolean.TRUE);
                    this.fEntityHandler.startEntity(entityName, this.fResourceIdentifier, encoding3, this.fEntityAugs);
                    this.fEntityAugs.removeAllItems();
                    this.fEntityAugs.putItem("ENTITY_SKIPPED", Boolean.TRUE);
                    this.fEntityHandler.endEntity(entityName, this.fEntityAugs);
                }
                return;
            }
        }
        StaxXMLInputSource staxInputSource = null;
        XMLInputSource xmlInputSource = null;
        if (external) {
            staxInputSource = this.resolveEntityAsPerStax(externalEntity.entityLocation);
            xmlInputSource = staxInputSource.getXMLInputSource();
            if (!this.fISCreatedByResolver && this.fLoadExternalDTD) {
                final String accessError = SecuritySupport.checkAccess(expandedSystemId, this.fAccessExternalDTD, "all");
                if (accessError != null) {
                    this.fErrorReporter.reportError(this.getEntityScanner(), "http://www.w3.org/TR/1998/REC-xml-19980210", "AccessExternalEntity", new Object[] { SecuritySupport.sanitizePath(expandedSystemId), accessError }, (short)2);
                }
            }
        }
        else {
            final Entity.InternalEntity internalEntity = (Entity.InternalEntity)entity;
            final Reader reader = new StringReader(internalEntity.text);
            xmlInputSource = new XMLInputSource(null, null, null, reader, null);
        }
        this.startEntity(isGE, entityName, xmlInputSource, literal, external);
    }
    
    public void startDocumentEntity(final XMLInputSource xmlInputSource) throws IOException, XNIException {
        this.startEntity(false, XMLEntityManager.XMLEntity, xmlInputSource, false, true);
    }
    
    public void startDTDEntity(final XMLInputSource xmlInputSource) throws IOException, XNIException {
        this.startEntity(false, XMLEntityManager.DTDEntity, xmlInputSource, false, true);
    }
    
    public void startExternalSubset() {
        this.fInExternalSubset = true;
    }
    
    public void endExternalSubset() {
        this.fInExternalSubset = false;
    }
    
    public void startEntity(final boolean isGE, final String name, final XMLInputSource xmlInputSource, final boolean literal, final boolean isExternal) throws IOException, XNIException {
        final String encoding = this.setupCurrentEntity(isGE, name, xmlInputSource, literal, isExternal);
        ++this.fEntityExpansionCount;
        if (this.fLimitAnalyzer != null) {
            this.fLimitAnalyzer.addValue(this.entityExpansionIndex, name, 1);
        }
        if (this.fSecurityManager != null && this.fSecurityManager.isOverLimit(this.entityExpansionIndex, this.fLimitAnalyzer)) {
            this.fSecurityManager.debugPrint(this.fLimitAnalyzer);
            this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "EntityExpansionLimit", new Object[] { this.fSecurityManager.getLimitValueByIndex(this.entityExpansionIndex) }, (short)2);
            this.fEntityExpansionCount = 0;
        }
        if (this.fEntityHandler != null) {
            this.fEntityHandler.startEntity(name, this.fResourceIdentifier, encoding, null);
        }
    }
    
    public Entity.ScannedEntity getCurrentEntity() {
        return this.fCurrentEntity;
    }
    
    public Entity.ScannedEntity getTopLevelEntity() {
        return (Entity.ScannedEntity)(this.fEntityStack.empty() ? null : this.fEntityStack.elementAt(0));
    }
    
    public void closeReaders() {
        while (!this.fReaderStack.isEmpty()) {
            try {
                this.fReaderStack.pop().close();
            }
            catch (final IOException ex) {}
        }
    }
    
    public void endEntity() throws IOException, XNIException {
        final Entity.ScannedEntity entity = (this.fEntityStack.size() > 0) ? this.fEntityStack.pop() : null;
        if (this.fCurrentEntity != null) {
            try {
                if (this.fLimitAnalyzer != null) {
                    this.fLimitAnalyzer.endEntity(XMLSecurityManager.Limit.GENERAL_ENTITY_SIZE_LIMIT, this.fCurrentEntity.name);
                    if (this.fCurrentEntity.name.equals("[xml]")) {
                        this.fSecurityManager.debugPrint(this.fLimitAnalyzer);
                    }
                }
                this.fCurrentEntity.close();
            }
            catch (final IOException ex) {
                throw new XNIException(ex);
            }
        }
        if (!this.fReaderStack.isEmpty()) {
            this.fReaderStack.pop();
        }
        if (this.fEntityHandler != null) {
            if (entity == null) {
                this.fEntityAugs.removeAllItems();
                this.fEntityAugs.putItem("LAST_ENTITY", Boolean.TRUE);
                this.fEntityHandler.endEntity(this.fCurrentEntity.name, this.fEntityAugs);
                this.fEntityAugs.removeAllItems();
            }
            else {
                this.fEntityHandler.endEntity(this.fCurrentEntity.name, null);
            }
        }
        final boolean documentEntity = this.fCurrentEntity.name == XMLEntityManager.XMLEntity;
        this.fCurrentEntity = entity;
        this.fEntityScanner.setCurrentEntity(this.fCurrentEntity);
        if (this.fCurrentEntity == null & !documentEntity) {
            throw new EOFException();
        }
    }
    
    public void reset(final PropertyManager propertyManager) {
        this.fSymbolTable = (SymbolTable)propertyManager.getProperty("http://apache.org/xml/properties/internal/symbol-table");
        this.fErrorReporter = (XMLErrorReporter)propertyManager.getProperty("http://apache.org/xml/properties/internal/error-reporter");
        try {
            this.fStaxEntityResolver = (StaxEntityResolverWrapper)propertyManager.getProperty("http://apache.org/xml/properties/internal/stax-entity-resolver");
        }
        catch (final XMLConfigurationException e) {
            this.fStaxEntityResolver = null;
        }
        this.fSupportDTD = (boolean)propertyManager.getProperty("javax.xml.stream.supportDTD");
        this.fReplaceEntityReferences = (boolean)propertyManager.getProperty("javax.xml.stream.isReplacingEntityReferences");
        this.fSupportExternalEntities = (boolean)propertyManager.getProperty("javax.xml.stream.isSupportingExternalEntities");
        this.fLoadExternalDTD = !(boolean)propertyManager.getProperty("http://java.sun.com/xml/stream/properties/ignore-external-dtd");
        final XMLSecurityPropertyManager spm = (XMLSecurityPropertyManager)propertyManager.getProperty("http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager");
        this.fAccessExternalDTD = spm.getValue(XMLSecurityPropertyManager.Property.ACCESS_EXTERNAL_DTD);
        this.fSecurityManager = (XMLSecurityManager)propertyManager.getProperty("http://apache.org/xml/properties/security-manager");
        this.fLimitAnalyzer = new XMLLimitAnalyzer();
        this.fEntityStorage.reset(propertyManager);
        this.fEntityScanner.reset(propertyManager);
        this.fEntities.clear();
        this.fEntityStack.removeAllElements();
        this.fCurrentEntity = null;
        this.fValidation = false;
        this.fExternalGeneralEntities = true;
        this.fExternalParameterEntities = true;
        this.fAllowJavaEncodings = true;
    }
    
    @Override
    public void reset(final XMLComponentManager componentManager) throws XMLConfigurationException {
        final boolean parser_settings = componentManager.getFeature("http://apache.org/xml/features/internal/parser-settings", true);
        if (!parser_settings) {
            this.reset();
            if (this.fEntityScanner != null) {
                this.fEntityScanner.reset(componentManager);
            }
            if (this.fEntityStorage != null) {
                this.fEntityStorage.reset(componentManager);
            }
            return;
        }
        this.fValidation = componentManager.getFeature("http://xml.org/sax/features/validation", false);
        this.fExternalGeneralEntities = componentManager.getFeature("http://xml.org/sax/features/external-general-entities", true);
        this.fExternalParameterEntities = componentManager.getFeature("http://xml.org/sax/features/external-parameter-entities", true);
        this.fAllowJavaEncodings = componentManager.getFeature("http://apache.org/xml/features/allow-java-encodings", false);
        this.fWarnDuplicateEntityDef = componentManager.getFeature("http://apache.org/xml/features/warn-on-duplicate-entitydef", false);
        this.fStrictURI = componentManager.getFeature("http://apache.org/xml/features/standard-uri-conformant", false);
        this.fLoadExternalDTD = componentManager.getFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", true);
        this.fSymbolTable = (SymbolTable)componentManager.getProperty("http://apache.org/xml/properties/internal/symbol-table");
        this.fErrorReporter = (XMLErrorReporter)componentManager.getProperty("http://apache.org/xml/properties/internal/error-reporter");
        this.fEntityResolver = (XMLEntityResolver)componentManager.getProperty("http://apache.org/xml/properties/internal/entity-resolver", null);
        this.fStaxEntityResolver = (StaxEntityResolverWrapper)componentManager.getProperty("http://apache.org/xml/properties/internal/stax-entity-resolver", null);
        this.fValidationManager = (ValidationManager)componentManager.getProperty("http://apache.org/xml/properties/internal/validation-manager", null);
        this.fSecurityManager = (XMLSecurityManager)componentManager.getProperty("http://apache.org/xml/properties/security-manager", null);
        this.entityExpansionIndex = this.fSecurityManager.getIndex("http://www.oracle.com/xml/jaxp/properties/entityExpansionLimit");
        this.fSupportDTD = true;
        this.fReplaceEntityReferences = true;
        this.fSupportExternalEntities = true;
        XMLSecurityPropertyManager spm = (XMLSecurityPropertyManager)componentManager.getProperty("http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager", null);
        if (spm == null) {
            spm = new XMLSecurityPropertyManager();
        }
        this.fAccessExternalDTD = spm.getValue(XMLSecurityPropertyManager.Property.ACCESS_EXTERNAL_DTD);
        this.reset();
        this.fEntityScanner.reset(componentManager);
        this.fEntityStorage.reset(componentManager);
    }
    
    public void reset() {
        this.fLimitAnalyzer = new XMLLimitAnalyzer();
        this.fStandalone = false;
        this.fEntities.clear();
        this.fEntityStack.removeAllElements();
        this.fEntityExpansionCount = 0;
        this.fCurrentEntity = null;
        if (this.fXML10EntityScanner != null) {
            this.fXML10EntityScanner.reset(this.fSymbolTable, this, this.fErrorReporter);
        }
        if (this.fXML11EntityScanner != null) {
            this.fXML11EntityScanner.reset(this.fSymbolTable, this, this.fErrorReporter);
        }
        this.fEntityHandler = null;
    }
    
    @Override
    public String[] getRecognizedFeatures() {
        return XMLEntityManager.RECOGNIZED_FEATURES.clone();
    }
    
    @Override
    public void setFeature(final String featureId, final boolean state) throws XMLConfigurationException {
        if (featureId.startsWith("http://apache.org/xml/features/")) {
            final int suffixLength = featureId.length() - "http://apache.org/xml/features/".length();
            if (suffixLength == "allow-java-encodings".length() && featureId.endsWith("allow-java-encodings")) {
                this.fAllowJavaEncodings = state;
            }
            if (suffixLength == "nonvalidating/load-external-dtd".length() && featureId.endsWith("nonvalidating/load-external-dtd")) {
                this.fLoadExternalDTD = state;
            }
        }
    }
    
    @Override
    public void setProperty(final String propertyId, final Object value) {
        if (propertyId.startsWith("http://apache.org/xml/properties/")) {
            final int suffixLength = propertyId.length() - "http://apache.org/xml/properties/".length();
            if (suffixLength == "internal/symbol-table".length() && propertyId.endsWith("internal/symbol-table")) {
                this.fSymbolTable = (SymbolTable)value;
                return;
            }
            if (suffixLength == "internal/error-reporter".length() && propertyId.endsWith("internal/error-reporter")) {
                this.fErrorReporter = (XMLErrorReporter)value;
                return;
            }
            if (suffixLength == "internal/entity-resolver".length() && propertyId.endsWith("internal/entity-resolver")) {
                this.fEntityResolver = (XMLEntityResolver)value;
                return;
            }
            if (suffixLength == "input-buffer-size".length() && propertyId.endsWith("input-buffer-size")) {
                final Integer bufferSize = (Integer)value;
                if (bufferSize != null && bufferSize > 64) {
                    this.fBufferSize = bufferSize;
                    this.fEntityScanner.setBufferSize(this.fBufferSize);
                    this.fBufferPool.setExternalBufferSize(this.fBufferSize);
                }
            }
            if (suffixLength == "security-manager".length() && propertyId.endsWith("security-manager")) {
                this.fSecurityManager = (XMLSecurityManager)value;
            }
        }
        if (propertyId.equals("http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager")) {
            final XMLSecurityPropertyManager spm = (XMLSecurityPropertyManager)value;
            this.fAccessExternalDTD = spm.getValue(XMLSecurityPropertyManager.Property.ACCESS_EXTERNAL_DTD);
        }
    }
    
    public void setLimitAnalyzer(final XMLLimitAnalyzer fLimitAnalyzer) {
        this.fLimitAnalyzer = fLimitAnalyzer;
    }
    
    @Override
    public String[] getRecognizedProperties() {
        return XMLEntityManager.RECOGNIZED_PROPERTIES.clone();
    }
    
    @Override
    public Boolean getFeatureDefault(final String featureId) {
        for (int i = 0; i < XMLEntityManager.RECOGNIZED_FEATURES.length; ++i) {
            if (XMLEntityManager.RECOGNIZED_FEATURES[i].equals(featureId)) {
                return XMLEntityManager.FEATURE_DEFAULTS[i];
            }
        }
        return null;
    }
    
    @Override
    public Object getPropertyDefault(final String propertyId) {
        for (int i = 0; i < XMLEntityManager.RECOGNIZED_PROPERTIES.length; ++i) {
            if (XMLEntityManager.RECOGNIZED_PROPERTIES[i].equals(propertyId)) {
                return XMLEntityManager.PROPERTY_DEFAULTS[i];
            }
        }
        return null;
    }
    
    public static String expandSystemId(final String systemId) {
        return expandSystemId(systemId, null);
    }
    
    private static synchronized URI getUserDir() throws URI.MalformedURIException {
        String userDir = "";
        try {
            userDir = SecuritySupport.getSystemProperty("user.dir");
        }
        catch (final SecurityException ex) {}
        if (userDir.length() == 0) {
            return new URI("file", "", "", null, null);
        }
        if (XMLEntityManager.gUserDirURI != null && userDir.equals(XMLEntityManager.gUserDir)) {
            return XMLEntityManager.gUserDirURI;
        }
        XMLEntityManager.gUserDir = userDir;
        final char separator = File.separatorChar;
        userDir = userDir.replace(separator, '/');
        final int len = userDir.length();
        final StringBuffer buffer = new StringBuffer(len * 3);
        if (len >= 2 && userDir.charAt(1) == ':') {
            final int ch = Character.toUpperCase(userDir.charAt(0));
            if (ch >= 65 && ch <= 90) {
                buffer.append('/');
            }
        }
        int i;
        for (i = 0; i < len; ++i) {
            final int ch = userDir.charAt(i);
            if (ch >= 128) {
                break;
            }
            if (XMLEntityManager.gNeedEscaping[ch]) {
                buffer.append('%');
                buffer.append(XMLEntityManager.gAfterEscaping1[ch]);
                buffer.append(XMLEntityManager.gAfterEscaping2[ch]);
            }
            else {
                buffer.append((char)ch);
            }
        }
        if (i < len) {
            byte[] bytes = null;
            try {
                bytes = userDir.substring(i).getBytes("UTF-8");
            }
            catch (final UnsupportedEncodingException e) {
                return new URI("file", "", userDir, null, null);
            }
            for (final byte b : bytes) {
                if (b < 0) {
                    final int ch = b + 256;
                    buffer.append('%');
                    buffer.append(XMLEntityManager.gHexChs[ch >> 4]);
                    buffer.append(XMLEntityManager.gHexChs[ch & 0xF]);
                }
                else if (XMLEntityManager.gNeedEscaping[b]) {
                    buffer.append('%');
                    buffer.append(XMLEntityManager.gAfterEscaping1[b]);
                    buffer.append(XMLEntityManager.gAfterEscaping2[b]);
                }
                else {
                    buffer.append((char)b);
                }
            }
        }
        if (!userDir.endsWith("/")) {
            buffer.append('/');
        }
        return XMLEntityManager.gUserDirURI = new URI("file", "", buffer.toString(), null, null);
    }
    
    public static void absolutizeAgainstUserDir(final URI uri) throws URI.MalformedURIException {
        uri.absolutize(getUserDir());
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
            if (baseSystemId == null || baseSystemId.length() == 0 || baseSystemId.equals(systemId)) {
                final String dir = getUserDir().toString();
                base = new URI("file", "", dir, null, null);
            }
            else {
                try {
                    base = new URI(fixURI(baseSystemId));
                }
                catch (final URI.MalformedURIException e) {
                    if (baseSystemId.indexOf(58) != -1) {
                        base = new URI("file", "", fixURI(baseSystemId), null, null);
                    }
                    else {
                        String dir2 = getUserDir().toString();
                        dir2 += fixURI(baseSystemId);
                        base = new URI("file", "", dir2, null, null);
                    }
                }
            }
            uri2 = new URI(base, id);
        }
        catch (final Exception ex2) {}
        if (uri2 == null) {
            return systemId;
        }
        return uri2.toString();
    }
    
    public static String expandSystemId(final String systemId, final String baseSystemId, final boolean strict) throws URI.MalformedURIException {
        if (systemId == null) {
            return null;
        }
        if (strict) {
            try {
                new URI(systemId);
                return systemId;
            }
            catch (final URI.MalformedURIException ex) {
                URI base = null;
                if (baseSystemId == null || baseSystemId.length() == 0) {
                    base = new URI("file", "", getUserDir().toString(), null, null);
                }
                else {
                    try {
                        base = new URI(baseSystemId);
                    }
                    catch (final URI.MalformedURIException e) {
                        String dir = getUserDir().toString();
                        dir += baseSystemId;
                        base = new URI("file", "", dir, null, null);
                    }
                }
                final URI uri = new URI(base, systemId);
                return uri.toString();
            }
        }
        try {
            return expandSystemIdStrictOff(systemId, baseSystemId);
        }
        catch (final URI.MalformedURIException e2) {
            try {
                return expandSystemIdStrictOff1(systemId, baseSystemId);
            }
            catch (final URISyntaxException ex2) {
                if (systemId.length() == 0) {
                    return systemId;
                }
                final String id = fixURI(systemId);
                URI base2 = null;
                URI uri2 = null;
                try {
                    if (baseSystemId == null || baseSystemId.length() == 0 || baseSystemId.equals(systemId)) {
                        base2 = getUserDir();
                    }
                    else {
                        try {
                            base2 = new URI(fixURI(baseSystemId).trim());
                        }
                        catch (final URI.MalformedURIException e3) {
                            if (baseSystemId.indexOf(58) != -1) {
                                base2 = new URI("file", "", fixURI(baseSystemId).trim(), null, null);
                            }
                            else {
                                base2 = new URI(getUserDir(), fixURI(baseSystemId));
                            }
                        }
                    }
                    uri2 = new URI(base2, id.trim());
                }
                catch (final Exception ex3) {}
                if (uri2 == null) {
                    return systemId;
                }
                return uri2.toString();
            }
        }
    }
    
    private static String expandSystemIdStrictOn(final String systemId, final String baseSystemId) throws URI.MalformedURIException {
        final URI systemURI = new URI(systemId, true);
        if (systemURI.isAbsoluteURI()) {
            return systemId;
        }
        URI baseURI = null;
        if (baseSystemId == null || baseSystemId.length() == 0) {
            baseURI = getUserDir();
        }
        else {
            baseURI = new URI(baseSystemId, true);
            if (!baseURI.isAbsoluteURI()) {
                baseURI.absolutize(getUserDir());
            }
        }
        systemURI.absolutize(baseURI);
        return systemURI.toString();
    }
    
    public static void setInstanceFollowRedirects(final HttpURLConnection urlCon, final boolean followRedirects) {
        try {
            final Method method = HttpURLConnection.class.getMethod("setInstanceFollowRedirects", Boolean.TYPE);
            method.invoke(urlCon, followRedirects ? Boolean.TRUE : Boolean.FALSE);
        }
        catch (final Exception ex) {}
    }
    
    private static String expandSystemIdStrictOff(final String systemId, final String baseSystemId) throws URI.MalformedURIException {
        final URI systemURI = new URI(systemId, true);
        if (!systemURI.isAbsoluteURI()) {
            URI baseURI = null;
            if (baseSystemId == null || baseSystemId.length() == 0) {
                baseURI = getUserDir();
            }
            else {
                baseURI = new URI(baseSystemId, true);
                if (!baseURI.isAbsoluteURI()) {
                    baseURI.absolutize(getUserDir());
                }
            }
            systemURI.absolutize(baseURI);
            return systemURI.toString();
        }
        if (systemURI.getScheme().length() > 1) {
            return systemId;
        }
        throw new URI.MalformedURIException();
    }
    
    private static String expandSystemIdStrictOff1(final String systemId, final String baseSystemId) throws URISyntaxException, URI.MalformedURIException {
        java.net.URI systemURI = new java.net.URI(systemId);
        if (!systemURI.isAbsolute()) {
            URI baseURI = null;
            if (baseSystemId == null || baseSystemId.length() == 0) {
                baseURI = getUserDir();
            }
            else {
                baseURI = new URI(baseSystemId, true);
                if (!baseURI.isAbsoluteURI()) {
                    baseURI.absolutize(getUserDir());
                }
            }
            systemURI = new java.net.URI(baseURI.toString()).resolve(systemURI);
            return systemURI.toString();
        }
        if (systemURI.getScheme().length() > 1) {
            return systemId;
        }
        throw new URISyntaxException(systemId, "the scheme's length is only one character");
    }
    
    protected Object[] getEncodingName(final byte[] b4, final int count) {
        if (count < 2) {
            return this.defaultEncoding;
        }
        final int b5 = b4[0] & 0xFF;
        final int b6 = b4[1] & 0xFF;
        if (b5 == 254 && b6 == 255) {
            return new Object[] { "UTF-16BE", new Boolean(true) };
        }
        if (b5 == 255 && b6 == 254) {
            return new Object[] { "UTF-16LE", new Boolean(false) };
        }
        if (count < 3) {
            return this.defaultEncoding;
        }
        final int b7 = b4[2] & 0xFF;
        if (b5 == 239 && b6 == 187 && b7 == 191) {
            return this.defaultEncoding;
        }
        if (count < 4) {
            return this.defaultEncoding;
        }
        final int b8 = b4[3] & 0xFF;
        if (b5 == 0 && b6 == 0 && b7 == 0 && b8 == 60) {
            return new Object[] { "ISO-10646-UCS-4", new Boolean(true) };
        }
        if (b5 == 60 && b6 == 0 && b7 == 0 && b8 == 0) {
            return new Object[] { "ISO-10646-UCS-4", new Boolean(false) };
        }
        if (b5 == 0 && b6 == 0 && b7 == 60 && b8 == 0) {
            return new Object[] { "ISO-10646-UCS-4", null };
        }
        if (b5 == 0 && b6 == 60 && b7 == 0 && b8 == 0) {
            return new Object[] { "ISO-10646-UCS-4", null };
        }
        if (b5 == 0 && b6 == 60 && b7 == 0 && b8 == 63) {
            return new Object[] { "UTF-16BE", new Boolean(true) };
        }
        if (b5 == 60 && b6 == 0 && b7 == 63 && b8 == 0) {
            return new Object[] { "UTF-16LE", new Boolean(false) };
        }
        if (b5 == 76 && b6 == 111 && b7 == 167 && b8 == 148) {
            return new Object[] { "CP037", null };
        }
        return this.defaultEncoding;
    }
    
    protected Reader createReader(final InputStream inputStream, String encoding, final Boolean isBigEndian) throws IOException {
        if (encoding == null) {
            encoding = "UTF-8";
        }
        final String ENCODING = encoding.toUpperCase(Locale.ENGLISH);
        if (ENCODING.equals("UTF-8")) {
            return new UTF8Reader(inputStream, this.fBufferSize, this.fErrorReporter.getMessageFormatter("http://www.w3.org/TR/1998/REC-xml-19980210"), this.fErrorReporter.getLocale());
        }
        if (ENCODING.equals("US-ASCII")) {
            return new ASCIIReader(inputStream, this.fBufferSize, this.fErrorReporter.getMessageFormatter("http://www.w3.org/TR/1998/REC-xml-19980210"), this.fErrorReporter.getLocale());
        }
        if (ENCODING.equals("ISO-10646-UCS-4")) {
            if (isBigEndian != null) {
                final boolean isBE = isBigEndian;
                if (isBE) {
                    return new UCSReader(inputStream, (short)8);
                }
                return new UCSReader(inputStream, (short)4);
            }
            else {
                this.fErrorReporter.reportError(this.getEntityScanner(), "http://www.w3.org/TR/1998/REC-xml-19980210", "EncodingByteOrderUnsupported", new Object[] { encoding }, (short)2);
            }
        }
        if (ENCODING.equals("ISO-10646-UCS-2")) {
            if (isBigEndian != null) {
                final boolean isBE = isBigEndian;
                if (isBE) {
                    return new UCSReader(inputStream, (short)2);
                }
                return new UCSReader(inputStream, (short)1);
            }
            else {
                this.fErrorReporter.reportError(this.getEntityScanner(), "http://www.w3.org/TR/1998/REC-xml-19980210", "EncodingByteOrderUnsupported", new Object[] { encoding }, (short)2);
            }
        }
        final boolean validIANA = XMLChar.isValidIANAEncoding(encoding);
        final boolean validJava = XMLChar.isValidJavaEncoding(encoding);
        if (!validIANA || (this.fAllowJavaEncodings && !validJava)) {
            this.fErrorReporter.reportError(this.getEntityScanner(), "http://www.w3.org/TR/1998/REC-xml-19980210", "EncodingDeclInvalid", new Object[] { encoding }, (short)2);
            encoding = "ISO-8859-1";
        }
        String javaEncoding = EncodingMap.getIANA2JavaMapping(ENCODING);
        if (javaEncoding == null) {
            if (this.fAllowJavaEncodings) {
                javaEncoding = encoding;
            }
            else {
                this.fErrorReporter.reportError(this.getEntityScanner(), "http://www.w3.org/TR/1998/REC-xml-19980210", "EncodingDeclInvalid", new Object[] { encoding }, (short)2);
                javaEncoding = "ISO8859_1";
            }
        }
        return new BufferedReader(new InputStreamReader(inputStream, javaEncoding));
    }
    
    public String getPublicId() {
        return (this.fCurrentEntity != null && this.fCurrentEntity.entityLocation != null) ? this.fCurrentEntity.entityLocation.getPublicId() : null;
    }
    
    public String getExpandedSystemId() {
        if (this.fCurrentEntity != null) {
            if (this.fCurrentEntity.entityLocation != null && this.fCurrentEntity.entityLocation.getExpandedSystemId() != null) {
                return this.fCurrentEntity.entityLocation.getExpandedSystemId();
            }
            final int size = this.fEntityStack.size();
            for (int i = size - 1; i >= 0; --i) {
                final Entity.ScannedEntity externalEntity = this.fEntityStack.elementAt(i);
                if (externalEntity.entityLocation != null && externalEntity.entityLocation.getExpandedSystemId() != null) {
                    return externalEntity.entityLocation.getExpandedSystemId();
                }
            }
        }
        return null;
    }
    
    public String getLiteralSystemId() {
        if (this.fCurrentEntity != null) {
            if (this.fCurrentEntity.entityLocation != null && this.fCurrentEntity.entityLocation.getLiteralSystemId() != null) {
                return this.fCurrentEntity.entityLocation.getLiteralSystemId();
            }
            final int size = this.fEntityStack.size();
            for (int i = size - 1; i >= 0; --i) {
                final Entity.ScannedEntity externalEntity = this.fEntityStack.elementAt(i);
                if (externalEntity.entityLocation != null && externalEntity.entityLocation.getLiteralSystemId() != null) {
                    return externalEntity.entityLocation.getLiteralSystemId();
                }
            }
        }
        return null;
    }
    
    public int getLineNumber() {
        if (this.fCurrentEntity != null) {
            if (this.fCurrentEntity.isExternal()) {
                return this.fCurrentEntity.lineNumber;
            }
            final int size = this.fEntityStack.size();
            for (int i = size - 1; i > 0; --i) {
                final Entity.ScannedEntity firstExternalEntity = this.fEntityStack.elementAt(i);
                if (firstExternalEntity.isExternal()) {
                    return firstExternalEntity.lineNumber;
                }
            }
        }
        return -1;
    }
    
    public int getColumnNumber() {
        if (this.fCurrentEntity != null) {
            if (this.fCurrentEntity.isExternal()) {
                return this.fCurrentEntity.columnNumber;
            }
            final int size = this.fEntityStack.size();
            for (int i = size - 1; i > 0; --i) {
                final Entity.ScannedEntity firstExternalEntity = this.fEntityStack.elementAt(i);
                if (firstExternalEntity.isExternal()) {
                    return firstExternalEntity.columnNumber;
                }
            }
        }
        return -1;
    }
    
    protected static String fixURI(String str) {
        str = str.replace(File.separatorChar, '/');
        if (str.length() >= 2) {
            final char ch1 = str.charAt(1);
            if (ch1 == ':') {
                final char ch2 = Character.toUpperCase(str.charAt(0));
                if (ch2 >= 'A' && ch2 <= 'Z') {
                    str = "/" + str;
                }
            }
            else if (ch1 == '/' && str.charAt(0) == '/') {
                str = "file:" + str;
            }
        }
        final int pos = str.indexOf(32);
        if (pos >= 0) {
            final StringBuilder sb = new StringBuilder(str.length());
            for (int i = 0; i < pos; ++i) {
                sb.append(str.charAt(i));
            }
            sb.append("%20");
            for (int i = pos + 1; i < str.length(); ++i) {
                if (str.charAt(i) == ' ') {
                    sb.append("%20");
                }
                else {
                    sb.append(str.charAt(i));
                }
            }
            str = sb.toString();
        }
        return str;
    }
    
    final void print() {
    }
    
    public void test() {
        this.fEntityStorage.addExternalEntity("entityUsecase1", null, "/space/home/stax/sun/6thJan2004/zephyr/data/test.txt", "/space/home/stax/sun/6thJan2004/zephyr/data/entity.xml");
        this.fEntityStorage.addInternalEntity("entityUsecase2", "<Test>value</Test>");
        this.fEntityStorage.addInternalEntity("entityUsecase3", "value3");
        this.fEntityStorage.addInternalEntity("text", "Hello World.");
        this.fEntityStorage.addInternalEntity("empty-element", "<foo/>");
        this.fEntityStorage.addInternalEntity("balanced-element", "<foo></foo>");
        this.fEntityStorage.addInternalEntity("balanced-element-with-text", "<foo>Hello, World</foo>");
        this.fEntityStorage.addInternalEntity("balanced-element-with-entity", "<foo>&text;</foo>");
        this.fEntityStorage.addInternalEntity("unbalanced-entity", "<foo>");
        this.fEntityStorage.addInternalEntity("recursive-entity", "<foo>&recursive-entity2;</foo>");
        this.fEntityStorage.addInternalEntity("recursive-entity2", "<bar>&recursive-entity3;</bar>");
        this.fEntityStorage.addInternalEntity("recursive-entity3", "<baz>&recursive-entity;</baz>");
        this.fEntityStorage.addInternalEntity("ch", "&#x00A9;");
        this.fEntityStorage.addInternalEntity("ch1", "&#84;");
        this.fEntityStorage.addInternalEntity("% ch2", "param");
    }
    
    static {
        RECOGNIZED_FEATURES = new String[] { "http://xml.org/sax/features/validation", "http://xml.org/sax/features/external-general-entities", "http://xml.org/sax/features/external-parameter-entities", "http://apache.org/xml/features/allow-java-encodings", "http://apache.org/xml/features/warn-on-duplicate-entitydef", "http://apache.org/xml/features/standard-uri-conformant" };
        FEATURE_DEFAULTS = new Boolean[] { null, Boolean.TRUE, Boolean.TRUE, Boolean.TRUE, Boolean.FALSE, Boolean.FALSE };
        RECOGNIZED_PROPERTIES = new String[] { "http://apache.org/xml/properties/internal/symbol-table", "http://apache.org/xml/properties/internal/error-reporter", "http://apache.org/xml/properties/internal/entity-resolver", "http://apache.org/xml/properties/internal/validation-manager", "http://apache.org/xml/properties/input-buffer-size", "http://apache.org/xml/properties/security-manager", "http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager" };
        PROPERTY_DEFAULTS = new Object[] { null, null, null, null, new Integer(8192), null, null };
        XMLEntity = "[xml]".intern();
        DTDEntity = "[dtd]".intern();
        XMLEntityManager.gNeedEscaping = new boolean[128];
        XMLEntityManager.gAfterEscaping1 = new char[128];
        XMLEntityManager.gAfterEscaping2 = new char[128];
        XMLEntityManager.gHexChs = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
        for (int i = 0; i <= 31; ++i) {
            XMLEntityManager.gNeedEscaping[i] = true;
            XMLEntityManager.gAfterEscaping1[i] = XMLEntityManager.gHexChs[i >> 4];
            XMLEntityManager.gAfterEscaping2[i] = XMLEntityManager.gHexChs[i & 0xF];
        }
        XMLEntityManager.gNeedEscaping[127] = true;
        XMLEntityManager.gAfterEscaping1[127] = '7';
        XMLEntityManager.gAfterEscaping2[127] = 'F';
        for (final char ch : new char[] { ' ', '<', '>', '#', '%', '\"', '{', '}', '|', '\\', '^', '~', '[', ']', '`' }) {
            XMLEntityManager.gNeedEscaping[ch] = true;
            XMLEntityManager.gAfterEscaping1[ch] = XMLEntityManager.gHexChs[ch >> 4];
            XMLEntityManager.gAfterEscaping2[ch] = XMLEntityManager.gHexChs[ch & '\u000f'];
        }
    }
    
    private static class CharacterBuffer
    {
        private char[] ch;
        private boolean isExternal;
        
        public CharacterBuffer(final boolean isExternal, final int size) {
            this.isExternal = isExternal;
            this.ch = new char[size];
        }
    }
    
    private static class CharacterBufferPool
    {
        private static final int DEFAULT_POOL_SIZE = 3;
        private CharacterBuffer[] fInternalBufferPool;
        private CharacterBuffer[] fExternalBufferPool;
        private int fExternalBufferSize;
        private int fInternalBufferSize;
        private int poolSize;
        private int fInternalTop;
        private int fExternalTop;
        
        public CharacterBufferPool(final int externalBufferSize, final int internalBufferSize) {
            this(3, externalBufferSize, internalBufferSize);
        }
        
        public CharacterBufferPool(final int poolSize, final int externalBufferSize, final int internalBufferSize) {
            this.fExternalBufferSize = externalBufferSize;
            this.fInternalBufferSize = internalBufferSize;
            this.poolSize = poolSize;
            this.init();
        }
        
        private void init() {
            this.fInternalBufferPool = new CharacterBuffer[this.poolSize];
            this.fExternalBufferPool = new CharacterBuffer[this.poolSize];
            this.fInternalTop = -1;
            this.fExternalTop = -1;
        }
        
        public CharacterBuffer getBuffer(final boolean external) {
            if (external) {
                if (this.fExternalTop > -1) {
                    return this.fExternalBufferPool[this.fExternalTop--];
                }
                return new CharacterBuffer(true, this.fExternalBufferSize);
            }
            else {
                if (this.fInternalTop > -1) {
                    return this.fInternalBufferPool[this.fInternalTop--];
                }
                return new CharacterBuffer(false, this.fInternalBufferSize);
            }
        }
        
        public void returnToPool(final CharacterBuffer buffer) {
            if (buffer.isExternal) {
                if (this.fExternalTop < this.fExternalBufferPool.length - 1) {
                    this.fExternalBufferPool[++this.fExternalTop] = buffer;
                }
            }
            else if (this.fInternalTop < this.fInternalBufferPool.length - 1) {
                this.fInternalBufferPool[++this.fInternalTop] = buffer;
            }
        }
        
        public void setExternalBufferSize(final int bufferSize) {
            this.fExternalBufferSize = bufferSize;
            this.fExternalBufferPool = new CharacterBuffer[this.poolSize];
            this.fExternalTop = -1;
        }
    }
    
    protected final class RewindableInputStream extends InputStream
    {
        private InputStream fInputStream;
        private byte[] fData;
        private int fStartOffset;
        private int fEndOffset;
        private int fOffset;
        private int fLength;
        private int fMark;
        
        public RewindableInputStream(final InputStream is) {
            this.fData = new byte[64];
            this.fInputStream = is;
            this.fStartOffset = 0;
            this.fEndOffset = -1;
            this.fOffset = 0;
            this.fLength = 0;
            this.fMark = 0;
        }
        
        public void setStartOffset(final int offset) {
            this.fStartOffset = offset;
        }
        
        public void rewind() {
            this.fOffset = this.fStartOffset;
        }
        
        @Override
        public int read() throws IOException {
            int b = 0;
            if (this.fOffset < this.fLength) {
                return this.fData[this.fOffset++] & 0xFF;
            }
            if (this.fOffset == this.fEndOffset) {
                return -1;
            }
            if (this.fOffset == this.fData.length) {
                final byte[] newData = new byte[this.fOffset << 1];
                System.arraycopy(this.fData, 0, newData, 0, this.fOffset);
                this.fData = newData;
            }
            b = this.fInputStream.read();
            if (b == -1) {
                this.fEndOffset = this.fOffset;
                return -1;
            }
            this.fData[this.fLength++] = (byte)b;
            ++this.fOffset;
            return b & 0xFF;
        }
        
        @Override
        public int read(final byte[] b, final int off, int len) throws IOException {
            final int bytesLeft = this.fLength - this.fOffset;
            if (bytesLeft != 0) {
                if (len < bytesLeft) {
                    if (len <= 0) {
                        return 0;
                    }
                }
                else {
                    len = bytesLeft;
                }
                if (b != null) {
                    System.arraycopy(this.fData, this.fOffset, b, off, len);
                }
                this.fOffset += len;
                return len;
            }
            if (this.fOffset == this.fEndOffset) {
                return -1;
            }
            if (XMLEntityManager.this.fCurrentEntity.mayReadChunks || !XMLEntityManager.this.fCurrentEntity.xmlDeclChunkRead) {
                if (!XMLEntityManager.this.fCurrentEntity.xmlDeclChunkRead) {
                    XMLEntityManager.this.fCurrentEntity.xmlDeclChunkRead = true;
                    len = 28;
                }
                return this.fInputStream.read(b, off, len);
            }
            final int returnedVal = this.read();
            if (returnedVal == -1) {
                this.fEndOffset = this.fOffset;
                return -1;
            }
            b[off] = (byte)returnedVal;
            return 1;
        }
        
        @Override
        public long skip(long n) throws IOException {
            if (n <= 0L) {
                return 0L;
            }
            final int bytesLeft = this.fLength - this.fOffset;
            if (bytesLeft == 0) {
                if (this.fOffset == this.fEndOffset) {
                    return 0L;
                }
                return this.fInputStream.skip(n);
            }
            else {
                if (n <= bytesLeft) {
                    this.fOffset += (int)n;
                    return n;
                }
                this.fOffset += bytesLeft;
                if (this.fOffset == this.fEndOffset) {
                    return bytesLeft;
                }
                n -= bytesLeft;
                return this.fInputStream.skip(n) + bytesLeft;
            }
        }
        
        @Override
        public int available() throws IOException {
            final int bytesLeft = this.fLength - this.fOffset;
            if (bytesLeft != 0) {
                return bytesLeft;
            }
            if (this.fOffset == this.fEndOffset) {
                return -1;
            }
            return XMLEntityManager.this.fCurrentEntity.mayReadChunks ? this.fInputStream.available() : 0;
        }
        
        @Override
        public void mark(final int howMuch) {
            this.fMark = this.fOffset;
        }
        
        @Override
        public void reset() {
            this.fOffset = this.fMark;
        }
        
        @Override
        public boolean markSupported() {
            return true;
        }
        
        @Override
        public void close() throws IOException {
            if (this.fInputStream != null) {
                this.fInputStream.close();
                this.fInputStream = null;
            }
        }
    }
}
