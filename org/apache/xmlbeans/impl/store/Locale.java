package org.apache.xmlbeans.impl.store;

import org.apache.xmlbeans.QNameCache;
import org.xml.sax.SAXParseException;
import org.xml.sax.ErrorHandler;
import org.apache.xmlbeans.CDataBookmark;
import org.apache.xmlbeans.XmlLineNumber;
import org.apache.xmlbeans.XmlRuntimeException;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.DTDHandler;
import org.xml.sax.ext.DeclHandler;
import java.util.Hashtable;
import java.lang.ref.PhantomReference;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.w3c.dom.Element;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.apache.xmlbeans.impl.common.SAXHelper;
import org.apache.xmlbeans.XmlOptionsBean;
import org.xml.sax.XMLReader;
import org.apache.xmlbeans.impl.common.ResolverUtil;
import org.xml.sax.EntityResolver;
import java.util.Iterator;
import org.apache.xmlbeans.QNameSet;
import java.lang.ref.SoftReference;
import org.apache.xmlbeans.XmlSaxHandler;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import java.io.InputStream;
import org.apache.xmlbeans.XmlDocumentProperties;
import org.apache.xmlbeans.xml.stream.XMLName;
import org.apache.xmlbeans.xml.stream.Attribute;
import org.apache.xmlbeans.xml.stream.AttributeIterator;
import java.util.Map;
import org.apache.xmlbeans.xml.stream.ProcessingInstruction;
import org.apache.xmlbeans.xml.stream.Comment;
import org.apache.xmlbeans.xml.stream.CharacterData;
import org.apache.xmlbeans.xml.stream.Space;
import org.apache.xmlbeans.impl.common.XMLNameHelper;
import org.apache.xmlbeans.xml.stream.StartDocument;
import java.util.HashMap;
import org.apache.xmlbeans.xml.stream.StartElement;
import org.apache.xmlbeans.xml.stream.Location;
import org.apache.xmlbeans.xml.stream.XMLEvent;
import javax.xml.stream.XMLStreamReader;
import org.apache.xmlbeans.xml.stream.XMLStreamException;
import org.apache.xmlbeans.xml.stream.XMLInputStream;
import java.io.Reader;
import java.io.IOException;
import org.xml.sax.InputSource;
import java.io.StringReader;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlError;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.impl.common.QNameHelper;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlTokenSource;
import org.apache.xmlbeans.XmlBeans;
import org.apache.xmlbeans.XmlOptions;
import java.lang.ref.ReferenceQueue;
import org.apache.xmlbeans.SchemaTypeLoader;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.impl.common.XmlLocale;
import org.w3c.dom.DOMImplementation;

public final class Locale implements DOMImplementation, Saaj.SaajCallback, XmlLocale
{
    static final int ROOT = 1;
    static final int ELEM = 2;
    static final int ATTR = 3;
    static final int COMMENT = 4;
    static final int PROCINST = 5;
    static final int TEXT = 0;
    static final int WS_UNSPECIFIED = 0;
    static final int WS_PRESERVE = 1;
    static final int WS_REPLACE = 2;
    static final int WS_COLLAPSE = 3;
    static final String _xsi = "http://www.w3.org/2001/XMLSchema-instance";
    static final String _schema = "http://www.w3.org/2001/XMLSchema";
    static final String _openFragUri = "http://www.openuri.org/fragment";
    static final String _xml1998Uri = "http://www.w3.org/XML/1998/namespace";
    static final String _xmlnsUri = "http://www.w3.org/2000/xmlns/";
    static final QName _xsiNil;
    static final QName _xsiType;
    static final QName _xsiLoc;
    static final QName _xsiNoLoc;
    static final QName _openuriFragment;
    static final QName _xmlFragment;
    public static final String USE_SAME_LOCALE = "USE_SAME_LOCALE";
    @Deprecated
    public static final String COPY_USE_NEW_LOCALE = "COPY_USE_NEW_LOCALE";
    private static ThreadLocal tl_scrubBuffer;
    boolean _noSync;
    SchemaTypeLoader _schemaTypeLoader;
    private ReferenceQueue _refQueue;
    private int _entryCount;
    int _numTempFramesLeft;
    Cur[] _tempFrames;
    Cur _curPool;
    int _curPoolCount;
    Cur _registered;
    ChangeListener _changeListeners;
    long _versionAll;
    long _versionSansText;
    Cur.Locations _locations;
    private CharUtil _charUtil;
    int _offSrc;
    int _cchSrc;
    Saaj _saaj;
    DomImpl.Dom _ownerDoc;
    QNameFactory _qnameFactory;
    boolean _validateOnSet;
    int _posTemp;
    nthCache _nthCache_A;
    nthCache _nthCache_B;
    domNthCache _domNthCache_A;
    domNthCache _domNthCache_B;
    static final /* synthetic */ boolean $assertionsDisabled;
    
    private Locale(final SchemaTypeLoader stl, XmlOptions options) {
        this._nthCache_A = new nthCache();
        this._nthCache_B = new nthCache();
        this._domNthCache_A = new domNthCache();
        this._domNthCache_B = new domNthCache();
        options = XmlOptions.maskNull(options);
        this._noSync = options.hasOption("UNSYNCHRONIZED");
        final int numTempFramesLeft = 8;
        this._numTempFramesLeft = numTempFramesLeft;
        this._tempFrames = new Cur[numTempFramesLeft];
        this._qnameFactory = new DefaultQNameFactory();
        this._locations = new Cur.Locations(this);
        this._schemaTypeLoader = stl;
        this._validateOnSet = options.hasOption("VALIDATE_ON_SET");
        final Object saajObj = options.get("SAAJ_IMPL");
        if (saajObj != null) {
            if (!(saajObj instanceof Saaj)) {
                throw new IllegalStateException("Saaj impl not correct type: " + saajObj);
            }
            (this._saaj = (Saaj)saajObj).setCallback(this);
        }
    }
    
    static Locale getLocale(SchemaTypeLoader stl, XmlOptions options) {
        if (stl == null) {
            stl = XmlBeans.getContextTypeLoader();
        }
        options = XmlOptions.maskNull(options);
        Locale l = null;
        if (options.hasOption("USE_SAME_LOCALE")) {
            final Object source = options.get("USE_SAME_LOCALE");
            if (source instanceof Locale) {
                l = (Locale)source;
            }
            else {
                if (!(source instanceof XmlTokenSource)) {
                    throw new IllegalArgumentException("Source locale not understood: " + source);
                }
                l = (Locale)((XmlTokenSource)source).monitor();
            }
            if (l._schemaTypeLoader != stl) {
                throw new IllegalArgumentException("Source locale does not support same schema type loader");
            }
            if (l._saaj != null && l._saaj != options.get("SAAJ_IMPL")) {
                throw new IllegalArgumentException("Source locale does not support same saaj");
            }
            if (l._validateOnSet && !options.hasOption("VALIDATE_ON_SET")) {
                throw new IllegalArgumentException("Source locale does not support same validate on set");
            }
        }
        else {
            l = new Locale(stl, options);
        }
        return l;
    }
    
    static void associateSourceName(final Cur c, final XmlOptions options) {
        final String sourceName = (String)XmlOptions.safeGet(options, "DOCUMENT_SOURCE_NAME");
        if (sourceName != null) {
            getDocProps(c, true).setSourceName(sourceName);
        }
    }
    
    static void autoTypeDocument(final Cur c, final SchemaType requestedType, XmlOptions options) throws XmlException {
        assert c.isRoot();
        options = XmlOptions.maskNull(options);
        final SchemaType optionType = (SchemaType)options.get("DOCUMENT_TYPE");
        if (optionType != null) {
            c.setType(optionType);
            return;
        }
        SchemaType type = null;
        if (requestedType == null || requestedType.getName() != null) {
            final QName xsiTypeName = c.getXsiTypeName();
            final SchemaType xsiSchemaType = (xsiTypeName == null) ? null : c._locale._schemaTypeLoader.findType(xsiTypeName);
            if (requestedType == null || requestedType.isAssignableFrom(xsiSchemaType)) {
                type = xsiSchemaType;
            }
        }
        if (type == null && (requestedType == null || requestedType.isDocumentType())) {
            assert c.isRoot();
            c.push();
            final QName docElemName = (!c.hasAttrs() && toFirstChildElement(c) && !toNextSiblingElement(c)) ? c.getName() : null;
            c.pop();
            if (docElemName != null) {
                type = c._locale._schemaTypeLoader.findDocumentType(docElemName);
                if (type != null && requestedType != null) {
                    final QName requesteddocElemNameName = requestedType.getDocumentElementName();
                    if (!requesteddocElemNameName.equals(docElemName) && !requestedType.isValidSubstitution(docElemName)) {
                        throw new XmlException("Element " + QNameHelper.pretty(docElemName) + " is not a valid " + QNameHelper.pretty(requesteddocElemNameName) + " document or a valid substitution.");
                    }
                }
            }
        }
        if (type == null && requestedType == null) {
            c.push();
            type = ((toFirstNormalAttr(c) && !toNextNormalAttr(c)) ? c._locale._schemaTypeLoader.findAttributeType(c.getName()) : null);
            c.pop();
        }
        if (type == null) {
            type = requestedType;
        }
        if (type == null) {
            type = XmlBeans.NO_TYPE;
        }
        c.setType(type);
        if (requestedType != null) {
            if (type.isDocumentType()) {
                verifyDocumentType(c, type.getDocumentElementName());
            }
            else if (type.isAttributeType()) {
                verifyAttributeType(c, type.getAttributeTypeAttributeName());
            }
        }
    }
    
    private static boolean namespacesSame(final QName n1, final QName n2) {
        return n1 == n2 || (n1 != null && n2 != null && (n1.getNamespaceURI() == n2.getNamespaceURI() || (n1.getNamespaceURI() != null && n2.getNamespaceURI() != null && n1.getNamespaceURI().equals(n2.getNamespaceURI()))));
    }
    
