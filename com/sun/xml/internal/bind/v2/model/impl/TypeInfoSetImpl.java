package com.sun.xml.internal.bind.v2.model.impl;

import com.sun.xml.internal.bind.v2.model.core.ElementInfo;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.JAXBContext;
import javax.xml.transform.Result;
import javax.xml.bind.annotation.XmlNsForm;
import javax.xml.bind.annotation.XmlNs;
import javax.xml.bind.annotation.XmlSchema;
import java.util.HashMap;
import com.sun.xml.internal.bind.v2.runtime.IllegalAnnotationException;
import com.sun.xml.internal.bind.v2.model.annotation.Locatable;
import javax.xml.bind.annotation.XmlRegistry;
import com.sun.xml.internal.bind.v2.model.core.Ref;
import com.sun.xml.internal.bind.v2.model.core.LeafInfo;
import com.sun.xml.internal.bind.v2.util.FlattenIterator;
import java.util.Iterator;
import java.util.Collections;
import java.util.LinkedHashMap;
import com.sun.xml.internal.bind.v2.model.core.NonElement;
import javax.xml.namespace.QName;
import com.sun.xml.internal.bind.v2.runtime.RuntimeUtil;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import com.sun.xml.internal.bind.v2.model.core.BuiltinLeafInfo;
import java.util.Map;
import com.sun.xml.internal.bind.v2.model.annotation.AnnotationReader;
import javax.xml.bind.annotation.XmlTransient;
import com.sun.xml.internal.bind.v2.model.nav.Navigator;
import com.sun.xml.internal.bind.v2.model.core.TypeInfoSet;

class TypeInfoSetImpl<T, C, F, M> implements TypeInfoSet<T, C, F, M>
{
    @XmlTransient
    public final Navigator<T, C, F, M> nav;
    @XmlTransient
    public final AnnotationReader<T, C, F, M> reader;
    private final Map<T, BuiltinLeafInfo<T, C>> builtins;
    private final Map<C, EnumLeafInfoImpl<T, C, F, M>> enums;
    private final Map<T, ArrayInfoImpl<T, C, F, M>> arrays;
    @XmlJavaTypeAdapter(RuntimeUtil.ToStringAdapter.class)
    private final Map<C, ClassInfoImpl<T, C, F, M>> beans;
    @XmlTransient
    private final Map<C, ClassInfoImpl<T, C, F, M>> beansView;
    private final Map<C, Map<QName, ElementInfoImpl<T, C, F, M>>> elementMappings;
    private final Iterable<? extends ElementInfoImpl<T, C, F, M>> allElements;
    private final NonElement<T, C> anyType;
    private Map<String, Map<String, String>> xmlNsCache;
    
    public TypeInfoSetImpl(final Navigator<T, C, F, M> nav, final AnnotationReader<T, C, F, M> reader, final Map<T, ? extends BuiltinLeafInfoImpl<T, C>> leaves) {
        this.builtins = new LinkedHashMap<T, BuiltinLeafInfo<T, C>>();
        this.enums = new LinkedHashMap<C, EnumLeafInfoImpl<T, C, F, M>>();
        this.arrays = new LinkedHashMap<T, ArrayInfoImpl<T, C, F, M>>();
        this.beans = new LinkedHashMap<C, ClassInfoImpl<T, C, F, M>>();
        this.beansView = Collections.unmodifiableMap((Map<? extends C, ? extends ClassInfoImpl<T, C, F, M>>)this.beans);
        this.elementMappings = new LinkedHashMap<C, Map<QName, ElementInfoImpl<T, C, F, M>>>();
        this.allElements = new Iterable<ElementInfoImpl<T, C, F, M>>() {
            @Override
            public Iterator<ElementInfoImpl<T, C, F, M>> iterator() {
                return new FlattenIterator<ElementInfoImpl<T, C, F, M>>(TypeInfoSetImpl.this.elementMappings.values());
            }
        };
        this.nav = nav;
        this.reader = reader;
        this.builtins.putAll((Map<? extends T, ? extends BuiltinLeafInfo<T, C>>)leaves);
        this.anyType = this.createAnyType();
        for (final Map.Entry<Class, Class> e : RuntimeUtil.primitiveToBox.entrySet()) {
            this.builtins.put(nav.getPrimitive(e.getKey()), (BuiltinLeafInfo<T, C>)leaves.get(nav.ref(e.getValue())));
        }
        this.elementMappings.put(null, new LinkedHashMap<QName, ElementInfoImpl<T, C, F, M>>());
    }
    
