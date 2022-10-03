package org.apache.xmlbeans.impl.store;

import java.util.HashMap;
import java.util.Iterator;
import org.apache.xmlbeans.XmlDocumentProperties;
import org.apache.xmlbeans.XmlLineNumber;
import java.util.ConcurrentModificationException;
import javax.xml.stream.Location;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import org.apache.xmlbeans.XmlOptions;
import org.w3c.dom.Node;
import javax.xml.stream.XMLStreamReader;

public class Jsr173
{
    public static Node nodeFromStream(final XMLStreamReader xs) {
        if (!(xs instanceof Jsr173GateWay)) {
            return null;
        }
        final Jsr173GateWay gw = (Jsr173GateWay)xs;
        final Locale l = gw._l;
        if (l.noSync()) {
            l.enter();
            try {
                return nodeFromStreamImpl(gw);
            }
            finally {
                l.exit();
            }
        }
        synchronized (l) {
            l.enter();
            try {
                return nodeFromStreamImpl(gw);
            }
            finally {
                l.exit();
            }
        }
    }
    
    public static Node nodeFromStreamImpl(final Jsr173GateWay gw) {
        final Cur c = gw._xs.getStreamCur();
        return (Node)(c.isNode() ? c.getDom() : ((Node)null));
    }
    
    public static XMLStreamReader newXmlStreamReader(final Cur c, final Object src, final int off, final int cch) {
        final XMLStreamReaderBase xs = new XMLStreamReaderForString(c, src, off, cch);
        if (c._locale.noSync()) {
            return new UnsyncedJsr173(c._locale, xs);
        }
        return new SyncedJsr173(c._locale, xs);
    }
    
    public static XMLStreamReader newXmlStreamReader(final Cur c, XmlOptions options) {
        options = XmlOptions.maskNull(options);
        final boolean inner = options.hasOption("SAVE_INNER") && !options.hasOption("SAVE_OUTER");
        final int k = c.kind();
        XMLStreamReaderBase xs;
        if (k == 0 || k < 0) {
            xs = new XMLStreamReaderForString(c, c.getChars(-1), c._offSrc, c._cchSrc);
        }
        else if (inner) {
            if (!c.hasAttrs() && !c.hasChildren()) {
                xs = new XMLStreamReaderForString(c, c.getFirstChars(), c._offSrc, c._cchSrc);
            }
            else {
                assert c.isContainer();
                xs = new XMLStreamReaderForNode(c, true);
            }
        }
        else {
            xs = new XMLStreamReaderForNode(c, false);
        }
        if (c._locale.noSync()) {
            return new UnsyncedJsr173(c._locale, xs);
        }
        return new SyncedJsr173(c._locale, xs);
    }
    
    private static final class XMLStreamReaderForNode extends XMLStreamReaderBase
    {
        private boolean _wholeDoc;
        private boolean _done;
        private Cur _cur;
        private Cur _end;
        private boolean _srcFetched;
        private Object _src;
        private int _offSrc;
        private int _cchSrc;
        private boolean _textFetched;
        private char[] _chars;
        private int _offChars;
        private int _cchChars;
        
        public XMLStreamReaderForNode(final Cur c, final boolean inner) {
            super(c);
            assert c.isContainer() || c.isAttr();
            if (inner) {
                assert c.isContainer();
                this._cur = c.weakCur(this);
                if (!this._cur.toFirstAttr()) {
                    this._cur.next();
                }
                (this._end = c.weakCur(this)).toEnd();
            }
            else {
                this._cur = c.weakCur(this);
                if (c.isRoot()) {
                    this._wholeDoc = true;
                }
                else {
                    this._end = c.weakCur(this);
                    if (c.isAttr()) {
                        if (!this._end.toNextAttr()) {
                            this._end.toParent();
                            this._end.next();
                        }
                    }
                    else {
                        this._end.skip();
                    }
                }
            }
            if (!this._wholeDoc) {
                this._cur.push();
                try {
                    this.next();
                }
                catch (final XMLStreamException e) {
                    throw new RuntimeException(e.getMessage(), e);
                }
                this._cur.pop();
            }
            assert !this._cur.isSamePos(this._end);
        }
        
        @Override
        protected Cur getStreamCur() {
            return this._cur;
        }
        
        @Override
        public boolean hasNext() throws XMLStreamException {
            this.checkChanged();
            return !this._done;
        }
        
        @Override
        public int getEventType() {
            switch (this._cur.kind()) {
                case 1: {
                    return 7;
                }
                case -1: {
                    return 8;
                }
                case 2: {
                    return 1;
                }
                case -2: {
                    return 2;
                }
                case 3: {
                    return this._cur.isXmlns() ? 13 : 10;
                }
                case 0: {
                    return 4;
                }
                case 4: {
                    return 5;
                }
                case 5: {
                    return 3;
                }
                default: {
                    throw new IllegalStateException();
                }
            }
        }
        
        @Override
        public int next() throws XMLStreamException {
            this.checkChanged();
            if (!this.hasNext()) {
                throw new IllegalStateException("No next event in stream");
            }
            final int kind = this._cur.kind();
            if (kind == -1) {
                assert this._wholeDoc;
                this._done = true;
            }
            else {
                if (kind == 3) {
                    if (!this._cur.toNextAttr()) {
                        this._cur.toParent();
                        this._cur.next();
                    }
                }
                else if (kind == 4 || kind == 5) {
                    this._cur.skip();
                }
                else if (kind == 1) {
                    if (!this._cur.toFirstAttr()) {
                        this._cur.next();
                    }
                }
                else {
                    this._cur.next();
                }
                assert this._end != null;
                this._done = (this._wholeDoc ? (this._cur.kind() == -1) : this._cur.isSamePos(this._end));
            }
            this._textFetched = false;
            this._srcFetched = false;
            return this.getEventType();
        }
        
        @Override
        public String getText() {
            this.checkChanged();
            final int k = this._cur.kind();
            if (k == 4) {
                return this._cur.getValueAsString();
            }
            if (k == 0) {
                return this._cur.getCharsAsString(-1);
            }
            throw new IllegalStateException();
        }
        
