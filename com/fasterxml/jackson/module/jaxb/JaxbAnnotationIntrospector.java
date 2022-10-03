package com.fasterxml.jackson.module.jaxb;

import com.fasterxml.jackson.databind.util.Converter;
import java.util.Map;
import java.util.Collection;
import java.lang.reflect.Type;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapters;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Member;
import com.fasterxml.jackson.databind.introspect.AnnotatedParameter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.lang.reflect.Field;
import java.util.HashMap;
import javax.xml.bind.annotation.XmlEnumValue;
import com.fasterxml.jackson.databind.util.ClassUtil;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.XmlAccessOrder;
import javax.xml.bind.annotation.XmlAccessorOrder;
import java.io.Closeable;
import com.fasterxml.jackson.databind.JsonMappingException;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSeeAlso;
import java.beans.Introspector;
import javax.xml.bind.JAXBElement;
import java.util.ArrayList;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import java.util.List;
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.jsontype.impl.StdTypeResolverBuilder;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlElements;
import com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
import com.fasterxml.jackson.databind.JavaType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAccessType;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.introspect.VisibilityChecker;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlTransient;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import javax.xml.bind.annotation.XmlIDREF;
import java.util.Iterator;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.annotation.SimpleObjectIdResolver;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.databind.introspect.AnnotatedField;
import com.fasterxml.jackson.databind.util.BeanUtil;
import javax.xml.bind.annotation.XmlID;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.fasterxml.jackson.databind.introspect.ObjectIdInfo;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlAttribute;
import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.module.jaxb.deser.DataHandlerJsonDeserializer;
import com.fasterxml.jackson.module.jaxb.ser.DataHandlerJsonSerializer;
import javax.xml.bind.annotation.XmlElement;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.Versioned;
import com.fasterxml.jackson.databind.AnnotationIntrospector;

public class JaxbAnnotationIntrospector extends AnnotationIntrospector implements Versioned
{
    private static final long serialVersionUID = -1L;
    protected static final String DEFAULT_NAME_FOR_XML_VALUE = "value";
    protected static final boolean DEFAULT_IGNORE_XMLIDREF = false;
    protected static final String MARKER_FOR_DEFAULT = "##default";
    protected static final JsonFormat.Value FORMAT_STRING;
    protected static final JsonFormat.Value FORMAT_INT;
    protected final String _jaxbPackageName;
    protected final JsonSerializer<?> _dataHandlerSerializer;
    protected final JsonDeserializer<?> _dataHandlerDeserializer;
    protected final TypeFactory _typeFactory;
    protected final boolean _ignoreXmlIDREF;
    protected String _xmlValueName;
    protected JsonInclude.Include _nonNillableInclusion;
    
    @Deprecated
    public JaxbAnnotationIntrospector() {
        this(TypeFactory.defaultInstance());
    }
    
    public JaxbAnnotationIntrospector(final MapperConfig<?> config) {
        this(config.getTypeFactory());
    }
    
    public JaxbAnnotationIntrospector(final TypeFactory typeFactory) {
        this(typeFactory, false);
    }
    
    public JaxbAnnotationIntrospector(final TypeFactory typeFactory, final boolean ignoreXmlIDREF) {
        this._xmlValueName = "value";
        this._nonNillableInclusion = null;
        this._typeFactory = ((typeFactory == null) ? TypeFactory.defaultInstance() : typeFactory);
        this._ignoreXmlIDREF = ignoreXmlIDREF;
        this._jaxbPackageName = XmlElement.class.getPackage().getName();
        JsonSerializer<?> dataHandlerSerializer = null;
        JsonDeserializer<?> dataHandlerDeserializer = null;
        try {
            dataHandlerSerializer = (JsonSerializer<?>)DataHandlerJsonSerializer.class.newInstance();
            dataHandlerDeserializer = (JsonDeserializer<?>)DataHandlerJsonDeserializer.class.newInstance();
        }
        catch (final Throwable t) {}
        this._dataHandlerSerializer = dataHandlerSerializer;
        this._dataHandlerDeserializer = dataHandlerDeserializer;
    }
    
    public Version version() {
        return PackageVersion.VERSION;
    }
    
    public void setNameUsedForXmlValue(final String name) {
        this._xmlValueName = name;
    }
    
    public String getNameUsedForXmlValue() {
        return this._xmlValueName;
    }
    
    public JaxbAnnotationIntrospector setNonNillableInclusion(final JsonInclude.Include incl) {
        this._nonNillableInclusion = incl;
        return this;
    }
    
    public JsonInclude.Include getNonNillableInclusion() {
        return this._nonNillableInclusion;
    }
    
