package org.apache.xmlbeans.impl.store;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.ContentHandler;
import org.apache.xmlbeans.xml.stream.XMLStreamException;
import org.apache.xmlbeans.impl.common.GenericXmlInputStream;
import org.apache.xmlbeans.xml.stream.ProcessingInstruction;
import org.apache.xmlbeans.xml.stream.Comment;
import org.apache.xmlbeans.xml.stream.CharacterData;
import org.apache.xmlbeans.xml.stream.EndDocument;
import org.apache.xmlbeans.xml.stream.EndElement;
import org.apache.xmlbeans.xml.stream.EndPrefixMapping;
import org.apache.xmlbeans.xml.stream.ChangePrefixMapping;
import org.apache.xmlbeans.xml.stream.StartPrefixMapping;
import org.apache.xmlbeans.xml.stream.Attribute;
import org.apache.xmlbeans.xml.stream.AttributeIterator;
import org.apache.xmlbeans.xml.stream.StartElement;
import org.apache.xmlbeans.xml.stream.StartDocument;
import org.apache.xmlbeans.xml.stream.Location;
import org.apache.xmlbeans.impl.common.XmlEventBase;
import org.apache.xmlbeans.impl.common.XmlNameImpl;
import org.apache.xmlbeans.xml.stream.XMLEvent;
import org.apache.xmlbeans.xml.stream.XMLName;
import java.io.UnsupportedEncodingException;
import java.io.OutputStream;
import org.apache.xmlbeans.impl.common.EncodingMap;
import java.io.OutputStreamWriter;
import java.io.InputStream;
import java.io.Reader;
import java.io.IOException;
import java.io.Writer;
import java.util.LinkedHashMap;
import org.apache.xmlbeans.SystemProperties;
import org.apache.xmlbeans.impl.common.QNameHelper;
import org.apache.xmlbeans.XmlDocumentProperties;
import java.util.ConcurrentModificationException;
import javax.xml.namespace.QName;
import java.util.Iterator;
import org.apache.xmlbeans.XmlOptions;
import java.util.HashMap;
import java.util.ArrayList;
import org.apache.xmlbeans.XmlOptionCharEscapeMap;
import java.util.Map;
import java.util.List;

abstract class Saver
{
    static final int ROOT = 1;
    static final int ELEM = 2;
    static final int ATTR = 3;
    static final int COMMENT = 4;
    static final int PROCINST = 5;
    static final int TEXT = 0;
    private final Locale _locale;
    private final long _version;
    private SaveCur _cur;
    private List _ancestorNamespaces;
    private Map _suggestedPrefixes;
    protected XmlOptionCharEscapeMap _replaceChar;
    private boolean _useDefaultNamespace;
    private Map _preComputedNamespaces;
    private boolean _saveNamespacesFirst;
    private ArrayList _attrNames;
    private ArrayList _attrValues;
    private ArrayList _namespaceStack;
    private int _currentMapping;
    private HashMap _uriMap;
    private HashMap _prefixMap;
    private String _initialDefaultUri;
    static final String _newLine;
    
    protected abstract boolean emitElement(final SaveCur p0, final ArrayList p1, final ArrayList p2);
    
    protected abstract void emitFinish(final SaveCur p0);
    
    protected abstract void emitText(final SaveCur p0);
    
    protected abstract void emitComment(final SaveCur p0);
    
    protected abstract void emitProcinst(final SaveCur p0);
    
    protected abstract void emitDocType(final String p0, final String p1, final String p2);
    
    protected abstract void emitStartDoc(final SaveCur p0);
    
    protected abstract void emitEndDoc(final SaveCur p0);
    
    protected void syntheticNamespace(final String prefix, final String uri, final boolean considerDefault) {
    }
    
    Saver(final Cur c, XmlOptions options) {
        assert c._locale.entered();
        options = XmlOptions.maskNull(options);
        this._cur = createSaveCur(c, options);
        this._locale = c._locale;
        this._version = this._locale.version();
        this._namespaceStack = new ArrayList();
        this._uriMap = new HashMap();
        this._prefixMap = new HashMap();
        this._attrNames = new ArrayList();
        this._attrValues = new ArrayList();
        this.addMapping("xml", "http://www.w3.org/XML/1998/namespace");
        if (options.hasOption("SAVE_IMPLICIT_NAMESPACES")) {
            final Map m = (Map)options.get("SAVE_IMPLICIT_NAMESPACES");
            for (final String prefix : m.keySet()) {
                this.addMapping(prefix, m.get(prefix));
            }
        }
        if (options.hasOption("SAVE_SUBSTITUTE_CHARACTERS")) {
            this._replaceChar = (XmlOptionCharEscapeMap)options.get("SAVE_SUBSTITUTE_CHARACTERS");
        }
        if (this.getNamespaceForPrefix("") == null) {
            this.addMapping("", this._initialDefaultUri = new String(""));
        }
        if (options.hasOption("SAVE_AGGRESSIVE_NAMESPACES") && !(this instanceof SynthNamespaceSaver)) {
            final SynthNamespaceSaver saver = new SynthNamespaceSaver(c, options);
            while (saver.process()) {}
            if (!saver._synthNamespaces.isEmpty()) {
                this._preComputedNamespaces = saver._synthNamespaces;
            }
        }
        this._useDefaultNamespace = options.hasOption("SAVE_USE_DEFAULT_NAMESPACE");
        this._saveNamespacesFirst = options.hasOption("SAVE_NAMESPACES_FIRST");
        if (options.hasOption("SAVE_SUGGESTED_PREFIXES")) {
            this._suggestedPrefixes = (Map)options.get("SAVE_SUGGESTED_PREFIXES");
        }
        this._ancestorNamespaces = this._cur.getAncestorNamespaces();
    }
    
    private static SaveCur createSaveCur(final Cur c, final XmlOptions options) {
        QName fragName;
        final QName synthName = fragName = (QName)options.get("SAVE_SYNTHETIC_DOCUMENT_ELEMENT");
        if (fragName == null) {
            fragName = (options.hasOption("SAVE_USE_OPEN_FRAGMENT") ? Locale._openuriFragment : Locale._xmlFragment);
        }
        final boolean saveInner = options.hasOption("SAVE_INNER") && !options.hasOption("SAVE_OUTER");
        final Cur start = c.tempCur();
        final Cur end = c.tempCur();
        SaveCur cur = null;
        final int k = c.kind();
        switch (k) {
            case 1: {
                positionToInner(c, start, end);
                if (Locale.isFragment(start, end)) {
                    cur = new FragSaveCur(start, end, fragName);
                    break;
                }
                if (synthName != null) {
                    cur = new FragSaveCur(start, end, synthName);
                    break;
                }
                cur = new DocSaveCur(c);
                break;
            }
            case 2: {
                if (saveInner) {
                    positionToInner(c, start, end);
                    cur = new FragSaveCur(start, end, Locale.isFragment(start, end) ? fragName : synthName);
                    break;
                }
                if (synthName != null) {
                    positionToInner(c, start, end);
                    cur = new FragSaveCur(start, end, synthName);
                    break;
                }
                start.moveToCur(c);
                end.moveToCur(c);
                end.skip();
                cur = new FragSaveCur(start, end, null);
                break;
            }
        }
        if (cur == null) {
            assert k == 0;
            if (k < 0) {
                start.moveToCur(c);
                end.moveToCur(c);
            }
            else if (k == 0) {
                start.moveToCur(c);
                end.moveToCur(c);
                end.next();
            }
            else if (saveInner) {
                start.moveToCur(c);
                start.next();
                end.moveToCur(c);
                end.toEnd();
            }
            else if (k == 3) {
                start.moveToCur(c);
                end.moveToCur(c);
            }
            else {
                assert k == 5;
                start.moveToCur(c);
                end.moveToCur(c);
                end.skip();
            }
            cur = new FragSaveCur(start, end, fragName);
        }
        final String filterPI = (String)options.get("SAVE_FILTER_PROCINST");
        if (filterPI != null) {
            cur = new FilterPiSaveCur(cur, filterPI);
        }
        if (options.hasOption("SAVE_PRETTY_PRINT")) {
            cur = new PrettySaveCur(cur, options);
        }
        start.release();
        end.release();
        return cur;
    }
    
    private static void positionToInner(final Cur c, final Cur start, final Cur end) {
        assert c.isContainer();
        start.moveToCur(c);
        if (!start.toFirstAttr()) {
            start.next();
        }
        end.moveToCur(c);
        end.toEnd();
    }
    
    static boolean isBadChar(final char ch) {
        return !Character.isHighSurrogate(ch) && !Character.isLowSurrogate(ch) && (ch < ' ' || ch > '\ud7ff') && (ch < '\ue000' || ch > '\ufffd') && (ch < 65536 || ch > 1114111) && ch != '\t' && ch != '\n' && ch != '\r';
    }
    
    protected boolean saveNamespacesFirst() {
        return this._saveNamespacesFirst;
    }
    
    protected void enterLocale() {
        this._locale.enter();
    }
    
    protected void exitLocale() {
        this._locale.exit();
    }
    
    protected final boolean process() {
        assert this._locale.entered();
        if (this._cur == null) {
            return false;
        }
        if (this._version != this._locale.version()) {
            throw new ConcurrentModificationException("Document changed during save");
        }
        switch (this._cur.kind()) {
            case 1: {
                this.processRoot();
                break;
            }
            case 2: {
                this.processElement();
                break;
            }
            case -2: {
                this.processFinish();
                break;
            }
            case 0: {
                this.emitText(this._cur);
                break;
            }
            case 4: {
                this.emitComment(this._cur);
                this._cur.toEnd();
                break;
            }
            case 5: {
                this.emitProcinst(this._cur);
                this._cur.toEnd();
                break;
            }
            case -1: {
                this.emitEndDoc(this._cur);
                this._cur.release();
                this._cur = null;
                return true;
            }
            default: {
                throw new RuntimeException("Unexpected kind");
            }
        }
        this._cur.next();
        return true;
    }
    
    private final void processFinish() {
        this.emitFinish(this._cur);
        this.popMappings();
    }
    
    private final void processRoot() {
        assert this._cur.isRoot();
        final XmlDocumentProperties props = this._cur.getDocProps();
        String systemId = null;
        String docTypeName = null;
        if (props != null) {
            systemId = props.getDoctypeSystemId();
            docTypeName = props.getDoctypeName();
        }
        if (systemId != null || docTypeName != null) {
            if (docTypeName == null) {
                this._cur.push();
                while (!this._cur.isElem() && this._cur.next()) {}
                if (this._cur.isElem()) {
                    docTypeName = this._cur.getName().getLocalPart();
                }
                this._cur.pop();
            }
            final String publicId = props.getDoctypePublicId();
            if (docTypeName != null) {
                QName rootElemName = this._cur.getName();
                if (rootElemName == null) {
                    this._cur.push();
                    while (!this._cur.isFinish()) {
                        if (this._cur.isElem()) {
                            rootElemName = this._cur.getName();
                            break;
                        }
                        this._cur.next();
                    }
                    this._cur.pop();
                }
                if (rootElemName != null && docTypeName.equals(rootElemName.getLocalPart())) {
                    this.emitDocType(docTypeName, publicId, systemId);
                    return;
                }
            }
        }
        this.emitStartDoc(this._cur);
    }
    
    private final void processElement() {
        assert this._cur.isElem() && this._cur.getName() != null;
        final QName name = this._cur.getName();
        final boolean ensureDefaultEmpty = name.getNamespaceURI().length() == 0;
        this.pushMappings(this._cur, ensureDefaultEmpty);
        this.ensureMapping(name.getNamespaceURI(), name.getPrefix(), !ensureDefaultEmpty, false);
        this._attrNames.clear();
        this._attrValues.clear();
        this._cur.push();
    Label_0243:
        for (boolean A = this._cur.toFirstAttr(); A; A = this._cur.toNextAttr()) {
            if (this._cur.isNormalAttr()) {
                final QName attrName = this._cur.getName();
                this._attrNames.add(attrName);
                for (int i = this._attrNames.size() - 2; i >= 0; --i) {
                    if (this._attrNames.get(i).equals(attrName)) {
                        this._attrNames.remove(this._attrNames.size() - 1);
                        continue Label_0243;
                    }
                }
                this._attrValues.add(this._cur.getAttrValue());
                this.ensureMapping(attrName.getNamespaceURI(), attrName.getPrefix(), false, true);
            }
        }
        this._cur.pop();
        if (this._preComputedNamespaces != null) {
            for (final String uri : this._preComputedNamespaces.keySet()) {
                final String prefix = this._preComputedNamespaces.get(uri);
                final boolean considerDefault = prefix.length() == 0 && !ensureDefaultEmpty;
                this.ensureMapping(uri, prefix, considerDefault, false);
            }
            this._preComputedNamespaces = null;
        }
        if (this.emitElement(this._cur, this._attrNames, this._attrValues)) {
            this.popMappings();
            this._cur.toEnd();
        }
    }
    
