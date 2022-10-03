package org.apache.xerces.impl;

import org.apache.xerces.impl.io.Latin1Reader;
import org.apache.xerces.impl.io.ASCIIReader;
import org.apache.xerces.impl.io.UTF16Reader;
import org.apache.xerces.impl.io.UTF8Reader;
import java.io.InputStreamReader;
import org.apache.xerces.util.EncodingMap;
import org.apache.xerces.util.XMLChar;
import org.apache.xerces.impl.io.UCSReader;
import java.util.StringTokenizer;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.io.File;
import java.security.AccessController;
import org.apache.xerces.xni.parser.XMLConfigurationException;
import org.apache.xerces.xni.parser.XMLComponentManager;
import java.util.Iterator;
import java.net.URLConnection;
import java.io.InputStream;
import java.util.Locale;
import java.util.Map;
import org.apache.xerces.util.HTTPInputSource;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.Reader;
import java.io.StringReader;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.parser.XMLInputSource;
import java.io.IOException;
import org.apache.xerces.util.XMLEntityDescriptionImpl;
import org.apache.xerces.xni.XMLResourceIdentifier;
import org.apache.xerces.util.AugmentationsImpl;
import java.security.PrivilegedAction;
import org.apache.xerces.util.URI;
import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.util.XMLResourceIdentifierImpl;
import java.util.Stack;
import java.util.Hashtable;
import org.apache.xerces.util.SecurityManager;
import org.apache.xerces.impl.validation.ValidationManager;
import org.apache.xerces.util.SymbolTable;
import org.apache.xerces.xni.parser.XMLEntityResolver;
import org.apache.xerces.xni.parser.XMLComponent;

public class XMLEntityManager implements XMLComponent, XMLEntityResolver
{
    public static final int DEFAULT_BUFFER_SIZE = 2048;
    public static final int DEFAULT_XMLDECL_BUFFER_SIZE = 64;
    public static final int DEFAULT_INTERNAL_BUFFER_SIZE = 512;
    protected static final String VALIDATION = "http://xml.org/sax/features/validation";
    protected static final String EXTERNAL_GENERAL_ENTITIES = "http://xml.org/sax/features/external-general-entities";
    protected static final String EXTERNAL_PARAMETER_ENTITIES = "http://xml.org/sax/features/external-parameter-entities";
    protected static final String ALLOW_JAVA_ENCODINGS = "http://apache.org/xml/features/allow-java-encodings";
    protected static final String WARN_ON_DUPLICATE_ENTITYDEF = "http://apache.org/xml/features/warn-on-duplicate-entitydef";
    protected static final String STANDARD_URI_CONFORMANT = "http://apache.org/xml/features/standard-uri-conformant";
    protected static final String PARSER_SETTINGS = "http://apache.org/xml/features/internal/parser-settings";
    protected static final String SYMBOL_TABLE = "http://apache.org/xml/properties/internal/symbol-table";
    protected static final String ERROR_REPORTER = "http://apache.org/xml/properties/internal/error-reporter";
    protected static final String ENTITY_RESOLVER = "http://apache.org/xml/properties/internal/entity-resolver";
    protected static final String VALIDATION_MANAGER = "http://apache.org/xml/properties/internal/validation-manager";
    protected static final String BUFFER_SIZE = "http://apache.org/xml/properties/input-buffer-size";
    protected static final String SECURITY_MANAGER = "http://apache.org/xml/properties/security-manager";
    private static final String[] RECOGNIZED_FEATURES;
    private static final Boolean[] FEATURE_DEFAULTS;
    private static final String[] RECOGNIZED_PROPERTIES;
    private static final Object[] PROPERTY_DEFAULTS;
    private static final String XMLEntity;
    private static final String DTDEntity;
    private static final boolean DEBUG_BUFFER = false;
    private static final boolean DEBUG_ENTITIES = false;
    private static final boolean DEBUG_ENCODINGS = false;
    private static final boolean DEBUG_RESOLVER = false;
    protected boolean fValidation;
    protected boolean fExternalGeneralEntities;
    protected boolean fExternalParameterEntities;
    protected boolean fAllowJavaEncodings;
    protected boolean fWarnDuplicateEntityDef;
    protected boolean fStrictURI;
    protected SymbolTable fSymbolTable;
    protected XMLErrorReporter fErrorReporter;
    protected XMLEntityResolver fEntityResolver;
    protected ValidationManager fValidationManager;
    protected int fBufferSize;
    protected SecurityManager fSecurityManager;
    protected boolean fStandalone;
    protected boolean fHasPEReferences;
    protected boolean fInExternalSubset;
    protected XMLEntityHandler fEntityHandler;
    protected XMLEntityScanner fEntityScanner;
    protected XMLEntityScanner fXML10EntityScanner;
    protected XMLEntityScanner fXML11EntityScanner;
    protected int fEntityExpansionLimit;
    protected int fEntityExpansionCount;
    protected final Hashtable fEntities;
    protected final Stack fEntityStack;
    protected ScannedEntity fCurrentEntity;
    protected Hashtable fDeclaredEntities;
    private final XMLResourceIdentifierImpl fResourceIdentifier;
    private final Augmentations fEntityAugs;
    private final ByteBufferPool fSmallByteBufferPool;
    private final ByteBufferPool fLargeByteBufferPool;
    private byte[] fTempByteBuffer;
    private final CharacterBufferPool fCharacterBufferPool;
    protected Stack fReaderStack;
    private static String gUserDir;
    private static URI gUserDirURI;
    private static final boolean[] gNeedEscaping;
    private static final char[] gAfterEscaping1;
    private static final char[] gAfterEscaping2;
    private static final char[] gHexChs;
    private static PrivilegedAction GET_USER_DIR_SYSTEM_PROPERTY;
    
    public XMLEntityManager() {
        this(null);
    }
    
    public XMLEntityManager(final XMLEntityManager xmlEntityManager) {
        this.fExternalGeneralEntities = true;
        this.fExternalParameterEntities = true;
        this.fBufferSize = 2048;
        this.fSecurityManager = null;
        this.fInExternalSubset = false;
        this.fEntityExpansionLimit = 0;
        this.fEntityExpansionCount = 0;
        this.fEntities = new Hashtable();
        this.fEntityStack = new Stack();
        this.fResourceIdentifier = new XMLResourceIdentifierImpl();
        this.fEntityAugs = new AugmentationsImpl();
        this.fSmallByteBufferPool = new ByteBufferPool(this.fBufferSize);
        this.fLargeByteBufferPool = new ByteBufferPool(this.fBufferSize << 1);
        this.fTempByteBuffer = null;
        this.fCharacterBufferPool = new CharacterBufferPool(this.fBufferSize, 512);
        this.fReaderStack = new Stack();
        this.fDeclaredEntities = ((xmlEntityManager != null) ? xmlEntityManager.getDeclaredEntities() : null);
        this.setScannerVersion((short)1);
    }
    
    public void setStandalone(final boolean fStandalone) {
        this.fStandalone = fStandalone;
    }
    
    public boolean isStandalone() {
        return this.fStandalone;
    }
    
    final void notifyHasPEReferences() {
        this.fHasPEReferences = true;
    }
    
    final boolean hasPEReferences() {
        return this.fHasPEReferences;
    }
    
    public void setEntityHandler(final XMLEntityHandler fEntityHandler) {
        this.fEntityHandler = fEntityHandler;
    }
    
    public XMLResourceIdentifier getCurrentResourceIdentifier() {
        return this.fResourceIdentifier;
    }
    
    public ScannedEntity getCurrentEntity() {
        return this.fCurrentEntity;
    }
    
    public void addInternalEntity(final String s, final String s2, final int n) {
        if (!this.fEntities.containsKey(s)) {
            this.fEntities.put(s, new InternalEntity(s, s2, this.fInExternalSubset, n));
        }
        else if (this.fWarnDuplicateEntityDef) {
            this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_DUPLICATE_ENTITY_DEFINITION", new Object[] { s }, (short)0);
        }
    }
    
    public void addInternalEntity(final String s, final String s2) {
        this.addInternalEntity(s, s2, 0);
    }
    
    public int getParamEntityRefCount(final String s) {
        if (s != null && s.length() > 0 && s.charAt(0) == '%') {
            final Entity entity = this.fEntities.get(s);
            if (entity != null && !entity.isExternal()) {
                return ((InternalEntity)entity).paramEntityRefs;
            }
        }
        return 0;
    }
    
