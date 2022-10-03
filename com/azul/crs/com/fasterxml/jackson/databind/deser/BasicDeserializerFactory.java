package com.azul.crs.com.fasterxml.jackson.databind.deser;

import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.SortedMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.AbstractMap;
import java.util.NavigableSet;
import java.util.Deque;
import java.util.AbstractSet;
import java.util.AbstractList;
import java.util.Queue;
import java.util.TreeSet;
import java.util.SortedSet;
import java.util.HashSet;
import java.util.HashMap;
import com.azul.crs.com.fasterxml.jackson.databind.deser.std.TokenBufferDeserializer;
import com.azul.crs.com.fasterxml.jackson.databind.deser.std.MapEntryDeserializer;
import com.azul.crs.com.fasterxml.jackson.databind.type.TypeFactory;
import com.azul.crs.com.fasterxml.jackson.databind.deser.std.StringDeserializer;
import com.azul.crs.com.fasterxml.jackson.databind.deser.std.UntypedObjectDeserializer;
import com.azul.crs.com.fasterxml.jackson.databind.util.TokenBuffer;
import com.azul.crs.com.fasterxml.jackson.databind.deser.std.DateDeserializers;
import com.azul.crs.com.fasterxml.jackson.databind.deser.std.JdkDeserializers;
import com.azul.crs.com.fasterxml.jackson.databind.deser.std.NumberDeserializers;
import com.azul.crs.com.fasterxml.jackson.databind.util.EnumResolver;
import java.lang.reflect.Member;
import com.azul.crs.com.fasterxml.jackson.databind.deser.std.StdKeyDeserializers;
import com.azul.crs.com.fasterxml.jackson.databind.ext.OptionalHandlerFactory;
import com.azul.crs.com.fasterxml.jackson.databind.jsontype.NamedType;
import java.util.Collection;
import com.azul.crs.com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
import com.azul.crs.com.fasterxml.jackson.databind.exc.InvalidDefinitionException;
import com.azul.crs.com.fasterxml.jackson.core.JsonParser;
import com.azul.crs.com.fasterxml.jackson.databind.deser.std.AtomicReferenceDeserializer;
import java.util.concurrent.atomic.AtomicReference;
import com.azul.crs.com.fasterxml.jackson.databind.type.ReferenceType;
import com.azul.crs.com.fasterxml.jackson.databind.JsonNode;
import com.azul.crs.com.fasterxml.jackson.databind.deser.std.JsonNodeDeserializer;
import com.azul.crs.com.fasterxml.jackson.databind.MapperFeature;
import com.azul.crs.com.fasterxml.jackson.databind.deser.std.EnumDeserializer;
import com.azul.crs.com.fasterxml.jackson.databind.type.MapLikeType;
import com.azul.crs.com.fasterxml.jackson.annotation.JsonIncludeProperties;
import java.util.Set;
import com.azul.crs.com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.azul.crs.com.fasterxml.jackson.databind.deser.std.MapDeserializer;
import com.azul.crs.com.fasterxml.jackson.databind.deser.std.EnumMapDeserializer;
import java.util.EnumMap;
import com.azul.crs.com.fasterxml.jackson.databind.KeyDeserializer;
import com.azul.crs.com.fasterxml.jackson.databind.type.MapType;
import com.azul.crs.com.fasterxml.jackson.databind.type.CollectionLikeType;
import com.azul.crs.com.fasterxml.jackson.databind.deser.std.CollectionDeserializer;
import com.azul.crs.com.fasterxml.jackson.databind.deser.std.StringCollectionDeserializer;
import com.azul.crs.com.fasterxml.jackson.databind.deser.impl.JavaUtilCollectionsDeserializers;
import com.azul.crs.com.fasterxml.jackson.databind.deser.std.ArrayBlockingQueueDeserializer;
import java.util.concurrent.ArrayBlockingQueue;
import com.azul.crs.com.fasterxml.jackson.databind.deser.std.EnumSetDeserializer;
import java.util.EnumSet;
import com.azul.crs.com.fasterxml.jackson.databind.type.CollectionType;
import com.azul.crs.com.fasterxml.jackson.databind.deser.std.ObjectArrayDeserializer;
import com.azul.crs.com.fasterxml.jackson.databind.deser.std.StringArrayDeserializer;
import com.azul.crs.com.fasterxml.jackson.databind.deser.std.PrimitiveArrayDeserializers;
import com.azul.crs.com.fasterxml.jackson.databind.type.ArrayType;
import com.azul.crs.com.fasterxml.jackson.databind.cfg.ConfigOverride;
import com.azul.crs.com.fasterxml.jackson.annotation.JsonSetter;
import com.azul.crs.com.fasterxml.jackson.annotation.Nulls;
import com.azul.crs.com.fasterxml.jackson.databind.JsonDeserializer;
import com.azul.crs.com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.azul.crs.com.fasterxml.jackson.databind.BeanProperty;
import com.azul.crs.com.fasterxml.jackson.databind.PropertyMetadata;
import java.math.BigDecimal;
import java.math.BigInteger;
import com.azul.crs.com.fasterxml.jackson.databind.util.SimpleBeanPropertyDefinition;
import com.azul.crs.com.fasterxml.jackson.databind.introspect.BasicBeanDescription;
import com.azul.crs.com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.azul.crs.com.fasterxml.jackson.databind.util.NameTransformer;
import java.util.LinkedList;
import com.azul.crs.com.fasterxml.jackson.databind.introspect.POJOPropertyBuilder;
import com.azul.crs.com.fasterxml.jackson.databind.deser.impl.CreatorCandidate;
import com.azul.crs.com.fasterxml.jackson.annotation.JsonCreator;
import com.azul.crs.com.fasterxml.jackson.annotation.JacksonInject;
import com.azul.crs.com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.azul.crs.com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.azul.crs.com.fasterxml.jackson.databind.cfg.HandlerInstantiator;
import com.azul.crs.com.fasterxml.jackson.databind.util.ClassUtil;
import java.util.LinkedHashMap;
import com.azul.crs.com.fasterxml.jackson.databind.introspect.AnnotatedParameter;
import java.util.Collections;
import com.azul.crs.com.fasterxml.jackson.databind.introspect.AnnotatedConstructor;
import java.util.List;
import com.azul.crs.com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.azul.crs.com.fasterxml.jackson.databind.introspect.AnnotatedWithParams;
import java.util.Map;
import com.azul.crs.com.fasterxml.jackson.databind.cfg.ConstructorDetector;
import com.azul.crs.com.fasterxml.jackson.databind.introspect.VisibilityChecker;
import com.azul.crs.com.fasterxml.jackson.databind.jdk14.JDK14Util;
import java.util.ArrayList;
import com.azul.crs.com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.azul.crs.com.fasterxml.jackson.databind.deser.impl.CreatorCollector;
import com.azul.crs.com.fasterxml.jackson.databind.introspect.AnnotatedClass;
import com.azul.crs.com.fasterxml.jackson.databind.deser.impl.JDKValueInstantiators;
import com.azul.crs.com.fasterxml.jackson.databind.introspect.Annotated;
import com.azul.crs.com.fasterxml.jackson.databind.BeanDescription;
import com.azul.crs.com.fasterxml.jackson.databind.DeserializationContext;
import java.util.Iterator;
import com.azul.crs.com.fasterxml.jackson.databind.JsonMappingException;
import com.azul.crs.com.fasterxml.jackson.databind.JavaType;
import com.azul.crs.com.fasterxml.jackson.databind.DeserializationConfig;
import com.azul.crs.com.fasterxml.jackson.databind.AbstractTypeResolver;
import com.azul.crs.com.fasterxml.jackson.databind.cfg.DeserializerFactoryConfig;
import com.azul.crs.com.fasterxml.jackson.databind.PropertyName;
import java.io.Serializable;

public abstract class BasicDeserializerFactory extends DeserializerFactory implements Serializable
{
    private static final Class<?> CLASS_OBJECT;
    private static final Class<?> CLASS_STRING;
    private static final Class<?> CLASS_CHAR_SEQUENCE;
    private static final Class<?> CLASS_ITERABLE;
    private static final Class<?> CLASS_MAP_ENTRY;
    private static final Class<?> CLASS_SERIALIZABLE;
    protected static final PropertyName UNWRAPPED_CREATOR_PARAM_NAME;
    protected final DeserializerFactoryConfig _factoryConfig;
    
    protected BasicDeserializerFactory(final DeserializerFactoryConfig config) {
        this._factoryConfig = config;
    }
    
    public DeserializerFactoryConfig getFactoryConfig() {
        return this._factoryConfig;
    }
    
    protected abstract DeserializerFactory withConfig(final DeserializerFactoryConfig p0);
    
    @Override
    public final DeserializerFactory withAdditionalDeserializers(final Deserializers additional) {
        return this.withConfig(this._factoryConfig.withAdditionalDeserializers(additional));
    }
    
    @Override
    public final DeserializerFactory withAdditionalKeyDeserializers(final KeyDeserializers additional) {
        return this.withConfig(this._factoryConfig.withAdditionalKeyDeserializers(additional));
    }
    
    @Override
    public final DeserializerFactory withDeserializerModifier(final BeanDeserializerModifier modifier) {
        return this.withConfig(this._factoryConfig.withDeserializerModifier(modifier));
    }
    
    @Override
    public final DeserializerFactory withAbstractTypeResolver(final AbstractTypeResolver resolver) {
        return this.withConfig(this._factoryConfig.withAbstractTypeResolver(resolver));
    }
    
    @Override
    public final DeserializerFactory withValueInstantiators(final ValueInstantiators instantiators) {
        return this.withConfig(this._factoryConfig.withValueInstantiators(instantiators));
    }
    
    @Override
    public JavaType mapAbstractType(final DeserializationConfig config, JavaType type) throws JsonMappingException {
        while (true) {
            final JavaType next = this._mapAbstractType2(config, type);
            if (next == null) {
                return type;
            }
            final Class<?> prevCls = type.getRawClass();
            final Class<?> nextCls = next.getRawClass();
            if (prevCls == nextCls || !prevCls.isAssignableFrom(nextCls)) {
                throw new IllegalArgumentException("Invalid abstract type resolution from " + type + " to " + next + ": latter is not a subtype of former");
            }
            type = next;
        }
    }
    
