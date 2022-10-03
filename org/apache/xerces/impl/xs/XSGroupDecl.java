package org.apache.xerces.impl.xs;

import org.apache.xerces.impl.xs.util.XSObjectListImpl;
import org.apache.xerces.xs.XSAnnotation;
import org.apache.xerces.xs.XSModelGroup;
import org.apache.xerces.xs.XSNamespaceItem;
import org.apache.xerces.xs.XSObjectList;
import org.apache.xerces.xs.XSModelGroupDefinition;

public class XSGroupDecl implements XSModelGroupDefinition
{
    public String fName;
    public String fTargetNamespace;
    public XSModelGroupImpl fModelGroup;
    public XSObjectList fAnnotations;
    private XSNamespaceItem fNamespaceItem;
    
    public XSGroupDecl() {
        this.fName = null;
        this.fTargetNamespace = null;
        this.fModelGroup = null;
        this.fAnnotations = null;
        this.fNamespaceItem = null;
    }
    
    public short getType() {
        return 6;
    }
    
    public String getName() {
        return this.fName;
    }
    
    public String getNamespace() {
        return this.fTargetNamespace;
    }
    
    public XSModelGroup getModelGroup() {
        return this.fModelGroup;
    }
    
    public XSAnnotation getAnnotation() {
        return (this.fAnnotations != null) ? ((XSAnnotation)this.fAnnotations.item(0)) : null;
    }
    
    public XSObjectList getAnnotations() {
        return (this.fAnnotations != null) ? this.fAnnotations : XSObjectListImpl.EMPTY_LIST;
    }
    
    public XSNamespaceItem getNamespaceItem() {
        return this.fNamespaceItem;
    }
    
    void setNamespaceItem(final XSNamespaceItem fNamespaceItem) {
        this.fNamespaceItem = fNamespaceItem;
    }
}