        @Override
        public boolean isStartElement() {
            return this.getEventType() == 1;
        }
        
        @Override
        public boolean isEndElement() {
            return this.getEventType() == 2;
        }
        
        @Override
        public boolean isCharacters() {
            return this.getEventType() == 4;
        }
        
        @Override
        public String getElementText() throws XMLStreamException {
            this.checkChanged();
            if (!this.isStartElement()) {
                throw new IllegalStateException();
            }
            final StringBuffer sb = new StringBuffer();
            while (this.hasNext()) {
                final int e = this.next();
                if (e == 2) {
                    return sb.toString();
                }
                if (e == 1) {
                    throw new XMLStreamException();
                }
                if (e == 5 || e == 3) {
                    continue;
                }
                sb.append(this.getText());
            }
            throw new XMLStreamException();
        }
        
        @Override
        public int nextTag() throws XMLStreamException {
            this.checkChanged();
            while (!this.isStartElement() && !this.isEndElement()) {
                if (!this.isWhiteSpace()) {
                    throw new XMLStreamException();
                }
                if (!this.hasNext()) {
                    throw new XMLStreamException();
                }
                this.next();
            }
            return this.getEventType();
        }
        
        private static boolean matchAttr(final Cur c, final String uri, final String local) {
            assert c.isNormalAttr();
            final QName name = c.getName();
            return name.getLocalPart().equals(local) && (uri == null || name.getNamespaceURI().equals(uri));
        }
        
        private static Cur toAttr(final Cur c, final String uri, final String local) {
            if (uri == null || local == null || local.length() == 0) {
                throw new IllegalArgumentException();
            }
            Cur ca = c.tempCur();
            boolean match = false;
            Label_0103: {
                if (c.isElem()) {
                    if (ca.toFirstAttr()) {
                        while (!ca.isNormalAttr() || !matchAttr(ca, uri, local)) {
                            if (!ca.toNextSibling()) {
                                break Label_0103;
                            }
                        }
                        match = true;
                    }
                }
                else {
                    if (!c.isNormalAttr()) {
                        throw new IllegalStateException();
                    }
                    match = matchAttr(c, uri, local);
                }
            }
            if (!match) {
                ca.release();
                ca = null;
            }
            return ca;
        }
        
        @Override
        public String getAttributeValue(final String uri, final String local) {
            final Cur ca = toAttr(this._cur, uri, local);
            String value = null;
            if (ca != null) {
                value = ca.getValueAsString();
                ca.release();
            }
            return value;
        }
        
        private static Cur toAttr(final Cur c, int i) {
            if (i < 0) {
                throw new IndexOutOfBoundsException("Attribute index is negative");
            }
            final Cur ca = c.tempCur();
            boolean match = false;
            Label_0092: {
                if (c.isElem()) {
                    if (ca.toFirstAttr()) {
                        while (!ca.isNormalAttr() || i-- != 0) {
                            if (!ca.toNextSibling()) {
                                break Label_0092;
                            }
                        }
                        match = true;
                    }
                }
                else {
                    if (!c.isNormalAttr()) {
                        throw new IllegalStateException();
                    }
                    match = (i == 0);
                }
            }
            if (!match) {
                ca.release();
                throw new IndexOutOfBoundsException("Attribute index is too large");
            }
            return ca;
        }
        
        @Override
        public int getAttributeCount() {
            int n = 0;
            if (this._cur.isElem()) {
                final Cur ca = this._cur.tempCur();
                if (ca.toFirstAttr()) {
                    do {
                        if (ca.isNormalAttr()) {
                            ++n;
                        }
                    } while (ca.toNextSibling());
                }
                ca.release();
            }
            else {
                if (!this._cur.isNormalAttr()) {
                    throw new IllegalStateException();
                }
                ++n;
            }
            return n;
        }
        
        @Override
        public QName getAttributeName(final int index) {
            final Cur ca = toAttr(this._cur, index);
            final QName name = ca.getName();
            ca.release();
            return name;
        }
        
        @Override
        public String getAttributeNamespace(final int index) {
            return this.getAttributeName(index).getNamespaceURI();
        }
        
        @Override
        public String getAttributeLocalName(final int index) {
            return this.getAttributeName(index).getLocalPart();
        }
        
        @Override
        public String getAttributePrefix(final int index) {
            return this.getAttributeName(index).getPrefix();
        }
        
        @Override
        public String getAttributeType(final int index) {
            toAttr(this._cur, index).release();
            return "CDATA";
        }
        
        @Override
        public String getAttributeValue(final int index) {
            final Cur ca = toAttr(this._cur, index);
            String value = null;
            if (ca != null) {
                value = ca.getValueAsString();
                ca.release();
            }
            return value;
        }
        
        @Override
        public boolean isAttributeSpecified(final int index) {
            final Cur ca = toAttr(this._cur, index);
            ca.release();
            return false;
        }
        
        @Override
        public int getNamespaceCount() {
            int n = 0;
            if (this._cur.isElem() || this._cur.kind() == -2) {
                final Cur ca = this._cur.tempCur();
                if (this._cur.kind() == -2) {
                    ca.toParent();
                }
                if (ca.toFirstAttr()) {
                    do {
                        if (ca.isXmlns()) {
                            ++n;
                        }
                    } while (ca.toNextSibling());
                }
                ca.release();
            }
            else {
                if (!this._cur.isXmlns()) {
                    throw new IllegalStateException();
                }
                ++n;
            }
            return n;
        }
        