    private JavaType _mapAbstractType2(final DeserializationConfig config, final JavaType type) throws JsonMappingException {
        final Class<?> currClass = type.getRawClass();
        if (this._factoryConfig.hasAbstractTypeResolvers()) {
            for (final AbstractTypeResolver resolver : this._factoryConfig.abstractTypeResolvers()) {
                final JavaType concrete = resolver.findTypeMapping(config, type);
                if (concrete != null && !concrete.hasRawClass(currClass)) {
                    return concrete;
                }
            }
        }
        return null;
    }
    
    @Override
    public ValueInstantiator findValueInstantiator(final DeserializationContext ctxt, final BeanDescription beanDesc) throws JsonMappingException {
        final DeserializationConfig config = ctxt.getConfig();
        ValueInstantiator instantiator = null;
        final AnnotatedClass ac = beanDesc.getClassInfo();
        final Object instDef = ctxt.getAnnotationIntrospector().findValueInstantiator(ac);
        if (instDef != null) {
            instantiator = this._valueInstantiatorInstance(config, ac, instDef);
        }
        if (instantiator == null) {
            instantiator = JDKValueInstantiators.findStdValueInstantiator(config, beanDesc.getBeanClass());
            if (instantiator == null) {
                instantiator = this._constructDefaultValueInstantiator(ctxt, beanDesc);
            }
        }
        if (this._factoryConfig.hasValueInstantiators()) {
            for (final ValueInstantiators insts : this._factoryConfig.valueInstantiators()) {
                instantiator = insts.findValueInstantiator(config, beanDesc, instantiator);
                if (instantiator == null) {
                    ctxt.reportBadTypeDefinition(beanDesc, "Broken registered ValueInstantiators (of type %s): returned null ValueInstantiator", insts.getClass().getName());
                }
            }
        }
        if (instantiator != null) {
            instantiator = instantiator.createContextual(ctxt, beanDesc);
        }
        return instantiator;
    }
    
    protected ValueInstantiator _constructDefaultValueInstantiator(final DeserializationContext ctxt, final BeanDescription beanDesc) throws JsonMappingException {
        final DeserializationConfig config = ctxt.getConfig();
        final VisibilityChecker<?> vchecker = config.getDefaultVisibilityChecker(beanDesc.getBeanClass(), beanDesc.getClassInfo());
        final ConstructorDetector ctorDetector = config.getConstructorDetector();
        final CreatorCollector creators = new CreatorCollector(beanDesc, config);
        final Map<AnnotatedWithParams, BeanPropertyDefinition[]> creatorDefs = this._findCreatorsFromProperties(ctxt, beanDesc);
        final CreatorCollectionState ccState = new CreatorCollectionState(ctxt, beanDesc, vchecker, creators, creatorDefs);
        this._addExplicitFactoryCreators(ctxt, ccState, !ctorDetector.requireCtorAnnotation());
        if (beanDesc.getType().isConcrete()) {
            if (beanDesc.getType().isRecordType()) {
                final List<String> names = new ArrayList<String>();
                final AnnotatedConstructor canonical = JDK14Util.findRecordConstructor(ctxt, beanDesc, names);
                if (canonical != null) {
                    this._addRecordConstructor(ctxt, ccState, canonical, names);
                    return ccState.creators.constructValueInstantiator(ctxt);
                }
            }
            final boolean isNonStaticInnerClass = beanDesc.isNonStaticInnerClass();
            if (!isNonStaticInnerClass) {
                final boolean findImplicit = ctorDetector.shouldIntrospectorImplicitConstructors(beanDesc.getBeanClass());
                this._addExplicitConstructorCreators(ctxt, ccState, findImplicit);
                if (ccState.hasImplicitConstructorCandidates() && !ccState.hasExplicitFactories() && !ccState.hasExplicitConstructors()) {
                    this._addImplicitConstructorCreators(ctxt, ccState, ccState.implicitConstructorCandidates());
                }
            }
        }
        if (ccState.hasImplicitFactoryCandidates() && !ccState.hasExplicitFactories() && !ccState.hasExplicitConstructors()) {
            this._addImplicitFactoryCreators(ctxt, ccState, ccState.implicitFactoryCandidates());
        }
        return ccState.creators.constructValueInstantiator(ctxt);
    }
    
    protected Map<AnnotatedWithParams, BeanPropertyDefinition[]> _findCreatorsFromProperties(final DeserializationContext ctxt, final BeanDescription beanDesc) throws JsonMappingException {
        Map<AnnotatedWithParams, BeanPropertyDefinition[]> result = Collections.emptyMap();
        for (final BeanPropertyDefinition propDef : beanDesc.findProperties()) {
            final Iterator<AnnotatedParameter> it = propDef.getConstructorParameters();
            while (it.hasNext()) {
                final AnnotatedParameter param = it.next();
                final AnnotatedWithParams owner = param.getOwner();
                BeanPropertyDefinition[] defs = result.get(owner);
                final int index = param.getIndex();
                if (defs == null) {
                    if (result.isEmpty()) {
                        result = new LinkedHashMap<AnnotatedWithParams, BeanPropertyDefinition[]>();
                    }
                    defs = new BeanPropertyDefinition[owner.getParameterCount()];
                    result.put(owner, defs);
                }
                else if (defs[index] != null) {
                    ctxt.reportBadTypeDefinition(beanDesc, "Conflict: parameter #%d of %s bound to more than one property; %s vs %s", index, owner, defs[index], propDef);
                }
                defs[index] = propDef;
            }
        }
        return result;
    }
    
    public ValueInstantiator _valueInstantiatorInstance(final DeserializationConfig config, final Annotated annotated, final Object instDef) throws JsonMappingException {
        if (instDef == null) {
            return null;
        }
        if (instDef instanceof ValueInstantiator) {
            return (ValueInstantiator)instDef;
        }
        if (!(instDef instanceof Class)) {
            throw new IllegalStateException("AnnotationIntrospector returned key deserializer definition of type " + instDef.getClass().getName() + "; expected type KeyDeserializer or Class<KeyDeserializer> instead");
        }
        final Class<?> instClass = (Class<?>)instDef;
        if (ClassUtil.isBogusClass(instClass)) {
            return null;
        }
        if (!ValueInstantiator.class.isAssignableFrom(instClass)) {
            throw new IllegalStateException("AnnotationIntrospector returned Class " + instClass.getName() + "; expected Class<ValueInstantiator>");
        }
        final HandlerInstantiator hi = config.getHandlerInstantiator();
        if (hi != null) {
            final ValueInstantiator inst = hi.valueInstantiatorInstance(config, annotated, instClass);
            if (inst != null) {
                return inst;
            }
        }
        return ClassUtil.createInstance(instClass, config.canOverrideAccessModifiers());
    }
    
    protected void _addRecordConstructor(final DeserializationContext ctxt, final CreatorCollectionState ccState, final AnnotatedConstructor canonical, final List<String> implicitNames) throws JsonMappingException {
        final int argCount = canonical.getParameterCount();
        final AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
        final SettableBeanProperty[] properties = new SettableBeanProperty[argCount];
        for (int i = 0; i < argCount; ++i) {
            final AnnotatedParameter param = canonical.getParameter(i);
            final JacksonInject.Value injectable = intr.findInjectableValue(param);
            PropertyName name = intr.findNameForDeserialization(param);
            if (name == null || name.isEmpty()) {
                name = PropertyName.construct(implicitNames.get(i));
            }
            properties[i] = this.constructCreatorProperty(ctxt, ccState.beanDesc, name, i, param, injectable);
        }
        ccState.creators.addPropertyCreator(canonical, false, properties);
    }
    
    protected void _addExplicitConstructorCreators(final DeserializationContext ctxt, final CreatorCollectionState ccState, final boolean findImplicit) throws JsonMappingException {
        final BeanDescription beanDesc = ccState.beanDesc;
        final CreatorCollector creators = ccState.creators;
        final AnnotationIntrospector intr = ccState.annotationIntrospector();
        final VisibilityChecker<?> vchecker = ccState.vchecker;
        final Map<AnnotatedWithParams, BeanPropertyDefinition[]> creatorParams = ccState.creatorParams;
        final AnnotatedConstructor defaultCtor = beanDesc.findDefaultConstructor();
        if (defaultCtor != null && (!creators.hasDefaultCreator() || this._hasCreatorAnnotation(ctxt, defaultCtor))) {
            creators.setDefaultCreator(defaultCtor);
        }
        for (final AnnotatedConstructor ctor : beanDesc.getConstructors()) {
            final JsonCreator.Mode creatorMode = intr.findCreatorAnnotation(ctxt.getConfig(), ctor);
            if (JsonCreator.Mode.DISABLED == creatorMode) {
                continue;
            }
            if (creatorMode == null) {
                if (!findImplicit || !vchecker.isCreatorVisible(ctor)) {
                    continue;
                }
                ccState.addImplicitConstructorCandidate(CreatorCandidate.construct(intr, ctor, creatorParams.get(ctor)));
            }
            else {
                switch (creatorMode) {
                    case DELEGATING: {
                        this._addExplicitDelegatingCreator(ctxt, beanDesc, creators, CreatorCandidate.construct(intr, ctor, null));
                        break;
                    }
                    case PROPERTIES: {
                        this._addExplicitPropertyCreator(ctxt, beanDesc, creators, CreatorCandidate.construct(intr, ctor, creatorParams.get(ctor)));
                        break;
                    }
                    default: {
                        this._addExplicitAnyCreator(ctxt, beanDesc, creators, CreatorCandidate.construct(intr, ctor, creatorParams.get(ctor)), ctxt.getConfig().getConstructorDetector());
                        break;
                    }
                }
                ccState.increaseExplicitConstructorCount();
            }
        }
    }
    
