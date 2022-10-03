package com.sun.xml.internal.bind.v2.model.impl;

import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlAttachmentRef;
import javax.xml.bind.annotation.XmlMimeType;
import javax.xml.bind.annotation.XmlInlineBinaryData;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlID;
import com.sun.xml.internal.bind.v2.util.EditDistance;
import java.util.AbstractList;
import java.util.HashSet;
import java.lang.reflect.Method;
import com.sun.xml.internal.bind.v2.runtime.Location;
import com.sun.xml.internal.bind.v2.model.core.PropertyKind;
import java.util.ArrayList;
import java.util.Set;
import com.sun.xml.internal.bind.v2.model.annotation.MethodLocatable;
import java.util.Collection;
import java.util.TreeSet;
import java.util.LinkedHashMap;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Map;
import javax.xml.bind.annotation.XmlMixed;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlAccessorType;
import com.sun.xml.internal.bind.v2.model.core.ValuePropertyInfo;
import com.sun.xml.internal.bind.annotation.OverrideAnnotationOf;
import javax.xml.bind.annotation.XmlAccessType;
import java.util.Comparator;
import java.util.Collections;
import java.util.List;
import java.util.Iterator;
import com.sun.xml.internal.bind.v2.model.core.ID;
import com.sun.xml.internal.bind.v2.model.core.PropertyInfo;
import com.sun.xml.internal.bind.v2.model.core.NonElement;
import com.sun.xml.internal.bind.v2.runtime.IllegalAnnotationException;
import javax.xml.bind.annotation.XmlAccessOrder;
import javax.xml.bind.annotation.XmlAccessorOrder;
import javax.xml.bind.annotation.XmlType;
import com.sun.xml.internal.bind.v2.model.annotation.Locatable;
import java.util.HashMap;
import java.lang.annotation.Annotation;
import com.sun.istack.internal.FinalArrayList;
import javax.xml.namespace.QName;
import com.sun.xml.internal.bind.v2.model.core.Element;
import com.sun.xml.internal.bind.v2.model.core.ClassInfo;

public class ClassInfoImpl<T, C, F, M> extends TypeInfoImpl<T, C, F, M> implements ClassInfo<T, C>, Element<T, C>
{
    protected final C clazz;
    private final QName elementName;
    private final QName typeName;
    private FinalArrayList<PropertyInfoImpl<T, C, F, M>> properties;
    private String[] propOrder;
    private ClassInfoImpl<T, C, F, M> baseClass;
    private boolean baseClassComputed;
    private boolean hasSubClasses;
    protected PropertySeed<T, C, F, M> attributeWildcard;
    private M factoryMethod;
    private static final SecondaryAnnotation[] SECONDARY_ANNOTATIONS;
    private static final Annotation[] EMPTY_ANNOTATIONS;
    private static final HashMap<Class, Integer> ANNOTATION_NUMBER_MAP;
    private static final String[] DEFAULT_ORDER;
    
    ClassInfoImpl(final ModelBuilder<T, C, F, M> builder, final Locatable upstream, final C clazz) {
        super(builder, upstream);
        this.baseClassComputed = false;
        this.hasSubClasses = false;
        this.factoryMethod = null;
        this.clazz = clazz;
        assert clazz != null;
        this.elementName = this.parseElementName(clazz);
        final XmlType t = this.reader().getClassAnnotation(XmlType.class, clazz, this);
        this.typeName = this.parseTypeName(clazz, t);
        if (t != null) {
            final String[] propOrder = t.propOrder();
            if (propOrder.length == 0) {
                this.propOrder = null;
            }
            else if (propOrder[0].length() == 0) {
                this.propOrder = ClassInfoImpl.DEFAULT_ORDER;
            }
            else {
                this.propOrder = propOrder;
            }
        }
        else {
            this.propOrder = ClassInfoImpl.DEFAULT_ORDER;
        }
        XmlAccessorOrder xao = this.reader().getPackageAnnotation(XmlAccessorOrder.class, clazz, this);
        if (xao != null && xao.value() == XmlAccessOrder.UNDEFINED) {
            this.propOrder = null;
        }
        xao = this.reader().getClassAnnotation(XmlAccessorOrder.class, clazz, this);
        if (xao != null && xao.value() == XmlAccessOrder.UNDEFINED) {
            this.propOrder = null;
        }
        if (this.nav().isInterface(clazz)) {
            builder.reportError(new IllegalAnnotationException(Messages.CANT_HANDLE_INTERFACE.format(this.nav().getClassName(clazz)), this));
        }
        if (!this.hasFactoryConstructor(t) && !this.nav().hasDefaultConstructor(clazz)) {
            if (this.nav().isInnerClass(clazz)) {
                builder.reportError(new IllegalAnnotationException(Messages.CANT_HANDLE_INNER_CLASS.format(this.nav().getClassName(clazz)), this));
            }
            else if (this.elementName != null) {
                builder.reportError(new IllegalAnnotationException(Messages.NO_DEFAULT_CONSTRUCTOR.format(this.nav().getClassName(clazz)), this));
            }
        }
    }
    
