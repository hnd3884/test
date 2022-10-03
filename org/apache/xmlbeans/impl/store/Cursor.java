package org.apache.xmlbeans.impl.store;

import java.util.ArrayList;
import org.apache.xmlbeans.SchemaTypeLoader;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.common.GlobalLock;
import java.util.Collection;
import org.apache.xmlbeans.XmlObject;
import java.util.Map;
import java.io.FileOutputStream;
import org.apache.xmlbeans.XmlDocumentProperties;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.ContentHandler;
import java.io.Writer;
import java.io.OutputStream;
import java.io.IOException;
import java.io.File;
import java.io.Reader;
import java.io.InputStream;
import org.w3c.dom.Node;
import javax.xml.stream.XMLStreamReader;
import org.apache.xmlbeans.XmlOptions;
import org.apache.xmlbeans.xml.stream.XMLInputStream;
import org.apache.xmlbeans.impl.common.XMLChar;
import javax.xml.namespace.QName;
import java.io.PrintStream;
import org.apache.xmlbeans.XmlCursor;

public final class Cursor implements XmlCursor, Locale.ChangeListener
{
    static final int ROOT = 1;
    static final int ELEM = 2;
    static final int ATTR = 3;
    static final int COMMENT = 4;
    static final int PROCINST = 5;
    static final int TEXT = 0;
    private static final int MOVE_XML = 0;
    private static final int COPY_XML = 1;
    private static final int MOVE_XML_CONTENTS = 2;
    private static final int COPY_XML_CONTENTS = 3;
    private static final int MOVE_CHARS = 4;
    private static final int COPY_CHARS = 5;
    private Cur _cur;
    private Path.PathEngine _pathEngine;
    private int _currentSelection;
    private Locale.ChangeListener _nextChangeListener;
    
    Cursor(final Xobj x, final int p) {
        (this._cur = x._locale.weakCur(this)).moveTo(x, p);
        this._currentSelection = -1;
    }
    
    Cursor(final Cur c) {
        this(c._xobj, c._pos);
    }
    
    private static boolean isValid(final Cur c) {
        if (c.kind() <= 0) {
            c.push();
            if (c.toParentRaw()) {
                final int pk = c.kind();
                if (pk == 4 || pk == 5 || pk == 3) {
                    return false;
                }
            }
            c.pop();
        }
        return true;
    }
    
    private boolean isValid() {
        return isValid(this._cur);
    }
    
    Locale locale() {
        return this._cur._locale;
    }
    
    Cur tempCur() {
        return this._cur.tempCur();
    }
    
    public void dump(final PrintStream o) {
        this._cur.dump(o);
    }
    
    static void validateLocalName(final QName name) {
        if (name == null) {
            throw new IllegalArgumentException("QName is null");
        }
        validateLocalName(name.getLocalPart());
    }
    
    static void validateLocalName(final String name) {
        if (name == null) {
            throw new IllegalArgumentException("Name is null");
        }
        if (name.length() == 0) {
            throw new IllegalArgumentException("Name is empty");
        }
        if (!XMLChar.isValidNCName(name)) {
            throw new IllegalArgumentException("Name is not valid");
        }
    }
    
    static void validatePrefix(final String name) {
        if (name == null) {
            throw new IllegalArgumentException("Prefix is null");
        }
        if (name.length() == 0) {
            throw new IllegalArgumentException("Prefix is empty");
        }
        if (Locale.beginsWithXml(name)) {
            throw new IllegalArgumentException("Prefix begins with 'xml'");
        }
        if (!XMLChar.isValidNCName(name)) {
            throw new IllegalArgumentException("Prefix is not valid");
        }
    }
    
    private static void complain(final String msg) {
        throw new IllegalArgumentException(msg);
    }
    
    private void checkInsertionValidity(final Cur that) {
        final int thatKind = that.kind();
        if (thatKind < 0) {
            complain("Can't move/copy/insert an end token.");
        }
        if (thatKind == 1) {
            complain("Can't move/copy/insert a whole document.");
        }
        final int thisKind = this._cur.kind();
        if (thisKind == 1) {
            complain("Can't insert before the start of the document.");
        }
        if (thatKind == 3) {
            this._cur.push();
            this._cur.prevWithAttrs();
            final int pk = this._cur.kind();
            this._cur.pop();
            if (pk != 2 && pk != 1 && pk != -3) {
                complain("Can only insert attributes before other attributes or after containers.");
            }
        }
        if (thisKind == 3 && thatKind != 3) {
            complain("Can only insert attributes before other attributes or after containers.");
        }
    }
    
    private void insertNode(final Cur that, final String text) {
        assert !that.isRoot();
        assert that.isNode();
        assert isValid(that);
        assert this.isValid();
        if (text != null && text.length() > 0) {
            that.next();
            that.insertString(text);
            that.toParent();
        }
        this.checkInsertionValidity(that);
        that.moveNode(this._cur);
        this._cur.toEnd();
        this._cur.nextWithAttrs();
    }
    
    public void _dispose() {
        this._cur.release();
        this._cur = null;
    }
    
    public XmlCursor _newCursor() {
        return new Cursor(this._cur);
    }
    
    public QName _getName() {
        switch (this._cur.kind()) {
            case 3: {
                if (this._cur.isXmlns()) {
                    return this._cur._locale.makeQNameNoCheck(this._cur.getXmlnsUri(), this._cur.getXmlnsPrefix());
                }
                return this._cur.getName();
            }
            case 2:
            case 5: {
                return this._cur.getName();
            }
            default: {
                return null;
            }
        }
    }
    
    public void _setName(final QName name) {
        if (name == null) {
            throw new IllegalArgumentException("Name is null");
        }
        switch (this._cur.kind()) {
            case 2:
            case 3: {
                validateLocalName(name.getLocalPart());
                break;
            }
            case 5: {
                validatePrefix(name.getLocalPart());
                if (name.getNamespaceURI().length() > 0) {
                    throw new IllegalArgumentException("Procinst name must have no URI");
                }
                if (name.getPrefix().length() > 0) {
                    throw new IllegalArgumentException("Procinst name must have no prefix");
                }
                break;
            }
            default: {
                throw new IllegalStateException("Can set name on element, atrtribute and procinst only");
            }
        }
        this._cur.setName(name);
    }
    
