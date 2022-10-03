package com.sun.org.apache.xalan.internal.xsltc.compiler;

import java.util.zip.ZipEntry;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.Date;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import com.sun.org.apache.bcel.internal.classfile.JavaClass;
import com.sun.org.apache.xalan.internal.utils.SecuritySupport;
import java.util.ArrayList;
import java.util.Enumeration;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;
import java.io.InputStream;
import java.io.IOException;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import org.xml.sax.InputSource;
import java.net.URL;
import java.util.Collections;
import java.util.Properties;
import java.util.HashMap;
import jdk.xml.internal.JdkXmlFeatures;
import com.sun.org.apache.xalan.internal.utils.XMLSecurityManager;
import java.io.File;
import java.util.Map;
import java.util.Vector;
import org.xml.sax.XMLReader;

public final class XSLTC
{
    private Parser _parser;
    private XMLReader _reader;
    private SourceLoader _loader;
    private Stylesheet _stylesheet;
    private int _modeSerial;
    private int _stylesheetSerial;
    private int _stepPatternSerial;
    private int _helperClassSerial;
    private int _attributeSetSerial;
    private int[] _numberFieldIndexes;
    private int _nextGType;
    private Vector _namesIndex;
    private Map<String, Integer> _elements;
    private Map<String, Integer> _attributes;
    private int _nextNSType;
    private Vector _namespaceIndex;
    private Map<String, Integer> _namespaces;
    private Map<String, Integer> _namespacePrefixes;
    private Vector m_characterData;
    public static final int FILE_OUTPUT = 0;
    public static final int JAR_OUTPUT = 1;
    public static final int BYTEARRAY_OUTPUT = 2;
    public static final int CLASSLOADER_OUTPUT = 3;
    public static final int BYTEARRAY_AND_FILE_OUTPUT = 4;
    public static final int BYTEARRAY_AND_JAR_OUTPUT = 5;
    private boolean _debug;
    private String _jarFileName;
    private String _className;
    private String _packageName;
    private File _destDir;
    private int _outputType;
    private Vector _classes;
    private Vector _bcelClasses;
    private boolean _callsNodeset;
    private boolean _multiDocument;
    private boolean _hasIdCall;
    private boolean _templateInlining;
    private boolean _isSecureProcessing;
    private boolean _overrideDefaultParser;
    private String _accessExternalStylesheet;
    private String _accessExternalDTD;
    private XMLSecurityManager _xmlSecurityManager;
    private final JdkXmlFeatures _xmlFeatures;
    private ClassLoader _extensionClassLoader;
    private final Map<String, Class> _externalExtensionFunctions;
    
    public XSLTC(final JdkXmlFeatures featureManager) {
        this._reader = null;
        this._loader = null;
        this._modeSerial = 1;
        this._stylesheetSerial = 1;
        this._stepPatternSerial = 1;
        this._helperClassSerial = 0;
        this._attributeSetSerial = 0;
        this._debug = false;
        this._jarFileName = null;
        this._className = null;
        this._packageName = null;
        this._destDir = null;
        this._outputType = 0;
        this._callsNodeset = false;
        this._multiDocument = false;
        this._hasIdCall = false;
        this._templateInlining = false;
        this._isSecureProcessing = false;
        this._accessExternalStylesheet = "all";
        this._accessExternalDTD = "all";
        this._overrideDefaultParser = featureManager.getFeature(JdkXmlFeatures.XmlFeature.JDK_OVERRIDE_PARSER);
        this._parser = new Parser(this, this._overrideDefaultParser);
        this._xmlFeatures = featureManager;
        this._extensionClassLoader = null;
        this._externalExtensionFunctions = new HashMap<String, Class>();
    }
    
    public void setSecureProcessing(final boolean flag) {
        this._isSecureProcessing = flag;
    }
    
    public boolean isSecureProcessing() {
        return this._isSecureProcessing;
    }
    
    public boolean getFeature(final JdkXmlFeatures.XmlFeature name) {
        return this._xmlFeatures.getFeature(name);
    }
    
