package org.apache.xerces.parsers;

import org.apache.xerces.xni.XMLString;
import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.XMLLocator;
import org.apache.xerces.xni.parser.XMLDTDFilter;
import java.io.FilterReader;
import java.io.FilterInputStream;
import org.apache.xerces.xni.XNIException;
import java.net.URL;
import java.io.Reader;
import org.apache.xerces.impl.XMLEntityDescription;
import org.apache.xerces.xni.parser.XMLInputSource;
import org.apache.xerces.xni.XMLResourceIdentifier;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.File;
import org.apache.xerces.impl.dtd.XMLDTDProcessor;
import org.apache.xerces.xni.parser.XMLDTDScanner;
import org.apache.xerces.xni.parser.XMLDTDSource;
import org.apache.xerces.xni.XMLDTDHandler;
import org.apache.xerces.xni.parser.XMLEntityResolver;
import org.apache.xerces.xni.parser.XMLConfigurationException;
import org.apache.xerces.xni.parser.XMLComponentManager;
import org.apache.xerces.xni.grammars.XMLGrammarPool;
import org.apache.xerces.util.SymbolTable;
import org.apache.xerces.util.SecurityManager;
import java.util.Properties;

public final class SecureProcessingConfiguration extends XIncludeAwareParserConfiguration
{
    private static final String SECURITY_MANAGER_PROPERTY = "http://apache.org/xml/properties/security-manager";
    private static final String ENTITY_RESOLVER_PROPERTY = "http://apache.org/xml/properties/internal/entity-resolver";
    private static final String EXTERNAL_GENERAL_ENTITIES = "http://xml.org/sax/features/external-general-entities";
    private static final String EXTERNAL_PARAMETER_ENTITIES = "http://xml.org/sax/features/external-parameter-entities";
    private static final String LOAD_EXTERNAL_DTD = "http://apache.org/xml/features/nonvalidating/load-external-dtd";
    private static final boolean DEBUG;
    private static Properties jaxpProperties;
    private static long lastModified;
    private static final int SECURITY_MANAGER_DEFAULT_ENTITY_EXPANSION_LIMIT = 100000;
    private static final int SECURITY_MANAGER_DEFAULT_MAX_OCCUR_NODE_LIMIT = 3000;
    private static final String ENTITY_EXPANSION_LIMIT_PROPERTY_NAME = "jdk.xml.entityExpansionLimit";
    private static final String MAX_OCCUR_LIMIT_PROPERTY_NAME = "jdk.xml.maxOccur";
    private static final String TOTAL_ENTITY_SIZE_LIMIT_PROPERTY_NAME = "jdk.xml.totalEntitySizeLimit";
    private static final String MAX_GENERAL_ENTITY_SIZE_LIMIT_PROPERTY_NAME = "jdk.xml.maxGeneralEntitySizeLimit";
    private static final String MAX_PARAMETER_ENTITY_SIZE_LIMIT_PROPERTY_NAME = "jdk.xml.maxParameterEntitySizeLimit";
    private static final String RESOLVE_EXTERNAL_ENTITIES_PROPERTY_NAME = "jdk.xml.resolveExternalEntities";
    private static final int ENTITY_EXPANSION_LIMIT_DEFAULT_VALUE = 64000;
    private static final int MAX_OCCUR_LIMIT_DEFAULT_VALUE = 5000;
    private static final int TOTAL_ENTITY_SIZE_LIMIT_DEFAULT_VALUE = 50000000;
    private static final int MAX_GENERAL_ENTITY_SIZE_LIMIT_DEFAULT_VALUE = Integer.MAX_VALUE;
    private static final int MAX_PARAMETER_ENTITY_SIZE_LIMIT_DEFAULT_VALUE = Integer.MAX_VALUE;
    private static final boolean RESOLVE_EXTERNAL_ENTITIES_DEFAULT_VALUE = true;
    protected final int ENTITY_EXPANSION_LIMIT_SYSTEM_VALUE;
    protected final int MAX_OCCUR_LIMIT_SYSTEM_VALUE;
    protected final int TOTAL_ENTITY_SIZE_LIMIT_SYSTEM_VALUE;
    protected final int MAX_GENERAL_ENTITY_SIZE_LIMIT_SYSTEM_VALUE;
    protected final int MAX_PARAMETER_ENTITY_SIZE_LIMIT_SYSTEM_VALUE;
    protected final boolean RESOLVE_EXTERNAL_ENTITIES_SYSTEM_VALUE;
    private final boolean fJavaSecurityManagerEnabled;
    private boolean fLimitSpecified;
    private SecurityManager fSecurityManager;
    private InternalEntityMonitor fInternalEntityMonitor;
    private final ExternalEntityMonitor fExternalEntityMonitor;
    private int fTotalEntitySize;
    static /* synthetic */ Class class$org$apache$xerces$parsers$SecureProcessingConfiguration;
    