    boolean hasMappings() {
        final int i = this._namespaceStack.size();
        return i > 0 && this._namespaceStack.get(i - 1) != null;
    }
    
    void iterateMappings() {
        this._currentMapping = this._namespaceStack.size();
        while (this._currentMapping > 0 && this._namespaceStack.get(this._currentMapping - 1) != null) {
            this._currentMapping -= 8;
        }
    }
    
    boolean hasMapping() {
        return this._currentMapping < this._namespaceStack.size();
    }
    
    void nextMapping() {
        this._currentMapping += 8;
    }
    
    String mappingPrefix() {
        assert this.hasMapping();
        return this._namespaceStack.get(this._currentMapping + 6);
    }
    
    String mappingUri() {
        assert this.hasMapping();
        return this._namespaceStack.get(this._currentMapping + 7);
    }
    
    private final void pushMappings(final SaveCur c, final boolean ensureDefaultEmpty) {
        assert c.isContainer();
        this._namespaceStack.add(null);
        c.push();
        for (boolean A = c.toFirstAttr(); A; A = c.toNextAttr()) {
            if (c.isXmlns()) {
                this.addNewFrameMapping(c.getXmlnsPrefix(), c.getXmlnsUri(), ensureDefaultEmpty);
            }
        }
        c.pop();
        if (this._ancestorNamespaces != null) {
            for (int i = 0; i < this._ancestorNamespaces.size(); i += 2) {
                final String prefix = this._ancestorNamespaces.get(i);
                final String uri = this._ancestorNamespaces.get(i + 1);
                this.addNewFrameMapping(prefix, uri, ensureDefaultEmpty);
            }
            this._ancestorNamespaces = null;
        }
        if (ensureDefaultEmpty) {
            final String defaultUri = this._prefixMap.get("");
            assert defaultUri != null;
            if (defaultUri.length() > 0) {
                this.addMapping("", "");
            }
        }
    }
    
    private final void addNewFrameMapping(final String prefix, final String uri, final boolean ensureDefaultEmpty) {
        if ((prefix.length() == 0 || uri.length() > 0) && (!ensureDefaultEmpty || prefix.length() > 0 || uri.length() == 0)) {
            this.iterateMappings();
            while (this.hasMapping()) {
                if (this.mappingPrefix().equals(prefix)) {
                    return;
                }
                this.nextMapping();
            }
            if (uri.equals(this.getNamespaceForPrefix(prefix))) {
                return;
            }
            this.addMapping(prefix, uri);
        }
    }
    
    private final void addMapping(final String prefix, final String uri) {
        assert uri != null;
        assert prefix != null;
        String renameUri = this._prefixMap.get(prefix);
        String renamePrefix = null;
        if (renameUri != null) {
            if (renameUri.equals(uri)) {
                renameUri = null;
            }
            else {
                int i = this._namespaceStack.size();
                while (i > 0) {
                    if (this._namespaceStack.get(i - 1) == null) {
                        --i;
                    }
                    else {
                        if (this._namespaceStack.get(i - 7).equals(renameUri)) {
                            renamePrefix = this._namespaceStack.get(i - 8);
                            if (renamePrefix == null) {
                                break;
                            }
                            if (!renamePrefix.equals(prefix)) {
                                break;
                            }
                        }
                        i -= 8;
                    }
                }
                assert i > 0;
            }
        }
        this._namespaceStack.add(this._uriMap.get(uri));
        this._namespaceStack.add(uri);
        if (renameUri != null) {
            this._namespaceStack.add(this._uriMap.get(renameUri));
            this._namespaceStack.add(renameUri);
        }
        else {
            this._namespaceStack.add(null);
            this._namespaceStack.add(null);
        }
        this._namespaceStack.add(prefix);
        this._namespaceStack.add(this._prefixMap.get(prefix));
        this._namespaceStack.add(prefix);
        this._namespaceStack.add(uri);
        this._uriMap.put(uri, prefix);
        this._prefixMap.put(prefix, uri);
        if (renameUri != null) {
            this._uriMap.put(renameUri, renamePrefix);
        }
    }
    
    private final void popMappings() {
        while (true) {
            final int i = this._namespaceStack.size();
            if (i == 0) {
                break;
            }
            if (this._namespaceStack.get(i - 1) == null) {
                this._namespaceStack.remove(i - 1);
                break;
            }
            Object oldUri = this._namespaceStack.get(i - 7);
            Object oldPrefix = this._namespaceStack.get(i - 8);
            if (oldPrefix == null) {
                this._uriMap.remove(oldUri);
            }
            else {
                this._uriMap.put(oldUri, oldPrefix);
            }
            oldPrefix = this._namespaceStack.get(i - 4);
            oldUri = this._namespaceStack.get(i - 3);
            if (oldUri == null) {
                this._prefixMap.remove(oldPrefix);
            }
            else {
                this._prefixMap.put(oldPrefix, oldUri);
            }
            final String uri = this._namespaceStack.get(i - 5);
            if (uri != null) {
                this._uriMap.put(uri, this._namespaceStack.get(i - 6));
            }
            this._namespaceStack.remove(i - 1);
            this._namespaceStack.remove(i - 2);
            this._namespaceStack.remove(i - 3);
            this._namespaceStack.remove(i - 4);
            this._namespaceStack.remove(i - 5);
            this._namespaceStack.remove(i - 6);
            this._namespaceStack.remove(i - 7);
            this._namespaceStack.remove(i - 8);
        }
    }
    
    private final void dumpMappings() {
        int i = this._namespaceStack.size();
        while (i > 0) {
            if (this._namespaceStack.get(i - 1) == null) {
                System.out.println("----------------");
                --i;
            }
            else {
                System.out.print("Mapping: ");
                System.out.print(this._namespaceStack.get(i - 2));
                System.out.print(" -> ");
                System.out.print(this._namespaceStack.get(i - 1));
                System.out.println();
                System.out.print("Prefix Undo: ");
                System.out.print(this._namespaceStack.get(i - 4));
                System.out.print(" -> ");
                System.out.print(this._namespaceStack.get(i - 3));
                System.out.println();
                System.out.print("Uri Rename: ");
                System.out.print(this._namespaceStack.get(i - 5));
                System.out.print(" -> ");
                System.out.print(this._namespaceStack.get(i - 6));
                System.out.println();
                System.out.print("UriUndo: ");
                System.out.print(this._namespaceStack.get(i - 7));
                System.out.print(" -> ");
                System.out.print(this._namespaceStack.get(i - 8));
                System.out.println();
                System.out.println();
                i -= 8;
            }
        }
    }
    
    private final String ensureMapping(final String uri, String candidatePrefix, final boolean considerCreatingDefault, final boolean mustHavePrefix) {
        assert uri != null;
        if (uri.length() == 0) {
            return null;
        }
        final String prefix = this._uriMap.get(uri);
        if (prefix != null && (prefix.length() > 0 || !mustHavePrefix)) {
            return prefix;
        }
        if (candidatePrefix != null && candidatePrefix.length() == 0) {
            candidatePrefix = null;
        }
        if (candidatePrefix == null || !this.tryPrefix(candidatePrefix)) {
            if (this._suggestedPrefixes != null && this._suggestedPrefixes.containsKey(uri) && this.tryPrefix(this._suggestedPrefixes.get(uri))) {
                candidatePrefix = this._suggestedPrefixes.get(uri);
            }
            else if (considerCreatingDefault && this._useDefaultNamespace && this.tryPrefix("")) {
                candidatePrefix = "";
            }
            else {
                final String basePrefix = candidatePrefix = QNameHelper.suggestPrefix(uri);
                for (int i = 1; !this.tryPrefix(candidatePrefix); candidatePrefix = basePrefix + i, ++i) {}
            }
        }
        assert candidatePrefix != null;
        this.syntheticNamespace(candidatePrefix, uri, considerCreatingDefault);
        this.addMapping(candidatePrefix, uri);
        return candidatePrefix;
    }
    
    protected final String getUriMapping(final String uri) {
        assert this._uriMap.get(uri) != null;
        return this._uriMap.get(uri);
    }
    
    String getNonDefaultUriMapping(final String uri) {
        String prefix = this._uriMap.get(uri);
        if (prefix != null && prefix.length() > 0) {
            return prefix;
        }
        final Iterator keys = this._prefixMap.keySet().iterator();
        while (keys.hasNext()) {
            prefix = keys.next();
            if (prefix.length() > 0 && this._prefixMap.get(prefix).equals(uri)) {
                return prefix;
            }
        }
        assert false : "Could not find non-default mapping";
        return null;
    }
    
    private final boolean tryPrefix(final String prefix) {
        if (prefix == null || Locale.beginsWithXml(prefix)) {
            return false;
        }
        final String existingUri = this._prefixMap.get(prefix);
        return existingUri == null || (prefix.length() <= 0 && existingUri == this._initialDefaultUri);
    }
    
    public final String getNamespaceForPrefix(final String prefix) {
        assert !(!this._prefixMap.get(prefix).equals("http://www.w3.org/XML/1998/namespace"));
        return this._prefixMap.get(prefix);
    }
    
    protected Map getPrefixMap() {
        return this._prefixMap;
    }
    
    static {
        _newLine = ((SystemProperties.getProperty("line.separator") == null) ? "\n" : SystemProperties.getProperty("line.separator"));
    }
    
    static final class SynthNamespaceSaver extends Saver
    {
        LinkedHashMap _synthNamespaces;
        
        SynthNamespaceSaver(final Cur c, final XmlOptions options) {
            super(c, options);
            this._synthNamespaces = new LinkedHashMap();
        }
        
        @Override
        protected void syntheticNamespace(final String prefix, final String uri, final boolean considerCreatingDefault) {
            this._synthNamespaces.put(uri, considerCreatingDefault ? "" : prefix);
        }
        
        @Override
        protected boolean emitElement(final SaveCur c, final ArrayList attrNames, final ArrayList attrValues) {
            return false;
        }
        
        @Override
        protected void emitFinish(final SaveCur c) {
        }
        
        @Override
        protected void emitText(final SaveCur c) {
        }
        
        @Override
        protected void emitComment(final SaveCur c) {
        }
        
        @Override
        protected void emitProcinst(final SaveCur c) {
        }
        
        @Override
        protected void emitDocType(final String docTypeName, final String publicId, final String systemId) {
        }
        
        @Override
        protected void emitStartDoc(final SaveCur c) {
        }
        
        @Override
        protected void emitEndDoc(final SaveCur c) {
        }
    }
    
    static final class TextSaver extends Saver
    {
        private static final int _initialBufSize = 4096;
        private int _cdataLengthThreshold;
        private int _cdataEntityCountThreshold;
        private boolean _useCDataBookmarks;
        private boolean _isPrettyPrint;
        private int _lastEmitIn;
        private int _lastEmitCch;
        private int _free;
        private int _in;
        private int _out;
        private char[] _buf;
        
        TextSaver(final Cur c, final XmlOptions options, final String encoding) {
            super(c, options);
            this._cdataLengthThreshold = 32;
            this._cdataEntityCountThreshold = 5;
            this._useCDataBookmarks = false;
            this._isPrettyPrint = false;
            final boolean noSaveDecl = options != null && options.hasOption("SAVE_NO_XML_DECL");
            if (options != null && options.hasOption("SAVE_CDATA_LENGTH_THRESHOLD")) {
                this._cdataLengthThreshold = (int)options.get("SAVE_CDATA_LENGTH_THRESHOLD");
            }
            if (options != null && options.hasOption("SAVE_CDATA_ENTITY_COUNT_THRESHOLD")) {
                this._cdataEntityCountThreshold = (int)options.get("SAVE_CDATA_ENTITY_COUNT_THRESHOLD");
            }
            if (options != null && options.hasOption("LOAD_SAVE_CDATA_BOOKMARKS")) {
                this._useCDataBookmarks = true;
            }
            if (options != null && options.hasOption("SAVE_PRETTY_PRINT")) {
                this._isPrettyPrint = true;
            }
            final int n = 0;
            this._out = n;
            this._in = n;
            this._free = 0;
            assert this._out == this._in && this._free == 0 : "_buf.length:" + this._buf.length + " _in:" + this._in + " _out:" + this._out + " _free:" + this._free;
            if (encoding != null && !noSaveDecl) {
                final XmlDocumentProperties props = Locale.getDocProps(c, false);
                String version = (props == null) ? null : props.getVersion();
                if (version == null) {
                    version = "1.0";
                }
                this.emit("<?xml version=\"");
                this.emit(version);
                this.emit("\" encoding=\"" + encoding + "\"?>" + TextSaver._newLine);
            }
        }
        