    protected void _addImplicitConstructorCreators(final DeserializationContext ctxt, final CreatorCollectionState ccState, final List<CreatorCandidate> ctorCandidates) throws JsonMappingException {
        final DeserializationConfig config = ctxt.getConfig();
        final BeanDescription beanDesc = ccState.beanDesc;
        final CreatorCollector creators = ccState.creators;
        final AnnotationIntrospector intr = ccState.annotationIntrospector();
        final VisibilityChecker<?> vchecker = ccState.vchecker;
        List<AnnotatedWithParams> implicitCtors = null;
        final boolean preferPropsBased = config.getConstructorDetector().singleArgCreatorDefaultsToProperties();
        for (final CreatorCandidate candidate : ctorCandidates) {
            final int argCount = candidate.paramCount();
            final AnnotatedWithParams ctor = candidate.creator();
            if (argCount == 1) {
                final BeanPropertyDefinition propDef = candidate.propertyDef(0);
                final boolean useProps = preferPropsBased || this._checkIfCreatorPropertyBased(intr, ctor, propDef);
                if (useProps) {
                    final SettableBeanProperty[] properties = { null };
                    final JacksonInject.Value injection = candidate.injection(0);
                    PropertyName name = candidate.paramName(0);
                    if (name == null) {
                        name = candidate.findImplicitParamName(0);
                        if (name == null && injection == null) {
                            continue;
                        }
                    }
                    properties[0] = this.constructCreatorProperty(ctxt, beanDesc, name, 0, candidate.parameter(0), injection);
                    creators.addPropertyCreator(ctor, false, properties);
                }
                else {
                    this._handleSingleArgumentCreator(creators, ctor, false, vchecker.isCreatorVisible(ctor));
                    if (propDef == null) {
                        continue;
                    }
                    ((POJOPropertyBuilder)propDef).removeConstructors();
                }
            }
            else {
                int nonAnnotatedParamIndex = -1;
                final SettableBeanProperty[] properties2 = new SettableBeanProperty[argCount];
                int explicitNameCount = 0;
                final int implicitWithCreatorCount = 0;
                int injectCount = 0;
                for (int i = 0; i < argCount; ++i) {
                    final AnnotatedParameter param = ctor.getParameter(i);
                    final BeanPropertyDefinition propDef2 = candidate.propertyDef(i);
                    final JacksonInject.Value injectable = intr.findInjectableValue(param);
                    final PropertyName name2 = (propDef2 == null) ? null : propDef2.getFullName();
                    if (propDef2 != null && propDef2.isExplicitlyNamed()) {
                        ++explicitNameCount;
                        properties2[i] = this.constructCreatorProperty(ctxt, beanDesc, name2, i, param, injectable);
                    }
                    else if (injectable != null) {
                        ++injectCount;
                        properties2[i] = this.constructCreatorProperty(ctxt, beanDesc, name2, i, param, injectable);
                    }
                    else {
                        final NameTransformer unwrapper = intr.findUnwrappingNameTransformer(param);
                        if (unwrapper != null) {
                            this._reportUnwrappedCreatorProperty(ctxt, beanDesc, param);
                        }
                        else if (nonAnnotatedParamIndex < 0) {
                            nonAnnotatedParamIndex = i;
                        }
                    }
                }
                final int namedCount = explicitNameCount + implicitWithCreatorCount;
                if (explicitNameCount > 0 || injectCount > 0) {
                    if (namedCount + injectCount == argCount) {
                        creators.addPropertyCreator(ctor, false, properties2);
                        continue;
                    }
                    if (explicitNameCount == 0 && injectCount + 1 == argCount) {
                        creators.addDelegatingCreator(ctor, false, properties2, 0);
                        continue;
                    }
                    final PropertyName impl = candidate.findImplicitParamName(nonAnnotatedParamIndex);
                    if (impl == null || impl.isEmpty()) {
                        ctxt.reportBadTypeDefinition(beanDesc, "Argument #%d of constructor %s has no property name annotation; must have name when multiple-parameter constructor annotated as Creator", nonAnnotatedParamIndex, ctor);
                    }
                }
                if (creators.hasDefaultCreator()) {
                    continue;
                }
                if (implicitCtors == null) {
                    implicitCtors = new LinkedList<AnnotatedWithParams>();
                }
                implicitCtors.add(ctor);
            }
        }
        if (implicitCtors != null && !creators.hasDelegatingCreator() && !creators.hasPropertyBasedCreator()) {
            this._checkImplicitlyNamedConstructors(ctxt, beanDesc, vchecker, intr, creators, implicitCtors);
        }
    }
    
    protected void _addExplicitFactoryCreators(final DeserializationContext ctxt, final CreatorCollectionState ccState, final boolean findImplicit) throws JsonMappingException {
        final BeanDescription beanDesc = ccState.beanDesc;
        final CreatorCollector creators = ccState.creators;
        final AnnotationIntrospector intr = ccState.annotationIntrospector();
        final VisibilityChecker<?> vchecker = ccState.vchecker;
        final Map<AnnotatedWithParams, BeanPropertyDefinition[]> creatorParams = ccState.creatorParams;
        for (final AnnotatedMethod factory : beanDesc.getFactoryMethods()) {
            final JsonCreator.Mode creatorMode = intr.findCreatorAnnotation(ctxt.getConfig(), factory);
            final int argCount = factory.getParameterCount();
            if (creatorMode == null) {
                if (!findImplicit || argCount != 1 || !vchecker.isCreatorVisible(factory)) {
                    continue;
                }
                ccState.addImplicitFactoryCandidate(CreatorCandidate.construct(intr, factory, null));
            }
            else {
                if (creatorMode == JsonCreator.Mode.DISABLED) {
                    continue;
                }
                if (argCount == 0) {
                    creators.setDefaultCreator(factory);
                }
                else {
                    switch (creatorMode) {
                        case DELEGATING: {
                            this._addExplicitDelegatingCreator(ctxt, beanDesc, creators, CreatorCandidate.construct(intr, factory, null));
                            break;
                        }
                        case PROPERTIES: {
                            this._addExplicitPropertyCreator(ctxt, beanDesc, creators, CreatorCandidate.construct(intr, factory, creatorParams.get(factory)));
                            break;
                        }
                        default: {
                            this._addExplicitAnyCreator(ctxt, beanDesc, creators, CreatorCandidate.construct(intr, factory, creatorParams.get(factory)), ConstructorDetector.DEFAULT);
                            break;
                        }
                    }
                    ccState.increaseExplicitFactoryCount();
                }
            }
        }
    }
    
    protected void _addImplicitFactoryCreators(final DeserializationContext ctxt, final CreatorCollectionState ccState, final List<CreatorCandidate> factoryCandidates) throws JsonMappingException {
        final BeanDescription beanDesc = ccState.beanDesc;
        final CreatorCollector creators = ccState.creators;
        final AnnotationIntrospector intr = ccState.annotationIntrospector();
        final VisibilityChecker<?> vchecker = ccState.vchecker;
        final Map<AnnotatedWithParams, BeanPropertyDefinition[]> creatorParams = ccState.creatorParams;
        for (final CreatorCandidate candidate : factoryCandidates) {
            final int argCount = candidate.paramCount();
            final AnnotatedWithParams factory = candidate.creator();
            final BeanPropertyDefinition[] propDefs = creatorParams.get(factory);
            if (argCount != 1) {
                continue;
            }
            final BeanPropertyDefinition argDef = candidate.propertyDef(0);
            final boolean useProps = this._checkIfCreatorPropertyBased(intr, factory, argDef);
            if (!useProps) {
                this._handleSingleArgumentCreator(creators, factory, false, vchecker.isCreatorVisible(factory));
                if (argDef == null) {
                    continue;
                }
                ((POJOPropertyBuilder)argDef).removeConstructors();
            }
            else {
                AnnotatedParameter nonAnnotatedParam = null;
                final SettableBeanProperty[] properties = new SettableBeanProperty[argCount];
                final int implicitNameCount = 0;
                int explicitNameCount = 0;
                int injectCount = 0;
                for (int i = 0; i < argCount; ++i) {
                    final AnnotatedParameter param = factory.getParameter(i);
                    final BeanPropertyDefinition propDef = (propDefs == null) ? null : propDefs[i];
                    final JacksonInject.Value injectable = intr.findInjectableValue(param);
                    final PropertyName name = (propDef == null) ? null : propDef.getFullName();
                    if (propDef != null && propDef.isExplicitlyNamed()) {
                        ++explicitNameCount;
                        properties[i] = this.constructCreatorProperty(ctxt, beanDesc, name, i, param, injectable);
                    }
                    else if (injectable != null) {
                        ++injectCount;
                        properties[i] = this.constructCreatorProperty(ctxt, beanDesc, name, i, param, injectable);
                    }
                    else {
                        final NameTransformer unwrapper = intr.findUnwrappingNameTransformer(param);
                        if (unwrapper != null) {
                            this._reportUnwrappedCreatorProperty(ctxt, beanDesc, param);
                        }
                        else if (nonAnnotatedParam == null) {
                            nonAnnotatedParam = param;
                        }
                    }
                }
                final int namedCount = explicitNameCount + implicitNameCount;
                if (explicitNameCount <= 0 && injectCount <= 0) {
                    continue;
                }
                if (namedCount + injectCount == argCount) {
                    creators.addPropertyCreator(factory, false, properties);
                }
                else if (explicitNameCount == 0 && injectCount + 1 == argCount) {
                    creators.addDelegatingCreator(factory, false, properties, 0);
                }
                else {
                    ctxt.reportBadTypeDefinition(beanDesc, "Argument #%d of factory method %s has no property name annotation; must have name when multiple-parameter constructor annotated as Creator", nonAnnotatedParam.getIndex(), factory);
                }
            }
        }
    }
    
    protected void _addExplicitDelegatingCreator(final DeserializationContext ctxt, final BeanDescription beanDesc, final CreatorCollector creators, final CreatorCandidate candidate) throws JsonMappingException {
        int ix = -1;
        final int argCount = candidate.paramCount();
        final SettableBeanProperty[] properties = new SettableBeanProperty[argCount];
        for (int i = 0; i < argCount; ++i) {
            final AnnotatedParameter param = candidate.parameter(i);
            final JacksonInject.Value injectId = candidate.injection(i);
            if (injectId != null) {
                properties[i] = this.constructCreatorProperty(ctxt, beanDesc, null, i, param, injectId);
            }
            else if (ix < 0) {
                ix = i;
            }
            else {
                ctxt.reportBadTypeDefinition(beanDesc, "More than one argument (#%d and #%d) left as delegating for Creator %s: only one allowed", ix, i, candidate);
            }
        }
        if (ix < 0) {
            ctxt.reportBadTypeDefinition(beanDesc, "No argument left as delegating for Creator %s: exactly one required", candidate);
        }
        if (argCount == 1) {
            this._handleSingleArgumentCreator(creators, candidate.creator(), true, true);
            final BeanPropertyDefinition paramDef = candidate.propertyDef(0);
            if (paramDef != null) {
                ((POJOPropertyBuilder)paramDef).removeConstructors();
            }
            return;
        }
        creators.addDelegatingCreator(candidate.creator(), true, properties, ix);
    }
    