        private static Cur toXmlns(final Cur c, int i) {
            if (i < 0) {
                throw new IndexOutOfBoundsException("Namespace index is negative");
            }
            final Cur ca = c.tempCur();
            boolean match = false;
            Label_0115: {
                if (c.isElem() || c.kind() == -2) {
                    if (c.kind() == -2) {
                        ca.toParent();
                    }
                    if (ca.toFirstAttr()) {
                        while (!ca.isXmlns() || i-- != 0) {
                            if (!ca.toNextSibling()) {
                                break Label_0115;
                            }
                        }
                        match = true;
                    }
                }
                else {
                    if (!c.isXmlns()) {
                        throw new IllegalStateException();
                    }
                    match = (i == 0);
                }
            }
            if (!match) {
                ca.release();
                throw new IndexOutOfBoundsException("Namespace index is too large");
            }
            return ca;
        }
        
        @Override
        public String getNamespacePrefix(final int index) {
            final Cur ca = toXmlns(this._cur, index);
            final String prefix = ca.getXmlnsPrefix();
            ca.release();
            return prefix;
        }
        
        @Override
        public String getNamespaceURI(final int index) {
            final Cur ca = toXmlns(this._cur, index);
            final String uri = ca.getXmlnsUri();
            ca.release();
            return uri;
        }
        
        private void fetchChars() {
            if (!this._textFetched) {
                final int k = this._cur.kind();
                Cur cText = null;
                if (k == 4) {
                    cText = this._cur.tempCur();
                    cText.next();
                }
                else {
                    if (k != 0) {
                        throw new IllegalStateException();
                    }
                    cText = this._cur;
                }
                final Object src = cText.getChars(-1);
                this.ensureCharBufLen(cText._cchSrc);
                CharUtil.getChars(this._chars, this._offChars = 0, src, cText._offSrc, this._cchChars = cText._cchSrc);
                if (cText != this._cur) {
                    cText.release();
                }
                this._textFetched = true;
            }
        }
        
        private void ensureCharBufLen(final int cch) {
            if (this._chars == null || this._chars.length < cch) {
                int l;
                for (l = 256; l < cch; l *= 2) {}
                this._chars = new char[l];
            }
        }
        
        @Override
        public char[] getTextCharacters() {
            this.checkChanged();
            this.fetchChars();
            return this._chars;
        }
        
        @Override
        public int getTextStart() {
            this.checkChanged();
            this.fetchChars();
            return this._offChars;
        }
        
        @Override
        public int getTextLength() {
            this.checkChanged();
            this.fetchChars();
            return this._cchChars;
        }
        
        @Override
        public int getTextCharacters(final int sourceStart, final char[] target, final int targetStart, int length) throws XMLStreamException {
            if (length < 0) {
                throw new IndexOutOfBoundsException();
            }
            if (targetStart < 0 || targetStart >= target.length) {
                throw new IndexOutOfBoundsException();
            }
            if (targetStart + length > target.length) {
                throw new IndexOutOfBoundsException();
            }
            if (!this._srcFetched) {
                final int k = this._cur.kind();
                Cur cText = null;
                if (k == 4) {
                    cText = this._cur.tempCur();
                    cText.next();
                }
                else {
                    if (k != 0) {
                        throw new IllegalStateException();
                    }
                    cText = this._cur;
                }
                this._src = cText.getChars(-1);
                this._offSrc = cText._offSrc;
                this._cchSrc = cText._cchSrc;
                if (cText != this._cur) {
                    cText.release();
                }
                this._srcFetched = true;
            }
            if (sourceStart > this._cchSrc) {
                throw new IndexOutOfBoundsException();
            }
            if (sourceStart + length > this._cchSrc) {
                length = this._cchSrc - sourceStart;
            }
            CharUtil.getChars(target, targetStart, this._src, this._offSrc, length);
            return length;
        }
        
        @Override
        public boolean hasText() {
            final int k = this._cur.kind();
            return k == 4 || k == 0;
        }
        
        @Override
        public boolean hasName() {
            final int k = this._cur.kind();
            return k == 2 || k == -2;
        }
        
        @Override
        public QName getName() {
            if (!this.hasName()) {
                throw new IllegalStateException();
            }
            return this._cur.getName();
        }
        
        @Override
        public String getNamespaceURI() {
            return this.getName().getNamespaceURI();
        }
        
        @Override
        public String getLocalName() {
            return this.getName().getLocalPart();
        }
        
        @Override
        public String getPrefix() {
            return this.getName().getPrefix();
        }
        
        @Override
        public String getPITarget() {
            return (this._cur.kind() == 5) ? this._cur.getName().getLocalPart() : null;
        }
        
        @Override
        public String getPIData() {
            return (this._cur.kind() == 5) ? this._cur.getValueAsString() : null;
        }
    }
    
    private abstract static class XMLStreamReaderBase implements XMLStreamReader, NamespaceContext, Location
    {
        private Locale _locale;
        private long _version;
        String _uri;
        int _line;
        int _column;
        int _offset;
        
        XMLStreamReaderBase(final Cur c) {
            this._line = -1;
            this._column = -1;
            this._offset = -1;
            this._locale = c._locale;
            this._version = this._locale.version();
        }
        
        protected final void checkChanged() {
            if (this._version != this._locale.version()) {
                throw new ConcurrentModificationException("Document changed while streaming");
            }
        }
        
        @Override
        public void close() throws XMLStreamException {
            this.checkChanged();
        }
        
        @Override
        public boolean isWhiteSpace() {
            this.checkChanged();
            final String s = this.getText();
            return this._locale.getCharUtil().isWhiteSpace(s, 0, s.length());
        }
        
        @Override
        public Location getLocation() {
            this.checkChanged();
            final Cur c = this.getStreamCur();
            final XmlLineNumber ln = (XmlLineNumber)c.getBookmark(XmlLineNumber.class);
            this._uri = null;
            if (ln != null) {
                this._line = ln.getLine();
                this._column = ln.getColumn();
                this._offset = ln.getOffset();
            }
            else {
                this._line = -1;
                this._column = -1;
                this._offset = -1;
            }
            return this;
        }
        
        @Override
        public Object getProperty(final String name) {
            this.checkChanged();
            if (name == null) {
                throw new IllegalArgumentException("Property name is null");
            }
            return null;
        }
        