    public String findNamespace(final Annotated ann) {
        String ns = null;
        if (ann instanceof AnnotatedClass) {
            final XmlRootElement elem = this.findRootElementAnnotation((AnnotatedClass)ann);
            if (elem != null) {
                ns = elem.namespace();
            }
        }
        else {
            final XmlElement elem2 = this.findAnnotation(XmlElement.class, ann, false, false, false);
            if (elem2 != null) {
                ns = elem2.namespace();
            }
            if (ns == null || "##default".equals(ns)) {
                final XmlAttribute attr = this.findAnnotation(XmlAttribute.class, ann, false, false, false);
                if (attr != null) {
                    ns = attr.namespace();
                }
            }
        }
        if ("##default".equals(ns)) {
            ns = null;
        }
        return ns;
    }
    
    public Boolean isOutputAsAttribute(final Annotated ann) {
        final XmlAttribute attr = this.findAnnotation(XmlAttribute.class, ann, false, false, false);
        if (attr != null) {
            return Boolean.TRUE;
        }
        final XmlElement elem = this.findAnnotation(XmlElement.class, ann, false, false, false);
        if (elem != null) {
            return Boolean.FALSE;
        }
        return null;
    }
    
    public Boolean isOutputAsText(final Annotated ann) {
        final XmlValue attr = this.findAnnotation(XmlValue.class, ann, false, false, false);
        if (attr != null) {
            return Boolean.TRUE;
        }
        return null;
    }
    
    public ObjectIdInfo findObjectIdInfo(final Annotated ann) {
        if (!(ann instanceof AnnotatedClass)) {
            return null;
        }
        final AnnotatedClass ac = (AnnotatedClass)ann;
        PropertyName idPropName = null;
    Label_0147:
        for (final AnnotatedMethod m : ac.memberMethods()) {
            final XmlID idProp = (XmlID)m.getAnnotation((Class)XmlID.class);
            if (idProp == null) {
                continue;
            }
            switch (m.getParameterCount()) {
                case 0: {
                    idPropName = this.findJaxbPropertyName((Annotated)m, m.getRawType(), BeanUtil.okNameForGetter(m, true));
                    break Label_0147;
                }
                case 1: {
                    idPropName = this.findJaxbPropertyName((Annotated)m, m.getRawType(), BeanUtil.okNameForMutator(m, "set", true));
                    break Label_0147;
                }
                default: {
                    continue;
                }
            }
        }
        if (idPropName == null) {
            for (final AnnotatedField f : ac.fields()) {
                final XmlID idProp = (XmlID)f.getAnnotation((Class)XmlID.class);
                if (idProp != null) {
                    idPropName = this.findJaxbPropertyName((Annotated)f, f.getRawType(), f.getName());
                    break;
                }
            }
        }
        if (idPropName != null) {
            final Class<?> scope = Object.class;
            return new ObjectIdInfo(idPropName, (Class)scope, (Class)ObjectIdGenerators.PropertyGenerator.class, (Class)SimpleObjectIdResolver.class);
        }
        return null;
    }
    
    public ObjectIdInfo findObjectReferenceInfo(final Annotated ann, ObjectIdInfo base) {
        if (!this._ignoreXmlIDREF) {
            final XmlIDREF idref = (XmlIDREF)ann.getAnnotation((Class)XmlIDREF.class);
            if (idref != null) {
                if (base == null) {
                    base = ObjectIdInfo.empty();
                }
                base = base.withAlwaysAsId(true);
            }
        }
        return base;
    }
    
    public PropertyName findRootName(final AnnotatedClass ac) {
        final XmlRootElement elem = this.findRootElementAnnotation(ac);
        if (elem != null) {
            return _combineNames(elem.name(), elem.namespace(), "");
        }
        return null;
    }
    
    public Boolean isIgnorableType(final AnnotatedClass ac) {
        return null;
    }
    
    public boolean hasIgnoreMarker(final AnnotatedMember m) {
        return m.getAnnotation((Class)XmlTransient.class) != null;
    }
    
    public Boolean hasRequiredMarker(final AnnotatedMember m) {
        final XmlAttribute attr = (XmlAttribute)m.getAnnotation((Class)XmlAttribute.class);
        if (attr != null) {
            return attr.required();
        }
        final XmlElement elem = (XmlElement)m.getAnnotation((Class)XmlElement.class);
        if (elem != null) {
            return elem.required();
        }
        return null;
    }
    
    public PropertyName findWrapperName(final Annotated ann) {
        final XmlElementWrapper w = this.findAnnotation(XmlElementWrapper.class, ann, false, false, false);
        if (w == null) {
            return null;
        }
        final PropertyName name = _combineNames(w.name(), w.namespace(), "");
        if (!name.hasSimpleName()) {
            if (ann instanceof AnnotatedMethod) {
                final AnnotatedMethod am = (AnnotatedMethod)ann;
                String str;
                if (am.getParameterCount() == 0) {
                    str = BeanUtil.okNameForGetter(am, true);
                }
                else {
                    str = BeanUtil.okNameForMutator(am, "set", true);
                }
                if (str != null) {
                    return name.withSimpleName(str);
                }
            }
            return name.withSimpleName(ann.getName());
        }
        return name;
    }
    