    protected void _addExplicitPropertyCreator(final DeserializationContext ctxt, final BeanDescription beanDesc, final CreatorCollector creators, final CreatorCandidate candidate) throws JsonMappingException {
        final int paramCount = candidate.paramCount();
        final SettableBeanProperty[] properties = new SettableBeanProperty[paramCount];
        for (int i = 0; i < paramCount; ++i) {
            final JacksonInject.Value injectId = candidate.injection(i);
            final AnnotatedParameter param = candidate.parameter(i);
            PropertyName name = candidate.paramName(i);
            if (name == null) {
                final NameTransformer unwrapper = ctxt.getAnnotationIntrospector().findUnwrappingNameTransformer(param);
                if (unwrapper != null) {
                    this._reportUnwrappedCreatorProperty(ctxt, beanDesc, param);
                }
                name = candidate.findImplicitParamName(i);
                if (name == null && injectId == null) {
                    ctxt.reportBadTypeDefinition(beanDesc, "Argument #%d has no property name, is not Injectable: can not use as Creator %s", i, candidate);
                }
            }
            properties[i] = this.constructCreatorProperty(ctxt, beanDesc, name, i, param, injectId);
        }
        creators.addPropertyCreator(candidate.creator(), true, properties);
    }
    
    @Deprecated
    protected void _addExplicitAnyCreator(final DeserializationContext ctxt, final BeanDescription beanDesc, final CreatorCollector creators, final CreatorCandidate candidate) throws JsonMappingException {
        this._addExplicitAnyCreator(ctxt, beanDesc, creators, candidate, ctxt.getConfig().getConstructorDetector());
    }
    
    protected void _addExplicitAnyCreator(final DeserializationContext ctxt, final BeanDescription beanDesc, final CreatorCollector creators, final CreatorCandidate candidate, final ConstructorDetector ctorDetector) throws JsonMappingException {
        if (1 != candidate.paramCount()) {
            if (!ctorDetector.singleArgCreatorDefaultsToProperties()) {
                final int oneNotInjected = candidate.findOnlyParamWithoutInjection();
                if (oneNotInjected >= 0 && (ctorDetector.singleArgCreatorDefaultsToDelegating() || candidate.paramName(oneNotInjected) == null)) {
                    this._addExplicitDelegatingCreator(ctxt, beanDesc, creators, candidate);
                    return;
                }
            }
            this._addExplicitPropertyCreator(ctxt, beanDesc, creators, candidate);
            return;
        }
        final AnnotatedParameter param = candidate.parameter(0);
        final JacksonInject.Value injectId = candidate.injection(0);
        PropertyName paramName = null;
        boolean useProps = false;
        switch (ctorDetector.singleArgMode()) {
            case DELEGATING: {
                useProps = false;
                break;
            }
            case PROPERTIES: {
                useProps = true;
                paramName = candidate.paramName(0);
                break;
            }
            case REQUIRE_MODE: {
                ctxt.reportBadTypeDefinition(beanDesc, "Single-argument constructor (%s) is annotated but no 'mode' defined; `CreatorDetector`configured with `SingleArgConstructor.REQUIRE_MODE`", candidate.creator());
                return;
            }
            default: {
                final BeanPropertyDefinition paramDef = candidate.propertyDef(0);
                paramName = candidate.explicitParamName(0);
                useProps = (paramName != null || injectId != null);
                if (!useProps && paramDef != null) {
                    paramName = candidate.paramName(0);
                    useProps = (paramName != null && paramDef.couldSerialize());
                    break;
                }
                break;
            }
        }
        if (useProps) {
            final SettableBeanProperty[] properties = { this.constructCreatorProperty(ctxt, beanDesc, paramName, 0, param, injectId) };
            creators.addPropertyCreator(candidate.creator(), true, properties);
            return;
        }
        this._handleSingleArgumentCreator(creators, candidate.creator(), true, true);
        final BeanPropertyDefinition paramDef = candidate.propertyDef(0);
        if (paramDef != null) {
            ((POJOPropertyBuilder)paramDef).removeConstructors();
        }
    }
    
    private boolean _checkIfCreatorPropertyBased(final AnnotationIntrospector intr, final AnnotatedWithParams creator, final BeanPropertyDefinition propDef) {
        if ((propDef != null && propDef.isExplicitlyNamed()) || intr.findInjectableValue(creator.getParameter(0)) != null) {
            return true;
        }
        if (propDef != null) {
            final String implName = propDef.getName();
            if (implName != null && !implName.isEmpty() && propDef.couldSerialize()) {
                return true;
            }
        }
        return false;
    }
    
    private void _checkImplicitlyNamedConstructors(final DeserializationContext ctxt, final BeanDescription beanDesc, final VisibilityChecker<?> vchecker, final AnnotationIntrospector intr, final CreatorCollector creators, final List<AnnotatedWithParams> implicitCtors) throws JsonMappingException {
        AnnotatedWithParams found = null;
        SettableBeanProperty[] foundProps = null;
    Label_0015:
        for (final AnnotatedWithParams ctor : implicitCtors) {
            if (!vchecker.isCreatorVisible(ctor)) {
                continue;
            }
            final int argCount = ctor.getParameterCount();
            final SettableBeanProperty[] properties = new SettableBeanProperty[argCount];
            for (int i = 0; i < argCount; ++i) {
                final AnnotatedParameter param = ctor.getParameter(i);
                final PropertyName name = this._findParamName(param, intr);
                if (name == null) {
                    continue Label_0015;
                }
                if (name.isEmpty()) {
                    continue Label_0015;
                }
                properties[i] = this.constructCreatorProperty(ctxt, beanDesc, name, param.getIndex(), param, null);
            }
            if (found != null) {
                found = null;
                break;
            }
            found = ctor;
            foundProps = properties;
        }
        if (found != null) {
            creators.addPropertyCreator(found, false, foundProps);
            final BasicBeanDescription bbd = (BasicBeanDescription)beanDesc;
            for (final SettableBeanProperty prop : foundProps) {
                final PropertyName pn = prop.getFullName();
                if (!bbd.hasProperty(pn)) {
                    final BeanPropertyDefinition newDef = SimpleBeanPropertyDefinition.construct(ctxt.getConfig(), prop.getMember(), pn);
                    bbd.addProperty(newDef);
                }
            }
        }
    }
    
    protected boolean _handleSingleArgumentCreator(final CreatorCollector creators, final AnnotatedWithParams ctor, final boolean isCreator, final boolean isVisible) {
        final Class<?> type = ctor.getRawParameterType(0);
        if (type == String.class || type == BasicDeserializerFactory.CLASS_CHAR_SEQUENCE) {
            if (isCreator || isVisible) {
                creators.addStringCreator(ctor, isCreator);
            }
            return true;
        }
        if (type == Integer.TYPE || type == Integer.class) {
            if (isCreator || isVisible) {
                creators.addIntCreator(ctor, isCreator);
            }
            return true;
        }
        if (type == Long.TYPE || type == Long.class) {
            if (isCreator || isVisible) {
                creators.addLongCreator(ctor, isCreator);
            }
            return true;
        }
        if (type == Double.TYPE || type == Double.class) {
            if (isCreator || isVisible) {
                creators.addDoubleCreator(ctor, isCreator);
            }
            return true;
        }
        if (type == Boolean.TYPE || type == Boolean.class) {
            if (isCreator || isVisible) {
                creators.addBooleanCreator(ctor, isCreator);
            }
            return true;
        }
        if (type == BigInteger.class && (isCreator || isVisible)) {
            creators.addBigIntegerCreator(ctor, isCreator);
        }
        if (type == BigDecimal.class && (isCreator || isVisible)) {
            creators.addBigDecimalCreator(ctor, isCreator);
        }
        if (isCreator) {
            creators.addDelegatingCreator(ctor, isCreator, null, 0);
            return true;
        }
        return false;
    }
    
    protected void _reportUnwrappedCreatorProperty(final DeserializationContext ctxt, final BeanDescription beanDesc, final AnnotatedParameter param) throws JsonMappingException {
        ctxt.reportBadTypeDefinition(beanDesc, "Cannot define Creator parameter %d as `@JsonUnwrapped`: combination not yet supported", param.getIndex());
    }
    
    protected SettableBeanProperty constructCreatorProperty(final DeserializationContext ctxt, final BeanDescription beanDesc, final PropertyName name, final int index, final AnnotatedParameter param, final JacksonInject.Value injectable) throws JsonMappingException {
        final DeserializationConfig config = ctxt.getConfig();
        final AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
        PropertyMetadata metadata;
        if (intr == null) {
            metadata = PropertyMetadata.STD_REQUIRED_OR_OPTIONAL;
        }
        else {
            final Boolean b = intr.hasRequiredMarker(param);
            final String desc = intr.findPropertyDescription(param);
            final Integer idx = intr.findPropertyIndex(param);
            final String def = intr.findPropertyDefaultValue(param);
            metadata = PropertyMetadata.construct(b, desc, idx, def);
        }
        final JavaType type = this.resolveMemberAndTypeAnnotations(ctxt, param, param.getType());
        final BeanProperty.Std property = new BeanProperty.Std(name, type, intr.findWrapperName(param), param, metadata);
        TypeDeserializer typeDeser = type.getTypeHandler();
        if (typeDeser == null) {
            typeDeser = this.findTypeDeserializer(config, type);
        }
        metadata = this._getSetterInfo(ctxt, property, metadata);
        SettableBeanProperty prop = CreatorProperty.construct(name, type, property.getWrapperName(), typeDeser, beanDesc.getClassAnnotations(), param, index, injectable, metadata);
        JsonDeserializer<?> deser = this.findDeserializerFromAnnotation(ctxt, param);
        if (deser == null) {
            deser = type.getValueHandler();
        }
        if (deser != null) {
            deser = ctxt.handlePrimaryContextualization(deser, prop, type);
            prop = prop.withValueDeserializer(deser);
        }
        return prop;
    }
    