    protected NonElement<T, C> createAnyType() {
        return new AnyTypeImpl<T, C>(this.nav);
    }
    
    @Override
    public Navigator<T, C, F, M> getNavigator() {
        return this.nav;
    }
    
    public void add(final ClassInfoImpl<T, C, F, M> ci) {
        this.beans.put(ci.getClazz(), ci);
    }
    
    public void add(final EnumLeafInfoImpl<T, C, F, M> li) {
        this.enums.put(li.clazz, li);
    }
    
    public void add(final ArrayInfoImpl<T, C, F, M> ai) {
        this.arrays.put(ai.getType(), ai);
    }
    
    @Override
    public NonElement<T, C> getTypeInfo(T type) {
        type = this.nav.erasure(type);
        final LeafInfo<T, C> l = this.builtins.get(type);
        if (l != null) {
            return l;
        }
        if (this.nav.isArray(type)) {
            return this.arrays.get(type);
        }
        final C d = this.nav.asDecl(type);
        if (d == null) {
            return null;
        }
        return this.getClassInfo(d);
    }
    
    @Override
    public NonElement<T, C> getAnyTypeInfo() {
        return this.anyType;
    }
    
    @Override
    public NonElement<T, C> getTypeInfo(final Ref<T, C> ref) {
        assert !ref.valueList;
        final C c = this.nav.asDecl(ref.type);
        if (c != null && this.reader.getClassAnnotation(XmlRegistry.class, c, null) != null) {
            return null;
        }
        return this.getTypeInfo(ref.type);
    }
    
    @Override
    public Map<C, ? extends ClassInfoImpl<T, C, F, M>> beans() {
        return this.beansView;
    }
    
    @Override
    public Map<T, ? extends BuiltinLeafInfo<T, C>> builtins() {
        return this.builtins;
    }
    
    @Override
    public Map<C, ? extends EnumLeafInfoImpl<T, C, F, M>> enums() {
        return this.enums;
    }
    
    @Override
    public Map<? extends T, ? extends ArrayInfoImpl<T, C, F, M>> arrays() {
        return (Map<? extends T, ? extends ArrayInfoImpl<T, C, F, M>>)this.arrays;
    }
    
    @Override
    public NonElement<T, C> getClassInfo(final C type) {
        LeafInfo<T, C> l = this.builtins.get(this.nav.use(type));
        if (l != null) {
            return l;
        }
        l = this.enums.get(type);
        if (l != null) {
            return l;
        }
        if (this.nav.asDecl(Object.class).equals(type)) {
            return this.anyType;
        }
        return this.beans.get(type);
    }
    
    @Override
    public ElementInfoImpl<T, C, F, M> getElementInfo(C scope, final QName name) {
        while (scope != null) {
            final Map<QName, ElementInfoImpl<T, C, F, M>> m = this.elementMappings.get(scope);
            if (m != null) {
                final ElementInfoImpl<T, C, F, M> r = m.get(name);
                if (r != null) {
                    return r;
                }
            }
            scope = this.nav.getSuperClass(scope);
        }
        return (ElementInfoImpl)this.elementMappings.get(null).get(name);
    }
    
