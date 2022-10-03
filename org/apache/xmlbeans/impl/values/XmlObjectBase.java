package org.apache.xmlbeans.impl.values;

import java.io.ObjectStreamException;
import java.io.DataInputStream;
import java.io.ByteArrayInputStream;
import org.apache.xmlbeans.XmlBeans;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.apache.xmlbeans.SchemaLocalAttribute;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;
import org.apache.xmlbeans.impl.common.GlobalLock;
import org.apache.xmlbeans.XmlAnySimpleType;
import org.apache.xmlbeans.GDurationSpecification;
import org.apache.xmlbeans.GDateSpecification;
import java.util.List;
import org.apache.xmlbeans.GDuration;
import org.apache.xmlbeans.GDate;
import java.util.Date;
import java.util.Calendar;
import org.apache.xmlbeans.StringEnumAbstractBase;
import java.math.BigDecimal;
import org.apache.xmlbeans.impl.common.XmlWhitespace;
import org.apache.xmlbeans.SchemaAttributeModel;
import org.apache.xmlbeans.impl.schema.SchemaTypeVisitorImpl;
import org.apache.xmlbeans.QNameSet;
import org.apache.xmlbeans.impl.schema.SchemaTypeImpl;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlRuntimeException;
import java.lang.reflect.Array;
import org.apache.xmlbeans.SchemaProperty;
import org.apache.xmlbeans.XmlError;
import org.apache.xmlbeans.impl.common.QNameHelper;
import org.apache.xmlbeans.impl.common.XmlErrorWatcher;
import org.apache.xmlbeans.impl.common.ValidatorListener;
import java.util.Collection;
import org.apache.xmlbeans.impl.validator.Validator;
import org.apache.xmlbeans.SchemaField;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.common.XmlLocale;
import java.io.Writer;
import java.io.OutputStream;
import java.io.IOException;
import java.io.File;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.ContentHandler;
import org.w3c.dom.Node;
import java.io.Reader;
import java.io.InputStream;
import javax.xml.stream.XMLStreamReader;
import org.apache.xmlbeans.xml.stream.XMLInputStream;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlDocumentProperties;
import org.apache.xmlbeans.SchemaTypeLoader;
import org.apache.xmlbeans.DelegateXmlObject;
import org.apache.xmlbeans.XmlOptions;
import java.math.BigInteger;
import org.apache.xmlbeans.impl.common.ValidationContext;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.XmlObject;
import java.io.Serializable;

public abstract class XmlObjectBase implements TypeStoreUser, Serializable, XmlObject, SimpleValue
{
    public static final short MAJOR_VERSION_NUMBER = 1;
    public static final short MINOR_VERSION_NUMBER = 1;
    public static final short KIND_SETTERHELPER_SINGLETON = 1;
    public static final short KIND_SETTERHELPER_ARRAYITEM = 2;
    public static final ValidationContext _voorVc;
    private int _flags;
    private Object _textsource;
    private static final int FLAG_NILLABLE = 1;
    private static final int FLAG_HASDEFAULT = 2;
    private static final int FLAG_FIXED = 4;
    private static final int FLAG_ATTRIBUTE = 8;
    private static final int FLAG_STORE = 16;
    private static final int FLAG_VALUE_DATED = 32;
    private static final int FLAG_NIL = 64;
    private static final int FLAG_NIL_DATED = 128;
    private static final int FLAG_ISDEFAULT = 256;
    private static final int FLAG_ELEMENT_DATED = 512;
    private static final int FLAG_SETTINGDEFAULT = 1024;
    private static final int FLAG_ORPHANED = 2048;
    private static final int FLAG_IMMUTABLE = 4096;
    private static final int FLAG_COMPLEXTYPE = 8192;
    private static final int FLAG_COMPLEXCONTENT = 16384;
    private static final int FLAG_NOT_VARIABLE = 32768;
    private static final int FLAG_VALIDATE_ON_SET = 65536;
    private static final int FLAGS_DATED = 672;
    private static final int FLAGS_ELEMENT = 7;
    private static final BigInteger _max;
    private static final BigInteger _min;
    private static final XmlOptions _toStringOptions;
    private static final XmlObject[] EMPTY_RESULT;
    static final /* synthetic */ boolean $assertionsDisabled;
    
    @Override
    public final Object monitor() {
        if (this.has_store()) {
            return this.get_store().get_locale();
        }
        return this;
    }
    
