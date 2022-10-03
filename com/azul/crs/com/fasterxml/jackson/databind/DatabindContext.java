package com.azul.crs.com.fasterxml.jackson.databind;

import com.azul.crs.com.fasterxml.jackson.databind.util.Converter;
import com.azul.crs.com.fasterxml.jackson.annotation.ObjectIdResolver;
import com.azul.crs.com.fasterxml.jackson.databind.cfg.HandlerInstantiator;
import com.azul.crs.com.fasterxml.jackson.annotation.ObjectIdGenerator;
import com.azul.crs.com.fasterxml.jackson.databind.introspect.ObjectIdInfo;
import com.azul.crs.com.fasterxml.jackson.databind.introspect.Annotated;
import com.azul.crs.com.fasterxml.jackson.databind.type.TypeFactory;
import com.azul.crs.com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.azul.crs.com.fasterxml.jackson.databind.util.ClassUtil;
import java.lang.reflect.Type;
import com.azul.crs.com.fasterxml.jackson.annotation.JsonFormat;
import java.util.TimeZone;
import java.util.Locale;
import com.azul.crs.com.fasterxml.jackson.databind.cfg.MapperConfig;

public abstract class DatabindContext
{
    private static final int MAX_ERROR_STR_LEN = 500;
    
    public abstract MapperConfig<?> getConfig();
    
    public abstract AnnotationIntrospector getAnnotationIntrospector();
    
    public abstract boolean isEnabled(final MapperFeature p0);
    
    public abstract boolean canOverrideAccessModifiers();
    
    public abstract Class<?> getActiveView();
    
    public abstract Locale getLocale();
    
    public abstract TimeZone getTimeZone();
    
    public abstract JsonFormat.Value getDefaultPropertyFormat(final Class<?> p0);
    
    public abstract Object getAttribute(final Object p0);
    
    public abstract DatabindContext setAttribute(final Object p0, final Object p1);
    
    public JavaType constructType(final Type type) {
        if (type == null) {
            return null;
        }
        return this.getTypeFactory().constructType(type);
    }
    
    public abstract JavaType constructSpecializedType(final JavaType p0, final Class<?> p1);
    
    public JavaType resolveSubType(final JavaType baseType, final String subClassName) throws JsonMappingException {
        if (subClassName.indexOf(60) > 0) {
            final JavaType t = this.getTypeFactory().constructFromCanonical(subClassName);
            if (t.isTypeOrSubTypeOf(baseType.getRawClass())) {
                return t;
            }
        }
        else {
            Class<?> cls;
            try {
                cls = this.getTypeFactory().findClass(subClassName);
            }
            catch (final ClassNotFoundException e) {
                return null;
            }
            catch (final Exception e2) {
                throw this.invalidTypeIdException(baseType, subClassName, String.format("problem: (%s) %s", e2.getClass().getName(), ClassUtil.exceptionMessage(e2)));
            }
            if (baseType.isTypeOrSuperTypeOf(cls)) {
                return this.getTypeFactory().constructSpecializedType(baseType, cls);
            }
        }
        throw this.invalidTypeIdException(baseType, subClassName, "Not a subtype");
    }
    
    public JavaType resolveAndValidateSubType(final JavaType baseType, final String subClass, final PolymorphicTypeValidator ptv) throws JsonMappingException {
        final int ltIndex = subClass.indexOf(60);
        if (ltIndex > 0) {
            return this._resolveAndValidateGeneric(baseType, subClass, ptv, ltIndex);
        }
        final MapperConfig<?> config = this.getConfig();
        PolymorphicTypeValidator.Validity vld = ptv.validateSubClassName(config, baseType, subClass);
        if (vld == PolymorphicTypeValidator.Validity.DENIED) {
            return this._throwSubtypeNameNotAllowed(baseType, subClass, ptv);
        }
        Class<?> cls;
        try {
            cls = this.getTypeFactory().findClass(subClass);
        }
        catch (final ClassNotFoundException e) {
            return null;
        }
        catch (final Exception e2) {
            throw this.invalidTypeIdException(baseType, subClass, String.format("problem: (%s) %s", e2.getClass().getName(), ClassUtil.exceptionMessage(e2)));
        }
        if (!baseType.isTypeOrSuperTypeOf(cls)) {
            return this._throwNotASubtype(baseType, subClass);
        }
        final JavaType subType = config.getTypeFactory().constructSpecializedType(baseType, cls);
        if (vld == PolymorphicTypeValidator.Validity.INDETERMINATE) {
            vld = ptv.validateSubType(config, baseType, subType);
            if (vld != PolymorphicTypeValidator.Validity.ALLOWED) {
                return this._throwSubtypeClassNotAllowed(baseType, subClass, ptv);
            }
        }
        return subType;
    }
    
    private JavaType _resolveAndValidateGeneric(final JavaType baseType, final String subClass, final PolymorphicTypeValidator ptv, final int ltIndex) throws JsonMappingException {
        final MapperConfig<?> config = this.getConfig();
        final PolymorphicTypeValidator.Validity vld = ptv.validateSubClassName(config, baseType, subClass.substring(0, ltIndex));
        if (vld == PolymorphicTypeValidator.Validity.DENIED) {
            return this._throwSubtypeNameNotAllowed(baseType, subClass, ptv);
        }
        final JavaType subType = this.getTypeFactory().constructFromCanonical(subClass);
        if (!subType.isTypeOrSubTypeOf(baseType.getRawClass())) {
            return this._throwNotASubtype(baseType, subClass);
        }
        if (vld != PolymorphicTypeValidator.Validity.ALLOWED && ptv.validateSubType(config, baseType, subType) != PolymorphicTypeValidator.Validity.ALLOWED) {
            return this._throwSubtypeClassNotAllowed(baseType, subClass, ptv);
        }
        return subType;
    }
    