    public String findImplicitPropertyName(final AnnotatedMember m) {
        final XmlValue valueInfo = (XmlValue)m.getAnnotation((Class)XmlValue.class);
        if (valueInfo != null) {
            return this._xmlValueName;
        }
        return null;
    }
    
    public JsonFormat.Value findFormat(final Annotated m) {
        if (m instanceof AnnotatedClass) {
            final XmlEnum ann = (XmlEnum)m.getAnnotation((Class)XmlEnum.class);
            if (ann != null) {
                final Class<?> type = ann.value();
                if (type == String.class || type.isEnum()) {
                    return JaxbAnnotationIntrospector.FORMAT_STRING;
                }
                if (Number.class.isAssignableFrom(type)) {
                    return JaxbAnnotationIntrospector.FORMAT_INT;
                }
            }
        }
        return null;
    }
    
    public VisibilityChecker<?> findAutoDetectVisibility(final AnnotatedClass ac, final VisibilityChecker<?> checker) {
        final XmlAccessType at = this.findAccessType((Annotated)ac);
        if (at == null) {
            return checker;
        }
        switch (at) {
            case FIELD: {
                return (VisibilityChecker<?>)checker.withFieldVisibility(JsonAutoDetect.Visibility.ANY).withSetterVisibility(JsonAutoDetect.Visibility.NONE).withGetterVisibility(JsonAutoDetect.Visibility.NONE).withIsGetterVisibility(JsonAutoDetect.Visibility.NONE);
            }
            case NONE: {
                return (VisibilityChecker<?>)checker.withFieldVisibility(JsonAutoDetect.Visibility.NONE).withSetterVisibility(JsonAutoDetect.Visibility.NONE).withGetterVisibility(JsonAutoDetect.Visibility.NONE).withIsGetterVisibility(JsonAutoDetect.Visibility.NONE);
            }
            case PROPERTY: {
                return (VisibilityChecker<?>)checker.withFieldVisibility(JsonAutoDetect.Visibility.NONE).withSetterVisibility(JsonAutoDetect.Visibility.PUBLIC_ONLY).withGetterVisibility(JsonAutoDetect.Visibility.PUBLIC_ONLY).withIsGetterVisibility(JsonAutoDetect.Visibility.PUBLIC_ONLY);
            }
            case PUBLIC_MEMBER: {
                return (VisibilityChecker<?>)checker.withFieldVisibility(JsonAutoDetect.Visibility.PUBLIC_ONLY).withSetterVisibility(JsonAutoDetect.Visibility.PUBLIC_ONLY).withGetterVisibility(JsonAutoDetect.Visibility.PUBLIC_ONLY).withIsGetterVisibility(JsonAutoDetect.Visibility.PUBLIC_ONLY);
            }
            default: {
                return checker;
            }
        }
    }
    
    protected XmlAccessType findAccessType(final Annotated ac) {
        final XmlAccessorType at = this.findAnnotation(XmlAccessorType.class, ac, true, true, true);
        return (at == null) ? null : at.value();
    }
    
    public TypeResolverBuilder<?> findTypeResolver(final MapperConfig<?> config, final AnnotatedClass ac, final JavaType baseType) {
        return null;
    }
    
    public TypeResolverBuilder<?> findPropertyTypeResolver(final MapperConfig<?> config, final AnnotatedMember am, final JavaType baseType) {
        if (baseType.isContainerType()) {
            return null;
        }
        return this._typeResolverFromXmlElements(am);
    }
    
    public TypeResolverBuilder<?> findPropertyContentTypeResolver(final MapperConfig<?> config, final AnnotatedMember am, final JavaType containerType) {
        if (containerType.getContentType() == null) {
            throw new IllegalArgumentException("Must call method with a container or reference type (got " + containerType + ")");
        }
        return this._typeResolverFromXmlElements(am);
    }
    
    protected TypeResolverBuilder<?> _typeResolverFromXmlElements(final AnnotatedMember am) {
        final XmlElements elems = this.findAnnotation(XmlElements.class, (Annotated)am, false, false, false);
        final XmlElementRefs elemRefs = this.findAnnotation(XmlElementRefs.class, (Annotated)am, false, false, false);
        if (elems == null && elemRefs == null) {
            return null;
        }
        TypeResolverBuilder<?> b = (TypeResolverBuilder<?>)new StdTypeResolverBuilder();
        b = (TypeResolverBuilder<?>)b.init(JsonTypeInfo.Id.NAME, (TypeIdResolver)null);
        b = (TypeResolverBuilder<?>)b.inclusion(JsonTypeInfo.As.WRAPPER_OBJECT);
        return b;
    }
    