    public final void add(final ElementInfoImpl<T, C, F, M> ei, final ModelBuilder<T, C, F, M> builder) {
        C scope = null;
        if (ei.getScope() != null) {
            scope = ei.getScope().getClazz();
        }
        Map<QName, ElementInfoImpl<T, C, F, M>> m = this.elementMappings.get(scope);
        if (m == null) {
            this.elementMappings.put(scope, m = new LinkedHashMap<QName, ElementInfoImpl<T, C, F, M>>());
        }
        final ElementInfoImpl<T, C, F, M> existing = m.put(ei.getElementName(), ei);
        if (existing != null) {
            final QName en = ei.getElementName();
            builder.reportError(new IllegalAnnotationException(Messages.CONFLICTING_XML_ELEMENT_MAPPING.format(en.getNamespaceURI(), en.getLocalPart()), ei, existing));
        }
    }
    
    @Override
    public Map<QName, ? extends ElementInfoImpl<T, C, F, M>> getElementMappings(final C scope) {
        return this.elementMappings.get(scope);
    }
    
    @Override
    public Iterable<? extends ElementInfoImpl<T, C, F, M>> getAllElements() {
        return this.allElements;
    }
    
    @Override
    public Map<String, String> getXmlNs(final String namespaceUri) {
        if (this.xmlNsCache == null) {
            this.xmlNsCache = new HashMap<String, Map<String, String>>();
            for (final ClassInfoImpl<T, C, F, M> ci : this.beans().values()) {
                final XmlSchema xs = this.reader.getPackageAnnotation(XmlSchema.class, ci.getClazz(), null);
                if (xs == null) {
                    continue;
                }
                final String uri = xs.namespace();
                Map<String, String> m = this.xmlNsCache.get(uri);
                if (m == null) {
                    this.xmlNsCache.put(uri, m = new HashMap<String, String>());
                }
                for (final XmlNs xns : xs.xmlns()) {
                    m.put(xns.prefix(), xns.namespaceURI());
                }
            }
        }
        final Map<String, String> r = this.xmlNsCache.get(namespaceUri);
        if (r != null) {
            return r;
        }
        return Collections.emptyMap();
    }
    
    @Override
    public Map<String, String> getSchemaLocations() {
        final Map<String, String> r = new HashMap<String, String>();
        for (final ClassInfoImpl<T, C, F, M> ci : this.beans().values()) {
            final XmlSchema xs = this.reader.getPackageAnnotation(XmlSchema.class, ci.getClazz(), null);
            if (xs == null) {
                continue;
            }
            final String loc = xs.location();
            if (loc.equals("##generate")) {
                continue;
            }
            r.put(xs.namespace(), loc);
        }
        return r;
    }
    
    @Override
    public final XmlNsForm getElementFormDefault(final String nsUri) {
        for (final ClassInfoImpl<T, C, F, M> ci : this.beans().values()) {
            final XmlSchema xs = this.reader.getPackageAnnotation(XmlSchema.class, ci.getClazz(), null);
            if (xs == null) {
                continue;
            }
            if (!xs.namespace().equals(nsUri)) {
                continue;
            }
            final XmlNsForm xnf = xs.elementFormDefault();
            if (xnf != XmlNsForm.UNSET) {
                return xnf;
            }
        }
        return XmlNsForm.UNSET;
    }
    
    @Override
    public final XmlNsForm getAttributeFormDefault(final String nsUri) {
        for (final ClassInfoImpl<T, C, F, M> ci : this.beans().values()) {
            final XmlSchema xs = this.reader.getPackageAnnotation(XmlSchema.class, ci.getClazz(), null);
            if (xs == null) {
                continue;
            }
            if (!xs.namespace().equals(nsUri)) {
                continue;
            }
            final XmlNsForm xnf = xs.attributeFormDefault();
            if (xnf != XmlNsForm.UNSET) {
                return xnf;
            }
        }
        return XmlNsForm.UNSET;
    }
    
    @Override
    public void dump(final Result out) throws JAXBException {
        final JAXBContext context = JAXBContext.newInstance(this.getClass());
        final Marshaller m = context.createMarshaller();
        m.marshal(this, out);
    }
}