        @Override
        protected boolean emitElement(final SaveCur c, final ArrayList attrNames, final ArrayList attrValues) {
            assert c.isElem();
            this.emit('<');
            this.emitName(c.getName(), false);
            if (this.saveNamespacesFirst()) {
                this.emitNamespacesHelper();
            }
            for (int i = 0; i < attrNames.size(); ++i) {
                this.emitAttrHelper(attrNames.get(i), attrValues.get(i));
            }
            if (!this.saveNamespacesFirst()) {
                this.emitNamespacesHelper();
            }
            if (!c.hasChildren() && !c.hasText()) {
                this.emit('/', '>');
                return true;
            }
            this.emit('>');
            return false;
        }
        
        @Override
        protected void emitFinish(final SaveCur c) {
            this.emit('<', '/');
            this.emitName(c.getName(), false);
            this.emit('>');
        }
        
        protected void emitXmlns(final String prefix, final String uri) {
            assert prefix != null;
            assert uri != null;
            this.emit("xmlns");
            if (prefix.length() > 0) {
                this.emit(':');
                this.emit(prefix);
            }
            this.emit('=', '\"');
            this.emit(uri);
            this.entitizeAttrValue(false);
            this.emit('\"');
        }
        
        private void emitNamespacesHelper() {
            this.iterateMappings();
            while (this.hasMapping()) {
                this.emit(' ');
                this.emitXmlns(this.mappingPrefix(), this.mappingUri());
                this.nextMapping();
            }
        }
        
        private void emitAttrHelper(final QName attrName, final String attrValue) {
            this.emit(' ');
            this.emitName(attrName, true);
            this.emit('=', '\"');
            this.emit(attrValue);
            this.entitizeAttrValue(true);
            this.emit('\"');
        }
        
        @Override
        protected void emitText(final SaveCur c) {
            assert c.isText();
            final boolean forceCData = this._useCDataBookmarks && c.isTextCData();
            this.emit(c);
            this.entitizeContent(forceCData);
        }
        
        @Override
        protected void emitComment(final SaveCur c) {
            assert c.isComment();
            this.emit("<!--");
            c.push();
            c.next();
            this.emit(c);
            c.pop();
            this.entitizeComment();
            this.emit("-->");
        }
        
        @Override
        protected void emitProcinst(final SaveCur c) {
            assert c.isProcinst();
            this.emit("<?");
            this.emit(c.getName().getLocalPart());
            c.push();
            c.next();
            if (c.isText()) {
                this.emit(" ");
                this.emit(c);
                this.entitizeProcinst();
            }
            c.pop();
            this.emit("?>");
        }
        
        private void emitLiteral(final String literal) {
            if (literal.indexOf("\"") < 0) {
                this.emit('\"');
                this.emit(literal);
                this.emit('\"');
            }
            else {
                this.emit('\'');
                this.emit(literal);
                this.emit('\'');
            }
        }
        
        @Override
        protected void emitDocType(final String docTypeName, final String publicId, final String systemId) {
            assert docTypeName != null;
            this.emit("<!DOCTYPE ");
            this.emit(docTypeName);
            if (publicId == null && systemId != null) {
                this.emit(" SYSTEM ");
                this.emitLiteral(systemId);
            }
            else if (publicId != null) {
                this.emit(" PUBLIC ");
                this.emitLiteral(publicId);
                this.emit(" ");
                this.emitLiteral(systemId);
            }
            this.emit(">");
            this.emit(TextSaver._newLine);
        }
        
        @Override
        protected void emitStartDoc(final SaveCur c) {
        }
        
        @Override
        protected void emitEndDoc(final SaveCur c) {
        }
        
        private void emitName(final QName name, final boolean needsPrefix) {
            assert name != null;
            final String uri = name.getNamespaceURI();
            assert uri != null;
            if (uri.length() != 0) {
                String prefix = name.getPrefix();
                final String mappedUri = this.getNamespaceForPrefix(prefix);
                if (mappedUri == null || !mappedUri.equals(uri)) {
                    prefix = this.getUriMapping(uri);
                }
                if (needsPrefix && prefix.length() == 0) {
                    prefix = this.getNonDefaultUriMapping(uri);
                }
                if (prefix.length() > 0) {
                    this.emit(prefix);
                    this.emit(':');
                }
            }
            assert name.getLocalPart().length() > 0;
            this.emit(name.getLocalPart());
        }
        
        private void emit(final char ch) {
            assert this._out == this._in && this._free == 0 : "_buf.length:" + this._buf.length + " _in:" + this._in + " _out:" + this._out + " _free:" + this._free;
            this.preEmit(1);
            this._buf[this._in] = ch;
            this._in = (this._in + 1) % this._buf.length;
            assert this._out == this._in && this._free == 0 : "_buf.length:" + this._buf.length + " _in:" + this._in + " _out:" + this._out + " _free:" + this._free;
        }
        
        private void emit(final char ch1, final char ch2) {
            if (this.preEmit(2)) {
                return;
            }
            this._buf[this._in] = ch1;
            this._in = (this._in + 1) % this._buf.length;
            this._buf[this._in] = ch2;
            this._in = (this._in + 1) % this._buf.length;
            assert this._out == this._in && this._free == 0 : "_buf.length:" + this._buf.length + " _in:" + this._in + " _out:" + this._out + " _free:" + this._free;
        }
        
        private void emit(final String s) {
            assert this._out == this._in && this._free == 0 : "_buf.length:" + this._buf.length + " _in:" + this._in + " _out:" + this._out + " _free:" + this._free;
            final int cch = (s == null) ? 0 : s.length();
            if (this.preEmit(cch)) {
                return;
            }
            final int chunk;
            if (this._in <= this._out || cch < (chunk = this._buf.length - this._in)) {
                s.getChars(0, cch, this._buf, this._in);
                this._in += cch;
            }
            else {
                s.getChars(0, chunk, this._buf, this._in);
                s.getChars(chunk, cch, this._buf, 0);
                this._in = (this._in + cch) % this._buf.length;
            }
            assert this._out == this._in && this._free == 0 : "_buf.length:" + this._buf.length + " _in:" + this._in + " _out:" + this._out + " _free:" + this._free;
        }
        
        private void emit(final SaveCur c) {
            if (c.isText()) {
                final Object src = c.getChars();
                final int cch = c._cchSrc;
                if (this.preEmit(cch)) {
                    return;
                }
                final int chunk;
                if (this._in <= this._out || cch < (chunk = this._buf.length - this._in)) {
                    CharUtil.getChars(this._buf, this._in, src, c._offSrc, cch);
                    this._in += cch;
                }
                else {
                    CharUtil.getChars(this._buf, this._in, src, c._offSrc, chunk);
                    CharUtil.getChars(this._buf, 0, src, c._offSrc + chunk, cch - chunk);
                    this._in = (this._in + cch) % this._buf.length;
                }
            }
            else {
                this.preEmit(0);
            }
        }
        
        private boolean preEmit(final int cch) {
            assert cch >= 0;
            assert this._out == this._in && this._free == 0 : "_buf.length:" + this._buf.length + " _in:" + this._in + " _out:" + this._out + " _free:" + this._free;
            if ((this._lastEmitCch = cch) == 0) {
                return true;
            }
            if (this._free <= cch) {
                this.resize(cch, -1);
            }
            assert cch <= this._free;
            final int used = this.getAvailable();
            if (used == 0) {
                assert this._in == this._out;
                assert this._free == this._buf.length;
                final int n = 0;
                this._out = n;
                this._in = n;
            }
            this._lastEmitIn = this._in;
            this._free -= cch;
            assert this._free >= 0;
            assert this._free == ((this._in >= this._out) ? (this._buf.length - (this._in - this._out)) : (this._out - this._in)) - cch : "_buf.length:" + this._buf.length + " _in:" + this._in + " _out:" + this._out + " _free:" + this._free;
            assert this._out == this._in && this._free == 0 : "_buf.length:" + this._buf.length + " _in:" + this._in + " _out:" + this._out + " _free:" + this._free;
            return false;
        }
        
        private void entitizeContent(final boolean forceCData) {
            assert this._free >= 0;
            if (this._lastEmitCch == 0) {
                return;
            }
            int i = this._lastEmitIn;
            final int n = this._buf.length;
            boolean hasCharToBeReplaced = false;
            int count = 0;
            char prevChar = '\0';
            char prevPrevChar = '\0';
            for (int cch = this._lastEmitCch; cch > 0; --cch) {
                final char ch = this._buf[i];
                if (ch == '<' || ch == '&') {
                    ++count;
                }
                else if (prevPrevChar == ']' && prevChar == ']' && ch == '>') {
                    hasCharToBeReplaced = true;
                }
                else if (Saver.isBadChar(ch) || this.isEscapedChar(ch) || (!this._isPrettyPrint && ch == '\r')) {
                    hasCharToBeReplaced = true;
                }
                if (++i == n) {
                    i = 0;
                }
                prevPrevChar = prevChar;
                prevChar = ch;
            }
            if (!forceCData && count == 0 && !hasCharToBeReplaced && count < this._cdataEntityCountThreshold) {
                return;
            }
            i = this._lastEmitIn;
            if (forceCData || (this._lastEmitCch > this._cdataLengthThreshold && count > this._cdataEntityCountThreshold)) {
                boolean lastWasBracket = this._buf[i] == ']';
                i = this.replace(i, "<![CDATA[" + this._buf[i]);
                boolean secondToLastWasBracket = lastWasBracket;
                lastWasBracket = (this._buf[i] == ']');
                if (++i == this._buf.length) {
                    i = 0;
                }
                for (int cch2 = this._lastEmitCch - 2; cch2 > 0; --cch2) {
                    final char ch2 = this._buf[i];
                    if (ch2 == '>' && secondToLastWasBracket && lastWasBracket) {
                        i = this.replace(i, "]]>><![CDATA[");
                    }
                    else if (Saver.isBadChar(ch2)) {
                        i = this.replace(i, "?");
                    }
                    else {
                        ++i;
                    }
                    secondToLastWasBracket = lastWasBracket;
                    lastWasBracket = (ch2 == ']');
                    if (i == this._buf.length) {
                        i = 0;
                    }
                }
                this.emit("]]>");
            }
            else {
                char ch3 = '\0';
                char ch_1 = '\0';
                for (int cch3 = this._lastEmitCch; cch3 > 0; --cch3) {
                    final char ch_2 = ch_1;
                    ch_1 = ch3;
                    ch3 = this._buf[i];
                    if (ch3 == '<') {
                        i = this.replace(i, "&lt;");
                    }
                    else if (ch3 == '&') {
                        i = this.replace(i, "&amp;");
                    }
                    else if (ch3 == '>' && ch_1 == ']' && ch_2 == ']') {
                        i = this.replace(i, "&gt;");
                    }
                    else if (Saver.isBadChar(ch3)) {
                        i = this.replace(i, "?");
                    }
                    else if (!this._isPrettyPrint && ch3 == '\r') {
                        i = this.replace(i, "&#13;");
                    }
                    else if (this.isEscapedChar(ch3)) {
                        i = this.replace(i, this._replaceChar.getEscapedString(ch3));
                    }
                    else {
                        ++i;
                    }
                    if (i == this._buf.length) {
                        i = 0;
                    }
                }
            }
        }
        
        private void entitizeAttrValue(final boolean replaceEscapedChar) {
            if (this._lastEmitCch == 0) {
                return;
            }
            int i = this._lastEmitIn;
            for (int cch = this._lastEmitCch; cch > 0; --cch) {
                final char ch = this._buf[i];
                if (ch == '<') {
                    i = this.replace(i, "&lt;");
                }
                else if (ch == '&') {
                    i = this.replace(i, "&amp;");
                }
                else if (ch == '\"') {
                    i = this.replace(i, "&quot;");
                }
                else if (this.isEscapedChar(ch)) {
                    if (replaceEscapedChar) {
                        i = this.replace(i, this._replaceChar.getEscapedString(ch));
                    }
                }
                else {
                    ++i;
                }
                if (i == this._buf.length) {
                    i = 0;
                }
            }
        }
        
        private void entitizeComment() {
            if (this._lastEmitCch == 0) {
                return;
            }
            int i = this._lastEmitIn;
            boolean lastWasDash = false;
            for (int cch = this._lastEmitCch; cch > 0; --cch) {
                final char ch = this._buf[i];
                if (Saver.isBadChar(ch)) {
                    i = this.replace(i, "?");
                }
                else if (ch == '-') {
                    if (lastWasDash) {
                        i = this.replace(i, " ");
                        lastWasDash = false;
                    }
                    else {
                        lastWasDash = true;
                        ++i;
                    }
                }
                else {
                    lastWasDash = false;
                    ++i;
                }
                if (i == this._buf.length) {
                    i = 0;
                }
            }
            final int offset = (this._lastEmitIn + this._lastEmitCch - 1) % this._buf.length;
            if (this._buf[offset] == '-') {
                i = this.replace(offset, " ");
            }
        }
        
