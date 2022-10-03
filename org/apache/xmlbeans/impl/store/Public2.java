package org.apache.xmlbeans.impl.store;

import org.apache.xmlbeans.impl.values.TypeStoreVisitor;
import org.apache.xmlbeans.QNameSet;
import org.apache.xmlbeans.SchemaField;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.impl.values.NamespaceManager;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.TypeStore;
import org.apache.xmlbeans.impl.values.TypeStoreUser;
import org.apache.xmlbeans.XmlObject;
import java.io.PrintStream;
import org.apache.xmlbeans.XmlCursor;
import java.io.OutputStream;
import org.w3c.dom.Node;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;
import java.io.InputStream;
import org.apache.xmlbeans.XmlException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.apache.xmlbeans.SchemaTypeLoader;
import org.apache.xmlbeans.XmlOptions;

public final class Public2
{
    private static Locale newLocale(final Saaj saaj) {
        XmlOptions options = null;
        if (saaj != null) {
            options = new XmlOptions();
            options.put("SAAJ_IMPL", saaj);
        }
        return Locale.getLocale(null, options);
    }
    
    private static Locale newLocale() {
        return Locale.getLocale(null, null);
    }
    
    public static void setSync(final Document doc, final boolean sync) {
        assert doc instanceof DomImpl.Dom;
        final Locale l = ((DomImpl.Dom)doc).locale();
        l._noSync = !sync;
    }
    
    public static String compilePath(final String path, final XmlOptions options) {
        return Path.compilePath(path, options);
    }
    
    public static DOMImplementation getDomImplementation() {
        return newLocale();
    }
    
    public static DOMImplementation getDomImplementation(final Saaj saaj) {
        return newLocale(saaj);
    }
    
    public static Document parse(final String s) throws XmlException {
        final Locale l = newLocale();
        DomImpl.Dom d;
        if (l.noSync()) {
            l.enter();
            try {
                d = l.load(s);
            }
            finally {
                l.exit();
            }
        }
        else {
            synchronized (l) {
                l.enter();
                try {
                    d = l.load(s);
                }
                finally {
                    l.exit();
                }
            }
        }
        return (Document)d;
    }
    
    public static Document parse(final String s, final XmlOptions options) throws XmlException {
        final Locale l = newLocale();
        DomImpl.Dom d;
        if (l.noSync()) {
            l.enter();
            try {
                d = l.load(s, options);
            }
            finally {
                l.exit();
            }
        }
        else {
            synchronized (l) {
                l.enter();
                try {
                    d = l.load(s, options);
                }
                finally {
                    l.exit();
                }
            }
        }
        return (Document)d;
    }
    
    public static Document parse(final String s, final Saaj saaj) throws XmlException {
        final Locale l = newLocale(saaj);
        DomImpl.Dom d;
        if (l.noSync()) {
            l.enter();
            try {
                d = l.load(s);
            }
            finally {
                l.exit();
            }
        }
        else {
            synchronized (l) {
                l.enter();
                try {
                    d = l.load(s);
                }
                finally {
                    l.exit();
                }
            }
        }
        return (Document)d;
    }
    