        @Override
        public String getCharacterEncodingScheme() {
            this.checkChanged();
            final Locale locale = this._locale;
            final XmlDocumentProperties props = Locale.getDocProps(this.getStreamCur(), false);
            return (props == null) ? null : props.getEncoding();
        }
        
        @Override
        public String getEncoding() {
            return null;
        }
        
        @Override
        public String getVersion() {
            this.checkChanged();
            final Locale locale = this._locale;
            final XmlDocumentProperties props = Locale.getDocProps(this.getStreamCur(), false);
            return (props == null) ? null : props.getVersion();
        }
        
        @Override
        public boolean isStandalone() {
            this.checkChanged();
            final Locale locale = this._locale;
            final XmlDocumentProperties props = Locale.getDocProps(this.getStreamCur(), false);
            return props != null && props.getStandalone();
        }
        
        @Override
        public boolean standaloneSet() {
            this.checkChanged();
            return false;
        }
        
        @Override
        public void require(final int type, final String namespaceURI, final String localName) throws XMLStreamException {
            this.checkChanged();
            if (type != this.getEventType()) {
                throw new XMLStreamException();
            }
            if (namespaceURI != null && !this.getNamespaceURI().equals(namespaceURI)) {
                throw new XMLStreamException();
            }
            if (localName != null && !this.getLocalName().equals(localName)) {
                throw new XMLStreamException();
            }
        }
        
        @Override
        public int getCharacterOffset() {
            return this._offset;
        }
        
        @Override
        public int getColumnNumber() {
            return this._column;
        }
        
        @Override
        public int getLineNumber() {
            return this._line;
        }
        
        public String getLocationURI() {
            return this._uri;
        }
        
        @Override
        public String getPublicId() {
            return null;
        }
        
        @Override
        public String getSystemId() {
            return null;
        }
        
        @Override
        public NamespaceContext getNamespaceContext() {
            throw new RuntimeException("This version of getNamespaceContext should not be called");
        }
        
        @Override
        public String getNamespaceURI(final String prefix) {
            this.checkChanged();
            final Cur c = this.getStreamCur();
            c.push();
            if (!c.isContainer()) {
                c.toParent();
            }
            final String ns = c.namespaceForPrefix(prefix, true);
            c.pop();
            return ns;
        }
        
        @Override
        public String getPrefix(final String namespaceURI) {
            this.checkChanged();
            final Cur c = this.getStreamCur();
            c.push();
            if (!c.isContainer()) {
                c.toParent();
            }
            final String prefix = c.prefixForNamespace(namespaceURI, null, false);
            c.pop();
            return prefix;
        }
        
        @Override
        public Iterator getPrefixes(final String namespaceURI) {
            this.checkChanged();
            final HashMap map = new HashMap();
            map.put(namespaceURI, this.getPrefix(namespaceURI));
            return map.values().iterator();
        }
        
        protected abstract Cur getStreamCur();
    }
    
    private static final class XMLStreamReaderForString extends XMLStreamReaderBase
    {
        private Cur _cur;
        private Object _src;
        private int _off;
        private int _cch;
        
        XMLStreamReaderForString(final Cur c, final Object src, final int off, final int cch) {
            super(c);
            this._src = src;
            this._off = off;
            this._cch = cch;
            this._cur = c;
        }
        
        @Override
        protected Cur getStreamCur() {
            return this._cur;
        }
        
        @Override
        public String getText() {
            this.checkChanged();
            return CharUtil.getString(this._src, this._off, this._cch);
        }
        
        @Override
        public char[] getTextCharacters() {
            this.checkChanged();
            final char[] chars = new char[this._cch];
            CharUtil.getChars(chars, 0, this._src, this._off, this._cch);
            return chars;
        }
        
        @Override
        public int getTextStart() {
            this.checkChanged();
            return this._off;
        }
        
        @Override
        public int getTextLength() {
            this.checkChanged();
            return this._cch;
        }
        
        @Override
        public int getTextCharacters(final int sourceStart, final char[] target, final int targetStart, int length) {
            this.checkChanged();
            if (length < 0) {
                throw new IndexOutOfBoundsException();
            }
            if (sourceStart > this._cch) {
                throw new IndexOutOfBoundsException();
            }
            if (sourceStart + length > this._cch) {
                length = this._cch - sourceStart;
            }
            CharUtil.getChars(target, targetStart, this._src, this._off + sourceStart, length);
            return length;
        }
        
        @Override
        public int getEventType() {
            this.checkChanged();
            return 4;
        }
        
        @Override
        public boolean hasName() {
            this.checkChanged();
            return false;
        }
        
        @Override
        public boolean hasNext() {
            this.checkChanged();
            return false;
        }
        
        @Override
        public boolean hasText() {
            this.checkChanged();
            return true;
        }
        
        @Override
        public boolean isCharacters() {
            this.checkChanged();
            return true;
        }
        
        @Override
        public boolean isEndElement() {
            this.checkChanged();
            return false;
        }
        
        @Override
        public boolean isStartElement() {
            this.checkChanged();
            return false;
        }
        
        @Override
        public int getAttributeCount() {
            throw new IllegalStateException();
        }
        
        @Override
        public String getAttributeLocalName(final int index) {
            throw new IllegalStateException();
        }
        
        @Override
        public QName getAttributeName(final int index) {
            throw new IllegalStateException();
        }
        
        @Override
        public String getAttributeNamespace(final int index) {
            throw new IllegalStateException();
        }
        
        @Override
        public String getAttributePrefix(final int index) {
            throw new IllegalStateException();
        }
        
        @Override
        public String getAttributeType(final int index) {
            throw new IllegalStateException();
        }
        
        @Override
        public String getAttributeValue(final int index) {
            throw new IllegalStateException();
        }
        
        @Override
        public String getAttributeValue(final String namespaceURI, final String localName) {
            throw new IllegalStateException();
        }
        
        @Override
        public String getElementText() {
            throw new IllegalStateException();
        }
        
        @Override
        public String getLocalName() {
            throw new IllegalStateException();
        }
        