        private void entitizeProcinst() {
            if (this._lastEmitCch == 0) {
                return;
            }
            int i = this._lastEmitIn;
            boolean lastWasQuestion = false;
            for (int cch = this._lastEmitCch; cch > 0; --cch) {
                final char ch = this._buf[i];
                if (Saver.isBadChar(ch)) {
                    i = this.replace(i, "?");
                }
                if (ch == '>') {
                    if (lastWasQuestion) {
                        i = this.replace(i, " ");
                    }
                    else {
                        ++i;
                    }
                    lastWasQuestion = false;
                }
                else {
                    lastWasQuestion = (ch == '?');
                    ++i;
                }
                if (i == this._buf.length) {
                    i = 0;
                }
            }
        }
        
        private boolean isEscapedChar(final char ch) {
            return null != this._replaceChar && this._replaceChar.containsChar(ch);
        }
        
        private int replace(int i, final String replacement) {
            assert replacement.length() > 0;
            final int dCch = replacement.length() - 1;
            if (dCch == 0) {
                this._buf[i] = replacement.charAt(0);
                return i + 1;
            }
            assert this._free >= 0;
            if (dCch > this._free) {
                i = this.resize(dCch, i);
            }
            assert this._free >= 0;
            assert this._free >= dCch;
            assert this.getAvailable() > 0;
            int charsToCopy = dCch + 1;
            if (this._out > this._in && i >= this._out) {
                System.arraycopy(this._buf, this._out, this._buf, this._out - dCch, i - this._out);
                this._out -= dCch;
                i -= dCch;
            }
            else {
                assert i < this._in;
                final int availableEndChunk = this._buf.length - this._in;
                if (dCch <= availableEndChunk) {
                    System.arraycopy(this._buf, i, this._buf, i + dCch, this._in - i);
                    this._in = (this._in + dCch) % this._buf.length;
                }
                else if (dCch <= availableEndChunk + this._in - i - 1) {
                    final int numToCopyToStart = dCch - availableEndChunk;
                    System.arraycopy(this._buf, this._in - numToCopyToStart, this._buf, 0, numToCopyToStart);
                    System.arraycopy(this._buf, i + 1, this._buf, i + 1 + dCch, this._in - i - 1 - numToCopyToStart);
                    this._in = numToCopyToStart;
                }
                else {
                    final int numToCopyToStart = this._in - i - 1;
                    charsToCopy = availableEndChunk + this._in - i;
                    System.arraycopy(this._buf, this._in - numToCopyToStart, this._buf, dCch - charsToCopy + 1, numToCopyToStart);
                    replacement.getChars(charsToCopy, dCch + 1, this._buf, 0);
                    this._in = numToCopyToStart + dCch - charsToCopy + 1;
                }
            }
            replacement.getChars(0, charsToCopy, this._buf, i);
            this._free -= dCch;
            assert this._free >= 0;
            assert this._out == this._in && this._free == 0 : "_buf.length:" + this._buf.length + " _in:" + this._in + " _out:" + this._out + " _free:" + this._free;
            return (i + dCch + 1) % this._buf.length;
        }
        
        private int ensure(int cch) {
            if (cch <= 0) {
                cch = 1;
            }
            int available;
            for (available = this.getAvailable(); available < cch && this.process(); available = this.getAvailable()) {}
            assert available == this.getAvailable();
            return available;
        }
        
        int getAvailable() {
            return (this._buf == null) ? 0 : (this._buf.length - this._free);
        }
        
        private int resize(final int cch, int i) {
            assert this._free >= 0;
            assert cch > 0;
            assert cch >= this._free;
            assert this._out == this._in && this._free == 0 : "_buf.length:" + this._buf.length + " _in:" + this._in + " _out:" + this._out + " _free:" + this._free;
            int newLen;
            int used;
            for (newLen = ((this._buf == null) ? 4096 : (this._buf.length * 2)), used = this.getAvailable(); newLen - used < cch; newLen *= 2) {}
            final char[] newBuf = new char[newLen];
            if (used > 0) {
                if (this._in > this._out) {
                    assert i >= this._out && i < this._in;
                    System.arraycopy(this._buf, this._out, newBuf, 0, used);
                    i -= this._out;
                }
                else {
                    assert i < this._in;
                    System.arraycopy(this._buf, this._out, newBuf, 0, used - this._in);
                    System.arraycopy(this._buf, 0, newBuf, used - this._in, this._in);
                    i = ((i >= this._out) ? (i - this._out) : (i + this._out));
                }
                this._out = 0;
                this._in = used;
                this._free += newBuf.length - this._buf.length;
            }
            else {
                this._free = newBuf.length;
                assert this._in == 0 && this._out == 0;
                assert i == -1;
            }
            this._buf = newBuf;
            assert this._free >= 0;
            assert this._out == this._in && this._free == 0 : "_buf.length:" + this._buf.length + " _in:" + this._in + " _out:" + this._out + " _free:" + this._free;
            return i;
        }
        
        public int read() {
            if (this.ensure(1) == 0) {
                return -1;
            }
            assert this.getAvailable() > 0;
            final int ch = this._buf[this._out];
            this._out = (this._out + 1) % this._buf.length;
            ++this._free;
            assert this._out == this._in && this._free == 0 : "_buf.length:" + this._buf.length + " _in:" + this._in + " _out:" + this._out + " _free:" + this._free;
            return ch;
        }
        
        public int read(final char[] cbuf, final int off, int len) {
            final int n;
            if ((n = this.ensure(len)) == 0) {
                return -1;
            }
            if (cbuf == null || len <= 0) {
                return 0;
            }
            if (n < len) {
                len = n;
            }
            if (this._out < this._in) {
                System.arraycopy(this._buf, this._out, cbuf, off, len);
            }
            else {
                final int chunk = this._buf.length - this._out;
                if (chunk >= len) {
                    System.arraycopy(this._buf, this._out, cbuf, off, len);
                }
                else {
                    System.arraycopy(this._buf, this._out, cbuf, off, chunk);
                    System.arraycopy(this._buf, 0, cbuf, off + chunk, len - chunk);
                }
            }
            this._out = (this._out + len) % this._buf.length;
            this._free += len;
            assert this._out == this._in && this._free == 0 : "_buf.length:" + this._buf.length + " _in:" + this._in + " _out:" + this._out + " _free:" + this._free;
            assert this._free >= 0;
            return len;
        }
        
        public int write(final Writer writer, final int cchMin) {
            while (this.getAvailable() < cchMin && this.process()) {}
            final int charsAvailable = this.getAvailable();
            if (charsAvailable > 0) {
                assert this._out == 0;
                assert this._in >= this._out : "_in:" + this._in + " < _out:" + this._out;
                assert this._free == this._buf.length - this._in;
                try {
                    writer.write(this._buf, 0, charsAvailable);
                    writer.flush();
                }
                catch (final IOException e) {
                    throw new RuntimeException(e);
                }
                this._free += charsAvailable;
                assert this._free >= 0;
                this._in = 0;
            }
            assert this._out == this._in && this._free == 0 : "_buf.length:" + this._buf.length + " _in:" + this._in + " _out:" + this._out + " _free:" + this._free;
            return charsAvailable;
        }
        
        public String saveToString() {
            while (this.process()) {}
            assert this._out == 0;
            final int available = this.getAvailable();
            return (available == 0) ? "" : new String(this._buf, this._out, available);
        }
    }
    
    static final class OptimizedForSpeedSaver extends Saver
    {
        Writer _w;
        private char[] _buf;
        
        OptimizedForSpeedSaver(final Cur cur, final Writer writer) {
            super(cur, XmlOptions.maskNull(null));
            this._buf = new char[1024];
            this._w = writer;
        }
        
        static void save(final Cur cur, final Writer writer) throws IOException {
            try {
                final Saver saver = new OptimizedForSpeedSaver(cur, writer);
                while (saver.process()) {}
            }
            catch (final SaverIOException e) {
                throw (IOException)e.getCause();
            }
        }
        
        private void emit(final String s) {
            try {
                this._w.write(s);
            }
            catch (final IOException e) {
                throw new SaverIOException(e);
            }
        }
        
        private void emit(final char c) {
            try {
                this._buf[0] = c;
                this._w.write(this._buf, 0, 1);
            }
            catch (final IOException e) {
                throw new SaverIOException(e);
            }
        }
        
        private void emit(final char c1, final char c2) {
            try {
                this._buf[0] = c1;
                this._buf[1] = c2;
                this._w.write(this._buf, 0, 2);
            }
            catch (final IOException e) {
                throw new SaverIOException(e);
            }
        }
        
        private void emit(final char[] buf, final int start, final int len) {
            try {
                this._w.write(buf, start, len);
            }
            catch (final IOException e) {
                throw new SaverIOException(e);
            }
        }
        
        @Override
        protected boolean emitElement(final SaveCur c, final ArrayList attrNames, final ArrayList attrValues) {
            assert c.isElem();
            this.emit('<');
            this.emitName(c.getName(), false);
            for (int i = 0; i < attrNames.size(); ++i) {
                this.emitAttrHelper(attrNames.get(i), attrValues.get(i));
            }
            if (!this.saveNamespacesFirst()) {
                this.emitNamespacesHelper();
            }
            if (!c.hasChildren() && !c.hasText()) {
                this.emit('/', '>');
                return true;
            }
            this.emit('>');
            return false;
        }
        
        @Override
        protected void emitFinish(final SaveCur c) {
            this.emit('<', '/');
            this.emitName(c.getName(), false);
            this.emit('>');
        }
        
        protected void emitXmlns(final String prefix, final String uri) {
            assert prefix != null;
            assert uri != null;
            this.emit("xmlns");
            if (prefix.length() > 0) {
                this.emit(':');
                this.emit(prefix);
            }
            this.emit('=', '\"');
            this.emitAttrValue(uri);
            this.emit('\"');
        }
        
        private void emitNamespacesHelper() {
            this.iterateMappings();
            while (this.hasMapping()) {
                this.emit(' ');
                this.emitXmlns(this.mappingPrefix(), this.mappingUri());
                this.nextMapping();
            }
        }
        
        private void emitAttrHelper(final QName attrName, final String attrValue) {
            this.emit(' ');
            this.emitName(attrName, true);
            this.emit('=', '\"');
            this.emitAttrValue(attrValue);
            this.emit('\"');
        }
        
        @Override
        protected void emitComment(final SaveCur c) {
            assert c.isComment();
            this.emit("<!--");
            c.push();
            c.next();
            this.emitCommentText(c);
            c.pop();
            this.emit("-->");
        }
        
        @Override
        protected void emitProcinst(final SaveCur c) {
            assert c.isProcinst();
            this.emit("<?");
            this.emit(c.getName().getLocalPart());
            c.push();
            c.next();
            if (c.isText()) {
                this.emit(' ');
                this.emitPiText(c);
            }
            c.pop();
            this.emit("?>");
        }
        
        @Override
        protected void emitDocType(final String docTypeName, final String publicId, final String systemId) {
            assert docTypeName != null;
            this.emit("<!DOCTYPE ");
            this.emit(docTypeName);
            if (publicId == null && systemId != null) {
                this.emit(" SYSTEM ");
                this.emitLiteral(systemId);
            }
            else if (publicId != null) {
                this.emit(" PUBLIC ");
                this.emitLiteral(publicId);
                this.emit(' ');
                this.emitLiteral(systemId);
            }
            this.emit('>');
            this.emit(OptimizedForSpeedSaver._newLine);
        }
        
        @Override
        protected void emitStartDoc(final SaveCur c) {
        }
        
        @Override
        protected void emitEndDoc(final SaveCur c) {
        }
        
        private void emitName(final QName name, final boolean needsPrefix) {
            assert name != null;
            final String uri = name.getNamespaceURI();
            assert uri != null;
            if (uri.length() != 0) {
                String prefix = name.getPrefix();
                final String mappedUri = this.getNamespaceForPrefix(prefix);
                if (mappedUri == null || !mappedUri.equals(uri)) {
                    prefix = this.getUriMapping(uri);
                }
                if (needsPrefix && prefix.length() == 0) {
                    prefix = this.getNonDefaultUriMapping(uri);
                }
                if (prefix.length() > 0) {
                    this.emit(prefix);
                    this.emit(':');
                }
            }
            assert name.getLocalPart().length() > 0;
            this.emit(name.getLocalPart());
        }
        
        private void emitAttrValue(final CharSequence attVal) {
            for (int len = attVal.length(), i = 0; i < len; ++i) {
                final char ch = attVal.charAt(i);
                if (ch == '<') {
                    this.emit("&lt;");
                }
                else if (ch == '&') {
                    this.emit("&amp;");
                }
                else if (ch == '\"') {
                    this.emit("&quot;");
                }
                else {
                    this.emit(ch);
                }
            }
        }
        
        private void emitLiteral(final String literal) {
            if (literal.indexOf("\"") < 0) {
                this.emit('\"');
                this.emit(literal);
                this.emit('\"');
            }
            else {
                this.emit('\'');
                this.emit(literal);
                this.emit('\'');
            }
        }
        