    private static XmlObjectBase underlying(XmlObject obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof XmlObjectBase) {
            return (XmlObjectBase)obj;
        }
        while (obj instanceof DelegateXmlObject) {
            obj = ((DelegateXmlObject)obj).underlyingXmlObject();
        }
        if (obj instanceof XmlObjectBase) {
            return (XmlObjectBase)obj;
        }
        throw new IllegalStateException("Non-native implementations of XmlObject should extend FilterXmlObject or implement DelegateXmlObject");
    }
    
    @Override
    public final XmlObject copy() {
        if (this.preCheck()) {
            return this._copy();
        }
        synchronized (this.monitor()) {
            return this._copy();
        }
    }
    
    @Override
    public final XmlObject copy(final XmlOptions options) {
        if (this.preCheck()) {
            return this._copy(options);
        }
        synchronized (this.monitor()) {
            return this._copy(options);
        }
    }
    
    private boolean preCheck() {
        return this.has_store() && this.get_store().get_locale().noSync();
    }
    
    public final XmlObject _copy() {
        return this._copy(null);
    }
    
    public final XmlObject _copy(final XmlOptions xmlOptions) {
        if (this.isImmutable()) {
            return this;
        }
        this.check_orphaned();
        final SchemaTypeLoader stl = this.get_store().get_schematypeloader();
        final XmlObject result = (XmlObject)this.get_store().copy(stl, this.schemaType(), xmlOptions);
        return result;
    }
    
    @Override
    public XmlDocumentProperties documentProperties() {
        final XmlCursor cur = this.newCursorForce();
        try {
            return cur.documentProperties();
        }
        finally {
            cur.dispose();
        }
    }
    
    @Override
    @Deprecated
    public XMLInputStream newXMLInputStream() {
        return this.newXMLInputStream(null);
    }
    
    @Override
    @Deprecated
    public XMLInputStream newXMLInputStream(final XmlOptions options) {
        final XmlCursor cur = this.newCursorForce();
        try {
            return cur.newXMLInputStream(makeInnerOptions(options));
        }
        finally {
            cur.dispose();
        }
    }
    
    @Override
    public XMLStreamReader newXMLStreamReader() {
        return this.newXMLStreamReader(null);
    }
    
    @Override
    public XMLStreamReader newXMLStreamReader(final XmlOptions options) {
        final XmlCursor cur = this.newCursorForce();
        try {
            return cur.newXMLStreamReader(makeInnerOptions(options));
        }
        finally {
            cur.dispose();
        }
    }
    
    @Override
    public InputStream newInputStream() {
        return this.newInputStream(null);
    }
    
    @Override
    public InputStream newInputStream(final XmlOptions options) {
        final XmlCursor cur = this.newCursorForce();
        try {
            return cur.newInputStream(makeInnerOptions(options));
        }
        finally {
            cur.dispose();
        }
    }
    
    @Override
    public Reader newReader() {
        return this.newReader(null);
    }
    
    @Override
    public Reader newReader(final XmlOptions options) {
        final XmlCursor cur = this.newCursorForce();
        try {
            return cur.newReader(makeInnerOptions(options));
        }
        finally {
            cur.dispose();
        }
    }
    
    @Override
    public Node getDomNode() {
        final XmlCursor cur = this.newCursorForce();
        try {
            return cur.getDomNode();
        }
        finally {
            cur.dispose();
        }
    }
    
    @Override
    public Node newDomNode() {
        return this.newDomNode(null);
    }
    
    @Override
    public Node newDomNode(final XmlOptions options) {
        final XmlCursor cur = this.newCursorForce();
        try {
            return cur.newDomNode(makeInnerOptions(options));
        }
        finally {
            cur.dispose();
        }
    }
    
    @Override
    public void save(final ContentHandler ch, final LexicalHandler lh, final XmlOptions options) throws SAXException {
        final XmlCursor cur = this.newCursorForce();
        try {
            cur.save(ch, lh, makeInnerOptions(options));
        }
        finally {
            cur.dispose();
        }
    }
    
    @Override
    public void save(final File file, final XmlOptions options) throws IOException {
        final XmlCursor cur = this.newCursorForce();
        try {
            cur.save(file, makeInnerOptions(options));
        }
        finally {
            cur.dispose();
        }
    }
    
    @Override
    public void save(final OutputStream os, final XmlOptions options) throws IOException {
        final XmlCursor cur = this.newCursorForce();
        try {
            cur.save(os, makeInnerOptions(options));
        }
        finally {
            cur.dispose();
        }
    }
    
    @Override
    public void save(final Writer w, final XmlOptions options) throws IOException {
        final XmlCursor cur = this.newCursorForce();
        try {
            cur.save(w, makeInnerOptions(options));
        }
        finally {
            cur.dispose();
        }
    }
    
    @Override
    public void save(final ContentHandler ch, final LexicalHandler lh) throws SAXException {
        this.save(ch, lh, null);
    }
    
    @Override
    public void save(final File file) throws IOException {
        this.save(file, null);
    }
    
    @Override
    public void save(final OutputStream os) throws IOException {
        this.save(os, null);
    }
    
    @Override
    public void save(final Writer w) throws IOException {
        this.save(w, null);
    }
    
    @Override
    public void dump() {
        final XmlCursor cur = this.newCursorForce();
        try {
            cur.dump();
        }
        finally {
            cur.dispose();
        }
    }
    
    public XmlCursor newCursorForce() {
        synchronized (this.monitor()) {
            return this.ensureStore().newCursor();
        }
    }
    
    private XmlObject ensureStore() {
        if ((this._flags & 0x10) != 0x0) {
            return this;
        }
        this.check_dated();
        final String value = ((this._flags & 0x40) != 0x0) ? "" : this.compute_text(this.has_store() ? this.get_store() : null);
        final XmlOptions options = new XmlOptions().setDocumentType(this.schemaType());
        final XmlObject x = Factory.newInstance(options);
        final XmlCursor c = x.newCursor();
        c.toNextToken();
        c.insertChars(value);
        return x;
    }
    
    private static XmlOptions makeInnerOptions(final XmlOptions options) {
        final XmlOptions innerOptions = new XmlOptions(options);
        innerOptions.put("SAVE_INNER");
        return innerOptions;
    }
    
    @Override
    public XmlCursor newCursor() {
        if ((this._flags & 0x10) == 0x0) {
            throw new IllegalStateException("XML Value Objects cannot create cursors");
        }
        this.check_orphaned();
        final XmlLocale l = this.getXmlLocale();
        if (l.noSync()) {
            l.enter();
            try {
                return this.get_store().new_cursor();
            }
            finally {
                l.exit();
            }
        }
        synchronized (l) {
            l.enter();
            try {
                return this.get_store().new_cursor();
            }
            finally {
                l.exit();
            }
        }
    }
    
    @Override
    public abstract SchemaType schemaType();
    
    @Override
    public SchemaType instanceType() {
        synchronized (this.monitor()) {
            return this.isNil() ? null : this.schemaType();
        }
    }
    
    private SchemaField schemaField() {
        final SchemaType st = this.schemaType();
        SchemaField field = st.getContainerField();
        if (field == null) {
            field = this.get_store().get_schema_field();
        }
        return field;
    }
    
    @Override
    public boolean validate() {
        return this.validate(null);
    }
    
    @Override
    public boolean validate(final XmlOptions options) {
        if ((this._flags & 0x10) == 0x0) {
            if ((this._flags & 0x1000) != 0x0) {
                return this.validate_immutable(options);
            }
            throw new IllegalStateException("XML objects with no underlying store cannot be validated");
        }
        else {
            synchronized (this.monitor()) {
                if ((this._flags & 0x800) != 0x0) {
                    throw new XmlValueDisconnectedException();
                }
                final SchemaField field = this.schemaField();
                final SchemaType type = this.schemaType();
                final TypeStore typeStore = this.get_store();
                final Validator validator = new Validator(type, field, typeStore.get_schematypeloader(), options, null);
                typeStore.validate(validator);
                return validator.isValid();
            }
        }
    }
    
    private boolean validate_immutable(final XmlOptions options) {
        final Collection errorListener = (options == null) ? null : ((Collection)options.get("ERROR_LISTENER"));
        final XmlErrorWatcher watcher = new XmlErrorWatcher(errorListener);
        if (!this.schemaType().isSimpleType() && (options == null || !options.hasOption("VALIDATE_TEXT_ONLY"))) {
            final SchemaProperty[] properties = this.schemaType().getProperties();
            for (int i = 0; i < properties.length; ++i) {
                if (properties[i].getMinOccurs().signum() > 0) {
                    if (properties[i].isAttribute()) {
                        watcher.add(XmlError.forObject("cvc-complex-type.4", new Object[] { QNameHelper.pretty(properties[i].getName()) }, this));
                    }
                    else {
                        watcher.add(XmlError.forObject("cvc-complex-type.2.4c", new Object[] { properties[i].getMinOccurs(), QNameHelper.pretty(properties[i].getName()) }, this));
                    }
                }
            }
            if (this.schemaType().getContentType() != 2) {
                return !watcher.hasError();
            }
        }
        String text = (String)this._textsource;
        if (text == null) {
            text = "";
        }
        this.validate_simpleval(text, new ImmutableValueValidationContext(watcher, this));
        return !watcher.hasError();
    }
    
    protected void validate_simpleval(final String lexical, final ValidationContext ctx) {
    }
    
    private static XmlObject[] _typedArray(final XmlObject[] input) {
        if (input.length == 0) {
            return input;
        }
        SchemaType commonType = input[0].schemaType();
        if (commonType.equals(XmlObject.type) || commonType.isNoType()) {
            return input;
        }
        for (int i = 1; i < input.length; ++i) {
            if (input[i].schemaType().isNoType()) {
                return input;
            }
            commonType = commonType.getCommonBaseType(input[i].schemaType());
            if (commonType.equals(XmlObject.type)) {
                return input;
            }
        }
        Class desiredClass;
        for (desiredClass = commonType.getJavaClass(); desiredClass == null; desiredClass = commonType.getJavaClass()) {
            commonType = commonType.getBaseType();
            if (XmlObject.type.equals(commonType)) {
                return input;
            }
        }
        final XmlObject[] result = (XmlObject[])Array.newInstance(desiredClass, input.length);
        System.arraycopy(input, 0, result, 0, input.length);
        return result;
    }
    
    @Override
    public XmlObject[] selectPath(final String path) {
        return this.selectPath(path, null);
    }
    
    @Override
    public XmlObject[] selectPath(final String path, final XmlOptions options) {
        final XmlCursor c = this.newCursor();
        if (c == null) {
            throw new XmlValueDisconnectedException();
        }
        XmlObject[] selections;
        try {
            c.selectPath(path, options);
            if (!c.hasNextSelection()) {
                selections = XmlObjectBase.EMPTY_RESULT;
            }
            else {
                selections = new XmlObject[c.getSelectionCount()];
                int i = 0;
                while (c.toNextSelection()) {
                    final XmlObject[] array = selections;
                    final int n = i;
                    final XmlObject object = c.getObject();
                    array[n] = object;
                    if (object == null && (!c.toParent() || (selections[i] = c.getObject()) == null)) {
                        throw new XmlRuntimeException("Path must select only elements and attributes");
                    }
                    ++i;
                }
            }
        }
        finally {
            c.dispose();
        }
        return _typedArray(selections);
    }
    
    @Override
    public XmlObject[] execQuery(final String path) {
        return this.execQuery(path, null);
    }
    
    @Override
    public XmlObject[] execQuery(final String queryExpr, final XmlOptions options) {
        synchronized (this.monitor()) {
            final TypeStore typeStore = this.get_store();
            if (typeStore == null) {
                throw new XmlRuntimeException("Cannot do XQuery on XML Value Objects");
            }
            try {
                return _typedArray(typeStore.exec_query(queryExpr, options));
            }
            catch (final XmlException e) {
                throw new XmlRuntimeException(e);
            }
        }
    }
    
    @Override
    public XmlObject changeType(final SchemaType type) {
        if (type == null) {
            throw new IllegalArgumentException("Invalid type (null)");
        }
        if ((this._flags & 0x10) == 0x0) {
            throw new IllegalStateException("XML Value Objects cannot have thier type changed");
        }
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlObject)this.get_store().change_type(type);
        }
    }
    
    @Override
    public XmlObject substitute(final QName name, final SchemaType type) {
        if (name == null) {
            throw new IllegalArgumentException("Invalid name (null)");
        }
        if (type == null) {
            throw new IllegalArgumentException("Invalid type (null)");
        }
        if ((this._flags & 0x10) == 0x0) {
            throw new IllegalStateException("XML Value Objects cannot be used with substitution");
        }
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlObject)this.get_store().substitute(name, type);
        }
    }
    
    protected XmlObjectBase() {
        this._flags = 65;
    }
    
    public void init_flags(final SchemaProperty prop) {
        if (prop == null) {
            return;
        }
        if (prop.hasDefault() == 1 || prop.hasFixed() == 1 || prop.hasNillable() == 1) {
            return;
        }
        this._flags &= 0xFFFFFFF8;
        this._flags |= (((prop.hasDefault() == 0) ? 0 : 2) | ((prop.hasFixed() == 0) ? 0 : 4) | ((prop.hasNillable() != 0) ? 1 : 0) | 0x8000);
    }
    
    protected void initComplexType(final boolean complexType, final boolean complexContent) {
        this._flags |= ((complexType ? 8192 : 0) | (complexContent ? 16384 : 0));
    }
    
    protected boolean _isComplexType() {
        return (this._flags & 0x2000) != 0x0;
    }
    
    protected boolean _isComplexContent() {
        return (this._flags & 0x4000) != 0x0;
    }
    
    public void setValidateOnSet() {
        this._flags |= 0x10000;
    }
    
    protected boolean _validateOnSet() {
        return (this._flags & 0x10000) != 0x0;
    }
    
    @Override
    public final boolean isNil() {
        synchronized (this.monitor()) {
            this.check_dated();
            return (this._flags & 0x40) != 0x0;
        }
    }
    
    public final boolean isFixed() {
        this.check_element_dated();
        return (this._flags & 0x4) != 0x0;
    }
    
    public final boolean isNillable() {
        this.check_element_dated();
        return (this._flags & 0x1) != 0x0;
    }
    
    public final boolean isDefaultable() {
        this.check_element_dated();
        return (this._flags & 0x2) != 0x0;
    }
    
    public final boolean isDefault() {
        this.check_dated();
        return (this._flags & 0x100) != 0x0;
    }
    
    @Override
    public final void setNil() {
        synchronized (this.monitor()) {
            this.set_prepare();
            if ((this._flags & 0x1) == 0x0 && (this._flags & 0x10000) != 0x0) {
                throw new XmlValueNotNillableException();
            }
            this.set_nil();
            this._flags |= 0x40;
            if ((this._flags & 0x10) != 0x0) {
                this.get_store().invalidate_text();
                this._flags &= 0xFFFFFD5F;
                this.get_store().invalidate_nil();
            }
            else {
                this._textsource = null;
            }
        }
    }
    
    protected int elementFlags() {
        this.check_element_dated();
        return this._flags & 0x7;
    }
    
    public void setImmutable() {
        if ((this._flags & 0x1010) != 0x0) {
            throw new IllegalStateException();
        }
        this._flags |= 0x1000;
    }
    
    @Override
    public boolean isImmutable() {
        return (this._flags & 0x1000) != 0x0;
    }
    
    @Override
    public final void attach_store(final TypeStore store) {
        this._textsource = store;
        if ((this._flags & 0x1000) != 0x0) {
            throw new IllegalStateException();
        }
        this._flags |= 0x2B0;
        if (store.is_attribute()) {
            this._flags |= 0x8;
        }
        if (store.validate_on_set()) {
            this._flags |= 0x10000;
        }
    }
    
    @Override
    public final void invalidate_value() {
        assert (this._flags & 0x10) != 0x0;
        this._flags |= 0x20;
    }
    
    @Override
    public final boolean uses_invalidate_value() {
        final SchemaType type = this.schemaType();
        return type.isSimpleType() || type.getContentType() == 2;
    }
    
    @Override
    public final void invalidate_nilvalue() {
        assert (this._flags & 0x10) != 0x0;
        this._flags |= 0xA0;
    }
    
    @Override
    public final void invalidate_element_order() {
        assert (this._flags & 0x10) != 0x0;
        this._flags |= 0x2A0;
    }
    
    @Override
    public final TypeStore get_store() {
        assert (this._flags & 0x10) != 0x0;
        return (TypeStore)this._textsource;
    }
    
    public final XmlLocale getXmlLocale() {
        return this.get_store().get_locale();
    }
    
    protected final boolean has_store() {
        return (this._flags & 0x10) != 0x0;
    }
    
    @Override
    public final String build_text(final NamespaceManager nsm) {
        assert (this._flags & 0x10) != 0x0;
        assert (this._flags & 0x20) == 0x0;
        if ((this._flags & 0x140) != 0x0) {
            return "";
        }
        return this.compute_text((nsm == null) ? (this.has_store() ? this.get_store() : null) : nsm);
    }
    
    @Override
    public boolean build_nil() {
        assert (this._flags & 0x10) != 0x0;
        assert (this._flags & 0x20) == 0x0;
        return (this._flags & 0x40) != 0x0;
    }
    
    @Override
    public void validate_now() {
        this.check_dated();
    }
    
    @Override
    public void disconnect_store() {
        assert (this._flags & 0x10) != 0x0;
        this._flags |= 0xAA0;
    }
    
    @Override
    public TypeStoreUser create_element_user(final QName eltName, final QName xsiType) {
        return (TypeStoreUser)((SchemaTypeImpl)this.schemaType()).createElementType(eltName, xsiType, this.get_store().get_schematypeloader());
    }
    
    @Override
    public TypeStoreUser create_attribute_user(final QName attrName) {
        return (TypeStoreUser)((SchemaTypeImpl)this.schemaType()).createAttributeType(attrName, this.get_store().get_schematypeloader());
    }
    
    @Override
    public SchemaType get_schema_type() {
        return this.schemaType();
    }
    
    @Override
    public SchemaType get_element_type(final QName eltName, final QName xsiType) {
        return this.schemaType().getElementType(eltName, xsiType, this.get_store().get_schematypeloader());
    }
    
    @Override
    public SchemaType get_attribute_type(final QName attrName) {
        return this.schemaType().getAttributeType(attrName, this.get_store().get_schematypeloader());
    }
    
    @Override
    public String get_default_element_text(final QName eltName) {
        assert this._isComplexContent();
        if (!this._isComplexContent()) {
            throw new IllegalStateException();
        }
        final SchemaProperty prop = this.schemaType().getElementProperty(eltName);
        if (prop == null) {
            return "";
        }
        return prop.getDefaultText();
    }
    
    @Override
    public String get_default_attribute_text(final QName attrName) {
        assert this._isComplexType();
        if (!this._isComplexType()) {
            throw new IllegalStateException();
        }
        final SchemaProperty prop = this.schemaType().getAttributeProperty(attrName);
        if (prop == null) {
            return "";
        }
        return prop.getDefaultText();
    }
    
    @Override
    public int get_elementflags(final QName eltName) {
        if (!this._isComplexContent()) {
            return 0;
        }
        final SchemaProperty prop = this.schemaType().getElementProperty(eltName);
        if (prop == null) {
            return 0;
        }
        if (prop.hasDefault() == 1 || prop.hasFixed() == 1 || prop.hasNillable() == 1) {
            return -1;
        }
        return ((prop.hasDefault() == 0) ? 0 : 2) | ((prop.hasFixed() == 0) ? 0 : 4) | ((prop.hasNillable() != 0) ? 1 : 0);
    }
    
    @Override
    public int get_attributeflags(final QName attrName) {
        if (!this._isComplexType()) {
            return 0;
        }
        final SchemaProperty prop = this.schemaType().getAttributeProperty(attrName);
        if (prop == null) {
            return 0;
        }
        return ((prop.hasDefault() == 0) ? 0 : 2) | ((prop.hasFixed() == 0) ? 0 : 4);
    }
    
    @Override
    public boolean is_child_element_order_sensitive() {
        return this._isComplexType() && this.schemaType().isOrderSensitive();
    }
    
    @Override
    public final QNameSet get_element_ending_delimiters(final QName eltname) {
        final SchemaProperty prop = this.schemaType().getElementProperty(eltname);
        if (prop == null) {
            return null;
        }
        return prop.getJavaSetterDelimiter();
    }
    
    @Override
    public TypeStoreVisitor new_visitor() {
        if (!this._isComplexContent()) {
            return null;
        }
        return new SchemaTypeVisitorImpl(this.schemaType().getContentModel());
    }
    
    @Override
    public SchemaField get_attribute_field(final QName attrName) {
        final SchemaAttributeModel model = this.schemaType().getAttributeModel();
        if (model == null) {
            return null;
        }
        return model.getAttribute(attrName);
    }
    
    protected void set_String(final String v) {
        if ((this._flags & 0x1000) != 0x0) {
            throw new IllegalStateException();
        }
        final boolean wasNilled = (this._flags & 0x40) != 0x0;
        final String wscanon = this.apply_wscanon(v);
        this.update_from_wscanon_text(wscanon);
        if ((this._flags & 0x10) != 0x0) {
            this._flags &= 0xFFFFFFDF;
            if ((this._flags & 0x400) == 0x0) {
                this.get_store().store_text(v);
            }
            if (wasNilled) {
                this.get_store().invalidate_nil();
            }
        }
        else {
            this._textsource = v;
        }
    }
    
    protected void update_from_complex_content() {
        throw new XmlValueNotSupportedException("Complex content");
    }
    
    private final void update_from_wscanon_text(final String v) {
        if ((this._flags & 0x2) == 0x0 || (this._flags & 0x400) != 0x0 || (this._flags & 0x8) != 0x0 || !v.equals("")) {
            this.set_text(v);
            this._flags &= 0xFFFFFEBF;
            return;
        }
        final String def = this.get_store().compute_default_text();
        if (def == null) {
            throw new XmlValueOutOfRangeException();
        }
        this._flags |= 0x400;
        try {
            this.setStringValue(def);
        }
        finally {
            this._flags &= 0xFFFFFBFF;
        }
        this._flags &= 0xFFFFFFBF;
        this._flags |= 0x100;
    }
    
    protected boolean is_defaultable_ws(final String v) {
        return true;
    }
    
    protected int get_wscanon_rule() {
        return 3;
    }
    
    private final String apply_wscanon(final String v) {
        return XmlWhitespace.collapse(v, this.get_wscanon_rule());
    }
    
    private final void check_element_dated() {
        if ((this._flags & 0x200) != 0x0 && (this._flags & 0x8000) == 0x0) {
            if ((this._flags & 0x800) != 0x0) {
                throw new XmlValueDisconnectedException();
            }
            final int eltflags = this.get_store().compute_flags();
            this._flags &= 0xFFFFFDF8;
            this._flags |= eltflags;
        }
        if ((this._flags & 0x8000) != 0x0) {
            this._flags &= 0xFFFFFDFF;
        }
    }
    
    protected final boolean is_orphaned() {
        return (this._flags & 0x800) != 0x0;
    }
    
    protected final void check_orphaned() {
        if (this.is_orphaned()) {
            throw new XmlValueDisconnectedException();
        }
    }
    
    public final void check_dated() {
        if ((this._flags & 0x2A0) != 0x0) {
            if ((this._flags & 0x800) != 0x0) {
                throw new XmlValueDisconnectedException();
            }
            assert (this._flags & 0x10) != 0x0;
            this.check_element_dated();
            if ((this._flags & 0x200) != 0x0) {
                final int eltflags = this.get_store().compute_flags();
                this._flags &= 0xFFFFFDF8;
                this._flags |= eltflags;
            }
            boolean nilled = false;
            if ((this._flags & 0x80) != 0x0) {
                if (this.get_store().find_nil()) {
                    if ((this._flags & 0x1) == 0x0 && (this._flags & 0x10000) != 0x0) {
                        throw new XmlValueOutOfRangeException();
                    }
                    this.set_nil();
                    this._flags |= 0x40;
                    nilled = true;
                }
                else {
                    this._flags &= 0xFFFFFFBF;
                }
                this._flags &= 0xFFFFFF7F;
            }
            if (!nilled) {
                final String text;
                if ((this._flags & 0x4000) != 0x0 || (text = this.get_wscanon_text()) == null) {
                    this.update_from_complex_content();
                }
                else {
                    NamespaceContext.push(new NamespaceContext(this.get_store()));
                    try {
                        this.update_from_wscanon_text(text);
                    }
                    finally {
                        NamespaceContext.pop();
                    }
                }
            }
            this._flags &= 0xFFFFFFDF;
        }
    }
    
    private final void set_prepare() {
        this.check_element_dated();
        if ((this._flags & 0x1000) != 0x0) {
            throw new IllegalStateException();
        }
    }
    
    private final void set_commit() {
        final boolean wasNilled = (this._flags & 0x40) != 0x0;
        this._flags &= 0xFFFFFEBF;
        if ((this._flags & 0x10) != 0x0) {
            this._flags &= 0xFFFFFD5F;
            this.get_store().invalidate_text();
            if (wasNilled) {
                this.get_store().invalidate_nil();
            }
        }
        else {
            this._textsource = null;
        }
    }
    
    public final String get_wscanon_text() {
        if ((this._flags & 0x10) == 0x0) {
            return this.apply_wscanon((String)this._textsource);
        }
        return this.get_store().fetch_text(this.get_wscanon_rule());
    }
    
    protected abstract void set_text(final String p0);
    
    protected abstract void set_nil();
    
    protected abstract String compute_text(final NamespaceManager p0);
    
    @Override
    public float getFloatValue() {
        final BigDecimal bd = this.getBigDecimalValue();
        return (bd == null) ? 0.0f : bd.floatValue();
    }
    
    @Override
    public double getDoubleValue() {
        final BigDecimal bd = this.getBigDecimalValue();
        return (bd == null) ? 0.0 : bd.doubleValue();
    }
    
    @Override
    public BigDecimal getBigDecimalValue() {
        throw new XmlValueNotSupportedException("exception.value.not.supported.s2j", new Object[] { this.getPrimitiveTypeName(), "numeric" });
    }
    
    @Override
    public BigInteger getBigIntegerValue() {
        final BigDecimal bd = this.bigDecimalValue();
        return (bd == null) ? null : bd.toBigInteger();
    }
    
    @Override
    public byte getByteValue() {
        final long l = this.getIntValue();
        if (l > 127L) {
            throw new XmlValueOutOfRangeException();
        }
        if (l < -128L) {
            throw new XmlValueOutOfRangeException();
        }
        return (byte)l;
    }
    
    @Override
    public short getShortValue() {
        final long l = this.getIntValue();
        if (l > 32767L) {
            throw new XmlValueOutOfRangeException();
        }
        if (l < -32768L) {
            throw new XmlValueOutOfRangeException();
        }
        return (short)l;
    }
    
    @Override
    public int getIntValue() {
        final long l = this.getLongValue();
        if (l > 2147483647L) {
            throw new XmlValueOutOfRangeException();
        }
        if (l < -2147483648L) {
            throw new XmlValueOutOfRangeException();
        }
        return (int)l;
    }
    
    @Override
    public long getLongValue() {
        final BigInteger b = this.getBigIntegerValue();
        if (b == null) {
            return 0L;
        }
        if (b.compareTo(XmlObjectBase._max) >= 0) {
            throw new XmlValueOutOfRangeException();
        }
        if (b.compareTo(XmlObjectBase._min) <= 0) {
            throw new XmlValueOutOfRangeException();
        }
        return b.longValue();
    }
    
    static final XmlOptions buildInnerPrettyOptions() {
        final XmlOptions options = new XmlOptions();
        options.put("SAVE_INNER");
        options.put("SAVE_PRETTY_PRINT");
        options.put("SAVE_AGGRESSIVE_NAMESPACES");
        options.put("SAVE_USE_DEFAULT_NAMESPACE");
        return options;
    }
    
    @Override
    public final String toString() {
        synchronized (this.monitor()) {
            return this.ensureStore().xmlText(XmlObjectBase._toStringOptions);
        }
    }
    
    @Override
    public String xmlText() {
        return this.xmlText(null);
    }
    
    @Override
    public String xmlText(final XmlOptions options) {
        final XmlCursor cur = this.newCursorForce();
        try {
            return cur.xmlText(makeInnerOptions(options));
        }
        finally {
            cur.dispose();
        }
    }
    
    @Override
    public StringEnumAbstractBase getEnumValue() {
        throw new XmlValueNotSupportedException("exception.value.not.supported.s2j", new Object[] { this.getPrimitiveTypeName(), "enum" });
    }
    
    @Override
    public String getStringValue() {
        if (this.isImmutable()) {
            if ((this._flags & 0x40) != 0x0) {
                return null;
            }
            return this.compute_text(null);
        }
        else {
            synchronized (this.monitor()) {
                if (this._isComplexContent()) {
                    return this.get_store().fetch_text(1);
                }
                this.check_dated();
                if ((this._flags & 0x40) != 0x0) {
                    return null;
                }
                return this.compute_text(this.has_store() ? this.get_store() : null);
            }
        }
    }
    
    @Override
    @Deprecated
    public String stringValue() {
        return this.getStringValue();
    }
    
    @Override
    @Deprecated
    public boolean booleanValue() {
        return this.getBooleanValue();
    }
    
    @Override
    @Deprecated
    public byte byteValue() {
        return this.getByteValue();
    }
    
    @Override
    @Deprecated
    public short shortValue() {
        return this.getShortValue();
    }
    
    @Override
    @Deprecated
    public int intValue() {
        return this.getIntValue();
    }
    
    @Override
    @Deprecated
    public long longValue() {
        return this.getLongValue();
    }
    
    @Override
    @Deprecated
    public BigInteger bigIntegerValue() {
        return this.getBigIntegerValue();
    }
    
    @Override
    @Deprecated
    public BigDecimal bigDecimalValue() {
        return this.getBigDecimalValue();
    }
    
    @Override
    @Deprecated
    public float floatValue() {
        return this.getFloatValue();
    }
    
    @Override
    @Deprecated
    public double doubleValue() {
        return this.getDoubleValue();
    }
    
    @Override
    @Deprecated
    public byte[] byteArrayValue() {
        return this.getByteArrayValue();
    }
    
    @Override
    @Deprecated
    public StringEnumAbstractBase enumValue() {
        return this.getEnumValue();
    }
    
    @Override
    @Deprecated
    public Calendar calendarValue() {
        return this.getCalendarValue();
    }
    
    @Override
    @Deprecated
    public Date dateValue() {
        return this.getDateValue();
    }
    
    @Override
    @Deprecated
    public GDate gDateValue() {
        return this.getGDateValue();
    }
    
    @Override
    @Deprecated
    public GDuration gDurationValue() {
        return this.getGDurationValue();
    }
    
    @Override
    @Deprecated
    public QName qNameValue() {
        return this.getQNameValue();
    }
    
    @Override
    @Deprecated
    public List xlistValue() {
        return this.xgetListValue();
    }
    
    @Override
    @Deprecated
    public List listValue() {
        return this.getListValue();
    }
    
    @Override
    @Deprecated
    public Object objectValue() {
        return this.getObjectValue();
    }
    
    @Override
    @Deprecated
    public void set(final String obj) {
        this.setStringValue(obj);
    }
    
    @Override
    @Deprecated
    public void set(final boolean v) {
        this.setBooleanValue(v);
    }
    
    @Override
    @Deprecated
    public void set(final byte v) {
        this.setByteValue(v);
    }
    
    @Override
    @Deprecated
    public void set(final short v) {
        this.setShortValue(v);
    }
    
    @Override
    @Deprecated
    public void set(final int v) {
        this.setIntValue(v);
    }
    
    @Override
    @Deprecated
    public void set(final long v) {
        this.setLongValue(v);
    }
    
    @Override
    @Deprecated
    public void set(final BigInteger obj) {
        this.setBigIntegerValue(obj);
    }
    
    @Override
    @Deprecated
    public void set(final BigDecimal obj) {
        this.setBigDecimalValue(obj);
    }
    
    @Override
    @Deprecated
    public void set(final float v) {
        this.setFloatValue(v);
    }
    
    @Override
    @Deprecated
    public void set(final double v) {
        this.setDoubleValue(v);
    }
    
    @Override
    @Deprecated
    public void set(final byte[] obj) {
        this.setByteArrayValue(obj);
    }
    
    @Override
    @Deprecated
    public void set(final StringEnumAbstractBase obj) {
        this.setEnumValue(obj);
    }
    
    @Override
    @Deprecated
    public void set(final Calendar obj) {
        this.setCalendarValue(obj);
    }
    
    @Override
    @Deprecated
    public void set(final Date obj) {
        this.setDateValue(obj);
    }
    
    @Override
    @Deprecated
    public void set(final GDateSpecification obj) {
        this.setGDateValue(obj);
    }
    
    @Override
    @Deprecated
    public void set(final GDurationSpecification obj) {
        this.setGDurationValue(obj);
    }
    
    @Override
    @Deprecated
    public void set(final QName obj) {
        this.setQNameValue(obj);
    }
    
    @Override
    @Deprecated
    public void set(final List obj) {
        this.setListValue(obj);
    }
    
    @Override
    @Deprecated
    public void objectSet(final Object obj) {
        this.setObjectValue(obj);
    }
    
    @Override
    public byte[] getByteArrayValue() {
        throw new XmlValueNotSupportedException("exception.value.not.supported.s2j", new Object[] { this.getPrimitiveTypeName(), "byte[]" });
    }
    
    @Override
    public boolean getBooleanValue() {
        throw new XmlValueNotSupportedException("exception.value.not.supported.s2j", new Object[] { this.getPrimitiveTypeName(), "boolean" });
    }
    
    @Override
    public GDate getGDateValue() {
        throw new XmlValueNotSupportedException("exception.value.not.supported.s2j", new Object[] { this.getPrimitiveTypeName(), "Date" });
    }
    
    @Override
    public Date getDateValue() {
        throw new XmlValueNotSupportedException("exception.value.not.supported.s2j", new Object[] { this.getPrimitiveTypeName(), "Date" });
    }
    
    @Override
    public Calendar getCalendarValue() {
        throw new XmlValueNotSupportedException("exception.value.not.supported.s2j", new Object[] { this.getPrimitiveTypeName(), "Calendar" });
    }
    
    @Override
    public GDuration getGDurationValue() {
        throw new XmlValueNotSupportedException("exception.value.not.supported.s2j", new Object[] { this.getPrimitiveTypeName(), "Duration" });
    }
    
    @Override
    public QName getQNameValue() {
        throw new XmlValueNotSupportedException("exception.value.not.supported.s2j", new Object[] { this.getPrimitiveTypeName(), "QName" });
    }
    
    @Override
    public List getListValue() {
        throw new XmlValueNotSupportedException("exception.value.not.supported.s2j", new Object[] { this.getPrimitiveTypeName(), "List" });
    }
    
    @Override
    public List xgetListValue() {
        throw new XmlValueNotSupportedException("exception.value.not.supported.s2j", new Object[] { this.getPrimitiveTypeName(), "List" });
    }
    
    @Override
    public Object getObjectValue() {
        return java_value(this);
    }
    
    @Override
    public final void setBooleanValue(final boolean v) {
        synchronized (this.monitor()) {
            this.set_prepare();
            this.set_boolean(v);
            this.set_commit();
        }
    }
    
    @Override
    public final void setByteValue(final byte v) {
        synchronized (this.monitor()) {
            this.set_prepare();
            this.set_byte(v);
            this.set_commit();
        }
    }
    
    @Override
    public final void setShortValue(final short v) {
        synchronized (this.monitor()) {
            this.set_prepare();
            this.set_short(v);
            this.set_commit();
        }
    }
    
    @Override
    public final void setIntValue(final int v) {
        synchronized (this.monitor()) {
            this.set_prepare();
            this.set_int(v);
            this.set_commit();
        }
    }
    
    @Override
    public final void setLongValue(final long v) {
        synchronized (this.monitor()) {
            this.set_prepare();
            this.set_long(v);
            this.set_commit();
        }
    }
    
    @Override
    public final void setFloatValue(final float v) {
        synchronized (this.monitor()) {
            this.set_prepare();
            this.set_float(v);
            this.set_commit();
        }
    }
    
    @Override
    public final void setDoubleValue(final double v) {
        synchronized (this.monitor()) {
            this.set_prepare();
            this.set_double(v);
            this.set_commit();
        }
    }
    
    @Override
    public final void setByteArrayValue(final byte[] obj) {
        if (obj == null) {
            this.setNil();
        }
        else {
            synchronized (this.monitor()) {
                this.set_prepare();
                this.set_ByteArray(obj);
                this.set_commit();
            }
        }
    }
    
    @Override
    public final void setEnumValue(final StringEnumAbstractBase obj) {
        if (obj == null) {
            this.setNil();
        }
        else {
            synchronized (this.monitor()) {
                this.set_prepare();
                this.set_enum(obj);
                this.set_commit();
            }
        }
    }
    
    @Override
    public final void setBigIntegerValue(final BigInteger obj) {
        if (obj == null) {
            this.setNil();
        }
        else {
            synchronized (this.monitor()) {
                this.set_prepare();
                this.set_BigInteger(obj);
                this.set_commit();
            }
        }
    }
    
    @Override
    public final void setBigDecimalValue(final BigDecimal obj) {
        if (obj == null) {
            this.setNil();
        }
        else {
            synchronized (this.monitor()) {
                this.set_prepare();
                this.set_BigDecimal(obj);
                this.set_commit();
            }
        }
    }
    
    @Override
    public final void setCalendarValue(final Calendar obj) {
        if (obj == null) {
            this.setNil();
        }
        else {
            synchronized (this.monitor()) {
                this.set_prepare();
                this.set_Calendar(obj);
                this.set_commit();
            }
        }
    }
    
    @Override
    public final void setDateValue(final Date obj) {
        if (obj == null) {
            this.setNil();
        }
        else {
            synchronized (this.monitor()) {
                this.set_prepare();
                this.set_Date(obj);
                this.set_commit();
            }
        }
    }
    
    @Override
    public final void setGDateValue(final GDate obj) {
        if (obj == null) {
            this.setNil();
        }
        else {
            synchronized (this.monitor()) {
                this.set_prepare();
                this.set_GDate(obj);
                this.set_commit();
            }
        }
    }
    
    public final void setGDateValue(final GDateSpecification obj) {
        if (obj == null) {
            this.setNil();
        }
        else {
            synchronized (this.monitor()) {
                this.set_prepare();
                this.set_GDate(obj);
                this.set_commit();
            }
        }
    }
    
    @Override
    public final void setGDurationValue(final GDuration obj) {
        if (obj == null) {
            this.setNil();
        }
        else {
            synchronized (this.monitor()) {
                this.set_prepare();
                this.set_GDuration(obj);
                this.set_commit();
            }
        }
    }
    
    public final void setGDurationValue(final GDurationSpecification obj) {
        if (obj == null) {
            this.setNil();
        }
        else {
            synchronized (this.monitor()) {
                this.set_prepare();
                this.set_GDuration(obj);
                this.set_commit();
            }
        }
    }
    
    @Override
    public final void setQNameValue(final QName obj) {
        if (obj == null) {
            this.setNil();
        }
        else {
            synchronized (this.monitor()) {
                this.set_prepare();
                this.set_QName(obj);
                this.set_commit();
            }
        }
    }
    
    @Override
    public final void setListValue(final List obj) {
        if (obj == null) {
            this.setNil();
        }
        else {
            synchronized (this.monitor()) {
                this.set_prepare();
                this.set_list(obj);
                this.set_commit();
            }
        }
    }
    
    @Override
    public final void setStringValue(final String obj) {
        if (obj == null) {
            this.setNil();
        }
        else {
            synchronized (this.monitor()) {
                this.set_prepare();
                this.set_String(obj);
            }
        }
    }
    
    @Override
    public void setObjectValue(final Object o) {
        if (o == null) {
            this.setNil();
            return;
        }
        if (o instanceof XmlObject) {
            this.set((XmlObject)o);
        }
        else if (o instanceof String) {
            this.setStringValue((String)o);
        }
        else if (o instanceof StringEnumAbstractBase) {
            this.setEnumValue((StringEnumAbstractBase)o);
        }
        else if (o instanceof BigInteger) {
            this.setBigIntegerValue((BigInteger)o);
        }
        else if (o instanceof BigDecimal) {
            this.setBigDecimalValue((BigDecimal)o);
        }
        else if (o instanceof Byte) {
            this.setByteValue((byte)o);
        }
        else if (o instanceof Short) {
            this.setShortValue((short)o);
        }
        else if (o instanceof Integer) {
            this.setIntValue((int)o);
        }
        else if (o instanceof Long) {
            this.setLongValue((long)o);
        }
        else if (o instanceof Boolean) {
            this.setBooleanValue((boolean)o);
        }
        else if (o instanceof Float) {
            this.setFloatValue((float)o);
        }
        else if (o instanceof Double) {
            this.setDoubleValue((double)o);
        }
        else if (o instanceof Calendar) {
            this.setCalendarValue((Calendar)o);
        }
        else if (o instanceof Date) {
            this.setDateValue((Date)o);
        }
        else if (o instanceof GDateSpecification) {
            this.setGDateValue((GDateSpecification)o);
        }
        else if (o instanceof GDurationSpecification) {
            this.setGDurationValue((GDurationSpecification)o);
        }
        else if (o instanceof QName) {
            this.setQNameValue((QName)o);
        }
        else if (o instanceof List) {
            this.setListValue((List)o);
        }
        else {
            if (!(o instanceof byte[])) {
                throw new XmlValueNotSupportedException("Can't set union object of class : " + o.getClass().getName());
            }
            this.setByteArrayValue((byte[])o);
        }
    }
    
    public final void set_newValue(final XmlObject obj) {
        if (obj == null || obj.isNil()) {
            this.setNil();
            return;
        }
        if (obj instanceof XmlAnySimpleType) {
            final XmlAnySimpleType v = (XmlAnySimpleType)obj;
            final SchemaType instanceType = ((SimpleValue)v).instanceType();
            assert instanceType != null : "Nil case should have been handled already";
            if (instanceType.getSimpleVariety() == 3) {
                synchronized (this.monitor()) {
                    this.set_prepare();
                    this.set_list(((SimpleValue)v).xgetListValue());
                    this.set_commit();
                    return;
                }
            }
            synchronized (this.monitor()) {
                assert instanceType.getSimpleVariety() == 1;
                Label_0814: {
                    switch (instanceType.getPrimitiveType().getBuiltinTypeCode()) {
                        default: {
                            assert false : "encountered nonprimitive type.";
                            throw new IllegalStateException("Complex type unexpected");
                        }
                        case 3: {
                            final boolean bool = ((SimpleValue)v).getBooleanValue();
                            this.set_prepare();
                            this.set_boolean(bool);
                            break;
                        }
                        case 4: {
                            final byte[] byteArr = ((SimpleValue)v).getByteArrayValue();
                            this.set_prepare();
                            this.set_b64(byteArr);
                            break;
                        }
                        case 5: {
                            final byte[] byteArr = ((SimpleValue)v).getByteArrayValue();
                            this.set_prepare();
                            this.set_hex(byteArr);
                            break;
                        }
                        case 7: {
                            final QName name = ((SimpleValue)v).getQNameValue();
                            this.set_prepare();
                            this.set_QName(name);
                            break;
                        }
                        case 9: {
                            final float f = ((SimpleValue)v).getFloatValue();
                            this.set_prepare();
                            this.set_float(f);
                            break;
                        }
                        case 10: {
                            final double d = ((SimpleValue)v).getDoubleValue();
                            this.set_prepare();
                            this.set_double(d);
                            break;
                        }
                        case 11: {
                            switch (instanceType.getDecimalSize()) {
                                case 8: {
                                    final byte b = ((SimpleValue)v).getByteValue();
                                    this.set_prepare();
                                    this.set_byte(b);
                                    break Label_0814;
                                }
                                case 16: {
                                    final short s = ((SimpleValue)v).getShortValue();
                                    this.set_prepare();
                                    this.set_short(s);
                                    break Label_0814;
                                }
                                case 32: {
                                    final int i = ((SimpleValue)v).getIntValue();
                                    this.set_prepare();
                                    this.set_int(i);
                                    break Label_0814;
                                }
                                case 64: {
                                    final long l = ((SimpleValue)v).getLongValue();
                                    this.set_prepare();
                                    this.set_long(l);
                                    break Label_0814;
                                }
                                case 1000000: {
                                    final BigInteger bi = ((SimpleValue)v).getBigIntegerValue();
                                    this.set_prepare();
                                    this.set_BigInteger(bi);
                                    break Label_0814;
                                }
                                default: {
                                    assert false : "invalid numeric bit count";
                                }
                                case 1000001: {
                                    final BigDecimal bd = ((SimpleValue)v).getBigDecimalValue();
                                    this.set_prepare();
                                    this.set_BigDecimal(bd);
                                    break Label_0814;
                                }
                            }
                            break;
                        }
                        case 6: {
                            final String uri = v.getStringValue();
                            this.set_prepare();
                            this.set_text(uri);
                            break;
                        }
                        case 8: {
                            final String s2 = v.getStringValue();
                            this.set_prepare();
                            this.set_notation(s2);
                            break;
                        }
                        case 13: {
                            final GDuration gd = ((SimpleValue)v).getGDurationValue();
                            this.set_prepare();
                            this.set_GDuration(gd);
                            break;
                        }
                        case 14:
                        case 15:
                        case 16:
                        case 17:
                        case 18:
                        case 19:
                        case 20:
                        case 21: {
                            final GDate gd2 = ((SimpleValue)v).getGDateValue();
                            this.set_prepare();
                            this.set_GDate(gd2);
                            break;
                        }
                        case 12: {
                            final String s2 = v.getStringValue();
                            this.set_prepare();
                            this.set_String(s2);
                            break;
                        }
                        case 2: {
                            boolean pushed = false;
                            if (!v.isImmutable()) {
                                pushed = true;
                                NamespaceContext.push(new NamespaceContext(v));
                            }
                            try {
                                this.set_prepare();
                                this.set_xmlanysimple(v);
                            }
                            finally {
                                if (pushed) {
                                    NamespaceContext.pop();
                                }
                            }
                            break;
                        }
                    }
                }
                this.set_commit();
                return;
            }
        }
        throw new IllegalStateException("Complex type unexpected");
    }
    
    private TypeStoreUser setterHelper(final XmlObjectBase src) {
        this.check_orphaned();
        src.check_orphaned();
        return this.get_store().copy_contents_from(src.get_store()).get_store().change_type(src.schemaType());
    }
    
    @Override
    public final XmlObject set(final XmlObject src) {
        if (this.isImmutable()) {
            throw new IllegalStateException("Cannot set the value of an immutable XmlObject");
        }
        final XmlObjectBase obj = underlying(src);
        TypeStoreUser newObj = this;
        if (obj == null) {
            this.setNil();
            return this;
        }
        if (obj.isImmutable()) {
            this.setStringValue(obj.getStringValue());
        }
        else {
            final boolean noSyncThis = this.preCheck();
            final boolean noSyncObj = obj.preCheck();
            if (this.monitor() == obj.monitor()) {
                if (noSyncThis) {
                    newObj = this.setterHelper(obj);
                }
                else {
                    synchronized (this.monitor()) {
                        newObj = this.setterHelper(obj);
                    }
                }
            }
            else if (noSyncThis) {
                if (noSyncObj) {
                    newObj = this.setterHelper(obj);
                }
                else {
                    synchronized (obj.monitor()) {
                        newObj = this.setterHelper(obj);
                    }
                }
            }
            else if (noSyncObj) {
                synchronized (this.monitor()) {
                    newObj = this.setterHelper(obj);
                }
            }
            else {
                boolean acquired = false;
                try {
                    GlobalLock.acquire();
                    acquired = true;
                    synchronized (this.monitor()) {
                        synchronized (obj.monitor()) {
                            GlobalLock.release();
                            acquired = false;
                            newObj = this.setterHelper(obj);
                        }
                    }
                }
                catch (final InterruptedException e) {
                    throw new XmlRuntimeException(e);
                }
                finally {
                    if (acquired) {
                        GlobalLock.release();
                    }
                }
            }
        }
        return (XmlObject)newObj;
    }
    
    public final XmlObject generatedSetterHelperImpl(final XmlObject src, final QName propName, final int index, final short kindSetterHelper) {
        final XmlObjectBase srcObj = underlying(src);
        if (srcObj == null) {
            synchronized (this.monitor()) {
                final XmlObjectBase target = this.getTargetForSetter(propName, index, kindSetterHelper);
                target.setNil();
                return target;
            }
        }
        if (srcObj.isImmutable()) {
            synchronized (this.monitor()) {
                final XmlObjectBase target = this.getTargetForSetter(propName, index, kindSetterHelper);
                target.setStringValue(srcObj.getStringValue());
                return target;
            }
        }
        final boolean noSyncThis = this.preCheck();
        final boolean noSyncObj = srcObj.preCheck();
        if (this.monitor() == srcObj.monitor()) {
            if (noSyncThis) {
                return (XmlObject)this.objSetterHelper(srcObj, propName, index, kindSetterHelper);
            }
            synchronized (this.monitor()) {
                return (XmlObject)this.objSetterHelper(srcObj, propName, index, kindSetterHelper);
            }
        }
        if (noSyncThis) {
            if (noSyncObj) {
                return (XmlObject)this.objSetterHelper(srcObj, propName, index, kindSetterHelper);
            }
            synchronized (srcObj.monitor()) {
                return (XmlObject)this.objSetterHelper(srcObj, propName, index, kindSetterHelper);
            }
        }
        if (noSyncObj) {
            synchronized (this.monitor()) {
                return (XmlObject)this.objSetterHelper(srcObj, propName, index, kindSetterHelper);
            }
        }
        boolean acquired = false;
        try {
            GlobalLock.acquire();
            acquired = true;
            synchronized (this.monitor()) {
                synchronized (srcObj.monitor()) {
                    GlobalLock.release();
                    acquired = false;
                    return (XmlObject)this.objSetterHelper(srcObj, propName, index, kindSetterHelper);
                }
            }
        }
        catch (final InterruptedException e) {
            throw new XmlRuntimeException(e);
        }
        finally {
            if (acquired) {
                GlobalLock.release();
            }
        }
    }
    
    private TypeStoreUser objSetterHelper(final XmlObjectBase srcObj, final QName propName, final int index, final short kindSetterHelper) {
        final XmlObjectBase target = this.getTargetForSetter(propName, index, kindSetterHelper);
        target.check_orphaned();
        srcObj.check_orphaned();
        return target.get_store().copy_contents_from(srcObj.get_store()).get_store().change_type(srcObj.schemaType());
    }
    
    private XmlObjectBase getTargetForSetter(final QName propName, final int index, final short kindSetterHelper) {
        switch (kindSetterHelper) {
            case 1: {
                this.check_orphaned();
                XmlObjectBase target = null;
                target = (XmlObjectBase)this.get_store().find_element_user(propName, index);
                if (target == null) {
                    target = (XmlObjectBase)this.get_store().add_element_user(propName);
                }
                if (target.isImmutable()) {
                    throw new IllegalStateException("Cannot set the value of an immutable XmlObject");
                }
                return target;
            }
            case 2: {
                this.check_orphaned();
                XmlObjectBase target = null;
                target = (XmlObjectBase)this.get_store().find_element_user(propName, index);
                if (target == null) {
                    throw new IndexOutOfBoundsException();
                }
                if (target.isImmutable()) {
                    throw new IllegalStateException("Cannot set the value of an immutable XmlObject");
                }
                return target;
            }
            default: {
                throw new IllegalArgumentException("Unknown kindSetterHelper: " + kindSetterHelper);
            }
        }
    }
    
    public final XmlObject _set(final XmlObject src) {
        if (this.isImmutable()) {
            throw new IllegalStateException("Cannot set the value of an immutable XmlObject");
        }
        final XmlObjectBase obj = underlying(src);
        TypeStoreUser newObj = this;
        if (obj == null) {
            this.setNil();
            return this;
        }
        if (obj.isImmutable()) {
            this.set(obj.stringValue());
        }
        else {
            this.check_orphaned();
            obj.check_orphaned();
            newObj = this.get_store().copy_contents_from(obj.get_store()).get_store().change_type(obj.schemaType());
        }
        return (XmlObject)newObj;
    }
    
    protected void set_list(final List list) {
        throw new XmlValueNotSupportedException("exception.value.not.supported.j2s", new Object[] { "List", this.getPrimitiveTypeName() });
    }
    
    protected void set_boolean(final boolean v) {
        throw new XmlValueNotSupportedException("exception.value.not.supported.j2s", new Object[] { "boolean", this.getPrimitiveTypeName() });
    }
    
    protected void set_byte(final byte v) {
        this.set_int(v);
    }
    
    protected void set_short(final short v) {
        this.set_int(v);
    }
    
    protected void set_int(final int v) {
        this.set_long(v);
    }
    
    protected void set_long(final long v) {
        this.set_BigInteger(BigInteger.valueOf(v));
    }
    
    protected void set_char(final char v) {
        this.set_String(Character.toString(v));
    }
    
    protected void set_float(final float v) {
        this.set_BigDecimal(new BigDecimal(v));
    }
    
    protected void set_double(final double v) {
        this.set_BigDecimal(new BigDecimal(v));
    }
    
    protected void set_enum(final StringEnumAbstractBase e) {
        throw new XmlValueNotSupportedException("exception.value.not.supported.j2s", new Object[] { "enum", this.getPrimitiveTypeName() });
    }
    
    protected void set_ByteArray(final byte[] b) {
        throw new XmlValueNotSupportedException("exception.value.not.supported.j2s", new Object[] { "byte[]", this.getPrimitiveTypeName() });
    }
    
    protected void set_b64(final byte[] b) {
        this.set_ByteArray(b);
    }
    
    protected void set_hex(final byte[] b) {
        this.set_ByteArray(b);
    }
    
    protected void set_BigInteger(final BigInteger v) {
        this.set_BigDecimal(new BigDecimal(v));
    }
    
    protected void set_BigDecimal(final BigDecimal v) {
        throw new XmlValueNotSupportedException("exception.value.not.supported.j2s", new Object[] { "numeric", this.getPrimitiveTypeName() });
    }
    
    protected void set_Date(final Date v) {
        throw new XmlValueNotSupportedException("exception.value.not.supported.j2s", new Object[] { "Date", this.getPrimitiveTypeName() });
    }
    
    protected void set_Calendar(final Calendar v) {
        throw new XmlValueNotSupportedException("exception.value.not.supported.j2s", new Object[] { "Calendar", this.getPrimitiveTypeName() });
    }
    
    protected void set_GDate(final GDateSpecification v) {
        throw new XmlValueNotSupportedException("exception.value.not.supported.j2s", new Object[] { "Date", this.getPrimitiveTypeName() });
    }
    
    protected void set_GDuration(final GDurationSpecification v) {
        throw new XmlValueNotSupportedException("exception.value.not.supported.j2s", new Object[] { "Duration", this.getPrimitiveTypeName() });
    }
    
    protected void set_ComplexXml(final XmlObject v) {
        throw new XmlValueNotSupportedException("exception.value.not.supported.j2s", new Object[] { "complex content", this.getPrimitiveTypeName() });
    }
    
    protected void set_QName(final QName v) {
        throw new XmlValueNotSupportedException("exception.value.not.supported.j2s", new Object[] { "QName", this.getPrimitiveTypeName() });
    }
    
    protected void set_notation(final String v) {
        throw new XmlValueNotSupportedException();
    }
    
    protected void set_xmlanysimple(final XmlAnySimpleType v) {
        this.set_String(v.getStringValue());
    }
    
    private final String getPrimitiveTypeName() {
        final SchemaType type = this.schemaType();
        if (type.isNoType()) {
            return "unknown";
        }
        final SchemaType t = type.getPrimitiveType();
        if (t == null) {
            return "complex";
        }
        return t.getName().getLocalPart();
    }
    
    private final boolean comparable_value_spaces(final SchemaType t1, final SchemaType t2) {
        assert t1.getSimpleVariety() != 2 && t2.getSimpleVariety() != 2;
        if (!t1.isSimpleType() && !t2.isSimpleType()) {
            return t1.getContentType() == t2.getContentType();
        }
        return t1.isSimpleType() && t2.isSimpleType() && ((t1.getSimpleVariety() == 3 && t2.getSimpleVariety() == 3) || (t1.getSimpleVariety() != 3 && t2.getSimpleVariety() != 3 && t1.getPrimitiveType().equals(t2.getPrimitiveType())));
    }
    
    private final boolean valueEqualsImpl(final XmlObject xmlobj) {
        this.check_dated();
        final SchemaType typethis = this.instanceType();
        final SchemaType typeother = ((SimpleValue)xmlobj).instanceType();
        if (typethis == null && typeother == null) {
            return true;
        }
        if (typethis == null || typeother == null) {
            return false;
        }
        if (!this.comparable_value_spaces(typethis, typeother)) {
            return false;
        }
        if (xmlobj.schemaType().getSimpleVariety() == 2) {
            return underlying(xmlobj).equal_to(this);
        }
        return this.equal_to(xmlobj);
    }
    
    @Override
    public final boolean valueEquals(final XmlObject xmlobj) {
        boolean acquired = false;
        try {
            if (this.isImmutable()) {
                if (xmlobj.isImmutable()) {
                    return this.valueEqualsImpl(xmlobj);
                }
                synchronized (xmlobj.monitor()) {
                    return this.valueEqualsImpl(xmlobj);
                }
            }
            if (xmlobj.isImmutable() || this.monitor() == xmlobj.monitor()) {
                synchronized (this.monitor()) {
                    return this.valueEqualsImpl(xmlobj);
                }
            }
            GlobalLock.acquire();
            acquired = true;
            synchronized (this.monitor()) {
                synchronized (xmlobj.monitor()) {
                    GlobalLock.release();
                    acquired = false;
                    return this.valueEqualsImpl(xmlobj);
                }
            }
        }
        catch (final InterruptedException e) {
            throw new XmlRuntimeException(e);
        }
        finally {
            if (acquired) {
                GlobalLock.release();
            }
        }
    }
    
    @Override
    public final int compareTo(final Object obj) {
        final int result = this.compareValue((XmlObject)obj);
        if (result == 2) {
            throw new ClassCastException();
        }
        return result;
    }
    
    private final int compareValueImpl(final XmlObject xmlobj) {
        SchemaType type1;
        SchemaType type2;
        try {
            type1 = this.instanceType();
            type2 = ((SimpleValue)xmlobj).instanceType();
        }
        catch (final XmlValueOutOfRangeException e) {
            return 2;
        }
        if (type1 == null && type2 == null) {
            return 0;
        }
        if (type1 == null || type2 == null) {
            return 2;
        }
        if (!type1.isSimpleType() || type1.isURType()) {
            return 2;
        }
        if (!type2.isSimpleType() || type2.isURType()) {
            return 2;
        }
        type1 = type1.getPrimitiveType();
        type2 = type2.getPrimitiveType();
        if (type1.getBuiltinTypeCode() != type2.getBuiltinTypeCode()) {
            return 2;
        }
        return this.compare_to(xmlobj);
    }
    
    @Override
    public final int compareValue(final XmlObject xmlobj) {
        if (xmlobj == null) {
            return 2;
        }
        boolean acquired = false;
        try {
            if (this.isImmutable()) {
                if (xmlobj.isImmutable()) {
                    return this.compareValueImpl(xmlobj);
                }
                synchronized (xmlobj.monitor()) {
                    return this.compareValueImpl(xmlobj);
                }
            }
            if (xmlobj.isImmutable() || this.monitor() == xmlobj.monitor()) {
                synchronized (this.monitor()) {
                    return this.compareValueImpl(xmlobj);
                }
            }
            GlobalLock.acquire();
            acquired = true;
            synchronized (this.monitor()) {
                synchronized (xmlobj.monitor()) {
                    GlobalLock.release();
                    acquired = false;
                    return this.compareValueImpl(xmlobj);
                }
            }
        }
        catch (final InterruptedException e) {
            throw new XmlRuntimeException(e);
        }
        finally {
            if (acquired) {
                GlobalLock.release();
            }
        }
    }
    
    protected int compare_to(final XmlObject xmlobj) {
        if (this.equal_to(xmlobj)) {
            return 0;
        }
        return 2;
    }
    
    protected abstract boolean equal_to(final XmlObject p0);
    
    protected abstract int value_hash_code();
    
    @Override
    public int valueHashCode() {
        synchronized (this.monitor()) {
            return this.value_hash_code();
        }
    }
    
    public boolean isInstanceOf(final SchemaType type) {
        if (type.getSimpleVariety() != 2) {
            for (SchemaType myType = this.instanceType(); myType != null; myType = myType.getBaseType()) {
                if (type == myType) {
                    return true;
                }
            }
            return false;
        }
        final Set ctypes = new HashSet(Arrays.asList(type.getUnionConstituentTypes()));
        for (SchemaType myType = this.instanceType(); myType != null; myType = myType.getBaseType()) {
            if (ctypes.contains(myType)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public final boolean equals(final Object obj) {
        if (!this.isImmutable()) {
            return super.equals(obj);
        }
        if (!(obj instanceof XmlObject)) {
            return false;
        }
        final XmlObject xmlobj = (XmlObject)obj;
        return xmlobj.isImmutable() && this.valueEquals(xmlobj);
    }
    
    @Override
    public final int hashCode() {
        if (!this.isImmutable()) {
            return super.hashCode();
        }
        synchronized (this.monitor()) {
            if (this.isNil()) {
                return 0;
            }
            return this.value_hash_code();
        }
    }
    
    @Override
    public XmlObject[] selectChildren(final QName elementName) {
        final XmlCursor xc = this.newCursor();
        try {
            if (!xc.isContainer()) {
                return XmlObjectBase.EMPTY_RESULT;
            }
            final List result = new ArrayList();
            if (xc.toChild(elementName)) {
                do {
                    result.add(xc.getObject());
                } while (xc.toNextSibling(elementName));
            }
            if (result.size() == 0) {
                return XmlObjectBase.EMPTY_RESULT;
            }
            return result.toArray(XmlObjectBase.EMPTY_RESULT);
        }
        finally {
            xc.dispose();
        }
    }
    
    @Override
    public XmlObject[] selectChildren(final String elementUri, final String elementLocalName) {
        return this.selectChildren(new QName(elementUri, elementLocalName));
    }
    
    @Override
    public XmlObject[] selectChildren(final QNameSet elementNameSet) {
        if (elementNameSet == null) {
            throw new IllegalArgumentException();
        }
        final XmlCursor xc = this.newCursor();
        try {
            if (!xc.isContainer()) {
                return XmlObjectBase.EMPTY_RESULT;
            }
            final List result = new ArrayList();
            Label_0113: {
                if (xc.toFirstChild()) {
                    while (XmlObjectBase.$assertionsDisabled || xc.isContainer()) {
                        if (elementNameSet.contains(xc.getName())) {
                            result.add(xc.getObject());
                        }
                        if (!xc.toNextSibling()) {
                            break Label_0113;
                        }
                    }
                    throw new AssertionError();
                }
            }
            if (result.size() == 0) {
                return XmlObjectBase.EMPTY_RESULT;
            }
            return result.toArray(XmlObjectBase.EMPTY_RESULT);
        }
        finally {
            xc.dispose();
        }
    }
    
    @Override
    public XmlObject selectAttribute(final QName attributeName) {
        final XmlCursor xc = this.newCursor();
        try {
            if (!xc.isContainer()) {
                return null;
            }
            if (xc.toFirstAttribute()) {
                while (!xc.getName().equals(attributeName)) {
                    if (!xc.toNextAttribute()) {
                        return null;
                    }
                }
                return xc.getObject();
            }
            return null;
        }
        finally {
            xc.dispose();
        }
    }
    
    @Override
    public XmlObject selectAttribute(final String attributeUri, final String attributeLocalName) {
        return this.selectAttribute(new QName(attributeUri, attributeLocalName));
    }
    
    @Override
    public XmlObject[] selectAttributes(final QNameSet attributeNameSet) {
        if (attributeNameSet == null) {
            throw new IllegalArgumentException();
        }
        final XmlCursor xc = this.newCursor();
        try {
            if (!xc.isContainer()) {
                return XmlObjectBase.EMPTY_RESULT;
            }
            final List result = new ArrayList();
            if (xc.toFirstAttribute()) {
                do {
                    if (attributeNameSet.contains(xc.getName())) {
                        result.add(xc.getObject());
                    }
                } while (xc.toNextAttribute());
            }
            if (result.size() == 0) {
                return XmlObjectBase.EMPTY_RESULT;
            }
            return result.toArray(XmlObjectBase.EMPTY_RESULT);
        }
        finally {
            xc.dispose();
        }
    }
    
    public Object writeReplace() {
        synchronized (this.monitor()) {
            if (this.isRootXmlObject()) {
                return new SerializedRootObject((XmlObject)this);
            }
            return new SerializedInteriorObject((XmlObject)this, this.getRootXmlObject());
        }
    }
    
    private boolean isRootXmlObject() {
        final XmlCursor cur = this.newCursor();
        if (cur == null) {
            return false;
        }
        final boolean result = !cur.toParent();
        cur.dispose();
        return result;
    }
    
    private XmlObject getRootXmlObject() {
        final XmlCursor cur = this.newCursor();
        if (cur == null) {
            return this;
        }
        cur.toStartDoc();
        final XmlObject result = cur.getObject();
        cur.dispose();
        return result;
    }
    
    protected static Object java_value(final XmlObject obj) {
        if (obj.isNil()) {
            return null;
        }
        if (!(obj instanceof XmlAnySimpleType)) {
            return obj;
        }
        final SchemaType instanceType = ((SimpleValue)obj).instanceType();
        assert instanceType != null : "Nil case should have been handled above";
        if (instanceType.getSimpleVariety() == 3) {
            return ((SimpleValue)obj).getListValue();
        }
        final SimpleValue base = (SimpleValue)obj;
        switch (instanceType.getPrimitiveType().getBuiltinTypeCode()) {
            case 3: {
                return base.getBooleanValue() ? Boolean.TRUE : Boolean.FALSE;
            }
            case 4:
            case 5: {
                return base.getByteArrayValue();
            }
            case 7: {
                return base.getQNameValue();
            }
            case 9: {
                return new Float(base.getFloatValue());
            }
            case 10: {
                return new Double(base.getDoubleValue());
            }
            case 11: {
                switch (instanceType.getDecimalSize()) {
                    case 8: {
                        return new Byte(base.getByteValue());
                    }
                    case 16: {
                        return new Short(base.getShortValue());
                    }
                    case 32: {
                        return new Integer(base.getIntValue());
                    }
                    case 64: {
                        return new Long(base.getLongValue());
                    }
                    case 1000000: {
                        return base.getBigIntegerValue();
                    }
                    default: {
                        assert false : "invalid numeric bit count";
                        return base.getBigDecimalValue();
                    }
                    case 1000001: {
                        return base.getBigDecimalValue();
                    }
                }
                break;
            }
            case 6: {
                return base.getStringValue();
            }
            case 13: {
                return base.getGDurationValue();
            }
            case 14:
            case 15:
            case 16:
            case 17:
            case 18:
            case 19:
            case 20:
            case 21: {
                return base.getCalendarValue();
            }
            default: {
                assert false : "encountered nonprimitive type.";
                return base.getStringValue();
            }
            case 2:
            case 8:
            case 12: {
                return base.getStringValue();
            }
        }
    }
    
    protected XmlAnySimpleType get_default_attribute_value(final QName name) {
        final SchemaType sType = this.schemaType();
        final SchemaAttributeModel aModel = sType.getAttributeModel();
        if (aModel == null) {
            return null;
        }
        final SchemaLocalAttribute sAttr = aModel.getAttribute(name);
        if (sAttr == null) {
            return null;
        }
        return sAttr.getDefaultValue();
    }
    
    static {
        _voorVc = new ValueOutOfRangeValidationContext();
        _max = BigInteger.valueOf(Long.MAX_VALUE);
        _min = BigInteger.valueOf(Long.MIN_VALUE);
        _toStringOptions = buildInnerPrettyOptions();
        EMPTY_RESULT = new XmlObject[0];
    }
    
    private static final class ValueOutOfRangeValidationContext implements ValidationContext
    {
        @Override
        public void invalid(final String message) {
            throw new XmlValueOutOfRangeException(message);
        }
        
        @Override
        public void invalid(final String code, final Object[] args) {
            throw new XmlValueOutOfRangeException(code, args);
        }
    }
    
    private static final class ImmutableValueValidationContext implements ValidationContext
    {
        private XmlObject _loc;
        private Collection _coll;
        
        ImmutableValueValidationContext(final Collection coll, final XmlObject loc) {
            this._coll = coll;
            this._loc = loc;
        }
        
        @Override
        public void invalid(final String message) {
            this._coll.add(XmlError.forObject(message, this._loc));
        }
        
        @Override
        public void invalid(final String code, final Object[] args) {
            this._coll.add(XmlError.forObject(code, args, this._loc));
        }
    }
    
    private static class SerializedRootObject implements Serializable
    {
        private static final long serialVersionUID = 1L;
        transient Class _xbeanClass;
        transient XmlObject _impl;
        
        private SerializedRootObject() {
        }
        
        private SerializedRootObject(final XmlObject impl) {
            this._xbeanClass = impl.schemaType().getJavaClass();
            this._impl = impl;
        }
        
        private void writeObject(final ObjectOutputStream out) throws IOException {
            out.writeObject(this._xbeanClass);
            out.writeShort(0);
            out.writeShort(1);
            out.writeShort(1);
            final String xmlText = this._impl.xmlText();
            out.writeObject(xmlText);
            out.writeBoolean(false);
        }
        
        private void readObject(final ObjectInputStream in) throws IOException {
            try {
                this._xbeanClass = (Class)in.readObject();
                final int utfBytes = in.readUnsignedShort();
                int majorVersionNum = 0;
                int minorVersionNum = 0;
                if (utfBytes == 0) {
                    majorVersionNum = in.readUnsignedShort();
                    minorVersionNum = in.readUnsignedShort();
                }
                String xmlText = null;
                Label_0205: {
                    switch (majorVersionNum) {
                        case 0: {
                            xmlText = this.readObjectV0(in, utfBytes);
                            in.readBoolean();
                            break;
                        }
                        case 1: {
                            switch (minorVersionNum) {
                                case 1: {
                                    xmlText = (String)in.readObject();
                                    in.readBoolean();
                                    break Label_0205;
                                }
                                default: {
                                    throw new IOException("Deserialization error: version number " + majorVersionNum + "." + minorVersionNum + " not supported.");
                                }
                            }
                            break;
                        }
                        default: {
                            throw new IOException("Deserialization error: version number " + majorVersionNum + "." + minorVersionNum + " not supported.");
                        }
                    }
                }
                final XmlOptions opts = new XmlOptions().setDocumentType(XmlBeans.typeForClass(this._xbeanClass));
                this._impl = XmlBeans.getContextTypeLoader().parse(xmlText, null, opts);
            }
            catch (final Exception e) {
                throw (IOException)new IOException(e.getMessage()).initCause(e);
            }
        }
        
        private String readObjectV0(final ObjectInputStream in, final int utfBytes) throws IOException {
            final byte[] bArray = new byte[utfBytes + 2];
            bArray[0] = (byte)(0xFF & utfBytes >> 8);
            bArray[1] = (byte)(0xFF & utfBytes);
            int totalBytesRead;
            int numRead;
            for (totalBytesRead = 0; totalBytesRead < utfBytes; totalBytesRead += numRead) {
                numRead = in.read(bArray, 2 + totalBytesRead, utfBytes - totalBytesRead);
                if (numRead == -1) {
                    break;
                }
            }
            if (totalBytesRead != utfBytes) {
                throw new IOException("Error reading backwards compatible XmlObject: number of bytes read (" + totalBytesRead + ") != number expected (" + utfBytes + ")");
            }
            DataInputStream dis = null;
            String str = null;
            try {
                dis = new DataInputStream(new ByteArrayInputStream(bArray));
                str = dis.readUTF();
            }
            finally {
                if (dis != null) {
                    dis.close();
                }
            }
            return str;
        }
        
        private Object readResolve() throws ObjectStreamException {
            return this._impl;
        }
    }
    
    private static class SerializedInteriorObject implements Serializable
    {
        private static final long serialVersionUID = 1L;
        transient XmlObject _impl;
        transient XmlObject _root;
        
        private SerializedInteriorObject() {
        }
        
        private SerializedInteriorObject(final XmlObject impl, final XmlObject root) {
            this._impl = impl;
            this._root = root;
        }
        
        private void writeObject(final ObjectOutputStream out) throws IOException {
            out.writeObject(this._root);
            out.writeBoolean(false);
            out.writeInt(this.distanceToRoot());
        }
        
        private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
            this._root = (XmlObject)in.readObject();
            in.readBoolean();
            this._impl = this.objectAtDistance(in.readInt());
        }
        
        private Object readResolve() throws ObjectStreamException {
            return this._impl;
        }
        
        private int distanceToRoot() {
            final XmlCursor cur = this._impl.newCursor();
            int count = 0;
            while (!cur.toPrevToken().isNone()) {
                if (!cur.currentTokenType().isNamespace()) {
                    ++count;
                }
            }
            cur.dispose();
            return count;
        }
        
        private XmlObject objectAtDistance(int count) {
            final XmlCursor cur = this._root.newCursor();
            while (count > 0) {
                cur.toNextToken();
                if (!cur.currentTokenType().isNamespace()) {
                    --count;
                }
            }
            final XmlObject result = cur.getObject();
            cur.dispose();
            return result;
        }
    }
}
