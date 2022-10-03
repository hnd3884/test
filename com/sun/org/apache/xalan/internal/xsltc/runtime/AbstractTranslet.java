package com.sun.org.apache.xalan.internal.xsltc.runtime;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.DocumentType;
import jdk.xml.internal.JdkXmlUtils;
import org.w3c.dom.Document;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.File;
import com.sun.org.apache.xalan.internal.xsltc.runtime.output.TransletOutputHandlerFactory;
import com.sun.org.apache.xml.internal.serializer.SerializationHandler;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import java.util.Iterator;
import com.sun.org.apache.xalan.internal.xsltc.DOMEnhancedForDTM;
import java.util.HashMap;
import java.text.DecimalFormatSymbols;
import com.sun.org.apache.xalan.internal.xsltc.TransletException;
import com.sun.org.apache.xalan.internal.xsltc.dom.DOMAdapter;
import com.sun.org.apache.xalan.internal.xsltc.DOM;
import org.w3c.dom.DOMImplementation;
import com.sun.org.apache.xalan.internal.xsltc.DOMCache;
import com.sun.org.apache.xalan.internal.xsltc.dom.KeyIndex;
import java.text.DecimalFormat;
import java.util.Map;
import java.util.ArrayList;
import javax.xml.transform.Templates;
import java.util.Vector;
import com.sun.org.apache.xalan.internal.xsltc.Translet;

public abstract class AbstractTranslet implements Translet
{
    public String _version;
    public String _method;
    public String _encoding;
    public boolean _omitHeader;
    public String _standalone;
    public boolean _isStandalone;
    public String _doctypePublic;
    public String _doctypeSystem;
    public boolean _indent;
    public String _mediaType;
    public Vector _cdata;
    public int _indentamount;
    public static final int FIRST_TRANSLET_VERSION = 100;
    public static final int VER_SPLIT_NAMES_ARRAY = 101;
    public static final int CURRENT_TRANSLET_VERSION = 101;
    protected int transletVersion;
    protected String[] namesArray;
    protected String[] urisArray;
    protected int[] typesArray;
    protected String[] namespaceArray;
    protected Templates _templates;
    protected boolean _hasIdCall;
    protected StringValueHandler stringValueHandler;
    private static final String EMPTYSTRING = "";
    private static final String ID_INDEX_NAME = "##id";
    private boolean _overrideDefaultParser;
    private String _accessExternalStylesheet;
    protected int pbase;
    protected int pframe;
    protected ArrayList paramsStack;
    private MessageHandler _msgHandler;
    public Map<String, DecimalFormat> _formatSymbols;
    private Map<String, KeyIndex> _keyIndexes;
    private KeyIndex _emptyKeyIndex;
    private int _indexSize;
    private int _currentRootForKeys;
    private DOMCache _domCache;
    private Map<String, Class<?>> _auxClasses;
    protected DOMImplementation _domImplementation;
    
    public AbstractTranslet() {
        this._version = "1.0";
        this._method = null;
        this._encoding = "UTF-8";
        this._omitHeader = false;
        this._standalone = null;
        this._isStandalone = false;
        this._doctypePublic = null;
        this._doctypeSystem = null;
        this._indent = false;
        this._mediaType = null;
        this._cdata = null;
        this._indentamount = -1;
        this.transletVersion = 100;
        this._templates = null;
        this._hasIdCall = false;
        this.stringValueHandler = new StringValueHandler();
        this._accessExternalStylesheet = "all";
        this.pbase = 0;
        this.pframe = 0;
        this.paramsStack = new ArrayList();
        this._msgHandler = null;
        this._formatSymbols = null;
        this._keyIndexes = null;
        this._emptyKeyIndex = null;
        this._indexSize = 0;
        this._currentRootForKeys = 0;
        this._domCache = null;
        this._auxClasses = null;
        this._domImplementation = null;
    }
    
    public void printInternalState() {
        System.out.println("-------------------------------------");
        System.out.println("AbstractTranslet this = " + this);
        System.out.println("pbase = " + this.pbase);
        System.out.println("vframe = " + this.pframe);
        System.out.println("paramsStack.size() = " + this.paramsStack.size());
        System.out.println("namesArray.size = " + this.namesArray.length);
        System.out.println("namespaceArray.size = " + this.namespaceArray.length);
        System.out.println("");
        System.out.println("Total memory = " + Runtime.getRuntime().totalMemory());
    }
    