        @Override
        protected void emitText(final SaveCur c) {
            assert c.isText();
            final Object src = c.getChars();
            final int cch = c._cchSrc;
            final int off = c._offSrc;
            for (int index = 0, indexLimit = 0; index < cch; index = indexLimit) {
                indexLimit = ((index + 512 > cch) ? cch : (index + 512));
                CharUtil.getChars(this._buf, 0, src, off + index, indexLimit - index);
                this.entitizeAndWriteText(indexLimit - index);
            }
        }
        
        protected void emitPiText(final SaveCur c) {
            assert c.isText();
            final Object src = c.getChars();
            final int cch = c._cchSrc;
            final int off = c._offSrc;
            for (int index = 0, indexLimit = 0; index < cch; index = indexLimit) {
                indexLimit = ((index + 512 > cch) ? cch : 512);
                CharUtil.getChars(this._buf, 0, src, off + index, indexLimit);
                this.entitizeAndWritePIText(indexLimit - index);
            }
        }
        
        protected void emitCommentText(final SaveCur c) {
            assert c.isText();
            final Object src = c.getChars();
            final int cch = c._cchSrc;
            final int off = c._offSrc;
            for (int index = 0, indexLimit = 0; index < cch; index = indexLimit) {
                indexLimit = ((index + 512 > cch) ? cch : 512);
                CharUtil.getChars(this._buf, 0, src, off + index, indexLimit);
                this.entitizeAndWriteCommentText(indexLimit - index);
            }
        }
        
        private void entitizeAndWriteText(final int bufLimit) {
            int index = 0;
            for (int i = 0; i < bufLimit; ++i) {
                final char c = this._buf[i];
                switch (c) {
                    case '<': {
                        this.emit(this._buf, index, i - index);
                        this.emit("&lt;");
                        index = i + 1;
                        break;
                    }
                    case '&': {
                        this.emit(this._buf, index, i - index);
                        this.emit("&amp;");
                        index = i + 1;
                        break;
                    }
                }
            }
            this.emit(this._buf, index, bufLimit - index);
        }
        
        private void entitizeAndWriteCommentText(final int bufLimit) {
            boolean lastWasDash = false;
            for (int i = 0; i < bufLimit; ++i) {
                final char ch = this._buf[i];
                if (Saver.isBadChar(ch)) {
                    this._buf[i] = '?';
                }
                else if (ch == '-') {
                    if (lastWasDash) {
                        this._buf[i] = ' ';
                        lastWasDash = false;
                    }
                    else {
                        lastWasDash = true;
                    }
                }
                else {
                    lastWasDash = false;
                }
                if (i == this._buf.length) {
                    i = 0;
                }
            }
            if (this._buf[bufLimit - 1] == '-') {
                this._buf[bufLimit - 1] = ' ';
            }
            this.emit(this._buf, 0, bufLimit);
        }
        
        private void entitizeAndWritePIText(final int bufLimit) {
            boolean lastWasQuestion = false;
            for (int i = 0; i < bufLimit; ++i) {
                char ch = this._buf[i];
                if (Saver.isBadChar(ch)) {
                    this._buf[i] = '?';
                    ch = '?';
                }
                if (ch == '>') {
                    if (lastWasQuestion) {
                        this._buf[i] = ' ';
                    }
                    lastWasQuestion = false;
                }
                else {
                    lastWasQuestion = (ch == '?');
                }
            }
            this.emit(this._buf, 0, bufLimit);
        }
        
        private static class SaverIOException extends RuntimeException
        {
            SaverIOException(final IOException e) {
                super(e);
            }
        }
    }
    
    static final class TextReader extends Reader
    {
        private Locale _locale;
        private TextSaver _textSaver;
        private boolean _closed;
        
        TextReader(final Cur c, final XmlOptions options) {
            this._textSaver = new TextSaver(c, options, null);
            this._locale = c._locale;
            this._closed = false;
        }
        
        @Override
        public void close() throws IOException {
            this._closed = true;
        }
        
        @Override
        public boolean ready() throws IOException {
            return !this._closed;
        }
        
        @Override
        public int read() throws IOException {
            this.checkClosed();
            if (this._locale.noSync()) {
                this._locale.enter();
                try {
                    return this._textSaver.read();
                }
                finally {
                    this._locale.exit();
                }
            }
            synchronized (this._locale) {
                this._locale.enter();
                try {
                    return this._textSaver.read();
                }
                finally {
                    this._locale.exit();
                }
            }
        }
        
        @Override
        public int read(final char[] cbuf) throws IOException {
            this.checkClosed();
            if (this._locale.noSync()) {
                this._locale.enter();
                try {
                    return this._textSaver.read(cbuf, 0, (cbuf == null) ? 0 : cbuf.length);
                }
                finally {
                    this._locale.exit();
                }
            }
            synchronized (this._locale) {
                this._locale.enter();
                try {
                    return this._textSaver.read(cbuf, 0, (cbuf == null) ? 0 : cbuf.length);
                }
                finally {
                    this._locale.exit();
                }
            }
        }
        
        @Override
        public int read(final char[] cbuf, final int off, final int len) throws IOException {
            this.checkClosed();
            if (this._locale.noSync()) {
                this._locale.enter();
                try {
                    return this._textSaver.read(cbuf, off, len);
                }
                finally {
                    this._locale.exit();
                }
            }
            synchronized (this._locale) {
                this._locale.enter();
                try {
                    return this._textSaver.read(cbuf, off, len);
                }
                finally {
                    this._locale.exit();
                }
            }
        }
        
        private void checkClosed() throws IOException {
            if (this._closed) {
                throw new IOException("Reader has been closed");
            }
        }
    }
    
    static final class InputStreamSaver extends InputStream
    {
        private Locale _locale;
        private boolean _closed;
        private OutputStreamImpl _outStreamImpl;
        private TextSaver _textSaver;
        private OutputStreamWriter _converter;
        
        InputStreamSaver(final Cur c, XmlOptions options) {
            this._locale = c._locale;
            this._closed = false;
            assert this._locale.entered();
            options = XmlOptions.maskNull(options);
            this._outStreamImpl = new OutputStreamImpl();
            String encoding = null;
            final XmlDocumentProperties props = Locale.getDocProps(c, false);
            if (props != null && props.getEncoding() != null) {
                encoding = EncodingMap.getIANA2JavaMapping(props.getEncoding());
            }
            if (options.hasOption("CHARACTER_ENCODING")) {
                encoding = (String)options.get("CHARACTER_ENCODING");
            }
            if (encoding != null) {
                final String ianaEncoding = EncodingMap.getJava2IANAMapping(encoding);
                if (ianaEncoding != null) {
                    encoding = ianaEncoding;
                }
            }
            if (encoding == null) {
                encoding = EncodingMap.getJava2IANAMapping("UTF8");
            }
            final String javaEncoding = EncodingMap.getIANA2JavaMapping(encoding);
            if (javaEncoding == null) {
                throw new IllegalStateException("Unknown encoding: " + encoding);
            }
            try {
                this._converter = new OutputStreamWriter(this._outStreamImpl, javaEncoding);
            }
            catch (final UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
            this._textSaver = new TextSaver(c, options, encoding);
        }
        
        @Override
        public void close() throws IOException {
            this._closed = true;
        }
        
        private void checkClosed() throws IOException {
            if (this._closed) {
                throw new IOException("Stream closed");
            }
        }
        
        @Override
        public int read() throws IOException {
            this.checkClosed();
            if (this._locale.noSync()) {
                this._locale.enter();
                try {
                    return this._outStreamImpl.read();
                }
                finally {
                    this._locale.exit();
                }
            }
            synchronized (this._locale) {
                this._locale.enter();
                try {
                    return this._outStreamImpl.read();
                }
                finally {
                    this._locale.exit();
                }
            }
        }
        
        @Override
        public int read(final byte[] bbuf, final int off, final int len) throws IOException {
            this.checkClosed();
            if (bbuf == null) {
                throw new NullPointerException("buf to read into is null");
            }
            if (off < 0 || off > bbuf.length) {
                throw new IndexOutOfBoundsException("Offset is not within buf");
            }
            if (this._locale.noSync()) {
                this._locale.enter();
                try {
                    return this._outStreamImpl.read(bbuf, off, len);
                }
                finally {
                    this._locale.exit();
                }
            }
            synchronized (this._locale) {
                this._locale.enter();
                try {
                    return this._outStreamImpl.read(bbuf, off, len);
                }
                finally {
                    this._locale.exit();
                }
            }
        }
        
        private int ensure(int cbyte) {
            if (cbyte <= 0) {
                cbyte = 1;
            }
            for (int bytesAvailable = this._outStreamImpl.getAvailable(); bytesAvailable < cbyte && this._textSaver.write(this._converter, 2048) >= 2048; bytesAvailable = this._outStreamImpl.getAvailable()) {}
            int bytesAvailable = this._outStreamImpl.getAvailable();
            return bytesAvailable;
        }
        
        @Override
        public int available() throws IOException {
            if (this._locale.noSync()) {
                this._locale.enter();
                try {
                    return this.ensure(1024);
                }
                finally {
                    this._locale.exit();
                }
            }
            synchronized (this._locale) {
                this._locale.enter();
                try {
                    return this.ensure(1024);
                }
                finally {
                    this._locale.exit();
                }
            }
        }
        
        private final class OutputStreamImpl extends OutputStream
        {
            private static final int _initialBufSize = 4096;
            private int _free;
            private int _in;
            private int _out;
            private byte[] _buf;
            
            int read() {
                if (InputStreamSaver.this.ensure(1) == 0) {
                    return -1;
                }
                assert this.getAvailable() > 0;
                final int bite = this._buf[this._out];
                this._out = (this._out + 1) % this._buf.length;
                ++this._free;
                return bite;
            }
            
            int read(final byte[] bbuf, final int off, int len) {
                final int n;
                if ((n = InputStreamSaver.this.ensure(len)) == 0) {
                    return -1;
                }
                if (bbuf == null || len <= 0) {
                    return 0;
                }
                if (n < len) {
                    len = n;
                }
                if (this._out < this._in) {
                    System.arraycopy(this._buf, this._out, bbuf, off, len);
                }
                else {
                    final int chunk = this._buf.length - this._out;
                    if (chunk >= len) {
                        System.arraycopy(this._buf, this._out, bbuf, off, len);
                    }
                    else {
                        System.arraycopy(this._buf, this._out, bbuf, off, chunk);
                        System.arraycopy(this._buf, 0, bbuf, off + chunk, len - chunk);
                    }
                }
                this._out = (this._out + len) % this._buf.length;
                this._free += len;
                return len;
            }
            
            int getAvailable() {
                return (this._buf == null) ? 0 : (this._buf.length - this._free);
            }
            
            @Override
            public void write(final int bite) {
                if (this._free == 0) {
                    this.resize(1);
                }
                assert this._free > 0;
                this._buf[this._in] = (byte)bite;
                this._in = (this._in + 1) % this._buf.length;
                --this._free;
            }
            
            @Override
            public void write(final byte[] buf, final int off, final int cbyte) {
                assert cbyte >= 0;
                if (cbyte == 0) {
                    return;
                }
                if (this._free < cbyte) {
                    this.resize(cbyte);
                }
                if (this._in == this._out) {
                    assert this.getAvailable() == 0;
                    assert this._free == this._buf.length - this.getAvailable();
                    final int n = 0;
                    this._out = n;
                    this._in = n;
                }
                final int chunk = this._buf.length - this._in;
                if (this._in <= this._out || cbyte < chunk) {
                    System.arraycopy(buf, off, this._buf, this._in, cbyte);
                    this._in += cbyte;
                }
                else {
                    System.arraycopy(buf, off, this._buf, this._in, chunk);
                    System.arraycopy(buf, off + chunk, this._buf, 0, cbyte - chunk);
                    this._in = (this._in + cbyte) % this._buf.length;
                }
                this._free -= cbyte;
            }
            
            void resize(final int cbyte) {
                assert cbyte > this._free : cbyte + " !> " + this._free;
                int newLen;
                int used;
                for (newLen = ((this._buf == null) ? 4096 : (this._buf.length * 2)), used = this.getAvailable(); newLen - used < cbyte; newLen *= 2) {}
                final byte[] newBuf = new byte[newLen];
                if (used > 0) {
                    if (this._in > this._out) {
                        System.arraycopy(this._buf, this._out, newBuf, 0, used);
                    }
                    else {
                        System.arraycopy(this._buf, this._out, newBuf, 0, used - this._in);
                        System.arraycopy(this._buf, 0, newBuf, used - this._in, this._in);
                    }
                    this._out = 0;
                    this._in = used;
                    this._free += newBuf.length - this._buf.length;
                }
                else {
                    this._free = newBuf.length;
                    assert this._in == this._out;
                }
                this._buf = newBuf;
            }
        }
    }
    
    static final class XmlInputStreamSaver extends Saver
    {
        private XmlEventImpl _in;
        private XmlEventImpl _out;
        