    @Override
    public ClassInfoImpl<T, C, F, M> getBaseClass() {
        if (!this.baseClassComputed) {
            final C s = this.nav().getSuperClass(this.clazz);
            if (s == null || s == this.nav().asDecl(Object.class)) {
                this.baseClass = null;
            }
            else {
                final NonElement<T, C> b = this.builder.getClassInfo(s, true, this);
                if (b instanceof ClassInfoImpl) {
                    this.baseClass = (ClassInfoImpl)b;
                    this.baseClass.hasSubClasses = true;
                }
                else {
                    this.baseClass = null;
                }
            }
            this.baseClassComputed = true;
        }
        return this.baseClass;
    }
    
    @Override
    public final Element<T, C> getSubstitutionHead() {
        ClassInfoImpl<T, C, F, M> c;
        for (c = this.getBaseClass(); c != null && !c.isElement(); c = c.getBaseClass()) {}
        return c;
    }
    
    @Override
    public final C getClazz() {
        return this.clazz;
    }
    
    @Override
    @Deprecated
    public ClassInfoImpl<T, C, F, M> getScope() {
        return null;
    }
    
    @Override
    public final T getType() {
        return this.nav().use(this.clazz);
    }
    
    @Override
    public boolean canBeReferencedByIDREF() {
        for (final PropertyInfo<T, C> p : this.getProperties()) {
            if (p.id() == ID.ID) {
                return true;
            }
        }
        final ClassInfoImpl<T, C, F, M> base = this.getBaseClass();
        return base != null && base.canBeReferencedByIDREF();
    }
    
    @Override
    public final String getName() {
        return this.nav().getClassName(this.clazz);
    }
    
    public <A extends Annotation> A readAnnotation(final Class<A> a) {
        return this.reader().getClassAnnotation(a, this.clazz, this);
    }
    
    @Override
    public Element<T, C> asElement() {
        if (this.isElement()) {
            return this;
        }
        return null;
    }
    
    @Override
    public List<? extends PropertyInfo<T, C>> getProperties() {
        if (this.properties != null) {
            return this.properties;
        }
        final XmlAccessType at = this.getAccessType();
        this.properties = new FinalArrayList<PropertyInfoImpl<T, C, F, M>>();
        this.findFieldProperties(this.clazz, at);
        this.findGetterSetterProperties(at);
        if (this.propOrder == ClassInfoImpl.DEFAULT_ORDER || this.propOrder == null) {
            final XmlAccessOrder ao = this.getAccessorOrder();
            if (ao == XmlAccessOrder.ALPHABETICAL) {
                Collections.sort(this.properties);
            }
        }
        else {
            final PropertySorter sorter = new PropertySorter();
            for (final PropertyInfoImpl p : this.properties) {
                sorter.checkedGet(p);
            }
            Collections.sort(this.properties, sorter);
            sorter.checkUnusedProperties();
        }
        PropertyInfoImpl vp = null;
        PropertyInfoImpl ep = null;
        for (final PropertyInfoImpl p2 : this.properties) {
            switch (p2.kind()) {
                case ELEMENT:
                case REFERENCE:
                case MAP: {
                    ep = p2;
                    continue;
                }
                case VALUE: {
                    if (vp != null) {
                        this.builder.reportError(new IllegalAnnotationException(Messages.MULTIPLE_VALUE_PROPERTY.format(new Object[0]), vp, p2));
                    }
                    if (this.getBaseClass() != null) {
                        this.builder.reportError(new IllegalAnnotationException(Messages.XMLVALUE_IN_DERIVED_TYPE.format(new Object[0]), p2));
                    }
                    vp = p2;
                    continue;
                }
                case ATTRIBUTE: {
                    continue;
                }
                default: {
                    assert false;
                    continue;
                }
            }
        }
        if (ep != null && vp != null) {
            this.builder.reportError(new IllegalAnnotationException(Messages.ELEMENT_AND_VALUE_PROPERTY.format(new Object[0]), vp, ep));
        }
        return this.properties;
    }
    
