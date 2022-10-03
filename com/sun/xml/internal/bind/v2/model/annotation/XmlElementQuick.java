package com.sun.xml.internal.bind.v2.model.annotation;

import java.lang.annotation.Annotation;
import javax.xml.bind.annotation.XmlElement;

final class XmlElementQuick extends Quick implements XmlElement
{
    private final XmlElement core;
    
    public XmlElementQuick(final Locatable upstream, final XmlElement core) {
        super(upstream);
        this.core = core;
    }
    
    @Override
    protected Annotation getAnnotation() {
        return this.core;
    }
    
    @Override
    protected Quick newInstance(final Locatable upstream, final Annotation core) {
        return new XmlElementQuick(upstream, (XmlElement)core);
    }
    
    @Override
    public Class<XmlElement> annotationType() {
        return XmlElement.class;
    }
    
    @Override
    public String name() {
        return this.core.name();
    }
    
    @Override
    public Class type() {
        return this.core.type();
    }
    
    @Override
    public String namespace() {
        return this.core.namespace();
    }
    
    @Override
    public String defaultValue() {
        return this.core.defaultValue();
    }
    
    @Override
    public boolean required() {
        return this.core.required();
    }
    
    @Override
    public boolean nillable() {
        return this.core.nillable();
    }
}
