package com.sun.xml.internal.bind.v2.model.impl;

import java.util.Collection;
import com.sun.xml.internal.bind.v2.model.core.NonElement;
import com.sun.xml.internal.bind.v2.model.core.ClassInfo;
import javax.xml.bind.annotation.XmlNsForm;
import javax.xml.bind.annotation.XmlSchema;
import com.sun.xml.internal.bind.v2.model.core.ElementInfo;
import javax.xml.namespace.QName;
import java.util.Iterator;
import com.sun.xml.internal.bind.v2.model.annotation.AnnotationReader;
import com.sun.xml.internal.bind.v2.model.nav.Navigator;
import java.util.Collections;
import com.sun.xml.internal.bind.v2.model.annotation.Locatable;
import javax.xml.bind.JAXBElement;
import com.sun.xml.internal.bind.v2.runtime.IllegalAnnotationException;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import com.sun.xml.internal.bind.v2.model.core.PropertyKind;
import java.lang.annotation.Annotation;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlMixed;
import java.util.LinkedHashSet;
import com.sun.xml.internal.bind.v2.model.core.WildcardMode;
import com.sun.xml.internal.bind.v2.model.core.Element;
import java.util.Set;
import com.sun.xml.internal.bind.v2.model.core.ReferencePropertyInfo;

class ReferencePropertyInfoImpl<T, C, F, M> extends ERPropertyInfoImpl<T, C, F, M> implements ReferencePropertyInfo<T, C>, DummyPropertyInfo<T, C, F, M>
{
    private Set<Element<T, C>> types;
    private Set<ReferencePropertyInfoImpl<T, C, F, M>> subTypes;
    private final boolean isMixed;
    private final WildcardMode wildcard;
    private final C domHandler;
    private Boolean isRequired;
    private static boolean is2_2;
    
    public ReferencePropertyInfoImpl(final ClassInfoImpl<T, C, F, M> classInfo, final PropertySeed<T, C, F, M> seed) {
        super(classInfo, seed);
        this.subTypes = new LinkedHashSet<ReferencePropertyInfoImpl<T, C, F, M>>();
        this.isMixed = (seed.readAnnotation(XmlMixed.class) != null);
        final XmlAnyElement xae = seed.readAnnotation(XmlAnyElement.class);
        if (xae == null) {
            this.wildcard = null;
            this.domHandler = null;
        }
        else {
            this.wildcard = (xae.lax() ? WildcardMode.LAX : WildcardMode.SKIP);
            this.domHandler = this.nav().asDecl(this.reader().getClassValue(xae, "value"));
        }
    }
    
    @Override
    public Set<? extends Element<T, C>> ref() {
        return this.getElements();
    }
    
    @Override
    public PropertyKind kind() {
        return PropertyKind.REFERENCE;
    }
    
    @Override
    public Set<? extends Element<T, C>> getElements() {
        if (this.types == null) {
            this.calcTypes(false);
        }
        assert this.types != null;
        return this.types;
    }
    
    private void calcTypes(final boolean last) {
        this.types = new LinkedHashSet<Element<T, C>>();
        XmlElementRefs refs = this.seed.readAnnotation(XmlElementRefs.class);
        XmlElementRef ref = this.seed.readAnnotation(XmlElementRef.class);
        if (refs != null && ref != null) {
            this.parent.builder.reportError(new IllegalAnnotationException(Messages.MUTUALLY_EXCLUSIVE_ANNOTATIONS.format(this.nav().getClassName(this.parent.getClazz()) + '#' + this.seed.getName(), ref.annotationType().getName(), refs.annotationType().getName()), ref, refs));
        }
        XmlElementRef[] ann;
        if (refs != null) {
            ann = refs.value();
        }
        else if (ref != null) {
            ann = new XmlElementRef[] { ref };
        }
        else {
            ann = null;
        }
        this.isRequired = !this.isCollection();
        if (ann != null) {
            final Navigator<T, C, F, M> nav = this.nav();
            final AnnotationReader<T, C, F, M> reader = this.reader();
            final T defaultType = nav.ref(XmlElementRef.DEFAULT.class);
            final C je = nav.asDecl(JAXBElement.class);
            for (final XmlElementRef r : ann) {
                T type = reader.getClassValue(r, "type");
                if (this.nav().isSameType(type, defaultType)) {
                    type = nav.erasure(this.getIndividualType());
                }
                boolean yield;
                if (nav.getBaseClass(type, je) != null) {
                    yield = this.addGenericElement(r);
                }
                else {
                    yield = this.addAllSubtypes(type);
                }
                if (this.isRequired && !this.isRequired(r)) {
                    this.isRequired = false;
                }
                if (last && !yield) {
                    if (this.nav().isSameType(type, nav.ref(JAXBElement.class))) {
                        this.parent.builder.reportError(new IllegalAnnotationException(Messages.NO_XML_ELEMENT_DECL.format(this.getEffectiveNamespaceFor(r), r.name()), this));
                    }
                    else {
                        this.parent.builder.reportError(new IllegalAnnotationException(Messages.INVALID_XML_ELEMENT_REF.format(type), this));
                    }
                    return;
                }
            }
        }
        for (final ReferencePropertyInfoImpl<T, C, F, M> info : this.subTypes) {
            final PropertySeed sd = info.seed;
            refs = (XmlElementRefs)sd.readAnnotation(XmlElementRefs.class);
            ref = (XmlElementRef)sd.readAnnotation(XmlElementRef.class);
            if (refs != null && ref != null) {
                this.parent.builder.reportError(new IllegalAnnotationException(Messages.MUTUALLY_EXCLUSIVE_ANNOTATIONS.format(this.nav().getClassName(this.parent.getClazz()) + '#' + this.seed.getName(), ref.annotationType().getName(), refs.annotationType().getName()), ref, refs));
            }
            if (refs != null) {
                ann = refs.value();
            }
            else if (ref != null) {
                ann = new XmlElementRef[] { ref };
            }
            else {
                ann = null;
            }
            if (ann != null) {
                final Navigator<T, C, F, M> nav2 = this.nav();
                final AnnotationReader<T, C, F, M> reader2 = this.reader();
                final T defaultType2 = nav2.ref(XmlElementRef.DEFAULT.class);
                final C je2 = nav2.asDecl(JAXBElement.class);
                for (final XmlElementRef r2 : ann) {
                    T type2 = reader2.getClassValue(r2, "type");
                    if (this.nav().isSameType(type2, defaultType2)) {
                        type2 = nav2.erasure(this.getIndividualType());
                    }
                    boolean yield2;
                    if (nav2.getBaseClass(type2, je2) != null) {
                        yield2 = this.addGenericElement(r2, info);
                    }
                    else {
                        yield2 = this.addAllSubtypes(type2);
                    }
                    if (last && !yield2) {
                        if (this.nav().isSameType(type2, nav2.ref(JAXBElement.class))) {
                            this.parent.builder.reportError(new IllegalAnnotationException(Messages.NO_XML_ELEMENT_DECL.format(this.getEffectiveNamespaceFor(r2), r2.name()), this));
                        }
                        else {
                            this.parent.builder.reportError(new IllegalAnnotationException(Messages.INVALID_XML_ELEMENT_REF.format(new Object[0]), this));
                        }
                        return;
                    }
                }
            }
        }
        this.types = Collections.unmodifiableSet((Set<? extends Element<T, C>>)this.types);
    }
    