        @Override
        public QName getName() {
            throw new IllegalStateException();
        }
        
        @Override
        public int getNamespaceCount() {
            throw new IllegalStateException();
        }
        
        @Override
        public String getNamespacePrefix(final int index) {
            throw new IllegalStateException();
        }
        
        @Override
        public String getNamespaceURI(final int index) {
            throw new IllegalStateException();
        }
        
        @Override
        public String getNamespaceURI() {
            throw new IllegalStateException();
        }
        
        @Override
        public String getPIData() {
            throw new IllegalStateException();
        }
        
        @Override
        public String getPITarget() {
            throw new IllegalStateException();
        }
        
        @Override
        public String getPrefix() {
            throw new IllegalStateException();
        }
        
        @Override
        public boolean isAttributeSpecified(final int index) {
            throw new IllegalStateException();
        }
        
        @Override
        public int next() {
            throw new IllegalStateException();
        }
        
        @Override
        public int nextTag() {
            throw new IllegalStateException();
        }
        
        @Override
        public String getPublicId() {
            throw new IllegalStateException();
        }
        
        @Override
        public String getSystemId() {
            throw new IllegalStateException();
        }
    }
    
    private abstract static class Jsr173GateWay
    {
        Locale _l;
        XMLStreamReaderBase _xs;
        
        public Jsr173GateWay(final Locale l, final XMLStreamReaderBase xs) {
            this._l = l;
            this._xs = xs;
        }
    }
    
    private static final class SyncedJsr173 extends Jsr173GateWay implements XMLStreamReader, Location, NamespaceContext
    {
        public SyncedJsr173(final Locale l, final XMLStreamReaderBase xs) {
            super(l, xs);
        }
        
        @Override
        public Object getProperty(final String name) {
            synchronized (this._l) {
                this._l.enter();
                try {
                    return this._xs.getProperty(name);
                }
                finally {
                    this._l.exit();
                }
            }
        }
        
        @Override
        public int next() throws XMLStreamException {
            synchronized (this._l) {
                this._l.enter();
                try {
                    return this._xs.next();
                }
                finally {
                    this._l.exit();
                }
            }
        }
        
        @Override
        public void require(final int type, final String namespaceURI, final String localName) throws XMLStreamException {
            synchronized (this._l) {
                this._l.enter();
                try {
                    this._xs.require(type, namespaceURI, localName);
                }
                finally {
                    this._l.exit();
                }
            }
        }
        
        @Override
        public String getElementText() throws XMLStreamException {
            synchronized (this._l) {
                this._l.enter();
                try {
                    return this._xs.getElementText();
                }
                finally {
                    this._l.exit();
                }
            }
        }
        
        @Override
        public int nextTag() throws XMLStreamException {
            synchronized (this._l) {
                this._l.enter();
                try {
                    return this._xs.nextTag();
                }
                finally {
                    this._l.exit();
                }
            }
        }
        
        @Override
        public boolean hasNext() throws XMLStreamException {
            synchronized (this._l) {
                this._l.enter();
                try {
                    return this._xs.hasNext();
                }
                finally {
                    this._l.exit();
                }
            }
        }
        
        @Override
        public void close() throws XMLStreamException {
            synchronized (this._l) {
                this._l.enter();
                try {
                    this._xs.close();
                }
                finally {
                    this._l.exit();
                }
            }
        }
        
        @Override
        public String getNamespaceURI(final String prefix) {
            synchronized (this._l) {
                this._l.enter();
                try {
                    return this._xs.getNamespaceURI(prefix);
                }
                finally {
                    this._l.exit();
                }
            }
        }
        
        @Override
        public boolean isStartElement() {
            synchronized (this._l) {
                this._l.enter();
                try {
                    return this._xs.isStartElement();
                }
                finally {
                    this._l.exit();
                }
            }
        }
        
        @Override
        public boolean isEndElement() {
            synchronized (this._l) {
                this._l.enter();
                try {
                    return this._xs.isEndElement();
                }
                finally {
                    this._l.exit();
                }
            }
        }
        
        @Override
        public boolean isCharacters() {
            synchronized (this._l) {
                this._l.enter();
                try {
                    return this._xs.isCharacters();
                }
                finally {
                    this._l.exit();
                }
            }
        }
        
        @Override
        public boolean isWhiteSpace() {
            synchronized (this._l) {
                this._l.enter();
                try {
                    return this._xs.isWhiteSpace();
                }
                finally {
                    this._l.exit();
                }
            }
        }
        
        @Override
        public String getAttributeValue(final String namespaceURI, final String localName) {
            synchronized (this._l) {
                this._l.enter();
                try {
                    return this._xs.getAttributeValue(namespaceURI, localName);
                }
                finally {
                    this._l.exit();
                }
            }
        }
        
        @Override
        public int getAttributeCount() {
            synchronized (this._l) {
                this._l.enter();
                try {
                    return this._xs.getAttributeCount();
                }
                finally {
                    this._l.exit();
                }
            }
        }
        
        @Override
        public QName getAttributeName(final int index) {
            synchronized (this._l) {
                this._l.enter();
                try {
                    return this._xs.getAttributeName(index);
                }
                finally {
                    this._l.exit();
                }
            }
        }
        
        @Override
        public String getAttributeNamespace(final int index) {
            synchronized (this._l) {
                this._l.enter();
                try {
                    return this._xs.getAttributeNamespace(index);
                }
                finally {
                    this._l.exit();
                }
            }
        }
        
        @Override
        public String getAttributeLocalName(final int index) {
            synchronized (this._l) {
                this._l.enter();
                try {
                    return this._xs.getAttributeLocalName(index);
                }
                finally {
                    this._l.exit();
                }
            }
        }
        
        @Override
        public String getAttributePrefix(final int index) {
            synchronized (this._l) {
                this._l.enter();
                try {
                    return this._xs.getAttributePrefix(index);
                }
                finally {
                    this._l.exit();
                }
            }
        }
        