    public SecureProcessingConfiguration() {
        this(null, null, null);
    }
    
    public SecureProcessingConfiguration(final SymbolTable symbolTable) {
        this(symbolTable, null, null);
    }
    
    public SecureProcessingConfiguration(final SymbolTable symbolTable, final XMLGrammarPool xmlGrammarPool) {
        this(symbolTable, xmlGrammarPool, null);
    }
    
    public SecureProcessingConfiguration(final SymbolTable symbolTable, final XMLGrammarPool xmlGrammarPool, final XMLComponentManager xmlComponentManager) {
        super(symbolTable, xmlGrammarPool, xmlComponentManager);
        this.fTotalEntitySize = 0;
        this.fJavaSecurityManagerEnabled = (System.getSecurityManager() != null);
        this.ENTITY_EXPANSION_LIMIT_SYSTEM_VALUE = this.getPropertyValue("jdk.xml.entityExpansionLimit", 64000);
        this.MAX_OCCUR_LIMIT_SYSTEM_VALUE = this.getPropertyValue("jdk.xml.maxOccur", 5000);
        this.TOTAL_ENTITY_SIZE_LIMIT_SYSTEM_VALUE = this.getPropertyValue("jdk.xml.totalEntitySizeLimit", 50000000);
        this.MAX_GENERAL_ENTITY_SIZE_LIMIT_SYSTEM_VALUE = this.getPropertyValue("jdk.xml.maxGeneralEntitySizeLimit", Integer.MAX_VALUE);
        this.MAX_PARAMETER_ENTITY_SIZE_LIMIT_SYSTEM_VALUE = this.getPropertyValue("jdk.xml.maxParameterEntitySizeLimit", Integer.MAX_VALUE);
        this.RESOLVE_EXTERNAL_ENTITIES_SYSTEM_VALUE = this.getPropertyValue("jdk.xml.resolveExternalEntities", true);
        if (this.fJavaSecurityManagerEnabled || this.fLimitSpecified) {
            if (!this.RESOLVE_EXTERNAL_ENTITIES_SYSTEM_VALUE) {
                super.setFeature("http://xml.org/sax/features/external-general-entities", false);
                super.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
                super.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            }
            (this.fSecurityManager = new SecurityManager()).setEntityExpansionLimit(this.ENTITY_EXPANSION_LIMIT_SYSTEM_VALUE);
            this.fSecurityManager.setMaxOccurNodeLimit(this.MAX_OCCUR_LIMIT_SYSTEM_VALUE);
            super.setProperty("http://apache.org/xml/properties/security-manager", this.fSecurityManager);
        }
        super.setProperty("http://apache.org/xml/properties/internal/entity-resolver", this.fExternalEntityMonitor = new ExternalEntityMonitor());
    }
    
