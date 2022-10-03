package org.apache.xmlbeans.impl.store;

import org.apache.xmlbeans.impl.soap.DetailEntry;
import org.apache.xmlbeans.impl.soap.SOAPFaultElement;
import org.apache.xmlbeans.impl.soap.Detail;
import org.apache.xmlbeans.impl.soap.SOAPHeaderElement;
import org.apache.xmlbeans.impl.soap.SOAPHeader;
import org.apache.xmlbeans.impl.soap.SOAPBodyElement;
import org.apache.xmlbeans.impl.soap.SOAPFault;
import org.apache.xmlbeans.impl.soap.SOAPBody;
import org.apache.xmlbeans.impl.soap.SOAPException;
import org.apache.xmlbeans.impl.soap.Name;
import org.apache.xmlbeans.impl.soap.SOAPElement;
import javax.xml.transform.Source;
import org.apache.xmlbeans.impl.soap.SOAPEnvelope;
import java.util.Iterator;
import org.apache.xmlbeans.impl.soap.SOAPPart;
import org.w3c.dom.TypeInfo;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Text;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.EntityReference;
import org.w3c.dom.Element;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Comment;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Attr;
import java.util.Hashtable;
import org.w3c.dom.UserDataHandler;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.apache.xmlbeans.XmlException;
import java.util.ArrayList;
import org.apache.xmlbeans.XmlOptions;
import java.util.Map;
import java.util.List;
import org.apache.xmlbeans.QNameSet;
import org.apache.xmlbeans.impl.common.ValidatorListener;
import org.apache.xmlbeans.impl.values.TypeStoreVisitor;
import org.apache.xmlbeans.SchemaField;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.impl.common.XmlLocale;
import org.apache.xmlbeans.SchemaTypeLoader;
import org.apache.xmlbeans.impl.values.NamespaceManager;
import org.apache.xmlbeans.XmlBeans;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.common.QNameHelper;
import org.apache.xmlbeans.impl.values.TypeStoreUserFactory;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.CDataBookmark;
import java.io.PrintStream;
import org.apache.xmlbeans.impl.values.TypeStoreUser;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.impl.values.TypeStore;

abstract class Xobj implements TypeStore
{
    static final int TEXT = 0;
    static final int ROOT = 1;
    static final int ELEM = 2;
    static final int ATTR = 3;
    static final int COMMENT = 4;
    static final int PROCINST = 5;
    static final int END_POS = -1;
    static final int NO_POS = -2;
    static final int VACANT = 256;
    static final int STABLE_USER = 512;
    static final int INHIBIT_DISCONNECT = 1024;
    Locale _locale;
    QName _name;
    Cur _embedded;
    Bookmark _bookmarks;
    int _bits;
    Xobj _parent;
    Xobj _nextSibling;
    Xobj _prevSibling;
    Xobj _firstChild;
    Xobj _lastChild;
    Object _srcValue;
    Object _srcAfter;
    int _offValue;
    int _offAfter;
    int _cchValue;
    int _cchAfter;
    DomImpl.CharNode _charNodesValue;
    DomImpl.CharNode _charNodesAfter;
    TypeStoreUser _user;
    
    Xobj(final Locale l, final int kind, final int domType) {
        assert kind == 5;
        this._locale = l;
        this._bits = (domType << 4) + kind;
    }
    
    final boolean entered() {
        return this._locale.entered();
    }
    
    final int kind() {
        return this._bits & 0xF;
    }
    
    final int domType() {
        return (this._bits & 0xF0) >> 4;
    }
    
    final boolean isRoot() {
        return this.kind() == 1;
    }
    
    final boolean isAttr() {
        return this.kind() == 3;
    }
    
    final boolean isElem() {
        return this.kind() == 2;
    }
    
    final boolean isProcinst() {
        return this.kind() == 5;
    }
    
    final boolean isComment() {
        return this.kind() == 4;
    }
    
    final boolean isContainer() {
        return Cur.kindIsContainer(this.kind());
    }
    
    final boolean isUserNode() {
        final int k = this.kind();
        return k == 2 || k == 1 || (k == 3 && !this.isXmlns());
    }
    
    final boolean isNormalAttr() {
        return this.isAttr() && !Locale.isXmlns(this._name);
    }
    
    final boolean isXmlns() {
        return this.isAttr() && Locale.isXmlns(this._name);
    }
    
    final int cchValue() {
        return this._cchValue;
    }
    
    final int cchAfter() {
        return this._cchAfter;
    }
    
    final int posAfter() {
        return 2 + this._cchValue;
    }
    
    final int posMax() {
        return 2 + this._cchValue + this._cchAfter;
    }
    
    final String getXmlnsPrefix() {
        return Locale.xmlnsPrefix(this._name);
    }
    
    final String getXmlnsUri() {
        return this.getValueAsString();
    }
    
    final boolean hasTextEnsureOccupancy() {
        this.ensureOccupancy();
        return this.hasTextNoEnsureOccupancy();
    }
    
    final boolean hasTextNoEnsureOccupancy() {
        if (this._cchValue > 0) {
            return true;
        }
        final Xobj lastAttr = this.lastAttr();
        return lastAttr != null && lastAttr._cchAfter > 0;
    }
    
    final boolean hasAttrs() {
        return this._firstChild != null && this._firstChild.isAttr();
    }
    
    final boolean hasChildren() {
        return this._lastChild != null && !this._lastChild.isAttr();
    }
    
    protected final int getDomZeroOneChildren() {
        if (this._firstChild == null && this._srcValue == null && this._charNodesValue == null) {
            return 0;
        }
        if (this._lastChild != null && this._lastChild.isAttr() && this._lastChild._charNodesAfter == null && this._lastChild._srcAfter == null && this._srcValue == null && this._charNodesValue == null) {
            return 0;
        }
        if (this._firstChild == this._lastChild && this._firstChild != null && !this._firstChild.isAttr() && this._srcValue == null && this._charNodesValue == null && this._firstChild._srcAfter == null) {
            return 1;
        }
        if (this._firstChild == null && this._srcValue != null && (this._charNodesValue == null || (this._charNodesValue._next == null && this._charNodesValue._cch == this._cchValue))) {
            return 1;
        }
        final Xobj lastAttr = this.lastAttr();
        final Xobj node = (lastAttr == null) ? null : lastAttr._nextSibling;
        if (lastAttr != null && lastAttr._srcAfter == null && node != null && node._srcAfter == null && node._nextSibling == null) {
            return 1;
        }
        return 2;
    }
    
    protected final boolean isFirstChildPtrDomUsable() {
        if (this._firstChild == null && this._srcValue == null && this._charNodesValue == null) {
            return true;
        }
        if (this._firstChild == null || this._firstChild.isAttr() || this._srcValue != null || this._charNodesValue != null) {
            return false;
        }
        assert this._firstChild instanceof NodeXobj : "wrong node type";
        return true;
    }
    
    protected final boolean isNextSiblingPtrDomUsable() {
        if (this._charNodesAfter != null || this._srcAfter != null) {
            return false;
        }
        assert !(!(this._nextSibling instanceof NodeXobj)) : "wrong node type";
        return true;
    }
    
    protected final boolean isExistingCharNodesValueUsable() {
        return this._srcValue != null && (this._charNodesValue != null && this._charNodesValue._next == null && this._charNodesValue._cch == this._cchValue);
    }
    
    protected final boolean isCharNodesValueUsable() {
        return this.isExistingCharNodesValueUsable() || (this._charNodesValue = Cur.updateCharNodes(this._locale, this, this._charNodesValue, this._cchValue)) != null;
    }
    
    protected final boolean isCharNodesAfterUsable() {
        return this._srcAfter != null && ((this._charNodesAfter != null && this._charNodesAfter._next == null && this._charNodesAfter._cch == this._cchAfter) || (this._charNodesAfter = Cur.updateCharNodes(this._locale, this, this._charNodesAfter, this._cchAfter)) != null);
    }
    
    final Xobj lastAttr() {
        if (this._firstChild == null || !this._firstChild.isAttr()) {
            return null;
        }
        Xobj lastAttr;
        for (lastAttr = this._firstChild; lastAttr._nextSibling != null && lastAttr._nextSibling.isAttr(); lastAttr = lastAttr._nextSibling) {}
        return lastAttr;
    }
    
    abstract DomImpl.Dom getDom();
    
    abstract Xobj newNode(final Locale p0);
    
    final int cchLeft(int p) {
        if (this.isRoot() && p == 0) {
            return 0;
        }
        final Xobj x = this.getDenormal(p);
        p = this.posTemp();
        final int pa = x.posAfter();
        final int n;
        return n - (((n = p) < pa) ? 1 : pa);
    }
    
    final int cchRight(final int p) {
        assert p < this.posMax();
        if (p <= 0) {
            return 0;
        }
        final int pa = this.posAfter();
        return (p < pa) ? (pa - p - 1) : (this.posMax() - p);
    }
    
    public final Locale locale() {
        return this._locale;
    }
    
    public final int nodeType() {
        return this.domType();
    }
    
    public final QName getQName() {
        return this._name;
    }
    
    public final Cur tempCur() {
        final Cur c = this._locale.tempCur();
        c.moveTo(this);
        return c;
    }
    
    public void dump(final PrintStream o, final Object ref) {
        Cur.dump(o, this, ref);
    }
    
    public void dump(final PrintStream o) {
        Cur.dump(o, this, this);
    }
    
    public void dump() {
        this.dump(System.out);
    }
    
    final Cur getEmbedded() {
        this._locale.embedCurs();
        return this._embedded;
    }
    
    final boolean inChars(final int p, Xobj xIn, int pIn, final int cch, final boolean includeEnd) {
        assert p > 0 && p < this.posMax() && p != this.posAfter() - 1 && cch > 0;
        assert xIn.isNormal(pIn);
        int offset;
        if (includeEnd) {
            if (xIn.isRoot() && pIn == 0) {
                return false;
            }
            xIn = xIn.getDenormal(pIn);
            pIn = xIn.posTemp();
            offset = 1;
        }
        else {
            offset = 0;
        }
        return xIn == this && pIn >= p && pIn < p + ((cch < 0) ? this.cchRight(p) : cch) + offset;
    }
    
    final boolean isJustAfterEnd(final Xobj x, final int p) {
        assert x.isNormal(p);
        return (!x.isRoot() || p != 0) && ((x == this) ? (p == this.posAfter()) : (x.getDenormal(p) == this && x.posTemp() == this.posAfter()));
    }
    
    final boolean isInSameTree(Xobj x) {
        if (this._locale != x._locale) {
            return false;
        }
        for (Xobj y = this; y != x; y = y._parent) {
            if (y._parent == null) {
                while (x != this) {
                    if (x._parent == null) {
                        return x == y;
                    }
                    x = x._parent;
                }
                return true;
            }
        }
        return true;
    }
    
    final boolean contains(final Cur c) {
        assert c.isNormal();
        return this.contains(c._xobj, c._pos);
    }
    
    final boolean contains(Xobj x, final int p) {
        assert x.isNormal(p);
        if (this == x) {
            return p == -1 || (p > 0 && p < this.posAfter());
        }
        if (this._firstChild == null) {
            return false;
        }
        while (x != null) {
            if (x == this) {
                return true;
            }
            x = x._parent;
        }
        return false;
    }
    
    final Bookmark setBookmark(final int p, final Object key, final Object value) {
        assert this.isNormal(p);
        Bookmark b = this._bookmarks;
        while (b != null) {
            if (p == b._pos && key == b._key) {
                if (value == null) {
                    this._bookmarks = b.listRemove(this._bookmarks);
                    return null;
                }
                b._value = value;
                return b;
            }
            else {
                b = b._next;
            }
        }
        if (value == null) {
            return null;
        }
        b = new Bookmark();
        b._xobj = this;
        b._pos = p;
        b._key = key;
        b._value = value;
        this._bookmarks = b.listInsert(this._bookmarks);
        return b;
    }
    
    final boolean hasBookmark(final Object key, final int pos) {
        for (Bookmark b = this._bookmarks; b != null; b = b._next) {
            if (b._pos == pos && key == b._key) {
                return true;
            }
        }
        return false;
    }
    
    final Xobj findXmlnsForPrefix(final String prefix) {
        assert this.isContainer() && prefix != null;
        for (Xobj c = this; c != null; c = c._parent) {
            for (Xobj a = c.firstAttr(); a != null; a = a.nextAttr()) {
                if (a.isXmlns() && a.getXmlnsPrefix().equals(prefix)) {
                    return a;
                }
            }
        }
        return null;
    }
    