    public void addExternalEntity(final String s, final String s2, final String s3, String s4) throws IOException {
        if (!this.fEntities.containsKey(s)) {
            if (s4 == null) {
                final int size = this.fEntityStack.size();
                if (size == 0 && this.fCurrentEntity != null && this.fCurrentEntity.entityLocation != null) {
                    s4 = this.fCurrentEntity.entityLocation.getExpandedSystemId();
                }
                for (int i = size - 1; i >= 0; --i) {
                    final ScannedEntity scannedEntity = (ScannedEntity)this.fEntityStack.elementAt(i);
                    if (scannedEntity.entityLocation != null && scannedEntity.entityLocation.getExpandedSystemId() != null) {
                        s4 = scannedEntity.entityLocation.getExpandedSystemId();
                        break;
                    }
                }
            }
            this.fEntities.put(s, new ExternalEntity(s, new XMLEntityDescriptionImpl(s, s2, s3, s4, expandSystemId(s3, s4, false)), null, this.fInExternalSubset));
        }
        else if (this.fWarnDuplicateEntityDef) {
            this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_DUPLICATE_ENTITY_DEFINITION", new Object[] { s }, (short)0);
        }
    }
    
    public boolean isExternalEntity(final String s) {
        final Entity entity = this.fEntities.get(s);
        return entity != null && entity.isExternal();
    }
    
    public boolean isEntityDeclInExternalSubset(final String s) {
        final Entity entity = this.fEntities.get(s);
        return entity != null && entity.isEntityDeclInExternalSubset();
    }
    
    public void addUnparsedEntity(final String s, final String s2, final String s3, final String s4, final String s5) {
        if (!this.fEntities.containsKey(s)) {
            this.fEntities.put(s, new ExternalEntity(s, new XMLEntityDescriptionImpl(s, s2, s3, s4, null), s5, this.fInExternalSubset));
        }
        else if (this.fWarnDuplicateEntityDef) {
            this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_DUPLICATE_ENTITY_DEFINITION", new Object[] { s }, (short)0);
        }
    }
    
    public boolean isUnparsedEntity(final String s) {
        final Entity entity = this.fEntities.get(s);
        return entity != null && entity.isUnparsed();
    }
    
    public boolean isDeclaredEntity(final String s) {
        return this.fEntities.get(s) != null;
    }
    
    public XMLInputSource resolveEntity(final XMLResourceIdentifier xmlResourceIdentifier) throws IOException, XNIException {
        if (xmlResourceIdentifier == null) {
            return null;
        }
        final String publicId = xmlResourceIdentifier.getPublicId();
        final String literalSystemId = xmlResourceIdentifier.getLiteralSystemId();
        String baseSystemId = xmlResourceIdentifier.getBaseSystemId();
        String expandedSystemId = xmlResourceIdentifier.getExpandedSystemId();
        boolean b = expandedSystemId == null;
        if (baseSystemId == null && this.fCurrentEntity != null && this.fCurrentEntity.entityLocation != null) {
            baseSystemId = this.fCurrentEntity.entityLocation.getExpandedSystemId();
            if (baseSystemId != null) {
                b = true;
            }
        }
        XMLInputSource resolveEntity = null;
        if (this.fEntityResolver != null) {
            if (b) {
                expandedSystemId = expandSystemId(literalSystemId, baseSystemId, false);
            }
            xmlResourceIdentifier.setBaseSystemId(baseSystemId);
            xmlResourceIdentifier.setExpandedSystemId(expandedSystemId);
            resolveEntity = this.fEntityResolver.resolveEntity(xmlResourceIdentifier);
        }
        if (resolveEntity == null) {
            resolveEntity = new XMLInputSource(publicId, literalSystemId, baseSystemId);
        }
        return resolveEntity;
    }
    
