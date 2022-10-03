package org.apache.xmlbeans.impl.values;

import org.apache.xmlbeans.XmlCursor;
import java.util.List;
import org.apache.xmlbeans.StringEnumAbstractBase;
import java.util.Date;
import java.util.Calendar;
import org.apache.xmlbeans.GDurationSpecification;
import org.apache.xmlbeans.GDuration;
import org.apache.xmlbeans.GDateSpecification;
import org.apache.xmlbeans.GDate;
import java.math.BigInteger;
import java.math.BigDecimal;
import org.apache.xmlbeans.QNameSet;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.SchemaProperty;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.impl.schema.SchemaTypeVisitorImpl;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.schema.SchemaTypeImpl;

public class XmlComplexContentImpl extends XmlObjectBase
{
    private SchemaTypeImpl _schemaType;
    
    public XmlComplexContentImpl(final SchemaType type) {
        this._schemaType = (SchemaTypeImpl)type;
        this.initComplexType(true, true);
    }
    
    @Override
    public SchemaType schemaType() {
        return this._schemaType;
    }
    
    public String compute_text(final NamespaceManager nsm) {
        return null;
    }
    
    @Override
    protected final void set_String(final String v) {
        assert this._schemaType.getContentType() != 2;
        if (this._schemaType.getContentType() != 4 && !this._schemaType.isNoType()) {
            throw new IllegalArgumentException("Type does not allow for textual content: " + this._schemaType);
        }
        super.set_String(v);
    }
    
    public void set_text(final String str) {
        assert !(!this._schemaType.isNoType());
    }
    
    @Override
    protected void update_from_complex_content() {
    }
    
    public void set_nil() {
    }
    
    public boolean equal_to(final XmlObject complexObject) {
        return this._schemaType.equals(complexObject.schemaType());
    }
    
    @Override
    protected int value_hash_code() {
        throw new IllegalStateException("Complex types cannot be used as hash keys");
    }
    
    @Override
    public TypeStoreVisitor new_visitor() {
        return new SchemaTypeVisitorImpl(this._schemaType.getContentModel());
    }
    
    @Override
    public boolean is_child_element_order_sensitive() {
        return this.schemaType().isOrderSensitive();
    }
    