    final boolean removeAttr(final QName name) {
        assert this.isContainer();
        Xobj a = this.getAttr(name);
        if (a == null) {
            return false;
        }
        final Cur c = a.tempCur();
        while (true) {
            c.moveNode(null);
            a = this.getAttr(name);
            if (a == null) {
                break;
            }
            c.moveTo(a);
        }
        c.release();
        return true;
    }
    
    final Xobj setAttr(final QName name, final String value) {
        assert this.isContainer();
        final Cur c = this.tempCur();
        if (c.toAttr(name)) {
            c.removeFollowingAttrs();
        }
        else {
            c.next();
            c.createAttr(name);
        }
        c.setValue(value);
        final Xobj a = c._xobj;
        c.release();
        return a;
    }
    
    final void setName(final QName newName) {
        assert this.isAttr() || this.isProcinst();
        assert newName != null;
        if (!this._name.equals(newName) || !this._name.getPrefix().equals(newName.getPrefix())) {
            this._locale.notifyChange();
            final QName oldName = this._name;
            this._name = newName;
            if (this instanceof NamedNodeXobj) {
                final NamedNodeXobj me = (NamedNodeXobj)this;
                me._canHavePrefixUri = true;
            }
            if (!this.isProcinst()) {
                Xobj disconnectFromHere = this;
                if (this.isAttr() && this._parent != null) {
                    if (oldName.equals(Locale._xsiType) || newName.equals(Locale._xsiType)) {
                        disconnectFromHere = this._parent;
                    }
                    if (oldName.equals(Locale._xsiNil) || newName.equals(Locale._xsiNil)) {
                        this._parent.invalidateNil();
                    }
                }
                disconnectFromHere.disconnectNonRootUsers();
            }
            final Locale locale = this._locale;
            ++locale._versionAll;
            final Locale locale2 = this._locale;
            ++locale2._versionSansText;
        }
    }
    
    final Xobj ensureParent() {
        assert !this.isRoot() && this.cchAfter() == 0;
        return (this._parent == null) ? new DocumentFragXobj(this._locale).appendXobj(this) : this._parent;
    }
    
    final Xobj firstAttr() {
        return (this._firstChild == null || !this._firstChild.isAttr()) ? null : this._firstChild;
    }
    
    final Xobj nextAttr() {
        if (this._firstChild != null && this._firstChild.isAttr()) {
            return this._firstChild;
        }
        if (this._nextSibling != null && this._nextSibling.isAttr()) {
            return this._nextSibling;
        }
        return null;
    }
    
    final boolean isValid() {
        return !this.isVacant() || (this._cchValue == 0 && this._user != null);
    }
    
    final int posTemp() {
        return this._locale._posTemp;
    }
    
    final Xobj getNormal(int p) {
        assert p >= 0 && p <= this.posMax();
        Xobj x = this;
        if (p == x.posMax()) {
            if (x._nextSibling != null) {
                x = x._nextSibling;
                p = 0;
            }
            else {
                x = x.ensureParent();
                p = -1;
            }
        }
        else if (p == x.posAfter() - 1) {
            p = -1;
        }
        this._locale._posTemp = p;
        return x;
    }
    
    final Xobj getDenormal(int p) {
        assert p > 0;
        Xobj x = this;
        if (p == 0) {
            if (x._prevSibling == null) {
                x = x.ensureParent();
                p = x.posAfter() - 1;
            }
            else {
                x = x._prevSibling;
                p = x.posMax();
            }
        }
        else if (p == -1) {
            if (x._lastChild == null) {
                p = x.posAfter() - 1;
            }
            else {
                x = x._lastChild;
                p = x.posMax();
            }
        }
        this._locale._posTemp = p;
        return x;
    }
    
    final boolean isNormal(final int p) {
        if (!this.isValid()) {
            return false;
        }
        if (p == -1 || p == 0) {
            return true;
        }
        if (p < 0 || p >= this.posMax()) {
            return false;
        }
        if (p >= this.posAfter()) {
            if (this.isRoot()) {
                return false;
            }
            if (this._nextSibling != null && this._nextSibling.isAttr()) {
                return false;
            }
            if (this._parent == null || !this._parent.isContainer()) {
                return false;
            }
        }
        return p != this.posAfter() - 1;
    }
    
    final Xobj walk(final Xobj root, final boolean walkChildren) {
        if (this._firstChild != null && walkChildren) {
            return this._firstChild;
        }
        for (Xobj x = this; x != root; x = x._parent) {
            if (x._nextSibling != null) {
                return x._nextSibling;
            }
        }
        return null;
    }
    
    final Xobj removeXobj() {
        if (this._parent != null) {
            if (this._parent._firstChild == this) {
                this._parent._firstChild = this._nextSibling;
            }
            if (this._parent._lastChild == this) {
                this._parent._lastChild = this._prevSibling;
            }
            if (this._prevSibling != null) {
                this._prevSibling._nextSibling = this._nextSibling;
            }
            if (this._nextSibling != null) {
                this._nextSibling._prevSibling = this._prevSibling;
            }
            this._parent = null;
            this._prevSibling = null;
            this._nextSibling = null;
        }
        return this;
    }
    
    final Xobj insertXobj(final Xobj s) {
        assert this._locale == s._locale;
        assert !s.isRoot() && !this.isRoot();
        assert s._parent == null;
        assert s._prevSibling == null;
        assert s._nextSibling == null;
        this.ensureParent();
        s._parent = this._parent;
        s._prevSibling = this._prevSibling;
        s._nextSibling = this;
        if (this._prevSibling != null) {
            this._prevSibling._nextSibling = s;
        }
        else {
            this._parent._firstChild = s;
        }
        this._prevSibling = s;
        return this;
    }
    
    final Xobj appendXobj(final Xobj c) {
        assert this._locale == c._locale;
        assert !c.isRoot();
        assert c._parent == null;
        assert c._prevSibling == null;
        assert c._nextSibling == null;
        assert this._firstChild != null;
        c._parent = this;
        c._prevSibling = this._lastChild;
        if (this._lastChild == null) {
            this._firstChild = c;
        }
        else {
            this._lastChild._nextSibling = c;
        }
        this._lastChild = c;
        return this;
    }
    
    final void removeXobjs(Xobj first, final Xobj last) {
        assert last._locale == first._locale;
        assert first._parent == this;
        assert last._parent == this;
        if (this._firstChild == first) {
            this._firstChild = last._nextSibling;
        }
        if (this._lastChild == last) {
            this._lastChild = first._prevSibling;
        }
        if (first._prevSibling != null) {
            first._prevSibling._nextSibling = last._nextSibling;
        }
        if (last._nextSibling != null) {
            last._nextSibling._prevSibling = first._prevSibling;
        }
        first._prevSibling = null;
        last._nextSibling = null;
        while (first != null) {
            first._parent = null;
            first = first._nextSibling;
        }
    }
    
    final void insertXobjs(Xobj first, final Xobj last) {
        assert this._locale == first._locale;
        assert last._locale == first._locale;
        assert first._parent == null && last._parent == null;
        assert first._prevSibling == null;
        assert last._nextSibling == null;
        first._prevSibling = this._prevSibling;
        last._nextSibling = this;
        if (this._prevSibling != null) {
            this._prevSibling._nextSibling = first;
        }
        else {
            this._parent._firstChild = first;
        }
        this._prevSibling = last;
        while (first != this) {
            first._parent = this._parent;
            first = first._nextSibling;
        }
    }
    
    final void appendXobjs(Xobj first, final Xobj last) {
        assert this._locale == first._locale;
        assert last._locale == first._locale;
        assert first._parent == null && last._parent == null;
        assert first._prevSibling == null;
        assert last._nextSibling == null;
        assert !first.isRoot();
        first._prevSibling = this._lastChild;
        if (this._lastChild == null) {
            this._firstChild = first;
        }
        else {
            this._lastChild._nextSibling = first;
        }
        this._lastChild = last;
        while (first != null) {
            first._parent = this;
            first = first._nextSibling;
        }
    }
    
    static final void disbandXobjs(Xobj first, final Xobj last) {
        assert last._locale == first._locale;
        assert first._parent == null && last._parent == null;
        assert first._prevSibling == null;
        assert last._nextSibling == null;
        assert !first.isRoot();
        while (first != null) {
            final Xobj next = first._nextSibling;
            final Xobj xobj = first;
            final Xobj xobj2 = first;
            final Xobj xobj3 = null;
            xobj2._prevSibling = xobj3;
            xobj._nextSibling = xobj3;
            first = next;
        }
    }
    
    final void invalidateSpecialAttr(final Xobj newParent) {
        if (this.isAttr()) {
            if (this._name.equals(Locale._xsiType)) {
                if (this._parent != null) {
                    this._parent.disconnectNonRootUsers();
                }
                if (newParent != null) {
                    newParent.disconnectNonRootUsers();
                }
            }
            if (this._name.equals(Locale._xsiNil)) {
                if (this._parent != null) {
                    this._parent.invalidateNil();
                }
                if (newParent != null) {
                    newParent.invalidateNil();
                }
            }
        }
    }
    
    final void removeCharsHelper(final int p, final int cchRemove, final Xobj xTo, final int pTo, final boolean moveCurs, final boolean invalidate) {
        assert p > 0 && p < this.posMax() && p != this.posAfter() - 1;
        assert cchRemove > 0;
        assert this.cchRight(p) >= cchRemove;
        assert xTo != null;
        Cur next;
        for (Cur c = this.getEmbedded(); c != null; c = next) {
            next = c._next;
            assert c._xobj == this;
            if (c._pos >= p && c._pos < p + cchRemove) {
                if (moveCurs) {
                    c.moveToNoCheck(xTo, pTo + c._pos - p);
                }
                else {
                    c.nextChars(cchRemove - c._pos + p);
                }
            }
            if (c._xobj == this && c._pos >= p + cchRemove) {
                final Cur cur = c;
                cur._pos -= cchRemove;
            }
        }
        for (Bookmark b = this._bookmarks; b != null; b = b._next) {
            final Bookmark next2 = b._next;
            assert b._xobj == this;
            if (b._pos >= p && b._pos < p + cchRemove) {
                assert xTo != null;
                b.moveTo(xTo, pTo + b._pos - p);
            }
            if (b._xobj == this && b._pos >= p + cchRemove) {
                final Bookmark bookmark = b;
                bookmark._pos -= cchRemove;
            }
        }
        final int pa = this.posAfter();
        final CharUtil cu = this._locale.getCharUtil();
        if (p < pa) {
            this._srcValue = cu.removeChars(p - 1, cchRemove, this._srcValue, this._offValue, this._cchValue);
            this._offValue = cu._offSrc;
            this._cchValue = cu._cchSrc;
            if (invalidate) {
                this.invalidateUser();
                this.invalidateSpecialAttr(null);
            }
        }
        else {
            this._srcAfter = cu.removeChars(p - pa, cchRemove, this._srcAfter, this._offAfter, this._cchAfter);
            this._offAfter = cu._offSrc;
            this._cchAfter = cu._cchSrc;
            if (invalidate && this._parent != null) {
                this._parent.invalidateUser();
            }
        }
    }
    
    final void insertCharsHelper(final int p, final Object src, final int off, final int cch, final boolean invalidate) {
        assert p > 0;
        assert !(!this.isOccupied());
        final int pa = this.posAfter();
        if (p - ((p < pa) ? 1 : 2) < this._cchValue + this._cchAfter) {
            for (Cur c = this.getEmbedded(); c != null; c = c._next) {
                if (c._pos >= p) {
                    final Cur cur = c;
                    cur._pos += cch;
                }
            }
            for (Bookmark b = this._bookmarks; b != null; b = b._next) {
                if (b._pos >= p) {
                    final Bookmark bookmark = b;
                    bookmark._pos += cch;
                }
            }
        }
        final CharUtil cu = this._locale.getCharUtil();
        if (p < pa) {
            this._srcValue = cu.insertChars(p - 1, this._srcValue, this._offValue, this._cchValue, src, off, cch);
            this._offValue = cu._offSrc;
            this._cchValue = cu._cchSrc;
            if (invalidate) {
                this.invalidateUser();
                this.invalidateSpecialAttr(null);
            }
        }
        else {
            this._srcAfter = cu.insertChars(p - pa, this._srcAfter, this._offAfter, this._cchAfter, src, off, cch);
            this._offAfter = cu._offSrc;
            this._cchAfter = cu._cchSrc;
            if (invalidate && this._parent != null) {
                this._parent.invalidateUser();
            }
        }
    }
    