    private static void addNamespace(final StringBuffer sb, final QName name) {
        if (name.getNamespaceURI() == null) {
            sb.append("<no namespace>");
        }
        else {
            sb.append("\"");
            sb.append(name.getNamespaceURI());
            sb.append("\"");
        }
    }
    
    private static void verifyDocumentType(final Cur c, final QName docElemName) throws XmlException {
        assert c.isRoot();
        c.push();
        try {
            StringBuffer sb = null;
            if (!toFirstChildElement(c) || toNextSiblingElement(c)) {
                sb = new StringBuffer();
                sb.append("The document is not a ");
                sb.append(QNameHelper.pretty(docElemName));
                sb.append(c.isRoot() ? ": no document element" : ": multiple document elements");
            }
            else {
                final QName name = c.getName();
                if (!name.equals(docElemName)) {
                    sb = new StringBuffer();
                    sb.append("The document is not a ");
                    sb.append(QNameHelper.pretty(docElemName));
                    if (docElemName.getLocalPart().equals(name.getLocalPart())) {
                        sb.append(": document element namespace mismatch ");
                        sb.append("expected ");
                        addNamespace(sb, docElemName);
                        sb.append(" got ");
                        addNamespace(sb, name);
                    }
                    else if (namespacesSame(docElemName, name)) {
                        sb.append(": document element local name mismatch ");
                        sb.append("expected " + docElemName.getLocalPart());
                        sb.append(" got " + name.getLocalPart());
                    }
                    else {
                        sb.append(": document element mismatch ");
                        sb.append("got ");
                        sb.append(QNameHelper.pretty(name));
                    }
                }
            }
            if (sb != null) {
                final XmlError err = XmlError.forCursor(sb.toString(), new Cursor(c));
                throw new XmlException(err.toString(), null, err);
            }
        }
        finally {
            c.pop();
        }
    }
    
    private static void verifyAttributeType(final Cur c, final QName attrName) throws XmlException {
        assert c.isRoot();
        c.push();
        try {
            StringBuffer sb = null;
            if (!toFirstNormalAttr(c) || toNextNormalAttr(c)) {
                sb = new StringBuffer();
                sb.append("The document is not a ");
                sb.append(QNameHelper.pretty(attrName));
                sb.append(c.isRoot() ? ": no attributes" : ": multiple attributes");
            }
            else {
                final QName name = c.getName();
                if (!name.equals(attrName)) {
                    sb = new StringBuffer();
                    sb.append("The document is not a ");
                    sb.append(QNameHelper.pretty(attrName));
                    if (attrName.getLocalPart().equals(name.getLocalPart())) {
                        sb.append(": attribute namespace mismatch ");
                        sb.append("expected ");
                        addNamespace(sb, attrName);
                        sb.append(" got ");
                        addNamespace(sb, name);
                    }
                    else if (namespacesSame(attrName, name)) {
                        sb.append(": attribute local name mismatch ");
                        sb.append("expected " + attrName.getLocalPart());
                        sb.append(" got " + name.getLocalPart());
                    }
                    else {
                        sb.append(": attribute element mismatch ");
                        sb.append("got ");
                        sb.append(QNameHelper.pretty(name));
                    }
                }
            }
            if (sb != null) {
                final XmlError err = XmlError.forCursor(sb.toString(), new Cursor(c));
                throw new XmlException(err.toString(), null, err);
            }
        }
        finally {
            c.pop();
        }
    }
    
    static boolean isFragmentQName(final QName name) {
        return name.equals(Locale._openuriFragment) || name.equals(Locale._xmlFragment);
    }
    
    static boolean isFragment(final Cur start, final Cur end) {
        assert !end.isAttr();
        start.push();
        end.push();
        int numDocElems = 0;
        boolean isFrag = false;
        while (!start.isSamePos(end)) {
            final int k = start.kind();
            if (k == 3) {
                break;
            }
            if (k == 0 && !isWhiteSpace(start.getCharsAsString(-1))) {
                isFrag = true;
                break;
            }
            if (k == 2 && ++numDocElems > 1) {
                isFrag = true;
                break;
            }
            assert k != 3;
            if (k != 0) {
                start.toEnd();
            }
            start.next();
        }
        start.pop();
        end.pop();
        return isFrag || numDocElems != 1;
    }
    
    public static XmlObject newInstance(final SchemaTypeLoader stl, final SchemaType type, final XmlOptions options) {
        final Locale l = getLocale(stl, options);
        if (l.noSync()) {
            l.enter();
            try {
                return l.newInstance(type, options);
            }
            finally {
                l.exit();
            }
        }
        synchronized (l) {
            l.enter();
            try {
                return l.newInstance(type, options);
            }
            finally {
                l.exit();
            }
        }
    }
    
    private XmlObject newInstance(final SchemaType type, XmlOptions options) {
        options = XmlOptions.maskNull(options);
        final Cur c = this.tempCur();
        SchemaType sType = (SchemaType)options.get("DOCUMENT_TYPE");
        if (sType == null) {
            sType = ((type == null) ? XmlObject.type : type);
        }
        if (sType.isDocumentType()) {
            c.createDomDocumentRoot();
        }
        else {
            c.createRoot();
        }
        c.setType(sType);
        final XmlObject x = (XmlObject)c.getUser();
        c.release();
        return x;
    }
    
    public static DOMImplementation newDomImplementation(final SchemaTypeLoader stl, final XmlOptions options) {
        return getLocale(stl, options);
    }
    
    public static XmlObject parseToXmlObject(final SchemaTypeLoader stl, final String xmlText, final SchemaType type, final XmlOptions options) throws XmlException {
        final Locale l = getLocale(stl, options);
        if (l.noSync()) {
            l.enter();
            try {
                return l.parseToXmlObject(xmlText, type, options);
            }
            finally {
                l.exit();
            }
        }
        synchronized (l) {
            l.enter();
            try {
                return l.parseToXmlObject(xmlText, type, options);
            }
            finally {
                l.exit();
            }
        }
    }
    
    private XmlObject parseToXmlObject(final String xmlText, final SchemaType type, final XmlOptions options) throws XmlException {
        final Cur c = this.parse(xmlText, type, options);
        final XmlObject x = (XmlObject)c.getUser();
        c.release();
        return x;
    }
    
    Cur parse(final String s, final SchemaType type, final XmlOptions options) throws XmlException {
        final Reader r = new StringReader(s);
        try {
            final Cur c = getSaxLoader(options).load(this, new InputSource(r), options);
            autoTypeDocument(c, type, options);
            return c;
        }
        catch (final IOException e) {
            assert false : "StringReader should not throw IOException";
            throw new XmlException(e.getMessage(), e);
        }
        finally {
            try {
                r.close();
            }
            catch (final IOException ex) {}
        }
    }
    
    @Deprecated
    public static XmlObject parseToXmlObject(final SchemaTypeLoader stl, final XMLInputStream xis, final SchemaType type, final XmlOptions options) throws XmlException, XMLStreamException {
        final Locale l = getLocale(stl, options);
        if (l.noSync()) {
            l.enter();
            try {
                return l.parseToXmlObject(xis, type, options);
            }
            finally {
                l.exit();
            }
        }
        synchronized (l) {
            l.enter();
            try {
                return l.parseToXmlObject(xis, type, options);
            }
            finally {
                l.exit();
            }
        }
    }
    
    @Deprecated
    public XmlObject parseToXmlObject(final XMLInputStream xis, final SchemaType type, final XmlOptions options) throws XmlException, XMLStreamException {
        Cur c;
        try {
            c = this.loadXMLInputStream(xis, options);
        }
        catch (final XMLStreamException e) {
            throw new XmlException(e.getMessage(), e);
        }
        autoTypeDocument(c, type, options);
        final XmlObject x = (XmlObject)c.getUser();
        c.release();
        return x;
    }
    
    public static XmlObject parseToXmlObject(final SchemaTypeLoader stl, final XMLStreamReader xsr, final SchemaType type, final XmlOptions options) throws XmlException {
        final Locale l = getLocale(stl, options);
        if (l.noSync()) {
            l.enter();
            try {
                return l.parseToXmlObject(xsr, type, options);
            }
            finally {
                l.exit();
            }
        }
        synchronized (l) {
            l.enter();
            try {
                return l.parseToXmlObject(xsr, type, options);
            }
            finally {
                l.exit();
            }
        }
    }
    
    public XmlObject parseToXmlObject(final XMLStreamReader xsr, final SchemaType type, final XmlOptions options) throws XmlException {
        Cur c;
        try {
            c = this.loadXMLStreamReader(xsr, options);
        }
        catch (final javax.xml.stream.XMLStreamException e) {
            throw new XmlException(e.getMessage(), e);
        }
        autoTypeDocument(c, type, options);
        final XmlObject x = (XmlObject)c.getUser();
        c.release();
        return x;
    }
    
    private static void lineNumber(final XMLEvent xe, final LoadContext context) {
        final Location loc = xe.getLocation();
        if (loc != null) {
            context.lineNumber(loc.getLineNumber(), loc.getColumnNumber(), -1);
        }
    }
    
    private static void lineNumber(final XMLStreamReader xsr, final LoadContext context) {
        final javax.xml.stream.Location loc = xsr.getLocation();
        if (loc != null) {
            context.lineNumber(loc.getLineNumber(), loc.getColumnNumber(), loc.getCharacterOffset());
        }
    }
    
    private void doAttributes(final XMLStreamReader xsr, final LoadContext context) {
        for (int n = xsr.getAttributeCount(), a = 0; a < n; ++a) {
            context.attr(xsr.getAttributeLocalName(a), xsr.getAttributeNamespace(a), xsr.getAttributePrefix(a), xsr.getAttributeValue(a));
        }
    }
    
    private void doNamespaces(final XMLStreamReader xsr, final LoadContext context) {
        for (int n = xsr.getNamespaceCount(), a = 0; a < n; ++a) {
            final String prefix = xsr.getNamespacePrefix(a);
            if (prefix == null || prefix.length() == 0) {
                context.attr("xmlns", "http://www.w3.org/2000/xmlns/", null, xsr.getNamespaceURI(a));
            }
            else {
                context.attr(prefix, "http://www.w3.org/2000/xmlns/", "xmlns", xsr.getNamespaceURI(a));
            }
        }
    }
    
