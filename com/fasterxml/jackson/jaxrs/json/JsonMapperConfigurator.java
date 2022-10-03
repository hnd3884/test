package com.fasterxml.jackson.jaxrs.json;

import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import java.util.ArrayList;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.jaxrs.cfg.Annotations;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.cfg.MapperConfiguratorBase;

public class JsonMapperConfigurator extends MapperConfiguratorBase<JsonMapperConfigurator, ObjectMapper>
{
    public JsonMapperConfigurator(final ObjectMapper mapper, final Annotations[] defAnnotations) {
        super(mapper, defAnnotations);
    }
    
    public synchronized ObjectMapper getConfiguredMapper() {
        return this._mapper;
    }
    
    public synchronized ObjectMapper getDefaultMapper() {
        if (this._defaultMapper == null) {
            this._setAnnotations(this._defaultMapper = new ObjectMapper(), this._defaultAnnotationsToUse);
        }
        return this._defaultMapper;
    }
    
    protected ObjectMapper mapper() {
        if (this._mapper == null) {
            this._setAnnotations(this._mapper = new ObjectMapper(), this._defaultAnnotationsToUse);
        }
        return this._mapper;
    }
    
    protected AnnotationIntrospector _resolveIntrospectors(final Annotations[] annotationsToUse) {
        final ArrayList<AnnotationIntrospector> intr = new ArrayList<AnnotationIntrospector>();
        for (final Annotations a : annotationsToUse) {
            if (a != null) {
                intr.add(this._resolveIntrospector(a));
            }
        }
        final int count = intr.size();
        if (count == 0) {
            return AnnotationIntrospector.nopInstance();
        }
        AnnotationIntrospector curr = intr.get(0);
        for (int i = 1, len = intr.size(); i < len; ++i) {
            curr = AnnotationIntrospector.pair(curr, (AnnotationIntrospector)intr.get(i));
        }
        return curr;
    }
    
    protected AnnotationIntrospector _resolveIntrospector(final Annotations ann) {
        switch (ann) {
            case JACKSON: {
                return (AnnotationIntrospector)new JacksonAnnotationIntrospector();
            }
            case JAXB: {
                try {
                    if (this._jaxbIntrospectorClass == null) {
                        this._jaxbIntrospectorClass = JaxbAnnotationIntrospector.class;
                    }
                    return this._jaxbIntrospectorClass.newInstance();
                }
                catch (final Exception e) {
                    throw new IllegalStateException("Failed to instantiate JaxbAnnotationIntrospector: " + e.getMessage(), e);
                }
                break;
            }
        }
        throw new IllegalStateException();
    }
}