        XmlInputStreamSaver(final Cur c, final XmlOptions options) {
            super(c, options);
        }
        
        @Override
        protected boolean emitElement(final SaveCur c, final ArrayList attrNames, final ArrayList attrValues) {
            assert c.isElem();
            this.iterateMappings();
            while (this.hasMapping()) {
                this.enqueue(new StartPrefixMappingImpl(this.mappingPrefix(), this.mappingUri()));
                this.nextMapping();
            }
            StartElementImpl.AttributeImpl lastAttr = null;
            StartElementImpl.AttributeImpl attributes = null;
            StartElementImpl.AttributeImpl namespaces = null;
            for (int i = 0; i < attrNames.size(); ++i) {
                final XMLName attXMLName = computeName(attrNames.get(i), this, true);
                final StartElementImpl.AttributeImpl attr = new StartElementImpl.NormalAttributeImpl(attXMLName, attrValues.get(i));
                if (attributes == null) {
                    attributes = attr;
                }
                else {
                    lastAttr._next = attr;
                }
                lastAttr = attr;
            }
            lastAttr = null;
            this.iterateMappings();
            while (this.hasMapping()) {
                final String prefix = this.mappingPrefix();
                final String uri = this.mappingUri();
                final StartElementImpl.AttributeImpl attr = new StartElementImpl.XmlnsAttributeImpl(prefix, uri);
                if (namespaces == null) {
                    namespaces = attr;
                }
                else {
                    lastAttr._next = attr;
                }
                lastAttr = attr;
                this.nextMapping();
            }
            final QName name = c.getName();
            this.enqueue(new StartElementImpl(computeName(name, this, false), attributes, namespaces, this.getPrefixMap()));
            return false;
        }
        
        @Override
        protected void emitFinish(final SaveCur c) {
            if (c.isRoot()) {
                this.enqueue(new EndDocumentImpl());
            }
            else {
                final XMLName xmlName = computeName(c.getName(), this, false);
                this.enqueue(new EndElementImpl(xmlName));
            }
            this.emitEndPrefixMappings();
        }
        
        @Override
        protected void emitText(final SaveCur c) {
            assert c.isText();
            final Object src = c.getChars();
            final int cch = c._cchSrc;
            final int off = c._offSrc;
            this.enqueue(new CharacterDataImpl(src, cch, off));
        }
        
        @Override
        protected void emitComment(final SaveCur c) {
            this.enqueue(new CommentImpl(c.getChars(), c._cchSrc, c._offSrc));
        }
        
        @Override
        protected void emitProcinst(final SaveCur c) {
            String target = null;
            final QName name = c.getName();
            if (name != null) {
                target = name.getLocalPart();
            }
            this.enqueue(new ProcessingInstructionImpl(target, c.getChars(), c._cchSrc, c._offSrc));
        }
        
        @Override
        protected void emitDocType(final String doctypeName, final String publicID, final String systemID) {
            this.enqueue(new StartDocumentImpl(systemID, null, true, null));
        }
        
        @Override
        protected void emitStartDoc(final SaveCur c) {
            this.emitDocType(null, null, null);
        }
        
        @Override
        protected void emitEndDoc(final SaveCur c) {
            this.enqueue(new EndDocumentImpl());
        }
        
        XMLEvent dequeue() {
            if (this._out == null) {
                this.enterLocale();
                try {
                    if (!this.process()) {
                        return null;
                    }
                }
                finally {
                    this.exitLocale();
                }
            }
            if (this._out == null) {
                return null;
            }
            final XmlEventImpl e = this._out;
            if ((this._out = this._out._next) == null) {
                this._in = null;
            }
            return e;
        }
        
        private void enqueue(final XmlEventImpl e) {
            assert e._next == null;
            if (this._in == null) {
                assert this._out == null;
                this._in = e;
                this._out = e;
            }
            else {
                this._in._next = e;
                this._in = e;
            }
        }
        
        protected void emitEndPrefixMappings() {
            this.iterateMappings();
            while (this.hasMapping()) {
                final String prevPrefixUri = null;
                final String prefix = this.mappingPrefix();
                final String uri = this.mappingUri();
                if (prevPrefixUri == null) {
                    this.enqueue(new EndPrefixMappingImpl(prefix));
                }
                else {
                    this.enqueue(new ChangePrefixMappingImpl(prefix, uri, prevPrefixUri));
                }
                this.nextMapping();
            }
        }
        
        private static XMLName computeName(final QName name, final Saver saver, final boolean needsPrefix) {
            final String uri = name.getNamespaceURI();
            final String local = name.getLocalPart();
            assert uri != null;
            assert local.length() > 0;
            String prefix = null;
            if (uri != null && uri.length() != 0) {
                prefix = name.getPrefix();
                final String mappedUri = saver.getNamespaceForPrefix(prefix);
                if (mappedUri == null || !mappedUri.equals(uri)) {
                    prefix = saver.getUriMapping(uri);
                }
                if (needsPrefix && prefix.length() == 0) {
                    prefix = saver.getNonDefaultUriMapping(uri);
                }
            }
            return new XmlNameImpl(uri, local, prefix);
        }
        
        private abstract static class XmlEventImpl extends XmlEventBase
        {
            XmlEventImpl _next;
            
            XmlEventImpl(final int type) {
                super(type);
            }
            
            @Override
            public XMLName getName() {
                return null;
            }
            
            @Override
            public XMLName getSchemaType() {
                throw new RuntimeException("NYI");
            }
            
            @Override
            public boolean hasName() {
                return false;
            }
            
            @Override
            public final Location getLocation() {
                return null;
            }
        }
        
        private static class StartDocumentImpl extends XmlEventImpl implements StartDocument
        {
            String _systemID;
            String _encoding;
            boolean _standAlone;
            String _version;
            
            StartDocumentImpl(final String systemID, final String encoding, final boolean isStandAlone, final String version) {
                super(256);
                this._systemID = systemID;
                this._encoding = encoding;
                this._standAlone = isStandAlone;
                this._version = version;
            }
            
            @Override
            public String getSystemId() {
                return this._systemID;
            }
            
            @Override
            public String getCharacterEncodingScheme() {
                return this._encoding;
            }
            
            @Override
            public boolean isStandalone() {
                return this._standAlone;
            }
            
            @Override
            public String getVersion() {
                return this._version;
            }
        }
        
        private static class StartElementImpl extends XmlEventImpl implements StartElement
        {
            private XMLName _name;
            private Map _prefixMap;
            private AttributeImpl _attributes;
            private AttributeImpl _namespaces;
            
            StartElementImpl(final XMLName name, final AttributeImpl attributes, final AttributeImpl namespaces, final Map prefixMap) {
                super(2);
                this._name = name;
                this._attributes = attributes;
                this._namespaces = namespaces;
                this._prefixMap = prefixMap;
            }
            
            @Override
            public boolean hasName() {
                return true;
            }
            
            @Override
            public XMLName getName() {
                return this._name;
            }
            
            @Override
            public AttributeIterator getAttributes() {
                return new AttributeIteratorImpl(this._attributes, null);
            }
            
            @Override
            public AttributeIterator getNamespaces() {
                return new AttributeIteratorImpl(null, this._namespaces);
            }
            
            @Override
            public AttributeIterator getAttributesAndNamespaces() {
                return new AttributeIteratorImpl(this._attributes, this._namespaces);
            }
            
            @Override
            public Attribute getAttributeByName(final XMLName xmlName) {
                for (AttributeImpl a = this._attributes; a != null; a = a._next) {
                    if (xmlName.equals(a.getName())) {
                        return a;
                    }
                }
                return null;
            }
            
            @Override
            public String getNamespaceUri(final String prefix) {
                return this._prefixMap.get((prefix == null) ? "" : prefix);
            }
            
            @Override
            public Map getNamespaceMap() {
                return this._prefixMap;
            }
            
            private static class AttributeIteratorImpl implements AttributeIterator
            {
                private AttributeImpl _attributes;
                private AttributeImpl _namespaces;
                
                AttributeIteratorImpl(final AttributeImpl attributes, final AttributeImpl namespaces) {
                    this._attributes = attributes;
                    this._namespaces = namespaces;
                }
                
                public Object monitor() {
                    return this;
                }
                
                @Override
                public Attribute next() {
                    synchronized (this.monitor()) {
                        this.checkVersion();
                        AttributeImpl attr = null;
                        if (this._attributes != null) {
                            attr = this._attributes;
                            this._attributes = attr._next;
                        }
                        else if (this._namespaces != null) {
                            attr = this._namespaces;
                            this._namespaces = attr._next;
                        }
                        return attr;
                    }
                }
                
                @Override
                public boolean hasNext() {
                    synchronized (this.monitor()) {
                        this.checkVersion();
                        return this._attributes != null || this._namespaces != null;
                    }
                }
                
                @Override
                public Attribute peek() {
                    synchronized (this.monitor()) {
                        this.checkVersion();
                        if (this._attributes != null) {
                            return this._attributes;
                        }
                        if (this._namespaces != null) {
                            return this._namespaces;
                        }
                        return null;
                    }
                }
                
                @Override
                public void skip() {
                    synchronized (this.monitor()) {
                        this.checkVersion();
                        if (this._attributes != null) {
                            this._attributes = this._attributes._next;
                        }
                        else if (this._namespaces != null) {
                            this._namespaces = this._namespaces._next;
                        }
                    }
                }
                
                private final void checkVersion() {
                }
            }
            
            private abstract static class AttributeImpl implements Attribute
            {
                AttributeImpl _next;
                protected XMLName _name;
                
                AttributeImpl() {
                }
                
                @Override
                public XMLName getName() {
                    return this._name;
                }
                
                @Override
                public String getType() {
                    return "CDATA";
                }
                
                @Override
                public XMLName getSchemaType() {
                    return null;
                }
            }
            
            private static class XmlnsAttributeImpl extends AttributeImpl
            {
                private String _uri;
                
                XmlnsAttributeImpl(String prefix, final String uri) {
                    this._uri = uri;
                    String local;
                    if (prefix.length() == 0) {
                        prefix = null;
                        local = "xmlns";
                    }
                    else {
                        local = prefix;
                        prefix = "xmlns";
                    }
                    this._name = new XmlNameImpl(null, local, prefix);
                }
                
                @Override
                public String getValue() {
                    return this._uri;
                }
            }
            
            private static class NormalAttributeImpl extends AttributeImpl
            {
                private String _value;
                
                NormalAttributeImpl(final XMLName name, final String value) {
                    this._name = name;
                    this._value = value;
                }
                
                @Override
                public String getValue() {
                    return this._value;
                }
            }
        }
        
        private static class StartPrefixMappingImpl extends XmlEventImpl implements StartPrefixMapping
        {
            private String _prefix;
            private String _uri;
            
            StartPrefixMappingImpl(final String prefix, final String uri) {
                super(1024);
                this._prefix = prefix;
                this._uri = uri;
            }
            
            @Override
            public String getNamespaceUri() {
                return this._uri;
            }
            
            @Override
            public String getPrefix() {
                return this._prefix;
            }
        }
        
        private static class ChangePrefixMappingImpl extends XmlEventImpl implements ChangePrefixMapping
        {
            private String _oldUri;
            private String _newUri;
            private String _prefix;
            
            ChangePrefixMappingImpl(final String prefix, final String oldUri, final String newUri) {
                super(4096);
                this._oldUri = oldUri;
                this._newUri = newUri;
                this._prefix = prefix;
            }
            
            @Override
            public String getOldNamespaceUri() {
                return this._oldUri;
            }
            
            @Override
            public String getNewNamespaceUri() {
                return this._newUri;
            }
            
            @Override
            public String getPrefix() {
                return this._prefix;
            }
        }
        
        private static class EndPrefixMappingImpl extends XmlEventImpl implements EndPrefixMapping
        {
            private String _prefix;
            
            EndPrefixMappingImpl(final String prefix) {
                super(2048);
                this._prefix = prefix;
            }
            
            @Override
            public String getPrefix() {
                return this._prefix;
            }
        }
        
        private static class EndElementImpl extends XmlEventImpl implements EndElement
        {
            private XMLName _name;
            
            EndElementImpl(final XMLName name) {
                super(4);
                this._name = name;
            }
            
            @Override
            public boolean hasName() {
                return true;
            }
            
            @Override
            public XMLName getName() {
                return this._name;
            }
        }
        
        private static class EndDocumentImpl extends XmlEventImpl implements EndDocument
        {
            EndDocumentImpl() {
                super(512);
            }
        }
        
        private static class TripletEventImpl extends XmlEventImpl implements CharacterData
        {
            private Object _obj;
            private int _cch;
            private int _off;
            
            TripletEventImpl(final int eventType, final Object obj, final int cch, final int off) {
                super(eventType);
                this._obj = obj;
                this._cch = cch;
                this._off = off;
            }
            
            @Override
            public String getContent() {
                return CharUtil.getString(this._obj, this._off, this._cch);
            }
            