    @Deprecated
    private Cur loadXMLInputStream(final XMLInputStream xis, XmlOptions options) throws XMLStreamException {
        options = XmlOptions.maskNull(options);
        final boolean lineNums = options.hasOption("LOAD_LINE_NUMBERS");
        final XMLEvent x = xis.peek();
        if (x != null && x.getType() == 2) {
            final Map nsMap = ((StartElement)x).getNamespaceMap();
            if (nsMap != null && nsMap.size() > 0) {
                final Map namespaces = new HashMap();
                namespaces.putAll(nsMap);
                options = new XmlOptions(options);
                options.put("LOAD_ADDITIONAL_NAMESPACES", namespaces);
            }
        }
        String systemId = null;
        String encoding = null;
        String version = null;
        boolean standAlone = true;
        final LoadContext context = new Cur.CurLoadContext(this, options);
    Label_0739:
        for (XMLEvent xe = xis.next(); xe != null; xe = xis.next()) {
            switch (xe.getType()) {
                case 256: {
                    final StartDocument doc = (StartDocument)xe;
                    systemId = doc.getSystemId();
                    encoding = doc.getCharacterEncodingScheme();
                    version = doc.getVersion();
                    standAlone = doc.isStandalone();
                    standAlone = doc.isStandalone();
                    if (lineNums) {
                        lineNumber(xe, context);
                        break;
                    }
                    break;
                }
                case 512: {
                    if (lineNums) {
                        lineNumber(xe, context);
                        break Label_0739;
                    }
                    break Label_0739;
                }
                case 128: {
                    if (!xis.hasNext()) {
                        break Label_0739;
                    }
                    break;
                }
                case 2: {
                    context.startElement(XMLNameHelper.getQName(xe.getName()));
                    if (lineNums) {
                        lineNumber(xe, context);
                    }
                    AttributeIterator ai = ((StartElement)xe).getAttributes();
                    while (ai.hasNext()) {
                        final Attribute attr = ai.next();
                        context.attr(XMLNameHelper.getQName(attr.getName()), attr.getValue());
                    }
                    ai = ((StartElement)xe).getNamespaces();
                    while (ai.hasNext()) {
                        final Attribute attr = ai.next();
                        final XMLName name = attr.getName();
                        String local = name.getLocalName();
                        if (name.getPrefix() == null && local.equals("xmlns")) {
                            local = "";
                        }
                        context.xmlns(local, attr.getValue());
                    }
                    break;
                }
                case 4: {
                    context.endElement();
                    if (lineNums) {
                        lineNumber(xe, context);
                        break;
                    }
                    break;
                }
                case 64: {
                    if (((Space)xe).ignorable()) {
                        break;
                    }
                }
                case 16: {
                    final CharacterData cd = (CharacterData)xe;
                    if (!cd.hasContent()) {
                        break;
                    }
                    context.text(cd.getContent());
                    if (lineNums) {
                        lineNumber(xe, context);
                        break;
                    }
                    break;
                }
                case 32: {
                    final Comment comment = (Comment)xe;
                    if (!comment.hasContent()) {
                        break;
                    }
                    context.comment(comment.getContent());
                    if (lineNums) {
                        lineNumber(xe, context);
                        break;
                    }
                    break;
                }
                case 8: {
                    final ProcessingInstruction procInstr = (ProcessingInstruction)xe;
                    context.procInst(procInstr.getTarget(), procInstr.getData());
                    if (lineNums) {
                        lineNumber(xe, context);
                        break;
                    }
                    break;
                }
                case 1:
                case 1024:
                case 2048:
                case 4096:
                case 8192: {
                    break;
                }
                default: {
                    throw new RuntimeException("Unhandled xml event type: " + xe.getTypeAsString());
                }
            }
        }
        final Cur c = context.finish();
        associateSourceName(c, options);
        final XmlDocumentProperties props = getDocProps(c, true);
        props.setDoctypeSystemId(systemId);
        props.setEncoding(encoding);
        props.setVersion(version);
        props.setStandalone(standAlone);
        return c;
    }
    
    private Cur loadXMLStreamReader(final XMLStreamReader xsr, XmlOptions options) throws javax.xml.stream.XMLStreamException {
        options = XmlOptions.maskNull(options);
        final boolean lineNums = options.hasOption("LOAD_LINE_NUMBERS");
        String encoding = null;
        String version = null;
        boolean standAlone = false;
        final LoadContext context = new Cur.CurLoadContext(this, options);
        int depth = 0;
        int eventType = xsr.getEventType();
    Label_0417:
        while (true) {
            switch (eventType) {
                case 7: {
                    ++depth;
                    encoding = xsr.getCharacterEncodingScheme();
                    version = xsr.getVersion();
                    standAlone = xsr.isStandalone();
                    if (lineNums) {
                        lineNumber(xsr, context);
                        break;
                    }
                    break;
                }
                case 8: {
                    --depth;
                    if (lineNums) {
                        lineNumber(xsr, context);
                        break Label_0417;
                    }
                    break Label_0417;
                }
                case 1: {
                    ++depth;
                    context.startElement(xsr.getName());
                    if (lineNums) {
                        lineNumber(xsr, context);
                    }
                    this.doAttributes(xsr, context);
                    this.doNamespaces(xsr, context);
                    break;
                }
                case 2: {
                    --depth;
                    context.endElement();
                    if (lineNums) {
                        lineNumber(xsr, context);
                        break;
                    }
                    break;
                }
                case 4:
                case 12: {
                    context.text(xsr.getTextCharacters(), xsr.getTextStart(), xsr.getTextLength());
                    if (lineNums) {
                        lineNumber(xsr, context);
                        break;
                    }
                    break;
                }
                case 5: {
                    final String comment = xsr.getText();
                    context.comment(comment);
                    if (lineNums) {
                        lineNumber(xsr, context);
                        break;
                    }
                    break;
                }
                case 3: {
                    context.procInst(xsr.getPITarget(), xsr.getPIData());
                    if (lineNums) {
                        lineNumber(xsr, context);
                        break;
                    }
                    break;
                }
                case 10: {
                    this.doAttributes(xsr, context);
                    break;
                }
                case 13: {
                    this.doNamespaces(xsr, context);
                    break;
                }
                case 9: {
                    context.text(xsr.getText());
                    break;
                }
                case 6:
                case 11: {
                    break;
                }
                default: {
                    throw new RuntimeException("Unhandled xml event type: " + eventType);
                }
            }
            if (!xsr.hasNext()) {
                break;
            }
            if (depth <= 0) {
                break;
            }
            eventType = xsr.next();
        }
        final Cur c = context.finish();
        associateSourceName(c, options);
        final XmlDocumentProperties props = getDocProps(c, true);
        props.setEncoding(encoding);
        props.setVersion(version);
        props.setStandalone(standAlone);
        return c;
    }
    
    public static XmlObject parseToXmlObject(final SchemaTypeLoader stl, final InputStream is, final SchemaType type, final XmlOptions options) throws XmlException, IOException {
        final Locale l = getLocale(stl, options);
        if (l.noSync()) {
            l.enter();
            try {
                return l.parseToXmlObject(is, type, options);
            }
            finally {
                l.exit();
            }
        }
        synchronized (l) {
            l.enter();
            try {
                return l.parseToXmlObject(is, type, options);
            }
            finally {
                l.exit();
            }
        }
    }
    
    private XmlObject parseToXmlObject(final InputStream is, final SchemaType type, final XmlOptions options) throws XmlException, IOException {
        final Cur c = getSaxLoader(options).load(this, new InputSource(is), options);
        autoTypeDocument(c, type, options);
        final XmlObject x = (XmlObject)c.getUser();
        c.release();
        return x;
    }
    
    public static XmlObject parseToXmlObject(final SchemaTypeLoader stl, final Reader reader, final SchemaType type, final XmlOptions options) throws XmlException, IOException {
        final Locale l = getLocale(stl, options);
        if (l.noSync()) {
            l.enter();
            try {
                return l.parseToXmlObject(reader, type, options);
            }
            finally {
                l.exit();
            }
        }
        synchronized (l) {
            l.enter();
            try {
                return l.parseToXmlObject(reader, type, options);
            }
            finally {
                l.exit();
            }
        }
    }
    
    private XmlObject parseToXmlObject(final Reader reader, final SchemaType type, final XmlOptions options) throws XmlException, IOException {
        final Cur c = getSaxLoader(options).load(this, new InputSource(reader), options);
        autoTypeDocument(c, type, options);
        final XmlObject x = (XmlObject)c.getUser();
        c.release();
        return x;
    }
    
    public static XmlObject parseToXmlObject(final SchemaTypeLoader stl, final Node node, final SchemaType type, final XmlOptions options) throws XmlException {
        final Locale l = getLocale(stl, options);
        if (l.noSync()) {
            l.enter();
            try {
                return l.parseToXmlObject(node, type, options);
            }
            finally {
                l.exit();
            }
        }
        synchronized (l) {
            l.enter();
            try {
                return l.parseToXmlObject(node, type, options);
            }
            finally {
                l.exit();
            }
        }
    }
    
    public XmlObject parseToXmlObject(final Node node, final SchemaType type, final XmlOptions options) throws XmlException {
        final LoadContext context = new Cur.CurLoadContext(this, options);
        this.loadNode(node, context);
        final Cur c = context.finish();
        associateSourceName(c, options);
        autoTypeDocument(c, type, options);
        final XmlObject x = (XmlObject)c.getUser();
        c.release();
        return x;
    }
    
    private void loadNodeChildren(final Node n, final LoadContext context) {
        for (Node c = n.getFirstChild(); c != null; c = c.getNextSibling()) {
            this.loadNode(c, context);
        }
    }
    