    private void findFieldProperties(final C c, final XmlAccessType at) {
        final C sc = this.nav().getSuperClass(c);
        if (this.shouldRecurseSuperClass(sc)) {
            this.findFieldProperties(sc, at);
        }
        for (final F f : this.nav().getDeclaredFields(c)) {
            final Annotation[] annotations = this.reader().getAllFieldAnnotations(f, this);
            final boolean isDummy = this.reader().hasFieldAnnotation(OverrideAnnotationOf.class, f);
            if (this.nav().isTransient(f)) {
                if (!hasJAXBAnnotation(annotations)) {
                    continue;
                }
                this.builder.reportError(new IllegalAnnotationException(Messages.TRANSIENT_FIELD_NOT_BINDABLE.format(this.nav().getFieldName(f)), getSomeJAXBAnnotation(annotations)));
            }
            else if (this.nav().isStaticField(f)) {
                if (!hasJAXBAnnotation(annotations)) {
                    continue;
                }
                this.addProperty(this.createFieldSeed(f), annotations, false);
            }
            else {
                if (at == XmlAccessType.FIELD || (at == XmlAccessType.PUBLIC_MEMBER && this.nav().isPublicField(f)) || hasJAXBAnnotation(annotations)) {
                    if (isDummy) {
                        ClassInfo<T, C> top;
                        for (top = this.getBaseClass(); top != null && top.getProperty("content") == null; top = top.getBaseClass()) {}
                        final DummyPropertyInfo prop = (DummyPropertyInfo)top.getProperty("content");
                        final PropertySeed seed = this.createFieldSeed(f);
                        prop.addType(this.createReferenceProperty(seed));
                    }
                    else {
                        this.addProperty(this.createFieldSeed(f), annotations, false);
                    }
                }
                this.checkFieldXmlLocation(f);
            }
        }
    }
    
