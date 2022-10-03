package com.sun.xml.internal.bind.v2.model.annotation;

import javax.xml.bind.annotation.XmlElementRef;
import java.lang.annotation.Annotation;
import javax.xml.bind.annotation.XmlElementRefs;

final class XmlElementRefsQuick extends Quick implements XmlElementRefs
{
    private final XmlElementRefs core;
    
    public XmlElementRefsQuick(final Locatable upstream, final XmlElementRefs core) {
        super(upstream);
        this.core = core;
    }
    
    @Override
    protected Annotation getAnnotation() {
        return this.core;
    }
    
    @Override
    protected Quick newInstance(final Locatable upstream, final Annotation core) {
        return new XmlElementRefsQuick(upstream, (XmlElementRefs)core);
    }
    
    @Override
    public Class<XmlElementRefs> annotationType() {
        return XmlElementRefs.class;
    }
    
    @Override
    public XmlElementRef[] value() {
        return this.core.value();
    }
}
