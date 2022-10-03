package com.fasterxml.jackson.jaxrs.cfg;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.jaxrs.annotation.JacksonFeatures;
import com.fasterxml.jackson.annotation.JsonView;
import java.lang.annotation.Annotation;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.cfg.MapperConfig;

public abstract class EndpointConfigBase<THIS extends EndpointConfigBase<THIS>>
{
    protected final MapperConfig<?> _config;
    protected Class<?> _activeView;
    protected String _rootName;
    protected DeserializationFeature[] _deserEnable;
    protected DeserializationFeature[] _deserDisable;
    protected ObjectReader _reader;
    protected SerializationFeature[] _serEnable;
    protected SerializationFeature[] _serDisable;
    protected ObjectWriter _writer;
    
    protected EndpointConfigBase(final MapperConfig<?> config) {
        this._config = config;
    }
    
    protected THIS add(final Annotation[] annotations, final boolean forWriting) {
        if (annotations != null) {
            for (final Annotation annotation : annotations) {
                this.addAnnotation(annotation.annotationType(), annotation, forWriting);
            }
        }
        return (THIS)this;
    }
    
    protected void addAnnotation(final Class<? extends Annotation> type, final Annotation annotation, final boolean forWriting) {
        if (type == JsonView.class) {
            final Class<?>[] views = ((JsonView)annotation).value();
            this._activeView = ((views.length > 0) ? views[0] : null);
        }
        else if (type == JacksonFeatures.class) {
            final JacksonFeatures feats = (JacksonFeatures)annotation;
            if (forWriting) {
                this._serEnable = nullIfEmpty(feats.serializationEnable());
                this._serDisable = nullIfEmpty(feats.serializationDisable());
            }
            else {
                this._deserEnable = nullIfEmpty(feats.deserializationEnable());
                this._deserDisable = nullIfEmpty(feats.deserializationDisable());
            }
        }
        else if (type == JsonRootName.class) {
            this._rootName = ((JsonRootName)annotation).value();
        }
        else if (type != JacksonAnnotationsInside.class) {
            final JacksonAnnotationsInside inside = type.getAnnotation(JacksonAnnotationsInside.class);
            if (inside != null) {
                this.add(type.getAnnotations(), forWriting);
            }
        }
    }
    
    protected THIS initReader(ObjectReader reader) {
        if (this._activeView != null) {
            reader = reader.withView((Class)this._activeView);
        }
        if (this._rootName != null) {
            reader = reader.withRootName(this._rootName);
        }
        if (this._deserEnable != null) {
            reader = reader.withFeatures(this._deserEnable);
        }
        if (this._deserDisable != null) {
            reader = reader.withoutFeatures(this._deserDisable);
        }
        this._reader = reader;
        return (THIS)this;
    }
    
    protected THIS initWriter(ObjectWriter writer) {
        if (this._activeView != null) {
            writer = writer.withView((Class)this._activeView);
        }
        if (this._rootName != null) {
            writer = writer.withRootName(this._rootName);
        }
        if (this._serEnable != null) {
            writer = writer.withFeatures(this._serEnable);
        }
        if (this._serDisable != null) {
            writer = writer.withoutFeatures(this._serDisable);
        }
        this._writer = writer;
        return (THIS)this;
    }
    
    public String getRootName() {
        return this._rootName;
    }
    
    public Class<?> getActiveView() {
        return this._activeView;
    }
    
    public final ObjectReader getReader() {
        if (this._reader == null) {
            throw new IllegalStateException();
        }
        return this._reader;
    }
    
    public final ObjectWriter getWriter() {
        if (this._writer == null) {
            throw new IllegalStateException();
        }
        return this._writer;
    }
    
    public abstract Object modifyBeforeWrite(final Object p0);
    
    protected static <T> T[] nullIfEmpty(final T[] arg) {
        if (arg == null || arg.length == 0) {
            return null;
        }
        return arg;
    }
}
