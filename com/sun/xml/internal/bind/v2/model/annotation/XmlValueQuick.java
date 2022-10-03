package com.sun.xml.internal.bind.v2.model.annotation;

import java.lang.annotation.Annotation;
import javax.xml.bind.annotation.XmlValue;

final class XmlValueQuick extends Quick implements XmlValue
{
    private final XmlValue core;
    
    public XmlValueQuick(final Locatable upstream, final XmlValue core) {
        super(upstream);
        this.core = core;
    }
    
    @Override
    protected Annotation getAnnotation() {
        return this.core;
    }
    
    @Override
    protected Quick newInstance(final Locatable upstream, final Annotation core) {
        return new XmlValueQuick(upstream, (XmlValue)core);
    }
    
    @Override
    public Class<XmlValue> annotationType() {
        return XmlValue.class;
    }
}
