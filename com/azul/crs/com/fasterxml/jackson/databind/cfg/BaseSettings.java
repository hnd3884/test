package com.azul.crs.com.fasterxml.jackson.databind.cfg;

import com.azul.crs.com.fasterxml.jackson.databind.util.StdDateFormat;
import com.azul.crs.com.fasterxml.jackson.databind.introspect.AnnotationIntrospectorPair;
import com.azul.crs.com.fasterxml.jackson.databind.introspect.DefaultAccessorNamingStrategy;
import com.azul.crs.com.fasterxml.jackson.core.Base64Variant;
import java.util.Locale;
import java.text.DateFormat;
import com.azul.crs.com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.azul.crs.com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
import com.azul.crs.com.fasterxml.jackson.databind.introspect.AccessorNamingStrategy;
import com.azul.crs.com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.azul.crs.com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.azul.crs.com.fasterxml.jackson.databind.introspect.ClassIntrospector;
import com.azul.crs.com.fasterxml.jackson.databind.type.TypeFactory;
import java.util.TimeZone;
import java.io.Serializable;

public final class BaseSettings implements Serializable
{
    private static final long serialVersionUID = 1L;
    private static final TimeZone DEFAULT_TIMEZONE;
    protected final TypeFactory _typeFactory;
    protected final ClassIntrospector _classIntrospector;
    protected final AnnotationIntrospector _annotationIntrospector;
    protected final PropertyNamingStrategy _propertyNamingStrategy;
    protected final AccessorNamingStrategy.Provider _accessorNaming;
    protected final TypeResolverBuilder<?> _typeResolverBuilder;
    protected final PolymorphicTypeValidator _typeValidator;
    protected final DateFormat _dateFormat;
    protected final HandlerInstantiator _handlerInstantiator;
    protected final Locale _locale;
    protected final TimeZone _timeZone;
    protected final Base64Variant _defaultBase64;
    
    public BaseSettings(final ClassIntrospector ci, final AnnotationIntrospector ai, final PropertyNamingStrategy pns, final TypeFactory tf, final TypeResolverBuilder<?> typer, final DateFormat dateFormat, final HandlerInstantiator hi, final Locale locale, final TimeZone tz, final Base64Variant defaultBase64, final PolymorphicTypeValidator ptv, final AccessorNamingStrategy.Provider accNaming) {
        this._classIntrospector = ci;
        this._annotationIntrospector = ai;
        this._propertyNamingStrategy = pns;
        this._typeFactory = tf;
        this._typeResolverBuilder = typer;
        this._dateFormat = dateFormat;
        this._handlerInstantiator = hi;
        this._locale = locale;
        this._timeZone = tz;
        this._defaultBase64 = defaultBase64;
        this._typeValidator = ptv;
        this._accessorNaming = accNaming;
    }
    
    @Deprecated
    public BaseSettings(final ClassIntrospector ci, final AnnotationIntrospector ai, final PropertyNamingStrategy pns, final TypeFactory tf, final TypeResolverBuilder<?> typer, final DateFormat dateFormat, final HandlerInstantiator hi, final Locale locale, final TimeZone tz, final Base64Variant defaultBase64, final PolymorphicTypeValidator ptv) {
        this(ci, ai, pns, tf, typer, dateFormat, hi, locale, tz, defaultBase64, ptv, new DefaultAccessorNamingStrategy.Provider());
    }
    
    public BaseSettings copy() {
        return new BaseSettings(this._classIntrospector.copy(), this._annotationIntrospector, this._propertyNamingStrategy, this._typeFactory, this._typeResolverBuilder, this._dateFormat, this._handlerInstantiator, this._locale, this._timeZone, this._defaultBase64, this._typeValidator, this._accessorNaming);
    }
    
    public BaseSettings withClassIntrospector(final ClassIntrospector ci) {
        if (this._classIntrospector == ci) {
            return this;
        }
        return new BaseSettings(ci, this._annotationIntrospector, this._propertyNamingStrategy, this._typeFactory, this._typeResolverBuilder, this._dateFormat, this._handlerInstantiator, this._locale, this._timeZone, this._defaultBase64, this._typeValidator, this._accessorNaming);
    }
    
    public BaseSettings withAnnotationIntrospector(final AnnotationIntrospector ai) {
        if (this._annotationIntrospector == ai) {
            return this;
        }
        return new BaseSettings(this._classIntrospector, ai, this._propertyNamingStrategy, this._typeFactory, this._typeResolverBuilder, this._dateFormat, this._handlerInstantiator, this._locale, this._timeZone, this._defaultBase64, this._typeValidator, this._accessorNaming);
    }
    
    public BaseSettings withInsertedAnnotationIntrospector(final AnnotationIntrospector ai) {
        return this.withAnnotationIntrospector(AnnotationIntrospectorPair.create(ai, this._annotationIntrospector));
    }
    
    public BaseSettings withAppendedAnnotationIntrospector(final AnnotationIntrospector ai) {
        return this.withAnnotationIntrospector(AnnotationIntrospectorPair.create(this._annotationIntrospector, ai));
    }
    
    public BaseSettings withPropertyNamingStrategy(final PropertyNamingStrategy pns) {
        if (this._propertyNamingStrategy == pns) {
            return this;
        }
        return new BaseSettings(this._classIntrospector, this._annotationIntrospector, pns, this._typeFactory, this._typeResolverBuilder, this._dateFormat, this._handlerInstantiator, this._locale, this._timeZone, this._defaultBase64, this._typeValidator, this._accessorNaming);
    }
    