    public Object getProperty(final String name) {
        if (name.equals("http://javax.xml.XMLConstants/property/accessExternalStylesheet")) {
            return this._accessExternalStylesheet;
        }
        if (name.equals("http://javax.xml.XMLConstants/property/accessExternalDTD")) {
            return this._accessExternalDTD;
        }
        if (name.equals("http://apache.org/xml/properties/security-manager")) {
            return this._xmlSecurityManager;
        }
        if (name.equals("jdk.xml.transform.extensionClassLoader")) {
            return this._extensionClassLoader;
        }
        return null;
    }
    
    public void setProperty(final String name, final Object value) {
        if (name.equals("http://javax.xml.XMLConstants/property/accessExternalStylesheet")) {
            this._accessExternalStylesheet = (String)value;
        }
        else if (name.equals("http://javax.xml.XMLConstants/property/accessExternalDTD")) {
            this._accessExternalDTD = (String)value;
        }
        else if (name.equals("http://apache.org/xml/properties/security-manager")) {
            this._xmlSecurityManager = (XMLSecurityManager)value;
        }
        else if (name.equals("jdk.xml.transform.extensionClassLoader")) {
            this._extensionClassLoader = (ClassLoader)value;
            this._externalExtensionFunctions.clear();
        }
    }
    
    public Parser getParser() {
        return this._parser;
    }
    
    public void setOutputType(final int type) {
        this._outputType = type;
    }
    
    public Properties getOutputProperties() {
        return this._parser.getOutputProperties();
    }
    
    public void init() {
        this.reset();
        this._reader = null;
        this._classes = new Vector();
        this._bcelClasses = new Vector();
    }
    
    private void setExternalExtensionFunctions(final String name, final Class clazz) {
        if (this._isSecureProcessing && clazz != null && !this._externalExtensionFunctions.containsKey(name)) {
            this._externalExtensionFunctions.put(name, clazz);
        }
    }
    
    Class loadExternalFunction(final String name) throws ClassNotFoundException {
        Class loaded = null;
        if (this._externalExtensionFunctions.containsKey(name)) {
            loaded = this._externalExtensionFunctions.get(name);
        }
        else if (this._extensionClassLoader != null) {
            loaded = Class.forName(name, true, this._extensionClassLoader);
            this.setExternalExtensionFunctions(name, loaded);
        }
        if (loaded == null) {
            throw new ClassNotFoundException(name);
        }
        return loaded;
    }
    
    public Map<String, Class> getExternalExtensionFunctions() {
        return (Map<String, Class>)Collections.unmodifiableMap((Map<? extends String, ? extends Class>)this._externalExtensionFunctions);
    }
    
    private void reset() {
        this._nextGType = 14;
        this._elements = new HashMap<String, Integer>();
        this._attributes = new HashMap<String, Integer>();
        (this._namespaces = new HashMap<String, Integer>()).put("", new Integer(this._nextNSType));
        this._namesIndex = new Vector(128);
        this._namespaceIndex = new Vector(32);
        this._namespacePrefixes = new HashMap<String, Integer>();
        this._stylesheet = null;
        this._parser.init();
        this._modeSerial = 1;
        this._stylesheetSerial = 1;
        this._stepPatternSerial = 1;
        this._helperClassSerial = 0;
        this._attributeSetSerial = 0;
        this._multiDocument = false;
        this._hasIdCall = false;
        this._numberFieldIndexes = new int[] { -1, -1, -1 };
        this._externalExtensionFunctions.clear();
    }
    
    public void setSourceLoader(final SourceLoader loader) {
        this._loader = loader;
    }
    
    public void setTemplateInlining(final boolean templateInlining) {
        this._templateInlining = templateInlining;
    }
    
    public boolean getTemplateInlining() {
        return this._templateInlining;
    }
    
    public void setPIParameters(final String media, final String title, final String charset) {
        this._parser.setPIParameters(media, title, charset);
    }
    
    public boolean compile(final URL url) {
        try {
            final InputStream stream = url.openStream();
            final InputSource input = new InputSource(stream);
            input.setSystemId(url.toString());
            return this.compile(input, this._className);
        }
        catch (final IOException e) {
            this._parser.reportError(2, new ErrorMsg("JAXP_COMPILE_ERR", e));
            return false;
        }
    }
    