    public void startEntity(final String s, final boolean b) throws IOException, XNIException {
        final Entity entity = this.fEntities.get(s);
        if (entity == null) {
            if (this.fEntityHandler != null) {
                final String s2 = null;
                this.fResourceIdentifier.clear();
                this.fEntityAugs.removeAllItems();
                this.fEntityAugs.putItem("ENTITY_SKIPPED", Boolean.TRUE);
                this.fEntityHandler.startEntity(s, this.fResourceIdentifier, s2, this.fEntityAugs);
                this.fEntityAugs.removeAllItems();
                this.fEntityAugs.putItem("ENTITY_SKIPPED", Boolean.TRUE);
                this.fEntityHandler.endEntity(s, this.fEntityAugs);
            }
            return;
        }
        final boolean external = entity.isExternal();
        if (external && (this.fValidationManager == null || !this.fValidationManager.isCachedDTD())) {
            final boolean unparsed = entity.isUnparsed();
            final boolean startsWith = s.startsWith("%");
            final boolean b2 = !startsWith;
            if (unparsed || (b2 && !this.fExternalGeneralEntities) || (startsWith && !this.fExternalParameterEntities)) {
                if (this.fEntityHandler != null) {
                    this.fResourceIdentifier.clear();
                    final String s3 = null;
                    final ExternalEntity externalEntity = (ExternalEntity)entity;
                    final String s4 = (externalEntity.entityLocation != null) ? externalEntity.entityLocation.getLiteralSystemId() : null;
                    final String s5 = (externalEntity.entityLocation != null) ? externalEntity.entityLocation.getBaseSystemId() : null;
                    this.fResourceIdentifier.setValues((externalEntity.entityLocation != null) ? externalEntity.entityLocation.getPublicId() : null, s4, s5, expandSystemId(s4, s5, false));
                    this.fEntityAugs.removeAllItems();
                    this.fEntityAugs.putItem("ENTITY_SKIPPED", Boolean.TRUE);
                    this.fEntityHandler.startEntity(s, this.fResourceIdentifier, s3, this.fEntityAugs);
                    this.fEntityAugs.removeAllItems();
                    this.fEntityAugs.putItem("ENTITY_SKIPPED", Boolean.TRUE);
                    this.fEntityHandler.endEntity(s, this.fEntityAugs);
                }
                return;
            }
        }
        int i;
        for (int n = i = this.fEntityStack.size(); i >= 0; --i) {
            if (((i == n) ? this.fCurrentEntity : ((Entity)this.fEntityStack.elementAt(i))).name == s) {
                final StringBuffer sb = new StringBuffer(s);
                for (int j = i + 1; j < n; ++j) {
                    final Entity entity2 = (Entity)this.fEntityStack.elementAt(j);
                    sb.append(" -> ");
                    sb.append(entity2.name);
                }
                sb.append(" -> ");
                sb.append(this.fCurrentEntity.name);
                sb.append(" -> ");
                sb.append(s);
                this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "RecursiveReference", new Object[] { s, sb.toString() }, (short)2);
                if (this.fEntityHandler != null) {
                    this.fResourceIdentifier.clear();
                    final String s6 = null;
                    if (external) {
                        final ExternalEntity externalEntity2 = (ExternalEntity)entity;
                        final String s7 = (externalEntity2.entityLocation != null) ? externalEntity2.entityLocation.getLiteralSystemId() : null;
                        final String s8 = (externalEntity2.entityLocation != null) ? externalEntity2.entityLocation.getBaseSystemId() : null;
                        this.fResourceIdentifier.setValues((externalEntity2.entityLocation != null) ? externalEntity2.entityLocation.getPublicId() : null, s7, s8, expandSystemId(s7, s8, false));
                    }
                    this.fEntityAugs.removeAllItems();
                    this.fEntityAugs.putItem("ENTITY_SKIPPED", Boolean.TRUE);
                    this.fEntityHandler.startEntity(s, this.fResourceIdentifier, s6, this.fEntityAugs);
                    this.fEntityAugs.removeAllItems();
                    this.fEntityAugs.putItem("ENTITY_SKIPPED", Boolean.TRUE);
                    this.fEntityHandler.endEntity(s, this.fEntityAugs);
                }
                return;
            }
        }
        XMLInputSource resolveEntity;
        if (external) {
            resolveEntity = this.resolveEntity(((ExternalEntity)entity).entityLocation);
        }
        else {
            resolveEntity = new XMLInputSource(null, null, null, new StringReader(((InternalEntity)entity).text), null);
        }
        this.startEntity(s, resolveEntity, b, external);
    }
    
    public void startDocumentEntity(final XMLInputSource xmlInputSource) throws IOException, XNIException {
        this.startEntity(XMLEntityManager.XMLEntity, xmlInputSource, false, true);
    }
    
    public void startDTDEntity(final XMLInputSource xmlInputSource) throws IOException, XNIException {
        this.startEntity(XMLEntityManager.DTDEntity, xmlInputSource, false, true);
    }
    
    public void startExternalSubset() {
        this.fInExternalSubset = true;
    }
    
    public void endExternalSubset() {
        this.fInExternalSubset = false;
    }
    
    public void startEntity(final String s, final XMLInputSource xmlInputSource, final boolean b, final boolean b2) throws IOException, XNIException {
        final String setupCurrentEntity = this.setupCurrentEntity(s, xmlInputSource, b, b2);
        if (this.fSecurityManager != null) {
            this.fEntityExpansionCount += this.getParamEntityRefCount(s);
            if (this.fEntityExpansionCount++ > this.fEntityExpansionLimit) {
                this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "EntityExpansionLimitExceeded", new Object[] { new Integer(this.fEntityExpansionLimit) }, (short)2);
                this.fEntityExpansionCount = 0;
            }
        }
        if (this.fEntityHandler != null) {
            this.fEntityHandler.startEntity(s, this.fResourceIdentifier, setupCurrentEntity, null);
        }
    }
    
    public String setupCurrentEntity(final String s, final XMLInputSource xmlInputSource, final boolean b, final boolean b2) throws IOException, XNIException {
        final String publicId = xmlInputSource.getPublicId();
        String systemId = xmlInputSource.getSystemId();
        String baseSystemId = xmlInputSource.getBaseSystemId();
        String s2 = xmlInputSource.getEncoding();
        final boolean encodingExternallySpecified = s2 != null;
        Boolean b3 = null;
        this.fTempByteBuffer = null;
        InputStream inputStream = null;
        Reader reader = xmlInputSource.getCharacterStream();
        String expandSystemId = expandSystemId(systemId, baseSystemId, this.fStrictURI);
        if (baseSystemId == null) {
            baseSystemId = expandSystemId;
        }
        if (reader == null) {
            InputStream inputStream2 = xmlInputSource.getByteStream();
            if (inputStream2 == null) {
                final URLConnection openConnection = new URL(expandSystemId).openConnection();
                if (!(openConnection instanceof HttpURLConnection)) {
                    inputStream2 = openConnection.getInputStream();
                }
                else {
                    boolean followHTTPRedirects = true;
                    if (xmlInputSource instanceof HTTPInputSource) {
                        final HttpURLConnection httpURLConnection = (HttpURLConnection)openConnection;
                        final HTTPInputSource httpInputSource = (HTTPInputSource)xmlInputSource;
                        final Iterator httpRequestProperties = httpInputSource.getHTTPRequestProperties();
                        while (httpRequestProperties.hasNext()) {
                            final Map.Entry entry = httpRequestProperties.next();
                            httpURLConnection.setRequestProperty((String)entry.getKey(), (String)entry.getValue());
                        }
                        followHTTPRedirects = httpInputSource.getFollowHTTPRedirects();
                        if (!followHTTPRedirects) {
                            httpURLConnection.setInstanceFollowRedirects(followHTTPRedirects);
                        }
                    }
                    inputStream2 = openConnection.getInputStream();
                    if (followHTTPRedirects) {
                        final String string = openConnection.getURL().toString();
                        if (!string.equals(expandSystemId)) {
                            systemId = string;
                            expandSystemId = string;
                        }
                    }
                }
            }
            final RewindableInputStream rewindableInputStream = (RewindableInputStream)(inputStream = new RewindableInputStream(inputStream2));
            if (s2 == null) {
                final byte[] array = new byte[4];
                int i;
                for (i = 0; i < 4; ++i) {
                    array[i] = (byte)rewindableInputStream.readAndBuffer();
                }
                if (i == 4) {
                    final EncodingInfo encodingInfo = this.getEncodingInfo(array, i);
                    s2 = encodingInfo.autoDetectedEncoding;
                    final String readerEncoding = encodingInfo.readerEncoding;
                    final Boolean isBigEndian = encodingInfo.isBigEndian;
                    inputStream.reset();
                    if (encodingInfo.hasBOM) {
                        if (readerEncoding == "UTF-8") {
                            inputStream.skip(3L);
                        }
                        else if (readerEncoding == "UTF-16") {
                            inputStream.skip(2L);
                        }
                    }
                    reader = this.createReader(inputStream, readerEncoding, isBigEndian);
                }
                else {
                    reader = this.createReader(inputStream, s2, b3);
                }
            }
            else {
                s2 = s2.toUpperCase(Locale.ENGLISH);
                if (s2.equals("UTF-8")) {
                    final int[] array2 = new int[3];
                    int j;
                    for (j = 0; j < 3; ++j) {
                        array2[j] = rewindableInputStream.readAndBuffer();
                        if (array2[j] == -1) {
                            break;
                        }
                    }
                    if (j == 3) {
                        if (array2[0] != 239 || array2[1] != 187 || array2[2] != 191) {
                            inputStream.reset();
                        }
                    }
                    else {
                        inputStream.reset();
                    }
                    reader = this.createReader(inputStream, "UTF-8", b3);
                }
                else if (s2.equals("UTF-16")) {
                    final int[] array3 = new int[4];
                    int k;
                    for (k = 0; k < 4; ++k) {
                        array3[k] = rewindableInputStream.readAndBuffer();
                        if (array3[k] == -1) {
                            break;
                        }
                    }
                    inputStream.reset();
                    if (k >= 2) {
                        final int n = array3[0];
                        final int n2 = array3[1];
                        if (n == 254 && n2 == 255) {
                            b3 = Boolean.TRUE;
                            inputStream.skip(2L);
                        }
                        else if (n == 255 && n2 == 254) {
                            b3 = Boolean.FALSE;
                            inputStream.skip(2L);
                        }
                        else if (k == 4) {
                            final int n3 = array3[2];
                            final int n4 = array3[3];
                            if (n == 0 && n2 == 60 && n3 == 0 && n4 == 63) {
                                b3 = Boolean.TRUE;
                            }
                            if (n == 60 && n2 == 0 && n3 == 63 && n4 == 0) {
                                b3 = Boolean.FALSE;
                            }
                        }
                    }
                    reader = this.createReader(inputStream, "UTF-16", b3);
                }
                else if (s2.equals("ISO-10646-UCS-4")) {
                    final int[] array4 = new int[4];
                    int l;
                    for (l = 0; l < 4; ++l) {
                        array4[l] = rewindableInputStream.readAndBuffer();
                        if (array4[l] == -1) {
                            break;
                        }
                    }
                    inputStream.reset();
                    if (l == 4) {
                        if (array4[0] == 0 && array4[1] == 0 && array4[2] == 0 && array4[3] == 60) {
                            b3 = Boolean.TRUE;
                        }
                        else if (array4[0] == 60 && array4[1] == 0 && array4[2] == 0 && array4[3] == 0) {
                            b3 = Boolean.FALSE;
                        }
                    }
                    reader = this.createReader(inputStream, s2, b3);
                }
                else if (s2.equals("ISO-10646-UCS-2")) {
                    final int[] array5 = new int[4];
                    int n5;
                    for (n5 = 0; n5 < 4; ++n5) {
                        array5[n5] = rewindableInputStream.readAndBuffer();
                        if (array5[n5] == -1) {
                            break;
                        }
                    }
                    inputStream.reset();
                    if (n5 == 4) {
                        if (array5[0] == 0 && array5[1] == 60 && array5[2] == 0 && array5[3] == 63) {
                            b3 = Boolean.TRUE;
                        }
                        else if (array5[0] == 60 && array5[1] == 0 && array5[2] == 63 && array5[3] == 0) {
                            b3 = Boolean.FALSE;
                        }
                    }
                    reader = this.createReader(inputStream, s2, b3);
                }
                else {
                    reader = this.createReader(inputStream, s2, b3);
                }
            }
        }
        this.fReaderStack.push(reader);
        if (this.fCurrentEntity != null) {
            this.fEntityStack.push(this.fCurrentEntity);
        }
        (this.fCurrentEntity = new ScannedEntity(s, new XMLResourceIdentifierImpl(publicId, systemId, baseSystemId, expandSystemId), inputStream, reader, this.fTempByteBuffer, s2, b, false, b2)).setEncodingExternallySpecified(encodingExternallySpecified);
        this.fEntityScanner.setCurrentEntity(this.fCurrentEntity);
        this.fResourceIdentifier.setValues(publicId, systemId, baseSystemId, expandSystemId);
        return s2;
    }
    
    public void setScannerVersion(final short n) {
        if (n == 1) {
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
    
    public void closeReaders() {
        for (int i = this.fReaderStack.size() - 1; i >= 0; --i) {
            try {
                this.fReaderStack.pop().close();
            }
            catch (final IOException ex) {}
        }
    }
    
    public void reset(final XMLComponentManager xmlComponentManager) throws XMLConfigurationException {
        boolean feature;
        try {
            feature = xmlComponentManager.getFeature("http://apache.org/xml/features/internal/parser-settings");
        }
        catch (final XMLConfigurationException ex) {
            feature = true;
        }
        if (!feature) {
            this.reset();
            return;
        }
        try {
            this.fValidation = xmlComponentManager.getFeature("http://xml.org/sax/features/validation");
        }
        catch (final XMLConfigurationException ex2) {
            this.fValidation = false;
        }
        try {
            this.fExternalGeneralEntities = xmlComponentManager.getFeature("http://xml.org/sax/features/external-general-entities");
        }
        catch (final XMLConfigurationException ex3) {
            this.fExternalGeneralEntities = true;
        }
        try {
            this.fExternalParameterEntities = xmlComponentManager.getFeature("http://xml.org/sax/features/external-parameter-entities");
        }
        catch (final XMLConfigurationException ex4) {
            this.fExternalParameterEntities = true;
        }
        try {
            this.fAllowJavaEncodings = xmlComponentManager.getFeature("http://apache.org/xml/features/allow-java-encodings");
        }
        catch (final XMLConfigurationException ex5) {
            this.fAllowJavaEncodings = false;
        }
        try {
            this.fWarnDuplicateEntityDef = xmlComponentManager.getFeature("http://apache.org/xml/features/warn-on-duplicate-entitydef");
        }
        catch (final XMLConfigurationException ex6) {
            this.fWarnDuplicateEntityDef = false;
        }
        try {
            this.fStrictURI = xmlComponentManager.getFeature("http://apache.org/xml/features/standard-uri-conformant");
        }
        catch (final XMLConfigurationException ex7) {
            this.fStrictURI = false;
        }
        this.fSymbolTable = (SymbolTable)xmlComponentManager.getProperty("http://apache.org/xml/properties/internal/symbol-table");
        this.fErrorReporter = (XMLErrorReporter)xmlComponentManager.getProperty("http://apache.org/xml/properties/internal/error-reporter");
        try {
            this.fEntityResolver = (XMLEntityResolver)xmlComponentManager.getProperty("http://apache.org/xml/properties/internal/entity-resolver");
        }
        catch (final XMLConfigurationException ex8) {
            this.fEntityResolver = null;
        }
        try {
            this.fValidationManager = (ValidationManager)xmlComponentManager.getProperty("http://apache.org/xml/properties/internal/validation-manager");
        }
        catch (final XMLConfigurationException ex9) {
            this.fValidationManager = null;
        }
        try {
            this.fSecurityManager = (SecurityManager)xmlComponentManager.getProperty("http://apache.org/xml/properties/security-manager");
        }
        catch (final XMLConfigurationException ex10) {
            this.fSecurityManager = null;
        }
        this.reset();
    }
    
    public void reset() {
        this.fEntityExpansionLimit = ((this.fSecurityManager != null) ? this.fSecurityManager.getEntityExpansionLimit() : 0);
        this.fStandalone = false;
        this.fHasPEReferences = false;
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
        if (this.fDeclaredEntities != null) {
            final Iterator iterator = this.fDeclaredEntities.entrySet().iterator();
            while (iterator.hasNext()) {
                final Map.Entry entry = (Map.Entry)iterator.next();
                this.fEntities.put(entry.getKey(), entry.getValue());
            }
        }
        this.fEntityHandler = null;
    }
    
    public String[] getRecognizedFeatures() {
        return XMLEntityManager.RECOGNIZED_FEATURES.clone();
    }
    
    public void setFeature(final String s, final boolean fAllowJavaEncodings) throws XMLConfigurationException {
        if (s.startsWith("http://apache.org/xml/features/") && s.length() - "http://apache.org/xml/features/".length() == "allow-java-encodings".length() && s.endsWith("allow-java-encodings")) {
            this.fAllowJavaEncodings = fAllowJavaEncodings;
        }
    }
    
    public String[] getRecognizedProperties() {
        return XMLEntityManager.RECOGNIZED_PROPERTIES.clone();
    }
    
    public void setProperty(final String s, final Object o) throws XMLConfigurationException {
        if (s.startsWith("http://apache.org/xml/properties/")) {
            final int n = s.length() - "http://apache.org/xml/properties/".length();
            if (n == "internal/symbol-table".length() && s.endsWith("internal/symbol-table")) {
                this.fSymbolTable = (SymbolTable)o;
                return;
            }
            if (n == "internal/error-reporter".length() && s.endsWith("internal/error-reporter")) {
                this.fErrorReporter = (XMLErrorReporter)o;
                return;
            }
            if (n == "internal/entity-resolver".length() && s.endsWith("internal/entity-resolver")) {
                this.fEntityResolver = (XMLEntityResolver)o;
                return;
            }
            if (n == "input-buffer-size".length() && s.endsWith("input-buffer-size")) {
                final Integer n2 = (Integer)o;
                if (n2 != null && n2 > 64) {
                    this.fBufferSize = n2;
                    this.fEntityScanner.setBufferSize(this.fBufferSize);
                    this.fSmallByteBufferPool.setBufferSize(this.fBufferSize);
                    this.fLargeByteBufferPool.setBufferSize(this.fBufferSize << 1);
                    this.fCharacterBufferPool.setExternalBufferSize(this.fBufferSize);
                }
            }
            if (n == "security-manager".length() && s.endsWith("security-manager")) {
                this.fSecurityManager = (SecurityManager)o;
                this.fEntityExpansionLimit = ((this.fSecurityManager != null) ? this.fSecurityManager.getEntityExpansionLimit() : 0);
            }
        }
    }
    
    public Boolean getFeatureDefault(final String s) {
        for (int i = 0; i < XMLEntityManager.RECOGNIZED_FEATURES.length; ++i) {
            if (XMLEntityManager.RECOGNIZED_FEATURES[i].equals(s)) {
                return XMLEntityManager.FEATURE_DEFAULTS[i];
            }
        }
        return null;
    }
    
    public Object getPropertyDefault(final String s) {
        for (int i = 0; i < XMLEntityManager.RECOGNIZED_PROPERTIES.length; ++i) {
            if (XMLEntityManager.RECOGNIZED_PROPERTIES[i].equals(s)) {
                return XMLEntityManager.PROPERTY_DEFAULTS[i];
            }
        }
        return null;
    }
    
    private static synchronized URI getUserDir() throws URI.MalformedURIException {
        String gUserDir = "";
        try {
            gUserDir = AccessController.doPrivileged((PrivilegedAction<String>)XMLEntityManager.GET_USER_DIR_SYSTEM_PROPERTY);
        }
        catch (final SecurityException ex) {}
        if (gUserDir.length() == 0) {
            return new URI("file", "", "", null, null);
        }
        if (XMLEntityManager.gUserDirURI != null && gUserDir.equals(XMLEntityManager.gUserDir)) {
            return XMLEntityManager.gUserDirURI;
        }
        XMLEntityManager.gUserDir = gUserDir;
        final String replace = gUserDir.replace(File.separatorChar, '/');
        final int length = replace.length();
        final StringBuffer sb = new StringBuffer(length * 3);
        if (length >= 2 && replace.charAt(1) == ':') {
            final char upperCase = Character.toUpperCase(replace.charAt(0));
            if (upperCase >= 'A' && upperCase <= 'Z') {
                sb.append('/');
            }
        }
        int i;
        for (i = 0; i < length; ++i) {
            final char char1 = replace.charAt(i);
            if (char1 >= '\u0080') {
                break;
            }
            if (XMLEntityManager.gNeedEscaping[char1]) {
                sb.append('%');
                sb.append(XMLEntityManager.gAfterEscaping1[char1]);
                sb.append(XMLEntityManager.gAfterEscaping2[char1]);
            }
            else {
                sb.append(char1);
            }
        }
        if (i < length) {
            byte[] bytes;
            try {
                bytes = replace.substring(i).getBytes("UTF-8");
            }
            catch (final UnsupportedEncodingException ex2) {
                return new URI("file", "", replace, null, null);
            }
            for (final byte b : bytes) {
                if (b < 0) {
                    final int n = b + 256;
                    sb.append('%');
                    sb.append(XMLEntityManager.gHexChs[n >> 4]);
                    sb.append(XMLEntityManager.gHexChs[n & 0xF]);
                }
                else if (XMLEntityManager.gNeedEscaping[b]) {
                    sb.append('%');
                    sb.append(XMLEntityManager.gAfterEscaping1[b]);
                    sb.append(XMLEntityManager.gAfterEscaping2[b]);
                }
                else {
                    sb.append((char)b);
                }
            }
        }
        if (!replace.endsWith("/")) {
            sb.append('/');
        }
        return XMLEntityManager.gUserDirURI = new URI("file", "", sb.toString(), null, null);
    }
    
    public static void absolutizeAgainstUserDir(final URI uri) throws URI.MalformedURIException {
        uri.absolutize(getUserDir());
    }
    
    public static String expandSystemId(final String s, final String s2, final boolean b) throws URI.MalformedURIException {
        if (s == null) {
            return null;
        }
        if (b) {
            return expandSystemIdStrictOn(s, s2);
        }
        try {
            return expandSystemIdStrictOff(s, s2);
        }
        catch (final URI.MalformedURIException ex) {
            if (s.length() == 0) {
                return s;
            }
            final String fixURI = fixURI(s);
            URI uri = null;
            try {
                URI userDir;
                if (s2 == null || s2.length() == 0 || s2.equals(s)) {
                    userDir = getUserDir();
                }
                else {
                    try {
                        userDir = new URI(fixURI(s2).trim());
                    }
                    catch (final URI.MalformedURIException ex2) {
                        if (s2.indexOf(58) != -1) {
                            userDir = new URI("file", "", fixURI(s2).trim(), null, null);
                        }
                        else {
                            userDir = new URI(getUserDir(), fixURI(s2));
                        }
                    }
                }
                uri = new URI(userDir, fixURI.trim());
            }
            catch (final Exception ex3) {}
            if (uri == null) {
                return s;
            }
            return uri.toString();
        }
    }
    
    private static String expandSystemIdStrictOn(final String s, final String s2) throws URI.MalformedURIException {
        final URI uri = new URI(s, true);
        if (uri.isAbsoluteURI()) {
            return s;
        }
        URI userDir;
        if (s2 == null || s2.length() == 0) {
            userDir = getUserDir();
        }
        else {
            userDir = new URI(s2, true);
            if (!userDir.isAbsoluteURI()) {
                userDir.absolutize(getUserDir());
            }
        }
        uri.absolutize(userDir);
        return uri.toString();
    }
    
    private static String expandSystemIdStrictOff(final String s, final String s2) throws URI.MalformedURIException {
        final URI uri = new URI(s, true);
        if (!uri.isAbsoluteURI()) {
            URI userDir;
            if (s2 == null || s2.length() == 0) {
                userDir = getUserDir();
            }
            else {
                userDir = new URI(s2, true);
                if (!userDir.isAbsoluteURI()) {
                    userDir.absolutize(getUserDir());
                }
            }
            uri.absolutize(userDir);
            return uri.toString();
        }
        if (uri.getScheme().length() > 1) {
            return s;
        }
        throw new URI.MalformedURIException();
    }
    
    public static OutputStream createOutputStream(final String s) throws IOException {
        final String expandSystemId = expandSystemId(s, null, true);
        final URL url = new URL((expandSystemId != null) ? expandSystemId : s);
        final String protocol = url.getProtocol();
        final String host = url.getHost();
        OutputStream outputStream;
        if (protocol.equals("file") && (host == null || host.length() == 0 || host.equals("localhost"))) {
            final File file = new File(getPathWithoutEscapes(url.getPath()));
            if (!file.exists()) {
                final File parentFile = file.getParentFile();
                if (parentFile != null && !parentFile.exists()) {
                    parentFile.mkdirs();
                }
            }
            outputStream = new FileOutputStream(file);
        }
        else {
            final URLConnection openConnection = url.openConnection();
            openConnection.setDoInput(false);
            openConnection.setDoOutput(true);
            openConnection.setUseCaches(false);
            if (openConnection instanceof HttpURLConnection) {
                ((HttpURLConnection)openConnection).setRequestMethod("PUT");
            }
            outputStream = openConnection.getOutputStream();
        }
        return outputStream;
    }
    
    private static String getPathWithoutEscapes(final String s) {
        if (s != null && s.length() != 0 && s.indexOf(37) != -1) {
            final StringTokenizer stringTokenizer = new StringTokenizer(s, "%");
            final StringBuffer sb = new StringBuffer(s.length());
            final int countTokens = stringTokenizer.countTokens();
            sb.append(stringTokenizer.nextToken());
            for (int i = 1; i < countTokens; ++i) {
                final String nextToken = stringTokenizer.nextToken();
                sb.append((char)(int)Integer.valueOf(nextToken.substring(0, 2), 16));
                sb.append(nextToken.substring(2));
            }
            return sb.toString();
        }
        return s;
    }
    
    void endEntity() throws XNIException {
        if (this.fEntityHandler != null) {
            this.fEntityHandler.endEntity(this.fCurrentEntity.name, null);
        }
        try {
            this.fCurrentEntity.reader.close();
        }
        catch (final IOException ex) {}
        if (!this.fReaderStack.isEmpty()) {
            this.fReaderStack.pop();
        }
        this.fCharacterBufferPool.returnBuffer(this.fCurrentEntity.fCharacterBuffer);
        if (this.fCurrentEntity.fByteBuffer != null) {
            if (this.fCurrentEntity.fByteBuffer.length == this.fBufferSize) {
                this.fSmallByteBufferPool.returnBuffer(this.fCurrentEntity.fByteBuffer);
            }
            else {
                this.fLargeByteBufferPool.returnBuffer(this.fCurrentEntity.fByteBuffer);
            }
        }
        this.fCurrentEntity = ((this.fEntityStack.size() > 0) ? this.fEntityStack.pop() : null);
        this.fEntityScanner.setCurrentEntity(this.fCurrentEntity);
    }
    
    protected EncodingInfo getEncodingInfo(final byte[] array, final int n) {
        if (n < 2) {
            return EncodingInfo.UTF_8;
        }
        final int n2 = array[0] & 0xFF;
        final int n3 = array[1] & 0xFF;
        if (n2 == 254 && n3 == 255) {
            return EncodingInfo.UTF_16_BIG_ENDIAN_WITH_BOM;
        }
        if (n2 == 255 && n3 == 254) {
            return EncodingInfo.UTF_16_LITTLE_ENDIAN_WITH_BOM;
        }
        if (n < 3) {
            return EncodingInfo.UTF_8;
        }
        final int n4 = array[2] & 0xFF;
        if (n2 == 239 && n3 == 187 && n4 == 191) {
            return EncodingInfo.UTF_8_WITH_BOM;
        }
        if (n < 4) {
            return EncodingInfo.UTF_8;
        }
        final int n5 = array[3] & 0xFF;
        if (n2 == 0 && n3 == 0 && n4 == 0 && n5 == 60) {
            return EncodingInfo.UCS_4_BIG_ENDIAN;
        }
        if (n2 == 60 && n3 == 0 && n4 == 0 && n5 == 0) {
            return EncodingInfo.UCS_4_LITTLE_ENDIAN;
        }
        if (n2 == 0 && n3 == 0 && n4 == 60 && n5 == 0) {
            return EncodingInfo.UCS_4_UNUSUAL_BYTE_ORDER;
        }
        if (n2 == 0 && n3 == 60 && n4 == 0 && n5 == 0) {
            return EncodingInfo.UCS_4_UNUSUAL_BYTE_ORDER;
        }
        if (n2 == 0 && n3 == 60 && n4 == 0 && n5 == 63) {
            return EncodingInfo.UTF_16_BIG_ENDIAN;
        }
        if (n2 == 60 && n3 == 0 && n4 == 63 && n5 == 0) {
            return EncodingInfo.UTF_16_LITTLE_ENDIAN;
        }
        if (n2 == 76 && n3 == 111 && n4 == 167 && n5 == 148) {
            return EncodingInfo.EBCDIC;
        }
        return EncodingInfo.UTF_8;
    }
    
    protected Reader createReader(final InputStream inputStream, final String s, final Boolean b) throws IOException {
        if (s == "UTF-8" || s == null) {
            return this.createUTF8Reader(inputStream);
        }
        if (s == "UTF-16" && b != null) {
            return this.createUTF16Reader(inputStream, b);
        }
        final String upperCase = s.toUpperCase(Locale.ENGLISH);
        if (upperCase.equals("UTF-8")) {
            return this.createUTF8Reader(inputStream);
        }
        if (upperCase.equals("UTF-16BE")) {
            return this.createUTF16Reader(inputStream, true);
        }
        if (upperCase.equals("UTF-16LE")) {
            return this.createUTF16Reader(inputStream, false);
        }
        if (upperCase.equals("ISO-10646-UCS-4")) {
            if (b != null) {
                if (b) {
                    return new UCSReader(inputStream, (short)8);
                }
                return new UCSReader(inputStream, (short)4);
            }
            else {
                this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "EncodingByteOrderUnsupported", new Object[] { s }, (short)2);
            }
        }
        if (upperCase.equals("ISO-10646-UCS-2")) {
            if (b != null) {
                if (b) {
                    return new UCSReader(inputStream, (short)2);
                }
                return new UCSReader(inputStream, (short)1);
            }
            else {
                this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "EncodingByteOrderUnsupported", new Object[] { s }, (short)2);
            }
        }
        final boolean validIANAEncoding = XMLChar.isValidIANAEncoding(s);
        final boolean validJavaEncoding = XMLChar.isValidJavaEncoding(s);
        if (!validIANAEncoding || (this.fAllowJavaEncodings && !validJavaEncoding)) {
            this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "EncodingDeclInvalid", new Object[] { s }, (short)2);
            return this.createLatin1Reader(inputStream);
        }
        String iana2JavaMapping = EncodingMap.getIANA2JavaMapping(upperCase);
        if (iana2JavaMapping == null) {
            if (!this.fAllowJavaEncodings) {
                this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "EncodingDeclInvalid", new Object[] { s }, (short)2);
                return this.createLatin1Reader(inputStream);
            }
            iana2JavaMapping = s;
        }
        else {
            if (iana2JavaMapping.equals("ASCII")) {
                return this.createASCIIReader(inputStream);
            }
            if (iana2JavaMapping.equals("ISO8859_1")) {
                return this.createLatin1Reader(inputStream);
            }
        }
        return new InputStreamReader(inputStream, iana2JavaMapping);
    }
    
    private Reader createUTF8Reader(final InputStream inputStream) {
        if (this.fTempByteBuffer == null) {
            this.fTempByteBuffer = this.fSmallByteBufferPool.getBuffer();
        }
        return new UTF8Reader(inputStream, this.fTempByteBuffer, this.fErrorReporter.getMessageFormatter("http://www.w3.org/TR/1998/REC-xml-19980210"), this.fErrorReporter.getLocale());
    }
    
    private Reader createUTF16Reader(final InputStream inputStream, final boolean b) {
        if (this.fTempByteBuffer == null) {
            this.fTempByteBuffer = this.fLargeByteBufferPool.getBuffer();
        }
        else if (this.fTempByteBuffer.length == this.fBufferSize) {
            this.fSmallByteBufferPool.returnBuffer(this.fTempByteBuffer);
            this.fTempByteBuffer = this.fLargeByteBufferPool.getBuffer();
        }
        return new UTF16Reader(inputStream, this.fTempByteBuffer, b, this.fErrorReporter.getMessageFormatter("http://www.w3.org/TR/1998/REC-xml-19980210"), this.fErrorReporter.getLocale());
    }
    
    private Reader createASCIIReader(final InputStream inputStream) {
        if (this.fTempByteBuffer == null) {
            this.fTempByteBuffer = this.fSmallByteBufferPool.getBuffer();
        }
        return new ASCIIReader(inputStream, this.fTempByteBuffer, this.fErrorReporter.getMessageFormatter("http://www.w3.org/TR/1998/REC-xml-19980210"), this.fErrorReporter.getLocale());
    }
    
    private Reader createLatin1Reader(final InputStream inputStream) {
        if (this.fTempByteBuffer == null) {
            this.fTempByteBuffer = this.fSmallByteBufferPool.getBuffer();
        }
        return new Latin1Reader(inputStream, this.fTempByteBuffer);
    }
    
    protected static String fixURI(String s) {
        s = s.replace(File.separatorChar, '/');
        StringBuffer sb = null;
        if (s.length() >= 2) {
            final char char1 = s.charAt(1);
            if (char1 == ':') {
                final char upperCase = Character.toUpperCase(s.charAt(0));
                if (upperCase >= 'A' && upperCase <= 'Z') {
                    sb = new StringBuffer(s.length() + 8);
                    sb.append("file:///");
                }
            }
            else if (char1 == '/' && s.charAt(0) == '/') {
                sb = new StringBuffer(s.length() + 5);
                sb.append("file:");
            }
        }
        final int index = s.indexOf(32);
        if (index < 0) {
            if (sb != null) {
                sb.append(s);
                s = sb.toString();
            }
        }
        else {
            if (sb == null) {
                sb = new StringBuffer(s.length());
            }
            for (int i = 0; i < index; ++i) {
                sb.append(s.charAt(i));
            }
            sb.append("%20");
            for (int j = index + 1; j < s.length(); ++j) {
                if (s.charAt(j) == ' ') {
                    sb.append("%20");
                }
                else {
                    sb.append(s.charAt(j));
                }
            }
            s = sb.toString();
        }
        return s;
    }
    
    Hashtable getDeclaredEntities() {
        return this.fEntities;
    }
    
    static final void print(final ScannedEntity scannedEntity) {
    }
    
    static {
        RECOGNIZED_FEATURES = new String[] { "http://xml.org/sax/features/validation", "http://xml.org/sax/features/external-general-entities", "http://xml.org/sax/features/external-parameter-entities", "http://apache.org/xml/features/allow-java-encodings", "http://apache.org/xml/features/warn-on-duplicate-entitydef", "http://apache.org/xml/features/standard-uri-conformant" };
        FEATURE_DEFAULTS = new Boolean[] { null, Boolean.TRUE, Boolean.TRUE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE };
        RECOGNIZED_PROPERTIES = new String[] { "http://apache.org/xml/properties/internal/symbol-table", "http://apache.org/xml/properties/internal/error-reporter", "http://apache.org/xml/properties/internal/entity-resolver", "http://apache.org/xml/properties/internal/validation-manager", "http://apache.org/xml/properties/input-buffer-size", "http://apache.org/xml/properties/security-manager" };
        PROPERTY_DEFAULTS = new Object[] { null, null, null, null, new Integer(2048), null };
        XMLEntity = "[xml]".intern();
        DTDEntity = "[dtd]".intern();
        gNeedEscaping = new boolean[128];
        gAfterEscaping1 = new char[128];
        gAfterEscaping2 = new char[128];
        gHexChs = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
        for (int i = 0; i <= 31; ++i) {
            XMLEntityManager.gNeedEscaping[i] = true;
            XMLEntityManager.gAfterEscaping1[i] = XMLEntityManager.gHexChs[i >> 4];
            XMLEntityManager.gAfterEscaping2[i] = XMLEntityManager.gHexChs[i & 0xF];
        }
        XMLEntityManager.gNeedEscaping[127] = true;
        XMLEntityManager.gAfterEscaping1[127] = '7';
        XMLEntityManager.gAfterEscaping2[127] = 'F';
        for (final char c : new char[] { ' ', '<', '>', '#', '%', '\"', '{', '}', '|', '\\', '^', '~', '[', ']', '`' }) {
            XMLEntityManager.gNeedEscaping[c] = true;
            XMLEntityManager.gAfterEscaping1[c] = XMLEntityManager.gHexChs[c >> 4];
            XMLEntityManager.gAfterEscaping2[c] = XMLEntityManager.gHexChs[c & '\u000f'];
        }
        XMLEntityManager.GET_USER_DIR_SYSTEM_PROPERTY = new PrivilegedAction() {
            public Object run() {
                return System.getProperty("user.dir");
            }
        };
    }
    
    private static final class ByteBufferPool
    {
        private static final int DEFAULT_POOL_SIZE = 3;
        private int fPoolSize;
        private int fBufferSize;
        private byte[][] fByteBufferPool;
        private int fDepth;
        
        public ByteBufferPool(final int n) {
            this(3, n);
        }
        
        public ByteBufferPool(final int fPoolSize, final int fBufferSize) {
            this.fPoolSize = fPoolSize;
            this.fBufferSize = fBufferSize;
            this.fByteBufferPool = new byte[this.fPoolSize][];
            this.fDepth = 0;
        }
        
        public byte[] getBuffer() {
            byte[] array;
            if (this.fDepth > 0) {
                final byte[][] fByteBufferPool = this.fByteBufferPool;
                final int fDepth = this.fDepth - 1;
                this.fDepth = fDepth;
                array = fByteBufferPool[fDepth];
            }
            else {
                array = new byte[this.fBufferSize];
            }
            return array;
        }
        
        public void returnBuffer(final byte[] array) {
            if (this.fDepth < this.fByteBufferPool.length) {
                this.fByteBufferPool[this.fDepth++] = array;
            }
        }
        
        public void setBufferSize(final int fBufferSize) {
            this.fBufferSize = fBufferSize;
            this.fByteBufferPool = new byte[this.fPoolSize][];
            this.fDepth = 0;
        }
    }
    
    private static final class CharacterBuffer
    {
        private final char[] ch;
        private final boolean isExternal;
        
        public CharacterBuffer(final boolean isExternal, final int n) {
            this.isExternal = isExternal;
            this.ch = new char[n];
        }
    }
    
    private static final class CharacterBufferPool
    {
        private static final int DEFAULT_POOL_SIZE = 3;
        private CharacterBuffer[] fInternalBufferPool;
        private CharacterBuffer[] fExternalBufferPool;
        private int fExternalBufferSize;
        private int fInternalBufferSize;
        private int fPoolSize;
        private int fInternalTop;
        private int fExternalTop;
        
        public CharacterBufferPool(final int n, final int n2) {
            this(3, n, n2);
        }
        
        public CharacterBufferPool(final int fPoolSize, final int fExternalBufferSize, final int fInternalBufferSize) {
            this.fExternalBufferSize = fExternalBufferSize;
            this.fInternalBufferSize = fInternalBufferSize;
            this.fPoolSize = fPoolSize;
            this.init();
        }
        
        private void init() {
            this.fInternalBufferPool = new CharacterBuffer[this.fPoolSize];
            this.fExternalBufferPool = new CharacterBuffer[this.fPoolSize];
            this.fInternalTop = -1;
            this.fExternalTop = -1;
        }
        
        public CharacterBuffer getBuffer(final boolean b) {
            if (b) {
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
        
        public void returnBuffer(final CharacterBuffer characterBuffer) {
            if (characterBuffer.isExternal) {
                if (this.fExternalTop < this.fExternalBufferPool.length - 1) {
                    this.fExternalBufferPool[++this.fExternalTop] = characterBuffer;
                }
            }
            else if (this.fInternalTop < this.fInternalBufferPool.length - 1) {
                this.fInternalBufferPool[++this.fInternalTop] = characterBuffer;
            }
        }
        
        public void setExternalBufferSize(final int fExternalBufferSize) {
            this.fExternalBufferSize = fExternalBufferSize;
            this.fExternalBufferPool = new CharacterBuffer[this.fPoolSize];
            this.fExternalTop = -1;
        }
    }
    
    private static class EncodingInfo
    {
        public static final EncodingInfo UTF_8;
        public static final EncodingInfo UTF_8_WITH_BOM;
        public static final EncodingInfo UTF_16_BIG_ENDIAN;
        public static final EncodingInfo UTF_16_BIG_ENDIAN_WITH_BOM;
        public static final EncodingInfo UTF_16_LITTLE_ENDIAN;
        public static final EncodingInfo UTF_16_LITTLE_ENDIAN_WITH_BOM;
        public static final EncodingInfo UCS_4_BIG_ENDIAN;
        public static final EncodingInfo UCS_4_LITTLE_ENDIAN;
        public static final EncodingInfo UCS_4_UNUSUAL_BYTE_ORDER;
        public static final EncodingInfo EBCDIC;
        public final String autoDetectedEncoding;
        public final String readerEncoding;
        public final Boolean isBigEndian;
        public final boolean hasBOM;
        
        private EncodingInfo(final String s, final Boolean b, final boolean b2) {
            this(s, s, b, b2);
        }
        
        private EncodingInfo(final String autoDetectedEncoding, final String readerEncoding, final Boolean isBigEndian, final boolean hasBOM) {
            this.autoDetectedEncoding = autoDetectedEncoding;
            this.readerEncoding = readerEncoding;
            this.isBigEndian = isBigEndian;
            this.hasBOM = hasBOM;
        }
        
        static {
            UTF_8 = new EncodingInfo("UTF-8", null, false);
            UTF_8_WITH_BOM = new EncodingInfo("UTF-8", null, true);
            UTF_16_BIG_ENDIAN = new EncodingInfo("UTF-16BE", "UTF-16", Boolean.TRUE, false);
            UTF_16_BIG_ENDIAN_WITH_BOM = new EncodingInfo("UTF-16BE", "UTF-16", Boolean.TRUE, true);
            UTF_16_LITTLE_ENDIAN = new EncodingInfo("UTF-16LE", "UTF-16", Boolean.FALSE, false);
            UTF_16_LITTLE_ENDIAN_WITH_BOM = new EncodingInfo("UTF-16LE", "UTF-16", Boolean.FALSE, true);
            UCS_4_BIG_ENDIAN = new EncodingInfo("ISO-10646-UCS-4", Boolean.TRUE, false);
            UCS_4_LITTLE_ENDIAN = new EncodingInfo("ISO-10646-UCS-4", Boolean.FALSE, false);
            UCS_4_UNUSUAL_BYTE_ORDER = new EncodingInfo("ISO-10646-UCS-4", null, false);
            EBCDIC = new EncodingInfo("CP037", null, false);
        }
    }
    
    public abstract static class Entity
    {
        public String name;
        public boolean inExternalSubset;
        
        public Entity() {
            this.clear();
        }
        
        public Entity(final String name, final boolean inExternalSubset) {
            this.name = name;
            this.inExternalSubset = inExternalSubset;
        }
        
        public boolean isEntityDeclInExternalSubset() {
            return this.inExternalSubset;
        }
        
        public abstract boolean isExternal();
        
        public abstract boolean isUnparsed();
        
        public void clear() {
            this.name = null;
            this.inExternalSubset = false;
        }
        
        public void setValues(final Entity entity) {
            this.name = entity.name;
            this.inExternalSubset = entity.inExternalSubset;
        }
    }
    
    protected static class ExternalEntity extends Entity
    {
        public XMLResourceIdentifier entityLocation;
        public String notation;
        
        public ExternalEntity() {
            this.clear();
        }
        
        public ExternalEntity(final String s, final XMLResourceIdentifier entityLocation, final String notation, final boolean b) {
            super(s, b);
            this.entityLocation = entityLocation;
            this.notation = notation;
        }
        
        public final boolean isExternal() {
            return true;
        }
        
        public final boolean isUnparsed() {
            return this.notation != null;
        }
        
        public void clear() {
            super.clear();
            this.entityLocation = null;
            this.notation = null;
        }
        
        public void setValues(final Entity values) {
            super.setValues(values);
            this.entityLocation = null;
            this.notation = null;
        }
        
        public void setValues(final ExternalEntity values) {
            super.setValues(values);
            this.entityLocation = values.entityLocation;
            this.notation = values.notation;
        }
    }
    
    protected static class InternalEntity extends Entity
    {
        public String text;
        public int paramEntityRefs;
        
        public InternalEntity() {
            this.clear();
        }
        
        public InternalEntity(final String s, final String text, final boolean b) {
            super(s, b);
            this.text = text;
        }
        
        public InternalEntity(final String s, final String s2, final boolean b, final int paramEntityRefs) {
            this(s, s2, b);
            this.paramEntityRefs = paramEntityRefs;
        }
        
        public final boolean isExternal() {
            return false;
        }
        
        public final boolean isUnparsed() {
            return false;
        }
        
        public void clear() {
            super.clear();
            this.text = null;
        }
        
        public void setValues(final Entity values) {
            super.setValues(values);
            this.text = null;
        }
        
        public void setValues(final InternalEntity values) {
            super.setValues(values);
            this.text = values.text;
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
        
        public RewindableInputStream(final InputStream fInputStream) {
            this.fData = new byte[64];
            this.fInputStream = fInputStream;
            this.fStartOffset = 0;
            this.fEndOffset = -1;
            this.fOffset = 0;
            this.fLength = 0;
            this.fMark = 0;
        }
        
        public void setStartOffset(final int fStartOffset) {
            this.fStartOffset = fStartOffset;
        }
        
        public void rewind() {
            this.fOffset = this.fStartOffset;
        }
        
        public int readAndBuffer() throws IOException {
            if (this.fOffset == this.fData.length) {
                final byte[] fData = new byte[this.fOffset << 1];
                System.arraycopy(this.fData, 0, fData, 0, this.fOffset);
                this.fData = fData;
            }
            final int read = this.fInputStream.read();
            if (read == -1) {
                this.fEndOffset = this.fOffset;
                return -1;
            }
            this.fData[this.fLength++] = (byte)read;
            ++this.fOffset;
            return read & 0xFF;
        }
        
        public int read() throws IOException {
            if (this.fOffset < this.fLength) {
                return this.fData[this.fOffset++] & 0xFF;
            }
            if (this.fOffset == this.fEndOffset) {
                return -1;
            }
            if (XMLEntityManager.this.fCurrentEntity.mayReadChunks) {
                return this.fInputStream.read();
            }
            return this.readAndBuffer();
        }
        
        public int read(final byte[] array, final int n, int n2) throws IOException {
            final int n3 = this.fLength - this.fOffset;
            if (n3 != 0) {
                if (n2 < n3) {
                    if (n2 <= 0) {
                        return 0;
                    }
                }
                else {
                    n2 = n3;
                }
                if (array != null) {
                    System.arraycopy(this.fData, this.fOffset, array, n, n2);
                }
                this.fOffset += n2;
                return n2;
            }
            if (this.fOffset == this.fEndOffset) {
                return -1;
            }
            if (XMLEntityManager.this.fCurrentEntity.mayReadChunks) {
                return this.fInputStream.read(array, n, n2);
            }
            final int andBuffer = this.readAndBuffer();
            if (andBuffer == -1) {
                this.fEndOffset = this.fOffset;
                return -1;
            }
            array[n] = (byte)andBuffer;
            return 1;
        }
        
        public long skip(long n) throws IOException {
            if (n <= 0L) {
                return 0L;
            }
            final int n2 = this.fLength - this.fOffset;
            if (n2 == 0) {
                if (this.fOffset == this.fEndOffset) {
                    return 0L;
                }
                return this.fInputStream.skip(n);
            }
            else {
                if (n <= n2) {
                    this.fOffset += (int)n;
                    return n;
                }
                this.fOffset += n2;
                if (this.fOffset == this.fEndOffset) {
                    return n2;
                }
                n -= n2;
                return this.fInputStream.skip(n) + n2;
            }
        }
        
        public int available() throws IOException {
            final int n = this.fLength - this.fOffset;
            if (n != 0) {
                return n;
            }
            if (this.fOffset == this.fEndOffset) {
                return -1;
            }
            return XMLEntityManager.this.fCurrentEntity.mayReadChunks ? this.fInputStream.available() : 0;
        }
        
        public void mark(final int n) {
            this.fMark = this.fOffset;
        }
        
        public void reset() {
            this.fOffset = this.fMark;
        }
        
        public boolean markSupported() {
            return true;
        }
        
        public void close() throws IOException {
            if (this.fInputStream != null) {
                this.fInputStream.close();
                this.fInputStream = null;
            }
        }
    }
    
    public class ScannedEntity extends Entity
    {
        public InputStream stream;
        public Reader reader;
        public XMLResourceIdentifier entityLocation;
        public int lineNumber;
        public int columnNumber;
        public String encoding;
        boolean externallySpecifiedEncoding;
        public String xmlVersion;
        public boolean literal;
        public boolean isExternal;
        public char[] ch;
        public int position;
        public int baseCharOffset;
        public int startPosition;
        public int count;
        public boolean mayReadChunks;
        private CharacterBuffer fCharacterBuffer;
        private byte[] fByteBuffer;
        
        public ScannedEntity(final String s, final XMLResourceIdentifier entityLocation, final InputStream stream, final Reader reader, final byte[] fByteBuffer, final String encoding, final boolean literal, final boolean mayReadChunks, final boolean isExternal) {
            super(s, XMLEntityManager.this.fInExternalSubset);
            this.lineNumber = 1;
            this.columnNumber = 1;
            this.externallySpecifiedEncoding = false;
            this.xmlVersion = "1.0";
            this.ch = null;
            this.entityLocation = entityLocation;
            this.stream = stream;
            this.reader = reader;
            this.encoding = encoding;
            this.literal = literal;
            this.mayReadChunks = mayReadChunks;
            this.isExternal = isExternal;
            this.fCharacterBuffer = XMLEntityManager.this.fCharacterBufferPool.getBuffer(isExternal);
            this.ch = this.fCharacterBuffer.ch;
            this.fByteBuffer = fByteBuffer;
        }
        
        public final boolean isExternal() {
            return this.isExternal;
        }
        
        public final boolean isUnparsed() {
            return false;
        }
        
        public void setReader(final InputStream inputStream, final String s, final Boolean b) throws IOException {
            XMLEntityManager.this.fTempByteBuffer = this.fByteBuffer;
            this.reader = XMLEntityManager.this.createReader(inputStream, s, b);
            this.fByteBuffer = XMLEntityManager.this.fTempByteBuffer;
        }
        
        public String getExpandedSystemId() {
            for (int i = XMLEntityManager.this.fEntityStack.size() - 1; i >= 0; --i) {
                final ScannedEntity scannedEntity = (ScannedEntity)XMLEntityManager.this.fEntityStack.elementAt(i);
                if (scannedEntity.entityLocation != null && scannedEntity.entityLocation.getExpandedSystemId() != null) {
                    return scannedEntity.entityLocation.getExpandedSystemId();
                }
            }
            return null;
        }
        
        public String getLiteralSystemId() {
            for (int i = XMLEntityManager.this.fEntityStack.size() - 1; i >= 0; --i) {
                final ScannedEntity scannedEntity = (ScannedEntity)XMLEntityManager.this.fEntityStack.elementAt(i);
                if (scannedEntity.entityLocation != null && scannedEntity.entityLocation.getLiteralSystemId() != null) {
                    return scannedEntity.entityLocation.getLiteralSystemId();
                }
            }
            return null;
        }
        
        public int getLineNumber() {
            for (int i = XMLEntityManager.this.fEntityStack.size() - 1; i >= 0; --i) {
                final ScannedEntity scannedEntity = (ScannedEntity)XMLEntityManager.this.fEntityStack.elementAt(i);
                if (scannedEntity.isExternal()) {
                    return scannedEntity.lineNumber;
                }
            }
            return -1;
        }
        
        public int getColumnNumber() {
            for (int i = XMLEntityManager.this.fEntityStack.size() - 1; i >= 0; --i) {
                final ScannedEntity scannedEntity = (ScannedEntity)XMLEntityManager.this.fEntityStack.elementAt(i);
                if (scannedEntity.isExternal()) {
                    return scannedEntity.columnNumber;
                }
            }
            return -1;
        }
        
        public int getCharacterOffset() {
            for (int i = XMLEntityManager.this.fEntityStack.size() - 1; i >= 0; --i) {
                final ScannedEntity scannedEntity = (ScannedEntity)XMLEntityManager.this.fEntityStack.elementAt(i);
                if (scannedEntity.isExternal()) {
                    return scannedEntity.baseCharOffset + (scannedEntity.position - scannedEntity.startPosition);
                }
            }
            return -1;
        }
        
        public String getEncoding() {
            for (int i = XMLEntityManager.this.fEntityStack.size() - 1; i >= 0; --i) {
                final ScannedEntity scannedEntity = (ScannedEntity)XMLEntityManager.this.fEntityStack.elementAt(i);
                if (scannedEntity.isExternal()) {
                    return scannedEntity.encoding;
                }
            }
            return null;
        }
        
        public String getXMLVersion() {
            for (int i = XMLEntityManager.this.fEntityStack.size() - 1; i >= 0; --i) {
                final ScannedEntity scannedEntity = (ScannedEntity)XMLEntityManager.this.fEntityStack.elementAt(i);
                if (scannedEntity.isExternal()) {
                    return scannedEntity.xmlVersion;
                }
            }
            return null;
        }
        
        public boolean isEncodingExternallySpecified() {
            return this.externallySpecifiedEncoding;
        }
        
        public void setEncodingExternallySpecified(final boolean externallySpecifiedEncoding) {
            this.externallySpecifiedEncoding = externallySpecifiedEncoding;
        }
        
        public String toString() {
            final StringBuffer sb = new StringBuffer();
            sb.append("name=\"").append(this.name).append('\"');
            sb.append(",ch=");
            sb.append(this.ch);
            sb.append(",position=").append(this.position);
            sb.append(",count=").append(this.count);
            sb.append(",baseCharOffset=").append(this.baseCharOffset);
            sb.append(",startPosition=").append(this.startPosition);
            return sb.toString();
        }
    }
}