    void loadNode(final Node n, final LoadContext context) {
        switch (n.getNodeType()) {
            case 5:
            case 9:
            case 11: {
                this.loadNodeChildren(n, context);
                break;
            }
            case 1: {
                context.startElement(this.makeQualifiedQName(n.getNamespaceURI(), n.getNodeName()));
                final NamedNodeMap attrs = n.getAttributes();
                for (int i = 0; i < attrs.getLength(); ++i) {
                    final Node a = attrs.item(i);
                    final String attrName = a.getNodeName();
                    final String attrValue = a.getNodeValue();
                    if (attrName.toLowerCase().startsWith("xmlns")) {
                        if (attrName.length() == 5) {
                            context.xmlns(null, attrValue);
                        }
                        else {
                            context.xmlns(attrName.substring(6), attrValue);
                        }
                    }
                    else {
                        context.attr(this.makeQualifiedQName(a.getNamespaceURI(), attrName), attrValue);
                    }
                }
                this.loadNodeChildren(n, context);
                context.endElement();
                break;
            }
            case 3:
            case 4: {
                context.text(n.getNodeValue());
                break;
            }
            case 8: {
                context.comment(n.getNodeValue());
                break;
            }
            case 7: {
                context.procInst(n.getNodeName(), n.getNodeValue());
                break;
            }
            case 6:
            case 10:
            case 12: {
                final Node next = n.getNextSibling();
                if (next != null) {
                    this.loadNode(next, context);
                    break;
                }
                break;
            }
            case 2: {
                throw new RuntimeException("Unexpected node");
            }
        }
    }
    
    public static XmlSaxHandler newSaxHandler(final SchemaTypeLoader stl, final SchemaType type, final XmlOptions options) {
        final Locale l = getLocale(stl, options);
        if (l.noSync()) {
            l.enter();
            try {
                return l.newSaxHandler(type, options);
            }
            finally {
                l.exit();
            }
        }
        synchronized (l) {
            l.enter();
            try {
                return l.newSaxHandler(type, options);
            }
            finally {
                l.exit();
            }
        }
    }
    
    public XmlSaxHandler newSaxHandler(final SchemaType type, final XmlOptions options) {
        return new XmlSaxHandlerImpl(this, type, options);
    }
    
    QName makeQName(final String uri, final String localPart) {
        assert localPart != null && localPart.length() > 0;
        return this._qnameFactory.getQName(uri, localPart);
    }
    
    QName makeQNameNoCheck(final String uri, final String localPart) {
        return this._qnameFactory.getQName(uri, localPart);
    }
    
    QName makeQName(final String uri, final String local, final String prefix) {
        return this._qnameFactory.getQName(uri, local, (prefix == null) ? "" : prefix);
    }
    
    QName makeQualifiedQName(final String uri, String qname) {
        if (qname == null) {
            qname = "";
        }
        final int i = qname.indexOf(58);
        return (i < 0) ? this._qnameFactory.getQName(uri, qname) : this._qnameFactory.getQName(uri, qname.substring(i + 1), qname.substring(0, i));
    }
    
    static XmlDocumentProperties getDocProps(final Cur c, final boolean ensure) {
        c.push();
        while (c.toParent()) {}
        DocProps props = (DocProps)c.getBookmark(DocProps.class);
        if (props == null && ensure) {
            c.setBookmark(DocProps.class, props = new DocProps());
        }
        c.pop();
        return props;
    }
    
    void registerForChange(final ChangeListener listener) {
        if (listener.getNextChangeListener() == null) {
            if (this._changeListeners == null) {
                listener.setNextChangeListener(listener);
            }
            else {
                listener.setNextChangeListener(this._changeListeners);
            }
            this._changeListeners = listener;
        }
    }
    
    void notifyChange() {
        while (this._changeListeners != null) {
            this._changeListeners.notifyChange();
            if (this._changeListeners.getNextChangeListener() == this._changeListeners) {
                this._changeListeners.setNextChangeListener(null);
            }
            final ChangeListener next = this._changeListeners.getNextChangeListener();
            this._changeListeners.setNextChangeListener(null);
            this._changeListeners = next;
        }
        this._locations.notifyChange();
    }
    
    static String getTextValue(final Cur c) {
        assert c.isNode();
        if (!c.hasChildren()) {
            return c.getValueAsString();
        }
        final StringBuffer sb = new StringBuffer();
        c.push();
        c.next();
        while (!c.isAtEndOfLastPush()) {
            if (c.isText()) {
                if ((!c._xobj.isComment() && !c._xobj.isProcinst()) || c._pos >= c._xobj._cchValue) {
                    CharUtil.getString(sb, c.getChars(-1), c._offSrc, c._cchSrc);
                }
            }
            c.next();
        }
        c.pop();
        return sb.toString();
    }
    
    static int getTextValue(final Cur c, final int wsr, final char[] chars, final int off, final int maxCch) {
        assert c.isNode();
        final String s = c._xobj.getValueAsString(wsr);
        int n = s.length();
        if (n > maxCch) {
            n = maxCch;
        }
        if (n <= 0) {
            return 0;
        }
        s.getChars(0, n, chars, off);
        return n;
    }
    
    static String applyWhiteSpaceRule(final String s, final int wsr) {
        final int l = (s == null) ? 0 : s.length();
        if (l == 0 || wsr == 1) {
            return s;
        }
        if (wsr == 2) {
            for (int i = 0; i < l; ++i) {
                final char ch;
                if ((ch = s.charAt(i)) == '\n' || ch == '\r' || ch == '\t') {
                    return processWhiteSpaceRule(s, wsr);
                }
            }
        }
        else if (wsr == 3) {
            if (CharUtil.isWhiteSpace(s.charAt(0)) || CharUtil.isWhiteSpace(s.charAt(l - 1))) {
                return processWhiteSpaceRule(s, wsr);
            }
            boolean lastWasWhite = false;
            for (int j = 1; j < l; ++j) {
                final boolean isWhite = CharUtil.isWhiteSpace(s.charAt(j));
                if (isWhite && lastWasWhite) {
                    return processWhiteSpaceRule(s, wsr);
                }
                lastWasWhite = isWhite;
            }
        }
        return s;
    }
    
    static String processWhiteSpaceRule(final String s, final int wsr) {
        final ScrubBuffer sb = getScrubBuffer(wsr);
        sb.scrub(s, 0, s.length());
        return sb.getResultAsString();
    }
    
    public static void clearThreadLocals() {
        Locale.tl_scrubBuffer.remove();
    }
    
    static ScrubBuffer getScrubBuffer(final int wsr) {
        final SoftReference softRef = Locale.tl_scrubBuffer.get();
        ScrubBuffer scrubBuffer = softRef.get();
        if (scrubBuffer == null) {
            scrubBuffer = new ScrubBuffer();
            Locale.tl_scrubBuffer.set(new SoftReference(scrubBuffer));
        }
        scrubBuffer.init(wsr);
        return scrubBuffer;
    }
    
    static boolean pushToContainer(final Cur c) {
        c.push();
        while (true) {
            switch (c.kind()) {
                case 1:
                case 2: {
                    return true;
                }
                case -2:
                case -1: {
                    c.pop();
                    return false;
                }
                case 4:
                case 5: {
                    c.skip();
                    continue;
                }
                default: {
                    c.nextWithAttrs();
                    continue;
                }
            }
        }
    }
    
    static boolean toFirstNormalAttr(final Cur c) {
        c.push();
        Label_0031: {
            if (c.toFirstAttr()) {
                while (c.isXmlns()) {
                    if (!c.toNextAttr()) {
                        break Label_0031;
                    }
                }
                c.popButStay();
                return true;
            }
        }
        c.pop();
        return false;
    }
    
    static boolean toPrevNormalAttr(final Cur c) {
        if (c.isAttr()) {
            c.push();
            while (Locale.$assertionsDisabled || c.isAttr()) {
                if (!c.prev()) {
                    c.pop();
                    return false;
                }
                c.prev();
                if (!c.isAttr()) {
                    c.prev();
                }
                if (c.isNormalAttr()) {
                    c.popButStay();
                    return true;
                }
            }
            throw new AssertionError();
        }
        return false;
    }
    
    static boolean toNextNormalAttr(final Cur c) {
        c.push();
        while (c.toNextAttr()) {
            if (!c.isXmlns()) {
                c.popButStay();
                return true;
            }
        }
        c.pop();
        return false;
    }
    
    Xobj findNthChildElem(final Xobj parent, final QName name, final QNameSet set, final int n) {
        assert set == null;
        assert n >= 0;
        if (parent == null) {
            return null;
        }
        final int da = this._nthCache_A.distance(parent, name, set, n);
        final int db = this._nthCache_B.distance(parent, name, set, n);
        final Xobj x = (da <= db) ? this._nthCache_A.fetch(parent, name, set, n) : this._nthCache_B.fetch(parent, name, set, n);
        if (da == db) {
            final nthCache temp = this._nthCache_A;
            this._nthCache_A = this._nthCache_B;
            this._nthCache_B = temp;
        }
        return x;
    }
    
    int count(final Xobj parent, final QName name, final QNameSet set) {
        int n = 0;
        for (Xobj x = this.findNthChildElem(parent, name, set, 0); x != null; x = x._nextSibling) {
            if (x.isElem()) {
                if (set == null) {
                    if (x._name.equals(name)) {
                        ++n;
                    }
                }
                else if (set.contains(x._name)) {
                    ++n;
                }
            }
        }
        return n;
    }
    
    static boolean toChild(final Cur c, final QName name, final int n) {
        if (n >= 0 && pushToContainer(c)) {
            final Xobj x = c._locale.findNthChildElem(c._xobj, name, null, n);
            c.pop();
            if (x != null) {
                c.moveTo(x);
                return true;
            }
        }
        return false;
    }
    
    static boolean toFirstChildElement(final Cur c) {
        final Xobj originalXobj = c._xobj;
        final int originalPos = c._pos;
        while (true) {
            switch (c.kind()) {
                case 1:
                case 2: {
                    if (!c.toFirstChild() || (!c.isElem() && !toNextSiblingElement(c))) {
                        c.moveTo(originalXobj, originalPos);
                        return false;
                    }
                    return true;
                }
                case -2:
                case -1: {
                    c.moveTo(originalXobj, originalPos);
                    return false;
                }
                case 4:
                case 5: {
                    c.skip();
                    continue;
                }
                default: {
                    c.nextWithAttrs();
                    continue;
                }
            }
        }
    }
    