        @Override
        public String getAttributeType(final int index) {
            synchronized (this._l) {
                this._l.enter();
                try {
                    return this._xs.getAttributeType(index);
                }
                finally {
                    this._l.exit();
                }
            }
        }
        
        @Override
        public String getAttributeValue(final int index) {
            synchronized (this._l) {
                this._l.enter();
                try {
                    return this._xs.getAttributeValue(index);
                }
                finally {
                    this._l.exit();
                }
            }
        }
        
        @Override
        public boolean isAttributeSpecified(final int index) {
            synchronized (this._l) {
                this._l.enter();
                try {
                    return this._xs.isAttributeSpecified(index);
                }
                finally {
                    this._l.exit();
                }
            }
        }
        
        @Override
        public int getNamespaceCount() {
            synchronized (this._l) {
                this._l.enter();
                try {
                    return this._xs.getNamespaceCount();
                }
                finally {
                    this._l.exit();
                }
            }
        }
        
        @Override
        public String getNamespacePrefix(final int index) {
            synchronized (this._l) {
                this._l.enter();
                try {
                    return this._xs.getNamespacePrefix(index);
                }
                finally {
                    this._l.exit();
                }
            }
        }
        
        @Override
        public String getNamespaceURI(final int index) {
            synchronized (this._l) {
                this._l.enter();
                try {
                    return this._xs.getNamespaceURI(index);
                }
                finally {
                    this._l.exit();
                }
            }
        }
        
        @Override
        public NamespaceContext getNamespaceContext() {
            return this;
        }
        
        @Override
        public int getEventType() {
            synchronized (this._l) {
                this._l.enter();
                try {
                    return this._xs.getEventType();
                }
                finally {
                    this._l.exit();
                }
            }
        }
        
        @Override
        public String getText() {
            synchronized (this._l) {
                this._l.enter();
                try {
                    return this._xs.getText();
                }
                finally {
                    this._l.exit();
                }
            }
        }
        
        @Override
        public char[] getTextCharacters() {
            synchronized (this._l) {
                this._l.enter();
                try {
                    return this._xs.getTextCharacters();
                }
                finally {
                    this._l.exit();
                }
            }
        }
        
        @Override
        public int getTextCharacters(final int sourceStart, final char[] target, final int targetStart, final int length) throws XMLStreamException {
            synchronized (this._l) {
                this._l.enter();
                try {
                    return this._xs.getTextCharacters(sourceStart, target, targetStart, length);
                }
                finally {
                    this._l.exit();
                }
            }
        }
        
        @Override
        public int getTextStart() {
            synchronized (this._l) {
                this._l.enter();
                try {
                    return this._xs.getTextStart();
                }
                finally {
                    this._l.exit();
                }
            }
        }
        
        @Override
        public int getTextLength() {
            synchronized (this._l) {
                this._l.enter();
                try {
                    return this._xs.getTextLength();
                }
                finally {
                    this._l.exit();
                }
            }
        }
        
        @Override
        public String getEncoding() {
            synchronized (this._l) {
                this._l.enter();
                try {
                    return this._xs.getEncoding();
                }
                finally {
                    this._l.exit();
                }
            }
        }
        
        @Override
        public boolean hasText() {
            synchronized (this._l) {
                this._l.enter();
                try {
                    return this._xs.hasText();
                }
                finally {
                    this._l.exit();
                }
            }
        }
        
        @Override
        public Location getLocation() {
            synchronized (this._l) {
                this._l.enter();
                try {
                    return this._xs.getLocation();
                }
                finally {
                    this._l.exit();
                }
            }
        }
        
        @Override
        public QName getName() {
            synchronized (this._l) {
                this._l.enter();
                try {
                    return this._xs.getName();
                }
                finally {
                    this._l.exit();
                }
            }
        }
        
        @Override
        public String getLocalName() {
            synchronized (this._l) {
                this._l.enter();
                try {
                    return this._xs.getLocalName();
                }
                finally {
                    this._l.exit();
                }
            }
        }
        
        @Override
        public boolean hasName() {
            synchronized (this._l) {
                this._l.enter();
                try {
                    return this._xs.hasName();
                }
                finally {
                    this._l.exit();
                }
            }
        }
        
        @Override
        public String getNamespaceURI() {
            synchronized (this._l) {
                this._l.enter();
                try {
                    return this._xs.getNamespaceURI();
                }
                finally {
                    this._l.exit();
                }
            }
        }
        
        @Override
        public String getPrefix() {
            synchronized (this._l) {
                this._l.enter();
                try {
                    return this._xs.getPrefix();
                }
                finally {
                    this._l.exit();
                }
            }
        }
        
        @Override
        public String getVersion() {
            synchronized (this._l) {
                this._l.enter();
                try {
                    return this._xs.getVersion();
                }
                finally {
                    this._l.exit();
                }
            }
        }
        
        @Override
        public boolean isStandalone() {
            synchronized (this._l) {
                this._l.enter();
                try {
                    return this._xs.isStandalone();
                }
                finally {
                    this._l.exit();
                }
            }
        }
        
        @Override
        public boolean standaloneSet() {
            synchronized (this._l) {
                this._l.enter();
                try {
                    return this._xs.standaloneSet();
                }
                finally {
                    this._l.exit();
                }
            }
        }
        
        @Override
        public String getCharacterEncodingScheme() {
            synchronized (this._l) {
                this._l.enter();
                try {
                    return this._xs.getCharacterEncodingScheme();
                }
                finally {
                    this._l.exit();
                }
            }
        }
        
        @Override
        public String getPITarget() {
            synchronized (this._l) {
                this._l.enter();
                try {
                    return this._xs.getPITarget();
                }
                finally {
                    this._l.exit();
                }
            }
        }
        
        @Override
        public String getPIData() {
            synchronized (this._l) {
                this._l.enter();
                try {
                    return this._xs.getPIData();
                }
                finally {
                    this._l.exit();
                }
            }
        }
        
