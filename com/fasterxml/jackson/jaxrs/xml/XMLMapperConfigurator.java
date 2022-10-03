package com.fasterxml.jackson.jaxrs.xml;

import com.fasterxml.jackson.dataformat.xml.jaxb.XmlJaxbAnnotationIntrospector;
import com.fasterxml.jackson.dataformat.xml.JacksonXmlAnnotationIntrospector;
import java.util.ArrayList;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.cfg.Annotations;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.jaxrs.cfg.MapperConfiguratorBase;

public class XMLMapperConfigurator extends MapperConfiguratorBase<XMLMapperConfigurator, XmlMapper>
{
    public XMLMapperConfigurator(final XmlMapper mapper, final Annotations[] defAnnotations) {
        super((ObjectMapper)mapper, defAnnotations);
    }
    
    public synchronized XmlMapper getConfiguredMapper() {
        return (XmlMapper)this._mapper;
    }
    
    public synchronized XmlMapper getDefaultMapper() {
        if (this._defaultMapper == null) {
            final JacksonXmlModule module = this.getConfiguredModule();
            this._setAnnotations(this._defaultMapper = (ObjectMapper)((module == null) ? new XmlMapper() : new XmlMapper(module)), this._defaultAnnotationsToUse);
        }
        return (XmlMapper)this._defaultMapper;
    }
    
    protected JacksonXmlModule getConfiguredModule() {
        return new JacksonXmlModule();
    }
    
    protected XmlMapper mapper() {
        if (this._mapper == null) {
            this._setAnnotations(this._mapper = (ObjectMapper)new XmlMapper(), this._defaultAnnotationsToUse);
        }
        return (XmlMapper)this._mapper;
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
                return (AnnotationIntrospector)new JacksonXmlAnnotationIntrospector();
            }
            case JAXB: {
                try {
                    if (this._jaxbIntrospectorClass == null) {
                        this._jaxbIntrospectorClass = XmlJaxbAnnotationIntrospector.class;
                    }
                    return this._jaxbIntrospectorClass.newInstance();
                }
                catch (final Exception e) {
                    throw new IllegalStateException("Failed to instantiate XmlJaxbAnnotationIntrospector: " + e.getMessage(), e);
                }
                break;
            }
        }
        throw new IllegalStateException();
    }
}
