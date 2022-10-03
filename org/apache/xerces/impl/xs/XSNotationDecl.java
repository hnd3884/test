package org.apache.xerces.impl.xs;

import org.apache.xerces.impl.xs.util.XSObjectListImpl;
import org.apache.xerces.xs.XSAnnotation;
import org.apache.xerces.xs.XSNamespaceItem;
import org.apache.xerces.xs.XSObjectList;
import org.apache.xerces.xs.XSNotationDeclaration;

public class XSNotationDecl implements XSNotationDeclaration
{
    public String fName;
    public String fTargetNamespace;
    public String fPublicId;
    public String fSystemId;
    public XSObjectList fAnnotations;
    private XSNamespaceItem fNamespaceItem;
    
    public XSNotationDecl() {
        this.fName = null;
        this.fTargetNamespace = null;
        this.fPublicId = null;
        this.fSystemId = null;
        this.fAnnotations = null;
        this.fNamespaceItem = null;
    }
    
    public short getType() {
        return 11;
    }
    
    public String getName() {
        return this.fName;
    }
    
    public String getNamespace() {
        return this.fTargetNamespace;
    }
    
    public String getSystemId() {
        return this.fSystemId;
    }
    
    public String getPublicId() {
        return this.fPublicId;
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
