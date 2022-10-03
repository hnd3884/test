package com.sun.xml.internal.bind.v2.model.annotation;

import javax.xml.bind.annotation.XmlNsForm;
import javax.xml.bind.annotation.XmlNs;
import java.lang.annotation.Annotation;
import javax.xml.bind.annotation.XmlSchema;

final class XmlSchemaQuick extends Quick implements XmlSchema
{
    private final XmlSchema core;
    
    public XmlSchemaQuick(final Locatable upstream, final XmlSchema core) {
        super(upstream);
        this.core = core;
    }
    
    @Override
    protected Annotation getAnnotation() {
        return this.core;
    }
    
    @Override
    protected Quick newInstance(final Locatable upstream, final Annotation core) {
        return new XmlSchemaQuick(upstream, (XmlSchema)core);
    }
    
    @Override
    public Class<XmlSchema> annotationType() {
        return XmlSchema.class;
    }
    
    @Override
    public String location() {
        return this.core.location();
    }
    
    @Override
    public String namespace() {
        return this.core.namespace();
    }
    
    @Override
    public XmlNs[] xmlns() {
        return this.core.xmlns();
    }
    
    @Override
    public XmlNsForm elementFormDefault() {
        return this.core.elementFormDefault();
    }
    
    @Override
    public XmlNsForm attributeFormDefault() {
        return this.core.attributeFormDefault();
    }
}
