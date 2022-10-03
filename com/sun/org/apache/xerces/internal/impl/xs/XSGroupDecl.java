package com.sun.org.apache.xerces.internal.impl.xs;

import com.sun.org.apache.xerces.internal.impl.xs.util.XSObjectListImpl;
import com.sun.org.apache.xerces.internal.xs.XSAnnotation;
import com.sun.org.apache.xerces.internal.xs.XSModelGroup;
import com.sun.org.apache.xerces.internal.xs.XSNamespaceItem;
import com.sun.org.apache.xerces.internal.xs.XSObjectList;
import com.sun.org.apache.xerces.internal.xs.XSModelGroupDefinition;

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
    
    @Override
    public short getType() {
        return 6;
    }
    
    @Override
    public String getName() {
        return this.fName;
    }
    
    @Override
    public String getNamespace() {
        return this.fTargetNamespace;
    }
    
    @Override
    public XSModelGroup getModelGroup() {
        return this.fModelGroup;
    }
    
    @Override
    public XSAnnotation getAnnotation() {
        return (this.fAnnotations != null) ? ((XSAnnotation)this.fAnnotations.item(0)) : null;
    }
    
    @Override
    public XSObjectList getAnnotations() {
        return (this.fAnnotations != null) ? this.fAnnotations : XSObjectListImpl.EMPTY_LIST;
    }
    
    @Override
    public XSNamespaceItem getNamespaceItem() {
        return this.fNamespaceItem;
    }
    
    void setNamespaceItem(final XSNamespaceItem namespaceItem) {
        this.fNamespaceItem = namespaceItem;
    }
}
