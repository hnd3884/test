package com.sun.xml.internal.bind.v2.model.annotation;

import java.lang.annotation.Annotation;
import javax.xml.bind.annotation.XmlTransient;

final class XmlTransientQuick extends Quick implements XmlTransient
{
    private final XmlTransient core;
    
    public XmlTransientQuick(final Locatable upstream, final XmlTransient core) {
        super(upstream);
        this.core = core;
    }
    
    @Override
    protected Annotation getAnnotation() {
        return this.core;
    }
    
    @Override
    protected Quick newInstance(final Locatable upstream, final Annotation core) {
        return new XmlTransientQuick(upstream, (XmlTransient)core);
    }
    
    @Override
    public Class<XmlTransient> annotationType() {
        return XmlTransient.class;
    }
}