    public TokenType _currentTokenType() {
        assert this.isValid();
        switch (this._cur.kind()) {
            case 1: {
                return TokenType.STARTDOC;
            }
            case -1: {
                return TokenType.ENDDOC;
            }
            case 2: {
                return TokenType.START;
            }
            case -2: {
                return TokenType.END;
            }
            case 0: {
                return TokenType.TEXT;
            }
            case 3: {
                return this._cur.isXmlns() ? TokenType.NAMESPACE : TokenType.ATTR;
            }
            case 4: {
                return TokenType.COMMENT;
            }
            case 5: {
                return TokenType.PROCINST;
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    public boolean _isStartdoc() {
        assert this.isValid();
        return this._cur.isRoot();
    }
    
    public boolean _isEnddoc() {
        assert this.isValid();
        return this._cur.isEndRoot();
    }
    
    public boolean _isStart() {
        assert this.isValid();
        return this._cur.isElem();
    }
    
    public boolean _isEnd() {
        assert this.isValid();
        return this._cur.isEnd();
    }
    
    public boolean _isText() {
        assert this.isValid();
        return this._cur.isText();
    }
    
    public boolean _isAttr() {
        assert this.isValid();
        return this._cur.isNormalAttr();
    }
    
    public boolean _isNamespace() {
        assert this.isValid();
        return this._cur.isXmlns();
    }
    
    public boolean _isComment() {
        assert this.isValid();
        return this._cur.isComment();
    }
    
    public boolean _isProcinst() {
        assert this.isValid();
        return this._cur.isProcinst();
    }
    
    public boolean _isContainer() {
        assert this.isValid();
        return this._cur.isContainer();
    }
    
    public boolean _isFinish() {
        assert this.isValid();
        return this._cur.isFinish();
    }
    
    public boolean _isAnyAttr() {
        assert this.isValid();
        return this._cur.isAttr();
    }
    
    public TokenType _toNextToken() {
        assert this.isValid();
        switch (this._cur.kind()) {
            case 1:
            case 2: {
                if (!this._cur.toFirstAttr()) {
                    this._cur.next();
                    break;
                }
                break;
            }
            case 3: {
                if (!this._cur.toNextSibling()) {
                    this._cur.toParent();
                    this._cur.next();
                    break;
                }
                break;
            }
            case 4:
            case 5: {
                this._cur.skip();
                break;
            }
            default: {
                if (!this._cur.next()) {
                    return TokenType.NONE;
                }
                break;
            }
        }
        return this._currentTokenType();
    }
    
    public TokenType _toPrevToken() {
        assert this.isValid();
        final boolean wasText = this._cur.isText();
        if (!this._cur.prev()) {
            assert this._cur.isRoot() || this._cur.isAttr();
            if (this._cur.isRoot()) {
                return TokenType.NONE;
            }
            this._cur.toParent();
        }
        else {
            final int k = this._cur.kind();
            if (k < 0 && (k == -4 || k == -5 || k == -3)) {
                this._cur.toParent();
            }
            else if (this._cur.isContainer()) {
                this._cur.toLastAttr();
            }
            else if (wasText && this._cur.isText()) {
                return this._toPrevToken();
            }
        }
        return this._currentTokenType();
    }
    
    public Object _monitor() {
        return this._cur._locale;
    }
    
    public boolean _toParent() {
        final Cur c = this._cur.tempCur();
        if (!c.toParent()) {
            return false;
        }
        this._cur.moveToCur(c);
        c.release();
        return true;
    }
    
    public ChangeStamp _getDocChangeStamp() {
        return new ChangeStampImpl(this._cur._locale);
    }
    
    @Deprecated
    public XMLInputStream _newXMLInputStream() {
        return this._newXMLInputStream(null);
    }
    
    public XMLStreamReader _newXMLStreamReader() {
        return this._newXMLStreamReader(null);
    }
    
    public Node _newDomNode() {
        return this._newDomNode(null);
    }
    
    public InputStream _newInputStream() {
        return this._newInputStream(null);
    }
    
    public String _xmlText() {
        return this._xmlText(null);
    }
    
    public Reader _newReader() {
        return this._newReader(null);
    }
    
    public void _save(final File file) throws IOException {
        this._save(file, null);
    }
    
    public void _save(final OutputStream os) throws IOException {
        this._save(os, null);
    }
    
    public void _save(final Writer w) throws IOException {
        this._save(w, null);
    }
    
    public void _save(final ContentHandler ch, final LexicalHandler lh) throws SAXException {
        this._save(ch, lh, null);
    }
    
    public XmlDocumentProperties _documentProperties() {
        return Locale.getDocProps(this._cur, true);
    }
    
    public XMLStreamReader _newXMLStreamReader(final XmlOptions options) {
        return Jsr173.newXmlStreamReader(this._cur, options);
    }
    
    @Deprecated
    public XMLInputStream _newXMLInputStream(final XmlOptions options) {
        return new Saver.XmlInputStreamImpl(this._cur, options);
    }
    
    public String _xmlText(final XmlOptions options) {
        assert this.isValid();
        return new Saver.TextSaver(this._cur, options, null).saveToString();
    }
    
    public InputStream _newInputStream(final XmlOptions options) {
        return new Saver.InputStreamSaver(this._cur, options);
    }
    
    public Reader _newReader(final XmlOptions options) {
        return new Saver.TextReader(this._cur, options);
    }
    
    public void _save(final ContentHandler ch, final LexicalHandler lh, final XmlOptions options) throws SAXException {
        new Saver.SaxSaver(this._cur, options, ch, lh);
    }
    
    public void _save(final File file, final XmlOptions options) throws IOException {
        if (file == null) {
            throw new IllegalArgumentException("Null file specified");
        }
        final OutputStream os = new FileOutputStream(file);
        try {
            this._save(os, options);
        }
        finally {
            os.close();
        }
    }
    
    public void _save(final OutputStream os, final XmlOptions options) throws IOException {
        if (os == null) {
            throw new IllegalArgumentException("Null OutputStream specified");
        }
        final InputStream is = this._newInputStream(options);
        try {
            final byte[] bytes = new byte[8192];
            while (true) {
                final int n = is.read(bytes);
                if (n < 0) {
                    break;
                }
                os.write(bytes, 0, n);
            }
        }
        finally {
            is.close();
        }
    }
    
    public void _save(final Writer w, final XmlOptions options) throws IOException {
        if (w == null) {
            throw new IllegalArgumentException("Null Writer specified");
        }
        if (options != null && options.hasOption("SAVE_OPTIMIZE_FOR_SPEED")) {
            Saver.OptimizedForSpeedSaver.save(this._cur, w);
            return;
        }
        final Reader r = this._newReader(options);
        try {
            final char[] chars = new char[8192];
            while (true) {
                final int n = r.read(chars);
                if (n < 0) {
                    break;
                }
                w.write(chars, 0, n);
            }
        }
        finally {
            r.close();
        }
    }
    
    public Node _getDomNode() {
        return (Node)this._cur.getDom();
    }
    
    private boolean isDomFragment() {
        if (!this.isStartdoc()) {
            return true;
        }
        boolean seenElement = false;
        final XmlCursor c = this.newCursor();
        int token = c.toNextToken().intValue();
        try {
        Label_0193:
            while (true) {
                switch (token) {
                    case 3: {
                        if (seenElement) {
                            return true;
                        }
                        seenElement = true;
                        token = c.toEndToken().intValue();
                        continue;
                    }
                    case 5: {
                        if (!Locale.isWhiteSpace(c.getChars())) {
                            return true;
                        }
                        token = c.toNextToken().intValue();
                        continue;
                    }
                    case 0:
                    case 2: {
                        break Label_0193;
                    }
                    case 6:
                    case 7: {
                        return true;
                    }
                    case 4:
                    case 8:
                    case 9: {
                        token = c.toNextToken().intValue();
                        continue;
                    }
                    case 1: {
                        assert false;
                        break Label_0193;
                    }
                    default: {
                        continue;
                    }
                }
            }
        }
        finally {
            c.dispose();
        }
        return !seenElement;
    }
    
    public Node _newDomNode(XmlOptions options) {
        if (XmlOptions.hasOption(options, "SAVE_INNER")) {
            options = new XmlOptions(options);
            options.remove("SAVE_INNER");
        }
        return new DomSaver(this._cur, this.isDomFragment(), options).saveDom();
    }
    
    public boolean _toCursor(final Cursor other) {
        assert this._cur._locale == other._cur._locale;
        this._cur.moveToCur(other._cur);
        return true;
    }
    
    public void _push() {
        this._cur.push();
    }
    
    public boolean _pop() {
        return this._cur.pop();
    }
    
    @Override
    public void notifyChange() {
        if (this._cur != null) {
            this._getSelectionCount();
        }
    }
    
    @Override
    public void setNextChangeListener(final Locale.ChangeListener listener) {
        this._nextChangeListener = listener;
    }
    
    @Override
    public Locale.ChangeListener getNextChangeListener() {
        return this._nextChangeListener;
    }
    
    public void _selectPath(final String path) {
        this._selectPath(path, null);
    }
    
    public void _selectPath(final String pathExpr, final XmlOptions options) {
        this._clearSelections();
        assert this._pathEngine == null;
        this._pathEngine = Path.getCompiledPath(pathExpr, options).execute(this._cur, options);
        this._cur._locale.registerForChange(this);
    }
    
    public boolean _hasNextSelection() {
        final int curr = this._currentSelection;
        this.push();
        try {
            return this._toNextSelection();
        }
        finally {
            this._currentSelection = curr;
            this.pop();
        }
    }
    
    public boolean _toNextSelection() {
        return this._toSelection(this._currentSelection + 1);
    }
    
    public boolean _toSelection(final int i) {
        if (i < 0) {
            return false;
        }
        while (i >= this._cur.selectionCount()) {
            if (this._pathEngine == null) {
                return false;
            }
            if (!this._pathEngine.next(this._cur)) {
                this._pathEngine.release();
                this._pathEngine = null;
                return false;
            }
        }
        this._cur.moveToSelection(this._currentSelection = i);
        return true;
    }
    
    public int _getSelectionCount() {
        this._toSelection(Integer.MAX_VALUE);
        return this._cur.selectionCount();
    }
    
    public void _addToSelection() {
        this._toSelection(Integer.MAX_VALUE);
        this._cur.addToSelection();
    }
    
    public void _clearSelections() {
        if (this._pathEngine != null) {
            this._pathEngine.release();
            this._pathEngine = null;
        }
        this._cur.clearSelection();
        this._currentSelection = -1;
    }
    
    public String _namespaceForPrefix(final String prefix) {
        if (!this._cur.isContainer()) {
            throw new IllegalStateException("Not on a container");
        }
        return this._cur.namespaceForPrefix(prefix, true);
    }
    
    public String _prefixForNamespace(final String ns) {
        if (ns == null || ns.length() == 0) {
            throw new IllegalArgumentException("Must specify a namespace");
        }
        return this._cur.prefixForNamespace(ns, null, true);
    }
    
    public void _getAllNamespaces(final Map addToThis) {
        if (!this._cur.isContainer()) {
            throw new IllegalStateException("Not on a container");
        }
        if (addToThis != null) {
            Locale.getAllNamespaces(this._cur, addToThis);
        }
    }
    
    public XmlObject _getObject() {
        return this._cur.getObject();
    }
    
    public TokenType _prevTokenType() {
        this._cur.push();
        final TokenType tt = this._toPrevToken();
        this._cur.pop();
        return tt;
    }
    
    public boolean _hasNextToken() {
        return this._cur._pos != -1 || this._cur._xobj.kind() != 1;
    }
    
    public boolean _hasPrevToken() {
        return this._cur.kind() != 1;
    }
    
    public TokenType _toFirstContentToken() {
        if (!this._cur.isContainer()) {
            return TokenType.NONE;
        }
        this._cur.next();
        return this.currentTokenType();
    }
    
    public TokenType _toEndToken() {
        if (!this._cur.isContainer()) {
            return TokenType.NONE;
        }
        this._cur.toEnd();
        return this.currentTokenType();
    }
    
    public boolean _toChild(final String local) {
        return this._toChild(null, local);
    }
    
    public boolean _toChild(final QName name) {
        return this._toChild(name, 0);
    }
    
    public boolean _toChild(final int index) {
        return this._toChild(null, index);
    }
    
    public boolean _toChild(final String uri, final String local) {
        validateLocalName(local);
        return this._toChild(this._cur._locale.makeQName(uri, local), 0);
    }
    
    public boolean _toChild(final QName name, final int index) {
        return Locale.toChild(this._cur, name, index);
    }
    
    public int _toNextChar(final int maxCharacterCount) {
        return this._cur.nextChars(maxCharacterCount);
    }
    
    public int _toPrevChar(final int maxCharacterCount) {
        return this._cur.prevChars(maxCharacterCount);
    }
    
    public boolean _toPrevSibling() {
        return Locale.toPrevSiblingElement(this._cur);
    }
    
    public boolean _toLastChild() {
        return Locale.toLastChildElement(this._cur);
    }
    
    public boolean _toFirstChild() {
        return Locale.toFirstChildElement(this._cur);
    }
    
    public boolean _toNextSibling(final String name) {
        return this._toNextSibling(new QName(name));
    }
    
    public boolean _toNextSibling(final String uri, final String local) {
        validateLocalName(local);
        return this._toNextSibling(this._cur._locale._qnameFactory.getQName(uri, local));
    }
    
    public boolean _toNextSibling(final QName name) {
        this._cur.push();
        while (this.___toNextSibling()) {
            if (this._cur.getName().equals(name)) {
                this._cur.popButStay();
                return true;
            }
        }
        this._cur.pop();
        return false;
    }
    
    public boolean _toFirstAttribute() {
        return this._cur.isContainer() && Locale.toFirstNormalAttr(this._cur);
    }
    
    public boolean _toLastAttribute() {
        if (this._cur.isContainer()) {
            this._cur.push();
            this._cur.push();
            boolean foundAttr = false;
            while (this._cur.toNextAttr()) {
                if (this._cur.isNormalAttr()) {
                    this._cur.popButStay();
                    this._cur.push();
                    foundAttr = true;
                }
            }
            this._cur.pop();
            if (foundAttr) {
                this._cur.popButStay();
                return true;
            }
            this._cur.pop();
        }
        return false;
    }
    
    public boolean _toNextAttribute() {
        return this._cur.isAttr() && Locale.toNextNormalAttr(this._cur);
    }
    
    public boolean _toPrevAttribute() {
        return this._cur.isAttr() && Locale.toPrevNormalAttr(this._cur);
    }
    
    public String _getAttributeText(final QName attrName) {
        if (attrName == null) {
            throw new IllegalArgumentException("Attr name is null");
        }
        if (!this._cur.isContainer()) {
            return null;
        }
        return this._cur.getAttrValue(attrName);
    }
    
    public boolean _setAttributeText(final QName attrName, final String value) {
        if (attrName == null) {
            throw new IllegalArgumentException("Attr name is null");
        }
        validateLocalName(attrName.getLocalPart());
        if (!this._cur.isContainer()) {
            return false;
        }
        this._cur.setAttrValue(attrName, value);
        return true;
    }
    
    public boolean _removeAttribute(final QName attrName) {
        if (attrName == null) {
            throw new IllegalArgumentException("Attr name is null");
        }
        return this._cur.isContainer() && this._cur.removeAttr(attrName);
    }
    
    public String _getTextValue() {
        if (this._cur.isText()) {
            return this._getChars();
        }
        if (!this._cur.isNode()) {
            throw new IllegalStateException("Can't get text value, current token can have no text value");
        }
        return this._cur.hasChildren() ? Locale.getTextValue(this._cur) : this._cur.getValueAsString();
    }
    
    public int _getTextValue(final char[] chars, final int offset, int max) {
        if (this._cur.isText()) {
            return this._getChars(chars, offset, max);
        }
        if (chars == null) {
            throw new IllegalArgumentException("char buffer is null");
        }
        if (offset < 0) {
            throw new IllegalArgumentException("offset < 0");
        }
        if (offset >= chars.length) {
            throw new IllegalArgumentException("offset off end");
        }
        if (max < 0) {
            max = Integer.MAX_VALUE;
        }
        if (offset + max > chars.length) {
            max = chars.length - offset;
        }
        if (!this._cur.isNode()) {
            throw new IllegalStateException("Can't get text value, current token can have no text value");
        }
        if (this._cur.hasChildren()) {
            return Locale.getTextValue(this._cur, 1, chars, offset, max);
        }
        final Object src = this._cur.getFirstChars();
        if (this._cur._cchSrc > max) {
            this._cur._cchSrc = max;
        }
        if (this._cur._cchSrc <= 0) {
            return 0;
        }
        CharUtil.getChars(chars, offset, src, this._cur._offSrc, this._cur._cchSrc);
        return this._cur._cchSrc;
    }
    
    private void setTextValue(final Object src, final int off, final int cch) {
        if (!this._cur.isNode()) {
            throw new IllegalStateException("Can't set text value, current token can have no text value");
        }
        this._cur.moveNodeContents(null, false);
        this._cur.next();
        this._cur.insertChars(src, off, cch);
        this._cur.toParent();
    }
    
    public void _setTextValue(String text) {
        if (text == null) {
            text = "";
        }
        this.setTextValue(text, 0, text.length());
    }
    
    public void _setTextValue(final char[] sourceChars, final int offset, int length) {
        if (length < 0) {
            throw new IndexOutOfBoundsException("setTextValue: length < 0");
        }
        if (sourceChars == null) {
            if (length > 0) {
                throw new IllegalArgumentException("setTextValue: sourceChars == null");
            }
            this.setTextValue(null, 0, 0);
        }
        else {
            if (offset < 0 || offset >= sourceChars.length) {
                throw new IndexOutOfBoundsException("setTextValue: offset out of bounds");
            }
            if (offset + length > sourceChars.length) {
                length = sourceChars.length - offset;
            }
            final CharUtil cu = this._cur._locale.getCharUtil();
            this.setTextValue(cu.saveChars(sourceChars, offset, length), cu._offSrc, cu._cchSrc);
        }
    }
    
    public String _getChars() {
        return this._cur.getCharsAsString(-1);
    }
    
    public int _getChars(final char[] buf, final int off, int cch) {
        final int cchRight = this._cur.cchRight();
        if (cch < 0 || cch > cchRight) {
            cch = cchRight;
        }
        if (buf == null || off >= buf.length) {
            return 0;
        }
        if (buf.length - off < cch) {
            cch = buf.length - off;
        }
        final Object src = this._cur.getChars(cch);
        CharUtil.getChars(buf, off, src, this._cur._offSrc, this._cur._cchSrc);
        return this._cur._cchSrc;
    }
    
    public void _toStartDoc() {
        this._cur.toRoot();
    }
    
    public void _toEndDoc() {
        this._toStartDoc();
        this._cur.toEnd();
    }
    
    public int _comparePosition(final Cursor other) {
        final int s = this._cur.comparePosition(other._cur);
        if (s == 2) {
            throw new IllegalArgumentException("Cursors not in same document");
        }
        assert s >= -1 && s <= 1;
        return s;
    }
    
    public boolean _isLeftOf(final Cursor other) {
        return this._comparePosition(other) < 0;
    }
    
    public boolean _isAtSamePositionAs(final Cursor other) {
        return this._cur.isSamePos(other._cur);
    }
    
    public boolean _isRightOf(final Cursor other) {
        return this._comparePosition(other) > 0;
    }
    
    public XmlCursor _execQuery(final String query) {
        return this._execQuery(query, null);
    }
    
    public XmlCursor _execQuery(final String query, final XmlOptions options) {
        this.checkThisCursor();
        return Query.cursorExecQuery(this._cur, query, options);
    }
    
    public boolean _toBookmark(final XmlBookmark bookmark) {
        if (bookmark == null || !(bookmark._currentMark instanceof Xobj.Bookmark)) {
            return false;
        }
        final Xobj.Bookmark m = (Xobj.Bookmark)bookmark._currentMark;
        if (m._xobj == null || m._xobj._locale != this._cur._locale) {
            return false;
        }
        this._cur.moveTo(m._xobj, m._pos);
        return true;
    }
    
    public XmlBookmark _toNextBookmark(final Object key) {
        if (key == null) {
            return null;
        }
        this._cur.push();
        while (true) {
            int cch;
            if ((cch = this._cur.cchRight()) > 1) {
                this._cur.nextChars(1);
                this._cur.nextChars(((cch = this._cur.firstBookmarkInChars(key, cch - 1)) >= 0) ? cch : -1);
            }
            else if (this._toNextToken().isNone()) {
                this._cur.pop();
                return null;
            }
            final XmlBookmark bm = getBookmark(key, this._cur);
            if (bm != null) {
                this._cur.popButStay();
                return bm;
            }
            if (this._cur.kind() == -1) {
                this._cur.pop();
                return null;
            }
        }
    }
    
    public XmlBookmark _toPrevBookmark(final Object key) {
        if (key == null) {
            return null;
        }
        this._cur.push();
        while (true) {
            int cch;
            if ((cch = this._cur.cchLeft()) > 1) {
                this._cur.prevChars(1);
                this._cur.prevChars(((cch = this._cur.firstBookmarkInCharsLeft(key, cch - 1)) >= 0) ? cch : -1);
            }
            else if (cch == 1) {
                this._cur.prevChars(1);
            }
            else if (this._toPrevToken().isNone()) {
                this._cur.pop();
                return null;
            }
            final XmlBookmark bm = getBookmark(key, this._cur);
            if (bm != null) {
                this._cur.popButStay();
                return bm;
            }
            if (this._cur.kind() == 1) {
                this._cur.pop();
                return null;
            }
        }
    }
    
    public void _setBookmark(final XmlBookmark bookmark) {
        if (bookmark != null) {
            if (bookmark.getKey() == null) {
                throw new IllegalArgumentException("Annotation key is null");
            }
            bookmark._currentMark = this._cur.setBookmark(bookmark.getKey(), bookmark);
        }
    }
    
    static XmlBookmark getBookmark(final Object key, final Cur c) {
        if (key == null) {
            return null;
        }
        final Object bm = c.getBookmark(key);
        return (bm != null && bm instanceof XmlBookmark) ? ((XmlBookmark)bm) : null;
    }
    
    public XmlBookmark _getBookmark(final Object key) {
        return (key == null) ? null : getBookmark(key, this._cur);
    }
    
    public void _clearBookmark(final Object key) {
        if (key != null) {
            this._cur.setBookmark(key, null);
        }
    }
    
    public void _getAllBookmarkRefs(final Collection listToFill) {
        if (listToFill != null) {
            for (Xobj.Bookmark b = this._cur._xobj._bookmarks; b != null; b = b._next) {
                if (b._value instanceof XmlBookmark) {
                    listToFill.add(b._value);
                }
            }
        }
    }
    
    public boolean _removeXml() {
        if (this._cur.isRoot()) {
            throw new IllegalStateException("Can't remove a whole document.");
        }
        if (this._cur.isFinish()) {
            return false;
        }
        assert this._cur.isText() || this._cur.isNode();
        if (this._cur.isText()) {
            this._cur.moveChars(null, -1);
        }
        else {
            this._cur.moveNode(null);
        }
        return true;
    }
    
    public boolean _moveXml(final Cursor to) {
        to.checkInsertionValidity(this._cur);
        if (this._cur.isText()) {
            final int cchRight = this._cur.cchRight();
            assert cchRight > 0;
            if (this._cur.inChars(to._cur, cchRight, true)) {
                return false;
            }
            this._cur.moveChars(to._cur, cchRight);
            to._cur.nextChars(cchRight);
            return true;
        }
        else {
            if (this._cur.contains(to._cur)) {
                return false;
            }
            final Cur c = to.tempCur();
            this._cur.moveNode(to._cur);
            to._cur.moveToCur(c);
            c.release();
            return true;
        }
    }
    
    public boolean _copyXml(final Cursor to) {
        to.checkInsertionValidity(this._cur);
        assert this._cur.isText() || this._cur.isNode();
        final Cur c = to.tempCur();
        if (this._cur.isText()) {
            to._cur.insertChars(this._cur.getChars(-1), this._cur._offSrc, this._cur._cchSrc);
        }
        else {
            this._cur.copyNode(to._cur);
        }
        to._cur.moveToCur(c);
        c.release();
        return true;
    }
    
    public boolean _removeXmlContents() {
        if (!this._cur.isContainer()) {
            return false;
        }
        this._cur.moveNodeContents(null, false);
        return true;
    }
    
    private boolean checkContentInsertionValidity(final Cursor to) {
        this._cur.push();
        this._cur.next();
        if (this._cur.isFinish()) {
            this._cur.pop();
            return false;
        }
        try {
            to.checkInsertionValidity(this._cur);
        }
        catch (final IllegalArgumentException e) {
            this._cur.pop();
            throw e;
        }
        this._cur.pop();
        return true;
    }
    
    public boolean _moveXmlContents(final Cursor to) {
        if (!this._cur.isContainer() || this._cur.contains(to._cur)) {
            return false;
        }
        if (!this.checkContentInsertionValidity(to)) {
            return false;
        }
        final Cur c = to.tempCur();
        this._cur.moveNodeContents(to._cur, false);
        to._cur.moveToCur(c);
        c.release();
        return true;
    }
    
    public boolean _copyXmlContents(final Cursor to) {
        if (!this._cur.isContainer() || this._cur.contains(to._cur)) {
            return false;
        }
        if (!this.checkContentInsertionValidity(to)) {
            return false;
        }
        final Cur c = this._cur._locale.tempCur();
        this._cur.copyNode(c);
        final Cur c2 = to._cur.tempCur();
        c.moveNodeContents(to._cur, false);
        c.release();
        to._cur.moveToCur(c2);
        c2.release();
        return true;
    }
    
    public int _removeChars(int cch) {
        final int cchRight = this._cur.cchRight();
        if (cchRight == 0 || cch == 0) {
            return 0;
        }
        if (cch < 0 || cch > cchRight) {
            cch = cchRight;
        }
        this._cur.moveChars(null, cch);
        return this._cur._cchSrc;
    }
    
    public int _moveChars(int cch, final Cursor to) {
        final int cchRight = this._cur.cchRight();
        if (cchRight <= 0 || cch == 0) {
            return 0;
        }
        if (cch < 0 || cch > cchRight) {
            cch = cchRight;
        }
        to.checkInsertionValidity(this._cur);
        this._cur.moveChars(to._cur, cch);
        to._cur.nextChars(this._cur._cchSrc);
        return this._cur._cchSrc;
    }
    
    public int _copyChars(int cch, final Cursor to) {
        final int cchRight = this._cur.cchRight();
        if (cchRight <= 0 || cch == 0) {
            return 0;
        }
        if (cch < 0 || cch > cchRight) {
            cch = cchRight;
        }
        to.checkInsertionValidity(this._cur);
        to._cur.insertChars(this._cur.getChars(cch), this._cur._offSrc, this._cur._cchSrc);
        to._cur.nextChars(this._cur._cchSrc);
        return this._cur._cchSrc;
    }
    
    public void _insertChars(final String text) {
        final int l = (text == null) ? 0 : text.length();
        if (l > 0) {
            if (this._cur.isRoot() || this._cur.isAttr()) {
                throw new IllegalStateException("Can't insert before the document or an attribute.");
            }
            this._cur.insertChars(text, 0, l);
            this._cur.nextChars(l);
        }
    }
    
    public void _beginElement(final String localName) {
        this._insertElementWithText(localName, null, null);
        this._toPrevToken();
    }
    
    public void _beginElement(final String localName, final String uri) {
        this._insertElementWithText(localName, uri, null);
        this._toPrevToken();
    }
    
    public void _beginElement(final QName name) {
        this._insertElementWithText(name, null);
        this._toPrevToken();
    }
    
    public void _insertElement(final String localName) {
        this._insertElementWithText(localName, null, null);
    }
    
    public void _insertElement(final String localName, final String uri) {
        this._insertElementWithText(localName, uri, null);
    }
    
    public void _insertElement(final QName name) {
        this._insertElementWithText(name, null);
    }
    
    public void _insertElementWithText(final String localName, final String text) {
        this._insertElementWithText(localName, null, text);
    }
    
    public void _insertElementWithText(final String localName, final String uri, final String text) {
        validateLocalName(localName);
        this._insertElementWithText(this._cur._locale.makeQName(uri, localName), text);
    }
    
    public void _insertElementWithText(final QName name, final String text) {
        validateLocalName(name.getLocalPart());
        final Cur c = this._cur._locale.tempCur();
        c.createElement(name);
        this.insertNode(c, text);
        c.release();
    }
    
    public void _insertAttribute(final String localName) {
        this._insertAttributeWithValue(localName, null);
    }
    
    public void _insertAttribute(final String localName, final String uri) {
        this._insertAttributeWithValue(localName, uri, null);
    }
    
    public void _insertAttribute(final QName name) {
        this._insertAttributeWithValue(name, null);
    }
    
    public void _insertAttributeWithValue(final String localName, final String value) {
        this._insertAttributeWithValue(localName, null, value);
    }
    
    public void _insertAttributeWithValue(final String localName, final String uri, final String value) {
        validateLocalName(localName);
        this._insertAttributeWithValue(this._cur._locale.makeQName(uri, localName), value);
    }
    
    public void _insertAttributeWithValue(final QName name, final String text) {
        validateLocalName(name.getLocalPart());
        final Cur c = this._cur._locale.tempCur();
        c.createAttr(name);
        this.insertNode(c, text);
        c.release();
    }
    
    public void _insertNamespace(final String prefix, final String namespace) {
        this._insertAttributeWithValue(this._cur._locale.createXmlns(prefix), namespace);
    }
    
    public void _insertComment(final String text) {
        final Cur c = this._cur._locale.tempCur();
        c.createComment();
        this.insertNode(c, text);
        c.release();
    }
    
    public void _insertProcInst(final String target, final String text) {
        validateLocalName(target);
        if (Locale.beginsWithXml(target) && target.length() == 3) {
            throw new IllegalArgumentException("Target is 'xml'");
        }
        final Cur c = this._cur._locale.tempCur();
        c.createProcinst(target);
        this.insertNode(c, text);
        c.release();
    }
    
    public void _dump() {
        this._cur.dump();
    }
    
    private void checkThisCursor() {
        if (this._cur == null) {
            throw new IllegalStateException("This cursor has been disposed");
        }
    }
    
    private Cursor checkCursors(final XmlCursor xOther) {
        this.checkThisCursor();
        if (xOther == null) {
            throw new IllegalArgumentException("Other cursor is <null>");
        }
        if (!(xOther instanceof Cursor)) {
            throw new IllegalArgumentException("Incompatible cursors: " + xOther);
        }
        final Cursor other = (Cursor)xOther;
        if (other._cur == null) {
            throw new IllegalStateException("Other cursor has been disposed");
        }
        return other;
    }
    
    private int twoLocaleOp(final XmlCursor xOther, final int op, final int arg) {
        final Cursor other = this.checkCursors(xOther);
        final Locale locale = this._cur._locale;
        final Locale otherLocale = other._cur._locale;
        if (locale == otherLocale) {
            if (locale.noSync()) {
                return this.twoLocaleOp(other, op, arg);
            }
            synchronized (locale) {
                return this.twoLocaleOp(other, op, arg);
            }
        }
        if (locale.noSync()) {
            if (otherLocale.noSync()) {
                return this.twoLocaleOp(other, op, arg);
            }
            synchronized (otherLocale) {
                return this.twoLocaleOp(other, op, arg);
            }
        }
        if (otherLocale.noSync()) {
            synchronized (locale) {
                return this.twoLocaleOp(other, op, arg);
            }
        }
        boolean acquired = false;
        try {
            GlobalLock.acquire();
            acquired = true;
            synchronized (locale) {
                synchronized (otherLocale) {
                    GlobalLock.release();
                    acquired = false;
                    return this.twoLocaleOp(other, op, arg);
                }
            }
        }
        catch (final InterruptedException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        finally {
            if (acquired) {
                GlobalLock.release();
            }
        }
    }
    
    private int twoLocaleOp(final Cursor other, final int op, final int arg) {
        final Locale locale = this._cur._locale;
        final Locale otherLocale = other._cur._locale;
        locale.enter(otherLocale);
        try {
            switch (op) {
                case 0: {
                    return this._moveXml(other) ? 1 : 0;
                }
                case 1: {
                    return this._copyXml(other) ? 1 : 0;
                }
                case 2: {
                    return this._moveXmlContents(other) ? 1 : 0;
                }
                case 3: {
                    return this._copyXmlContents(other) ? 1 : 0;
                }
                case 4: {
                    return this._moveChars(arg, other);
                }
                case 5: {
                    return this._copyChars(arg, other);
                }
                default: {
                    throw new RuntimeException("Unknown operation: " + op);
                }
            }
        }
        finally {
            locale.exit(otherLocale);
        }
    }
    
    @Override
    public boolean moveXml(final XmlCursor xTo) {
        return this.twoLocaleOp(xTo, 0, 0) == 1;
    }
    
    @Override
    public boolean copyXml(final XmlCursor xTo) {
        return this.twoLocaleOp(xTo, 1, 0) == 1;
    }
    
    @Override
    public boolean moveXmlContents(final XmlCursor xTo) {
        return this.twoLocaleOp(xTo, 2, 0) == 1;
    }
    
    @Override
    public boolean copyXmlContents(final XmlCursor xTo) {
        return this.twoLocaleOp(xTo, 3, 0) == 1;
    }
    
    @Override
    public int moveChars(final int cch, final XmlCursor xTo) {
        return this.twoLocaleOp(xTo, 4, cch);
    }
    
    @Override
    public int copyChars(final int cch, final XmlCursor xTo) {
        return this.twoLocaleOp(xTo, 5, cch);
    }
    
    @Override
    public boolean toCursor(final XmlCursor xOther) {
        final Cursor other = this.checkCursors(xOther);
        if (this._cur._locale != other._cur._locale) {
            return false;
        }
        if (this._cur._locale.noSync()) {
            this._cur._locale.enter();
            try {
                return this._toCursor(other);
            }
            finally {
                this._cur._locale.exit();
            }
        }
        synchronized (this._cur._locale) {
            this._cur._locale.enter();
            try {
                return this._toCursor(other);
            }
            finally {
                this._cur._locale.exit();
            }
        }
    }
    
    @Override
    public boolean isInSameDocument(final XmlCursor xOther) {
        return xOther != null && this._cur.isInSameTree(this.checkCursors(xOther)._cur);
    }
    
    private Cursor preCheck(final XmlCursor xOther) {
        final Cursor other = this.checkCursors(xOther);
        if (this._cur._locale != other._cur._locale) {
            throw new IllegalArgumentException("Cursors not in same document");
        }
        return other;
    }
    
    @Override
    public int comparePosition(final XmlCursor xOther) {
        final Cursor other = this.preCheck(xOther);
        if (this._cur._locale.noSync()) {
            this._cur._locale.enter();
            try {
                return this._comparePosition(other);
            }
            finally {
                this._cur._locale.exit();
            }
        }
        synchronized (this._cur._locale) {
            this._cur._locale.enter();
            try {
                return this._comparePosition(other);
            }
            finally {
                this._cur._locale.exit();
            }
        }
    }
    
    @Override
    public boolean isLeftOf(final XmlCursor xOther) {
        final Cursor other = this.preCheck(xOther);
        if (this._cur._locale.noSync()) {
            this._cur._locale.enter();
            try {
                return this._isLeftOf(other);
            }
            finally {
                this._cur._locale.exit();
            }
        }
        synchronized (this._cur._locale) {
            this._cur._locale.enter();
            try {
                return this._isLeftOf(other);
            }
            finally {
                this._cur._locale.exit();
            }
        }
    }
    
    @Override
    public boolean isAtSamePositionAs(final XmlCursor xOther) {
        final Cursor other = this.preCheck(xOther);
        if (this._cur._locale.noSync()) {
            this._cur._locale.enter();
            try {
                return this._isAtSamePositionAs(other);
            }
            finally {
                this._cur._locale.exit();
            }
        }
        synchronized (this._cur._locale) {
            this._cur._locale.enter();
            try {
                return this._isAtSamePositionAs(other);
            }
            finally {
                this._cur._locale.exit();
            }
        }
    }
    
    @Override
    public boolean isRightOf(final XmlCursor xOther) {
        final Cursor other = this.preCheck(xOther);
        if (this._cur._locale.noSync()) {
            this._cur._locale.enter();
            try {
                return this._isRightOf(other);
            }
            finally {
                this._cur._locale.exit();
            }
        }
        synchronized (this._cur._locale) {
            this._cur._locale.enter();
            try {
                return this._isRightOf(other);
            }
            finally {
                this._cur._locale.exit();
            }
        }
    }
    
    public static XmlCursor newCursor(final Xobj x, final int p) {
        final Locale l = x._locale;
        if (l.noSync()) {
            l.enter();
            try {
                return new Cursor(x, p);
            }
            finally {
                l.exit();
            }
        }
        synchronized (l) {
            l.enter();
            try {
                return new Cursor(x, p);
            }
            finally {
                l.exit();
            }
        }
    }
    
    private boolean preCheck() {
        this.checkThisCursor();
        return this._cur._locale.noSync();
    }
    
    @Override
    public void dispose() {
        if (this._cur != null) {
            final Locale l = this._cur._locale;
            if (this.preCheck()) {
                l.enter();
                try {
                    this._dispose();
                }
                finally {
                    l.exit();
                }
            }
            else {
                synchronized (l) {
                    l.enter();
                    try {
                        this._dispose();
                    }
                    finally {
                        l.exit();
                    }
                }
            }
        }
    }
    
    @Override
    public Object monitor() {
        if (this.preCheck()) {
            this._cur._locale.enter();
            try {
                return this._monitor();
            }
            finally {
                this._cur._locale.exit();
            }
        }
        synchronized (this._cur._locale) {
            this._cur._locale.enter();
            try {
                return this._monitor();
            }
            finally {
                this._cur._locale.exit();
            }
        }
    }
    
    @Override
    public XmlDocumentProperties documentProperties() {
        if (this.preCheck()) {
            this._cur._locale.enter();
            try {
                return this._documentProperties();
            }
            finally {
                this._cur._locale.exit();
            }
        }
        synchronized (this._cur._locale) {
            this._cur._locale.enter();
            try {
                return this._documentProperties();
            }
            finally {
                this._cur._locale.exit();
            }
        }
    }
    
    @Override
    public XmlCursor newCursor() {
        if (this.preCheck()) {
            this._cur._locale.enter();
            try {
                return this._newCursor();
            }
            finally {
                this._cur._locale.exit();
            }
        }
        synchronized (this._cur._locale) {
            this._cur._locale.enter();
            try {
                return this._newCursor();
            }
            finally {
                this._cur._locale.exit();
            }
        }
    }
    
    @Override
    public XMLStreamReader newXMLStreamReader() {
        if (this.preCheck()) {
            this._cur._locale.enter();
            try {
                return this._newXMLStreamReader();
            }
            finally {
                this._cur._locale.exit();
            }
        }
        synchronized (this._cur._locale) {
            this._cur._locale.enter();
            try {
                return this._newXMLStreamReader();
            }
            finally {
                this._cur._locale.exit();
            }
        }
    }
    
    @Override
    public XMLStreamReader newXMLStreamReader(final XmlOptions options) {
        if (this.preCheck()) {
            this._cur._locale.enter();
            try {
                return this._newXMLStreamReader(options);
            }
            finally {
                this._cur._locale.exit();
            }
        }
        synchronized (this._cur._locale) {
            this._cur._locale.enter();
            try {
                return this._newXMLStreamReader(options);
            }
            finally {
                this._cur._locale.exit();
            }
        }
    }
    
    @Override
    @Deprecated
    public XMLInputStream newXMLInputStream() {
        if (this.preCheck()) {
            this._cur._locale.enter();
            try {
                return this._newXMLInputStream();
            }
            finally {
                this._cur._locale.exit();
            }
        }
        synchronized (this._cur._locale) {
            this._cur._locale.enter();
            try {
                return this._newXMLInputStream();
            }
            finally {
                this._cur._locale.exit();
            }
        }
    }
    
    @Override
    public String xmlText() {
        if (this.preCheck()) {
            this._cur._locale.enter();
            try {
                return this._xmlText();
            }
            finally {
                this._cur._locale.exit();
            }
        }
        synchronized (this._cur._locale) {
            this._cur._locale.enter();
            try {
                return this._xmlText();
            }
            finally {
                this._cur._locale.exit();
            }
        }
    }
    
    @Override
    public InputStream newInputStream() {
        if (this.preCheck()) {
            this._cur._locale.enter();
            try {
                return this._newInputStream();
            }
            finally {
                this._cur._locale.exit();
            }
        }
        synchronized (this._cur._locale) {
            this._cur._locale.enter();
            try {
                return this._newInputStream();
            }
            finally {
                this._cur._locale.exit();
            }
        }
    }
    
    @Override
    public Reader newReader() {
        if (this.preCheck()) {
            this._cur._locale.enter();
            try {
                return this._newReader();
            }
            finally {
                this._cur._locale.exit();
            }
        }
        synchronized (this._cur._locale) {
            this._cur._locale.enter();
            try {
                return this._newReader();
            }
            finally {
                this._cur._locale.exit();
            }
        }
    }
    
    @Override
    public Node newDomNode() {
        if (this.preCheck()) {
            this._cur._locale.enter();
            try {
                return this._newDomNode();
            }
            finally {
                this._cur._locale.exit();
            }
        }
        synchronized (this._cur._locale) {
            this._cur._locale.enter();
            try {
                return this._newDomNode();
            }
            finally {
                this._cur._locale.exit();
            }
        }
    }
    
    @Override
    public Node getDomNode() {
        if (this.preCheck()) {
            this._cur._locale.enter();
            try {
                return this._getDomNode();
            }
            finally {
                this._cur._locale.exit();
            }
        }
        synchronized (this._cur._locale) {
            this._cur._locale.enter();
            try {
                return this._getDomNode();
            }
            finally {
                this._cur._locale.exit();
            }
        }
    }
    
    @Override
    public void save(final ContentHandler ch, final LexicalHandler lh) throws SAXException {
        if (this.preCheck()) {
            this._cur._locale.enter();
            try {
                this._save(ch, lh);
            }
            finally {
                this._cur._locale.exit();
            }
        }
        else {
            synchronized (this._cur._locale) {
                this._cur._locale.enter();
                try {
                    this._save(ch, lh);
                }
                finally {
                    this._cur._locale.exit();
                }
            }
        }
    }
    
    @Override
    public void save(final File file) throws IOException {
        if (this.preCheck()) {
            this._cur._locale.enter();
            try {
                this._save(file);
            }
            finally {
                this._cur._locale.exit();
            }
        }
        else {
            synchronized (this._cur._locale) {
                this._cur._locale.enter();
                try {
                    this._save(file);
                }
                finally {
                    this._cur._locale.exit();
                }
            }
        }
    }
    
    @Override
    public void save(final OutputStream os) throws IOException {
        if (this.preCheck()) {
            this._cur._locale.enter();
            try {
                this._save(os);
            }
            finally {
                this._cur._locale.exit();
            }
        }
        else {
            synchronized (this._cur._locale) {
                this._cur._locale.enter();
                try {
                    this._save(os);
                }
                finally {
                    this._cur._locale.exit();
                }
            }
        }
    }
    
    @Override
    public void save(final Writer w) throws IOException {
        if (this.preCheck()) {
            this._cur._locale.enter();
            try {
                this._save(w);
            }
            finally {
                this._cur._locale.exit();
            }
        }
        else {
            synchronized (this._cur._locale) {
                this._cur._locale.enter();
                try {
                    this._save(w);
                }
                finally {
                    this._cur._locale.exit();
                }
            }
        }
    }
    
    @Override
    @Deprecated
    public XMLInputStream newXMLInputStream(final XmlOptions options) {
        if (this.preCheck()) {
            this._cur._locale.enter();
            try {
                return this._newXMLInputStream(options);
            }
            finally {
                this._cur._locale.exit();
            }
        }
        synchronized (this._cur._locale) {
            this._cur._locale.enter();
            try {
                return this._newXMLInputStream(options);
            }
            finally {
                this._cur._locale.exit();
            }
        }
    }
    
    @Override
    public String xmlText(final XmlOptions options) {
        if (this.preCheck()) {
            this._cur._locale.enter();
            try {
                return this._xmlText(options);
            }
            finally {
                this._cur._locale.exit();
            }
        }
        synchronized (this._cur._locale) {
            this._cur._locale.enter();
            try {
                return this._xmlText(options);
            }
            finally {
                this._cur._locale.exit();
            }
        }
    }
    
    @Override
    public InputStream newInputStream(final XmlOptions options) {
        if (this.preCheck()) {
            this._cur._locale.enter();
            try {
                return this._newInputStream(options);
            }
            finally {
                this._cur._locale.exit();
            }
        }
        synchronized (this._cur._locale) {
            this._cur._locale.enter();
            try {
                return this._newInputStream(options);
            }
            finally {
                this._cur._locale.exit();
            }
        }
    }
    
    @Override
    public Reader newReader(final XmlOptions options) {
        if (this.preCheck()) {
            this._cur._locale.enter();
            try {
                return this._newReader(options);
            }
            finally {
                this._cur._locale.exit();
            }
        }
        synchronized (this._cur._locale) {
            this._cur._locale.enter();
            try {
                return this._newReader(options);
            }
            finally {
                this._cur._locale.exit();
            }
        }
    }
    
    @Override
    public Node newDomNode(final XmlOptions options) {
        if (this.preCheck()) {
            this._cur._locale.enter();
            try {
                return this._newDomNode(options);
            }
            finally {
                this._cur._locale.exit();
            }
        }
        synchronized (this._cur._locale) {
            this._cur._locale.enter();
            try {
                return this._newDomNode(options);
            }
            finally {
                this._cur._locale.exit();
            }
        }
    }
    
    @Override
    public void save(final ContentHandler ch, final LexicalHandler lh, final XmlOptions options) throws SAXException {
        if (this.preCheck()) {
            this._cur._locale.enter();
            try {
                this._save(ch, lh, options);
            }
            finally {
                this._cur._locale.exit();
            }
        }
        else {
            synchronized (this._cur._locale) {
                this._cur._locale.enter();
                try {
                    this._save(ch, lh, options);
                }
                finally {
                    this._cur._locale.exit();
                }
            }
        }
    }
    
    @Override
    public void save(final File file, final XmlOptions options) throws IOException {
        if (this.preCheck()) {
            this._cur._locale.enter();
            try {
                this._save(file, options);
            }
            finally {
                this._cur._locale.exit();
            }
        }
        else {
            synchronized (this._cur._locale) {
                this._cur._locale.enter();
                try {
                    this._save(file, options);
                }
                finally {
                    this._cur._locale.exit();
                }
            }
        }
    }
    
    @Override
    public void save(final OutputStream os, final XmlOptions options) throws IOException {
        if (this.preCheck()) {
            this._cur._locale.enter();
            try {
                this._save(os, options);
            }
            finally {
                this._cur._locale.exit();
            }
        }
        else {
            synchronized (this._cur._locale) {
                this._cur._locale.enter();
                try {
                    this._save(os, options);
                }
                finally {
                    this._cur._locale.exit();
                }
            }
        }
    }
    
    @Override
    public void save(final Writer w, final XmlOptions options) throws IOException {
        if (this.preCheck()) {
            this._cur._locale.enter();
            try {
                this._save(w, options);
            }
            finally {
                this._cur._locale.exit();
            }
        }
        else {
            synchronized (this._cur._locale) {
                this._cur._locale.enter();
                try {
                    this._save(w, options);
                }
                finally {
                    this._cur._locale.exit();
                }
            }
        }
    }
    
    @Override
    public void push() {
        if (this.preCheck()) {
            this._cur._locale.enter();
            try {
                this._push();
            }
            finally {
                this._cur._locale.exit();
            }
        }
        else {
            synchronized (this._cur._locale) {
                this._cur._locale.enter();
                try {
                    this._push();
                }
                finally {
                    this._cur._locale.exit();
                }
            }
        }
    }
    
    @Override
    public boolean pop() {
        if (this.preCheck()) {
            this._cur._locale.enter();
            try {
                return this._pop();
            }
            finally {
                this._cur._locale.exit();
            }
        }
        synchronized (this._cur._locale) {
            this._cur._locale.enter();
            try {
                return this._pop();
            }
            finally {
                this._cur._locale.exit();
            }
        }
    }
    
    @Override
    public void selectPath(final String path) {
        if (this.preCheck()) {
            this._cur._locale.enter();
            try {
                this._selectPath(path);
            }
            finally {
                this._cur._locale.exit();
            }
        }
        else {
            synchronized (this._cur._locale) {
                this._cur._locale.enter();
                try {
                    this._selectPath(path);
                }
                finally {
                    this._cur._locale.exit();
                }
            }
        }
    }
    
    @Override
    public void selectPath(final String path, final XmlOptions options) {
        if (this.preCheck()) {
            this._cur._locale.enter();
            try {
                this._selectPath(path, options);
            }
            finally {
                this._cur._locale.exit();
            }
        }
        else {
            synchronized (this._cur._locale) {
                this._cur._locale.enter();
                try {
                    this._selectPath(path, options);
                }
                finally {
                    this._cur._locale.exit();
                }
            }
        }
    }
    
    @Override
    public boolean hasNextSelection() {
        if (this.preCheck()) {
            this._cur._locale.enter();
            try {
                return this._hasNextSelection();
            }
            finally {
                this._cur._locale.exit();
            }
        }
        synchronized (this._cur._locale) {
            this._cur._locale.enter();
            try {
                return this._hasNextSelection();
            }
            finally {
                this._cur._locale.exit();
            }
        }
    }
    
    @Override
    public boolean toNextSelection() {
        if (this.preCheck()) {
            this._cur._locale.enter();
            try {
                return this._toNextSelection();
            }
            finally {
                this._cur._locale.exit();
            }
        }
        synchronized (this._cur._locale) {
            this._cur._locale.enter();
            try {
                return this._toNextSelection();
            }
            finally {
                this._cur._locale.exit();
            }
        }
    }
    
    @Override
    public boolean toSelection(final int i) {
        if (this.preCheck()) {
            this._cur._locale.enter();
            try {
                return this._toSelection(i);
            }
            finally {
                this._cur._locale.exit();
            }
        }
        synchronized (this._cur._locale) {
            this._cur._locale.enter();
            try {
                return this._toSelection(i);
            }
            finally {
                this._cur._locale.exit();
            }
        }
    }
    
    @Override
    public int getSelectionCount() {
        if (this.preCheck()) {
            this._cur._locale.enter();
            try {
                return this._getSelectionCount();
            }
            finally {
                this._cur._locale.exit();
            }
        }
        synchronized (this._cur._locale) {
            this._cur._locale.enter();
            try {
                return this._getSelectionCount();
            }
            finally {
                this._cur._locale.exit();
            }
        }
    }
    
    @Override
    public void addToSelection() {
        if (this.preCheck()) {
            this._cur._locale.enter();
            try {
                this._addToSelection();
            }
            finally {
                this._cur._locale.exit();
            }
        }
        else {
            synchronized (this._cur._locale) {
                this._cur._locale.enter();
                try {
                    this._addToSelection();
                }
                finally {
                    this._cur._locale.exit();
                }
            }
        }
    }
    
    @Override
    public void clearSelections() {
        if (this.preCheck()) {
            this._cur._locale.enter();
            try {
                this._clearSelections();
            }
            finally {
                this._cur._locale.exit();
            }
        }
        else {
            synchronized (this._cur._locale) {
                this._cur._locale.enter();
                try {
                    this._clearSelections();
                }
                finally {
                    this._cur._locale.exit();
                }
            }
        }
    }
    
    @Override
    public boolean toBookmark(final XmlBookmark bookmark) {
        if (this.preCheck()) {
            this._cur._locale.enter();
            try {
                return this._toBookmark(bookmark);
            }
            finally {
                this._cur._locale.exit();
            }
        }
        synchronized (this._cur._locale) {
            this._cur._locale.enter();
            try {
                return this._toBookmark(bookmark);
            }
            finally {
                this._cur._locale.exit();
            }
        }
    }
    
    @Override
    public XmlBookmark toNextBookmark(final Object key) {
        if (this.preCheck()) {
            this._cur._locale.enter();
            try {
                return this._toNextBookmark(key);
            }
            finally {
                this._cur._locale.exit();
            }
        }
        synchronized (this._cur._locale) {
            this._cur._locale.enter();
            try {
                return this._toNextBookmark(key);
            }
            finally {
                this._cur._locale.exit();
            }
        }
    }
    
    @Override
    public XmlBookmark toPrevBookmark(final Object key) {
        if (this.preCheck()) {
            this._cur._locale.enter();
            try {
                return this._toPrevBookmark(key);
            }
            finally {
                this._cur._locale.exit();
            }
        }
        synchronized (this._cur._locale) {
            this._cur._locale.enter();
            try {
                return this._toPrevBookmark(key);
            }
            finally {
                this._cur._locale.exit();
            }
        }
    }
    
    @Override
    public QName getName() {
        if (this.preCheck()) {
            this._cur._locale.enter();
            try {
                return this._getName();
            }
            finally {
                this._cur._locale.exit();
            }
        }
        synchronized (this._cur._locale) {
            this._cur._locale.enter();
            try {
                return this._getName();
            }
            finally {
                this._cur._locale.exit();
            }
        }
    }
    
    @Override
    public void setName(final QName name) {
        if (this.preCheck()) {
            this._cur._locale.enter();
            try {
                this._setName(name);
            }
            finally {
                this._cur._locale.exit();
            }
        }
        else {
            synchronized (this._cur._locale) {
                this._cur._locale.enter();
                try {
                    this._setName(name);
                }
                finally {
                    this._cur._locale.exit();
                }
            }
        }
    }
    
    @Override
    public String namespaceForPrefix(final String prefix) {
        if (this.preCheck()) {
            this._cur._locale.enter();
            try {
                return this._namespaceForPrefix(prefix);
            }
            finally {
                this._cur._locale.exit();
            }
        }
        synchronized (this._cur._locale) {
            this._cur._locale.enter();
            try {
                return this._namespaceForPrefix(prefix);
            }
            finally {
                this._cur._locale.exit();
            }
        }
    }
    
    @Override
    public String prefixForNamespace(final String namespaceURI) {
        if (this.preCheck()) {
            this._cur._locale.enter();
            try {
                return this._prefixForNamespace(namespaceURI);
            }
            finally {
                this._cur._locale.exit();
            }
        }
        synchronized (this._cur._locale) {
            this._cur._locale.enter();
            try {
                return this._prefixForNamespace(namespaceURI);
            }
            finally {
                this._cur._locale.exit();
            }
        }
    }
    
    @Override
    public void getAllNamespaces(final Map addToThis) {
        if (this.preCheck()) {
            this._cur._locale.enter();
            try {
                this._getAllNamespaces(addToThis);
            }
            finally {
                this._cur._locale.exit();
            }
        }
        else {
            synchronized (this._cur._locale) {
                this._cur._locale.enter();
                try {
                    this._getAllNamespaces(addToThis);
                }
                finally {
                    this._cur._locale.exit();
                }
            }
        }
    }
    
    @Override
    public XmlObject getObject() {
        if (this.preCheck()) {
            this._cur._locale.enter();
            try {
                return this._getObject();
            }
            finally {
                this._cur._locale.exit();
            }
        }
        synchronized (this._cur._locale) {
            this._cur._locale.enter();
            try {
                return this._getObject();
            }
            finally {
                this._cur._locale.exit();
            }
        }
    }
    
    @Override
    public TokenType currentTokenType() {
        if (this.preCheck()) {
            return this._currentTokenType();
        }
        synchronized (this._cur._locale) {
            return this._currentTokenType();
        }
    }
    
    @Override
    public boolean isStartdoc() {
        if (this.preCheck()) {
            return this._isStartdoc();
        }
        synchronized (this._cur._locale) {
            return this._isStartdoc();
        }
    }
    
    @Override
    public boolean isEnddoc() {
        if (this.preCheck()) {
            return this._isEnddoc();
        }
        synchronized (this._cur._locale) {
            return this._isEnddoc();
        }
    }
    
    @Override
    public boolean isStart() {
        if (this.preCheck()) {
            return this._isStart();
        }
        synchronized (this._cur._locale) {
            return this._isStart();
        }
    }
    
    @Override
    public boolean isEnd() {
        if (this.preCheck()) {
            return this._isEnd();
        }
        synchronized (this._cur._locale) {
            return this._isEnd();
        }
    }
    
    @Override
    public boolean isText() {
        if (this.preCheck()) {
            return this._isText();
        }
        synchronized (this._cur._locale) {
            return this._isText();
        }
    }
    
    @Override
    public boolean isAttr() {
        if (this.preCheck()) {
            return this._isAttr();
        }
        synchronized (this._cur._locale) {
            return this._isAttr();
        }
    }
    
    @Override
    public boolean isNamespace() {
        if (this.preCheck()) {
            return this._isNamespace();
        }
        synchronized (this._cur._locale) {
            return this._isNamespace();
        }
    }
    
    @Override
    public boolean isComment() {
        if (this.preCheck()) {
            return this._isComment();
        }
        synchronized (this._cur._locale) {
            return this._isComment();
        }
    }
    
    @Override
    public boolean isProcinst() {
        if (this.preCheck()) {
            return this._isProcinst();
        }
        synchronized (this._cur._locale) {
            return this._isProcinst();
        }
    }
    
    @Override
    public boolean isContainer() {
        if (this.preCheck()) {
            return this._isContainer();
        }
        synchronized (this._cur._locale) {
            return this._isContainer();
        }
    }
    
    @Override
    public boolean isFinish() {
        if (this.preCheck()) {
            return this._isFinish();
        }
        synchronized (this._cur._locale) {
            return this._isFinish();
        }
    }
    
    @Override
    public boolean isAnyAttr() {
        if (this.preCheck()) {
            return this._isAnyAttr();
        }
        synchronized (this._cur._locale) {
            return this._isAnyAttr();
        }
    }
    
    @Override
    public TokenType prevTokenType() {
        if (this.preCheck()) {
            this._cur._locale.enter();
            try {
                return this._prevTokenType();
            }
            finally {
                this._cur._locale.exit();
            }
        }
        synchronized (this._cur._locale) {
            this._cur._locale.enter();
            try {
                return this._prevTokenType();
            }
            finally {
                this._cur._locale.exit();
            }
        }
    }
    
    @Override
    public boolean hasNextToken() {
        if (this.preCheck()) {
            return this._hasNextToken();
        }
        synchronized (this._cur._locale) {
            return this._hasNextToken();
        }
    }
    
    @Override
    public boolean hasPrevToken() {
        if (this.preCheck()) {
            this._cur._locale.enter();
            try {
                return this._hasPrevToken();
            }
            finally {
                this._cur._locale.exit();
            }
        }
        synchronized (this._cur._locale) {
            this._cur._locale.enter();
            try {
                return this._hasPrevToken();
            }
            finally {
                this._cur._locale.exit();
            }
        }
    }
    
    @Override
    public TokenType toNextToken() {
        if (this.preCheck()) {
            this._cur._locale.enter();
            try {
                return this._toNextToken();
            }
            finally {
                this._cur._locale.exit();
            }
        }
        synchronized (this._cur._locale) {
            this._cur._locale.enter();
            try {
                return this._toNextToken();
            }
            finally {
                this._cur._locale.exit();
            }
        }
    }
    
    @Override
    public TokenType toPrevToken() {
        if (this.preCheck()) {
            this._cur._locale.enter();
            try {
                return this._toPrevToken();
            }
            finally {
                this._cur._locale.exit();
            }
        }
        synchronized (this._cur._locale) {
            this._cur._locale.enter();
            try {
                return this._toPrevToken();
            }
            finally {
                this._cur._locale.exit();
            }
        }
    }
    
    @Override
    public TokenType toFirstContentToken() {
        if (this.preCheck()) {
            this._cur._locale.enter();
            try {
                return this._toFirstContentToken();
            }
            finally {
                this._cur._locale.exit();
            }
        }
        synchronized (this._cur._locale) {
            this._cur._locale.enter();
            try {
                return this._toFirstContentToken();
            }
            finally {
                this._cur._locale.exit();
            }
        }
    }
    
    @Override
    public TokenType toEndToken() {
        if (this.preCheck()) {
            this._cur._locale.enter();
            try {
                return this._toEndToken();
            }
            finally {
                this._cur._locale.exit();
            }
        }
        synchronized (this._cur._locale) {
            this._cur._locale.enter();
            try {
                return this._toEndToken();
            }
            finally {
                this._cur._locale.exit();
            }
        }
    }
    
    @Override
    public int toNextChar(final int cch) {
        if (this.preCheck()) {
            this._cur._locale.enter();
            try {
                return this._toNextChar(cch);
            }
            finally {
                this._cur._locale.exit();
            }
        }
        synchronized (this._cur._locale) {
            this._cur._locale.enter();
            try {
                return this._toNextChar(cch);
            }
            finally {
                this._cur._locale.exit();
            }
        }
    }
    
    @Override
    public int toPrevChar(final int cch) {
        if (this.preCheck()) {
            this._cur._locale.enter();
            try {
                return this._toPrevChar(cch);
            }
            finally {
                this._cur._locale.exit();
            }
        }
        synchronized (this._cur._locale) {
            this._cur._locale.enter();
            try {
                return this._toPrevChar(cch);
            }
            finally {
                this._cur._locale.exit();
            }
        }
    }
    
    public boolean ___toNextSibling() {
        if (!this._cur.hasParent()) {
            return false;
        }
        Xobj parent = this._cur.getParentNoRoot();
        if (parent == null) {
            this._cur._locale.enter();
            try {
                parent = this._cur.getParent();
            }
            finally {
                this._cur._locale.exit();
            }
        }
        return Locale.toNextSiblingElement(this._cur, parent);
    }
    
    @Override
    public boolean toNextSibling() {
        if (this.preCheck()) {
            return this.___toNextSibling();
        }
        synchronized (this._cur._locale) {
            return this.___toNextSibling();
        }
    }
    
    @Override
    public boolean toPrevSibling() {
        if (this.preCheck()) {
            this._cur._locale.enter();
            try {
                return this._toPrevSibling();
            }
            finally {
                this._cur._locale.exit();
            }
        }
        synchronized (this._cur._locale) {
            this._cur._locale.enter();
            try {
                return this._toPrevSibling();
            }
            finally {
                this._cur._locale.exit();
            }
        }
    }
    
    @Override
    public boolean toParent() {
        if (this.preCheck()) {
            this._cur._locale.enter();
            try {
                return this._toParent();
            }
            finally {
                this._cur._locale.exit();
            }
        }
        synchronized (this._cur._locale) {
            this._cur._locale.enter();
            try {
                return this._toParent();
            }
            finally {
                this._cur._locale.exit();
            }
        }
    }
    
    @Override
    public boolean toFirstChild() {
        if (this.preCheck()) {
            return this._toFirstChild();
        }
        synchronized (this._cur._locale) {
            return this._toFirstChild();
        }
    }
    
    @Override
    public boolean toLastChild() {
        if (this.preCheck()) {
            this._cur._locale.enter();
            try {
                return this._toLastChild();
            }
            finally {
                this._cur._locale.exit();
            }
        }
        synchronized (this._cur._locale) {
            this._cur._locale.enter();
            try {
                return this._toLastChild();
            }
            finally {
                this._cur._locale.exit();
            }
        }
    }
    
    @Override
    public boolean toChild(final String name) {
        if (this.preCheck()) {
            this._cur._locale.enter();
            try {
                return this._toChild(name);
            }
            finally {
                this._cur._locale.exit();
            }
        }
        synchronized (this._cur._locale) {
            this._cur._locale.enter();
            try {
                return this._toChild(name);
            }
            finally {
                this._cur._locale.exit();
            }
        }
    }
    
    @Override
    public boolean toChild(final String namespace, final String name) {
        if (this.preCheck()) {
            this._cur._locale.enter();
            try {
                return this._toChild(namespace, name);
            }
            finally {
                this._cur._locale.exit();
            }
        }
        synchronized (this._cur._locale) {
            this._cur._locale.enter();
            try {
                return this._toChild(namespace, name);
            }
            finally {
                this._cur._locale.exit();
            }
        }
    }
    
    @Override
    public boolean toChild(final QName name) {
        if (this.preCheck()) {
            this._cur._locale.enter();
            try {
                return this._toChild(name);
            }
            finally {
                this._cur._locale.exit();
            }
        }
        synchronized (this._cur._locale) {
            this._cur._locale.enter();
            try {
                return this._toChild(name);
            }
            finally {
                this._cur._locale.exit();
            }
        }
    }
    
    @Override
    public boolean toChild(final int index) {
        if (this.preCheck()) {
            this._cur._locale.enter();
            try {
                return this._toChild(index);
            }
            finally {
                this._cur._locale.exit();
            }
        }
        synchronized (this._cur._locale) {
            this._cur._locale.enter();
            try {
                return this._toChild(index);
            }
            finally {
                this._cur._locale.exit();
            }
        }
    }
    
    @Override
    public boolean toChild(final QName name, final int index) {
        if (this.preCheck()) {
            this._cur._locale.enter();
            try {
                return this._toChild(name, index);
            }
            finally {
                this._cur._locale.exit();
            }
        }
        synchronized (this._cur._locale) {
            this._cur._locale.enter();
            try {
                return this._toChild(name, index);
            }
            finally {
                this._cur._locale.exit();
            }
        }
    }
    
    @Override
    public boolean toNextSibling(final String name) {
        if (this.preCheck()) {
            this._cur._locale.enter();
            try {
                return this._toNextSibling(name);
            }
            finally {
                this._cur._locale.exit();
            }
        }
        synchronized (this._cur._locale) {
            this._cur._locale.enter();
            try {
                return this._toNextSibling(name);
            }
            finally {
                this._cur._locale.exit();
            }
        }
    }
    
    @Override
    public boolean toNextSibling(final String namespace, final String name) {
        if (this.preCheck()) {
            this._cur._locale.enter();
            try {
                return this._toNextSibling(namespace, name);
            }
            finally {
                this._cur._locale.exit();
            }
        }
        synchronized (this._cur._locale) {
            this._cur._locale.enter();
            try {
                return this._toNextSibling(namespace, name);
            }
            finally {
                this._cur._locale.exit();
            }
        }
    }
    
    @Override
    public boolean toNextSibling(final QName name) {
        if (this.preCheck()) {
            this._cur._locale.enter();
            try {
                return this._toNextSibling(name);
            }
            finally {
                this._cur._locale.exit();
            }
        }
        synchronized (this._cur._locale) {
            this._cur._locale.enter();
            try {
                return this._toNextSibling(name);
            }
            finally {
                this._cur._locale.exit();
            }
        }
    }
    
    @Override
    public boolean toFirstAttribute() {
        if (this.preCheck()) {
            return this._toFirstAttribute();
        }
        synchronized (this._cur._locale) {
            return this._toFirstAttribute();
        }
    }
    
    @Override
    public boolean toLastAttribute() {
        if (this.preCheck()) {
            this._cur._locale.enter();
            try {
                return this._toLastAttribute();
            }
            finally {
                this._cur._locale.exit();
            }
        }
        synchronized (this._cur._locale) {
            this._cur._locale.enter();
            try {
                return this._toLastAttribute();
            }
            finally {
                this._cur._locale.exit();
            }
        }
    }
    
    @Override
    public boolean toNextAttribute() {
        if (this.preCheck()) {
            this._cur._locale.enter();
            try {
                return this._toNextAttribute();
            }
            finally {
                this._cur._locale.exit();
            }
        }
        synchronized (this._cur._locale) {
            this._cur._locale.enter();
            try {
                return this._toNextAttribute();
            }
            finally {
                this._cur._locale.exit();
            }
        }
    }
    
    @Override
    public boolean toPrevAttribute() {
        if (this.preCheck()) {
            this._cur._locale.enter();
            try {
                return this._toPrevAttribute();
            }
            finally {
                this._cur._locale.exit();
            }
        }
        synchronized (this._cur._locale) {
            this._cur._locale.enter();
            try {
                return this._toPrevAttribute();
            }
            finally {
                this._cur._locale.exit();
            }
        }
    }
    
    @Override
    public String getAttributeText(final QName attrName) {
        if (this.preCheck()) {
            this._cur._locale.enter();
            try {
                return this._getAttributeText(attrName);
            }
            finally {
                this._cur._locale.exit();
            }
        }
        synchronized (this._cur._locale) {
            this._cur._locale.enter();
            try {
                return this._getAttributeText(attrName);
            }
            finally {
                this._cur._locale.exit();
            }
        }
    }
    
    @Override
    public boolean setAttributeText(final QName attrName, final String value) {
        if (this.preCheck()) {
            this._cur._locale.enter();
            try {
                return this._setAttributeText(attrName, value);
            }
            finally {
                this._cur._locale.exit();
            }
        }
        synchronized (this._cur._locale) {
            this._cur._locale.enter();
            try {
                return this._setAttributeText(attrName, value);
            }
            finally {
                this._cur._locale.exit();
            }
        }
    }
    
    @Override
    public boolean removeAttribute(final QName attrName) {
        if (this.preCheck()) {
            this._cur._locale.enter();
            try {
                return this._removeAttribute(attrName);
            }
            finally {
                this._cur._locale.exit();
            }
        }
        synchronized (this._cur._locale) {
            this._cur._locale.enter();
            try {
                return this._removeAttribute(attrName);
            }
            finally {
                this._cur._locale.exit();
            }
        }
    }
    
    @Override
    public String getTextValue() {
        if (this.preCheck()) {
            this._cur._locale.enter();
            try {
                return this._getTextValue();
            }
            finally {
                this._cur._locale.exit();
            }
        }
        synchronized (this._cur._locale) {
            this._cur._locale.enter();
            try {
                return this._getTextValue();
            }
            finally {
                this._cur._locale.exit();
            }
        }
    }
    
    @Override
    public int getTextValue(final char[] chars, final int offset, final int cch) {
        if (this.preCheck()) {
            this._cur._locale.enter();
            try {
                return this._getTextValue(chars, offset, cch);
            }
            finally {
                this._cur._locale.exit();
            }
        }
        synchronized (this._cur._locale) {
            this._cur._locale.enter();
            try {
                return this._getTextValue(chars, offset, cch);
            }
            finally {
                this._cur._locale.exit();
            }
        }
    }
    
    @Override
    public void setTextValue(final String text) {
        if (this.preCheck()) {
            this._cur._locale.enter();
            try {
                this._setTextValue(text);
            }
            finally {
                this._cur._locale.exit();
            }
        }
        else {
            synchronized (this._cur._locale) {
                this._cur._locale.enter();
                try {
                    this._setTextValue(text);
                }
                finally {
                    this._cur._locale.exit();
                }
            }
        }
    }
    
    @Override
    public void setTextValue(final char[] sourceChars, final int offset, final int length) {
        if (this.preCheck()) {
            this._cur._locale.enter();
            try {
                this._setTextValue(sourceChars, offset, length);
            }
            finally {
                this._cur._locale.exit();
            }
        }
        else {
            synchronized (this._cur._locale) {
                this._cur._locale.enter();
                try {
                    this._setTextValue(sourceChars, offset, length);
                }
                finally {
                    this._cur._locale.exit();
                }
            }
        }
    }
    
    @Override
    public String getChars() {
        if (this.preCheck()) {
            this._cur._locale.enter();
            try {
                return this._getChars();
            }
            finally {
                this._cur._locale.exit();
            }
        }
        synchronized (this._cur._locale) {
            this._cur._locale.enter();
            try {
                return this._getChars();
            }
            finally {
                this._cur._locale.exit();
            }
        }
    }
    
    @Override
    public int getChars(final char[] chars, final int offset, final int cch) {
        if (this.preCheck()) {
            this._cur._locale.enter();
            try {
                return this._getChars(chars, offset, cch);
            }
            finally {
                this._cur._locale.exit();
            }
        }
        synchronized (this._cur._locale) {
            this._cur._locale.enter();
            try {
                return this._getChars(chars, offset, cch);
            }
            finally {
                this._cur._locale.exit();
            }
        }
    }
    
    @Override
    public void toStartDoc() {
        if (this.preCheck()) {
            this._toStartDoc();
        }
        else {
            synchronized (this._cur._locale) {
                this._toStartDoc();
            }
        }
    }
    
    @Override
    public void toEndDoc() {
        if (this.preCheck()) {
            this._toEndDoc();
        }
        else {
            synchronized (this._cur._locale) {
                this._toEndDoc();
            }
        }
    }
    
    @Override
    public XmlCursor execQuery(final String query) {
        if (this.preCheck()) {
            this._cur._locale.enter();
            try {
                return this._execQuery(query);
            }
            finally {
                this._cur._locale.exit();
            }
        }
        synchronized (this._cur._locale) {
            this._cur._locale.enter();
            try {
                return this._execQuery(query);
            }
            finally {
                this._cur._locale.exit();
            }
        }
    }
    
    @Override
    public XmlCursor execQuery(final String query, final XmlOptions options) {
        if (this.preCheck()) {
            this._cur._locale.enter();
            try {
                return this._execQuery(query, options);
            }
            finally {
                this._cur._locale.exit();
            }
        }
        synchronized (this._cur._locale) {
            this._cur._locale.enter();
            try {
                return this._execQuery(query, options);
            }
            finally {
                this._cur._locale.exit();
            }
        }
    }
    
    @Override
    public ChangeStamp getDocChangeStamp() {
        if (this.preCheck()) {
            this._cur._locale.enter();
            try {
                return this._getDocChangeStamp();
            }
            finally {
                this._cur._locale.exit();
            }
        }
        synchronized (this._cur._locale) {
            this._cur._locale.enter();
            try {
                return this._getDocChangeStamp();
            }
            finally {
                this._cur._locale.exit();
            }
        }
    }
    
    @Override
    public void setBookmark(final XmlBookmark bookmark) {
        if (this.preCheck()) {
            this._cur._locale.enter();
            try {
                this._setBookmark(bookmark);
            }
            finally {
                this._cur._locale.exit();
            }
        }
        else {
            synchronized (this._cur._locale) {
                this._cur._locale.enter();
                try {
                    this._setBookmark(bookmark);
                }
                finally {
                    this._cur._locale.exit();
                }
            }
        }
    }
    
    @Override
    public XmlBookmark getBookmark(final Object key) {
        if (this.preCheck()) {
            this._cur._locale.enter();
            try {
                return this._getBookmark(key);
            }
            finally {
                this._cur._locale.exit();
            }
        }
        synchronized (this._cur._locale) {
            this._cur._locale.enter();
            try {
                return this._getBookmark(key);
            }
            finally {
                this._cur._locale.exit();
            }
        }
    }
    
    @Override
    public void clearBookmark(final Object key) {
        if (this.preCheck()) {
            this._cur._locale.enter();
            try {
                this._clearBookmark(key);
            }
            finally {
                this._cur._locale.exit();
            }
        }
        else {
            synchronized (this._cur._locale) {
                this._cur._locale.enter();
                try {
                    this._clearBookmark(key);
                }
                finally {
                    this._cur._locale.exit();
                }
            }
        }
    }
    
    @Override
    public void getAllBookmarkRefs(final Collection listToFill) {
        if (this.preCheck()) {
            this._cur._locale.enter();
            try {
                this._getAllBookmarkRefs(listToFill);
            }
            finally {
                this._cur._locale.exit();
            }
        }
        else {
            synchronized (this._cur._locale) {
                this._cur._locale.enter();
                try {
                    this._getAllBookmarkRefs(listToFill);
                }
                finally {
                    this._cur._locale.exit();
                }
            }
        }
    }
    
    @Override
    public boolean removeXml() {
        if (this.preCheck()) {
            this._cur._locale.enter();
            try {
                return this._removeXml();
            }
            finally {
                this._cur._locale.exit();
            }
        }
        synchronized (this._cur._locale) {
            this._cur._locale.enter();
            try {
                return this._removeXml();
            }
            finally {
                this._cur._locale.exit();
            }
        }
    }
    
    @Override
    public boolean removeXmlContents() {
        if (this.preCheck()) {
            this._cur._locale.enter();
            try {
                return this._removeXmlContents();
            }
            finally {
                this._cur._locale.exit();
            }
        }
        synchronized (this._cur._locale) {
            this._cur._locale.enter();
            try {
                return this._removeXmlContents();
            }
            finally {
                this._cur._locale.exit();
            }
        }
    }
    
    @Override
    public int removeChars(final int cch) {
        if (this.preCheck()) {
            this._cur._locale.enter();
            try {
                return this._removeChars(cch);
            }
            finally {
                this._cur._locale.exit();
            }
        }
        synchronized (this._cur._locale) {
            this._cur._locale.enter();
            try {
                return this._removeChars(cch);
            }
            finally {
                this._cur._locale.exit();
            }
        }
    }
    
    @Override
    public void insertChars(final String text) {
        if (this.preCheck()) {
            this._cur._locale.enter();
            try {
                this._insertChars(text);
            }
            finally {
                this._cur._locale.exit();
            }
        }
        else {
            synchronized (this._cur._locale) {
                this._cur._locale.enter();
                try {
                    this._insertChars(text);
                }
                finally {
                    this._cur._locale.exit();
                }
            }
        }
    }
    
    @Override
    public void insertElement(final QName name) {
        if (this.preCheck()) {
            this._cur._locale.enter();
            try {
                this._insertElement(name);
            }
            finally {
                this._cur._locale.exit();
            }
        }
        else {
            synchronized (this._cur._locale) {
                this._cur._locale.enter();
                try {
                    this._insertElement(name);
                }
                finally {
                    this._cur._locale.exit();
                }
            }
        }
    }
    
    @Override
    public void insertElement(final String localName) {
        if (this.preCheck()) {
            this._cur._locale.enter();
            try {
                this._insertElement(localName);
            }
            finally {
                this._cur._locale.exit();
            }
        }
        else {
            synchronized (this._cur._locale) {
                this._cur._locale.enter();
                try {
                    this._insertElement(localName);
                }
                finally {
                    this._cur._locale.exit();
                }
            }
        }
    }
    
    @Override
    public void insertElement(final String localName, final String uri) {
        if (this.preCheck()) {
            this._cur._locale.enter();
            try {
                this._insertElement(localName, uri);
            }
            finally {
                this._cur._locale.exit();
            }
        }
        else {
            synchronized (this._cur._locale) {
                this._cur._locale.enter();
                try {
                    this._insertElement(localName, uri);
                }
                finally {
                    this._cur._locale.exit();
                }
            }
        }
    }
    
    @Override
    public void beginElement(final QName name) {
        if (this.preCheck()) {
            this._cur._locale.enter();
            try {
                this._beginElement(name);
            }
            finally {
                this._cur._locale.exit();
            }
        }
        else {
            synchronized (this._cur._locale) {
                this._cur._locale.enter();
                try {
                    this._beginElement(name);
                }
                finally {
                    this._cur._locale.exit();
                }
            }
        }
    }
    
    @Override
    public void beginElement(final String localName) {
        if (this.preCheck()) {
            this._cur._locale.enter();
            try {
                this._beginElement(localName);
            }
            finally {
                this._cur._locale.exit();
            }
        }
        else {
            synchronized (this._cur._locale) {
                this._cur._locale.enter();
                try {
                    this._beginElement(localName);
                }
                finally {
                    this._cur._locale.exit();
                }
            }
        }
    }
    
    @Override
    public void beginElement(final String localName, final String uri) {
        if (this.preCheck()) {
            this._cur._locale.enter();
            try {
                this._beginElement(localName, uri);
            }
            finally {
                this._cur._locale.exit();
            }
        }
        else {
            synchronized (this._cur._locale) {
                this._cur._locale.enter();
                try {
                    this._beginElement(localName, uri);
                }
                finally {
                    this._cur._locale.exit();
                }
            }
        }
    }
    
    @Override
    public void insertElementWithText(final QName name, final String text) {
        if (this.preCheck()) {
            this._cur._locale.enter();
            try {
                this._insertElementWithText(name, text);
            }
            finally {
                this._cur._locale.exit();
            }
        }
        else {
            synchronized (this._cur._locale) {
                this._cur._locale.enter();
                try {
                    this._insertElementWithText(name, text);
                }
                finally {
                    this._cur._locale.exit();
                }
            }
        }
    }
    
    @Override
    public void insertElementWithText(final String localName, final String text) {
        if (this.preCheck()) {
            this._cur._locale.enter();
            try {
                this._insertElementWithText(localName, text);
            }
            finally {
                this._cur._locale.exit();
            }
        }
        else {
            synchronized (this._cur._locale) {
                this._cur._locale.enter();
                try {
                    this._insertElementWithText(localName, text);
                }
                finally {
                    this._cur._locale.exit();
                }
            }
        }
    }
    
    @Override
    public void insertElementWithText(final String localName, final String uri, final String text) {
        if (this.preCheck()) {
            this._cur._locale.enter();
            try {
                this._insertElementWithText(localName, uri, text);
            }
            finally {
                this._cur._locale.exit();
            }
        }
        else {
            synchronized (this._cur._locale) {
                this._cur._locale.enter();
                try {
                    this._insertElementWithText(localName, uri, text);
                }
                finally {
                    this._cur._locale.exit();
                }
            }
        }
    }
    
    @Override
    public void insertAttribute(final String localName) {
        if (this.preCheck()) {
            this._cur._locale.enter();
            try {
                this._insertAttribute(localName);
            }
            finally {
                this._cur._locale.exit();
            }
        }
        else {
            synchronized (this._cur._locale) {
                this._cur._locale.enter();
                try {
                    this._insertAttribute(localName);
                }
                finally {
                    this._cur._locale.exit();
                }
            }
        }
    }
    
    @Override
    public void insertAttribute(final String localName, final String uri) {
        if (this.preCheck()) {
            this._cur._locale.enter();
            try {
                this._insertAttribute(localName, uri);
            }
            finally {
                this._cur._locale.exit();
            }
        }
        else {
            synchronized (this._cur._locale) {
                this._cur._locale.enter();
                try {
                    this._insertAttribute(localName, uri);
                }
                finally {
                    this._cur._locale.exit();
                }
            }
        }
    }
    
    @Override
    public void insertAttribute(final QName name) {
        if (this.preCheck()) {
            this._cur._locale.enter();
            try {
                this._insertAttribute(name);
            }
            finally {
                this._cur._locale.exit();
            }
        }
        else {
            synchronized (this._cur._locale) {
                this._cur._locale.enter();
                try {
                    this._insertAttribute(name);
                }
                finally {
                    this._cur._locale.exit();
                }
            }
        }
    }
    
    @Override
    public void insertAttributeWithValue(final String Name, final String value) {
        if (this.preCheck()) {
            this._cur._locale.enter();
            try {
                this._insertAttributeWithValue(Name, value);
            }
            finally {
                this._cur._locale.exit();
            }
        }
        else {
            synchronized (this._cur._locale) {
                this._cur._locale.enter();
                try {
                    this._insertAttributeWithValue(Name, value);
                }
                finally {
                    this._cur._locale.exit();
                }
            }
        }
    }
    
    @Override
    public void insertAttributeWithValue(final String name, final String uri, final String value) {
        if (this.preCheck()) {
            this._cur._locale.enter();
            try {
                this._insertAttributeWithValue(name, uri, value);
            }
            finally {
                this._cur._locale.exit();
            }
        }
        else {
            synchronized (this._cur._locale) {
                this._cur._locale.enter();
                try {
                    this._insertAttributeWithValue(name, uri, value);
                }
                finally {
                    this._cur._locale.exit();
                }
            }
        }
    }
    
    @Override
    public void insertAttributeWithValue(final QName name, final String value) {
        if (this.preCheck()) {
            this._cur._locale.enter();
            try {
                this._insertAttributeWithValue(name, value);
            }
            finally {
                this._cur._locale.exit();
            }
        }
        else {
            synchronized (this._cur._locale) {
                this._cur._locale.enter();
                try {
                    this._insertAttributeWithValue(name, value);
                }
                finally {
                    this._cur._locale.exit();
                }
            }
        }
    }
    
    @Override
    public void insertNamespace(final String prefix, final String namespace) {
        if (this.preCheck()) {
            this._cur._locale.enter();
            try {
                this._insertNamespace(prefix, namespace);
            }
            finally {
                this._cur._locale.exit();
            }
        }
        else {
            synchronized (this._cur._locale) {
                this._cur._locale.enter();
                try {
                    this._insertNamespace(prefix, namespace);
                }
                finally {
                    this._cur._locale.exit();
                }
            }
        }
    }
    
    @Override
    public void insertComment(final String text) {
        if (this.preCheck()) {
            this._cur._locale.enter();
            try {
                this._insertComment(text);
            }
            finally {
                this._cur._locale.exit();
            }
        }
        else {
            synchronized (this._cur._locale) {
                this._cur._locale.enter();
                try {
                    this._insertComment(text);
                }
                finally {
                    this._cur._locale.exit();
                }
            }
        }
    }
    
    @Override
    public void insertProcInst(final String target, final String text) {
        if (this.preCheck()) {
            this._cur._locale.enter();
            try {
                this._insertProcInst(target, text);
            }
            finally {
                this._cur._locale.exit();
            }
        }
        else {
            synchronized (this._cur._locale) {
                this._cur._locale.enter();
                try {
                    this._insertProcInst(target, text);
                }
                finally {
                    this._cur._locale.exit();
                }
            }
        }
    }
    
    @Override
    public void dump() {
        if (this.preCheck()) {
            this._cur._locale.enter();
            try {
                this._dump();
            }
            finally {
                this._cur._locale.exit();
            }
        }
        else {
            synchronized (this._cur._locale) {
                this._cur._locale.enter();
                try {
                    this._dump();
                }
                finally {
                    this._cur._locale.exit();
                }
            }
        }
    }
    
    private static final class ChangeStampImpl implements ChangeStamp
    {
        private final Locale _locale;
        private final long _versionStamp;
        
        ChangeStampImpl(final Locale l) {
            this._locale = l;
            this._versionStamp = this._locale.version();
        }
        
        @Override
        public boolean hasChanged() {
            return this._versionStamp != this._locale.version();
        }
    }
    
    private static final class DomSaver extends Saver
    {
        private Cur _nodeCur;
        private SchemaType _type;
        private SchemaTypeLoader _stl;
        private XmlOptions _options;
        private boolean _isFrag;
        
        DomSaver(final Cur c, final boolean isFrag, final XmlOptions options) {
            super(c, options);
            if (c.isUserNode()) {
                this._type = c.getUser().get_schema_type();
            }
            this._stl = c._locale._schemaTypeLoader;
            this._options = options;
            this._isFrag = isFrag;
        }
        
        Node saveDom() {
            final Locale l = Locale.getLocale(this._stl, this._options);
            l.enter();
            try {
                this._nodeCur = l.getCur();
                while (this.process()) {}
                while (!this._nodeCur.isRoot()) {
                    this._nodeCur.toParent();
                }
                if (this._type != null) {
                    this._nodeCur.setType(this._type);
                }
                final Node node = (Node)this._nodeCur.getDom();
                this._nodeCur.release();
                this._nodeCur = null;
                return node;
            }
            finally {
                l.exit();
            }
        }
        
        @Override
        protected boolean emitElement(final SaveCur c, final ArrayList attrNames, final ArrayList attrValues) {
            if (Locale.isFragmentQName(c.getName())) {
                this._nodeCur.moveTo(null, -2);
            }
            this.ensureDoc();
            this._nodeCur.createElement(this.getQualifiedName(c, c.getName()));
            this._nodeCur.next();
            this.iterateMappings();
            while (this.hasMapping()) {
                this._nodeCur.createAttr(this._nodeCur._locale.createXmlns(this.mappingPrefix()));
                this._nodeCur.next();
                this._nodeCur.insertString(this.mappingUri());
                this._nodeCur.toParent();
                this._nodeCur.skipWithAttrs();
                this.nextMapping();
            }
            for (int i = 0; i < attrNames.size(); ++i) {
                this._nodeCur.createAttr(this.getQualifiedName(c, attrNames.get(i)));
                this._nodeCur.next();
                this._nodeCur.insertString(attrValues.get(i));
                this._nodeCur.toParent();
                this._nodeCur.skipWithAttrs();
            }
            return false;
        }
        
        @Override
        protected void emitFinish(final SaveCur c) {
            if (!Locale.isFragmentQName(c.getName())) {
                assert this._nodeCur.isEnd();
                this._nodeCur.next();
            }
        }
        
        @Override
        protected void emitText(final SaveCur c) {
            this.ensureDoc();
            final Object src = c.getChars();
            if (c._cchSrc > 0) {
                this._nodeCur.insertChars(src, c._offSrc, c._cchSrc);
                this._nodeCur.next();
            }
        }
        
        @Override
        protected void emitComment(final SaveCur c) {
            this.ensureDoc();
            this._nodeCur.createComment();
            this.emitTextValue(c);
            this._nodeCur.skip();
        }
        
        @Override
        protected void emitProcinst(final SaveCur c) {
            this.ensureDoc();
            this._nodeCur.createProcinst(c.getName().getLocalPart());
            this.emitTextValue(c);
            this._nodeCur.skip();
        }
        
        @Override
        protected void emitDocType(final String docTypeName, final String publicId, final String systemId) {
            this.ensureDoc();
            final XmlDocumentProperties props = Locale.getDocProps(this._nodeCur, true);
            props.setDoctypeName(docTypeName);
            props.setDoctypePublicId(publicId);
            props.setDoctypeSystemId(systemId);
        }
        
        @Override
        protected void emitStartDoc(final SaveCur c) {
            this.ensureDoc();
        }
        
        @Override
        protected void emitEndDoc(final SaveCur c) {
        }
        
        private QName getQualifiedName(final SaveCur c, final QName name) {
            final String uri = name.getNamespaceURI();
            final String prefix = (uri.length() > 0) ? this.getUriMapping(uri) : "";
            if (prefix.equals(name.getPrefix())) {
                return name;
            }
            return this._nodeCur._locale.makeQName(uri, name.getLocalPart(), prefix);
        }
        
        private void emitTextValue(final SaveCur c) {
            c.push();
            c.next();
            if (c.isText()) {
                this._nodeCur.next();
                this._nodeCur.insertChars(c.getChars(), c._offSrc, c._cchSrc);
                this._nodeCur.toParent();
            }
            c.pop();
        }
        
        private void ensureDoc() {
            if (!this._nodeCur.isPositioned()) {
                if (this._isFrag) {
                    this._nodeCur.createDomDocFragRoot();
                }
                else {
                    this._nodeCur.createDomDocumentRoot();
                }
                this._nodeCur.next();
            }
        }
    }
}