    protected void checkEntitySizeLimits(final int n, final int n2, final boolean b) {
        this.fTotalEntitySize += n2;
        if (this.fTotalEntitySize > this.TOTAL_ENTITY_SIZE_LIMIT_SYSTEM_VALUE) {
            this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "TotalEntitySizeLimitExceeded", new Object[] { new Integer(this.TOTAL_ENTITY_SIZE_LIMIT_SYSTEM_VALUE) }, (short)2);
        }
        if (b) {
            if (n > this.MAX_PARAMETER_ENTITY_SIZE_LIMIT_SYSTEM_VALUE) {
                this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MaxParameterEntitySizeLimitExceeded", new Object[] { new Integer(this.MAX_PARAMETER_ENTITY_SIZE_LIMIT_SYSTEM_VALUE) }, (short)2);
            }
        }
        else if (n > this.MAX_GENERAL_ENTITY_SIZE_LIMIT_SYSTEM_VALUE) {
            this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MaxGeneralEntitySizeLimitExceeded", new Object[] { new Integer(this.MAX_GENERAL_ENTITY_SIZE_LIMIT_SYSTEM_VALUE) }, (short)2);
        }
    }
    
    public Object getProperty(final String s) throws XMLConfigurationException {
        if ("http://apache.org/xml/properties/security-manager".equals(s)) {
            return this.fSecurityManager;
        }
        if ("http://apache.org/xml/properties/internal/entity-resolver".equals(s)) {
            return this.fExternalEntityMonitor;
        }
        return super.getProperty(s);
    }
    
    public void setProperty(final String s, final Object o) throws XMLConfigurationException {
        if ("http://apache.org/xml/properties/security-manager".equals(s)) {
            if (o == null && this.fJavaSecurityManagerEnabled) {
                return;
            }
            this.fSecurityManager = (SecurityManager)o;
            if (this.fSecurityManager != null) {
                if (this.fSecurityManager.getEntityExpansionLimit() == 100000) {
                    this.fSecurityManager.setEntityExpansionLimit(this.ENTITY_EXPANSION_LIMIT_SYSTEM_VALUE);
                }
                if (this.fSecurityManager.getMaxOccurNodeLimit() == 3000) {
                    this.fSecurityManager.setMaxOccurNodeLimit(this.MAX_OCCUR_LIMIT_SYSTEM_VALUE);
                }
            }
        }
        else if ("http://apache.org/xml/properties/internal/entity-resolver".equals(s)) {
            this.fExternalEntityMonitor.setEntityResolver((XMLEntityResolver)o);
            return;
        }
        super.setProperty(s, o);
    }
    
    protected void configurePipeline() {
        super.configurePipeline();
        this.configurePipelineCommon(true);
    }
    
    protected void configureXML11Pipeline() {
        super.configureXML11Pipeline();
        this.configurePipelineCommon(false);
    }
    
    private void configurePipelineCommon(final boolean b) {
        if (this.fSecurityManager != null) {
            this.fTotalEntitySize = 0;
            if (this.fInternalEntityMonitor == null) {
                this.fInternalEntityMonitor = new InternalEntityMonitor();
            }
            XMLDTDScanner dtdSource;
            XMLDTDProcessor dtdHandler;
            if (b) {
                dtdSource = this.fDTDScanner;
                dtdHandler = this.fDTDProcessor;
            }
            else {
                dtdSource = this.fXML11DTDScanner;
                dtdHandler = this.fXML11DTDProcessor;
            }
            dtdSource.setDTDHandler(this.fInternalEntityMonitor);
            this.fInternalEntityMonitor.setDTDSource(dtdSource);
            this.fInternalEntityMonitor.setDTDHandler(dtdHandler);
            dtdHandler.setDTDSource(this.fInternalEntityMonitor);
        }
    }
    
    private int getPropertyValue(final String s, final int n) {
        try {
            final String systemProperty = SecuritySupport.getSystemProperty(s);
            if (systemProperty != null && systemProperty.length() > 0) {
                if (SecureProcessingConfiguration.DEBUG) {
                    debugPrintln("found system property \"" + s + "\", value=" + systemProperty);
                }
                final int int1 = Integer.parseInt(systemProperty);
                this.fLimitSpecified = true;
                if (int1 > 0) {
                    return int1;
                }
                return Integer.MAX_VALUE;
            }
        }
        catch (final VirtualMachineError virtualMachineError) {
            throw virtualMachineError;
        }
        catch (final ThreadDeath threadDeath) {
            throw threadDeath;
        }
        catch (final Throwable t) {
            if (SecureProcessingConfiguration.DEBUG) {
                debugPrintln(t.getClass().getName() + ": " + t.getMessage());
                t.printStackTrace();
            }
        }
        try {
            boolean fileExists = false;
            File file = null;
            try {
                file = new File(SecuritySupport.getSystemProperty("java.home") + File.separator + "lib" + File.separator + "jaxp.properties");
                fileExists = SecuritySupport.getFileExists(file);
            }
            catch (final SecurityException ex) {
                SecureProcessingConfiguration.lastModified = -1L;
                SecureProcessingConfiguration.jaxpProperties = null;
            }
            Class class$;
            Class class$org$apache$xerces$parsers$SecureProcessingConfiguration;
            if (SecureProcessingConfiguration.class$org$apache$xerces$parsers$SecureProcessingConfiguration == null) {
                class$org$apache$xerces$parsers$SecureProcessingConfiguration = (SecureProcessingConfiguration.class$org$apache$xerces$parsers$SecureProcessingConfiguration = (class$ = class$("org.apache.xerces.parsers.SecureProcessingConfiguration")));
            }
            else {
                class$ = (class$org$apache$xerces$parsers$SecureProcessingConfiguration = SecureProcessingConfiguration.class$org$apache$xerces$parsers$SecureProcessingConfiguration);
            }
            final Class clazz = class$org$apache$xerces$parsers$SecureProcessingConfiguration;
            synchronized (class$) {
                int n2 = 0;
                FileInputStream fileInputStream = null;
                try {
                    if (SecureProcessingConfiguration.lastModified >= 0L) {
                        if (fileExists && SecureProcessingConfiguration.lastModified < (SecureProcessingConfiguration.lastModified = SecuritySupport.getLastModified(file))) {
                            n2 = 1;
                        }
                        else if (!fileExists) {
                            SecureProcessingConfiguration.lastModified = -1L;
                            SecureProcessingConfiguration.jaxpProperties = null;
                        }
                    }
                    else if (fileExists) {
                        n2 = 1;
                        SecureProcessingConfiguration.lastModified = SecuritySupport.getLastModified(file);
                    }
                    if (n2 == 1) {
                        SecureProcessingConfiguration.jaxpProperties = new Properties();
                        fileInputStream = SecuritySupport.getFileInputStream(file);
                        SecureProcessingConfiguration.jaxpProperties.load(fileInputStream);
                    }
                }
                catch (final Exception ex2) {
                    SecureProcessingConfiguration.lastModified = -1L;
                    SecureProcessingConfiguration.jaxpProperties = null;
                }
                finally {
                    if (fileInputStream != null) {
                        try {
                            fileInputStream.close();
                        }
                        catch (final IOException ex3) {}
                    }
                }
            }
            if (SecureProcessingConfiguration.jaxpProperties != null) {
                final String property = SecureProcessingConfiguration.jaxpProperties.getProperty(s);
                if (property != null && property.length() > 0) {
                    if (SecureProcessingConfiguration.DEBUG) {
                        debugPrintln("found \"" + s + "\" in jaxp.properties, value=" + property);
                    }
                    final int int2 = Integer.parseInt(property);
                    this.fLimitSpecified = true;
                    if (int2 > 0) {
                        return int2;
                    }
                    return Integer.MAX_VALUE;
                }
            }
        }
        catch (final VirtualMachineError virtualMachineError2) {
            throw virtualMachineError2;
        }
        catch (final ThreadDeath threadDeath2) {
            throw threadDeath2;
        }
        catch (final Throwable t2) {
            if (SecureProcessingConfiguration.DEBUG) {
                debugPrintln(t2.getClass().getName() + ": " + t2.getMessage());
                t2.printStackTrace();
            }
        }
        return n;
    }
    
    private boolean getPropertyValue(final String s, final boolean b) {
        try {
            final String systemProperty = SecuritySupport.getSystemProperty(s);
            if (systemProperty != null && systemProperty.length() > 0) {
                if (SecureProcessingConfiguration.DEBUG) {
                    debugPrintln("found system property \"" + s + "\", value=" + systemProperty);
                }
                final boolean booleanValue = Boolean.valueOf(systemProperty);
                this.fLimitSpecified = true;
                return booleanValue;
            }
        }
        catch (final VirtualMachineError virtualMachineError) {
            throw virtualMachineError;
        }
        catch (final ThreadDeath threadDeath) {
            throw threadDeath;
        }
        catch (final Throwable t) {
            if (SecureProcessingConfiguration.DEBUG) {
                debugPrintln(t.getClass().getName() + ": " + t.getMessage());
                t.printStackTrace();
            }
        }
        try {
            boolean fileExists = false;
            File file = null;
            try {
                file = new File(SecuritySupport.getSystemProperty("java.home") + File.separator + "lib" + File.separator + "jaxp.properties");
                fileExists = SecuritySupport.getFileExists(file);
            }
            catch (final SecurityException ex) {
                SecureProcessingConfiguration.lastModified = -1L;
                SecureProcessingConfiguration.jaxpProperties = null;
            }
            Class class$;
            Class class$org$apache$xerces$parsers$SecureProcessingConfiguration;
            if (SecureProcessingConfiguration.class$org$apache$xerces$parsers$SecureProcessingConfiguration == null) {
                class$org$apache$xerces$parsers$SecureProcessingConfiguration = (SecureProcessingConfiguration.class$org$apache$xerces$parsers$SecureProcessingConfiguration = (class$ = class$("org.apache.xerces.parsers.SecureProcessingConfiguration")));
            }
            else {
                class$ = (class$org$apache$xerces$parsers$SecureProcessingConfiguration = SecureProcessingConfiguration.class$org$apache$xerces$parsers$SecureProcessingConfiguration);
            }
            final Class clazz = class$org$apache$xerces$parsers$SecureProcessingConfiguration;
            synchronized (class$) {
                int n = 0;
                FileInputStream fileInputStream = null;
                try {
                    if (SecureProcessingConfiguration.lastModified >= 0L) {
                        if (fileExists && SecureProcessingConfiguration.lastModified < (SecureProcessingConfiguration.lastModified = SecuritySupport.getLastModified(file))) {
                            n = 1;
                        }
                        else if (!fileExists) {
                            SecureProcessingConfiguration.lastModified = -1L;
                            SecureProcessingConfiguration.jaxpProperties = null;
                        }
                    }
                    else if (fileExists) {
                        n = 1;
                        SecureProcessingConfiguration.lastModified = SecuritySupport.getLastModified(file);
                    }
                    if (n == 1) {
                        SecureProcessingConfiguration.jaxpProperties = new Properties();
                        fileInputStream = SecuritySupport.getFileInputStream(file);
                        SecureProcessingConfiguration.jaxpProperties.load(fileInputStream);
                    }
                }
                catch (final Exception ex2) {
                    SecureProcessingConfiguration.lastModified = -1L;
                    SecureProcessingConfiguration.jaxpProperties = null;
                }
                finally {
                    if (fileInputStream != null) {
                        try {
                            fileInputStream.close();
                        }
                        catch (final IOException ex3) {}
                    }
                }
            }
            if (SecureProcessingConfiguration.jaxpProperties != null) {
                final String property = SecureProcessingConfiguration.jaxpProperties.getProperty(s);
                if (property != null && property.length() > 0) {
                    if (SecureProcessingConfiguration.DEBUG) {
                        debugPrintln("found \"" + s + "\" in jaxp.properties, value=" + property);
                    }
                    final boolean booleanValue2 = Boolean.valueOf(property);
                    this.fLimitSpecified = true;
                    return booleanValue2;
                }
            }
        }
        catch (final VirtualMachineError virtualMachineError2) {
            throw virtualMachineError2;
        }
        catch (final ThreadDeath threadDeath2) {
            throw threadDeath2;
        }
        catch (final Throwable t2) {
            if (SecureProcessingConfiguration.DEBUG) {
                debugPrintln(t2.getClass().getName() + ": " + t2.getMessage());
                t2.printStackTrace();
            }
        }
        return b;
    }
    
    private static boolean isDebugEnabled() {
        try {
            final String systemProperty = SecuritySupport.getSystemProperty("xerces.debug");
            return systemProperty != null && !"false".equals(systemProperty);
        }
        catch (final SecurityException ex) {
            return false;
        }
    }
    
    private static void debugPrintln(final String s) {
        if (SecureProcessingConfiguration.DEBUG) {
            System.err.println("XERCES: " + s);
        }
    }
    
    static /* synthetic */ Class class$(final String s) {
        try {
            return Class.forName(s);
        }
        catch (final ClassNotFoundException ex) {
            throw new NoClassDefFoundError().initCause(ex);
        }
    }
    
    static {
        DEBUG = isDebugEnabled();
        SecureProcessingConfiguration.jaxpProperties = null;
        SecureProcessingConfiguration.lastModified = -1L;
    }
    
    final class ExternalEntityMonitor implements XMLEntityResolver
    {
        private XMLEntityResolver fEntityResolver;
        
        public XMLInputSource resolveEntity(final XMLResourceIdentifier xmlResourceIdentifier) throws XNIException, IOException {
            XMLInputSource resolveEntity = null;
            if (this.fEntityResolver != null) {
                resolveEntity = this.fEntityResolver.resolveEntity(xmlResourceIdentifier);
            }
            if (SecureProcessingConfiguration.this.fSecurityManager != null && xmlResourceIdentifier instanceof XMLEntityDescription) {
                final String entityName = ((XMLEntityDescription)xmlResourceIdentifier).getEntityName();
                final boolean b = entityName != null && entityName.startsWith("%");
                if (resolveEntity == null) {
                    resolveEntity = new XMLInputSource(xmlResourceIdentifier.getPublicId(), xmlResourceIdentifier.getExpandedSystemId(), xmlResourceIdentifier.getBaseSystemId());
                }
                final Reader characterStream = resolveEntity.getCharacterStream();
                if (characterStream != null) {
                    resolveEntity.setCharacterStream(new ReaderMonitor(characterStream, b));
                }
                else {
                    final InputStream byteStream = resolveEntity.getByteStream();
                    if (byteStream != null) {
                        resolveEntity.setByteStream(new InputStreamMonitor(byteStream, b));
                    }
                    else {
                        resolveEntity.setByteStream(new InputStreamMonitor(new URL(xmlResourceIdentifier.getExpandedSystemId()).openStream(), b));
                    }
                }
            }
            return resolveEntity;
        }
        
        public void setEntityResolver(final XMLEntityResolver fEntityResolver) {
            this.fEntityResolver = fEntityResolver;
        }
        
        public XMLEntityResolver getEntityResolver() {
            return this.fEntityResolver;
        }
        
        final class InputStreamMonitor extends FilterInputStream
        {
            private final boolean isPE;
            private int size;
            
            protected InputStreamMonitor(final InputStream inputStream, final boolean isPE) {
                super(inputStream);
                this.size = 0;
                this.isPE = isPE;
            }
            
            public int read() throws IOException {
                final int read = super.read();
                if (read != -1) {
                    ++this.size;
                    SecureProcessingConfiguration.this.checkEntitySizeLimits(this.size, 1, this.isPE);
                }
                return read;
            }
            
            public int read(final byte[] array, final int n, final int n2) throws IOException {
                final int read = super.read(array, n, n2);
                if (read > 0) {
                    this.size += read;
                    SecureProcessingConfiguration.this.checkEntitySizeLimits(this.size, read, this.isPE);
                }
                return read;
            }
        }
        
        final class ReaderMonitor extends FilterReader
        {
            private final boolean isPE;
            private int size;
            
            protected ReaderMonitor(final Reader reader, final boolean isPE) {
                super(reader);
                this.size = 0;
                this.isPE = isPE;
            }
            
            public int read() throws IOException {
                final int read = super.read();
                if (read != -1) {
                    ++this.size;
                    SecureProcessingConfiguration.this.checkEntitySizeLimits(this.size, 1, this.isPE);
                }
                return read;
            }
            
            public int read(final char[] array, final int n, final int n2) throws IOException {
                final int read = super.read(array, n, n2);
                if (read > 0) {
                    this.size += read;
                    SecureProcessingConfiguration.this.checkEntitySizeLimits(this.size, read, this.isPE);
                }
                return read;
            }
        }
    }
    
    final class InternalEntityMonitor implements XMLDTDFilter
    {
        private XMLDTDSource fDTDSource;
        private XMLDTDHandler fDTDHandler;
        
        public InternalEntityMonitor() {
        }
        
        public void startDTD(final XMLLocator xmlLocator, final Augmentations augmentations) throws XNIException {
            if (this.fDTDHandler != null) {
                this.fDTDHandler.startDTD(xmlLocator, augmentations);
            }
        }
        
        public void startParameterEntity(final String s, final XMLResourceIdentifier xmlResourceIdentifier, final String s2, final Augmentations augmentations) throws XNIException {
            if (this.fDTDHandler != null) {
                this.fDTDHandler.startParameterEntity(s, xmlResourceIdentifier, s2, augmentations);
            }
        }
        
        public void textDecl(final String s, final String s2, final Augmentations augmentations) throws XNIException {
            if (this.fDTDHandler != null) {
                this.fDTDHandler.textDecl(s, s2, augmentations);
            }
        }
        
        public void endParameterEntity(final String s, final Augmentations augmentations) throws XNIException {
            if (this.fDTDHandler != null) {
                this.fDTDHandler.endParameterEntity(s, augmentations);
            }
        }
        
        public void startExternalSubset(final XMLResourceIdentifier xmlResourceIdentifier, final Augmentations augmentations) throws XNIException {
            if (this.fDTDHandler != null) {
                this.fDTDHandler.startExternalSubset(xmlResourceIdentifier, augmentations);
            }
        }
        
        public void endExternalSubset(final Augmentations augmentations) throws XNIException {
            if (this.fDTDHandler != null) {
                this.fDTDHandler.endExternalSubset(augmentations);
            }
        }
        
        public void comment(final XMLString xmlString, final Augmentations augmentations) throws XNIException {
            if (this.fDTDHandler != null) {
                this.fDTDHandler.comment(xmlString, augmentations);
            }
        }
        
        public void processingInstruction(final String s, final XMLString xmlString, final Augmentations augmentations) throws XNIException {
            if (this.fDTDHandler != null) {
                this.fDTDHandler.processingInstruction(s, xmlString, augmentations);
            }
        }
        
        public void elementDecl(final String s, final String s2, final Augmentations augmentations) throws XNIException {
            if (this.fDTDHandler != null) {
                this.fDTDHandler.elementDecl(s, s2, augmentations);
            }
        }
        
        public void startAttlist(final String s, final Augmentations augmentations) throws XNIException {
            if (this.fDTDHandler != null) {
                this.fDTDHandler.startAttlist(s, augmentations);
            }
        }
        
        public void attributeDecl(final String s, final String s2, final String s3, final String[] array, final String s4, final XMLString xmlString, final XMLString xmlString2, final Augmentations augmentations) throws XNIException {
            if (this.fDTDHandler != null) {
                this.fDTDHandler.attributeDecl(s, s2, s3, array, s4, xmlString, xmlString2, augmentations);
            }
        }
        
        public void endAttlist(final Augmentations augmentations) throws XNIException {
            if (this.fDTDHandler != null) {
                this.fDTDHandler.endAttlist(augmentations);
            }
        }
        
        public void internalEntityDecl(final String s, final XMLString xmlString, final XMLString xmlString2, final Augmentations augmentations) throws XNIException {
            SecureProcessingConfiguration.this.checkEntitySizeLimits(xmlString.length, xmlString.length, s != null && s.startsWith("%"));
            if (this.fDTDHandler != null) {
                this.fDTDHandler.internalEntityDecl(s, xmlString, xmlString2, augmentations);
            }
        }
        
        public void externalEntityDecl(final String s, final XMLResourceIdentifier xmlResourceIdentifier, final Augmentations augmentations) throws XNIException {
            if (this.fDTDHandler != null) {
                this.fDTDHandler.externalEntityDecl(s, xmlResourceIdentifier, augmentations);
            }
        }
        
        public void unparsedEntityDecl(final String s, final XMLResourceIdentifier xmlResourceIdentifier, final String s2, final Augmentations augmentations) throws XNIException {
            if (this.fDTDHandler != null) {
                this.fDTDHandler.unparsedEntityDecl(s, xmlResourceIdentifier, s2, augmentations);
            }
        }
        
        public void notationDecl(final String s, final XMLResourceIdentifier xmlResourceIdentifier, final Augmentations augmentations) throws XNIException {
            if (this.fDTDHandler != null) {
                this.fDTDHandler.notationDecl(s, xmlResourceIdentifier, augmentations);
            }
        }
        
        public void startConditional(final short n, final Augmentations augmentations) throws XNIException {
            if (this.fDTDHandler != null) {
                this.fDTDHandler.startConditional(n, augmentations);
            }
        }
        
        public void ignoredCharacters(final XMLString xmlString, final Augmentations augmentations) throws XNIException {
            if (this.fDTDHandler != null) {
                this.fDTDHandler.ignoredCharacters(xmlString, augmentations);
            }
        }
        
        public void endConditional(final Augmentations augmentations) throws XNIException {
            if (this.fDTDHandler != null) {
                this.fDTDHandler.endConditional(augmentations);
            }
        }
        
        public void endDTD(final Augmentations augmentations) throws XNIException {
            if (this.fDTDHandler != null) {
                this.fDTDHandler.endDTD(augmentations);
            }
        }
        
        public void setDTDSource(final XMLDTDSource fdtdSource) {
            this.fDTDSource = fdtdSource;
        }
        
        public XMLDTDSource getDTDSource() {
            return this.fDTDSource;
        }
        
        public void setDTDHandler(final XMLDTDHandler fdtdHandler) {
            this.fDTDHandler = fdtdHandler;
        }
        
        public XMLDTDHandler getDTDHandler() {
            return this.fDTDHandler;
        }
    }
}