    public List<NamedType> findSubtypes(final Annotated a) {
        final XmlElements elems = this.findAnnotation(XmlElements.class, a, false, false, false);
        ArrayList<NamedType> result = null;
        if (elems != null) {
            result = new ArrayList<NamedType>();
            for (final XmlElement elem : elems.value()) {
                String name = elem.name();
                if ("##default".equals(name)) {
                    name = null;
                }
                result.add(new NamedType(elem.type(), name));
            }
        }
        else {
            final XmlElementRefs elemRefs = this.findAnnotation(XmlElementRefs.class, a, false, false, false);
            if (elemRefs != null) {
                result = new ArrayList<NamedType>();
                for (final XmlElementRef elemRef : elemRefs.value()) {
                    final Class<?> refType = elemRef.type();
                    if (!JAXBElement.class.isAssignableFrom(refType)) {
                        String name2 = elemRef.name();
                        if (name2 == null || "##default".equals(name2)) {
                            final XmlRootElement rootElement = refType.getAnnotation(XmlRootElement.class);
                            if (rootElement != null) {
                                name2 = rootElement.name();
                            }
                        }
                        if (name2 == null || "##default".equals(name2)) {
                            name2 = Introspector.decapitalize(refType.getSimpleName());
                        }
                        result.add(new NamedType((Class)refType, name2));
                    }
                }
            }
        }
        final XmlSeeAlso ann = (XmlSeeAlso)a.getAnnotation((Class)XmlSeeAlso.class);
        if (ann != null) {
            if (result == null) {
                result = new ArrayList<NamedType>();
            }
            for (final Class<?> cls : ann.value()) {
                result.add(new NamedType((Class)cls));
            }
        }
        return result;
    }
    
    public String findTypeName(final AnnotatedClass ac) {
        final XmlType type = this.findAnnotation(XmlType.class, (Annotated)ac, false, false, false);
        if (type != null) {
            final String name = type.name();
            if (!"##default".equals(name)) {
                return name;
            }
        }
        return null;
    }
    
    public JsonSerializer<?> findSerializer(final Annotated am) {
        final Class<?> type = this._rawSerializationType(am);
        if (type != null && this._dataHandlerSerializer != null && this.isDataHandler(type)) {
            return this._dataHandlerSerializer;
        }
        return null;
    }
    
    private boolean isDataHandler(final Class<?> type) {
        return type != null && Object.class != type && ("javax.activation.DataHandler".equals(type.getName()) || this.isDataHandler(type.getSuperclass()));
    }
    
    public Object findContentSerializer(final Annotated a) {
        return null;
    }
    
    @Deprecated
    public Class<?> findSerializationType(final Annotated a) {
        final Class<?> allegedType = this._getTypeFromXmlElement(a);
        if (allegedType != null) {
            final Class<?> rawPropType = this._rawSerializationType(a);
            if (!this.isContainerType(rawPropType)) {
                return allegedType;
            }
        }
        return null;
    }
    
    public JsonInclude.Value findPropertyInclusion(final Annotated a) {
        final JsonInclude.Include incl = this._serializationInclusion(a, null);
        if (incl == null) {
            return JsonInclude.Value.empty();
        }
        return JsonInclude.Value.construct(incl, (JsonInclude.Include)null);
    }
    
    JsonInclude.Include _serializationInclusion(final Annotated a, final JsonInclude.Include defValue) {
        final XmlElementWrapper w = (XmlElementWrapper)a.getAnnotation((Class)XmlElementWrapper.class);
        if (w != null) {
            if (w.nillable()) {
                return JsonInclude.Include.ALWAYS;
            }
            if (this._nonNillableInclusion != null) {
                return this._nonNillableInclusion;
            }
        }
        final XmlElement e = (XmlElement)a.getAnnotation((Class)XmlElement.class);
        if (e != null) {
            if (e.nillable()) {
                return JsonInclude.Include.ALWAYS;
            }
            if (this._nonNillableInclusion != null) {
                return this._nonNillableInclusion;
            }
        }
        return defValue;
    }
    
    public JavaType refineSerializationType(final MapperConfig<?> config, final Annotated a, final JavaType baseType) throws JsonMappingException {
        final Class<?> serClass = this._getTypeFromXmlElement(a);
        if (serClass == null) {
            return baseType;
        }
        final TypeFactory tf = config.getTypeFactory();
        if (baseType.getContentType() == null) {
            if (!serClass.isAssignableFrom(baseType.getRawClass())) {
                return baseType;
            }
            if (baseType.hasRawClass((Class)serClass)) {
                return baseType.withStaticTyping();
            }
            try {
                return tf.constructGeneralizedType(baseType, (Class)serClass);
            }
            catch (final IllegalArgumentException iae) {
                throw new JsonMappingException((Closeable)null, String.format("Failed to widen type %s with annotation (value %s), from '%s': %s", baseType, serClass.getName(), a.getName(), iae.getMessage()), (Throwable)iae);
            }
        }
        JavaType contentType = baseType.getContentType();
        if (contentType == null) {
            return baseType;
        }
        if (!serClass.isAssignableFrom(contentType.getRawClass())) {
            return baseType;
        }
        if (contentType.hasRawClass((Class)serClass)) {
            contentType = contentType.withStaticTyping();
        }
        else {
            try {
                contentType = tf.constructGeneralizedType(contentType, (Class)serClass);
            }
            catch (final IllegalArgumentException iae2) {
                throw new JsonMappingException((Closeable)null, String.format("Failed to widen value type of %s with concrete-type annotation (value %s), from '%s': %s", baseType, serClass.getName(), a.getName(), iae2.getMessage()), (Throwable)iae2);
            }
        }
        return baseType.withContentType(contentType);
    }
    