    public BaseSettings withAccessorNaming(final AccessorNamingStrategy.Provider p) {
        if (this._accessorNaming == p) {
            return this;
        }
        return new BaseSettings(this._classIntrospector, this._annotationIntrospector, this._propertyNamingStrategy, this._typeFactory, this._typeResolverBuilder, this._dateFormat, this._handlerInstantiator, this._locale, this._timeZone, this._defaultBase64, this._typeValidator, p);
    }
    
    public BaseSettings withTypeFactory(final TypeFactory tf) {
        if (this._typeFactory == tf) {
            return this;
        }
        return new BaseSettings(this._classIntrospector, this._annotationIntrospector, this._propertyNamingStrategy, tf, this._typeResolverBuilder, this._dateFormat, this._handlerInstantiator, this._locale, this._timeZone, this._defaultBase64, this._typeValidator, this._accessorNaming);
    }
    
    public BaseSettings withTypeResolverBuilder(final TypeResolverBuilder<?> typer) {
        if (this._typeResolverBuilder == typer) {
            return this;
        }
        return new BaseSettings(this._classIntrospector, this._annotationIntrospector, this._propertyNamingStrategy, this._typeFactory, typer, this._dateFormat, this._handlerInstantiator, this._locale, this._timeZone, this._defaultBase64, this._typeValidator, this._accessorNaming);
    }
    
    public BaseSettings withDateFormat(DateFormat df) {
        if (this._dateFormat == df) {
            return this;
        }
        if (df != null && this.hasExplicitTimeZone()) {
            df = this._force(df, this._timeZone);
        }
        return new BaseSettings(this._classIntrospector, this._annotationIntrospector, this._propertyNamingStrategy, this._typeFactory, this._typeResolverBuilder, df, this._handlerInstantiator, this._locale, this._timeZone, this._defaultBase64, this._typeValidator, this._accessorNaming);
    }
    
    public BaseSettings withHandlerInstantiator(final HandlerInstantiator hi) {
        if (this._handlerInstantiator == hi) {
            return this;
        }
        return new BaseSettings(this._classIntrospector, this._annotationIntrospector, this._propertyNamingStrategy, this._typeFactory, this._typeResolverBuilder, this._dateFormat, hi, this._locale, this._timeZone, this._defaultBase64, this._typeValidator, this._accessorNaming);
    }
    
    public BaseSettings with(final Locale l) {
        if (this._locale == l) {
            return this;
        }
        return new BaseSettings(this._classIntrospector, this._annotationIntrospector, this._propertyNamingStrategy, this._typeFactory, this._typeResolverBuilder, this._dateFormat, this._handlerInstantiator, l, this._timeZone, this._defaultBase64, this._typeValidator, this._accessorNaming);
    }
    
    public BaseSettings with(final TimeZone tz) {
        if (tz == this._timeZone) {
            return this;
        }
        final DateFormat df = this._force(this._dateFormat, (tz == null) ? BaseSettings.DEFAULT_TIMEZONE : tz);
        return new BaseSettings(this._classIntrospector, this._annotationIntrospector, this._propertyNamingStrategy, this._typeFactory, this._typeResolverBuilder, df, this._handlerInstantiator, this._locale, tz, this._defaultBase64, this._typeValidator, this._accessorNaming);
    }
    
    public BaseSettings with(final Base64Variant base64) {
        if (base64 == this._defaultBase64) {
            return this;
        }
        return new BaseSettings(this._classIntrospector, this._annotationIntrospector, this._propertyNamingStrategy, this._typeFactory, this._typeResolverBuilder, this._dateFormat, this._handlerInstantiator, this._locale, this._timeZone, base64, this._typeValidator, this._accessorNaming);
    }
    
    public BaseSettings with(final PolymorphicTypeValidator v) {
        if (v == this._typeValidator) {
            return this;
        }
        return new BaseSettings(this._classIntrospector, this._annotationIntrospector, this._propertyNamingStrategy, this._typeFactory, this._typeResolverBuilder, this._dateFormat, this._handlerInstantiator, this._locale, this._timeZone, this._defaultBase64, v, this._accessorNaming);
    }
    
    public ClassIntrospector getClassIntrospector() {
        return this._classIntrospector;
    }
    
    public AnnotationIntrospector getAnnotationIntrospector() {
        return this._annotationIntrospector;
    }
    
    public PropertyNamingStrategy getPropertyNamingStrategy() {
        return this._propertyNamingStrategy;
    }
    
    public AccessorNamingStrategy.Provider getAccessorNaming() {
        return this._accessorNaming;
    }
    
    public TypeFactory getTypeFactory() {
        return this._typeFactory;
    }
    
    public TypeResolverBuilder<?> getTypeResolverBuilder() {
        return this._typeResolverBuilder;
    }
    
    public PolymorphicTypeValidator getPolymorphicTypeValidator() {
        return this._typeValidator;
    }
    
    public DateFormat getDateFormat() {
        return this._dateFormat;
    }
    
    public HandlerInstantiator getHandlerInstantiator() {
        return this._handlerInstantiator;
    }
    
    public Locale getLocale() {
        return this._locale;
    }
    
    public TimeZone getTimeZone() {
        final TimeZone tz = this._timeZone;
        return (tz == null) ? BaseSettings.DEFAULT_TIMEZONE : tz;
    }
    
    public boolean hasExplicitTimeZone() {
        return this._timeZone != null;
    }
    
    public Base64Variant getBase64Variant() {
        return this._defaultBase64;
    }
    
    private DateFormat _force(DateFormat df, final TimeZone tz) {
        if (df instanceof StdDateFormat) {
            return ((StdDateFormat)df).withTimeZone(tz);
        }
        df = (DateFormat)df.clone();
        df.setTimeZone(tz);
        return df;
    }
    
    static {
        DEFAULT_TIMEZONE = TimeZone.getTimeZone("UTC");
    }
}