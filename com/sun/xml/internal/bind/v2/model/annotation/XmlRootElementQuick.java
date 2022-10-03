package com.sun.xml.internal.bind.v2.model.annotation;

import java.lang.annotation.Annotation;
import javax.xml.bind.annotation.XmlRootElement;

final class XmlRootElementQuick extends Quick implements XmlRootElement
{
    private final XmlRootElement core;
    
    public XmlRootElementQuick(final Locatable upstream, final XmlRootElement core) {
        super(upstream);
        this.core = core;
    }
    
    @Override
    protected Annotation getAnnotation() {
        return this.core;
    }
    
    @Override
    protected Quick newInstance(final Locatable upstream, final Annotation core) {
        return new XmlRootElementQuick(upstream, (XmlRootElement)core);
    }
    
    @Override
    public Class<XmlRootElement> annotationType() {
        return XmlRootElement.class;
    }
    
    @Override
    public String name() {
        return this.core.name();
    }
    
    @Override
    public String namespace() {
        return this.core.namespace();
    }
}