    public String[] findSerializationPropertyOrder(final AnnotatedClass ac) {
        final XmlType type = this.findAnnotation(XmlType.class, (Annotated)ac, true, true, true);
        if (type == null) {
            return null;
        }
        final String[] order = type.propOrder();
        if (order == null || order.length == 0) {
            return null;
        }
        return order;
    }
    
    public Boolean findSerializationSortAlphabetically(final Annotated ann) {
        return this._findAlpha(ann);
    }
    
    private final Boolean _findAlpha(final Annotated ann) {
        final XmlAccessorOrder order = this.findAnnotation(XmlAccessorOrder.class, ann, true, true, true);
        return (order == null) ? null : Boolean.valueOf(order.value() == XmlAccessOrder.ALPHABETICAL);
    }
    
    public Object findSerializationConverter(final Annotated a) {
        final Class<?> serType = this._rawSerializationType(a);
        final XmlAdapter<?, ?> adapter = this.findAdapter(a, true, serType);
        if (adapter != null) {
            return this._converter(adapter, true);
        }
        return null;
    }
    
    public Object findSerializationContentConverter(final AnnotatedMember a) {
        final Class<?> serType = this._rawSerializationType((Annotated)a);
        if (this.isContainerType(serType)) {
            final XmlAdapter<?, ?> adapter = this._findContentAdapter((Annotated)a, true);
            if (adapter != null) {
                return this._converter(adapter, true);
            }
        }
        return null;
    }
    
    public PropertyName findNameForSerialization(final Annotated a) {
        if (a instanceof AnnotatedMethod) {
            final AnnotatedMethod am = (AnnotatedMethod)a;
            return this.isVisible(am) ? this.findJaxbPropertyName((Annotated)am, am.getRawType(), BeanUtil.okNameForGetter(am, true)) : null;
        }
        if (a instanceof AnnotatedField) {
            final AnnotatedField af = (AnnotatedField)a;
            return this.isVisible(af) ? this.findJaxbPropertyName((Annotated)af, af.getRawType(), null) : null;
        }
        return null;
    }
    
    @Deprecated
    public boolean hasAsValueAnnotation(final AnnotatedMethod am) {
        return false;
    }
    
    public String[] findEnumValues(final Class<?> enumType, final Enum<?>[] enumValues, final String[] names) {
        HashMap<String, String> expl = null;
        for (final Field f : ClassUtil.getDeclaredFields((Class)enumType)) {
            if (f.isEnumConstant()) {
                final XmlEnumValue enumValue = f.getAnnotation(XmlEnumValue.class);
                if (enumValue != null) {
                    final String n = enumValue.value();
                    if (!n.isEmpty()) {
                        if (expl == null) {
                            expl = new HashMap<String, String>();
                        }
                        expl.put(f.getName(), n);
                    }
                }
            }
        }
        if (expl != null) {
            for (int i = 0, end = enumValues.length; i < end; ++i) {
                final String defName = enumValues[i].name();
                final String explValue = expl.get(defName);
                if (explValue != null) {
                    names[i] = explValue;
                }
            }
        }
        return names;
    }
    
    public Object findDeserializer(final Annotated am) {
        final Class<?> type = this._rawDeserializationType(am);
        if (type != null && this._dataHandlerDeserializer != null && this.isDataHandler(type)) {
            return this._dataHandlerDeserializer;
        }
        return null;
    }
    
    public Object findKeyDeserializer(final Annotated am) {
        return null;
    }
    
    public Object findContentDeserializer(final Annotated a) {
        return null;
    }
    
    protected Class<?> _doFindDeserializationType(final Annotated a, final JavaType baseType) {
        if (a.hasAnnotation((Class)XmlJavaTypeAdapter.class)) {
            return null;
        }
        final XmlElement annotation = this.findAnnotation(XmlElement.class, a, false, false, false);
        if (annotation != null) {
            final Class<?> type = annotation.type();
            if (type != XmlElement.DEFAULT.class) {
                return type;
            }
        }
        return null;
    }
    