    private PropertyName _findParamName(final AnnotatedParameter param, final AnnotationIntrospector intr) {
        if (intr != null) {
            final PropertyName name = intr.findNameForDeserialization(param);
            if (name != null && !name.isEmpty()) {
                return name;
            }
            final String str = intr.findImplicitPropertyName(param);
            if (str != null && !str.isEmpty()) {
                return PropertyName.construct(str);
            }
        }
        return null;
    }
    
    protected PropertyMetadata _getSetterInfo(final DeserializationContext ctxt, final BeanProperty prop, PropertyMetadata metadata) {
        final AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
        final DeserializationConfig config = ctxt.getConfig();
        final boolean needMerge = true;
        Nulls valueNulls = null;
        Nulls contentNulls = null;
        final AnnotatedMember prim = prop.getMember();
        if (prim != null) {
            if (intr != null) {
                final JsonSetter.Value setterInfo = intr.findSetterInfo(prim);
                if (setterInfo != null) {
                    valueNulls = setterInfo.nonDefaultValueNulls();
                    contentNulls = setterInfo.nonDefaultContentNulls();
                }
            }
            if (needMerge || valueNulls == null || contentNulls == null) {
                final ConfigOverride co = config.getConfigOverride(prop.getType().getRawClass());
                final JsonSetter.Value setterInfo2 = co.getSetterInfo();
                if (setterInfo2 != null) {
                    if (valueNulls == null) {
                        valueNulls = setterInfo2.nonDefaultValueNulls();
                    }
                    if (contentNulls == null) {
                        contentNulls = setterInfo2.nonDefaultContentNulls();
                    }
                }
            }
        }
        if (needMerge || valueNulls == null || contentNulls == null) {
            final JsonSetter.Value setterInfo = config.getDefaultSetterInfo();
            if (valueNulls == null) {
                valueNulls = setterInfo.nonDefaultValueNulls();
            }
            if (contentNulls == null) {
                contentNulls = setterInfo.nonDefaultContentNulls();
            }
        }
        if (valueNulls != null || contentNulls != null) {
            metadata = metadata.withNulls(valueNulls, contentNulls);
        }
        return metadata;
    }
    
    @Override
    public JsonDeserializer<?> createArrayDeserializer(final DeserializationContext ctxt, final ArrayType type, final BeanDescription beanDesc) throws JsonMappingException {
        final DeserializationConfig config = ctxt.getConfig();
        final JavaType elemType = type.getContentType();
        final JsonDeserializer<Object> contentDeser = elemType.getValueHandler();
        TypeDeserializer elemTypeDeser = elemType.getTypeHandler();
        if (elemTypeDeser == null) {
            elemTypeDeser = this.findTypeDeserializer(config, elemType);
        }
        JsonDeserializer<?> deser = this._findCustomArrayDeserializer(type, config, beanDesc, elemTypeDeser, contentDeser);
        if (deser == null) {
            if (contentDeser == null) {
                final Class<?> raw = elemType.getRawClass();
                if (elemType.isPrimitive()) {
                    return PrimitiveArrayDeserializers.forType(raw);
                }
                if (raw == String.class) {
                    return StringArrayDeserializer.instance;
                }
            }
            deser = new ObjectArrayDeserializer(type, contentDeser, elemTypeDeser);
        }
        if (this._factoryConfig.hasDeserializerModifiers()) {
            for (final BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
                deser = mod.modifyArrayDeserializer(config, type, beanDesc, deser);
            }
        }
        return deser;
    }
    
    @Override
    public JsonDeserializer<?> createCollectionDeserializer(final DeserializationContext ctxt, CollectionType type, BeanDescription beanDesc) throws JsonMappingException {
        final JavaType contentType = type.getContentType();
        final JsonDeserializer<Object> contentDeser = contentType.getValueHandler();
        final DeserializationConfig config = ctxt.getConfig();
        TypeDeserializer contentTypeDeser = contentType.getTypeHandler();
        if (contentTypeDeser == null) {
            contentTypeDeser = this.findTypeDeserializer(config, contentType);
        }
        JsonDeserializer<?> deser = this._findCustomCollectionDeserializer(type, config, beanDesc, contentTypeDeser, contentDeser);
        if (deser == null) {
            final Class<?> collectionClass = type.getRawClass();
            if (contentDeser == null && EnumSet.class.isAssignableFrom(collectionClass)) {
                deser = new EnumSetDeserializer(contentType, null);
            }
        }
        if (deser == null) {
            if (type.isInterface() || type.isAbstract()) {
                final CollectionType implType = this._mapAbstractCollectionType(type, config);
                if (implType == null) {
                    if (type.getTypeHandler() == null) {
                        throw new IllegalArgumentException("Cannot find a deserializer for non-concrete Collection type " + type);
                    }
                    deser = AbstractDeserializer.constructForNonPOJO(beanDesc);
                }
                else {
                    type = implType;
                    beanDesc = config.introspectForCreation(type);
                }
            }
            if (deser == null) {
                final ValueInstantiator inst = this.findValueInstantiator(ctxt, beanDesc);
                if (!inst.canCreateUsingDefault()) {
                    if (type.hasRawClass(ArrayBlockingQueue.class)) {
                        return new ArrayBlockingQueueDeserializer(type, contentDeser, contentTypeDeser, inst);
                    }
                    deser = JavaUtilCollectionsDeserializers.findForCollection(ctxt, type);
                    if (deser != null) {
                        return deser;
                    }
                }
                if (contentType.hasRawClass(String.class)) {
                    deser = new StringCollectionDeserializer(type, contentDeser, inst);
                }
                else {
                    deser = new CollectionDeserializer(type, contentDeser, contentTypeDeser, inst);
                }
            }
        }
        if (this._factoryConfig.hasDeserializerModifiers()) {
            for (final BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
                deser = mod.modifyCollectionDeserializer(config, type, beanDesc, deser);
            }
        }
        return deser;
    }
    
    protected CollectionType _mapAbstractCollectionType(final JavaType type, final DeserializationConfig config) {
        final Class<?> collectionClass = ContainerDefaultMappings.findCollectionFallback(type);
        if (collectionClass != null) {
            return (CollectionType)config.getTypeFactory().constructSpecializedType(type, collectionClass, true);
        }
        return null;
    }
    
    @Override
    public JsonDeserializer<?> createCollectionLikeDeserializer(final DeserializationContext ctxt, final CollectionLikeType type, final BeanDescription beanDesc) throws JsonMappingException {
        final JavaType contentType = type.getContentType();
        final JsonDeserializer<Object> contentDeser = contentType.getValueHandler();
        final DeserializationConfig config = ctxt.getConfig();
        TypeDeserializer contentTypeDeser = contentType.getTypeHandler();
        if (contentTypeDeser == null) {
            contentTypeDeser = this.findTypeDeserializer(config, contentType);
        }
        JsonDeserializer<?> deser = this._findCustomCollectionLikeDeserializer(type, config, beanDesc, contentTypeDeser, contentDeser);
        if (deser != null && this._factoryConfig.hasDeserializerModifiers()) {
            for (final BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
                deser = mod.modifyCollectionLikeDeserializer(config, type, beanDesc, deser);
            }
        }
        return deser;
    }
    
    @Override
    public JsonDeserializer<?> createMapDeserializer(final DeserializationContext ctxt, MapType type, BeanDescription beanDesc) throws JsonMappingException {
        final DeserializationConfig config = ctxt.getConfig();
        final JavaType keyType = type.getKeyType();
        final JavaType contentType = type.getContentType();
        final JsonDeserializer<Object> contentDeser = contentType.getValueHandler();
        final KeyDeserializer keyDes = keyType.getValueHandler();
        TypeDeserializer contentTypeDeser = contentType.getTypeHandler();
        if (contentTypeDeser == null) {
            contentTypeDeser = this.findTypeDeserializer(config, contentType);
        }
        JsonDeserializer<?> deser = this._findCustomMapDeserializer(type, config, beanDesc, keyDes, contentTypeDeser, contentDeser);
        if (deser == null) {
            Class<?> mapClass = type.getRawClass();
            if (EnumMap.class.isAssignableFrom(mapClass)) {
                ValueInstantiator inst;
                if (mapClass == EnumMap.class) {
                    inst = null;
                }
                else {
                    inst = this.findValueInstantiator(ctxt, beanDesc);
                }
                if (!keyType.isEnumImplType()) {
                    throw new IllegalArgumentException("Cannot construct EnumMap; generic (key) type not available");
                }
                deser = new EnumMapDeserializer(type, inst, null, contentDeser, contentTypeDeser, null);
            }
            if (deser == null) {
                if (type.isInterface() || type.isAbstract()) {
                    final MapType fallback = this._mapAbstractMapType(type, config);
                    if (fallback != null) {
                        type = fallback;
                        mapClass = type.getRawClass();
                        beanDesc = config.introspectForCreation(type);
                    }
                    else {
                        if (type.getTypeHandler() == null) {
                            throw new IllegalArgumentException("Cannot find a deserializer for non-concrete Map type " + type);
                        }
                        deser = AbstractDeserializer.constructForNonPOJO(beanDesc);
                    }
                }
                else {
                    deser = JavaUtilCollectionsDeserializers.findForMap(ctxt, type);
                    if (deser != null) {
                        return deser;
                    }
                }
                if (deser == null) {
                    final ValueInstantiator inst = this.findValueInstantiator(ctxt, beanDesc);
                    final MapDeserializer md = new MapDeserializer(type, inst, keyDes, contentDeser, contentTypeDeser);
                    final JsonIgnoreProperties.Value ignorals = config.getDefaultPropertyIgnorals(Map.class, beanDesc.getClassInfo());
                    final Set<String> ignored = (ignorals == null) ? null : ignorals.findIgnoredForDeserialization();
                    md.setIgnorableProperties(ignored);
                    final JsonIncludeProperties.Value inclusions = config.getDefaultPropertyInclusions(Map.class, beanDesc.getClassInfo());
                    final Set<String> included = (inclusions == null) ? null : inclusions.getIncluded();
                    md.setIncludableProperties(included);
                    deser = md;
                }
            }
        }
        if (this._factoryConfig.hasDeserializerModifiers()) {
            for (final BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
                deser = mod.modifyMapDeserializer(config, type, beanDesc, deser);
            }
        }
        return deser;
    }
    
