package com.sun.xml.internal.bind.v2.model.annotation;

import java.lang.annotation.Annotation;
import javax.xml.bind.annotation.XmlType;

final class XmlTypeQuick extends Quick implements XmlType
{
    private final XmlType core;
    
    public XmlTypeQuick(final Locatable upstream, final XmlType core) {
        super(upstream);
        this.core = core;
    }
    
    @Override
    protected Annotation getAnnotation() {
        return this.core;
    }
    
    @Override
    protected Quick newInstance(final Locatable upstream, final Annotation core) {
        return new XmlTypeQuick(upstream, (XmlType)core);
    }
    
    @Override
    public Class<XmlType> annotationType() {
        return XmlType.class;
    }
    
    @Override
    public String name() {
        return this.core.name();
    }
    
    @Override
    public String namespace() {
        return this.core.namespace();
    }
    
    @Override
    public String[] propOrder() {
        return this.core.propOrder();
    }
    
    @Override
    public Class factoryClass() {
        return this.core.factoryClass();
    }
    
    @Override
    public String factoryMethod() {
        return this.core.factoryMethod();
    }
}
