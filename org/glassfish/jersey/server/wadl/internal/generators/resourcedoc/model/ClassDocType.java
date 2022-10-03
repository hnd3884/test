package org.glassfish.jersey.server.wadl.internal.generators.resourcedoc.model;

import java.util.ArrayList;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import java.util.List;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "classDoc", propOrder = {})
public class ClassDocType
{
    private String className;
    private String commentText;
    @XmlElementWrapper(name = "methodDocs")
    private List<MethodDocType> methodDoc;
    @XmlAnyElement(lax = true)
    private List<Object> any;
    
    public List<MethodDocType> getMethodDocs() {
        if (this.methodDoc == null) {
            this.methodDoc = new ArrayList<MethodDocType>();
        }
        return this.methodDoc;
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
    
    public String getClassName() {
        return this.className;
    }
    
    public void setClassName(final String className) {
        this.className = className;
    }
}
