package org.glassfish.jersey.server.wadl.internal.generators.resourcedoc.model;

import java.util.ArrayList;
import javax.xml.bind.annotation.XmlElementWrapper;
import java.util.List;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "annotationDoc", propOrder = {})
public class AnnotationDocType
{
    private String annotationTypeName;
    @XmlElementWrapper(name = "attributes")
    protected List<NamedValueType> attribute;
    
    public List<NamedValueType> getAttributeDocs() {
        if (this.attribute == null) {
            this.attribute = new ArrayList<NamedValueType>();
        }
        return this.attribute;
    }
    
    public boolean hasAttributeDocs() {
        return this.attribute != null && !this.attribute.isEmpty();
    }
    
    public String getAnnotationTypeName() {
        return this.annotationTypeName;
    }
    
    public void setAnnotationTypeName(final String annotationTypeName) {
        this.annotationTypeName = annotationTypeName;
    }
}