    protected MapType _mapAbstractMapType(final JavaType type, final DeserializationConfig config) {
        final Class<?> mapClass = ContainerDefaultMappings.findMapFallback(type);
        if (mapClass != null) {
            return (MapType)config.getTypeFactory().constructSpecializedType(type, mapClass, true);
        }
        return null;
    }
    
    @Override
    public JsonDeserializer<?> createMapLikeDeserializer(final DeserializationContext ctxt, final MapLikeType type, final BeanDescription beanDesc) throws JsonMappingException {
        final JavaType keyType = type.getKeyType();
        final JavaType contentType = type.getContentType();
        final DeserializationConfig config = ctxt.getConfig();
        final JsonDeserializer<Object> contentDeser = contentType.getValueHandler();
        final KeyDeserializer keyDes = keyType.getValueHandler();
        TypeDeserializer contentTypeDeser = contentType.getTypeHandler();
        if (contentTypeDeser == null) {
            contentTypeDeser = this.findTypeDeserializer(config, contentType);
        }
        JsonDeserializer<?> deser = this._findCustomMapLikeDeserializer(type, config, beanDesc, keyDes, contentTypeDeser, contentDeser);
        if (deser != null && this._factoryConfig.hasDeserializerModifiers()) {
            for (final BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
                deser = mod.modifyMapLikeDeserializer(config, type, beanDesc, deser);
            }
        }
        return deser;
    }
    
    @Override
    public JsonDeserializer<?> createEnumDeserializer(final DeserializationContext ctxt, final JavaType type, final BeanDescription beanDesc) throws JsonMappingException {
        final DeserializationConfig config = ctxt.getConfig();
        final Class<?> enumClass = type.getRawClass();
        JsonDeserializer<?> deser = this._findCustomEnumDeserializer(enumClass, config, beanDesc);
        if (deser == null) {
            if (enumClass == Enum.class) {
                return AbstractDeserializer.constructForNonPOJO(beanDesc);
            }
            final ValueInstantiator valueInstantiator = this._constructDefaultValueInstantiator(ctxt, beanDesc);
            final SettableBeanProperty[] creatorProps = (SettableBeanProperty[])((valueInstantiator == null) ? null : valueInstantiator.getFromObjectArguments(ctxt.getConfig()));
            for (final AnnotatedMethod factory : beanDesc.getFactoryMethods()) {
                if (this._hasCreatorAnnotation(ctxt, factory)) {
                    if (factory.getParameterCount() == 0) {
                        deser = EnumDeserializer.deserializerForNoArgsCreator(config, enumClass, factory);
                        break;
                    }
                    final Class<?> returnType = factory.getRawReturnType();
                    if (!returnType.isAssignableFrom(enumClass)) {
                        ctxt.reportBadDefinition(type, String.format("Invalid `@JsonCreator` annotated Enum factory method [%s]: needs to return compatible type", factory.toString()));
                    }
                    deser = EnumDeserializer.deserializerForCreator(config, enumClass, factory, valueInstantiator, creatorProps);
                    break;
                }
            }
            if (deser == null) {
                deser = new EnumDeserializer(this.constructEnumResolver(enumClass, config, beanDesc.findJsonValueAccessor()), config.isEnabled(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS));
            }
        }
        if (this._factoryConfig.hasDeserializerModifiers()) {
            for (final BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
                deser = mod.modifyEnumDeserializer(config, type, beanDesc, deser);
            }
        }
        return deser;
    }
    
    @Override
    public JsonDeserializer<?> createTreeDeserializer(final DeserializationConfig config, final JavaType nodeType, final BeanDescription beanDesc) throws JsonMappingException {
        final Class<? extends JsonNode> nodeClass = (Class<? extends JsonNode>)nodeType.getRawClass();
        final JsonDeserializer<?> custom = this._findCustomTreeNodeDeserializer(nodeClass, config, beanDesc);
        if (custom != null) {
            return custom;
        }
        return JsonNodeDeserializer.getDeserializer(nodeClass);
    }
    
    @Override
    public JsonDeserializer<?> createReferenceDeserializer(final DeserializationContext ctxt, final ReferenceType type, final BeanDescription beanDesc) throws JsonMappingException {
        final JavaType contentType = type.getContentType();
        final JsonDeserializer<Object> contentDeser = contentType.getValueHandler();
        final DeserializationConfig config = ctxt.getConfig();
        TypeDeserializer contentTypeDeser = contentType.getTypeHandler();
        if (contentTypeDeser == null) {
            contentTypeDeser = this.findTypeDeserializer(config, contentType);
        }
        JsonDeserializer<?> deser = this._findCustomReferenceDeserializer(type, config, beanDesc, contentTypeDeser, contentDeser);
        if (deser == null && type.isTypeOrSubTypeOf(AtomicReference.class)) {
            final Class<?> rawType = type.getRawClass();
            ValueInstantiator inst;
            if (rawType == AtomicReference.class) {
                inst = null;
            }
            else {
                inst = this.findValueInstantiator(ctxt, beanDesc);
            }
            return new AtomicReferenceDeserializer(type, inst, contentTypeDeser, contentDeser);
        }
        if (deser != null && this._factoryConfig.hasDeserializerModifiers()) {
            for (final BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
                deser = mod.modifyReferenceDeserializer(config, type, beanDesc, deser);
            }
        }
        return deser;
    }
    
    @Override
    public TypeDeserializer findTypeDeserializer(final DeserializationConfig config, final JavaType baseType) throws JsonMappingException {
        final BeanDescription bean = config.introspectClassAnnotations(baseType.getRawClass());
        final AnnotatedClass ac = bean.getClassInfo();
        final AnnotationIntrospector ai = config.getAnnotationIntrospector();
        TypeResolverBuilder<?> b = ai.findTypeResolver(config, ac, baseType);
        Collection<NamedType> subtypes = null;
        if (b == null) {
            b = config.getDefaultTyper(baseType);
            if (b == null) {
                return null;
            }
        }
        else {
            subtypes = config.getSubtypeResolver().collectAndResolveSubtypesByTypeId(config, ac);
        }
        if (b.getDefaultImpl() == null && baseType.isAbstract()) {
            final JavaType defaultType = this.mapAbstractType(config, baseType);
            if (defaultType != null && !defaultType.hasRawClass(baseType.getRawClass())) {
                b = (TypeResolverBuilder<?>)b.defaultImpl(defaultType.getRawClass());
            }
        }
        try {
            return b.buildTypeDeserializer(config, baseType, subtypes);
        }
        catch (final IllegalArgumentException e0) {
            final InvalidDefinitionException e2 = InvalidDefinitionException.from((JsonParser)null, ClassUtil.exceptionMessage(e0), baseType);
            e2.initCause(e0);
            throw e2;
        }
    }
    
    protected JsonDeserializer<?> findOptionalStdDeserializer(final DeserializationContext ctxt, final JavaType type, final BeanDescription beanDesc) throws JsonMappingException {
        return OptionalHandlerFactory.instance.findDeserializer(type, ctxt.getConfig(), beanDesc);
    }
    
    @Override
    public KeyDeserializer createKeyDeserializer(final DeserializationContext ctxt, final JavaType type) throws JsonMappingException {
        final DeserializationConfig config = ctxt.getConfig();
        BeanDescription beanDesc = null;
        KeyDeserializer deser = null;
        if (this._factoryConfig.hasKeyDeserializers()) {
            beanDesc = config.introspectClassAnnotations(type);
            for (final KeyDeserializers d : this._factoryConfig.keyDeserializers()) {
                deser = d.findKeyDeserializer(type, config, beanDesc);
                if (deser != null) {
                    break;
                }
            }
        }
        if (deser == null) {
            if (beanDesc == null) {
                beanDesc = config.introspectClassAnnotations(type.getRawClass());
            }
            deser = this.findKeyDeserializerFromAnnotation(ctxt, beanDesc.getClassInfo());
            if (deser == null) {
                if (type.isEnumType()) {
                    deser = this._createEnumKeyDeserializer(ctxt, type);
                }
                else {
                    deser = StdKeyDeserializers.findStringBasedKeyDeserializer(config, type);
                }
            }
        }
        if (deser != null && this._factoryConfig.hasDeserializerModifiers()) {
            for (final BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
                deser = mod.modifyKeyDeserializer(config, type, deser);
            }
        }
        return deser;
    }
    
    private KeyDeserializer _createEnumKeyDeserializer(final DeserializationContext ctxt, final JavaType type) throws JsonMappingException {
        final DeserializationConfig config = ctxt.getConfig();
        final Class<?> enumClass = type.getRawClass();
        final BeanDescription beanDesc = config.introspect(type);
        final KeyDeserializer des = this.findKeyDeserializerFromAnnotation(ctxt, beanDesc.getClassInfo());
        if (des != null) {
            return des;
        }
        final JsonDeserializer<?> custom = this._findCustomEnumDeserializer(enumClass, config, beanDesc);
        if (custom != null) {
            return StdKeyDeserializers.constructDelegatingKeyDeserializer(config, type, custom);
        }
        final JsonDeserializer<?> valueDesForKey = this.findDeserializerFromAnnotation(ctxt, beanDesc.getClassInfo());
        if (valueDesForKey != null) {
            return StdKeyDeserializers.constructDelegatingKeyDeserializer(config, type, valueDesForKey);
        }
        final EnumResolver enumRes = this.constructEnumResolver(enumClass, config, beanDesc.findJsonValueAccessor());
        for (final AnnotatedMethod factory : beanDesc.getFactoryMethods()) {
            if (this._hasCreatorAnnotation(ctxt, factory)) {
                final int argCount = factory.getParameterCount();
                if (argCount == 1) {
                    final Class<?> returnType = factory.getRawReturnType();
                    if (returnType.isAssignableFrom(enumClass)) {
                        if (factory.getRawParameterType(0) != String.class) {
                            continue;
                        }
                        if (config.canOverrideAccessModifiers()) {
                            ClassUtil.checkAndFixAccess(factory.getMember(), ctxt.isEnabled(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS));
                        }
                        return StdKeyDeserializers.constructEnumKeyDeserializer(enumRes, factory);
                    }
                }
                throw new IllegalArgumentException("Unsuitable method (" + factory + ") decorated with @JsonCreator (for Enum type " + enumClass.getName() + ")");
            }
        }
        return StdKeyDeserializers.constructEnumKeyDeserializer(enumRes);
    }
    