    Xobj copyNode(final Locale toLocale) {
        Xobj newParent = null;
        Xobj copy = null;
        Xobj x = this;
        while (true) {
            x.ensureOccupancy();
            final Xobj newX = x.newNode(toLocale);
            newX._srcValue = x._srcValue;
            newX._offValue = x._offValue;
            newX._cchValue = x._cchValue;
            newX._srcAfter = x._srcAfter;
            newX._offAfter = x._offAfter;
            newX._cchAfter = x._cchAfter;
            for (Bookmark b = x._bookmarks; b != null; b = b._next) {
                if (x.hasBookmark(CDataBookmark.CDATA_BOOKMARK.getKey(), b._pos)) {
                    newX.setBookmark(b._pos, CDataBookmark.CDATA_BOOKMARK.getKey(), CDataBookmark.CDATA_BOOKMARK);
                }
            }
            if (newParent == null) {
                copy = newX;
            }
            else {
                newParent.appendXobj(newX);
            }
            Xobj y = x;
            if ((x = x.walk(this, true)) == null) {
                break;
            }
            if (y == x._parent) {
                newParent = newX;
            }
            else {
                while (y._parent != x._parent) {
                    newParent = newParent._parent;
                    y = y._parent;
                }
            }
        }
        copy._srcAfter = null;
        copy._offAfter = 0;
        copy._cchAfter = 0;
        return copy;
    }
    
    String getCharsAsString(final int p, final int cch, final int wsr) {
        if (this.cchRight(p) == 0) {
            return "";
        }
        final Object src = this.getChars(p, cch);
        if (wsr == 1) {
            return CharUtil.getString(src, this._locale._offSrc, this._locale._cchSrc);
        }
        final Locale.ScrubBuffer scrub = Locale.getScrubBuffer(wsr);
        scrub.scrub(src, this._locale._offSrc, this._locale._cchSrc);
        return scrub.getResultAsString();
    }
    
    String getCharsAfterAsString(final int off, final int cch) {
        int offset = off + this._cchValue + 2;
        if (offset == this.posMax()) {
            offset = -1;
        }
        return this.getCharsAsString(offset, cch, 1);
    }
    
    String getCharsValueAsString(final int off, final int cch) {
        return this.getCharsAsString(off + 1, cch, 1);
    }
    
    String getValueAsString(final int wsr) {
        if (this.hasChildren()) {
            final Locale.ScrubBuffer scrub = Locale.getScrubBuffer(wsr);
            final Cur c = this.tempCur();
            c.push();
            c.next();
            while (!c.isAtEndOfLastPush()) {
                if (c.isText()) {
                    scrub.scrub(c.getChars(-1), c._offSrc, c._cchSrc);
                }
                if (c.isComment() || c.isProcinst()) {
                    c.skip();
                }
                else {
                    c.next();
                }
            }
            final String s = scrub.getResultAsString();
            c.release();
            return s;
        }
        final Object src = this.getFirstChars();
        if (wsr == 1) {
            final String s2 = CharUtil.getString(src, this._locale._offSrc, this._locale._cchSrc);
            final int cch = s2.length();
            if (cch > 0) {
                final Xobj lastAttr = this.lastAttr();
                assert ((lastAttr == null) ? this._cchValue : lastAttr._cchAfter) == cch;
                if (lastAttr != null) {
                    lastAttr._srcAfter = s2;
                    lastAttr._offAfter = 0;
                }
                else {
                    this._srcValue = s2;
                    this._offValue = 0;
                }
            }
            return s2;
        }
        final Locale.ScrubBuffer scrub2 = Locale.getScrubBuffer(wsr);
        scrub2.scrub(src, this._locale._offSrc, this._locale._cchSrc);
        return scrub2.getResultAsString();
    }
    
    String getValueAsString() {
        return this.getValueAsString(1);
    }
    
    String getString(final int p, int cch) {
        final int cchRight = this.cchRight(p);
        if (cchRight == 0) {
            return "";
        }
        if (cch < 0 || cch > cchRight) {
            cch = cchRight;
        }
        final int pa = this.posAfter();
        assert p > 0;
        String s;
        if (p >= pa) {
            s = CharUtil.getString(this._srcAfter, this._offAfter + p - pa, cch);
            if (p == pa && cch == this._cchAfter) {
                this._srcAfter = s;
                this._offAfter = 0;
            }
        }
        else {
            s = CharUtil.getString(this._srcValue, this._offValue + p - 1, cch);
            if (p == 1 && cch == this._cchValue) {
                this._srcValue = s;
                this._offValue = 0;
            }
        }
        return s;
    }
    
    Object getFirstChars() {
        this.ensureOccupancy();
        if (this._cchValue > 0) {
            return this.getChars(1, -1);
        }
        final Xobj lastAttr = this.lastAttr();
        if (lastAttr == null || lastAttr._cchAfter <= 0) {
            this._locale._offSrc = 0;
            this._locale._cchSrc = 0;
            return null;
        }
        return lastAttr.getChars(lastAttr.posAfter(), -1);
    }
    
    Object getChars(final int pos, final int cch, final Cur c) {
        final Object src = this.getChars(pos, cch);
        c._offSrc = this._locale._offSrc;
        c._cchSrc = this._locale._cchSrc;
        return src;
    }
    
    Object getChars(final int pos, int cch) {
        assert this.isNormal(pos);
        final int cchRight = this.cchRight(pos);
        if (cch < 0 || cch > cchRight) {
            cch = cchRight;
        }
        if (cch == 0) {
            this._locale._offSrc = 0;
            this._locale._cchSrc = 0;
            return null;
        }
        return this.getCharsHelper(pos, cch);
    }
    
    Object getCharsHelper(final int pos, final int cch) {
        assert cch > 0 && this.cchRight(pos) >= cch;
        final int pa = this.posAfter();
        Object src;
        if (pos >= pa) {
            src = this._srcAfter;
            this._locale._offSrc = this._offAfter + pos - pa;
        }
        else {
            src = this._srcValue;
            this._locale._offSrc = this._offValue + pos - 1;
        }
        this._locale._cchSrc = cch;
        return src;
    }
    
    final void setBit(final int mask) {
        this._bits |= mask;
    }
    
    final void clearBit(final int mask) {
        this._bits &= ~mask;
    }
    
    final boolean bitIsSet(final int mask) {
        return (this._bits & mask) != 0x0;
    }
    
    final boolean bitIsClear(final int mask) {
        return (this._bits & mask) == 0x0;
    }
    
    final boolean isVacant() {
        return this.bitIsSet(256);
    }
    
    final boolean isOccupied() {
        return this.bitIsClear(256);
    }
    
    final boolean inhibitDisconnect() {
        return this.bitIsSet(1024);
    }
    
    final boolean isStableUser() {
        return this.bitIsSet(512);
    }
    
    void invalidateNil() {
        if (this._user != null) {
            this._user.invalidate_nilvalue();
        }
    }
    
    void setStableType(final SchemaType type) {
        this.setStableUser(((TypeStoreUserFactory)type).createTypeStoreUser());
    }
    
    void setStableUser(final TypeStoreUser user) {
        this.disconnectNonRootUsers();
        this.disconnectUser();
        assert this._user == null;
        (this._user = user).attach_store(this);
        this.setBit(512);
    }
    
    void disconnectUser() {
        if (this._user != null && !this.inhibitDisconnect()) {
            this.ensureOccupancy();
            this._user.disconnect_store();
            this._user = null;
        }
    }
    
    void disconnectNonRootUsers() {
        Xobj next;
        for (Xobj x = this; x != null; x = next) {
            next = x.walk(this, x._user != null);
            if (!x.isRoot()) {
                x.disconnectUser();
            }
        }
    }
    
    void disconnectChildrenUsers() {
        Xobj next;
        for (Xobj x = this.walk(this, this._user == null); x != null; x = next) {
            next = x.walk(this, x._user != null);
            x.disconnectUser();
        }
    }
    
    final String namespaceForPrefix(String prefix, final boolean defaultAlwaysMapped) {
        if (prefix == null) {
            prefix = "";
        }
        if (prefix.equals("xml")) {
            return "http://www.w3.org/XML/1998/namespace";
        }
        if (prefix.equals("xmlns")) {
            return "http://www.w3.org/2000/xmlns/";
        }
        for (Xobj x = this; x != null; x = x._parent) {
            for (Xobj a = x._firstChild; a != null && a.isAttr(); a = a._nextSibling) {
                if (a.isXmlns() && a.getXmlnsPrefix().equals(prefix)) {
                    return a.getXmlnsUri();
                }
            }
        }
        return (defaultAlwaysMapped && prefix.length() == 0) ? "" : null;
    }
    
    final String prefixForNamespace(String ns, String suggestion, final boolean createIfMissing) {
        if (ns == null) {
            ns = "";
        }
        if (ns.equals("http://www.w3.org/XML/1998/namespace")) {
            return "xml";
        }
        if (ns.equals("http://www.w3.org/2000/xmlns/")) {
            return "xmlns";
        }
        Xobj base;
        for (base = this; !base.isContainer(); base = base.ensureParent()) {}
        if (ns.length() == 0) {
            final Xobj a = base.findXmlnsForPrefix("");
            if (a == null || a.getXmlnsUri().length() == 0) {
                return "";
            }
            if (!createIfMissing) {
                return null;
            }
            base.setAttr(this._locale.createXmlns(null), "");
            return "";
        }
        else {
            for (Xobj c = base; c != null; c = c._parent) {
                for (Xobj a2 = c.firstAttr(); a2 != null; a2 = a2.nextAttr()) {
                    if (a2.isXmlns() && a2.getXmlnsUri().equals(ns) && base.findXmlnsForPrefix(a2.getXmlnsPrefix()) == a2) {
                        return a2.getXmlnsPrefix();
                    }
                }
            }
            if (!createIfMissing) {
                return null;
            }
            if (suggestion != null && (suggestion.length() == 0 || suggestion.toLowerCase().startsWith("xml") || base.findXmlnsForPrefix(suggestion) != null)) {
                suggestion = null;
            }
            if (suggestion == null) {
                final String prefixBase = suggestion = QNameHelper.suggestPrefix(ns);
                for (int i = 1; base.findXmlnsForPrefix(suggestion) != null; suggestion = prefixBase + i++) {}
            }
            for (Xobj c = base; !c.isRoot() && !c.ensureParent().isRoot(); c = c._parent) {}
            base.setAttr(this._locale.createXmlns(suggestion), ns);
            return suggestion;
        }
    }
    
    final QName getValueAsQName() {
        assert !this.hasChildren();
        final String value = this.getValueAsString(3);
        final int firstcolon = value.indexOf(58);
        String prefix;
        String localname;
        if (firstcolon >= 0) {
            prefix = value.substring(0, firstcolon);
            localname = value.substring(firstcolon + 1);
        }
        else {
            prefix = "";
            localname = value;
        }
        final String uri = this.namespaceForPrefix(prefix, true);
        if (uri == null) {
            return null;
        }
        return new QName(uri, localname);
    }
    
    final Xobj getAttr(final QName name) {
        for (Xobj x = this._firstChild; x != null && x.isAttr(); x = x._nextSibling) {
            if (x._name.equals(name)) {
                return x;
            }
        }
        return null;
    }
    
    final QName getXsiTypeName() {
        assert this.isContainer();
        final Xobj a = this.getAttr(Locale._xsiType);
        return (a == null) ? null : a.getValueAsQName();
    }
    
    final XmlObject getObject() {
        return this.isUserNode() ? ((XmlObject)this.getUser()) : null;
    }
    
    final TypeStoreUser getUser() {
        assert this.isUserNode();
        assert !this.isRoot() && !this.isStableUser();
        if (this._user == null) {
            final TypeStoreUser parentUser = (this._parent == null) ? ((TypeStoreUserFactory)XmlBeans.NO_TYPE).createTypeStoreUser() : this._parent.getUser();
            (this._user = (this.isElem() ? parentUser.create_element_user(this._name, this.getXsiTypeName()) : parentUser.create_attribute_user(this._name))).attach_store(this);
        }
        return this._user;
    }
    
    final void invalidateUser() {
        assert this.isValid();
        assert !(!this.isUserNode());
        if (this._user != null) {
            this._user.invalidate_value();
        }
    }
    