    public JavaType refineDeserializationType(final MapperConfig<?> config, final Annotated a, final JavaType baseType) throws JsonMappingException {
        final Class<?> deserClass = this._getTypeFromXmlElement(a);
        if (deserClass == null) {
            return baseType;
        }
        final TypeFactory tf = config.getTypeFactory();
        if (baseType.getContentType() == null) {
            if (baseType.hasRawClass((Class)deserClass)) {
                return baseType;
            }
            if (!baseType.getRawClass().isAssignableFrom(deserClass)) {
                return baseType;
            }
            try {
                return tf.constructSpecializedType(baseType, (Class)deserClass);
            }
            catch (final IllegalArgumentException iae) {
                throw new JsonMappingException((Closeable)null, String.format("Failed to narrow type %s with annotation (value %s), from '%s': %s", baseType, deserClass.getName(), a.getName(), iae.getMessage()), (Throwable)iae);
            }
        }
        JavaType contentType = baseType.getContentType();
        if (contentType != null) {
            if (!contentType.getRawClass().isAssignableFrom(deserClass)) {
                return baseType;
            }
            try {
                contentType = tf.constructSpecializedType(contentType, (Class)deserClass);
                return baseType.withContentType(contentType);
            }
            catch (final IllegalArgumentException iae2) {
                throw new JsonMappingException((Closeable)null, String.format("Failed to narrow type %s with annotation (value %s), from '%s': %s", baseType, deserClass.getName(), a.getName(), iae2.getMessage()), (Throwable)iae2);
            }
        }
        return baseType;
    }
    
    public PropertyName findNameForDeserialization(final Annotated a) {
        if (a instanceof AnnotatedMethod) {
            final AnnotatedMethod am = (AnnotatedMethod)a;
            if (!this.isVisible(am)) {
                return null;
            }
            final Class<?> rawType = am.getRawParameterType(0);
            return this.findJaxbPropertyName((Annotated)am, rawType, BeanUtil.okNameForMutator(am, "set", true));
        }
        else {
            if (a instanceof AnnotatedField) {
                final AnnotatedField af = (AnnotatedField)a;
                return this.isVisible(af) ? this.findJaxbPropertyName((Annotated)af, af.getRawType(), null) : null;
            }
            return null;
        }
    }
    
    public Object findDeserializationConverter(final Annotated a) {
        final Class<?> deserType = this._rawDeserializationType(a);
        if (this.isContainerType(deserType)) {
            final XmlAdapter<?, ?> adapter = this.findAdapter(a, true, deserType);
            if (adapter != null) {
                return this._converter(adapter, false);
            }
        }
        else {
            final XmlAdapter<?, ?> adapter = this.findAdapter(a, true, deserType);
            if (adapter != null) {
                return this._converter(adapter, false);
            }
        }
        return null;
    }
    
    public Object findDeserializationContentConverter(final AnnotatedMember a) {
        final Class<?> deserType = this._rawDeserializationType((Annotated)a);
        if (this.isContainerType(deserType)) {
            final XmlAdapter<?, ?> adapter = this._findContentAdapter((Annotated)a, false);
            if (adapter != null) {
                return this._converter(adapter, false);
            }
        }
        return null;
    }
    
    private boolean isVisible(final AnnotatedField f) {
        for (final Annotation annotation : f.getAnnotated().getDeclaredAnnotations()) {
            if (this.isJAXBAnnotation(annotation)) {
                return true;
            }
        }
        XmlAccessType accessType = XmlAccessType.PUBLIC_MEMBER;
        final XmlAccessorType at = this.findAnnotation(XmlAccessorType.class, (Annotated)f, true, true, true);
        if (at != null) {
            accessType = at.value();
        }
        return accessType == XmlAccessType.FIELD || (accessType == XmlAccessType.PUBLIC_MEMBER && Modifier.isPublic(f.getAnnotated().getModifiers()));
    }
    
    private boolean isVisible(final AnnotatedMethod m) {
        for (final Annotation annotation : m.getAnnotated().getDeclaredAnnotations()) {
            if (this.isJAXBAnnotation(annotation)) {
                return true;
            }
        }
        XmlAccessType accessType = XmlAccessType.PUBLIC_MEMBER;
        final XmlAccessorType at = this.findAnnotation(XmlAccessorType.class, (Annotated)m, true, true, true);
        if (at != null) {
            accessType = at.value();
        }
        return (accessType == XmlAccessType.PROPERTY || accessType == XmlAccessType.PUBLIC_MEMBER) && Modifier.isPublic(m.getModifiers());
    }
    