    public static Document parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
        final Locale l = newLocale();
        DomImpl.Dom d;
        if (l.noSync()) {
            l.enter();
            try {
                d = l.load(is, options);
            }
            finally {
                l.exit();
            }
        }
        else {
            synchronized (l) {
                l.enter();
                try {
                    d = l.load(is, options);
                }
                finally {
                    l.exit();
                }
            }
        }
        return (Document)d;
    }
    
    public static Document parse(final InputStream is, final Saaj saaj) throws XmlException, IOException {
        final Locale l = newLocale(saaj);
        DomImpl.Dom d;
        if (l.noSync()) {
            l.enter();
            try {
                d = l.load(is);
            }
            finally {
                l.exit();
            }
        }
        else {
            synchronized (l) {
                l.enter();
                try {
                    d = l.load(is);
                }
                finally {
                    l.exit();
                }
            }
        }
        return (Document)d;
    }
    
    public static Node getNode(final XMLStreamReader s) {
        return Jsr173.nodeFromStream(s);
    }
    
    public static XMLStreamReader getStream(final Node n) {
        assert n instanceof DomImpl.Dom;
        final DomImpl.Dom d = (DomImpl.Dom)n;
        final Locale l = d.locale();
        if (l.noSync()) {
            l.enter();
            try {
                return DomImpl.getXmlStreamReader(d);
            }
            finally {
                l.exit();
            }
        }
        synchronized (l) {
            l.enter();
            try {
                return DomImpl.getXmlStreamReader(d);
            }
            finally {
                l.exit();
            }
        }
    }
    
    public static String save(final Node n) {
        return save(n, null);
    }
    
    public static void save(final Node n, final OutputStream os, final XmlOptions options) throws IOException {
        final XmlCursor c = getCursor(n);
        c.save(os, options);
        c.dispose();
    }
    
    public static String save(final Node n, final XmlOptions options) {
        assert n instanceof DomImpl.Dom;
        final DomImpl.Dom d = (DomImpl.Dom)n;
        final Locale l = d.locale();
        if (l.noSync()) {
            l.enter();
            try {
                return saveImpl(d, options);
            }
            finally {
                l.exit();
            }
        }
        synchronized (l) {
            l.enter();
            try {
                return saveImpl(d, options);
            }
            finally {
                l.exit();
            }
        }
    }
    
    private static String saveImpl(final DomImpl.Dom d, final XmlOptions options) {
        final Cur c = d.tempCur();
        final String s = new Saver.TextSaver(c, options, null).saveToString();
        c.release();
        return s;
    }
    
    public static String save(final XmlCursor c) {
        return save(c, null);
    }
    
    public static String save(final XmlCursor xc, final XmlOptions options) {
        final Cursor cursor = (Cursor)xc;
        final Locale l = cursor.locale();
        if (l.noSync()) {
            l.enter();
            try {
                return saveImpl(cursor, options);
            }
            finally {
                l.exit();
            }
        }
        synchronized (l) {
            l.enter();
            try {
                return saveImpl(cursor, options);
            }
            finally {
                l.exit();
            }
        }
    }
    
    private static String saveImpl(final Cursor cursor, final XmlOptions options) {
        final Cur c = cursor.tempCur();
        final String s = new Saver.TextSaver(c, options, null).saveToString();
        c.release();
        return s;
    }
    
    public static XmlCursor newStore() {
        return newStore(null);
    }
    
    public static XmlCursor newStore(final Saaj saaj) {
        final Locale l = newLocale(saaj);
        if (l.noSync()) {
            l.enter();
            try {
                return _newStore(l);
            }
            finally {
                l.exit();
            }
        }
        synchronized (l) {
            l.enter();
            try {
                return _newStore(l);
            }
            finally {
                l.exit();
            }
        }
    }
    
    public static XmlCursor _newStore(final Locale l) {
        final Cur c = l.tempCur();
        c.createRoot();
        final Cursor cursor = new Cursor(c);
        c.release();
        return cursor;
    }
    
    public static XmlCursor getCursor(final Node n) {
        assert n instanceof DomImpl.Dom;
        final DomImpl.Dom d = (DomImpl.Dom)n;
        final Locale l = d.locale();
        if (l.noSync()) {
            l.enter();
            try {
                return DomImpl.getXmlCursor(d);
            }
            finally {
                l.exit();
            }
        }
        synchronized (l) {
            l.enter();
            try {
                return DomImpl.getXmlCursor(d);
            }
            finally {
                l.exit();
            }
        }
    }
    
    public static void dump(final PrintStream o, final DomImpl.Dom d) {
        d.dump(o);
    }
    
    public static void dump(final PrintStream o, final Node n) {
        dump(o, (DomImpl.Dom)n);
    }
    
    public static void dump(final PrintStream o, final XmlCursor c) {
        ((Cursor)c).dump(o);
    }
    
    public static void dump(final PrintStream o, final XmlObject x) {
        final XmlCursor xc = x.newCursor();
        final Node n = xc.getDomNode();
        final DomImpl.Dom d = (DomImpl.Dom)n;
        xc.dispose();
        dump(o, d);
    }
    
    public static void dump(final DomImpl.Dom d) {
        dump(System.out, d);
    }
    
    public static void dump(final Node n) {
        dump(System.out, n);
    }
    
    public static void dump(final XmlCursor c) {
        dump(System.out, c);
    }
    
    public static void dump(final XmlObject x) {
        dump(System.out, x);
    }
    
    public static void test() throws Exception {
        final Xobj x = (Xobj)parse("<a>XY</a>");
        final Locale l = x._locale;
        l.enter();
        try {
            final Cur c = x.tempCur();
            c.next();
            final Cur c2 = c.tempCur();
            c2.next();
            final Cur c3 = c2.tempCur();
            c3.nextChars(1);
            final Cur c4 = c3.tempCur();
            c4.nextChars(1);
            c.dump();
            c.moveNodeContents(c, true);
            c.dump();
        }
        catch (final Throwable e) {
            e.printStackTrace();
        }
        finally {
            l.exit();
        }
    }
    
    private static class TestTypeStoreUser implements TypeStoreUser
    {
        private String _value;
        
        TestTypeStoreUser(final String value) {
            this._value = value;
        }
        
        @Override
        public void attach_store(final TypeStore store) {
        }
        
        @Override
        public SchemaType get_schema_type() {
            throw new RuntimeException("Not impl");
        }
        
        @Override
        public TypeStore get_store() {
            throw new RuntimeException("Not impl");
        }
        
        @Override
        public void invalidate_value() {
        }
        
        @Override
        public boolean uses_invalidate_value() {
            throw new RuntimeException("Not impl");
        }
        
        @Override
        public String build_text(final NamespaceManager nsm) {
            return this._value;
        }
        
        @Override
        public boolean build_nil() {
            throw new RuntimeException("Not impl");
        }
        
        @Override
        public void invalidate_nilvalue() {
            throw new RuntimeException("Not impl");
        }
        
        @Override
        public void invalidate_element_order() {
            throw new RuntimeException("Not impl");
        }
        
        @Override
        public void validate_now() {
            throw new RuntimeException("Not impl");
        }
        
        @Override
        public void disconnect_store() {
            throw new RuntimeException("Not impl");
        }
        
        @Override
        public TypeStoreUser create_element_user(final QName eltName, final QName xsiType) {
            return new TestTypeStoreUser("ELEM");
        }
        
        @Override
        public TypeStoreUser create_attribute_user(final QName attrName) {
            throw new RuntimeException("Not impl");
        }
        
        @Override
        public String get_default_element_text(final QName eltName) {
            throw new RuntimeException("Not impl");
        }
        
        @Override
        public String get_default_attribute_text(final QName attrName) {
            throw new RuntimeException("Not impl");
        }
        
        @Override
        public SchemaType get_element_type(final QName eltName, final QName xsiType) {
            throw new RuntimeException("Not impl");
        }
        
        @Override
        public SchemaType get_attribute_type(final QName attrName) {
            throw new RuntimeException("Not impl");
        }
        
        @Override
        public int get_elementflags(final QName eltName) {
            throw new RuntimeException("Not impl");
        }
        
        @Override
        public int get_attributeflags(final QName attrName) {
            throw new RuntimeException("Not impl");
        }
        
        @Override
        public SchemaField get_attribute_field(final QName attrName) {
            throw new RuntimeException("Not impl");
        }
        
        @Override
        public boolean is_child_element_order_sensitive() {
            throw new RuntimeException("Not impl");
        }
        
        @Override
        public QNameSet get_element_ending_delimiters(final QName eltname) {
            throw new RuntimeException("Not impl");
        }
        
        @Override
        public TypeStoreVisitor new_visitor() {
            throw new RuntimeException("Not impl");
        }
    }
}