    static boolean toLastChildElement(final Cur c) {
        if (!pushToContainer(c)) {
            return false;
        }
        if (!c.toLastChild() || (!c.isElem() && !toPrevSiblingElement(c))) {
            c.pop();
            return false;
        }
        c.popButStay();
        return true;
    }
    
    static boolean toPrevSiblingElement(final Cur cur) {
        if (!cur.hasParent()) {
            return false;
        }
        final Cur c = cur.tempCur();
        boolean moved = false;
        int k = c.kind();
        if (k != 3) {
            while (c.prev()) {
                k = c.kind();
                if (k == 1) {
                    break;
                }
                if (k == 2) {
                    break;
                }
                if (c.kind() == -2) {
                    c.toParent();
                    cur.moveToCur(c);
                    moved = true;
                    break;
                }
            }
        }
        c.release();
        return moved;
    }
    
    static boolean toNextSiblingElement(final Cur c) {
        if (!c.hasParent()) {
            return false;
        }
        c.push();
        int k = c.kind();
        if (k == 3) {
            c.toParent();
            c.next();
        }
        else if (k == 2) {
            c.skip();
        }
        while ((k = c.kind()) >= 0) {
            if (k == 2) {
                c.popButStay();
                return true;
            }
            if (k > 0) {
                c.toEnd();
            }
            c.next();
        }
        c.pop();
        return false;
    }
    
    static boolean toNextSiblingElement(final Cur c, final Xobj parent) {
        final Xobj originalXobj = c._xobj;
        final int originalPos = c._pos;
        int k = c.kind();
        if (k == 3) {
            c.moveTo(parent);
            c.next();
        }
        else if (k == 2) {
            c.skip();
        }
        while ((k = c.kind()) >= 0) {
            if (k == 2) {
                return true;
            }
            if (k > 0) {
                c.toEnd();
            }
            c.next();
        }
        c.moveTo(originalXobj, originalPos);
        return false;
    }
    
    static void applyNamespaces(final Cur c, final Map namespaces) {
        assert c.isContainer();
        for (final String prefix : namespaces.keySet()) {
            if (!prefix.toLowerCase().startsWith("xml") && c.namespaceForPrefix(prefix, false) == null) {
                c.push();
                c.next();
                c.createAttr(c._locale.createXmlns(prefix));
                c.next();
                c.insertString(namespaces.get(prefix));
                c.pop();
            }
        }
    }
    
    static Map getAllNamespaces(final Cur c, Map filleMe) {
        assert c.isNode();
        c.push();
        if (!c.isContainer()) {
            c.toParent();
        }
        assert c.isContainer();
        do {
            final QName cName = c.getName();
            while (c.toNextAttr()) {
                if (c.isXmlns()) {
                    final String prefix = c.getXmlnsPrefix();
                    final String uri = c.getXmlnsUri();
                    if (filleMe == null) {
                        filleMe = new HashMap();
                    }
                    if (filleMe.containsKey(prefix)) {
                        continue;
                    }
                    filleMe.put(prefix, uri);
                }
            }
            if (!c.isContainer()) {
                c.toParentRaw();
            }
        } while (c.toParentRaw());
        c.pop();
        return filleMe;
    }
    
    DomImpl.Dom findDomNthChild(final DomImpl.Dom parent, final int n) {
        assert n >= 0;
        if (parent == null) {
            return null;
        }
        final int da = this._domNthCache_A.distance(parent, n);
        final int db = this._domNthCache_B.distance(parent, n);
        DomImpl.Dom x = null;
        final boolean bInvalidate = db - this._domNthCache_B._len / 2 > 0 && db - this._domNthCache_B._len / 2 - 40 > 0;
        final boolean aInvalidate = da - this._domNthCache_A._len / 2 > 0 && da - this._domNthCache_A._len / 2 - 40 > 0;
        if (da <= db) {
            if (!aInvalidate) {
                x = this._domNthCache_A.fetch(parent, n);
            }
            else {
                this._domNthCache_B._version = -1L;
                x = this._domNthCache_B.fetch(parent, n);
            }
        }
        else if (!bInvalidate) {
            x = this._domNthCache_B.fetch(parent, n);
        }
        else {
            this._domNthCache_A._version = -1L;
            x = this._domNthCache_A.fetch(parent, n);
        }
        if (da == db) {
            final domNthCache temp = this._domNthCache_A;
            this._domNthCache_A = this._domNthCache_B;
            this._domNthCache_B = temp;
        }
        return x;
    }
    
    int domLength(final DomImpl.Dom parent) {
        if (parent == null) {
            return 0;
        }
        final int da = this._domNthCache_A.distance(parent, 0);
        final int db = this._domNthCache_B.distance(parent, 0);
        final int len = (da <= db) ? this._domNthCache_A.length(parent) : this._domNthCache_B.length(parent);
        if (da == db) {
            final domNthCache temp = this._domNthCache_A;
            this._domNthCache_A = this._domNthCache_B;
            this._domNthCache_B = temp;
        }
        return len;
    }
    
    void invalidateDomCaches(final DomImpl.Dom d) {
        if (this._domNthCache_A._parent == d) {
            this._domNthCache_A._version = -1L;
        }
        if (this._domNthCache_B._parent == d) {
            this._domNthCache_B._version = -1L;
        }
    }
    
    boolean isDomCached(final DomImpl.Dom d) {
        return this._domNthCache_A._parent == d || this._domNthCache_B._parent == d;
    }
    
    CharUtil getCharUtil() {
        if (this._charUtil == null) {
            this._charUtil = new CharUtil(1024);
        }
        return this._charUtil;
    }
    
    long version() {
        return this._versionAll;
    }
    
    Cur weakCur(final Object o) {
        assert o != null && !(o instanceof Ref);
        final Cur c = this.getCur();
        assert c._tempFrame == -1;
        assert c._ref == null;
        c._ref = new Ref(c, o);
        return c;
    }
    
    final ReferenceQueue refQueue() {
        if (this._refQueue == null) {
            this._refQueue = new ReferenceQueue();
        }
        return this._refQueue;
    }
    
    Cur tempCur() {
        return this.tempCur(null);
    }
    
    Cur tempCur(final String id) {
        final Cur c = this.getCur();
        assert c._tempFrame == -1;
        assert this._numTempFramesLeft < this._tempFrames.length : "Temp frame not pushed";
        final int frame = this._tempFrames.length - this._numTempFramesLeft - 1;
        assert frame >= 0 && frame < this._tempFrames.length;
        final Cur next = this._tempFrames[frame];
        c._nextTemp = next;
        assert c._prevTemp == null;
        if (next != null) {
            assert next._prevTemp == null;
            next._prevTemp = c;
        }
        this._tempFrames[frame] = c;
        c._tempFrame = frame;
        c._id = id;
        return c;
    }
    
    Cur getCur() {
        assert this._curPoolCount > 0;
        Cur c;
        if (this._curPool == null) {
            c = new Cur(this);
        }
        else {
            this._curPool = this._curPool.listRemove(c = this._curPool);
            --this._curPoolCount;
        }
        assert c._state == 0;
        assert c._prev == null && c._next == null;
        assert c._xobj == null && c._pos == -2;
        assert c._ref == null;
        this._registered = c.listInsert(this._registered);
        c._state = 1;
        return c;
    }
    
    void embedCurs() {
        Cur c;
        while ((c = this._registered) != null) {
            assert c._xobj != null;
            this._registered = c.listRemove(this._registered);
            c._xobj._embedded = c.listInsert(c._xobj._embedded);
            c._state = 2;
        }
    }
    
    DomImpl.TextNode createTextNode() {
        return (this._saaj == null) ? new DomImpl.TextNode(this) : new DomImpl.SaajTextNode(this);
    }
    
    DomImpl.CdataNode createCdataNode() {
        return (this._saaj == null) ? new DomImpl.CdataNode(this) : new DomImpl.SaajCdataNode(this);
    }
    
    boolean entered() {
        return this._tempFrames.length - this._numTempFramesLeft > 0;
    }
    
    public void enter(final Locale otherLocale) {
        this.enter();
        if (otherLocale != this) {
            otherLocale.enter();
        }
    }
    
    @Override
    public void enter() {
        assert this._numTempFramesLeft >= 0;
        if (--this._numTempFramesLeft <= 0) {
            final Cur[] newTempFrames = new Cur[this._tempFrames.length * 2];
            this._numTempFramesLeft = this._tempFrames.length;
            System.arraycopy(this._tempFrames, 0, newTempFrames, 0, this._tempFrames.length);
            this._tempFrames = newTempFrames;
        }
        if (++this._entryCount > 1000) {
            this.pollQueue();
            this._entryCount = 0;
        }
    }
    
    private void pollQueue() {
        if (this._refQueue != null) {
            while (true) {
                final Ref ref = (Ref)this._refQueue.poll();
                if (ref == null) {
                    break;
                }
                if (ref._cur == null) {
                    continue;
                }
                ref._cur.release();
            }
        }
    }
    
    public void exit(final Locale otherLocale) {
        this.exit();
        if (otherLocale != this) {
            otherLocale.exit();
        }
    }
    
    @Override
    public void exit() {
        assert this._numTempFramesLeft >= 0 && this._numTempFramesLeft <= this._tempFrames.length - 1 : " Temp frames mismanaged. Impossible stack frame. Unsynchronized: " + this.noSync();
        final int frame = this._tempFrames.length - ++this._numTempFramesLeft;
        while (this._tempFrames[frame] != null) {
            this._tempFrames[frame].release();
        }
    }
    
    @Override
    public boolean noSync() {
        return this._noSync;
    }
    
    @Override
    public boolean sync() {
        return !this._noSync;
    }
    
    static final boolean isWhiteSpace(final String s) {
        int l = s.length();
        while (l-- > 0) {
            if (!CharUtil.isWhiteSpace(s.charAt(l))) {
                return false;
            }
        }
        return true;
    }
    