    private <A extends Annotation> A findAnnotation(final Class<A> annotationClass, final Annotated annotated, final boolean includePackage, final boolean includeClass, final boolean includeSuperclasses) {
        A annotation = (A)annotated.getAnnotation((Class)annotationClass);
        if (annotation != null) {
            return annotation;
        }
        Class<?> memberClass = null;
        if (annotated instanceof AnnotatedParameter) {
            memberClass = ((AnnotatedParameter)annotated).getDeclaringClass();
        }
        else {
            final AnnotatedElement annType = annotated.getAnnotated();
            if (annType instanceof Member) {
                memberClass = ((Member)annType).getDeclaringClass();
                if (includeClass) {
                    annotation = memberClass.getAnnotation(annotationClass);
                    if (annotation != null) {
                        return annotation;
                    }
                }
            }
            else if (annType instanceof Class) {
                memberClass = (Class)annType;
            }
        }
        if (memberClass != null) {
            if (includeSuperclasses) {
                for (Class<?> superclass = memberClass.getSuperclass(); superclass != null && superclass != Object.class; superclass = superclass.getSuperclass()) {
                    annotation = superclass.getAnnotation(annotationClass);
                    if (annotation != null) {
                        return annotation;
                    }
                }
            }
            if (includePackage) {
                final Package pkg = memberClass.getPackage();
                if (pkg != null) {
                    return memberClass.getPackage().getAnnotation(annotationClass);
                }
            }
        }
        return null;
    }
    
    protected boolean isJAXBAnnotation(final Annotation ann) {
        final Class<?> cls = ann.annotationType();
        final Package pkg = cls.getPackage();
        final String pkgName = (pkg != null) ? pkg.getName() : cls.getName();
        return pkgName.startsWith(this._jaxbPackageName);
    }
    
    private PropertyName findJaxbPropertyName(final Annotated ae, final Class<?> aeType, final String defaultName) {
        final XmlAttribute attribute = (XmlAttribute)ae.getAnnotation((Class)XmlAttribute.class);
        if (attribute != null) {
            return _combineNames(attribute.name(), attribute.namespace(), defaultName);
        }
        final XmlElement element = (XmlElement)ae.getAnnotation((Class)XmlElement.class);
        if (element != null) {
            return _combineNames(element.name(), element.namespace(), defaultName);
        }
        final XmlElementRef elementRef = (XmlElementRef)ae.getAnnotation((Class)XmlElementRef.class);
        boolean hasAName = elementRef != null;
        if (hasAName) {
            if (!"##default".equals(elementRef.name())) {
                return _combineNames(elementRef.name(), elementRef.namespace(), defaultName);
            }
            if (aeType != null) {
                final XmlRootElement rootElement = aeType.getAnnotation(XmlRootElement.class);
                if (rootElement != null) {
                    final String name = rootElement.name();
                    if (!"##default".equals(name)) {
                        return _combineNames(name, rootElement.namespace(), defaultName);
                    }
                    return new PropertyName(Introspector.decapitalize(aeType.getSimpleName()));
                }
            }
        }
        if (!hasAName) {
            hasAName = (ae.hasAnnotation((Class)XmlElementWrapper.class) || ae.hasAnnotation((Class)XmlElements.class) || ae.hasAnnotation((Class)XmlValue.class));
        }
        return hasAName ? PropertyName.USE_DEFAULT : null;
    }
    
    private static PropertyName _combineNames(final String localName, final String namespace, final String defaultName) {
        if ("##default".equals(localName)) {
            if ("##default".equals(namespace)) {
                return new PropertyName(defaultName);
            }
            return new PropertyName(defaultName, namespace);
        }
        else {
            if ("##default".equals(namespace)) {
                return new PropertyName(localName);
            }
            return new PropertyName(localName, namespace);
        }
    }
    
    private XmlRootElement findRootElementAnnotation(final AnnotatedClass ac) {
        return this.findAnnotation(XmlRootElement.class, (Annotated)ac, true, false, true);
    }
    
    private XmlAdapter<Object, Object> findAdapter(final Annotated am, final boolean forSerialization, final Class<?> type) {
        if (am instanceof AnnotatedClass) {
            return this.findAdapterForClass((AnnotatedClass)am, forSerialization);
        }
        final XmlJavaTypeAdapter adapterInfo = this.findAnnotation(XmlJavaTypeAdapter.class, am, true, false, false);
        if (adapterInfo != null) {
            final XmlAdapter<Object, Object> adapter = this.checkAdapter(adapterInfo, type, forSerialization);
            if (adapter != null) {
                return adapter;
            }
        }
        final XmlJavaTypeAdapters adapters = this.findAnnotation(XmlJavaTypeAdapters.class, am, true, false, false);
        if (adapters != null) {
            for (final XmlJavaTypeAdapter info : adapters.value()) {
                final XmlAdapter<Object, Object> adapter2 = this.checkAdapter(info, type, forSerialization);
                if (adapter2 != null) {
                    return adapter2;
                }
            }
        }
        return null;
    }
    
    private final XmlAdapter<Object, Object> checkAdapter(final XmlJavaTypeAdapter adapterInfo, final Class<?> typeNeeded, final boolean forSerialization) {
        Class<?> adaptedType = adapterInfo.type();
        if (adaptedType == XmlJavaTypeAdapter.DEFAULT.class) {
            final JavaType type = this._typeFactory.constructType((Type)adapterInfo.value());
            final JavaType[] params = this._typeFactory.findTypeParameters(type, (Class)XmlAdapter.class);
            adaptedType = params[1].getRawClass();
        }
        if (adaptedType.isAssignableFrom(typeNeeded)) {
            final Class<? extends XmlAdapter> cls = adapterInfo.value();
            return (XmlAdapter)ClassUtil.createInstance((Class)cls, true);
        }
        return null;
    }
    