    protected <T> T _throwNotASubtype(final JavaType baseType, final String subType) throws JsonMappingException {
        throw this.invalidTypeIdException(baseType, subType, "Not a subtype");
    }
    
    protected <T> T _throwSubtypeNameNotAllowed(final JavaType baseType, final String subType, final PolymorphicTypeValidator ptv) throws JsonMappingException {
        throw this.invalidTypeIdException(baseType, subType, "Configured `PolymorphicTypeValidator` (of type " + ClassUtil.classNameOf(ptv) + ") denied resolution");
    }
    
    protected <T> T _throwSubtypeClassNotAllowed(final JavaType baseType, final String subType, final PolymorphicTypeValidator ptv) throws JsonMappingException {
        throw this.invalidTypeIdException(baseType, subType, "Configured `PolymorphicTypeValidator` (of type " + ClassUtil.classNameOf(ptv) + ") denied resolution");
    }
    
    protected abstract JsonMappingException invalidTypeIdException(final JavaType p0, final String p1, final String p2);
    
    public abstract TypeFactory getTypeFactory();
    
    public ObjectIdGenerator<?> objectIdGeneratorInstance(final Annotated annotated, final ObjectIdInfo objectIdInfo) throws JsonMappingException {
        final Class<?> implClass = objectIdInfo.getGeneratorType();
        final MapperConfig<?> config = this.getConfig();
        final HandlerInstantiator hi = config.getHandlerInstantiator();
        ObjectIdGenerator<?> gen = (hi == null) ? null : hi.objectIdGeneratorInstance(config, annotated, implClass);
        if (gen == null) {
            gen = ClassUtil.createInstance(implClass, config.canOverrideAccessModifiers());
        }
        return gen.forScope(objectIdInfo.getScope());
    }
    
    public ObjectIdResolver objectIdResolverInstance(final Annotated annotated, final ObjectIdInfo objectIdInfo) {
        final Class<? extends ObjectIdResolver> implClass = objectIdInfo.getResolverType();
        final MapperConfig<?> config = this.getConfig();
        final HandlerInstantiator hi = config.getHandlerInstantiator();
        ObjectIdResolver resolver = (hi == null) ? null : hi.resolverIdGeneratorInstance(config, annotated, implClass);
        if (resolver == null) {
            resolver = ClassUtil.createInstance(implClass, config.canOverrideAccessModifiers());
        }
        return resolver;
    }
    
    public Converter<Object, Object> converterInstance(final Annotated annotated, final Object converterDef) throws JsonMappingException {
        if (converterDef == null) {
            return null;
        }
        if (converterDef instanceof Converter) {
            return (Converter)converterDef;
        }
        if (!(converterDef instanceof Class)) {
            throw new IllegalStateException("AnnotationIntrospector returned Converter definition of type " + converterDef.getClass().getName() + "; expected type Converter or Class<Converter> instead");
        }
        final Class<?> converterClass = (Class<?>)converterDef;
        if (converterClass == Converter.None.class || ClassUtil.isBogusClass(converterClass)) {
            return null;
        }
        if (!Converter.class.isAssignableFrom(converterClass)) {
            throw new IllegalStateException("AnnotationIntrospector returned Class " + converterClass.getName() + "; expected Class<Converter>");
        }
        final MapperConfig<?> config = this.getConfig();
        final HandlerInstantiator hi = config.getHandlerInstantiator();
        Converter<?, ?> conv = (hi == null) ? null : hi.converterInstance(config, annotated, converterClass);
        if (conv == null) {
            conv = ClassUtil.createInstance(converterClass, config.canOverrideAccessModifiers());
        }
        return (Converter<Object, Object>)conv;
    }
    
    public abstract <T> T reportBadDefinition(final JavaType p0, final String p1) throws JsonMappingException;
    
    public <T> T reportBadDefinition(final Class<?> type, final String msg) throws JsonMappingException {
        return this.reportBadDefinition(this.constructType(type), msg);
    }
    
    protected final String _format(final String msg, final Object... msgArgs) {
        if (msgArgs.length > 0) {
            return String.format(msg, msgArgs);
        }
        return msg;
    }
    
    protected final String _truncate(final String desc) {
        if (desc == null) {
            return "";
        }
        if (desc.length() <= 500) {
            return desc;
        }
        return desc.substring(0, 500) + "]...[" + desc.substring(desc.length() - 500);
    }
    
    protected String _quotedString(final String desc) {
        if (desc == null) {
            return "[N/A]";
        }
        return String.format("\"%s\"", this._truncate(desc));
    }
    
    protected String _colonConcat(final String msgBase, final String extra) {
        if (extra == null) {
            return msgBase;
        }
        return msgBase + ": " + extra;
    }
    
    protected String _desc(final String desc) {
        if (desc == null) {
            return "[N/A]";
        }
        return this._truncate(desc);
    }
}