    static final boolean isWhiteSpace(final StringBuffer sb) {
        int l = sb.length();
        while (l-- > 0) {
            if (!CharUtil.isWhiteSpace(sb.charAt(l))) {
                return false;
            }
        }
        return true;
    }
    
    static boolean beginsWithXml(final String name) {
        char ch;
        return name.length() >= 3 && ((ch = name.charAt(0)) == 'x' || ch == 'X') && ((ch = name.charAt(1)) == 'm' || ch == 'M') && ((ch = name.charAt(2)) == 'l' || ch == 'L');
    }
    
    static boolean isXmlns(final QName name) {
        final String prefix = name.getPrefix();
        return prefix.equals("xmlns") || (prefix.length() == 0 && name.getLocalPart().equals("xmlns"));
    }
    
    QName createXmlns(String prefix) {
        if (prefix == null) {
            prefix = "";
        }
        return (prefix.length() == 0) ? this.makeQName("http://www.w3.org/2000/xmlns/", "xmlns", "") : this.makeQName("http://www.w3.org/2000/xmlns/", prefix, "xmlns");
    }
    
    static String xmlnsPrefix(final QName name) {
        return name.getPrefix().equals("xmlns") ? name.getLocalPart() : "";
    }
    
    private static SaxLoader getSaxLoader(XmlOptions options) throws XmlException {
        options = XmlOptions.maskNull(options);
        EntityResolver er = null;
        if (!options.hasOption("LOAD_USE_DEFAULT_RESOLVER")) {
            er = (EntityResolver)options.get("ENTITY_RESOLVER");
            if (er == null) {
                er = ResolverUtil.getGlobalEntityResolver();
            }
            if (er == null) {
                er = new DefaultEntityResolver();
            }
        }
        XMLReader xr = (XMLReader)options.get("LOAD_USE_XMLREADER");
        if (xr == null) {
            try {
                xr = SAXHelper.newXMLReader(new XmlOptionsBean(options));
            }
            catch (final Exception e) {
                throw new XmlException("Problem creating XMLReader", e);
            }
        }
        final SaxLoader sl = new XmlReaderSaxLoader(xr);
        if (er != null) {
            xr.setEntityResolver(er);
        }
        return sl;
    }
    
    private DomImpl.Dom load(final InputSource is, final XmlOptions options) throws XmlException, IOException {
        return getSaxLoader(options).load(this, is, options).getDom();
    }
    
    public DomImpl.Dom load(final Reader r) throws XmlException, IOException {
        return this.load(r, null);
    }
    
    public DomImpl.Dom load(final Reader r, final XmlOptions options) throws XmlException, IOException {
        return this.load(new InputSource(r), options);
    }
    
    public DomImpl.Dom load(final InputStream in) throws XmlException, IOException {
        return this.load(in, null);
    }
    
    public DomImpl.Dom load(final InputStream in, final XmlOptions options) throws XmlException, IOException {
        return this.load(new InputSource(in), options);
    }
    
    public DomImpl.Dom load(final String s) throws XmlException {
        return this.load(s, null);
    }
    
    public DomImpl.Dom load(final String s, final XmlOptions options) throws XmlException {
        final Reader r = new StringReader(s);
        try {
            return this.load(r, options);
        }
        catch (final IOException e) {
            assert false : "StringReader should not throw IOException";
            throw new XmlException(e.getMessage(), e);
        }
        finally {
            try {
                r.close();
            }
            catch (final IOException ex) {}
        }
    }
    
    @Override
    public Document createDocument(final String uri, final String qname, final DocumentType doctype) {
        return DomImpl._domImplementation_createDocument(this, uri, qname, doctype);
    }
    
    @Override
    public DocumentType createDocumentType(final String qname, final String publicId, final String systemId) {
        throw new RuntimeException("Not implemented");
    }
    
    @Override
    public boolean hasFeature(final String feature, final String version) {
        return DomImpl._domImplementation_hasFeature(this, feature, version);
    }
    
    @Override
    public Object getFeature(final String feature, final String version) {
        throw new RuntimeException("DOM Level 3 Not implemented");
    }
    
    private static DomImpl.Dom checkNode(final Node n) {
        if (n == null) {
            throw new IllegalArgumentException("Node is null");
        }
        if (!(n instanceof DomImpl.Dom)) {
            throw new IllegalArgumentException("Node is not an XmlBeans node");
        }
        return (DomImpl.Dom)n;
    }
    
    public static XmlCursor nodeToCursor(final Node n) {
        return DomImpl._getXmlCursor(checkNode(n));
    }
    
    public static XmlObject nodeToXmlObject(final Node n) {
        return DomImpl._getXmlObject(checkNode(n));
    }
    
    public static XMLStreamReader nodeToXmlStream(final Node n) {
        return DomImpl._getXmlStreamReader(checkNode(n));
    }
    
    public static Node streamToNode(final XMLStreamReader xs) {
        return Jsr173.nodeFromStream(xs);
    }
    
    @Override
    public void setSaajData(final Node n, final Object o) {
        assert n instanceof DomImpl.Dom;
        DomImpl.saajCallback_setSaajData((DomImpl.Dom)n, o);
    }
    
    @Override
    public Object getSaajData(final Node n) {
        assert n instanceof DomImpl.Dom;
        return DomImpl.saajCallback_getSaajData((DomImpl.Dom)n);
    }
    
    @Override
    public Element createSoapElement(final QName name, final QName parentName) {
        assert this._ownerDoc != null;
        return DomImpl.saajCallback_createSoapElement(this._ownerDoc, name, parentName);
    }
    
    @Override
    public Element importSoapElement(final Document doc, final Element elem, final boolean deep, final QName parentName) {
        assert doc instanceof DomImpl.Dom;
        return DomImpl.saajCallback_importSoapElement((DomImpl.Dom)doc, elem, deep, parentName);
    }
    
    static {
        _xsiNil = new QName("http://www.w3.org/2001/XMLSchema-instance", "nil", "xsi");
        _xsiType = new QName("http://www.w3.org/2001/XMLSchema-instance", "type", "xsi");
        _xsiLoc = new QName("http://www.w3.org/2001/XMLSchema-instance", "schemaLocation", "xsi");
        _xsiNoLoc = new QName("http://www.w3.org/2001/XMLSchema-instance", "noNamespaceSchemaLocation", "xsi");
        _openuriFragment = new QName("http://www.openuri.org/fragment", "fragment", "frag");
        _xmlFragment = new QName("xml-fragment");
        Locale.tl_scrubBuffer = new ThreadLocal() {
            @Override
            protected Object initialValue() {
                return new SoftReference(new ScrubBuffer());
            }
        };
    }
    
    private class XmlSaxHandlerImpl extends SaxHandler implements XmlSaxHandler
    {
        private SchemaType _type;
        private XmlOptions _options;
        
        XmlSaxHandlerImpl(final Locale l, final SchemaType type, final XmlOptions options) {
            super(null);
            this._options = options;
            this._type = type;
            final XmlOptions saxHandlerOptions = new XmlOptions(options);
            saxHandlerOptions.put("LOAD_USE_LOCALE_CHAR_UTIL");
            this.initSaxHandler(l, saxHandlerOptions);
        }
        
        @Override
        public ContentHandler getContentHandler() {
            return (this._context == null) ? null : this;
        }
        
        @Override
        public LexicalHandler getLexicalHandler() {
            return (this._context == null) ? null : this;
        }
        
        @Override
        public void bookmarkLastEvent(final XmlCursor.XmlBookmark mark) {
            this._context.bookmarkLastNonAttr(mark);
        }
        
        @Override
        public void bookmarkLastAttr(final QName attrName, final XmlCursor.XmlBookmark mark) {
            this._context.bookmarkLastAttr(attrName, mark);
        }
        
        @Override
        public XmlObject getObject() throws XmlException {
            if (this._context == null) {
                return null;
            }
            this._locale.enter();
            try {
                final Cur c = this._context.finish();
                Locale.autoTypeDocument(c, this._type, this._options);
                final XmlObject x = (XmlObject)c.getUser();
                c.release();
                this._context = null;
                return x;
            }
            finally {
                this._locale.exit();
            }
        }
    }
    
    private static class DocProps extends XmlDocumentProperties
    {
        private HashMap _map;
        
        private DocProps() {
            this._map = new HashMap();
        }
        
        @Override
        public Object put(final Object key, final Object value) {
            return this._map.put(key, value);
        }
        
        @Override
        public Object get(final Object key) {
            return this._map.get(key);
        }
        
        @Override
        public Object remove(final Object key) {
            return this._map.remove(key);
        }
    }
    
    static final class ScrubBuffer
    {
        private static final int START_STATE = 0;
        private static final int SPACE_SEEN_STATE = 1;
        private static final int NOSPACE_STATE = 2;
        private int _state;
        private int _wsr;
        private char[] _srcBuf;
        private StringBuffer _sb;
        
        ScrubBuffer() {
            this._srcBuf = new char[1024];
            this._sb = new StringBuffer();
        }
        
        void init(final int wsr) {
            this._sb.delete(0, this._sb.length());
            this._wsr = wsr;
            this._state = 0;
        }
        
        void scrub(final Object src, int off, final int cch) {
            if (cch == 0) {
                return;
            }
            if (this._wsr == 1) {
                CharUtil.getString(this._sb, src, off, cch);
                return;
            }
            char[] chars;
            if (src instanceof char[]) {
                chars = (char[])src;
            }
            else {
                if (cch <= this._srcBuf.length) {
                    chars = this._srcBuf;
                }
                else if (cch <= 16384) {
                    final char[] srcBuf = new char[16384];
                    this._srcBuf = srcBuf;
                    chars = srcBuf;
                }
                else {
                    chars = new char[cch];
                }
                CharUtil.getChars(chars, 0, src, off, cch);
                off = 0;
            }
            int start = 0;
            for (int i = 0; i < cch; ++i) {
                final char ch = chars[off + i];
                if (ch == ' ' || ch == '\n' || ch == '\r' || ch == '\t') {
                    this._sb.append(chars, off + start, i - start);
                    start = i + 1;
                    if (this._wsr == 2) {
                        this._sb.append(' ');
                    }
                    else if (this._state == 2) {
                        this._state = 1;
                    }
                }
                else {
                    if (this._state == 1) {
                        this._sb.append(' ');
                    }
                    this._state = 2;
                }
            }
            this._sb.append(chars, off + start, cch - start);
        }
        
