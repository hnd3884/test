package com.fasterxml.jackson.dataformat.xml.jaxb;

import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.dataformat.xml.XmlAnnotationIntrospector;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;

public class XmlJaxbAnnotationIntrospector extends JaxbAnnotationIntrospector implements XmlAnnotationIntrospector
{
    private static final long serialVersionUID = 1L;
    
    @Deprecated
    public XmlJaxbAnnotationIntrospector() {
    }
    
    public XmlJaxbAnnotationIntrospector(final TypeFactory typeFactory) {
        super(typeFactory);
    }
    
    public String findNamespace(final Annotated ann) {
        return super.findNamespace(ann);
    }
    
    public Boolean isOutputAsAttribute(final Annotated ann) {
        return super.isOutputAsAttribute(ann);
    }
    
    public Boolean isOutputAsText(final Annotated ann) {
        return super.isOutputAsText(ann);
    }
    
    public Boolean isOutputAsCData(final Annotated ann) {
        return null;
    }
    
    public void setDefaultUseWrapper(final boolean b) {
    }
}