    @Override
    public boolean isRequired() {
        if (this.isRequired == null) {
            this.calcTypes(false);
        }
        return this.isRequired;
    }
    
    private boolean isRequired(final XmlElementRef ref) {
        if (!ReferencePropertyInfoImpl.is2_2) {
            return true;
        }
        try {
            return ref.required();
        }
        catch (final LinkageError e) {
            ReferencePropertyInfoImpl.is2_2 = false;
            return true;
        }
    }
    
    private boolean addGenericElement(final XmlElementRef r) {
        final String nsUri = this.getEffectiveNamespaceFor(r);
        return this.addGenericElement(this.parent.owner.getElementInfo(this.parent.getClazz(), new QName(nsUri, r.name())));
    }
    
    private boolean addGenericElement(final XmlElementRef r, final ReferencePropertyInfoImpl<T, C, F, M> info) {
        final String nsUri = info.getEffectiveNamespaceFor(r);
        final ElementInfo ei = this.parent.owner.getElementInfo(info.parent.getClazz(), new QName(nsUri, r.name()));
        this.types.add(ei);
        return true;
    }
    
    private String getEffectiveNamespaceFor(final XmlElementRef r) {
        String nsUri = r.namespace();
        final XmlSchema xs = this.reader().getPackageAnnotation(XmlSchema.class, this.parent.getClazz(), this);
        if (xs != null && xs.attributeFormDefault() == XmlNsForm.QUALIFIED && nsUri.length() == 0) {
            nsUri = this.parent.builder.defaultNsUri;
        }
        return nsUri;
    }
    
    private boolean addGenericElement(final ElementInfo<T, C> ei) {
        if (ei == null) {
            return false;
        }
        this.types.add(ei);
        for (final ElementInfo<T, C> subst : ei.getSubstitutionMembers()) {
            this.addGenericElement(subst);
        }
        return true;
    }
    
    private boolean addAllSubtypes(final T type) {
        final Navigator<T, C, F, M> nav = this.nav();
        final NonElement<T, C> t = this.parent.builder.getClassInfo(nav.asDecl(type), this);
        if (!(t instanceof ClassInfo)) {
            return false;
        }
        boolean result = false;
        final ClassInfo<T, C> c = (ClassInfo)t;
        if (c.isElement()) {
            this.types.add(c.asElement());
            result = true;
        }
        for (final ClassInfo<T, C> ci : this.parent.owner.beans().values()) {
            if (ci.isElement() && nav.isSubClassOf(ci.getType(), type)) {
                this.types.add(ci.asElement());
                result = true;
            }
        }
        for (final ElementInfo<T, C> ei : this.parent.owner.getElementMappings(null).values()) {
            if (nav.isSubClassOf(ei.getType(), type)) {
                this.types.add(ei);
                result = true;
            }
        }
        return result;
    }
    
    @Override
    protected void link() {
        super.link();
        this.calcTypes(true);
    }
    
    @Override
    public final void addType(final PropertyInfoImpl<T, C, F, M> info) {
        this.subTypes.add((ReferencePropertyInfoImpl)info);
    }
    
    @Override
    public final boolean isMixed() {
        return this.isMixed;
    }
    
    @Override
    public final WildcardMode getWildcard() {
        return this.wildcard;
    }
    
    @Override
    public final C getDOMHandler() {
        return this.domHandler;
    }
    
    static {
        ReferencePropertyInfoImpl.is2_2 = true;
    }
}
