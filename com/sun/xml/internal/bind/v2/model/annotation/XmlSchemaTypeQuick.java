package com.sun.xml.internal.bind.v2.model.annotation;

import java.lang.annotation.Annotation;
import javax.xml.bind.annotation.XmlSchemaType;

final class XmlSchemaTypeQuick extends Quick implements XmlSchemaType
{
    private final XmlSchemaType core;
    
    public XmlSchemaTypeQuick(final Locatable upstream, final XmlSchemaType core) {
        super(upstream);
        this.core = core;
    }
    
    @Override
    protected Annotation getAnnotation() {
        return this.core;
    }
    
    @Override
    protected Quick newInstance(final Locatable upstream, final Annotation core) {
        return new XmlSchemaTypeQuick(upstream, (XmlSchemaType)core);
    }
    
    @Override
    public Class<XmlSchemaType> annotationType() {
        return XmlSchemaType.class;
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
}