        String getResultAsString() {
            return this._sb.toString();
        }
    }
    
    class nthCache
    {
        private long _version;
        private Xobj _parent;
        private QName _name;
        private QNameSet _set;
        private Xobj _child;
        private int _n;
        
        private boolean namesSame(final QName pattern, final QName name) {
            return pattern == null || pattern.equals(name);
        }
        
        private boolean setsSame(final QNameSet patternSet, final QNameSet set) {
            return patternSet != null && patternSet == set;
        }
        
        private boolean nameHit(final QName namePattern, final QNameSet setPattern, final QName name) {
            return (setPattern == null) ? this.namesSame(namePattern, name) : setPattern.contains(name);
        }
        
        private boolean cacheSame(final QName namePattern, final QNameSet setPattern) {
            return (setPattern == null) ? this.namesSame(namePattern, this._name) : this.setsSame(setPattern, this._set);
        }
        
        int distance(final Xobj parent, final QName name, final QNameSet set, final int n) {
            assert n >= 0;
            if (this._version != Locale.this.version()) {
                return 2147483646;
            }
            if (parent != this._parent || !this.cacheSame(name, set)) {
                return Integer.MAX_VALUE;
            }
            return (n > this._n) ? (n - this._n) : (this._n - n);
        }
        
        Xobj fetch(final Xobj parent, final QName name, final QNameSet set, final int n) {
            assert n >= 0;
            if (this._version != Locale.this.version() || this._parent != parent || !this.cacheSame(name, set) || n == 0) {
                this._version = Locale.this.version();
                this._parent = parent;
                this._name = name;
                this._child = null;
                this._n = -1;
                for (Xobj x = parent._firstChild; x != null; x = x._nextSibling) {
                    if (x.isElem() && this.nameHit(name, set, x._name)) {
                        this._child = x;
                        this._n = 0;
                        break;
                    }
                }
            }
            if (this._n < 0) {
                return null;
            }
            if (n > this._n) {
            Label_0162:
                while (n > this._n) {
                    for (Xobj x = this._child._nextSibling; x != null; x = x._nextSibling) {
                        if (x.isElem() && this.nameHit(name, set, x._name)) {
                            this._child = x;
                            ++this._n;
                            continue Label_0162;
                        }
                    }
                    return null;
                }
            }
            else if (n < this._n) {
            Label_0250:
                while (n < this._n) {
                    for (Xobj x = this._child._prevSibling; x != null; x = x._prevSibling) {
                        if (x.isElem() && this.nameHit(name, set, x._name)) {
                            this._child = x;
                            --this._n;
                            continue Label_0250;
                        }
                    }
                    return null;
                }
            }
            return this._child;
        }
    }
    
    class domNthCache
    {
        public static final int BLITZ_BOUNDARY = 40;
        private long _version;
        private DomImpl.Dom _parent;
        private DomImpl.Dom _child;
        private int _n;
        private int _len;
        
        int distance(final DomImpl.Dom parent, final int n) {
            assert n >= 0;
            if (this._version != Locale.this.version()) {
                return 2147483646;
            }
            if (parent != this._parent) {
                return Integer.MAX_VALUE;
            }
            return (n > this._n) ? (n - this._n) : (this._n - n);
        }
        
        int length(final DomImpl.Dom parent) {
            if (this._version != Locale.this.version() || this._parent != parent) {
                this._parent = parent;
                this._version = Locale.this.version();
                this._child = null;
                this._n = -1;
                this._len = -1;
            }
            if (this._len == -1) {
                DomImpl.Dom x = null;
                if (this._child != null && this._n != -1) {
                    x = this._child;
                    this._len = this._n;
                }
                else {
                    x = DomImpl.firstChild(this._parent);
                    this._len = 0;
                    this._child = x;
                    this._n = 0;
                }
                while (x != null) {
                    ++this._len;
                    x = DomImpl.nextSibling(x);
                }
            }
            return this._len;
        }
        
        DomImpl.Dom fetch(final DomImpl.Dom parent, final int n) {
            assert n >= 0;
            if (this._version != Locale.this.version() || this._parent != parent) {
                this._parent = parent;
                this._version = Locale.this.version();
                this._child = null;
                this._n = -1;
                this._len = -1;
                for (DomImpl.Dom x = DomImpl.firstChild(this._parent); x != null; x = DomImpl.nextSibling(x)) {
                    ++this._n;
                    if (this._child == null && n == this._n) {
                        this._child = x;
                        break;
                    }
                }
                return this._child;
            }
            if (this._n < 0) {
                return null;
            }
            if (n > this._n) {
                while (n > this._n) {
                    final DomImpl.Dom x = DomImpl.nextSibling(this._child);
                    if (x == null) {
                        return null;
                    }
                    this._child = x;
                    ++this._n;
                }
            }
            else if (n < this._n) {
                while (n < this._n) {
                    final DomImpl.Dom x = DomImpl.prevSibling(this._child);
                    if (x == null) {
                        return null;
                    }
                    this._child = x;
                    --this._n;
                }
            }
            return this._child;
        }
    }
    
    static final class Ref extends PhantomReference
    {
        Cur _cur;
        
        Ref(final Cur c, final Object obj) {
            super(obj, c._locale.refQueue());
            this._cur = c;
        }
    }
    
    abstract static class LoadContext
    {
        private Hashtable _idAttrs;
        
        protected abstract void startDTD(final String p0, final String p1, final String p2);
        
        protected abstract void endDTD();
        
        protected abstract void startElement(final QName p0);
        
        protected abstract void endElement();
        
        protected abstract void attr(final QName p0, final String p1);
        
        protected abstract void attr(final String p0, final String p1, final String p2, final String p3);
        
        protected abstract void xmlns(final String p0, final String p1);
        
        protected abstract void comment(final char[] p0, final int p1, final int p2);
        
        protected abstract void comment(final String p0);
        
        protected abstract void procInst(final String p0, final String p1);
        
        protected abstract void text(final char[] p0, final int p1, final int p2);
        
        protected abstract void text(final String p0);
        
        protected abstract Cur finish();
        
        protected abstract void abort();
        
        protected abstract void bookmark(final XmlCursor.XmlBookmark p0);
        
        protected abstract void bookmarkLastNonAttr(final XmlCursor.XmlBookmark p0);
        
        protected abstract void bookmarkLastAttr(final QName p0, final XmlCursor.XmlBookmark p1);
        
        protected abstract void lineNumber(final int p0, final int p1, final int p2);
        
        protected void addIdAttr(final String eName, final String aName) {
            if (this._idAttrs == null) {
                this._idAttrs = new Hashtable();
            }
            this._idAttrs.put(aName, eName);
        }
        
        protected boolean isAttrOfTypeId(final QName aqn, final QName eqn) {
            if (this._idAttrs == null) {
                return false;
            }
            String pre = aqn.getPrefix();
            String lName = aqn.getLocalPart();
            String urnName = "".equals(pre) ? lName : (pre + ":" + lName);
            final String eName = this._idAttrs.get(urnName);
            if (eName == null) {
                return false;
            }
            pre = eqn.getPrefix();
            lName = eqn.getLocalPart();
            lName = eqn.getLocalPart();
            urnName = ("".equals(pre) ? lName : (pre + ":" + lName));
            return eName.equals(urnName);
        }
    }
    
    private static class DefaultEntityResolver implements EntityResolver
    {
        @Override
        public InputSource resolveEntity(final String publicId, final String systemId) {
            return new InputSource(new StringReader(""));
        }
    }
    
    private static class XmlReaderSaxLoader extends SaxLoader
    {
        XmlReaderSaxLoader(final XMLReader xr) {
            super(xr, null);
        }
    }
    
    private abstract static class SaxHandler implements ContentHandler, LexicalHandler, DeclHandler, DTDHandler
    {
        protected Locale _locale;
        protected LoadContext _context;
        private boolean _wantLineNumbers;
        private boolean _wantLineNumbersAtEndElt;
        private boolean _wantCdataBookmarks;
        private Locator _startLocator;
        private boolean _insideCDATA;
        private int _entityBytesLimit;
        private int _entityBytes;
        private int _insideEntity;
        
        SaxHandler(final Locator startLocator) {
            this._insideCDATA = false;
            this._entityBytesLimit = 10240;
            this._entityBytes = 0;
            this._insideEntity = 0;
            this._startLocator = startLocator;
        }
        
        SaxHandler() {
            this(null);
        }
        
        void initSaxHandler(final Locale l, final XmlOptions options) {
            this._locale = l;
            final XmlOptions safeOptions = XmlOptions.maskNull(options);
            this._context = new Cur.CurLoadContext(this._locale, safeOptions);
            this._wantLineNumbers = safeOptions.hasOption("LOAD_LINE_NUMBERS");
            this._wantLineNumbersAtEndElt = safeOptions.hasOption("LOAD_LINE_NUMBERS_END_ELEMENT");
            this._wantCdataBookmarks = safeOptions.hasOption("LOAD_SAVE_CDATA_BOOKMARKS");
            if (safeOptions.hasOption("LOAD_ENTITY_BYTES_LIMIT")) {
                this._entityBytesLimit = (int)safeOptions.get("LOAD_ENTITY_BYTES_LIMIT");
            }
        }
        
        @Override
        public void startDocument() throws SAXException {
        }
        
        @Override
        public void endDocument() throws SAXException {
        }
        