    @Override
    public boolean hasExplicitDeserializerFor(final DeserializationConfig config, Class<?> valueType) {
        while (valueType.isArray()) {
            valueType = valueType.getComponentType();
        }
        if (Enum.class.isAssignableFrom(valueType)) {
            return true;
        }
        final String clsName = valueType.getName();
        if (clsName.startsWith("java.")) {
            if (Collection.class.isAssignableFrom(valueType)) {
                return true;
            }
            if (Map.class.isAssignableFrom(valueType)) {
                return true;
            }
            if (Number.class.isAssignableFrom(valueType)) {
                return NumberDeserializers.find(valueType, clsName) != null;
            }
            return JdkDeserializers.hasDeserializerFor(valueType) || valueType == BasicDeserializerFactory.CLASS_STRING || valueType == Boolean.class || valueType == EnumMap.class || valueType == AtomicReference.class || DateDeserializers.hasDeserializerFor(valueType);
        }
        else {
            if (clsName.startsWith("com.fasterxml.")) {
                return JsonNode.class.isAssignableFrom(valueType) || valueType == TokenBuffer.class;
            }
            return OptionalHandlerFactory.instance.hasDeserializerFor(valueType);
        }
    }
    
    public TypeDeserializer findPropertyTypeDeserializer(final DeserializationConfig config, final JavaType baseType, final AnnotatedMember annotated) throws JsonMappingException {
        final AnnotationIntrospector ai = config.getAnnotationIntrospector();
        final TypeResolverBuilder<?> b = ai.findPropertyTypeResolver(config, annotated, baseType);
        if (b == null) {
            return this.findTypeDeserializer(config, baseType);
        }
        final Collection<NamedType> subtypes = config.getSubtypeResolver().collectAndResolveSubtypesByTypeId(config, annotated, baseType);
        try {
            return b.buildTypeDeserializer(config, baseType, subtypes);
        }
        catch (final IllegalArgumentException e0) {
            final InvalidDefinitionException e2 = InvalidDefinitionException.from((JsonParser)null, ClassUtil.exceptionMessage(e0), baseType);
            e2.initCause(e0);
            throw e2;
        }
    }
    
    public TypeDeserializer findPropertyContentTypeDeserializer(final DeserializationConfig config, final JavaType containerType, final AnnotatedMember propertyEntity) throws JsonMappingException {
        final AnnotationIntrospector ai = config.getAnnotationIntrospector();
        final TypeResolverBuilder<?> b = ai.findPropertyContentTypeResolver(config, propertyEntity, containerType);
        final JavaType contentType = containerType.getContentType();
        if (b == null) {
            return this.findTypeDeserializer(config, contentType);
        }
        final Collection<NamedType> subtypes = config.getSubtypeResolver().collectAndResolveSubtypesByTypeId(config, propertyEntity, contentType);
        return b.buildTypeDeserializer(config, contentType, subtypes);
    }
    
    public JsonDeserializer<?> findDefaultDeserializer(final DeserializationContext ctxt, final JavaType type, final BeanDescription beanDesc) throws JsonMappingException {
        final Class<?> rawType = type.getRawClass();
        if (rawType == BasicDeserializerFactory.CLASS_OBJECT || rawType == BasicDeserializerFactory.CLASS_SERIALIZABLE) {
            final DeserializationConfig config = ctxt.getConfig();
            JavaType lt;
            JavaType mt;
            if (this._factoryConfig.hasAbstractTypeResolvers()) {
                lt = this._findRemappedType(config, List.class);
                mt = this._findRemappedType(config, Map.class);
            }
            else {
                mt = (lt = null);
            }
            return new UntypedObjectDeserializer(lt, mt);
        }
        if (rawType == BasicDeserializerFactory.CLASS_STRING || rawType == BasicDeserializerFactory.CLASS_CHAR_SEQUENCE) {
            return StringDeserializer.instance;
        }
        if (rawType == BasicDeserializerFactory.CLASS_ITERABLE) {
            final TypeFactory tf = ctxt.getTypeFactory();
            final JavaType[] tps = tf.findTypeParameters(type, BasicDeserializerFactory.CLASS_ITERABLE);
            final JavaType elemType = (tps == null || tps.length != 1) ? TypeFactory.unknownType() : tps[0];
            final CollectionType ct = tf.constructCollectionType(Collection.class, elemType);
            return this.createCollectionDeserializer(ctxt, ct, beanDesc);
        }
        if (rawType == BasicDeserializerFactory.CLASS_MAP_ENTRY) {
            final JavaType kt = type.containedTypeOrUnknown(0);
            final JavaType vt = type.containedTypeOrUnknown(1);
            TypeDeserializer vts = vt.getTypeHandler();
            if (vts == null) {
                vts = this.findTypeDeserializer(ctxt.getConfig(), vt);
            }
            final JsonDeserializer<Object> valueDeser = vt.getValueHandler();
            final KeyDeserializer keyDes = kt.getValueHandler();
            return new MapEntryDeserializer(type, keyDes, valueDeser, vts);
        }
        final String clsName = rawType.getName();
        if (rawType.isPrimitive() || clsName.startsWith("java.")) {
            JsonDeserializer<?> deser = NumberDeserializers.find(rawType, clsName);
            if (deser == null) {
                deser = DateDeserializers.find(rawType, clsName);
            }
            if (deser != null) {
                return deser;
            }
        }
        if (rawType == TokenBuffer.class) {
            return new TokenBufferDeserializer();
        }
        JsonDeserializer<?> deser = this.findOptionalStdDeserializer(ctxt, type, beanDesc);
        if (deser != null) {
            return deser;
        }
        return JdkDeserializers.find(rawType, clsName);
    }
    
    protected JavaType _findRemappedType(final DeserializationConfig config, final Class<?> rawType) throws JsonMappingException {
        final JavaType type = this.mapAbstractType(config, config.constructType(rawType));
        return (type == null || type.hasRawClass(rawType)) ? null : type;
    }
    
    protected JsonDeserializer<?> _findCustomTreeNodeDeserializer(final Class<? extends JsonNode> type, final DeserializationConfig config, final BeanDescription beanDesc) throws JsonMappingException {
        for (final Deserializers d : this._factoryConfig.deserializers()) {
            final JsonDeserializer<?> deser = d.findTreeNodeDeserializer(type, config, beanDesc);
            if (deser != null) {
                return deser;
            }
        }
        return null;
    }
    
    protected JsonDeserializer<?> _findCustomReferenceDeserializer(final ReferenceType type, final DeserializationConfig config, final BeanDescription beanDesc, final TypeDeserializer contentTypeDeserializer, final JsonDeserializer<?> contentDeserializer) throws JsonMappingException {
        for (final Deserializers d : this._factoryConfig.deserializers()) {
            final JsonDeserializer<?> deser = d.findReferenceDeserializer(type, config, beanDesc, contentTypeDeserializer, contentDeserializer);
            if (deser != null) {
                return deser;
            }
        }
        return null;
    }
    
    protected JsonDeserializer<Object> _findCustomBeanDeserializer(final JavaType type, final DeserializationConfig config, final BeanDescription beanDesc) throws JsonMappingException {
        for (final Deserializers d : this._factoryConfig.deserializers()) {
            final JsonDeserializer<?> deser = d.findBeanDeserializer(type, config, beanDesc);
            if (deser != null) {
                return (JsonDeserializer<Object>)deser;
            }
        }
        return null;
    }
    
    protected JsonDeserializer<?> _findCustomArrayDeserializer(final ArrayType type, final DeserializationConfig config, final BeanDescription beanDesc, final TypeDeserializer elementTypeDeserializer, final JsonDeserializer<?> elementDeserializer) throws JsonMappingException {
        for (final Deserializers d : this._factoryConfig.deserializers()) {
            final JsonDeserializer<?> deser = d.findArrayDeserializer(type, config, beanDesc, elementTypeDeserializer, elementDeserializer);
            if (deser != null) {
                return deser;
            }
        }
        return null;
    }
    
    protected JsonDeserializer<?> _findCustomCollectionDeserializer(final CollectionType type, final DeserializationConfig config, final BeanDescription beanDesc, final TypeDeserializer elementTypeDeserializer, final JsonDeserializer<?> elementDeserializer) throws JsonMappingException {
        for (final Deserializers d : this._factoryConfig.deserializers()) {
            final JsonDeserializer<?> deser = d.findCollectionDeserializer(type, config, beanDesc, elementTypeDeserializer, elementDeserializer);
            if (deser != null) {
                return deser;
            }
        }
        return null;
    }
    
    protected JsonDeserializer<?> _findCustomCollectionLikeDeserializer(final CollectionLikeType type, final DeserializationConfig config, final BeanDescription beanDesc, final TypeDeserializer elementTypeDeserializer, final JsonDeserializer<?> elementDeserializer) throws JsonMappingException {
        for (final Deserializers d : this._factoryConfig.deserializers()) {
            final JsonDeserializer<?> deser = d.findCollectionLikeDeserializer(type, config, beanDesc, elementTypeDeserializer, elementDeserializer);
            if (deser != null) {
                return deser;
            }
        }
        return null;
    }
    
    protected JsonDeserializer<?> _findCustomEnumDeserializer(final Class<?> type, final DeserializationConfig config, final BeanDescription beanDesc) throws JsonMappingException {
        for (final Deserializers d : this._factoryConfig.deserializers()) {
            final JsonDeserializer<?> deser = d.findEnumDeserializer(type, config, beanDesc);
            if (deser != null) {
                return deser;
            }
        }
        return null;
    }
    
    protected JsonDeserializer<?> _findCustomMapDeserializer(final MapType type, final DeserializationConfig config, final BeanDescription beanDesc, final KeyDeserializer keyDeserializer, final TypeDeserializer elementTypeDeserializer, final JsonDeserializer<?> elementDeserializer) throws JsonMappingException {
        for (final Deserializers d : this._factoryConfig.deserializers()) {
            final JsonDeserializer<?> deser = d.findMapDeserializer(type, config, beanDesc, keyDeserializer, elementTypeDeserializer, elementDeserializer);
            if (deser != null) {
                return deser;
            }
        }
        return null;
    }
    