    private XmlAdapter<Object, Object> findAdapterForClass(final AnnotatedClass ac, final boolean forSerialization) {
        final XmlJavaTypeAdapter adapterInfo = ac.getAnnotated().getAnnotation(XmlJavaTypeAdapter.class);
        if (adapterInfo != null) {
            final Class<? extends XmlAdapter> cls = adapterInfo.value();
            return (XmlAdapter)ClassUtil.createInstance((Class)cls, true);
        }
        return null;
    }
    
    protected final TypeFactory getTypeFactory() {
        return this._typeFactory;
    }
    
    private boolean isContainerType(final Class<?> raw) {
        return raw.isArray() || Collection.class.isAssignableFrom(raw) || Map.class.isAssignableFrom(raw);
    }
    
    private boolean adapterTypeMatches(final XmlAdapter<?, ?> adapter, final Class<?> targetType) {
        return this.findAdapterBoundType(adapter).isAssignableFrom(targetType);
    }
    
    private Class<?> findAdapterBoundType(final XmlAdapter<?, ?> adapter) {
        final TypeFactory tf = this.getTypeFactory();
        final JavaType adapterType = tf.constructType((Type)adapter.getClass());
        final JavaType[] params = tf.findTypeParameters(adapterType, (Class)XmlAdapter.class);
        if (params == null || params.length < 2) {
            return Object.class;
        }
        return params[1].getRawClass();
    }
    
    protected XmlAdapter<?, ?> _findContentAdapter(final Annotated ann, final boolean forSerialization) {
        final Class<?> rawType = forSerialization ? this._rawSerializationType(ann) : this._rawDeserializationType(ann);
        if (this.isContainerType(rawType) && ann instanceof AnnotatedMember) {
            final AnnotatedMember member = (AnnotatedMember)ann;
            final JavaType fullType = forSerialization ? this._fullSerializationType(member) : this._fullDeserializationType(member);
            final Class<?> contentType = fullType.getContentType().getRawClass();
            final XmlAdapter<Object, Object> adapter = this.findAdapter((Annotated)member, forSerialization, contentType);
            if (adapter != null && this.adapterTypeMatches(adapter, contentType)) {
                return adapter;
            }
        }
        return null;
    }
    
    protected String _propertyNameToString(final PropertyName n) {
        return (n == null) ? null : n.getSimpleName();
    }
    
    protected Class<?> _rawDeserializationType(final Annotated a) {
        if (a instanceof AnnotatedMethod) {
            final AnnotatedMethod am = (AnnotatedMethod)a;
            if (am.getParameterCount() == 1) {
                return am.getRawParameterType(0);
            }
        }
        return a.getRawType();
    }
    
    protected JavaType _fullDeserializationType(final AnnotatedMember am) {
        if (am instanceof AnnotatedMethod) {
            final AnnotatedMethod method = (AnnotatedMethod)am;
            if (method.getParameterCount() == 1) {
                return ((AnnotatedMethod)am).getParameterType(0);
            }
        }
        return am.getType();
    }
    
    protected Class<?> _rawSerializationType(final Annotated a) {
        return a.getRawType();
    }
    
    protected JavaType _fullSerializationType(final AnnotatedMember am) {
        return am.getType();
    }
    
    protected Converter<Object, Object> _converter(final XmlAdapter<?, ?> adapter, final boolean forSerialization) {
        final TypeFactory tf = this.getTypeFactory();
        final JavaType adapterType = tf.constructType((Type)adapter.getClass());
        final JavaType[] pt = tf.findTypeParameters(adapterType, (Class)XmlAdapter.class);
        if (forSerialization) {
            return (Converter<Object, Object>)new AdapterConverter(adapter, pt[1], pt[0], forSerialization);
        }
        return (Converter<Object, Object>)new AdapterConverter(adapter, pt[0], pt[1], forSerialization);
    }
    
    protected Class<?> _getTypeFromXmlElement(final Annotated a) {
        final XmlElement annotation = this.findAnnotation(XmlElement.class, a, false, false, false);
        if (annotation != null) {
            if (a.getAnnotation((Class)XmlJavaTypeAdapter.class) != null) {
                return null;
            }
            final Class<?> type = annotation.type();
            if (type != XmlElement.DEFAULT.class) {
                return type;
            }
        }
        return null;
    }
    
    static {
        FORMAT_STRING = new JsonFormat.Value().withShape(JsonFormat.Shape.STRING);
        FORMAT_INT = new JsonFormat.Value().withShape(JsonFormat.Shape.NUMBER_INT);
    }
}