    public final DOMAdapter makeDOMAdapter(final DOM dom) throws TransletException {
        this.setRootForKeys(dom.getDocument());
        return new DOMAdapter(dom, this.namesArray, this.urisArray, this.typesArray, this.namespaceArray);
    }
    
    public final void pushParamFrame() {
        this.paramsStack.add(this.pframe, new Integer(this.pbase));
        this.pbase = ++this.pframe;
    }
    
    public final void popParamFrame() {
        if (this.pbase > 0) {
            final ArrayList paramsStack = this.paramsStack;
            final int pbase = this.pbase - 1;
            this.pbase = pbase;
            final int oldpbase = (int)paramsStack.get(pbase);
            for (int i = this.pframe - 1; i >= this.pbase; --i) {
                this.paramsStack.remove(i);
            }
            this.pframe = this.pbase;
            this.pbase = oldpbase;
        }
    }
    
    @Override
    public final Object addParameter(String name, final Object value) {
        name = BasisLibrary.mapQNameToJavaName(name);
        return this.addParameter(name, value, false);
    }
    
    public final Object addParameter(final String name, final Object value, final boolean isDefault) {
        int i = this.pframe - 1;
        while (i >= this.pbase) {
            final Parameter param = this.paramsStack.get(i);
            if (param._name.equals(name)) {
                if (param._isDefault || !isDefault) {
                    param._value = value;
                    param._isDefault = isDefault;
                    return value;
                }
                return param._value;
            }
            else {
                --i;
            }
        }
        this.paramsStack.add(this.pframe++, new Parameter(name, value, isDefault));
        return value;
    }
    
    public void clearParameters() {
        final int n = 0;
        this.pframe = n;
        this.pbase = n;
        this.paramsStack.clear();
    }
    
    public final Object getParameter(String name) {
        name = BasisLibrary.mapQNameToJavaName(name);
        for (int i = this.pframe - 1; i >= this.pbase; --i) {
            final Parameter param = this.paramsStack.get(i);
            if (param._name.equals(name)) {
                return param._value;
            }
        }
        return null;
    }
    
    public final void setMessageHandler(final MessageHandler handler) {
        this._msgHandler = handler;
    }
    
    public final void displayMessage(final String msg) {
        if (this._msgHandler == null) {
            System.err.println(msg);
        }
        else {
            this._msgHandler.displayMessage(msg);
        }
    }
    
    public void addDecimalFormat(String name, final DecimalFormatSymbols symbols) {
        if (this._formatSymbols == null) {
            this._formatSymbols = new HashMap<String, DecimalFormat>();
        }
        if (name == null) {
            name = "";
        }
        final DecimalFormat df = new DecimalFormat();
        if (symbols != null) {
            df.setDecimalFormatSymbols(symbols);
        }
        this._formatSymbols.put(name, df);
    }
    
    public final DecimalFormat getDecimalFormat(String name) {
        if (this._formatSymbols != null) {
            if (name == null) {
                name = "";
            }
            DecimalFormat df = this._formatSymbols.get(name);
            if (df == null) {
                df = this._formatSymbols.get("");
            }
            return df;
        }
        return null;
    }
    
    public final void prepassDocument(final DOM document) {
        this.setIndexSize(document.getSize());
        this.buildIDIndex(document);
    }
    
    private final void buildIDIndex(final DOM document) {
        this.setRootForKeys(document.getDocument());
        if (document instanceof DOMEnhancedForDTM) {
            final DOMEnhancedForDTM enhancedDOM = (DOMEnhancedForDTM)document;
            if (enhancedDOM.hasDOMSource()) {
                this.buildKeyIndex("##id", document);
                return;
            }
            final Map<String, Integer> elementsByID = enhancedDOM.getElementsWithIDs();
            if (elementsByID == null) {
                return;
            }
            boolean hasIDValues = false;
            for (final Map.Entry<String, Integer> entry : elementsByID.entrySet()) {
                final int element = document.getNodeHandle(entry.getValue());
                this.buildKeyIndex("##id", element, entry.getKey());
                hasIDValues = true;
            }
            if (hasIDValues) {
                this.setKeyIndexDom("##id", document);
            }
        }
    }
    