    @Override
    public int get_elementflags(final QName eltName) {
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
    public String get_default_attribute_text(final QName attrName) {
        return super.get_default_attribute_text(attrName);
    }
    
    @Override
    public String get_default_element_text(final QName eltName) {
        final SchemaProperty prop = this.schemaType().getElementProperty(eltName);
        if (prop == null) {
            return "";
        }
        return prop.getDefaultText();
    }
    
    protected void unionArraySetterHelper(final Object[] sources, final QName elemName) {
        final int n = (sources == null) ? 0 : sources.length;
        final TypeStore store = this.get_store();
        int m;
        for (m = store.count_elements(elemName); m > n; --m) {
            store.remove_element(elemName, m - 1);
        }
        for (int i = 0; i < n; ++i) {
            TypeStoreUser user;
            if (i >= m) {
                user = store.add_element_user(elemName);
            }
            else {
                user = store.find_element_user(elemName, i);
            }
            ((XmlObjectBase)user).objectSet(sources[i]);
        }
    }
    
    protected SimpleValue[] arraySetterHelper(final int sourcesLength, final QName elemName) {
        final SimpleValue[] dests = new SimpleValue[sourcesLength];
        final TypeStore store = this.get_store();
        int m;
        for (m = store.count_elements(elemName); m > sourcesLength; --m) {
            store.remove_element(elemName, m - 1);
        }
        for (int i = 0; i < sourcesLength; ++i) {
            TypeStoreUser user;
            if (i >= m) {
                user = store.add_element_user(elemName);
            }
            else {
                user = store.find_element_user(elemName, i);
            }
            dests[i] = (SimpleValue)user;
        }
        return dests;
    }
    
    protected SimpleValue[] arraySetterHelper(final int sourcesLength, final QName elemName, final QNameSet set) {
        final SimpleValue[] dests = new SimpleValue[sourcesLength];
        final TypeStore store = this.get_store();
        int m;
        for (m = store.count_elements(set); m > sourcesLength; --m) {
            store.remove_element(set, m - 1);
        }
        for (int i = 0; i < sourcesLength; ++i) {
            TypeStoreUser user;
            if (i >= m) {
                user = store.add_element_user(elemName);
            }
            else {
                user = store.find_element_user(set, i);
            }
            dests[i] = (SimpleValue)user;
        }
        return dests;
    }
    
    protected void arraySetterHelper(final boolean[] sources, final QName elemName) {
        final int n = (sources == null) ? 0 : sources.length;
        final TypeStore store = this.get_store();
        int m;
        for (m = store.count_elements(elemName); m > n; --m) {
            store.remove_element(elemName, m - 1);
        }
        for (int i = 0; i < n; ++i) {
            TypeStoreUser user;
            if (i >= m) {
                user = store.add_element_user(elemName);
            }
            else {
                user = store.find_element_user(elemName, i);
            }
            ((XmlObjectBase)user).set(sources[i]);
        }
    }
    
    protected void arraySetterHelper(final float[] sources, final QName elemName) {
        final int n = (sources == null) ? 0 : sources.length;
        final TypeStore store = this.get_store();
        int m;
        for (m = store.count_elements(elemName); m > n; --m) {
            store.remove_element(elemName, m - 1);
        }
        for (int i = 0; i < n; ++i) {
            TypeStoreUser user;
            if (i >= m) {
                user = store.add_element_user(elemName);
            }
            else {
                user = store.find_element_user(elemName, i);
            }
            ((XmlObjectBase)user).set(sources[i]);
        }
    }
    
    protected void arraySetterHelper(final double[] sources, final QName elemName) {
        final int n = (sources == null) ? 0 : sources.length;
        final TypeStore store = this.get_store();
        int m;
        for (m = store.count_elements(elemName); m > n; --m) {
            store.remove_element(elemName, m - 1);
        }
        for (int i = 0; i < n; ++i) {
            TypeStoreUser user;
            if (i >= m) {
                user = store.add_element_user(elemName);
            }
            else {
                user = store.find_element_user(elemName, i);
            }
            ((XmlObjectBase)user).set(sources[i]);
        }
    }
    
    protected void arraySetterHelper(final byte[] sources, final QName elemName) {
        final int n = (sources == null) ? 0 : sources.length;
        final TypeStore store = this.get_store();
        int m;
        for (m = store.count_elements(elemName); m > n; --m) {
            store.remove_element(elemName, m - 1);
        }
        for (int i = 0; i < n; ++i) {
            TypeStoreUser user;
            if (i >= m) {
                user = store.add_element_user(elemName);
            }
            else {
                user = store.find_element_user(elemName, i);
            }
            ((XmlObjectBase)user).set(sources[i]);
        }
    }
    
    protected void arraySetterHelper(final short[] sources, final QName elemName) {
        final int n = (sources == null) ? 0 : sources.length;
        final TypeStore store = this.get_store();
        int m;
        for (m = store.count_elements(elemName); m > n; --m) {
            store.remove_element(elemName, m - 1);
        }
        for (int i = 0; i < n; ++i) {
            TypeStoreUser user;
            if (i >= m) {
                user = store.add_element_user(elemName);
            }
            else {
                user = store.find_element_user(elemName, i);
            }
            ((XmlObjectBase)user).set(sources[i]);
        }
    }
    
    protected void arraySetterHelper(final int[] sources, final QName elemName) {
        final int n = (sources == null) ? 0 : sources.length;
        final TypeStore store = this.get_store();
        int m;
        for (m = store.count_elements(elemName); m > n; --m) {
            store.remove_element(elemName, m - 1);
        }
        for (int i = 0; i < n; ++i) {
            TypeStoreUser user;
            if (i >= m) {
                user = store.add_element_user(elemName);
            }
            else {
                user = store.find_element_user(elemName, i);
            }
            ((XmlObjectBase)user).set(sources[i]);
        }
    }
    
    protected void arraySetterHelper(final long[] sources, final QName elemName) {
        final int n = (sources == null) ? 0 : sources.length;
        final TypeStore store = this.get_store();
        int m;
        for (m = store.count_elements(elemName); m > n; --m) {
            store.remove_element(elemName, m - 1);
        }
        for (int i = 0; i < n; ++i) {
            TypeStoreUser user;
            if (i >= m) {
                user = store.add_element_user(elemName);
            }
            else {
                user = store.find_element_user(elemName, i);
            }
            ((XmlObjectBase)user).set(sources[i]);
        }
    }
    
    protected void arraySetterHelper(final BigDecimal[] sources, final QName elemName) {
        final int n = (sources == null) ? 0 : sources.length;
        final TypeStore store = this.get_store();
        int m;
        for (m = store.count_elements(elemName); m > n; --m) {
            store.remove_element(elemName, m - 1);
        }
        for (int i = 0; i < n; ++i) {
            TypeStoreUser user;
            if (i >= m) {
                user = store.add_element_user(elemName);
            }
            else {
                user = store.find_element_user(elemName, i);
            }
            ((XmlObjectBase)user).set(sources[i]);
        }
    }
    
    protected void arraySetterHelper(final BigInteger[] sources, final QName elemName) {
        final int n = (sources == null) ? 0 : sources.length;
        final TypeStore store = this.get_store();
        int m;
        for (m = store.count_elements(elemName); m > n; --m) {
            store.remove_element(elemName, m - 1);
        }
        for (int i = 0; i < n; ++i) {
            TypeStoreUser user;
            if (i >= m) {
                user = store.add_element_user(elemName);
            }
            else {
                user = store.find_element_user(elemName, i);
            }
            ((XmlObjectBase)user).set(sources[i]);
        }
    }
    
    protected void arraySetterHelper(final String[] sources, final QName elemName) {
        final int n = (sources == null) ? 0 : sources.length;
        final TypeStore store = this.get_store();
        int m;
        for (m = store.count_elements(elemName); m > n; --m) {
            store.remove_element(elemName, m - 1);
        }
        for (int i = 0; i < n; ++i) {
            TypeStoreUser user;
            if (i >= m) {
                user = store.add_element_user(elemName);
            }
            else {
                user = store.find_element_user(elemName, i);
            }
            ((XmlObjectBase)user).set(sources[i]);
        }
    }
    
    protected void arraySetterHelper(final byte[][] sources, final QName elemName) {
        final int n = (sources == null) ? 0 : sources.length;
        final TypeStore store = this.get_store();
        int m;
        for (m = store.count_elements(elemName); m > n; --m) {
            store.remove_element(elemName, m - 1);
        }
        for (int i = 0; i < n; ++i) {
            TypeStoreUser user;
            if (i >= m) {
                user = store.add_element_user(elemName);
            }
            else {
                user = store.find_element_user(elemName, i);
            }
            ((XmlObjectBase)user).set(sources[i]);
        }
    }
    
    protected void arraySetterHelper(final GDate[] sources, final QName elemName) {
        final int n = (sources == null) ? 0 : sources.length;
        final TypeStore store = this.get_store();
        int m;
        for (m = store.count_elements(elemName); m > n; --m) {
            store.remove_element(elemName, m - 1);
        }
        for (int i = 0; i < n; ++i) {
            TypeStoreUser user;
            if (i >= m) {
                user = store.add_element_user(elemName);
            }
            else {
                user = store.find_element_user(elemName, i);
            }
            ((XmlObjectBase)user).set(sources[i]);
        }
    }
    
    protected void arraySetterHelper(final GDuration[] sources, final QName elemName) {
        final int n = (sources == null) ? 0 : sources.length;
        final TypeStore store = this.get_store();
        int m;
        for (m = store.count_elements(elemName); m > n; --m) {
            store.remove_element(elemName, m - 1);
        }
        for (int i = 0; i < n; ++i) {
            TypeStoreUser user;
            if (i >= m) {
                user = store.add_element_user(elemName);
            }
            else {
                user = store.find_element_user(elemName, i);
            }
            ((XmlObjectBase)user).set(sources[i]);
        }
    }
    
    protected void arraySetterHelper(final Calendar[] sources, final QName elemName) {
        final int n = (sources == null) ? 0 : sources.length;
        final TypeStore store = this.get_store();
        int m;
        for (m = store.count_elements(elemName); m > n; --m) {
            store.remove_element(elemName, m - 1);
        }
        for (int i = 0; i < n; ++i) {
            TypeStoreUser user;
            if (i >= m) {
                user = store.add_element_user(elemName);
            }
            else {
                user = store.find_element_user(elemName, i);
            }
            ((XmlObjectBase)user).set(sources[i]);
        }
    }
    
    protected void arraySetterHelper(final Date[] sources, final QName elemName) {
        final int n = (sources == null) ? 0 : sources.length;
        final TypeStore store = this.get_store();
        int m;
        for (m = store.count_elements(elemName); m > n; --m) {
            store.remove_element(elemName, m - 1);
        }
        for (int i = 0; i < n; ++i) {
            TypeStoreUser user;
            if (i >= m) {
                user = store.add_element_user(elemName);
            }
            else {
                user = store.find_element_user(elemName, i);
            }
            ((XmlObjectBase)user).set(sources[i]);
        }
    }
    
    protected void arraySetterHelper(final QName[] sources, final QName elemName) {
        final int n = (sources == null) ? 0 : sources.length;
        final TypeStore store = this.get_store();
        int m;
        for (m = store.count_elements(elemName); m > n; --m) {
            store.remove_element(elemName, m - 1);
        }
        for (int i = 0; i < n; ++i) {
            TypeStoreUser user;
            if (i >= m) {
                user = store.add_element_user(elemName);
            }
            else {
                user = store.find_element_user(elemName, i);
            }
            ((XmlObjectBase)user).set(sources[i]);
        }
    }
    
    protected void arraySetterHelper(final StringEnumAbstractBase[] sources, final QName elemName) {
        final int n = (sources == null) ? 0 : sources.length;
        final TypeStore store = this.get_store();
        int m;
        for (m = store.count_elements(elemName); m > n; --m) {
            store.remove_element(elemName, m - 1);
        }
        for (int i = 0; i < n; ++i) {
            TypeStoreUser user;
            if (i >= m) {
                user = store.add_element_user(elemName);
            }
            else {
                user = store.find_element_user(elemName, i);
            }
            ((XmlObjectBase)user).set(sources[i]);
        }
    }
    
    protected void arraySetterHelper(final List[] sources, final QName elemName) {
        final int n = (sources == null) ? 0 : sources.length;
        final TypeStore store = this.get_store();
        int m;
        for (m = store.count_elements(elemName); m > n; --m) {
            store.remove_element(elemName, m - 1);
        }
        for (int i = 0; i < n; ++i) {
            TypeStoreUser user;
            if (i >= m) {
                user = store.add_element_user(elemName);
            }
            else {
                user = store.find_element_user(elemName, i);
            }
            ((XmlObjectBase)user).set(sources[i]);
        }
    }
    
    protected void unionArraySetterHelper(final Object[] sources, final QName elemName, final QNameSet set) {
        final int n = (sources == null) ? 0 : sources.length;
        final TypeStore store = this.get_store();
        int m;
        for (m = store.count_elements(set); m > n; --m) {
            store.remove_element(set, m - 1);
        }
        for (int i = 0; i < n; ++i) {
            TypeStoreUser user;
            if (i >= m) {
                user = store.add_element_user(elemName);
            }
            else {
                user = store.find_element_user(set, i);
            }
            ((XmlObjectBase)user).objectSet(sources[i]);
        }
    }
    
    protected void arraySetterHelper(final boolean[] sources, final QName elemName, final QNameSet set) {
        final int n = (sources == null) ? 0 : sources.length;
        final TypeStore store = this.get_store();
        int m;
        for (m = store.count_elements(set); m > n; --m) {
            store.remove_element(set, m - 1);
        }
        for (int i = 0; i < n; ++i) {
            TypeStoreUser user;
            if (i >= m) {
                user = store.add_element_user(elemName);
            }
            else {
                user = store.find_element_user(set, i);
            }
            ((XmlObjectBase)user).set(sources[i]);
        }
    }
    
    protected void arraySetterHelper(final float[] sources, final QName elemName, final QNameSet set) {
        final int n = (sources == null) ? 0 : sources.length;
        final TypeStore store = this.get_store();
        int m;
        for (m = store.count_elements(set); m > n; --m) {
            store.remove_element(set, m - 1);
        }
        for (int i = 0; i < n; ++i) {
            TypeStoreUser user;
            if (i >= m) {
                user = store.add_element_user(elemName);
            }
            else {
                user = store.find_element_user(set, i);
            }
            ((XmlObjectBase)user).set(sources[i]);
        }
    }
    
    protected void arraySetterHelper(final double[] sources, final QName elemName, final QNameSet set) {
        final int n = (sources == null) ? 0 : sources.length;
        final TypeStore store = this.get_store();
        int m;
        for (m = store.count_elements(set); m > n; --m) {
            store.remove_element(set, m - 1);
        }
        for (int i = 0; i < n; ++i) {
            TypeStoreUser user;
            if (i >= m) {
                user = store.add_element_user(elemName);
            }
            else {
                user = store.find_element_user(set, i);
            }
            ((XmlObjectBase)user).set(sources[i]);
        }
    }
    
    protected void arraySetterHelper(final byte[] sources, final QName elemName, final QNameSet set) {
        final int n = (sources == null) ? 0 : sources.length;
        final TypeStore store = this.get_store();
        int m;
        for (m = store.count_elements(set); m > n; --m) {
            store.remove_element(set, m - 1);
        }
        for (int i = 0; i < n; ++i) {
            TypeStoreUser user;
            if (i >= m) {
                user = store.add_element_user(elemName);
            }
            else {
                user = store.find_element_user(set, i);
            }
            ((XmlObjectBase)user).set(sources[i]);
        }
    }
    
    protected void arraySetterHelper(final short[] sources, final QName elemName, final QNameSet set) {
        final int n = (sources == null) ? 0 : sources.length;
        final TypeStore store = this.get_store();
        int m;
        for (m = store.count_elements(set); m > n; --m) {
            store.remove_element(set, m - 1);
        }
        for (int i = 0; i < n; ++i) {
            TypeStoreUser user;
            if (i >= m) {
                user = store.add_element_user(elemName);
            }
            else {
                user = store.find_element_user(set, i);
            }
            ((XmlObjectBase)user).set(sources[i]);
        }
    }
    
    protected void arraySetterHelper(final int[] sources, final QName elemName, final QNameSet set) {
        final int n = (sources == null) ? 0 : sources.length;
        final TypeStore store = this.get_store();
        int m;
        for (m = store.count_elements(set); m > n; --m) {
            store.remove_element(set, m - 1);
        }
        for (int i = 0; i < n; ++i) {
            TypeStoreUser user;
            if (i >= m) {
                user = store.add_element_user(elemName);
            }
            else {
                user = store.find_element_user(set, i);
            }
            ((XmlObjectBase)user).set(sources[i]);
        }
    }
    
    protected void arraySetterHelper(final long[] sources, final QName elemName, final QNameSet set) {
        final int n = (sources == null) ? 0 : sources.length;
        final TypeStore store = this.get_store();
        int m;
        for (m = store.count_elements(set); m > n; --m) {
            store.remove_element(set, m - 1);
        }
        for (int i = 0; i < n; ++i) {
            TypeStoreUser user;
            if (i >= m) {
                user = store.add_element_user(elemName);
            }
            else {
                user = store.find_element_user(set, i);
            }
            ((XmlObjectBase)user).set(sources[i]);
        }
    }
    
    protected void arraySetterHelper(final BigDecimal[] sources, final QName elemName, final QNameSet set) {
        final int n = (sources == null) ? 0 : sources.length;
        final TypeStore store = this.get_store();
        int m;
        for (m = store.count_elements(set); m > n; --m) {
            store.remove_element(set, m - 1);
        }
        for (int i = 0; i < n; ++i) {
            TypeStoreUser user;
            if (i >= m) {
                user = store.add_element_user(elemName);
            }
            else {
                user = store.find_element_user(set, i);
            }
            ((XmlObjectBase)user).set(sources[i]);
        }
    }
    
    protected void arraySetterHelper(final BigInteger[] sources, final QName elemName, final QNameSet set) {
        final int n = (sources == null) ? 0 : sources.length;
        final TypeStore store = this.get_store();
        int m;
        for (m = store.count_elements(set); m > n; --m) {
            store.remove_element(set, m - 1);
        }
        for (int i = 0; i < n; ++i) {
            TypeStoreUser user;
            if (i >= m) {
                user = store.add_element_user(elemName);
            }
            else {
                user = store.find_element_user(set, i);
            }
            ((XmlObjectBase)user).set(sources[i]);
        }
    }
    
    protected void arraySetterHelper(final String[] sources, final QName elemName, final QNameSet set) {
        final int n = (sources == null) ? 0 : sources.length;
        final TypeStore store = this.get_store();
        int m;
        for (m = store.count_elements(set); m > n; --m) {
            store.remove_element(set, m - 1);
        }
        for (int i = 0; i < n; ++i) {
            TypeStoreUser user;
            if (i >= m) {
                user = store.add_element_user(elemName);
            }
            else {
                user = store.find_element_user(set, i);
            }
            ((XmlObjectBase)user).set(sources[i]);
        }
    }
    
    protected void arraySetterHelper(final byte[][] sources, final QName elemName, final QNameSet set) {
        final int n = (sources == null) ? 0 : sources.length;
        final TypeStore store = this.get_store();
        int m;
        for (m = store.count_elements(set); m > n; --m) {
            store.remove_element(set, m - 1);
        }
        for (int i = 0; i < n; ++i) {
            TypeStoreUser user;
            if (i >= m) {
                user = store.add_element_user(elemName);
            }
            else {
                user = store.find_element_user(set, i);
            }
            ((XmlObjectBase)user).set(sources[i]);
        }
    }
    
    protected void arraySetterHelper(final GDate[] sources, final QName elemName, final QNameSet set) {
        final int n = (sources == null) ? 0 : sources.length;
        final TypeStore store = this.get_store();
        int m;
        for (m = store.count_elements(set); m > n; --m) {
            store.remove_element(set, m - 1);
        }
        for (int i = 0; i < n; ++i) {
            TypeStoreUser user;
            if (i >= m) {
                user = store.add_element_user(elemName);
            }
            else {
                user = store.find_element_user(set, i);
            }
            ((XmlObjectBase)user).set(sources[i]);
        }
    }
    
    protected void arraySetterHelper(final GDuration[] sources, final QName elemName, final QNameSet set) {
        final int n = (sources == null) ? 0 : sources.length;
        final TypeStore store = this.get_store();
        int m;
        for (m = store.count_elements(set); m > n; --m) {
            store.remove_element(set, m - 1);
        }
        for (int i = 0; i < n; ++i) {
            TypeStoreUser user;
            if (i >= m) {
                user = store.add_element_user(elemName);
            }
            else {
                user = store.find_element_user(set, i);
            }
            ((XmlObjectBase)user).set(sources[i]);
        }
    }
    
    protected void arraySetterHelper(final Calendar[] sources, final QName elemName, final QNameSet set) {
        final int n = (sources == null) ? 0 : sources.length;
        final TypeStore store = this.get_store();
        int m;
        for (m = store.count_elements(set); m > n; --m) {
            store.remove_element(set, m - 1);
        }
        for (int i = 0; i < n; ++i) {
            TypeStoreUser user;
            if (i >= m) {
                user = store.add_element_user(elemName);
            }
            else {
                user = store.find_element_user(set, i);
            }
            ((XmlObjectBase)user).set(sources[i]);
        }
    }
    
    protected void arraySetterHelper(final Date[] sources, final QName elemName, final QNameSet set) {
        final int n = (sources == null) ? 0 : sources.length;
        final TypeStore store = this.get_store();
        int m;
        for (m = store.count_elements(set); m > n; --m) {
            store.remove_element(set, m - 1);
        }
        for (int i = 0; i < n; ++i) {
            TypeStoreUser user;
            if (i >= m) {
                user = store.add_element_user(elemName);
            }
            else {
                user = store.find_element_user(set, i);
            }
            ((XmlObjectBase)user).set(sources[i]);
        }
    }
    
    protected void arraySetterHelper(final QName[] sources, final QName elemName, final QNameSet set) {
        final int n = (sources == null) ? 0 : sources.length;
        final TypeStore store = this.get_store();
        int m;
        for (m = store.count_elements(set); m > n; --m) {
            store.remove_element(set, m - 1);
        }
        for (int i = 0; i < n; ++i) {
            TypeStoreUser user;
            if (i >= m) {
                user = store.add_element_user(elemName);
            }
            else {
                user = store.find_element_user(set, i);
            }
            ((XmlObjectBase)user).set(sources[i]);
        }
    }
    
    protected void arraySetterHelper(final StringEnumAbstractBase[] sources, final QName elemName, final QNameSet set) {
        final int n = (sources == null) ? 0 : sources.length;
        final TypeStore store = this.get_store();
        int m;
        for (m = store.count_elements(set); m > n; --m) {
            store.remove_element(set, m - 1);
        }
        for (int i = 0; i < n; ++i) {
            TypeStoreUser user;
            if (i >= m) {
                user = store.add_element_user(elemName);
            }
            else {
                user = store.find_element_user(set, i);
            }
            ((XmlObjectBase)user).set(sources[i]);
        }
    }
    
    protected void arraySetterHelper(final List[] sources, final QName elemName, final QNameSet set) {
        final int n = (sources == null) ? 0 : sources.length;
        final TypeStore store = this.get_store();
        int m;
        for (m = store.count_elements(set); m > n; --m) {
            store.remove_element(set, m - 1);
        }
        for (int i = 0; i < n; ++i) {
            TypeStoreUser user;
            if (i >= m) {
                user = store.add_element_user(elemName);
            }
            else {
                user = store.find_element_user(set, i);
            }
            ((XmlObjectBase)user).set(sources[i]);
        }
    }
    
    protected void arraySetterHelper(final XmlObject[] sources, final QName elemName) {
        final TypeStore store = this.get_store();
        if (sources == null || sources.length == 0) {
            for (int m = store.count_elements(elemName); m > 0; --m) {
                store.remove_element(elemName, 0);
            }
            return;
        }
        int i = store.count_elements(elemName);
        int startSrc = 0;
        int startDest = 0;
        int j;
        for (j = 0; j < sources.length; ++j) {
            if (!sources[j].isImmutable()) {
                final XmlCursor c = sources[j].newCursor();
                if (c.toParent() && c.getObject() == this) {
                    c.dispose();
                    break;
                }
                c.dispose();
            }
        }
        if (j < sources.length) {
            TypeStoreUser current = store.find_element_user(elemName, 0);
            if (current == sources[j]) {
                int k;
                TypeStoreUser user;
                for (k = 0, k = 0; k < j; ++k) {
                    user = store.insert_element_user(elemName, k);
                    ((XmlObjectBase)user).set(sources[k]);
                }
                ++j;
                ++k;
                while (j < sources.length) {
                    final XmlCursor c2 = sources[j].isImmutable() ? null : sources[j].newCursor();
                    if (c2 != null && c2.toParent() && c2.getObject() == this) {
                        c2.dispose();
                        current = store.find_element_user(elemName, k);
                        if (current != sources[j]) {
                            break;
                        }
                    }
                    else {
                        c2.dispose();
                        final TypeStoreUser user2 = store.insert_element_user(elemName, k);
                        ((XmlObjectBase)user2).set(sources[j]);
                    }
                    ++j;
                    ++k;
                }
                startDest = k;
                startSrc = j;
                i = store.count_elements(elemName);
            }
        }
        for (int l = j; l < sources.length; ++l) {
            final TypeStoreUser user3 = store.add_element_user(elemName);
            ((XmlObjectBase)user3).set(sources[l]);
        }
        int n;
        for (n = j; i > n - startSrc + startDest; --i) {
            store.remove_element(elemName, i - 1);
        }
        for (j = startSrc, int k = startDest; j < n; ++j, ++k) {
            TypeStoreUser user;
            if (k >= i) {
                user = store.add_element_user(elemName);
            }
            else {
                user = store.find_element_user(elemName, k);
            }
            ((XmlObjectBase)user).set(sources[j]);
        }
    }
    
    protected void arraySetterHelper(final XmlObject[] sources, final QName elemName, final QNameSet set) {
        final TypeStore store = this.get_store();
        if (sources == null || sources.length == 0) {
            for (int m = store.count_elements(set); m > 0; --m) {
                store.remove_element(set, 0);
            }
            return;
        }
        int i = store.count_elements(set);
        int startSrc = 0;
        int startDest = 0;
        int j;
        for (j = 0; j < sources.length; ++j) {
            if (!sources[j].isImmutable()) {
                final XmlCursor c = sources[j].newCursor();
                if (c.toParent() && c.getObject() == this) {
                    c.dispose();
                    break;
                }
                c.dispose();
            }
        }
        if (j < sources.length) {
            TypeStoreUser current = store.find_element_user(set, 0);
            if (current == sources[j]) {
                int k;
                TypeStoreUser user;
                for (k = 0, k = 0; k < j; ++k) {
                    user = store.insert_element_user(set, elemName, k);
                    ((XmlObjectBase)user).set(sources[k]);
                }
                ++j;
                ++k;
                while (j < sources.length) {
                    final XmlCursor c2 = sources[j].isImmutable() ? null : sources[j].newCursor();
                    if (c2 != null && c2.toParent() && c2.getObject() == this) {
                        c2.dispose();
                        current = store.find_element_user(set, k);
                        if (current != sources[j]) {
                            break;
                        }
                    }
                    else {
                        c2.dispose();
                        final TypeStoreUser user2 = store.insert_element_user(set, elemName, k);
                        ((XmlObjectBase)user2).set(sources[j]);
                    }
                    ++j;
                    ++k;
                }
                startDest = k;
                startSrc = j;
                i = store.count_elements(elemName);
            }
        }
        for (int l = j; l < sources.length; ++l) {
            final TypeStoreUser user3 = store.add_element_user(elemName);
            ((XmlObjectBase)user3).set(sources[l]);
        }
        int n;
        for (n = j; i > n - startSrc + startDest; --i) {
            store.remove_element(set, i - 1);
        }
        for (j = startSrc, int k = startDest; j < n; ++j, ++k) {
            TypeStoreUser user;
            if (k >= i) {
                user = store.add_element_user(elemName);
            }
            else {
                user = store.find_element_user(set, k);
            }
            ((XmlObjectBase)user).set(sources[j]);
        }
    }
}