        @Override
        public void startElement(final String uri, String local, final String qName, final Attributes atts) throws SAXException {
            if (local.length() == 0) {
                local = qName;
            }
            if (qName.indexOf(58) >= 0 && uri.length() == 0) {
                final XmlError err = XmlError.forMessage("Use of undefined namespace prefix: " + qName.substring(0, qName.indexOf(58)));
                throw new XmlRuntimeException(err.toString(), null, err);
            }
            this._context.startElement(this._locale.makeQualifiedQName(uri, qName));
            if (this._wantLineNumbers && this._startLocator != null) {
                this._context.bookmark(new XmlLineNumber(this._startLocator.getLineNumber(), this._startLocator.getColumnNumber() - 1, -1));
            }
            for (int i = 0, len = atts.getLength(); i < len; ++i) {
                final String aqn = atts.getQName(i);
                if (aqn.equals("xmlns")) {
                    this._context.xmlns("", atts.getValue(i));
                }
                else if (aqn.startsWith("xmlns:")) {
                    final String prefix = aqn.substring(6);
                    if (prefix.length() == 0) {
                        final XmlError err2 = XmlError.forMessage("Prefix not specified", 0);
                        throw new XmlRuntimeException(err2.toString(), null, err2);
                    }
                    final String attrUri = atts.getValue(i);
                    if (attrUri.length() == 0) {
                        final XmlError err3 = XmlError.forMessage("Prefix can't be mapped to no namespace: " + prefix, 0);
                        throw new XmlRuntimeException(err3.toString(), null, err3);
                    }
                    this._context.xmlns(prefix, attrUri);
                }
                else {
                    final int colon = aqn.indexOf(58);
                    if (colon < 0) {
                        this._context.attr(aqn, atts.getURI(i), null, atts.getValue(i));
                    }
                    else {
                        this._context.attr(aqn.substring(colon + 1), atts.getURI(i), aqn.substring(0, colon), atts.getValue(i));
                    }
                }
            }
        }
        
        @Override
        public void endElement(final String namespaceURI, final String localName, final String qName) throws SAXException {
            this._context.endElement();
            if (this._wantLineNumbersAtEndElt && this._startLocator != null) {
                this._context.bookmark(new XmlLineNumber(this._startLocator.getLineNumber(), this._startLocator.getColumnNumber() - 1, -1));
            }
        }
        
        @Override
        public void characters(final char[] ch, final int start, final int length) throws SAXException {
            this._context.text(ch, start, length);
            if (this._wantCdataBookmarks && this._insideCDATA && this._startLocator != null) {
                this._context.bookmarkLastNonAttr(CDataBookmark.CDATA_BOOKMARK);
            }
            if (this._insideEntity != 0 && (this._entityBytes += length) > this._entityBytesLimit) {
                final XmlError err = XmlError.forMessage("exceeded-entity-bytes", new Integer[] { this._entityBytesLimit });
                throw new SAXException(err.getMessage());
            }
        }
        
        @Override
        public void ignorableWhitespace(final char[] ch, final int start, final int length) throws SAXException {
        }
        
        @Override
        public void comment(final char[] ch, final int start, final int length) throws SAXException {
            this._context.comment(ch, start, length);
        }
        
        @Override
        public void processingInstruction(final String target, final String data) throws SAXException {
            this._context.procInst(target, data);
        }
        
        @Override
        public void startDTD(final String name, final String publicId, final String systemId) throws SAXException {
            this._context.startDTD(name, publicId, systemId);
        }
        
        @Override
        public void endDTD() throws SAXException {
            this._context.endDTD();
        }
        
        @Override
        public void startPrefixMapping(final String prefix, final String uri) throws SAXException {
            if (Locale.beginsWithXml(prefix) && (!"xml".equals(prefix) || !"http://www.w3.org/XML/1998/namespace".equals(uri))) {
                final XmlError err = XmlError.forMessage("Prefix can't begin with XML: " + prefix, 0);
                throw new XmlRuntimeException(err.toString(), null, err);
            }
        }
        
        @Override
        public void endPrefixMapping(final String prefix) throws SAXException {
        }
        
        @Override
        public void skippedEntity(final String name) throws SAXException {
        }
        
        @Override
        public void startCDATA() throws SAXException {
            this._insideCDATA = true;
        }
        
        @Override
        public void endCDATA() throws SAXException {
            this._insideCDATA = false;
        }
        
        @Override
        public void startEntity(final String name) throws SAXException {
            ++this._insideEntity;
        }
        
        @Override
        public void endEntity(final String name) throws SAXException {
            --this._insideEntity;
            assert this._insideEntity >= 0;
            if (this._insideEntity == 0) {
                this._entityBytes = 0;
            }
        }
        
        @Override
        public void setDocumentLocator(final Locator locator) {
            if (this._startLocator == null) {
                this._startLocator = locator;
            }
        }
        
        @Override
        public void attributeDecl(final String eName, final String aName, final String type, final String valueDefault, final String value) {
            if (type.equals("ID")) {
                this._context.addIdAttr(eName, aName);
            }
        }
        
        @Override
        public void elementDecl(final String name, final String model) {
        }
        
        @Override
        public void externalEntityDecl(final String name, final String publicId, final String systemId) {
        }
        
        @Override
        public void internalEntityDecl(final String name, final String value) {
        }
        
        @Override
        public void notationDecl(final String name, final String publicId, final String systemId) {
        }
        
        @Override
        public void unparsedEntityDecl(final String name, final String publicId, final String systemId, final String notationName) {
        }
    }
    
    private abstract static class SaxLoader extends SaxHandler implements ErrorHandler
    {
        private XMLReader _xr;
        
        SaxLoader(final XMLReader xr, final Locator startLocator) {
            super(startLocator);
            this._xr = xr;
            try {
                this._xr.setFeature("http://xml.org/sax/features/namespace-prefixes", true);
                this._xr.setFeature("http://xml.org/sax/features/namespaces", true);
                this._xr.setFeature("http://xml.org/sax/features/validation", false);
                this._xr.setProperty("http://xml.org/sax/properties/lexical-handler", this);
                this._xr.setContentHandler(this);
                this._xr.setProperty("http://xml.org/sax/properties/declaration-handler", this);
                this._xr.setDTDHandler(this);
                this._xr.setErrorHandler(this);
            }
            catch (final Throwable e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }
        
        void setEntityResolver(final EntityResolver er) {
            this._xr.setEntityResolver(er);
        }
        
        void postLoad(final Cur c) {
            this._locale = null;
            this._context = null;
        }
        
        public Cur load(final Locale l, final InputSource is, final XmlOptions options) throws XmlException, IOException {
            is.setSystemId("file://");
            this.initSaxHandler(l, options);
            try {
                this._xr.parse(is);
                final Cur c = this._context.finish();
                Locale.associateSourceName(c, options);
                this.postLoad(c);
                return c;
            }
            catch (final XmlRuntimeException e) {
                this._context.abort();
                throw new XmlException(e);
            }
            catch (final SAXParseException e2) {
                this._context.abort();
                final XmlError err = XmlError.forLocation(e2.getMessage(), (String)XmlOptions.safeGet(options, "DOCUMENT_SOURCE_NAME"), e2.getLineNumber(), e2.getColumnNumber(), -1);
                throw new XmlException(err.toString(), e2, err);
            }
            catch (final SAXException e3) {
                this._context.abort();
                final XmlError err = XmlError.forMessage(e3.getMessage());
                throw new XmlException(err.toString(), e3, err);
            }
            catch (final RuntimeException e4) {
                this._context.abort();
                throw e4;
            }
        }
        
        @Override
        public void fatalError(final SAXParseException e) throws SAXException {
            throw e;
        }
        
        @Override
        public void error(final SAXParseException e) throws SAXException {
            throw e;
        }
        
        @Override
        public void warning(final SAXParseException e) throws SAXException {
            throw e;
        }
    }
    
    private static final class DefaultQNameFactory implements QNameFactory
    {
        private QNameCache _cache;
        
        private DefaultQNameFactory() {
            this._cache = XmlBeans.getQNameCache();
        }
        
        @Override
        public QName getQName(final String uri, final String local) {
            return this._cache.getName(uri, local, "");
        }
        
        @Override
        public QName getQName(final String uri, final String local, final String prefix) {
            return this._cache.getName(uri, local, prefix);
        }
        
        @Override
        public QName getQName(final char[] uriSrc, final int uriPos, final int uriCch, final char[] localSrc, final int localPos, final int localCch) {
            return this._cache.getName(new String(uriSrc, uriPos, uriCch), new String(localSrc, localPos, localCch), "");
        }
        
        @Override
        public QName getQName(final char[] uriSrc, final int uriPos, final int uriCch, final char[] localSrc, final int localPos, final int localCch, final char[] prefixSrc, final int prefixPos, final int prefixCch) {
            return this._cache.getName(new String(uriSrc, uriPos, uriCch), new String(localSrc, localPos, localCch), new String(prefixSrc, prefixPos, prefixCch));
        }
    }
    
    private static final class LocalDocumentQNameFactory implements QNameFactory
    {
        private QNameCache _cache;
        
        private LocalDocumentQNameFactory() {
            this._cache = new QNameCache(32);
        }
        
        @Override
        public QName getQName(final String uri, final String local) {
            return this._cache.getName(uri, local, "");
        }
        
        @Override
        public QName getQName(final String uri, final String local, final String prefix) {
            return this._cache.getName(uri, local, prefix);
        }
        
        @Override
        public QName getQName(final char[] uriSrc, final int uriPos, final int uriCch, final char[] localSrc, final int localPos, final int localCch) {
            return this._cache.getName(new String(uriSrc, uriPos, uriCch), new String(localSrc, localPos, localCch), "");
        }
        
        @Override
        public QName getQName(final char[] uriSrc, final int uriPos, final int uriCch, final char[] localSrc, final int localPos, final int localCch, final char[] prefixSrc, final int prefixPos, final int prefixCch) {
            return this._cache.getName(new String(uriSrc, uriPos, uriCch), new String(localSrc, localPos, localCch), new String(prefixSrc, prefixPos, prefixCch));
        }
    }
    
    interface ChangeListener
    {
        void notifyChange();
        
        void setNextChangeListener(final ChangeListener p0);
        
        ChangeListener getNextChangeListener();
    }
}