    public final void postInitialization() {
        if (this.transletVersion < 101) {
            final int arraySize = this.namesArray.length;
            final String[] newURIsArray = new String[arraySize];
            final String[] newNamesArray = new String[arraySize];
            final int[] newTypesArray = new int[arraySize];
            for (int i = 0; i < arraySize; ++i) {
                final String name = this.namesArray[i];
                final int colonIndex = name.lastIndexOf(58);
                int lNameStartIdx = colonIndex + 1;
                if (colonIndex > -1) {
                    newURIsArray[i] = name.substring(0, colonIndex);
                }
                if (name.charAt(lNameStartIdx) == '@') {
                    ++lNameStartIdx;
                    newTypesArray[i] = 2;
                }
                else if (name.charAt(lNameStartIdx) == '?') {
                    ++lNameStartIdx;
                    newTypesArray[i] = 13;
                }
                else {
                    newTypesArray[i] = 1;
                }
                newNamesArray[i] = ((lNameStartIdx == 0) ? name : name.substring(lNameStartIdx));
            }
            this.namesArray = newNamesArray;
            this.urisArray = newURIsArray;
            this.typesArray = newTypesArray;
        }
        if (this.transletVersion > 101) {
            BasisLibrary.runTimeError("UNKNOWN_TRANSLET_VERSION_ERR", this.getClass().getName());
        }
    }
    
    public void setIndexSize(final int size) {
        if (size > this._indexSize) {
            this._indexSize = size;
        }
    }
    
    public KeyIndex createKeyIndex() {
        return new KeyIndex(this._indexSize);
    }
    
    public void buildKeyIndex(final String name, final int node, final String value) {
        final KeyIndex index = this.buildKeyIndexHelper(name);
        index.add(value, node, this._currentRootForKeys);
    }
    
    public void buildKeyIndex(final String name, final DOM dom) {
        final KeyIndex index = this.buildKeyIndexHelper(name);
        index.setDom(dom, dom.getDocument());
    }
    
    private KeyIndex buildKeyIndexHelper(final String name) {
        if (this._keyIndexes == null) {
            this._keyIndexes = new HashMap<String, KeyIndex>();
        }
        KeyIndex index = this._keyIndexes.get(name);
        if (index == null) {
            this._keyIndexes.put(name, index = new KeyIndex(this._indexSize));
        }
        return index;
    }
    
    public KeyIndex getKeyIndex(final String name) {
        if (this._keyIndexes == null) {
            return (this._emptyKeyIndex != null) ? this._emptyKeyIndex : (this._emptyKeyIndex = new KeyIndex(1));
        }
        final KeyIndex index = this._keyIndexes.get(name);
        if (index == null) {
            return (this._emptyKeyIndex != null) ? this._emptyKeyIndex : (this._emptyKeyIndex = new KeyIndex(1));
        }
        return index;
    }
    
    private void setRootForKeys(final int root) {
        this._currentRootForKeys = root;
    }
    
    @Override
    public void buildKeys(final DOM document, final DTMAxisIterator iterator, final SerializationHandler handler, final int root) throws TransletException {
    }
    
    public void setKeyIndexDom(final String name, final DOM document) {
        this.getKeyIndex(name).setDom(document, document.getDocument());
    }
    
    public void setDOMCache(final DOMCache cache) {
        this._domCache = cache;
    }
    
    public DOMCache getDOMCache() {
        return this._domCache;
    }
    
    public SerializationHandler openOutputHandler(final String filename, final boolean append) throws TransletException {
        try {
            final TransletOutputHandlerFactory factory = TransletOutputHandlerFactory.newInstance(this._overrideDefaultParser);
            final String dirStr = new File(filename).getParent();
            if (null != dirStr && dirStr.length() > 0) {
                final File dir = new File(dirStr);
                dir.mkdirs();
            }
            factory.setEncoding(this._encoding);
            factory.setOutputMethod(this._method);
            factory.setOutputStream(new BufferedOutputStream(new FileOutputStream(filename, append)));
            factory.setOutputType(0);
            final SerializationHandler handler = factory.getSerializationHandler();
            this.transferOutputSettings(handler);
            handler.startDocument();
            return handler;
        }
        catch (final Exception e) {
            throw new TransletException(e);
        }
    }
    
    public SerializationHandler openOutputHandler(final String filename) throws TransletException {
        return this.openOutputHandler(filename, false);
    }
    
    public void closeOutputHandler(final SerializationHandler handler) {
        try {
            handler.endDocument();
            handler.close();
        }
        catch (final Exception ex) {}
    }
    
    @Override
    public abstract void transform(final DOM p0, final DTMAxisIterator p1, final SerializationHandler p2) throws TransletException;
    