    protected JsonDeserializer<?> _findCustomMapLikeDeserializer(final MapLikeType type, final DeserializationConfig config, final BeanDescription beanDesc, final KeyDeserializer keyDeserializer, final TypeDeserializer elementTypeDeserializer, final JsonDeserializer<?> elementDeserializer) throws JsonMappingException {
        for (final Deserializers d : this._factoryConfig.deserializers()) {
            final JsonDeserializer<?> deser = d.findMapLikeDeserializer(type, config, beanDesc, keyDeserializer, elementTypeDeserializer, elementDeserializer);
            if (deser != null) {
                return deser;
            }
        }
        return null;
    }
    
    protected JsonDeserializer<Object> findDeserializerFromAnnotation(final DeserializationContext ctxt, final Annotated ann) throws JsonMappingException {
        final AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
        if (intr != null) {
            final Object deserDef = intr.findDeserializer(ann);
            if (deserDef != null) {
                return ctxt.deserializerInstance(ann, deserDef);
            }
        }
        return null;
    }
    
    protected KeyDeserializer findKeyDeserializerFromAnnotation(final DeserializationContext ctxt, final Annotated ann) throws JsonMappingException {
        final AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
        if (intr != null) {
            final Object deserDef = intr.findKeyDeserializer(ann);
            if (deserDef != null) {
                return ctxt.keyDeserializerInstance(ann, deserDef);
            }
        }
        return null;
    }
    
    protected JsonDeserializer<Object> findContentDeserializerFromAnnotation(final DeserializationContext ctxt, final Annotated ann) throws JsonMappingException {
        final AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
        if (intr != null) {
            final Object deserDef = intr.findContentDeserializer(ann);
            if (deserDef != null) {
                return ctxt.deserializerInstance(ann, deserDef);
            }
        }
        return null;
    }
    
    protected JavaType resolveMemberAndTypeAnnotations(final DeserializationContext ctxt, final AnnotatedMember member, JavaType type) throws JsonMappingException {
        final AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
        if (intr == null) {
            return type;
        }
        if (type.isMapLikeType()) {
            JavaType keyType = type.getKeyType();
            if (keyType != null) {
                final Object kdDef = intr.findKeyDeserializer(member);
                final KeyDeserializer kd = ctxt.keyDeserializerInstance(member, kdDef);
                if (kd != null) {
                    type = ((MapLikeType)type).withKeyValueHandler(kd);
                    keyType = type.getKeyType();
                }
            }
        }
        if (type.hasContentType()) {
            final Object cdDef = intr.findContentDeserializer(member);
            final JsonDeserializer<?> cd = ctxt.deserializerInstance(member, cdDef);
            if (cd != null) {
                type = type.withContentValueHandler(cd);
            }
            final TypeDeserializer contentTypeDeser = this.findPropertyContentTypeDeserializer(ctxt.getConfig(), type, member);
            if (contentTypeDeser != null) {
                type = type.withContentTypeHandler(contentTypeDeser);
            }
        }
        final TypeDeserializer valueTypeDeser = this.findPropertyTypeDeserializer(ctxt.getConfig(), type, member);
        if (valueTypeDeser != null) {
            type = type.withTypeHandler(valueTypeDeser);
        }
        type = intr.refineDeserializationType(ctxt.getConfig(), member, type);
        return type;
    }
    
    protected EnumResolver constructEnumResolver(final Class<?> enumClass, final DeserializationConfig config, final AnnotatedMember jsonValueAccessor) {
        if (jsonValueAccessor != null) {
            if (config.canOverrideAccessModifiers()) {
                ClassUtil.checkAndFixAccess(jsonValueAccessor.getMember(), config.isEnabled(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS));
            }
            return EnumResolver.constructUsingMethod(config, enumClass, jsonValueAccessor);
        }
        return EnumResolver.constructFor(config, enumClass);
    }
    
    protected boolean _hasCreatorAnnotation(final DeserializationContext ctxt, final Annotated ann) {
        final AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
        if (intr != null) {
            final JsonCreator.Mode mode = intr.findCreatorAnnotation(ctxt.getConfig(), ann);
            return mode != null && mode != JsonCreator.Mode.DISABLED;
        }
        return false;
    }
    
    @Deprecated
    protected JavaType modifyTypeByAnnotation(final DeserializationContext ctxt, final Annotated a, final JavaType type) throws JsonMappingException {
        final AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
        if (intr == null) {
            return type;
        }
        return intr.refineDeserializationType(ctxt.getConfig(), a, type);
    }
    
    @Deprecated
    protected JavaType resolveType(final DeserializationContext ctxt, final BeanDescription beanDesc, final JavaType type, final AnnotatedMember member) throws JsonMappingException {
        return this.resolveMemberAndTypeAnnotations(ctxt, member, type);
    }
    
    @Deprecated
    protected AnnotatedMethod _findJsonValueFor(final DeserializationConfig config, final JavaType enumType) {
        if (enumType == null) {
            return null;
        }
        final BeanDescription beanDesc = config.introspect(enumType);
        return beanDesc.findJsonValueMethod();
    }
    
    static {
        CLASS_OBJECT = Object.class;
        CLASS_STRING = String.class;
        CLASS_CHAR_SEQUENCE = CharSequence.class;
        CLASS_ITERABLE = Iterable.class;
        CLASS_MAP_ENTRY = Map.Entry.class;
        CLASS_SERIALIZABLE = Serializable.class;
        UNWRAPPED_CREATOR_PARAM_NAME = new PropertyName("@JsonUnwrapped");
    }
    
    protected static class ContainerDefaultMappings
    {
        static final HashMap<String, Class<? extends Collection>> _collectionFallbacks;
        static final HashMap<String, Class<? extends Map>> _mapFallbacks;
        
        public static Class<?> findCollectionFallback(final JavaType type) {
            return ContainerDefaultMappings._collectionFallbacks.get(type.getRawClass().getName());
        }
        
        public static Class<?> findMapFallback(final JavaType type) {
            return ContainerDefaultMappings._mapFallbacks.get(type.getRawClass().getName());
        }
        
        static {
            final HashMap<String, Class<? extends Collection>> fallbacks = new HashMap<String, Class<? extends Collection>>();
            final Class<? extends Collection> DEFAULT_LIST = ArrayList.class;
            final Class<? extends Collection> DEFAULT_SET = HashSet.class;
            fallbacks.put(Collection.class.getName(), DEFAULT_LIST);
            fallbacks.put(List.class.getName(), DEFAULT_LIST);
            fallbacks.put(Set.class.getName(), DEFAULT_SET);
            fallbacks.put(SortedSet.class.getName(), TreeSet.class);
            fallbacks.put(Queue.class.getName(), LinkedList.class);
            fallbacks.put(AbstractList.class.getName(), DEFAULT_LIST);
            fallbacks.put(AbstractSet.class.getName(), DEFAULT_SET);
            fallbacks.put(Deque.class.getName(), LinkedList.class);
            fallbacks.put(NavigableSet.class.getName(), TreeSet.class);
            _collectionFallbacks = fallbacks;
            final HashMap<String, Class<? extends Map>> fallbacks2 = new HashMap<String, Class<? extends Map>>();
            final Class<? extends Map> DEFAULT_MAP = LinkedHashMap.class;
            fallbacks2.put(Map.class.getName(), DEFAULT_MAP);
            fallbacks2.put(AbstractMap.class.getName(), DEFAULT_MAP);
            fallbacks2.put(ConcurrentMap.class.getName(), ConcurrentHashMap.class);
            fallbacks2.put(SortedMap.class.getName(), TreeMap.class);
            fallbacks2.put(NavigableMap.class.getName(), TreeMap.class);
            fallbacks2.put(ConcurrentNavigableMap.class.getName(), ConcurrentSkipListMap.class);
            _mapFallbacks = fallbacks2;
        }
    }
    
    protected static class CreatorCollectionState
    {
        public final DeserializationContext context;
        public final BeanDescription beanDesc;
        public final VisibilityChecker<?> vchecker;
        public final CreatorCollector creators;
        public final Map<AnnotatedWithParams, BeanPropertyDefinition[]> creatorParams;
        private List<CreatorCandidate> _implicitFactoryCandidates;
        private int _explicitFactoryCount;
        private List<CreatorCandidate> _implicitConstructorCandidates;
        private int _explicitConstructorCount;
        
        public CreatorCollectionState(final DeserializationContext ctxt, final BeanDescription bd, final VisibilityChecker<?> vc, final CreatorCollector cc, final Map<AnnotatedWithParams, BeanPropertyDefinition[]> cp) {
            this.context = ctxt;
            this.beanDesc = bd;
            this.vchecker = vc;
            this.creators = cc;
            this.creatorParams = cp;
        }
        
        public AnnotationIntrospector annotationIntrospector() {
            return this.context.getAnnotationIntrospector();
        }
        
        public void addImplicitFactoryCandidate(final CreatorCandidate cc) {
            if (this._implicitFactoryCandidates == null) {
                this._implicitFactoryCandidates = new LinkedList<CreatorCandidate>();
            }
            this._implicitFactoryCandidates.add(cc);
        }
        
        public void increaseExplicitFactoryCount() {
            ++this._explicitFactoryCount;
        }
        
        public boolean hasExplicitFactories() {
            return this._explicitFactoryCount > 0;
        }
        
        public boolean hasImplicitFactoryCandidates() {
            return this._implicitFactoryCandidates != null;
        }
        
        public List<CreatorCandidate> implicitFactoryCandidates() {
            return this._implicitFactoryCandidates;
        }
        
        public void addImplicitConstructorCandidate(final CreatorCandidate cc) {
            if (this._implicitConstructorCandidates == null) {
                this._implicitConstructorCandidates = new LinkedList<CreatorCandidate>();
            }
            this._implicitConstructorCandidates.add(cc);
        }
        
        public void increaseExplicitConstructorCount() {
            ++this._explicitConstructorCount;
        }
        
        public boolean hasExplicitConstructors() {
            return this._explicitConstructorCount > 0;
        }
        
        public boolean hasImplicitConstructorCandidates() {
            return this._implicitConstructorCandidates != null;
        }
        
        public List<CreatorCandidate> implicitConstructorCandidates() {
            return this._implicitConstructorCandidates;
        }
    }
}