        @Override
        public String getPrefix(final String namespaceURI) {
            synchronized (this._l) {
                this._l.enter();
                try {
                    return this._xs.getPrefix(namespaceURI);
                }
                finally {
                    this._l.exit();
                }
            }
        }
        
        @Override
        public Iterator getPrefixes(final String namespaceURI) {
            synchronized (this._l) {
                this._l.enter();
                try {
                    return this._xs.getPrefixes(namespaceURI);
                }
                finally {
                    this._l.exit();
                }
            }
        }
        
        @Override
        public int getCharacterOffset() {
            synchronized (this._l) {
                this._l.enter();
                try {
                    return this._xs.getCharacterOffset();
                }
                finally {
                    this._l.exit();
                }
            }
        }
        
        @Override
        public int getColumnNumber() {
            synchronized (this._l) {
                this._l.enter();
                try {
                    return this._xs.getColumnNumber();
                }
                finally {
                    this._l.exit();
                }
            }
        }
        
        @Override
        public int getLineNumber() {
            synchronized (this._l) {
                this._l.enter();
                try {
                    return this._xs.getLineNumber();
                }
                finally {
                    this._l.exit();
                }
            }
        }
        
        public String getLocationURI() {
            synchronized (this._l) {
                this._l.enter();
                try {
                    return this._xs.getLocationURI();
                }
                finally {
                    this._l.exit();
                }
            }
        }
        
        @Override
        public String getPublicId() {
            synchronized (this._l) {
                this._l.enter();
                try {
                    return this._xs.getPublicId();
                }
                finally {
                    this._l.exit();
                }
            }
        }
        
        @Override
        public String getSystemId() {
            synchronized (this._l) {
                this._l.enter();
                try {
                    return this._xs.getSystemId();
                }
                finally {
                    this._l.exit();
                }
            }
        }
    }
    
    private static final class UnsyncedJsr173 extends Jsr173GateWay implements XMLStreamReader, Location, NamespaceContext
    {
        public UnsyncedJsr173(final Locale l, final XMLStreamReaderBase xs) {
            super(l, xs);
        }
        
        @Override
        public Object getProperty(final String name) {
            try {
                this._l.enter();
                return this._xs.getProperty(name);
            }
            finally {
                this._l.exit();
            }
        }
        
        @Override
        public int next() throws XMLStreamException {
            try {
                this._l.enter();
                return this._xs.next();
            }
            finally {
                this._l.exit();
            }
        }
        
        @Override
        public void require(final int type, final String namespaceURI, final String localName) throws XMLStreamException {
            try {
                this._l.enter();
                this._xs.require(type, namespaceURI, localName);
            }
            finally {
                this._l.exit();
            }
        }
        
        @Override
        public String getElementText() throws XMLStreamException {
            try {
                this._l.enter();
                return this._xs.getElementText();
            }
            finally {
                this._l.exit();
            }
        }
        
        @Override
        public int nextTag() throws XMLStreamException {
            try {
                this._l.enter();
                return this._xs.nextTag();
            }
            finally {
                this._l.exit();
            }
        }
        
        @Override
        public boolean hasNext() throws XMLStreamException {
            try {
                this._l.enter();
                return this._xs.hasNext();
            }
            finally {
                this._l.exit();
            }
        }
        
        @Override
        public void close() throws XMLStreamException {
            try {
                this._l.enter();
                this._xs.close();
            }
            finally {
                this._l.exit();
            }
        }
        
        @Override
        public String getNamespaceURI(final String prefix) {
            try {
                this._l.enter();
                return this._xs.getNamespaceURI(prefix);
            }
            finally {
                this._l.exit();
            }
        }
        
        @Override
        public boolean isStartElement() {
            try {
                this._l.enter();
                return this._xs.isStartElement();
            }
            finally {
                this._l.exit();
            }
        }
        
        @Override
        public boolean isEndElement() {
            try {
                this._l.enter();
                return this._xs.isEndElement();
            }
            finally {
                this._l.exit();
            }
        }
        
        @Override
        public boolean isCharacters() {
            try {
                this._l.enter();
                return this._xs.isCharacters();
            }
            finally {
                this._l.exit();
            }
        }
        
        @Override
        public boolean isWhiteSpace() {
            try {
                this._l.enter();
                return this._xs.isWhiteSpace();
            }
            finally {
                this._l.exit();
            }
        }
        
        @Override
        public String getAttributeValue(final String namespaceURI, final String localName) {
            try {
                this._l.enter();
                return this._xs.getAttributeValue(namespaceURI, localName);
            }
            finally {
                this._l.exit();
            }
        }
        
        @Override
        public int getAttributeCount() {
            try {
                this._l.enter();
                return this._xs.getAttributeCount();
            }
            finally {
                this._l.exit();
            }
        }
        
        @Override
        public QName getAttributeName(final int index) {
            try {
                this._l.enter();
                return this._xs.getAttributeName(index);
            }
            finally {
                this._l.exit();
            }
        }
        
        @Override
        public String getAttributeNamespace(final int index) {
            try {
                this._l.enter();
                return this._xs.getAttributeNamespace(index);
            }
            finally {
                this._l.exit();
            }
        }
        
        @Override
        public String getAttributeLocalName(final int index) {
            try {
                this._l.enter();
                return this._xs.getAttributeLocalName(index);
            }
            finally {
                this._l.exit();
            }
        }
        
        @Override
        public String getAttributePrefix(final int index) {
            try {
                this._l.enter();
                return this._xs.getAttributePrefix(index);
            }
            finally {
                this._l.exit();
            }
        }
        
        @Override
        public String getAttributeType(final int index) {
            try {
                this._l.enter();
                return this._xs.getAttributeType(index);
            }
            finally {
                this._l.exit();
            }
        }
        
        @Override
        public String getAttributeValue(final int index) {
            try {
                this._l.enter();
                return this._xs.getAttributeValue(index);
            }
            finally {
                this._l.exit();
            }
        }
        
        @Override
        public boolean isAttributeSpecified(final int index) {
            try {
                this._l.enter();
                return this._xs.isAttributeSpecified(index);
            }
            finally {
                this._l.exit();
            }
        }
        
