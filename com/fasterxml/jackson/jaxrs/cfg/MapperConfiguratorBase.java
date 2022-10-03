package com.fasterxml.jackson.jaxrs.cfg;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class MapperConfiguratorBase<IMPL extends MapperConfiguratorBase<IMPL, MAPPER>, MAPPER extends ObjectMapper>
{
    protected MAPPER _mapper;
    protected MAPPER _defaultMapper;
    protected Annotations[] _defaultAnnotationsToUse;
    protected Class<? extends AnnotationIntrospector> _jaxbIntrospectorClass;
    
    public MapperConfiguratorBase(final MAPPER mapper, final Annotations[] defaultAnnotations) {
        this._mapper = mapper;
        this._defaultAnnotationsToUse = defaultAnnotations;
    }
    
    public abstract MAPPER getConfiguredMapper();
    
    public abstract MAPPER getDefaultMapper();
    
    protected abstract MAPPER mapper();
    
    protected abstract AnnotationIntrospector _resolveIntrospectors(final Annotations[] p0);
    
    public final synchronized void setMapper(final MAPPER m) {
        this._mapper = m;
    }
    
    public final synchronized void setAnnotationsToUse(final Annotations[] annotationsToUse) {
        this._setAnnotations(this.mapper(), annotationsToUse);
    }
    
    public final synchronized void configure(final DeserializationFeature f, final boolean state) {
        this.mapper().configure(f, state);
    }
    
    public final synchronized void configure(final SerializationFeature f, final boolean state) {
        this.mapper().configure(f, state);
    }
    
    public final synchronized void configure(final JsonParser.Feature f, final boolean state) {
        this.mapper().configure(f, state);
    }
    
    public final synchronized void configure(final JsonGenerator.Feature f, final boolean state) {
        this.mapper().configure(f, state);
    }
    
    protected final void _setAnnotations(final ObjectMapper mapper, final Annotations[] annotationsToUse) {
        AnnotationIntrospector intr;
        if (annotationsToUse == null || annotationsToUse.length == 0) {
            intr = AnnotationIntrospector.nopInstance();
        }
        else {
            intr = this._resolveIntrospectors(annotationsToUse);
        }
        mapper.setAnnotationIntrospector(intr);
    }
}
