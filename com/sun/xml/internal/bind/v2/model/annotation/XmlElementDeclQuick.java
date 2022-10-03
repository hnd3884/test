package com.sun.xml.internal.bind.v2.model.annotation;

import java.lang.annotation.Annotation;
import javax.xml.bind.annotation.XmlElementDecl;

final class XmlElementDeclQuick extends Quick implements XmlElementDecl
{
    private final XmlElementDecl core;
    
    public XmlElementDeclQuick(final Locatable upstream, final XmlElementDecl core) {
        super(upstream);
        this.core = core;
    }
    
    @Override
    protected Annotation getAnnotation() {
        return this.core;
    }
    
    @Override
    protected Quick newInstance(final Locatable upstream, final Annotation core) {
        return new XmlElementDeclQuick(upstream, (XmlElementDecl)core);
    }
    
    @Override
    public Class<XmlElementDecl> annotationType() {
        return XmlElementDecl.class;
    }
    
    @Override
    public String name() {
        return this.core.name();
    }
    
    @Override
    public Class scope() {
        return this.core.scope();
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
    public String substitutionHeadNamespace() {
        return this.core.substitutionHeadNamespace();
    }
    
    @Override
    public String substitutionHeadName() {
        return this.core.substitutionHeadName();
    }
}
