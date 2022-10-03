package com.fasterxml.jackson.databind.cfg;

import com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import java.util.Collection;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.Base64Variant;
import java.util.Locale;
import java.util.TimeZone;
import java.text.DateFormat;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;
import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.SerializerFactory;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.introspect.VisibilityChecker;
import com.fasterxml.jackson.databind.jsontype.SubtypeResolver;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ServiceLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.core.StreamWriteFeature;
import com.fasterxml.jackson.core.StreamReadFeature;
import com.fasterxml.jackson.core.TokenStreamFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class MapperBuilder<M extends ObjectMapper, B extends MapperBuilder<M, B>>
{
    protected final M _mapper;
    
    protected MapperBuilder(final M mapper) {
        this._mapper = mapper;
    }
    
    public M build() {
        return this._mapper;
    }
    
    public boolean isEnabled(final MapperFeature f) {
        return this._mapper.isEnabled(f);
    }
    
    public boolean isEnabled(final DeserializationFeature f) {
        return this._mapper.isEnabled(f);
    }
    
    public boolean isEnabled(final SerializationFeature f) {
        return this._mapper.isEnabled(f);
    }
    
    public boolean isEnabled(final JsonParser.Feature f) {
        return this._mapper.isEnabled(f);
    }
    
    public boolean isEnabled(final JsonGenerator.Feature f) {
        return this._mapper.isEnabled(f);
    }
    
    public TokenStreamFactory streamFactory() {
        return (TokenStreamFactory)this._mapper.tokenStreamFactory();
    }
    
    public B enable(final MapperFeature... features) {
        this._mapper.enable(features);
        return this._this();
    }
    
    public B disable(final MapperFeature... features) {
        this._mapper.disable(features);
        return this._this();
    }
    
    public B configure(final MapperFeature feature, final boolean state) {
        this._mapper.configure(feature, state);
        return this._this();
    }
    
    public B enable(final SerializationFeature... features) {
        for (final SerializationFeature f : features) {
            this._mapper.enable(f);
        }
        return this._this();
    }
    
    public B disable(final SerializationFeature... features) {
        for (final SerializationFeature f : features) {
            this._mapper.disable(f);
        }
        return this._this();
    }
    
    public B configure(final SerializationFeature feature, final boolean state) {
        this._mapper.configure(feature, state);
        return this._this();
    }
    
    public B enable(final DeserializationFeature... features) {
        for (final DeserializationFeature f : features) {
            this._mapper.enable(f);
        }
        return this._this();
    }
    
    public B disable(final DeserializationFeature... features) {
        for (final DeserializationFeature f : features) {
            this._mapper.disable(f);
        }
        return this._this();
    }
    
    public B configure(final DeserializationFeature feature, final boolean state) {
        this._mapper.configure(feature, state);
        return this._this();
    }
    
    public B enable(final JsonParser.Feature... features) {
        this._mapper.enable(features);
        return this._this();
    }
    
    public B disable(final JsonParser.Feature... features) {
        this._mapper.disable(features);
        return this._this();
    }
    
    public B configure(final JsonParser.Feature feature, final boolean state) {
        this._mapper.configure(feature, state);
        return this._this();
    }
    
    public B enable(final JsonGenerator.Feature... features) {
        this._mapper.enable(features);
        return this._this();
    }
    
    public B disable(final JsonGenerator.Feature... features) {
        this._mapper.disable(features);
        return this._this();
    }
    
    public B configure(final JsonGenerator.Feature feature, final boolean state) {
        this._mapper.configure(feature, state);
        return this._this();
    }
    
    public B enable(final StreamReadFeature... features) {
        for (final StreamReadFeature f : features) {
            this._mapper.enable(f.mappedFeature());
        }
        return this._this();
    }
    
    public B disable(final StreamReadFeature... features) {
        for (final StreamReadFeature f : features) {
            this._mapper.disable(f.mappedFeature());
        }
        return this._this();
    }
    
    public B configure(final StreamReadFeature feature, final boolean state) {
        this._mapper.configure(feature.mappedFeature(), state);
        return this._this();
    }
    
    public B enable(final StreamWriteFeature... features) {
        for (final StreamWriteFeature f : features) {
            this._mapper.enable(f.mappedFeature());
        }
        return this._this();
    }
    
    public B disable(final StreamWriteFeature... features) {
        for (final StreamWriteFeature f : features) {
            this._mapper.disable(f.mappedFeature());
        }
        return this._this();
    }
    
    public B configure(final StreamWriteFeature feature, final boolean state) {
        this._mapper.configure(feature.mappedFeature(), state);
        return this._this();
    }
    
    public B addModule(final Module module) {
        this._mapper.registerModule(module);
        return this._this();
    }
    
    public B addModules(final Module... modules) {
        for (final Module module : modules) {
            this.addModule(module);
        }
        return this._this();
    }
    
    public B addModules(final Iterable<? extends Module> modules) {
        for (final Module module : modules) {
            this.addModule(module);
        }
        return this._this();
    }
    
    public static List<Module> findModules() {
        return findModules(null);
    }
    
    public static List<Module> findModules(final ClassLoader classLoader) {
        final ArrayList<Module> modules = new ArrayList<Module>();
        final ServiceLoader<Module> loader = secureGetServiceLoader(Module.class, classLoader);
        for (final Module module : loader) {
            modules.add(module);
        }
        return modules;
    }
    
    private static <T> ServiceLoader<T> secureGetServiceLoader(final Class<T> clazz, final ClassLoader classLoader) {
        final SecurityManager sm = System.getSecurityManager();
        if (sm == null) {
            return (classLoader == null) ? ServiceLoader.load(clazz) : ServiceLoader.load(clazz, classLoader);
        }
        return AccessController.doPrivileged((PrivilegedAction<ServiceLoader<T>>)new PrivilegedAction<ServiceLoader<T>>() {
            @Override
            public ServiceLoader<T> run() {
                return (classLoader == null) ? ServiceLoader.load(clazz) : ServiceLoader.load(clazz, classLoader);
            }
        });
    }
    
    public B findAndAddModules() {
        return this.addModules(findModules());
    }
    
    public B annotationIntrospector(final AnnotationIntrospector intr) {
        this._mapper.setAnnotationIntrospector(intr);
        return this._this();
    }
    
    public B nodeFactory(final JsonNodeFactory f) {
        this._mapper.setNodeFactory(f);
        return this._this();
    }
    
    public B typeFactory(final TypeFactory f) {
        this._mapper.setTypeFactory(f);
        return this._this();
    }
    
    public B subtypeResolver(final SubtypeResolver r) {
        this._mapper.setSubtypeResolver(r);
        return this._this();
    }
    
    public B visibility(final VisibilityChecker<?> vc) {
        this._mapper.setVisibility(vc);
        return this._this();
    }
    
    public B visibility(final PropertyAccessor forMethod, final JsonAutoDetect.Visibility visibility) {
        this._mapper.setVisibility(forMethod, visibility);
        return this._this();
    }
    
    public B handlerInstantiator(final HandlerInstantiator hi) {
        this._mapper.setHandlerInstantiator(hi);
        return this._this();
    }
    
    public B propertyNamingStrategy(final PropertyNamingStrategy s) {
        this._mapper.setPropertyNamingStrategy(s);
        return this._this();
    }
    
    public B serializerFactory(final SerializerFactory f) {
        this._mapper.setSerializerFactory(f);
        return this._this();
    }
    
    public B filterProvider(final FilterProvider prov) {
        this._mapper.setFilterProvider(prov);
        return this._this();
    }
    
    public B defaultPrettyPrinter(final PrettyPrinter pp) {
        this._mapper.setDefaultPrettyPrinter(pp);
        return this._this();
    }
    
    public B injectableValues(final InjectableValues v) {
        this._mapper.setInjectableValues(v);
        return this._this();
    }
    
    public B addHandler(final DeserializationProblemHandler h) {
        this._mapper.addHandler(h);
        return this._this();
    }
    
    public B clearProblemHandlers() {
        this._mapper.clearProblemHandlers();
        return this._this();
    }
    
    public B defaultSetterInfo(final JsonSetter.Value v) {
        this._mapper.setDefaultSetterInfo(v);
        return this._this();
    }
    
    public B defaultMergeable(final Boolean b) {
        this._mapper.setDefaultMergeable(b);
        return this._this();
    }
    
    public B defaultLeniency(final Boolean b) {
        this._mapper.setDefaultLeniency(b);
        return this._this();
    }
    
    public B defaultDateFormat(final DateFormat df) {
        this._mapper.setDateFormat(df);
        return this._this();
    }
    
    public B defaultTimeZone(final TimeZone tz) {
        this._mapper.setTimeZone(tz);
        return this._this();
    }
    
    public B defaultLocale(final Locale locale) {
        this._mapper.setLocale(locale);
        return this._this();
    }
    
    public B defaultBase64Variant(final Base64Variant v) {
        this._mapper.setBase64Variant(v);
        return this._this();
    }
    
    public B serializationInclusion(final JsonInclude.Include incl) {
        this._mapper.setSerializationInclusion(incl);
        return this._this();
    }
    
    public B defaultPropertyInclusion(final JsonInclude.Value incl) {
        this._mapper.setDefaultPropertyInclusion(incl);
        return this._this();
    }
    
    public B addMixIn(final Class<?> target, final Class<?> mixinSource) {
        this._mapper.addMixIn(target, mixinSource);
        return this._this();
    }
    
    public B registerSubtypes(final Class<?>... subtypes) {
        this._mapper.registerSubtypes(subtypes);
        return this._this();
    }
    
    public B registerSubtypes(final NamedType... subtypes) {
        this._mapper.registerSubtypes(subtypes);
        return this._this();
    }
    
    public B registerSubtypes(final Collection<Class<?>> subtypes) {
        this._mapper.registerSubtypes(subtypes);
        return this._this();
    }
    
    public B polymorphicTypeValidator(final PolymorphicTypeValidator ptv) {
        this._mapper.setPolymorphicTypeValidator(ptv);
        return this._this();
    }
    
    public B activateDefaultTyping(final PolymorphicTypeValidator subtypeValidator) {
        this._mapper.activateDefaultTyping(subtypeValidator);
        return this._this();
    }
    
    public B activateDefaultTyping(final PolymorphicTypeValidator subtypeValidator, final ObjectMapper.DefaultTyping dti) {
        this._mapper.activateDefaultTyping(subtypeValidator, dti);
        return this._this();
    }
    
    public B activateDefaultTyping(final PolymorphicTypeValidator subtypeValidator, final ObjectMapper.DefaultTyping applicability, final JsonTypeInfo.As includeAs) {
        this._mapper.activateDefaultTyping(subtypeValidator, applicability, includeAs);
        return this._this();
    }
    
    public B activateDefaultTypingAsProperty(final PolymorphicTypeValidator subtypeValidator, final ObjectMapper.DefaultTyping applicability, final String propertyName) {
        this._mapper.activateDefaultTypingAsProperty(subtypeValidator, applicability, propertyName);
        return this._this();
    }
    
    public B deactivateDefaultTyping() {
        this._mapper.deactivateDefaultTyping();
        return this._this();
    }
    
    public B setDefaultTyping(final TypeResolverBuilder<?> typer) {
        this._mapper.setDefaultTyping(typer);
        return this._this();
    }
    
    protected final B _this() {
        return (B)this;
    }
}