    public boolean compile(final URL url, final String name) {
        try {
            final InputStream stream = url.openStream();
            final InputSource input = new InputSource(stream);
            input.setSystemId(url.toString());
            return this.compile(input, name);
        }
        catch (final IOException e) {
            this._parser.reportError(2, new ErrorMsg("JAXP_COMPILE_ERR", e));
            return false;
        }
    }
    
    public boolean compile(final InputStream stream, final String name) {
        final InputSource input = new InputSource(stream);
        input.setSystemId(name);
        return this.compile(input, name);
    }
    
    public boolean compile(final InputSource input, final String name) {
        try {
            this.reset();
            String systemId = null;
            if (input != null) {
                systemId = input.getSystemId();
            }
            if (this._className == null) {
                if (name != null) {
                    this.setClassName(name);
                }
                else if (systemId != null && !systemId.equals("")) {
                    this.setClassName(Util.baseName(systemId));
                }
                if (this._className == null || this._className.length() == 0) {
                    this.setClassName("GregorSamsa");
                }
            }
            SyntaxTreeNode element = null;
            if (this._reader == null) {
                element = this._parser.parse(input);
            }
            else {
                element = this._parser.parse(this._reader, input);
            }
            if (!this._parser.errorsFound() && element != null) {
                (this._stylesheet = this._parser.makeStylesheet(element)).setSourceLoader(this._loader);
                this._stylesheet.setSystemId(systemId);
                this._stylesheet.setParentStylesheet(null);
                this._stylesheet.setTemplateInlining(this._templateInlining);
                this._parser.setCurrentStylesheet(this._stylesheet);
                this._parser.createAST(this._stylesheet);
            }
            if (!this._parser.errorsFound() && this._stylesheet != null) {
                this._stylesheet.setCallsNodeset(this._callsNodeset);
                this._stylesheet.setMultiDocument(this._multiDocument);
                this._stylesheet.setHasIdCall(this._hasIdCall);
                synchronized (this.getClass()) {
                    this._stylesheet.translate();
                }
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
            this._parser.reportError(2, new ErrorMsg("JAXP_COMPILE_ERR", e));
        }
        catch (final Error e2) {
            if (this._debug) {
                e2.printStackTrace();
            }
            this._parser.reportError(2, new ErrorMsg("JAXP_COMPILE_ERR", e2));
        }
        finally {
            this._reader = null;
        }
        return !this._parser.errorsFound();
    }
    
    public boolean compile(final Vector stylesheets) {
        final int count = stylesheets.size();
        if (count == 0) {
            return true;
        }
        if (count == 1) {
            final Object url = stylesheets.firstElement();
            return url instanceof URL && this.compile((URL)url);
        }
        final Enumeration urls = stylesheets.elements();
        while (urls.hasMoreElements()) {
            this._className = null;
            final Object url2 = urls.nextElement();
            if (url2 instanceof URL && !this.compile((URL)url2)) {
                return false;
            }
        }
        return true;
    }
    
    public byte[][] getBytecodes() {
        final int count = this._classes.size();
        final byte[][] result = new byte[count][1];
        for (int i = 0; i < count; ++i) {
            result[i] = this._classes.elementAt(i);
        }
        return result;
    }
    
    public byte[][] compile(final String name, final InputSource input, final int outputType) {
        this._outputType = outputType;
        if (this.compile(input, name)) {
            return this.getBytecodes();
        }
        return null;
    }
    
    public byte[][] compile(final String name, final InputSource input) {
        return this.compile(name, input, 2);
    }
    
    public void setXMLReader(final XMLReader reader) {
        this._reader = reader;
    }
    
    public XMLReader getXMLReader() {
        return this._reader;
    }
    
    public ArrayList<ErrorMsg> getErrors() {
        return this._parser.getErrors();
    }
    
    public ArrayList<ErrorMsg> getWarnings() {
        return this._parser.getWarnings();
    }
    
    public void printErrors() {
        this._parser.printErrors();
    }
    
    public void printWarnings() {
        this._parser.printWarnings();
    }
    
    protected void setMultiDocument(final boolean flag) {
        this._multiDocument = flag;
    }
    
    public boolean isMultiDocument() {
        return this._multiDocument;
    }
    
    protected void setCallsNodeset(final boolean flag) {
        if (flag) {
            this.setMultiDocument(flag);
        }
        this._callsNodeset = flag;
    }
    
    public boolean callsNodeset() {
        return this._callsNodeset;
    }
    
    protected void setHasIdCall(final boolean flag) {
        this._hasIdCall = flag;
    }
    
    public boolean hasIdCall() {
        return this._hasIdCall;
    }
    
    public void setClassName(final String className) {
        final String base = Util.baseName(className);
        final String noext = Util.noExtName(base);
        final String name = Util.toJavaName(noext);
        if (this._packageName == null) {
            this._className = name;
        }
        else {
            this._className = this._packageName + '.' + name;
        }
    }
    
    public String getClassName() {
        return this._className;
    }
    
    private String classFileName(final String className) {
        return className.replace('.', File.separatorChar) + ".class";
    }
    
    private File getOutputFile(final String className) {
        if (this._destDir != null) {
            return new File(this._destDir, this.classFileName(className));
        }
        return new File(this.classFileName(className));
    }
    
    public boolean setDestDirectory(final String dstDirName) {
        final File dir = new File(dstDirName);
        if (SecuritySupport.getFileExists(dir) || dir.mkdirs()) {
            this._destDir = dir;
            return true;
        }
        this._destDir = null;
        return false;
    }
    
    public void setPackageName(final String packageName) {
        this._packageName = packageName;
        if (this._className != null) {
            this.setClassName(this._className);
        }
    }
    
    public void setJarFileName(final String jarFileName) {
        final String JAR_EXT = ".jar";
        if (jarFileName.endsWith(".jar")) {
            this._jarFileName = jarFileName;
        }
        else {
            this._jarFileName = jarFileName + ".jar";
        }
        this._outputType = 1;
    }
    
    public String getJarFileName() {
        return this._jarFileName;
    }
    
    public void setStylesheet(final Stylesheet stylesheet) {
        if (this._stylesheet == null) {
            this._stylesheet = stylesheet;
        }
    }
    
    public Stylesheet getStylesheet() {
        return this._stylesheet;
    }
    
    public int registerAttribute(final QName name) {
        Integer code = this._attributes.get(name.toString());
        if (code == null) {
            code = this._nextGType++;
            this._attributes.put(name.toString(), code);
            final String uri = name.getNamespace();
            final String local = "@" + name.getLocalPart();
            if (uri != null && !uri.equals("")) {
                this._namesIndex.addElement(uri + ":" + local);
            }
            else {
                this._namesIndex.addElement(local);
            }
            if (name.getLocalPart().equals("*")) {
                this.registerNamespace(name.getNamespace());
            }
        }
        return code;
    }
    
    public int registerElement(final QName name) {
        Integer code = this._elements.get(name.toString());
        if (code == null) {
            this._elements.put(name.toString(), code = this._nextGType++);
            this._namesIndex.addElement(name.toString());
        }
        if (name.getLocalPart().equals("*")) {
            this.registerNamespace(name.getNamespace());
        }
        return code;
    }
    
    public int registerNamespacePrefix(final QName name) {
        Integer code = this._namespacePrefixes.get(name.toString());
        if (code == null) {
            code = this._nextGType++;
            this._namespacePrefixes.put(name.toString(), code);
            final String uri = name.getNamespace();
            if (uri != null && !uri.equals("")) {
                this._namesIndex.addElement("?");
            }
            else {
                this._namesIndex.addElement("?" + name.getLocalPart());
            }
        }
        return code;
    }
    
    public int registerNamespace(final String namespaceURI) {
        Integer code = this._namespaces.get(namespaceURI);
        if (code == null) {
            code = this._nextNSType++;
            this._namespaces.put(namespaceURI, code);
            this._namespaceIndex.addElement(namespaceURI);
        }
        return code;
    }
    
    public int nextModeSerial() {
        return this._modeSerial++;
    }
    
    public int nextStylesheetSerial() {
        return this._stylesheetSerial++;
    }
    
    public int nextStepPatternSerial() {
        return this._stepPatternSerial++;
    }
    
    public int[] getNumberFieldIndexes() {
        return this._numberFieldIndexes;
    }
    
    public int nextHelperClassSerial() {
        return this._helperClassSerial++;
    }
    
    public int nextAttributeSetSerial() {
        return this._attributeSetSerial++;
    }
    
    public Vector getNamesIndex() {
        return this._namesIndex;
    }
    
    public Vector getNamespaceIndex() {
        return this._namespaceIndex;
    }
    
    public String getHelperClassName() {
        return this.getClassName() + '$' + this._helperClassSerial++;
    }
    
    public void dumpClass(final JavaClass clazz) {
        if (this._outputType == 0 || this._outputType == 4) {
            final File outFile = this.getOutputFile(clazz.getClassName());
            final String parentDir = outFile.getParent();
            if (parentDir != null) {
                final File parentFile = new File(parentDir);
                if (!SecuritySupport.getFileExists(parentFile)) {
                    parentFile.mkdirs();
                }
            }
        }
        try {
            switch (this._outputType) {
                case 0: {
                    clazz.dump(new BufferedOutputStream(new FileOutputStream(this.getOutputFile(clazz.getClassName()))));
                    break;
                }
                case 1: {
                    this._bcelClasses.addElement(clazz);
                    break;
                }
                case 2:
                case 3:
                case 4:
                case 5: {
                    final ByteArrayOutputStream out = new ByteArrayOutputStream(2048);
                    clazz.dump(out);
                    this._classes.addElement(out.toByteArray());
                    if (this._outputType == 4) {
                        clazz.dump(new BufferedOutputStream(new FileOutputStream(this.getOutputFile(clazz.getClassName()))));
                        break;
                    }
                    if (this._outputType == 5) {
                        this._bcelClasses.addElement(clazz);
                        break;
                    }
                    break;
                }
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
    }
    
    private String entryName(final File f) throws IOException {
        return f.getName().replace(File.separatorChar, '/');
    }
    
    public void outputToJar() throws IOException {
        final Manifest manifest = new Manifest();
        final Attributes atrs = manifest.getMainAttributes();
        atrs.put(Attributes.Name.MANIFEST_VERSION, "1.2");
        final Map map = manifest.getEntries();
        Enumeration classes = this._bcelClasses.elements();
        final String now = new Date().toString();
        final Attributes.Name dateAttr = new Attributes.Name("Date");
        while (classes.hasMoreElements()) {
            final JavaClass clazz = classes.nextElement();
            final String className = clazz.getClassName().replace('.', '/');
            final Attributes attr = new Attributes();
            attr.put(dateAttr, now);
            map.put(className + ".class", attr);
        }
        final File jarFile = new File(this._destDir, this._jarFileName);
        final JarOutputStream jos = new JarOutputStream(new FileOutputStream(jarFile), manifest);
        classes = this._bcelClasses.elements();
        while (classes.hasMoreElements()) {
            final JavaClass clazz2 = classes.nextElement();
            final String className2 = clazz2.getClassName().replace('.', '/');
            jos.putNextEntry(new JarEntry(className2 + ".class"));
            final ByteArrayOutputStream out = new ByteArrayOutputStream(2048);
            clazz2.dump(out);
            out.writeTo(jos);
        }
        jos.close();
    }
    
    public void setDebug(final boolean debug) {
        this._debug = debug;
    }
    
    public boolean debug() {
        return this._debug;
    }
    
    public String getCharacterData(final int index) {
        return this.m_characterData.elementAt(index).toString();
    }
    
    public int getCharacterDataCount() {
        return (this.m_characterData != null) ? this.m_characterData.size() : 0;
    }
    
    public int addCharacterData(final String newData) {
        StringBuffer currData;
        if (this.m_characterData == null) {
            this.m_characterData = new Vector();
            currData = new StringBuffer();
            this.m_characterData.addElement(currData);
        }
        else {
            currData = this.m_characterData.elementAt(this.m_characterData.size() - 1);
        }
        if (newData.length() + currData.length() > 21845) {
            currData = new StringBuffer();
            this.m_characterData.addElement(currData);
        }
        final int newDataOffset = currData.length();
        currData.append(newData);
        return newDataOffset;
    }
}