    final void ensureOccupancy() {
        assert this.isValid();
        if (this.isVacant()) {
            assert this.isUserNode();
            this.clearBit(256);
            final TypeStoreUser user = this._user;
            this._user = null;
            final String value = user.build_text(this);
            final long saveVersion = this._locale._versionAll;
            final long saveVersionSansText = this._locale._versionSansText;
            this.setValue(value);
            assert saveVersionSansText == this._locale._versionSansText;
            this._locale._versionAll = saveVersion;
            assert this._user == null;
            this._user = user;
        }
    }
    
    private void setValue(final String val) {
        assert CharUtil.isValid(val, 0, val.length());
        if (val.length() <= 0) {
            return;
        }
        this._locale.notifyChange();
        final Xobj lastAttr = this.lastAttr();
        int startPos = 1;
        Xobj charOwner = this;
        if (lastAttr != null) {
            charOwner = lastAttr;
            startPos = charOwner.posAfter();
        }
        charOwner.insertCharsHelper(startPos, val, 0, val.length(), true);
    }
    
    @Override
    public SchemaTypeLoader get_schematypeloader() {
        return this._locale._schemaTypeLoader;
    }
    
    @Override
    public XmlLocale get_locale() {
        return this._locale;
    }
    
    @Override
    public Object get_root_object() {
        return this._locale;
    }
    
    @Override
    public boolean is_attribute() {
        assert this.isValid();
        return this.isAttr();
    }
    
    @Override
    public boolean validate_on_set() {
        assert this.isValid();
        return this._locale._validateOnSet;
    }
    
    @Override
    public void invalidate_text() {
        this._locale.enter();
        try {
            assert this.isValid();
            if (this.isOccupied()) {
                if (this.hasTextNoEnsureOccupancy() || this.hasChildren()) {
                    final TypeStoreUser user = this._user;
                    this._user = null;
                    final Cur c = this.tempCur();
                    c.moveNodeContents(null, false);
                    c.release();
                    assert this._user == null;
                    this._user = user;
                }
                this.setBit(256);
            }
            assert this.isValid();
        }
        finally {
            this._locale.exit();
        }
    }
    
    @Override
    public String fetch_text(final int wsr) {
        this._locale.enter();
        try {
            assert this.isValid() && this.isOccupied();
            return this.getValueAsString(wsr);
        }
        finally {
            this._locale.exit();
        }
    }
    
    @Override
    public XmlCursor new_cursor() {
        this._locale.enter();
        try {
            final Cur c = this.tempCur();
            final XmlCursor xc = new Cursor(c);
            c.release();
            return xc;
        }
        finally {
            this._locale.exit();
        }
    }
    
    @Override
    public SchemaField get_schema_field() {
        assert this.isValid();
        if (this.isRoot()) {
            return null;
        }
        final TypeStoreUser parentUser = this.ensureParent().getUser();
        if (this.isAttr()) {
            return parentUser.get_attribute_field(this._name);
        }
        assert this.isElem();
        final TypeStoreVisitor visitor = parentUser.new_visitor();
        if (visitor == null) {
            return null;
        }
        Xobj x = this._parent._firstChild;
        while (true) {
            if (x.isElem()) {
                visitor.visit(x._name);
                if (x == this) {
                    break;
                }
            }
            x = x._nextSibling;
        }
        return visitor.get_schema_field();
    }
    
    @Override
    public void validate(final ValidatorListener eventSink) {
        this._locale.enter();
        try {
            final Cur c = this.tempCur();
            final Validate validate = new Validate(c, eventSink);
            c.release();
        }
        finally {
            this._locale.exit();
        }
    }
    
    @Override
    public TypeStoreUser change_type(final SchemaType type) {
        this._locale.enter();
        try {
            final Cur c = this.tempCur();
            c.setType(type, false);
            c.release();
        }
        finally {
            this._locale.exit();
        }
        return this.getUser();
    }
    
    @Override
    public TypeStoreUser substitute(final QName name, final SchemaType type) {
        this._locale.enter();
        try {
            final Cur c = this.tempCur();
            c.setSubstitution(name, type, false);
            c.release();
        }
        finally {
            this._locale.exit();
        }
        return this.getUser();
    }
    
    @Override
    public QName get_xsi_type() {
        return this.getXsiTypeName();
    }
    
    @Override
    public void store_text(final String text) {
        this._locale.enter();
        final TypeStoreUser user = this._user;
        this._user = null;
        try {
            final Cur c = this.tempCur();
            c.moveNodeContents(null, false);
            if (text != null && text.length() > 0) {
                c.next();
                c.insertString(text);
            }
            c.release();
        }
        finally {
            assert this._user == null;
            this._user = user;
            this._locale.exit();
        }
    }
    
    @Override
    public int compute_flags() {
        if (this.isRoot()) {
            return 0;
        }
        final TypeStoreUser parentUser = this.ensureParent().getUser();
        if (this.isAttr()) {
            return parentUser.get_attributeflags(this._name);
        }
        final int f = parentUser.get_elementflags(this._name);
        if (f != -1) {
            return f;
        }
        final TypeStoreVisitor visitor = parentUser.new_visitor();
        if (visitor == null) {
            return 0;
        }
        Xobj x = this._parent._firstChild;
        while (true) {
            if (x.isElem()) {
                visitor.visit(x._name);
                if (x == this) {
                    break;
                }
            }
            x = x._nextSibling;
        }
        return visitor.get_elementflags();
    }
    
    @Override
    public String compute_default_text() {
        if (this.isRoot()) {
            return null;
        }
        final TypeStoreUser parentUser = this.ensureParent().getUser();
        if (this.isAttr()) {
            return parentUser.get_default_attribute_text(this._name);
        }
        final String result = parentUser.get_default_element_text(this._name);
        if (result != null) {
            return result;
        }
        final TypeStoreVisitor visitor = parentUser.new_visitor();
        if (visitor == null) {
            return null;
        }
        Xobj x = this._parent._firstChild;
        while (true) {
            if (x.isElem()) {
                visitor.visit(x._name);
                if (x == this) {
                    break;
                }
            }
            x = x._nextSibling;
        }
        return visitor.get_default_text();
    }
    
    @Override
    public boolean find_nil() {
        if (this.isAttr()) {
            return false;
        }
        this._locale.enter();
        try {
            final Xobj a = this.getAttr(Locale._xsiNil);
            if (a == null) {
                return false;
            }
            final String value = a.getValueAsString(3);
            return value.equals("true") || value.equals("1");
        }
        finally {
            this._locale.exit();
        }
    }
    
    @Override
    public void invalidate_nil() {
        if (this.isAttr()) {
            return;
        }
        this._locale.enter();
        try {
            if (!this._user.build_nil()) {
                this.removeAttr(Locale._xsiNil);
            }
            else {
                this.setAttr(Locale._xsiNil, "true");
            }
        }
        finally {
            this._locale.exit();
        }
    }
    
    @Override
    public int count_elements(final QName name) {
        return this._locale.count(this, name, null);
    }
    
    @Override
    public int count_elements(final QNameSet names) {
        return this._locale.count(this, null, names);
    }
    
    @Override
    public TypeStoreUser find_element_user(final QName name, int i) {
        for (Xobj x = this._firstChild; x != null; x = x._nextSibling) {
            if (x.isElem() && x._name.equals(name) && --i < 0) {
                return x.getUser();
            }
        }
        return null;
    }
    
    @Override
    public TypeStoreUser find_element_user(final QNameSet names, int i) {
        for (Xobj x = this._firstChild; x != null; x = x._nextSibling) {
            if (x.isElem() && names.contains(x._name) && --i < 0) {
                return x.getUser();
            }
        }
        return null;
    }
    
    @Override
    public void find_all_element_users(final QName name, final List fillMeUp) {
        for (Xobj x = this._firstChild; x != null; x = x._nextSibling) {
            if (x.isElem() && x._name.equals(name)) {
                fillMeUp.add(x.getUser());
            }
        }
    }
    
    @Override
    public void find_all_element_users(final QNameSet names, final List fillMeUp) {
        for (Xobj x = this._firstChild; x != null; x = x._nextSibling) {
            if (x.isElem() && names.contains(x._name)) {
                fillMeUp.add(x.getUser());
            }
        }
    }
    
    private static TypeStoreUser insertElement(final QName name, final Xobj x, final int pos) {
        x._locale.enter();
        try {
            final Cur c = x._locale.tempCur();
            c.moveTo(x, pos);
            c.createElement(name);
            final TypeStoreUser user = c.getUser();
            c.release();
            return user;
        }
        finally {
            x._locale.exit();
        }
    }
    
    @Override
    public TypeStoreUser insert_element_user(final QName name, final int i) {
        if (i < 0) {
            throw new IndexOutOfBoundsException();
        }
        if (!this.isContainer()) {
            throw new IllegalStateException();
        }
        final Xobj x = this._locale.findNthChildElem(this, name, null, i);
        if (x != null) {
            return insertElement(name, x, 0);
        }
        if (i > this._locale.count(this, name, null) + 1) {
            throw new IndexOutOfBoundsException();
        }
        return this.add_element_user(name);
    }
    
    @Override
    public TypeStoreUser insert_element_user(final QNameSet names, final QName name, final int i) {
        if (i < 0) {
            throw new IndexOutOfBoundsException();
        }
        if (!this.isContainer()) {
            throw new IllegalStateException();
        }
        final Xobj x = this._locale.findNthChildElem(this, null, names, i);
        if (x != null) {
            return insertElement(name, x, 0);
        }
        if (i > this._locale.count(this, null, names) + 1) {
            throw new IndexOutOfBoundsException();
        }
        return this.add_element_user(name);
    }
    
    @Override
    public TypeStoreUser add_element_user(final QName name) {
        if (!this.isContainer()) {
            throw new IllegalStateException();
        }
        QNameSet endSet = null;
        boolean gotEndSet = false;
        Xobj candidate = null;
        for (Xobj x = this._lastChild; x != null; x = x._prevSibling) {
            if (x.isContainer()) {
                if (x._name.equals(name)) {
                    break;
                }
                if (!gotEndSet) {
                    endSet = this._user.get_element_ending_delimiters(name);
                    gotEndSet = true;
                }
                if (endSet == null || endSet.contains(x._name)) {
                    candidate = x;
                }
            }
        }
        return (candidate == null) ? insertElement(name, this, -1) : insertElement(name, candidate, 0);
    }
    
    private static void removeElement(final Xobj x) {
        if (x == null) {
            throw new IndexOutOfBoundsException();
        }
        x._locale.enter();
        try {
            final Cur c = x.tempCur();
            c.moveNode(null);
            c.release();
        }
        finally {
            x._locale.exit();
        }
    }
    
    @Override
    public void remove_element(final QName name, int i) {
        if (i < 0) {
            throw new IndexOutOfBoundsException();
        }
        if (!this.isContainer()) {
            throw new IllegalStateException();
        }
        Xobj x;
        for (x = this._firstChild; x != null && (!x.isElem() || !x._name.equals(name) || --i >= 0); x = x._nextSibling) {}
        removeElement(x);
    }
    
    @Override
    public void remove_element(final QNameSet names, int i) {
        if (i < 0) {
            throw new IndexOutOfBoundsException();
        }
        if (!this.isContainer()) {
            throw new IllegalStateException();
        }
        Xobj x;
        for (x = this._firstChild; x != null && (!x.isElem() || !names.contains(x._name) || --i >= 0); x = x._nextSibling) {}
        removeElement(x);
    }
    
    @Override
    public TypeStoreUser find_attribute_user(final QName name) {
        final Xobj a = this.getAttr(name);
        return (a == null) ? null : a.getUser();
    }
    
    @Override
    public TypeStoreUser add_attribute_user(final QName name) {
        if (this.getAttr(name) != null) {
            throw new IndexOutOfBoundsException();
        }
        this._locale.enter();
        try {
            return this.setAttr(name, "").getUser();
        }
        finally {
            this._locale.exit();
        }
    }
    
    @Override
    public void remove_attribute(final QName name) {
        this._locale.enter();
        try {
            if (!this.removeAttr(name)) {
                throw new IndexOutOfBoundsException();
            }
        }
        finally {
            this._locale.exit();
        }
    }
    