    @Override
    public final boolean hasValueProperty() {
        final ClassInfoImpl<T, C, F, M> bc = this.getBaseClass();
        if (bc != null && bc.hasValueProperty()) {
            return true;
        }
        for (final PropertyInfo p : this.getProperties()) {
            if (p instanceof ValuePropertyInfo) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public PropertyInfo<T, C> getProperty(final String name) {
        for (final PropertyInfo<T, C> p : this.getProperties()) {
            if (p.getName().equals(name)) {
                return p;
            }
        }
        return null;
    }
    
    protected void checkFieldXmlLocation(final F f) {
    }
    
    private <T extends Annotation> T getClassOrPackageAnnotation(final Class<T> type) {
        final T t = this.reader().getClassAnnotation(type, this.clazz, this);
        if (t != null) {
            return t;
        }
        return this.reader().getPackageAnnotation(type, this.clazz, this);
    }
    
    private XmlAccessType getAccessType() {
        final XmlAccessorType xat = this.getClassOrPackageAnnotation(XmlAccessorType.class);
        if (xat != null) {
            return xat.value();
        }
        return XmlAccessType.PUBLIC_MEMBER;
    }
    
    private XmlAccessOrder getAccessorOrder() {
        final XmlAccessorOrder xao = this.getClassOrPackageAnnotation(XmlAccessorOrder.class);
        if (xao != null) {
            return xao.value();
        }
        return XmlAccessOrder.UNDEFINED;
    }
    
    @Override
    public boolean hasProperties() {
        return !this.properties.isEmpty();
    }
    
    private static <T> T pickOne(final T... args) {
        for (final T arg : args) {
            if (arg != null) {
                return arg;
            }
        }
        return null;
    }
    
    private static <T> List<T> makeSet(final T... args) {
        final List<T> l = new FinalArrayList<T>();
        for (final T arg : args) {
            if (arg != null) {
                l.add(arg);
            }
        }
        return l;
    }
    
    private void checkConflict(final Annotation a, final Annotation b) throws DuplicateException {
        assert b != null;
        if (a != null) {
            throw new DuplicateException(a, b);
        }
    }
    
    private void addProperty(final PropertySeed<T, C, F, M> seed, final Annotation[] annotations, final boolean dummy) {
        XmlTransient t = null;
        XmlAnyAttribute aa = null;
        XmlAttribute a = null;
        XmlValue v = null;
        XmlElement e1 = null;
        XmlElements e2 = null;
        XmlElementRef r1 = null;
        XmlElementRefs r2 = null;
        XmlAnyElement xae = null;
        XmlMixed mx = null;
        OverrideAnnotationOf ov = null;
        int secondaryAnnotations = 0;
        try {
            for (final Annotation ann : annotations) {
                final Integer index = ClassInfoImpl.ANNOTATION_NUMBER_MAP.get(ann.annotationType());
                if (index != null) {
                    switch (index) {
                        case 0: {
                            this.checkConflict(t, ann);
                            t = (XmlTransient)ann;
                            break;
                        }
                        case 1: {
                            this.checkConflict(aa, ann);
                            aa = (XmlAnyAttribute)ann;
                            break;
                        }
                        case 2: {
                            this.checkConflict(a, ann);
                            a = (XmlAttribute)ann;
                            break;
                        }
                        case 3: {
                            this.checkConflict(v, ann);
                            v = (XmlValue)ann;
                            break;
                        }
                        case 4: {
                            this.checkConflict(e1, ann);
                            e1 = (XmlElement)ann;
                            break;
                        }
                        case 5: {
                            this.checkConflict(e2, ann);
                            e2 = (XmlElements)ann;
                            break;
                        }
                        case 6: {
                            this.checkConflict(r1, ann);
                            r1 = (XmlElementRef)ann;
                            break;
                        }
                        case 7: {
                            this.checkConflict(r2, ann);
                            r2 = (XmlElementRefs)ann;
                            break;
                        }
                        case 8: {
                            this.checkConflict(xae, ann);
                            xae = (XmlAnyElement)ann;
                            break;
                        }
                        case 9: {
                            this.checkConflict(mx, ann);
                            mx = (XmlMixed)ann;
                            break;
                        }
                        case 10: {
                            this.checkConflict(ov, ann);
                            ov = (OverrideAnnotationOf)ann;
                            break;
                        }
                        default: {
                            secondaryAnnotations |= 1 << index - 20;
                            break;
                        }
                    }
                }
            }
            PropertyGroup group = null;
            int groupCount = 0;
            if (t != null) {
                group = PropertyGroup.TRANSIENT;
                ++groupCount;
            }
            if (aa != null) {
                group = PropertyGroup.ANY_ATTRIBUTE;
                ++groupCount;
            }
            if (a != null) {
                group = PropertyGroup.ATTRIBUTE;
                ++groupCount;
            }
            if (v != null) {
                group = PropertyGroup.VALUE;
                ++groupCount;
            }
            if (e1 != null || e2 != null) {
                group = PropertyGroup.ELEMENT;
                ++groupCount;
            }
            if (r1 != null || r2 != null || xae != null || mx != null || ov != null) {
                group = PropertyGroup.ELEMENT_REF;
                ++groupCount;
            }
            if (groupCount > 1) {
                final List<Annotation> err = makeSet(t, aa, a, v, pickOne(e1, e2), pickOne(r1, r2, xae));
                throw new ConflictException(err);
            }
            if (group == null) {
                assert groupCount == 0;
                if (this.nav().isSubClassOf(seed.getRawType(), this.nav().ref(Map.class)) && !seed.hasAnnotation(XmlJavaTypeAdapter.class)) {
                    group = PropertyGroup.MAP;
                }
                else {
                    group = PropertyGroup.ELEMENT;
                }
            }
            else if (group.equals(PropertyGroup.ELEMENT) && this.nav().isSubClassOf(seed.getRawType(), this.nav().ref(Map.class)) && !seed.hasAnnotation(XmlJavaTypeAdapter.class)) {
                group = PropertyGroup.MAP;
            }
            if ((secondaryAnnotations & group.allowedsecondaryAnnotations) != 0x0) {
                for (final SecondaryAnnotation sa : ClassInfoImpl.SECONDARY_ANNOTATIONS) {
                    if (!group.allows(sa)) {
                        for (final Class<? extends Annotation> m : sa.members) {
                            final Annotation offender = seed.readAnnotation(m);
                            if (offender != null) {
                                this.builder.reportError(new IllegalAnnotationException(Messages.ANNOTATION_NOT_ALLOWED.format(m.getSimpleName()), offender));
                                return;
                            }
                        }
                    }
                }
                assert false;
            }
            switch (group) {
                case TRANSIENT: {
                    return;
                }
                case ANY_ATTRIBUTE: {
                    if (this.attributeWildcard != null) {
                        this.builder.reportError(new IllegalAnnotationException(Messages.TWO_ATTRIBUTE_WILDCARDS.format(this.nav().getClassName(this.getClazz())), aa, this.attributeWildcard));
                        return;
                    }
                    this.attributeWildcard = seed;
                    if (this.inheritsAttributeWildcard()) {
                        this.builder.reportError(new IllegalAnnotationException(Messages.SUPER_CLASS_HAS_WILDCARD.format(new Object[0]), aa, this.getInheritedAttributeWildcard()));
                        return;
                    }
                    if (!this.nav().isSubClassOf(seed.getRawType(), this.nav().ref(Map.class))) {
                        this.builder.reportError(new IllegalAnnotationException(Messages.INVALID_ATTRIBUTE_WILDCARD_TYPE.format(this.nav().getTypeName(seed.getRawType())), aa, this.getInheritedAttributeWildcard()));
                    }
                    return;
                }
                case ATTRIBUTE: {
                    this.properties.add(this.createAttributeProperty(seed));
                    return;
                }
                case VALUE: {
                    this.properties.add(this.createValueProperty(seed));
                    return;
                }
                case ELEMENT: {
                    this.properties.add(this.createElementProperty(seed));
                    return;
                }
                case ELEMENT_REF: {
                    this.properties.add(this.createReferenceProperty(seed));
                    return;
                }
                case MAP: {
                    this.properties.add(this.createMapProperty(seed));
                    return;
                }
                default: {
                    assert false;
                    break;
                }
            }
        }
        catch (final ConflictException x) {
            final List<Annotation> err2 = x.annotations;
            this.builder.reportError(new IllegalAnnotationException(Messages.MUTUALLY_EXCLUSIVE_ANNOTATIONS.format(this.nav().getClassName(this.getClazz()) + '#' + seed.getName(), err2.get(0).annotationType().getName(), err2.get(1).annotationType().getName()), err2.get(0), err2.get(1)));
        }
        catch (final DuplicateException e3) {
            this.builder.reportError(new IllegalAnnotationException(Messages.DUPLICATE_ANNOTATIONS.format(e3.a1.annotationType().getName()), e3.a1, e3.a2));
        }
    }
    
    protected ReferencePropertyInfoImpl<T, C, F, M> createReferenceProperty(final PropertySeed<T, C, F, M> seed) {
        return new ReferencePropertyInfoImpl<T, C, F, M>(this, seed);
    }
    
    protected AttributePropertyInfoImpl<T, C, F, M> createAttributeProperty(final PropertySeed<T, C, F, M> seed) {
        return new AttributePropertyInfoImpl<T, C, F, M>(this, seed);
    }
    
    protected ValuePropertyInfoImpl<T, C, F, M> createValueProperty(final PropertySeed<T, C, F, M> seed) {
        return new ValuePropertyInfoImpl<T, C, F, M>(this, seed);
    }
    
    protected ElementPropertyInfoImpl<T, C, F, M> createElementProperty(final PropertySeed<T, C, F, M> seed) {
        return new ElementPropertyInfoImpl<T, C, F, M>(this, seed);
    }
    
    protected MapPropertyInfoImpl<T, C, F, M> createMapProperty(final PropertySeed<T, C, F, M> seed) {
        return new MapPropertyInfoImpl<T, C, F, M>(this, seed);
    }
    
    private void findGetterSetterProperties(final XmlAccessType at) {
        final Map<String, M> getters = new LinkedHashMap<String, M>();
        final Map<String, M> setters = new LinkedHashMap<String, M>();
        C c = this.clazz;
        do {
            this.collectGetterSetters(this.clazz, getters, setters);
            c = this.nav().getSuperClass(c);
        } while (this.shouldRecurseSuperClass(c));
        final Set<String> complete = new TreeSet<String>(getters.keySet());
        complete.retainAll(setters.keySet());
        this.resurrect(getters, complete);
        this.resurrect(setters, complete);
        for (final String name : complete) {
            final M getter = getters.get(name);
            final M setter = setters.get(name);
            final Annotation[] ga = (getter != null) ? this.reader().getAllMethodAnnotations(getter, new MethodLocatable<Object>(this, getter, this.nav())) : ClassInfoImpl.EMPTY_ANNOTATIONS;
            final Annotation[] sa = (setter != null) ? this.reader().getAllMethodAnnotations(setter, new MethodLocatable<Object>(this, setter, this.nav())) : ClassInfoImpl.EMPTY_ANNOTATIONS;
            final boolean hasAnnotation = hasJAXBAnnotation(ga) || hasJAXBAnnotation(sa);
            boolean isOverriding = false;
            if (!hasAnnotation) {
                isOverriding = (getter != null && this.nav().isOverriding(getter, c) && setter != null && this.nav().isOverriding(setter, c));
            }
            if ((at == XmlAccessType.PROPERTY && !isOverriding) || (at == XmlAccessType.PUBLIC_MEMBER && this.isConsideredPublic(getter) && this.isConsideredPublic(setter) && !isOverriding) || hasAnnotation) {
                if (getter != null && setter != null && !this.nav().isSameType(this.nav().getReturnType(getter), this.nav().getMethodParameters(setter)[0])) {
                    this.builder.reportError(new IllegalAnnotationException(Messages.GETTER_SETTER_INCOMPATIBLE_TYPE.format(this.nav().getTypeName(this.nav().getReturnType(getter)), this.nav().getTypeName(this.nav().getMethodParameters(setter)[0])), new MethodLocatable<Object>(this, getter, this.nav()), new MethodLocatable<Object>(this, setter, this.nav())));
                }
                else {
                    Annotation[] r;
                    if (ga.length == 0) {
                        r = sa;
                    }
                    else if (sa.length == 0) {
                        r = ga;
                    }
                    else {
                        r = new Annotation[ga.length + sa.length];
                        System.arraycopy(ga, 0, r, 0, ga.length);
                        System.arraycopy(sa, 0, r, ga.length, sa.length);
                    }
                    this.addProperty(this.createAccessorSeed(getter, setter), r, false);
                }
            }
        }
        getters.keySet().removeAll(complete);
        setters.keySet().removeAll(complete);
    }
    
    private void collectGetterSetters(final C c, final Map<String, M> getters, final Map<String, M> setters) {
        final C sc = this.nav().getSuperClass(c);
        if (this.shouldRecurseSuperClass(sc)) {
            this.collectGetterSetters(sc, getters, setters);
        }
        final Collection<? extends M> methods = this.nav().getDeclaredMethods(c);
        final Map<String, List<M>> allSetters = new LinkedHashMap<String, List<M>>();
        for (final M method : methods) {
            boolean used = false;
            if (this.nav().isBridgeMethod(method)) {
                continue;
            }
            final String name = this.nav().getMethodName(method);
            final int arity = this.nav().getMethodParameters(method).length;
            if (this.nav().isStaticMethod(method)) {
                this.ensureNoAnnotation(method);
            }
            else {
                String propName = getPropertyNameFromGetMethod(name);
                if (propName != null && arity == 0) {
                    getters.put(propName, method);
                    used = true;
                }
                propName = getPropertyNameFromSetMethod(name);
                if (propName != null && arity == 1) {
                    List<M> propSetters = allSetters.get(propName);
                    if (null == propSetters) {
                        propSetters = new ArrayList<M>();
                        allSetters.put(propName, propSetters);
                    }
                    propSetters.add(method);
                    used = true;
                }
                if (used) {
                    continue;
                }
                this.ensureNoAnnotation(method);
            }
        }
        for (final Map.Entry<String, M> entry : getters.entrySet()) {
            final String propName2 = entry.getKey();
            final M getter = entry.getValue();
            final List<M> propSetters2 = allSetters.remove(propName2);
            if (null == propSetters2) {
                continue;
            }
            final T getterType = this.nav().getReturnType(getter);
            for (final M setter : propSetters2) {
                final T setterType = this.nav().getMethodParameters(setter)[0];
                if (this.nav().isSameType(setterType, getterType)) {
                    setters.put(propName2, setter);
                    break;
                }
            }
        }
        for (final Map.Entry<String, List<M>> e : allSetters.entrySet()) {
            setters.put(e.getKey(), e.getValue().get(0));
        }
    }
    
    private boolean shouldRecurseSuperClass(final C sc) {
        return sc != null && (this.builder.isReplaced(sc) || this.reader().hasClassAnnotation(sc, XmlTransient.class));
    }
    
    private boolean isConsideredPublic(final M m) {
        return m == null || this.nav().isPublicMethod(m);
    }
    
    private void resurrect(final Map<String, M> methods, final Set<String> complete) {
        for (final Map.Entry<String, M> e : methods.entrySet()) {
            if (complete.contains(e.getKey())) {
                continue;
            }
            if (!hasJAXBAnnotation(this.reader().getAllMethodAnnotations(e.getValue(), this))) {
                continue;
            }
            complete.add(e.getKey());
        }
    }
    
    private void ensureNoAnnotation(final M method) {
        final Annotation[] allMethodAnnotations;
        final Annotation[] annotations = allMethodAnnotations = this.reader().getAllMethodAnnotations(method, this);
        for (final Annotation a : allMethodAnnotations) {
            if (isJAXBAnnotation(a)) {
                this.builder.reportError(new IllegalAnnotationException(Messages.ANNOTATION_ON_WRONG_METHOD.format(new Object[0]), a));
                return;
            }
        }
    }
    
    private static boolean isJAXBAnnotation(final Annotation a) {
        return ClassInfoImpl.ANNOTATION_NUMBER_MAP.containsKey(a.annotationType());
    }
    
    private static boolean hasJAXBAnnotation(final Annotation[] annotations) {
        return getSomeJAXBAnnotation(annotations) != null;
    }
    
    private static Annotation getSomeJAXBAnnotation(final Annotation[] annotations) {
        for (final Annotation a : annotations) {
            if (isJAXBAnnotation(a)) {
                return a;
            }
        }
        return null;
    }
    
    private static String getPropertyNameFromGetMethod(final String name) {
        if (name.startsWith("get") && name.length() > 3) {
            return name.substring(3);
        }
        if (name.startsWith("is") && name.length() > 2) {
            return name.substring(2);
        }
        return null;
    }
    
    private static String getPropertyNameFromSetMethod(final String name) {
        if (name.startsWith("set") && name.length() > 3) {
            return name.substring(3);
        }
        return null;
    }
    
    protected PropertySeed<T, C, F, M> createFieldSeed(final F f) {
        return new FieldPropertySeed<T, C, F, M>(this, f);
    }
    
    protected PropertySeed<T, C, F, M> createAccessorSeed(final M getter, final M setter) {
        return new GetterSetterPropertySeed<T, C, F, M>(this, getter, setter);
    }
    
    @Override
    public final boolean isElement() {
        return this.elementName != null;
    }
    
    @Override
    public boolean isAbstract() {
        return this.nav().isAbstract(this.clazz);
    }
    
    @Override
    public boolean isOrdered() {
        return this.propOrder != null;
    }
    
    @Override
    public final boolean isFinal() {
        return this.nav().isFinal(this.clazz);
    }
    
    @Override
    public final boolean hasSubClasses() {
        return this.hasSubClasses;
    }
    
    @Override
    public final boolean hasAttributeWildcard() {
        return this.declaresAttributeWildcard() || this.inheritsAttributeWildcard();
    }
    
    @Override
    public final boolean inheritsAttributeWildcard() {
        return this.getInheritedAttributeWildcard() != null;
    }
    
    @Override
    public final boolean declaresAttributeWildcard() {
        return this.attributeWildcard != null;
    }
    
    private PropertySeed<T, C, F, M> getInheritedAttributeWildcard() {
        for (ClassInfoImpl<T, C, F, M> c = this.getBaseClass(); c != null; c = c.getBaseClass()) {
            if (c.attributeWildcard != null) {
                return c.attributeWildcard;
            }
        }
        return null;
    }
    
    @Override
    public final QName getElementName() {
        return this.elementName;
    }
    
    @Override
    public final QName getTypeName() {
        return this.typeName;
    }
    
    @Override
    public final boolean isSimpleType() {
        final List<? extends PropertyInfo> props = this.getProperties();
        return props.size() == 1 && ((PropertyInfo)props.get(0)).kind() == PropertyKind.VALUE;
    }
    
    @Override
    void link() {
        this.getProperties();
        final Map<String, PropertyInfoImpl> names = new HashMap<String, PropertyInfoImpl>();
        for (final PropertyInfoImpl<T, C, F, M> p : this.properties) {
            p.link();
            final PropertyInfoImpl old = names.put(p.getName(), p);
            if (old != null) {
                this.builder.reportError(new IllegalAnnotationException(Messages.PROPERTY_COLLISION.format(p.getName()), p, old));
            }
        }
        super.link();
    }
    
    @Override
    public Location getLocation() {
        return this.nav().getClassLocation(this.clazz);
    }
    
    private boolean hasFactoryConstructor(final XmlType t) {
        if (t == null) {
            return false;
        }
        final String method = t.factoryMethod();
        T fClass = this.reader().getClassValue(t, "factoryClass");
        if (method.length() > 0) {
            if (this.nav().isSameType(fClass, this.nav().ref(XmlType.DEFAULT.class))) {
                fClass = this.nav().use(this.clazz);
            }
            for (final M m : this.nav().getDeclaredMethods(this.nav().asDecl(fClass))) {
                if (this.nav().getMethodName(m).equals(method) && this.nav().isSameType(this.nav().getReturnType(m), this.nav().use(this.clazz)) && this.nav().getMethodParameters(m).length == 0 && this.nav().isStaticMethod(m)) {
                    this.factoryMethod = m;
                    break;
                }
            }
            if (this.factoryMethod == null) {
                this.builder.reportError(new IllegalAnnotationException(Messages.NO_FACTORY_METHOD.format(this.nav().getClassName(this.nav().asDecl(fClass)), method), this));
            }
        }
        else if (!this.nav().isSameType(fClass, this.nav().ref(XmlType.DEFAULT.class))) {
            this.builder.reportError(new IllegalAnnotationException(Messages.FACTORY_CLASS_NEEDS_FACTORY_METHOD.format(this.nav().getClassName(this.nav().asDecl(fClass))), this));
        }
        return this.factoryMethod != null;
    }
    
    public Method getFactoryMethod() {
        return (Method)this.factoryMethod;
    }
    
    @Override
    public String toString() {
        return "ClassInfo(" + this.clazz + ')';
    }
    
    static {
        SECONDARY_ANNOTATIONS = SecondaryAnnotation.values();
        EMPTY_ANNOTATIONS = new Annotation[0];
        ANNOTATION_NUMBER_MAP = new HashMap<Class, Integer>();
        final Class[] annotations = { XmlTransient.class, XmlAnyAttribute.class, XmlAttribute.class, XmlValue.class, XmlElement.class, XmlElements.class, XmlElementRef.class, XmlElementRefs.class, XmlAnyElement.class, XmlMixed.class, OverrideAnnotationOf.class };
        final HashMap<Class, Integer> m = ClassInfoImpl.ANNOTATION_NUMBER_MAP;
        for (final Class c : annotations) {
            m.put(c, m.size());
        }
        int index = 20;
        for (final SecondaryAnnotation sa : ClassInfoImpl.SECONDARY_ANNOTATIONS) {
            for (final Class member : sa.members) {
                m.put(member, index);
            }
            ++index;
        }
        DEFAULT_ORDER = new String[0];
    }
    
    private final class PropertySorter extends HashMap<String, Integer> implements Comparator<PropertyInfoImpl>
    {
        PropertyInfoImpl[] used;
        private Set<String> collidedNames;
        
        PropertySorter() {
            super(ClassInfoImpl.this.propOrder.length);
            this.used = new PropertyInfoImpl[ClassInfoImpl.this.propOrder.length];
            for (final String name : ClassInfoImpl.this.propOrder) {
                if (this.put(name, this.size()) != null) {
                    ClassInfoImpl.this.builder.reportError(new IllegalAnnotationException(Messages.DUPLICATE_ENTRY_IN_PROP_ORDER.format(name), ClassInfoImpl.this));
                }
            }
        }
        
        @Override
        public int compare(final PropertyInfoImpl o1, final PropertyInfoImpl o2) {
            final int lhs = this.checkedGet(o1);
            final int rhs = this.checkedGet(o2);
            return lhs - rhs;
        }
        
        private int checkedGet(final PropertyInfoImpl p) {
            Integer i = ((HashMap<K, Integer>)this).get(p.getName());
            if (i == null) {
                if (p.kind().isOrdered) {
                    ClassInfoImpl.this.builder.reportError(new IllegalAnnotationException(Messages.PROPERTY_MISSING_FROM_ORDER.format(p.getName()), p));
                }
                i = this.size();
                this.put(p.getName(), i);
            }
            final int ii = i;
            if (ii < this.used.length) {
                if (this.used[ii] != null && this.used[ii] != p) {
                    if (this.collidedNames == null) {
                        this.collidedNames = new HashSet<String>();
                    }
                    if (this.collidedNames.add(p.getName())) {
                        ClassInfoImpl.this.builder.reportError(new IllegalAnnotationException(Messages.DUPLICATE_PROPERTIES.format(p.getName()), p, this.used[ii]));
                    }
                }
                this.used[ii] = p;
            }
            return i;
        }
        
        public void checkUnusedProperties() {
            for (int i = 0; i < this.used.length; ++i) {
                if (this.used[i] == null) {
                    final String unusedName = ClassInfoImpl.this.propOrder[i];
                    final String nearest = EditDistance.findNearest(unusedName, new AbstractList<String>() {
                        @Override
                        public String get(final int index) {
                            return ((PropertyInfoImpl)ClassInfoImpl.this.properties.get(index)).getName();
                        }
                        
                        @Override
                        public int size() {
                            return ClassInfoImpl.this.properties.size();
                        }
                    });
                    final boolean isOverriding = i <= ClassInfoImpl.this.properties.size() - 1 && ((PropertyInfoImpl)ClassInfoImpl.this.properties.get(i)).hasAnnotation(OverrideAnnotationOf.class);
                    if (!isOverriding) {
                        ClassInfoImpl.this.builder.reportError(new IllegalAnnotationException(Messages.PROPERTY_ORDER_CONTAINS_UNUSED_ENTRY.format(unusedName, nearest), ClassInfoImpl.this));
                    }
                }
            }
        }
    }
    
    private static final class ConflictException extends Exception
    {
        final List<Annotation> annotations;
        
        public ConflictException(final List<Annotation> one) {
            this.annotations = one;
        }
    }
    
    private static final class DuplicateException extends Exception
    {
        final Annotation a1;
        final Annotation a2;
        
        public DuplicateException(final Annotation a1, final Annotation a2) {
            this.a1 = a1;
            this.a2 = a2;
        }
    }
    
    private enum SecondaryAnnotation
    {
        JAVA_TYPE(1, (Class<? extends Annotation>[])new Class[] { XmlJavaTypeAdapter.class }), 
        ID_IDREF(2, (Class<? extends Annotation>[])new Class[] { XmlID.class, XmlIDREF.class }), 
        BINARY(4, (Class<? extends Annotation>[])new Class[] { XmlInlineBinaryData.class, XmlMimeType.class, XmlAttachmentRef.class }), 
        ELEMENT_WRAPPER(8, (Class<? extends Annotation>[])new Class[] { XmlElementWrapper.class }), 
        LIST(16, (Class<? extends Annotation>[])new Class[] { XmlList.class }), 
        SCHEMA_TYPE(32, (Class<? extends Annotation>[])new Class[] { XmlSchemaType.class });
        
        final int bitMask;
        final Class<? extends Annotation>[] members;
        
        private SecondaryAnnotation(final int bitMask, final Class<? extends Annotation>[] members) {
            this.bitMask = bitMask;
            this.members = members;
        }
    }
    
    private enum PropertyGroup
    {
        TRANSIENT(new boolean[] { false, false, false, false, false, false }), 
        ANY_ATTRIBUTE(new boolean[] { true, false, false, false, false, false }), 
        ATTRIBUTE(new boolean[] { true, true, true, false, true, true }), 
        VALUE(new boolean[] { true, true, true, false, true, true }), 
        ELEMENT(new boolean[] { true, true, true, true, true, true }), 
        ELEMENT_REF(new boolean[] { true, false, false, true, false, false }), 
        MAP(new boolean[] { false, false, false, true, false, false });
        
        final int allowedsecondaryAnnotations;
        
        private PropertyGroup(final boolean[] bits) {
            int mask = 0;
            assert bits.length == ClassInfoImpl.SECONDARY_ANNOTATIONS.length;
            for (int i = 0; i < bits.length; ++i) {
                if (bits[i]) {
                    mask |= ClassInfoImpl.SECONDARY_ANNOTATIONS[i].bitMask;
                }
            }
            this.allowedsecondaryAnnotations = ~mask;
        }
        
        boolean allows(final SecondaryAnnotation a) {
            return (this.allowedsecondaryAnnotations & a.bitMask) == 0x0;
        }
    }
}