        @Override
        public int getNamespaceCount() {
            try {
                this._l.enter();
                return this._xs.getNamespaceCount();
            }
            finally {
                this._l.exit();
            }
        }
        
        @Override
        public String getNamespacePrefix(final int index) {
            try {
                this._l.enter();
                return this._xs.getNamespacePrefix(index);
            }
            finally {
                this._l.exit();
            }
        }
        
        @Override
        public String getNamespaceURI(final int index) {
            try {
                this._l.enter();
                return this._xs.getNamespaceURI(index);
            }
            finally {
                this._l.exit();
            }
        }
        
        @Override
        public NamespaceContext getNamespaceContext() {
            return this;
        }
        
        @Override
        public int getEventType() {
            try {
                this._l.enter();
                return this._xs.getEventType();
            }
            finally {
                this._l.exit();
            }
        }
        
        @Override
        public String getText() {
            try {
                this._l.enter();
                return this._xs.getText();
            }
            finally {
                this._l.exit();
            }
        }
        
        @Override
        public char[] getTextCharacters() {
            try {
                this._l.enter();
                return this._xs.getTextCharacters();
            }
            finally {
                this._l.exit();
            }
        }
        
        @Override
        public int getTextCharacters(final int sourceStart, final char[] target, final int targetStart, final int length) throws XMLStreamException {
            try {
                this._l.enter();
                return this._xs.getTextCharacters(sourceStart, target, targetStart, length);
            }
            finally {
                this._l.exit();
            }
        }
        
        @Override
        public int getTextStart() {
            try {
                this._l.enter();
                return this._xs.getTextStart();
            }
            finally {
                this._l.exit();
            }
        }
        
        @Override
        public int getTextLength() {
            try {
                this._l.enter();
                return this._xs.getTextLength();
            }
            finally {
                this._l.exit();
            }
        }
        
        @Override
        public String getEncoding() {
            try {
                this._l.enter();
                return this._xs.getEncoding();
            }
            finally {
                this._l.exit();
            }
        }
        
        @Override
        public boolean hasText() {
            try {
                this._l.enter();
                return this._xs.hasText();
            }
            finally {
                this._l.exit();
            }
        }
        
        @Override
        public Location getLocation() {
            try {
                this._l.enter();
                return this._xs.getLocation();
            }
            finally {
                this._l.exit();
            }
        }
        
        @Override
        public QName getName() {
            try {
                this._l.enter();
                return this._xs.getName();
            }
            finally {
                this._l.exit();
            }
        }
        
        @Override
        public String getLocalName() {
            try {
                this._l.enter();
                return this._xs.getLocalName();
            }
            finally {
                this._l.exit();
            }
        }
        
        @Override
        public boolean hasName() {
            try {
                this._l.enter();
                return this._xs.hasName();
            }
            finally {
                this._l.exit();
            }
        }
        
        @Override
        public String getNamespaceURI() {
            try {
                this._l.enter();
                return this._xs.getNamespaceURI();
            }
            finally {
                this._l.exit();
            }
        }
        
        @Override
        public String getPrefix() {
            try {
                this._l.enter();
                return this._xs.getPrefix();
            }
            finally {
                this._l.exit();
            }
        }
        
        @Override
        public String getVersion() {
            try {
                this._l.enter();
                return this._xs.getVersion();
            }
            finally {
                this._l.exit();
            }
        }
        
        @Override
        public boolean isStandalone() {
            try {
                this._l.enter();
                return this._xs.isStandalone();
            }
            finally {
                this._l.exit();
            }
        }
        
        @Override
        public boolean standaloneSet() {
            try {
                this._l.enter();
                return this._xs.standaloneSet();
            }
            finally {
                this._l.exit();
            }
        }
        
        @Override
        public String getCharacterEncodingScheme() {
            try {
                this._l.enter();
                return this._xs.getCharacterEncodingScheme();
            }
            finally {
                this._l.exit();
            }
        }
        
        @Override
        public String getPITarget() {
            try {
                this._l.enter();
                return this._xs.getPITarget();
            }
            finally {
                this._l.exit();
            }
        }
        
        @Override
        public String getPIData() {
            try {
                this._l.enter();
                return this._xs.getPIData();
            }
            finally {
                this._l.exit();
            }
        }
        
        @Override
        public String getPrefix(final String namespaceURI) {
            try {
                this._l.enter();
                return this._xs.getPrefix(namespaceURI);
            }
            finally {
                this._l.exit();
            }
        }
        
        @Override
        public Iterator getPrefixes(final String namespaceURI) {
            try {
                this._l.enter();
                return this._xs.getPrefixes(namespaceURI);
            }
            finally {
                this._l.exit();
            }
        }
        
        @Override
        public int getCharacterOffset() {
            try {
                this._l.enter();
                return this._xs.getCharacterOffset();
            }
            finally {
                this._l.exit();
            }
        }
        
        @Override
        public int getColumnNumber() {
            try {
                this._l.enter();
                return this._xs.getColumnNumber();
            }
            finally {
                this._l.exit();
            }
        }
        
        @Override
        public int getLineNumber() {
            synchronized (this._l) {
                this._l.enter();
                try {
                    return this._xs.getLineNumber();
                }
                finally {
                    this._l.exit();
                }
            }
        }
        
        public String getLocationURI() {
            synchronized (this._l) {
                this._l.enter();
                try {
                    return this._xs.getLocationURI();
                }
                finally {
                    this._l.exit();
                }
            }
        }
        
        @Override
        public String getPublicId() {
            synchronized (this._l) {
                this._l.enter();
                try {
                    return this._xs.getPublicId();
                }
                finally {
                    this._l.exit();
                }
            }
        }
        
        @Override
        public String getSystemId() {
            synchronized (this._l) {
                this._l.enter();
                try {
                    return this._xs.getSystemId();
                }
                finally {
                    this._l.exit();
                }
            }
        }
    }
}