    @Override
    public TypeStoreUser copy_contents_from(final TypeStore source) {
        final Xobj xSrc = (Xobj)source;
        if (xSrc == this) {
            return this.getUser();
        }
        this._locale.enter();
        try {
            xSrc._locale.enter();
            final Cur c = this.tempCur();
            try {
                final Cur cSrc1 = xSrc.tempCur();
                final Map sourceNamespaces = Locale.getAllNamespaces(cSrc1, null);
                cSrc1.release();
                if (this.isAttr()) {
                    final Cur cSrc2 = xSrc.tempCur();
                    final String value = Locale.getTextValue(cSrc2);
                    cSrc2.release();
                    c.setValue(value);
                }
                else {
                    this.disconnectChildrenUsers();
                    assert !this.inhibitDisconnect();
                    this.setBit(1024);
                    final QName xsiType = this.isContainer() ? this.getXsiTypeName() : null;
                    final Xobj copy = xSrc.copyNode(this._locale);
                    Cur.moveNodeContents(this, null, true);
                    c.next();
                    Cur.moveNodeContents(copy, c, true);
                    c.moveTo(this);
                    if (xsiType != null) {
                        c.setXsiType(xsiType);
                    }
                    assert this.inhibitDisconnect();
                    this.clearBit(1024);
                }
                if (sourceNamespaces != null) {
                    if (!c.isContainer()) {
                        c.toParent();
                    }
                    Locale.applyNamespaces(c, sourceNamespaces);
                }
            }
            finally {
                c.release();
                xSrc._locale.exit();
            }
        }
        finally {
            this._locale.exit();
        }
        return this.getUser();
    }
    
    @Override
    public TypeStoreUser copy(final SchemaTypeLoader stl, final SchemaType type, XmlOptions options) {
        Xobj destination = null;
        options = XmlOptions.maskNull(options);
        SchemaType sType = (SchemaType)options.get("DOCUMENT_TYPE");
        if (sType == null) {
            sType = ((type == null) ? XmlObject.type : type);
        }
        Locale locale = this.locale();
        if (Boolean.TRUE.equals(options.get("COPY_USE_NEW_LOCALE"))) {
            locale = Locale.getLocale(stl, options);
        }
        if (sType.isDocumentType() || (sType.isNoType() && this instanceof DocumentXobj)) {
            destination = Cur.createDomDocumentRootXobj(locale, false);
        }
        else {
            destination = Cur.createDomDocumentRootXobj(locale, true);
        }
        locale.enter();
        try {
            final Cur c = destination.tempCur();
            c.setType(type);
            c.release();
        }
        finally {
            locale.exit();
        }
        final TypeStoreUser tsu = destination.copy_contents_from(this);
        return tsu;
    }
    
    @Override
    public void array_setter(final XmlObject[] sources, final QName elementName) {
        this._locale.enter();
        try {
            final int m = sources.length;
            final ArrayList copies = new ArrayList();
            final ArrayList types = new ArrayList();
            for (int i = 0; i < m; ++i) {
                if (sources[i] == null) {
                    throw new IllegalArgumentException("Array element null");
                }
                if (sources[i].isImmutable()) {
                    copies.add(null);
                    types.add(null);
                }
                else {
                    final Xobj x = (Xobj)((TypeStoreUser)sources[i]).get_store();
                    if (x._locale == this._locale) {
                        copies.add(x.copyNode(this._locale));
                    }
                    else {
                        x._locale.enter();
                        try {
                            copies.add(x.copyNode(this._locale));
                        }
                        finally {
                            x._locale.exit();
                        }
                    }
                    types.add(sources[i].schemaType());
                }
            }
            int n;
            for (n = this.count_elements(elementName); n > m; --n) {
                this.remove_element(elementName, m);
            }
            while (m > n) {
                this.add_element_user(elementName);
                ++n;
            }
            assert m == n;
            final ArrayList elements = new ArrayList();
            this.find_all_element_users(elementName, elements);
            for (int j = 0; j < elements.size(); ++j) {
                elements.set(j, elements.get(j).get_store());
            }
            assert elements.size() == n;
            final Cur c = this.tempCur();
            for (int k = 0; k < n; ++k) {
                final Xobj x2 = elements.get(k);
                if (sources[k].isImmutable()) {
                    x2.getObject().set(sources[k]);
                }
                else {
                    Cur.moveNodeContents(x2, null, true);
                    c.moveTo(x2);
                    c.next();
                    Cur.moveNodeContents(copies.get(k), c, true);
                    x2.change_type(types.get(k));
                }
            }
            c.release();
        }
        finally {
            this._locale.exit();
        }
    }
    
    @Override
    public void visit_elements(final TypeStoreVisitor visitor) {
        throw new RuntimeException("Not implemeneted");
    }
    
    @Override
    public XmlObject[] exec_query(final String queryExpr, final XmlOptions options) throws XmlException {
        this._locale.enter();
        try {
            final Cur c = this.tempCur();
            final XmlObject[] result = Query.objectExecQuery(c, queryExpr, options);
            c.release();
            return result;
        }
        finally {
            this._locale.exit();
        }
    }
    
    @Override
    public String find_prefix_for_nsuri(final String nsuri, final String suggested_prefix) {
        this._locale.enter();
        try {
            return this.prefixForNamespace(nsuri, suggested_prefix, true);
        }
        finally {
            this._locale.exit();
        }
    }
    
    @Override
    public String getNamespaceForPrefix(final String prefix) {
        return this.namespaceForPrefix(prefix, true);
    }
    
    abstract static class NodeXobj extends Xobj implements DomImpl.Dom, Node, NodeList
    {
        NodeXobj(final Locale l, final int kind, final int domType) {
            super(l, kind, domType);
        }
        
        @Override
        DomImpl.Dom getDom() {
            return this;
        }
        
        @Override
        public int getLength() {
            return DomImpl._childNodes_getLength(this);
        }
        
        @Override
        public Node item(final int i) {
            return DomImpl._childNodes_item(this, i);
        }
        
        @Override
        public Node appendChild(final Node newChild) {
            return DomImpl._node_appendChild(this, newChild);
        }
        
        @Override
        public Node cloneNode(final boolean deep) {
            return DomImpl._node_cloneNode(this, deep);
        }
        
        @Override
        public NamedNodeMap getAttributes() {
            return null;
        }
        
        @Override
        public NodeList getChildNodes() {
            return this;
        }
        
        @Override
        public Node getParentNode() {
            return DomImpl._node_getParentNode(this);
        }
        
        @Override
        public Node removeChild(final Node oldChild) {
            return DomImpl._node_removeChild(this, oldChild);
        }
        
        @Override
        public Node getFirstChild() {
            return DomImpl._node_getFirstChild(this);
        }
        
        @Override
        public Node getLastChild() {
            return DomImpl._node_getLastChild(this);
        }
        
        @Override
        public String getLocalName() {
            return DomImpl._node_getLocalName(this);
        }
        
        @Override
        public String getNamespaceURI() {
            return DomImpl._node_getNamespaceURI(this);
        }
        
        @Override
        public Node getNextSibling() {
            return DomImpl._node_getNextSibling(this);
        }
        
        @Override
        public String getNodeName() {
            return DomImpl._node_getNodeName(this);
        }
        
        @Override
        public short getNodeType() {
            return DomImpl._node_getNodeType(this);
        }
        
        @Override
        public String getNodeValue() {
            return DomImpl._node_getNodeValue(this);
        }
        
        @Override
        public Document getOwnerDocument() {
            return DomImpl._node_getOwnerDocument(this);
        }
        
        @Override
        public String getPrefix() {
            return DomImpl._node_getPrefix(this);
        }
        
        @Override
        public Node getPreviousSibling() {
            return DomImpl._node_getPreviousSibling(this);
        }
        
        @Override
        public boolean hasAttributes() {
            return DomImpl._node_hasAttributes(this);
        }
        
        @Override
        public boolean hasChildNodes() {
            return DomImpl._node_hasChildNodes(this);
        }
        
        @Override
        public Node insertBefore(final Node newChild, final Node refChild) {
            return DomImpl._node_insertBefore(this, newChild, refChild);
        }
        
        @Override
        public boolean isSupported(final String feature, final String version) {
            return DomImpl._node_isSupported(this, feature, version);
        }
        
        @Override
        public void normalize() {
            DomImpl._node_normalize(this);
        }
        
        @Override
        public Node replaceChild(final Node newChild, final Node oldChild) {
            return DomImpl._node_replaceChild(this, newChild, oldChild);
        }
        
        @Override
        public void setNodeValue(final String nodeValue) {
            DomImpl._node_setNodeValue(this, nodeValue);
        }
        
        @Override
        public void setPrefix(final String prefix) {
            DomImpl._node_setPrefix(this, prefix);
        }
        
        @Override
        public boolean nodeCanHavePrefixUri() {
            return false;
        }
        
        @Override
        public Object getUserData(final String key) {
            return DomImpl._node_getUserData(this, key);
        }
        
        @Override
        public Object setUserData(final String key, final Object data, final UserDataHandler handler) {
            return DomImpl._node_setUserData(this, key, data, handler);
        }
        
        @Override
        public Object getFeature(final String feature, final String version) {
            return DomImpl._node_getFeature(this, feature, version);
        }
        
        @Override
        public boolean isEqualNode(final Node arg) {
            return DomImpl._node_isEqualNode(this, arg);
        }
        
        @Override
        public boolean isSameNode(final Node arg) {
            return DomImpl._node_isSameNode(this, arg);
        }
        
        @Override
        public String lookupNamespaceURI(final String prefix) {
            return DomImpl._node_lookupNamespaceURI(this, prefix);
        }
        
        @Override
        public String lookupPrefix(final String namespaceURI) {
            return DomImpl._node_lookupPrefix(this, namespaceURI);
        }
        
        @Override
        public boolean isDefaultNamespace(final String namespaceURI) {
            return DomImpl._node_isDefaultNamespace(this, namespaceURI);
        }
        
        @Override
        public void setTextContent(final String textContent) {
            DomImpl._node_setTextContent(this, textContent);
        }
        
        @Override
        public String getTextContent() {
            return DomImpl._node_getTextContent(this);
        }
        
        @Override
        public short compareDocumentPosition(final Node other) {
            return DomImpl._node_compareDocumentPosition(this, other);
        }
        
        @Override
        public String getBaseURI() {
            return DomImpl._node_getBaseURI(this);
        }
    }
    
    static class DocumentXobj extends NodeXobj implements Document
    {
        private Hashtable _idToElement;
        
        DocumentXobj(final Locale l) {
            super(l, 1, 9);
        }
        
        @Override
        Xobj newNode(final Locale l) {
            return new DocumentXobj(l);
        }
        
        @Override
        public Attr createAttribute(final String name) {
            return DomImpl._document_createAttribute(this, name);
        }
        
        @Override
        public Attr createAttributeNS(final String namespaceURI, final String qualifiedName) {
            return DomImpl._document_createAttributeNS(this, namespaceURI, qualifiedName);
        }
        
        @Override
        public CDATASection createCDATASection(final String data) {
            return DomImpl._document_createCDATASection(this, data);
        }
        
        @Override
        public Comment createComment(final String data) {
            return DomImpl._document_createComment(this, data);
        }
        
        @Override
        public DocumentFragment createDocumentFragment() {
            return DomImpl._document_createDocumentFragment(this);
        }
        
        @Override
        public Element createElement(final String tagName) {
            return DomImpl._document_createElement(this, tagName);
        }
        
        @Override
        public Element createElementNS(final String namespaceURI, final String qualifiedName) {
            return DomImpl._document_createElementNS(this, namespaceURI, qualifiedName);
        }
        
        @Override
        public EntityReference createEntityReference(final String name) {
            return DomImpl._document_createEntityReference(this, name);
        }
        
        @Override
        public ProcessingInstruction createProcessingInstruction(final String target, final String data) {
            return DomImpl._document_createProcessingInstruction(this, target, data);
        }
        
        @Override
        public Text createTextNode(final String data) {
            return DomImpl._document_createTextNode(this, data);
        }
        
        @Override
        public DocumentType getDoctype() {
            return DomImpl._document_getDoctype(this);
        }
        
        @Override
        public Element getDocumentElement() {
            return DomImpl._document_getDocumentElement(this);
        }
        
        @Override
        public Element getElementById(final String elementId) {
            if (this._idToElement == null) {
                return null;
            }
            final Xobj o = this._idToElement.get(elementId);
            if (o == null) {
                return null;
            }
            if (!this.isInSameTree(o)) {
                this._idToElement.remove(elementId);
            }
            return (Element)o;
        }
        
        @Override
        public NodeList getElementsByTagName(final String tagname) {
            return DomImpl._document_getElementsByTagName(this, tagname);
        }
        
