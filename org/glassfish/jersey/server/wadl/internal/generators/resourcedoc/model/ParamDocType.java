package org.glassfish.jersey.server.wadl.internal.generators.resourcedoc.model;

import java.util.ArrayList;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import java.util.List;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "paramDoc", propOrder = {})
public class ParamDocType
{
    private String paramName;
    private String commentText;
    @XmlElementWrapper(name = "annotationDocs")
    protected List<AnnotationDocType> annotationDoc;
    @XmlAnyElement(lax = true)
    private List<Object> any;
    
    public ParamDocType() {
    }
    
    public ParamDocType(final String paramName, final String commentText) {
        this.paramName = paramName;
        this.commentText = commentText;
    }
    
    public List<AnnotationDocType> getAnnotationDocs() {
        if (this.annotationDoc == null) {
            this.annotationDoc = new ArrayList<AnnotationDocType>();
        }
        return this.annotationDoc;
    }
    
    public List<Object> getAny() {
        if (this.any == null) {
            this.any = new ArrayList<Object>();
        }
        return this.any;
    }
    
    public String getCommentText() {
        return this.commentText;
    }
    
    public void setCommentText(final String commentText) {
        this.commentText = commentText;
    }
    
    public String getParamName() {
        return this.paramName;
    }
    
    public void setParamName(final String paramName) {
        this.paramName = paramName;
    }
}