            @Override
            public boolean hasContent() {
                return this._cch > 0;
            }
        }
        
        private static class CharacterDataImpl extends TripletEventImpl implements CharacterData
        {
            CharacterDataImpl(final Object obj, final int cch, final int off) {
                super(16, obj, cch, off);
            }
        }
        
        private static class CommentImpl extends TripletEventImpl implements Comment
        {
            CommentImpl(final Object obj, final int cch, final int off) {
                super(32, obj, cch, off);
            }
        }
        
        private static class ProcessingInstructionImpl extends TripletEventImpl implements ProcessingInstruction
        {
            private String _target;
            
            ProcessingInstructionImpl(final String target, final Object obj, final int cch, final int off) {
                super(8, obj, cch, off);
                this._target = target;
            }
            
            @Override
            public String getTarget() {
                return this._target;
            }
            
            @Override
            public String getData() {
                return this.getContent();
            }
        }
    }
    
    static final class XmlInputStreamImpl extends GenericXmlInputStream
    {
        private XmlInputStreamSaver _xmlInputStreamSaver;
        
        XmlInputStreamImpl(final Cur cur, final XmlOptions options) {
            (this._xmlInputStreamSaver = new XmlInputStreamSaver(cur, options)).process();
        }
        
        @Override
        protected XMLEvent nextEvent() throws XMLStreamException {
            return this._xmlInputStreamSaver.dequeue();
        }
    }
    
    static final class SaxSaver extends Saver
    {
        private ContentHandler _contentHandler;
        private LexicalHandler _lexicalHandler;
        private AttributesImpl _attributes;
        private char[] _buf;
        private boolean _nsAsAttrs;
        
        SaxSaver(final Cur c, final XmlOptions options, final ContentHandler ch, final LexicalHandler lh) throws SAXException {
            super(c, options);
            this._contentHandler = ch;
            this._lexicalHandler = lh;
            this._attributes = new AttributesImpl();
            this._nsAsAttrs = !options.hasOption("SAVE_SAX_NO_NSDECLS_IN_ATTRIBUTES");
            this._contentHandler.startDocument();
            try {
                while (this.process()) {}
            }
            catch (final SaverSAXException e) {
                throw e._saxException;
            }
            this._contentHandler.endDocument();
        }
        
        private String getPrefixedName(final QName name) {
            final String uri = name.getNamespaceURI();
            final String local = name.getLocalPart();
            if (uri.length() == 0) {
                return local;
            }
            final String prefix = this.getUriMapping(uri);
            if (prefix.length() == 0) {
                return local;
            }
            return prefix + ":" + local;
        }
        
        private void emitNamespacesHelper() {
            this.iterateMappings();
            while (this.hasMapping()) {
                final String prefix = this.mappingPrefix();
                final String uri = this.mappingUri();
                try {
                    this._contentHandler.startPrefixMapping(prefix, uri);
                }
                catch (final SAXException e) {
                    throw new SaverSAXException(e);
                }
                if (this._nsAsAttrs) {
                    if (prefix == null || prefix.length() == 0) {
                        this._attributes.addAttribute("http://www.w3.org/2000/xmlns/", "xmlns", "xmlns", "CDATA", uri);
                    }
                    else {
                        this._attributes.addAttribute("http://www.w3.org/2000/xmlns/", prefix, "xmlns:" + prefix, "CDATA", uri);
                    }
                }
                this.nextMapping();
            }
        }
        
        @Override
        protected boolean emitElement(final SaveCur c, final ArrayList attrNames, final ArrayList attrValues) {
            this._attributes.clear();
            if (this.saveNamespacesFirst()) {
                this.emitNamespacesHelper();
            }
            for (int i = 0; i < attrNames.size(); ++i) {
                final QName name = attrNames.get(i);
                this._attributes.addAttribute(name.getNamespaceURI(), name.getLocalPart(), this.getPrefixedName(name), "CDATA", attrValues.get(i));
            }
            if (!this.saveNamespacesFirst()) {
                this.emitNamespacesHelper();
            }
            final QName elemName = c.getName();
            try {
                this._contentHandler.startElement(elemName.getNamespaceURI(), elemName.getLocalPart(), this.getPrefixedName(elemName), this._attributes);
            }
            catch (final SAXException e) {
                throw new SaverSAXException(e);
            }
            return false;
        }
        
        @Override
        protected void emitFinish(final SaveCur c) {
            final QName name = c.getName();
            try {
                this._contentHandler.endElement(name.getNamespaceURI(), name.getLocalPart(), this.getPrefixedName(name));
                this.iterateMappings();
                while (this.hasMapping()) {
                    this._contentHandler.endPrefixMapping(this.mappingPrefix());
                    this.nextMapping();
                }
            }
            catch (final SAXException e) {
                throw new SaverSAXException(e);
            }
        }
        
        @Override
        protected void emitText(final SaveCur c) {
            assert c.isText();
            final Object src = c.getChars();
            try {
                if (src instanceof char[]) {
                    this._contentHandler.characters((char[])src, c._offSrc, c._cchSrc);
                }
                else {
                    if (this._buf == null) {
                        this._buf = new char[1024];
                    }
                    while (c._cchSrc > 0) {
                        final int cch = Math.min(this._buf.length, c._cchSrc);
                        CharUtil.getChars(this._buf, 0, src, c._offSrc, cch);
                        this._contentHandler.characters(this._buf, 0, cch);
                        c._offSrc += cch;
                        c._cchSrc -= cch;
                    }
                }
            }
            catch (final SAXException e) {
                throw new SaverSAXException(e);
            }
        }
        
        @Override
        protected void emitComment(final SaveCur c) {
            if (this._lexicalHandler != null) {
                c.push();
                c.next();
                try {
                    if (!c.isText()) {
                        this._lexicalHandler.comment(null, 0, 0);
                    }
                    else {
                        final Object src = c.getChars();
                        if (src instanceof char[]) {
                            this._lexicalHandler.comment((char[])src, c._offSrc, c._cchSrc);
                        }
                        else {
                            if (this._buf == null || this._buf.length < c._cchSrc) {
                                this._buf = new char[Math.max(1024, c._cchSrc)];
                            }
                            CharUtil.getChars(this._buf, 0, src, c._offSrc, c._cchSrc);
                            this._lexicalHandler.comment(this._buf, 0, c._cchSrc);
                        }
                    }
                }
                catch (final SAXException e) {
                    throw new SaverSAXException(e);
                }
                c.pop();
            }
        }
        
        @Override
        protected void emitProcinst(final SaveCur c) {
            final String target = c.getName().getLocalPart();
            c.push();
            c.next();
            final String value = CharUtil.getString(c.getChars(), c._offSrc, c._cchSrc);
            c.pop();
            try {
                this._contentHandler.processingInstruction(c.getName().getLocalPart(), value);
            }
            catch (final SAXException e) {
                throw new SaverSAXException(e);
            }
        }
        
        @Override
        protected void emitDocType(final String docTypeName, final String publicId, final String systemId) {
            if (this._lexicalHandler != null) {
                try {
                    this._lexicalHandler.startDTD(docTypeName, publicId, systemId);
                    this._lexicalHandler.endDTD();
                }
                catch (final SAXException e) {
                    throw new SaverSAXException(e);
                }
            }
        }
        
        @Override
        protected void emitStartDoc(final SaveCur c) {
        }
        
        @Override
        protected void emitEndDoc(final SaveCur c) {
        }
        
        private class SaverSAXException extends RuntimeException
        {
            SAXException _saxException;
            
            SaverSAXException(final SAXException e) {
                this._saxException = e;
            }
        }
    }
    
    abstract static class SaveCur
    {
        int _offSrc;
        int _cchSrc;
        
        final boolean isRoot() {
            return this.kind() == 1;
        }
        
        final boolean isElem() {
            return this.kind() == 2;
        }
        
        final boolean isAttr() {
            return this.kind() == 3;
        }
        
        final boolean isText() {
            return this.kind() == 0;
        }
        
        final boolean isComment() {
            return this.kind() == 4;
        }
        
        final boolean isProcinst() {
            return this.kind() == 5;
        }
        
        final boolean isFinish() {
            return Cur.kindIsFinish(this.kind());
        }
        
        final boolean isContainer() {
            return Cur.kindIsContainer(this.kind());
        }
        
        final boolean isNormalAttr() {
            return this.kind() == 3 && !this.isXmlns();
        }
        
        final boolean skip() {
            this.toEnd();
            return this.next();
        }
        
        abstract void release();
        
        abstract int kind();
        
        abstract QName getName();
        
        abstract String getXmlnsPrefix();
        
        abstract String getXmlnsUri();
        
        abstract boolean isXmlns();
        
        abstract boolean hasChildren();
        
        abstract boolean hasText();
        
        abstract boolean isTextCData();
        
        abstract boolean toFirstAttr();
        
        abstract boolean toNextAttr();
        
        abstract String getAttrValue();
        
        abstract boolean next();
        
        abstract void toEnd();
        
        abstract void push();
        
        abstract void pop();
        
        abstract Object getChars();
        
        abstract List getAncestorNamespaces();
        
        abstract XmlDocumentProperties getDocProps();
    }
    
    private static final class DocSaveCur extends SaveCur
    {
        private Cur _cur;
        
        DocSaveCur(final Cur c) {
            assert c.isRoot();
            this._cur = c.weakCur(this);
        }
        
        @Override
        void release() {
            this._cur.release();
            this._cur = null;
        }
        
        @Override
        int kind() {
            return this._cur.kind();
        }
        
        @Override
        QName getName() {
            return this._cur.getName();
        }
        
        @Override
        String getXmlnsPrefix() {
            return this._cur.getXmlnsPrefix();
        }
        
        @Override
        String getXmlnsUri() {
            return this._cur.getXmlnsUri();
        }
        
        @Override
        boolean isXmlns() {
            return this._cur.isXmlns();
        }
        
        @Override
        boolean hasChildren() {
            return this._cur.hasChildren();
        }
        
        @Override
        boolean hasText() {
            return this._cur.hasText();
        }
        
        @Override
        boolean isTextCData() {
            return this._cur.isTextCData();
        }
        
        @Override
        boolean toFirstAttr() {
            return this._cur.toFirstAttr();
        }
        
        @Override
        boolean toNextAttr() {
            return this._cur.toNextAttr();
        }
        
        @Override
        String getAttrValue() {
            assert this._cur.isAttr();
            return this._cur.getValueAsString();
        }
        
        @Override
        void toEnd() {
            this._cur.toEnd();
        }
        
        @Override
        boolean next() {
            return this._cur.next();
        }
        
        @Override
        void push() {
            this._cur.push();
        }
        
        @Override
        void pop() {
            this._cur.pop();
        }
        
        @Override
        List getAncestorNamespaces() {
            return null;
        }
        
        @Override
        Object getChars() {
            final Object o = this._cur.getChars(-1);
            this._offSrc = this._cur._offSrc;
            this._cchSrc = this._cur._cchSrc;
            return o;
        }
        
        @Override
        XmlDocumentProperties getDocProps() {
            return Locale.getDocProps(this._cur, false);
        }
    }
    
    private abstract static class FilterSaveCur extends SaveCur
    {
        private SaveCur _cur;
        
        FilterSaveCur(final SaveCur c) {
            assert c.isRoot();
            this._cur = c;
        }
        
        protected abstract boolean filter();
        
        @Override
        void release() {
            this._cur.release();
            this._cur = null;
        }
        
        @Override
        int kind() {
            return this._cur.kind();
        }
        
        @Override
        QName getName() {
            return this._cur.getName();
        }
        
        @Override
        String getXmlnsPrefix() {
            return this._cur.getXmlnsPrefix();
        }
        
        @Override
        String getXmlnsUri() {
            return this._cur.getXmlnsUri();
        }
        
        @Override
        boolean isXmlns() {
            return this._cur.isXmlns();
        }
        
        @Override
        boolean hasChildren() {
            return this._cur.hasChildren();
        }
        
        @Override
        boolean hasText() {
            return this._cur.hasText();
        }
        
        @Override
        boolean isTextCData() {
            return this._cur.isTextCData();
        }
        
        @Override
        boolean toFirstAttr() {
            return this._cur.toFirstAttr();
        }
        
        @Override
        boolean toNextAttr() {
            return this._cur.toNextAttr();
        }
        
        @Override
        String getAttrValue() {
            return this._cur.getAttrValue();
        }
        
        @Override
        void toEnd() {
            this._cur.toEnd();
        }
        
        @Override
        boolean next() {
            if (!this._cur.next()) {
                return false;
            }
            if (!this.filter()) {
                return true;
            }
            assert !this.isRoot() && !this.isText() && !this.isAttr();
            this.toEnd();
            return this.next();
        }
        
        @Override
        void push() {
            this._cur.push();
        }
        
        @Override
        void pop() {
            this._cur.pop();
        }
        
        @Override
        List getAncestorNamespaces() {
            return this._cur.getAncestorNamespaces();
        }
        