        @Override
        public NodeList getElementsByTagNameNS(final String namespaceURI, final String localName) {
            return DomImpl._document_getElementsByTagNameNS(this, namespaceURI, localName);
        }
        
        @Override
        public DOMImplementation getImplementation() {
            return DomImpl._document_getImplementation(this);
        }
        
        @Override
        public Node importNode(final Node importedNode, final boolean deep) {
            return DomImpl._document_importNode(this, importedNode, deep);
        }
        
        @Override
        public Node adoptNode(final Node source) {
            throw new RuntimeException("DOM Level 3 Not implemented");
        }
        
        @Override
        public String getDocumentURI() {
            throw new RuntimeException("DOM Level 3 Not implemented");
        }
        
        @Override
        public DOMConfiguration getDomConfig() {
            throw new RuntimeException("DOM Level 3 Not implemented");
        }
        
        @Override
        public String getInputEncoding() {
            throw new RuntimeException("DOM Level 3 Not implemented");
        }
        
        @Override
        public boolean getStrictErrorChecking() {
            throw new RuntimeException("DOM Level 3 Not implemented");
        }
        
        @Override
        public String getXmlEncoding() {
            throw new RuntimeException("DOM Level 3 Not implemented");
        }
        
        @Override
        public boolean getXmlStandalone() {
            throw new RuntimeException("DOM Level 3 Not implemented");
        }
        
        @Override
        public String getXmlVersion() {
            throw new RuntimeException("DOM Level 3 Not implemented");
        }
        
        @Override
        public void normalizeDocument() {
            throw new RuntimeException("DOM Level 3 Not implemented");
        }
        
        @Override
        public Node renameNode(final Node n, final String namespaceURI, final String qualifiedName) {
            throw new RuntimeException("DOM Level 3 Not implemented");
        }
        
        @Override
        public void setDocumentURI(final String documentURI) {
            throw new RuntimeException("DOM Level 3 Not implemented");
        }
        
        @Override
        public void setStrictErrorChecking(final boolean strictErrorChecking) {
            throw new RuntimeException("DOM Level 3 Not implemented");
        }
        
        @Override
        public void setXmlStandalone(final boolean xmlStandalone) {
            throw new RuntimeException("DOM Level 3 Not implemented");
        }
        
        @Override
        public void setXmlVersion(final String xmlVersion) {
            throw new RuntimeException("DOM Level 3 Not implemented");
        }
        
        protected void addIdElement(final String idVal, final DomImpl.Dom e) {
            if (this._idToElement == null) {
                this._idToElement = new Hashtable();
            }
            this._idToElement.put(idVal, e);
        }
        
        void removeIdElement(final String idVal) {
            if (this._idToElement != null) {
                this._idToElement.remove(idVal);
            }
        }
    }
    
    static class DocumentFragXobj extends NodeXobj implements DocumentFragment
    {
        DocumentFragXobj(final Locale l) {
            super(l, 1, 11);
        }
        
        @Override
        Xobj newNode(final Locale l) {
            return new DocumentFragXobj(l);
        }
    }
    
    static final class ElementAttributes implements NamedNodeMap
    {
        private ElementXobj _elementXobj;
        
        ElementAttributes(final ElementXobj elementXobj) {
            this._elementXobj = elementXobj;
        }
        
        @Override
        public int getLength() {
            return DomImpl._attributes_getLength(this._elementXobj);
        }
        
        @Override
        public Node getNamedItem(final String name) {
            return DomImpl._attributes_getNamedItem(this._elementXobj, name);
        }
        
        @Override
        public Node getNamedItemNS(final String namespaceURI, final String localName) {
            return DomImpl._attributes_getNamedItemNS(this._elementXobj, namespaceURI, localName);
        }
        
        @Override
        public Node item(final int index) {
            return DomImpl._attributes_item(this._elementXobj, index);
        }
        
        @Override
        public Node removeNamedItem(final String name) {
            return DomImpl._attributes_removeNamedItem(this._elementXobj, name);
        }
        
        @Override
        public Node removeNamedItemNS(final String namespaceURI, final String localName) {
            return DomImpl._attributes_removeNamedItemNS(this._elementXobj, namespaceURI, localName);
        }
        
        @Override
        public Node setNamedItem(final Node arg) {
            return DomImpl._attributes_setNamedItem(this._elementXobj, arg);
        }
        
        @Override
        public Node setNamedItemNS(final Node arg) {
            return DomImpl._attributes_setNamedItemNS(this._elementXobj, arg);
        }
    }
    
    abstract static class NamedNodeXobj extends NodeXobj
    {
        boolean _canHavePrefixUri;
        
        NamedNodeXobj(final Locale l, final int kind, final int domType) {
            super(l, kind, domType);
            this._canHavePrefixUri = true;
        }
        
        @Override
        public boolean nodeCanHavePrefixUri() {
            return this._canHavePrefixUri;
        }
    }
    
    static class ElementXobj extends NamedNodeXobj implements Element
    {
        private ElementAttributes _attributes;
        
        ElementXobj(final Locale l, final QName name) {
            super(l, 2, 1);
            this._name = name;
        }
        
        @Override
        Xobj newNode(final Locale l) {
            return new ElementXobj(l, this._name);
        }
        
        @Override
        public NamedNodeMap getAttributes() {
            if (this._attributes == null) {
                this._attributes = new ElementAttributes(this);
            }
            return this._attributes;
        }
        
        @Override
        public String getAttribute(final String name) {
            return DomImpl._element_getAttribute(this, name);
        }
        
        @Override
        public Attr getAttributeNode(final String name) {
            return DomImpl._element_getAttributeNode(this, name);
        }
        
        @Override
        public Attr getAttributeNodeNS(final String namespaceURI, final String localName) {
            return DomImpl._element_getAttributeNodeNS(this, namespaceURI, localName);
        }
        
        @Override
        public String getAttributeNS(final String namespaceURI, final String localName) {
            return DomImpl._element_getAttributeNS(this, namespaceURI, localName);
        }
        
        @Override
        public NodeList getElementsByTagName(final String name) {
            return DomImpl._element_getElementsByTagName(this, name);
        }
        
        @Override
        public NodeList getElementsByTagNameNS(final String namespaceURI, final String localName) {
            return DomImpl._element_getElementsByTagNameNS(this, namespaceURI, localName);
        }
        
        @Override
        public String getTagName() {
            return DomImpl._element_getTagName(this);
        }
        
        @Override
        public boolean hasAttribute(final String name) {
            return DomImpl._element_hasAttribute(this, name);
        }
        
        @Override
        public boolean hasAttributeNS(final String namespaceURI, final String localName) {
            return DomImpl._element_hasAttributeNS(this, namespaceURI, localName);
        }
        
        @Override
        public void removeAttribute(final String name) {
            DomImpl._element_removeAttribute(this, name);
        }
        
        @Override
        public Attr removeAttributeNode(final Attr oldAttr) {
            return DomImpl._element_removeAttributeNode(this, oldAttr);
        }
        
        @Override
        public void removeAttributeNS(final String namespaceURI, final String localName) {
            DomImpl._element_removeAttributeNS(this, namespaceURI, localName);
        }
        
        @Override
        public void setAttribute(final String name, final String value) {
            DomImpl._element_setAttribute(this, name, value);
        }
        
        @Override
        public Attr setAttributeNode(final Attr newAttr) {
            return DomImpl._element_setAttributeNode(this, newAttr);
        }
        
        @Override
        public Attr setAttributeNodeNS(final Attr newAttr) {
            return DomImpl._element_setAttributeNodeNS(this, newAttr);
        }
        
        @Override
        public void setAttributeNS(final String namespaceURI, final String qualifiedName, final String value) {
            DomImpl._element_setAttributeNS(this, namespaceURI, qualifiedName, value);
        }
        
        @Override
        public TypeInfo getSchemaTypeInfo() {
            throw new RuntimeException("DOM Level 3 Not implemented");
        }
        
        @Override
        public void setIdAttribute(final String name, final boolean isId) {
            throw new RuntimeException("DOM Level 3 Not implemented");
        }
        
        @Override
        public void setIdAttributeNS(final String namespaceURI, final String localName, final boolean isId) {
            throw new RuntimeException("DOM Level 3 Not implemented");
        }
        
        @Override
        public void setIdAttributeNode(final Attr idAttr, final boolean isId) {
            throw new RuntimeException("DOM Level 3 Not implemented");
        }
    }
    
    static class AttrXobj extends NamedNodeXobj implements Attr
    {
        AttrXobj(final Locale l, final QName name) {
            super(l, 3, 2);
            this._name = name;
        }
        
        @Override
        Xobj newNode(final Locale l) {
            return new AttrXobj(l, this._name);
        }
        
        @Override
        public Node getNextSibling() {
            return null;
        }
        
        @Override
        public String getName() {
            return DomImpl._node_getNodeName(this);
        }
        
        @Override
        public Element getOwnerElement() {
            return DomImpl._attr_getOwnerElement(this);
        }
        
        @Override
        public boolean getSpecified() {
            return DomImpl._attr_getSpecified(this);
        }
        
        @Override
        public String getValue() {
            return DomImpl._node_getNodeValue(this);
        }
        
        @Override
        public void setValue(final String value) {
            DomImpl._node_setNodeValue(this, value);
        }
        
        @Override
        public TypeInfo getSchemaTypeInfo() {
            throw new RuntimeException("DOM Level 3 Not implemented");
        }
        
        @Override
        public boolean isId() {
            return false;
        }
    }
    
    static class AttrIdXobj extends AttrXobj
    {
        AttrIdXobj(final Locale l, final QName name) {
            super(l, name);
        }
        
        @Override
        public boolean isId() {
            return true;
        }
    }
    
    static class CommentXobj extends NodeXobj implements Comment
    {
        CommentXobj(final Locale l) {
            super(l, 4, 8);
        }
        
        @Override
        Xobj newNode(final Locale l) {
            return new CommentXobj(l);
        }
        
        @Override
        public NodeList getChildNodes() {
            return DomImpl._emptyNodeList;
        }
        
        @Override
        public void appendData(final String arg) {
            DomImpl._characterData_appendData(this, arg);
        }
        
        @Override
        public void deleteData(final int offset, final int count) {
            DomImpl._characterData_deleteData(this, offset, count);
        }
        
        @Override
        public String getData() {
            return DomImpl._characterData_getData(this);
        }
        
        @Override
        public int getLength() {
            return DomImpl._characterData_getLength(this);
        }
        
        @Override
        public Node getFirstChild() {
            return null;
        }
        
        @Override
        public void insertData(final int offset, final String arg) {
            DomImpl._characterData_insertData(this, offset, arg);
        }
        
        @Override
        public void replaceData(final int offset, final int count, final String arg) {
            DomImpl._characterData_replaceData(this, offset, count, arg);
        }
        
        @Override
        public void setData(final String data) {
            DomImpl._characterData_setData(this, data);
        }
        
        @Override
        public String substringData(final int offset, final int count) {
            return DomImpl._characterData_substringData(this, offset, count);
        }
    }
    
    static class ProcInstXobj extends NodeXobj implements ProcessingInstruction
    {
        ProcInstXobj(final Locale l, final String target) {
            super(l, 5, 7);
            this._name = this._locale.makeQName(null, target);
        }
        
        @Override
        Xobj newNode(final Locale l) {
            return new ProcInstXobj(l, this._name.getLocalPart());
        }
        
        @Override
        public int getLength() {
            return 0;
        }
        
        @Override
        public Node getFirstChild() {
            return null;
        }
        
        @Override
        public String getData() {
            return DomImpl._processingInstruction_getData(this);
        }
        
        @Override
        public String getTarget() {
            return DomImpl._processingInstruction_getTarget(this);
        }
        
        @Override
        public void setData(final String data) {
            DomImpl._processingInstruction_setData(this, data);
        }
    }
    
    static class SoapPartDocXobj extends DocumentXobj
    {
        SoapPartDom _soapPartDom;
        
        SoapPartDocXobj(final Locale l) {
            super(l);
            this._soapPartDom = new SoapPartDom(this);
        }
        
        @Override
        DomImpl.Dom getDom() {
            return this._soapPartDom;
        }
        
        @Override
        Xobj newNode(final Locale l) {
            return new SoapPartDocXobj(l);
        }
    }
    
    static class SoapPartDom extends SOAPPart implements DomImpl.Dom, Document, NodeList
    {
        SoapPartDocXobj _docXobj;
        
        SoapPartDom(final SoapPartDocXobj docXobj) {
            this._docXobj = docXobj;
        }
        