    @Override
    public final void transform(final DOM document, final SerializationHandler handler) throws TransletException {
        try {
            this.transform(document, document.getIterator(), handler);
        }
        finally {
            this._keyIndexes = null;
        }
    }
    
    public final void characters(final String string, final SerializationHandler handler) throws TransletException {
        if (string != null) {
            try {
                handler.characters(string);
            }
            catch (final Exception e) {
                throw new TransletException(e);
            }
        }
    }
    
    public void addCdataElement(final String name) {
        if (this._cdata == null) {
            this._cdata = new Vector();
        }
        final int lastColon = name.lastIndexOf(58);
        if (lastColon > 0) {
            final String uri = name.substring(0, lastColon);
            final String localName = name.substring(lastColon + 1);
            this._cdata.addElement(uri);
            this._cdata.addElement(localName);
        }
        else {
            this._cdata.addElement(null);
            this._cdata.addElement(name);
        }
    }
    
    protected void transferOutputSettings(final SerializationHandler handler) {
        if (this._method != null) {
            if (this._method.equals("xml")) {
                if (this._standalone != null) {
                    handler.setStandalone(this._standalone);
                }
                if (this._omitHeader) {
                    handler.setOmitXMLDeclaration(true);
                }
                handler.setCdataSectionElements(this._cdata);
                if (this._version != null) {
                    handler.setVersion(this._version);
                }
                handler.setIndent(this._indent);
                handler.setIndentAmount(this._indentamount);
                if (this._doctypeSystem != null) {
                    handler.setDoctype(this._doctypeSystem, this._doctypePublic);
                }
                handler.setIsStandalone(this._isStandalone);
            }
            else if (this._method.equals("html")) {
                handler.setIndent(this._indent);
                handler.setDoctype(this._doctypeSystem, this._doctypePublic);
                if (this._mediaType != null) {
                    handler.setMediaType(this._mediaType);
                }
            }
        }
        else {
            handler.setCdataSectionElements(this._cdata);
            if (this._version != null) {
                handler.setVersion(this._version);
            }
            if (this._standalone != null) {
                handler.setStandalone(this._standalone);
            }
            if (this._omitHeader) {
                handler.setOmitXMLDeclaration(true);
            }
            handler.setIndent(this._indent);
            handler.setDoctype(this._doctypeSystem, this._doctypePublic);
            handler.setIsStandalone(this._isStandalone);
        }
    }
    
    @Override
    public void addAuxiliaryClass(final Class auxClass) {
        if (this._auxClasses == null) {
            this._auxClasses = new HashMap<String, Class<?>>();
        }
        this._auxClasses.put(auxClass.getName(), auxClass);
    }
    
    public void setAuxiliaryClasses(final Map<String, Class<?>> auxClasses) {
        this._auxClasses = auxClasses;
    }
    
    @Override
    public Class getAuxiliaryClass(final String className) {
        if (this._auxClasses == null) {
            return null;
        }
        return this._auxClasses.get(className);
    }
    
    @Override
    public String[] getNamesArray() {
        return this.namesArray;
    }
    
    @Override
    public String[] getUrisArray() {
        return this.urisArray;
    }
    
    @Override
    public int[] getTypesArray() {
        return this.typesArray;
    }
    
    @Override
    public String[] getNamespaceArray() {
        return this.namespaceArray;
    }
    
    public boolean hasIdCall() {
        return this._hasIdCall;
    }
    
    public Templates getTemplates() {
        return this._templates;
    }
    
    public void setTemplates(final Templates templates) {
        this._templates = templates;
    }
    
    @Override
    public boolean overrideDefaultParser() {
        return this._overrideDefaultParser;
    }
    
    @Override
    public void setOverrideDefaultParser(final boolean flag) {
        this._overrideDefaultParser = flag;
    }
    
    public String getAllowedProtocols() {
        return this._accessExternalStylesheet;
    }
    
    public void setAllowedProtocols(final String protocols) {
        this._accessExternalStylesheet = protocols;
    }
    
    public Document newDocument(final String uri, final String qname) throws ParserConfigurationException {
        if (this._domImplementation == null) {
            final DocumentBuilderFactory dbf = JdkXmlUtils.getDOMFactory(this._overrideDefaultParser);
            this._domImplementation = dbf.newDocumentBuilder().getDOMImplementation();
        }
        return this._domImplementation.createDocument(uri, qname, null);
    }
}