        @Override
        Object getChars() {
            final Object o = this._cur.getChars();
            this._offSrc = this._cur._offSrc;
            this._cchSrc = this._cur._cchSrc;
            return o;
        }
        
        @Override
        XmlDocumentProperties getDocProps() {
            return this._cur.getDocProps();
        }
    }
    
    private static final class FilterPiSaveCur extends FilterSaveCur
    {
        private String _piTarget;
        
        FilterPiSaveCur(final SaveCur c, final String target) {
            super(c);
            this._piTarget = target;
        }
        
        @Override
        protected boolean filter() {
            return this.kind() == 5 && this.getName().getLocalPart().equals(this._piTarget);
        }
    }
    
    private static final class FragSaveCur extends SaveCur
    {
        private Cur _cur;
        private Cur _end;
        private ArrayList _ancestorNamespaces;
        private QName _elem;
        private boolean _saveAttr;
        private static final int ROOT_START = 1;
        private static final int ELEM_START = 2;
        private static final int ROOT_END = 3;
        private static final int ELEM_END = 4;
        private static final int CUR = 5;
        private int _state;
        private int[] _stateStack;
        private int _stateStackSize;
        
        FragSaveCur(final Cur start, final Cur end, final QName synthElem) {
            this._saveAttr = (start.isAttr() && start.isSamePos(end));
            this._cur = start.weakCur(this);
            this._end = end.weakCur(this);
            this._elem = synthElem;
            this._state = 1;
            this._stateStack = new int[8];
            start.push();
            this.computeAncestorNamespaces(start);
            start.pop();
        }
        
        @Override
        List getAncestorNamespaces() {
            return this._ancestorNamespaces;
        }
        
        private void computeAncestorNamespaces(final Cur c) {
            this._ancestorNamespaces = new ArrayList();
            while (c.toParentRaw()) {
                if (c.toFirstAttr()) {
                    do {
                        if (c.isXmlns()) {
                            final String prefix = c.getXmlnsPrefix();
                            final String uri = c.getXmlnsUri();
                            if (uri.length() <= 0 && prefix.length() != 0) {
                                continue;
                            }
                            this._ancestorNamespaces.add(c.getXmlnsPrefix());
                            this._ancestorNamespaces.add(c.getXmlnsUri());
                        }
                    } while (c.toNextAttr());
                    c.toParent();
                }
            }
        }
        
        @Override
        void release() {
            this._cur.release();
            this._cur = null;
            this._end.release();
            this._end = null;
        }
        
        @Override
        int kind() {
            switch (this._state) {
                case 1: {
                    return 1;
                }
                case 2: {
                    return 2;
                }
                case 4: {
                    return -2;
                }
                case 3: {
                    return -1;
                }
                default: {
                    assert this._state == 5;
                    return this._cur.kind();
                }
            }
        }
        
        @Override
        QName getName() {
            switch (this._state) {
                case 1:
                case 3: {
                    return null;
                }
                case 2:
                case 4: {
                    return this._elem;
                }
                default: {
                    assert this._state == 5;
                    return this._cur.getName();
                }
            }
        }
        
        @Override
        String getXmlnsPrefix() {
            assert this._state == 5 && this._cur.isAttr();
            return this._cur.getXmlnsPrefix();
        }
        
        @Override
        String getXmlnsUri() {
            assert this._state == 5 && this._cur.isAttr();
            return this._cur.getXmlnsUri();
        }
        
        @Override
        boolean isXmlns() {
            assert this._state == 5 && this._cur.isAttr();
            return this._cur.isXmlns();
        }
        
        @Override
        boolean hasChildren() {
            boolean hasChildren = false;
            if (this.isContainer()) {
                this.push();
                this.next();
                if (!this.isText() && !this.isFinish()) {
                    hasChildren = true;
                }
                this.pop();
            }
            return hasChildren;
        }
        
        @Override
        boolean hasText() {
            boolean hasText = false;
            if (this.isContainer()) {
                this.push();
                this.next();
                if (this.isText()) {
                    hasText = true;
                }
                this.pop();
            }
            return hasText;
        }
        
        @Override
        boolean isTextCData() {
            return this._cur.isTextCData();
        }
        
        @Override
        Object getChars() {
            assert this._state == 5 && this._cur.isText();
            final Object src = this._cur.getChars(-1);
            this._offSrc = this._cur._offSrc;
            this._cchSrc = this._cur._cchSrc;
            return src;
        }
        
        @Override
        boolean next() {
            switch (this._state) {
                case 1: {
                    this._state = ((this._elem == null) ? 5 : 2);
                    break;
                }
                case 2: {
                    if (this._saveAttr) {
                        this._state = 4;
                        break;
                    }
                    if (this._cur.isAttr()) {
                        this._cur.toParent();
                        this._cur.next();
                    }
                    if (this._cur.isSamePos(this._end)) {
                        this._state = 4;
                        break;
                    }
                    this._state = 5;
                    break;
                }
                case 5: {
                    assert !this._cur.isAttr();
                    this._cur.next();
                    if (this._cur.isSamePos(this._end)) {
                        this._state = ((this._elem == null) ? 3 : 4);
                        break;
                    }
                    break;
                }
                case 4: {
                    this._state = 3;
                    break;
                }
                case 3: {
                    return false;
                }
            }
            return true;
        }
        
        @Override
        void toEnd() {
            switch (this._state) {
                case 1: {
                    this._state = 3;
                    return;
                }
                case 2: {
                    this._state = 4;
                    return;
                }
                case 3:
                case 4: {
                    return;
                }
                default: {
                    assert this._state == 5 && !this._cur.isAttr() && !this._cur.isText();
                    this._cur.toEnd();
                }
            }
        }
        
        @Override
        boolean toFirstAttr() {
            switch (this._state) {
                case 1:
                case 3:
                case 4: {
                    return false;
                }
                case 5: {
                    return this._cur.toFirstAttr();
                }
                default: {
                    assert this._state == 2;
                    if (!this._cur.isAttr()) {
                        return false;
                    }
                    this._state = 5;
                    return true;
                }
            }
        }
        
        @Override
        boolean toNextAttr() {
            assert this._state == 5;
            return !this._saveAttr && this._cur.toNextAttr();
        }
        
        @Override
        String getAttrValue() {
            assert this._state == 5 && this._cur.isAttr();
            return this._cur.getValueAsString();
        }
        
        @Override
        void push() {
            if (this._stateStackSize == this._stateStack.length) {
                final int[] newStateStack = new int[this._stateStackSize * 2];
                System.arraycopy(this._stateStack, 0, newStateStack, 0, this._stateStackSize);
                this._stateStack = newStateStack;
            }
            this._stateStack[this._stateStackSize++] = this._state;
            this._cur.push();
        }
        
        @Override
        void pop() {
            this._cur.pop();
            final int[] stateStack = this._stateStack;
            final int stateStackSize = this._stateStackSize - 1;
            this._stateStackSize = stateStackSize;
            this._state = stateStack[stateStackSize];
        }
        
        @Override
        XmlDocumentProperties getDocProps() {
            return Locale.getDocProps(this._cur, false);
        }
    }
    
    private static final class PrettySaveCur extends SaveCur
    {
        private SaveCur _cur;
        private int _prettyIndent;
        private int _prettyOffset;
        private String _txt;
        private StringBuffer _sb;
        private int _depth;
        private ArrayList _stack;
        private boolean _isTextCData;
        private boolean _useCDataBookmarks;
        
        PrettySaveCur(final SaveCur c, final XmlOptions options) {
            this._isTextCData = false;
            this._useCDataBookmarks = false;
            this._sb = new StringBuffer();
            this._stack = new ArrayList();
            this._cur = c;
            assert options != null;
            this._prettyIndent = 2;
            if (options.hasOption("SAVE_PRETTY_PRINT_INDENT")) {
                this._prettyIndent = (int)options.get("SAVE_PRETTY_PRINT_INDENT");
            }
            if (options.hasOption("SAVE_PRETTY_PRINT_OFFSET")) {
                this._prettyOffset = (int)options.get("SAVE_PRETTY_PRINT_OFFSET");
            }
            if (options.hasOption("LOAD_SAVE_CDATA_BOOKMARKS")) {
                this._useCDataBookmarks = true;
            }
        }
        
        @Override
        List getAncestorNamespaces() {
            return this._cur.getAncestorNamespaces();
        }
        
        @Override
        void release() {
            this._cur.release();
        }
        
        @Override
        int kind() {
            return (this._txt == null) ? this._cur.kind() : 0;
        }
        
        @Override
        QName getName() {
            assert this._txt == null;
            return this._cur.getName();
        }
        
        @Override
        String getXmlnsPrefix() {
            assert this._txt == null;
            return this._cur.getXmlnsPrefix();
        }
        
        @Override
        String getXmlnsUri() {
            assert this._txt == null;
            return this._cur.getXmlnsUri();
        }
        
        @Override
        boolean isXmlns() {
            return this._txt == null && this._cur.isXmlns();
        }
        
        @Override
        boolean hasChildren() {
            return this._txt == null && this._cur.hasChildren();
        }
        
        @Override
        boolean hasText() {
            return this._txt == null && this._cur.hasText();
        }
        
        @Override
        boolean isTextCData() {
            return (this._txt == null) ? (this._useCDataBookmarks && this._cur.isTextCData()) : this._isTextCData;
        }
        
        @Override
        boolean toFirstAttr() {
            assert this._txt == null;
            return this._cur.toFirstAttr();
        }
        
        @Override
        boolean toNextAttr() {
            assert this._txt == null;
            return this._cur.toNextAttr();
        }
        
        @Override
        String getAttrValue() {
            assert this._txt == null;
            return this._cur.getAttrValue();
        }
        
        @Override
        void toEnd() {
            assert this._txt == null;
            this._cur.toEnd();
            if (this._cur.kind() == -2) {
                --this._depth;
            }
        }
        
        @Override
        boolean next() {
            int k;
            if (this._txt != null) {
                assert this._txt.length() > 0;
                assert !this._cur.isText();
                this._txt = null;
                this._isTextCData = false;
                k = this._cur.kind();
            }
            else {
                final int prevKind;
                k = (prevKind = this._cur.kind());
                if (!this._cur.next()) {
                    return false;
                }
                this._sb.delete(0, this._sb.length());
                assert this._txt == null;
                if (this._cur.isText()) {
                    this._isTextCData = (this._useCDataBookmarks && this._cur.isTextCData());
                    CharUtil.getString(this._sb, this._cur.getChars(), this._cur._offSrc, this._cur._cchSrc);
                    this._cur.next();
                    trim(this._sb);
                }
                k = this._cur.kind();
                if (this._prettyIndent >= 0 && prevKind != 4 && prevKind != 5 && (prevKind != 2 || k != -2)) {
                    if (this._sb.length() > 0) {
                        this._sb.insert(0, Saver._newLine);
                        spaces(this._sb, Saver._newLine.length(), this._prettyOffset + this._prettyIndent * this._depth);
                    }
                    if (k != -1) {
                        if (prevKind != 1) {
                            this._sb.append(Saver._newLine);
                        }
                        final int d = (k < 0) ? (this._depth - 1) : this._depth;
                        spaces(this._sb, this._sb.length(), this._prettyOffset + this._prettyIndent * d);
                    }
                }
                if (this._sb.length() > 0) {
                    this._txt = this._sb.toString();
                    k = 0;
                }
            }
            if (k == 2) {
                ++this._depth;
            }
            else if (k == -2) {
                --this._depth;
            }
            return true;
        }
        
        @Override
        void push() {
            this._cur.push();
            this._stack.add(this._txt);
            this._stack.add(new Integer(this._depth));
            this._isTextCData = false;
        }
        
        @Override
        void pop() {
            this._cur.pop();
            this._depth = this._stack.remove(this._stack.size() - 1);
            this._txt = this._stack.remove(this._stack.size() - 1);
            this._isTextCData = false;
        }
        
        @Override
        Object getChars() {
            if (this._txt != null) {
                this._offSrc = 0;
                this._cchSrc = this._txt.length();
                return this._txt;
            }
            final Object o = this._cur.getChars();
            this._offSrc = this._cur._offSrc;
            this._cchSrc = this._cur._cchSrc;
            return o;
        }
        
        @Override
        XmlDocumentProperties getDocProps() {
            return this._cur.getDocProps();
        }
        
        static void spaces(final StringBuffer sb, final int offset, int count) {
            while (count-- > 0) {
                sb.insert(offset, ' ');
            }
        }
        
        static void trim(final StringBuffer sb) {
            int i;
            for (i = 0; i < sb.length() && CharUtil.isWhiteSpace(sb.charAt(i)); ++i) {}
            sb.delete(0, i);
            for (i = sb.length(); i > 0 && CharUtil.isWhiteSpace(sb.charAt(i - 1)); --i) {}
            sb.delete(i, sb.length());
        }
    }
}