        @Override
        public int nodeType() {
            return 9;
        }
        
        @Override
        public Locale locale() {
            return this._docXobj._locale;
        }
        
        @Override
        public Cur tempCur() {
            return this._docXobj.tempCur();
        }
        
        @Override
        public QName getQName() {
            return this._docXobj._name;
        }
        
        @Override
        public void dump() {
            this.dump(System.out);
        }
        
        @Override
        public void dump(final PrintStream o) {
            this._docXobj.dump(o);
        }
        
        @Override
        public void dump(final PrintStream o, final Object ref) {
            this._docXobj.dump(o, ref);
        }
        
        public String name() {
            return "#document";
        }
        
        @Override
        public Node appendChild(final Node newChild) {
            return DomImpl._node_appendChild(this, newChild);
        }
        
        @Override
        public Node cloneNode(final boolean deep) {
            return DomImpl._node_cloneNode(this, deep);
        }
        
        @Override
        public NamedNodeMap getAttributes() {
            return null;
        }
        
        @Override
        public NodeList getChildNodes() {
            return this;
        }
        
        @Override
        public Node getParentNode() {
            return DomImpl._node_getParentNode(this);
        }
        
        @Override
        public Node removeChild(final Node oldChild) {
            return DomImpl._node_removeChild(this, oldChild);
        }
        
        @Override
        public Node getFirstChild() {
            return DomImpl._node_getFirstChild(this);
        }
        
        @Override
        public Node getLastChild() {
            return DomImpl._node_getLastChild(this);
        }
        
        @Override
        public String getLocalName() {
            return DomImpl._node_getLocalName(this);
        }
        
        @Override
        public String getNamespaceURI() {
            return DomImpl._node_getNamespaceURI(this);
        }
        
        @Override
        public Node getNextSibling() {
            return DomImpl._node_getNextSibling(this);
        }
        
        @Override
        public String getNodeName() {
            return DomImpl._node_getNodeName(this);
        }
        
        @Override
        public short getNodeType() {
            return DomImpl._node_getNodeType(this);
        }
        
        @Override
        public String getNodeValue() {
            return DomImpl._node_getNodeValue(this);
        }
        
        @Override
        public Document getOwnerDocument() {
            return DomImpl._node_getOwnerDocument(this);
        }
        
        @Override
        public String getPrefix() {
            return DomImpl._node_getPrefix(this);
        }
        
        @Override
        public Node getPreviousSibling() {
            return DomImpl._node_getPreviousSibling(this);
        }
        
        @Override
        public boolean hasAttributes() {
            return DomImpl._node_hasAttributes(this);
        }
        
        @Override
        public boolean hasChildNodes() {
            return DomImpl._node_hasChildNodes(this);
        }
        
        @Override
        public Node insertBefore(final Node newChild, final Node refChild) {
            return DomImpl._node_insertBefore(this, newChild, refChild);
        }
        
        @Override
        public boolean isSupported(final String feature, final String version) {
            return DomImpl._node_isSupported(this, feature, version);
        }
        
        @Override
        public void normalize() {
            DomImpl._node_normalize(this);
        }
        
        @Override
        public Node replaceChild(final Node newChild, final Node oldChild) {
            return DomImpl._node_replaceChild(this, newChild, oldChild);
        }
        
        @Override
        public void setNodeValue(final String nodeValue) {
            DomImpl._node_setNodeValue(this, nodeValue);
        }
        
        @Override
        public void setPrefix(final String prefix) {
            DomImpl._node_setPrefix(this, prefix);
        }
        
        @Override
        public Object getUserData(final String key) {
            return DomImpl._node_getUserData(this, key);
        }
        
        @Override
        public Object setUserData(final String key, final Object data, final UserDataHandler handler) {
            return DomImpl._node_setUserData(this, key, data, handler);
        }
        
        @Override
        public Object getFeature(final String feature, final String version) {
            return DomImpl._node_getFeature(this, feature, version);
        }
        
        @Override
        public boolean isEqualNode(final Node arg) {
            return DomImpl._node_isEqualNode(this, arg);
        }
        
        @Override
        public boolean isSameNode(final Node arg) {
            return DomImpl._node_isSameNode(this, arg);
        }
        
        @Override
        public String lookupNamespaceURI(final String prefix) {
            return DomImpl._node_lookupNamespaceURI(this, prefix);
        }
        
        @Override
        public String lookupPrefix(final String namespaceURI) {
            return DomImpl._node_lookupPrefix(this, namespaceURI);
        }
        
        @Override
        public boolean isDefaultNamespace(final String namespaceURI) {
            return DomImpl._node_isDefaultNamespace(this, namespaceURI);
        }
        
        @Override
        public void setTextContent(final String textContent) {
            DomImpl._node_setTextContent(this, textContent);
        }
        
        @Override
        public String getTextContent() {
            return DomImpl._node_getTextContent(this);
        }
        
        @Override
        public short compareDocumentPosition(final Node other) {
            return DomImpl._node_compareDocumentPosition(this, other);
        }
        
        @Override
        public String getBaseURI() {
            return DomImpl._node_getBaseURI(this);
        }
        
        @Override
        public Node adoptNode(final Node source) {
            throw new RuntimeException("DOM Level 3 Not implemented");
        }
        
        @Override
        public String getDocumentURI() {
            throw new RuntimeException("DOM Level 3 Not implemented");
        }
        
        @Override
        public DOMConfiguration getDomConfig() {
            throw new RuntimeException("DOM Level 3 Not implemented");
        }
        
        @Override
        public String getInputEncoding() {
            throw new RuntimeException("DOM Level 3 Not implemented");
        }
        
        @Override
        public boolean getStrictErrorChecking() {
            throw new RuntimeException("DOM Level 3 Not implemented");
        }
        
        @Override
        public String getXmlEncoding() {
            throw new RuntimeException("DOM Level 3 Not implemented");
        }
        
        @Override
        public boolean getXmlStandalone() {
            throw new RuntimeException("DOM Level 3 Not implemented");
        }
        
        @Override
        public String getXmlVersion() {
            throw new RuntimeException("DOM Level 3 Not implemented");
        }
        
        @Override
        public void normalizeDocument() {
            throw new RuntimeException("DOM Level 3 Not implemented");
        }
        
        @Override
        public Node renameNode(final Node n, final String namespaceURI, final String qualifiedName) {
            throw new RuntimeException("DOM Level 3 Not implemented");
        }
        
        @Override
        public void setDocumentURI(final String documentURI) {
            throw new RuntimeException("DOM Level 3 Not implemented");
        }
        
        @Override
        public void setStrictErrorChecking(final boolean strictErrorChecking) {
            throw new RuntimeException("DOM Level 3 Not implemented");
        }
        
        @Override
        public void setXmlStandalone(final boolean xmlStandalone) {
            throw new RuntimeException("DOM Level 3 Not implemented");
        }
        
        @Override
        public void setXmlVersion(final String xmlVersion) {
            throw new RuntimeException("DOM Level 3 Not implemented");
        }
        
        @Override
        public Attr createAttribute(final String name) {
            return DomImpl._document_createAttribute(this, name);
        }
        
        @Override
        public Attr createAttributeNS(final String namespaceURI, final String qualifiedName) {
            return DomImpl._document_createAttributeNS(this, namespaceURI, qualifiedName);
        }
        
        @Override
        public CDATASection createCDATASection(final String data) {
            return DomImpl._document_createCDATASection(this, data);
        }
        
        @Override
        public Comment createComment(final String data) {
            return DomImpl._document_createComment(this, data);
        }
        
        @Override
        public DocumentFragment createDocumentFragment() {
            return DomImpl._document_createDocumentFragment(this);
        }
        
        @Override
        public Element createElement(final String tagName) {
            return DomImpl._document_createElement(this, tagName);
        }
        
        @Override
        public Element createElementNS(final String namespaceURI, final String qualifiedName) {
            return DomImpl._document_createElementNS(this, namespaceURI, qualifiedName);
        }
        
        @Override
        public EntityReference createEntityReference(final String name) {
            return DomImpl._document_createEntityReference(this, name);
        }
        
        @Override
        public ProcessingInstruction createProcessingInstruction(final String target, final String data) {
            return DomImpl._document_createProcessingInstruction(this, target, data);
        }
        
        @Override
        public Text createTextNode(final String data) {
            return DomImpl._document_createTextNode(this, data);
        }
        
        @Override
        public DocumentType getDoctype() {
            return DomImpl._document_getDoctype(this);
        }
        
        @Override
        public Element getDocumentElement() {
            return DomImpl._document_getDocumentElement(this);
        }
        
        @Override
        public Element getElementById(final String elementId) {
            return DomImpl._document_getElementById(this, elementId);
        }
        
        @Override
        public NodeList getElementsByTagName(final String tagname) {
            return DomImpl._document_getElementsByTagName(this, tagname);
        }
        
        @Override
        public NodeList getElementsByTagNameNS(final String namespaceURI, final String localName) {
            return DomImpl._document_getElementsByTagNameNS(this, namespaceURI, localName);
        }
        
        @Override
        public DOMImplementation getImplementation() {
            return DomImpl._document_getImplementation(this);
        }
        
        @Override
        public Node importNode(final Node importedNode, final boolean deep) {
            return DomImpl._document_importNode(this, importedNode, deep);
        }
        
        @Override
        public int getLength() {
            return DomImpl._childNodes_getLength(this);
        }
        
        @Override
        public Node item(final int i) {
            return DomImpl._childNodes_item(this, i);
        }
        
        @Override
        public void removeAllMimeHeaders() {
            DomImpl._soapPart_removeAllMimeHeaders(this);
        }
        
        @Override
        public void removeMimeHeader(final String name) {
            DomImpl._soapPart_removeMimeHeader(this, name);
        }
        
        @Override
        public Iterator getAllMimeHeaders() {
            return DomImpl._soapPart_getAllMimeHeaders(this);
        }
        
        @Override
        public SOAPEnvelope getEnvelope() {
            return DomImpl._soapPart_getEnvelope(this);
        }
        
        @Override
        public Source getContent() {
            return DomImpl._soapPart_getContent(this);
        }
        
        @Override
        public void setContent(final Source source) {
            DomImpl._soapPart_setContent(this, source);
        }
        
        @Override
        public String[] getMimeHeader(final String name) {
            return DomImpl._soapPart_getMimeHeader(this, name);
        }
        
        @Override
        public void addMimeHeader(final String name, final String value) {
            DomImpl._soapPart_addMimeHeader(this, name, value);
        }
        
        @Override
        public void setMimeHeader(final String name, final String value) {
            DomImpl._soapPart_setMimeHeader(this, name, value);
        }
        
        @Override
        public Iterator getMatchingMimeHeaders(final String[] names) {
            return DomImpl._soapPart_getMatchingMimeHeaders(this, names);
        }
        
        @Override
        public Iterator getNonMatchingMimeHeaders(final String[] names) {
            return DomImpl._soapPart_getNonMatchingMimeHeaders(this, names);
        }
        
        @Override
        public boolean nodeCanHavePrefixUri() {
            return true;
        }
    }
    
    static class SoapElementXobj extends ElementXobj implements SOAPElement, org.apache.xmlbeans.impl.soap.Node
    {
        SoapElementXobj(final Locale l, final QName name) {
            super(l, name);
        }
        
        @Override
        Xobj newNode(final Locale l) {
            return new SoapElementXobj(l, this._name);
        }
        
        @Override
        public void detachNode() {
            DomImpl._soapNode_detachNode(this);
        }
        
        @Override
        public void recycleNode() {
            DomImpl._soapNode_recycleNode(this);
        }
        
        @Override
        public String getValue() {
            return DomImpl._soapNode_getValue(this);
        }
        
        @Override
        public void setValue(final String value) {
            DomImpl._soapNode_setValue(this, value);
        }
        
        @Override
        public SOAPElement getParentElement() {
            return DomImpl._soapNode_getParentElement(this);
        }
        
        @Override
        public void setParentElement(final SOAPElement p) {
            DomImpl._soapNode_setParentElement(this, p);
        }
        
        @Override
        public void removeContents() {
            DomImpl._soapElement_removeContents(this);
        }
        
        @Override
        public String getEncodingStyle() {
            return DomImpl._soapElement_getEncodingStyle(this);
        }
        
        @Override
        public void setEncodingStyle(final String encodingStyle) {
            DomImpl._soapElement_setEncodingStyle(this, encodingStyle);
        }
        
        @Override
        public boolean removeNamespaceDeclaration(final String prefix) {
            return DomImpl._soapElement_removeNamespaceDeclaration(this, prefix);
        }
        
        @Override
        public Iterator getAllAttributes() {
            return DomImpl._soapElement_getAllAttributes(this);
        }
        
        @Override
        public Iterator getChildElements() {
            return DomImpl._soapElement_getChildElements(this);
        }
        
        @Override
        public Iterator getNamespacePrefixes() {
            return DomImpl._soapElement_getNamespacePrefixes(this);
        }
        
        @Override
        public SOAPElement addAttribute(final Name name, final String value) throws SOAPException {
            return DomImpl._soapElement_addAttribute(this, name, value);
        }
        
        @Override
        public SOAPElement addChildElement(final SOAPElement oldChild) throws SOAPException {
            return DomImpl._soapElement_addChildElement(this, oldChild);
        }
        
        @Override
        public SOAPElement addChildElement(final Name name) throws SOAPException {
            return DomImpl._soapElement_addChildElement(this, name);
        }
        
        @Override
        public SOAPElement addChildElement(final String localName) throws SOAPException {
            return DomImpl._soapElement_addChildElement(this, localName);
        }
        
        @Override
        public SOAPElement addChildElement(final String localName, final String prefix) throws SOAPException {
            return DomImpl._soapElement_addChildElement(this, localName, prefix);
        }
        
        @Override
        public SOAPElement addChildElement(final String localName, final String prefix, final String uri) throws SOAPException {
            return DomImpl._soapElement_addChildElement(this, localName, prefix, uri);
        }
        
        @Override
        public SOAPElement addNamespaceDeclaration(final String prefix, final String uri) {
            return DomImpl._soapElement_addNamespaceDeclaration(this, prefix, uri);
        }
        
        @Override
        public SOAPElement addTextNode(final String data) {
            return DomImpl._soapElement_addTextNode(this, data);
        }
        
        @Override
        public String getAttributeValue(final Name name) {
            return DomImpl._soapElement_getAttributeValue(this, name);
        }
        
        @Override
        public Iterator getChildElements(final Name name) {
            return DomImpl._soapElement_getChildElements(this, name);
        }
        
        @Override
        public Name getElementName() {
            return DomImpl._soapElement_getElementName(this);
        }
        
        @Override
        public String getNamespaceURI(final String prefix) {
            return DomImpl._soapElement_getNamespaceURI(this, prefix);
        }
        
        @Override
        public Iterator getVisibleNamespacePrefixes() {
            return DomImpl._soapElement_getVisibleNamespacePrefixes(this);
        }
        
        @Override
        public boolean removeAttribute(final Name name) {
            return DomImpl._soapElement_removeAttribute(this, name);
        }
    }
    
    static class SoapBodyXobj extends SoapElementXobj implements SOAPBody
    {
        SoapBodyXobj(final Locale l, final QName name) {
            super(l, name);
        }
        
        @Override
        Xobj newNode(final Locale l) {
            return new SoapBodyXobj(l, this._name);
        }
        
        @Override
        public boolean hasFault() {
            return DomImpl.soapBody_hasFault(this);
        }
        
        @Override
        public SOAPFault addFault() throws SOAPException {
            return DomImpl.soapBody_addFault(this);
        }
        
        @Override
        public SOAPFault getFault() {
            return DomImpl.soapBody_getFault(this);
        }
        
        @Override
        public SOAPBodyElement addBodyElement(final Name name) {
            return DomImpl.soapBody_addBodyElement(this, name);
        }
        
        @Override
        public SOAPBodyElement addDocument(final Document document) {
            return DomImpl.soapBody_addDocument(this, document);
        }
        
        @Override
        public SOAPFault addFault(final Name name, final String s) throws SOAPException {
            return DomImpl.soapBody_addFault(this, name, s);
        }
        
        @Override
        public SOAPFault addFault(final Name faultCode, final String faultString, final java.util.Locale locale) throws SOAPException {
            return DomImpl.soapBody_addFault(this, faultCode, faultString, locale);
        }
    }
    
    static class SoapBodyElementXobj extends SoapElementXobj implements SOAPBodyElement
    {
        SoapBodyElementXobj(final Locale l, final QName name) {
            super(l, name);
        }
        
        @Override
        Xobj newNode(final Locale l) {
            return new SoapBodyElementXobj(l, this._name);
        }
    }
    
    static class SoapEnvelopeXobj extends SoapElementXobj implements SOAPEnvelope
    {
        SoapEnvelopeXobj(final Locale l, final QName name) {
            super(l, name);
        }
        
        @Override
        Xobj newNode(final Locale l) {
            return new SoapEnvelopeXobj(l, this._name);
        }
        
        @Override
        public SOAPBody addBody() throws SOAPException {
            return DomImpl._soapEnvelope_addBody(this);
        }
        
        @Override
        public SOAPBody getBody() throws SOAPException {
            return DomImpl._soapEnvelope_getBody(this);
        }
        
        @Override
        public SOAPHeader getHeader() throws SOAPException {
            return DomImpl._soapEnvelope_getHeader(this);
        }
        
        @Override
        public SOAPHeader addHeader() throws SOAPException {
            return DomImpl._soapEnvelope_addHeader(this);
        }
        
        @Override
        public Name createName(final String localName) {
            return DomImpl._soapEnvelope_createName(this, localName);
        }
        
        @Override
        public Name createName(final String localName, final String prefix, final String namespaceURI) {
            return DomImpl._soapEnvelope_createName(this, localName, prefix, namespaceURI);
        }
    }
    
    static class SoapHeaderXobj extends SoapElementXobj implements SOAPHeader
    {
        SoapHeaderXobj(final Locale l, final QName name) {
            super(l, name);
        }
        
        @Override
        Xobj newNode(final Locale l) {
            return new SoapHeaderXobj(l, this._name);
        }
        
        @Override
        public Iterator examineAllHeaderElements() {
            return DomImpl.soapHeader_examineAllHeaderElements(this);
        }
        
        @Override
        public Iterator extractAllHeaderElements() {
            return DomImpl.soapHeader_extractAllHeaderElements(this);
        }
        
        @Override
        public Iterator examineHeaderElements(final String actor) {
            return DomImpl.soapHeader_examineHeaderElements(this, actor);
        }
        
        @Override
        public Iterator examineMustUnderstandHeaderElements(final String mustUnderstandString) {
            return DomImpl.soapHeader_examineMustUnderstandHeaderElements(this, mustUnderstandString);
        }
        
        @Override
        public Iterator extractHeaderElements(final String actor) {
            return DomImpl.soapHeader_extractHeaderElements(this, actor);
        }
        
        @Override
        public SOAPHeaderElement addHeaderElement(final Name name) {
            return DomImpl.soapHeader_addHeaderElement(this, name);
        }
    }
    
    static class SoapHeaderElementXobj extends SoapElementXobj implements SOAPHeaderElement
    {
        SoapHeaderElementXobj(final Locale l, final QName name) {
            super(l, name);
        }
        
        @Override
        Xobj newNode(final Locale l) {
            return new SoapHeaderElementXobj(l, this._name);
        }
        
        @Override
        public void setMustUnderstand(final boolean mustUnderstand) {
            DomImpl.soapHeaderElement_setMustUnderstand(this, mustUnderstand);
        }
        
        @Override
        public boolean getMustUnderstand() {
            return DomImpl.soapHeaderElement_getMustUnderstand(this);
        }
        
        @Override
        public void setActor(final String actor) {
            DomImpl.soapHeaderElement_setActor(this, actor);
        }
        
        @Override
        public String getActor() {
            return DomImpl.soapHeaderElement_getActor(this);
        }
    }
    
    static class SoapFaultXobj extends SoapBodyElementXobj implements SOAPFault
    {
        SoapFaultXobj(final Locale l, final QName name) {
            super(l, name);
        }
        
        @Override
        Xobj newNode(final Locale l) {
            return new SoapFaultXobj(l, this._name);
        }
        
        @Override
        public void setFaultString(final String faultString) {
            DomImpl.soapFault_setFaultString(this, faultString);
        }
        
        @Override
        public void setFaultString(final String faultString, final java.util.Locale locale) {
            DomImpl.soapFault_setFaultString(this, faultString, locale);
        }
        
        @Override
        public void setFaultCode(final Name faultCodeName) throws SOAPException {
            DomImpl.soapFault_setFaultCode(this, faultCodeName);
        }
        
        @Override
        public void setFaultActor(final String faultActorString) {
            DomImpl.soapFault_setFaultActor(this, faultActorString);
        }
        
        @Override
        public String getFaultActor() {
            return DomImpl.soapFault_getFaultActor(this);
        }
        
        @Override
        public String getFaultCode() {
            return DomImpl.soapFault_getFaultCode(this);
        }
        
        @Override
        public void setFaultCode(final String faultCode) throws SOAPException {
            DomImpl.soapFault_setFaultCode(this, faultCode);
        }
        
        @Override
        public java.util.Locale getFaultStringLocale() {
            return DomImpl.soapFault_getFaultStringLocale(this);
        }
        
        @Override
        public Name getFaultCodeAsName() {
            return DomImpl.soapFault_getFaultCodeAsName(this);
        }
        
        @Override
        public String getFaultString() {
            return DomImpl.soapFault_getFaultString(this);
        }
        
        @Override
        public Detail addDetail() throws SOAPException {
            return DomImpl.soapFault_addDetail(this);
        }
        
        @Override
        public Detail getDetail() {
            return DomImpl.soapFault_getDetail(this);
        }
    }
    
    static class SoapFaultElementXobj extends SoapElementXobj implements SOAPFaultElement
    {
        SoapFaultElementXobj(final Locale l, final QName name) {
            super(l, name);
        }
        
        @Override
        Xobj newNode(final Locale l) {
            return new SoapFaultElementXobj(l, this._name);
        }
    }
    
    static class DetailXobj extends SoapFaultElementXobj implements Detail
    {
        DetailXobj(final Locale l, final QName name) {
            super(l, name);
        }
        
        @Override
        Xobj newNode(final Locale l) {
            return new DetailXobj(l, this._name);
        }
        
        @Override
        public DetailEntry addDetailEntry(final Name name) {
            return DomImpl.detail_addDetailEntry(this, name);
        }
        
        @Override
        public Iterator getDetailEntries() {
            return DomImpl.detail_getDetailEntries(this);
        }
    }
    
    static class DetailEntryXobj extends SoapElementXobj implements DetailEntry
    {
        @Override
        Xobj newNode(final Locale l) {
            return new DetailEntryXobj(l, this._name);
        }
        
        DetailEntryXobj(final Locale l, final QName name) {
            super(l, name);
        }
    }
    
    static class Bookmark implements XmlCursor.XmlMark
    {
        Xobj _xobj;
        int _pos;
        Bookmark _next;
        Bookmark _prev;
        Object _key;
        Object _value;
        
        boolean isOnList(Bookmark head) {
            while (head != null) {
                if (head == this) {
                    return true;
                }
                head = head._next;
            }
            return false;
        }
        
        Bookmark listInsert(Bookmark head) {
            assert this._next == null && this._prev == null;
            if (head == null) {
                this._prev = this;
                head = this;
            }
            else {
                this._prev = head._prev;
                final Bookmark bookmark = head;
                head._prev._next = this;
                bookmark._prev = this;
            }
            return head;
        }
        
        Bookmark listRemove(Bookmark head) {
            assert this._prev != null && this.isOnList(head);
            if (this._prev == this) {
                head = null;
            }
            else {
                if (head == this) {
                    head = this._next;
                }
                else {
                    this._prev._next = this._next;
                }
                if (this._next == null) {
                    head._prev = this._prev;
                }
                else {
                    this._next._prev = this._prev;
                    this._next = null;
                }
            }
            this._prev = null;
            assert this._next == null;
            return head;
        }
        
        void moveTo(final Xobj x, final int p) {
            assert this.isOnList(this._xobj._bookmarks);
            if (this._xobj != x) {
                this._xobj._bookmarks = this.listRemove(this._xobj._bookmarks);
                x._bookmarks = this.listInsert(x._bookmarks);
                this._xobj = x;
            }
            this._pos = p;
        }
        
        @Override
        public XmlCursor createCursor() {
            if (this._xobj == null) {
                throw new IllegalStateException("Attempting to create a cursor on a bookmark that has been cleared or replaced.");
            }
            return Cursor.newCursor(this._xobj, this._pos);
        }
    }
}
